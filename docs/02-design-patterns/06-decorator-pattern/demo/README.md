# 装饰器模式 - 代码示例

## 示例说明

本目录包含装饰器模式的三个核心示例：

1. **CoffeeDecoratorDemo.java** - 咖啡装饰器
2. **TextDecoratorDemo.java** - 文本装饰器
3. **IoStreamDemo.java** - I/O流装饰器

---

## 运行方式

```bash
# 进入demo目录
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\06-decorator-pattern\demo

# 编译
javac CoffeeDecoratorDemo.java
javac TextDecoratorDemo.java
javac IoStreamDemo.java

# 运行
java CoffeeDecoratorDemo
java TextDecoratorDemo
java IoStreamDemo
```

---

## 示例1：CoffeeDecoratorDemo.java

### 场景
咖啡店点单系统，支持动态添加配料

### 演示内容
- 基础咖啡 + 动态添加配料
- 价格累加、描述拼接
- 装饰器 vs 继承对比
- 递归调用过程

### 核心要点

**问题：如果用继承实现配料组合**
```java
// ❌ 类爆炸
class Americano extends Coffee { }
class AmericanoWithMilk extends Americano { }
class AmericanoWithMilkAndSugar extends Americano { }
class AmericanoWithMilkSugarMocha extends Americano { }
// 3种咖啡 × 2^4种配料组合 = 48个类
```

**解决：用装饰器模式**
```java
// ✅ 灵活组合
Coffee coffee = new Americano();
coffee = new Milk(coffee);    // 加牛奶
coffee = new Sugar(coffee);   // 加糖
coffee = new Mocha(coffee);   // 加摩卡
// 3种咖啡 + 4种配料 = 7个类
```

**递归调用过程**：
```
coffee.getPrice() 的调用链：
  Mocha.getPrice()
    → Sugar.getPrice() + 3.0
      → Milk.getPrice() + 1.0
        → Americano.getPrice() + 2.0
          → 15.0
  最终结果: 15 + 2 + 1 + 3 = 21元
```

---

## 示例2：TextDecoratorDemo.java

### 场景
文本编辑器，支持加粗、斜体、下划线等格式

### 演示内容
- 多层装饰嵌套
- 装饰顺序的影响
- 可视化嵌套结构（俄罗斯套娃）

### 核心要点

**装饰器嵌套**：
```java
Text text = new PlainText("Hello");
text = new Bold(text);       // <b>Hello</b>
text = new Italic(text);     // <i><b>Hello</b></i>
text = new Underline(text);  // <u><i><b>Hello</b></i></u>
```

**嵌套结构（俄罗斯套娃）**：
```
Underline
  └─ Italic
       └─ Bold
            └─ PlainText("Hello")
```

**调用过程**：
```
text.render() 的执行：
  1. Underline.render() → "<u>" + text.render() + "</u>"
  2. Italic.render()    → "<i>" + text.render() + "</i>"
  3. Bold.render()      → "<b>" + text.render() + "</b>"
  4. PlainText.render() → "Hello"
  5. 回溯组装 → <u><i><b>Hello</b></i></u>
```

---

## 示例3：IoStreamDemo.java

### 场景
模拟Java I/O流的装饰器设计

### 演示内容
- BufferedInputStream、DataInputStream等的设计原理
- 装饰器顺序的重要性
- 真实世界的装饰器应用

### 核心要点

**Java I/O的装饰器结构**：
```
InputStream (Component)
  ├─ FileInputStream (ConcreteComponent)
  └─ FilterInputStream (Decorator)
       ├─ BufferedInputStream (ConcreteDecorator)
       ├─ DataInputStream (ConcreteDecorator)
       └─ PushbackInputStream (ConcreteDecorator)
```

**典型用法**：
```java
InputStream in = new FileInputStream("file.txt");
in = new BufferedInputStream(in);    // 添加缓冲功能
in = new DataInputStream(in);        // 添加读取基本类型功能
```

**装饰顺序很重要**：
```java
// ✅ 正确顺序
DataInputStream(BufferedInputStream(FileInputStream))
// 先缓冲，再读取数据类型

// ❌ 不好的顺序
BufferedInputStream(DataInputStream(FileInputStream))
// DataInputStream的缓冲效果被浪费
```

---

## 核心概念

### 1. 装饰器模式的结构

```
Component (接口)
  ├─ ConcreteComponent (具体组件)
  └─ Decorator (抽象装饰器)
       ├─ ConcreteDecoratorA (具体装饰器A)
       └─ ConcreteDecoratorB (具体装饰器B)
```

**角色说明**：
- **Component**：定义对象接口（Coffee、Text、InputStream）
- **ConcreteComponent**：具体组件，被装饰的对象（Americano、PlainText）
- **Decorator**：抽象装饰器，持有Component（CoffeeDecorator）
- **ConcreteDecorator**：具体装饰器，添加功能（Milk、Bold）

---

### 2. 装饰器的关键设计

