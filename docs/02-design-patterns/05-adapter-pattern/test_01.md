# 适配器模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 45 分）

### 1. 适配器模式的主要目的是什么？

A. 增强对象的功能  
B. 将一个类的接口转换成客户希望的另一个接口  
C. 控制对对象的访问  
D. 简化复杂系统的接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 适配器模式的核心目的是**接口转换**，让原本接口不兼容的类可以合作。

**形象类比**：
- 插座转换器：让国标插头能插入美国插座
- 读卡器：让SD卡能连接USB接口

**与其他模式的区别**：
- **适配器模式**：改变接口（转换）
- **装饰器模式**：增强功能（保持接口）
- **代理模式**：控制访问（保持接口）
- **外观模式**：简化接口（多对一）
</details>

---

### 2. 对象适配器和类适配器的主要区别是什么？

A. 对象适配器使用继承，类适配器使用组合  
B. 对象适配器使用组合，类适配器使用继承  
C. 两者没有区别，只是实现方式不同  
D. 对象适配器更快，类适配器更灵活

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**对象适配器（组合）✅**：
```java
class Adapter implements Target {
    private Adaptee adaptee;  // 组合：持有被适配者
    
    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public void request() {
        adaptee.specificRequest();
    }
}
```

**类适配器（继承）⚠️**：
```java
class Adapter extends Adaptee implements Target {
    @Override
    public void request() {
        specificRequest();  // 直接调用父类方法
    }
}
```

**对比**：

| 对比 | 对象适配器 | 类适配器 |
|-----|-----------|---------|
| 实现 | 组合 | 继承 |
| 灵活性 | ✅ 高 | ❌ 低 |
| 适配多个类 | ✅ 可以 | ❌ 不行 |
| 推荐度 | ⭐⭐⭐⭐⭐ | ⭐⭐ |

**推荐**：使用对象适配器（组合）
</details>

---

### 3. 以下哪个场景最适合使用适配器模式？

A. 你需要给对象增加日志功能  
B. 你需要使用一个第三方库，但它的接口与你的系统不兼容  
C. 你需要简化一个复杂子系统的接口  
D. 你需要控制对某个对象的访问

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**B正确** - 第三方库接口不兼容：
```java
// 你的系统接口
interface PaymentProcessor {
    boolean processPayment(String orderId, double amount);
}

// 第三方支付SDK
class AlipaySDK {
    public String pay(String orderId, int cents) { ... }
}

// ❌ 接口不兼容！

// ✅ 适配器解决
class AlipayAdapter implements PaymentProcessor {
    private AlipaySDK alipay;
    
    @Override
    public boolean processPayment(String orderId, double amount) {
        int cents = (int) (amount * 100);
        String result = alipay.pay(orderId, cents);
        return "SUCCESS".equals(result);
    }
}
```

**其他选项**：
- A：增加功能 → **装饰器模式**
- C：简化接口 → **外观模式**
- D：控制访问 → **代理模式**
</details>

---

## 二、代码分析（每题 20 分，共 40 分）

### 4. 以下代码有什么问题？如何改进？

```java
// 老系统的日志接口
class LegacyLogger {
    public void writeLog(String message) {
        System.out.println("[LOG] " + message);
    }
}

// 新系统的日志接口
interface Logger {
    void log(String level, String message);
}

// 适配器
class LoggerAdapter extends LegacyLogger implements Logger {
    @Override
    public void log(String level, String message) {
        writeLog("[" + level + "] " + message);
    }
}
```

<details>
<summary>参考答案</summary>

**问题**: 使用了类适配器（继承），不够灵活

**问题分析**：
1. ❌ 使用继承（类适配器）
2. ❌ 只能适配LegacyLogger一个类
3. ❌ 暴露了LegacyLogger的writeLog()方法

**改进方案**：使用对象适配器（组合）

