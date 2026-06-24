# 工厂模式 - 代码示例

## 示例说明

本目录包含三种工厂模式的完整示例：

1. **SimpleFactoryDemo.java** - 简单工厂模式
2. **FactoryMethodDemo.java** - 工厂方法模式
3. **AbstractFactoryDemo.java** - 抽象工厂模式

---

## 运行方式

### 前提条件
- JDK 8 或更高版本

### 编译和运行

```bash
# 进入demo目录
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\02-factory-pattern\demo

# 编译所有文件
javac SimpleFactoryDemo.java
javac FactoryMethodDemo.java
javac AbstractFactoryDemo.java

# 运行示例
java SimpleFactoryDemo
java FactoryMethodDemo
java AbstractFactoryDemo
```

---

## 示例1：SimpleFactoryDemo.java

### 场景
图形绘制系统，支持圆形、矩形、三角形

### 演示内容
1. **不使用工厂的问题**：创建逻辑分散，代码重复
2. **使用简单工厂的改进**：创建逻辑集中，易于维护
3. **扩展性测试**：批量创建、异常处理

### 核心代码
```java
// 简单工厂
class ShapeFactory {
    public static Shape createShape(String type, double... params) {
        switch (type.toLowerCase()) {
            case "circle":
                return new Circle(params[0]);
            case "rectangle":
                return new Rectangle(params[0], params[1]);
            case "triangle":
                return new Triangle(params[0], params[1]);
            default:
                throw new IllegalArgumentException("不支持的图形类型");
        }
    }
}

// 使用
Shape shape = ShapeFactory.createShape("circle", 5.0);
shape.draw();
```

### 预期输出
```
=== 简单工厂模式示例 ===

【1. 不使用工厂的问题】
绘制圆形，半径=5.0
绘制矩形，宽=4.0, 高=6.0
  ❌ 创建逻辑重复，修改困难

【2. 使用简单工厂的改进】
绘制圆形，半径=5.0
  面积: 78.54
绘制矩形，宽=4.0, 高=6.0
  面积: 24.00
绘制三角形，底=3.0, 高=4.0
  面积: 6.00
  ✅ 客户端代码简洁，易于理解
```

---

## 示例2：FactoryMethodDemo.java

### 场景
电商支付系统，支持支付宝、微信、银行卡

### 演示内容
1. **不同支付方式**：每种支付方式有独立的工厂
2. **扩展性演示**：新增PayPal支付，无需修改现有代码
3. **对比**：工厂方法 vs 简单工厂

### 核心代码
```java
// 抽象工厂
abstract class PaymentFactory {
    // 工厂方法
    public abstract Payment createPayment();
    
    // 模板方法
    public boolean processPayment(double amount) {
        Payment payment = createPayment();
        return payment.pay(amount);
    }
}

// 具体工厂
class AlipayFactory extends PaymentFactory {
    public Payment createPayment() {
        return new AlipayPayment();
    }
}

// 使用
PaymentFactory factory = new AlipayFactory();
factory.processPayment(99.99);
```

### 预期输出
```
=== 工厂方法模式示例 ===

【1. 不同支付方式】

开始支付流程...
  → 选择支付方式: 支付宝
[支付宝] 支付 ¥99.99
  → 调用支付宝SDK
  → 验证用户身份
  → 扣款成功
✅ 支付成功！交易完成。
```

---

## 示例3：AbstractFactoryDemo.java

### 场景
数据访问层，支持MySQL、PostgreSQL、Oracle

### 演示内容
1. **使用MySQL数据库**：创建MySQL的Connection和Transaction
2. **切换到PostgreSQL**：只需更换工厂
3. **扩展Oracle**：新增产品族，无需修改现有代码
4. **产品族一致性保证**：同一工厂创建的对象一定配套

### 核心代码
```java
// 抽象工厂
interface DatabaseFactory {
    Connection createConnection();
    Transaction createTransaction();
}

// 具体工厂
class MySQLFactory implements DatabaseFactory {
    public Connection createConnection() {
        return new MySQLConnection();
    }
    public Transaction createTransaction() {
        return new MySQLTransaction();
    }
}

// 使用
DatabaseFactory factory = new MySQLFactory();
Connection conn = factory.createConnection();      // MySQL连接
Transaction tx = factory.createTransaction();      // MySQL事务（自动匹配）
```

