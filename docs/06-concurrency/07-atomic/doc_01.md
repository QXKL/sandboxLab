# 原子类与CAS

## 一、为什么需要原子类？

### 问题：普通变量的线程安全

```java
// ❌ 线程不安全
private int count = 0;

public void increment() {
    count++;  // 不是原子操作
    // 实际是3步：
    // 1. 读取count
    // 2. count+1
    // 3. 写回count
}

// 100个线程同时increment
// 期望：count=100
// 实际：count < 100（丢失更新）
```

### 传统解决方案的问题

```java
// 方案1：synchronized（性能差）
public synchronized void increment() {
    count++;  // 加锁，阻塞其他线程
}

// 方案2：volatile（不保证原子性）
private volatile int count = 0;
public void increment() {
    count++;  // 仍然不是原子操作
}
```

### 解决方案：原子类

```java
// ✅ 原子类（无锁，高性能）
private AtomicInteger count = new AtomicInteger(0);

public void increment() {
    count.incrementAndGet();  // 原子操作
}

// 100个线程同时increment
// 结果：count=100（正确）
```

## 二、CAS（Compare-And-Swap）

### 定义

**CAS**：比较并交换，是实现原子类的核心算法。

```
CAS(内存位置V, 预期值A, 新值B)

伪代码：
if (V == A) {
    V = B;
    return true;
} else {
    return false;
}

整个过程是原子的（CPU指令级别）
```

### CAS示例

```java
AtomicInteger count = new AtomicInteger(10);

// 线程1
count.compareAndSet(10, 20);  // 期望10，更新为20
// 返回true，count=20

// 线程2（同时执行）
count.compareAndSet(10, 30);  // 期望10，更新为30
// 返回false（因为count已经是20，不是10）
```

### CAS的优缺点

**优点**：
- 无锁算法，不阻塞线程
- 性能高（相比synchronized）
- 避免死锁

**缺点**：
- ABA问题
- 循环开销（自旋）
- 只能保证单个变量的原子性

### ABA问题

**场景**：
```
时刻1：变量V=A
时刻2：线程1读取V=A
时刻3：线程2将V改为B
时刻4：线程2又将V改回A
时刻5：线程1执行CAS(V, A, C)
       → 成功（但V经历了A→B→A）
```

**问题**：线程1以为V没变，实际上V已经被修改过。

**解决方案**：AtomicStampedReference（带版本号）

```java
AtomicStampedReference<Integer> ref = 
    new AtomicStampedReference<>(10, 0);  // 值=10, 版本=0

int[] stampHolder = new int[1];
Integer value = ref.get(stampHolder);  // 获取值和版本
int stamp = stampHolder[0];

// CAS：比较值和版本
ref.compareAndSet(10, 20, stamp, stamp + 1);
```

## 三、原子类分类

### 1. 基本类型原子类

#### AtomicInteger

```java
AtomicInteger count = new AtomicInteger(0);

// 获取值
int value = count.get();

// 设置值
count.set(10);

// 原子增加
int newValue = count.incrementAndGet();  // ++count
int oldValue = count.getAndIncrement();  // count++

// 原子减少
count.decrementAndGet();  // --count
count.getAndDecrement();  // count--

// 原子加法
count.addAndGet(5);  // count += 5
count.getAndAdd(5);  // 先返回，再+5

// CAS操作
boolean success = count.compareAndSet(10, 20);  // 期望10，更新为20

// 更新操作（JDK 8）
count.updateAndGet(x -> x * 2);  // count = count * 2
count.getAndUpdate(x -> x * 2);

count.accumulateAndGet(10, (x, y) -> x + y);  // count = count + 10
```

#### AtomicLong

```java
AtomicLong counter = new AtomicLong(0);

counter.incrementAndGet();
counter.addAndGet(100L);
```

#### AtomicBoolean

```java
AtomicBoolean flag = new AtomicBoolean(false);

// CAS操作
if (flag.compareAndSet(false, true)) {
    // 只有一个线程会进入
    // 类似于tryLock
}

// 获取并设置
boolean oldValue = flag.getAndSet(true);
```

### 2. 数组类型原子类

#### AtomicIntegerArray

