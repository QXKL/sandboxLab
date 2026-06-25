# 桥接模式 (Bridge Pattern)

> 将抽象部分与实现部分分离，使它们都可以独立变化

---

## 一、生活中的例子

### 📺 遥控器与电视

想象你有多种遥控器和多种电视：

```
继承方式（类爆炸）：
- 索尼电视遥控器
- 三星电视遥控器
- LG电视遥控器
- 索尼电视通用遥控器
- 三星电视通用遥控器
- LG电视通用遥控器
→ 3种电视 × 2种遥控器 = 6个类

桥接方式：
遥控器（抽象） ----桥接----> 电视（实现）
- 通用遥控器              - 索尼电视
- 高级遥控器              - 三星电视
                          - LG电视
→ 2个遥控器 + 3个电视 = 5个类
```

**桥接的关键**：
- 遥控器和电视分离
- 遥控器持有电视的引用
- 两者可以独立变化

---

### 🖌️ 画笔与颜色

```
继承方式（类爆炸）：
- 红色圆形画笔
- 蓝色圆形画笔
- 红色方形画笔
- 蓝色方形画笔
→ 2种形状 × 2种颜色 = 4个类
→ 如果有5种形状、10种颜色 = 50个类！

桥接方式：
画笔（抽象） ----桥接----> 颜色（实现）
- 圆形画笔              - 红色
- 方形画笔              - 蓝色
→ 2个画笔 + 2个颜色 = 4个类
→ 5种形状 + 10种颜色 = 15个类
```

---

## 二、为什么需要桥接模式？

### 问题场景：跨平台消息发送

**需求**：支持不同类型的消息（普通消息、紧急消息）和不同的发送方式（邮件、短信、推送）

**继承方式（类爆炸）**：

```java
// ❌ 继承导致类爆炸
class Message { }

class EmailMessage extends Message { }
class SMSMessage extends Message { }
class PushMessage extends Message { }

class UrgentEmailMessage extends EmailMessage { }
class UrgentSMSMessage extends SMSMessage { }
class UrgentPushMessage extends PushMessage { }

class NormalEmailMessage extends EmailMessage { }
class NormalSMSMessage extends SMSMessage { }
class NormalPushMessage extends PushMessage { }

// 2种消息类型 × 3种发送方式 = 6个类
// 如果增加1种类型或方式，需要增加3个或2个类！
```

**问题**：
1. ❌ **类爆炸**（两个维度组合导致类数量指数增长）
2. ❌ **不灵活**（新增一个维度需要修改多个类）
3. ❌ **违反开闭原则**（扩展困难）
4. ❌ **代码重复**（相似逻辑分散在多个类）

---

**桥接模式解决**：

```java
// ✅ 桥接：分离抽象与实现
// 抽象部分：消息
abstract class Message {
    protected MessageSender sender;  // 桥接：持有实现的引用
    
    public Message(MessageSender sender) {
        this.sender = sender;
    }
    
    abstract void send(String content);
}

class NormalMessage extends Message {
    public void send(String content) {
        sender.sendMessage(content);
    }
}

class UrgentMessage extends Message {
    public void send(String content) {
        sender.sendMessage("[紧急] " + content);
    }
}

// 实现部分：发送方式
interface MessageSender {
    void sendMessage(String message);
}

class EmailSender implements MessageSender {
    public void sendMessage(String message) {
        System.out.println("邮件发送: " + message);
    }
}

class SMSSender implements MessageSender {
    public void sendMessage(String message) {
        System.out.println("短信发送: " + message);
    }
}

// 使用（灵活组合）
Message msg1 = new NormalMessage(new EmailSender());
msg1.send("Hello");  // 普通邮件

Message msg2 = new UrgentMessage(new SMSSender());
msg2.send("Alert!");  // 紧急短信
```

**优势**：
- ✅ **避免类爆炸**（2个消息 + 3个发送方式 = 5个类）
- ✅ **灵活组合**（运行时可以切换发送方式）
- ✅ **独立扩展**（新增消息类型或发送方式互不影响）
- ✅ **符合开闭原则**

---

## 三、桥接模式的核心思想

### 定义

**桥接模式（Bridge Pattern）**：将抽象部分与实现部分分离，使它们都可以独立变化。

### 核心要点

| 要点 | 说明 |
|-----|------|
| **分离抽象与实现** | 抽象和实现不在同一继承体系 |
| **组合优于继承** | 用组合替代多重继承 |
| **独立变化** | 两个维度可以独立扩展 |
| **避免类爆炸** | 类数量从N×M减少到N+M |

---

### 形象理解

```
桥接模式就像"遥控器与电视"：

遥控器（抽象）
    ↓ 桥接（持有引用）
电视（实现）

遥控器可以换（通用→高级）
电视也可以换（索尼→三星）
两者独立变化
```

**关键点**：
- 抽象持有实现的引用（组合）
- 抽象调用实现的方法
- 两者可以独立变化

---

## 四、UML结构与角色

### UML类图

