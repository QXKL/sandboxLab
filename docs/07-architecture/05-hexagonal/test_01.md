# 六边形架构 - 测试题

## 一、选择题

### 1. 六边形架构的别名不包括？
A. 端口适配器架构  
B. 洋葱架构  
C. 分层架构  
D. 清洁架构

**答案**：C

**解析**：六边形架构又称端口适配器架构、洋葱架构、清洁架构，但不是分层架构。

---

### 2. 六边形架构的核心原则是什么？
A. 单向依赖  
B. 依赖倒置  
C. 双向依赖  
D. 循环依赖

**答案**：B

**解析**：六边形架构的核心是依赖倒置，外部依赖核心，核心不依赖外部。

---

### 3. 端口（Port）是什么？
A. 具体实现  
B. 接口定义  
C. 业务逻辑  
D. 数据库连接

**答案**：B

**解析**：端口是接口定义，定义核心需要什么、提供什么。

---

### 4. 适配器（Adapter）是什么？
A. 接口定义  
B. 业务逻辑  
C. 端口的具体实现  
D. 数据模型

**答案**：C

**解析**：适配器是端口的具体实现，连接核心与外部世界。

---

### 5. 六边形架构中，核心依赖什么？
A. 数据库  
B. 框架  
C. 接口（端口）  
D. UI

**答案**：C

**解析**：核心只依赖接口（端口），不依赖具体的外部技术。

---

## 二、填空题

### 1. 六边形架构又称________架构或________架构。

**答案**：端口适配器（Ports and Adapters）、清洁（Clean）

---

### 2. 六边形架构的核心原则是________，即外部依赖________，核心不依赖外部。

**答案**：依赖倒置、核心

---

### 3. 端口分为________端口（输入）和________端口（输出）。

**答案**：驱动（Driving）、被驱动（Driven）

---

### 4. 适配器分为________适配器（调用核心）和________适配器（被核心调用）。

**答案**：驱动（Driving）、被驱动（Driven）

---

### 5. 六边形架构的核心层包含________和________，不依赖任何外部技术。

**答案**：领域模型、领域服务（或：业务逻辑）

---

## 三、判断题

### 1. 六边形架构中，核心业务逻辑依赖数据库。（ ）

**答案**：✗

**解析**：核心不依赖数据库，而是依赖接口（端口），数据库是适配器。

---

### 2. 六边形架构比分层架构更容易测试。（ ）

**答案**：✓

**解析**：核心不依赖外部技术，可以独立测试，不需要启动数据库或框架。

---

### 3. 六边形架构适合所有类型的应用。（ ）

**答案**：✗

**解析**：六边形架构不适合简单CRUD应用，会增加不必要的复杂度。

---

### 4. 六边形架构可以轻松替换外部技术。（ ）

**答案**：✓

**解析**：只需替换适配器，核心不变，可以轻松替换数据库、消息队列等技术。

---

### 5. 六边形架构中，REST Controller是输出适配器。（ ）

**答案**：✗

**解析**：REST Controller是输入适配器（驱动适配器），调用核心；输出适配器是被核心调用的，如数据库。

---

## 四、简答题

### 1. 说明六边形架构的端口和适配器分别是什么，并举例说明。

**答案**：

**端口（Port）**：

**定义**：核心对外的接口，定义核心需要什么、提供什么。

**分类**：
- **输入端口（驱动端口）**：外部调用核心
- **输出端口（被驱动端口）**：核心调用外部

**特点**：
- 定义在核心层
- 只是接口，没有实现
- 体现依赖倒置

**示例**：
```java
// 输入端口：外部通过这个接口调用核心
package com.example.domain.port.in;

public interface OrderUseCase {
    Order createOrder(CreateOrderCommand command);
    void cancelOrder(Long orderId);
    Order getOrder(Long orderId);
}

// 输出端口：核心通过这个接口调用外部
package com.example.domain.port.out;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
    void delete(Order order);
}

public interface NotificationService {
    void sendOrderConfirmation(Order order);
}

public interface PaymentGateway {
    PaymentResult process(Payment payment);
}
```

---

**适配器（Adapter）**：

**定义**：实现端口的具体技术，连接核心与外部世界。

**分类**：
- **输入适配器（驱动适配器）**：调用核心
- **输出适配器（被驱动适配器）**：被核心调用

**特点**：
- 定义在适配器层
- 实现端口接口
- 包含具体技术细节

