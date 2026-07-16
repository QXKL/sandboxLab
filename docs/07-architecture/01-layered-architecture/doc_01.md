# 分层架构

## 一、什么是分层架构？

### 定义

**分层架构（Layered Architecture）**：将系统按职责划分为多个水平层次，每层只与相邻层交互，是最常见的软件架构模式。

**类比**：建筑楼层
```
顶层：用户界面（UI）
中层：业务逻辑（Business Logic）
底层：数据存储（Data Storage）

每层只能访问下层，不能跨层访问
```

### 核心原则

1. **单向依赖**：上层依赖下层，下层不依赖上层
2. **职责分离**：每层有明确的职责
3. **高内聚低耦合**：层内高内聚，层间低耦合
4. **可替换性**：某一层可以独立替换

## 二、经典三层架构

### 架构图

```
┌─────────────────────────┐
│  表现层 Presentation    │  用户界面、API接口
│  (Controller/UI)        │
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  业务层 Business Logic  │  业务规则、流程控制
│  (Service)              │
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  数据访问层 Data Access │  数据库操作、持久化
│  (Repository/DAO)       │
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  数据库 Database        │
└─────────────────────────┘
```

### 各层职责

#### 1. 表现层（Presentation Layer）

**职责**：
- 接收用户请求
- 调用业务层
- 返回响应结果
- 数据格式转换（DTO）

**示例**：
```java
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        // 1. 调用业务层
        User user = userService.getUserById(id);
        
        // 2. 转换为DTO
        UserDTO dto = UserDTO.from(user);
        
        // 3. 返回响应
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
        // 1. DTO转实体
        User user = dto.toEntity();
        
        // 2. 调用业务层
        User saved = userService.createUser(user);
        
        // 3. 返回结果
        return ResponseEntity.ok(UserDTO.from(saved));
    }
}
```

**要点**：
- 不包含业务逻辑
- 只做参数校验、格式转换
- 不直接访问数据库

#### 2. 业务层（Business Logic Layer）

**职责**：
- 实现业务规则
- 流程控制
- 事务管理
- 调用数据访问层

**示例**：
```java
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    public User createUser(User user) {
        // 1. 业务规则：检查用户名是否重复
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUsernameException(user.getUsername());
        }
        
        // 2. 业务规则：设置默认值
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        
        // 3. 保存用户
        User saved = userRepository.save(user);
        
        // 4. 业务流程：发送欢迎邮件
        sendWelcomeEmail(saved);
        
        return saved;
    }
    
    public void deleteUser(Long userId) {
        // 1. 检查用户是否存在
        User user = getUserById(userId);
        
        // 2. 业务规则：有订单的用户不能删除
        if (orderRepository.existsByUserId(userId)) {
            throw new UserHasOrdersException(userId);
        }
        
        // 3. 删除用户
        userRepository.delete(user);
    }
}
```

**要点**：
- 包含所有业务逻辑
- 管理事务边界
- 协调多个Repository

#### 3. 数据访问层（Data Access Layer）

**职责**：
- 封装数据库操作
- 执行CRUD操作
- 不包含业务逻辑

**示例**：
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 简单查询
    Optional<User> findByUsername(String username);
    
    // 存在性检查
    boolean existsByUsername(String username);
    
    // 自定义查询
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    // 批量查询
    List<User> findByStatus(UserStatus status);
}
```

**要点**：
- 只负责数据访问
- 不包含业务逻辑
- 返回实体对象

## 三、MVC架构

### MVC架构图

```
┌──────────┐
│   View   │  视图：展示数据
└──────────┘
     ↑ ↓
┌──────────┐      ┌──────────┐
│Controller│ ───→ │  Model   │  模型：业务数据
└──────────┘      └──────────┘
控制器：处理请求
```

### MVC vs 三层架构

```
MVC架构：
View (视图)
  ↓
Controller (控制器)  → 表现层
  ↓
Service (服务)       → 业务层
  ↓
Repository (仓储)    → 数据访问层
  ↓
Database (数据库)
```

### Spring MVC示例

```java
// Model：数据模型
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String email;
    // getters and setters
}

// View：返回视图名称或JSON
@Controller
public class UserController {
    
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user-detail";  // 返回视图名称
    }
}

// Controller：RESTful API
@RestController
public class UserApiController {
    
    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);  // 返回JSON
    }
}
```

## 四、四层架构（扩展）

### 架构图

```
┌─────────────────────────┐
│  表现层 Presentation    │  UI、API
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  应用层 Application     │  用例、流程编排
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  领域层 Domain          │  核心业务逻辑
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  基础设施层 Infrastructure│ 技术实现、数据访问
└─────────────────────────┘
```

### 各层职责

**应用层（Application Layer）**：
- 用例实现
- 流程编排
- 事务管理
- 不包含业务规则

**领域层（Domain Layer）**：
- 核心业务逻辑
- 领域模型
- 业务规则

**示例**：
```java
// 领域层：领域模型
public class Order {
    private Long id;
    private List<OrderItem> items;
    private OrderStatus status;
    