```java
// 对象适配器（推荐）
class LoggerAdapter implements Logger {
    private LegacyLogger legacyLogger;  // 组合
    
    public LoggerAdapter(LegacyLogger legacyLogger) {
        this.legacyLogger = legacyLogger;
    }
    
    @Override
    public void log(String level, String message) {
        legacyLogger.writeLog("[" + level + "] " + message);
    }
}

// 使用
Logger logger = new LoggerAdapter(new LegacyLogger());
logger.log("INFO", "System started");
```

**优势**：
1. ✅ 更灵活（可以在运行时切换logger）
2. ✅ 可以适配多个不同的logger
3. ✅ 不暴露LegacyLogger的其他方法
4. ✅ 符合组合复用原则

**对比**：
```
类适配器：
LoggerAdapter
  ├─ 继承 LegacyLogger
  └─ 实现 Logger
  问题：单继承限制，暴露父类方法

对象适配器：
LoggerAdapter
  ├─ 持有 LegacyLogger（组合）
  └─ 实现 Logger
  优势：灵活、可适配多个类
```
</details>

---

### 5. 实现数据库连接适配器

要求：
- 老系统使用MySQLConnection接口：`connect(String host, int port, String db)`
- 新系统使用DBConnection接口：`connect(String url)`
- 实现适配器，转换接口

<details>
<summary>参考答案</summary>

```java
// 老系统：MySQL连接接口
class MySQLConnection {
    public void connect(String host, int port, String db) {
        System.out.println("连接MySQL: " + host + ":" + port + "/" + db);
    }
    
    public void query(String sql) {
        System.out.println("执行SQL: " + sql);
    }
}

// 新系统：统一的数据库连接接口
interface DBConnection {
    /**
     * 连接数据库
     * @param url 连接URL，格式：mysql://host:port/database
     */
    void connect(String url);
    
    void executeQuery(String sql);
}

// 适配器：将MySQLConnection适配为DBConnection
class MySQLAdapter implements DBConnection {
    private MySQLConnection mysqlConnection;
    
    public MySQLAdapter(MySQLConnection mysqlConnection) {
        this.mysqlConnection = mysqlConnection;
    }
    
    @Override
    public void connect(String url) {
        // 解析URL：mysql://localhost:3306/testdb
        String[] parts = url.replace("mysql://", "").split("[:/]");
        
        if (parts.length >= 3) {
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            String db = parts[2];
            
            // 转换调用
            mysqlConnection.connect(host, port, db);
        } else {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }
    
    @Override
    public void executeQuery(String sql) {
        mysqlConnection.query(sql);
    }
}

// 使用
DBConnection db = new MySQLAdapter(new MySQLConnection());
db.connect("mysql://localhost:3306/testdb");
db.executeQuery("SELECT * FROM users");
```

**适配器转换的内容**：
1. **方法名**：`connect()` → `connect()`（名称相同但参数不同）
2. **参数**：`(String, int, String)` → `(String)`
3. **URL解析**：`"mysql://host:port/db"` → `host, port, db`

**关键点**：
- ✅ 使用对象适配器（组合）
- ✅ 解析URL字符串
- ✅ 转换参数格式
- ✅ 统一接口

**扩展**：可以适配其他数据库
```java
// PostgreSQL适配器
class PostgreSQLAdapter implements DBConnection {
    private PostgreSQLConnection pgConnection;
    
    @Override
    public void connect(String url) {
        // 解析URL：postgresql://...
        // 转换调用
    }
}
```
</details>

---

## 三、场景判断（15 分）

### 6. 判断以下场景是否适合适配器模式

**场景A**: 你需要使用SLF4J统一日志接口，但底层使用Log4j实现  
**场景B**: 你需要给一个Stream添加缓冲功能  
**场景C**: 你需要将旧系统的`UserService`接口适配为新系统的`UserAPI`接口  
**场景D**: 你需要统一管理家电（电视、空调、灯）的控制

<details>
<summary>参考答案</summary>

### 场景A: SLF4J + Log4j
**推荐**: ✅ 适合适配器模式

**理由**：
- SLF4J提供统一接口
- Log4j有自己的接口
- 需要接口转换

