# 里氏替换原则 - 代码示例说明

本目录包含了里氏替换原则（Liskov Substitution Principle, LSP）的代码示例。

## 文件说明

- **BadExample.java** - 违反LSP的示例（Rectangle-Square问题）
- **GoodExample.java** - 符合LSP的示例（正确的设计方案）
- **README.md** - 本文件，使用说明

## 快速运行

### 方式1: 使用命令行编译运行

```bash
# 进入demo目录
cd docs/01-design-principles/03-liskov-substitution-principle/demo

# 编译并运行BadExample
javac BadExample.java
java BadExample

# 编译并运行GoodExample
javac GoodExample.java
java GoodExample
```

### 方式2: 使用IDE

1. 使用IntelliJ IDEA或Eclipse打开项目
2. 找到 `demo/` 目录
3. 右键点击 `BadExample.java` → Run
4. 右键点击 `GoodExample.java` → Run

## 代码结构

### BadExample.java - 违反LSP

**问题场景**：
```
Rectangle (父类)
    ├─ setWidth(int)  - 契约：只改变宽度
    ├─ setHeight(int) - 契约：只改变高度
    └─ getArea()      - 返回 width * height

Square (子类) extends Rectangle
    ├─ setWidth(int)  - ❌ 同时改变宽度和高度
    └─ setHeight(int) - ❌ 同时改变宽度和高度
```

**核心问题**：
- `Square` 为了保持"宽高相等"的特性，重写了 `setWidth` 和 `setHeight`
- 这破坏了 `Rectangle` 的行为契约
- 导致 `Square` 不能可靠地替换 `Rectangle`

**运行结果**：
```
测试1: 使用Rectangle
  设置宽度=5, 高度=4
  ✓ 面积正确: 20

测试2: 使用Square
  设置宽度=5, 高度=4
  ❌ 面积错误: 期望=20, 实际=16
```

### GoodExample.java - 符合LSP

**解决方案1：接口 + 组合（推荐）**
```
Shape (接口)
    ├─ Rectangle implements Shape
    ├─ Square implements Shape
    └─ Circle implements Shape
```

**关键设计**：
- `Rectangle` 和 `Square` 不再有继承关系
- 都实现 `Shape` 接口
- 使用不可变设计，避免状态变化

**解决方案2：只读继承层次**
```
ReadOnlyShape (抽象类)
    └─ ReadOnlyRectangle extends ReadOnlyShape
        └─ ReadOnlySquare extends ReadOnlyRectangle
```

**关键设计**：
- 不提供 `setter` 方法
- 只读对象没有状态变化，不存在契约冲突
- 子类只是特化构造方式，不改变行为

## 核心概念对比

| 维度 | BadExample（违反LSP） | GoodExample（符合LSP） |
|------|---------------------|---------------------|
| **继承关系** | Square extends Rectangle | Rectangle & Square 都实现 Shape |
| **可变性** | 可变对象（有setter） | 不可变对象（无setter） |
| **行为一致性** | ❌ 子类改变了父类行为 | ✅ 所有实现遵循接口契约 |
| **可替换性** | ❌ Square不能替换Rectangle | ✅ 所有Shape实现可以互换 |
| **多态可靠性** | ❌ 多态失效 | ✅ 多态可靠 |

## 学习要点

### 1. 概念上的"is-a"不等于代码上的"is-a"

- 数学上：正方形是特殊的矩形 ✓
- 代码上：Square 继承 Rectangle ✗

**原因**：OOP的继承是行为上的替代关系，不是分类学关系。

### 2. 契约式设计

**父类的契约**：
- 前置条件：输入要求
- 后置条件：输出保证
- 不变式：始终成立的约束

**子类的责任**：
- 前置条件不能强化（不能更严格）
- 后置条件不能弱化（不能更宽松）
- 不变式必须保持

### 3. 不可变对象的优势

```java
// ❌ 可变对象：容易违反契约
class Rectangle {
    private int width, height;
    public void setWidth(int w) { width = w; }  // 可能破坏契约
}

// ✅ 不可变对象：天然符合LSP
class Rectangle {
    private final int width, height;
    public Rectangle withWidth(int w) {  // 返回新对象
        return new Rectangle(w, this.height);
    }
}
```

### 4. 组合优于继承

```java
// ❌ 继承：强耦合，容易违反LSP
class Square extends Rectangle { }

// ✅ 组合：松耦合，灵活安全
class Square implements Shape { }
class Rectangle implements Shape { }
```

## 运行观察重点

### 运行 BadExample 时观察

1. **行为变化**：相同的操作，传入子类时结果不同
2. **预期失败**：客户端代码的断言失败
3. **替换失败**：Square 不能替换 Rectangle

### 运行 GoodExample 时观察

1. **行为一致**：所有 Shape 实现都遵循契约
2. **可靠替换**：任何 Shape 实现都可以互换
3. **扩展性**：新增 Circle 不影响现有代码

## 实践建议

### 何时使用继承？

✅ **应该继承**：
- 真正的"is-a"关系（行为上的，不只是概念上的）
- 子类扩展父类功能，不改变原有行为
- 子类能通过所有父类的测试用例

⚠️ **避免继承**：
- 只是为了代码复用（用组合）
- 子类需要禁用父类的某些功能
- 子类需要改变父类的核心行为

### 设计检查清单

在设计继承关系前，检查：

- [ ] 子类是否改变了父类的方法行为？
- [ ] 子类是否抛出了父类未声明的异常？
- [ ] 子类是否强化了前置条件（输入要求更严格）？
- [ ] 子类是否弱化了后置条件（输出保证更弱）？
- [ ] 子类是否违反了父类的不变式？

如果任何一项是"是"，考虑使用组合而非继承。

## 扩展思考

1. **为什么 Java 的 `Stack` 继承 `Vector` 被认为是设计失误？**
   
   提示：Stack 暴露了 Vector 的所有方法，破坏了 LIFO 约束。

2. **如何测试子类的可替换性？**
   
   提示：编写抽象测试类，让所有子类都继承这个测试类。

3. **不可变对象一定符合 LSP 吗？**
   
   提示：不可变只是减少了状态变化的问题，但仍需遵守契约。

## 相关资料

- `../doc_01.md` - 里氏替换原则详细文档
- `../test_01.md` - 自测题
- `../note_template.md` - 学习笔记模板

---

**💡 提示**：理解LSP的关键是理解"契约"。父类定义了行为契约，子类必须遵守这个契约，才能可靠地替换父类。
