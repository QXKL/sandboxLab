# 分层架构 - 测试题

## 一、选择题

### 1. 分层架构的核心原则是什么？
A. 双向依赖  
B. 上层依赖下层  
C. 下层依赖上层  
D. 相互独立

**答案**：B

**解析**：分层架构的核心原则是单向依赖，上层依赖下层，下层不依赖上层。

---

### 2. 经典三层架构不包括哪一层？
A. 表现层  
B. 业务层  
C. 数据访问层  
D. 应用层

**答案**：D

**解析**：经典三层架构包括：表现层、业务层、数据访问层。应用层是四层架构的概念。

---

### 3. 在三层架构中，事务边界应该在哪一层？
A. 表现层  
B. 业务层  
C. 数据访问层  
D. 任意层

**答案**：B

**解析**：事务边界应该在业务层（Service），由业务层管理事务的开始、提交、回滚。

---

### 4. 以下哪个不是表现层的职责？
A. 接收用户请求  
B. 调用业务层  
C. 实现业务规则  
D. 返回响应结果

**答案**：C

**解析**：实现业务规则是业务层的职责，表现层只负责接收请求、调用业务层、返回响应。

---

### 5. DTO的主要作用是什么？
A. 提高性能  
B. 隔离实体对象，防止敏感信息泄露  
C. 简化代码  
D. 提高可读性

**答案**：B

**解析**：DTO（Data Transfer Object）用于在层之间传递数据，隔离实体对象，防止敏感信息泄露。

---

### 6. 以下哪种做法违反了分层架构原则？
A. Controller调用Service  
B. Service调用Repository  
C. Controller直接调用Repository  
D. Service调用Service

**答案**：C

**解析**：Controller直接调用Repository是跨层调用，违反了分层架构原则。

---

### 7. MVC中的M指的是？
A. Manager  
B. Model  
C. Module  
D. Method

**答案**：B

**解析**：MVC是Model-View-Controller，M是Model（模型），表示数据和业务逻辑。

---

### 8. 四层架构相比三层架构增加了哪一层？
A. 表现层  
B. 应用层  
C. 数据层  
D. 基础设施层

**答案**：B

**解析**：四层架构在三层基础上增加了应用层，将业务层拆分为应用层和领域层。

---

### 9. 以下哪个不是分层架构的优点？
A. 职责清晰  
B. 易于测试  
C. 高性能  
D. 易于开发

**答案**：C

**解析**：分层架构的缺点之一是性能开销（层间调用、数据转换），不是高性能架构。

---

### 10. Repository层应该返回什么？
A. DTO  
B. JSON  
C. 实体对象  
D. Map

**答案**：C

**解析**：Repository层应该返回实体对象（Entity），DTO是在表现层和业务层之间传递的。

---

## 二、填空题

### 1. 经典三层架构包括：________、________、________。

**答案**：表现层（Presentation Layer）、业务层（Business Logic Layer）、数据访问层（Data Access Layer）

---

### 2. 分层架构的核心原则是________依赖，即上层依赖________，下层不依赖________。

**答案**：单向、下层、上层

---

### 3. MVC架构中，V代表________，C代表________，M代表________。

**答案**：View（视图）、Controller（控制器）、Model（模型）

---

### 4. 在Spring MVC中，________注解用于标记控制器，________注解用于标记业务层，________注解用于标记数据访问层。

**答案**：@Controller（或@RestController）、@Service、@Repository

---

### 5. DTO的全称是________，用于在________之间传递数据。

**答案**：Data Transfer Object、层（或：不同层）

---

## 三、判断题

### 1. 在分层架构中，上层可以依赖下层，下层也可以依赖上层。（ ）

**答案**：✗

**解析**：分层架构是单向依赖，上层依赖下层，下层不能依赖上层。

---

### 2. Controller层应该包含业务逻辑。（ ）

**答案**：✗

**解析**：Controller层不应该包含业务逻辑，业务逻辑应该在Service层。

---

### 3. Service层应该直接返回实体对象给Controller。（ ）

**答案**：✗

**解析**：应该返回DTO，避免暴露实体对象的敏感信息。

---

### 4. 事务应该在Controller层开启。（ ）

**答案**：✗

**解析**：事务应该在Service层开启，由业务层管理事务边界。

---

### 5. 分层架构适合所有类型的应用。（ ）

**答案**：✗

**解析**：分层架构适合大多数应用，但对于简单的CRUD应用可能过度设计，对于复杂的微服务可能不够灵活。

---

## 四、简答题

### 1. 说明三层架构中各层的职责，以及它们之间的调用关系。

**答案**：

**三层架构**：

```
┌─────────────────────────┐
│  表现层 (Controller)    │
└─────────────────────────┘
           ↓ 调用
┌─────────────────────────┐
│  业务层 (Service)       │
└─────────────────────────┘
           ↓ 调用
┌─────────────────────────┐
│  数据访问层 (Repository)│
└─────────────────────────┘
           ↓ 访问
┌─────────────────────────┐
│  数据库 (Database)      │
└─────────────────────────┘
```

