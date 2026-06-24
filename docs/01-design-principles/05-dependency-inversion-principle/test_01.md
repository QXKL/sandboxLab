# 依赖倒置原则 - 自测题

## 一、概念理解（选择题）

### 1. 依赖倒置原则（DIP）的核心含义是什么？

A. 低层模块不应该依赖高层模块  
B. 高层模块不应该依赖低层模块，两者都应该依赖抽象  
C. 所有类都必须有接口  
D. 使用依赖注入框架

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: DIP的核心是"高层模块不应该依赖低层模块，两者都应该依赖抽象"。
- 传统依赖：高层 → 低层（紧耦合）
- 依赖倒置：高层 → 抽象 ← 低层（松耦合）
- 抽象不依赖细节，细节依赖抽象
</details>

---

### 2. 以下哪种方式符合依赖倒置原则？

```java
// 方式A
class OrderService {
    private MySQLDatabase db = new MySQLDatabase();
}

// 方式B
class OrderService {
    private Repository repo;
    public OrderService(Repository repo) {
        this.repo = repo;
    }
}
```

A. 方式A符合DIP  
B. 方式B符合DIP  
C. 两者都符合DIP  
D. 两者都不符合DIP

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 
- **方式A违反DIP**：直接依赖具体类MySQLDatabase，硬编码创建依赖
- **方式B符合DIP**：依赖抽象接口Repository，通过构造函数注入

符合DIP的关键：
1. 依赖抽象（接口）而非具体类
2. 通过依赖注入获得实现
3. 不在业务逻辑中new具体类
</details>

---

### 3. DIP、IoC、DI的关系是什么？

A. 三者是同一个概念  
B. DIP是原则，IoC是模式，DI是实现技术  
C. DIP和IoC是原则，DI是框架  
D. 三者没有关系

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 
- **DIP（Dependency Inversion Principle）**：设计原则 - 依赖抽象，不依赖具体
- **IoC（Inversion of Control）**：设计模式 - 控制反转，由容器管理对象
- **DI（Dependency Injection）**：实现技术 - 依赖注入，注入依赖对象

**关系**：DIP是理论基础，IoC是实现模式，DI是具体技术。
</details>

---

## 二、代码分析题

### 4. 重构代码以符合 DIP

以下代码违反了依赖倒置原则，请重构：

```java
class UserService {
    private MySQLDatabase database;
    private EmailSender emailSender;
    
    public UserService() {
        this.database = new MySQLDatabase();
        this.emailSender = new EmailSender();
    }
    
    public void createUser(User user) {
        database.save(user);
        emailSender.send("欢迎注册");
    }
}
```

<details>
<summary>参考答案</summary>

**重构方案**:

```java
// 1. 定义抽象接口
interface UserRepository {
    void save(User user);
}

interface NotificationService {
    void send(String message);
}

// 2. 具体实现
class MySQLUserRepository implements UserRepository {
    public void save(User user) {
        // MySQL实现
    }
}

class EmailNotificationService implements NotificationService {
    public void send(String message) {
        // Email实现
    }
}

// 3. 高层依赖抽象，通过依赖注入
class UserService {
    private UserRepository repository;
    private NotificationService notificationService;
    
    // 构造函数注入
    public UserService(UserRepository repository, 
                      NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }
    
    public void createUser(User user) {
        repository.save(user);
        notificationService.send("欢迎注册");
    }
}

// 4. 使用
UserRepository repo = new MySQLUserRepository();
NotificationService notifier = new EmailNotificationService();
UserService service = new UserService(repo, notifier);
```

**改进点**:
- ✅ 定义抽象接口
- ✅ 高层依赖抽象
- ✅ 使用依赖注入
- ✅ 易于更换实现
- ✅ 易于测试
</details>

---

## 三、SOLID 总结题

### 5. 总结 SOLID 五大原则

请简要说明SOLID五大原则的核心思想及它们之间的关系。

<details>
<summary>参考答案</summary>

**SOLID五大原则**:

| 原则 | 核心思想 | 目标 |
|-----|---------|------|
| **S**RP | 一个类只做一件事，只有一个修改的理由 | 高内聚 |
| **O**CP | 对扩展开放，对修改关闭 | 可扩展 |
| **L**SP | 子类必须能够替换父类，且程序行为不变 | 多态正确 |
| **I**SP | 客户端不应该依赖它不使用的接口 | 接口隔离 |
| **D**IP | 高层不依赖低层，都依赖抽象 | 低耦合 |

**它们的关系**:

```
DIP（依赖抽象）—— 基石
    ↓
SRP（职责单一）+ ISP（接口隔离）—— 设计指导
    ↓
LSP（正确替换）—— 质量保证
    ↓
OCP（可扩展）—— 最终目标
```

**协作示例**:

```java
// DIP: 依赖抽象
interface PaymentMethod {
    void pay(double amount);
}

// SRP: 每个支付方式职责单一
class AlipayPayment implements PaymentMethod {
    public void pay(double amount) { /* Alipay */ }
}

class WeChatPayment implements PaymentMethod {
    public void pay(double amount) { /* WeChat */ }
}

// ISP: 接口小而专注（只有pay方法）
// LSP: 所有实现都能正确替换
// OCP: 新增支付方式不修改现有代码

// 业务代码依赖抽象
class PaymentService {
    private PaymentMethod method;
    
    public PaymentService(PaymentMethod method) {  // DIP
        this.method = method;
    }
    
    public void processPayment(double amount) {
        method.pay(amount);  // 多态
    }
}
```

**总结**: 
- DIP是基础，确保依赖抽象
- SRP和ISP指导如何设计类和接口
- LSP保证多态的正确性
- 最终实现OCP（可扩展、易维护）

**记住**: SOLID不是孤立的，而是相互支持的设计原则体系！
</details>

---

## 总分统计

- **选择题**（1-3题）：每题 20 分，共 60 分
- **代码分析题**（4题）：20 分
- **综合题**（5题）：20 分

**总分**: 100 分  
**及格线**: 80 分

---

## 学习建议

**🎉 恭喜完成 SOLID 五大原则的学习！**

现在你应该：
1. 理解每个原则的核心思想
2. 知道如何判断是否违反原则
3. 能够重构违反原则的代码
4. 理解原则之间的关系

**下一步**:
- 回顾五个原则，总结它们的关系
- 在项目中识别违反原则的代码
- 应用这些原则改进代码设计
- 继续学习设计模式（建立在SOLID基础上）

**实践口诀**:
> 单一职责要牢记，  
> 开闭原则别忘记，  
> 里氏替换保质量，  
> 接口隔离降耦合，  
> 依赖倒置是基石，  
> SOLID助你写好码！
