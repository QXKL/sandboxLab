# 适配器模式 - 代码示例

## 示例说明

本目录包含适配器模式的三个核心示例：

1. **PaymentAdapterDemo.java** - 支付系统适配器
2. **MediaPlayerDemo.java** - 媒体播放器适配器
3. **ClassVsObjectAdapterDemo.java** - 类适配器 vs 对象适配器

---

## 运行方式

```bash
# 进入demo目录
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\05-adapter-pattern\demo

# 编译
javac PaymentAdapterDemo.java
javac MediaPlayerDemo.java
javac ClassVsObjectAdapterDemo.java

# 运行
java PaymentAdapterDemo
java MediaPlayerDemo
java ClassVsObjectAdapterDemo
```

---

## 示例1：PaymentAdapterDemo.java

### 场景
统一支付接口，适配不同的第三方支付SDK

### 演示内容
- 接口转换（方法名、参数类型、返回值）
- 单位转换（元 ↔ 分）
- 统一接口的优势

### 核心要点

**适配前：三种不同的接口**
```java
// 支付宝：pay(String orderId, int cents)
AlipaySDK.pay("12345", 9999);  // 单位：分

// 微信：doPay(long orderNumber, double yuan)
WeChatPaySDK.doPay(12345L, 99.99);  // 单位：元

// 银联：payByUnionPay(String order, String amount)
UnionPaySDK.payByUnionPay("12345", "99.99");  // 字符串
```

**适配后：统一的接口**
```java
// 统一接口：processPayment(String orderId, double amount)
PaymentProcessor alipay = new AlipayAdapter(new AlipaySDK());
alipay.processPayment("12345", 99.99);

PaymentProcessor wechat = new WeChatPayAdapter(new WeChatPaySDK());
wechat.processPayment("12345", 99.99);
```

**适配器转换的内容**：
1. 方法名转换
2. 参数类型转换
3. 单位转换（元 ↔ 分）
4. 返回值转换（String/int → boolean）

---

## 示例2：MediaPlayerDemo.java

### 场景
媒体播放器适配：原生支持MP3，通过适配器支持VLC和MP4

### 演示内容
- 原生功能 + 适配器扩展
- 统一的客户端接口
- 符合开闭原则

### 核心要点

**流程图**：
```
AudioPlayer
  ├─ mp3 → 直接播放（原生支持）
  ├─ vlc → MediaAdapter → VLCPlayer
  └─ mp4 → MediaAdapter → MP4Player
```

**优势**：
- AudioPlayer无需修改，通过适配器透明扩展
- 增加新格式只需添加新适配器
- 符合开闭原则（对扩展开放，对修改关闭）

---

## 示例3：ClassVsObjectAdapterDemo.java

### 场景
USB-C适配为USB-A接口

### 演示内容
- 对象适配器（组合）实现
- 类适配器（继承）实现
- 两种方式的优缺点对比

### 核心要点

**对象适配器（推荐✅）**：
```java
class Adapter implements Target {
    private Adaptee adaptee;  // 组合
    
    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public void request() {
        adaptee.specificRequest();
    }
}
```

**类适配器（少用⚠️）**：
```java
class Adapter extends Adaptee implements Target {
    @Override
    public void request() {
        specificRequest();  // 直接调用父类方法
    }
}
```

**对比表格**：

| 对比维度 | 对象适配器✅ | 类适配器⚠️ |
|---------|------------|-----------|
| 实现方式 | 组合 | 继承 |
| 灵活性 | 高 | 低 |
| 适配多个类 | ✅ 可以 | ❌ 不行（单继承） |
| 代码量 | 稍多 | 简洁 |
| 推荐度 | ⭐⭐⭐⭐⭐ | ⭐⭐ |

---

## 核心概念

### 1. 适配器模式的角色

```
┌─────────┐
│ Client  │ 使用
└────┬────┘
     │ 依赖
     ↓
┌─────────┐
│ Target  │ 目标接口
│(接口)   │
└────▲────┘
     │ 实现
┌────┴────┐
│ Adapter │ 适配器
│ - adaptee│ 持有被适配者
└────┬────┘
     │ 组合
     ↓
┌─────────┐
│ Adaptee │ 被适配者
└─────────┘
```