**示例**：
```java
// 输入适配器1：REST API
package com.example.adapter.in.rest;

@RestController
@RequestMapping("/orders")
public class OrderRestAdapter {
    
    private final OrderUseCase orderUseCase;  // 依赖输入端口
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = toCommand(request);
        Order order = orderUseCase.createOrder(command);  // 调用核心
        return ResponseEntity.ok(order);
    }
}

// 输入适配器2：CLI
package com.example.adapter.in.cli;

@Component
public class OrderCliAdapter implements CommandLineRunner {
    
    private final OrderUseCase orderUseCase;  // 依赖输入端口
    
    @Override
    public void run(String... args) {
        // 从命令行接收参数，调用核心
        if (args[0].equals("create-order")) {
            orderUseCase.createOrder(command);
        }
    }
}

// 输出适配器1：数据库
package com.example.adapter.out.persistence;

@Repository
public class OrderJpaAdapter implements OrderRepository {  // 实现输出端口
    
    @Autowired
    private JpaOrderRepository jpaRepository;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderMapper.toDomain(saved);
    }
}

// 输出适配器2：邮件通知
package com.example.adapter.out.notification;

@Component
public class EmailNotificationAdapter implements NotificationService {  // 实现输出端口
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendOrderConfirmation(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUserEmail());
        message.setSubject("订单确认");
        mailSender.send(message);
    }
}
```

---

**端口与适配器的关系**：

```
输入端口 ← 输入适配器（REST、CLI、消息队列）
   ↓
核心业务逻辑
   ↓
输出端口 ← 输出适配器（数据库、邮件、第三方API）
```

**核心优势**：
- 核心只依赖端口（接口），不依赖具体实现
- 可以轻松替换适配器（如MySQL → MongoDB）
- 可以添加新适配器（如新增GraphQL适配器）
- 核心易于测试（Mock端口即可）

---

### 2. 比较六边形架构和分层架构的区别。

**答案**：

**分层架构**：

**结构**：
```
┌─────────────────┐
│  Controller     │  表现层
└────────┬────────┘
         ↓ 依赖
┌────────────────┐
│   Service      │  业务层
└────────┬───────┘
         ↓ 依赖
┌────────────────┐
│  Repository    │  数据访问层
└────────┬───────┘
         ↓ 依赖
┌────────────────┐
│   Database     │  数据库
└────────────────┘
```

**依赖方向**：单向向下
- Controller → Service → Repository → Database
- 上层依赖下层

**核心位置**：中间（Service层）

**外部依赖**：
- Service依赖Repository（具体实现）
- 核心依赖外部技术

**示例**：
```java
// Service直接依赖Repository实现
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;  // 依赖具体实现
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
}

// Repository是具体实现
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 具体的JPA实现
}
```

---

**六边形架构**：

**结构**：
```
       ┌─────────────┐
       │  REST API   │  输入适配器
       └──────┬──────┘
              ↓ 依赖
       ┌──────────────┐
       │  输入端口    │  接口
       └──────┬───────┘
              ↓
    ┌─────────────────┐
    │   核心业务逻辑  │  不依赖外部
    └─────────┬───────┘
              ↓
       ┌──────────────┐
       │  输出端口    │  接口
       └──────┬───────┘
              ↑ 实现
       ┌──────────────┐
       │   数据库     │  输出适配器
       └──────────────┘
```

**依赖方向**：依赖倒置
- 外部 → 端口 ← 核心
- 外部依赖核心

**核心位置**：中心

**外部依赖**：
- 核心定义端口（接口）
- 外部实现端口
- 核心不依赖外部

**示例**：
```java
// 核心定义端口（接口）
package com.example.domain.port;

public interface OrderRepository {  // 接口在核心层
    Order save(Order order);
}

// 核心使用端口
package com.example.domain.service;

public class OrderService {
    
    private final OrderRepository orderRepository;  // 依赖接口
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
}

// 外部实现端口
package com.example.adapter.persistence;

public class JpaOrderRepositoryAdapter implements OrderRepository {  // 实现接口
    
    @Autowired
    private JpaOrderRepository jpaRepository;
    
    @Override
    public Order save(Order order) {
        // 具体实现
    }
}
```

---

**对比总结**：

| 特性 | 分层架构 | 六边形架构 |
|-----|---------|-----------|
| **依赖方向** | 单向向下 | 依赖倒置（外部依赖核心） |
| **核心位置** | 中间层（Service） | 中心 |
| **核心依赖** | 依赖外部（Repository实现） | 依赖接口（Port） |
| **接口定义** | 外部定义 | 核心定义 |
| **可测试性** | 中（需要Mock具体类） | 高（只Mock接口） |
| **技术替换** | 困难（核心依赖外部） | 容易（只需替换适配器） |
| **框架依赖** | 核心依赖框架 | 核心不依赖框架 |
| **复杂度** | 低 | 中 |
| **学习曲线** | 简单 | 需要理解依赖倒置 |
| **适用场景** | 大多数应用 | 复杂业务、需要高可测试性 |

