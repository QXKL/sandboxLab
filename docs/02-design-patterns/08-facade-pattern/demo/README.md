# 外观模式 - 代码示例

## 示例说明

本目录包含外观模式的三个核心示例：

1. **HomeTheaterFacadeDemo.java** - 家庭影院外观
2. **OrderFacadeDemo.java** - 订单处理外观
3. **DatabaseFacadeDemo.java** - 数据库操作外观

---

## 运行方式

```bash
# 进入demo目录
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\08-facade-pattern\demo

# 编译
javac HomeTheaterFacadeDemo.java
javac OrderFacadeDemo.java
javac DatabaseFacadeDemo.java

# 运行
java HomeTheaterFacadeDemo
java OrderFacadeDemo
java DatabaseFacadeDemo
```

---

## 核心概念

### 外观模式的本质

```
客户端 → 外观（统一接口） → 子系统1
                          → 子系统2
                          → 子系统3
                          → 子系统N
```

**关键点**：
- 外观提供简单统一的高层接口
- 隐藏子系统的复杂性
- 客户端与子系统解耦
- 多对一的关系

---

## 外观 vs 其他模式

| 模式 | 目的 | 对象数量 | 接口 | 例子 |
|-----|------|---------|------|------|
| **外观** | 简化接口 | 多对一 | 新高层接口 | 前台接待 |
| **适配器** | 转换接口 | 一对一 | 转换目标接口 | 插座转换器 |
| **代理** | 控制访问 | 一对一 | 相同接口 | 门卫 |
| **装饰器** | 增强功能 | 一对一 | 相同接口 | 给咖啡加料 |

---

## 学习建议

### 学习顺序
1. **HomeTheaterFacadeDemo** - 理解外观的基本概念
2. **OrderFacadeDemo** - 理解业务编排
3. **DatabaseFacadeDemo** - 理解真实应用

### 重点理解

#### 1. 外观简化复杂操作
运行HomeTheaterFacadeDemo，对比：
- 不使用外观：20多行代码
- 使用外观：1行代码
- 代码减少：95%

#### 2. 外观协调多个子系统
运行OrderFacadeDemo，理解：
- 如何协调7个子系统
- Service层就是外观模式
- 业务逻辑的封装

#### 3. 外观隐藏复杂性
运行DatabaseFacadeDemo，观察：
- 隐藏资源管理
- 统一异常处理
- Spring JdbcTemplate的本质

---

## 真实应用案例

### 1. Spring JdbcTemplate
```java
JdbcTemplate template = new JdbcTemplate(dataSource);
List<User> users = template.query(sql, rowMapper);
// 简化了JDBC的复杂API
```

### 2. SLF4J
```java
Logger logger = LoggerFactory.getLogger(MyClass.class);
logger.info("message");
// 统一不同日志框架的接口
```

### 3. Service层
```java
@Service
public class OrderService {
    // 协调多个DAO，是外观模式
    public void placeOrder(Order order) {
        inventoryDao.check();
        paymentDao.process();
        orderDao.save();
    }
}
```

---

**记住**：
> **子系统复杂难调用，**  
> **外观模式来帮忙，**  
> **统一接口简单化，**  
> **前台接待是榜样。**
