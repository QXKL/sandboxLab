# 策略模式 - 学习笔记

> 定义一系列算法，把它们封装起来，并使它们可以相互替换

---

## 我的理解

### 策略模式是什么？
```
[用自己的话解释...]

提示：
- 什么是"算法族"？
- 为什么叫"策略"？
- 核心思想是什么？
- 用支付方式的例子说明
```

---

## 为什么重要

策略模式解决了什么问题？

```
[在这里写...]

提示：
- 大量if-else的问题
- 违反开闭原则的问题
- 算法无法独立复用的问题
```

---

## 核心要点

### 1. 策略模式的三个角色

1. **Strategy（抽象策略）**：
2. **ConcreteStrategy（具体策略）**：
3. **Context（上下文）**：

**关键代码**：
```java
// 抽象策略
interface PaymentStrategy {
    boolean pay(double amount);
}

// 具体策略
class AlipayPayment implements PaymentStrategy { }

// 上下文
class PaymentContext {
    private PaymentStrategy strategy;
    
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void executePayment(double amount) {
        strategy.pay(amount);  // 委托给策略
    }
}
```

---

### 2. 策略模式消除if-else

**改进前**：
```java
if (type.equals("alipay")) {
    // ...
} else if (type.equals("wechat")) {
    // ...
}
```

**改进后**：
```java
// 使用策略工厂 + Map
DiscountStrategy strategy = StrategyFactory.get(type);
context.setStrategy(strategy);
```

**关键技巧**：
```
[你学到的消除if-else的方法...]
```

---

### 3. 策略模式 vs 其他模式

#### 策略 vs 简单工厂

| 对比 | 策略模式 | 简单工厂 |
|-----|---------|---------|
| **关注点** |  |  |
| **目的** |  |  |
| **职责** |  |  |

**记忆口诀**：
> 策略关注行为，工厂关注创建。

---

#### 策略 vs 状态模式

| 对比 | 策略模式 | 状态模式 |
|-----|---------|---------|
| **切换方式** |  |  |
| **关注点** |  |  |
| **独立性** |  |  |

**记忆口诀**：
> 策略主动选，状态自动转。

---

## 代码示例总结

### 示例1：支付策略

**抽象策略**：
```java
interface PaymentStrategy {
    boolean pay(double amount);
}
```

**关键点**：
- 不同支付方式实现统一接口
- 运行时切换支付方式
- 结合工厂模式简化创建

---

### 示例2：排序策略

**智能选择**：
```
根据数据量自动选择：
- 数据量 < 10：冒泡排序
- 数据量 < 100：快速排序
- 数据量 >= 100：归并排序
```

**关键点**：
- 算法性能差异
- 根据场景选择合适算法

---

## 使用场景

### 何时使用策略模式？

✅ 适合的场景：
1. 
2. 
3. 

❌ 不适合的场景：
1. 
2. 
3. 

---

## 策略模式的注意事项

### 1. 客户端需要了解所有策略

**问题**：
```
[描述问题...]
```

**解决方案**：
```
[你的方案...]

提示：结合工厂模式
```

---

### 2. 策略数量过多

**解决方案**：
```
[你的方案...]

提示：
- 使用Lambda表达式
- 使用配置文件
- 使用策略工厂
```

---

## easySay - 用大白话讲一遍

> 假如你要给别人讲解策略模式，你会怎么说？

```
[用支付方式的例子讲解...]

提示：
- 从超市结账开始
- 解释"算法可替换"的好处
- 说明如何消除if-else
```

---

## 真实应用

### 案例1：Java Comparator

**Comparator如何使用策略模式**：
```java
// 策略1：按字母顺序
Collections.sort(names, (a, b) -> a.compareTo(b));

// 策略2：按长度排序
Collections.sort(names, (a, b) -> a.length() - b.length());
```

**分析**：
```
Strategy: 


ConcreteStrategy:


Context:
```

---

### 案例2：Spring的Resource加载

**策略体现**：
```java
Resource r1 = new ClassPathResource("config.xml");
Resource r2 = new FileSystemResource("D:/config.xml");
Resource r3 = new UrlResource("http://example.com/config.xml");

// 统一接口
InputStream is = resource.getInputStream();
```

---

## 自我检测

- [ ] 我理解策略模式的核心思想（算法可替换）
- [ ] 我能用策略模式消除if-else
- [ ] 我理解策略 vs 工厂 vs 状态模式的区别
- [ ] 我能手写策略模式代码
- [ ] 我理解Java Comparator的策略实现
- [ ] 我完成了自测题，正确率 ≥ 80%

---

## 核心口诀

> **算法多个要切换，**  
> **策略模式来帮忙，**  
> **封装算法成对象，**  
> **运行时刻任你选。**

---

## 关键理解

### 1. 为什么策略模式能消除if-else？

```
[你的理解...]

提示：多态 + 策略工厂
```

### 2. 策略模式的核心价值是什么？

```
[你的理解...]

提示：开闭原则、运行时切换、职责分离
```

### 3. 什么时候用策略模式？

```
[你的判断标准...]

提示：多种算法、需要切换、消除if-else
```

---

## 实战练习

### 练习1：设计会员折扣系统

**需求**：
- 普通会员：95折
- VIP会员：85折
- 超级VIP：75折
- 新用户：9折

**思考**：
1. 如何定义策略接口？
2. 如何设计策略工厂？
3. 如何消除if-else？

```
[你的设计...]
```

---

### 练习2：文件压缩系统

**需求**：
- 支持ZIP、RAR、7z压缩
- 根据文件类型或用户选择压缩方式

**思考**：
1. 策略接口如何定义？
2. 如何实现智能选择？

```
[你的设计...]
```

---

## 策略模式与设计原则

### 1. 开闭原则

```
策略模式如何体现开闭原则？

[你的理解...]
```

### 2. 单一职责原则

```
策略模式如何体现单一职责？

[你的理解...]
```

### 3. 依赖倒置原则

```
策略模式如何体现依赖倒置？

[你的理解...]
```

---

**学习日期**：____年__月__日
