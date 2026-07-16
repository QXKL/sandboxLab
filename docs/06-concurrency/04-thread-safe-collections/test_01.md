# 线程安全集合 - 测试题

## 一、选择题

### 1. 以下哪个集合类是线程安全的？
A. HashMap  
B. ArrayList  
C. ConcurrentHashMap  
D. HashSet

**答案**：C

**解析**：ConcurrentHashMap是线程安全的Map实现，HashMap、ArrayList、HashSet都不是线程安全的。

---

### 2. ConcurrentHashMap不允许什么？
A. 并发读  
B. 并发写  
C. null key和null value  
D. 重复key

**答案**：C

**解析**：ConcurrentHashMap不允许null key和null value，HashMap允许一个null key和多个null value。

---

### 3. CopyOnWriteArrayList适合什么场景？
A. 写多读少  
B. 读多写少  
C. 读写相当  
D. 只写不读

**答案**：B

**解析**：CopyOnWriteArrayList写时复制整个数组，开销大，只适合读多写少的场景。

---

### 4. 以下关于CopyOnWriteArrayList的说法错误的是？
A. 读操作不加锁  
B. 写操作会复制数组  
C. 遍历时可能读到旧数据  
D. 适合频繁写入的场景

**答案**：D

**解析**：CopyOnWriteArrayList不适合频繁写入，每次写入都要复制整个数组，开销极大。

---

### 5. BlockingQueue的put()方法在队列满时会怎样？
A. 抛异常  
B. 返回false  
C. 阻塞  
D. 返回null

**答案**：C

**解析**：put()在队列满时会阻塞等待，直到有空间可用。

---

### 6. 以下哪个队列没有容量限制？
A. ArrayBlockingQueue  
B. LinkedBlockingQueue(默认)  
C. SynchronousQueue  
D. ArrayBlockingQueue和LinkedBlockingQueue都有容量限制

**答案**：B

**解析**：LinkedBlockingQueue默认容量为Integer.MAX_VALUE，相当于无界队列。

---

### 7. SynchronousQueue的容量是多少？
A. 0  
B. 1  
C. 10  
D. Integer.MAX_VALUE

**答案**：A

**解析**：SynchronousQueue没有容量，每个put()必须等待一个take()。

---

### 8. ConcurrentHashMap在JDK 1.8使用什么锁机制？
A. 全表锁  
B. 分段锁  
C. CAS + synchronized  
D. 无锁

**答案**：C

**解析**：JDK 1.8的ConcurrentHashMap使用CAS + synchronized替代了分段锁，锁粒度更细。

---

### 9. 以下哪个操作在ConcurrentHashMap中是原子的？
A. get()  
B. putIfAbsent()  
C. size()  
D. 以上都是

**答案**：B

**解析**：putIfAbsent()是原子操作，get()和size()虽然线程安全但不是严格的原子操作。

---

### 10. Vector和CopyOnWriteArrayList的主要区别是？
A. 完全相同  
B. Vector所有方法加synchronized，CopyOnWriteArrayList写时复制  
C. Vector线程安全，CopyOnWriteArrayList不安全  
D. 没有区别

**答案**：B

**解析**：Vector所有方法都加synchronized，读写都互斥；CopyOnWriteArrayList写时复制，读操作不加锁。

---

## 二、填空题

### 1. ConcurrentHashMap在JDK 1.7使用________锁，在JDK 1.8使用________。

**答案**：分段（Segment）、CAS + synchronized

---

### 2. CopyOnWriteArrayList的写操作会复制________，读操作________（加锁/不加锁）。

**答案**：整个数组、不加锁

---

### 3. BlockingQueue的四组方法分别是：抛异常（add/remove）、返回特殊值（offer/poll）、________、超时。

**答案**：阻塞（put/take）

---

### 4. SynchronousQueue适合________场景，每个put()必须等待一个________。

**答案**：线程间直接传递数据、take()

---

