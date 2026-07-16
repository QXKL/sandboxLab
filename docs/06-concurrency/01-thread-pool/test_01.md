# 线程基础与线程池 - 测试题

## 一、选择题

### 1. 以下哪种创建线程的方式可以获取返回值？
A. 继承Thread类  
B. 实现Runnable接口  
C. 实现Callable接口  
D. 使用Lambda表达式

**答案**：C

**解析**：Callable接口的call()方法可以返回结果，配合FutureTask使用。

---

### 2. 线程池的核心参数中，corePoolSize表示什么？
A. 最大线程数  
B. 核心线程数  
C. 队列大小  
D. 空闲线程存活时间

**答案**：B

**解析**：corePoolSize是线程池的核心线程数，即使空闲也不会被回收（除非设置allowCoreThreadTimeOut）。

---

### 3. 以下哪个线程池存在OOM风险？
A. 手动创建的ThreadPoolExecutor（有界队列）  
B. Executors.newFixedThreadPool()  
C. 自定义ThreadPoolExecutor（ArrayBlockingQueue）  
D. 以上都不会

**答案**：B

**解析**：newFixedThreadPool使用无界队列LinkedBlockingQueue，任务堆积时可能导致OOM。

---

### 4. 线程池任务提交流程正确的是？
A. 核心线程 → 临时线程 → 队列 → 拒绝  
B. 队列 → 核心线程 → 临时线程 → 拒绝  
C. 核心线程 → 队列 → 临时线程 → 拒绝  
D. 临时线程 → 核心线程 → 队列 → 拒绝

**答案**：C

**解析**：先创建核心线程，满了放队列，队列满了创建临时线程，都满了执行拒绝策略。

---

### 5. CPU密集型任务，线程数设置多少合适？（假设8核CPU）
A. 8  
B. 9  
C. 16  
D. 32

**答案**：B

**解析**：CPU密集型任务，线程数 = CPU核心数 + 1，即 8 + 1 = 9。

---

### 6. 以下哪个拒绝策略会抛出异常？
A. CallerRunsPolicy  
B. AbortPolicy  
C. DiscardPolicy  
D. DiscardOldestPolicy

**答案**：B

**解析**：AbortPolicy是默认策略，会抛出RejectedExecutionException。

---

### 7. 以下代码的输出是什么？
```java
ExecutorService executor = Executors.newSingleThreadExecutor();
executor.execute(() -> System.out.println("A"));
executor.execute(() -> System.out.println("B"));
executor.execute(() -> System.out.println("C"));
```
A. ABC（顺序输出）  
B. 随机顺序  
C. CBA（逆序输出）  
D. 可能死锁

**答案**：A

**解析**：SingleThreadExecutor只有1个线程，任务按提交顺序执行。

---

### 8. 以下关于线程池的说法错误的是？
A. 线程池可以复用线程，减少创建销毁开销  
B. 核心线程默认不会被回收  
C. 使用Executors创建线程池是最佳实践  
D. 线程池可以控制并发数量

**答案**：C

**解析**：阿里规范明确禁止使用Executors，应手动创建ThreadPoolExecutor。

---

### 9. ScheduledThreadPool的scheduleAtFixedRate和scheduleWithFixedDelay的区别？
A. 完全相同  
B. 前者固定频率，后者固定延迟  
C. 前者固定延迟，后者固定频率  
D. 前者不会重复执行

**答案**：B

**解析**：scheduleAtFixedRate按固定频率执行（不考虑任务执行时间），scheduleWithFixedDelay在任务执行完后固定延迟再执行。

---

### 10. 以下哪个方法可以优雅关闭线程池？
A. shutdownNow()  
B. shutdown()  
C. interrupt()  
D. stop()

**答案**：B

**解析**：shutdown()停止接收新任务，等待已提交任务完成；shutdownNow()立即停止所有任务。

---

## 二、填空题

### 1. ThreadPoolExecutor的7个核心参数分别是：________、________、________、________、________、________、________。

**答案**：corePoolSize、maximumPoolSize、keepAliveTime、unit、workQueue、threadFactory、handler

