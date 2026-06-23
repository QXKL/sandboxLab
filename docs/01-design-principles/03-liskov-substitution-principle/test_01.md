# 里氏替换原则 - 自测题

完成这些题目,检验你对里氏替换原则的理解程度。

---

## 一、概念理解（选择题）

### 1. 里氏替换原则（LSP）的核心含义是什么？

A. 子类必须实现父类的所有方法  
B. 子类对象必须能够替换父类对象,而不影响程序的正确性  
C. 子类不能有比父类更多的方法  
D. 子类必须和父类有完全相同的行为

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 里氏替换原则的核心是"子类对象必须能够替换父类对象出现的任何地方,而不影响程序的正确性"。这意味着:
- 子类应该扩展父类的功能,而不是改变父类的行为
- 使用父类的地方,替换成子类后,程序应该表现一致
- 子类不应该违反父类定义的契约

选项A是接口实现的要求,不是LSP的核心。选项C错误,子类可以有额外的方法。选项D过于绝对,子类可以扩展行为,只是不能改变已有的行为契约。
</details>

---

### 2. 以下哪个场景违反了里氏替换原则？

A. `ArrayList` 继承 `AbstractList`,实现了所有抽象方法  
B. `Square` 继承 `Rectangle`,重写了 `setWidth` 使其同时修改高度  
C. `Dog` 继承 `Animal`,新增了 `bark()` 方法  
D. `SortedList` 继承 `List`,保证元素始终有序

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: `Square` 继承 `Rectangle` 是经典的违反LSP的例子。

**问题分析**:
- `Rectangle` 的契约是: `setWidth` 不影响高度, `setHeight` 不影响宽度
- `Square` 为了保持"宽高相等"的特性,重写了这些方法,使得设置宽度会同时改变高度
- 这破坏了父类的行为约定,导致 `Square` 不能替换 `Rectangle`

**其他选项**:
- A: `ArrayList` 正确实现了 `AbstractList`,没有改变父类行为 ✓
- C: `Dog` 扩展了新方法但没有改变父类行为 ✓
- D: `SortedList` 加强了后置条件(元素有序),这是允许的 ✓

**关键**: 违反LSP的标志是子类**改变**了父类的已有行为,而不是**扩展**新行为。
</details>

---

### 3. 关于里氏替换原则,以下说法错误的是？

A. LSP是实现开闭原则的基础  
B. 遵循LSP可以保证多态的可靠性  
C. 数学上的"is-a"关系在代码中就应该用继承表示  
D. 不可变对象更容易遵循LSP

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: 数学/概念上的"is-a"关系不等于代码上的继承关系。

**为什么C是错误的**:
- 数学上: 正方形是矩形 ✓
- 代码上: `Square extends Rectangle` ✗ (违反LSP)
- OOP的继承是**行为上的替代关系**,不是分类学关系

**正确的理解**:
- 只有当子类能够在**行为上**完全替换父类时,才应该使用继承
- 如果只是概念上相似,但行为上有差异,应该用接口或组合

**其他选项都是正确的**:
- A: OCP依赖多态,多态依赖可替换的子类(LSP) ✓
- B: LSP保证了子类可以安全替换父类,多态才可靠 ✓
- D: 不可变对象没有状态变化,不容易违反契约 ✓
</details>

---

## 二、代码分析题

### 4. 判断是否违反LSP

以下是一个鸟类的继承层次,请判断是否违反了里氏替换原则,并说明理由。

```java
class Bird {
    public void fly() {
        System.out.println("鸟在飞");
    }
    
    public void eat() {
        System.out.println("鸟在吃");
    }
}

class Sparrow extends Bird {
    @Override
    public void fly() {
        System.out.println("麻雀在飞");
    }
}

class Penguin extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("企鹅不会飞");
    }
}
```

<details>
<summary>参考答案</summary>

**判断**: 违反了里氏替换原则

**理由**:

1. **契约违反**: 
   - `Bird` 类定义了 `fly()` 方法,隐含的契约是"所有鸟都会飞"
   - `Penguin` 重写 `fly()` 抛出异常,违反了这个契约
   - 使用者期望所有 `Bird` 都能调用 `fly()`,但 `Penguin` 打破了这个期望

