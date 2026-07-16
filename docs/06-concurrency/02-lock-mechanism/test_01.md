# 锁机制 - 测试题

## 一、选择题

### 1. 以下哪个不是死锁的必要条件？
A. 互斥  
B. 持有并等待  
C. 线程优先级  
D. 循环等待

**答案**：C

**解析**：死锁的4个必要条件是：互斥、持有并等待、不可剥夺、循环等待。

---

### 2. synchronized锁的是什么？
A. 代码块  
B. 对象  
C. 线程  
D. 方法

**答案**：B

**解析**：synchronized锁的是对象（实例对象、类对象或指定对象），不是代码或方法本身。

---

### 3. 以下代码会互斥吗？
```java
public class Demo {
    public synchronized void method1() { }
    public static synchronized void method2() { }
}
```
A. 会互斥  
B. 不会互斥  
C. 可能互斥  
D. 编译错误

**答案**：B

**解析**：method1锁的是this对象，method2锁的是Demo.class类对象，不是同一个锁，不会互斥。

---

### 4. ReentrantLock相比synchronized的优势不包括？
A. 可中断  
B. 支持公平锁  
C. 自动释放锁  
D. 支持tryLock

**答案**：C

**解析**：ReentrantLock需要手动释放锁（在finally中），synchronized才是自动释放。

---

### 5. 读写锁的规则，以下说法正确的是？
A. 读-读互斥  
B. 读-写不互斥  
C. 写-写不互斥  
D. 读-读不互斥

**答案**：D

**解析**：读锁是共享锁，多个线程可同时持有读锁，读-读不互斥。读-写、写-写都互斥。

---

### 6. 以下哪个操作必须在finally块中执行？
A. synchronized加锁  
B. ReentrantLock加锁  
C. ReentrantLock释放锁  
D. volatile变量赋值

**答案**：C

**解析**：ReentrantLock.unlock()必须在finally中执行，确保异常时也能释放锁。

---

### 7. 公平锁和非公平锁的区别？
A. 公平锁性能更好  
B. 非公平锁按申请顺序获取锁  
C. 公平锁按申请顺序获取锁  
D. 两者没有区别

**答案**：C

**解析**：公平锁按申请顺序（FIFO）获取锁，非公平锁允许插队，性能更好但可能饥饿。

---

### 8. 以下哪种情况适合使用读写锁？
A. 写多读少  
B. 读多写少  
C. 读写相当  
D. 只有写操作

**答案**：B

**解析**：读写锁适合读多写少场景，允许多个线程并发读，提升性能。

---

### 9. synchronized的锁升级过程正确的是？
A. 无锁 → 重量级锁 → 轻量级锁 → 偏向锁  
B. 偏向锁 → 轻量级锁 → 重量级锁 → 无锁  
C. 无锁 → 偏向锁 → 轻量级锁 → 重量级锁  
D. 重量级锁 → 轻量级锁 → 偏向锁 → 无锁

**答案**：C

**解析**：JDK 6优化后，synchronized会根据竞争情况逐步升级：无锁 → 偏向锁 → 轻量级锁 → 重量级锁。

---

### 10. 以下代码存在什么问题？
```java
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // 业务逻辑
} catch (Exception e) {
    e.printStackTrace();
}
lock.unlock();
```
A. 没有问题  
B. unlock应该在finally中  
C. 应该用synchronized  
D. lock()位置错误

**答案**：B

**解析**：如果业务逻辑抛出异常且被catch捕获，不会执行unlock()，导致死锁。应该在finally中释放锁。

---

## 二、填空题

### 1. synchronized关键字锁实例方法时，锁的是________对象；锁静态方法时，锁的是________对象。

**答案**：this、类（或Class）

---

### 2. ReentrantLock创建公平锁的代码是：________。

**答案**：`new ReentrantLock(true)`

---

### 3. 读写锁中，读锁是________锁（共享/独占），写锁是________锁（共享/独占）。

**答案**：共享、独占

---

### 4. 死锁的4个必要条件是：互斥、________、不可剥夺、________。

**答案**：持有并等待（或持有并请求）、循环等待

---

### 5. 锁降级是指从________锁降级为________锁，这种操作是________（支持/不支持）的。

**答案**：写、读、支持

---

## 三、判断题

### 1. synchronized是可重入锁。（ ）

**答案**：✓

**解析**：synchronized和ReentrantLock都是可重入锁，同一个线程可以多次获取同一把锁。

---

### 2. 使用ReentrantLock时，lock()和unlock()的次数必须相等。（ ）

**答案**：✓

**解析**：加锁和解锁次数必须对应，否则锁无法释放或抛出异常。

---

### 3. 读写锁支持从读锁升级为写锁。（ ）

**答案**：✗

**解析**：读写锁支持锁降级（写→读），不支持锁升级（读→写），否则可能死锁。

---

### 4. synchronized和ReentrantLock在JDK 6之后性能相近。（ ）

**答案**：✓

**解析**：JDK 6对synchronized进行了大量优化（锁升级、偏向锁等），性能已接近ReentrantLock。

