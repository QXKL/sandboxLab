# 事件驱动架构 - 测试题

## 一、选择题

### 1. 事件驱动架构的核心特征是什么？
A. 同步调用  
B. 通过事件进行松耦合通信  
C. 强依赖  
D. 实时一致性

**答案**：B

**解析**：事件驱动架构的核心是通过事件进行松耦合的异步通信。

---

### 2. 以下哪个不是事件驱动架构的优点？
A. 松耦合  
B. 异步处理  
C. 强一致性  
D. 故障隔离

**答案**：C

**解析**：事件驱动架构是最终一致性，不是强一致性。

---

### 3. 事件命名应该使用什么时态？
A. 现在时  
B. 过去式  
C. 将来时  
D. 进行时

**答案**：B

**解析**：事件表示已经发生的事情，应该使用过去式，如UserRegisteredEvent。

---

### 4. 如何解决事件重复消费问题？
A. 忽略  
B. 幂等性处理  
C. 加锁  
D. 使用事务

**答案**：B

**解析**：通过幂等性处理，确保同一事件处理多次的结果与处理一次相同。

---

### 5. 事务性发件箱模式（Transactional Outbox）解决什么问题？
A. 性能问题  
B. 事务边界问题  
C. 并发问题  
D. 安全问题

**答案**：B

**解析**：解决保存数据和发布事件的事务一致性问题。

---

## 二、填空题

### 1. 事件驱动架构中，发布事件的组件称为________，处理事件的组件称为________。

**答案**：生产者（Producer）、消费者（Consumer）

---

### 2. 事件驱动架构实现的是________一致性，而不是强一致性。

**答案**：最终

---

### 3. 事件应该是________的，一旦创建不应该被修改。

**答案**：不可变

---

### 4. 为了防止事件重复消费，消费者应该实现________。

**答案**：幂等性

---

### 5. 事件命名应该使用________时态，如UserRegisteredEvent。

**答案**：过去式

---

## 三、判断题

### 1. 事件驱动架构是同步通信。（ ）

**答案**：✗

**解析**：事件驱动架构是异步通信。

---

### 2. 事件驱动架构可以实现组件间的松耦合。（ ）

**答案**：✓

**解析**：生产者和消费者通过事件解耦，互不依赖。

---

### 3. 事件驱动架构保证强一致性。（ ）

**答案**：✗

**解析**：事件驱动架构是最终一致性。

---

### 4. 事件一旦发布就不能修改。（ ）

**答案**：✓

**解析**：事件应该是不可变的。

---

### 5. 事件驱动架构比请求驱动更容易调试。（ ）

**答案**：✗

**解析**：事件驱动的异步特性使调试更复杂，需要链路追踪工具。

---

## 四、简答题

### 1. 比较事件驱动架构和请求驱动架构的区别。

**答案**：

**请求驱动（Request-Driven）**：

```java
// 同步调用
@Service
public class UserService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private PointsService pointsService;
    
    public User registerUser(RegisterRequest request) {
        // 1. 创建用户
        User user = userRepository.save(new User(request));
        
        // 2. 同步调用
        emailService.sendWelcomeEmail(user);  // 阻塞
        pointsService.grantPoints(user.getId());  // 阻塞
        
        return user;  // 响应慢
    }
}
```

**特点**：
- 同步调用
- 强耦合（依赖多个服务）
- 响应慢（等待所有调用完成）
- 某个服务故障，整个流程失败

---

**事件驱动（Event-Driven）**：

```java
// 异步事件
@Service
public class UserService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public User registerUser(RegisterRequest request) {
        // 1. 创建用户
        User user = userRepository.save(new User(request));
        
        // 2. 发布事件（异步）
        eventPublisher.publishEvent(new UserRegisteredEvent(user));
        
        return user;  // 立即返回
    }
}

// 消费者独立处理
@Component
public class EmailEventListener {
    @EventListener
    @Async
    public void onUserRegistered(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event.getUser());
    }
}

@Component
public class PointsEventListener {
    @EventListener
    @Async
    public void onUserRegistered(UserRegisteredEvent event) {
        pointsService.grantPoints(event.getUserId());
    }
}
```

**特点**：
- 异步处理
- 松耦合（通过事件解耦）
- 响应快（不等待后续处理）
- 某个消费者故障，不影响其他消费者

---

**对比总结**：

| 特性 | 请求驱动 | 事件驱动 |
|-----|---------|---------|
| **通信方式** | 同步 | 异步 |
| **耦合度** | 强耦合 | 松耦合 |
| **响应速度** | 慢 | 快 |
| **故障影响** | 全局 | 隔离 |
| **可扩展性** | 低 | 高 |
| **一致性** | 强一致 | 最终一致 |
| **调试难度** | 简单 | 复杂 |

---

### 2. 说明事件驱动架构如何处理事件重复消费的问题。

**答案**：

**问题场景**：

消息队列可能重复投递消息：
```
1. 消费者处理完消息
2. 准备发送ACK确认
3. 网络故障，ACK丢失
4. 消息队列认为消息未处理，再次投递
5. 消费者重复处理同一消息
```

---

**解决方案：幂等性处理**

**方案1：使用事件ID去重**

```java
@Component
public class OrderEventListener {
    
    @Autowired
    private ProcessedEventRepository processedEventRepo;
    
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        // 1. 检查是否已处理
        if (processedEventRepo.exists(event.getEventId())) {
            log.info("事件已处理，跳过: {}", event.getEventId());
            return;
        }
        
        // 2. 处理事件
        inventoryService.decreaseStock(event.getItems());
        
        // 3. 记录已处理
        processedEventRepo.save(new ProcessedEvent(event.getEventId()));
    }
}
```

