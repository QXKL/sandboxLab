# 原子类与CAS - 测试题

## 一、选择题

### 1. CAS的全称是什么？
A. Compare-And-Set  
B. Compare-And-Swap  
C. Check-And-Set  
D. Check-And-Swap

**答案**：B

**解析**：CAS是Compare-And-Swap（比较并交换），是实现原子类的核心算法。

---

### 2. 以下哪个不是原子类的优点？
A. 无锁算法  
B. 高性能  
C. 保证多个变量的原子性  
D. 避免死锁

**答案**：C

**解析**：CAS只能保证单个变量的原子性，不能保证多个变量的原子性。

---

### 3. AtomicInteger.incrementAndGet()相当于什么操作？
A. count++  
B. ++count  
C. count += 1  
D. count = count + 1

**答案**：B

**解析**：incrementAndGet()是先增加再返回，相当于++count；getAndIncrement()相当于count++。

---

### 4. 高并发场景下，以下哪个性能最好？
A. synchronized  
B. AtomicLong  
C. LongAdder  
D. volatile

**答案**：C

**解析**：高并发下，LongAdder通过分段累加减少竞争，性能最好。

---

### 5. ABA问题是什么？
A. 变量从A变到B  
B. 变量从A变到B再变回A，但CAS认为没变  
C. 两个线程同时修改变量  
D. 变量值为A或B

**答案**：B

**解析**：ABA问题是指变量经历了A→B→A的变化，但CAS只比较值，认为没变。

---

### 6. 如何解决ABA问题？
A. 使用synchronized  
B. 使用volatile  
C. 使用AtomicStampedReference  
D. 使用AtomicInteger

**答案**：C

**解析**：AtomicStampedReference通过版本号（stamp）解决ABA问题，同时比较值和版本。

---

### 7. AtomicIntegerFieldUpdater要求字段必须是什么类型？
A. public  
B. static  
C. volatile  
D. final

**答案**：C

**解析**：AtomicIntegerFieldUpdater要求字段必须是volatile修饰的，保证可见性。

---

### 8. LongAdder的sum()方法返回的值是否精确？
A. 一定精确  
B. 并发修改时可能不精确  
C. 永远不精确  
D. 取决于线程数

**答案**：B

**解析**：LongAdder.sum()在并发修改时可能不精确，因为不是强一致性的。

---

### 9. 以下代码count最终的值是多少？
```java
AtomicInteger count = new AtomicInteger(10);
count.compareAndSet(10, 20);
count.compareAndSet(10, 30);
```
A. 10  
B. 20  
C. 30  
D. 不确定

**答案**：B

**解析**：第一次CAS成功，count=20；第二次CAS期望10但实际是20，失败，count仍为20。

---

### 10. CAS的缺点不包括？
A. ABA问题  
B. 循环开销（自旋）  
C. 会阻塞线程  
D. 只能保证单个变量原子性

**答案**：C

**解析**：CAS是无锁算法，不会阻塞线程，但可能导致自旋（循环重试）。

---

## 二、填空题

### 1. CAS操作包含3个操作数：内存位置V、________、________。

**答案**：预期值A、新值B

---

### 2. AtomicInteger底层使用________算法实现原子操作。

**答案**：CAS

---

### 3. LongAdder通过________累加的方式减少竞争，提高性能。

**答案**：分段

---

### 4. 要解决ABA问题，可以使用________类，它通过________来区分不同状态。

**答案**：AtomicStampedReference、版本号（stamp）

---

### 5. 高并发场景下，________的性能优于AtomicLong，低并发场景下两者性能________。

**答案**：LongAdder、相近

---

## 三、判断题

### 1. AtomicInteger可以保证count++操作的原子性。（ ）

**答案**：✓

**解析**：AtomicInteger.incrementAndGet()是原子操作，保证了count++的原子性。

---

### 2. CAS操作一定会成功。（ ）

**答案**：✗

**解析**：CAS可能失败（期望值与实际值不匹配），失败后需要重试。

---

### 3. LongAdder适合所有场景。（ ）

**答案**：✗

**解析**：LongAdder适合高并发计数场景，但sum()不保证强一致性，不适合需要精确值的场景。

---

