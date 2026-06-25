# 装饰器模式 (Decorator Pattern)

> 动态地给对象添加额外的职责

---

## 一、生活中的例子

### ☕ 咖啡加料

想象你在星巴克点咖啡：

```
基础咖啡（美式）
    ↓ 加牛奶
牛奶咖啡
    ↓ 加糖
牛奶糖咖啡
    ↓ 加摩卡
牛奶糖摩卡咖啡
```

**每加一样配料**：
- 咖啡本质不变（还是咖啡）
- 功能增强了（味道更丰富）
- 价格累加了

**装饰器做了什么？**
- 不改变咖啡的本质（接口保持不变）
- 动态添加新功能（加料）
- 可以灵活组合（想加什么加什么）

---

### 📱 手机壳装饰

```
手机（裸机）
    ↓ 套保护壳
手机+保护壳
    ↓ 贴钢化膜
手机+保护壳+钢化膜
    ↓ 挂挂绳
手机+保护壳+钢化膜+挂绳
```

**每加一层装饰**：
- 手机功能不变（还是手机）
- 附加功能增加（保护、美观）
- 可以随时拆卸组合

---

## 二、为什么需要装饰器模式？

### 问题场景：咖啡店系统

**需求**：咖啡店有多种咖啡和配料组合

**方式1：用继承（糟糕的设计）**

```java
class Coffee { }

class Espresso extends Coffee { }
class Americano extends Coffee { }

// 加牛奶
class EspressoWithMilk extends Espresso { }
class AmericanoWithMilk extends Americano { }

// 加牛奶和糖
class EspressoWithMilkAndSugar extends Espresso { }
class AmericanoWithMilkAndSugar extends Americano { }

// 加牛奶、糖、摩卡
class EspressoWithMilkSugarMocha extends Espresso { }
class AmericanoWithMilkSugarMocha extends Americano { }

// ...还有更多组合！
```

**问题**：
1. ❌ **类爆炸**：2种咖啡 × 8种配料组合 = 16个类
2. ❌ **不灵活**：新增配料需要修改所有类
3. ❌ **违反开闭原则**：每次扩展都要修改代码
4. ❌ **重复代码**：相似逻辑在多个类中重复

---

**方式2：用装饰器模式（优雅的设计）**

```java
// 基础咖啡
Coffee coffee = new Espresso();

// 动态添加配料
coffee = new Milk(coffee);        // 加牛奶
coffee = new Sugar(coffee);       // 加糖
coffee = new Mocha(coffee);       // 加摩卡

// 计算价格
double price = coffee.getPrice();  // 自动累加
```

**优势**：
1. ✅ **灵活组合**：运行时动态添加功能
2. ✅ **符合开闭原则**：新增配料不修改现有代码
3. ✅ **避免类爆炸**：配料独立，可任意组合
4. ✅ **单一职责**：每个装饰器只负责一种配料

---

## 三、装饰器模式的核心思想

### 定义

**装饰器模式（Decorator Pattern）**：动态地给一个对象添加一些额外的职责。就增加功能来说，装饰器模式比生成子类更为灵活。

### 核心要点

| 要点 | 说明 |
|-----|------|
| **动态扩展** | 运行时添加功能，而非编译时 |
| **保持接口** | 装饰前后接口相同 |
| **透明包装** | 客户端无需知道对象是否被装饰 |
| **递归组合** | 装饰器可以嵌套装饰器 |

---

### 形象理解

```
装饰器就像"俄罗斯套娃"：

核心对象（最里层）
  ↓ 包装
装饰器1
  ↓ 包装
装饰器2
  ↓ 包装
装饰器3（最外层）
```

**客户端调用最外层**：
```java
coffee.getPrice();  // 自动穿透所有装饰器，累加价格
```

---

## 四、UML结构与角色

### UML类图

