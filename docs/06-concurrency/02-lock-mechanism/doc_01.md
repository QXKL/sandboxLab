# 锁机制

## 一、为什么需要锁？

### 线程安全问题

**场景**：银行账户转账

```java
public class BankAccount {
    private int balance = 1000;
    
    // ❌ 线程不安全
    public void withdraw(int amount) {
        if (balance >= amount) {
            // 线程切换，此时另一个线程也通过了if判断
            balance -= amount;
        }
    }
}

// 两个线程同时取款600
Thread t1 = new Thread(() -> account.withdraw(600));
Thread t2 = new Thread(() -> account.withdraw(600));
t1.start();
t2.start();

// 期望：其中一个失败（余额不足）
// 实际：两个都成功，余额变成-200（数据错误！）
```

**原因**：多个线程同时访问共享变量，产生竞态条件（Race Condition）。

### 解决方案：加锁

```java
// ✅ 线程安全
public synchronized void withdraw(int amount) {
    if (balance >= amount) {
        balance -= amount;
    }
}

// 同一时刻只有一个线程能执行withdraw方法
```

## 二、synchronized关键字

### 用法1：同步方法

```java
public class Counter {
    private int count = 0;
    
    // 锁对象：this
    public synchronized void increment() {
        count++;
    }
    
    // 锁对象：Counter.class
    public static synchronized void staticMethod() {
        // 静态方法
    }
}
```

### 用法2：同步代码块

```java
public class Counter {
    private int count = 0;
    private final Object lock = new Object();
    
    public void increment() {
        synchronized (lock) {  // 指定锁对象
            count++;
        }
    }
    
    public void method() {
        // 非同步代码...
        
        synchronized (this) {  // 只锁关键代码
            // 同步代码...
        }
        
        // 非同步代码...
    }
}
```

### synchronized锁的是什么？

```java
// 1. 锁实例对象
public synchronized void method() { }
// 等价于
public void method() {
    synchronized (this) { }
}

// 2. 锁类对象
public static synchronized void method() { }
// 等价于
public static void method() {
    synchronized (ClassName.class) { }
}

// 3. 锁指定对象
synchronized (lock) { }
```

**关键**：不同线程必须竞争**同一个锁对象**才会互斥

```java
public class Demo {
    public synchronized void method1() { }  // 锁this
    public synchronized void method2() { }  // 锁this
    
    public static synchronized void method3() { }  // 锁Demo.class
}

// t1访问method1，t2访问method2 → 互斥（同一个this锁）
// t1访问method1，t2访问method3 → 不互斥（this vs Demo.class）
```

### synchronized底层原理

**字节码层面**：
```
monitorenter  // 获取锁
...
monitorexit   // 释放锁
```

**对象头**：
```
Java对象 = 对象头 + 实例数据 + 对齐填充

对象头包含：
- Mark Word（锁信息、GC信息）
- Class Pointer（类型指针）
```

**锁升级过程**（JDK 6优化）：
```
无锁 → 偏向锁 → 轻量级锁 → 重量级锁

偏向锁：只有一个线程访问，记录线程ID
轻量级锁：少量竞争，CAS自旋
重量级锁：竞争激烈，操作系统互斥量
```

## 三、ReentrantLock（可重入锁）

### 基本用法

```java
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();  // 获取锁
        try {
            count++;
        } finally {
            lock.unlock();  // 释放锁（必须在finally中）
        }
    }
}
```

**注意**：必须在finally中释放锁，否则异常时锁无法释放

### synchronized vs ReentrantLock

| 特性 | synchronized | ReentrantLock |
|-----|-------------|--------------|
| **锁类型** | JVM内置 | JDK类库 |
| **使用方式** | 自动释放 | 手动释放 |
| **等待可中断** | 不支持 | 支持 |
| **公平锁** | 非公平 | 支持公平/非公平 |
| **锁绑定多个条件** | 不支持 | 支持 |
| **性能** | JDK 6后相近 | JDK 6后相近 |
| **推荐场景** | 简单场景 | 复杂场景 |

### ReentrantLock高级功能

#### 功能1：可中断锁

```java
ReentrantLock lock = new ReentrantLock();

try {
    // 可响应中断的加锁
    lock.lockInterruptibly();
    try {
        // 业务逻辑
    } finally {
        lock.unlock();
    }
} catch (InterruptedException e) {
    // 线程被中断，放弃锁
}
```