---

**各层职责**：

**1. 表现层（Presentation Layer / Controller）**

**职责**：
- 接收用户请求（HTTP请求、API调用）
- 参数校验和格式转换
- 调用业务层
- 返回响应结果（JSON、HTML、XML）
- 异常处理和错误信息返回

**不应该做**：
- 实现业务逻辑
- 直接访问数据库
- 包含复杂的数据处理

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
}
```

---

**2. 业务层（Business Logic Layer / Service）**

**职责**：
- 实现核心业务逻辑和业务规则
- 流程控制和编排
- 事务管理
- 协调多个数据访问对象
- 调用外部服务

**不应该做**：
- 处理HTTP请求细节
- 直接执行SQL
- 包含UI相关逻辑

**示例**：
```java
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    public User createUser(User user) {
        // 1. 业务规则：检查用户名重复
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUsernameException();
        }
        
        // 2. 业务规则：设置默认值
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        
        // 3. 保存数据
        User saved = userRepository.save(user);
        
        // 4. 业务流程：发送欢迎邮件
        emailService.sendWelcomeEmail(saved);
        
        return saved;
    }
}
```

---

**3. 数据访问层（Data Access Layer / Repository）**

**职责**：
- 封装数据库操作（CRUD）
- 执行SQL查询
- 对象关系映射（ORM）
- 数据持久化

**不应该做**：
- 包含业务逻辑
- 事务管理（应该在Service层）
- 数据验证（应该在Service层）

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
}
```

---

**调用关系**：

```
表现层 (Controller)
  ↓ 只能调用业务层
业务层 (Service)
  ↓ 只能调用数据访问层
数据访问层 (Repository)
  ↓ 只能访问数据库
数据库 (Database)
```

**关键规则**：
1. **单向依赖**：只能向下调用，不能向上调用
2. **不跨层调用**：Controller不能直接调用Repository
3. **职责分离**：每层只做自己的事情

---

### 2. 什么是DTO？为什么要使用DTO？请举例说明。

**答案**：

**DTO定义**：

**DTO（Data Transfer Object，数据传输对象）**：用于在不同层之间传递数据的简单对象，通常只包含字段和getter/setter方法，不包含业务逻辑。

---

**为什么要使用DTO**：

**1. 隔离实体对象**

**问题**：直接暴露实体对象，可能泄露敏感信息

```java
// ❌ 错误：直接返回实体
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
        // 问题：User包含password等敏感字段
    }
}

// User实体
@Entity
public class User {
    private Long id;
    private String username;
    private String password;  // 敏感信息！
    private String salt;      // 敏感信息！
    private List<Role> roles;
    // ...
}
```

**解决方案**：使用DTO

```java
// ✅ 正确：返回DTO
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserDTO.from(user);  // 只包含需要的字段
    }
}

// UserDTO
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    // 不包含password、salt等敏感信息
    
    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
```

---

**2. 防止循环引用**

**问题**：实体对象之间的关联可能导致JSON序列化时循环引用

```java
// ❌ 循环引用问题
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    private List<Order> orders;  // User → Order
}

@Entity
public class Order {
    @ManyToOne
    private User user;  // Order → User → Order → ...
}

// 序列化时会无限循环
```

**解决方案**：DTO只包含需要的数据

```java
// ✅ DTO避免循环引用
public class UserDTO {
    private Long id;
    private String username;
    // 不包含orders，或者只包含订单数量
    private int orderCount;
}
```

---

**3. 减少网络传输**

**问题**：实体对象可能包含大量不需要的字段

```java
// ❌ 返回完整实体，数据量大
@Entity
public class Article {
    private Long id;
    private String title;
    private String content;  // 长文本
    private byte[] thumbnail;  // 图片数据
    private List<Comment> comments;  // 评论列表
    // ...
}
```

**解决方案**：DTO只包含需要的字段

```java
// ✅ 列表页只需要部分字段
public class ArticleListDTO {
    private Long id;
    private String title;
    private String summary;  // 摘要，不是完整content
    private int commentCount;  // 评论数量，不是完整列表
}

// ✅ 详情页包含更多字段
public class ArticleDetailDTO {
    private Long id;
    private String title;
    private String content;  // 完整内容
    private List<CommentDTO> comments;  // 评论列表
}
```

---

**4. 接口稳定性**

**问题**：实体变化会影响API接口

```java
// ❌ 直接返回实体
// 实体增加字段，API也会返回新字段，可能破坏客户端
@Entity
public class User {
    private Long id;
    private String username;
    private String newField;  // 新增字段
}
```

**解决方案**：DTO隔离变化

