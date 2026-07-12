# 策略模式 - 代码示例

## 示例说明

本目录包含策略模式的两个核心示例：

1. **PaymentStrategyDemo.java** - 支付策略（多种支付方式）
2. **SortStrategyDemo.java** - 排序策略（多种排序算法）

---

## 运行方式

```bash
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\12-strategy-pattern\demo

javac PaymentStrategyDemo.java
javac SortStrategyDemo.java

java PaymentStrategyDemo
java SortStrategyDemo
```

---

## 核心概念

### 策略模式的本质

```
抽象策略（Strategy）
    ↓ 实现
具体策略A、B、C（ConcreteStrategy）
    ↓ 使用
上下文（Context）

定义算法族 + 封装 + 可替换 = 策略模式
```

---

## 三个角色

### 1. Strategy（抽象策略）
```java
interface PaymentStrategy {
    boolean pay(double amount);
}
```

### 2. ConcreteStrategy（具体策略）
```java
class AlipayPayment implements PaymentStrategy {
    public boolean pay(double amount) {
        // 支付宝支付逻辑
    }
}

class WeChatPayment implements PaymentStrategy {
    public boolean pay(double amount) {
        // 微信支付逻辑
    }
}
```

### 3. Context（上下文）
```java
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

## 策略模式消除if-else

### ❌ 传统if-else

```java
void processPayment(String type, double amount) {
    if (type.equals("alipay")) {
        // 支付宝逻辑
    } else if (type.equals("wechat")) {
        // 微信逻辑
    } else if (type.equals("creditcard")) {
        // 信用卡逻辑
    }
}
```

**问题**：
- 违反开闭原则
- 难以测试
- 代码重复

---

### ✅ 策略模式

```java
// 1. 定义策略接口
interface PaymentStrategy {
    boolean pay(double amount);
}

// 2. 具体策略
class AlipayPayment implements PaymentStrategy { }
class WeChatPayment implements PaymentStrategy { }

// 3. 使用（无if-else）
context.setStrategy(new AlipayPayment());
context.executePayment(amount);
```

**优势**：
- 无if-else
- 符合开闭原则
- 易于测试

---

## 策略模式 vs 简单工厂

| 对比 | 策略模式 | 简单工厂 |
|-----|---------|---------|
| **关注点** | 算法行为 | 对象创建 |
| **目的** | 算法可替换 | 封装创建 |
| **职责** | 执行算法 | 创建实例 |

**可以结合使用**：
```java
// 工厂创建策略
PaymentStrategy strategy = StrategyFactory.create("alipay");

// 策略执行算法
context.setStrategy(strategy);
context.executePayment(amount);
```

---

## 真实应用

### 1. Java Comparator

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 策略1：按字母顺序
Collections.sort(names, (a, b) -> a.compareTo(b));

// 策略2：按长度排序
Collections.sort(names, (a, b) -> a.length() - b.length());
```

### 2. 智能策略选择

```java
// 根据数据量自动选择算法
SortStrategy strategy = SmartSelector.select(dataSize);
context.setStrategy(strategy);
context.executeSort(array);
```

---

## 关键点

1. **消除if-else**（核心价值）
2. **算法独立封装**（单一职责）
3. **运行时切换**（灵活性）
4. **符合开闭原则**（易扩展）

---

**记住**：
> **算法多个要切换，**  
> **策略模式来帮忙，**  
> **封装算法成对象，**  
> **运行时刻任你选。**
