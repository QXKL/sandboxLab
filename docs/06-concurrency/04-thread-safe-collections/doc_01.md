# 线程安全集合

## 一、为什么需要线程安全集合？

### 普通集合的线程安全问题

```java
// ❌ ArrayList线程不安全
List<Integer> list = new ArrayList<>();

// 100个线程同时添加元素
for (int i = 0; i < 100; i++) {
    int num = i;
    new Thread(() -> {
        list.add(num);  // 并发修改，可能出现问题
    }).start();
}

// 可能的问题：
// 1. 数据丢失（size不准确）
// 2. ArrayIndexOutOfBoundsException
// 3. NullPointerException
```

### 传统解决方案的问题

#### 方案1：Collections.synchronizedXXX

```java
List<Integer> list = Collections.synchronizedList(new ArrayList<>());

// 问题：性能差
// 所有操作都加synchronized锁，读写都互斥
```

#### 方案2：Vector、Hashtable

```java
Vector<Integer> vector = new Vector<>();  // 线程安全，但性能差
Hashtable<String, String> map = new Hashtable<>();  // 线程安全，但性能差

// 问题：
// 1. 所有方法都加synchronized
// 2. 读写互斥，性能低
// 3. 已过时，不推荐使用
```

### 解决方案：并发集合

```java
// ✅ 使用并发集合
List<Integer> list = new CopyOnWriteArrayList<>();
Map<String, String> map = new ConcurrentHashMap<>();

// 优点：
// 1. 线程安全
// 2. 性能好（细粒度锁、无锁算法）
// 3. JDK官方推荐
```

## 二、ConcurrentHashMap

### 定义

**ConcurrentHashMap**：线程安全的HashMap，采用分段锁/CAS实现高并发。

### 演进历史

#### JDK 1.7：分段锁（Segment）

```
Segment数组（默认16个）
每个Segment是一个小HashMap
不同Segment可以并发访问

优点：并发度高（最多16个线程并发写）
缺点：复杂度高
```

#### JDK 1.8：CAS + synchronized

```
数组 + 链表/红黑树
使用CAS + synchronized替代分段锁

优点：
1. 并发度更高（锁粒度到单个桶）
2. 实现更简单
3. 性能更好
```

### 核心特性

#### 特性1：线程安全

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// 多线程并发写
for (int i = 0; i < 100; i++) {
    int num = i;
    new Thread(() -> {
        map.put("key" + num, num);  // 线程安全
    }).start();
}

// 不会出现HashMap的并发问题：
// - 数据丢失
// - 死循环
// - 数据不一致
```

#### 特性2：高性能读操作

```java
// 读操作不加锁
Integer value = map.get("key");  // 无锁读，性能高

// 原理：volatile + CAS
// 1. 数组和节点使用volatile修饰
// 2. 保证可见性
// 3. 读操作无需加锁
```

#### 特性3：原子操作

```java
// putIfAbsent：不存在才put
map.putIfAbsent("key", 1);

// computeIfAbsent：不存在时计算并put
map.computeIfAbsent("key", k -> {
    // 计算value
    return expensiveComputation(k);
});

// compute：计算新值
map.compute("key", (k, v) -> {
    return (v == null) ? 1 : v + 1;
});

// merge：合并值
map.merge("key", 1, (oldVal, newVal) -> oldVal + newVal);
```

### ConcurrentHashMap vs HashMap vs Hashtable

| 特性 | HashMap | Hashtable | ConcurrentHashMap |
|-----|---------|-----------|------------------|
| **线程安全** | ✗ | ✓ | ✓ |
| **null key** | 允许1个 | ✗ | ✗ |
| **null value** | 允许 | ✗ | ✗ |
| **锁机制** | 无 | 全表锁 | 细粒度锁/CAS |
| **读性能** | 高 | 低 | 高 |
| **写性能** | 高 | 低 | 中 |
| **推荐使用** | 单线程 | 已过时 | 多线程 |

### 应用场景

#### 场景1：缓存

```java
// 线程安全的缓存
ConcurrentHashMap<String, User> userCache = new ConcurrentHashMap<>();

public User getUser(String userId) {
    return userCache.computeIfAbsent(userId, id -> {
        // 缓存不存在，从数据库加载
        return loadUserFromDB(id);
    });
}
```

#### 场景2：计数器

```java
// 统计访问次数
ConcurrentHashMap<String, AtomicLong> urlCountMap = new ConcurrentHashMap<>();

public void recordVisit(String url) {
    urlCountMap.computeIfAbsent(url, k -> new AtomicLong(0))
               .incrementAndGet();
}
```

#### 场景3：分组聚合

```java
// 按类型分组
ConcurrentHashMap<String, List<Item>> groupMap = new ConcurrentHashMap<>();

