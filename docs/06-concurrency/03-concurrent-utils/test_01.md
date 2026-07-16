# 并发工具类 - 测试题

## 一、选择题

### 1. CountDownLatch的计数器到达0后，还能重新使用吗？
A. 可以  
B. 不可以  
C. 需要调用reset()方法  
D. 取决于初始值

**答案**：B

**解析**：CountDownLatch不可重用，计数器到0后无法重置。如需重用，应使用CyclicBarrier。

---

### 2. 以下哪个并发工具类适合实现"一个线程等待多个线程完成"的场景？
A. CyclicBarrier  
B. Semaphore  
C. CountDownLatch  
D. Exchanger

**答案**：C

**解析**：CountDownLatch专门用于一个或多个线程等待其他线程完成操作。

---

### 3. Semaphore的主要作用是什么？
A. 线程间交换数据  
B. 控制同时访问资源的线程数量  
C. 等待所有线程完成  
D. 线程同步屏障

**答案**：B

**解析**：Semaphore（信号量）用于控制同时访问特定资源的线程数量，常用于限流和资源池。

---

### 4. CyclicBarrier和CountDownLatch的主要区别是？
A. 完全相同  
B. CyclicBarrier可重用，CountDownLatch不可重用  
C. CountDownLatch可重用，CyclicBarrier不可重用  
D. 两者都不可重用

**答案**：B

**解析**：CyclicBarrier可重用（所有线程到达后自动重置），CountDownLatch不可重用（计数到0后无法重置）。

---

### 5. Exchanger最多支持几个线程交换数据？
A. 1个  
B. 2个  
C. 任意个  
D. 取决于构造参数

**答案**：B

**解析**：Exchanger用于两个线程之间交换数据，只支持2个线程。

---

### 6. 以下代码的输出是什么？
```java
CountDownLatch latch = new CountDownLatch(2);
latch.countDown();
latch.countDown();
latch.countDown();  // 第3次
latch.await();
System.out.println("完成");
```
A. 阻塞  
B. 完成  
C. 抛异常  
D. 编译错误

**答案**：B

**解析**：countDown()可以多次调用，计数器到0后就不再减少。第2次countDown后计数器已为0，await()立即返回。

---

### 7. Semaphore(3)表示什么？
A. 创建3个信号量  
B. 允许3个线程同时访问  
C. 等待3个线程完成  
D. 3个阶段

**答案**：B

**解析**：Semaphore(3)创建一个拥有3个许可的信号量，最多允许3个线程同时获取许可（访问资源）。

---

### 8. 使用Semaphore时，以下哪个操作必须在finally块中执行？
A. acquire()  
B. release()  
C. tryAcquire()  
D. availablePermits()

**答案**：B

**解析**：release()必须在finally中执行，确保即使发生异常也能释放许可，避免许可泄漏。

---

### 9. CyclicBarrier创建时可以指定什么？
A. 只能指定参与线程数  
B. 只能指定barrierAction  
C. 可以指定参与线程数和barrierAction  
D. 不需要指定任何参数

**答案**：C

**解析**：CyclicBarrier构造函数可以指定参与线程数（必须）和barrierAction（可选，所有线程到达后执行）。

---

### 10. 以下哪个工具类支持动态调整参与者数量？
A. CountDownLatch  
B. CyclicBarrier  
C. Semaphore  
D. Phaser

**答案**：D

**解析**：Phaser支持动态注册和注销参与者，其他工具类的参与者数量在创建时固定。

---

## 二、填空题

### 1. CountDownLatch的两个核心方法是________和________。

**答案**：countDown()、await()

---

### 2. CyclicBarrier的核心方法是________，表示线程到达屏障并等待其他线程。

**答案**：await()

---

### 3. Semaphore获取许可的方法是________，释放许可的方法是________。

**答案**：acquire()、release()

---

### 4. Exchanger的核心方法是________，用于两个线程交换数据。

**答案**：exchange()

---

### 5. 要创建一个公平的Semaphore，构造函数应该写成：________。

**答案**：`new Semaphore(permits, true)`

---

## 三、判断题

### 1. CountDownLatch的计数器可以增加。（ ）

**答案**：✗

**解析**：CountDownLatch的计数器只能递减（countDown），不能增加。

---

### 2. CyclicBarrier可以重复使用多次。（ ）

**答案**：✓

**解析**：CyclicBarrier是"循环"的，所有线程到达后会自动重置，可以重复使用。

---

### 3. Semaphore.acquire()如果获取不到许可会一直阻塞。（ ）