---

### 5. 避免死锁的一个有效方法是按固定顺序加锁。（ ）

**答案**：✓

**解析**：按固定顺序加锁可以打破"循环等待"条件，有效避免死锁。

---

## 四、简答题

### 1. 解释synchronized锁升级的过程，以及每种锁的应用场景。

**答案**：

JDK 6对synchronized进行了优化，引入锁升级机制：

**1. 无锁状态**
- 对象刚创建，没有线程访问
- Mark Word记录hashCode等信息

**2. 偏向锁**
- **场景**：只有一个线程访问
- **原理**：在对象头记录线程ID，下次该线程访问时直接通过
- **优点**：几乎无性能开销
- **缺点**：有其他线程竞争时需要撤销

**3. 轻量级锁**
- **场景**：少量竞争，线程交替执行
- **原理**：CAS自旋获取锁，不阻塞线程
- **优点**：避免线程阻塞/唤醒的开销
- **缺点**：自旋消耗CPU，竞争激烈时浪费资源

**4. 重量级锁**
- **场景**：竞争激烈，线程需要等待
- **原理**：使用操作系统互斥量（Mutex），线程阻塞
- **优点**：不消耗CPU
- **缺点**：线程阻塞/唤醒开销大

**升级方向**：单向升级，不会降级（JDK 15开始支持降级）

---

### 2. synchronized和ReentrantLock如何选择？列举各自的适用场景。

**答案**：

**选择原则**：能用synchronized就用synchronized

**synchronized适用场景**：
1. **简单同步需求**：方法级或代码块同步
2. **自动释放锁**：不用担心忘记unlock
3. **代码简洁**：不需要显式加锁/解锁
4. **JVM优化**：锁升级、偏向锁等优化

```java
// 简单场景
public synchronized void increment() {
    count++;
}
```

**ReentrantLock适用场景**：
1. **需要可中断**：lockInterruptibly()
2. **需要超时机制**：tryLock(timeout)
3. **需要公平锁**：避免线程饥饿
4. **需要多个条件变量**：生产者-消费者模式
5. **需要tryLock**：尝试获取锁，获取不到执行其他逻辑

```java
// 复杂场景
ReentrantLock lock = new ReentrantLock(true);  // 公平锁
if (lock.tryLock(3, TimeUnit.SECONDS)) {
    try {
        // 业务逻辑
    } finally {
        lock.unlock();
    }
} else {
    // 未获取到锁的处理
}
```

**性能对比**：JDK 6后相近，不是选择依据

---

### 3. 什么是死锁？如何预防死锁？

**答案**：

**死锁定义**：
多个线程互相持有对方需要的资源，都在等待对方释放资源，导致所有线程都无法继续执行。

**死锁的4个必要条件**：
1. **互斥**：资源只能被一个线程占用
2. **持有并等待**：持有资源的同时等待其他资源
3. **不可剥夺**：资源不能被强制抢占
4. **循环等待**：线程间形成等待环路

**预防死锁的方法**：

**方法1：按固定顺序加锁**
```java
// 打破"循环等待"
public void transfer(Account from, Account to, int amount) {
    Account first = from.id < to.id ? from : to;
    Account second = from.id < to.id ? to : from;
    
    synchronized (first) {
        synchronized (second) {
            from.balance -= amount;
            to.balance += amount;
        }
    }
}
```

**方法2：使用tryLock**
```java
// 获取不到锁时放弃，打破"持有并等待"
while (true) {
    if (lock1.tryLock()) {
        try {
            if (lock2.tryLock()) {
                try {
                    // 业务逻辑
                    break;
                } finally {
                    lock2.unlock();
                }
            }
        } finally {
            lock1.unlock();
        }
    }
    Thread.sleep(随机时间);
}
```

**方法3：设置超时**
```java
// 超时放弃，避免无限等待
if (lock1.tryLock(3, TimeUnit.SECONDS)) {
    // ...
}
```

**方法4：避免锁嵌套**
```java
// 尽量不要嵌套使用多个锁
```

---

### 4. 说明读写锁的工作原理，以及为什么适合读多写少的场景。

**答案**：

**读写锁原理**：

读写锁（ReentrantReadWriteLock）维护一对锁：
- **读锁（共享锁）**：多个线程可同时持有
- **写锁（独占锁）**：只有一个线程可持有

**互斥规则**：
```
读-读：不互斥 ✓（多线程并发读）
读-写：互斥   ✗
写-读：互斥   ✗
写-写：互斥   ✗
```

**示例**：
```java
ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

// 读操作
public Object read() {
    rwLock.readLock().lock();
    try {
        return data;  // 多个线程可并发读
    } finally {
        rwLock.readLock().unlock();
    }
}

// 写操作
public void write(Object newData) {
    rwLock.writeLock().lock();
    try {
        data = newData;  // 独占，其他读/写都阻塞
    } finally {
        rwLock.writeLock().unlock();
    }
}
```

**为什么适合读多写少**：

1. **读操作并发执行**：
   - 100个线程同时读 → 可以并发执行
   - 使用synchronized → 排队执行，性能差