### 5. 如果需要一个线程安全的有界队列用于生产者-消费者模式，应该使用________。

**答案**：ArrayBlockingQueue（或LinkedBlockingQueue）

---

## 三、判断题

### 1. ConcurrentHashMap允许null key。（ ）

**答案**：✗

**解析**：ConcurrentHashMap不允许null key和null value。

---

### 2. CopyOnWriteArrayList遍历时不会抛ConcurrentModificationException。（ ）

**答案**：✓

**解析**：因为遍历的是快照数组，即使其他线程修改也不影响当前遍历。

---

### 3. Vector已经过时，不推荐使用。（ ）

**答案**：✓

**解析**：Vector性能差，应该使用CopyOnWriteArrayList或Collections.synchronizedList。

---

### 4. ConcurrentHashMap的size()方法返回的是精确值。（ ）

**答案**：✗

**解析**：由于并发修改，size()可能不是实时精确值，只是一个估算值。

---

### 5. DelayQueue中的元素必须实现Delayed接口。（ ）

**答案**：✓

**解析**：DelayQueue要求元素实现Delayed接口，以确定延迟时间。

---

## 四、简答题

### 1. 说明ConcurrentHashMap在JDK 1.7和JDK 1.8的实现区别。

**答案**：

**JDK 1.7：分段锁（Segment）**

**结构**：
```
Segment数组（默认16个）
每个Segment是一个小HashMap
Segment继承ReentrantLock
```

**并发机制**：
- 不同Segment可以并发访问
- 同一Segment内串行访问
- 最大并发度 = Segment数量（默认16）

**优点**：
- 提高并发度（相比Hashtable全表锁）

**缺点**：
- 结构复杂
- 并发度受Segment数量限制
- 扩容复杂

---

**JDK 1.8：Node数组 + 链表/红黑树**

**结构**：
```
Node数组
链表（长度 < 8）
红黑树（长度 >= 8）
```

**并发机制**：
- 使用CAS + synchronized
- 锁粒度细化到单个桶（数组元素）
- 读操作无锁（volatile）
- 写操作锁单个桶

**优点**：
1. **并发度更高**：理论上并发度 = 数组长度
2. **实现更简单**：去掉Segment层
3. **性能更好**：CAS + 细粒度锁

**对比总结**：

| 特性 | JDK 1.7 | JDK 1.8 |
|-----|---------|---------|
| **结构** | Segment + HashEntry | Node + 链表/红黑树 |
| **锁机制** | 分段锁（ReentrantLock） | CAS + synchronized |
| **锁粒度** | Segment级别 | 桶级别 |
| **最大并发度** | Segment数量（16） | 数组长度 |
| **复杂度** | 高 | 中 |
| **性能** | 好 | 更好 |

---

### 2. CopyOnWriteArrayList的写时复制原理是什么？为什么适合读多写少？

**答案**：

**写时复制（Copy-On-Write）原理**：

**写操作流程**：
```java
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();  // 1. 加锁
    try {
        Object[] elements = getArray();  // 2. 获取原数组
        int len = elements.length;
        Object[] newElements = Arrays.copyOf(elements, len + 1);  // 3. 复制新数组
        newElements[len] = e;  // 4. 在新数组上修改
        setArray(newElements);  // 5. 替换原数组引用（volatile写）
        return true;
    } finally {
        lock.unlock();  // 6. 释放锁
    }
}
```

**读操作流程**：
```java
public E get(int index) {
    return (E) getArray()[index];  // 直接读，不加锁
}
```

**关键点**：
1. 写操作复制整个数组
2. 读操作直接访问当前数组
3. 数组引用使用volatile修饰，保证可见性

---

**为什么适合读多写少**：

**优点**（读多时）：
1. **读操作无锁**：
   - 不需要synchronized
   - 不需要CAS
   - 性能极高

2. **读写不互斥**：
   - 读线程不会被写线程阻塞
   - 多个读线程可以并发

