# 适配器模式 (Adapter Pattern)

> 让不兼容的接口能够合作

---

## 一、生活中的例子

### 🔌 插座转换器

想象你去国外旅行：

```
你的充电器 (国标三脚插头)
    ↓
❌ 无法直接插入
    ↓
美国插座 (两脚插座)

解决方案：
你的充电器 → 插座转换器 → 美国插座
            (适配器)
```

**适配器做了什么？**
- 一端连接你的三脚插头（现有接口）
- 另一端连接美国插座（目标接口）
- 让两个不兼容的接口能够合作

---

### 📱 读卡器

```
SD卡 → 读卡器 → USB接口 → 电脑
     (适配器)
```

**读卡器是适配器**：
- SD卡：现有接口
- USB：目标接口
- 读卡器：适配器

---

## 二、什么是适配器模式？

### 定义

**适配器模式（Adapter Pattern）**：将一个类的接口转换成客户希望的另一个接口。适配器让原本接口不兼容的类可以合作。

### 核心思想

```
客户端 → 目标接口 ← 适配器 → 被适配者
(Client)  (Target)   (Adapter)  (Adaptee)
```

**形象理解**：
- **客户端**：你（想充电）
- **目标接口**：你期望的接口（美国插座标准）
- **适配器**：转换器（做转换）
- **被适配者**：你的充电器（国标插头）

---

## 三、为什么需要适配器模式？

### 问题场景

**场景1：系统集成**
```java
// 老系统的日志接口
class OldLogger {
    public void writeLog(String msg) { ... }
}

// 新系统期望的接口
interface Logger {
    void log(String level, String message);
}

// ❌ 接口不兼容！
Logger logger = new OldLogger();  // 编译错误
```

**场景2：第三方库不兼容**
```java
// 你的系统
interface PaymentProcessor {
    boolean processPayment(double amount);
}

// 第三方支付SDK
class AlipaySDK {
    public String pay(String orderId, int cents) { ... }
}

// ❌ 接口不同，无法直接使用
```

### 解决方案

**适配器模式解决**：
```java
// 适配器：将OldLogger适配为Logger接口
class LoggerAdapter implements Logger {
    private OldLogger oldLogger;
    
    @Override
    public void log(String level, String message) {
        // 转换调用
        oldLogger.writeLog("[" + level + "] " + message);
    }
}

// ✅ 现在可以使用了
Logger logger = new LoggerAdapter(new OldLogger());
logger.log("INFO", "System started");
```

---

## 四、适配器模式的结构

### UML类图

```
┌─────────────┐
│   Client    │ 使用
└──────┬──────┘
       │
       │ 依赖
       ↓
┌─────────────┐
│   Target    │ 目标接口
│  (interface)│
├─────────────┤
│ + request() │
└──────▲──────┘
       │
       │ 实现
┌──────┴──────┐
│   Adapter   │ 适配器
├─────────────┤
│ - adaptee   │ 持有被适配者
├─────────────┤
│ + request() │ 转换调用
└──────┬──────┘
       │
       │ 组合
       ↓
┌─────────────┐
│   Adaptee   │ 被适配者
├─────────────┤
│ + specific()│ 特定接口
└─────────────┘
```

### 角色说明

| 角色 | 职责 | 类比 |
|-----|------|------|
| **Client** | 使用目标接口的客户端 | 你（想充电） |
| **Target** | 客户端期望的接口 | 美国插座标准 |
| **Adapter** | 转换接口的适配器 | 插座转换器 |
| **Adaptee** | 需要被适配的类 | 你的国标插头 |

---

## 五、适配器的两种实现方式

### 1. 对象适配器（组合，推荐✅）

**实现**：适配器持有被适配者的实例（组合）

```java
// 目标接口
interface MediaPlayer {
    void play(String filename);
}

// 被适配者
class VLCPlayer {
    public void playVLC(String filename) {
        System.out.println("Playing VLC: " + filename);
    }
}

// 对象适配器（组合）
class VLCAdapter implements MediaPlayer {
    private VLCPlayer vlcPlayer;  // 持有被适配者
    
    public VLCAdapter(VLCPlayer vlcPlayer) {
        this.vlcPlayer = vlcPlayer;
    }
    
    @Override
    public void play(String filename) {
        // 转换调用
        vlcPlayer.playVLC(filename);
    }
}

// 使用
MediaPlayer player = new VLCAdapter(new VLCPlayer());
player.play("movie.vlc");
```

**优点**：
- ✅ 灵活（可以适配多个被适配者）
- ✅ 符合组合复用原则
- ✅ 单继承语言的唯一选择