**持有被装饰对象**：
```java
abstract class Decorator implements Component {
    protected Component component;  // 持有被装饰对象
    
    public Decorator(Component component) {
        this.component = component;
    }
}
```

**增强功能**：
```java
class Milk extends CoffeeDecorator {
    @Override
    public double getPrice() {
        return coffee.getPrice() + 2.0;  // 原价格 + 牛奶价格
    }
}
```

**递归组合**：
```java
Coffee coffee = new Americano();
coffee = new Milk(coffee);     // 第1层
coffee = new Sugar(coffee);    // 第2层
coffee = new Mocha(coffee);    // 第3层
```

---

### 3. 装饰器 vs 其他模式

| 模式 | 目的 | 接口 | 例子 |
|-----|------|------|------|
| **装饰器** | 增强功能 | 保持不变 | 给咖啡加料 |
| **适配器** | 转换接口 | 改变 | 插座转换器 |
| **代理** | 控制访问 | 保持不变 | 门卫 |

**区别要点**：
- **装饰器 vs 适配器**：装饰器增强功能，适配器转换接口
- **装饰器 vs 代理**：装饰器关注功能增强，代理关注访问控制
- **装饰器 vs 继承**：装饰器运行时动态组合，继承编译时固定

---

## 学习建议

### 学习顺序
1. **CoffeeDecoratorDemo** - 理解装饰器的基本原理
2. **TextDecoratorDemo** - 理解多层嵌套和顺序影响
3. **IoStreamDemo** - 理解真实世界的应用

### 重点理解

#### 1. 装饰器的本质
运行CoffeeDecoratorDemo，思考：
- 为什么不用继承？
- 装饰器如何避免类爆炸？
- 递归调用是如何工作的？

#### 2. 多层嵌套
运行TextDecoratorDemo，观察：
- 装饰顺序如何影响结果？
- 嵌套结构像什么？（俄罗斯套娃）
- 调用如何从外向内穿透，从内向外组装？

#### 3. 真实应用
运行IoStreamDemo，理解：
- Java I/O为什么这么设计？
- 装饰顺序为什么重要？
- 如何避免类爆炸？（10个基础流 × 5个功能 = 15个类，而非50个）

---

## 思考题

1. **装饰器 vs 继承**：
   - 为什么咖啡系统用装饰器而不是继承？
   - 何时用继承，何时用装饰器？

2. **装饰器顺序**：
   - 为什么I/O流要先BufferedInputStream再DataInputStream？
   - 装饰器顺序错误会有什么后果？

3. **装饰器的限制**：
   - 装饰器模式有什么缺点？
   - 如何避免过度装饰？

4. **真实应用**：
   - Java I/O流还有哪些装饰器？
   - Spring AOP如何使用装饰器思想？

---

## 常见问题

### Q1: 装饰器和继承有什么区别？
A: 
- **继承**：编译时确定，是"is-a"关系，不灵活
- **装饰器**：运行时动态，是"has-a"关系，灵活组合

### Q2: 装饰器和适配器有什么区别？
A: 
- **装饰器**：增强功能，接口保持不变
- **适配器**：转换接口，功能保持不变

### Q3: 装饰器顺序重要吗？
A: 重要！特别是I/O流：
- 缓冲装饰器应该靠近底层
- 功能装饰器应该在外层

### Q4: 如何避免过度装饰？
A: 
- 控制装饰层数（一般不超过3-5层）
- 合并相似功能
- 考虑使用工厂简化创建

---

## 扩展阅读

完成这三个示例后，建议：
1. 阅读 `doc_01.md` 了解理论细节
2. 完成 `test_01.md` 的自测题
3. 填写 `note_template.md` 巩固知识
4. 思考：为什么Java I/O要用装饰器模式？

---

## 真实应用案例

### Java标准库中的装饰器

**1. InputStream家族**
```java
InputStream in = new FileInputStream("file.txt");
in = new BufferedInputStream(in);
in = new DataInputStream(in);
```

**2. Reader/Writer**
```java
Reader reader = new FileReader("file.txt");
reader = new BufferedReader(reader);
```

**3. Collections装饰器**
```java
List<String> list = new ArrayList<>();
list = Collections.synchronizedList(list);  // 添加同步功能
list = Collections.unmodifiableList(list);  // 添加只读功能
```

---

## 装饰器的优缺点

### 优点
✅ 灵活扩展：运行时动态添加功能  
✅ 符合开闭原则：新增功能不修改原有代码  
✅ 避免类爆炸：功能组合不需要大量子类  
✅ 单一职责：每个装饰器只负责一个功能

### 缺点
❌ 多层包装复杂：嵌套太多难以理解  
❌ 顺序敏感：装饰顺序影响结果  
❌ 调试困难：多层嵌套不好调试  
❌ 小对象增多：产生很多小的装饰器对象

---

**记住**：
> **咖啡加料真灵活，**  
> **装饰器来把功能拓，**  
> **接口保持是关键，**  
> **动态组合不用说。**
