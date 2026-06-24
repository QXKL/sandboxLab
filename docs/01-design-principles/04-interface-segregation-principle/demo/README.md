# 接口隔离原则 - 代码示例

## 示例说明

本示例展示了打印机管理系统的两种设计：

1. **违反 ISP**：`BadExample.java` - 使用胖接口，简单设备被迫实现不需要的方法
2. **符合 ISP**：`GoodExample.java` - 接口隔离，设备只实现需要的功能

## 核心问题

**胖接口（Fat Interface）**的问题：
- 接口包含太多方法
- 客户端只使用其中一部分
- 实现类被迫实现不需要的方法（空实现或抛异常）

## 运行方式

### 编译
```bash
cd O:/JavaProjects/sandboxLab/docs/01-design-principles/04-interface-segregation-principle/demo
javac *.java
```

### 运行违反ISP的示例
```bash
java BadExample
```

### 运行符合ISP的示例
```bash
java GoodExample
```

## 预期输出

两个示例都会演示打印机、扫描仪和多功能一体机的操作，但设计完全不同：

- **BadExample**：所有设备实现同一个胖接口，简单设备被迫实现不支持的方法
- **GoodExample**：接口拆分，设备只实现支持的功能接口

## 问题分析

### 为什么胖接口是问题？

#### 胖接口的特征
- 包含多个不相关的方法
- 客户端只使用部分方法
- 实现类被迫实现所有方法

#### 导致的问题
```java
// 简单打印机只能打印，但必须实现扫描和传真
class SimplePrinter implements MultiFunctionDevice {
    public void print(Document doc) { /* 正常实现 */ }
    
    public void scan(Document doc) {
        throw new UnsupportedOperationException();  // ❌ 被迫抛异常
    }
    
    public void fax(Document doc) {
        throw new UnsupportedOperationException();  // ❌ 被迫抛异常
    }
}

// 客户端无法从接口判断哪些方法可用
void useDevice(MultiFunctionDevice device) {
    device.scan(doc);  // 可能在运行时抛异常！
}
```

## 代码结构对比

### 违反 ISP（BadExample）
```
MultiFunctionDevice（胖接口）
├── print()
├── scan()
└── fax()

SimplePrinter implements MultiFunctionDevice
├── print() ✓ 实现
├── scan() ✗ 抛异常
└── fax() ✗ 抛异常

Scanner implements MultiFunctionDevice
├── print() ✗ 抛异常
├── scan() ✓ 实现
└── fax() ✗ 抛异常
```

### 符合 ISP（GoodExample）
```
Printer接口          Scanner接口         Fax接口
└── print()          └── scan()          └── fax()

SimplePrinter        BasicScanner        AdvancedPrinter
implements Printer   implements Scanner  implements Printer, Scanner, Fax
└── print() ✓        └── scan() ✓        ├── print() ✓
                                         ├── scan() ✓
                                         └── fax() ✓
```

## 关键对比点

| 维度 | BadExample | GoodExample |
|-----|-----------|------------|
| **接口设计** | 一个胖接口 | 多个小接口 |
| **实现负担** | 被迫实现不需要的方法 | 只实现需要的接口 |
| **异常处理** | 抛出 UnsupportedOperationException | 不会抛异常 |
| **类型安全** | 编译通过，运行时出错 | 编译期就能发现问题 |
| **客户端依赖** | 依赖整个胖接口 | 只依赖需要的小接口 |
| **维护性** | 接口变化影响所有实现 | 接口变化影响范围小 |

## ISP 核心要点

### 1. 客户端不应该依赖不需要的接口

```java
// ❌ 客户端依赖胖接口
void printOnly(MultiFunctionDevice device) {
    device.print(doc);  // 只用 print，但依赖了整个接口
}

// ✅ 客户端只依赖需要的接口
void printOnly(Printer printer) {
    printer.print(doc);  // 清晰明确
}
```

### 2. 接口应该小而专注

- 一个接口 3-7 个相关方法
- 方法应该高度相关（高内聚）
- 按功能或职责拆分

### 3. 避免接口污染

接口污染的表现：
- 空实现：`public void method() { }`
- 抛异常：`throw new UnsupportedOperationException()`
- 返回null或默认值：`return null;`

## 思考题

1. 如果要添加"装订"功能，哪种设计更容易扩展？
2. 客户端只需要打印功能时，依赖哪个接口更合适？
3. SimplePrinter 在 BadExample 中违反了哪些原则？（提示：不只是 ISP）
4. 如何判断一个接口是否"太胖"？
5. 接口隔离和单一职责原则有什么区别？

## 扩展练习

尝试设计以下场景，确保符合 ISP：

1. **支付系统**
   - 不同支付方式支持不同功能
   - 有些支持退款，有些不支持
   - 有些需要预授权，有些不需要

2. **用户管理系统**
   - 管理员、普通用户、访客
   - 不同角色有不同的接口

3. **文档处理系统**
   - 可读、可写、可审批、可归档
   - 不同用户有不同权限

看看你是否能做到：**接口小而专注，客户端只依赖需要的部分**。

## 记住

> **胖接口是万恶之源，接口隔离保平安。**

客户端不应该被迫依赖它不使用的接口。