#### 功能2：尝试加锁

```java
ReentrantLock lock = new ReentrantLock();

// 尝试加锁，立即返回
if (lock.tryLock()) {
    try {
        // 获取到锁，执行业务
    } finally {
        lock.unlock();
    }
} else {
    // 没获取到锁，执行其他逻辑
}

// 尝试加锁，等待指定时间
if (lock.tryLock(3, TimeUnit.SECONDS)) {
    try {
        // 3秒内获取到锁
    } finally {
        lock.unlock();
    }
} else {
    // 3秒后仍未获取到锁
}
```

#### 功能3：公平锁

```java
// 非公平锁（默认）：性能好，可能饥饿
ReentrantLock lock = new ReentrantLock(false);

// 公平锁：按申请顺序获取锁，性能稍差
ReentrantLock lock = new ReentrantLock(true);
```

**公平锁示例**：
```
线程1持有锁
线程2等待（队列：2）
线程3等待（队列：2 → 3）
线程4等待（队列：2 → 3 → 4）

线程1释放锁
→ 线程2获取锁（公平，按队列顺序）
```

#### 功能4：多个条件变量

```java
ReentrantLock lock = new ReentrantLock();
Condition notEmpty = lock.newCondition();  // 条件1：非空
Condition notFull = lock.newCondition();   // 条件2：非满

// 生产者
public void put(T item) throws InterruptedException {
    lock.lock();
    try {
        while (队列已满) {
            notFull.await();  // 等待非满条件
        }
        队列.add(item);
        notEmpty.signal();  // 通知非空条件
    } finally {
        lock.unlock();
    }
}

// 消费者
public T take() throws InterruptedException {
    lock.lock();
    try {
        while (队列为空) {
            notEmpty.await();  // 等待非空条件
        }
        T item = 队列.remove();
        notFull.signal();  // 通知非满条件
        return item;
    } finally {
        lock.unlock();
    }
}
```

## 四、ReadWriteLock（读写锁）

### 问题：读多写少场景

```java
// 使用synchronized
public class Cache {
    private final Map<String, Object> cache = new HashMap<>();
    
    public synchronized Object get(String key) {
        return cache.get(key);
    }
    
    public synchronized void put(String key, Object value) {
        cache.put(key, value);
    }
}

// 问题：读操作也互斥，性能差
// 10个线程同时读 → 排队执行（实际可以并发读）
```

### 解决方案：读写锁

```java
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache {
    private final Map<String, Object> cache = new HashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    public Object get(String key) {
        rwLock.readLock().lock();  // 读锁
        try {
            return cache.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public void put(String key, Object value) {
        rwLock.writeLock().lock();  // 写锁
        try {
            cache.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```

### 读写锁规则

```
读锁（共享锁）：多个线程可同时持有
写锁（独占锁）：只有一个线程可持有

规则：
1. 读-读：不互斥（可并发）
2. 读-写：互斥
3. 写-写：互斥
```

**示例**：
```
时刻1：线程1持有读锁
       线程2请求读锁 → 成功（读-读不互斥）
       
时刻2：线程1、2持有读锁
       线程3请求写锁 → 阻塞（读-写互斥）
       
时刻3：线程1、2释放读锁
       线程3获取写锁
       线程4请求读锁 → 阻塞（写-读互斥）
```

### 锁降级

```java
rwLock.writeLock().lock();  // 1. 获取写锁
try {
    // 2. 写操作
    cache.put(key, value);
    
    rwLock.readLock().lock();  // 3. 获取读锁（降级）
} finally {
    rwLock.writeLock().unlock();  // 4. 释放写锁
}

try {
    // 5. 读操作（此时持有读锁）
    return cache.get(key);
} finally {
    rwLock.readLock().unlock();  // 6. 释放读锁
}
```

**注意**：支持锁降级（写→读），不支持锁升级（读→写）

## 五、死锁

### 什么是死锁？

