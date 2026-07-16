# 事件驱动架构

## 一、什么是事件驱动架构？

### 定义

**事件驱动架构（Event-Driven Architecture, EDA）**：通过生产、检测、消费和响应事件来构建系统，组件间通过事件进行松耦合通信。

**类比**：报社订阅
```
传统方式：你每天去报社问"有新报纸吗？"（轮询）
事件驱动：报社有新报纸时主动送到你家（推送）

优势：
- 不用反复询问
- 实时获取信息
- 报社和订户解耦
```

### 核心概念

**事件（Event）**：
```
系统中发生的有意义的状态变化

示例：
- 用户注册成功
- 订单创建完成
- 商品库存不足
- 支付完成
```

**事件生产者（Producer）**：
```
发布事件的组件
```

**事件消费者（Consumer）**：
```
订阅并处理事件的组件
```

**事件总线（Event Bus）**：
```
传递事件的中间件
如：Kafka、RabbitMQ、EventBus
```

## 二、事件驱动 vs 请求驱动

### 请求驱动（传统方式）

```
用户服务 --调用--> 邮件服务.sendEmail()
          --调用--> 积分服务.addPoints()
          --调用--> 通知服务.notify()

问题：
1. 同步调用，响应慢
2. 强耦合，依赖多个服务
3. 某个服务故障，整个流程失败
```

### 事件驱动

```
用户服务：发布"用户注册"事件
    ↓
事件总线
    ↓
邮件服务：监听并发送邮件
积分服务：监听并赠送积分
通知服务：监听并推送通知

优势：
1. 异步处理，响应快
2. 松耦合，服务独立
3. 某个服务故障，不影响其他服务
```

## 三、事件驱动架构的类型

### 1. 简单事件处理（Simple Event Processing）

**特点**：立即处理单个事件

**示例**：
```java
// 用户注册事件
@EventListener
public void onUserRegistered(UserRegisteredEvent event) {
    // 立即发送欢迎邮件
    emailService.sendWelcomeEmail(event.getUser());
}
```

### 2. 事件流处理（Event Stream Processing）

**特点**：处理事件流，可以过滤、聚合、转换

**示例**：
```java
// 实时统计每分钟的订单数
stream.filter(e -> e.getType() == EventType.ORDER_CREATED)
      .window(Duration.ofMinutes(1))
      .count()
      .subscribe(count -> {
          metrics.record("orders_per_minute", count);
      });
```

### 3. 复杂事件处理（Complex Event Processing）

**特点**：从多个事件中识别模式

**示例**：
```java
// 检测欺诈行为：5分钟内同一用户下单超过10次
Pattern<Event> pattern = Pattern
    .<Event>begin("start")
    .where(e -> e.getType() == ORDER_CREATED)
    .times(10)
    .within(Time.minutes(5));

// 触发警报
stream.match(pattern).subscribe(events -> {
    alertService.sendFraudAlert(events.get(0).getUserId());
});
```

## 四、事件驱动架构实现

### 1. 定义事件

```java
// 事件基类
public abstract class DomainEvent {
    private String eventId;
    private LocalDateTime occurredOn;
    
    public DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
}

// 用户注册事件
public class UserRegisteredEvent extends DomainEvent {
    private Long userId;
    private String username;
    private String email;
    
    // 构造函数、getters
}

// 订单创建事件
public class OrderCreatedEvent extends DomainEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private List<OrderItem> items;
    
    // 构造函数、getters
}
```

### 2. 发布事件

```java
// Spring事件发布
@Service
public class UserService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public User registerUser(RegisterRequest request) {
        // 1. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        User saved = userRepository.save(user);
        
        // 2. 发布事件
        UserRegisteredEvent event = new UserRegisteredEvent(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail()
        );
        eventPublisher.publishEvent(event);
        
        return saved;
    }
}
```

### 3. 监听事件

```java
// 同步监听（Spring）
@Component
public class UserEventListener {
    
    @EventListener
    @Async  // 异步处理
    public void onUserRegistered(UserRegisteredEvent event) {
        // 发送欢迎邮件
        emailService.sendWelcomeEmail(
            event.getEmail(),
            event.getUsername()
        );
    }
}

// 异步监听（Kafka）
@Component
public class OrderEventConsumer {
    
    @KafkaListener(topics = "order-created")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // 扣减库存
        inventoryService.decreaseStock(event.getItems());
    }
}
```

### 4. 使用消息队列

**Kafka示例**：
```java
// 生产者
@Service
public class EventPublisher {
    
    @Autowired
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;
    
    public void publish(String topic, DomainEvent event) {
        kafkaTemplate.send(topic, event.getEventId(), event);
    }
}

// 消费者
@Component
public class EventConsumer {
    
    @KafkaListener(topics = "user-registered", groupId = "email-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event);
    }
    
    @KafkaListener(topics = "user-registered", groupId = "points-service")
    public void handleUserRegistered2(UserRegisteredEvent event) {
        pointsService.grantWelcomePoints(event.getUserId());
    }
}
```

## 五、事件驱动的优点

### 1. 松耦合

```
生产者不需要知道消费者
新增消费者不影响生产者

示例：
用户服务发布"用户注册"事件
- 当前：邮件服务、积分服务监听
- 新增：推荐服务也可以监听（不用修改用户服务）
```

### 2. 异步处理

```
不阻塞主流程，提升响应速度

用户注册：
同步：注册 + 发邮件 + 赠送积分 = 3秒
异步：注册（1秒）+ 后台处理邮件和积分
```

### 3. 可扩展性

```
可以独立扩展消费者

订单创建事件：
- 库存服务：3个实例
- 通知服务：5个实例（通知量大）
- 日志服务：1个实例
```