```
┌─────────────────┐
│  Abstraction    │ 抽象类
├─────────────────┤
│- implementor    │ 持有实现的引用（桥接）
├─────────────────┤
│+ operation()    │
└────────▲────────┘
         │
    ┌────┴────┐
    │         │
┌───┴────┐ ┌──┴──────┐
│Refined │ │Refined  │ 扩充抽象类
│AbstrA  │ │AbstrB   │
└────────┘ └─────────┘

         桥接（组合）
         ↓

┌─────────────────┐
│  Implementor    │ 实现接口
├─────────────────┤
│+ operationImpl()│
└────────▲────────┘
         │
    ┌────┴────┐
    │         │
┌───┴────┐ ┌──┴──────┐
│Concrete│ │Concrete │ 具体实现类
│ImplA   │ │ImplB    │
└────────┘ └─────────┘
```

---

### 角色说明

| 角色 | 职责 | 类比 |
|-----|------|------|
| **Abstraction** | 抽象类，持有Implementor引用 | 遥控器 |
| **RefinedAbstraction** | 扩充抽象类 | 通用遥控器、高级遥控器 |
| **Implementor** | 实现接口 | 电视接口 |
| **ConcreteImplementor** | 具体实现类 | 索尼电视、三星电视 |

---

### 关键设计

**1. Abstraction持有Implementor引用（桥接）**
```java
abstract class Abstraction {
    protected Implementor implementor;  // 桥接：组合
    
    public Abstraction(Implementor implementor) {
        this.implementor = implementor;
    }
    
    abstract void operation();
}
```

**2. 抽象调用实现的方法**
```java
class RefinedAbstraction extends Abstraction {
    @Override
    public void operation() {
        implementor.operationImpl();  // 委托给实现
    }
}
```

**3. 独立变化**
```java
// 抽象可以扩展（不影响实现）
class AdvancedAbstraction extends Abstraction { }

// 实现也可以扩展（不影响抽象）
class NewImplementor implements Implementor { }

// 灵活组合
Abstraction abs = new RefinedAbstraction(new NewImplementor());
```

---

## 五、代码示例讲解

### 示例1：遥控器与电视

**场景**：遥控器控制不同品牌的电视

**核心设计**：

```java
// 实现接口：电视
interface TV {
    void on();
    void off();
    void setChannel(int channel);
}

// 具体实现：索尼电视
class SonyTV implements TV {
    public void on() {
        System.out.println("索尼电视开机");
    }
    
    public void off() {
        System.out.println("索尼电视关机");
    }
    
    public void setChannel(int channel) {
        System.out.println("索尼电视切换到频道: " + channel);
    }
}

// 抽象类：遥控器
abstract class RemoteControl {
    protected TV tv;  // 桥接：持有电视引用
    
    public RemoteControl(TV tv) {
        this.tv = tv;
    }
    
    abstract void on();
    abstract void off();
    abstract void setChannel(int channel);
}

// 扩充抽象：基础遥控器
class BasicRemote extends RemoteControl {
    public BasicRemote(TV tv) {
        super(tv);
    }
    
    public void on() {
        tv.on();
    }
    
    public void off() {
        tv.off();
    }
    
    public void setChannel(int channel) {
        tv.setChannel(channel);
    }
}

// 使用
TV sonyTV = new SonyTV();
RemoteControl remote = new BasicRemote(sonyTV);
remote.on();
remote.setChannel(5);
remote.off();
```

**关键点**：
- ✅ 遥控器持有电视引用（桥接）
- ✅ 遥控器和电视独立变化
- ✅ 避免类爆炸

---

### 示例2：形状与颜色

**场景**：形状（圆形、矩形）与颜色（红色、蓝色）分离

**详细内容请查看 `demo/` 目录**

---

### 示例3：消息发送

**场景**：消息类型与发送方式分离

**详细内容请查看 `demo/` 目录**

---

## 六、桥接模式 vs 其他模式

### 1. 桥接 vs 适配器

| 对比 | 桥接模式 | 适配器模式 |
|-----|---------|-----------|
| **目的** | 分离抽象与实现 | 转换接口 |
| **时机** | 设计时（主动） | 事后补救（被动） |
| **对象数量** | 两个维度 | 一对一 |
| **意图** | 让两者独立变化 | 让不兼容的接口合作 |

**示例**：
```java
// 桥接：设计时分离
abstract class Shape {
    protected Color color;  // 桥接：组合
    
    public Shape(Color color) {
        this.color = color;
    }
}

// 适配器：事后转换
class Adapter implements Target {
    private Adaptee adaptee;
    
    public void request() {
        adaptee.specificRequest();  // 转换接口
    }
}
```

---

### 2. 桥接 vs 策略

| 对比 | 桥接模式 | 策略模式 |
|-----|---------|---------|
| **目的** | 分离抽象与实现 | 封装算法 |
| **关注点** | 结构（两个维度） | 行为（算法族） |
| **变化** | 两个维度独立变化 | 算法可以互换 |

---

### 记忆口诀

> **桥接分离两维度，**  
> **组合优于多继承，**  
> **抽象实现各自变，**  
> **避免类爆炸问题。**

---

## 七、使用场景

### ✅ 适合桥接模式的场景

