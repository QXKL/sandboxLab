# 装饰器模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 45 分）

### 1. 装饰器模式的主要目的是什么？

A. 将一个类的接口转换成客户希望的另一个接口  
B. 动态地给一个对象添加额外的职责  
C. 控制对对象的访问  
D. 为子系统中的一组接口提供统一的接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 装饰器模式的核心目的是**动态地给对象添加额外的职责**，在不修改原有代码的情况下扩展功能。

**关键词**：
- **动态**：运行时添加功能
- **额外职责**：增强功能
- **不修改原有代码**：符合开闭原则

**形象类比**：
- 给咖啡加料：咖啡本质不变，功能增强（味道更丰富）
- 手机套保护壳：手机功能不变，附加功能增加（保护、美观）

**与其他模式的区别**：
- **A选项**：适配器模式（转换接口）
- **B选项**：✅ 装饰器模式（增强功能）
- **C选项**：代理模式（控制访问）
- **D选项**：外观模式（简化接口）
</details>

---

### 2. 装饰器模式和适配器模式的主要区别是什么？

A. 装饰器改变接口，适配器增强功能  
B. 装饰器增强功能，适配器转换接口  
C. 两者没有区别，只是名称不同  
D. 装饰器用于单个对象，适配器用于多个对象

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**装饰器模式**：
- **目的**：增强功能
- **接口**：保持不变
- **例子**：给咖啡加料

```java
Coffee coffee = new Americano();          // Coffee接口
coffee = new Milk(coffee);                // 还是Coffee接口
coffee.getPrice();                        // 功能增强（价格变了）
```

**适配器模式**：
- **目的**：转换接口
- **接口**：改变
- **例子**：插座转换器

```java
AlipaySDK alipay = new AlipaySDK();       // AlipaySDK接口
PaymentProcessor payment = 
    new AlipayAdapter(alipay);            // 转换为PaymentProcessor接口
payment.processPayment(...);              // 接口改变了
```

**对比表格**：

| 对比 | 装饰器模式 | 适配器模式 |
|-----|-----------|-----------|
| 目的 | 增强功能 | 转换接口 |
| 接口 | 保持不变 | 改变 |
| 例子 | 给咖啡加料 | 插座转换器 |

**记忆口诀**：
> **装饰器加功能，**  
> **适配器转接口。**
</details>

---

### 3. 以下哪个场景最适合使用装饰器模式？

A. 你需要使用一个第三方库，但它的接口与你的系统不兼容  
B. 你需要在运行时动态地给对象添加不同的功能组合  
C. 你需要控制对某个对象的访问权限  
D. 你需要为复杂的子系统提供一个简单的接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**B正确** - 运行时动态添加功能组合：

```java
// 咖啡店系统：用户可以自由选择配料
Coffee coffee = new Americano();

// 根据用户选择动态添加
if (wantMilk) {
    coffee = new Milk(coffee);
}
if (wantSugar) {
    coffee = new Sugar(coffee);
}
if (wantMocha) {
    coffee = new Mocha(coffee);
}
```

**优势**：
- ✅ 灵活：运行时动态组合
- ✅ 避免类爆炸：不需要为每种组合创建子类
- ✅ 符合开闭原则：新增配料不修改现有代码

**其他选项**：
- **A**：第三方库接口不兼容 → **适配器模式**
- **C**：控制访问权限 → **代理模式**
- **D**：简化复杂子系统接口 → **外观模式**

**判断标准**：
```
需要扩展对象功能？
  ├─ 功能固定？ → 继承
  ├─ 功能可变、可组合？ → ✅ 装饰器模式
  ├─ 接口需要改变？ → 适配器模式
  └─ 需要控制访问？ → 代理模式
```
</details>

---

## 二、代码分析（每题 20 分，共 40 分）

### 4. 以下代码有什么问题？如何改进？

```java
// 咖啡接口
interface Coffee {
    double getPrice();
}

// 美式咖啡
class Americano implements Coffee {
    public double getPrice() { return 15.0; }
}

// 加牛奶的美式咖啡
class AmericanoWithMilk extends Americano {
    public double getPrice() { return 17.0; }
}

// 加牛奶和糖的美式咖啡
class AmericanoWithMilkAndSugar extends Americano {
    public double getPrice() { return 18.0; }
}

// 加牛奶、糖、摩卡的美式咖啡
class AmericanoWithMilkSugarMocha extends Americano {
    public double getPrice() { return 21.0; }
}
```

<details>
<summary>参考答案</summary>

**问题**: 使用继承实现配料组合，导致类爆炸

**问题分析**：
1. ❌ **类爆炸**：2种咖啡 × 8种配料组合 = 16个类
2. ❌ **不灵活**：无法运行时动态选择配料
3. ❌ **违反开闭原则**：新增配料需要修改代码
4. ❌ **重复代码**：相似逻辑在多个类中重复

