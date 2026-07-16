# CQRS架构

## 一、什么是CQRS？

### 定义

**CQRS（Command Query Responsibility Segregation）**：命令查询职责分离，将系统的读操作（查询）和写操作（命令）分离到不同的模型中。

**类比**：图书馆系统
```
传统方式：
- 借书、还书、查询书籍都用同一个系统
- 借还书操作复杂（需要验证、记录、更新）
- 查询简单但频繁

CQRS方式：
- 写操作：借书还书系统（复杂逻辑）
- 读操作：书籍查询系统（优化查询）
- 两个系统独立，互不影响
```

### 核心思想

```
传统架构：
┌──────────────────┐
│   应用服务       │
│  (增删改查)      │
└────────┬─────────┘
         ↓
┌────────────────┐
│   领域模型     │
└────────┬───────┘
         ↓
┌────────────────┐
│    数据库      │
└────────────────┘

CQRS架构：
┌──────────┐        ┌──────────┐
│命令模型  │        │查询模型  │
│(写操作)  │        │(读操作)  │
└────┬─────┘        └────┬─────┘
     ↓                   ↓
┌─────────┐        ┌─────────┐
│写数据库 │  同步  │读数据库 │
│(规范化) │ ────→ │(反规范) │
└─────────┘        └─────────┘
```

## 二、CQRS的核心概念

### 1. 命令（Command）

**定义**：改变系统状态的操作

**特点**：
- 有副作用（修改数据）
- 返回成功/失败，不返回数据
- 需要验证和业务规则

**示例**：
```java
// 创建订单命令
public class CreateOrderCommand {
    private Long userId;
    private List<OrderItem> items;
    private String shippingAddress;
    
    // getters
}

// 取消订单命令
public class CancelOrderCommand {
    private Long orderId;
    private String reason;
    
    // getters
}
```

### 2. 查询（Query）

**定义**：读取系统状态的操作

**特点**：
- 无副作用（不修改数据）
- 返回数据
- 可以优化（缓存、索引、非规范化）

**示例**：
```java
// 查询订单详情
public class GetOrderDetailsQuery {
    private Long orderId;
    
    // getters
}

// 查询用户订单列表
public class GetUserOrdersQuery {
    private Long userId;
    private int page;
    private int size;
    
    // getters
}
```

### 3. 命令处理器

```java
@Component
public class CreateOrderCommandHandler {
    
    @Autowired
    private OrderRepository orderRepository;
    
    public OrderId handle(CreateOrderCommand command) {
        // 1. 验证
        validateCommand(command);
        
        // 2. 创建领域对象
        Order order = Order.create(
            command.getUserId(),
            command.getItems(),
            command.getShippingAddress()
        );
        
        // 3. 保存
        orderRepository.save(order);
        
        // 4. 返回ID（不返回完整对象）
        return order.getId();
    }
}
```

### 4. 查询处理器

```java
@Component
public class GetOrderDetailsQueryHandler {
    
    @Autowired
    private OrderQueryRepository queryRepository;
    
    public OrderDetailsDTO handle(GetOrderDetailsQuery query) {
        // 直接查询，不经过领域模型
        return queryRepository.findOrderDetails(query.getOrderId());
    }
}
```

## 三、CQRS的优点

### 1. 读写分离

```
写操作：
- 复杂的业务逻辑
- 规范化数据库
- 强一致性

读操作：
- 简单的查询
- 非规范化数据库（为查询优化）
- 最终一致性
```

### 2. 性能优化

```
读数据库：
- 反规范化（减少JOIN）
- 添加冗余字段
- 添加索引
- 使用缓存

写数据库：
- 规范化（保证一致性）
- 减少索引（提高写入性能）
```

### 3. 独立扩展

```
读多写少的场景：
- 写数据库：1个实例
- 读数据库：10个实例（读压力大）

按需扩展，成本优化
```

### 4. 技术选型灵活

```
写数据库：PostgreSQL（ACID事务）
读数据库：Elasticsearch（全文搜索）
         Redis（缓存）
         MongoDB（文档查询）
```