**答案**：✓

**解析**：acquire()会阻塞直到获取到许可，或者使用tryAcquire()可以设置超时或不阻塞。

---

### 4. Exchanger可以用于3个线程之间交换数据。（ ）

**答案**：✗

**解析**：Exchanger只支持2个线程之间交换数据。

---

### 5. 使用CyclicBarrier时，必须确保有足够的线程调用await()，否则会永久阻塞。（ ）

**答案**：✓

**解析**：如果参与线程数少于指定数量，没到达的线程会永久阻塞。

---

## 四、简答题

### 1. 说明CountDownLatch和CyclicBarrier的区别，以及各自的适用场景。

**答案**：

**区别对比**：

| 特性 | CountDownLatch | CyclicBarrier |
|-----|---------------|--------------|
| **计数方式** | 递减（countDown） | 不计数（await到达） |
| **是否可重用** | 不可重用 | 可重用 |
| **等待方式** | 1个或多个线程等待 | N个线程互相等待 |
| **触发动作** | 无 | 可指定barrierAction |
| **应用场景** | 一个等多个完成 | 多个线程分阶段同步 |

**CountDownLatch适用场景**：
1. **主线程等待多个工作线程完成**：
   ```java
   CountDownLatch latch = new CountDownLatch(10);
   // 创建10个工作线程，每个完成后countDown()
   latch.await();  // 主线程等待
   ```

2. **并行计算后汇总结果**：
   ```java
   // 多个线程并行计算，主线程等待后汇总
   ```

3. **模拟高并发测试**：
   ```java
   // 所有线程准备好后，同时开始请求
   ```

**CyclicBarrier适用场景**：
1. **多线程分阶段执行**：
   ```java
   CyclicBarrier barrier = new CyclicBarrier(3);
   // 阶段1 → barrier.await()
   // 阶段2 → barrier.await()
   // 可重复使用
   ```

2. **迭代计算**：
   ```java
   // 每次迭代所有线程都要到达屏障再继续
   ```

**选择建议**：
- 一个线程等多个线程 → CountDownLatch
- 多个线程互相等待 → CyclicBarrier
- 需要重用 → CyclicBarrier

---

### 2. Semaphore如何实现限流？请给出示例代码。

**答案**：

**原理**：Semaphore通过控制许可数量来限制同时访问资源的线程数。

**限流实现**：

```java
public class RateLimiter {
    private final Semaphore semaphore;
    
    /**
     * @param maxConcurrent 最大并发数
     */
    public RateLimiter(int maxConcurrent) {
        this.semaphore = new Semaphore(maxConcurrent);
    }
    
    /**
     * 执行任务，限制并发数
     */
    public void execute(Runnable task) {
        try {
            semaphore.acquire();  // 获取许可
            try {
                task.run();  // 执行任务
            } finally {
                semaphore.release();  // 释放许可（必须在finally中）
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 尝试执行任务，获取不到许可立即返回
     */
    public boolean tryExecute(Runnable task) {
        if (semaphore.tryAcquire()) {
            try {
                task.run();
                return true;
            } finally {
                semaphore.release();
            }
        }
        return false;  // 未获取到许可
    }
    
    /**
     * 尝试执行任务，等待指定时间
     */
    public boolean tryExecute(Runnable task, long timeout, TimeUnit unit) 
            throws InterruptedException {
        if (semaphore.tryAcquire(timeout, unit)) {
            try {
                task.run();
                return true;
            } finally {
                semaphore.release();
            }
        }
        return false;
    }
}
```

**使用示例**：

```java
// 限制最多10个并发请求
RateLimiter limiter = new RateLimiter(10);

// 100个线程请求
for (int i = 0; i < 100; i++) {
    int id = i;
    new Thread(() -> {
        limiter.execute(() -> {
            System.out.println("线程" + id + "执行任务");
            try {
                Thread.sleep(1000);  // 模拟任务
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }).start();
}

// 同一时刻最多10个线程在执行任务
```

**应用场景**：
1. **API限流**：限制同时访问API的请求数
2. **数据库连接池**：限制并发连接数
3. **资源访问控制**：限制对有限资源的并发访问

---

### 3. 解释以下代码的执行流程，并说明最终输出。

