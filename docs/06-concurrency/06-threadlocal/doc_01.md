# ThreadLocal

## 一、什么是ThreadLocal？

### 定义

**ThreadLocal**：线程局部变量，为每个线程提供独立的变量副本，互不干扰。

**类比**：学生的课本
```
教室有30个学生（线程）
每人有一本数学课本（ThreadLocal变量）
学生A在自己的书上做笔记，不影响学生B的书
```

### 基本用法

```java
// 创建ThreadLocal
ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

// 线程1
new Thread(() -> {
    threadLocal.set(100);  // 设置值
    System.out.println(threadLocal.get());  // 输出：100
}).start();

// 线程2
new Thread(() -> {
    threadLocal.set(200);  // 设置值
    System.out.println(threadLocal.get());  // 输出：200
}).start();

// 两个线程互不影响
```

## 二、ThreadLocal的核心API

### 常用方法

```java
// 1. 设置值
threadLocal.set(value);

// 2. 获取值
T value = threadLocal.get();

// 3. 移除值（防止内存泄漏，务必调用）
threadLocal.remove();

// 4. 初始值（可选）
ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
// 或
ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>() {
    @Override
    protected Integer initialValue() {
        return 0;
    }
};
```

## 三、ThreadLocal的实现原理

### 核心结构

```java
// Thread类中
public class Thread {
    // 每个线程都有自己的ThreadLocalMap
    ThreadLocal.ThreadLocalMap threadLocals = null;
}

// ThreadLocal.ThreadLocalMap（内部类）
static class ThreadLocalMap {
    // Entry数组
    private Entry[] table;
    
    // Entry：key是ThreadLocal，value是实际值
    static class Entry extends WeakReference<ThreadLocal<?>> {
        Object value;
        Entry(ThreadLocal<?> k, Object v) {
            super(k);
            value = v;
        }
    }
}
```

### 工作流程

```java
ThreadLocal<String> threadLocal = new ThreadLocal<>();
threadLocal.set("Hello");

// 实际执行：
1. 获取当前线程 Thread.currentThread()
2. 获取线程的ThreadLocalMap
3. 以threadLocal为key，"Hello"为value存入map
4. map存储在线程对象中

threadLocal.get();
// 实际执行：
1. 获取当前线程
2. 获取线程的ThreadLocalMap
3. 以threadLocal为key从map中取值
```

**数据结构示意**：
```
Thread对象
  ├─ threadLocals (ThreadLocalMap)
       ├─ Entry[0]: ThreadLocal1 → value1
       ├─ Entry[1]: ThreadLocal2 → value2
       ├─ Entry[2]: null
       └─ Entry[3]: ThreadLocal3 → value3
```

### 关键点

1. **数据存储在Thread对象中**，不是ThreadLocal对象中
2. **ThreadLocal只是key**，用于从map中取值
3. **每个线程独立**，互不影响

## 四、ThreadLocal的应用场景

### 场景1：数据库连接管理

```java
public class ConnectionManager {
    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    
    public static Connection getConnection() {
        Connection conn = connectionHolder.get();
        if (conn == null) {
            conn = DriverManager.getConnection("jdbc:mysql://...");
            connectionHolder.set(conn);
        }
        return conn;
    }
    
    public static void closeConnection() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connectionHolder.remove();  // 必须remove
            }
        }
    }
}

// 使用
public void processRequest() {
    try {
        Connection conn = ConnectionManager.getConnection();
        // 使用连接
        // 同一个线程内，多次调用getConnection()返回同一个连接
    } finally {
        ConnectionManager.closeConnection();
    }
}
```

### 场景2：用户上下文传递

```java
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    
    public static void setUser(User user) {
        userHolder.set(user);
    }
    
    public static User getUser() {
        return userHolder.get();
    }
    
    public static void clear() {
        userHolder.remove();
    }
}

// 使用
public class UserService {
    public void handleRequest(HttpServletRequest request) {
        // 1. 从请求中获取用户
        User user = getUserFromRequest(request);
        
        try {
            // 2. 设置到ThreadLocal
            UserContext.setUser(user);
            
            // 3. 后续代码都能获取用户
            processOrder();  // 内部调用UserContext.getUser()
            sendNotification();  // 内部调用UserContext.getUser()
            
        } finally {
            // 4. 清理（必须）
            UserContext.clear();
        }
    }
    
    private void processOrder() {
        User user = UserContext.getUser();  // 获取当前用户
        System.out.println("处理订单：" + user.getName());
    }
}
```

