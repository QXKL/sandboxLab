# ThreadLocal - 测试题

## 一、选择题

### 1. ThreadLocal的数据存储在哪里？
A. ThreadLocal对象中  
B. 堆内存中  
C. Thread对象中  
D. 方法区中

**答案**：C

**解析**：ThreadLocal的数据存储在Thread对象的threadLocals字段（ThreadLocalMap）中，不是ThreadLocal对象中。

---

### 2. ThreadLocal.get()获取不到值时会返回什么？
A. 抛异常  
B. 返回null  
C. 返回初始值  
D. 阻塞等待

**答案**：C

**解析**：如果设置了初始值（initialValue或withInitial），返回初始值；否则返回null。

---

### 3. ThreadLocal为什么会导致内存泄漏？
A. ThreadLocal对象无法回收  
B. Entry的value是强引用，线程长期存活导致无法回收  
C. Thread对象无法回收  
D. ThreadLocalMap无法回收

**答案**：B

**解析**：Entry的key是弱引用（ThreadLocal被回收后key=null），但value是强引用，线程长期存活（如线程池）时value无法回收。

---

### 4. 如何避免ThreadLocal内存泄漏？
A. 不使用ThreadLocal  
B. 使用完后调用remove()  
C. 设置为null  
D. 使用WeakReference

**答案**：B

**解析**：使用完ThreadLocal后，必须调用remove()清理数据，尤其是在线程池场景。

---

### 5. 以下哪个场景适合使用ThreadLocal？
A. 多线程共享计数器  
B. 用户上下文传递  
C. 线程间通信  
D. 数据库主键生成

**答案**：B

**解析**：ThreadLocal适合线程上下文传递（如用户信息、事务状态），不适合线程间通信。

---

### 6. InheritableThreadLocal的作用是什么？
A. 提高性能  
B. 子线程可以继承父线程的值  
C. 防止内存泄漏  
D. 线程间通信

**答案**：B

**解析**：InheritableThreadLocal允许子线程继承父线程的ThreadLocal值，普通ThreadLocal不行。

---

### 7. 以下哪个说法是错误的？
A. ThreadLocal为每个线程提供独立的变量副本  
B. ThreadLocal是线程安全的  
C. ThreadLocal保证线程间数据共享  
D. ThreadLocal的数据存储在Thread对象中

**答案**：C

**解析**：ThreadLocal是线程隔离的，不是数据共享。每个线程有自己的副本，互不影响。

---

### 8. ThreadLocal的initialValue()方法什么时候调用？
A. 创建ThreadLocal时  
B. 第一次调用get()且未set时  
C. 每次调用get()时  
D. 调用set()时

**答案**：B

**解析**：initialValue()在第一次调用get()且未调用set()时被调用，用于提供初始值。

---

### 9. ThreadLocalMap.Entry的key是什么类型的引用？
A. 强引用  
B. 软引用  
C. 弱引用  
D. 虚引用

**答案**：C

**解析**：Entry的key是WeakReference<ThreadLocal<?>>，弱引用，当ThreadLocal对象被回收时key会变成null。

---

### 10. 线程池使用ThreadLocal时最需要注意什么？
A. 性能问题  
B. 线程复用导致数据污染  
C. 线程安全问题  
D. 编译错误

**答案**：B

**解析**：线程池中线程会被复用，如果不调用remove()，下次使用该线程时会读到上次的旧数据。

---

## 二、填空题

### 1. ThreadLocal的数据存储在Thread对象的________字段中，该字段的类型是________。

**答案**：threadLocals、ThreadLocalMap

---

### 2. ThreadLocal的三个核心方法是：________、________、________。

**答案**：set()、get()、remove()

---

### 3. ThreadLocalMap.Entry的key是________引用，value是________引用。

**答案**：弱、强

---

### 4. 使用ThreadLocal时，为了防止内存泄漏，应该在________块中调用________方法。

**答案**：finally、remove()

---

### 5. 如果需要子线程继承父线程的ThreadLocal值，应该使用________。

**答案**：InheritableThreadLocal

---

