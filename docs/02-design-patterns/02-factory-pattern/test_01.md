# 工厂模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 10 分，共 30 分）

### 1. 以下关于三种工厂模式的描述，哪个是正确的？

A. 简单工厂是GoF 23种设计模式之一  
B. 工厂方法模式中，一个工厂只创建一种产品  
C. 抽象工厂模式适合产品类型经常变化的场景  
D. 三种工厂模式都违反开闭原则

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:
- A错误：简单工厂不是GoF 23种设计模式之一，它是工厂方法的简化版
- **B正确**：工厂方法模式的核心特征是"一个工厂创建一种产品"
- C错误：抽象工厂不适合产品类型频繁变化，因为新增产品类型需要修改所有工厂
- D错误：工厂方法和抽象工厂（在产品族扩展上）符合开闭原则

**记忆口诀**：
- 简单工厂：一对多
- 工厂方法：一对一
- 抽象工厂：一对族
</details>

---

### 2. 以下哪个场景最适合使用抽象工厂模式？

A. 日志级别（DEBUG、INFO、WARN、ERROR）  
B. 图形绘制（圆形、矩形、三角形）  
C. 跨平台UI（Button + TextField + Menu）  
D. 支付方式（支付宝、微信、银行卡）

<details>
<summary>查看答案</summary>

**答案**: C

**解析**:

**C正确** - 跨平台UI需要创建**一套配套的组件**：
- Windows主题：Windows按钮 + Windows文本框 + Windows菜单
- Mac主题：Mac按钮 + Mac文本框 + Mac菜单
- 同一主题的组件必须配套，符合抽象工厂的产品族概念

**其他选项分析**：
- A和B：产品之间没有关联，适合简单工厂或工厂方法
- D：每种支付方式独立，适合工厂方法

**判断标准**：
- 需要创建**多个相关对象** → 抽象工厂
- 对象之间有**兼容性约束** → 抽象工厂
- 对象需要**配套使用** → 抽象工厂
</details>

---

### 3. 关于工厂方法模式，以下说法错误的是？

A. 符合开闭原则，新增产品无需修改现有代码  
B. 每个具体工厂只负责创建一种产品  
C. 可以在抽象工厂中定义模板方法  
D. 客户端不需要知道具体使用哪个工厂类

<details>
<summary>查看答案</summary>

**答案**: D

**解析**:

**D错误** - 工厂方法模式中，客户端**必须知道**使用哪个具体工厂：
```java
// 客户端需要选择具体工厂
PaymentFactory factory = new AlipayFactory();  // 明确选择
Payment payment = factory.createPayment();
```

这是工厂方法的一个局限：客户端需要决定使用哪个工厂。

**其他选项正确**：
- A正确：新增产品只需新增工厂类
- B正确：一个工厂一种产品
- C正确：可以在抽象工厂中定义统一流程（模板方法）

**改进方案**：
- 结合配置文件
- 使用依赖注入（Spring）
- 用简单工厂创建工厂（工厂的工厂）
</details>

---

## 二、代码分析（每题 15 分，共 30 分）

### 4. 判断以下代码使用了哪种工厂模式，并指出问题

```java
class LoggerFactory {
    public static Logger getLogger(String level) {
        switch (level) {
            case "debug":
                return new DebugLogger();
            case "info":
                return new InfoLogger();
            case "error":
                return new ErrorLogger();
            default:
                return new InfoLogger();
        }
    }
}

// 使用
Logger logger = LoggerFactory.getLogger("debug");
logger.log("测试消息");
```

<details>
<summary>查看答案</summary>

**模式**: 简单工厂模式

**特征识别**：
- ✅ 一个工厂类
- ✅ 静态方法创建对象
- ✅ 用 `switch` 判断类型
- ✅ 返回不同的产品

**存在的问题**：

1. **违反开闭原则**
   - 新增日志级别（如WARN）需要修改工厂类
   - 修改了 `switch` 语句

2. **工厂类职责过重**
   - 所有日志创建逻辑集中在一个方法
   - 随着日志类型增加，方法会变得臃肿

3. **硬编码类型字符串**
   - `"debug"`, `"info"` 等字符串容易写错
   - 建议用枚举代替

**改进方案1 - 使用枚举**：
```java
enum LogLevel {
    DEBUG, INFO, ERROR
}

class LoggerFactory {
    public static Logger getLogger(LogLevel level) {
        switch (level) {
            case DEBUG: return new DebugLogger();
            case INFO: return new InfoLogger();
            case ERROR: return new ErrorLogger();
        }
    }
}
```