```java
CountDownLatch startSignal = new CountDownLatch(1);
CountDownLatch doneSignal = new CountDownLatch(3);

for (int i = 0; i < 3; i++) {
    int id = i;
    new Thread(() -> {
        try {
            System.out.println("线程" + id + "准备");
            startSignal.await();
            System.out.println("线程" + id + "执行");
            Thread.sleep(1000);
            doneSignal.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
}

Thread.sleep(2000);
System.out.println("发送启动信号");
startSignal.countDown();

doneSignal.await();
System.out.println("所有任务完成");
```

**答案**：

**执行流程**：

1. **创建两个CountDownLatch**：
   - `startSignal`：计数器=1（用于发送启动信号）
   - `doneSignal`：计数器=3（等待3个线程完成）

2. **启动3个线程**：
   ```
   线程0：打印"线程0准备" → await()阻塞
   线程1：打印"线程1准备" → await()阻塞
   线程2：打印"线程2准备" → await()阻塞
   ```

3. **主线程休眠2秒**：
   ```
   所有工作线程都在等待startSignal
   ```

4. **主线程发送启动信号**：
   ```
   打印"发送启动信号"
   startSignal.countDown() → 计数器0
   → 所有等待的线程被唤醒
   ```

5. **3个线程开始执行**：
   ```
   线程0：打印"线程0执行" → sleep 1秒 → countDown()
   线程1：打印"线程1执行" → sleep 1秒 → countDown()
   线程2：打印"线程2执行" → sleep 1秒 → countDown()
   ```

6. **主线程等待完成**：
   ```
   doneSignal.await()阻塞
   → 3个线程都countDown()后，计数器0
   → 主线程被唤醒
   ```

7. **主线程继续执行**：
   ```
   打印"所有任务完成"
   ```

**输出顺序**（可能有细微差异）：
```
线程0准备
线程1准备
线程2准备
（等待2秒）
发送启动信号
线程0执行
线程1执行
线程2执行
（等待1秒）
所有任务完成
```

**设计模式**：这是典型的"等待所有线程就绪 → 统一开始 → 等待所有线程完成"模式。

---

### 4. 使用CyclicBarrier实现3个线程分3个阶段执行，每个阶段结束后同步。

**答案**：

```java
import java.util.concurrent.CyclicBarrier;

public class PhaseExecutionDemo {
    public static void main(String[] args) {
        int threadCount = 3;
        
        // 创建屏障，所有线程到达后打印阶段完成信息
        CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
            System.out.println("===== 阶段完成，进入下一阶段 =====");
        });
        
        for (int i = 0; i < threadCount; i++) {
            int id = i;
            new Thread(() -> {
                try {
                    // 阶段1：数据准备
                    System.out.println("线程" + id + " - 阶段1：准备数据");
                    Thread.sleep((long) (Math.random() * 1000));
                    System.out.println("线程" + id + " - 阶段1完成");
                    barrier.await();  // 等待其他线程完成阶段1
                    
                    // 阶段2：数据处理
                    System.out.println("线程" + id + " - 阶段2：处理数据");
                    Thread.sleep((long) (Math.random() * 1000));
                    System.out.println("线程" + id + " - 阶段2完成");
                    barrier.await();  // 等待其他线程完成阶段2
                    
                    // 阶段3：数据保存
                    System.out.println("线程" + id + " - 阶段3：保存数据");
                    Thread.sleep((long) (Math.random() * 1000));
                    System.out.println("线程" + id + " - 阶段3完成");
                    barrier.await();  // 等待其他线程完成阶段3
                    
                    System.out.println("线程" + id + " - 所有阶段完成");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Worker-" + id).start();
        }
    }
}
```

**输出示例**：
```
线程0 - 阶段1：准备数据
线程1 - 阶段1：准备数据
线程2 - 阶段1：准备数据
线程1 - 阶段1完成
线程0 - 阶段1完成
线程2 - 阶段1完成
===== 阶段完成，进入下一阶段 =====
线程2 - 阶段2：处理数据
线程0 - 阶段2：处理数据
线程1 - 阶段2：处理数据
线程0 - 阶段2完成
线程2 - 阶段2完成
线程1 - 阶段2完成
===== 阶段完成，进入下一阶段 =====
线程1 - 阶段3：保存数据
线程2 - 阶段3：保存数据
线程0 - 阶段3：保存数据
线程1 - 阶段3完成
线程0 - 阶段3完成
线程2 - 阶段3完成
===== 阶段完成，进入下一阶段 =====
线程1 - 所有阶段完成
线程2 - 所有阶段完成
线程0 - 所有阶段完成
```

