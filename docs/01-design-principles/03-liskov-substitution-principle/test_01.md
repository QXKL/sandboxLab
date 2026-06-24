# 里氏替换原则 - 自测题

完成这些题目，检验你对里氏替换原则的理解程度。

---

## 一、概念理解（选择题）

### 1. 里氏替换原则（LSP）的核心含义是什么？

A. 子类可以随意覆盖父类的方法  
B. 父类对象必须能够替换子类对象  
C. 子类对象必须能够替换父类对象，且程序行为不变  
D. 子类必须继承父类的所有方法

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: 里氏替换原则的核心是"子类对象必须能够替换父类对象，且程序行为不变"。这意味着：
- 在使用父类的地方，换成子类应该正常工作
- 子类不能破坏父类的"契约"（承诺的行为）
- 客户端代码不应该需要知道具体是父类还是子类

选项B反了，选项A和D不是LSP的重点。
</details>

---

### 2. 以下关于"契约"（Contract）的说法，哪个是错误的？

A. 子类可以弱化前置条件（接受更多输入）  
B. 子类可以弱化后置条件（返回更弱的保证）  
C. 子类必须维持父类的所有不变式  
D. 子类不能抛出父类未声明的异常

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 选项B是错误的。契约规则：

1. **前置条件**：子类可以弱化（接受更多输入），不能强化
   - ✓ 父类接受整数，子类接受任意数字
   - ✗ 父类接受整数，子类只接受正整数

2. **后置条件**：子类必须满足或强化（保证更好的输出），不能弱化
   - ✓ 父类返回非负数，子类返回正整数
   - ✗ 父类返回非负数，子类可能返回负数

3. **不变式**：子类必须维持父类的所有约束

4. **异常**：子类不能抛出父类未声明的异常（或抛出父类声明异常的子类）

所以选项B"子类可以弱化后置条件"是错误的。
</details>

---

### 3. 正方形-矩形问题中，为什么 Square 不能继承 Rectangle？

A. 因为正方形的面积计算公式不同  
B. 因为正方形破坏了矩形"宽高可独立设置"的契约  
C. 因为正方形比矩形小  
D. 因为正方形不是矩形

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 

**Rectangle 的契约**：
- 宽度和高度可以**独立设置**
- `setWidth(5)` 只改变宽度，不影响高度
- `setHeight(4)` 只改变高度，不影响宽度

**Square 破坏了这个契约**：
- `setWidth(5)` 会同时将高度改为 5
- `setHeight(4)` 会同时将宽度改为 4
- 无法独立设置宽和高

**导致的问题**：
```java
void resize(Rectangle rect) {
    rect.setWidth(5);
    rect.setHeight(4);
    assert rect.getArea() == 20;  // 期望 20
}

resize(new Rectangle());  // ✓ 通过
resize(new Square());     // ✗ 失败：得到 16
```

**核心教训**：
- 数学上"正方形是矩形"（概念上的 is-a）
- 但编程中 Square 不能继承 Rectangle（行为上的 behaves-like-a）
- **LSP 关注行为契约，而非概念关系**

选项A、C、D都不是根本原因。
</details>

---

## 二、代码分析题

### 4. 判断是否违反 LSP

以下是一个鸟类继承体系，请判断是否违反了里氏替换原则，并说明理由。

```java
class Bird {
    void fly() {
        System.out.println("飞行中...");
    }
    
    void eat() {
        System.out.println("进食中...");
    }
}

class Sparrow extends Bird {
    // 麻雀会飞，不需要覆盖
}

class Penguin extends Bird {
    @Override
    void fly() {
        throw new UnsupportedOperationException("企鹅不会飞");
    }
}

// 客户端代码
void letBirdFly(Bird bird) {
    bird.fly();  // 期望所有鸟都能飞
}
```

<details>
<summary>参考答案</summary>

**判断**: 违反了里氏替换原则

**理由**:

1. **破坏契约**
   - Bird 类承诺"所有鸟都能飞"（提供了 fly() 方法）
   - Penguin 破坏了这个契约（抛出异常）