3. **遍历安全**：
   - 遍历时不会抛ConcurrentModificationException
   - 即使其他线程修改也不影响

**缺点**（写多时）：
1. **写操作开销大**：
   ```
   1000个元素的list
   add一个元素 → 复制1000个元素
   开销 = O(n)
   ```

2. **内存占用高**：
   ```
   写操作时同时存在两份数组：
   - 旧数组（读线程使用）
   - 新数组（写线程创建）
   ```

3. **不保证实时性**：
   ```
   线程A正在遍历（读旧数组）
   线程B添加元素（创建新数组）
   → 线程A看不到新元素
   ```

**适用场景**：
```java
// ✅ 监听器列表
List<EventListener> listeners = new CopyOnWriteArrayList<>();
listeners.add(listener);  // 写：少
listeners.forEach(l -> l.onEvent(event));  // 读：多

// ✅ 配置列表
List<Config> configs = new CopyOnWriteArrayList<>();

// ❌ 日志列表
List<LogEntry> logs = new CopyOnWriteArrayList<>();
logs.add(entry);  // 写：多（不适合）
```

---

### 3. BlockingQueue有哪些常用实现类？分别适用于什么场景？

**答案**：

**1. ArrayBlockingQueue**

**特点**：
- 有界队列（必须指定容量）
- 底层数组实现
- FIFO顺序
- 支持公平/非公平锁

**适用场景**：
- 生产者-消费者模式
- 需要限制队列大小
- 防止内存溢出

**示例**：
```java
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);

// 生产者
queue.put(task);  // 队列满时阻塞

// 消费者
Task task = queue.take();  // 队列空时阻塞
```

---

**2. LinkedBlockingQueue**

**特点**：
- 可选有界（默认Integer.MAX_VALUE）
- 底层链表实现
- FIFO顺序
- 吞吐量通常高于ArrayBlockingQueue

**适用场景**：
- 生产者-消费者模式
- 不确定队列大小
- 需要高吞吐量

**示例**：
```java
// 无界队列
BlockingQueue<Task> queue1 = new LinkedBlockingQueue<>();

// 有界队列
BlockingQueue<Task> queue2 = new LinkedBlockingQueue<>(1000);
```

---

**3. PriorityBlockingQueue**

**特点**：
- 无界队列
- 按优先级排序（Comparable或Comparator）
- 不保证相同优先级的顺序

**适用场景**：
- 需要按优先级处理任务
- 定时任务调度

**示例**：
```java
BlockingQueue<Task> queue = new PriorityBlockingQueue<>();

queue.put(new Task(3));  // 优先级3
queue.put(new Task(1));  // 优先级1

Task task = queue.take();  // 优先级1先出队
```

---

**4. DelayQueue**

**特点**：
- 无界队列
- 元素必须实现Delayed接口
- 只有到期的元素才能出队

**适用场景**：
- 延迟任务
- 缓存过期
- 定时任务

**示例**：
```java
public class DelayedTask implements Delayed {
    private long executeTime;
    
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(executeTime - System.currentTimeMillis(), 
                           TimeUnit.MILLISECONDS);
    }
    
    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.executeTime, 
                           ((DelayedTask) o).executeTime);
    }
}

BlockingQueue<DelayedTask> queue = new DelayQueue<>();
queue.put(new DelayedTask(System.currentTimeMillis() + 5000));
DelayedTask task = queue.take();  // 5秒后才能取出
```

---

**5. SynchronousQueue**

**特点**：
- 容量为0
- put()必须等待take()
- 直接传递，不存储

**适用场景**：
- 线程间直接传递数据
- CachedThreadPool内部使用

**示例**：
```java
BlockingQueue<Task> queue = new SynchronousQueue<>();

// 生产者
new Thread(() -> {
    queue.put(task);  // 阻塞，直到有消费者
}).start();

// 消费者
new Thread(() -> {
    Task task = queue.take();  // 阻塞，直到有生产者
}).start();
```

---

**选择建议**：