---

### 2. 线程的6种状态分别是：NEW、________、BLOCKED、________、________、TERMINATED。

**答案**：RUNNABLE、WAITING、TIMED_WAITING

---

### 3. IO密集型任务（阻塞系数0.9），8核CPU，线程数建议设置为________。

**答案**：80

**解析**：线程数 = 8 / (1 - 0.9) = 80

---

### 4. CallerRunsPolicy拒绝策略的行为是：________。

**答案**：由提交任务的线程执行任务（或：调用者线程执行）

---

### 5. 线程池中，当任务数超过核心线程数且队列已满时，会创建________线程。

**答案**：临时（或：非核心）

---

## 三、简答题

### 1. 为什么阿里巴巴Java开发手册禁止使用Executors创建线程池？

**答案**：

Executors创建的线程池存在资源耗尽风险：

1. **FixedThreadPool和SingleThreadExecutor**：
   - 使用无界队列LinkedBlockingQueue
   - 任务堆积时可能导致OOM

2. **CachedThreadPool**：
   - 最大线程数为Integer.MAX_VALUE
   - 高并发时可能创建大量线程导致OOM

3. **ScheduledThreadPool**：
   - 最大线程数也是Integer.MAX_VALUE
   - 存在同样的风险

**推荐做法**：手动创建ThreadPoolExecutor，使用有界队列，明确指定线程数上限。

---

### 2. 说明线程池任务提交的完整执行流程。

**答案**：

```
1. 提交任务

2. 判断：当前线程数 < corePoolSize？
   → 是：创建核心线程执行任务
   → 否：进入步骤3

3. 判断：队列是否已满？
   → 否：任务加入队列等待
   → 是：进入步骤4

4. 判断：当前线程数 < maximumPoolSize？
   → 是：创建临时线程执行任务
   → 否：进入步骤5

5. 执行拒绝策略（RejectedExecutionHandler）
```

**关键点**：
- 优先使用核心线程
- 核心线程满了才用队列
- 队列满了才创建临时线程
- 达到最大线程数才拒绝

---

### 3. 如何合理设置线程池的线程数？请分别说明CPU密集型和IO密集型任务。

**答案**：

**CPU密集型任务**（如计算、加密）：
```
线程数 = CPU核心数 + 1
```
- 原因：主要消耗CPU，线程过多会增加上下文切换开销
- +1是为了当某个线程阻塞时，能有一个备用线程利用CPU

**IO密集型任务**（如网络请求、数据库查询）：
```
线程数 = CPU核心数 * 2
或
线程数 = CPU核心数 / (1 - 阻塞系数)
```
- 原因：大部分时间在等待IO，可以增加线程数提高并发
- 阻塞系数 = 阻塞时间 / (阻塞时间 + 计算时间)

**示例**：
```java
int cpuCount = Runtime.getRuntime().availableProcessors();  // 8核

// CPU密集型
int cpuIntensive = cpuCount + 1;  // 9

// IO密集型（阻塞系数0.8）
int ioIntensive = (int) (cpuCount / (1 - 0.8));  // 40
```

---

### 4. 线程池关闭时，shutdown()和shutdownNow()有什么区别？

**答案**：

| 特性 | shutdown() | shutdownNow() |
|-----|-----------|--------------|
| **接收新任务** | 拒绝 | 拒绝 |
| **队列中任务** | 继续执行完 | 不执行，返回任务列表 |
| **正在执行任务** | 执行完 | 尝试中断 |
| **返回值** | void | List\<Runnable\>（未执行任务） |
| **等待方式** | 温和关闭 | 强制关闭 |

**优雅关闭示例**：
```java
executor.shutdown();  // 1. 温和关闭
if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
    executor.shutdownNow();  // 2. 超时强制关闭
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        System.err.println("线程池未正常关闭");
    }
}
```

---

### 5. 以下代码会发生什么问题？如何解决？

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    2, 2,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>()
);