## 四、CQRS实现示例

### 简单CQRS（共享数据库）

```java
// 命令服务（写）
@Service
public class OrderCommandService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Transactional
    public Long createOrder(CreateOrderCommand command) {
        Order order = new Order();
        order.setUserId(command.getUserId());
        order.setItems(command.getItems());
        order.setStatus(OrderStatus.PENDING);
        
        Order saved = orderRepository.save(order);
        return saved.getId();
    }
    
    @Transactional
    public void cancelOrder(CancelOrderCommand command) {
        Order order = orderRepository.findById(command.getOrderId())
            .orElseThrow(() -> new OrderNotFoundException());
        
        order.cancel(command.getReason());
        orderRepository.save(order);
    }
}

// 查询服务（读）
@Service
public class OrderQueryService {
    
    @Autowired
    private OrderQueryRepository queryRepository;
    
    public OrderDetailsDTO getOrderDetails(Long orderId) {
        // 使用优化的查询
        return queryRepository.findOrderDetailsById(orderId);
    }
    
    public List<OrderSummaryDTO> getUserOrders(Long userId, Pageable pageable) {
        // 返回DTO，不返回实体
        return queryRepository.findOrderSummariesByUserId(userId, pageable);
    }
}

// 查询仓储（优化查询）
@Repository
public interface OrderQueryRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT new com.example.OrderDetailsDTO(o.id, o.userId, o.status, " +
           "u.username, SUM(i.price * i.quantity)) " +
           "FROM Order o " +
           "JOIN User u ON o.userId = u.id " +
           "JOIN OrderItem i ON i.orderId = o.id " +
           "WHERE o.id = :orderId " +
           "GROUP BY o.id, o.userId, o.status, u.username")
    OrderDetailsDTO findOrderDetailsById(@Param("orderId") Long orderId);
}
```

### 完整CQRS（分离数据库）

```java
// 写模型
@Service
public class OrderCommandService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Transactional
    public Long createOrder(CreateOrderCommand command) {
        // 1. 创建订单（写数据库）
        Order order = Order.create(command);
        orderRepository.save(order);
        
        // 2. 发布事件
        eventPublisher.publish(new OrderCreatedEvent(order));
        
        return order.getId();
    }
}

// 读模型（监听事件，更新查询数据库）
@Component
public class OrderReadModelUpdater {
    
    @Autowired
    private OrderReadModelRepository readModelRepository;
    
    @EventListener
    public void on(OrderCreatedEvent event) {
        // 更新读数据库
        OrderReadModel readModel = new OrderReadModel();
        readModel.setOrderId(event.getOrderId());
        readModel.setUserId(event.getUserId());
        readModel.setUserName(event.getUserName());  // 冗余字段
        readModel.setTotalAmount(event.getTotalAmount());
        readModel.setStatus(event.getStatus());
        
        readModelRepository.save(readModel);
    }
    
    @EventListener
    public void on(OrderCancelledEvent event) {
        // 更新订单状态
        OrderReadModel readModel = readModelRepository.findById(event.getOrderId());
        readModel.setStatus(OrderStatus.CANCELLED);
        readModelRepository.save(readModel);
    }
}

// 查询服务（读数据库）
@Service
public class OrderQueryService {
    
    @Autowired
    private OrderReadModelRepository readModelRepository;
    
    public OrderDetailsDTO getOrderDetails(Long orderId) {
        // 直接从读数据库查询（已经是DTO格式）
        return readModelRepository.findById(orderId);
    }
}
```

## 五、CQRS + Event Sourcing

### Event Sourcing定义

**Event Sourcing**：不存储当前状态，而是存储所有状态变化的事件。

```
传统方式：
订单状态 = CANCELLED（只存储当前状态）

Event Sourcing：
事件流：
1. OrderCreated（订单创建）
2. OrderPaid（订单支付）
3. OrderShipped（订单发货）
4. OrderCancelled（订单取消）

当前状态 = 重放所有事件得到
```

### 实现示例