| 需求 | 推荐实现 |
|-----|---------|
| 有界队列 | ArrayBlockingQueue |
| 高吞吐量 | LinkedBlockingQueue |
| 优先级 | PriorityBlockingQueue |
| 延迟任务 | DelayQueue |
| 直接传递 | SynchronousQueue |

---

### 4. 为什么不推荐使用Vector和Hashtable？应该用什么替代？

**答案**：

**不推荐的原因**：

**1. 性能差**
```java
// Vector/Hashtable：所有方法都加synchronized
public synchronized boolean add(E e) { ... }
public synchronized E get(int index) { ... }
public synchronized int size() { ... }

// 问题：
// - 读写都互斥
// - 多线程读也要排队
// - 锁粒度太大（整个对象）
```

**2. 设计过时**
```java
// Vector/Hashtable是JDK 1.0的遗留类
// 设计理念落后，不符合现代并发编程需求
```

**3. 功能有限**
```java
// 不支持现代并发特性：
// - 原子操作（putIfAbsent、computeIfAbsent）
// - 细粒度锁
// - 无锁读
```

---

**替代方案**：

**Vector替代**：

**方案1：CopyOnWriteArrayList（读多写少）**
```java
// ✅ 推荐
List<String> list = new CopyOnWriteArrayList<>();

// 优点：
// - 读操作无锁，性能高
// - 适合读多写少
```

**方案2：Collections.synchronizedList（通用）**
```java
List<String> list = Collections.synchronizedList(new ArrayList<>());

// 优点：
// - 通用方案
// - 可包装任何List

// 缺点：
// - 性能不如CopyOnWriteArrayList
```

---

**Hashtable替代**：

**方案1：ConcurrentHashMap（推荐）**
```java
// ✅ 强烈推荐
Map<String, String> map = new ConcurrentHashMap<>();

// 优点：
// - 读操作无锁
// - 细粒度锁（JDK 1.8：桶级别）
// - 支持原子操作
// - 性能远超Hashtable
```

**方案2：Collections.synchronizedMap（兼容性）**
```java
Map<String, String> map = Collections.synchronizedMap(new HashMap<>());

// 优点：
// - 可包装任何Map

// 缺点：
// - 性能不如ConcurrentHashMap
```

---

**性能对比**：

| 操作 | Vector/Hashtable | CopyOnWrite | ConcurrentHashMap |
|-----|-----------------|-------------|-------------------|
| **读性能** | 低（加锁） | 高（无锁） | 高（无锁） |
| **写性能** | 低（加锁） | 极低（复制） | 中（细粒度锁） |
| **并发度** | 低（全表锁） | 高（读无锁） | 高（细粒度锁） |

**选择建议**：
- 多线程Map → **ConcurrentHashMap**
- 多线程List + 读多写少 → **CopyOnWriteArrayList**
- 多线程List + 读写相当 → **Collections.synchronizedList**
- 单线程 → **ArrayList/HashMap**

---

### 5. ConcurrentHashMap提供了哪些原子操作方法？它们解决了什么问题？

**答案**：

**原子操作方法**：

**1. putIfAbsent()**
```java
// 不存在才put
V putIfAbsent(K key, V value)

// 示例
map.putIfAbsent("key", "value");

// 等价于（但不是原子的）：
if (!map.containsKey("key")) {
    map.put("key", "value");
}
```

**解决问题**：避免重复插入
```java
// ❌ 非原子操作（线程不安全）
if (!map.containsKey("key")) {
    // 这里可能被其他线程插入
    map.put("key", "value");
}

// ✅ 原子操作
map.putIfAbsent("key", "value");
```

---

**2. computeIfAbsent()**
```java
// 不存在时计算并put
V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)

// 示例：缓存
User user = userCache.computeIfAbsent(userId, id -> {
    // 缓存不存在，从数据库加载（只执行一次）
    return loadUserFromDB(id);
});
```

