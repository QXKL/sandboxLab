# 单一职责原则 - 自测题

完成这些题目，检验你对单一职责原则的理解程度。

---

## 一、概念理解（选择题）

### 1. 单一职责原则（SRP）的核心含义是什么？

A. 一个类只能有一个方法  
B. 一个类只能有一个属性  
C. 一个类应该只有一个引起它变化的原因  
D. 一个类只能被一个其他类使用

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: 单一职责原则的核心是"一个类应该只有一个引起它变化的原因"。这意味着一个类应该只负责一个职责领域，当需求变化时，只因为这一个职责的变化而修改。选项A和B是对SRP的误解，SRP不限制方法或属性的数量，而是关注职责的单一性。选项D说的是依赖关系，与SRP无关。
</details>

---

### 2. 以下哪个类违反了单一职责原则？

A. `UserRepository` - 负责用户数据的增删改查  
B. `EmailService` - 负责发送各种类型的邮件  
C. `OrderManager` - 负责订单验证、订单保存、发送订单确认邮件、生成发票  
D. `StringUtils` - 提供各种字符串处理工具方法

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: `OrderManager` 承担了4个不同的职责：验证（业务规则）、持久化（数据存储）、邮件通知、财务处理（发票）。这些职责的变化原因完全不同：业务规则变化、数据库技术变化、邮件服务变化、财务规范变化。应该拆分成 `OrderValidator`、`OrderRepository`、`NotificationService`、`InvoiceGenerator`。

其他选项：
- A: UserRepository 的所有方法都属于"数据持久化"这一个职责
- B: EmailService 的所有方法都属于"邮件发送"这一个职责
- D: StringUtils 的所有方法都属于"字符串处理"这一个职责
</details>

---

### 3. 关于单一职责原则，以下说法错误的是？

A. SRP 有助于提高代码的可测试性  
B. SRP 会导致类的数量增加  
C. SRP 要求每个类只能有一个公共方法  
D. SRP 降低了类之间的耦合度

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: SRP 不限制方法的数量。一个类可以有多个公共方法，只要这些方法都服务于同一个职责即可。例如 `UserValidator` 可以有 `validate()`, `isValidEmail()`, `isStrongPassword()` 等多个方法，它们都属于"验证"这一个职责。

其他选项都是正确的：
- A: 职责单一的类依赖少，容易mock，测试更简单
- B: 职责分离确实会增加类的数量，但这是合理的代价
- D: 职责分离减少了类之间不必要的依赖
</details>

---

## 二、代码分析题

### 4. 判断职责是否单一

以下是一个 `Product` 类，请判断它是否违反了单一职责原则，并说明理由。

```java
class Product {
    private String name;
    private double price;
    private int stock;
    
    public Product(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // 价格计算
    public double calculateDiscount(double percentage) {
        return price * (1 - percentage / 100);
    }
    
    // 库存管理
    public boolean isInStock() {
        return stock > 0;
    }
    
    public void reduceStock(int quantity) {
        stock -= quantity;
    }
    
    // 数据持久化
    public void saveToDatabase() {
        // 保存到数据库的代码
        System.out.println("Saving product to database...");
    }
    
    // 格式化输出
    public String toJson() {
        return "{\"name\":\"" + name + "\",\"price\":" + price + "}";
    }
}
```

<details>
<summary>参考答案</summary>

**判断**: 违反了单一职责原则

**理由**:
这个类承担了至少 4 个不同的职责：

1. **数据存储**（属性: name, price, stock）
2. **业务逻辑**（calculateDiscount - 价格计算）
3. **库存管理**（isInStock, reduceStock）
4. **数据持久化**（saveToDatabase）
5. **数据表示**（toJson - 序列化）

**变化原因分析**:
- 业务规则变化（折扣算法）→ 要改这个类
- 数据库技术变化（MySQL → MongoDB）→ 要改这个类
- 序列化格式变化（JSON → XML）→ 要改这个类
- 库存管理规则变化 → 要改这个类

**重构建议**:
```java
// 职责1: 纯数据
class Product {
    private String name;
    private double price;
    private int stock;
    // getters/setters
}

// 职责2: 价格计算
class PriceCalculator {
    public double calculateDiscount(Product product, double percentage) {
        return product.getPrice() * (1 - percentage / 100);
    }
}

// 职责3: 库存管理
class InventoryManager {
    public boolean isInStock(Product product) {
        return product.getStock() > 0;
    }
    
    public void reduceStock(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
    }
}

// 职责4: 持久化
class ProductRepository {
    public void save(Product product) {
        // 数据库操作
    }
}

// 职责5: 序列化
class ProductSerializer {
    public String toJson(Product product) {
        return "{\"name\":\"" + product.getName() + 
               "\",\"price\":" + product.getPrice() + "}";
    }
}
```
</details>