```
┌──────────────────┐
│    Component     │ 抽象组件
│   (interface)    │
├──────────────────┤
│ + operation()    │
└────────▲─────────┘
         │
    ┌────┴────┐
    │         │
┌───┴────┐  ┌─┴──────────┐
│Concrete│  │  Decorator  │ 抽象装饰器
│Component│  │ (abstract)  │
└────────┘  ├─────────────┤
            │ - component │ 持有Component
            ├─────────────┤
            │ + operation()│
            └──────▲───────┘
                   │
         ┌─────────┴─────────┐
         │                   │
  ┌──────┴──────┐   ┌────────┴────────┐
  │ConcreteDecA │   │ConcreteDecB     │
  │             │   │                 │
  │+ operation()│   │+ operation()    │
  │  增强功能    │   │  增强功能       │
  └─────────────┘   └─────────────────┘
```

---

### 角色说明

| 角色 | 职责 | 类比 |
|-----|------|------|
| **Component** | 定义对象接口 | 饮料（统一接口） |
| **ConcreteComponent** | 具体组件，被装饰的对象 | 美式咖啡、浓缩咖啡 |
| **Decorator** | 抽象装饰器，持有Component | 配料（抽象） |
| **ConcreteDecorator** | 具体装饰器，添加功能 | 牛奶、糖、摩卡 |

---

### 关键设计

**1. Decorator持有Component**
```java
abstract class Decorator implements Component {
    protected Component component;  // 被装饰的对象
    
    public Decorator(Component component) {
        this.component = component;
    }
}
```

**2. 装饰器增强功能**
```java
class Milk extends Decorator {
    @Override
    public double getPrice() {
        return component.getPrice() + 2.0;  // 原价格 + 牛奶价格
    }
}
```

**3. 递归调用**
```java
Coffee coffee = new Espresso();        // 原始对象
coffee = new Milk(coffee);             // 第1层装饰
coffee = new Sugar(coffee);            // 第2层装饰
coffee = new Mocha(coffee);            // 第3层装饰

// 调用getPrice()
Mocha.getPrice()
  → Sugar.getPrice() + Mocha价格
    → Milk.getPrice() + Sugar价格
      → Espresso.getPrice() + Milk价格
        → Espresso基础价格
```

---

## 五、代码示例讲解

### 示例1：咖啡装饰器（CoffeeDecoratorDemo.java）

**场景**：咖啡店点单系统，支持动态添加配料

**核心设计**：

```java
// 组件接口
interface Coffee {
    double getPrice();
    String getDescription();
}

// 具体组件：美式咖啡
class Americano implements Coffee {
    public double getPrice() { return 15.0; }
    public String getDescription() { return "美式咖啡"; }
}

// 抽象装饰器
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

// 具体装饰器：牛奶
class Milk extends CoffeeDecorator {
    public Milk(Coffee coffee) {
        super(coffee);
    }
    
    public double getPrice() {
        return coffee.getPrice() + 2.0;  // 加2元
    }
    
    public String getDescription() {
        return coffee.getDescription() + " + 牛奶";
    }
}

// 使用
Coffee coffee = new Americano();
coffee = new Milk(coffee);
coffee = new Sugar(coffee);
coffee = new Mocha(coffee);

System.out.println(coffee.getDescription());  // 美式咖啡 + 牛奶 + 糖 + 摩卡
System.out.println(coffee.getPrice());        // 15 + 2 + 1 + 3 = 21元
```

**关键点**：
- ✅ 接口统一（都是Coffee）
- ✅ 功能增强（累加价格和描述）
- ✅ 灵活组合（想加什么加什么）

---

### 示例2：文本装饰器（TextDecoratorDemo.java）

**场景**：文本编辑器，支持加粗、斜体、下划线

**演示重点**：
- 多层装饰器嵌套
- 装饰顺序影响结果

```java
Text text = new PlainText("Hello");
text = new Bold(text);           // <b>Hello</b>
text = new Italic(text);         // <i><b>Hello</b></i>
text = new Underline(text);      // <u><i><b>Hello</b></i></u>
```