for (int i = 0; i < 1000000; i++) {
    executor.execute(() -> {
        try {
            Thread.sleep(10000);  // 模拟耗时任务
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
}
```

**答案**：

**问题**：OOM（内存溢出）

**原因分析**：
1. 核心线程数=2，最大线程数=2，只有2个线程
2. 使用无界队列LinkedBlockingQueue
3. 提交100万个任务，每个任务耗时10秒
4. 2个线程消费速度慢，大量任务堆积在队列
5. 队列无限增长，最终内存耗尽

**解决方案**：

**方案1：使用有界队列**
```java
new ArrayBlockingQueue<>(1000)  // 限制队列大小
```

**方案2：增加线程数**
```java
new ThreadPoolExecutor(
    10, 20,  // 增加核心和最大线程数
    ...
)
```

**方案3：添加拒绝策略**
```java
new ThreadPoolExecutor(
    2, 5,
    60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),
    new ThreadPoolExecutor.CallerRunsPolicy()  // 调用者执行，降低提交速度
)
```

---

## 四、编程题

### 1. 手动创建一个线程池，要求：核心线程5个，最大线程10个，队列容量100，空闲线程60秒回收，拒绝策略为调用者执行，线程命名为"my-pool-线程号"。

**答案**：

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5,                              // 核心线程数
    10,                             // 最大线程数
    60, TimeUnit.SECONDS,           // 空闲线程60秒回收
    new ArrayBlockingQueue<>(100),  // 队列容量100
    new ThreadFactory() {           // 自定义线程工厂
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "my-pool-" + threadNumber.getAndIncrement());
            t.setDaemon(false);
            return t;
        }
    },
    new ThreadPoolExecutor.CallerRunsPolicy()  // 调用者执行策略
);
```

**使用Guava的简化写法**：
```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5, 10, 60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),
    new ThreadFactoryBuilder()
        .setNameFormat("my-pool-%d")
        .build(),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

---

### 2. 实现一个定时任务，每隔5秒打印当前时间，延迟3秒后开始执行。

**答案**：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

executor.scheduleAtFixedRate(() -> {
    System.out.println("当前时间：" + 
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
}, 3, 5, TimeUnit.SECONDS);  // 延迟3秒，每隔5秒执行

// 运行一段时间后关闭
Thread.sleep(30000);  // 30秒后
executor.shutdown();
```

---

### 3. 使用线程池并发执行10个任务，每个任务返回一个随机数，最后收集所有结果并打印。

**答案**：

```java
ExecutorService executor = Executors.newFixedThreadPool(5);

List<Future<Integer>> futures = new ArrayList<>();

// 提交10个任务
for (int i = 0; i < 10; i++) {
    int taskId = i;
    Future<Integer> future = executor.submit(() -> {
        int result = new Random().nextInt(100);
        System.out.println("任务" + taskId + "执行，结果：" + result);
        return result;
    });
    futures.add(future);
}

// 收集结果
List<Integer> results = new ArrayList<>();
for (Future<Integer> future : futures) {
    try {
        results.add(future.get());  // 阻塞等待结果
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// 打印所有结果
System.out.println("所有结果：" + results);
System.out.println("总和：" + results.stream().mapToInt(Integer::intValue).sum());

executor.shutdown();
```

---

## 五、场景题

### 某电商系统使用线程池处理订单，配置如下：核心线程10，最大线程20，队列容量50。现在突然来了100个订单请求，请问这100个订单会如何处理？

**答案**：

**处理过程**：

1. **前10个订单**：创建10个核心线程处理

2. **第11-60个订单**（50个）：核心线程都在忙，任务放入队列

3. **第61-70个订单**（10个）：队列满了，创建10个临时线程处理

4. **第71-100个订单**（30个）：
   - 线程数已达最大值20
   - 队列也满了
   - 执行拒绝策略

**结果统计**：
- 正在执行：20个（10核心 + 10临时）
- 队列等待：50个
- 被拒绝：30个

**优化建议**：
1. 增加核心线程数
2. 增加最大线程数
3. 增加队列容量
4. 使用CallerRunsPolicy拒绝策略（降低提交速度）
5. 监控并动态调整参数

---

💡 **提示**：线程池是并发编程的基础，务必理解其核心原理和最佳实践！