### 场景3：SimpleDateFormat线程安全

```java
// ❌ SimpleDateFormat不是线程安全的
private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

public String formatDate(Date date) {
    return DATE_FORMAT.format(date);  // 多线程下可能出错
}

// ✅ 使用ThreadLocal
private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = 
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

public String formatDate(Date date) {
    return DATE_FORMAT.get().format(date);  // 线程安全
}
```

### 场景4：事务管理

```java
public class TransactionManager {
    private static ThreadLocal<Boolean> transactionHolder = new ThreadLocal<>();
    
    public static void beginTransaction() {
        transactionHolder.set(true);
        System.out.println("开启事务");
    }
    
    public static void commit() {
        if (Boolean.TRUE.equals(transactionHolder.get())) {
            System.out.println("提交事务");
            transactionHolder.remove();
        }
    }
    
    public static void rollback() {
        if (Boolean.TRUE.equals(transactionHolder.get())) {
            System.out.println("回滚事务");
            transactionHolder.remove();
        }
    }
    
    public static boolean isInTransaction() {
        return Boolean.TRUE.equals(transactionHolder.get());
    }
}
```

### 场景5：请求追踪（Trace ID）

```java
public class TraceContext {
    private static ThreadLocal<String> traceIdHolder = new ThreadLocal<>();
    
    public static void setTraceId(String traceId) {
        traceIdHolder.set(traceId);
    }
    
    public static String getTraceId() {
        return traceIdHolder.get();
    }
    
    public static void clear() {
        traceIdHolder.remove();
    }
}

// 使用
public void handleRequest(HttpServletRequest request) {
    String traceId = UUID.randomUUID().toString();
    
    try {
        TraceContext.setTraceId(traceId);
        
        // 所有日志都会包含traceId
        log.info("[{}] 处理请求", TraceContext.getTraceId());
        service.process();
        log.info("[{}] 请求完成", TraceContext.getTraceId());
        
    } finally {
        TraceContext.clear();
    }
}
```

## 五、ThreadLocal的内存泄漏问题

### 问题原因

```java
// ThreadLocalMap.Entry
static class Entry extends WeakReference<ThreadLocal<?>> {
    Object value;  // 强引用
}
```

**引用链**：
```
Thread (强引用)
  → ThreadLocalMap (强引用)
    → Entry (强引用)
      → key: ThreadLocal (弱引用)
      → value: 实际对象 (强引用)
```

**问题场景**：
```
1. ThreadLocal对象被回收（弱引用）
2. Entry的key变成null
3. 但value是强引用，无法回收
4. 如果线程长期存活（如线程池），value永远不会被回收
→ 内存泄漏
```

### 示例

```java
public class MemoryLeakExample {
    static class BigObject {
        private byte[] data = new byte[1024 * 1024];  // 1MB
    }
    
    public static void main(String[] args) {
        ThreadLocal<BigObject> threadLocal = new ThreadLocal<>();
        
        // 线程池
        ExecutorService executor = Executors.newFixedThreadPool(1);
        
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                threadLocal.set(new BigObject());  // 1MB
                // ❌ 忘记remove
                // threadLocal.remove();
            });
        }
        
        // 线程池中的线程不会销毁
        // 每次执行都会创建新的BigObject
        // 旧的BigObject无法回收
        // 最终OOM
    }
}
```

### 解决方案

#### 方案1：手动remove（推荐）

```java
ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

try {
    connectionHolder.set(getConnection());
    // 使用连接
} finally {
    connectionHolder.remove();  // 必须remove
}
```

#### 方案2：try-with-resources封装