**改进方案2 - 改为工厂方法**：
```java
abstract class LoggerFactory {
    public abstract Logger createLogger();
}

class DebugLoggerFactory extends LoggerFactory {
    public Logger createLogger() { return new DebugLogger(); }
}

class InfoLoggerFactory extends LoggerFactory {
    public Logger createLogger() { return new InfoLogger(); }
}
```

**何时用简单工厂合适**：
- 日志级别固定（不会新增）
- 代码简单，易于理解
- 不需要考虑长期扩展
</details>

---

### 5. 重构以下代码：从简单工厂改为工厂方法模式

```java
// 原代码：简单工厂
class ShapeFactory {
    public static Shape createShape(String type) {
        if ("circle".equals(type)) {
            return new Circle();
        } else if ("rectangle".equals(type)) {
            return new Rectangle();
        }
        return null;
    }
}

// 使用
Shape shape = ShapeFactory.createShape("circle");
shape.draw();
```

要求：
- 改为工厂方法模式
- 符合开闭原则
- 支持扩展新图形

<details>
<summary>参考答案</summary>

**重构后代码**：

```java
// 1. 产品接口
interface Shape {
    void draw();
}

// 2. 具体产品
class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("绘制圆形");
    }
}

class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("绘制矩形");
    }
}

// 3. 抽象工厂
abstract class ShapeFactory {
    // 工厂方法：由子类决定创建哪种图形
    public abstract Shape createShape();
    
    // 模板方法：统一的绘制流程（可选）
    public void drawShape() {
        Shape shape = createShape();
        System.out.println("准备绘制...");
        shape.draw();
        System.out.println("绘制完成");
    }
}

// 4. 具体工厂
class CircleFactory extends ShapeFactory {
    @Override
    public Shape createShape() {
        return new Circle();
    }
}

class RectangleFactory extends ShapeFactory {
    @Override
    public Shape createShape() {
        return new Rectangle();
    }
}

// 5. 客户端使用
public class Client {
    public static void main(String[] args) {
        // 使用圆形工厂
        ShapeFactory factory1 = new CircleFactory();
        factory1.drawShape();
        
        // 使用矩形工厂
        ShapeFactory factory2 = new RectangleFactory();
        factory2.drawShape();
    }
}
```

**新增图形的扩展（无需修改现有代码）**：

```java
// 6. 新增三角形（扩展）
class Triangle implements Shape {
    @Override
    public void draw() {
        System.out.println("绘制三角形");
    }
}

class TriangleFactory extends ShapeFactory {
    @Override
    public Shape createShape() {
        return new Triangle();
    }
}

// 使用新图形
ShapeFactory factory3 = new TriangleFactory();
factory3.drawShape();
```

**改进要点**：

1. **创建抽象工厂**：定义 `createShape()` 工厂方法
2. **每个图形一个工厂**：CircleFactory、RectangleFactory
3. **符合开闭原则**：新增图形只需新增工厂类
4. **可选模板方法**：在抽象工厂中定义统一流程

**对比**：

| 维度 | 简单工厂 | 工厂方法 |
|-----|---------|---------|
| 新增图形 | 修改ShapeFactory | 新增XXXFactory |
| 开闭原则 | ❌ 违反 | ✅ 符合 |
| 类的数量 | 少 | 多 |
| 扩展性 | 差 | 好 |
</details>

---

## 三、实践应用（每题 20 分，共 40 分）

### 6. 手写抽象工厂：数据库访问层

**场景描述**：
设计一个数据访问层，需要支持MySQL和PostgreSQL两种数据库。每种数据库需要提供：
- Connection（连接）
- Statement（SQL执行器）

要求：
- 使用抽象工厂模式
- 保证同一数据库的Connection和Statement配套使用
- 支持扩展新数据库

<details>
<summary>参考答案</summary>

