# 策略模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 30 分）

### 1. 策略模式的主要目的是什么？

A. 将对象组合成树形结构以表示"部分-整体"的层次结构  
B. 定义一系列算法，把它们封装起来，并使它们可以相互替换  
C. 运用共享技术有效地支持大量细粒度对象的复用  
D. 将一个类的接口转换成客户希望的另一个接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 策略模式的核心目的是**定义一系列算法，把它们封装起来，并使它们可以相互替换**。策略模式让算法独立于使用它的客户端而变化。

**关键词**：算法族、封装、可替换、运行时切换

**形象类比**：
- 支付方式：支付宝、微信、信用卡（算法不同，接口统一）
- 出行方式：开车、地铁、骑车（目标相同，策略不同）

**核心价值**：
- 消除if-else/switch-case
- 符合开闭原则
- 运行时切换算法

**其他选项**：
- A：组合模式
- C：享元模式
- D：适配器模式
</details>

---

### 2. 策略模式的三个角色是什么？

A. Subject, Observer, ConcreteObserver  
B. Strategy, ConcreteStrategy, Context  
C. Component, Leaf, Composite  
D. Command, Receiver, Invoker

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 策略模式的三个角色：

1. **Strategy（抽象策略）**：
   - 定义所有支持的算法的公共接口
   - 声明算法方法

2. **ConcreteStrategy（具体策略）**：
   - 实现Strategy接口
   - 封装具体的算法或行为

3. **Context（上下文）**：
   - 维护一个Strategy引用
   - 负责在运行时切换策略
   - 将客户端请求委托给策略

**示例**：
```java
// Strategy
interface PaymentStrategy {
    boolean pay(double amount);
}

// ConcreteStrategy
class AlipayPayment implements PaymentStrategy {
    public boolean pay(double amount) { /* ... */ }
}

class WeChatPayment implements PaymentStrategy {
    public boolean pay(double amount) { /* ... */ }
}

// Context
class PaymentContext {
    private PaymentStrategy strategy;
    
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void executePayment(double amount) {
        strategy.pay(amount);  // 委托给策略
    }
}
```

**其他选项**：
- A：观察者模式
- C：组合模式
- D：命令模式
</details>

---

## 二、模式对比（15 分）

### 3. 策略模式和简单工厂模式的主要区别是什么？

A. 策略模式关注算法行为，简单工厂关注对象创建  
B. 策略模式关注对象创建，简单工厂关注算法行为  
C. 两者完全相同  
D. 策略模式用于单例对象，简单工厂用于多例对象

<details>
<summary>查看答案</summary>

**答案**: A

**解析**:

| 对比 | 策略模式 | 简单工厂 |
|-----|---------|---------|
| **关注点** | 算法行为 | 对象创建 |
| **目的** | 算法可替换 | 封装创建逻辑 |
| **运行时** | 可切换策略 | 返回固定对象 |
| **职责** | 执行算法 | 创建实例 |
| **例子** | 不同支付方式 | 创建不同类型对象 |

---

### 策略模式（关注行为）

```java
// 客户端选择策略
PaymentStrategy strategy = new AlipayPayment();
context.setStrategy(strategy);
context.executePayment(100);  // 执行支付算法

// 运行时切换策略
context.setStrategy(new WeChatPayment());
context.executePayment(200);
```

**重点**：如何执行算法

---

### 简单工厂（关注创建）

```java
// 工厂创建对象
Shape shape = ShapeFactory.create("circle");
shape.draw();
```

**重点**：如何创建对象

---

### 可以结合使用

```java
// 1. 工厂创建策略
PaymentStrategy strategy = PaymentStrategyFactory.create("alipay");

// 2. 策略执行算法
context.setStrategy(strategy);
context.executePayment(100);
```

**优势**：
- 工厂封装策略创建
- 策略封装算法行为
- 职责分离

</details>

---

## 三、场景判断（20 分）

### 4. 以下哪些场景适合使用策略模式？

A. 电商折扣系统（会员折扣、满减、优惠券）  
B. 文件压缩（ZIP、RAR、7z）  
C. 系统配置管理器（单例）  
D. 路径规划（最短路径、最快路径、最省钱路径）  
E. 订单状态流转（待支付→已支付→已发货）

<details>
<summary>查看答案</summary>

**答案**: A, B, D

**解析**:

### ✅ A. 电商折扣系统 - 适合