```java
public class ThreadLocalResource<T> implements AutoCloseable {
    private ThreadLocal<T> threadLocal;
    
    public ThreadLocalResource(T value) {
        this.threadLocal = new ThreadLocal<>();
        threadLocal.set(value);
    }
    
    public T get() {
        return threadLocal.get();
    }
    
    @Override
    public void close() {
        threadLocal.remove();
    }
}

// 使用
try (ThreadLocalResource<Connection> resource = new ThreadLocalResource<>(conn)) {
    Connection conn = resource.get();
    // 使用连接
}  // 自动remove
```

#### 方案3：使用InheritableThreadLocal（父子线程传递）

```java
// 普通ThreadLocal：子线程无法获取父线程的值
ThreadLocal<String> threadLocal = new ThreadLocal<>();

// InheritableThreadLocal：子线程可以获取父线程的值
InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

// 父线程
inheritableThreadLocal.set("parent");

// 子线程
new Thread(() -> {
    String value = inheritableThreadLocal.get();  // "parent"
}).start();
```

## 六、ThreadLocal的最佳实践

### 实践1：务必remove

```java
// ✅ 正确
ThreadLocal<User> userHolder = new ThreadLocal<>();

try {
    userHolder.set(user);
    // 使用
} finally {
    userHolder.remove();  // 必须
}
```

### 实践2：使用static final

```java
// ✅ 推荐
private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = 
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

// ❌ 不推荐
private ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<>();
```

**原因**：
- static：全局共享ThreadLocal对象（不是共享值）
- final：防止被修改
- 减少ThreadLocal对象数量

### 实践3：提供初始值

```java
// 使用withInitial
ThreadLocal<List<String>> listHolder = ThreadLocal.withInitial(ArrayList::new);

// 或重写initialValue
ThreadLocal<Integer> counterHolder = new ThreadLocal<Integer>() {
    @Override
    protected Integer initialValue() {
        return 0;
    }
};
```

### 实践4：线程池场景特别注意

```java
public class ThreadPoolExample {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    
    public void processRequest(User user) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        executor.submit(() -> {
            try {
                userHolder.set(user);
                // 处理请求
            } finally {
                userHolder.remove();  // 线程池场景必须remove
                // 因为线程会被复用
            }
        });
    }
}
```

### 实践5：避免大对象

```java
// ❌ 不推荐：大对象
ThreadLocal<byte[]> bigDataHolder = new ThreadLocal<>();
bigDataHolder.set(new byte[10 * 1024 * 1024]);  // 10MB

// ✅ 推荐：小对象或引用
ThreadLocal<Integer> idHolder = new ThreadLocal<>();
ThreadLocal<String> nameHolder = new ThreadLocal<>();
```

## 七、ThreadLocal vs 其他方案

| 方案 | 优点 | 缺点 | 适用场景 |
|-----|------|------|---------|
| **ThreadLocal** | 线程隔离，无锁 | 内存泄漏风险 | 线程上下文传递 |
| **synchronized** | 简单，数据共享 | 性能差，阻塞 | 需要同步访问 |
| **方法参数传递** | 显式，清晰 | 侵入性强 | 调用链短 |
| **全局变量+锁** | 数据共享 | 性能差 | 简单共享 |

## 八、小结

**核心要点**：

1. **ThreadLocal定义**：
   - 为每个线程提供独立的变量副本
   - 数据存储在Thread对象中
   - ThreadLocal只是key

2. **实现原理**：
   - Thread持有ThreadLocalMap
   - ThreadLocalMap存储Entry数组
   - Entry的key是ThreadLocal（弱引用），value是实际值（强引用）

3. **应用场景**：
   - 数据库连接管理
   - 用户上下文传递
   - SimpleDateFormat线程安全
   - 事务管理
   - 请求追踪

4. **内存泄漏**：
   - ThreadLocal被回收，但value无法回收
   - 线程池场景尤其注意
   - 解决：务必调用remove()

5. **最佳实践**：
   - 使用static final修饰
   - 务必在finally中remove
   - 提供初始值
   - 避免大对象
   - 线程池场景特别注意

**记忆口诀**：
- ThreadLocal线程隔离，每个线程独立副本
- 数据存Thread对象，ThreadLocal只是key
- 线程池要小心，务必记得remove
- 内存泄漏要预防，finally清理最安全

---

💡 **提示**：ThreadLocal是线程隔离的利器，但要注意内存泄漏问题。记住：用完必须remove！
