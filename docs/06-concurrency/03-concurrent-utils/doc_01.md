# 并发工具类

## 一、为什么需要并发工具类？

### 场景：线程协作

**问题**：使用wait/notify复杂且易错

```java
// ❌ 使用wait/notify（复杂）
public class WaitNotifyDemo {
    private boolean ready = false;
    
    public synchronized void waitForReady() throws InterruptedException {
        while (!ready) {
            wait();  // 等待
        }
        // 继续执行
    }
    
    public synchronized void setReady() {
        ready = true;
        notifyAll();  // 通知
    }
}
```

**解决方案**：使用并发工具类

```java
// ✅ 使用CountDownLatch（简单）
CountDownLatch latch = new CountDownLatch(1);

// 线程等待
latch.await();

// 通知继续
latch.countDown();
```

## 二、CountDownLatch（倒计时门栓）

### 定义

**CountDownLatch**：允许一个或多个线程等待其他线程完成操作。

**类比**：火箭发射倒计时
```
倒计时：10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
→ 发射！

CountDownLatch：count = 10
每完成一个任务 → countDown()
count = 0 → 等待的线程继续执行
```

### 基本用法

```java
import java.util.concurrent.CountDownLatch;

CountDownLatch latch = new CountDownLatch(3);  // 计数器=3

// 线程1：等待其他线程完成
new Thread(() -> {
    try {
        System.out.println("等待所有任务完成...");
        latch.await();  // 阻塞，直到计数器=0
        System.out.println("所有任务完成，继续执行");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}).start();

// 线程2：完成任务1
new Thread(() -> {
    System.out.println("任务1完成");
    latch.countDown();  // 计数器-1（3 → 2）
}).start();

// 线程3：完成任务2
new Thread(() -> {
    System.out.println("任务2完成");
    latch.countDown();  // 计数器-1（2 → 1）
}).start();

// 线程4：完成任务3
new Thread(() -> {
    System.out.println("任务3完成");
    latch.countDown();  // 计数器-1（1 → 0）
    // 此时latch.await()的线程被唤醒
}).start();
```

### 应用场景

#### 场景1：等待多个线程启动完成

```java
CountDownLatch startSignal = new CountDownLatch(1);
CountDownLatch doneSignal = new CountDownLatch(10);

// 创建10个线程
for (int i = 0; i < 10; i++) {
    new Thread(() -> {
        try {
            System.out.println(Thread.currentThread().getName() + " 准备就绪");
            startSignal.await();  // 等待启动信号
            
            // 执行任务
            doWork();
            
            doneSignal.countDown();  // 完成任务
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
}

System.out.println("所有线程准备完毕");
startSignal.countDown();  // 发送启动信号

doneSignal.await();  // 等待所有线程完成
System.out.println("所有任务完成");
```

#### 场景2：并行计算

```java
// 将大任务拆分成多个子任务并行计算
public long parallelSum(List<Integer> numbers) throws InterruptedException {
    int threadCount = 4;
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicLong sum = new AtomicLong(0);
    
    int chunkSize = numbers.size() / threadCount;
    
    for (int i = 0; i < threadCount; i++) {
        int start = i * chunkSize;
        int end = (i == threadCount - 1) ? numbers.size() : (i + 1) * chunkSize;
        List<Integer> subList = numbers.subList(start, end);
        
        new Thread(() -> {
            long subSum = subList.stream().mapToLong(Integer::longValue).sum();
            sum.addAndGet(subSum);
            latch.countDown();
        }).start();
    }
    
    latch.await();  // 等待所有子任务完成
    return sum.get();
}
```

#### 场景3：模拟高并发测试

```java
// 模拟100个用户同时请求
CountDownLatch latch = new CountDownLatch(1);
int userCount = 100;

for (int i = 0; i < userCount; i++) {
    new Thread(() -> {
        try {
            latch.await();  // 等待统一开始
            // 发送请求
            sendRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
}

Thread.sleep(1000);  // 等待所有线程准备
latch.countDown();  // 统一开始
```

### 注意事项

1. **CountDownLatch不可重用**：计数器到0后无法重置
2. **await可以超时**：`latch.await(10, TimeUnit.SECONDS)`
3. **countDown不会阻塞**：可以多次调用

## 三、CyclicBarrier（循环栅栏）

### 定义

**CyclicBarrier**：让一组线程到达一个屏障时阻塞，直到所有线程都到达，然后打开屏障，所有线程继续执行。

**类比**：旅游团集合
```
导游：等所有人到齐再出发
成员1到达 → 等待
成员2到达 → 等待
...
最后一人到达 → 所有人出发

下一个景点，继续集合（可重用）
```

### 基本用法

```java
import java.util.concurrent.CyclicBarrier;

// 创建屏障，等待3个线程
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    // 所有线程到达后执行的任务（可选）
    System.out.println("所有线程到达屏障，继续执行");
});

for (int i = 0; i < 3; i++) {
    int id = i;
    new Thread(() -> {
        try {
            System.out.println("线程" + id + "到达屏障");
            barrier.await();  // 等待其他线程
            System.out.println("线程" + id + "继续执行");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
```

