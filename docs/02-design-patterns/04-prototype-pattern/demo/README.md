# 原型模式 - 代码示例

## 示例说明

本目录包含原型模式的三个核心示例：

1. **ShallowCopyDemo.java** - 浅拷贝 vs 深拷贝
2. **DeepCopyDemo.java** - 深拷贝的三种实现方式
3. **PrototypeRegistryDemo.java** - 原型注册表

---

## 运行方式

```bash
# 进入demo目录
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\04-prototype-pattern\demo

# 编译
javac ShallowCopyDemo.java
javac DeepCopyDemo.java
javac PrototypeRegistryDemo.java

# 运行
java ShallowCopyDemo
java DeepCopyDemo
java PrototypeRegistryDemo
```

---

## 示例1：ShallowCopyDemo.java

### 场景
学生选课系统

### 演示内容
- 浅拷贝的问题（引用共享）
- 深拷贝的解决方案
- 对比分析

### 核心要点
**浅拷贝问题**：
```java
Student copy = original.clone();
copy.getCourses().add("Science");
// 原对象的courses也被修改了！
```

**深拷贝解决**：
```java
@Override
public Student clone() {
    Student cloned = (Student) super.clone();
    cloned.courses = new ArrayList<>(this.courses);  // 深拷贝List
    return cloned;
}
```

---

## 示例2：DeepCopyDemo.java

### 场景
文档编辑器

### 演示内容
深拷贝的三种实现方式：
1. **手动复制**（推荐）
2. **序列化反序列化**
3. **拷贝构造函数**

### 性能对比

| 方式 | 性能 | 复杂度 | 推荐度 |
|-----|------|--------|--------|
| 手动复制 | ⭐⭐⭐ 快 | 中 | ⭐⭐⭐⭐ |
| 序列化 | ⭐ 慢 | 低 | ⭐⭐ |
| 拷贝构造 | ⭐⭐⭐ 快 | 低 | ⭐⭐⭐⭐⭐ |

### 推荐选择
- 简单对象 → **拷贝构造函数**（最清晰）
- 一般对象 → **手动复制**（性能好）
- 复杂对象 → **序列化**（自动但慢）

---

## 示例3：PrototypeRegistryDemo.java

### 场景
游戏角色系统

### 演示内容
- 原型注册表的实现
- 通过key快速克隆对象
- 动态管理原型
- 性能优势对比

### 核心代码
```java
class PrototypeRegistry {
    private Map<String, Prototype> prototypes = new HashMap<>();
    
    public void register(String key, Prototype prototype) {
        prototypes.put(key, prototype);
    }
    
    public Prototype getPrototype(String key) {
        return prototypes.get(key).clone();
    }
}

// 使用
registry.register("warrior", new Warrior());
Character warrior1 = registry.getPrototype("warrior");
Character warrior2 = registry.getPrototype("warrior");
```

---

## 核心概念

### 1. 浅拷贝 vs 深拷贝

**浅拷贝**：
- 复制对象本身
- 基本类型：复制值
- 引用类型：复制引用（共享）
- 实现：`super.clone()`

**深拷贝**：
- 递归复制所有对象
- 基本类型：复制值
- 引用类型：复制对象（独立）
- 实现：手动复制引用字段

### 2. clone()方法

```java
public class MyClass implements Cloneable {
    @Override
    public MyClass clone() {
        try {
            return (MyClass) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```

**注意**：
- 必须实现Cloneable接口
- super.clone()默认是浅拷贝
- 需要手动处理引用类型字段

### 3. 三种深拷贝实现

**方式1：手动复制**
```java
@Override
public Document clone() {
    Document cloned = (Document) super.clone();
    cloned.paragraphs = new ArrayList<>();
    for (Paragraph p : this.paragraphs) {
        cloned.paragraphs.add(p.clone());
    }
    return cloned;
}
```

**方式2：序列化**
```java
public Document deepClone() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(this);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);
    return (Document) ois.readObject();
}
```

**方式3：拷贝构造函数**
```java
public Document(Document other) {
    this.title = other.title;
    this.paragraphs = new ArrayList<>();
    for (Paragraph p : other.paragraphs) {
        this.paragraphs.add(new Paragraph(p));
    }
}
```

---

## 学习建议

### 学习顺序
1. **ShallowCopyDemo** - 理解浅拷贝的陷阱
2. **DeepCopyDemo** - 掌握深拷贝的实现
3. **PrototypeRegistryDemo** - 理解原型注册表的应用

### 重点理解

#### 浅拷贝的陷阱
运行ShallowCopyDemo，观察：
- 修改副本的基本类型字段 → 原对象不变 ✅
- 修改副本的引用类型字段 → 原对象也变了 ❌

#### 何时需要深拷贝？
- 引用字段会被修改 → 深拷贝
- 引用字段只读 → 浅拷贝即可

#### 原型注册表的价值
- 集中管理原型对象
- 避免重复创建原型
- 支持动态注册/移除

---

## 思考题

1. **浅拷贝问题**：
   - 为什么修改副本的List会影响原对象？
   - 如何判断一个字段需要深拷贝？

2. **性能对比**：
   - 为什么序列化方式最慢？
   - 什么场景下性能不重要？

3. **clone() vs new**：
   - 什么时候用clone()比new快？
   - 什么时候应该用new而不是clone()？

4. **原型注册表**：
   - 原型注册表解决了什么问题？
   - 如何实现线程安全的原型注册表？

---

## 常见问题

### Q1: clone()一定比new快吗？
A: 不一定。只有当对象创建成本高时，clone()才有优势。

### Q2: 所有类都应该实现Cloneable吗？
A: 不。只有需要克隆功能的类才实现。很多情况下，拷贝构造函数更清晰。

### Q3: 如何选择深拷贝方式？
A: 
- 简单对象 → 拷贝构造函数
- 一般对象 → 手动复制
- 复杂对象 → 序列化（性能不敏感）

### Q4: 原型模式 vs 工厂模式？
A:
- 对象创建成本高 → 原型模式
- 需要选择类型 → 工厂模式

---

## 扩展阅读

完成这三个示例后，建议：
1. 阅读 `doc_01.md` 了解理论细节
2. 完成 `test_01.md` 的自测题
3. 填写 `note_template.md` 巩固知识
4. 思考：Java中哪些类使用了原型模式？

---

**记住**：
> **创建成本高又高，**  
> **原型克隆效率好，**  
> **浅拷贝要小心，**  
> **深拷贝更可靠。**
