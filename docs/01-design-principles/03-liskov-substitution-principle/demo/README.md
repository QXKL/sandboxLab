# 里氏替换原则 - 代码示例

## 示例说明

本示例展示了经典的"正方形-矩形问题"：

1. **违反 LSP**：`BadExample.java` - Square 继承 Rectangle，但破坏了父类的行为契约
2. **符合 LSP**：`GoodExample.java` - 重新设计继承关系，让子类能够正确替换父类

## 核心问题

**数学上**：正方形是特殊的矩形（is-a 关系）  
**编程中**：Square 不能继承 Rectangle（行为不一致）

这个案例完美展示了**概念上的"is-a"关系，在 OOP 中不一定成立**。

## 运行方式

### 编译
```bash
cd O:/JavaProjects/sandboxLab/docs/01-design-principles/03-liskov-substitution-principle/demo
javac *.java
```

### 运行违反LSP的示例
```bash
java BadExample
```

### 运行符合LSP的示例
```bash
java GoodExample
```

## 预期输出

两个示例都会演示矩形和正方形的行为，但设计完全不同：

- **BadExample**：Square 继承 Rectangle，导致替换时行为异常
- **GoodExample**：取消继承关系，让它们独立实现 Shape 接口

## 问题分析

### 为什么 Square 不能继承 Rectangle？

#### Rectangle 的契约
- 宽度和高度可以**独立设置**
- `setWidth(5)` 只改变宽度，不影响高度
- `setHeight(4)` 只改变高度，不影响宽度

#### Square 破坏了这个契约
- 宽度和高度**必须相等**
- `setWidth(5)` 会同时将高度改为 5
- 无法独立设置宽和高

#### 导致的问题
```java
void resize(Rectangle rect) {
    rect.setWidth(5);
    rect.setHeight(4);
    assert rect.getArea() == 20;  // 期望面积 = 5 × 4 = 20
}

resize(new Rectangle());  // ✓ 通过：面积 = 20
resize(new Square());     // ✗ 失败：面积 = 16（4×4）
```

客户端代码期望的是矩形的行为，Square 无法满足。

## 关键对比点

| 维度 | BadExample | GoodExample |
|-----|-----------|------------|
| **继承关系** | Square 继承 Rectangle | 都实现 Shape 接口 |
| **行为一致性** | Square 破坏了 Rectangle 的契约 | 各自有独立的行为契约 |
| **可替换性** | Square 不能替换 Rectangle | 都能替换 Shape |
| **客户端代码** | 需要特殊判断处理 Square | 统一通过 Shape 接口使用 |
| **扩展性** | 继承体系脆弱 | 易于扩展（添加三角形等） |

## LSP 核心要点

### 1. 契约（Contract）
- **前置条件**：子类不能强化（接受的输入范围不能比父类小）
- **后置条件**：子类不能弱化（输出保证不能比父类弱）
- **不变式**：子类必须维持父类的所有约束

### 2. 替换测试
```java
void test(Parent parent) {
    // 使用父类的方法
}

test(new Parent());  // 原始行为
test(new Child());   // 子类必须表现一致
```

如果传入子类导致异常或结果不符合预期，就违反了 LSP。

### 3. 行为一致性
- 客户端不应该需要知道具体是哪个子类
- 不应该用 `instanceof` 判断类型并分别处理
- 不应该针对不同子类有不同的错误处理

## 思考题

1. 为什么数学上"正方形是矩形"，但 OOP 中这个继承关系是错误的？
2. Rectangle 的核心契约是什么？Square 破坏了哪部分？
3. 如果一定要表达"正方形是特殊的矩形"，应该如何设计？
4. GoodExample 中的设计有什么优势？
5. 你能想到其他违反 LSP 的例子吗？（提示：鸟类和企鹅）

## 扩展练习

尝试设计以下场景，确保符合 LSP：

1. **鸟类继承体系**
   - Bird（鸟类基类）
   - Sparrow（麻雀，会飞）
   - Penguin（企鹅，不会飞）
   - 如何设计才能让 Penguin 不破坏 Bird 的契约？

2. **账户继承体系**
   - Account（账户基类，不允许透支）
   - SavingsAccount（储蓄账户）
   - OverdraftAccount（允许透支的账户）
   - 如何设计才能符合 LSP？

3. **文件处理器**
   - FileProcessor（处理任意文件）
   - ImageProcessor（只处理图片）
   - 子类强化了前置条件，如何重构？

## 记住

> **概念上的 is-a 关系 ≠ 行为上的 behaves-like-a 关系**

LSP 关注的是**行为契约**，而非**概念关系**。