---

### 5. 重构代码

以下是一个 `Report` 类，它负责生成报表并保存为文件。请重构这段代码，使其符合单一职责原则。

```java
class Report {
    private String title;
    private List<String> data;
    
    public Report(String title, List<String> data) {
        this.title = title;
        this.data = data;
    }
    
    // 生成报表内容
    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(title).append(" ===\n");
        for (String item : data) {
            sb.append("- ").append(item).append("\n");
        }
        return sb.toString();
    }
    
    // 保存到文件
    public void saveToFile(String filename) {
        String content = generate();
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            System.out.println("Report saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

<details>
<summary>参考答案</summary>

**问题分析**:
`Report` 类承担了两个职责：
1. 报表内容生成（业务逻辑）
2. 文件保存（I/O操作）

变化原因：
- 报表格式变化（文本 → HTML → PDF）→ 要改这个类
- 保存方式变化（本地文件 → 云存储 → 数据库）→ 要改这个类

**重构后的代码**:

```java
// 职责1: 报表数据
class Report {
    private final String title;
    private final List<String> data;
    
    public Report(String title, List<String> data) {
        this.title = title;
        this.data = data;
    }
    
    public String getTitle() { return title; }
    public List<String> getData() { return data; }
}

// 职责2: 报表格式化（生成内容）
class ReportFormatter {
    public String format(Report report) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(report.getTitle()).append(" ===\n");
        for (String item : report.getData()) {
            sb.append("- ").append(item).append("\n");
        }
        return sb.toString();
    }
}