---

### 示例3：I/O流装饰器（IoStreamDemo.java）

**场景**：模拟Java I/O流的装饰器设计

**演示重点**：
- 真实世界的装饰器应用
- 理解BufferedReader的本质

```java
InputStream in = new FileInputStream("file.txt");
in = new BufferedInputStream(in);    // 添加缓冲功能
in = new DataInputStream(in);        // 添加读取基本类型功能
```

**详细内容请查看 `demo/` 目录**

---

## 六、装饰器 vs 适配器 vs 代理

### 三者对比

| 模式 | 目的 | 接口 | 对象数量 | 例子 |
|-----|------|------|---------|------|
| **装饰器** | 增强功能 | 保持不变 | 1个（嵌套） | 给咖啡加料 |
| **适配器** | 转换接口 | 改变 | 1个 | 插座转换器 |
| **代理** | 控制访问 | 保持不变 | 1个 | 门卫 |

---

### 详细对比

**1. 装饰器 vs 适配器**

```java
// 装饰器：增强功能，接口不变
Coffee coffee = new Americano();          // Coffee接口
coffee = new Milk(coffee);                // 还是Coffee接口
coffee.getPrice();                        // 功能增强（价格变了）

// 适配器：转换接口
AlipaySDK alipay = new AlipaySDK();       // AlipaySDK接口
PaymentProcessor payment = 
    new AlipayAdapter(alipay);            // 转换为PaymentProcessor接口
payment.processPayment(...);              // 接口改变了
```

**区别**：
- **装饰器**：接口前后相同，功能增强
- **适配器**：接口前后不同，做转换

---

**2. 装饰器 vs 代理**

```java
// 装饰器：增强功能
Text text = new PlainText("Hello");
text = new Bold(text);                    // 增强：加粗
text = new Italic(text);                  // 增强：斜体

// 代理：控制访问
Image image = new ImageProxy("photo.jpg");
image.display();                          // 代理控制：懒加载、权限检查
```

**区别**：
- **装饰器**：关注功能增强（加东西）
- **代理**：关注访问控制（加限制）

---

### 记忆口诀

> **装饰器加功能，**  
> **适配器转接口，**  
> **代理控访问，**  
> **三者要分清。**

---

## 七、使用场景

### ✅ 适合装饰器模式的场景

**1. 需要动态添加功能**
```
Java I/O流：
InputStream → BufferedInputStream → DataInputStream
```

**2. 避免类爆炸**
```
咖啡系统：
2种咖啡 × 8种配料组合 = 16个类 ❌
改用装饰器：2 + 8 = 10个类 ✅
```

**3. 功能可以自由组合**
```
UI组件：
Panel → ScrollPane → BorderPanel
```

**4. 需要在运行时选择功能**
```
日志系统：
Logger → FileLogger → EncryptedLogger → CompressedLogger
```

**5. 单一职责原则**
```
每个装饰器只负责一个功能：
- 加密装饰器：只负责加密
- 压缩装饰器：只负责压缩
- 缓冲装饰器：只负责缓冲
```

---

### ❌ 不适合装饰器模式的场景

**1. 功能是互斥的**
```
支付方式（只能选一种）→ 策略模式
```

**2. 装饰顺序很重要且容易出错**
```
复杂的依赖关系 → 考虑其他设计
```

**3. 接口需要改变**
```
接口转换 → 适配器模式
```

---

## 八、优缺点分析

### 优点

| 优点 | 说明 | 例子 |
|-----|------|------|
| ✅ **灵活扩展** | 运行时动态添加功能 | 咖啡随时加料 |
| ✅ **符合开闭原则** | 新增功能不修改原有代码 | 新增配料 |
| ✅ **避免类爆炸** | 功能组合不需要大量子类 | 10个类 vs 16个类 |
| ✅ **单一职责** | 每个装饰器只负责一个功能 | 加密、压缩分开 |
| ✅ **可以组合** | 装饰器可以任意嵌套 | 多层装饰 |

