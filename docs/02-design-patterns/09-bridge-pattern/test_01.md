# 桥接模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 45 分）

### 1. 桥接模式的主要目的是什么？

A. 将一个类的接口转换成客户希望的另一个接口  
B. 将抽象部分与实现部分分离，使它们都可以独立变化  
C. 为其他对象提供一种代理以控制对这个对象的访问  
D. 为子系统中的一组接口提供一个统一的高层接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 桥接模式的核心目的是**将抽象部分与实现部分分离**，使它们都可以独立变化，避免类爆炸。

**关键词**：分离抽象与实现、独立变化、避免类爆炸

**形象类比**：遥控器（抽象）与电视（实现）分离，两者独立变化

类数量：N×M → N+M
</details>

---

### 2. 桥接模式和适配器模式的主要区别是什么？

A. 桥接是设计时主动分离，适配器是事后补救  
B. 桥接改变接口，适配器保持接口  
C. 桥接用于单个对象，适配器用于多个对象  
D. 两者没有区别

<details>
<summary>查看答案</summary>

**答案**: A

**解析**:

**桥接模式**：
- **时机**：设计时（主动）
- **目的**：分离抽象与实现
- **意图**：让两者独立变化

**适配器模式**：
- **时机**：事后补救（被动）
- **目的**：转换接口
- **意图**：让不兼容的接口合作

**记忆口诀**：
> **桥接设计时分离，适配事后来补救。**
</details>

---

### 3. 以下哪个场景最适合使用桥接模式？

A. 需要转换第三方接口以适配系统  
B. 形状（圆形、矩形）与颜色（红色、蓝色）两个维度都会变化  
C. 需要延迟加载大对象  
D. 需要为复杂子系统提供简单接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**B正确** - 两个维度都会变化：

```java
// 桥接：避免类爆炸
abstract class Shape {
    protected Color color;  // 桥接
    
    public Shape(Color color) {
        this.color = color;
    }
}

// 使用
Shape redCircle = new Circle(new Red());
Shape blueRectangle = new Rectangle(new Blue());
```

**类数量对比**：
- 继承方式：3种形状 × 3种颜色 = 9个类
- 桥接方式：3种形状 + 3种颜色 = 6个类

**其他选项**：
- **A**：转换接口 → **适配器模式**
- **C**：延迟加载 → **代理模式（虚拟代理）**
- **D**：简化接口 → **外观模式**
</details>

---

## 二、代码分析（每题 20 分，共 40 分）

### 4. 以下代码有什么问题？如何改进？

```java
// 形状类层次（继承方式）
class Shape { }

class RedCircle extends Shape { }
class BlueCircle extends Shape { }
class GreenCircle extends Shape { }

class RedRectangle extends Shape { }
class BlueRectangle extends Shape { }
class GreenRectangle extends Shape { }

class RedTriangle extends Shape { }
class BlueTriangle extends Shape { }
class GreenTriangle extends Shape { }

// 3种形状 × 3种颜色 = 9个类
// 如果5种形状 × 10种颜色 = 50个类！
```

<details>
<summary>参考答案</summary>

**问题**: 使用继承导致类爆炸

**问题分析**：
1. ❌ **类爆炸**（两个维度组合导致类数量指数增长）
2. ❌ **不灵活**（无法运行时切换颜色）
3. ❌ **难以扩展**（新增形状或颜色需要修改多个类）
4. ❌ **违反开闭原则**

**改进方案**：使用桥接模式

```java
// 1. 实现接口：颜色
interface Color {
    void applyColor();
}

class Red implements Color {
    public void applyColor() {
        System.out.println("红色");
    }
}

class Blue implements Color {
    public void applyColor() {
        System.out.println("蓝色");
    }
}

// 2. 抽象类：形状
abstract class Shape {
    protected Color color;  // 桥接：持有颜色引用
    
    public Shape(Color color) {
        this.color = color;
    }
    
    abstract void draw();
}

// 3. 扩充抽象：具体形状
class Circle extends Shape {
    public Circle(Color color) {
        super(color);
    }
    
    public void draw() {
        System.out.print("绘制圆形，颜色: ");
        color.applyColor();
    }
}

class Rectangle extends Shape {
    public Rectangle(Color color) {
        super(color);
    }
    
    public void draw() {
        System.out.print("绘制矩形，颜色: ");
        color.applyColor();
    }
}

// 4. 使用（灵活组合）
Shape redCircle = new Circle(new Red());
redCircle.draw();

Shape blueRectangle = new Rectangle(new Blue());
blueRectangle.draw();
```