**理由**：
- 多种折扣算法（会员折扣、满减、优惠券）
- 算法可以互换
- 需要运行时选择
- 避免大量if-else

```java
interface DiscountStrategy {
    double calculate(double amount);
}

class VIPDiscount implements DiscountStrategy {
    public double calculate(double amount) {
        return amount * 0.85;
    }
}

class FullReductionDiscount implements DiscountStrategy {
    public double calculate(double amount) {
        return amount >= 100 ? amount - 20 : amount;
    }
}
```

---

### ✅ B. 文件压缩 - 适合

**理由**：
- 多种压缩算法（ZIP、RAR、7z）
- 算法可以互换
- 根据文件类型或用户选择压缩方式

```java
interface CompressionStrategy {
    void compress(File file);
}

class ZipCompression implements CompressionStrategy { }
class RarCompression implements CompressionStrategy { }
```

---

### ❌ C. 系统配置管理器 - 不适合，应该用**单例模式**

**理由**：
- 不是多种算法场景
- 强调全局唯一
- 不需要切换策略

---

### ✅ D. 路径规划 - 适合

**理由**：
- 多种路径算法（最短、最快、最省钱）
- 用户可以选择
- 算法独立实现

```java
interface RouteStrategy {
    Route calculateRoute(Point start, Point end);
}

class ShortestPathStrategy implements RouteStrategy { }
class FastestPathStrategy implements RouteStrategy { }
class CheapestPathStrategy implements RouteStrategy { }
```

---

### ❌ E. 订单状态流转 - 不适合，应该用**状态模式**

**理由**：
- 不是主动选择算法
- 是状态自动流转
- 状态之间有依赖关系

**区别**：
```java
// 策略模式：客户端主动选择
context.setStrategy(new AlipayPayment());  // 我选择

// 状态模式：状态自动切换
order.process();  // 待支付 → 已支付（自动）
```

---

### 判断标准

**适合策略模式**：
1. ✅ 有多种算法可以互换
2. ✅ 需要在运行时选择算法
3. ✅ 消除大量if-else
4. ✅ 客户端主动选择

**不适合策略模式**：
1. ❌ 只有一种算法
2. ❌ 状态自动切换（用状态模式）
3. ❌ 强调全局唯一（用单例模式）

</details>

---

## 四、代码重构（30 分）

### 5. 用策略模式重构以下代码，消除if-else

```java
// 电商折扣计算（有大量if-else）
class DiscountCalculator {
    public double calculateDiscount(String userType, double amount) {
        if (userType.equals("普通会员")) {
            return amount * 0.95;
        } else if (userType.equals("VIP会员")) {
            return amount * 0.85;
        } else if (userType.equals("超级VIP")) {
            return amount * 0.75;
        } else if (userType.equals("新用户")) {
            return amount * 0.90;
        } else {
            return amount;
        }
    }
}
```

<details>
<summary>参考答案</summary>

**重构方案：使用策略模式**

```java
// 1. 定义策略接口
interface DiscountStrategy {
    double calculate(double amount);
    String getDiscountType();
}

// 2. 具体策略
class NormalMemberDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount * 0.95;  // 95折
    }
    
    @Override
    public String getDiscountType() {
        return "普通会员折扣";
    }
}

class VIPMemberDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount * 0.85;  // 85折
    }
    
    @Override
    public String getDiscountType() {
        return "VIP会员折扣";
    }
}

class SuperVIPDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount * 0.75;  // 75折
    }
    
    @Override
    public String getDiscountType() {
        return "超级VIP折扣";
    }
}

class NewUserDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount * 0.90;  // 9折
    }
    
    @Override
    public String getDiscountType() {
        return "新用户折扣";
    }
}

class NoDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount;  // 无折扣
    }
    
    @Override
    public String getDiscountType() {
        return "无折扣";
    }
}

// 3. 策略工厂（消除if-else）
class DiscountStrategyFactory {
    private static final Map<String, DiscountStrategy> strategies = new HashMap<>();
    
    static {
        strategies.put("普通会员", new NormalMemberDiscount());
        strategies.put("VIP会员", new VIPMemberDiscount());
        strategies.put("超级VIP", new SuperVIPDiscount());
        strategies.put("新用户", new NewUserDiscount());
    }
    
    public static DiscountStrategy getStrategy(String userType) {
        return strategies.getOrDefault(userType, new NoDiscount());
    }
}

// 4. 上下文
class ShoppingCart {
    private DiscountStrategy discountStrategy;
    private double totalAmount;
    
    public void setDiscountStrategy(DiscountStrategy strategy) {
        this.discountStrategy = strategy;
    }
    
    public double checkout() {
        if (discountStrategy == null) {
            discountStrategy = new NoDiscount();
        }
        
        double finalAmount = discountStrategy.calculate(totalAmount);
        System.out.println("原价: ¥" + totalAmount);
        System.out.println("折扣类型: " + discountStrategy.getDiscountType());
        System.out.println("实付: ¥" + finalAmount);
        
        return finalAmount;
    }
    
    public void addItem(double price) {
        totalAmount += price;
    }
}

// 5. 使用（无if-else）
public class DiscountDemo {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(100);
        cart.addItem(50);
        
        // 方式1：直接设置策略
        cart.setDiscountStrategy(new VIPMemberDiscount());
        cart.checkout();
        
        // 方式2：通过工厂获取策略（更简洁）
        String userType = "超级VIP";
        DiscountStrategy strategy = DiscountStrategyFactory.getStrategy(userType);
        cart.setDiscountStrategy(strategy);
        cart.checkout();
    }
}
```