```java
// 目标接口：SLF4J
interface Logger {
    void info(String msg);
}

// 被适配者：Log4j
class Log4jImpl {
    public void log(String level, String msg) { ... }
}

// 适配器
class Log4jAdapter implements Logger {
    private Log4jImpl log4j;
    
    @Override
    public void info(String msg) {
        log4j.log("INFO", msg);
    }
}
```

**真实应用**：SLF4J就是这么做的！

---

### 场景B: Stream添加缓冲
**推荐**: ❌ 不适合适配器模式，应该用**装饰器模式**

**理由**：
- 不是接口转换问题
- 是功能增强问题
- 接口保持不变

```java
// 装饰器模式
InputStream in = new FileInputStream("file.txt");
InputStream buffered = new BufferedInputStream(in);  // 装饰器
```

---

### 场景C: 旧系统接口 → 新系统接口
**推荐**: ✅ 适合适配器模式

**理由**：
- 系统集成典型场景
- 接口不兼容
- 不能修改旧系统代码

```java
// 旧系统
class UserService {
    public User getUser(int id) { ... }
}

// 新系统
interface UserAPI {
    UserDTO getUserById(String userId);
}

// 适配器
class UserServiceAdapter implements UserAPI {
    private UserService userService;
    
    @Override
    public UserDTO getUserById(String userId) {
        int id = Integer.parseInt(userId);
        User user = userService.getUser(id);
        return convertToDTO(user);
    }
}
```

---

### 场景D: 统一管理家电
**推荐**: ❌ 不适合适配器模式，应该用**外观模式**

**理由**：
- 不是接口转换
- 是简化多个接口
- 提供统一的高层接口

```java
// 外观模式
class HomeTheaterFacade {
    private TV tv;
    private AirConditioner ac;
    private Light light;
    
    public void watchMovie() {
        tv.on();
        light.dim();
        // ...
    }
}
```

---

### 总结

**适合适配器模式**：
- ✅ 场景A：日志框架适配
- ✅ 场景C：系统集成

**不适合适配器模式**：
- ❌ 场景B：功能增强（用装饰器）
- ❌ 场景D：简化接口（用外观）

**判断标准**：
```
是接口转换问题？
  ├─ 是 → ✅ 适配器模式
  └─ 否
      ├─ 功能增强？ → 装饰器模式
      ├─ 简化接口？ → 外观模式
      └─ 控制访问？ → 代理模式
```
</details>

---

## 四、评分标准

### 满分答案特征
- ✅ 理解适配器模式的本质（接口转换）
- ✅ 掌握对象适配器 vs 类适配器
- ✅ 能手写适配器代码
- ✅ 能准确判断使用场景
- ✅ 理解适配器 vs 其他模式

### 常见扣分点
- ❌ 混淆适配器和装饰器
- ❌ 不知道对象适配器优于类适配器
- ❌ 无法识别接口转换场景
- ❌ 适配器实现过于复杂
- ❌ 场景判断错误

---

## 核心要点回顾

### 适配器模式四要素
1. **Client**：客户端
2. **Target**：目标接口
3. **Adapter**：适配器
4. **Adaptee**：被适配者

### 对象适配器 vs 类适配器

| 对比 | 对象适配器✅ | 类适配器⚠️ |
|-----|------------|-----------|
| 实现 | 组合 | 继承 |
| 灵活性 | 高 | 低 |
| 适配多个类 | ✅ 可以 | ❌ 不行 |
| 推荐度 | ⭐⭐⭐⭐⭐ | ⭐⭐ |

### 适配器 vs 其他模式

| 模式 | 目的 | 接口 | 例子 |
|-----|------|------|------|
| **适配器** | 转换接口 | 改变 | 插座转换器 |
| **装饰器** | 增强功能 | 保持 | 给咖啡加奶 |
| **代理** | 控制访问 | 保持 | 门卫 |
| **外观** | 简化接口 | 统一 | 遥控器 |

### 记忆口诀
> **接口不兼容，**  
> **适配器来帮忙，**  
> **组合优于继承，**  
> **转换要轻量。**

---

**完成自测后**，填写 `note_template.md` 巩固知识！
