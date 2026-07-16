# CQRS架构 - 测试题

## 一、选择题

### 1. CQRS的全称是什么？
A. Command Query Responsibility Separation  
B. Command Query Responsibility Segregation  
C. Command Query Response Segregation  
D. Command Query Resource Segregation

**答案**：B

**解析**：CQRS是Command Query Responsibility Segregation（命令查询职责分离）。

---

### 2. CQRS的核心思想是什么？
A. 读写使用同一模型  
B. 读写分离到不同模型  
C. 只读不写  
D. 只写不读

**答案**：B

**解析**：CQRS将读操作（查询）和写操作（命令）分离到不同的模型中。

---

### 3. 命令（Command）的特点是什么？
A. 返回数据  
B. 无副作用  
C. 改变系统状态  
D. 可以缓存

**答案**：C

**解析**：命令改变系统状态，有副作用，返回成功/失败而不返回数据。

---

### 4. CQRS实现的是什么一致性？
A. 强一致性  
B. 最终一致性  
C. 弱一致性  
D. 因果一致性

**答案**：B

**解析**：CQRS（特别是读写分离数据库时）实现的是最终一致性。

---

### 5. Event Sourcing是什么？
A. 存储当前状态  
B. 存储所有事件  
C. 存储快照  
D. 存储日志

**答案**：B

**解析**：Event Sourcing不存储当前状态，而是存储所有状态变化的事件。

---

## 二、填空题

### 1. CQRS将系统分为________（写操作）和________（读操作）两个模型。

**答案**：命令模型、查询模型

---

### 2. 命令操作改变系统状态，返回________，而查询操作返回________。

**答案**：成功/失败（或：状态）、数据

---

### 3. CQRS的读写分离可以实现________一致性，而不是强一致性。

**答案**：最终

---

### 4. Event Sourcing通过存储________来重建系统当前状态。

**答案**：事件（或：所有事件）

---

### 5. CQRS适合________（读多/写多）的场景。

**答案**：读多写少

---

## 三、判断题

### 1. CQRS要求必须使用两个独立的数据库。（ ）

**答案**：✗

**解析**：CQRS可以共享数据库（简单CQRS），也可以使用独立数据库（完整CQRS）。

---

### 2. 命令（Command）应该返回查询结果。（ ）

**答案**：✗

**解析**：命令只返回成功/失败或ID，不返回查询结果。

---

### 3. CQRS适合所有类型的应用。（ ）

**答案**：✗

**解析**：CQRS不适合简单CRUD应用，会增加不必要的复杂度。

---

### 4. Event Sourcing可以重放历史事件。（ ）

**答案**：✓

**解析**：Event Sourcing存储所有事件，可以重放历史事件重建任意时刻的状态。

---

### 5. CQRS的读模型可以使用与写模型不同的数据库技术。（ ）

**答案**：✓

**解析**：这是CQRS的优点之一，读写可以使用不同的数据库技术（如写用PostgreSQL，读用Elasticsearch）。

---

## 四、简答题

### 1. 说明CQRS的命令和查询有什么区别。

**答案**：

**命令（Command）**：

**定义**：改变系统状态的操作

**特点**：
- **有副作用**：修改数据
- **返回值**：成功/失败或ID，不返回数据
- **需要验证**：业务规则、权限检查
- **不可缓存**：每次都要执行

**示例**：
```java
// 创建订单命令
public class CreateOrderCommand {
    private Long userId;
    private List<OrderItem> items;
    
    // 只包含必要的输入数据
}

// 命令处理器
@Component
public class CreateOrderCommandHandler {
    
    public Long handle(CreateOrderCommand command) {
        // 1. 验证命令
        validateCommand(command);
        
        // 2. 执行业务逻辑
        Order order = Order.create(command);
        orderRepository.save(order);
        
        // 3. 只返回ID，不返回完整数据
        return order.getId();
    }
}
```

---

**查询（Query）**：

**定义**：读取系统状态的操作

**特点**：
- **无副作用**：不修改数据
- **返回值**：返回数据（DTO）
- **不需要验证**：只读操作
- **可缓存**：相同查询返回相同结果

