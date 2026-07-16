# 微服务架构

## 一、什么是微服务架构？

### 定义

**微服务架构（Microservices Architecture）**：将应用拆分为一组小型、独立的服务，每个服务运行在自己的进程中，通过轻量级机制（通常是HTTP API）进行通信。

**类比**：连锁餐厅
```
单体应用 = 一家大型餐厅
  - 所有功能在一个建筑内
  - 厨房、服务、收银都在一起
  - 某个环节出问题，整个餐厅停摆

微服务 = 连锁餐厅网络
  - 每个餐厅独立运营（服务）
  - 统一品牌和标准（API契约）
  - 某家餐厅关闭，不影响其他餐厅
```

### 单体 vs 微服务

**单体应用**：
```
┌─────────────────────────────┐
│      单体应用 (Monolith)     │
│  ┌─────────────────────┐   │
│  │  用户模块           │   │
│  ├─────────────────────┤   │
│  │  订单模块           │   │
│  ├─────────────────────┤   │
│  │  支付模块           │   │
│  ├─────────────────────┤   │
│  │  库存模块           │   │
│  └─────────────────────┘   │
│                             │
│  共享数据库                 │
└─────────────────────────────┘
```

**微服务架构**：
```
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│用户服务  │  │订单服务  │  │支付服务  │  │库存服务  │
│  User    │  │  Order   │  │  Payment │  │Inventory │
│  API     │  │   API    │  │   API    │  │   API    │
└────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘
     │             │             │             │
┌────▼─────┐  ┌───▼──────┐  ┌───▼──────┐  ┌───▼──────┐
│用户数据库│  │订单数据库│  │支付数据库│  │库存数据库│
└──────────┘  └──────────┘  └──────────┘  └──────────┘
```

## 二、微服务的核心特征

### 1. 按业务能力组织服务

```
电商系统拆分：
- 用户服务：注册、登录、个人信息
- 商品服务：商品目录、搜索、详情
- 订单服务：下单、订单查询、订单管理
- 支付服务：支付、退款
- 库存服务：库存管理、库存扣减
- 物流服务：发货、物流追踪
```

### 2. 独立部署

```
用户服务更新：
- 只需重新部署用户服务
- 其他服务不受影响
- 可以独立回滚

vs 单体应用：
- 任何修改都要重新部署整个应用
- 风险大，回滚困难
```

### 3. 去中心化数据管理

```
每个服务有自己的数据库：
用户服务 → 用户数据库
订单服务 → 订单数据库
支付服务 → 支付数据库

优点：
- 数据隔离
- 技术选型自由（MySQL、MongoDB、Redis）
- 避免数据库成为瓶颈

挑战：
- 分布式事务
- 数据一致性
```

### 4. 轻量级通信

```
通信方式：
1. 同步：REST API、gRPC
2. 异步：消息队列（Kafka、RabbitMQ）

示例：
用户服务 --HTTP--> 订单服务
订单服务 --MQ--> 库存服务
```

### 5. 技术栈多样性

```
用户服务：Java + Spring Boot + MySQL
订单服务：Go + Gin + PostgreSQL
商品服务：Node.js + Express + MongoDB
推荐服务：Python + Flask + Redis
```

### 6. 自动化

```
必备自动化：
- CI/CD：持续集成、持续部署
- 自动化测试：单元测试、集成测试
- 监控告警：日志、指标、链路追踪
- 自动扩缩容：根据负载自动调整实例数
```

## 三、微服务的优点

### 1. 独立开发和部署

```
场景：双11促销
- 订单服务压力大 → 扩容订单服务
- 用户服务正常 → 不需要动
- 商品服务需要优化 → 独立部署新版本

单体应用：
- 整个应用扩容 → 浪费资源
- 任何改动都要重新部署整个应用
```

### 2. 故障隔离

```
微服务：
推荐服务挂了 → 只影响推荐功能
用户仍然可以浏览、下单

单体应用：
某个模块出问题 → 整个应用不可用
```