## 三、判断题

### 1. ThreadLocal可以实现线程间的数据共享。（ ）

**答案**：✗

**解析**：ThreadLocal是线程隔离的，不是数据共享。每个线程有独立的副本。

---

### 2. ThreadLocal对象通常应该定义为static final。（ ）

**答案**：✓

**解析**：static全局共享ThreadLocal对象（不是共享值），final防止被修改，减少对象数量。

---

### 3. 调用ThreadLocal.get()后，如果没有设置过值，一定返回null。（ ）

**答案**：✗

**解析**：如果重写了initialValue()或使用withInitial()设置了初始值，会返回初始值。

---

### 4. ThreadLocal导致内存泄漏的根本原因是Entry的value是强引用。（ ）

**答案**：✓

**解析**：ThreadLocal被回收后Entry的key变成null，但value是强引用无法回收，导致内存泄漏。

---

### 5. 在线程池中使用ThreadLocal不需要调用remove()。（ ）

**答案**：✗

**解析**：线程池场景更需要调用remove()，因为线程会被复用，不清理会导致数据污染。

---

## 四、简答题

### 1. 说明ThreadLocal的实现原理，包括数据是如何存储的。

**答案**：

**核心结构**：

```java
// Thread类
public class Thread {
    // 每个线程都有自己的ThreadLocalMap
    ThreadLocal.ThreadLocalMap threadLocals = null;
}

// ThreadLocalMap（ThreadLocal的内部类）
static class ThreadLocalMap {
    // Entry数组
    private Entry[] table;
    
    // Entry：继承WeakReference
    static class Entry extends WeakReference<ThreadLocal<?>> {
        Object value;  // 实际存储的值
        
        Entry(ThreadLocal<?> k, Object v) {
            super(k);  // key是弱引用
            value = v;  // value是强引用
        }
    }
}
```

---

**数据存储流程**：

**1. set()方法**：
```java
ThreadLocal<String> threadLocal = new ThreadLocal<>();
threadLocal.set("Hello");

// 实际执行：
public void set(T value) {
    Thread t = Thread.currentThread();  // 1. 获取当前线程
    ThreadLocalMap map = t.threadLocals;  // 2. 获取线程的map
    if (map != null) {
        map.set(this, value);  // 3. 以this为key，value为值存入map
    } else {
        createMap(t, value);  // 4. 首次使用，创建map
    }
}
```

**2. get()方法**：
```java
String value = threadLocal.get();

// 实际执行：
public T get() {
    Thread t = Thread.currentThread();  // 1. 获取当前线程
    ThreadLocalMap map = t.threadLocals;  // 2. 获取线程的map
    if (map != null) {
        Entry e = map.getEntry(this);  // 3. 以this为key从map获取
        if (e != null) {
            return (T) e.value;  // 4. 返回value
        }
    }
    return setInitialValue();  // 5. 未设置过，返回初始值
}
```

---

**数据结构示意**：

```
Thread-1对象
  └─ threadLocals (ThreadLocalMap)
       └─ Entry[]
            ├─ Entry[0]: ThreadLocal1(弱引用) → "value1"(强引用)
            ├─ Entry[1]: ThreadLocal2(弱引用) → User对象(强引用)
            └─ Entry[2]: null

Thread-2对象
  └─ threadLocals (ThreadLocalMap)
       └─ Entry[]
            ├─ Entry[0]: ThreadLocal1(弱引用) → "value2"(强引用)
            └─ Entry[1]: ThreadLocal2(弱引用) → User对象(强引用)
```

---

**关键点**：

1. **数据存储位置**：
   - 数据存储在Thread对象的threadLocals字段
   - 不是存储在ThreadLocal对象中

2. **ThreadLocal的角色**：
   - ThreadLocal只是key
   - 用于从ThreadLocalMap中存取数据

3. **线程隔离**：
   - 每个Thread对象有自己的ThreadLocalMap
   - 线程间互不影响

4. **引用类型**：
   - Entry的key：WeakReference<ThreadLocal<?>>（弱引用）
   - Entry的value：Object（强引用）

