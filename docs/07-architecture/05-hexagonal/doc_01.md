# 六边形架构

## 一、什么是六边形架构？

### 定义

**六边形架构（Hexagonal Architecture）**：又称端口适配器模式（Ports and Adapters），将应用的核心业务逻辑与外部依赖隔离，通过端口和适配器进行交互。

**别名**：
- 端口适配器架构（Ports and Adapters）
- 洋葱架构（Onion Architecture）
- 清洁架构（Clean Architecture）

**类比**：笔记本电脑的接口
```
笔记本核心：CPU、内存（业务逻辑）
接口（端口）：USB、HDMI、网线接口
外设（适配器）：鼠标、显示器、网线

核心不关心外设类型：
- USB接口可以插鼠标、键盘、U盘
- HDMI可以连显示器、投影仪
- 外设可以随意替换，核心不变
```

### 架构图

```
          ┌────────────────┐
          │   REST API     │
          │   (适配器)     │
          └────────┬───────┘
                   │
         ┌─────────▼─────────┐
         │     端口          │
         │  (接口定义)       │
         └─────────┬─────────┘
                   │
    ┌──────────────▼──────────────┐
    │                              │
    │      核心业务逻辑            │
    │    (领域模型、服务)          │
    │                              │
    └──────────────┬──────────────┘
                   │
         ┌─────────▼─────────┐
         │     端口          │
         │  (接口定义)       │
         └─────────┬─────────┘
                   │
          ┌────────▼───────┐
          │   数据库       │
          │   (适配器)     │
          └────────────────┘
```

## 二、核心概念

### 1. 核心（Core/Domain）

**定义**：应用的业务逻辑，不依赖任何外部技术。

**包含**：
- 领域模型（Domain Model）
- 领域服务（Domain Service）
- 业务规则

**示例**：
```java
// 领域模型
public class Order {
    private Long id;
    private List<OrderItem> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    
    // 业务规则在领域模型中
    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("已发货订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;
    }
    
    public BigDecimal calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// 领域服务
public class OrderService {
    
    public Order createOrder(List<OrderItem> items) {
        Order order = new Order();
        order.setItems(items);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(order.calculateTotal());
        return order;
    }
}
```

### 2. 端口（Port）

**定义**：核心对外的接口，定义核心需要什么、提供什么。

**分类**：
- **驱动端口（Driving Port）/主端口**：外部调用核心（输入）
- **被驱动端口（Driven Port）/次端口**：核心调用外部（输出）

**示例**：
```java
// 驱动端口（输入端口）：外部调用核心
public interface OrderUseCase {
    Order createOrder(CreateOrderRequest request);
    void cancelOrder(Long orderId);
    Order getOrder(Long orderId);
}

// 被驱动端口（输出端口）：核心调用外部
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
    void delete(Order order);
}

public interface NotificationService {
    void sendOrderConfirmation(Order order);
}
```

### 3. 适配器（Adapter）

**定义**：实现端口的具体技术，连接核心与外部世界。

**分类**：
- **驱动适配器（Driving Adapter）/主适配器**：调用核心（REST、Web、CLI）
- **被驱动适配器（Driven Adapter）/次适配器**：被核心调用（数据库、邮件、消息队列）

**示例**：
```java
// 驱动适配器：REST API
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderUseCase orderUseCase;
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderUseCase.createOrder(request);
        return ResponseEntity.ok(order);
    }
}

// 被驱动适配器：数据库
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    @Autowired
    private JpaOrderRepository jpaRepository;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderMapper.toDomain(saved);
    }
}

// 被驱动适配器：邮件
@Component
public class EmailNotificationAdapter implements NotificationService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendOrderConfirmation(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUserEmail());
        message.setSubject("订单确认");
        message.setText("您的订单已创建：" + order.getId());
        mailSender.send(message);
    }
}
```

## 三、依赖方向

### 依赖倒置原则

```
传统架构：
Controller → Service → Repository → Database
核心依赖外部技术（数据库）

六边形架构：
Controller → OrderUseCase ← OrderService
                ↑
         OrderRepository
                ↑
          RepositoryImpl

核心定义接口，外部实现接口
核心不依赖外部，外部依赖核心
```

### 示例

```java
// 核心定义端口（接口）
package com.example.domain.port;

public interface OrderRepository {
    Order save(Order order);
}

// 核心使用端口
package com.example.domain.service;

public class OrderService {
    
    private final OrderRepository orderRepository;  // 依赖接口
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    public Order createOrder(List<OrderItem> items) {
        Order order = new Order(items);
        return orderRepository.save(order);  // 调用接口
    }
}

// 外部实现端口
package com.example.adapter.persistence;

public class JpaOrderRepository implements OrderRepository {  // 实现接口
    
    @Override
    public Order save(Order order) {
        // 具体实现
    }
}
```

## 四、六边形架构的优点

### 1. 核心业务逻辑独立

```
核心不依赖：
- 框架（Spring、Hibernate）
- 数据库（MySQL、MongoDB）
- UI（Web、Mobile）
- 外部服务

优点：
- 易于理解和维护
- 易于测试（不需要启动框架）
- 业务逻辑不受技术变化影响
```

### 2. 可测试性强

```java
// 核心可以独立测试，不需要数据库
@Test
public void testCreateOrder() {
    // 使用Mock实现端口
    OrderRepository mockRepo = mock(OrderRepository.class);
    OrderService service = new OrderService(mockRepo);
    
    // 测试业务逻辑
    Order order = service.createOrder(items);
    
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    verify(mockRepo).save(any(Order.class));
}
```

