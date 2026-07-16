# ORM 设计与选型

## 一、什么是ORM？

### 定义

**ORM（Object-Relational Mapping，对象关系映射）**：将面向对象的类与数据库表建立映射关系，让开发者可以用面向对象的方式操作数据库。

```java
// 不使用ORM（JDBC）
String sql = "SELECT * FROM users WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setLong(1, id);
ResultSet rs = stmt.executeQuery();
if (rs.next()) {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setName(rs.getString("name"));
    user.setEmail(rs.getString("email"));
    return user;
}

// 使用ORM（JPA）
User user = userRepository.findById(id);
```

### 核心概念

**对象 ↔ 表**
```java
// Java类
public class User {
    private Long id;
    private String name;
    private String email;
}

// 数据库表
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);
```

**ORM框架自动处理**：
- SQL生成
- 结果集映射
- 关系映射（一对多、多对多）

## 二、主流ORM框架对比

### 框架1：JPA/Hibernate

**特点**：
- Java标准规范（JPA）
- Hibernate是实现
- 功能全面
- 学习曲线陡

**示例**：
```java
// 实体类
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    private String email;
    
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}

// Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);
}

// 使用
User user = userRepository.findById(1L);
user.setName("张三");
userRepository.save(user);  // 自动UPDATE
```

**优点**：
- ✅ 功能强大（缓存、延迟加载、级联操作）
- ✅ 符合JPA标准（可切换实现）
- ✅ 自动生成SQL

**缺点**：
- ❌ 学习曲线陡
- ❌ 性能可能不如原生SQL
- ❌ 复杂查询不方便

**适用场景**：
- 企业级应用
- CRUD操作为主
- 需要标准化

### 框架2：MyBatis（推荐）

**特点**：
- 半自动ORM
- SQL与代码分离
- 灵活
- 国内流行

**示例**：
```java
// Mapper接口
@Mapper
public interface UserMapper {
    User findById(Long id);
    List<User> findByName(String name);
    void insert(User user);
    void update(User user);
}

// XML映射
<mapper namespace="com.example.UserMapper">
    <select id="findById" resultType="User">
        SELECT * FROM users WHERE id = #{id}
    </select>
    
    <select id="findByName" resultType="User">
        SELECT * FROM users WHERE name = #{name}
    </select>
    
    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO users (name, email) VALUES (#{name}, #{email})
    </insert>
    
    <update id="update">
        UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}
    </update>
</mapper>

// 使用
User user = userMapper.findById(1L);
user.setName("张三");
userMapper.update(user);
```

**优点**：
- ✅ 灵活，完全控制SQL
- ✅ 学习曲线平缓
- ✅ 复杂查询方便
- ✅ 性能好

**缺点**：
- ❌ 需要手写SQL
- ❌ 功能不如Hibernate丰富

**适用场景**：
- 互联网应用
- 复杂查询多
- 对性能要求高

### 框架3：JOOQ

**特点**：
- 类型安全的SQL构建器
- 代码生成
- 适合复杂SQL

**示例**：
```java
// 代码生成的类
DSLContext dsl = DSL.using(connection, SQLDialect.MYSQL);

// 类型安全的查询
Result<Record> result = dsl
    .select()
    .from(USERS)
    .where(USERS.NAME.eq("zhangsan"))
    .fetch();
```

**优点**：
- ✅ 类型安全
- ✅ 适合复杂SQL
- ✅ IDE支持好

**缺点**：
- ❌ 需要代码生成
- ❌ 学习成本高

### 框架对比

| 框架 | 学习成本 | 灵活性 | 性能 | 适用场景 |
|-----|---------|-------|------|---------|
| **JPA/Hibernate** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | 企业级应用 |
| **MyBatis** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 互联网应用 |
| **JOOQ** | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | 复杂SQL |

**推荐**：
- 国内项目：MyBatis
- 国际项目：JPA/Hibernate

## 三、ORM的常见问题

### 问题1：N+1查询（已在专门章节讲解）

```java
// ❌ 触发N+1
List<User> users = userRepository.findAll();  // 1次查询
for (User user : users) {
    List<Order> orders = user.getOrders();  // N次查询
}

// ✅ 使用JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
List<User> findAllWithOrders();
```

### 问题2：懒加载陷阱

```java
// Hibernate懒加载
@OneToMany(fetch = FetchType.LAZY)
private List<Order> orders;

// 问题：Session关闭后访问
User user = userRepository.findById(1L);
// Session关闭
List<Order> orders = user.getOrders();  // LazyInitializationException!

// 解决：使用JOIN FETCH或DTO
```

### 问题3：缓存问题

```java
// 一级缓存（Session级别）
User user1 = userRepository.findById(1L);
User user2 = userRepository.findById(1L);  // 不查数据库，走缓存
// user1 == user2（同一个对象）

// 二级缓存（SessionFactory级别）
需要配置，跨Session共享
```

### 问题4：SQL性能

```java
// Hibernate生成的SQL可能不优
@Query("SELECT u FROM User u WHERE u.name = ?1")
// 生成：SELECT * FROM users WHERE name = ?
// 问题：查询了所有字段，可能不需要

// 优化：使用DTO投影
@Query("SELECT new UserDTO(u.id, u.name) FROM User u WHERE u.name = ?1")
```

## 四、ORM最佳实践

### 实践1：选择合适的框架

```
简单CRUD → Spring Data JPA
复杂查询 → MyBatis
类型安全 → JOOQ
```

### 实践2：避免N+1问题

```java
// 使用JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
User findByIdWithOrders(@Param("id") Long id);
```

### 实践3：使用DTO

```java
// 不要直接返回Entity
@RestController
public class UserController {
    
    // ❌ 返回Entity
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id);
    }
    
    // ✅ 返回DTO
    @GetMapping("/users/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        User user = userRepository.findById(id);
        return UserDTO.from(user);
    }
}
```

### 实践4：复杂查询用原生SQL

```java
// MyBatis
<select id="complexQuery" resultType="OrderSummary">
    SELECT 
        u.name,
        COUNT(o.id) as order_count,
        SUM(o.amount) as total_amount
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    WHERE o.created_at > #{startDate}
    GROUP BY u.id
    HAVING total_amount > 1000
</select>
```

### 实践5：监控SQL

```yaml
# Spring Boot配置
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

## 五、小结

**核心要点**：

1. **ORM定义**：对象关系映射，用面向对象方式操作数据库

2. **主流框架**：
   - JPA/Hibernate：功能全面，学习曲线陡
   - MyBatis：灵活，国内流行
   - JOOQ：类型安全，适合复杂SQL

3. **常见问题**：
   - N+1查询：使用JOIN FETCH
   - 懒加载：注意Session生命周期
   - 性能：复杂查询用原生SQL

4. **最佳实践**：
   - 选择合适框架
   - 避免N+1
   - 使用DTO
   - 监控SQL

**选择建议**：
- 国内互联网项目：MyBatis
- 企业级应用：JPA/Hibernate
- 复杂SQL场景：JOOQ或MyBatis

**记忆口诀**：
- ORM映射对象表
- MyBatis灵活强
- Hibernate功能全
- N+1要避免

---

💡 **提示**：ORM提高开发效率，但要注意性能陷阱。复杂查询时，原生SQL可能更好。