### 4. AtomicReference可以原子地更新对象引用。（ ）

**答案**：✓

**解析**：AtomicReference提供了原子地更新对象引用的能力。

---

### 5. CAS算法会导致线程阻塞。（ ）

**答案**：✗

**解析**：CAS是无锁算法，不会阻塞线程，但可能导致自旋（循环重试）。

---

## 四、简答题

### 1. 解释CAS算法的原理，以及为什么它能保证原子性。

**答案**：

**CAS（Compare-And-Swap）原理**：

**定义**：比较并交换，是一种无锁的原子操作。

**三个操作数**：
- V：内存位置（变量地址）
- A：预期值（Expected）
- B：新值（New）

**操作逻辑**：
```
伪代码：
if (V == A) {
    V = B;
    return true;  // 成功
} else {
    return false;  // 失败
}
```

**示例**：
```java
AtomicInteger count = new AtomicInteger(10);

// 线程1执行CAS(count, 10, 20)
// 1. 读取count的值：10
// 2. 比较：10 == 10，相等
// 3. 更新：count = 20
// 4. 返回true

// 线程2同时执行CAS(count, 10, 30)
// 1. 读取count的值：20（线程1已更新）
// 2. 比较：20 != 10，不相等
// 3. 不更新
// 4. 返回false
```

---

**为什么能保证原子性**：

**1. CPU指令级别的原子性**

CAS在硬件层面由单条CPU指令实现：
```
x86架构：CMPXCHG指令
ARM架构：LDREX/STREX指令
```

单条CPU指令是原子的，不会被中断。

**2. 总线锁定或缓存锁定**

**总线锁定（Bus Lock）**：
```
早期实现：
1. CPU发出LOCK#信号
2. 锁定系统总线
3. 其他CPU无法访问内存
4. 执行CAS指令
5. 释放总线锁

缺点：锁定整个总线，性能差
```

**缓存锁定（Cache Lock）**：
```
现代实现：
1. 使用缓存一致性协议（MESI）
2. 只锁定缓存行（Cache Line）
3. 其他CPU的缓存行失效
4. 执行CAS指令
5. 释放缓存锁

优点：只锁定缓存行，性能好
```

**3. Java层面的实现**

```java
// AtomicInteger的实现（简化）
public final int incrementAndGet() {
    for (;;) {  // 无限循环，直到成功
        int current = get();  // 1. 读取当前值
        int next = current + 1;  // 2. 计算新值
        if (compareAndSet(current, next)) {  // 3. CAS更新
            return next;  // 4. 成功，返回
        }
        // 5. 失败，继续循环重试
    }
}

// compareAndSet调用native方法
public final native boolean compareAndSwapInt(
    Object o,    // 对象
    long offset, // 字段偏移量
    int expected, // 预期值
    int x        // 新值
);
```

---

**为什么是原子的总结**：

1. **单条CPU指令**：比较和交换在一条指令内完成
2. **硬件保证**：CPU通过总线锁或缓存锁保证原子性
3. **不可中断**：指令执行过程不会被其他线程中断
4. **无锁实现**：不需要操作系统级别的互斥锁

---

### 2. 什么是ABA问题？如何解决？

**答案**：

**ABA问题定义**：

在使用CAS操作时，变量的值从A变成B，再变回A，但CAS只比较值，认为没有发生变化，导致逻辑错误。

---

**问题场景**：

```java
AtomicInteger value = new AtomicInteger(100);

// 时刻1：线程1读取value=100
int current = value.get();  // 100

// 时刻2：线程2执行
value.compareAndSet(100, 50);  // 100 → 50
value.compareAndSet(50, 100);  // 50 → 100

// 时刻3：线程1执行CAS
value.compareAndSet(current, 200);  // 成功！
// 线程1以为value没变过，实际上经历了 100→50→100
```

---

**实际问题示例：无锁栈**

```java
// 栈结构：A → B → C
// 线程1准备pop A

// 1. 线程1读取top=A
Node<String> oldTop = top.get();  // A
Node<String> newTop = oldTop.next;  // B

// 2. 线程2执行
pop();  // 弹出A，top=B
pop();  // 弹出B，top=C
push(A);  // 压入A，top=A

// 3. 线程1执行CAS
top.compareAndSet(oldTop, newTop);  // 成功！
// 问题：top从A变成B，但B已经被弹出了
// → B的next可能指向已释放的内存
// → 可能导致程序崩溃
```