### 3. 技术灵活

```
可以轻松替换技术：
- 数据库：MySQL → PostgreSQL → MongoDB
- 消息队列：RabbitMQ → Kafka
- 缓存：Redis → Memcached

只需替换适配器，核心不变
```

### 4. 延迟技术决策

```
开发初期：
- 先实现核心业务逻辑
- 使用内存实现适配器

后期：
- 根据需求选择合适的技术
- 实现真正的适配器
```

## 五、六边形架构实现

### 项目结构

```
src/main/java/com/example/
├── domain/                    # 核心（不依赖任何框架）
│   ├── model/                # 领域模型
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── service/              # 领域服务
│   │   └── OrderService.java
│   └── port/                 # 端口（接口定义）
│       ├── in/              # 输入端口
│       │   └── OrderUseCase.java
│       └── out/             # 输出端口
│           ├── OrderRepository.java
│           └── NotificationService.java
│
├── adapter/                   # 适配器（实现端口）
│   ├── in/                   # 输入适配器
│   │   ├── rest/
│   │   │   └── OrderController.java
│   │   └── cli/
│   │       └── OrderCommandLine.java
│   └── out/                  # 输出适配器
│       ├── persistence/
│       │   └── JpaOrderRepository.java
│       └── notification/
│           └── EmailNotificationAdapter.java
│
└── config/                    # 配置（组装）
    └── OrderConfiguration.java
```

### 完整示例

```java
// ========== 核心层 ==========

// 领域模型
package com.example.domain.model;

public class Order {
    private Long id;
    private List<OrderItem> items;
    private OrderStatus status;
    
    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("已发货订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;
    }
}

// 输入端口
package com.example.domain.port.in;

public interface OrderUseCase {
    Order createOrder(CreateOrderCommand command);
    void cancelOrder(Long orderId);
}

// 输出端口
package com.example.domain.port.out;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
}

// 领域服务（实现输入端口，使用输出端口）
package com.example.domain.service;

public class OrderService implements OrderUseCase {
    
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Order createOrder(CreateOrderCommand command) {
        Order order = new Order(command.getItems());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }
    
    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.cancel();
        orderRepository.save(order);
    }
}

// ========== 适配器层 ==========

// 输入适配器：REST
package com.example.adapter.in.rest;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderUseCase orderUseCase;
    
    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = new CreateOrderCommand(request.getItems());
        Order order = orderUseCase.createOrder(command);
        return ResponseEntity.ok(order);
    }
}

// 输出适配器：数据库
package com.example.adapter.out.persistence;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepository {
    
    @Autowired
    private JpaOrderRepository jpaRepository;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderMapper.toDomain(saved);
    }
    
    @Override
    public Optional<Order> findById(Long orderId) {
        return jpaRepository.findById(orderId)
            .map(OrderMapper::toDomain);
    }
}

// ========== 配置层 ==========

// 组装依赖
package com.example.config;

@Configuration
public class OrderConfiguration {
    
    @Bean
    public OrderUseCase orderUseCase(OrderRepository orderRepository) {
        return new OrderService(orderRepository);
    }
}
```

## 六、六边形架构 vs 分层架构

| 特性 | 分层架构 | 六边形架构 |
|-----|---------|-----------|
| **依赖方向** | 单向向下 | 依赖倒置 |
| **核心位置** | 中间层 | 中心 |
| **外部依赖** | 核心依赖外部 | 外部依赖核心 |
| **可测试性** | 中 | 高 |
| **技术替换** | 困难 | 容易 |
| **复杂度** | 低 | 中 |

## 七、何时使用六边形架构

### 适合使用

✅ **核心业务逻辑复杂**
```
复杂的业务规则
需要频繁变更
需要长期维护
```

✅ **需要高可测试性**
```
核心业务需要充分测试
不希望测试依赖数据库
```

✅ **技术不确定**
```
初期不确定使用什么数据库
可能需要切换技术栈
```

✅ **多端适配**
```
同一核心支持多种接口：
- Web API
- Mobile API
- CLI
- 消息队列
```

### 不适合使用

❌ **简单CRUD应用**
```
业务逻辑简单
六边形架构过度设计
```

❌ **快速原型**
```
需要快速验证想法
不需要长期维护
```

❌ **团队不熟悉**
```
团队不理解依赖倒置
学习成本高
```

## 八、小结

**核心要点**：

1. **六边形架构定义**：
   - 又称端口适配器架构
   - 核心业务逻辑与外部隔离
   - 通过端口和适配器交互

2. **核心概念**：
   - 核心：业务逻辑
   - 端口：接口定义
   - 适配器：具体实现

3. **依赖方向**：
   - 外部依赖核心
   - 核心不依赖外部
   - 依赖倒置原则

4. **优点**：
   - 核心业务独立
   - 可测试性强
   - 技术灵活
   - 延迟技术决策

5. **适用场景**：
   - 核心业务复杂
   - 需要高可测试性
   - 技术不确定
   - 多端适配

**记忆口诀**：
- 六边形核心居中，端口适配隔离外部
- 依赖倒置很关键，核心定义外部实现
- 测试容易技术灵活，复杂业务才使用

---

💡 **提示**：六边形架构适合复杂业务系统，简单应用用分层架构即可！
