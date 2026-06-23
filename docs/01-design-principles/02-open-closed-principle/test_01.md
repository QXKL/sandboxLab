# 开闭原则 - 自测题

完成这些题目，检验你对开闭原则的理解程度。

---

## 一、概念理解（选择题）

### 1. 开闭原则（OCP）的核心含义是什么？

A. 类应该对所有操作都开放  
B. 类应该对所有修改都关闭  
C. 软件实体应该对扩展开放，对修改关闭  
D. 代码应该既开放又关闭，保持中立

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: 开闭原则的核心是"对扩展开放，对修改关闭"。意思是当需求变化时，应该通过**添加新代码**来扩展功能，而不是**修改已有的稳定代码**。这样可以降低风险，提高稳定性。

- "对扩展开放"：可以添加新的功能模块
- "对修改关闭"：不修改已有的、经过测试的代码

选项A和B都只说了一半，选项D完全错误。
</details>

---

### 2. 以下哪种实现最符合开闭原则？

**场景**：实现一个图形面积计算器，需要支持圆形、矩形、三角形等多种图形。

A. 在一个方法中用 if-else 判断图形类型，分别计算面积  
B. 为每种图形创建独立的类，都实现 Shape 接口的 calculateArea() 方法  
C. 创建一个大类，包含所有图形的计算方法  
D. 使用 switch-case 根据图形类型调用不同的静态方法

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 选项B通过接口和多态实现，符合开闭原则：

```java
interface Shape {
    double calculateArea();
}

class Circle implements Shape {
    public double calculateArea() { /* 圆形面积 */ }
}

class Rectangle implements Shape {
    public double calculateArea() { /* 矩形面积 */ }
}

// 新增三角形：只需添加新类，不修改现有代码
class Triangle implements Shape {
    public double calculateArea() { /* 三角形面积 */ }
}
```

**为什么其他选项不符合？**
- A、D: 使用 if-else 或 switch-case，每次新增图形都要修改原有代码
- C: 违反单一职责，且新增图形仍需修改这个大类

**核心**：通过接口定义抽象，用多态实现扩展。
</details>

---

### 3. 关于开闭原则，以下说法错误的是？

A. OCP 的实现通常依赖于抽象（接口/抽象类）  
B. 遵循 OCP 可以降低修改现有代码带来的风险  
C. OCP 要求任何情况下都绝对不能修改现有代码  
D. OCP 和策略模式、模板方法模式等设计模式密切相关

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: OCP 强调的是"尽量不修改已有的、稳定的、经过测试的代码"，而不是"绝对不能修改"。

**何时可以修改现有代码？**
- 接口设计有缺陷，需要重构
- 修复 bug
- 性能优化
- 架构升级

**OCP 的本质**：在应对需求变化时，优先考虑通过扩展（添加新代码）而非修改（改动旧代码）来实现。

其他选项都是正确的：
- A: 抽象是实现OCP的关键
- B: 不修改现有代码，风险更低
- D: 很多设计模式都是OCP的具体应用
</details>

---

## 二、代码分析题

### 4. 判断是否违反 OCP

以下是一个折扣计算系统，请判断它是否违反了开闭原则，并说明理由。

```java
class DiscountCalculator {
    public double calculate(double price, String customerType) {
        if (customerType.equals("regular")) {
            return price * 0.95;  // 普通客户 95折
        } else if (customerType.equals("vip")) {
            return price * 0.85;  // VIP客户 85折
        } else if (customerType.equals("svip")) {
            return price * 0.75;  // 超级VIP 75折
        } else {
            return price;  // 无折扣
        }
    }
}
```

<details>
<summary>参考答案</summary>

**判断**: 违反了开闭原则

**理由**:
这个设计使用 if-else 判断客户类型，存在以下问题：

1. **对修改开放**：每次新增客户类型（如企业客户、学生客户），都要修改 `calculate` 方法
2. **对扩展关闭**：无法在不修改现有代码的情况下扩展新功能
3. **风险高**：修改 `calculate` 方法可能影响现有折扣逻辑
4. **测试成本高**：每次修改都要重新测试所有分支

**重构方案**（符合 OCP）:

```java
// 1. 定义折扣策略接口
interface DiscountStrategy {
    double calculate(double price);
}

// 2. 各种折扣策略的实现
class RegularCustomerDiscount implements DiscountStrategy {
    public double calculate(double price) {
        return price * 0.95;
    }
}

class VipCustomerDiscount implements DiscountStrategy {
    public double calculate(double price) {
        return price * 0.85;
    }
}

class SVipCustomerDiscount implements DiscountStrategy {
    public double calculate(double price) {
        return price * 0.75;
    }
}

// 3. 计算器依赖抽象
class DiscountCalculator {
    private DiscountStrategy strategy;
    
    public DiscountCalculator(DiscountStrategy strategy) {
        this.strategy = strategy;
    }
    
    public double calculate(double price) {
        return strategy.calculate(price);
    }
}

// 4. 使用示例
DiscountCalculator calculator = new DiscountCalculator(new VipCustomerDiscount());
double finalPrice = calculator.calculate(100.0);
```

**重构后的优势**:
- ✅ 新增客户类型：只需添加新的 DiscountStrategy 实现类
- ✅ 不修改现有代码：DiscountCalculator 和其他策略类无需改动
- ✅ 降低风险：新增功能不影响现有逻辑
- ✅ 易于测试：每个策略独立测试
- ✅ 灵活组合：可以运行时切换折扣策略

这就是"对扩展开放，对修改关闭"的体现。
</details>

---

### 5. 重构代码以符合 OCP

以下是一个日志系统，需要支持多种输出方式（控制台、文件、数据库）。请重构这段代码，使其符合开闭原则。

```java
class Logger {
    public void log(String message, String outputType) {
        if (outputType.equals("console")) {
            System.out.println("Console: " + message);
        } else if (outputType.equals("file")) {
            // 写入文件
            System.out.println("File: " + message);
        } else if (outputType.equals("database")) {
            // 写入数据库
            System.out.println("Database: " + message);
        }
    }
}
```

<details>
<summary>参考答案</summary>

**问题分析**:
当前设计使用 if-else 判断输出类型，违反了开闭原则：
- 新增输出方式（如远程日志、ElasticSearch）需要修改 log 方法
- 代码会越来越臃肿
- 测试和维护成本高

**重构方案**:

```java
// ========== 抽象层 ==========
/**
 * LogWriter 接口 - 定义日志写入行为
 */
interface LogWriter {
    void write(String message);
}

// ========== 具体实现 ==========
/**
 * 控制台日志
 */
class ConsoleLogWriter implements LogWriter {
    @Override
    public void write(String message) {
        System.out.println("[Console] " + message);
    }
}

/**
 * 文件日志
 */
class FileLogWriter implements LogWriter {
    private String filePath;
    
    public FileLogWriter(String filePath) {
        this.filePath = filePath;
    }
    
    @Override
    public void write(String message) {
        // 实际项目中这里会有文件写入逻辑
        System.out.println("[File: " + filePath + "] " + message);
    }
}

/**
 * 数据库日志
 */
class DatabaseLogWriter implements LogWriter {
    private String connectionString;
    
    public DatabaseLogWriter(String connectionString) {
        this.connectionString = connectionString;
    }
    
    @Override
    public void write(String message) {
        // 实际项目中这里会有数据库写入逻辑
        System.out.println("[Database: " + connectionString + "] " + message);
    }
}

/**
 * 远程日志（新增功能，不修改现有代码）
 */
class RemoteLogWriter implements LogWriter {
    private String serverUrl;
    
    public RemoteLogWriter(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    @Override
    public void write(String message) {
        System.out.println("[Remote: " + serverUrl + "] " + message);
    }
}

// ========== Logger类 - 依赖抽象 ==========
/**
 * Logger - 支持多个日志输出器
 */
class Logger {
    private List<LogWriter> writers = new ArrayList<>();
    
    /**
     * 添加日志输出器
     */
    public void addWriter(LogWriter writer) {
        writers.add(writer);
    }
    
    /**
     * 写日志 - 通过多态调用，无需if-else
     */
    public void log(String message) {
        for (LogWriter writer : writers) {
            writer.write(message);
        }
    }
}

// ========== 使用示例 ==========
public class LoggerDemo {
    public static void main(String[] args) {
        Logger logger = new Logger();
        
        // 配置日志输出方式
        logger.addWriter(new ConsoleLogWriter());
        logger.addWriter(new FileLogWriter("/var/log/app.log"));
        logger.addWriter(new DatabaseLogWriter("jdbc:mysql://localhost/logs"));
        
        // 写日志（同时输出到多个目标）
        logger.log("Application started");
        
        // 动态添加远程日志（不修改现有代码）
        logger.addWriter(new RemoteLogWriter("https://log-server.com/api"));
        logger.log("New feature deployed");
    }
}
```