### CyclicBarrier vs CountDownLatch

| 特性 | CountDownLatch | CyclicBarrier |
|-----|---------------|--------------|
| **计数方式** | 递减（countDown） | 递增（await） |
| **是否可重用** | 不可重用 | 可重用 |
| **等待线程** | 1个或多个 | N个（创建时指定） |
| **触发动作** | 无 | 可指定barrierAction |
| **应用场景** | 一个线程等N个线程完成 | N个线程互相等待 |

### 应用场景

#### 场景1：分阶段任务

```java
// 多线程计算，分3个阶段，每阶段结束后同步
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("===== 阶段完成，进入下一阶段 =====");
});

for (int i = 0; i < 3; i++) {
    int id = i;
    new Thread(() -> {
        try {
            // 阶段1
            System.out.println("线程" + id + "完成阶段1");
            barrier.await();
            
            // 阶段2
            System.out.println("线程" + id + "完成阶段2");
            barrier.await();
            
            // 阶段3
            System.out.println("线程" + id + "完成阶段3");
            barrier.await();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
```

#### 场景2：多线程计算合并结果

```java
int workerCount = 4;
List<List<Integer>> partialResults = new ArrayList<>();
CyclicBarrier barrier = new CyclicBarrier(workerCount, () -> {
    // 所有线程计算完成，合并结果
    List<Integer> finalResult = new ArrayList<>();
    for (List<Integer> partial : partialResults) {
        finalResult.addAll(partial);
    }
    System.out.println("最终结果：" + finalResult);
});

for (int i = 0; i < workerCount; i++) {
    int id = i;
    new Thread(() -> {
        try {
            // 计算部分结果
            List<Integer> result = compute(id);
            partialResults.add(result);
            
            barrier.await();  // 等待其他线程完成
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
```

## 四、Semaphore（信号量）

### 定义

**Semaphore**：控制同时访问特定资源的线程数量。

**类比**：停车场
```
停车场：10个车位
许可证（permits）= 10

车辆进入：acquire()（获取许可）
车辆离开：release()（释放许可）

10辆车停满 → 其他车等待
有车离开 → 等待的车进入
```

### 基本用法

```java
import java.util.concurrent.Semaphore;

// 创建信号量，允许3个线程同时访问
Semaphore semaphore = new Semaphore(3);

for (int i = 0; i < 10; i++) {
    int id = i;
    new Thread(() -> {
        try {
            semaphore.acquire();  // 获取许可
            System.out.println("线程" + id + "获取许可，开始执行");
            Thread.sleep(2000);  // 模拟任务
            System.out.println("线程" + id + "释放许可");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();  // 释放许可（必须在finally中）
        }
    }).start();
}

// 输出：同时最多3个线程执行
```

### 应用场景

#### 场景1：限流

```java
// 限制同时访问API的线程数
public class RateLimiter {
    private final Semaphore semaphore;
    
    public RateLimiter(int maxConcurrent) {
        this.semaphore = new Semaphore(maxConcurrent);
    }
    
    public void execute(Runnable task) {
        try {
            semaphore.acquire();
            try {
                task.run();
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// 使用
RateLimiter limiter = new RateLimiter(10);  // 最多10个并发
limiter.execute(() -> {
    // 调用API
});
```

#### 场景2：对象池

```java
// 数据库连接池
public class ConnectionPool {
    private final List<Connection> connections;
    private final Semaphore semaphore;
    
    public ConnectionPool(int size) {
        connections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            connections.add(createConnection());
        }
        semaphore = new Semaphore(size);
    }
    
    public Connection getConnection() throws InterruptedException {
        semaphore.acquire();  // 获取许可
        synchronized (connections) {
            return connections.remove(0);
        }
    }
    
    public void releaseConnection(Connection conn) {
        synchronized (connections) {
            connections.add(conn);
        }
        semaphore.release();  // 释放许可
    }
}
```

#### 场景3：资源访问控制

```java
// 限制同时打印的线程数
Semaphore printSemaphore = new Semaphore(2);  // 最多2个线程同时打印

for (int i = 0; i < 10; i++) {
    int id = i;
    new Thread(() -> {
        try {
            printSemaphore.acquire();
            System.out.println("线程" + id + "开始打印");
            Thread.sleep(1000);
            System.out.println("线程" + id + "打印完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            printSemaphore.release();
        }
    }).start();
}
```

### 公平性

```java
// 非公平信号量（默认，性能好）
Semaphore semaphore = new Semaphore(3, false);

// 公平信号量（按申请顺序获取，避免饥饿）
Semaphore semaphore = new Semaphore(3, true);
```

## 五、Exchanger（交换器）

### 定义

**Exchanger**：用于两个线程之间交换数据。

**类比**：物品交换
```
线程1：我有A，想要B
线程2：我有B，想要A

exchange()：交换
线程1得到B，线程2得到A
```

### 基本用法