2. **不能替换**:
   ```java
   void makeBirdFly(Bird bird) {
       bird.fly();  // 如果传入Penguin会抛异常
   }
   
   makeBirdFly(new Sparrow());  // ✓ 正常
   makeBirdFly(new Penguin());  // ❌ 抛异常
   ```

3. **后置条件弱化**: 
   - 父类保证: `fly()` 正常执行
   - 子类削弱: `fly()` 可能抛异常

**重构建议**:

```java
// 方案1: 接口隔离
interface Flyable {
    void fly();
}

interface Eatable {
    void eat();
}

class Sparrow implements Flyable, Eatable {
    public void fly() { /* 麻雀飞行 */ }
    public void eat() { /* 麻雀进食 */ }
}

class Penguin implements Eatable {
    public void eat() { /* 企鹅进食 */ }
    // 不实现Flyable,因为企鹅不会飞
}

// 方案2: 正确的继承层次
abstract class Bird {
    public abstract void eat();
}

abstract class FlyingBird extends Bird {
    public abstract void fly();
}

class Sparrow extends FlyingBird {
    public void fly() { /* 实现 */ }
    public void eat() { /* 实现 */ }
}

class Penguin extends Bird {
    public void eat() { /* 实现 */ }
    // 不继承FlyingBird,自然没有fly方法
}
```

**关键原则**: 不要通过抛异常来"禁用"父类方法,这违反了LSP。
</details>

---

### 5. 契约分析

以下代码中,`CreditCardPayment` 是否违反了里氏替换原则？请从契约的角度分析。

```java
abstract class Payment {
    // 前置条件: amount > 0
    // 后置条件: 返回true表示支付成功,false表示失败
    // 不变式: 不会抛出异常
    public abstract boolean pay(double amount);
}

class CashPayment extends Payment {
    @Override
    public boolean pay(double amount) {
        if (amount <= 0) return false;
        System.out.println("现金支付: " + amount);
        return true;
    }
}

class CreditCardPayment extends Payment {
    private double limit = 1000.0;
    
    @Override
    public boolean pay(double amount) {
        // 加强了前置条件: 不仅要求amount > 0,还要求amount <= limit
        if (amount <= 0 || amount > limit) {
            return false;
        }
        System.out.println("信用卡支付: " + amount);
        return true;
    }
}
```

<details>
<summary>参考答案</summary>

**判断**: 可能违反LSP,取决于父类契约的定义

**契约分析**:

| 契约维度 | Payment (父类) | CreditCardPayment (子类) | 是否符合LSP |
|---------|---------------|------------------------|-----------|
| **前置条件** | amount > 0 | amount > 0 且 amount <= limit | ❌ 加强了 |
| **后置条件** | 返回boolean | 返回boolean | ✓ 保持了 |
| **不变式** | 不抛异常 | 不抛异常 | ✓ 保持了 |

**问题点**: 子类加强了前置条件

- 父类: 只要 `amount > 0` 就应该尝试支付
- 子类: 要求 `amount > 0 且 amount <= limit`
- 这使得子类比父类更严格,违反了LSP的"前置条件不能强化"原则

**为什么这是问题**:

```java
void processPayment(Payment payment, double amount) {
    // 调用者相信: 只要amount > 0,就可以尝试支付
    if (amount > 0) {
        boolean success = payment.pay(amount);
        if (success) {
            System.out.println("支付成功");
        } else {
            System.out.println("支付失败");
        }
    }
}

processPayment(new CashPayment(), 1500);        // ✓ 成功
processPayment(new CreditCardPayment(), 1500);  // ❌ 失败(超出额度)
```

调用者按照父类的契约使用,但子类的额外限制导致行为不一致。

**重构方案**:

**方案1: 在父类契约中明确支付可能失败的原因**

```java
abstract class Payment {
    // 前置条件: amount > 0
    // 后置条件: 返回true表示成功,false表示失败(余额不足、额度不足等)
    // 说明: 子类可以有各种失败原因,只要返回false即可
    public abstract boolean pay(double amount);
}
```

这样 `CreditCardPayment` 的行为就符合契约了:输入合法(amount > 0),只是因为业务原因(超出额度)返回false。