### 3. 技术栈灵活

```
新功能用新技术：
- 图像识别服务：Python + TensorFlow
- 实时推荐：Go + Redis
- 传统业务：Java + Spring Boot
```

### 4. 团队独立

```
团队组织：
- 用户团队：负责用户服务
- 订单团队：负责订单服务
- 每个团队独立开发、测试、部署
- 减少沟通成本，提高效率
```

### 5. 易于扩展

```
水平扩展：
订单服务：3个实例
用户服务：2个实例
支付服务：5个实例（支付压力大）

按需扩容，成本优化
```

## 四、微服务的挑战

### 1. 分布式系统复杂性

**问题**：
```
网络延迟、网络故障、服务不可用
需要处理各种分布式问题
```

**解决方案**：
```java
// 服务调用需要容错
@Service
public class OrderService {
    
    @HystrixCommand(fallbackMethod = "getInventoryFallback")
    public int getInventory(Long productId) {
        // 调用库存服务
        return inventoryService.getStock(productId);
    }
    
    // 降级方法
    public int getInventoryFallback(Long productId) {
        // 服务不可用，返回默认值
        return 0;
    }
}
```

### 2. 分布式事务

**问题**：
```
下单流程：
1. 创建订单（订单服务）
2. 扣减库存（库存服务）
3. 扣减余额（支付服务）

如何保证一致性？
```

**解决方案**：

**方案1：Saga模式**
```java
// 编排Saga
public void createOrder(Order order) {
    try {
        // 1. 创建订单
        orderService.create(order);
        
        // 2. 扣减库存
        inventoryService.decrease(order.getItems());
        
        // 3. 扣减余额
        paymentService.deduct(order.getAmount());
        
    } catch (Exception e) {
        // 补偿事务
        inventoryService.increase(order.getItems());  // 恢复库存
        orderService.cancel(order.getId());  // 取消订单
    }
}
```

**方案2：最终一致性（事件驱动）**
```java
// 1. 订单服务：创建订单，发送事件
public void createOrder(Order order) {
    orderRepository.save(order);
    eventPublisher.publish(new OrderCreatedEvent(order));
}

// 2. 库存服务：监听事件，扣减库存
@EventListener
public void onOrderCreated(OrderCreatedEvent event) {
    inventoryService.decrease(event.getItems());
    eventPublisher.publish(new InventoryDecreasedEvent(event.getOrderId()));
}

// 3. 支付服务：监听事件，扣减余额
@EventListener
public void onInventoryDecreased(InventoryDecreasedEvent event) {
    paymentService.deduct(event.getOrderId());
}
```

### 3. 服务间通信

**问题**：
```
服务A调用服务B，服务B调用服务C
- 如何发现服务？
- 如何负载均衡？
- 如何处理超时？
```

**解决方案**：

**服务注册与发现**：
```java
// Eureka服务注册
@SpringBootApplication
@EnableEurekaClient
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

// Feign客户端
@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/inventory/{productId}")
    int getStock(@PathVariable Long productId);
}
```

### 4. 数据一致性

**问题**：
```
订单服务需要用户信息
- 方案1：调用用户服务（可能失败）
- 方案2：冗余用户数据（可能不一致）
```

**解决方案**：
```java
// 数据冗余 + 事件同步
@Entity
public class Order {
    private Long id;
    private Long userId;
    private String userName;  // 冗余用户名
    private String userPhone;  // 冗余手机号
}

// 监听用户更新事件
@EventListener
public void onUserUpdated(UserUpdatedEvent event) {
    // 更新订单中的冗余数据
    orderRepository.updateUserInfo(
        event.getUserId(),
        event.getNewName(),
        event.getNewPhone()
    );
}
```

### 5. 测试复杂

**问题**：
```
集成测试需要启动多个服务
端到端测试环境难以搭建
```

**解决方案**：
```
1. 契约测试（Contract Testing）
2. 服务虚拟化（Mock服务）
3. 测试环境自动化部署
```