```java
Object lock1 = new Object();
Object lock2 = new Object();

// 线程1
new Thread(() -> {
    synchronized (lock1) {
        System.out.println("线程1获取lock1");
        Thread.sleep(100);
        synchronized (lock2) {  // 等待lock2
            System.out.println("线程1获取lock2");
        }
    }
}).start();

// 线程2
new Thread(() -> {
    synchronized (lock2) {
        System.out.println("线程2获取lock2");
        Thread.sleep(100);
        synchronized (lock1) {  // 等待lock1
            System.out.println("线程2获取lock1");
        }
    }
}).start();

// 结果：线程1持有lock1等待lock2，线程2持有lock2等待lock1 → 死锁
```

### 死锁的4个必要条件

1. **互斥**：资源只能被一个线程占用
2. **持有并等待**：持有资源的同时等待其他资源
3. **不可剥夺**：资源不能被强制抢占
4. **循环等待**：线程间形成等待环路

### 避免死锁

#### 方法1：按顺序加锁

```java
// ❌ 可能死锁
synchronized (lock1) {
    synchronized (lock2) { }
}

synchronized (lock2) {
    synchronized (lock1) { }
}

// ✅ 按固定顺序加锁
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

#### 方法2：使用tryLock

```java
ReentrantLock lock1 = new ReentrantLock();
ReentrantLock lock2 = new ReentrantLock();

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
    Thread.sleep(随机时间);  // 避免活锁
}
```

#### 方法3：设置超时

```java
if (lock1.tryLock(3, TimeUnit.SECONDS)) {
    try {
        if (lock2.tryLock(3, TimeUnit.SECONDS)) {
            try {
                // 业务逻辑
            } finally {
                lock2.unlock();
            }
        }
    } finally {
        lock1.unlock();
    }
}
```

## 六、锁的最佳实践

### 实践1：能用synchronized就用synchronized

```java
// 简单场景：synchronized
public synchronized void method() { }

// 复杂场景：ReentrantLock
// - 需要可中断
// - 需要超时
// - 需要公平锁
// - 需要多个条件变量
```

### 实践2：缩小锁范围

```java
// ❌ 锁范围过大
public synchronized void method() {
    非关键代码1();
    关键代码();
    非关键代码2();
}

// ✅ 只锁关键代码
public void method() {
    非关键代码1();
    synchronized (this) {
        关键代码();
    }
    非关键代码2();
}
```

### 实践3：避免锁嵌套

```java
// ❌ 容易死锁
synchronized (lock1) {
    synchronized (lock2) {
        // ...
    }
}

// ✅ 尽量避免嵌套
// 如果必须嵌套，按固定顺序加锁
```

### 实践4：使用并发集合

```java
// ❌ 手动加锁
Map<String, Object> map = new HashMap<>();
synchronized (map) {
    map.put(key, value);
}

// ✅ 使用并发集合
Map<String, Object> map = new ConcurrentHashMap<>();
map.put(key, value);  // 内部已处理并发
```

### 实践5：读多写少用读写锁

```java
// 读多写少场景
ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

// 读操作用读锁
public Object read() {
    rwLock.readLock().lock();
    try {
        return data;
    } finally {
        rwLock.readLock().unlock();
    }
}

// 写操作用写锁
public void write(Object newData) {
    rwLock.writeLock().lock();
    try {
        data = newData;
    } finally {
        rwLock.writeLock().unlock();
    }
}
```

## 七、小结

**核心要点**：

1. **synchronized**：
   - JVM内置，自动释放
   - 锁对象：this、Class、指定对象
   - 简单场景首选

2. **ReentrantLock**：
   - 手动释放，必须在finally中
   - 支持可中断、tryLock、公平锁、多条件
   - 复杂场景使用

3. **ReadWriteLock**：
   - 读锁共享，写锁独占
   - 读-读不互斥，读-写、写-写互斥
   - 适合读多写少

4. **死锁**：
   - 4个必要条件：互斥、持有等待、不可剥夺、循环等待
   - 避免：按顺序加锁、tryLock、超时

5. **最佳实践**：
   - 能用synchronized就用synchronized
   - 缩小锁范围
   - 避免锁嵌套
   - 使用并发集合
   - 读多写少用读写锁

**记忆口诀**：
- synchronized简单用，ReentrantLock功能多
- 读写锁提升并发，死锁预防要记牢
- 锁范围要缩小，嵌套锁要避免

---

💡 **提示**：锁是保证线程安全的基础，但过度加锁会降低性能。合理选择锁类型，缩小锁范围。
