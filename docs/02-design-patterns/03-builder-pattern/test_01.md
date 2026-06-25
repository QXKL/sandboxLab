# 建造者模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 12 分，共 36 分）

### 1. 以下关于建造者模式的描述，哪个是正确的？

A. 建造者模式主要用于创建不同类型的对象  
B. 建造者模式适合对象只有1-2个参数的场景  
C. 建造者模式将对象的构建过程与表示分离  
D. 建造者模式不支持链式调用

<details>
<summary>查看答案</summary>

**答案**: C

**解析**:
- A错误：工厂模式用于创建不同类型的对象，建造者模式用于逐步构建复杂对象
- B错误：建造者模式适合参数≥4个的场景
- **C正确**：建造者模式的核心是"构建与表示分离"
- D错误：建造者模式通过返回this支持链式调用

**记忆要点**：
- 工厂模式：创建**哪个**对象（类型）
- 建造者模式：**如何**构建对象（配置）
</details>

---

### 2. Telescoping Constructor（重叠构造器）的问题是什么？

A. 性能问题  
B. 参数顺序混乱、可读性差、难以维护  
C. 不支持继承  
D. 不支持多态

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**Telescoping Constructor示例**：
```java
public User(String name, String password) { }
public User(String name, String password, String email) { }
public User(String name, String password, String email, String phone) { }
public User(String name, String password, String email, String phone, int age) { }
// ... 随着参数增加，构造函数呈指数增长
```

**问题**：
1. **参数顺序混乱**：调用时不知道哪个参数是什么
2. **可读性差**：`new User("alice", "pass", null, null, 0)`
3. **难以维护**：新增参数需要修改所有构造函数
4. **可选参数处理困难**：需要传null或默认值

**建造者模式解决**：
```java
User user = new User.Builder("alice", "pass")
    .email("alice@example.com")
    .phone("13800138000")
    .age(25)
    .build();
```
</details>

---

### 3. 以下哪个场景最适合使用建造者模式？

A. 创建简单的Point对象（x, y两个参数）  
B. 创建HTTP请求对象（URL、method、headers、body、timeout等10+参数）  
C. 创建不同类型的支付方式（支付宝、微信、银行卡）  
D. 创建单例对象

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**B正确** - HTTP请求对象：
- 参数多（10+个）
- 多数参数可选
- 需要逐步配置
- 典型的建造者模式应用

```java
Request request = new Request.Builder("https://api.example.com")
    .method("POST")
    .header("Authorization", "Bearer token")
    .header("Content-Type", "application/json")
    .body("{\"name\": \"Alice\"}")
    .timeout(5000)
    .build();
```

**其他选项分析**：
- A：参数太少（2个），直接构造函数即可
- C：创建不同类型对象，应该用工厂模式
- D：单例模式，不是建造者模式

**判断标准**：
- 参数 ≥ 4个 → 考虑建造者
- 多个可选参数 → 考虑建造者
- 需要不可变对象 → 考虑建造者
- 创建不同类型 → 用工厂模式
</details>

---

## 二、代码分析（每题 16 分，共 32 分）

### 4. 指出以下代码的问题，并用建造者模式重构

```java
public class Config {
    private String host;
    private int port;
    private String username;
    private String password;
    private int timeout;
    private boolean ssl;
    
    public Config(String host, int port, String username, 
                  String password, int timeout, boolean ssl) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.ssl = ssl;
    }
}

// 使用
Config config = new Config("localhost", 3306, "root", "pass123", 5000, true);
```

<details>
<summary>参考答案</summary>

**问题分析**：

1. **参数过多**：6个参数，容易混淆
2. **可读性差**：不知道每个参数的含义
3. **可选参数处理困难**：timeout和ssl可能是可选的
4. **无法创建不可变对象**：如果需要final字段，无法实现

**建造者模式重构**：

```java
public class Config {
    // 所有字段final（不可变）
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final int timeout;
    private final boolean ssl;
    
    // 私有构造函数
    private Config(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.username = builder.username;
        this.password = builder.password;
        this.timeout = builder.timeout;
        this.ssl = builder.ssl;
    }
    
    // 静态内部类Builder
    public static class Builder {
        // 必填参数
        private final String host;
        private final int port;
        
        // 可选参数（默认值）
        private String username = "";
        private String password = "";
        private int timeout = 3000;
        private boolean ssl = false;
        
        public Builder(String host, int port) {
            this.host = host;
            this.port = port;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder ssl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }
        
        public Config build() {
            // 参数验证
            if (host == null || host.isEmpty()) {
                throw new IllegalArgumentException("host不能为空");
            }
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("端口范围错误");
            }
            if (timeout < 0) {
                throw new IllegalArgumentException("timeout不能为负数");
            }
            return new Config(this);
        }
    }
}

// 使用（可读性强）
Config config = new Config.Builder("localhost", 3306)
    .username("root")
    .password("pass123")
    .timeout(5000)
    .ssl(true)
    .build();
```