**示例**：
```java
// 查询订单详情
public class GetOrderDetailsQuery {
    private Long orderId;
}

// 查询处理器
@Component
public class GetOrderDetailsQueryHandler {
    
    public OrderDetailsDTO handle(GetOrderDetailsQuery query) {
        // 直接查询，不经过领域模型
        return orderQueryRepository.findOrderDetails(query.getOrderId());
    }
}
```

---

**对比总结**：

| 特性 | 命令（Command） | 查询（Query） |
|-----|----------------|--------------|
| **副作用** | 有（修改数据） | 无（只读） |
| **返回值** | 成功/失败或ID | 数据（DTO） |
| **验证** | 需要 | 不需要 |
| **缓存** | 不可缓存 | 可缓存 |
| **业务逻辑** | 复杂 | 简单 |
| **优化方向** | 事务一致性 | 查询性能 |

---

**实际应用**：

```java
// Controller层分离命令和查询
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    // 命令：创建订单
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody CreateOrderCommand command) {
        Long orderId = commandService.createOrder(command);
        return ResponseEntity.ok(orderId);  // 只返回ID
    }
    
    // 查询：获取订单详情
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDTO> getOrder(@PathVariable Long id) {
        OrderDetailsDTO dto = queryService.getOrderDetails(id);
        return ResponseEntity.ok(dto);  // 返回完整数据
    }
}
```

---

### 2. 什么是Event Sourcing？它与CQRS有什么关系？

**答案**：

**Event Sourcing定义**：

不存储当前状态，而是存储所有导致当前状态的事件序列。当前状态可以通过重放所有事件来重建。

---

**核心概念**：

**传统方式**：
```
数据库存储当前状态：
Order表：
id=1, status=CANCELLED, amount=1000

只知道当前状态是"已取消"，不知道历史
```

**Event Sourcing**：
```
事件存储：
1. OrderCreatedEvent (orderId=1, amount=1000)
2. OrderPaidEvent (orderId=1)
3. OrderShippedEvent (orderId=1)
4. OrderCancelledEvent (orderId=1)

当前状态 = 重放所有事件：
1. 创建 → status=PENDING
2. 支付 → status=PAID
3. 发货 → status=SHIPPED
4. 取消 → status=CANCELLED

既知道当前状态，也知道完整历史
```

---

**实现示例**：

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
    private int version;
}

// 聚合根
public class Order {
    private Long id;
    private OrderStatus status;
    private BigDecimal amount;
    
    // 从事件重建状态
    public static Order fromEvents(List<OrderEvent> events) {
        Order order = new Order();
        for (OrderEvent event : events) {
            order.apply(event);
        }
        return order;
    }
    
    // 应用事件改变状态
    private void apply(OrderEvent event) {
        switch (event.getEventType()) {
            case "OrderCreated":
                this.status = OrderStatus.PENDING;
                break;
            case "OrderPaid":
                this.status = OrderStatus.PAID;
                break;
            case "OrderShipped":
                this.status = OrderStatus.SHIPPED;
                break;
            case "OrderCancelled":
                this.status = OrderStatus.CANCELLED;
                break;
        }
    }
}
```

---

**Event Sourcing与CQRS的关系**：

**1. 互补而非必需**
```
CQRS：可以不使用Event Sourcing
Event Sourcing：可以不使用CQRS

但两者结合效果最好
```

**2. 天然适配**
```
CQRS + Event Sourcing：

命令模型（写）：
- 生成事件并存储
- 不存储当前状态

查询模型（读）：
- 监听事件
- 更新读数据库
- 优化查询性能
```

**3. 实现方式**
```java
// 命令端：存储事件
@Service
public class OrderCommandService {
    
    @Autowired
    private EventStore eventStore;
    
    public void createOrder(CreateOrderCommand command) {
        // 1. 创建领域对象
        Order order = Order.create(command);
        
        // 2. 获取未提交的事件
        List<OrderEvent> events = order.getUncommittedEvents();
        
        // 3. 存储事件（而不是存储状态）
        eventStore.save(events);
        
        // 4. 发布事件
        events.forEach(eventPublisher::publish);
    }
}

// 查询端：监听事件，更新读模型
@Component
public class OrderReadModelUpdater {
    
    @EventListener
    public void on(OrderCreatedEvent event) {
        // 更新读数据库
        OrderReadModel model = new OrderReadModel();
        model.setOrderId(event.getOrderId());
        model.setStatus("PENDING");
        readModelRepository.save(model);
    }
    