### 预期输出
```
=== 抽象工厂模式示例 ===

【1. 使用MySQL数据库】
  [MySQL Connection] 连接到MySQL数据库
    → jdbc:mysql://localhost:3306/mydb
  [MySQL Transaction] 开始事务: START TRANSACTION
  [MySQL Connection] 执行SQL: INSERT INTO users VALUES (1, 'Alice')
  [MySQL Connection] 执行SQL: UPDATE users SET age = 25 WHERE id = 1
  [MySQL Transaction] 提交事务: COMMIT
  ✅ 事务执行成功
```

---

## 三种工厂模式对比

| 特性 | 简单工厂 | 工厂方法 | 抽象工厂 |
|-----|---------|---------|---------|
| **示例场景** | 图形绘制 | 支付系统 | 数据库访问 |
| **工厂数量** | 1个 | 多个（每种产品1个） | 多个（每个产品族1个） |
| **创建对象** | 多种产品 | 单一产品 | 一套产品 |
| **扩展方式** | 修改工厂类 | 新增工厂类 | 新增工厂类 |
| **开闭原则** | ❌ | ✅ | ✅（产品族）<br/>❌（产品类型） |
| **复杂度** | 低 | 中 | 高 |
| **推荐场景** | 产品少，不常变化 | 产品多，频繁扩展 | 需要创建产品族 |

---

## 学习建议

### 学习顺序
1. **先运行SimpleFactoryDemo** - 理解工厂模式的基本思想
2. **再运行FactoryMethodDemo** - 理解如何符合开闭原则
3. **最后运行AbstractFactoryDemo** - 理解产品族的概念

### 重点理解
1. **简单工厂**：
   - 为什么创建逻辑要集中？
   - 什么时候够用？

2. **工厂方法**：
   - 为什么需要抽象工厂？
   - 如何扩展新产品？
   - 模板方法的作用是什么？

3. **抽象工厂**：
   - 什么是产品族？
   - 为什么要保证产品族一致性？
   - 何时使用抽象工厂？

### 动手练习
1. **修改SimpleFactoryDemo**：
   - 新增一个"五角星"图形
   - 体会简单工厂需要修改工厂类

2. **修改FactoryMethodDemo**：
   - 新增"Apple Pay"支付方式
   - 体会工厂方法的扩展性

3. **修改AbstractFactoryDemo**：
   - 新增"Statement"产品（Connection、Transaction、Statement三个产品）
   - 体会抽象工厂扩展产品类型的困难

---

## 思考题

1. **场景判断**：
   - 日志系统（DEBUG、INFO、WARN、ERROR）适合哪种工厂？
   - 跨平台UI（Button、TextField、Menu）适合哪种工厂？
   - 消息队列（Producer + Consumer）适合哪种工厂？

2. **设计选择**：
   - 如果产品种类会频繁增加，选哪种工厂？
   - 如果需要保证对象配套使用，选哪种工厂？
   - 如果只是简单的对象创建，选哪种工厂？

3. **代码改进**：
   - 如何用配置文件避免硬编码工厂选择？
   - 如何结合依赖注入使用工厂模式？
   - 如何用反射简化简单工厂的实现？

---

## 常见问题

### Q1: 为什么不直接用 `new` 创建对象？
A: 当创建逻辑复杂、需要根据条件选择类型、或需要频繁修改时，工厂模式更合适。

### Q2: 三种工厂如何选择？
A: 
- 产品少、不常变化 → 简单工厂
- 产品多、需要扩展 → 工厂方法
- 需要创建产品族 → 抽象工厂

### Q3: 工厂模式和单例模式有什么关系？
A: 工厂类本身常常设计为单例，但这是两个独立的模式。

### Q4: 抽象工厂什么时候不合适？
A: 产品类型经常变化时，因为每次新增产品类型都要修改所有工厂。

---

## 扩展阅读

完成这三个示例后，建议：
1. 阅读 `doc_01.md` 和 `doc_02.md` 了解理论细节
2. 完成 `test_01.md` 的自测题
3. 填写 `note_template.md` 巩固知识
4. 在实际项目中识别工厂模式的应用场景

---

**记住**：
> 简单工厂一生多，  
> 工厂方法一生一，  
> 抽象工厂一生族，  
> 产品配套不分离。