**方案2: 将额度检查移到外部**

```java
class CreditCardPayment extends Payment {
    private double limit = 1000.0;
    
    public boolean canPay(double amount) {
        return amount > 0 && amount <= limit;
    }
    
    @Override
    public boolean pay(double amount) {
        if (amount <= 0) return false;
        // 实际支付逻辑,假设信用卡系统会检查额度
        return performPayment(amount);
    }
}

// 调用者的责任
if (creditCard.canPay(amount)) {
    creditCard.pay(amount);
}
```

**结论**: 方案1更符合实际情况,因为支付失败是正常的业务场景。关键是父类的契约要定义清楚。
</details>

---

## 三、设计题

### 6. 设计图形计算系统

你正在设计一个图形计算系统,需要支持各种形状(矩形、圆形、三角形等)的面积和周长计算。

**需求**:
1. 支持多种形状
2. 能够方便地添加新形状
3. 符合里氏替换原则

**问题**: 请设计类的层次结构,确保符合LSP。

<details>
<summary>参考答案</summary>

**设计方案**: 使用接口定义契约,避免错误的继承关系

```java
// ========== 核心接口 ==========

/**
 * Shape接口 - 定义所有形状的共同契约
 */
interface Shape {
    /**
     * 计算面积
     * @return 面积值,必须 >= 0
     */
    double getArea();
    
    /**
     * 计算周长
     * @return 周长值,必须 >= 0
     */
    double getPerimeter();
    
    /**
     * 获取形状描述
     * @return 非null的描述字符串
     */
    String getDescription();
}

// ========== 具体实现 ==========

/**
 * 矩形实现
 * 契约: width > 0, height > 0
 */
class Rectangle implements Shape {
    private final double width;
    private final double height;
    
    public Rectangle(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("宽度和高度必须大于0");
        }
        this.width = width;
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
    public String getDescription() {
        return String.format("矩形[%.2f x %.2f]", width, height);
    }
}

/**
 * 正方形实现 - 不继承Rectangle
 * 契约: side > 0
 */
class Square implements Shape {
    private final double side;
    
    public Square(double side) {
        if (side <= 0) {
            throw new IllegalArgumentException("边长必须大于0");
        }
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
    public String getDescription() {
        return String.format("正方形[%.2f]", side);
    }
}

/**
 * 圆形实现
 * 契约: radius > 0
 */
class Circle implements Shape {
    private final double radius;
    
    public Circle(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("半径必须大于0");
        }
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
    public String getDescription() {
        return String.format("圆形[r=%.2f]", radius);
    }
}

/**
 * 三角形实现
 * 契约: 三边必须满足三角形不等式
 */
class Triangle implements Shape {
    private final double a, b, c;
    
    public Triangle(double a, double b, double c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new IllegalArgumentException("边长必须大于0");
        }
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new IllegalArgumentException("不满足三角形不等式");
        }
        this.a = a;
        this.b = b;
        this.c = c;
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
    public String getDescription() {
        return String.format("三角形[%.2f, %.2f, %.2f]", a, b, c);
    }
}

// ========== 客户端代码 ==========

/**
 * 形状计算器 - 依赖Shape接口
 */
class ShapeCalculator {
    /**
     * 计算总面积 - 所有Shape实现都可以替换使用
     */
    public double calculateTotalArea(List<Shape> shapes) {
        double total = 0;
        for (Shape shape : shapes) {
            total += shape.getArea();  // 多态调用
        }
        return total;
    }
    
    /**
     * 打印形状信息
     */
    public void printShapeInfo(Shape shape) {
        System.out.println(shape.getDescription());
        System.out.println("  面积: " + shape.getArea());
        System.out.println("  周长: " + shape.getPerimeter());
    }
}

// 使用示例
public class ShapeDemo {
    public static void main(String[] args) {
        List<Shape> shapes = Arrays.asList(
            new Rectangle(5, 4),
            new Square(5),
            new Circle(3),
            new Triangle(3, 4, 5)
        );
        
        ShapeCalculator calculator = new ShapeCalculator();
        
        // 所有Shape实现都可以安全替换
        for (Shape shape : shapes) {
            calculator.printShapeInfo(shape);
        }
        
        System.out.println("总面积: " + calculator.calculateTotalArea(shapes));
    }
}
```