**改进方案**：使用装饰器模式

```java
// 1. 咖啡接口
interface Coffee {
    double getPrice();
    String getDescription();
}

// 2. 具体组件：美式咖啡
class Americano implements Coffee {
    @Override
    public double getPrice() {
        return 15.0;
    }
    
    @Override
    public String getDescription() {
        return "美式咖啡";
    }
}

// 3. 抽象装饰器
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;  // 持有被装饰的咖啡
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    
    @Override
    public double getPrice() {
        return coffee.getPrice();
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription();
    }
}

// 4. 具体装饰器：牛奶
class Milk extends CoffeeDecorator {
    public Milk(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double getPrice() {
        return coffee.getPrice() + 2.0;  // 原价格 + 牛奶价格
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + " + 牛奶";
    }
}

// 5. 具体装饰器：糖
class Sugar extends CoffeeDecorator {
    public Sugar(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double getPrice() {
        return coffee.getPrice() + 1.0;
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + " + 糖";
    }
}

// 6. 具体装饰器：摩卡
class Mocha extends CoffeeDecorator {
    public Mocha(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double getPrice() {
        return coffee.getPrice() + 3.0;
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + " + 摩卡";
    }
}

// 7. 使用
Coffee coffee = new Americano();
coffee = new Milk(coffee);
coffee = new Sugar(coffee);
coffee = new Mocha(coffee);

System.out.println(coffee.getDescription());  // 美式咖啡 + 牛奶 + 糖 + 摩卡
System.out.println(coffee.getPrice());        // 15 + 2 + 1 + 3 = 21元
```

**优势**：
1. ✅ **灵活组合**：运行时动态添加功能
2. ✅ **符合开闭原则**：新增配料不修改现有代码
3. ✅ **避免类爆炸**：3种咖啡 + 4种配料 = 7个类
4. ✅ **单一职责**：每个装饰器只负责一种配料

**对比**：
```
继承方式：
  3种咖啡 × 2^4种配料组合 = 48个类 ❌

装饰器方式：
  3种咖啡 + 4种配料 = 7个类 ✅
```
</details>

---

### 5. 实现日志装饰器

要求：
- 基础接口：`DataSource`，有`readData()`和`writeData()`方法
- 具体组件：`FileDataSource`（文件数据源）
- 装饰器：`LoggingDecorator`（记录每次读写操作的日志）
- 装饰器：`EncryptionDecorator`（加密/解密数据）

<details>
<summary>参考答案</summary>

```java
// 1. 组件接口
interface DataSource {
    void writeData(String data);
    String readData();
}

// 2. 具体组件：文件数据源
class FileDataSource implements DataSource {
    private String filename;
    private String data = "";

    public FileDataSource(String filename) {
        this.filename = filename;
    }

    @Override
    public void writeData(String data) {
        this.data = data;
        System.out.println("[文件] 写入数据到 " + filename);
    }

    @Override
    public String readData() {
        System.out.println("[文件] 从 " + filename + " 读取数据");
        return data;
    }
}

// 3. 抽象装饰器
abstract class DataSourceDecorator implements DataSource {
    protected DataSource dataSource;

    public DataSourceDecorator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void writeData(String data) {
        dataSource.writeData(data);
    }

    @Override
    public String readData() {
        return dataSource.readData();
    }
}

// 4. 具体装饰器：日志装饰器
class LoggingDecorator extends DataSourceDecorator {
    public LoggingDecorator(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void writeData(String data) {
        System.out.println("[日志] 准备写入数据: " + data);
        dataSource.writeData(data);
        System.out.println("[日志] 数据写入完成");
    }

    @Override
    public String readData() {
        System.out.println("[日志] 准备读取数据");
        String result = dataSource.readData();
        System.out.println("[日志] 数据读取完成: " + result);
        return result;
    }
}

// 5. 具体装饰器：加密装饰器
class EncryptionDecorator extends DataSourceDecorator {
    public EncryptionDecorator(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void writeData(String data) {
        String encrypted = encrypt(data);
        System.out.println("[加密] 数据已加密");
        dataSource.writeData(encrypted);
    }

    @Override
    public String readData() {
        String data = dataSource.readData();
        String decrypted = decrypt(data);
        System.out.println("[加密] 数据已解密");
        return decrypted;
    }

    private String encrypt(String data) {
        // 简单加密：Base64编码
        return java.util.Base64.getEncoder().encodeToString(data.getBytes());
    }

    private String decrypt(String data) {
        // 简单解密：Base64解码
        return new String(java.util.Base64.getDecoder().decode(data));
    }
}

// 6. 使用
DataSource dataSource = new FileDataSource("data.txt");

// 添加日志功能
dataSource = new LoggingDecorator(dataSource);

// 添加加密功能
dataSource = new EncryptionDecorator(dataSource);

// 写入数据
dataSource.writeData("Hello World");

// 读取数据
String data = dataSource.readData();
System.out.println("最终数据: " + data);
```