```java
import java.util.concurrent.Exchanger;

Exchanger<String> exchanger = new Exchanger<>();

// 线程1
new Thread(() -> {
    try {
        String data = "线程1的数据";
        System.out.println("线程1发送：" + data);
        String received = exchanger.exchange(data);
        System.out.println("线程1接收：" + received);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}).start();

// 线程2
new Thread(() -> {
    try {
        String data = "线程2的数据";
        System.out.println("线程2发送：" + data);
        String received = exchanger.exchange(data);
        System.out.println("线程2接收：" + received);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}).start();

// 输出：
// 线程1发送：线程1的数据
// 线程2发送：线程2的数据
// 线程1接收：线程2的数据
// 线程2接收：线程1的数据
```

### 应用场景

#### 场景1：遗传算法

```java
// 两个线程分别计算，交换结果
Exchanger<List<Gene>> exchanger = new Exchanger<>();

// 线程1：种群A
new Thread(() -> {
    List<Gene> populationA = initPopulation();
    for (int generation = 0; generation < 100; generation++) {
        populationA = evolve(populationA);
        if (generation % 10 == 0) {
            // 每10代交换一次
            populationA = exchanger.exchange(populationA);
        }
    }
}).start();

// 线程2：种群B
new Thread(() -> {
    List<Gene> populationB = initPopulation();
    for (int generation = 0; generation < 100; generation++) {
        populationB = evolve(populationB);
        if (generation % 10 == 0) {
            populationB = exchanger.exchange(populationB);
        }
    }
}).start();
```

#### 场景2：数据校验

```java
// 生产者-消费者交换数据进行校验
Exchanger<List<Data>> exchanger = new Exchanger<>();

// 生产者
new Thread(() -> {
    List<Data> produced = produce();
    List<Data> consumed = exchanger.exchange(produced);
    // 对比produced和consumed，检查数据一致性
}).start();

// 消费者
new Thread(() -> {
    List<Data> consumed = consume();
    List<Data> produced = exchanger.exchange(consumed);
    // 对比consumed和produced，检查数据一致性
}).start();
```

## 六、Phaser（阶段器）

### 定义

**Phaser**：可重用的同步屏障，支持动态调整线程数量，功能比CyclicBarrier更强大。

### 基本用法

```java
import java.util.concurrent.Phaser;

Phaser phaser = new Phaser(3);  // 3个参与者

for (int i = 0; i < 3; i++) {
    int id = i;
    new Thread(() -> {
        System.out.println("线程" + id + "阶段1");
        phaser.arriveAndAwaitAdvance();  // 到达并等待
        
        System.out.println("线程" + id + "阶段2");
        phaser.arriveAndAwaitAdvance();
        
        System.out.println("线程" + id + "阶段3");
        phaser.arriveAndAwaitAdvance();
    }).start();
}
```

### Phaser vs CyclicBarrier

| 特性 | CyclicBarrier | Phaser |
|-----|-------------|--------|
| **参与者数量** | 固定 | 动态调整 |
| **阶段数** | 无限制 | 支持阶段编号 |
| **灵活性** | 较低 | 较高 |
| **使用复杂度** | 简单 | 复杂 |

## 七、工具类对比总结

| 工具类 | 用途 | 可重用 | 典型场景 |
|-------|-----|--------|---------|
| **CountDownLatch** | 一个线程等N个线程完成 | ✗ | 并行计算、启动同步 |
| **CyclicBarrier** | N个线程互相等待 | ✓ | 分阶段任务 |
| **Semaphore** | 控制并发数量 | ✓ | 限流、资源池 |
| **Exchanger** | 两个线程交换数据 | ✓ | 数据交换 |
| **Phaser** | 多阶段同步（动态） | ✓ | 复杂多阶段任务 |

## 八、小结

**核心要点**：

1. **CountDownLatch**：
   - 倒计时门栓
   - countDown()递减，await()等待
   - 不可重用
   - 适合：一个等多个

2. **CyclicBarrier**：
   - 循环栅栏
   - await()到达屏障
   - 可重用
   - 适合：多个互相等待

3. **Semaphore**：
   - 信号量
   - acquire()获取许可，release()释放许可
   - 适合：限流、资源池

4. **Exchanger**：
   - 交换器
   - exchange()交换数据
   - 适合：两个线程交换

5. **Phaser**：
   - 阶段器
   - arriveAndAwaitAdvance()到达并等待
   - 支持动态参与者
   - 适合：复杂多阶段任务

**选择建议**：
- 一个等多个 → CountDownLatch
- 多个互相等待 → CyclicBarrier
- 限制并发数 → Semaphore
- 两线程交换 → Exchanger
- 复杂多阶段 → Phaser

**记忆口诀**：
- CountDown倒计时，一等多完成
- Barrier是栅栏，多个互等待
- Semaphore限并发，资源池常用
- Exchanger做交换，两线程互换

---

💡 **提示**：并发工具类比wait/notify更简单易用，合理使用可大幅简化线程协作代码。