---

### 2. 类适配器（继承，少用⚠️）

**实现**：适配器继承被适配者（继承）

```java
// 类适配器（继承）
class VLCAdapter extends VLCPlayer implements MediaPlayer {
    @Override
    public void play(String filename) {
        // 直接调用父类方法
        playVLC(filename);
    }
}

// 使用
MediaPlayer player = new VLCAdapter();
player.play("movie.vlc");
```

**缺点**：
- ❌ 只能适配一个类（Java单继承）
- ❌ 暴露被适配者的方法
- ❌ 灵活性差

**对比总结**：
```
对象适配器（组合）✅
  优点：灵活、可适配多个类、符合组合复用原则
  缺点：代码稍多

类适配器（继承）⚠️
  优点：代码简洁
  缺点：单继承限制、灵活性差
```

**推荐**：**使用对象适配器（组合）**

---

## 六、代码示例

### 示例1：媒体播放器适配器

**场景**：你的媒体播放器只支持MP3，现在要支持VLC和MP4格式。

```java
// 目标接口：你的播放器接口
interface MediaPlayer {
    void play(String audioType, String filename);
}

// 被适配者1：VLC播放器
class VLCPlayer {
    public void playVLC(String filename) {
        System.out.println("Playing VLC file: " + filename);
    }
}

// 被适配者2：MP4播放器
class MP4Player {
    public void playMP4(String filename) {
        System.out.println("Playing MP4 file: " + filename);
    }
}

// 适配器：高级播放器适配器
class MediaAdapter implements MediaPlayer {
    private VLCPlayer vlcPlayer;
    private MP4Player mp4Player;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            vlcPlayer = new VLCPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            mp4Player = new MP4Player();
        }
    }
    
    @Override
    public void play(String audioType, String filename) {
        if (audioType.equalsIgnoreCase("vlc")) {
            vlcPlayer.playVLC(filename);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            mp4Player.playMP4(filename);
        }
    }
}

// 客户端：你的播放器
class AudioPlayer implements MediaPlayer {
    @Override
    public void play(String audioType, String filename) {
        // 内置支持MP3
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing MP3 file: " + filename);
        }
        // 通过适配器支持其他格式
        else if (audioType.equalsIgnoreCase("vlc") || 
                 audioType.equalsIgnoreCase("mp4")) {
            MediaAdapter adapter = new MediaAdapter(audioType);
            adapter.play(audioType, filename);
        } else {
            System.out.println("Invalid format: " + audioType);
        }
    }
}

// 使用
AudioPlayer player = new AudioPlayer();
player.play("mp3", "song.mp3");      // 直接支持
player.play("vlc", "movie.vlc");     // 通过适配器
player.play("mp4", "video.mp4");     // 通过适配器
```

**流程图**：
```
AudioPlayer
    ├─ mp3 → 直接播放
    ├─ vlc → MediaAdapter → VLCPlayer
    └─ mp4 → MediaAdapter → MP4Player
```

---

### 示例2：支付系统适配器

**场景**：你的系统使用统一的支付接口，需要适配不同的第三方支付SDK。

```java
// 目标接口：统一支付接口
interface PaymentProcessor {
    boolean processPayment(String orderId, double amount);
}

// 被适配者1：支付宝SDK
class AlipaySDK {
    public String pay(String orderId, int cents) {
        System.out.println("支付宝支付: " + cents + "分");
        return "SUCCESS";
    }
}

// 被适配者2：微信支付SDK
class WeChatPaySDK {
    public boolean doPay(long orderNumber, double yuan) {
        System.out.println("微信支付: " + yuan + "元");
        return true;
    }
}

// 适配器1：支付宝适配器
class AlipayAdapter implements PaymentProcessor {
    private AlipaySDK alipay;
    
    public AlipayAdapter(AlipaySDK alipay) {
        this.alipay = alipay;
    }
    
    @Override
    public boolean processPayment(String orderId, double amount) {
        // 转换：元 → 分
        int cents = (int) (amount * 100);
        String result = alipay.pay(orderId, cents);
        return "SUCCESS".equals(result);
    }
}

// 适配器2：微信支付适配器
class WeChatPayAdapter implements PaymentProcessor {
    private WeChatPaySDK wechatPay;
    
    public WeChatPayAdapter(WeChatPaySDK wechatPay) {
        this.wechatPay = wechatPay;
    }
    
    @Override
    public boolean processPayment(String orderId, double amount) {
        // 转换：String → long
        long orderNumber = Long.parseLong(orderId);
        return wechatPay.doPay(orderNumber, amount);
    }
}

// 使用
PaymentProcessor alipay = new AlipayAdapter(new AlipaySDK());
alipay.processPayment("12345", 99.99);

PaymentProcessor wechat = new WeChatPayAdapter(new WeChatPaySDK());
wechat.processPayment("67890", 199.00);
```