```java
// 事件存储
@Entity
public class OrderEvent {
    @Id
    private Long id;
    private Long orderId;
    private String eventType;
    private String eventData;
    private LocalDateTime occurredAt;
    private int version;  // 事件版本号
}

// 聚合根
public class Order {
    private Long id;
    private OrderStatus status;
    private List<OrderEvent> events = new ArrayList<>();
    
    // 从事件重建状态
    public static Order fromEvents(List<OrderEvent> events) {
        Order order = new Order();
        for (OrderEvent event : events) {
            order.apply(event);
        }
        return order;
    }
    
    // 应用事件
    private void apply(OrderEvent event) {
        switch (event.getEventType()) {
            case "OrderCreated":
                // 设置初始状态
                this.id = event.getOrderId();
                this.status = OrderStatus.PENDING;
                break;
            case "OrderPaid":
                this.status = OrderStatus.PAID;
                break;
            case "OrderCancelled":
                this.status = OrderStatus.CANCELLED;
                break;
        }
    }
    
    // 创建订单
    public static Order create(CreateOrderCommand command) {
        Order order = new Order();
        OrderCreatedEvent event = new OrderCreatedEvent(command);
        order.applyAndRecord(event);
        return order;
    }
    
    // 应用并记录事件
    private void applyAndRecord(OrderEvent event) {
        apply(event);
        events.add(event);
    }
}

// 事件存储仓储
@Repository
public class OrderEventStore {
    
    public void save(Order order) {
        // 保存新事件
        for (OrderEvent event : order.getUncommittedEvents()) {
            eventRepository.save(event);
        }
    }
    
    public Order load(Long orderId) {
        // 加载所有事件
        List<OrderEvent> events = eventRepository.findByOrderId(orderId);
        
        // 从事件重建状态
        return Order.fromEvents(events);
    }
}
```

## 六、CQRS的挑战

### 1. 最终一致性

```
问题：
写操作完成 → 读操作还没更新
用户可能读到旧数据

解决方案：
1. 接受最终一致性
2. UI提示（"数据更新中..."）
3. 使用版本号或时间戳
```

### 2. 复杂度增加

```
需要维护：
- 命令模型
- 查询模型
- 同步机制
- 事件处理

小型项目可能过度设计
```

### 3. 学习曲线

```
开发团队需要理解：
- CQRS概念
- 事件驱动
- 最终一致性
- Event Sourcing（如果使用）
```

## 七、何时使用CQRS

### 适合使用

✅ **读写操作差异大**
```
读操作：复杂查询、多表JOIN、聚合统计
写操作：简单的CRUD

CQRS可以分别优化
```

✅ **读多写少**
```
读操作是写操作的100倍
可以独立扩展读数据库
```

✅ **需要不同的数据表示**
```
写：规范化数据库
读：Elasticsearch、Redis、图数据库
```

✅ **高性能要求**
```
需要极致的查询性能
```

### 不适合使用

❌ **简单CRUD应用**
```
读写逻辑简单
CQRS带来的复杂度大于收益
```

❌ **强一致性要求**
```
必须读到最新数据
不能接受最终一致性
```

❌ **团队能力不足**
```
团队不熟悉CQRS、事件驱动
```

## 八、小结

**核心要点**：

1. **CQRS定义**：
   - 命令查询职责分离
   - 读写分离到不同模型

2. **核心概念**：
   - 命令：修改状态，返回成功/失败
   - 查询：读取状态，返回数据
   - 命令处理器、查询处理器

3. **优点**：
   - 读写分离
   - 性能优化
   - 独立扩展
   - 技术选型灵活

4. **挑战**：
   - 最终一致性
   - 复杂度增加
   - 学习曲线

5. **适用场景**：
   - 读写差异大
   - 读多写少
   - 高性能要求

6. **Event Sourcing**：
   - 存储事件而非状态
   - 可以重放历史
   - 审计友好

**记忆口诀**：
- CQRS读写分离，命令查询要分清
- 最终一致要接受，复杂场景才使用
- 读多写少最适合，简单应用不推荐

---

💡 **提示**：CQRS不是银弹！只在确实需要时使用，否则会增加不必要的复杂度。