---

**选择建议**：

```
分层架构：
- 简单应用
- 快速开发
- 团队熟悉
- 业务逻辑简单

六边形架构：
- 复杂业务
- 需要高可测试性
- 技术不确定
- 长期维护
```

---

### 3. 说明六边形架构如何提高可测试性。

**答案**：

**传统架构的测试问题**：

```java
// Service依赖具体的Repository实现
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;  // JPA实现
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
}

// 测试困难
@Test
public void testCreateOrder() {
    // 问题1：需要启动Spring容器
    // 问题2：需要配置数据库
    // 问题3：需要准备测试数据
    // 问题4：测试慢（依赖数据库）
    
    OrderService service = new OrderService();
    // 无法注入Repository，因为依赖JPA实现
}
```

---

**六边形架构的测试优势**：

**1. 核心完全独立**

```java
// 核心定义接口
package com.example.domain.port;

public interface OrderRepository {
    Order save(Order order);
}

// 核心使用接口
package com.example.domain.service;

public class OrderService {
    
    private final OrderRepository orderRepository;  // 依赖接口
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;  // 构造函数注入
    }
    
    public Order createOrder(List<OrderItem> items) {
        // 纯业务逻辑，不依赖框架
        Order order = new Order();
        order.setItems(items);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(calculateTotal(items));
        
        return orderRepository.save(order);
    }
}
```

**2. 使用Mock测试**

```java
@Test
public void testCreateOrder() {
    // 1. Mock端口
    OrderRepository mockRepo = mock(OrderRepository.class);
    
    // 2. 创建Service（不需要Spring容器）
    OrderService service = new OrderService(mockRepo);
    
    // 3. 准备测试数据
    List<OrderItem> items = Arrays.asList(
        new OrderItem("Product1", 2, new BigDecimal("100"))
    );
    
    // 4. 执行测试
    Order order = service.createOrder(items);
    
    // 5. 验证结果
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(order.getTotalAmount()).isEqualTo(new BigDecimal("200"));
    
    // 6. 验证交互
    verify(mockRepo).save(any(Order.class));
    
    // 优点：
    // - 不需要Spring容器
    // - 不需要数据库
    // - 测试快速（毫秒级）
    // - 专注业务逻辑
}
```

**3. 使用Fake实现测试**

```java
// Fake实现（内存版本）
public class InMemoryOrderRepository implements OrderRepository {
    
    private final Map<Long, Order> orders = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }
    
    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }
}

// 使用Fake测试
@Test
public void testOrderWorkflow() {
    // 1. 使用Fake实现（真实逻辑，内存存储）
    OrderRepository fakeRepo = new InMemoryOrderRepository();
    OrderService service = new OrderService(fakeRepo);
    
    // 2. 测试完整流程
    Order created = service.createOrder(items);
    Order retrieved = service.getOrder(created.getId());
    service.cancelOrder(created.getId());
    
    // 3. 验证结果
    assertThat(retrieved.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    
    // 优点：
    // - 测试真实逻辑
    // - 不需要数据库
    // - 可以测试复杂场景
}
```

**4. 集成测试也简单**

```java
// 只测试适配器层
@SpringBootTest
public class JpaOrderRepositoryTest {
    
    @Autowired
    private OrderRepository orderRepository;  // 真实的JPA实现
    
    @Test
    public void testSave() {
        Order order = new Order();
        Order saved = orderRepository.save(order);
        
        assertThat(saved.getId()).isNotNull();
    }
    
    // 只测试数据库交互，不测试业务逻辑
}
```

---

**测试策略**：

```
单元测试（核心）：
- Mock或Fake端口
- 测试业务逻辑
- 快速、独立

集成测试（适配器）：
- 使用真实数据库
- 测试数据访问
- 相对较慢

端到端测试：
- 启动完整应用
- 测试用户场景
- 最慢、最全面
```

---

**总结**：

六边形架构提高可测试性的关键：
1. **依赖倒置**：核心依赖接口，不依赖具体实现
2. **纯业务逻辑**：核心不依赖框架，可以独立测试
3. **灵活Mock**：可以使用Mock、Fake或真实实现
4. **分层测试**：核心、适配器、端到端分别测试

---

💡 **提示**：六边形架构适合复杂业务系统，简单应用优先选择分层架构！