**重构要点**:

1. **定义抽象接口** LogWriter：规定"写日志"这个行为
2. **具体实现类**：每种输出方式一个类
3. **Logger 依赖抽象**：通过 LogWriter 接口调用，不依赖具体实现
4. **扩展方式**：添加新的 LogWriter 实现类即可

**优势对比**:

| 维度 | 重构前 | 重构后 |
|-----|--------|--------|
| 新增输出方式 | 修改 log 方法 | 添加新类 |
| 风险 | 可能影响现有功能 | 不影响现有代码 |
| 组合性 | 只能选一种输出 | 可以同时输出到多个目标 |
| 测试 | 测试所有分支 | 只测试新类 |
| 运行时配置 | 不支持 | 支持动态添加/移除 |

**符合 OCP**:
- ✅ 对扩展开放：可以添加新的 LogWriter 实现
- ✅ 对修改关闭：Logger 类和现有 LogWriter 无需修改
</details>

---

## 三、场景分析题

### 6. 设计一个支付系统

你正在开发一个电商系统的支付模块，需求如下：

**初始需求**:
- 支持支付宝支付
- 支持微信支付
- 支持信用卡支付

**未来可能的需求**:
- 支持银联支付
- 支持 PayPal 支付
- 支持数字货币支付
- 支持分期付款
- 支持组合支付（余额+信用卡）

**问题**: 请设计类的结构，使其符合开闭原则。要求：
1. 画出类图（或用代码表达）
2. 说明如何实现"对扩展开放，对修改关闭"
3. 举例说明新增支付方式时的操作步骤

<details>
<summary>参考答案</summary>

**设计方案**:

```java
// ========== 抽象层 ==========
/**
 * PaymentMethod - 支付方式接口
 */
interface PaymentMethod {
    /**
     * 执行支付
     * @param amount 支付金额
     * @return 支付结果
     */
    PaymentResult pay(double amount);
    
    /**
     * 获取支付方式名称
     */
    String getName();
}

/**
 * PaymentResult - 支付结果
 */
class PaymentResult {
    private boolean success;
    private String message;
    private String transactionId;
    
    public PaymentResult(boolean success, String message, String transactionId) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
    }
    
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getTransactionId() { return transactionId; }
}

// ========== 具体支付方式 ==========
/**
 * 支付宝支付
 */
class AlipayPayment implements PaymentMethod {
    @Override
    public PaymentResult pay(double amount) {
        System.out.println("使用支付宝支付: ¥" + amount);
        // 实际调用支付宝SDK
        return new PaymentResult(true, "支付成功", "ALIPAY_" + System.currentTimeMillis());
    }
    
    @Override
    public String getName() {
        return "支付宝";
    }
}

/**
 * 微信支付
 */
class WeChatPayment implements PaymentMethod {
    @Override
    public PaymentResult pay(double amount) {
        System.out.println("使用微信支付: ¥" + amount);
        // 实际调用微信支付SDK
        return new PaymentResult(true, "支付成功", "WECHAT_" + System.currentTimeMillis());
    }
    
    @Override
    public String getName() {
        return "微信支付";
    }
}

/**
 * 信用卡支付
 */
class CreditCardPayment implements PaymentMethod {
    private String cardNumber;
    
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public PaymentResult pay(double amount) {
        System.out.println("使用信用卡支付: ¥" + amount + " (卡号: " + maskCard(cardNumber) + ")");
        // 实际调用银行接口
        return new PaymentResult(true, "支付成功", "CARD_" + System.currentTimeMillis());
    }
    
    @Override
    public String getName() {
        return "信用卡";
    }
    
    private String maskCard(String card) {
        return "****" + card.substring(card.length() - 4);
    }
}

/**
 * PayPal支付（新需求：只需添加新类，不修改现有代码）
 */
class PayPalPayment implements PaymentMethod {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public PaymentResult pay(double amount) {
        System.out.println("使用PayPal支付: $" + amount + " (账号: " + email + ")");
        return new PaymentResult(true, "支付成功", "PAYPAL_" + System.currentTimeMillis());
    }
    
    @Override
    public String getName() {
        return "PayPal";
    }
}

// ========== 支付服务 ==========
/**
 * PaymentService - 支付服务（依赖抽象）
 */
class PaymentService {
    /**
     * 执行支付
     * 
     * 关键：依赖 PaymentMethod 接口，不依赖具体实现
     * 通过多态调用，无需 if-else 判断支付方式
     */
    public PaymentResult processPayment(PaymentMethod method, double amount) {
        System.out.println("\n=== 开始支付流程 ===");
        System.out.println("支付方式: " + method.getName());
        System.out.println("支付金额: ¥" + amount);
        
        // 多态调用：不关心具体是哪种支付方式
        PaymentResult result = method.pay(amount);
        
        if (result.isSuccess()) {
            System.out.println("✓ 支付成功");
            System.out.println("交易号: " + result.getTransactionId());
        } else {
            System.out.println("✗ 支付失败: " + result.getMessage());
        }
        
        return result;
    }
}

// ========== 使用示例 ==========
public class PaymentSystemDemo {
    public static void main(String[] args) {
        PaymentService service = new PaymentService();
        
        // 使用不同的支付方式
        service.processPayment(new AlipayPayment(), 99.99);
        service.processPayment(new WeChatPayment(), 199.50);
        service.processPayment(new CreditCardPayment("6222021234567890"), 299.00);
        
        // 新增PayPal支付：只需创建新类，不修改PaymentService
        service.processPayment(new PayPalPayment("user@example.com"), 49.99);
        
        System.out.println("\n=== 符合开闭原则的优势 ===");
        System.out.println("✓ 新增支付方式：只需实现 PaymentMethod 接口");
        System.out.println("✓ 不修改现有代码：PaymentService 无需改动");
        System.out.println("✓ 独立测试：每种支付方式单独测试");
        System.out.println("✓ 运行时扩展：可以动态加载支付插件");
    }
}
```