---

### 2. 适配器做了什么？

**转换内容**：
1. **方法名**：`pay()` → `processPayment()`
2. **参数类型**：`int` → `double`
3. **参数数量**：2个 → 3个
4. **单位**：分 ↔ 元
5. **返回值**：`String` → `boolean`

**形象类比**：
```
你的充电器（国标三脚） → 插座转换器 → 美国插座（两脚）
                      (适配器)
```

---

### 3. 何时使用适配器？

**✅ 适合的场景**：
1. 系统集成（老系统 + 新系统）
2. 第三方库接口不兼容
3. 复用旧代码但不能修改
4. 统一接口标准

**❌ 不适合的场景**：
1. 能直接修改代码
2. 接口差异过大
3. 只是简单转发

---

## 学习建议

### 学习顺序
1. **PaymentAdapterDemo** - 理解接口转换
2. **MediaPlayerDemo** - 理解原生 + 适配器扩展
3. **ClassVsObjectAdapterDemo** - 理解两种实现方式

### 重点理解

#### 1. 适配器转换什么？
运行PaymentAdapterDemo，观察：
- 方法名转换
- 参数类型转换
- 单位转换（元 ↔ 分）
- 返回值转换

#### 2. 统一接口的价值
运行MediaPlayerDemo，思考：
- 客户端代码是否需要修改？
- 增加新格式是否容易？
- 是否符合开闭原则？

#### 3. 组合 vs 继承
运行ClassVsObjectAdapterDemo，对比：
- 对象适配器：灵活、符合组合复用原则
- 类适配器：简洁但有单继承限制

---

## 思考题

1. **接口转换**：
   - 适配器可以转换哪些内容？
   - 如果接口差异太大怎么办？

2. **对象适配器 vs 类适配器**：
   - 为什么推荐对象适配器？
   - 什么情况下类适配器更好？

3. **适配器 vs 装饰器**：
   - 适配器改变接口，装饰器增强功能
   - 如何区分两者？

4. **真实案例**：
   - SLF4J如何适配Log4j/Logback？
   - JDBC如何适配不同数据库？

---

## 常见问题

### Q1: 适配器一定比直接修改好吗？
A: 不一定。如果能修改被适配者，直接修改更简单。适配器适用于**不能修改**的场景。

### Q2: 一个适配器可以适配多个类吗？
A: 可以，但不推荐。一个适配器适配一个类更清晰。

### Q3: 适配器和装饰器有什么区别？
A: 
- **适配器**：改变接口（让不兼容的接口能合作）
- **装饰器**：增强功能（保持接口不变）

### Q4: 什么时候用对象适配器，什么时候用类适配器？
A: 
- **优先用对象适配器**（组合）
- 类适配器只在代码极简单且确定只适配一个类时使用

---

## 扩展阅读

完成这三个示例后，建议：
1. 阅读 `doc_01.md` 了解理论细节
2. 完成 `test_01.md` 的自测题
3. 填写 `note_template.md` 巩固知识
4. 思考：你的项目中哪里可以用适配器？

---

## 真实应用

### Java标准库中的适配器

1. **InputStreamReader**
   ```java
   // 将字节流适配为字符流
   InputStream in = new FileInputStream("file.txt");
   Reader reader = new InputStreamReader(in, "UTF-8");
   ```

2. **Arrays.asList()**
   ```java
   // 将数组适配为List
   String[] array = {"A", "B", "C"};
   List<String> list = Arrays.asList(array);
   ```

3. **Collections.enumeration()**
   ```java
   // 将Collection适配为Enumeration
   List<String> list = new ArrayList<>();
   Enumeration<String> e = Collections.enumeration(list);
   ```

---

**记住**：
> **接口不兼容，**  
> **适配器来帮忙，**  
> **组合优于继承，**  
> **转换要轻量。**