---

**解决方案**：

**方案1：AtomicStampedReference（版本号）**

```java
// 带版本号的引用
AtomicStampedReference<Integer> ref = 
    new AtomicStampedReference<>(100, 0);  // 值=100, 版本=0

// 线程1
int[] stampHolder = new int[1];
Integer value = ref.get(stampHolder);  // 获取值和版本
int stamp = stampHolder[0];  // 版本=0

// 线程2修改
ref.compareAndSet(100, 50, 0, 1);   // 100→50, 版本0→1
ref.compareAndSet(50, 100, 1, 2);   // 50→100, 版本1→2

// 线程1执行CAS
boolean success = ref.compareAndSet(
    100,      // 期望值
    200,      // 新值
    stamp,    // 期望版本=0
    stamp + 1 // 新版本=1
);
// 失败！因为实际版本是2，不是0
```

**方案2：AtomicMarkableReference（标记）**

```java
AtomicMarkableReference<Node> ref = 
    new AtomicMarkableReference<>(node, false);

boolean[] markHolder = new boolean[1];
Node currentNode = ref.get(markHolder);
boolean mark = markHolder[0];

// 标记为已删除
ref.compareAndSet(currentNode, currentNode, mark, true);
```

**方案3：业务逻辑避免**

```
某些场景下，ABA问题不影响业务逻辑：
- 简单计数器（10→20→10，结果仍然是10）
- 布尔标志（true→false→true）

这些场景下，即使发生ABA，业务逻辑仍然正确。
```

---

**总结**：

| 特性 | AtomicReference | AtomicStampedReference |
|-----|----------------|----------------------|
| **比较内容** | 只比较值 | 比较值和版本 |
| **ABA问题** | 存在 | 解决 |
| **性能** | 高 | 稍低（多一次版本比较） |
| **适用场景** | 简单场景 | 需要避免ABA |

---

### 3. 比较AtomicLong和LongAdder的区别，以及各自的适用场景。

**答案**：

**实现原理对比**：

**AtomicLong**：
```
单个变量：
  value (volatile long)

所有线程竞争同一个变量：
线程1 ─┐
线程2 ─┼→ value (CAS)
线程3 ─┘

高并发下：
- CAS冲突多
- 重试次数多
- 性能下降
```

**LongAdder**：
```
分段累加：
  base (基础值)
  Cell[] cells (分段数组)
    ├─ Cell[0]: value1
    ├─ Cell[1]: value2
    ├─ Cell[2]: value3
    └─ Cell[3]: value4

线程分散到不同Cell：
线程1 → Cell[0]
线程2 → Cell[1]
线程3 → Cell[2]
线程4 → Cell[3]

最终求和：
sum = base + Cell[0] + Cell[1] + Cell[2] + Cell[3]

高并发下：
- 减少竞争
- CAS成功率高
- 性能更好
```

---

**详细对比**：

| 特性 | AtomicLong | LongAdder |
|-----|-----------|-----------|
| **实现原理** | 单个变量CAS | 分段累加 |
| **低并发性能** | 高 | 相近 |
| **高并发性能** | 中 | 高 |
| **内存占用** | 小（一个long） | 大（base + Cell数组） |
| **精确性** | 强一致性 | 最终一致性 |
| **线程竞争** | 所有线程竞争一个变量 | 线程分散到不同Cell |
| **适用场景** | 需要精确值 | 高并发计数 |

---

**性能测试**（1000万次操作）：

```java
// 单线程
AtomicLong: 100ms
LongAdder:  100ms
差异不大

// 10个线程
AtomicLong: 500ms
LongAdder:  200ms
LongAdder快2.5倍

// 100个线程
AtomicLong: 5000ms
LongAdder:  800ms
LongAdder快6倍
```

---

**适用场景**：

**AtomicLong适用于**：

1. **需要精确值**：
```java
// 序列号生成器
AtomicLong sequence = new AtomicLong(0);
long id = sequence.incrementAndGet();  // 必须精确
```