---

### 缺点

| 缺点 | 说明 | 解决方案 |
|-----|------|---------|
| ❌ **多层包装复杂** | 嵌套太多难以理解 | 控制装饰层数 |
| ❌ **顺序敏感** | 装饰顺序影响结果 | 文档说明 |
| ❌ **调试困难** | 多层嵌套不好调试 | 日志记录 |
| ❌ **小对象增多** | 产生很多小的装饰器对象 | 可接受的代价 |

---

## 九、注意事项与常见误区

### 陷阱1：过度装饰

```java
// ❌ 不好：装饰层数过多
Text text = new PlainText("Hello");
text = new Bold(text);
text = new Italic(text);
text = new Underline(text);
text = new StrikeThrough(text);
text = new Highlight(text);
text = new Shadow(text);
text = new Glow(text);
// ...还有10层
```

**问题**：
- 难以理解
- 调试困难
- 性能下降

**解决**：
- 控制装饰层数（一般不超过3-5层）
- 考虑合并相似功能

---

### 陷阱2：装饰顺序错误

```java
// ❌ 错误顺序
InputStream in = new DataInputStream(
    new FileInputStream("file.txt")
);
// 没有缓冲，性能差

// ✅ 正确顺序
InputStream in = new DataInputStream(
    new BufferedInputStream(
        new FileInputStream("file.txt")
    )
);
// 先缓冲，再读取数据
```

**规则**：
- **缓冲类装饰器**应该靠近底层
- **功能类装饰器**应该在外层

---

### 陷阱3：混淆装饰器和继承

```java
// ❌ 用继承（不灵活）
class MilkCoffee extends Coffee { }
class SugarCoffee extends Coffee { }
// 无法组合

// ✅ 用装饰器（灵活）
Coffee coffee = new Americano();
coffee = new Milk(coffee);
coffee = new Sugar(coffee);
// 可以自由组合
```

**何时用继承，何时用装饰器**：
- **继承**：是"is-a"关系，功能固定
- **装饰器**：是"has-a"关系，功能可变

---

### 陷阱4：忘记实现所有接口方法

```java
// ❌ 不好：只重写了部分方法
class Milk extends CoffeeDecorator {
    public double getPrice() {
        return coffee.getPrice() + 2.0;
    }
    
    // 忘记重写getDescription()了！
}
```

**解决**：
- 抽象装饰器提供默认实现
- 具体装饰器按需重写

```java
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    // 默认实现：直接转发
    public double getPrice() {
        return coffee.getPrice();
    }
    
    public String getDescription() {
        return coffee.getDescription();
    }
}
```

---

## 十、真实应用案例

### 1. Java I/O流（经典！）

**InputStream装饰器家族**：

```java
// 基础组件
InputStream in = new FileInputStream("file.txt");

// 添加缓冲功能
in = new BufferedInputStream(in);

// 添加读取基本类型功能
in = new DataInputStream(in);

// 添加对象序列化功能
in = new ObjectInputStream(in);
```

**设计分析**：
```
InputStream (Component)
  ├─ FileInputStream (ConcreteComponent)
  └─ FilterInputStream (Decorator)
       ├─ BufferedInputStream (ConcreteDecorator)
       ├─ DataInputStream (ConcreteDecorator)
       └─ PushbackInputStream (ConcreteDecorator)
```

---

### 2. Reader/Writer装饰器

```java
// 基础字符流
Reader reader = new FileReader("file.txt");

// 添加缓冲功能
reader = new BufferedReader(reader);

// 使用
String line = ((BufferedReader) reader).readLine();
```

---

### 3. Servlet Filter（Web开发）