// 职责3: 文件保存
class FileWriter {
    public void save(String content, String filename) {
        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(content);
            System.out.println("File saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 使用示例
class ReportService {
    private final ReportFormatter formatter;
    private final FileWriter fileWriter;
    
    public ReportService(ReportFormatter formatter, FileWriter fileWriter) {
        this.formatter = formatter;
        this.fileWriter = fileWriter;
    }
    
    public void generateAndSave(Report report, String filename) {
        String content = formatter.format(report);
        fileWriter.save(content, filename);
    }
}
```

**重构优势**:
- ✅ 想要支持HTML格式？只需添加 `HtmlReportFormatter`
- ✅ 想要保存到云存储？只需添加 `CloudStorageWriter`
- ✅ 每个类都很小，容易测试
- ✅ 可以自由组合：文本报表+云存储、HTML报表+本地文件
</details>

---

## 三、场景分析题

### 6. 电商订单处理系统

你正在开发一个电商系统的订单处理模块。产品经理给出了以下需求：

**需求**:
1. 创建订单时需要验证：用户信息是否完整、商品是否有库存、价格是否正确
2. 订单创建后需要：保存到数据库、扣减库存、发送确认邮件、记录日志
3. 订单支付成功后需要：更新订单状态、生成发票、通知物流系统、发送短信

**问题**: 请设计类的职责划分，使其符合单一职责原则。列出你会创建哪些类，每个类负责什么职责。

<details>
<summary>参考答案</summary>

**职责划分设计**:

```java
// ========== 核心数据模型 ==========
class Order {
    // 订单数据：订单号、用户ID、商品列表、金额、状态等
}

class OrderItem {
    // 订单项数据：商品ID、数量、单价等
}

// ========== 验证职责 ==========
class OrderValidator {
    // 职责：验证订单数据的合法性
    ValidationResult validate(Order order);
    boolean isUserInfoComplete(String userId);
    boolean isPriceCorrect(Order order);
}

class InventoryChecker {
    // 职责：检查库存是否充足
    boolean checkStock(List<OrderItem> items);
}

// ========== 持久化职责 ==========
class OrderRepository {
    // 职责：订单数据的数据库操作
    void save(Order order);
    void updateStatus(String orderId, OrderStatus status);
    Order findById(String orderId);
}

// ========== 库存管理职责 ==========
class InventoryManager {
    // 职责：库存的增减操作
    void reduceStock(List<OrderItem> items);
    void restoreStock(List<OrderItem> items);
}

// ========== 通知职责 ==========
class EmailNotifier {
    // 职责：发送邮件通知
    void sendOrderConfirmation(Order order);
}

class SmsNotifier {
    // 职责：发送短信通知
    void sendPaymentSuccess(Order order);
}

// ========== 日志职责 ==========
class OrderLogger {
    // 职责：记录订单相关的日志
    void logOrderCreated(Order order);
    void logPaymentSuccess(Order order);
}

// ========== 发票职责 ==========
class InvoiceGenerator {
    // 职责：生成发票
    Invoice generate(Order order);
}

// ========== 物流集成职责 ==========
class LogisticsService {
    // 职责：与物流系统对接
    void notifyShipping(Order order);
}

// ========== 业务流程协调 ==========
class OrderService {
    // 职责：协调各个职责完成业务流程
    private OrderValidator validator;
    private InventoryChecker inventoryChecker;
    private OrderRepository repository;
    private InventoryManager inventoryManager;
    private EmailNotifier emailNotifier;
    private SmsNotifier smsNotifier;
    private OrderLogger logger;
    private InvoiceGenerator invoiceGenerator;
    private LogisticsService logisticsService;
    
    // 创建订单流程
    public Order createOrder(Order order) {
        // 1. 验证
        validator.validate(order);
        inventoryChecker.checkStock(order.getItems());
        
        // 2. 处理
        repository.save(order);
        inventoryManager.reduceStock(order.getItems());
        
        // 3. 通知
        emailNotifier.sendOrderConfirmation(order);
        logger.logOrderCreated(order);
        
        return order;
    }
    
    // 支付成功流程
    public void handlePaymentSuccess(String orderId) {
        // 1. 更新状态
        repository.updateStatus(orderId, OrderStatus.PAID);
        
        // 2. 后续处理
        Order order = repository.findById(orderId);
        invoiceGenerator.generate(order);
        logisticsService.notifyShipping(order);
        
        // 3. 通知
        smsNotifier.sendPaymentSuccess(order);
        logger.logPaymentSuccess(order);
    }
}
```

**设计要点**:

1. **数据与行为分离**: `Order`、`OrderItem` 是纯数据类
2. **按变化原因分类**: 
   - 业务规则变化 → 只改 `OrderValidator`
   - 数据库变化 → 只改 `OrderRepository`
   - 邮件服务变化 → 只改 `EmailNotifier`
3. **单一入口**: `OrderService` 作为协调者，对外提供统一接口
4. **易于扩展**: 需要新增支付方式？只需添加新的 `PaymentProcessor`
5. **易于测试**: 每个类都可以独立测试，mock 其依赖即可

**对比**: 如果把这些都放在一个 `OrderManager` 类中，会有上千行代码，难以维护和测试。
</details>

---

## 四、判断题（快速检测）

判断以下说法是否正确，并简要说明理由。

### 7. "一个类的代码行数超过500行，一定违反了单一职责原则。"

<details>
<summary>查看答案</summary>

**答案**: ❌ 错误

**理由**: 代码行数不是判断是否违反SRP的标准。判断标准是"变化的原因"。如果一个类虽然有500行，但所有代码都服务于同一个职责（比如一个复杂的算法实现），那就没有违反SRP。反之，一个50行的类如果承担了3个不相关的职责，也是违反了SRP。

**判断SRP的正确方法**:
- 这个类会因为哪些原因而修改？
- 用一句话描述这个类，是否需要用到"和"、"以及"？
- 测试这个类需要mock多少依赖？
</details>

---

### 8. "单一职责原则会导致类的数量爆炸，增加系统复杂度。"

<details>
<summary>查看答案</summary>

**答案**: ⚠️ 部分正确

**理由**: 
- ✅ 类的数量确实会增加
- ❌ 但这不是"增加复杂度"，而是"显性化复杂度"

**解释**:
系统的本质复杂度是固定的（由业务需求决定）。把所有职责塞在一个类里，复杂度并没有降低，只是被隐藏了。职责分离后：
- 每个类都很小、很简单
- 复杂度被分散到多个小类中
- 更容易理解和维护

**类比**: 
- 违反SRP = 把所有衣服塞进一个大箱子（找衣服很难）
- 遵循SRP = 把衣服分类放进多个小抽屉（虽然抽屉多了，但找衣服更容易）

**实践**: 现代IDE的导航功能很强，类多不是问题。测试和维护的成本大幅降低，收益远大于成本。
</details>

---

## 总分统计

- **选择题**（1-3题）：每题 10 分，共 30 分
- **代码分析题**（4-5题）：每题 20 分，共 40 分
- **场景题**（6题）：20 分
- **判断题**（7-8题）：每题 5 分，共 10 分

**总分**: 100 分  
**及格线**: 80 分

---

## 学习建议

- ✅ 如果得分 ≥ 80分：恭喜！可以继续学习下一个原则
- ⚠️ 如果得分 60-79分：重新阅读 doc_01.md，重点理解"变化原因"
- ❌ 如果得分 < 60分：建议再运行一次 demo 代码，对比两种实现的差异

记住：**一个类只做一件事，只有一个修改的理由！**