### 6. 运维复杂

**问题**：
```
10个服务 × 3个实例 = 30个进程
- 如何监控？
- 如何追踪请求？
- 如何排查问题？
```

**解决方案**：
```
1. 集中式日志：ELK（Elasticsearch + Logstash + Kibana）
2. 链路追踪：Zipkin、Skywalking
3. 监控告警：Prometheus + Grafana
4. 服务网格：Istio
```

## 五、微服务拆分原则

### 1. 单一职责原则

```
✅ 好的拆分：
- 用户服务：只负责用户相关功能
- 订单服务：只负责订单相关功能

❌ 不好的拆分：
- 用户订单服务：用户 + 订单混在一起
```

### 2. 高内聚低耦合

```
✅ 高内聚：
订单服务内部：
- 创建订单
- 查询订单
- 取消订单
功能紧密相关

✅ 低耦合：
订单服务只通过API调用用户服务
不直接访问用户数据库
```

### 3. 避免过度拆分

```
❌ 过度拆分：
- 用户注册服务
- 用户登录服务
- 用户信息服务
太细粒度，增加复杂度

✅ 合理拆分：
- 用户服务（包含注册、登录、信息）
```

### 4. 按业务能力拆分

```
电商系统：
- 用户服务
- 商品服务
- 订单服务
- 支付服务
- 库存服务
- 物流服务

而不是按技术拆分：
- 数据库服务
- 缓存服务
```

## 六、微服务技术栈

### 1. 服务框架

```
Spring Boot + Spring Cloud
Dubbo
gRPC
```

### 2. 服务注册与发现

```
Eureka
Consul
Nacos
Zookeeper
```

### 3. API网关

```
Spring Cloud Gateway
Zuul
Kong
```

### 4. 配置中心

```
Spring Cloud Config
Nacos
Apollo
```

### 5. 链路追踪

```
Zipkin
Skywalking
Jaeger
```

### 6. 容器化

```
Docker
Kubernetes
```

## 七、何时使用微服务

### 适合微服务

✅ **大型复杂系统**
```
功能多、业务复杂
需要多团队协作
```

✅ **需要独立扩展**
```
不同功能的负载差异大
需要按需扩容
```

✅ **需要技术多样性**
```
不同功能适合不同技术栈
```

✅ **快速迭代**
```
需要频繁发布
不同功能独立演进
```

### 不适合微服务

❌ **小型简单应用**
```
功能简单，团队小
微服务带来的复杂度大于收益
```

❌ **初创公司**
```
业务不稳定，频繁调整
微服务拆分困难
```

❌ **团队能力不足**
```
缺乏分布式系统经验
缺乏自动化能力
```

## 八、小结

**核心要点**：

1. **微服务定义**：
   - 将应用拆分为小型独立服务
   - 每个服务独立部署、扩展
   - 通过轻量级机制通信

2. **核心特征**：
   - 按业务能力组织
   - 独立部署
   - 去中心化数据管理
   - 技术栈多样性

3. **优点**：
   - 独立开发部署
   - 故障隔离
   - 技术栈灵活
   - 团队独立
   - 易于扩展

4. **挑战**：
   - 分布式系统复杂性
   - 分布式事务
   - 服务间通信
   - 数据一致性
   - 测试复杂
   - 运维复杂

5. **拆分原则**：
   - 单一职责
   - 高内聚低耦合
   - 避免过度拆分
   - 按业务能力拆分

6. **选择建议**：
   - 大型复杂系统 → 微服务
   - 小型简单应用 → 单体应用
   - 根据团队能力和业务需求选择

**记忆口诀**：
- 微服务独立部署，故障隔离最重要
- 分布式事务难处理，最终一致要记牢
- 服务拆分有原则，高内聚低耦合

---

💡 **提示**：微服务不是银弹！只有在确实需要时才使用，否则会增加不必要的复杂度。