**解决问题**：避免重复计算
```java
// ❌ 可能重复计算
if (!cache.containsKey(key)) {
    Value value = expensiveComputation(key);  // 多个线程可能都计算
    cache.put(key, value);
}

// ✅ 只计算一次
cache.computeIfAbsent(key, k -> expensiveComputation(k));
```

---

**3. computeIfPresent()**
```java
// 存在时重新计算
V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)

// 示例：更新值
map.computeIfPresent("key", (k, v) -> v + 1);
```

---

**4. compute()**
```java
// 无论是否存在都计算
V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)

// 示例：计数器
map.compute("key", (k, v) -> (v == null) ? 1 : v + 1);
```

---

**5. merge()**
```java
// 合并值
V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)

// 示例：累加
map.merge("key", 1, (oldVal, newVal) -> oldVal + newVal);
```

**解决问题**：原子更新
```java
// ❌ 非原子（线程不安全）
Integer count = map.get("key");
map.put("key", (count == null) ? 1 : count + 1);

// ✅ 原子操作
map.merge("key", 1, Integer::sum);
```

---

**应用示例**：

**示例1：线程安全的缓存**
```java
ConcurrentHashMap<String, User> cache = new ConcurrentHashMap<>();

public User getUser(String userId) {
    return cache.computeIfAbsent(userId, id -> {
        System.out.println("从数据库加载：" + id);
        return loadUserFromDB(id);
    });
}

// 多个线程同时调用，只有一个线程会从数据库加载
```

**示例2：线程安全的计数器**
```java
ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

public void increment(String key) {
    counters.computeIfAbsent(key, k -> new AtomicLong(0))
            .incrementAndGet();
}

// 或使用merge
public void increment2(String key) {
    counters.merge(key, new AtomicLong(1), (old, val) -> {
        old.incrementAndGet();
        return old;
    });
}
```

**示例3：分组聚合**
```java
ConcurrentHashMap<String, List<Item>> groups = new ConcurrentHashMap<>();

public void addItem(Item item) {
    groups.computeIfAbsent(item.getType(), k -> new CopyOnWriteArrayList<>())
          .add(item);
}
```

---

**总结**：

这些原子操作方法解决了**check-then-act**竞态条件问题，确保：
1. **线程安全**：多线程并发调用不会出现数据不一致
2. **避免重复计算**：computeIfAbsent保证只计算一次
3. **代码简洁**：比手动加锁更简单
4. **性能更好**：利用ConcurrentHashMap的细粒度锁

---

## 五、编程题

### 使用ConcurrentHashMap实现一个线程安全的计数器，支持increment()和getCount()方法。

**答案**：

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadSafeCounter {
    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();
    
    /**
     * 增加计数
     */
    public long increment(String key) {
        return counters.computeIfAbsent(key, k -> new AtomicLong(0))
                      .incrementAndGet();
    }
    
    /**
     * 增加指定值
     */
    public long incrementBy(String key, long delta) {
        return counters.computeIfAbsent(key, k -> new AtomicLong(0))
                      .addAndGet(delta);
    }
    
    /**
     * 获取计数
     */
    public long getCount(String key) {
        AtomicLong counter = counters.get(key);
        return (counter == null) ? 0 : counter.get();
    }
    
    /**
     * 重置计数
     */
    public void reset(String key) {
        counters.remove(key);
    }
    
    /**
     * 获取所有计数
     */
    public ConcurrentHashMap<String, AtomicLong> getAllCounters() {
        return new ConcurrentHashMap<>(counters);
    }
    
    // 测试
    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter counter = new ThreadSafeCounter();
        
        // 100个线程并发增加计数
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment("requests");
                }
            }).start();
        }
        
        Thread.sleep(3000);
        System.out.println("总计数：" + counter.getCount("requests"));  // 100000
    }
}
```

---

💡 **提示**：线程安全集合是并发编程的基础，记住选择原则：ConcurrentHashMap用于Map，CopyOnWriteArrayList用于读多写少的List！
