# 线程基础与线程池

## 一、线程基础

### 什么是线程？

**定义**：线程是操作系统能够进行运算调度的最小单位，是进程中的一个执行流程。

**类比**：餐厅的服务员
```
进程 = 餐厅（资源容器）
线程 = 服务员（执行任务）
多线程 = 多个服务员同时服务（并发）
```

### 创建线程的方式

#### 方式1：继承Thread类

```java
public class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("线程执行：" + Thread.currentThread().getName());
    }
}

// 使用
MyThread thread = new MyThread();
thread.start();  // 启动线程
```

#### 方式2：实现Runnable接口（推荐）

```java
public class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("任务执行：" + Thread.currentThread().getName());
    }
}

// 使用
Thread thread = new Thread(new MyTask());
thread.start();

// Lambda写法
new Thread(() -> {
    System.out.println("Lambda线程");
}).start();
```

#### 方式3：实现Callable接口（有返回值）

```java
public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        Thread.sleep(1000);
        return "任务结果";
    }
}

// 使用
FutureTask<String> task = new FutureTask<>(new MyCallable());
new Thread(task).start();
String result = task.get();  // 阻塞等待结果
```

### 线程状态

```
NEW（新建）
   ↓ start()
RUNNABLE（可运行）
   ↓ 获得CPU
   ↓ ← → RUNNING（运行中）
   ↓
BLOCKED（阻塞）← 等待锁
WAITING（等待）← wait()、join()
TIMED_WAITING（超时等待）← sleep()、wait(timeout)
   ↓
TERMINATED（终止）
```

## 二、为什么需要线程池？

### 问题：直接创建线程的缺点

```java
// ❌ 每次请求都创建新线程
public void handleRequest(Request req) {
    new Thread(() -> {
        processRequest(req);
    }).start();
}
```

**缺点**：
1. **创建销毁开销大**：每次创建线程耗时耗资源
2. **无法控制数量**：高并发时可能创建上万个线程
3. **资源耗尽**：线程过多导致OOM（内存溢出）
4. **频繁上下文切换**：降低性能

### 解决方案：线程池

```
线程池 = 预先创建好的线程集合

好处：
1. 复用线程（避免频繁创建销毁）
2. 控制并发数量（避免资源耗尽）
3. 提供管理功能（定时、周期、取消）
```

## 三、线程池核心原理

### ThreadPoolExecutor参数

```java
public ThreadPoolExecutor(
    int corePoolSize,          // 核心线程数
    int maximumPoolSize,       // 最大线程数
    long keepAliveTime,        // 空闲线程存活时间
    TimeUnit unit,             // 时间单位
    BlockingQueue<Runnable> workQueue,  // 任务队列
    ThreadFactory threadFactory,        // 线程工厂
    RejectedExecutionHandler handler    // 拒绝策略
)
```

### 执行流程

```
提交任务
   ↓
线程数 < corePoolSize？
   ↓ 是
创建核心线程执行
   ↓ 否
队列已满？
   ↓ 否
任务入队列
   ↓ 是
线程数 < maximumPoolSize？
   ↓ 是
创建临时线程执行
   ↓ 否
执行拒绝策略
```

### 示例演示

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    2,                          // 核心线程数=2
    5,                          // 最大线程数=5
    60, TimeUnit.SECONDS,       // 空闲线程60秒后回收
    new LinkedBlockingQueue<>(3),  // 队列容量=3
    Executors.defaultThreadFactory(),
    new ThreadPoolExecutor.AbortPolicy()  // 拒绝策略：抛异常
);

// 提交任务
executor.execute(() -> {
    System.out.println("任务执行");
});
```

**执行过程**：
```
任务1 → 创建核心线程1执行
任务2 → 创建核心线程2执行
任务3 → 放入队列（位置1）
任务4 → 放入队列（位置2）
任务5 → 放入队列（位置3）
任务6 → 创建临时线程3执行
任务7 → 创建临时线程4执行
任务8 → 创建临时线程5执行
任务9 → 拒绝（抛异常）
```

## 四、常用线程池类型

### 1. FixedThreadPool（固定大小线程池）

```java
ExecutorService executor = Executors.newFixedThreadPool(5);

// 等价于
new ThreadPoolExecutor(
    5, 5,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>()  // 无界队列
);
```

**特点**：
- 核心线程数 = 最大线程数
- 无界队列
- 适合：任务数量稳定的场景

**风险**：队列无界，可能OOM

### 2. CachedThreadPool（缓存线程池）

```java
ExecutorService executor = Executors.newCachedThreadPool();

// 等价于
new ThreadPoolExecutor(
    0, Integer.MAX_VALUE,
    60L, TimeUnit.SECONDS,
    new SynchronousQueue<>()  // 直接交付队列
);
```

**特点**：
- 核心线程数=0，最大线程数=无限
- 线程空闲60秒后回收
- 适合：短期异步任务

**风险**：线程数无限，可能OOM

### 3. SingleThreadExecutor（单线程池）

```java
ExecutorService executor = Executors.newSingleThreadExecutor();

// 等价于
new ThreadPoolExecutor(
    1, 1,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>()
);
```

**特点**：
- 只有1个线程
- 保证任务顺序执行
- 适合：需要顺序执行的任务

### 4. ScheduledThreadPool（定时线程池）

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

// 延迟执行
executor.schedule(() -> {
    System.out.println("3秒后执行");
}, 3, TimeUnit.SECONDS);

// 周期执行（固定频率）
executor.scheduleAtFixedRate(() -> {
    System.out.println("每5秒执行一次");
}, 0, 5, TimeUnit.SECONDS);

// 周期执行（固定延迟）
executor.scheduleWithFixedDelay(() -> {
    System.out.println("上次执行结束后延迟3秒再执行");
}, 0, 3, TimeUnit.SECONDS);
```