**1. 多维度变化**
```
场景：形状 × 颜色、设备 × 系统
桥接：两个维度独立扩展
```

**2. 避免继承爆炸**
```
场景：N×M种组合
桥接：N+M个类
```

**3. 抽象与实现需要独立变化**
```
场景：跨平台开发
桥接：抽象业务逻辑，实现平台相关
```

**4. 需要运行时切换实现**
```
场景：动态切换数据库、日志框架
桥接：灵活组合
```

---

### ❌ 不适合桥接模式的场景

**1. 只有一个维度变化**
```
单一维度 → 普通继承即可
```

**2. 抽象与实现强耦合**
```
无法分离 → 不适合桥接
```

**3. 过度设计**
```
简单场景 → 不需要桥接
```

---

## 八、优缺点分析

### 优点

| 优点 | 说明 | 例子 |
|-----|------|------|
| ✅ **分离抽象与实现** | 两者独立变化 | 遥控器与电视 |
| ✅ **避免类爆炸** | 类数量从N×M减少到N+M | 2×3=6 → 2+3=5 |
| ✅ **符合开闭原则** | 扩展不修改 | 新增遥控器或电视 |
| ✅ **提高灵活性** | 运行时切换实现 | 切换发送方式 |

---

### 缺点

| 缺点 | 说明 | 解决方案 |
|-----|------|---------|
| ❌ **增加复杂度** | 多一层抽象 | 确实需要时才用 |
| ❌ **理解困难** | 抽象与实现的分离不易理解 | 多看示例 |

---

## 九、注意事项与常见误区

### 陷阱1：混淆桥接和适配器

```java
// 桥接：设计时主动分离
abstract class RemoteControl {
    protected TV tv;  // 桥接：设计时就分离
}

// 适配器：事后补救
class Adapter implements Target {
    private Adaptee adaptee;  // 适配：事后转换接口
}
```

**区别**：
- **桥接**：设计时就计划好的分离
- **适配器**：已有代码不兼容，事后补救

---

### 陷阱2：过度设计

```java
// ❌ 不好：只有一个维度，不需要桥接
abstract class Shape {
    protected Color color;  // 只有形状一个维度变化，不需要桥接
}

// ✅ 好：两个维度都会变化，需要桥接
abstract class Shape {
    protected Color color;  // 形状和颜色都会变化
}
```

**判断标准**：
- 是否有两个维度都会变化？
- 是否会导致类爆炸？
- 如果没有，不需要桥接

---

### 陷阱3：不理解"抽象"与"实现"

```
"抽象"不是abstract关键字
"实现"不是implements关键字

"抽象"：业务层面的抽象（如：遥控器）
"实现"：底层实现（如：具体的电视）
```

---

## 十、真实应用案例

### 1. JDBC（经典！）

**场景**：JDBC是桥接模式的经典应用

```java
// 抽象：JDBC API
Connection conn = DriverManager.getConnection(url);
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);

// 实现：不同数据库驱动
// - MySQL驱动
// - PostgreSQL驱动
// - Oracle驱动

// 桥接：
// Connection（抽象）持有数据库驱动（实现）的引用
// 业务代码与具体数据库解耦
```

**本质**：桥接模式

---

### 2. AWT/Swing

**场景**：跨平台GUI

```java
// 抽象：AWT组件
Button button = new Button("Click");

// 实现：不同平台的UI实现
// - Windows UI
// - Mac UI
// - Linux UI

// 桥接：AWT组件持有平台相关实现的引用
```

---

### 3. 日志框架

**场景**：SLF4J桥接不同的日志实现

```java
// 抽象：SLF4J接口
Logger logger = LoggerFactory.getLogger(MyClass.class);
logger.info("message");

// 实现：不同日志框架
// - Logback
// - Log4j
// - JUL

// 桥接：SLF4J持有具体日志实现的引用
```

---

## 十一、总结

### 核心要点

| 要点 | 内容 |
|-----|------|
| **定义** | 将抽象与实现分离，使它们独立变化 |
| **目的** | 避免类爆炸，提高灵活性 |
| **关键设计** | 抽象持有实现的引用（组合） |
| **优势** | 类数量从N×M减少到N+M |
| **典型应用** | JDBC、AWT、日志框架 |

---

### 何时使用？

**判断标准**：
```
是否有两个维度都会变化？
  ├─ 是 → ✅ 桥接模式
  └─ 否 → 普通继承
  
是否会导致类爆炸？
  ├─ 是 → ✅ 桥接模式
  └─ 否 → 考虑其他方案
```

---

### 记忆口诀

> **两个维度要变化，**  
> **桥接模式来分离，**  
> **抽象实现各自扩展，**  
> **遥控电视是典型。**

---

## 下一步

完成桥接模式学习后：
1. ✅ 运行 `demo/` 目录下的代码示例
2. ✅ 完成 `test_01.md` 的自测题
3. ✅ 填写 `note_template.md` 巩固知识
4. ✅ 思考：JDBC为什么用桥接模式？

**继续学习**：下一个结构型模式 → **组合模式**

---

**💡 记住**：桥接模式就像遥控器与电视，两者分离，各自独立变化。