```java
AtomicIntegerArray array = new AtomicIntegerArray(10);

// 设置索引0的值为100
array.set(0, 100);

// 原子增加索引1的值
array.incrementAndGet(1);

// CAS操作
array.compareAndSet(0, 100, 200);  // 索引0，期望100，更新为200
```

#### AtomicLongArray / AtomicReferenceArray

```java
AtomicLongArray longArray = new AtomicLongArray(10);
AtomicReferenceArray<User> userArray = new AtomicReferenceArray<>(10);
```

### 3. 引用类型原子类

#### AtomicReference

```java
AtomicReference<User> userRef = new AtomicReference<>();

User user1 = new User("张三");
userRef.set(user1);

User user2 = new User("李四");
userRef.compareAndSet(user1, user2);  // 替换用户对象

User currentUser = userRef.get();
```

#### AtomicStampedReference（解决ABA问题）

```java
AtomicStampedReference<Integer> ref = 
    new AtomicStampedReference<>(100, 0);  // 值=100, 版本=0

int[] stampHolder = new int[1];
Integer value = ref.get(stampHolder);
int stamp = stampHolder[0];

// CAS：同时比较值和版本
boolean success = ref.compareAndSet(
    100,         // 期望值
    200,         // 新值
    stamp,       // 期望版本
    stamp + 1    // 新版本
);
```

#### AtomicMarkableReference（标记版本）

```java
AtomicMarkableReference<User> ref = 
    new AtomicMarkableReference<>(user, false);

boolean[] markHolder = new boolean[1];
User currentUser = ref.get(markHolder);
boolean mark = markHolder[0];

ref.compareAndSet(user, newUser, mark, !mark);
```

### 4. 字段更新器

#### AtomicIntegerFieldUpdater

```java
public class User {
    volatile int age;  // 必须volatile
    
    private static final AtomicIntegerFieldUpdater<User> AGE_UPDATER =
        AtomicIntegerFieldUpdater.newUpdater(User.class, "age");
    
    public void incrementAge() {
        AGE_UPDATER.incrementAndGet(this);
    }
    
    public boolean setAge(int expect, int update) {
        return AGE_UPDATER.compareAndSet(this, expect, update);
    }
}
```

**使用场景**：
- 对象已经存在，不能改成AtomicInteger
- 节省内存（不用创建AtomicInteger对象）

### 5. 累加器（JDK 8，高性能）

#### LongAdder

```java
LongAdder counter = new LongAdder();

// 增加
counter.increment();  // +1
counter.add(10);      // +10

// 获取总和
long sum = counter.sum();

// 重置
counter.reset();
```

**性能对比**：
```
AtomicLong：
- 所有线程竞争同一个变量
- 高并发下CAS失败率高
- 需要不断自旋重试

LongAdder：
- 分段累加（类似ConcurrentHashMap）
- 每个线程累加到自己的Cell
- 最后汇总所有Cell
- 减少竞争，性能更高
```

**原理**：
```
LongAdder内部结构：
  base（基础值）
  Cell[] cells（分段数组）
    ├─ Cell[0]: value1
    ├─ Cell[1]: value2
    ├─ Cell[2]: value3
    └─ Cell[3]: value4

线程1 → Cell[0]累加
线程2 → Cell[1]累加
线程3 → Cell[2]累加
线程4 → Cell[3]累加

sum() = base + Cell[0] + Cell[1] + Cell[2] + Cell[3]
```

#### LongAccumulator

```java
// 自定义累加规则
LongAccumulator accumulator = new LongAccumulator((x, y) -> x + y, 0);

accumulator.accumulate(10);
accumulator.accumulate(20);

long result = accumulator.get();  // 30
```

#### DoubleAdder / DoubleAccumulator

```java
DoubleAdder adder = new DoubleAdder();
adder.add(1.5);
adder.add(2.5);
double sum = adder.sum();  // 4.0
```

## 四、原子类的应用场景

### 场景1：计数器

```java
public class Counter {
    private AtomicLong counter = new AtomicLong(0);
    
    public void increment() {
        counter.incrementAndGet();
    }
    
    public long getCount() {
        return counter.get();
    }
}
```

### 场景2：ID生成器