---

**优势对比**：

| 对比 | 重构前 | 重构后 |
|-----|--------|--------|
| if-else数量 | 5个 | 0个 |
| 新增折扣类型 | 修改原方法 | 新增策略类 |
| 测试难度 | 需要测试所有分支 | 每个策略独立测试 |
| 代码可读性 | 差 | 好 |
| 开闭原则 | 违反 | 符合 |

---

**关键改进**：
1. ✅ **消除if-else**：使用多态代替条件判断
2. ✅ **策略独立**：每个折扣算法独立封装
3. ✅ **易于扩展**：新增折扣只需添加策略类
4. ✅ **易于测试**：每个策略可以独立测试
5. ✅ **策略工厂**：结合工厂模式简化策略创建

</details>

---

## 五、实现题（15 分）

### 6. 实现一个文件导出系统，支持导出为Excel、PDF、CSV格式

要求：
- 使用策略模式
- 支持运行时切换导出格式
- 提供统一的导出接口

<details>
<summary>参考答案</summary>

```java
import java.util.*;

// 1. 数据模型
class DataTable {
    private List<String> headers;
    private List<List<String>> rows;
    
    public DataTable(List<String> headers, List<List<String>> rows) {
        this.headers = headers;
        this.rows = rows;
    }
    
    public List<String> getHeaders() {
        return headers;
    }
    
    public List<List<String>> getRows() {
        return rows;
    }
}

// 2. 抽象策略
interface ExportStrategy {
    void export(DataTable data, String filename);
    String getFormatName();
}

// 3. 具体策略
class ExcelExportStrategy implements ExportStrategy {
    @Override
    public void export(DataTable data, String filename) {
        System.out.println("\n【导出为Excel】");
        System.out.println("  文件名: " + filename + ".xlsx");
        System.out.println("  表头: " + data.getHeaders());
        System.out.println("  数据行数: " + data.getRows().size());
        System.out.println("  ✅ Excel导出成功！");
    }
    
    @Override
    public String getFormatName() {
        return "Excel";
    }
}

class PDFExportStrategy implements ExportStrategy {
    @Override
    public void export(DataTable data, String filename) {
        System.out.println("\n【导出为PDF】");
        System.out.println("  文件名: " + filename + ".pdf");
        System.out.println("  格式化表格...");
        System.out.println("  渲染PDF页面...");
        System.out.println("  ✅ PDF导出成功！");
    }
    
    @Override
    public String getFormatName() {
        return "PDF";
    }
}

class CSVExportStrategy implements ExportStrategy {
    @Override
    public void export(DataTable data, String filename) {
        System.out.println("\n【导出为CSV】");
        System.out.println("  文件名: " + filename + ".csv");
        
        // 模拟CSV格式输出
        System.out.println("  CSV内容:");
        System.out.println("  " + String.join(",", data.getHeaders()));
        for (List<String> row : data.getRows()) {
            System.out.println("  " + String.join(",", row));
        }
        
        System.out.println("  ✅ CSV导出成功！");
    }
    
    @Override
    public String getFormatName() {
        return "CSV";
    }
}

// 4. 上下文
class DataExporter {
    private ExportStrategy strategy;
    
    public void setStrategy(ExportStrategy strategy) {
        this.strategy = strategy;
        System.out.println(">>> 切换导出格式为: " + strategy.getFormatName());
    }
    
    public void export(DataTable data, String filename) {
        if (strategy == null) {
            System.out.println("❌ 请先设置导出格式！");
            return;
        }
        
        strategy.export(data, filename);
    }
}

// 5. 测试
public class ExportStrategyDemo {
    public static void main(String[] args) {
        // 准备数据
        List<String> headers = Arrays.asList("姓名", "年龄", "部门");
        List<List<String>> rows = Arrays.asList(
            Arrays.asList("张三", "28", "技术部"),
            Arrays.asList("李四", "32", "市场部"),
            Arrays.asList("王五", "25", "人事部")
        );
        DataTable data = new DataTable(headers, rows);
        
        DataExporter exporter = new DataExporter();
        
        // 导出为Excel
        exporter.setStrategy(new ExcelExportStrategy());
        exporter.export(data, "员工信息");
        
        // 切换到PDF
        exporter.setStrategy(new PDFExportStrategy());
        exporter.export(data, "员工信息");
        
        // 切换到CSV
        exporter.setStrategy(new CSVExportStrategy());
        exporter.export(data, "员工信息");
    }
}
```

