# 享元模式 - 代码示例

## 示例说明

本目录包含享元模式的两个核心示例：

1. **ChessPieceDemo.java** - 围棋棋子（黑白棋子共享）
2. **TextEditorDemo.java** - 文本编辑器（字符样式共享）

---

## 运行方式

```bash
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\11-flyweight-pattern\demo

javac ChessPieceDemo.java
javac TextEditorDemo.java

java ChessPieceDemo
java TextEditorDemo
```

---

## 核心概念

### 享元模式的本质

```
内部状态（Intrinsic State）
    ↓ 共享
享元对象（Flyweight）
    ↓ 使用时传入
外部状态（Extrinsic State）

共享 + 外部化 = 减少内存
```

---

## 内部状态 vs 外部状态

### 围棋示例

```java
// 内部状态：颜色（共享）
class ChessPiece {
    private String color;  // 黑色/白色（不变，可共享）
}

// 外部状态：位置（不共享）
piece.place(x, y);  // 位置作为参数传入
```

**关键**：
- 100颗棋子 = 2个对象（黑+白）
- 颜色是内部状态（共享）
- 位置是外部状态（参数传入）

---

### 文本编辑器示例

```java
// 内部状态：样式（共享）
class CharacterStyle {
    private String font;   // 字体
    private int size;      // 大小
    private String color;  // 颜色
}

// 外部状态：字符内容、位置（不共享）
class TextCharacter {
    private char content;           // 外部状态
    private int position;           // 外部状态
    private CharacterStyle style;   // 内部状态（共享）
}
```

**关键**：
- 1000个字符可能只需要10个样式对象
- 样式是内部状态（共享）
- 字符内容和位置是外部状态

---

## 享元工厂

### 作用

管理共享对象池，确保相同内部状态的对象只创建一次。

```java
class FlyweightFactory {
    private Map<String, Flyweight> pool = new HashMap<>();
    
    public Flyweight getFlyweight(String key) {
        if (!pool.containsKey(key)) {
            pool.put(key, new ConcreteFlyweight(key));
        }
        return pool.get(key);  // 返回共享对象
    }
}
```

---

## 内存优化效果

### 围棋示例

```
棋子数量     非享元模式    享元模式    节省比例
───────────────────────────────────────────
100          100个         2个         98.00%
1,000        1,000个       2个         99.80%
10,000       10,000个      2个         99.98%
100,000      100,000个     2个         99.998%
```

**结论**：对象数量越多，享元模式优势越明显。

---

## 享元模式 vs 单例模式 vs 对象池

| 对比 | 单例模式 | 享元模式 | 对象池 |
|-----|---------|---------|--------|
| **实例数量** | 1个 | 多个（按内部状态） | 多个 |
| **目的** | 全局唯一 | 减少内存占用 | 减少创建开销 |
| **例子** | 配置管理器 | String Pool | 连接池 |

---

## 真实应用

### 1. String Pool

```java
String s1 = "hello";
String s2 = "hello";
System.out.println(s1 == s2);  // true（共享）
```

### 2. Integer 缓存

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);  // true（-128~127缓存）
```

---

## 关键点

1. **区分内部状态和外部状态**（核心）
2. **享元工厂管理对象池**
3. **外部状态作为参数传入**
4. **享元对象应该是不可变的**

---

**记住**：
> **大量对象内存爆，**  
> **享元模式来减少，**  
> **内部共享外部传，**  
> **棋子字符都适用。**