2. **需要CAS操作**：
```java
// 需要compareAndSet
atomicLong.compareAndSet(100, 200);
```

3. **低并发场景**：
```java
// 并发度不高，AtomicLong足够
```

---

**LongAdder适用于**：

1. **高并发计数器**：
```java
// 统计访问次数
LongAdder visitCounter = new LongAdder();
visitCounter.increment();  // 高并发下性能好
```

2. **不需要精确值**：
```java
// 实时统计，允许略微延迟
long count = longAdder.sum();  // 可能不精确
```

3. **只有累加操作**：
```java
// 只需要add/increment
longAdder.add(10);
longAdder.increment();
```

---

**注意事项**：

**LongAdder的sum()不保证精确**：
```java
LongAdder adder = new LongAdder();

// 线程1
adder.increment();  // Cell[0] += 1

// 线程2（同时）
long sum = adder.sum();  // 读取时线程1还没写入
// sum可能不包含线程1的修改
```

**解决方案**：
```java
// 需要精确值时，先停止更新
// 然后再调用sum()

// 或使用AtomicLong
```

---

**选择建议**：

```
高并发 + 只统计 → LongAdder
需要CAS操作 → AtomicLong
需要精确值 → AtomicLong
低并发 → AtomicLong（简单）
```

---

### 4. 使用AtomicInteger实现一个线程安全的单例模式。

**答案**：

```java
public class Singleton {
    private static final AtomicReference<Singleton> INSTANCE = 
        new AtomicReference<>();
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        // 快速路径：已创建实例
        Singleton instance = INSTANCE.get();
        if (instance != null) {
            return instance;
        }
        
        // 慢速路径：创建实例
        instance = new Singleton();
        
        // CAS设置实例
        if (INSTANCE.compareAndSet(null, instance)) {
            // 成功：当前线程创建了实例
            return instance;
        } else {
            // 失败：其他线程已创建实例
            return INSTANCE.get();
        }
    }
    
    // 测试
    public static void main(String[] args) throws InterruptedException {
        // 100个线程同时获取实例
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                Singleton instance = Singleton.getInstance();
                System.out.println(instance.hashCode());
            }).start();
        }
        
        Thread.sleep(2000);
        // 所有输出的hashCode都相同，说明是同一个实例
    }
}
```

**说明**：
1. 使用AtomicReference存储单例实例
2. compareAndSet保证只有一个线程能成功创建实例
3. 无锁实现，性能优于synchronized
4. 线程安全，保证单例

---

## 五、编程题

### 使用AtomicInteger实现一个线程安全的ID生成器，支持并发获取唯一ID。

**答案**：

```java
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    // 使用AtomicLong存储当前ID
    private final AtomicLong currentId;
    
    /**
     * 构造函数
     * @param startId 起始ID
     */
    public IdGenerator(long startId) {
        this.currentId = new AtomicLong(startId);
    }
    
    /**
     * 默认从0开始
     */
    public IdGenerator() {
        this(0);
    }
    
    /**
     * 获取下一个ID
     */
    public long nextId() {
        return currentId.incrementAndGet();
    }
    
    /**
     * 批量获取ID
     * @param count 数量
     * @return 起始ID
     */
    public long nextBatch(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        return currentId.addAndGet(count) - count + 1;
    }
    
    /**
     * 获取当前ID（不增加）
     */
    public long getCurrentId() {
        return currentId.get();
    }
    
    /**
     * 重置ID
     */
    public void reset(long newId) {
        currentId.set(newId);
    }
    
    // 测试
    public static void main(String[] args) throws InterruptedException {
        IdGenerator generator = new IdGenerator();
        
        // 100个线程并发获取ID
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    long id = generator.nextId();
                }
            }).start();
        }
        
        Thread.sleep(3000);
        
        // 期望：100 * 1000 = 100000
        System.out.println("最终ID：" + generator.getCurrentId());  // 100000
        
        // 测试批量获取
        long batchStart = generator.nextBatch(100);
        System.out.println("批量起始ID：" + batchStart);  // 100001
        System.out.println("当前ID：" + generator.getCurrentId());  // 100100
    }
}
```

---

💡 **提示**：原子类是高性能并发编程的基础，高并发计数器优先使用LongAdder！