**关键点**：
- ✅ 统一的导出接口（ExportStrategy）
- ✅ 每种格式独立实现
- ✅ 运行时切换格式
- ✅ 易于扩展新格式（JSON、XML等）

</details>

---

## 六、真实应用（10 分）

### 7. Java的Comparator是如何体现策略模式的？

<details>
<summary>参考答案</summary>

### Comparator的策略模式实现

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

// 策略1：按字母顺序排序
Collections.sort(names, new Comparator<String>() {
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});

// 策略2：按长度排序
Collections.sort(names, new Comparator<String>() {
    public int compare(String a, String b) {
        return Integer.compare(a.length(), b.length());
    }
});

// 策略3：按逆序排序
Collections.sort(names, new Comparator<String>() {
    public int compare(String a, String b) {
        return b.compareTo(a);
    }
});
```

---

### Java 8 Lambda简化

```java
// 按字母顺序
Collections.sort(names, (a, b) -> a.compareTo(b));

// 按长度排序
Collections.sort(names, (a, b) -> a.length() - b.length());

// 按逆序
Collections.sort(names, (a, b) -> b.compareTo(a));
```

---

### 策略模式体现

| 策略模式角色 | Comparator实现 |
|------------|---------------|
| **Strategy** | Comparator接口 |
| **ConcreteStrategy** | 各种Comparator实现 |
| **Context** | Collections.sort() |

**工作原理**：
```java
// Collections.sort()源码（简化）
public static <T> void sort(List<T> list, Comparator<? super T> c) {
    // 使用传入的比较策略
    list.sort(c);  // 委托给策略
}
```

---

### 优势

1. **灵活的排序策略**：
   - 不需要修改排序算法
   - 只需提供不同的比较策略

2. **符合开闭原则**：
   - sort()方法无需修改
   - 新增排序规则只需实现Comparator

3. **运行时选择**：
   - 可以动态传入不同策略
   - 同一个列表可以用不同规则排序

---

### 实际应用示例

```java
class Student {
    String name;
    int age;
    double score;
    
    // 构造函数和getter...
}

List<Student> students = Arrays.asList(/* ... */);

// 按年龄排序
Collections.sort(students, (a, b) -> a.age - b.age);

// 按成绩排序
Collections.sort(students, (a, b) -> Double.compare(a.score, b.score));

// 按姓名排序
Collections.sort(students, (a, b) -> a.name.compareTo(b.name));
```

**结论**：Comparator是策略模式在Java标准库中的经典应用。

</details>

---

## 核心要点回顾

### 策略模式三要素
1. **Strategy**：定义算法接口
2. **ConcreteStrategy**：实现具体算法
3. **Context**：持有策略引用，委托执行

### 记忆口诀
> **算法多个要切换，**  
> **策略模式来帮忙，**  
> **封装算法成对象，**  
> **运行时刻任你选。**

### 使用场景
- ✅ 有多个算法可以互换
- ✅ 需要消除大量if-else
- ✅ 算法需要在运行时选择

---

**完成自测后**，填写 `note_template.md` 巩固知识！
