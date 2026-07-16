# N+1查询问题 - 自测题

**总分：100分**

## 一、概念理解（40分）

### 1. 什么是N+1查询问题？（10分）

<details>
<summary>查看答案</summary>
**答案**：1次查询主表 + N次查询关联表（N是主表记录数）  
**示例**：查10个用户的订单 = 1次查users + 10次查orders = 11次SQL
</details>

### 2. N+1问题的主要危害？（10分）

<details>
<summary>查看答案</summary>
- 性能差（可能慢几百倍）
- 数据库压力大
- 连接池耗尽
- 网络开销大
</details>

### 3. JPA中哪种配置容易导致N+1？（10分）

<details>
<summary>查看答案</summary>
**答案**：FetchType.LAZY（懒加载）  
访问关联对象时触发额外查询
</details>

### 4. 如何检测N+1问题？（10分）

<details>
<summary>查看答案</summary>
- 开启SQL日志
- 使用Hibernate Statistics
- 性能测试
- P6Spy监控
</details>

## 二、解决方案（40分）

### 5. JOIN查询 vs IN查询对比（20分）

<details>
<summary>查看答案</summary>
**JOIN**：1次查询，性能最好，但数据量可能大  
**IN**：2次查询，代码清晰，适合大多数场景
</details>

### 6. 设计查询方案（20分）

**场景**：查询100个用户及其订单（每人10个订单）

<details>
<summary>参考答案</summary>
```java
// 方案1：JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id IN :ids")
List<User> findWithOrders(@Param("ids") List<Long> ids);

// 方案2：IN查询
List<User> users = userRepo.findByIdIn(ids);
List<Order> orders = orderRepo.findByUserIdIn(ids);
// 手动组装
```
</details>

## 三、实战分析（20分）

### 7. 分析代码问题

```java
List<User> users = userRepository.findAll();
for (User user : users) {
    System.out.println(user.getOrders().size());
}
```

**问题**：存在什么问题？如何优化？

<details>
<summary>参考答案</summary>
**问题**：N+1查询（访问orders触发N次查询）  
**优化**：使用JOIN FETCH预加载orders
</details>

---

**及格线：60分 | 目标：80分以上**