**类图关系**:
```
<<interface>>                  
PaymentMethod                   
    ↑
    |
    |-- AlipayPayment
    |-- WeChatPayment
    |-- CreditCardPayment
    |-- PayPalPayment (新增)
    
PaymentService --> PaymentMethod (依赖抽象)
```

**如何实现"对扩展开放，对修改关闭"**:

1. **抽象是关键**：定义 PaymentMethod 接口
2. **依赖抽象**：PaymentService 依赖接口，不依赖具体实现
3. **多态调用**：通过接口调用 pay()，无需 if-else 判断
4. **扩展方式**：添加新的 PaymentMethod 实现类

**新增支付方式的操作步骤**:

假设要新增"数字货币支付"：

```java
// 步骤1: 创建新类实现接口（唯一需要写的代码）
class CryptoPayment implements PaymentMethod {
    @Override
    public PaymentResult pay(double amount) {
        System.out.println("使用比特币支付: " + amount + " BTC");
        return new PaymentResult(true, "支付成功", "CRYPTO_" + System.currentTimeMillis());
    }
    
    @Override
    public String getName() {
        return "数字货币";
    }
}

// 步骤2: 直接使用（不修改任何现有代码）
service.processPayment(new CryptoPayment(), 0.005);
```

**就这么简单！**
- ✅ 不修改 PaymentService
- ✅ 不修改 PaymentMethod 接口
- ✅ 不修改其他支付方式类
- ✅ 只添加新代码，不修改旧代码

**进阶扩展**（组合支付）:

```java
/**
 * 组合支付：余额+信用卡
 */
class CompositePayment implements PaymentMethod {
    private PaymentMethod primary;    // 主支付方式
    private PaymentMethod secondary;  // 备用支付方式
    private double primaryLimit;      // 主支付方式额度
    
    @Override
    public PaymentResult pay(double amount) {
        if (amount <= primaryLimit) {
            return primary.pay(amount);
        } else {
            primary.pay(primaryLimit);
            secondary.pay(amount - primaryLimit);
            return new PaymentResult(true, "组合支付成功", "COMBO_" + System.currentTimeMillis());
        }
    }
    
    @Override
    public String getName() {
        return "组合支付";
    }
}
```