**改进要点**：
1. ✅ 参数名称清晰
2. ✅ 支持可选参数
3. ✅ 链式调用
4. ✅ 创建不可变对象
5. ✅ 集中参数验证
</details>

---

### 5. 以下代码实现了建造者模式吗？如果有问题，请改正

```java
public class User {
    private String username;
    private String email;
    
    public static class Builder {
        private String username;
        private String email;
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public User build() {
            User user = new User();
            user.username = this.username;
            user.email = this.email;
            return user;
        }
    }
}

// 使用
User.Builder builder = new User.Builder();
builder.setUsername("alice");
builder.setEmail("alice@example.com");
User user = builder.build();
```

<details>
<summary>参考答案</summary>

**问题分析**：

❌ **问题1：不支持链式调用**
```java
public void setUsername(String username) {  // 返回void
    this.username = username;
}
```

❌ **问题2：方法名不符合建造者风格**
- 应该用`username()`而不是`setUsername()`

❌ **问题3：无法创建不可变对象**
- User的字段不是final
- User没有私有构造函数，外部可以直接new

❌ **问题4：使用方式不流畅**
```java
builder.setUsername("alice");  // 不是链式
builder.setEmail("alice@example.com");
```

**正确实现**：

```java
public class User {
    // final字段（不可变）
    private final String username;
    private final String email;
    
    // 私有构造函数
    private User(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
    }
    
    public static class Builder {
        private String username;
        private String email;
        
        // 返回Builder支持链式调用
        public Builder username(String username) {
            this.username = username;
            return this;  // 关键：返回this
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
}

// 使用（链式调用）
User user = new User.Builder()
    .username("alice")
    .email("alice@example.com")
    .build();
```

**核心改进**：
1. ✅ 方法返回Builder（支持链式）
2. ✅ 方法名简洁（username而不是setUsername）
3. ✅ User字段final（不可变）
4. ✅ User私有构造函数（强制使用Builder）
</details>

---

## 三、实践应用（每题 16 分，共 32 分）

### 6. 手写链式建造者：邮件对象

设计一个Email类，要求：
- 必填参数：收件人（to）、主题（subject）
- 可选参数：正文（body）、抄送（cc）、密送（bcc）
- 使用建造者模式实现
- 支持链式调用
- 创建不可变对象

<details>
<summary>参考答案</summary>

```java
public class Email {
    // 所有字段final（不可变）
    private final String to;
    private final String subject;
    private final String body;
    private final String cc;
    private final String bcc;
    
    // 私有构造函数
    private Email(Builder builder) {
        this.to = builder.to;
        this.subject = builder.subject;
        this.body = builder.body;
        this.cc = builder.cc;
        this.bcc = builder.bcc;
    }
    
    // Getters
    public String getTo() { return to; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getCc() { return cc; }
    public String getBcc() { return bcc; }
    
    @Override
    public String toString() {
        return "Email{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", cc='" + cc + '\'' +
                ", bcc='" + bcc + '\'' +
                '}';
    }
    
    // 静态内部类Builder
    public static class Builder {
        // 必填参数
        private final String to;
        private final String subject;
        
        // 可选参数（默认值）
        private String body = "";
        private String cc = "";
        private String bcc = "";
        
        // 构造函数只包含必填参数
        public Builder(String to, String subject) {
            this.to = to;
            this.subject = subject;
        }
        
        // 链式调用
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        
        public Builder cc(String cc) {
            this.cc = cc;
            return this;
        }
        
        public Builder bcc(String bcc) {
            this.bcc = bcc;
            return this;
        }
        
        // 构建最终对象
        public Email build() {
            // 参数验证
            if (to == null || to.isEmpty()) {
                throw new IllegalArgumentException("收件人不能为空");
            }
            if (!to.contains("@")) {
                throw new IllegalArgumentException("收件人邮箱格式错误");
            }
            if (subject == null || subject.isEmpty()) {
                throw new IllegalArgumentException("主题不能为空");
            }
            return new Email(this);
        }
    }
}

// 使用示例
public class EmailTest {
    public static void main(String[] args) {
        // 只有必填参数
        Email email1 = new Email.Builder("user@example.com", "测试邮件")
            .build();
        
        // 包含可选参数
        Email email2 = new Email.Builder("user@example.com", "会议通知")
            .body("明天下午3点开会")
            .cc("manager@example.com")
            .build();
        
        // 所有参数
        Email email3 = new Email.Builder("user@example.com", "项目报告")
            .body("本周项目进展...")
            .cc("team@example.com")
            .bcc("boss@example.com")
            .build();
        
        System.out.println(email3);
    }
}
```

**要点**：
1. ✅ 必填参数在构造函数中
2. ✅ 可选参数有默认值
3. ✅ 链式调用（返回this）
4. ✅ 不可变对象（final字段）
5. ✅ 参数验证在build()中
</details>

---

### 7. 场景判断：何时使用建造者模式？

判断以下场景是否适合使用建造者模式，并说明理由。