    @EventListener
    public void on(OrderCancelledEvent event) {
        // 更新状态
        OrderReadModel model = readModelRepository.findById(event.getOrderId());
        model.setStatus("CANCELLED");
        readModelRepository.save(model);
    }
}
```

---

**Event Sourcing的优点**：

1. **完整历史**：可以查看任意时刻的状态
2. **审计友好**：天然的审计日志
3. **时间旅行**：可以重放历史
4. **业务洞察**：分析事件流
5. **调试友好**：可以重现问题

**Event Sourcing的缺点**：

1. **复杂度高**：需要事件版本化、快照
2. **查询困难**：需要重建状态
3. **事件膨胀**：事件越来越多
4. **学习曲线**：概念复杂

---

**总结**：

```
CQRS：命令查询分离
Event Sourcing：存储事件而非状态

两者结合：
- 写端存储事件
- 读端监听事件，构建查询模型
- 既有CQRS的优点，又有Event Sourcing的完整历史
```

---

### 3. 说明CQRS适合什么场景，不适合什么场景。

**答案**：

**适合使用CQRS的场景**：

**1. 读写操作差异大**
```
场景：电商订单系统
- 读操作：复杂查询（订单详情、统计报表、多维度搜索）
- 写操作：简单CRUD（创建订单、更新状态）

CQRS优势：
- 读模型优化查询（反规范化、冗余字段、索引）
- 写模型保证一致性（规范化、事务）
```

**2. 读多写少**
```
场景：新闻网站
- 读操作：每秒10000次（浏览文章）
- 写操作：每秒10次（发布文章）

CQRS优势：
- 读数据库独立扩展（10个只读副本）
- 写数据库保持简单（1个主库）
- 成本优化
```

**3. 需要不同的数据表示**
```
场景：搜索系统
- 写：PostgreSQL（关系型，事务）
- 读：Elasticsearch（全文搜索）
     Redis（缓存热点数据）

CQRS优势：
- 技术选型灵活
- 各取所长
```

**4. 高性能要求**
```
场景：秒杀系统
- 需要极致的查询性能
- 可以接受最终一致性

CQRS优势：
- 读模型极致优化（缓存、预计算）
- 写模型保证正确性
```

**5. 复杂的业务领域**
```
场景：DDD项目
- 复杂的聚合根和领域逻辑
- 需要事件溯源

CQRS优势：
- 命令端专注业务逻辑
- 查询端专注用户体验
```

---

**不适合使用CQRS的场景**：

**1. 简单CRUD应用**
```
场景：内部管理系统
- 功能简单：增删改查
- 读写逻辑相似
- 没有复杂查询

问题：
- CQRS带来的复杂度 > 收益
- 过度设计
```

**2. 强一致性要求**
```
场景：金融交易系统
- 必须立即读到最新数据
- 不能接受延迟

问题：
- CQRS是最终一致性
- 读写分离导致延迟
- 需要额外的同步机制
```

**3. 小型项目**
```
场景：初创公司MVP
- 团队小，时间紧
- 需求不明确，频繁变更

问题：
- CQRS增加开发成本
- 维护成本高
- 不利于快速迭代
```

**4. 团队能力不足**
```
问题：
- 团队不熟悉CQRS、事件驱动
- 学习曲线陡峭
- 容易出错

后果：
- 开发效率低
- 代码质量差
- 维护困难
```

**5. 读写比例相近**
```
场景：协作编辑系统
- 读写频率相近
- 需要实时同步

问题：
- CQRS的优势不明显
- 最终一致性不适合实时场景
```

---

**选择建议**：

```
✅ 使用CQRS：
- 读写差异大
- 读多写少（10:1以上）
- 需要高性能查询
- 可以接受最终一致性
- 团队有能力

❌ 不使用CQRS：
- 简单CRUD
- 强一致性要求
- 小型项目
- 团队能力不足
- 读写比例相近

默认选择：
- 先用传统架构（分层架构）
- 遇到性能瓶颈时再考虑CQRS
- 逐步迁移，而不是全盘重写
```

---

💡 **提示**：CQRS不是银弹！只在确实需要时使用，优先选择简单的架构。