这就是开闭原则的强大之处：**通过良好的抽象设计，让系统具备无限的扩展可能性**。
</details>

---

## 四、对比题

### 7. OCP vs SRP 的区别

请说明开闭原则（OCP）和单一职责原则（SRP）的区别和联系。

<details>
<summary>参考答案</summary>

**核心区别**:

| 维度 | 单一职责原则（SRP） | 开闭原则（OCP） |
|------|-------------------|----------------|
| **关注点** | 职责划分 | 扩展性 |
| **核心思想** | 一个类只做一件事，只有一个修改的理由 | 通过扩展而非修改来应对变化 |
| **目标** | 提高内聚性，降低耦合 | 提高扩展性，降低修改风险 |
| **实现方式** | 拆分类，让每个类职责单一 | 定义抽象，依赖接口，用多态扩展 |
| **判断标准** | 看变化原因的数量 | 看扩展时是否需要修改现有代码 |
| **违反后果** | 类变得臃肿，难以维护 | 每次扩展都要改代码，风险高 |

**联系**:

1. **SRP 是 OCP 的基础**
   - 职责单一的类更容易抽象
   - 职责混乱的类很难符合 OCP

2. **OCP 依赖 SRP**
   - 如果一个类承担多个职责，很难做到"对修改关闭"
   - 职责清晰后，更容易识别变化点并抽象

3. **相互促进**
   - 遵循 SRP 的类，更容易符合 OCP
   - 为了实现 OCP，往往需要先应用 SRP

**示例对比**:

**场景**：通知系统

**违反 SRP 和 OCP**:
```java
class NotificationService {
    // 违反SRP: 一个类承担多个职责
    // 违反OCP: 每次新增通知方式都要修改这个类
    public void send(String message, String type) {
        if (type.equals("email")) {
            // 邮件发送逻辑
            // SMTP连接、格式化、发送...
        } else if (type.equals("sms")) {
            // 短信发送逻辑
            // 网关连接、编码、发送...
        }
    }
}
```

**应用 SRP（职责分离）**:
```java
class EmailNotifier {
    public void send(String message) { /* 邮件逻辑 */ }
}

class SmsNotifier {
    public void send(String message) { /* 短信逻辑 */ }
}

// 改善：职责分离了，但还不符合OCP
// 新增通知方式仍需修改调用代码
```

**同时应用 SRP + OCP**:
```java
// 抽象（OCP的关键）
interface Notifier {
    void send(String message);
}

// 具体实现（SRP：每个类职责单一）
class EmailNotifier implements Notifier {
    public void send(String message) { /* 邮件逻辑 */ }
}

class SmsNotifier implements Notifier {
    public void send(String message) { /* 短信逻辑 */ }
}

// 服务类（依赖抽象，符合OCP）
class NotificationService {
    public void sendAll(String message, List<Notifier> notifiers) {
        for (Notifier notifier : notifiers) {
            notifier.send(message);  // 多态
        }
    }
}

// 新增通知方式：只需添加新类，不修改现有代码
class PushNotifier implements Notifier {
    public void send(String message) { /* 推送逻辑 */ }
}
```

**总结**:
- **SRP** 让类的职责清晰，**是设计的起点**
- **OCP** 让系统可扩展，**是设计的目标**
- 两者结合，才能得到既清晰又灵活的设计
</details>

---

## 总分统计

- **选择题**（1-3题）：每题 10 分，共 30 分
- **代码分析题**（4-5题）：每题 20 分，共 40 分
- **场景题**（6题）：20 分
- **对比题**（7题）：10 分

**总分**: 100 分  
**及格线**: 80 分

---

## 学习建议

- ✅ 如果得分 ≥ 80分：恭喜！可以继续学习下一个原则
- ⚠️ 如果得分 60-79分：重新阅读 doc_01.md，重点理解"抽象"和"多态"
- ❌ 如果得分 < 60分：建议再运行一次 demo 代码，对比 if-else 和接口的区别

**核心要记住**：
1. **对扩展开放，对修改关闭**
2. **抽象是关键**：通过接口/抽象类定义契约
3. **依赖抽象**：客户端依赖接口，不依赖具体实现
4. **多态扩展**：通过添加新类来扩展功能

**口诀**：变化来临莫慌张，抽象接口来帮忙，新增类来做扩展，旧代码中不用慌。