**场景A**: Point类（x, y两个坐标）  
**场景B**: HTTP请求（URL、method、headers、body、timeout等）  
**场景C**: 数据库连接配置（host、port、username、password、poolSize、timeout等）  
**场景D**: 支付方式创建（支付宝、微信、银行卡）  
**场景E**: 复杂报表生成（标题、副标题、数据源、图表类型、样式等）

<details>
<summary>参考答案</summary>

### 场景A: Point类
**推荐**: ❌ 不适合建造者模式

**理由**：
- 参数太少（只有2个）
- 没有可选参数
- 直接构造函数更简单

```java
// 推荐方式
Point point = new Point(10, 20);

// 过度设计
Point point = new Point.Builder(10, 20).build();
```

---

### 场景B: HTTP请求
**推荐**: ✅ 非常适合建造者模式

**理由**：
- 参数多（10+个）
- 大部分参数可选
- 需要逐步配置
- 典型应用（OkHttp的Request.Builder）

```java
Request request = new Request.Builder("https://api.example.com")
    .method("POST")
    .header("Authorization", "Bearer token")
    .body(requestBody)
    .timeout(5000)
    .build();
```

---

### 场景C: 数据库连接配置
**推荐**: ✅ 适合建造者模式

**理由**：
- 参数较多（6+个）
- 多个可选参数（poolSize、timeout等）
- 需要参数验证
- 创建不可变配置对象

```java
DataSourceConfig config = new DataSourceConfig.Builder("localhost", 3306)
    .username("root")
    .password("pass123")
    .poolSize(10)
    .timeout(5000)
    .build();
```

---

### 场景D: 支付方式创建
**推荐**: ❌ 不适合建造者，应该用工厂模式

**理由**：
- 关注点是**创建不同类型的对象**
- 不是逐步配置一个对象
- 工厂模式更合适

```java
// 应该用工厂模式
PaymentFactory factory = new AlipayFactory();
Payment payment = factory.createPayment();
```

---

### 场景E: 复杂报表生成
**推荐**: ✅ 适合建造者模式

**理由**：
- 参数多且复杂
- 多个可选配置
- 构建过程复杂
- 可能需要Director封装预定义模板

```java
Report report = new Report.Builder("月度销售报告")
    .subtitle("2024年3月")
    .dataSource(salesData)
    .chartType(ChartType.BAR)
    .style(ReportStyle.PROFESSIONAL)
    .pageSize(PageSize.A4)
    .build();

// 或使用Director提供模板
Report report = reportDirector.createMonthlyReport(data);
```

---

### 总结

**适合建造者模式**：
- ✅ 参数 ≥ 4个
- ✅ 多个可选参数
- ✅ 需要不可变对象
- ✅ 构建过程复杂

**不适合建造者模式**：
- ❌ 参数 ≤ 3个
- ❌ 所有参数必填
- ❌ 对象很简单
- ❌ 需要创建不同类型对象（用工厂）

**判断口诀**：
> 参数多且可选多，建造者来帮助我。  
> 参数少或类型选，工厂模式更合适。
</details>

---

## 四、评分标准

### 满分答案特征
- ✅ 理解建造者模式的本质（构建与表示分离）
- ✅ 掌握链式建造者的实现（静态内部类、返回this）
- ✅ 理解不可变对象的概念
- ✅ 能区分建造者和工厂的使用场景
- ✅ 能手写符合规范的建造者代码

### 常见扣分点
- ❌ 混淆建造者模式和工厂模式
- ❌ Builder方法不返回this（不支持链式）
- ❌ 未创建不可变对象（字段不是final）
- ❌ 参数验证位置错误（应在build()中）
- ❌ 场景判断不准确

---

## 五、学习建议

### 如果得分 < 60 分
- 重新阅读 doc_01.md
- 重点理解Telescoping Constructor问题
- 运行demo/中的代码，理解链式调用
- 手写一个简单的Builder

### 如果得分 60-80 分
- 理解基本概念，需要加强实践
- 重点掌握静态内部类Builder的写法
- 理解不可变对象的重要性
- 多做场景判断练习

### 如果得分 > 80 分
- 恭喜！已经掌握建造者模式
- 尝试在项目中应用
- 学习Lombok的@Builder注解
- 继续学习下一个设计模式

---

## 六、核心要点回顾

### 建造者模式的本质
**构建与表示分离**，支持逐步构建复杂对象

### 解决的核心问题
**Telescoping Constructor**（重叠构造器）

### 最常用形式
**静态内部类Builder + 链式调用 + 不可变对象**

### 链式调用的关键
```java
public Builder email(String email) {
    this.email = email;
    return this;  // 返回this
}
```

### 不可变对象的关键
```java
private final String username;  // final字段
private User(Builder builder) { }  // 私有构造函数
// 无setter方法
```

### 记忆口诀
> **参数多且可选多，**  
> **建造者来帮助我，**  
> **逐步构建链式调，**  
> **不可变对象更安全。**

---

**完成自测后**，填写 `note_template.md` 巩固知识！