```java
// ========== 抽象产品 ==========

// 产品A：连接
interface Connection {
    void connect();
    void close();
}

// 产品B：SQL执行器
interface Statement {
    void execute(String sql);
}

// ========== 具体产品：MySQL ==========

class MySQLConnection implements Connection {
    @Override
    public void connect() {
        System.out.println("[MySQL] 建立连接");
    }
    
    @Override
    public void close() {
        System.out.println("[MySQL] 关闭连接");
    }
}

class MySQLStatement implements Statement {
    @Override
    public void execute(String sql) {
        System.out.println("[MySQL] 执行SQL: " + sql);
    }
}

// ========== 具体产品：PostgreSQL ==========

class PostgreSQLConnection implements Connection {
    @Override
    public void connect() {
        System.out.println("[PostgreSQL] 建立连接");
    }
    
    @Override
    public void close() {
        System.out.println("[PostgreSQL] 关闭连接");
    }
}

class PostgreSQLStatement implements Statement {
    @Override
    public void execute(String sql) {
        System.out.println("[PostgreSQL] 执行SQL: " + sql);
    }
}

// ========== 抽象工厂 ==========

interface DatabaseFactory {
    Connection createConnection();
    Statement createStatement();
}

// ========== 具体工厂 ==========

class MySQLFactory implements DatabaseFactory {
    @Override
    public Connection createConnection() {
        return new MySQLConnection();
    }
    
    @Override
    public Statement createStatement() {
        return new MySQLStatement();
    }
}

class PostgreSQLFactory implements DatabaseFactory {
    @Override
    public Connection createConnection() {
        return new PostgreSQLConnection();
    }
    
    @Override
    public Statement createStatement() {
        return new PostgreSQLStatement();
    }
}

// ========== 客户端 ==========

class DataAccessLayer {
    private DatabaseFactory factory;
    
    public DataAccessLayer(DatabaseFactory factory) {
        this.factory = factory;
    }
    
    public void executeQuery(String sql) {
        // 创建配套的组件
        Connection conn = factory.createConnection();
        Statement stmt = factory.createStatement();
        
        conn.connect();
        stmt.execute(sql);
        conn.close();
    }
}

// 使用
public class Client {
    public static void main(String[] args) {
        // 使用MySQL
        DatabaseFactory mysqlFactory = new MySQLFactory();
        DataAccessLayer dal1 = new DataAccessLayer(mysqlFactory);
        dal1.executeQuery("SELECT * FROM users");
        
        // 切换到PostgreSQL（只需更换工厂）
        DatabaseFactory pgFactory = new PostgreSQLFactory();
        DataAccessLayer dal2 = new DataAccessLayer(pgFactory);
        dal2.executeQuery("SELECT * FROM users");
    }
}
```

**关键点**：
1. **产品族一致性**：MySQLFactory只创建MySQL的组件
2. **易于切换**：更换数据库只需更换工厂
3. **易于扩展**：新增Oracle只需新增OracleFactory和Oracle产品

**产出结构**：
```
产品族1（MySQL）: MySQLConnection + MySQLStatement
产品族2（PostgreSQL）: PostgreSQLConnection + PostgreSQLStatement
```
</details>

---

### 7. 场景判断题

以下是5个真实开发场景，判断应该使用哪种工厂模式（简单工厂/工厂方法/抽象工厂），并说明理由。

**场景A**: HTTP请求方法（GET、POST、PUT、DELETE）  
**场景B**: 消息队列（Producer + Consumer配套使用）  
**场景C**: 游戏角色职业（战士、法师、刺客），每种职业有独特的技能树  
**场景D**: 配置文件解析（properties、yaml、json、xml）  
**场景E**: 跨云平台部署（AWS、阿里云、腾讯云），每个平台需要（存储+计算+网络）

<details>
<summary>参考答案</summary>

### 场景A: HTTP请求方法
**推荐**: 简单工厂

**理由**：
- HTTP方法固定（GET、POST、PUT、DELETE、PATCH、HEAD、OPTIONS）
- 不会频繁新增
- 创建逻辑简单
- 简单工厂足够

```java
class HttpMethodFactory {
    public static HttpMethod create(String method) {
        switch (method) {
            case "GET": return new GetMethod();
            case "POST": return new PostMethod();
            // ...
        }
    }
}
```

---

### 场景B: 消息队列（Producer + Consumer）
**推荐**: 抽象工厂

**理由**：
- 需要创建**配套的对象**（Producer和Consumer必须匹配）
- Kafka的Producer + Kafka的Consumer
- RabbitMQ的Producer + RabbitMQ的Consumer
- 保证产品族一致性

```java
interface MQFactory {
    Producer createProducer();
    Consumer createConsumer();
}

class KafkaFactory implements MQFactory { /*...*/ }
class RabbitMQFactory implements MQFactory { /*...*/ }
```

---

### 场景C: 游戏角色职业
**推荐**: 工厂方法

**理由**：
- 职业种类会扩展（新增职业）
- 每个职业有独特的创建逻辑（技能树、属性初始化）
- 符合开闭原则