---

**方案2：业务幂等性**

```java
@EventListener
public void onOrderCreated(OrderCreatedEvent event) {
    // 使用订单ID作为幂等键
    // 数据库唯一约束保证不会重复插入
    try {
        inventoryLog.insert(new InventoryLog(
            event.getOrderId(),  // 唯一键
            event.getItems()
        ));
        inventoryService.decreaseStock(event.getItems());
    } catch (DuplicateKeyException e) {
        // 已处理过，忽略
        log.info("订单已处理: {}", event.getOrderId());
    }
}
```

---

**方案3：分布式锁**

```java
@EventListener
public void onOrderCreated(OrderCreatedEvent event) {
    String lockKey = "order:" + event.getOrderId();
    
    // 尝试获取分布式锁
    if (redisLock.tryLock(lockKey, 10, TimeUnit.SECONDS)) {
        try {
            // 处理事件
            inventoryService.decreaseStock(event.getItems());
        } finally {
            redisLock.unlock(lockKey);
        }
    } else {
        log.info("其他实例正在处理: {}", event.getOrderId());
    }
}
```

---

**方案4：数据库唯一约束**

```sql
CREATE TABLE inventory_decrease_log (
    order_id BIGINT PRIMARY KEY,  -- 唯一约束
    product_id BIGINT,
    quantity INT,
    created_at TIMESTAMP
);
```

```java
@EventListener
public void onOrderCreated(OrderCreatedEvent event) {
    try {
        // 插入日志（唯一约束）
        inventoryLogRepository.insert(new InventoryLog(event.getOrderId()));
        
        // 扣减库存
        inventoryService.decreaseStock(event.getItems());
    } catch (DuplicateKeyException e) {
        // 已处理过
    }
}
```

---

**最佳实践**：

1. **设计幂等的业务逻辑**
2. **使用事件ID或业务ID去重**
3. **数据库唯一约束保证幂等**
4. **记录处理状态**

---

### 3. 什么是事务性发件箱模式（Transactional Outbox）？它解决什么问题？

**答案**：

**问题背景**：

在事件驱动架构中，需要保证：
1. 业务数据保存成功
2. 事件发布成功

如果分两步执行，可能出现不一致：

```java
// ❌ 问题示例
@Transactional
public User registerUser(RegisterRequest request) {
    // 1. 保存用户（事务内）
    User user = userRepository.save(new User(request));
    
    // 2. 发布事件（事务外）
    eventPublisher.publish(new UserRegisteredEvent(user));
    // 问题：如果发布失败，用户已保存，但没有发送欢迎邮件
    
    return user;
}
```

**可能的问题**：
- 用户保存成功，事件发布失败 → 没有发送欢迎邮件
- 事件发布成功，用户保存失败（事务回滚）→ 发送了邮件但用户不存在

---

**事务性发件箱模式**：

**核心思想**：
1. 业务数据和事件一起保存到数据库（同一事务）
2. 后台任务轮询事件表，发布事件

**实现步骤**：

**1. 创建Outbox表**

```sql
CREATE TABLE outbox_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id VARCHAR(36) UNIQUE NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP NULL
);
```

**2. 保存业务数据和事件（同一事务）**

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    
    @Transactional
    public User registerUser(RegisterRequest request) {
        // 1. 保存用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        User saved = userRepository.save(user);
        
        // 2. 保存事件到outbox表（同一事务）
        UserRegisteredEvent event = new UserRegisteredEvent(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail()
        );
        
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventId(UUID.randomUUID().toString());
        outboxEvent.setEventType("UserRegistered");
        outboxEvent.setPayload(objectMapper.writeValueAsString(event));
        outboxEventRepository.save(outboxEvent);
        
        // 3. 提交事务
        // 用户和事件都保存成功，或者都失败
        return saved;
    }
}
```

**3. 后台任务轮询并发布事件**

```java
@Component
public class OutboxEventPublisher {
    
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Scheduled(fixedDelay = 1000)  // 每秒执行一次
    @Transactional
    public void publishEvents() {
        // 查询未处理的事件
        List<OutboxEvent> events = outboxEventRepository
            .findByProcessedFalse(PageRequest.of(0, 100));
        
        for (OutboxEvent event : events) {
            try {
                // 发布到Kafka
                kafkaTemplate.send(
                    event.getEventType(),
                    event.getEventId(),
                    event.getPayload()
                );
                
                // 标记为已处理
                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
                outboxEventRepository.save(event);
                
            } catch (Exception e) {
                log.error("发布事件失败: {}", event.getEventId(), e);
                // 下次继续重试
            }
        }
    }
}
```

---

**优点**：

1. **原子性**：业务数据和事件在同一事务中
2. **可靠性**：事件不会丢失
3. **最终一致性**：事件最终会被发布

**缺点**：

1. **延迟**：事件不是立即发布
2. **复杂度**：需要额外的表和后台任务

---

**变种：CDC（Change Data Capture）**

使用数据库binlog捕获变更：
```
1. 应用只保存业务数据
2. CDC工具（Debezium）监听数据库变更
3. 自动发布事件到消息队列

优点：应用代码简单
缺点：依赖CDC工具
```

---

💡 **提示**：事件驱动架构适合需要松耦合和高扩展性的系统，但要接受最终一致性！