2. **不可替换**
   ```java
   letBirdFly(new Sparrow());  // ✓ 正常工作
   letBirdFly(new Penguin());  // ✗ 抛出异常
   ```
   客户端代码使用 Bird 时，传入 Penguin 会崩溃。

3. **行为不一致**
   - 客户端期望调用 `fly()` 是安全的
   - Penguin 却抛出异常，违反了预期

**重构方案**：

**方案1：接口隔离**
```java
interface Bird {
    void eat();
}

interface FlyableBird extends Bird {
    void fly();
}

class Sparrow implements FlyableBird {
    public void eat() { System.out.println("进食中..."); }
    public void fly() { System.out.println("飞行中..."); }
}

class Penguin implements Bird {
    public void eat() { System.out.println("进食中..."); }
    // 不实现 fly()，企鹅本来就不会飞
}

// 客户端代码
void letBirdFly(FlyableBird bird) {  // 只接受会飞的鸟
    bird.fly();
}
```

**方案2：能力接口**
```java
interface Bird {
    void eat();
}

interface Flyable {
    void fly();
}

class Sparrow implements Bird, Flyable {
    public void eat() { /* ... */ }
    public void fly() { /* ... */ }
}

class Penguin implements Bird {
    public void eat() { /* ... */ }
    // 不实现 Flyable
}
```

**重构要点**：
- ✅ 将"飞行"能力独立成接口
- ✅ 只有会飞的鸟才实现 Flyable 接口
- ✅ 企鹅不实现飞行接口，客户端也不会错误调用
- ✅ 符合接口隔离原则（ISP）和里氏替换原则（LSP）

**核心教训**：
不是所有鸟都会飞，不能让 Penguin 继承有 `fly()` 方法的 Bird 类。
应该将"飞行"抽象为独立的能力接口。
</details>

---

### 5. 重构代码以符合 LSP

以下是一个账户系统，存在违反 LSP 的设计。请重构这段代码，使其符合里氏替换原则。

```java
class Account {
    protected double balance;
    
    public Account(double initialBalance) {
        this.balance = initialBalance;
    }
    
    public void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        } else {
            throw new IllegalArgumentException("余额不足");
        }
    }
    
    public double getBalance() {
        return balance;
    }
}

class OverdraftAccount extends Account {
    private double overdraftLimit;  // 透支额度
    
    public OverdraftAccount(double initialBalance, double overdraftLimit) {
        super(initialBalance);
        this.overdraftLimit = overdraftLimit;
    }
    
    @Override
    public void withdraw(double amount) {
        // 允许透支
        if (balance + overdraftLimit >= amount) {
            balance -= amount;
        } else {
            throw new IllegalArgumentException("超过透支额度");
        }
    }
}
```

<details>
<summary>参考答案</summary>

**问题分析**:

1. **违反不变式**
   - Account 承诺 `balance >= 0`（不允许透支）
   - OverdraftAccount 允许 `balance < 0`（可以透支）
   - 破坏了父类的不变式

2. **行为不一致**
   ```java
   void processWithdraw(Account account) {
       account.withdraw(100);
       assert account.getBalance() >= 0;  // Account 保证这个
   }
   
   processWithdraw(new Account(50));           // ✗ 抛异常（余额不足）
   processWithdraw(new OverdraftAccount(50, 100));  // ✓ 成功，但 balance = -50
   ```

**重构方案**:

**方案1：取消继承关系，使用接口**

```java
// 定义账户的共同行为
interface Account {
    void deposit(double amount);
    boolean canWithdraw(double amount);
    void withdraw(double amount);
    double getBalance();
}

// 普通账户：不允许透支
class StandardAccount implements Account {
    private double balance;
    
    public StandardAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初始余额不能为负");
        }
        this.balance = initialBalance;
    }
    
    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    @Override
    public boolean canWithdraw(double amount) {
        return balance >= amount;
    }
    
    @Override
    public void withdraw(double amount) {
        if (!canWithdraw(amount)) {
            throw new IllegalArgumentException("余额不足");
        }
        balance -= amount;
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
}

// 透支账户：允许透支
class OverdraftAccount implements Account {
    private double balance;
    private double overdraftLimit;
    
    public OverdraftAccount(double initialBalance, double overdraftLimit) {
        this.balance = initialBalance;
        this.overdraftLimit = overdraftLimit;
    }
    
    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    @Override
    public boolean canWithdraw(double amount) {
        return balance + overdraftLimit >= amount;
    }
    
    @Override
    public void withdraw(double amount) {
        if (!canWithdraw(amount)) {
            throw new IllegalArgumentException("超过透支额度");
        }
        balance -= amount;
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
    
    public double getAvailableBalance() {
        return balance + overdraftLimit;
    }
}
```