```java
// ✅ DTO保持接口稳定
public class UserDTO {
    private Long id;
    private String username;
    // 不包含newField，接口不变
}
```

---

**5. 验证和转换**

**问题**：不同场景需要不同的验证规则

```java
// ✅ 创建用户DTO
public class CreateUserDTO {
    @NotBlank
    private String username;
    
    @Email
    private String email;
    
    @Size(min = 6)
    private String password;  // 创建时需要密码
    
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(encryptPassword(this.password));
        return user;
    }
}

// ✅ 更新用户DTO
public class UpdateUserDTO {
    @Email
    private String email;
    // 更新时不需要密码
    
    public void updateEntity(User user) {
        if (this.email != null) {
            user.setEmail(this.email);
        }
    }
}
```

---

**总结**：

| 优点 | 说明 |
|-----|------|
| **安全性** | 避免敏感信息泄露 |
| **灵活性** | 不同场景使用不同DTO |
| **稳定性** | 接口与实体解耦 |
| **性能** | 减少数据传输量 |
| **可维护性** | 实体变化不影响接口 |

---

### 3. 比较三层架构和四层架构的区别。

**答案**：

**三层架构**：

```
┌─────────────────────────┐
│  表现层 Presentation    │  Controller、UI
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  业务层 Business Logic  │  Service（业务逻辑+流程）
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  数据访问层 Data Access │  Repository、DAO
└─────────────────────────┘
```

---

**四层架构（DDD风格）**：

```
┌─────────────────────────┐
│  表现层 Presentation    │  Controller、UI
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  应用层 Application     │  用例、流程编排
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  领域层 Domain          │  核心业务逻辑、领域模型
└─────────────────────────┘
           ↓
┌─────────────────────────┐
│  基础设施层 Infrastructure│ Repository、外部服务
└─────────────────────────┘
```

---

**主要区别**：

**1. 业务层的拆分**

**三层架构**：
- 业务层包含所有业务逻辑和流程控制
- Service既有业务规则，又有流程编排

```java
// 三层架构的Service
@Service
public class OrderService {
    
    public void createOrder(Order order) {
        // 业务规则
        if (order.getAmount() > 10000) {
            throw new IllegalArgumentException("金额过大");
        }
        
        // 流程编排
        orderRepository.save(order);
        inventoryService.decreaseStock(order);
        notificationService.notify(order);
    }
}
```

**四层架构**：
- 应用层：用例、流程编排、事务管理
- 领域层：核心业务逻辑、领域模型

```java
// 四层架构的应用层
@Service
public class OrderApplicationService {
    
    @Transactional
    public void createOrder(CreateOrderCommand command) {
        // 流程编排
        Order order = Order.create(command);  // 调用领域层
        orderRepository.save(order);
        inventoryService.decreaseStock(order);
        notificationService.notify(order);
    }
}

// 四层架构的领域层
public class Order {
    private Long id;
    private BigDecimal amount;
    private OrderStatus status;
    
    // 业务规则在领域模型中
    public static Order create(CreateOrderCommand command) {
        if (command.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            throw new IllegalArgumentException("金额过大");
        }
        Order order = new Order();
        order.setAmount(command.getAmount());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }
    
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("已发货订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
```

---

**2. 职责划分**

| 层 | 三层架构 | 四层架构 |
|---|---------|---------|
| **表现层** | UI、API | UI、API |
| **业务/应用层** | 业务逻辑+流程 | 用例、流程编排 |
| **领域层** | - | 核心业务逻辑 |
| **数据/基础设施层** | 数据访问 | 数据访问+外部服务 |

---

**3. 依赖方向**

**三层架构**：
```
表现层 → 业务层 → 数据访问层
```

**四层架构**：
```
表现层 → 应用层 → 领域层 ← 基础设施层
                    ↑
              (依赖倒置)
```

四层架构中，基础设施层依赖领域层（依赖倒置原则），领域层不依赖基础设施层。

---

**4. 适用场景**

**三层架构适用**：
- 简单的CRUD应用
- 业务逻辑不复杂
- 快速开发

**四层架构适用**：
- 复杂的业务领域
- 需要DDD（领域驱动设计）
- 业务规则复杂

---

**对比总结**：

| 特性 | 三层架构 | 四层架构 |
|-----|---------|---------|
| **复杂度** | 简单 | 复杂 |
| **学习成本** | 低 | 高 |
| **业务逻辑位置** | Service层 | 领域层 |
| **流程编排位置** | Service层 | 应用层 |
| **适用场景** | CRUD应用 | 复杂业务 |
| **可测试性** | 中 | 高 |
| **维护性** | 中 | 高（业务逻辑集中） |

---

**选择建议**：
- 简单应用 → 三层架构
- 复杂业务 → 四层架构（DDD）
- 大多数应用 → 三层架构足够

---

💡 **提示**：分层架构是最基础的架构模式，理解好三层架构是学习其他架构的基础！