**关键点**：
1. 每个阶段结束后调用`barrier.await()`
2. 所有线程到达屏障后，执行barrierAction
3. CyclicBarrier自动重置，可以重复使用
4. 适合多阶段、多线程协作的场景

---

### 5. Semaphore如何避免许可泄漏？请说明正确的使用模式。

**答案**：

**许可泄漏问题**：

如果获取许可后没有释放，会导致许可泄漏，最终所有许可都被占用，后续线程无法获取。

```java
// ❌ 错误示例：可能泄漏
Semaphore semaphore = new Semaphore(10);
semaphore.acquire();
doSomething();  // 如果抛异常，release()不会执行
semaphore.release();  // 许可泄漏
```

**正确使用模式**：

**模式1：try-finally（推荐）**
```java
Semaphore semaphore = new Semaphore(10);

semaphore.acquire();  // 获取许可
try {
    // 业务逻辑
    doSomething();
} finally {
    semaphore.release();  // 必须在finally中释放
}
```

**模式2：try-with-resources（自定义封装）**
```java
// 封装AutoCloseable
public class SemaphoreResource implements AutoCloseable {
    private final Semaphore semaphore;
    
    public SemaphoreResource(Semaphore semaphore) throws InterruptedException {
        this.semaphore = semaphore;
        semaphore.acquire();
    }
    
    @Override
    public void close() {
        semaphore.release();
    }
}

// 使用
Semaphore semaphore = new Semaphore(10);
try (SemaphoreResource resource = new SemaphoreResource(semaphore)) {
    // 业务逻辑
    doSomething();
}  // 自动释放
```

**模式3：tryAcquire + try-finally**
```java
Semaphore semaphore = new Semaphore(10);

if (semaphore.tryAcquire()) {
    try {
        // 业务逻辑
        doSomething();
    } finally {
        semaphore.release();
    }
} else {
    // 未获取到许可的处理
}
```

**模式4：超时 + try-finally**
```java
Semaphore semaphore = new Semaphore(10);

if (semaphore.tryAcquire(3, TimeUnit.SECONDS)) {
    try {
        // 业务逻辑
        doSomething();
    } finally {
        semaphore.release();
    }
} else {
    // 超时未获取到许可
}
```

**注意事项**：
1. **必须在finally中释放**：确保异常时也能释放
2. **acquire和release成对出现**：避免重复释放或忘记释放
3. **不要在catch块中release**：如果acquire抛异常，不应该release
4. **监控可用许可数**：使用`availablePermits()`监控

**完整示例**：
```java
public class SemaphoreUsage {
    private final Semaphore semaphore = new Semaphore(10);
    
    public void execute() {
        try {
            semaphore.acquire();
            try {
                // 业务逻辑
                performTask();
            } finally {
                semaphore.release();  // 确保释放
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // 注意：这里不需要release，因为acquire失败时没有获取到许可
        }
    }
    
    public void monitorPermits() {
        System.out.println("可用许可数：" + semaphore.availablePermits());
        System.out.println("等待线程数：" + semaphore.getQueueLength());
    }
}
```

---

## 五、编程题

### 使用CountDownLatch实现：主线程等待5个工作线程完成任务后，汇总所有结果并打印。

**答案**：

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        int workerCount = 5;
        CountDownLatch latch = new CountDownLatch(workerCount);
        
        // 线程安全的结果列表
        List<Integer> results = Collections.synchronizedList(new ArrayList<>());
        
        // 创建5个工作线程
        for (int i = 0; i < workerCount; i++) {
            int workerId = i;
            new Thread(() -> {
                try {
                    // 模拟任务执行
                    System.out.println("工作线程" + workerId + "开始执行");
                    Thread.sleep((long) (Math.random() * 2000));
                    
                    // 计算结果
                    int result = new Random().nextInt(100);
                    results.add(result);
                    
                    System.out.println("工作线程" + workerId + "完成，结果：" + result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();  // 完成任务，计数器-1
                }
            }, "Worker-" + i).start();
        }
        
        System.out.println("主线程等待所有工作线程完成...");
        latch.await();  // 等待所有工作线程完成
        
        // 汇总结果
        System.out.println("\n===== 所有任务完成 =====");
        System.out.println("结果列表：" + results);
        int sum = results.stream().mapToInt(Integer::intValue).sum();
        double average = results.stream().mapToInt(Integer::intValue).average().orElse(0);
        System.out.println("总和：" + sum);
        System.out.println("平均值：" + average);
    }
}
```

---

💡 **提示**：并发工具类是JUC包的精华，掌握它们能让线程协作变得简单优雅！