**方案2：使用组合**

```java
// 账户基类：只包含共同逻辑
class Account {
    protected double balance;
    
    public Account(double initialBalance) {
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getBalance() {
        return balance;
    }
}

// 提款策略接口
interface WithdrawalStrategy {
    boolean canWithdraw(double balance, double amount);
}

// 标准提款策略：不允许透支
class StandardWithdrawal implements WithdrawalStrategy {
    public boolean canWithdraw(double balance, double amount) {
        return balance >= amount;
    }
}

// 透支提款策略
class OverdraftWithdrawal implements WithdrawalStrategy {
    private double overdraftLimit;
    
    public OverdraftWithdrawal(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
    
    public boolean canWithdraw(double balance, double amount) {
        return balance + overdraftLimit >= amount;
    }
}

// 账户服务：使用策略模式
class AccountService {
    private Account account;
    private WithdrawalStrategy strategy;
    
    public AccountService(Account account, WithdrawalStrategy strategy) {
        this.account = account;
        this.strategy = strategy;
    }
    
    public void withdraw(double amount) {
        if (strategy.canWithdraw(account.getBalance(), amount)) {
            account.deposit(-amount);  // 负数表示取款
        } else {
            throw new IllegalArgumentException("无法取款");
        }
    }
}
```

**重构优势**:

| 维度 | 重构前 | 重构后 |
|-----|-------|--------|
| 继承关系 | OverdraftAccount 继承 Account | 各自独立或通过接口关联 |
| 不变式 | OverdraftAccount 破坏不变式 | 各自维护自己的不变式 |
| 可替换性 | 不可替换 | 可以替换 |
| 扩展性 | 难以扩展 | 易于扩展（如添加信用卡账户） |

**核心教训**:
- 如果子类需要破坏父类的不变式，说明继承关系设计有问题
- 应该取消继承，使用接口或组合
- 让每个类维护自己的契约，互不干扰
</details>

---

## 三、场景分析题

### 6. 设计一个图形继承体系

你正在设计一个图形编辑器，需要支持以下图形：
- Rectangle（矩形）：可以独立设置宽度和高度
- Square（正方形）：边长必须相等
- Circle（圆形）：只有半径

**要求**：
1. 设计类结构，确保符合里氏替换原则
2. 说明为什么这样设计
3. 演示如何扩展（如添加三角形）

<details>
<summary>参考答案</summary>

**设计方案**:

```java
// ========== 接口层 ==========
/**
 * Shape - 图形接口
 *
 * 契约：所有图形都能计算面积和周长
 */
interface Shape {
    double getArea();
    double getPerimeter();
    String getType();
}

/**
 * Resizable - 可调整大小的接口
 *
 * 不是所有图形都需要调整大小，所以独立出来
 */
interface Resizable {
    void resize(double factor);  // 按比例缩放
}

// ========== 实现层 ==========
/**
 * Rectangle - 矩形
 */
class Rectangle implements Shape, Resizable {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    @Override
    public double getPerimeter() {
        return 2 * (width + height);
    }
    
    @Override
    public void resize(double factor) {
        this.width *= factor;
        this.height *= factor;
    }
    
    @Override
    public String getType() {
        return "Rectangle";
    }
}

/**
 * Square - 正方形
 *
 * 重要：不继承 Rectangle，各自独立
 */
class Square implements Shape, Resizable {
    private double side;
    
    public Square(double side) {
        this.side = side;
    }
    
    public void setSide(double side) {
        this.side = side;
    }
    
    @Override
    public double getArea() {
        return side * side;
    }
    
    @Override
    public double getPerimeter() {
        return 4 * side;
    }
    
    @Override
    public void resize(double factor) {
        this.side *= factor;
    }
    
    @Override
    public String getType() {
        return "Square";
    }
}

/**
 * Circle - 圆形
 */
class Circle implements Shape, Resizable {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    public void setRadius(double radius) {
        this.radius = radius;
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
    
    @Override
    public void resize(double factor) {
        this.radius *= factor;
    }
    
    @Override
    public String getType() {
        return "Circle";
    }
}

/**
 * Triangle - 三角形（演示扩展）
 */
class Triangle implements Shape {
    private double a, b, c;  // 三条边
    
    public Triangle(double a, double b, double c) {
        if (!isValidTriangle(a, b, c)) {
            throw new IllegalArgumentException("无效的三角形");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    private boolean isValidTriangle(double a, double b, double c) {
        return a + b > c && a + c > b && b + c > a;
    }
    
    @Override
    public double getArea() {
        // 海伦公式
        double s = getPerimeter() / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
    
    @Override
    public double getPerimeter() {
        return a + b + c;
    }
    
    @Override
    public String getType() {
        return "Triangle";
    }
}

// ========== 客户端代码 ==========
/**
 * ShapeEditor - 图形编辑器
 *
 * 依赖 Shape 接口，所有实现都能正确替换
 */
class ShapeEditor {
    public void displayShapeInfo(Shape shape) {
        System.out.println(shape.getType() + ":");
        System.out.println("  面积: " + shape.getArea());
        System.out.println("  周长: " + shape.getPerimeter());
    }
    
    public void resizeIfPossible(Shape shape, double factor) {
        if (shape instanceof Resizable) {
            ((Resizable) shape).resize(factor);
            System.out.println("已缩放 " + factor + " 倍");
        } else {
            System.out.println("此图形不支持缩放");
        }
    }
}
```

**设计说明**:

1. **为什么 Square 不继承 Rectangle？**
   - Rectangle 承诺"宽高可独立设置"
   - Square 无法满足这个契约
   - 因此各自独立，都实现 Shape 接口

2. **接口隔离**
   - Shape 定义所有图形的共同行为
   - Resizable 定义可缩放行为（不是所有图形都需要）
   - 符合接口隔离原则

3. **符合 LSP**
   - 所有 Shape 的实现都能正确替换
   - 客户端不需要知道具体类型
   - 不需要 instanceof 判断（除非需要特殊能力）

4. **易于扩展**
   - 添加 Triangle 只需实现 Shape 接口
   - 不影响现有代码
   - 符合开闭原则

**使用示例**:
```java
ShapeEditor editor = new ShapeEditor();

Shape rect = new Rectangle(5, 4);
Shape square = new Square(4);
Shape circle = new Circle(3);
Shape triangle = new Triangle(3, 4, 5);

// 所有图形都能正确工作
editor.displayShapeInfo(rect);
editor.displayShapeInfo(square);
editor.displayShapeInfo(circle);
editor.displayShapeInfo(triangle);
```

**核心优势**:
- ✅ 符合里氏替换原则
- ✅ 接口清晰，职责分离
- ✅ 易于扩展，易于测试
- ✅ 不依赖具体类型，依赖抽象
</details>

---

## 四、综合题

### 7. LSP、OCP、SRP 的关系

请说明里氏替换原则（LSP）、开闭原则（OCP）、单一职责原则（SRP）之间的关系和区别。

<details>
<summary>参考答案</summary>

**关系图**:

```
SRP（职责单一）
    ↓
    ↓ 清晰的职责 → 清晰的契约
    ↓
LSP（子类正确替换父类）
    ↓
    ↓ 多态有效 → 可以扩展
    ↓
OCP（对扩展开放，对修改关闭）
```

**核心区别**:

| 原则 | 关注点 | 核心思想 | 目标 |
|-----|--------|---------|------|
| **SRP** | 职责划分 | 一个类只做一件事 | 高内聚，低耦合 |
| **LSP** | 继承关系 | 子类必须能替换父类 | 保证多态正确性 |
| **OCP** | 扩展性 | 扩展而非修改 | 提高可维护性 |

**详细关系**:

### 1. SRP 是 LSP 的基础

**原因**：
- 职责单一的类更容易定义清晰的契约
- 契约清晰后，子类更容易正确实现
- 职责混乱的类很难被正确继承

