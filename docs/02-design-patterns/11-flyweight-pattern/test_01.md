# 享元模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 30 分）

### 1. 享元模式的主要目的是什么？

A. 将一个类的接口转换成客户希望的另一个接口  
B. 运用共享技术有效地支持大量细粒度对象的复用  
C. 将对象组合成树形结构以表示"部分-整体"的层次结构  
D. 动态地给对象添加额外的职责

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 享元模式的核心目的是**运用共享技术有效地支持大量细粒度对象的复用**，减少内存占用。

**关键词**：共享、大量对象、减少内存

**形象类比**：
- 围棋：100颗棋子只需要2个对象（黑+白）
- String Pool：相同字符串共享同一个对象

**"享元"含义**：
- **享** = 共享
- **元** = 元素、对象

**其他选项**：
- A：适配器模式
- C：组合模式
- D：装饰器模式
</details>

---

### 2. 享元模式中的"内部状态"和"外部状态"的区别是什么？

A. 内部状态由客户端维护，外部状态存储在享元对象内部  
B. 内部状态存储在享元对象内部且可共享，外部状态由客户端维护且不可共享  
C. 内部状态会随环境改变，外部状态不会改变  
D. 两者没有区别

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 

| 状态类型 | 内部状态（Intrinsic） | 外部状态（Extrinsic） |
|---------|---------------------|---------------------|
| **存储位置** | 享元对象内部 | 客户端维护 |
| **是否共享** | ✅ 可共享 | ❌ 不可共享 |
| **是否改变** | ❌ 不变 | ✅ 会变 |
| **例子（围棋）** | 颜色（黑/白） | 位置（x, y） |
| **例子（文本）** | 字体样式 | 字符内容、位置 |

**判断标准**：
```
内部状态：
- 不会随环境改变
- 可以被多个对象共享
- 存储在享元对象内部

外部状态：
- 随环境改变
- 每个对象可能不同
- 作为参数传入方法
```

**代码示例**：
```java
class ChessPiece {
    private String color;  // 内部状态（共享）
    
    public void place(int x, int y) {  // 外部状态（参数）
        System.out.println(color + "棋子在(" + x + "," + y + ")");
    }
}
```
</details>

---

## 二、内部状态 vs 外部状态（20 分）

### 3. 以下场景中，哪些属性应该作为内部状态（可共享）？

**场景：游戏中的树木渲染**

A. 树木的3D模型  
B. 树木的位置（x, y, z）  
C. 树木的纹理贴图  
D. 树木的大小（scale）  
E. 树木的旋转角度

<details>
<summary>查看答案</summary>

**答案**: A, C

**解析**:

### ✅ 内部状态（可共享）

**A. 树木的3D模型** - 正确
- 所有橡树使用同一个3D模型
- 模型数据不变
- 可以共享

**C. 树木的纹理贴图** - 正确
- 同类型树木使用相同纹理
- 纹理数据不变
- 可以共享

---

### ❌ 外部状态（不可共享）

**B. 树木的位置（x, y, z）** - 错误
- 每棵树位置不同
- 随环境变化
- 不可共享

**D. 树木的大小（scale）** - 错误
- 每棵树大小可能不同
- 需要独立设置
- 不可共享

**E. 树木的旋转角度** - 错误
- 每棵树朝向可能不同
- 需要独立设置
- 不可共享

---

### 代码实现

```java
// 内部状态：树木模型（享元对象）
class TreeModel {
    private Mesh mesh;        // 3D模型（共享）
    private Texture texture;  // 纹理（共享）
    
    public void render(Vector3 position, float scale, float rotation) {
        // 外部状态作为参数传入
    }
}

// 外部状态：树木实例
class Tree {
    private TreeModel model;     // 内部状态（共享）
    private Vector3 position;    // 外部状态
    private float scale;         // 外部状态
    private float rotation;      // 外部状态
}

// 10000棵树，只需要5种模型
```

---

### 判断技巧

**三个问题判断法**：
1. **是否可以共享？** → 内部状态
2. **是否随环境变化？** → 外部状态
3. **存储在哪里？** → 内部状态在享元对象内部，外部状态在客户端