2. **性能提升显著**：
   ```
   读操作占比95%，写操作占比5%
   
   synchronized：所有操作都串行
   吞吐量：1000 TPS
   
   ReadWriteLock：读操作并发
   吞吐量：5000+ TPS（提升5倍）
   ```

3. **读写分离**：
   - 读操作不影响其他读操作
   - 只有写操作需要独占
   - 符合读多写少的特点

**注意事项**：
- 写操作需要等待所有读操作完成
- 如果写操作频繁，可能导致写饥饿
- 可以设置写优先策略

---

### 5. 以下代码为什么会死锁？如何修复？

```java
public class DeadLockDemo {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    public void method1() {
        synchronized (lock1) {
            System.out.println("method1 获取 lock1");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            synchronized (lock2) {
                System.out.println("method1 获取 lock2");
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            System.out.println("method2 获取 lock2");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            synchronized (lock1) {
                System.out.println("method2 获取 lock1");
            }
        }
    }
}
```

**答案**：

**死锁原因分析**：

1. **线程1执行method1**：
   - 获取lock1 ✓
   - 等待100ms
   - 尝试获取lock2（被线程2持有）→ 阻塞

2. **线程2执行method2**：
   - 获取lock2 ✓
   - 等待100ms
   - 尝试获取lock1（被线程1持有）→ 阻塞

3. **循环等待形成**：
   ```
   线程1：持有lock1，等待lock2
   线程2：持有lock2，等待lock1
   → 死锁
   ```

**修复方案**：

**方案1：按固定顺序加锁**
```java
public void method1() {
    synchronized (lock1) {  // 都先锁lock1
        synchronized (lock2) {  // 再锁lock2
            // 业务逻辑
        }
    }
}

public void method2() {
    synchronized (lock1) {  // 保持相同顺序
        synchronized (lock2) {
            // 业务逻辑
        }
    }
}
```

**方案2：使用tryLock**
```java
ReentrantLock lock1 = new ReentrantLock();
ReentrantLock lock2 = new ReentrantLock();

public void method1() {
    while (true) {
        if (lock1.tryLock()) {
            try {
                if (lock2.tryLock()) {
                    try {
                        // 业务逻辑
                        break;
                    } finally {
                        lock2.unlock();
                    }
                }
            } finally {
                lock1.unlock();
            }
        }
        try {
            Thread.sleep((long) (Math.random() * 10));
        } catch (InterruptedException e) {}
    }
}
```

**方案3：避免锁嵌套**
```java
// 重构代码，避免同时持有两把锁
public void method1() {
    synchronized (lock1) {
        // 只操作lock1保护的资源
    }
}

public void method2() {
    synchronized (lock2) {
        // 只操作lock2保护的资源
    }
}
```

---

## 五、编程题

### 1. 使用synchronized实现一个线程安全的单例模式（双重检查锁）。

**答案**：

```java
public class Singleton {
    // volatile保证可见性，防止指令重排
    private static volatile Singleton instance;
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        if (instance == null) {  // 第一次检查，不加锁
            synchronized (Singleton.class) {  // 加锁
                if (instance == null) {  // 第二次检查
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**为什么需要volatile**：
```
new Singleton()分为3步：
1. 分配内存空间
2. 初始化对象
3. 将instance指向内存地址

可能的指令重排：1 → 3 → 2
→ instance不为null，但对象未初始化
→ 其他线程获取到未初始化的对象
→ volatile防止指令重排
```

---

### 2. 使用ReentrantReadWriteLock实现一个线程安全的缓存类。

**答案**：

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    // 读操作
    public V get(K key) {
        rwLock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    // 写操作
    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // 删除操作
    public void remove(K key) {
        rwLock.writeLock().lock();
        try {
            cache.remove(key);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // 清空操作
    public void clear() {
        rwLock.writeLock().lock();
        try {
            cache.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // 获取大小
    public int size() {
        rwLock.readLock().lock();
        try {
            return cache.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
```

---

### 3. 使用ReentrantLock和Condition实现一个阻塞队列（固定容量）。

**答案**：

```java
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();  // 非空条件
    private final Condition notFull = lock.newCondition();   // 非满条件
    
    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }
    
    // 入队（阻塞）
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();  // 队列满，等待非满条件
            }
            queue.offer(item);
            notEmpty.signal();  // 通知非空条件
        } finally {
            lock.unlock();
        }
    }
    
    // 出队（阻塞）
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();  // 队列空，等待非空条件
            }
            T item = queue.poll();
            notFull.signal();  // 通知非满条件
            return item;
        } finally {
            lock.unlock();
        }
    }
    
    // 获取大小
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
```

**测试代码**：
```java
BlockingQueue<Integer> queue = new BlockingQueue<>(5);

// 生产者
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            queue.put(i);
            System.out.println("生产：" + i);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}).start();

// 消费者
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            int item = queue.take();
            System.out.println("消费：" + item);
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}).start();
```

---

💡 **提示**：锁是并发编程的核心，务必理解synchronized和ReentrantLock的区别，以及如何避免死锁！
