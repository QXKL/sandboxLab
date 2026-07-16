# 数据库连接池管理 - 自测题

**总分：100分**

## 一、概念理解（40分）

### 1. 为什么需要数据库连接池？（10分）
<details>
<summary>查看答案</summary>
- 创建连接很慢（50-200ms）
- 数据库连接数有限
- 复用连接提高性能（100倍提升）
- 节省资源
</details>

### 2. minIdle和maxTotal的区别？（10分）
<details>
<summary>查看答案</summary>
- minIdle：最小空闲连接数（始终保持）
- maxTotal：最大连接数（上限）
- minIdle ≤ maxTotal
</details>

### 3. 什么是连接泄漏？如何避免？（10分）
<details>
<summary>查看答案</summary>
**连接泄漏**：获取连接后没有关闭，导致连接无法归还  
**避免方法**：使用try-with-resources自动关闭
</details>

### 4. HikariCP vs Druid的选择？（10分）
<details>
<summary>查看答案</summary>
- HikariCP：性能最好，Spring Boot默认
- Druid：功能丰富（监控、防火墙），适合国内项目
- 推荐：优先HikariCP
</details>

## 二、参数配置（30分）

### 5. 合理配置连接池参数（30分）

**场景**：Web应用，8核CPU，日常100并发，峰值500并发

**任务**：配置HikariCP参数

<details>
<summary>参考答案</summary>
```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 20              # 日常并发/5
      maximum-pool-size: 100        # (核心数×2)×2 + 余量
      connection-timeout: 3000      # 3秒超时
      idle-timeout: 600000          # 10分钟空闲超时
      max-lifetime: 1800000         # 30分钟最大生命周期
      connection-test-query: SELECT 1
```
**理由**：
- minIdle=20：覆盖日常流量
- maxTotal=100：应对峰值
- 30分钟生命周期 < MySQL的8小时wait_timeout
</details>

## 三、问题诊断（30分）

### 6. 分析问题

**现象**：应用运行2小时后，出现大量超时
```
java.sql.SQLException: Timeout: Pool empty. 
Unable to fetch a connection in 3 seconds
```

**问题**：可能的原因和解决方案？（30分）

<details>
<summary>参考答案</summary>
**可能原因**：
1. 连接泄漏（最常见）
2. maxTotal太小
3. 慢查询占用连接
4. 数据库连接断开

**解决方案**：
```java
// 1. 检查连接泄漏
try (Connection conn = dataSource.getConnection()) {
    // 确保自动关闭
}

// 2. 增大maxTotal
maximum-pool-size: 100

// 3. 监控慢查询
log.info("Active connections: {}", 
    dataSource.getHikariPoolMXBean().getActiveConnections());

// 4. 设置连接测试
connection-test-query: SELECT 1
max-lifetime: 1800000
```
</details>

---

**及格线：60分 | 目标：80分以上**