---

**为什么这样设计**：

1. **线程隔离**：数据存在Thread中，天然隔离
2. **性能**：无需加锁，每个线程访问自己的数据
3. **生命周期**：随线程销毁而销毁

---

### 2. ThreadLocal为什么会导致内存泄漏？如何避免？

**答案**：

**内存泄漏原因**：

**1. 引用链分析**：

```
Thread对象 (强引用，线程存活)
  ↓
ThreadLocalMap (强引用)
  ↓
Entry[] (强引用)
  ↓
Entry (强引用)
  ├─ key: ThreadLocal (弱引用) ← 可能被GC回收
  └─ value: 实际对象 (强引用) ← 无法回收
```

**2. 问题场景**：

```java
public class MemoryLeakExample {
    static class BigObject {
        private byte[] data = new byte[1024 * 1024];  // 1MB
    }
    
    public static void main(String[] args) {
        ThreadLocal<BigObject> threadLocal = new ThreadLocal<>();
        
        // 线程池
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 1000; i++) {
            executor.execute(() -> {
                threadLocal.set(new BigObject());  // 设置1MB对象
                // 处理业务...
                // ❌ 忘记remove()
            });
        }
    }
}
```

**3. 泄漏过程**：

```
第1次执行：
1. 线程1从线程池取出
2. threadLocal.set(new BigObject())  // 1MB
3. ThreadLocal对象在栈上，方法结束后可能被GC
4. Entry的key变成null（弱引用被回收）
5. 但Entry的value（BigObject）是强引用，无法回收
6. 线程1返回线程池，继续存活

第2次执行：
1. 线程1再次被使用
2. threadLocal.set(new BigObject())  // 又1MB
3. 创建新的Entry
4. 旧的Entry仍然存在（value无法回收）

第N次执行：
1. 累积了N个Entry，每个1MB
2. 内存持续增长
3. 最终OOM
```

---

**为什么会泄漏**：

1. **弱引用的key**：
   - ThreadLocal对象被回收
   - Entry的key变成null

2. **强引用的value**：
   - value是强引用，无法被GC
   - Thread对象存活（线程池），value一直无法回收

3. **ThreadLocalMap的清理不及时**：
   - ThreadLocalMap会在set/get时清理key=null的Entry
   - 但如果不再调用set/get，就无法清理

---

**避免内存泄漏的方法**：

**方法1：手动remove()（推荐）**

```java
ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

try {
    // 设置值
    connectionHolder.set(getConnection());
    
    // 使用值
    Connection conn = connectionHolder.get();
    // 业务逻辑...
    
} finally {
    // 必须清理
    connectionHolder.remove();
}
```

**方法2：try-with-resources封装**

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
        threadLocal.remove();  // 自动清理
    }
}

// 使用
try (ThreadLocalResource<Connection> resource = 
        new ThreadLocalResource<>(getConnection())) {
    Connection conn = resource.get();
    // 使用连接...
}  // 自动调用remove()
```

**方法3：使用弱引用包装value（不推荐，复杂）**

```java
ThreadLocal<WeakReference<BigObject>> threadLocal = new ThreadLocal<>();
threadLocal.set(new WeakReference<>(new BigObject()));

// 使用时
WeakReference<BigObject> ref = threadLocal.get();
BigObject obj = ref.get();  // 可能为null
```

---

**线程池场景特别注意**：

```java
public class ThreadPoolExample {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    
    public void handleRequest(User user) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        executor.submit(() -> {
            try {
                userHolder.set(user);
                
                // 处理请求...
                
            } finally {
                // 线程池场景必须remove
                // 因为线程会被复用
                userHolder.remove();
            }
        });
    }
}
```

---

**总结**：

**泄漏原因**：
- Entry的key是弱引用，value是强引用
- 线程长期存活（线程池），value无法回收

**解决方案**：
- 务必在finally中调用remove()
- 线程池场景尤其重要
- 可以封装成AutoCloseable

---

### 3. ThreadLocal有哪些典型的应用场景？请举例说明。

**答案**：

**场景1：数据库连接管理**

**问题**：每个线程需要独立的数据库连接，避免线程间干扰。

**解决方案**：
```java
public class ConnectionManager {
    private static ThreadLocal<Connection> connectionHolder = 
        new ThreadLocal<>();
    