```java
abstract class CharacterFactory {
    public abstract Character createCharacter();
}

class WarriorFactory extends CharacterFactory { /*...*/ }
class MageFactory extends CharacterFactory { /*...*/ }
```

---

### 场景D: 配置文件解析
**推荐**: 简单工厂 或 工厂方法

**理由**：
- **如果格式固定** → 简单工厂
- **如果会扩展新格式** → 工厂方法

**简单工厂版**：
```java
class ConfigParserFactory {
    public static ConfigParser create(String extension) {
        switch (extension) {
            case "properties": return new PropertiesParser();
            case "yaml": return new YamlParser();
            case "json": return new JsonParser();
        }
    }
}
```

**工厂方法版**（更灵活）：
```java
abstract class ConfigParserFactory {
    public abstract ConfigParser createParser();
}
```

---

### 场景E: 跨云平台部署
**推荐**: 抽象工厂

**理由**：
- 需要创建**一套配套的云服务**（存储、计算、网络）
- AWS的S3 + EC2 + VPC 必须配套
- 阿里云的OSS + ECS + VPC 必须配套
- 典型的产品族场景

```java
interface CloudFactory {
    Storage createStorage();
    Compute createCompute();
    Network createNetwork();
}

class AWSFactory implements CloudFactory {
    public Storage createStorage() { return new S3Storage(); }
    public Compute createCompute() { return new EC2Compute(); }
    public Network createNetwork() { return new VPCNetwork(); }
}

class AliyunFactory implements CloudFactory {
    public Storage createStorage() { return new OSSStorage(); }
    public Compute createCompute() { return new ECSCompute(); }
    public Network createNetwork() { return new VPCNetwork(); }
}
```

---

### 总结

| 场景 | 模式 | 关键判断依据 |
|-----|------|------------|
| A - HTTP方法 | 简单工厂 | 固定，不扩展 |
| B - 消息队列 | 抽象工厂 | 需要配套对象 |
| C - 游戏职业 | 工厂方法 | 会扩展新职业 |
| D - 配置解析 | 简单工厂/工厂方法 | 看是否扩展 |
| E - 云平台 | 抽象工厂 | 需要一套云服务 |

**判断口诀**：
- 固定不变 → 简单工厂
- 频繁扩展 → 工厂方法
- 配套使用 → 抽象工厂
</details>

---

## 四、评分标准

### 满分答案特征
- ✅ 准确识别工厂模式类型
- ✅ 理解三种模式的适用场景
- ✅ 能写出符合设计原则的代码
- ✅ 理解产品族和产品等级的概念
- ✅ 能分析真实场景并选择合适的模式

### 常见扣分点
- ❌ 混淆三种工厂模式的概念
- ❌ 不理解开闭原则
- ❌ 不理解产品族的概念
- ❌ 代码不符合工厂模式的结构
- ❌ 场景判断不准确

---

## 五、学习建议

### 如果得分 < 60 分
- 重新阅读 doc_01.md 和 doc_02.md
- 运行并理解 demo/ 中的代码
- 重点理解：简单工厂、工厂方法、抽象工厂的区别

### 如果得分 60-80 分
- 理解基本概念，但需要加强实践
- 手写代码巩固三种模式
- 重点理解：何时使用哪种模式

### 如果得分 > 80 分
- 恭喜！已经掌握工厂模式
- 尝试在实际项目中应用
- 继续学习下一个设计模式

---

## 六、核心要点回顾

### 三种模式对比

| 模式 | 结构 | 创建 | 扩展 | 适用场景 |
|-----|------|------|------|---------|
| 简单工厂 | 一个工厂类 | 创建多种产品 | 修改工厂 | 产品少、不常变化 |
| 工厂方法 | 多个工厂类 | 一个工厂一种产品 | 新增工厂 | 产品多、需要扩展 |
| 抽象工厂 | 多个工厂类 | 一个工厂一套产品 | 新增工厂 | 需要创建产品族 |

### 记忆口诀
> 简单工厂一生多，  
> 工厂方法一生一，  
> 抽象工厂一生族，  
> 产品配套不分离。

### 关键概念
1. **产品族**：同一工厂创建的一组相关产品
2. **产品等级**：不同工厂创建的同类产品
3. **开闭原则**：对扩展开放，对修改封闭
4. **依赖倒置**：依赖抽象，不依赖具体类

---

**完成自测后**，填写 `note_template.md` 巩固知识！