</details>

---

## 三、模式对比（15 分）

### 4. 享元模式、单例模式、对象池的主要区别是什么？

A. 享元模式有多个共享实例，单例模式只有一个实例  
B. 享元模式主要减少内存占用，对象池主要减少创建销毁开销  
C. 单例模式强调全局唯一，享元模式强调按内部状态共享  
D. 以上都正确

<details>
<summary>查看答案</summary>

**答案**: D

**解析**: 

| 对比 | 单例模式 | 享元模式 | 对象池 |
|-----|---------|---------|--------|
| **实例数量** | 1个 | 多个（按内部状态） | 多个 |
| **目的** | 全局唯一 | 减少内存占用 | 减少创建销毁开销 |
| **共享方式** | 全局共享 | 按内部状态共享 | 循环使用 |
| **状态管理** | 全局状态 | 内部+外部状态 | 重置状态 |
| **典型例子** | 配置管理器 | String Pool | 数据库连接池 |

---

### 单例模式

```java
// 一个类只有一个实例
ConfigManager config = ConfigManager.getInstance();
ConfigManager config2 = ConfigManager.getInstance();
// config == config2（同一个对象）
```

**特点**：全局唯一

---

### 享元模式

```java
// 一个类可以有多个共享实例（按内部状态区分）
ChessPiece black = factory.get("黑色");
ChessPiece white = factory.get("白色");
ChessPiece black2 = factory.get("黑色");
// black == black2（共享）
// black != white（不同内部状态）
```

**特点**：按内部状态共享

---

### 对象池

```java
// 从池中获取对象，使用完归还
Connection conn = pool.getConnection();
// ... 使用
pool.returnConnection(conn);  // 归还
```

**特点**：循环重用

---

### 核心区别

**单例模式**：
- 目的：确保全局唯一
- 实例数：1个

**享元模式**：
- 目的：减少内存占用（共享相似对象）
- 实例数：多个（按内部状态分类）

**对象池**：
- 目的：减少创建销毁开销（重用对象）
- 实例数：多个（循环使用）

</details>

---

## 四、场景判断（20 分）

### 5. 以下哪些场景适合使用享元模式？

A. 文本编辑器中的字符样式  
B. 游戏中的大量子弹对象  
C. 系统配置管理器  
D. 游戏中的玩家角色  
E. 网页浏览器中的图标缓存

<details>
<summary>查看答案</summary>

**答案**: A, B, E

**解析**:

### ✅ A. 文本编辑器中的字符样式 - 适合

**理由**：
- 大量字符（成千上万）
- 样式种类有限（几十种）
- 内部状态：字体、大小、颜色
- 外部状态：字符内容、位置

```java
// 1000个字符，可能只需要10个样式对象
CharacterStyle style1 = factory.get("Arial", 12, "黑色");
// 复用同一个样式对象
```

---

### ✅ B. 游戏中的大量子弹对象 - 适合

**理由**：
- 大量子弹（可能几百上千）
- 子弹类型有限（几种武器）
- 内部状态：子弹模型、纹理、伤害值
- 外部状态：位置、速度、方向

```java
// 1000颗子弹，只需要5种子弹类型对象
Bullet bullet = factory.getBullet("AK47");
bullet.fire(position, velocity);
```

---

### ❌ C. 系统配置管理器 - 不适合，应该用**单例模式**

**理由**：
- 只需要一个实例
- 强调全局唯一
- 不是大量对象场景

---

### ❌ D. 游戏中的玩家角色 - 不适合

**理由**：
- 玩家数量少（通常几个到几十个）
- 每个玩家状态完全不同
- 无法提取共享的内部状态

---

### ✅ E. 网页浏览器中的图标缓存 - 适合

**理由**：
- 大量图标显示（工具栏、菜单、网页）
- 图标种类有限（同一个图标多处使用）
- 内部状态：图标图像数据
- 外部状态：显示位置、大小