public void addItem(Item item) {
    groupMap.computeIfAbsent(item.getType(), k -> new CopyOnWriteArrayList<>())
            .add(item);
}
```

## 三、CopyOnWriteArrayList

### 定义

**CopyOnWriteArrayList**：写时复制的ArrayList，适合读多写少场景。

### 核心原理

```
写操作（add/set/remove）：
1. 加锁
2. 复制整个数组
3. 在新数组上修改
4. 替换原数组引用
5. 释放锁

读操作（get）：
1. 不加锁
2. 直接读取数组
```

**类比**：图书管理
```
读者看书：不需要登记，直接读
修订书籍：复制一本新书，修改后替换
```

### 基本用法

```java
List<String> list = new CopyOnWriteArrayList<>();

// 写操作：加锁，复制数组
list.add("A");
list.add("B");
list.set(0, "C");
list.remove("B");

// 读操作：无锁
String value = list.get(0);
for (String item : list) {
    System.out.println(item);  // 遍历时不怕并发修改
}
```

### 特点

#### 优点

1. **读操作无锁，性能高**
2. **遍历时不会抛ConcurrentModificationException**
3. **线程安全**

#### 缺点

1. **写操作开销大**（复制整个数组）
2. **内存占用高**（两份数组）
3. **不能保证实时一致性**（读到的可能是旧数据）

### 适用场景

**读多写少**：
```java
// ✅ 适合：监听器列表
List<EventListener> listeners = new CopyOnWriteArrayList<>();
listeners.add(listener);  // 写操作少
listeners.forEach(l -> l.onEvent(event));  // 读操作多

// ✅ 适合：配置列表
List<Config> configs = new CopyOnWriteArrayList<>();

// ❌ 不适合：频繁写入的列表
List<LogEntry> logs = new CopyOnWriteArrayList<>();
logs.add(entry);  // 频繁写入，性能差
```

### CopyOnWriteArrayList vs ArrayList vs Vector

| 特性 | ArrayList | Vector | CopyOnWriteArrayList |
|-----|-----------|--------|---------------------|
| **线程安全** | ✗ | ✓ | ✓ |
| **读性能** | 高 | 低 | 高 |
| **写性能** | 高 | 低 | 极低 |
| **适用场景** | 单线程 | 已过时 | 读多写少 |

## 四、CopyOnWriteArraySet

### 定义

**CopyOnWriteArraySet**：基于CopyOnWriteArrayList实现的Set，去重。

### 基本用法

```java
Set<String> set = new CopyOnWriteArraySet<>();

set.add("A");
set.add("B");
set.add("A");  // 重复，不会添加

System.out.println(set.size());  // 2
```

### 特点

- 底层使用CopyOnWriteArrayList
- add时会遍历检查是否存在（性能低）
- 适合小数据量的读多写少场景

## 五、ConcurrentLinkedQueue

### 定义

**ConcurrentLinkedQueue**：无界、线程安全的队列，基于CAS实现无锁。

### 基本用法

```java
Queue<String> queue = new ConcurrentLinkedQueue<>();

// 入队
queue.offer("A");
queue.offer("B");
queue.offer("C");

// 出队
String item = queue.poll();  // 返回并删除头元素，空返回null
String peek = queue.peek();  // 返回但不删除头元素，空返回null
```

### 特点

- **无锁**：使用CAS算法
- **无界**：可以无限增长
- **高性能**：适合高并发场景
- **不支持null**：元素不能为null

### 应用场景

```java
// 任务队列
ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();

// 生产者
taskQueue.offer(new Task());

// 消费者
Task task = taskQueue.poll();
if (task != null) {
    task.execute();
}
```

## 六、BlockingQueue（阻塞队列）

### 定义

**BlockingQueue**：支持阻塞的线程安全队列，生产者-消费者模式的利器。

### 核心方法

| 操作 | 抛异常 | 返回特殊值 | 阻塞 | 超时 |
|-----|-------|----------|-----|------|
| **插入** | add(e) | offer(e) | put(e) | offer(e, time, unit) |
| **删除** | remove() | poll() | take() | poll(time, unit) |
| **检查** | element() | peek() | - | - |

```java
// 抛异常
queue.add(e);     // 队列满时抛IllegalStateException
queue.remove();   // 队列空时抛NoSuchElementException

// 返回特殊值
queue.offer(e);   // 队列满时返回false
queue.poll();     // 队列空时返回null

// 阻塞
queue.put(e);     // 队列满时阻塞
queue.take();     // 队列空时阻塞

// 超时
queue.offer(e, 3, TimeUnit.SECONDS);  // 等待3秒
queue.poll(3, TimeUnit.SECONDS);
```

### 常用实现类

#### 1. ArrayBlockingQueue（有界队列）

```java
// 固定容量队列
BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