**适配器转换的内容**：
1. **方法名**：`pay()` → `processPayment()`
2. **参数类型**：`String, int` → `String, double`
3. **返回值**：`String` → `boolean`
4. **单位转换**：元 ↔ 分

---

## 七、适配器模式的优缺点

### 优点

| 优点 | 说明 | 例子 |
|-----|------|------|
| ✅ **复用现有类** | 不修改原有代码 | 复用第三方SDK |
| ✅ **解耦** | 客户端与被适配者解耦 | 统一支付接口 |
| ✅ **符合开闭原则** | 增加新适配器无需修改客户端 | 新增支付方式 |
| ✅ **灵活** | 可以适配多个不同的类 | 适配多种播放器 |

---

### 缺点

| 缺点 | 说明 | 解决方案 |
|-----|------|---------|
| ❌ **增加复杂度** | 新增适配器类 | 必要时才用 |
| ❌ **性能开销** | 多一层调用 | 影响不大 |
| ❌ **过度使用** | 适配器过多导致混乱 | 合理规划 |

---

## 八、何时使用适配器模式？

### ✅ 适合的场景

1. **系统集成**
   ```
   老系统 + 新系统 → 适配器桥接
   ```

2. **第三方库适配**
   ```
   你的接口 + 第三方SDK → 适配器转换
   ```

3. **接口不兼容**
   ```
   现有类的接口 ≠ 期望的接口 → 适配器转换
   ```

4. **复用旧代码**
   ```
   不能修改的旧代码 → 适配器包装
   ```

---

### ❌ 不适合的场景

1. **能直接修改代码**
   ```
   如果能修改被适配者，直接改比适配更好
   ```

2. **接口差异太大**
   ```
   转换逻辑过于复杂 → 重新设计
   ```

3. **简单包装**
   ```
   只是简单转发 → 不需要适配器，直接用即可
   ```

---

## 九、适配器模式 vs 其他模式

### 1. 适配器 vs 装饰器

| 对比 | 适配器模式 | 装饰器模式 |
|-----|-----------|-----------|
| **目的** | 转换接口 | 增强功能 |
| **接口** | 改变接口 | 保持接口 |
| **例子** | 插座转换器 | 给咖啡加奶 |

**示例**：
```java
// 适配器：改变接口
class Adapter implements NewInterface {
    private OldClass old;
    // 转换调用
}

// 装饰器：增强功能
class Decorator implements Interface {
    private Interface wrapped;
    // 增强 + 转发
}
```

---

### 2. 适配器 vs 代理

| 对比 | 适配器模式 | 代理模式 |
|-----|-----------|---------|
| **目的** | 转换接口 | 控制访问 |
| **接口** | 不同接口 | 相同接口 |
| **例子** | 读卡器 | 门卫 |

---

### 3. 适配器 vs 外观

| 对比 | 适配器模式 | 外观模式 |
|-----|-----------|---------|
| **目的** | 转换接口 | 简化接口 |
| **对象数量** | 1个 | 多个 |
| **例子** | 插座转换器 | 遥控器（统一家电） |

---

## 十、真实案用

### 1. SLF4J日志框架

**场景**：SLF4J提供统一的日志接口，适配不同的日志实现。

```java
// 目标接口：SLF4J
interface Logger {
    void info(String msg);
}

// 被适配者：Log4j
class Log4jImpl {
    public void log(String level, String msg) { ... }
}

// 适配器：Log4j适配器
class Log4jAdapter implements Logger {
    private Log4jImpl log4j;
    
    @Override
    public void info(String msg) {
        log4j.log("INFO", msg);
    }
}
```

**适配流程**：
```
你的代码 → SLF4J接口 → 适配器 → Log4j/Logback/JUL
```

---

### 2. JDBC数据库驱动

**场景**：JDBC提供统一的数据库接口，不同数据库提供适配器。

```java
// 目标接口：JDBC
interface Connection {
    Statement createStatement();
}

// 被适配者：MySQL原生驱动
class MySQLNativeDriver {
    public MySQLStatement getStatement() { ... }
}

// 适配器：MySQL JDBC驱动
class MySQLDriver implements Connection {
    private MySQLNativeDriver driver;
    
    @Override
    public Statement createStatement() {
        MySQLStatement stmt = driver.getStatement();
        return new StatementAdapter(stmt);
    }
}
```