```java
// 过滤器链就是装饰器模式
FilterChain chain = ...;

// 第1层：字符编码过滤器
chain.doFilter(request, response);  // EncodingFilter

// 第2层：日志过滤器
chain.doFilter(request, response);  // LoggingFilter

// 第3层：权限过滤器
chain.doFilter(request, response);  // AuthFilter
```

---

### 4. Spring AOP

**AOP本质是装饰器**：

```java
// 原始对象
UserService userService = new UserServiceImpl();

// 添加事务管理
userService = new TransactionProxy(userService);

// 添加日志
userService = new LoggingProxy(userService);

// 添加权限检查
userService = new SecurityProxy(userService);
```

---

### 5. UI组件装饰

**Swing组件**：

```java
// 基础面板
JPanel panel = new JPanel();

// 添加滚动条
JScrollPane scrollPane = new JScrollPane(panel);

// 添加边框
JPanel bordered = new JPanel();
bordered.setBorder(BorderFactory.createLineBorder(Color.BLACK));
```

---

## 十一、装饰器模式的变体

### 1. 半透明装饰器

**透明装饰器**：装饰后仍是原接口，客户端无感知
```java
Coffee coffee = new Milk(new Americano());
coffee.getPrice();  // 只能调用Coffee的方法
```

**半透明装饰器**：装饰器添加新方法
```java
Milk milk = new Milk(new Americano());
milk.getPrice();        // Coffee的方法
milk.getMilkType();     // Milk新增的方法
```

**使用场景**：需要访问装饰器特有方法

---

### 2. 装饰器工厂

**问题**：手动嵌套装饰器麻烦

```java
// ❌ 手动嵌套
Coffee coffee = new Mocha(
    new Sugar(
        new Milk(
            new Americano()
        )
    )
);
```

**解决**：使用工厂简化

```java
// ✅ 工厂创建
Coffee coffee = CoffeeFactory.create(
    CoffeeType.AMERICANO,
    Arrays.asList(Topping.MILK, Topping.SUGAR, Topping.MOCHA)
);
```

---

## 十二、总结

### 核心要点

| 要点 | 内容 |
|-----|------|
| **定义** | 动态地给对象添加额外的职责 |
| **目的** | 在不修改原有代码的情况下扩展功能 |
| **关键设计** | 装饰器持有被装饰对象，实现相同接口 |
| **优势** | 灵活、符合开闭原则、避免类爆炸 |
| **典型应用** | Java I/O流、Servlet Filter、Spring AOP |

---

### 何时使用？

**判断标准**：
```
需要扩展对象功能？
  ├─ 功能固定？ → 继承
  ├─ 功能可变、可组合？ → ✅ 装饰器模式
  ├─ 接口需要改变？ → 适配器模式
  └─ 需要控制访问？ → 代理模式
```

---

### 与其他模式的关系

**装饰器 + 工厂**：
```java
// 工厂创建装饰器链
CoffeeFactory.createDecoratedCoffee(...)
```

**装饰器 + 策略**：
```java
// 装饰器增强功能，策略选择算法
Payment payment = new LoggingDecorator(
    new AlipayStrategy()
);
```

---

### 记忆口诀

> **咖啡加料真灵活，**  
> **装饰器来把功能拓，**  
> **接口保持是关键，**  
> **动态组合不用说。**

---

### 装饰器 vs 其他模式

> **装饰器加功能，**  
> **适配器转接口，**  
> **代理控访问，**  
> **继承不灵活。**

---

## 下一步

完成装饰器模式学习后：
1. ✅ 运行 `demo/` 目录下的代码示例
2. ✅ 完成 `test_01.md` 的自测题
3. ✅ 填写 `note_template.md` 巩固知识
4. ✅ 思考：Java I/O为什么这么设计？

**继续学习**：下一个结构型模式 → **代理模式**

---

**💡 记住**：装饰器模式不改变接口，只增强功能。就像给咖啡加料，本质还是咖啡。