    // 领域逻辑：计算总价
    public BigDecimal calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // 领域逻辑：取消订单
    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("已发货订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;
    }
}

// 应用层：用例
@Service
public class OrderApplicationService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Transactional
    public void cancelOrder(Long orderId) {
        // 1. 加载领域对象
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // 2. 执行领域逻辑
        order.cancel();
        
        // 3. 保存
        orderRepository.save(order);
        
        // 4. 流程编排：发送通知
        notificationService.sendCancellationNotification(order);
    }
}
```

## 五、分层架构的优缺点

### 优点

**1. 职责清晰**
```
每层有明确的职责，易于理解和维护
```

**2. 易于开发**
```
开发人员可以专注于某一层
前端开发专注表现层
后端开发专注业务层和数据层
```

**3. 易于测试**
```
可以独立测试每一层
Mock下层依赖，单元测试上层逻辑
```

**4. 可替换性**
```
可以替换某一层的实现
例如：MySQL → PostgreSQL
      REST API → GraphQL
```

**5. 可复用性**
```
业务层可以被多个表现层复用
同一个Service可以被Web、Mobile、API调用
```

### 缺点

**1. 性能开销**
```
层与层之间的调用有开销
数据需要在层间传递和转换
```

**2. 可能过度设计**
```
简单的CRUD操作也要经过多层
增加了复杂度
```

**3. 跨层依赖问题**
```
有时需要跨层访问，违反分层原则
例如：性能优化时需要直接访问数据库
```

**4. 紧耦合**
```
虽然层间松耦合，但层内可能紧耦合
业务逻辑分散在多个Service中
```

## 六、分层架构最佳实践

### 实践1：DTO模式

**问题**：实体对象不应该暴露给表现层

**解决方案**：使用DTO（Data Transfer Object）

```java
// 实体类（不暴露）
@Entity
public class User {
    private Long id;
    private String username;
    private String password;  // 敏感信息
    private String email;
    private LocalDateTime createdAt;
    // ...
}

// DTO（暴露给表现层）
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    // 不包含password
    
    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        return user;
    }
}
```

### 实践2：依赖注入

```java
// ❌ 不推荐：直接new
public class UserController {
    private UserService userService = new UserServiceImpl();
}

// ✅ 推荐：依赖注入
@RestController
public class UserController {
    
    @Autowired  // 或构造函数注入
    private UserService userService;
}
```

### 实践3：事务边界在业务层

```java
// ✅ 事务在Service层
@Service
@Transactional
public class OrderService {
    
    public void createOrder(Order order) {
        // 多个数据库操作在一个事务中
        orderRepository.save(order);
        inventoryRepository.decreaseStock(order.getItems());
        // 事务结束
    }
}

// ❌ 不要在Controller层开启事务
@RestController
public class OrderController {
    @Transactional  // 错误
    public void createOrder() { }
}
```

### 实践4：避免跨层调用

```java
// ❌ 错误：Controller直接访问Repository
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;  // 跨层
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }
}

// ✅ 正确：通过Service
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
```

### 实践5：业务逻辑不放在Controller

```java
// ❌ 错误：业务逻辑在Controller
@RestController
public class OrderController {
    
    @PostMapping
    public Order createOrder(@RequestBody OrderDTO dto) {
        // 业务逻辑不应该在这里
        if (dto.getAmount() > 10000) {
            throw new AmountTooLargeException();
        }
        // ...
    }
}

// ✅ 正确：业务逻辑在Service
@Service
public class OrderService {
    
    public Order createOrder(Order order) {
        // 业务逻辑在Service
        if (order.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            throw new AmountTooLargeException();
        }
        return orderRepository.save(order);
    }
}
```

## 七、小结

**核心要点**：

1. **分层架构定义**：
   - 按职责划分为多个水平层次
   - 单向依赖，上层依赖下层

2. **经典三层架构**：
   - 表现层：UI、API
   - 业务层：业务逻辑、事务管理
   - 数据访问层：数据库操作

3. **各层职责**：
   - 表现层：接收请求，调用业务层，返回响应
   - 业务层：业务规则、流程控制
   - 数据访问层：封装数据库操作

4. **优缺点**：
   - 优点：职责清晰、易于开发、易于测试
   - 缺点：性能开销、可能过度设计

5. **最佳实践**：
   - 使用DTO模式
   - 依赖注入
   - 事务边界在业务层
   - 避免跨层调用
   - 业务逻辑不放Controller

**记忆口诀**：
- 分层架构职责清，上层依赖下层明
- 表现业务数据访问，三层结构要记清
- Controller不写业务，Service才是核心层

---

💡 **提示**：分层架构是最常见的架构模式，简单易懂，适合大多数应用。但要注意避免过度设计！