**特点**：
- 支持定时、周期任务
- 适合：定时任务

## 五、拒绝策略

### 1. AbortPolicy（默认，抛异常）

```java
new ThreadPoolExecutor.AbortPolicy()

// 行为：抛出RejectedExecutionException
```

### 2. CallerRunsPolicy（调用者执行）

```java
new ThreadPoolExecutor.CallerRunsPolicy()

// 行为：由提交任务的线程执行
// 好处：降低提交速度，给线程池缓冲时间
```

### 3. DiscardPolicy（丢弃任务）

```java
new ThreadPoolExecutor.DiscardPolicy()

// 行为：默默丢弃，不报错
```

### 4. DiscardOldestPolicy（丢弃最老任务）

```java
new ThreadPoolExecutor.DiscardOldestPolicy()

// 行为：丢弃队列中最早的任务，然后重试
```

## 六、线程池最佳实践

### 实践1：手动创建线程池（推荐）

```java
// ❌ 不推荐：使用Executors
ExecutorService executor = Executors.newFixedThreadPool(10);

// ✅ 推荐：手动创建
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    10,                         // 核心线程数
    20,                         // 最大线程数
    60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),  // 有界队列
    new ThreadFactoryBuilder()
        .setNameFormat("my-pool-%d")
        .build(),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

**原因**：Executors创建的线程池有OOM风险

### 实践2：合理设置线程数

**CPU密集型任务**（计算多）：
```
线程数 = CPU核心数 + 1
```

**IO密集型任务**（网络、磁盘IO多）：
```
线程数 = CPU核心数 * 2
或
线程数 = CPU核心数 / (1 - 阻塞系数)
阻塞系数 = 阻塞时间 / (阻塞时间 + 计算时间)
```

**示例**：
```java
int cpuCount = Runtime.getRuntime().availableProcessors();

// CPU密集型
int corePoolSize = cpuCount + 1;

// IO密集型（阻塞系数0.8）
int corePoolSize = (int) (cpuCount / (1 - 0.8));  // cpuCount * 5
```

### 实践3：使用有界队列

```java
// ❌ 无界队列
new LinkedBlockingQueue<>()

// ✅ 有界队列
new ArrayBlockingQueue<>(100)
new LinkedBlockingQueue<>(100)
```

### 实践4：给线程命名

```java
ThreadFactory factory = new ThreadFactoryBuilder()
    .setNameFormat("order-pool-%d")
    .setDaemon(false)
    .build();

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    10, 20, 60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),
    factory,  // 使用自定义线程工厂
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

**好处**：便于排查问题

### 实践5：优雅关闭

```java
// 1. 停止接收新任务，等待已提交任务完成
executor.shutdown();

// 2. 等待60秒
if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
    // 3. 超时强制关闭
    executor.shutdownNow();
    
    // 4. 再等待60秒
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        System.err.println("线程池未正常关闭");
    }
}
```

### 实践6：监控线程池

```java
ThreadPoolExecutor executor = ...;

// 监控指标
int activeCount = executor.getActiveCount();       // 活跃线程数
long completedTaskCount = executor.getCompletedTaskCount();  // 已完成任务数
int queueSize = executor.getQueue().size();        // 队列任务数
int poolSize = executor.getPoolSize();             // 当前线程数

// 定时打印
ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
monitor.scheduleAtFixedRate(() -> {
    System.out.printf("活跃线程: %d, 队列任务: %d, 已完成: %d%n",
        activeCount, queueSize, completedTaskCount);
}, 0, 10, TimeUnit.SECONDS);
```

## 七、常见问题

### 问题1：为什么不用Executors？

**阿里巴巴Java开发手册明确规定**：
```
【强制】线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor的方式。

原因：
1. FixedThreadPool/SingleThreadExecutor → 队列无界 → OOM
2. CachedThreadPool → 线程数无界 → OOM
```

### 问题2：核心线程会被回收吗？

**默认不会**：
```java
executor.setCorePoolSize(10);  // 核心线程=10，默认不回收
```

**可配置回收**：
```java
executor.allowCoreThreadTimeOut(true);  // 允许核心线程超时回收
```

### 问题3：execute vs submit

```java
// execute：无返回值
executor.execute(() -> {
    System.out.println("无返回值");
});

// submit：有返回值
Future<String> future = executor.submit(() -> {
    return "有返回值";
});
String result = future.get();  // 阻塞获取结果
```

## 八、小结

**核心要点**：

1. **线程基础**：
   - 创建方式：Thread、Runnable、Callable
   - 状态：NEW → RUNNABLE → RUNNING → TERMINATED

2. **线程池原理**：
   - 核心线程 → 队列 → 临时线程 → 拒绝策略
   - 7大参数：核心数、最大数、存活时间、队列、工厂、拒绝策略

3. **常用线程池**：
   - FixedThreadPool：固定大小
   - CachedThreadPool：缓存
   - SingleThreadExecutor：单线程
   - ScheduledThreadPool：定时

4. **最佳实践**：
   - 手动创建，不用Executors
   - 合理设置线程数
   - 使用有界队列
   - 给线程命名
   - 优雅关闭

**记忆口诀**：
- 线程池复用线程，控制并发
- 核心参数要记清，队列要有界
- CPU密集核心数，IO密集要翻倍
- Executors有风险，手动创建稳

---

💡 **提示**：线程池是并发编程的基础，合理使用可大幅提升性能。记住阿里规范：禁用Executors！