    public static Connection getConnection() {
        Connection conn = connectionHolder.get();
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db", 
                    "user", 
                    "password"
                );
                connectionHolder.set(conn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
                connectionHolder.remove();  // 必须清理
            }
        }
    }
}

// 使用
public void processRequest() {
    try {
        Connection conn = ConnectionManager.getConnection();
        // 同一个线程内，多次调用返回同一个连接
        PreparedStatement ps = conn.prepareStatement("...");
        // 执行SQL...
    } finally {
        ConnectionManager.closeConnection();
    }
}
```

---

**场景2：用户上下文传递**

**问题**：在整个请求处理过程中，多个方法需要访问当前用户信息，如果通过参数传递会很繁琐。

**解决方案**：
```java
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    
    public static void setUser(User user) {
        userHolder.set(user);
    }
    
    public static User getUser() {
        return userHolder.get();
    }
    
    public static Long getUserId() {
        User user = userHolder.get();
        return user != null ? user.getId() : null;
    }
    
    public static void clear() {
        userHolder.remove();
    }
}

// Controller层
@RestController
public class OrderController {
    @PostMapping("/orders")
    public Result createOrder(@RequestBody OrderDTO dto) {
        // 从token获取用户
        User user = getUserFromToken();
        
        try {
            // 设置到ThreadLocal
            UserContext.setUser(user);
            
            // 调用Service（不需要传递user参数）
            return orderService.createOrder(dto);
            
        } finally {
            // 清理
            UserContext.clear();
        }
    }
}

// Service层
@Service
public class OrderService {
    public Result createOrder(OrderDTO dto) {
        // 直接从ThreadLocal获取用户
        User user = UserContext.getUser();
        
        // 创建订单
        Order order = new Order();
        order.setUserId(user.getId());
        // ...
        
        // 调用其他Service
        notificationService.sendNotification();  // 内部也能获取user
        
        return Result.success();
    }
}