queue.put(1);  // 队列满时阻塞
Integer item = queue.take();  // 队列空时阻塞
```

**特点**：
- 有界（必须指定容量）
- 底层数组
- FIFO
- 支持公平/非公平

#### 2. LinkedBlockingQueue（可选有界队列）

```java
// 无界队列
BlockingQueue<Integer> queue1 = new LinkedBlockingQueue<>();

// 有界队列
BlockingQueue<Integer> queue2 = new LinkedBlockingQueue<>(100);
```

**特点**：
- 可选有界（默认Integer.MAX_VALUE）
- 底层链表
- FIFO
- 吞吐量通常高于ArrayBlockingQueue

#### 3. PriorityBlockingQueue（优先级队列）

```java
// 按优先级出队
BlockingQueue<Task> queue = new PriorityBlockingQueue<>();

queue.put(new Task(3));  // 优先级3
queue.put(new Task(1));  // 优先级1
queue.put(new Task(2));  // 优先级2

Task task = queue.take();  // 优先级1先出队
```

**特点**：
- 无界
- 按优先级排序（Comparable或Comparator）
- 不保证相同优先级的顺序

#### 4. DelayQueue（延迟队列）

```java
// 延迟任务队列
BlockingQueue<DelayedTask> queue = new DelayQueue<>();

queue.put(new DelayedTask("A", 5000));   // 5秒后执行
queue.put(new DelayedTask("B", 3000));   // 3秒后执行

DelayedTask task = queue.take();  // 阻塞直到有任务到期
```

**特点**：
- 无界
- 元素必须实现Delayed接口
- 只有到期的元素才能出队
- 适合：定时任务、缓存过期

#### 5. SynchronousQueue（同步队列）

```java
// 没有容量的队列
BlockingQueue<Integer> queue = new SynchronousQueue<>();

// 生产者
new Thread(() -> {
    queue.put(1);  // 阻塞，直到有消费者接收
}).start();

// 消费者
new Thread(() -> {
    Integer item = queue.take();  // 阻塞，直到有生产者提供
}).start();
```

**特点**：
- 容量为0
- put()必须等待take()
- 适合：线程间直接传递数据

## 七、线程安全集合总结

### 选择指南

```
Map：
- 多线程 → ConcurrentHashMap
- 单线程 → HashMap

List：
- 多线程 + 读多写少 → CopyOnWriteArrayList
- 单线程 → ArrayList

Set：
- 多线程 + 读多写少 → CopyOnWriteArraySet
- 多线程 + 并发高 → ConcurrentHashMap.newKeySet()
- 单线程 → HashSet

Queue：
- 无阻塞 → ConcurrentLinkedQueue
- 有阻塞 + 有界 → ArrayBlockingQueue
- 有阻塞 + 可选有界 → LinkedBlockingQueue
- 优先级 → PriorityBlockingQueue
- 延迟 → DelayQueue
- 同步传递 → SynchronousQueue
```

### 性能对比

| 集合 | 读性能 | 写性能 | 适用场景 |
|-----|-------|-------|---------|
| ConcurrentHashMap | 高 | 中 | 通用 |
| CopyOnWriteArrayList | 高 | 极低 | 读多写少 |
| ConcurrentLinkedQueue | 高 | 高 | 高并发队列 |
| ArrayBlockingQueue | 中 | 中 | 生产者-消费者 |

## 八、小结

**核心要点**：

1. **ConcurrentHashMap**：
   - 线程安全的HashMap
   - JDK 1.8使用CAS + synchronized
   - 读操作无锁，性能高
   - 提供原子操作（putIfAbsent、computeIfAbsent）

2. **CopyOnWriteArrayList**：
   - 写时复制
   - 读操作无锁
   - 适合读多写少
   - 写操作开销大

3. **ConcurrentLinkedQueue**：
   - 无界队列
   - 基于CAS无锁
   - 高性能

4. **BlockingQueue**：
   - 支持阻塞的队列
   - put/take阻塞
   - 适合生产者-消费者
   - 多种实现：Array、Linked、Priority、Delay、Synchronous

5. **选择原则**：
   - 性能要求高 → ConcurrentHashMap、ConcurrentLinkedQueue
   - 读多写少 → CopyOnWriteArrayList
   - 生产者-消费者 → BlockingQueue

**记忆口诀**：
- ConcurrentHashMap性能好，多线程Map首选它
- CopyOnWrite读多写少，写时复制要记牢
- BlockingQueue能阻塞，生产消费最适合

---

💡 **提示**：不要用Vector、Hashtable、Collections.synchronizedXXX，用JUC的并发集合！