```java
public class IdGenerator {
    private AtomicLong idGenerator = new AtomicLong(0);
    
    public long nextId() {
        return idGenerator.incrementAndGet();
    }
}
```

### 场景3：无锁栈

```java
public class LockFreeStack<T> {
    private AtomicReference<Node<T>> top = new AtomicReference<>();
    
    private static class Node<T> {
        T value;
        Node<T> next;
        
        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }
    
    public void push(T value) {
        Node<T> newHead = new Node<>(value, null);
        Node<T> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
    }
    
    public T pop() {
        Node<T> oldHead;
        Node<T> newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.value;
    }
}
```

### 场景4：限流器

```java
public class RateLimiter {
    private AtomicInteger permits;
    
    public RateLimiter(int permitsPerSecond) {
        this.permits = new AtomicInteger(permitsPerSecond);
        
        // 每秒补充permits
        ScheduledExecutorService scheduler = 
            Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            permits.set(permitsPerSecond);
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    public boolean tryAcquire() {
        int current;
        do {
            current = permits.get();
            if (current <= 0) {
                return false;
            }
        } while (!permits.compareAndSet(current, current - 1));
        return true;
    }
}
```

## 五、原子类性能对比

### 性能测试

```java
// synchronized
long start = System.currentTimeMillis();
for (int i = 0; i < 10000000; i++) {
    synchronized (lock) {
        count++;
    }
}
long time1 = System.currentTimeMillis() - start;

// AtomicInteger
start = System.currentTimeMillis();
for (int i = 0; i < 10000000; i++) {
    atomicCount.incrementAndGet();
}
long time2 = System.currentTimeMillis() - start;

// LongAdder
start = System.currentTimeMillis();
for (int i = 0; i < 10000000; i++) {
    longAdder.increment();
}
long time3 = System.currentTimeMillis() - start;
```

**性能排序**（高并发）：
```
LongAdder > AtomicLong > synchronized

低并发：AtomicLong ≈ LongAdder > synchronized
高并发：LongAdder >> AtomicLong > synchronized
```

## 六、原子类最佳实践

### 实践1：选择合适的类型

```
计数器 → LongAdder（高并发）或 AtomicLong（低并发）
布尔标志 → AtomicBoolean
引用对象 → AtomicReference
数组元素 → AtomicIntegerArray
字段更新 → AtomicIntegerFieldUpdater
```

### 实践2：避免过度自旋

```java
// ❌ 可能导致CPU空转
while (!atomicBoolean.compareAndSet(false, true)) {
    // 空循环
}

// ✅ 添加退避策略
int retries = 0;
while (!atomicBoolean.compareAndSet(false, true)) {
    if (++retries > 100) {
        Thread.sleep(1);  // 休息一下
        retries = 0;
    }
}
```

### 实践3：合理使用LongAdder

```java
// 高并发计数 → LongAdder
LongAdder counter = new LongAdder();

// 需要精确值 → AtomicLong
AtomicLong sequence = new AtomicLong();
```

**注意**：LongAdder.sum()不是强一致性，并发修改时可能不准确。

## 七、小结

**核心要点**：

1. **原子类定义**：
   - 提供原子操作的类
   - 基于CAS实现
   - 无锁，高性能

2. **CAS原理**：
   - Compare-And-Swap
   - CPU指令级别的原子操作
   - 优点：无锁；缺点：ABA问题、自旋开销

3. **原子类分类**：
   - 基本类型：AtomicInteger、AtomicLong、AtomicBoolean
   - 数组类型：AtomicIntegerArray
   - 引用类型：AtomicReference、AtomicStampedReference
   - 字段更新器：AtomicIntegerFieldUpdater
   - 累加器：LongAdder、LongAccumulator

4. **性能对比**：
   - 高并发：LongAdder > AtomicLong > synchronized
   - 低并发：AtomicLong ≈ LongAdder > synchronized

5. **应用场景**：
   - 计数器、ID生成器
   - 无锁数据结构
   - 限流器

**记忆口诀**：
- 原子类基于CAS，无锁高性能
- 基本引用数组类，累加器更快
- 计数用LongAdder，标志用AtomicBoolean
- ABA问题要注意，版本号来解决

---

💡 **提示**：原子类是高性能并发编程的利器，高并发场景下优先使用LongAdder！