### 4. 故障隔离

```
某个消费者故障不影响其他消费者

邮件服务挂了 → 积分服务仍然正常
消息会保留，邮件服务恢复后继续处理
```

### 5. 审计和重放

```
事件存储后可以：
- 审计：查看历史事件
- 重放：重新处理历史事件
- 分析：事件流分析
```

## 六、事件驱动的挑战

### 1. 最终一致性

**问题**：
```
用户注册成功 → 发布事件
邮件服务还没处理 → 用户还没收到邮件
这段时间数据不一致
```

**解决方案**：
```
1. 接受最终一致性（大多数场景可接受）
2. 补偿机制（失败重试）
3. 用户界面提示（"欢迎邮件将在5分钟内送达"）
```

### 2. 事件顺序

**问题**：
```
事件1：修改用户名为"张三"
事件2：修改用户名为"李四"

如果事件2先处理，事件1后处理 → 数据错误
```

**解决方案**：
```java
// 使用版本号
public class UserUpdatedEvent {
    private Long userId;
    private String newName;
    private int version;  // 版本号
}

// 消费者检查版本号
@EventListener
public void onUserUpdated(UserUpdatedEvent event) {
    User user = userRepository.findById(event.getUserId());
    if (user.getVersion() < event.getVersion()) {
        user.setName(event.getNewName());
        user.setVersion(event.getVersion());
        userRepository.save(user);
    }
}
```

### 3. 重复消费

**问题**：
```
消息队列可能重复投递消息
消费者可能处理同一事件多次
```

**解决方案**：
```java
// 幂等性处理
@EventListener
public void onOrderCreated(OrderCreatedEvent event) {
    // 检查是否已处理
    if (processedEvents.contains(event.getEventId())) {
        return;  // 已处理，直接返回
    }
    
    // 处理事件
    inventoryService.decreaseStock(event.getItems());
    
    // 记录已处理
    processedEvents.add(event.getEventId());
}
```

### 4. 调试困难

**问题**：
```
异步处理，调试链路复杂
难以追踪事件流
```

**解决方案**：
```
1. 链路追踪：Zipkin、Skywalking
2. 日志关联：使用TraceId
3. 事件存储：记录所有事件
```

### 5. 事务边界

**问题**：
```
用户注册 + 发布事件
如果发布事件失败，用户已经保存？
```

**解决方案**：
```java
// 事务性发件箱模式（Transactional Outbox）
@Transactional
public User registerUser(RegisterRequest request) {
    // 1. 保存用户
    User user = userRepository.save(new User(request));
    
    // 2. 保存事件到outbox表（同一事务）
    OutboxEvent outboxEvent = new OutboxEvent(
        "user-registered",
        new UserRegisteredEvent(user)
    );
    outboxRepository.save(outboxEvent);
    
    // 3. 后台任务轮询outbox表，发布事件
    return user;
}
```

## 七、事件驱动最佳实践

### 实践1：事件命名

```
✅ 好的命名：
- UserRegisteredEvent（过去式，表示已发生）
- OrderCreatedEvent
- PaymentCompletedEvent

❌ 不好的命名：
- UserRegisterEvent（动词原形）
- CreateOrderEvent
- UserEvent（太泛）
```

### 实践2：事件粒度

```
✅ 合适的粒度：
- UserRegisteredEvent（用户注册）
- OrderCreatedEvent（订单创建）

❌ 过细粒度：
- UserIdSetEvent（用户ID设置）
- UserNameSetEvent（用户名设置）

❌ 过粗粒度：
- UserChangedEvent（用户变更，太泛）
```

### 实践3：事件不可变

```java
// ✅ 不可变事件
public final class OrderCreatedEvent {
    private final Long orderId;
    private final BigDecimal amount;
    
    public OrderCreatedEvent(Long orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
    
    // 只有getters，没有setters
}
```

### 实践4：幂等性处理

```java
// ✅ 幂等的消费者
@EventListener
public void onOrderCreated(OrderCreatedEvent event) {
    // 检查是否已处理
    if (orderProcessedRepository.exists(event.getOrderId())) {
        return;
    }
    
    // 处理事件
    processOrder(event);
    
    // 标记已处理
    orderProcessedRepository.save(event.getOrderId());
}
```

### 实践5：事件版本化

```java
// ✅ 版本化事件
public class UserRegisteredEventV1 {
    private Long userId;
    private String username;
}

public class UserRegisteredEventV2 {
    private Long userId;
    private String username;
    private String email;  // 新增字段
}

// 消费者支持多版本
@EventListener
public void onUserRegistered(UserRegisteredEventV1 event) {
    // 处理V1
}

@EventListener
public void onUserRegistered(UserRegisteredEventV2 event) {
    // 处理V2
}
```

## 八、小结

**核心要点**：

1. **事件驱动定义**：
   - 通过事件进行通信
   - 组件间松耦合
   - 异步处理

2. **核心概念**：
   - 事件：状态变化
   - 生产者：发布事件
   - 消费者：处理事件
   - 事件总线：传递事件

3. **优点**：
   - 松耦合
   - 异步处理
   - 可扩展性
   - 故障隔离

4. **挑战**：
   - 最终一致性
   - 事件顺序
   - 重复消费
   - 调试困难

5. **最佳实践**：
   - 事件命名规范
   - 合适的事件粒度
   - 事件不可变
   - 幂等性处理
   - 事件版本化

**记忆口诀**：
- 事件驱动松耦合，异步处理响应快
- 最终一致要接受，幂等处理防重复
- 事件命名用过去式，不可变性要保证

---

💡 **提示**：事件驱动架构适合需要松耦合、高扩展性的系统，但要接受最终一致性！