```java
// 100个图标显示，可能只需要20个图标对象
Icon icon = factory.getIcon("save.png");
icon.draw(x, y, width, height);
```

---

### 判断标准

**适合享元模式**：
1. ✅ 需要创建大量对象
2. ✅ 对象的大部分状态可以外部化
3. ✅ 内存敏感

**不适合享元模式**：
1. ❌ 对象数量少
2. ❌ 对象状态完全不同
3. ❌ 外部状态管理复杂

</details>

---

## 五、代码分析（25 分）

### 6. 以下代码有什么问题？如何用享元模式改进？

```java
// 游戏中的树木渲染（不使用享元模式）
class Tree {
    private String type;        // 树木类型（橡树、松树等）
    private Mesh mesh;          // 3D模型
    private Texture texture;    // 纹理
    private Vector3 position;   // 位置
    private float scale;        // 大小
    
    public void render() {
        // 渲染树木
    }
}

// 创建10000棵树
List<Tree> trees = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    String type = selectRandomType();  // 随机选择5种树型之一
    trees.add(new Tree(type, loadMesh(type), loadTexture(type), 
                       randomPosition(), randomScale()));
}
// 10000棵树 = 10000个对象
// 每个对象包含完整的模型和纹理数据
```

<details>
<summary>参考答案</summary>

**问题分析**：

1. ❌ **内存浪费**：每棵树都存储完整的模型和纹理
2. ❌ **重复数据**：10000棵树可能只有5种类型，却加载了10000份模型数据
3. ❌ **加载开销**：每次创建树都要加载模型和纹理
4. ❌ **没有区分内部状态和外部状态**

**内存占用估算**：
```
假设每个模型+纹理 = 10MB
10000棵树 × 10MB = 100GB（爆炸！）

实际上只需要：
5种树型 × 10MB = 50MB
```

---

**改进方案：使用享元模式**

```java
// 1. 享元对象：树木类型（内部状态）
class TreeType {
    private final String name;       // 内部状态
    private final Mesh mesh;         // 内部状态
    private final Texture texture;   // 内部状态
    
    public TreeType(String name, Mesh mesh, Texture texture) {
        this.name = name;
        this.mesh = mesh;
        this.texture = texture;
        System.out.println("加载树木类型: " + name);
    }
    
    public void render(Vector3 position, float scale) {
        // 使用共享的模型和纹理渲染
        // 位置和大小作为参数传入（外部状态）
        System.out.println("渲染" + name + "在" + position);
    }
}

// 2. 享元工厂
class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();
    
    public static TreeType getTreeType(String name, Mesh mesh, Texture texture) {
        if (!treeTypes.containsKey(name)) {
            treeTypes.put(name, new TreeType(name, mesh, texture));
        }
        return treeTypes.get(name);
    }
    
    public static int getPoolSize() {
        return treeTypes.size();
    }
}

// 3. 树木实例（外部状态）
class Tree {
    private final TreeType type;      // 内部状态（共享）
    private final Vector3 position;   // 外部状态
    private final float scale;        // 外部状态
    
    public Tree(TreeType type, Vector3 position, float scale) {
        this.type = type;
        this.position = position;
        this.scale = scale;
    }
    
    public void render() {
        type.render(position, scale);
    }
}

// 4. 使用
class Forest {
    private final List<Tree> trees = new ArrayList<>();
    
    public void plantTrees(int count) {
        // 预加载树木类型
        TreeType oak = TreeFactory.getTreeType("橡树", 
                loadMesh("oak"), loadTexture("oak"));
        TreeType pine = TreeFactory.getTreeType("松树", 
                loadMesh("pine"), loadTexture("pine"));
        TreeType birch = TreeFactory.getTreeType("桦树", 
                loadMesh("birch"), loadTexture("birch"));
        
        // 创建树木实例
        for (int i = 0; i < count; i++) {
            TreeType type = selectRandomType(oak, pine, birch);
            Vector3 position = randomPosition();
            float scale = randomScale();
            
            trees.add(new Tree(type, position, scale));
        }
        
        System.out.println("种植了 " + count + " 棵树");
        System.out.println("使用了 " + TreeFactory.getPoolSize() + " 种树型");
    }
    
    public void render() {
        for (Tree tree : trees) {
            tree.render();
        }
    }
}

// 5. 测试
Forest forest = new Forest();
forest.plantTrees(10000);
// 输出：
// 加载树木类型: 橡树
// 加载树木类型: 松树
// 加载树木类型: 桦树
// 种植了 10000 棵树
// 使用了 3 种树型
```

