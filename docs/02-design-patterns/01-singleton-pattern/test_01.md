# 单例模式 - 自测题

## 一、概念理解（选择题）

### 1. 单例模式的核心目的是什么？

A. 提高性能  
B. 确保一个类只有一个实例，并提供全局访问点  
C. 简化代码  
D. 实现线程安全

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 单例模式的核心目的是"确保一个类只有一个实例，并提供全局访问点"。
- 控制实例数量为1
- 节省系统资源
- 提供统一的访问方式

其他选项是单例的附带效果，而非核心目的。
</details>

---

### 2. 以下哪种实现方式最推荐？

A. 懒汉式（不同步）  
B. 懒汉式（同步方法）  
C. 静态内部类  
D. 饿汉式

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: 静态内部类是最推荐的实现方式：
- ✅ 线程安全（类加载机制保证）
- ✅ 延迟加载（只在调用时加载）
- ✅ 实现简单，无需同步
- ✅ 性能好

其他选项的问题：
- A: 线程不安全
- B: 性能差（每次调用都同步）
- D: 不支持延迟加载

如果需要防止反射和序列化攻击，首选枚举。
</details>

---

### 3. DCL（双重检查锁定）为什么需要volatile？

A. 提高性能  
B. 防止指令重排序  
C. 实现延迟加载  
D. 提供全局访问

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: volatile的作用是防止指令重排序。

`instance = new Singleton()` 分三步：
1. 分配内存
2. 初始化对象
3. instance指向内存

JVM可能重排序为 1→3→2，导致其他线程获取到未初始化的对象。

**问题场景**：
```
线程A: instance指向内存（但未初始化）
线程B: 检查instance != null，直接返回
线程B: 使用未初始化的对象 → 出错！
```

volatile禁止指令重排序，保证正确性。
</details>

---

## 二、代码分析题

### 4. 判断以下单例实现是否正确

```java
public class Singleton {
    private static Singleton instance = new Singleton();
    
    public Singleton() {
    }
    
    public static Singleton getInstance() {
        return instance;
    }
}
```

<details>
<summary>参考答案</summary>

**判断**: ❌ 不正确

**问题**：
1. **构造函数是public**：外部可以直接new创建新实例，破坏单例
2. **instance应该是final**：防止被修改

**正确实现**：
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    
    // 私有构造函数
    private Singleton() {
    }
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

**关键点**：
- 构造函数必须private
- 静态实例建议final（饿汉式）
- 提供公共静态访问方法
</details>

---

### 5. 手写线程安全的单例（静态内部类方式）

要求：
- 线程安全
- 延迟加载
- 性能好

<details>
<summary>参考答案</summary>

```java
public class Singleton {
    // 私有构造函数
    private Singleton() {
    }
    
    // 静态内部类
    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }
    
    // 公共访问方法
    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

**原理**：
- 外部类加载时，内部类不会被加载
- 只有调用getInstance()时，才加载内部类
- 类加载机制保证线程安全
- 天然延迟加载

**优点**：
- ✅ 线程安全
- ✅ 延迟加载
- ✅ 无需同步
- ✅ 实现简单
- ✅ 性能好

这是最优雅的单例实现方式！
</details>

---

## 三、场景题

### 6. 以下哪些场景适合使用单例模式？

A. 配置管理器  
B. 用户对象  
C. 日志管理器  
D. 订单对象

<details>
<summary>参考答案</summary>

**答案**: A和C

**适合单例**：
- **A. 配置管理器** ✅
  - 全局唯一
  - 需要频繁访问
  - 创建成本高（加载配置文件）

- **C. 日志管理器** ✅
  - 全局唯一
  - 统一管理日志输出
  - 避免重复创建

**不适合单例**：
- **B. 用户对象** ❌
  - 需要多个实例（多个用户）
  - 有可变状态

- **D. 订单对象** ❌
  - 需要多个实例（多个订单）
  - 业务实体对象

**适合单例的特征**：
- 全局唯一
- 无状态或只读状态
- 需要频繁访问
- 创建成本高
</details>

---

## 总分统计

- **选择题**（1-3题）：每题 15 分，共 45 分
- **代码分析题**（4-5题）：每题 20 分，共 40 分
- **场景题**（6题）：15 分

**总分**: 100 分  
**及格线**: 80 分

---

## 学习建议

**核心要记住**：
1. **单例三要素**：私有构造、私有静态实例、公共访问方法
2. **推荐实现**：静态内部类（优雅）、枚举（最安全）
3. **线程安全**：懒汉式需要同步，饿汉式和静态内部类天然安全
4. **DCL需要volatile**：防止指令重排序

**实践口诀**：
> 单例模式保唯一，  
> 私有构造是关键，  
> 静态内部类最优雅，  
> 枚举方式最安全。

**重点掌握**：
- 为什么懒汉式线程不安全？
- 静态内部类何时加载？
- DCL为什么需要volatile？
- 如何防止反射和序列化攻击？