**设计要点**:

1. **接口定义契约**: `Shape` 接口明确规定了所有形状必须提供的行为
2. **平行实现**: `Rectangle` 和 `Square` 都实现 `Shape`,没有继承关系
3. **不可变设计**: 所有属性都是 `final`,避免状态变化导致的契约冲突
4. **前置条件验证**: 在构造函数中验证输入
5. **后置条件保证**: 所有方法都返回合法值(>= 0)
6. **可替换性**: 任何 `Shape` 实现都可以在客户端代码中互换

**符合LSP的证据**:
- ✅ 所有实现都遵守 `Shape` 接口的契约
- ✅ 可以安全地替换使用
- ✅ 多态调用可靠
- ✅ 易于扩展(新增形状只需实现接口)
</details>

---

## 四、判断题

### 7. "使用不可变对象就一定符合里氏替换原则。"

<details>
<summary>查看答案</summary>

**答案**: ❌ 错误

**理由**: 不可变对象只是减少了状态变化带来的契约冲突,但仍需遵守LSP的其他要求。

**反例**:

```java
class ImmutableRectangle {
    private final int width;
    private final int height;
    
    public ImmutableRectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

class ImmutableSquare extends ImmutableRectangle {
    public ImmutableSquare(int side) {
        super(side, side);
    }
    
    // ❌ 虽然不可变,但仍然违反了语义契约
    // Rectangle允许宽高不同,但Square强制宽高相等
    // 如果客户端代码期望创建宽高不同的矩形,传入Square就会出问题
}
```

**正确理解**:
- 不可变对象**更容易**遵循LSP(因为没有setter导致的契约冲突)
- 但不可变不是LSP的充分条件
- 仍需考虑:
  - 方法的前置条件、后置条件
  - 不变式约束
  - 语义契约

**结论**: 不可变是好的实践,但不能代替对LSP的全面理解和应用。
</details>

---

### 8. "如果子类抛出了父类未声明的checked异常,就违反了LSP。"

<details>
<summary>查看答案</summary>

**答案**: ✅ 正确

**理由**: 抛出父类未声明的checked异常违反了后置条件。

**契约分析**:

```java
class Parent {
    // 契约: 不抛出checked异常
    public void process(String data) {
        System.out.println("处理: " + data);
    }
}

class Child extends Parent {
    @Override
    public void process(String data) throws IOException {  // ❌ 编译错误!
        // Java不允许这样做,因为违反了契约
    }
}
```

**Java的保护机制**:
- Java编译器会阻止子类抛出父类未声明的checked异常
- 这是Java语言层面对LSP的保护

**但运行时异常不受限制**:

```java
class Parent {
    public void process(String data) {
        System.out.println("处理: " + data);
    }
}

class Child extends Parent {
    @Override
    public void process(String data) {
        if (data == null) {
            throw new NullPointerException();  // ⚠️ 可以编译,但违反LSP
        }
        System.out.println("处理: " + data);
    }
}
```

**LSP的要求**:
- 子类不应该抛出父类未声明的**任何**异常(包括运行时异常)
- 如果父类方法不抛异常,子类也不应该抛
- 这是后置条件"不能弱化"的体现

**实践建议**:
1. 子类只能抛出父类已声明的异常或其子类
2. 最好不要在子类中引入新的异常类型
3. 如果必须处理异常,在子类内部catch并处理,不要抛给调用者
</details>

---

## 总分统计

- **选择题**（1-3题）：每题 10 分,共 30 分
- **代码分析题**（4-5题）：每题 20 分,共 40 分
- **设计题**（6题）：20 分
- **判断题**（7-8题）：每题 5 分,共 10 分

**总分**: 100 分  
**及格线**: 70 分

---

## 学习建议

- ✅ 如果得分 ≥ 70分：恭喜！可以继续学习下一个原则
- ⚠️ 如果得分 50-69分：重新阅读 doc_01.md,重点理解"契约式设计"
- ❌ 如果得分 < 50分：建议再运行一次 demo 代码,深入理解 Rectangle-Square 问题

记住：**子类必须能够替换父类,而不改变程序的正确性！**