---

**优化效果对比**：

| 对比 | 改进前 | 改进后 |
|-----|--------|--------|
| 对象数量 | 10000个完整对象 | 10000个轻量对象 + 5个享元对象 |
| 内存占用 | 100GB | ~50MB |
| 加载次数 | 10000次 | 5次 |
| 节省比例 | - | 99.95% |

**关键改进**：
1. ✅ 区分内部状态（类型、模型、纹理）和外部状态（位置、大小）
2. ✅ 使用享元工厂管理共享对象
3. ✅ 模型和纹理只加载一次
4. ✅ 内存占用从100GB降到50MB

</details>

---

## 六、真实应用（10 分）

### 7. Java的String Pool是如何实现享元模式的？

<details>
<summary>参考答案</summary>

### String Pool的享元实现

```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");

System.out.println(s1 == s2);           // true（共享）
System.out.println(s1 == s3);           // false（不共享）
System.out.println(s1 == s3.intern());  // true（强制共享）
```

---

### 工作原理

**1. 字符串字面量自动进入池**

```java
String s1 = "hello";  // 放入String Pool
String s2 = "hello";  // 从String Pool获取（共享）
// s1和s2指向同一个对象
```

**2. new String()不自动进入池**

```java
String s3 = new String("hello");  // 堆中创建新对象
// s3是新对象，不共享
```

**3. intern()方法强制进入池**

```java
String s4 = new String("world").intern();  // 强制加入池
String s5 = "world";
// s4 == s5（共享）
```

---

### 享元模式体现

| 享元要素 | String Pool实现 |
|---------|----------------|
| **内部状态** | 字符串内容 |
| **享元对象** | String对象 |
| **享元工厂** | JVM的String Pool机制 |
| **共享效果** | 相同内容的字符串只存储一份 |

---

### 内存优化效果

```java
// 不使用String Pool
String[] words = new String[1000];
for (int i = 0; i < 1000; i++) {
    words[i] = new String("hello");  // 1000个对象
}

// 使用String Pool
String[] words = new String[1000];
for (int i = 0; i < 1000; i++) {
    words[i] = "hello";  // 1个共享对象
}
```

**优势**：
- ✅ 减少内存占用
- ✅ 字符串比较更快（`==`代替`equals()`）
- ✅ JVM自动管理

---

### Integer缓存（类似）

```java
Integer a = 127;
Integer b = 127;
System.out.println(a == b);  // true（缓存范围内）

Integer c = 128;
Integer d = 128;
System.out.println(c == d);  // false（超出缓存范围）
```

**Integer.valueOf()源码**：
```java
public static Integer valueOf(int i) {
    if (i >= -128 && i <= 127)
        return IntegerCache.cache[i + 128];  // 享元共享
    return new Integer(i);  // 不共享
}
```

---

### 总结

**String Pool是享元模式的经典应用**：
- 内部状态：字符串内容
- 共享机制：相同内容只存一份
- 工厂管理：JVM自动管理池
- 优化效果：显著减少内存占用

</details>

---

## 核心要点回顾

### 享元模式三要素
1. **内部状态**：存储在享元对象内部，可共享
2. **外部状态**：由客户端维护，不可共享
3. **享元工厂**：管理共享对象池

### 记忆口诀
> **大量对象内存爆，**  
> **享元模式来减少，**  
> **内部共享外部传，**  
> **棋子字符都适用。**

### 使用场景
- ✅ 大量相似对象
- ✅ 对象的大部分状态可以外部化
- ✅ 内存敏感

---

**完成自测后**，填写 `note_template.md` 巩固知识！