// 通知Service
@Service
public class NotificationService {
    public void sendNotification() {
        // 直接获取用户，不需要参数传递
        User user = UserContext.getUser();
        sendEmail(user.getEmail(), "订单创建成功");
    }
}
```

---

**场景3：SimpleDateFormat线程安全**

**问题**：SimpleDateFormat不是线程安全的，多线程共享会出错。

**错误示例**：
```java
// ❌ 线程不安全
private static final SimpleDateFormat DATE_FORMAT = 
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public String formatDate(Date date) {
    return DATE_FORMAT.format(date);  // 多线程下可能出错
}
```

**解决方案**：
```java
// ✅ 使用ThreadLocal
private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = 
    ThreadLocal.withInitial(() -> 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

public String formatDate(Date date) {
    return DATE_FORMAT.get().format(date);  // 线程安全
}

public Date parseDate(String dateStr) throws ParseException {
    return DATE_FORMAT.get().parse(dateStr);
}
```

---

**场景4：分布式追踪（Trace ID）**

**问题**：微服务架构中，需要追踪一个请求的完整调用链。

**解决方案**：
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

// 拦截器
@Component
public class TraceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        // 生成或获取traceId
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        
        // 设置到ThreadLocal
        TraceContext.setTraceId(traceId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Object handler, 
                               Exception ex) {
        // 清理
        TraceContext.clear();
    }
}

// 业务代码
@Service
public class OrderService {
    public void createOrder(Order order) {
        // 日志自动包含traceId
        log.info("[{}] 创建订单：{}", TraceContext.getTraceId(), order);
        
        // 调用其他服务
        userService.updateUserInfo();  // 日志也会包含同一个traceId
        
        // 远程调用时传递traceId
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Trace-Id", TraceContext.getTraceId());
        restTemplate.exchange(url, HttpMethod.POST, 
            new HttpEntity<>(body, headers), String.class);
    }
}
```

---

**场景5：事务管理**

**问题**：Spring事务管理需要在整个方法调用链中共享事务状态。

**Spring实现**（简化版）：
```java
public class TransactionSynchronizationManager {
    private static final ThreadLocal<Map<Object, Object>> resources = 
        new ThreadLocal<>();
    
    public static void bindResource(Object key, Object value) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            map = new HashMap<>();
            resources.set(map);
        }
        map.put(key, value);
    }
    
    public static Object getResource(Object key) {
        Map<Object, Object> map = resources.get();
        return map != null ? map.get(key) : null;
    }
    
    public static void unbindResource(Object key) {
        Map<Object, Object> map = resources.get();
        if (map != null) {
            map.remove(key);
            if (map.isEmpty()) {
                resources.remove();
            }
        }
    }
}
```

---

**总结**：

ThreadLocal适合的场景特点：
1. **线程独立**：每个线程需要独立的数据
2. **跨方法传递**：多个方法需要访问同一数据
3. **避免参数传递**：不想污染方法签名
4. **线程安全**：避免使用synchronized

---

## 五、编程题

### 使用ThreadLocal实现一个线程安全的用户上下文管理器，支持设置、获取、清理用户信息。

**答案**：

```java
import java.util.Objects;

/**
 * 用户实体
 */
class User {
    private Long id;
    private String username;
    private String email;
    
    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    // getters and setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}

/**
 * 用户上下文管理器
 */
public class UserContext {
    // 使用ThreadLocal存储用户信息
    private static final ThreadLocal<User> userHolder = new ThreadLocal<>();
    
    /**
     * 设置当前用户
     */
    public static void setUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        userHolder.set(user);
    }
    
    /**
     * 获取当前用户
     */
    public static User getUser() {
        return userHolder.get();
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        User user = userHolder.get();
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        User user = userHolder.get();
        return user != null ? user.getUsername() : null;
    }
    
    /**
     * 清理当前用户（必须调用）
     */
    public static void clear() {
        userHolder.remove();
    }
    
    /**
     * 判断是否已设置用户
     */
    public static boolean hasUser() {
        return userHolder.get() != null;
    }
    
    // 测试
    public static void main(String[] args) throws InterruptedException {
        // 线程1
        Thread t1 = new Thread(() -> {
            try {
                User user1 = new User(1L, "张三", "zhangsan@example.com");
                UserContext.setUser(user1);
                
                System.out.println("线程1 - 用户：" + UserContext.getUser());
                System.out.println("线程1 - 用户ID：" + UserContext.getUserId());
                
                Thread.sleep(1000);
                
                System.out.println("线程1 - 再次获取：" + UserContext.getUser());
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                UserContext.clear();
                System.out.println("线程1 - 已清理");
            }
        });
        
        // 线程2
        Thread t2 = new Thread(() -> {
            try {
                User user2 = new User(2L, "李四", "lisi@example.com");
                UserContext.setUser(user2);
                
                System.out.println("线程2 - 用户：" + UserContext.getUser());
                System.out.println("线程2 - 用户ID：" + UserContext.getUserId());
                
                Thread.sleep(500);
                
                System.out.println("线程2 - 再次获取：" + UserContext.getUser());
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                UserContext.clear();
                System.out.println("线程2 - 已清理");
            }
        });
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        System.out.println("主线程 - 用户：" + UserContext.getUser());  // null
    }
}
```

**输出示例**：
```
线程1 - 用户：User{id=1, username='张三'}
线程1 - 用户ID：1
线程2 - 用户：User{id=2, username='李四'}
线程2 - 用户ID：2
线程2 - 再次获取：User{id=2, username='李四'}
线程2 - 已清理
线程1 - 再次获取：User{id=1, username='张三'}
线程1 - 已清理
主线程 - 用户：null
```

**说明**：
1. 每个线程都有独立的User副本
2. 线程间互不影响
3. 使用完必须调用clear()清理
4. 主线程未设置用户，获取到null

---

💡 **提示**：ThreadLocal是线程隔离的利器，但务必记得remove()，尤其在线程池场景！