**优势**：
1. ✅ **避免类爆炸**（3种形状 + 3种颜色 = 6个类）
2. ✅ **独立扩展**（新增形状不影响颜色）
3. ✅ **灵活组合**（运行时切换颜色）
4. ✅ **符合开闭原则**

**类数量对比**：
```
┌─────────┬──────────┬──────────┐
│ 组合    │ 继承方式 │ 桥接模式 │
├─────────┼──────────┼──────────┤
│ 3 × 3   │   9类    │   6类    │
│ 5 × 10  │  50类    │  15类    │
│ 10 × 20 │ 200类    │  30类    │
└─────────┴──────────┴──────────┘
```
</details>

---

### 5. 实现消息发送桥接

要求：
- 抽象：消息类型（普通消息、紧急消息）
- 实现：发送方式（邮件、短信）
- 使用桥接模式实现

<details>
<summary>参考答案</summary>

```java
// 1. 实现接口：发送方式
interface MessageSender {
    void sendMessage(String message);
}

class EmailSender implements MessageSender {
    public void sendMessage(String message) {
        System.out.println("📧 邮件发送: " + message);
    }
}

class SMSSender implements MessageSender {
    public void sendMessage(String message) {
        System.out.println("📱 短信发送: " + message);
    }
}

// 2. 抽象类：消息
abstract class Message {
    protected MessageSender sender;  // 桥接
    
    public Message(MessageSender sender) {
        this.sender = sender;
    }
    
    abstract void send(String content);
}

// 3. 扩充抽象：具体消息
class NormalMessage extends Message {
    public NormalMessage(MessageSender sender) {
        super(sender);
    }
    
    public void send(String content) {
        sender.sendMessage(content);
    }
}

class UrgentMessage extends Message {
    public UrgentMessage(MessageSender sender) {
        super(sender);
    }
    
    public void send(String content) {
        sender.sendMessage("[紧急] " + content);
    }
}

// 4. 使用
Message msg1 = new NormalMessage(new EmailSender());
msg1.send("会议通知");

Message msg2 = new UrgentMessage(new SMSSender());
msg2.send("系统故障");
```

**关键点**：
- ✅ 消息类型和发送方式分离
- ✅ 两个维度独立扩展
- ✅ 灵活组合
</details>

---

## 三、场景判断（15 分）

### 6. 判断以下场景是否适合桥接模式

**场景A**: JDBC数据库驱动  
**场景B**: 将旧系统接口适配到新系统  
**场景C**: 跨平台GUI框架  
**场景D**: 家庭影院系统统一控制

<details>
<summary>参考答案</summary>

### 场景A: JDBC
**推荐**: ✅ 适合桥接模式

**理由**：
- 抽象：JDBC API
- 实现：不同数据库驱动
- 两者独立变化

```java
// 桥接
Connection conn = DriverManager.getConnection(url);
// Connection是抽象，具体驱动是实现
```

---

### 场景B: 旧系统适配
**推荐**: ❌ 不适合桥接模式，应该用**适配器模式**

**理由**：
- 不是设计时分离
- 是事后补救
- 适配器更合适

---

### 场景C: 跨平台GUI
**推荐**: ✅ 适合桥接模式

**理由**：
- 抽象：GUI组件
- 实现：平台相关UI
- AWT/Swing就是桥接模式

---

### 场景D: 家庭影院
**推荐**: ❌ 不适合桥接模式，应该用**外观模式**

**理由**：
- 不是两个维度变化
- 是简化复杂子系统
- 外观模式更合适

---

### 总结

**适合桥接模式**：
- ✅ 场景A：JDBC（抽象与实现分离）
- ✅ 场景C：跨平台GUI（两个维度）

**不适合桥接模式**：
- ❌ 场景B：旧系统适配（用适配器）
- ❌ 场景D：家庭影院（用外观）
</details>

---

## 核心要点回顾

### 桥接模式要素
1. **Abstraction**：抽象类
2. **RefinedAbstraction**：扩充抽象类
3. **Implementor**：实现接口
4. **ConcreteImplementor**：具体实现类

### 记忆口诀
> **两个维度要变化，**  
> **桥接模式来分离，**  
> **抽象实现各自扩展，**  
> **遥控电视是典型。**

---

**完成自测后**，填写 `note_template.md` 巩固知识！