**示例**：
```java
// 违反SRP：职责混乱
class UserManager {
    void validateUser() { }    // 验证
    void saveUser() { }        // 持久化
    void sendEmail() { }       // 通知
}

// 子类很难正确继承，因为不清楚要维护哪些契约

// 符合SRP：职责清晰
class User { }                      // 数据
class UserValidator { }             // 验证
class UserRepository { }            // 持久化
class EmailService { }              // 通知

// 每个类契约清晰，易于被正确继承
```

### 2. LSP 是 OCP 的保证

**原因**：
- OCP 通过扩展（子类）来应对变化
- 如果子类不能正确替换父类，扩展就会失败
- LSP 保证了多态的有效性，从而支持 OCP

**示例**：
```java
// OCP：通过扩展添加新功能
interface PaymentMethod {
    void pay(double amount);
}

// LSP：子类必须正确实现契约
class AlipayPayment implements PaymentMethod {
    public void pay(double amount) {
        // ✓ 正确实现支付
    }
}

class WeChatPayment implements PaymentMethod {
    public void pay(double amount) {
        // ✓ 正确实现支付
    }
}

// 客户端：依赖抽象，符合OCP
void checkout(PaymentMethod method, double amount) {
    method.pay(amount);  // 任何实现都能正确工作（LSP保证）
}
```

如果某个子类违反了 LSP（比如抛出异常或行为不一致），OCP 就失效了。

### 3. 三者共同作用

**完整流程**：

1. **SRP**：将复杂系统拆分成职责单一的类
   - 每个类有明确的职责和契约

2. **LSP**：确保继承关系正确
   - 子类能够替换父类
   - 多态机制有效

3. **OCP**：通过扩展应对变化
   - 添加新子类而不修改现有代码
   - 系统具备良好的可扩展性

**实践示例**：

```java
// === SRP：职责分离 ===
interface Shape {
    double getArea();  // 清晰的契约
}

// === LSP：子类正确实现 ===
class Rectangle implements Shape {
    public double getArea() {
        return width * height;  // ✓ 正确实现契约
    }
}

class Circle implements Shape {
    public double getArea() {
        return Math.PI * radius * radius;  // ✓ 正确实现契约
    }
}

// === OCP：通过扩展添加新功能 ===
class Triangle implements Shape {
    public double getArea() {
        return 0.5 * base * height;  // 新功能，不修改现有代码
    }
}

// 客户端代码
void printArea(Shape shape) {  // 依赖抽象
    System.out.println(shape.getArea());  // 所有实现都能工作
}
```

**违反任何一个原则的后果**：

- 违反 **SRP** → 契约不清晰 → 难以正确继承 → 违反 LSP
- 违反 **LSP** → 子类不能替换父类 → 多态失效 → 违反 OCP
- 违反 **OCP** → 每次变化都要修改 → 风险高，成本高

**总结**：
- **SRP** 提供清晰的职责和契约（基础）
- **LSP** 保证继承关系正确（桥梁）
- **OCP** 实现可扩展的架构（目标）

三者环环相扣，缺一不可。
</details>

---

## 总分统计

- **选择题**（1-3题）：每题 10 分，共 30 分
- **代码分析题**（4-5题）：每题 20 分，共 40 分
- **场景题**（6题）：20 分
- **综合题**（7题）：10 分

**总分**: 100 分  
**及格线**: 80 分

---

## 学习建议

- ✅ 如果得分 ≥ 80分：恭喜！可以继续学习下一个原则
- ⚠️ 如果得分 60-79分：重新阅读 doc_01.md，重点理解"契约"和"正方形-矩形问题"
- ❌ 如果得分 < 60分：建议再运行一次 demo 代码，对比 Square 继承 Rectangle 的问题

**核心要记住**：
1. **子类必须能够替换父类**，且程序行为不变
2. **契约是关键**：前置条件、后置条件、不变式
3. **行为一致性**：概念上的 is-a ≠ 行为上的 behaves-like-a
4. **LSP 是 OCP 的基础**：多态有效，才能扩展

**实践口诀**：父类契约要牢记，子类替换无问题，行为一致是关键，多态才能真给力。