**输出示例**：
```
[日志] 准备写入数据: Hello World
[加密] 数据已加密
[文件] 写入数据到 data.txt
[日志] 数据写入完成

[日志] 准备读取数据
[文件] 从 data.txt 读取数据
[加密] 数据已解密
[日志] 数据读取完成: Hello World
最终数据: Hello World
```

**关键点**：
- ✅ 接口统一（都是DataSource）
- ✅ 功能独立（日志、加密各自独立）
- ✅ 可以组合（可以只要日志，或只要加密，或两者都要）
- ✅ 装饰顺序影响结果（先加密再记日志 vs 先记日志再加密）
</details>

---

## 三、场景判断（15 分）

### 6. 判断以下场景是否适合装饰器模式

**场景A**: Java I/O流需要添加缓冲、数据类型读取等功能  
**场景B**: 支付系统需要适配不同的第三方支付SDK  
**场景C**: UI组件需要动态添加滚动条、边框、阴影等装饰  
**场景D**: 远程对象访问需要添加权限检查和懒加载

<details>
<summary>参考答案</summary>

### 场景A: Java I/O流
**推荐**: ✅ 非常适合装饰器模式

**理由**：
- 需要动态添加功能（缓冲、数据类型读取）
- 功能可以自由组合
- 避免类爆炸

```java
InputStream in = new FileInputStream("file.txt");
in = new BufferedInputStream(in);    // 添加缓冲
in = new DataInputStream(in);        // 添加数据类型读取
```

**真实应用**：Java I/O就是这么设计的！

---

### 场景B: 支付系统适配
**推荐**: ❌ 不适合装饰器模式，应该用**适配器模式**

**理由**：
- 不是功能增强问题
- 是接口转换问题
- 接口前后不同

```java
// 适配器模式
PaymentProcessor payment = new AlipayAdapter(new AlipaySDK());
payment.processPayment(...);  // 接口转换
```

---

### 场景C: UI组件装饰
**推荐**: ✅ 非常适合装饰器模式

**理由**：
- 需要动态添加装饰（滚动条、边框、阴影）
- 装饰可以组合
- 保持组件接口不变

```java
Component panel = new JPanel();
panel = new ScrollDecorator(panel);
panel = new BorderDecorator(panel);
panel = new ShadowDecorator(panel);
```

---

### 场景D: 远程对象访问
**推荐**: ❌ 不适合装饰器模式，应该用**代理模式**

**理由**：
- 不是功能增强
- 是访问控制（权限检查、懒加载）
- 代理模式更合适

```java
// 代理模式
Service service = new ServiceProxy(new RemoteService());
service.execute();  // 代理控制访问
```

---

### 总结

**适合装饰器模式**：
- ✅ 场景A：Java I/O流（动态添加功能）
- ✅ 场景C：UI组件装饰（动态添加装饰）

**不适合装饰器模式**：
- ❌ 场景B：支付系统适配（用适配器模式）
- ❌ 场景D：远程对象访问（用代理模式）

**判断标准**：
```
需要扩展对象功能？
  ├─ 功能可变、可组合？ → ✅ 装饰器模式
  ├─ 接口需要改变？ → 适配器模式
  └─ 需要控制访问？ → 代理模式
```
</details>

---

## 四、评分标准

### 满分答案特征
- ✅ 理解装饰器模式的本质（动态增强功能）
- ✅ 掌握装饰器 vs 适配器 vs 代理的区别
- ✅ 能手写装饰器代码
- ✅ 能准确判断使用场景
- ✅ 理解装饰器的优缺点

### 常见扣分点
- ❌ 混淆装饰器和适配器
- ❌ 不知道装饰器要保持接口一致
- ❌ 无法识别动态功能增强场景
- ❌ 装饰器实现不完整
- ❌ 场景判断错误

---

## 核心要点回顾

### 装饰器模式四要素
1. **Component**：组件接口
2. **ConcreteComponent**：具体组件
3. **Decorator**：抽象装饰器（持有Component）
4. **ConcreteDecorator**：具体装饰器（增强功能）

### 装饰器 vs 其他模式

| 模式 | 目的 | 接口 | 例子 |
|-----|------|------|------|
| **装饰器** | 增强功能 | 保持不变 | 给咖啡加料 |
| **适配器** | 转换接口 | 改变 | 插座转换器 |
| **代理** | 控制访问 | 保持不变 | 门卫 |

### 记忆口诀
> **咖啡加料真灵活，**  
> **装饰器来把功能拓，**  
> **接口保持是关键，**  
> **动态组合不用说。**

---

**完成自测后**，填写 `note_template.md` 巩固知识！