---

### 3. InputStreamReader（Java I/O）

**场景**：将字节流适配为字符流。

```java
// 被适配者：字节流
InputStream inputStream = new FileInputStream("file.txt");

// 适配器：字节流 → 字符流
Reader reader = new InputStreamReader(inputStream, "UTF-8");

// 现在可以按字符读取
int ch = reader.read();
```

**适配**：
```
InputStream (字节) → InputStreamReader → Reader (字符)
                     (适配器)
```

---

## 十一、实现适配器的最佳实践

### 1. 优先使用对象适配器（组合）

```java
// ✅ 推荐：对象适配器
class Adapter implements Target {
    private Adaptee adaptee;  // 组合
}

// ⚠️ 少用：类适配器
class Adapter extends Adaptee implements Target {
    // 继承
}
```

---

### 2. 适配器命名

**命名规范**：
```
被适配类名 + Adapter

例如：
AlipayAdapter
VLCAdapter
Log4jAdapter
```

---

### 3. 单一职责

```java
// ✅ 好：一个适配器适配一个类
class AlipayAdapter implements PaymentProcessor {
    private AlipaySDK alipay;
}

// ❌ 差：一个适配器适配多个类
class PaymentAdapter implements PaymentProcessor {
    private AlipaySDK alipay;
    private WeChatPaySDK wechat;
    private UnionPaySDK unionpay;
    // 太复杂了！
}
```

---

### 4. 适配器要轻量

```java
// ✅ 好：只做接口转换
class Adapter implements Target {
    public void request() {
        adaptee.specificRequest();  // 简单转发
    }
}

// ❌ 差：添加过多业务逻辑
class Adapter implements Target {
    public void request() {
        // 大量业务逻辑...
        adaptee.specificRequest();
        // 更多业务逻辑...
    }
}
```

---

## 十二、常见陷阱

### 陷阱1：过度适配

```java
// ❌ 不好：能改就不要适配
class MyClass {
    public void oldMethod() { ... }
}

// 如果能改，直接改名更好
class MyClass {
    public void newMethod() { ... }  // 直接改
}
```

---

### 陷阱2：适配器太重

```java
// ❌ 适配器包含大量业务逻辑
class HeavyAdapter implements Target {
    public void request() {
        // 100行业务逻辑...
        adaptee.specificRequest();
        // 又是100行...
    }
}

// ✅ 适配器应该轻量
class LightAdapter implements Target {
    public void request() {
        adaptee.specificRequest();  // 只做转换
    }
}
```

---

### 陷阱3：双向适配

```java
// ⚠️ 小心：双向适配
class TwoWayAdapter implements InterfaceA, InterfaceB {
    // 既适配A又适配B，容易混乱
}

// ✅ 更好：分成两个适配器
class AdapterA implements InterfaceA { ... }
class AdapterB implements InterfaceB { ... }
```

---

## 十三、总结

### 核心要点

| 要点 | 内容 |
|-----|------|
| **定义** | 将一个类的接口转换成客户希望的另一个接口 |
| **目的** | 让接口不兼容的类可以合作 |
| **实现方式** | 对象适配器（组合）、类适配器（继承） |
| **推荐** | 对象适配器 |
| **典型场景** | 系统集成、第三方库适配、旧代码复用 |

---

### 记忆口诀

> **接口不兼容，**  
> **适配器来帮忙，**  
> **组合优于继承，**  
> **转换要轻量。**

---

### 何时使用？

**判断标准**：
```
接口不兼容？
  ├─ 能修改代码？ → 直接修改，不用适配
  ├─ 不能修改？
  │   ├─ 接口差异小？ → ✅ 适配器模式
  │   └─ 接口差异大？ → 重新设计
```

---

### 与生活的类比

| 设计模式 | 生活类比 | 作用 |
|---------|---------|------|
| **适配器** | 插座转换器 | 让不同标准能合作 |
| **装饰器** | 给咖啡加糖 | 增强功能 |
| **代理** | 门卫 | 控制访问 |

---

## 下一步

完成适配器模式学习后：
1. ✅ 运行 `demo/` 目录下的代码示例
2. ✅ 完成 `test_01.md` 的自测题
3. ✅ 填写 `note_template.md` 巩固知识
4. ✅ 思考：你的项目中哪里可以用适配器？

**继续学习**：下一个结构型模式 → **装饰器模式**

---

**💡 记住**：适配器模式不改变功能，只转换接口。就像插座转换器不改变电压，只转换插头形状。
