# CompletableFuture与异步编程 - 测试题

## 一、选择题

### 1. CompletableFuture是哪个版本引入的？
A. JDK 6  
B. JDK 7  
C. JDK 8  
D. JDK 9

**答案**：C

**解析**：CompletableFuture是JDK 8引入的异步编程工具。

---

### 2. 以下哪个方法创建的CompletableFuture没有返回值？
A. supplyAsync  
B. runAsync  
C. completedFuture  
D. allOf

**答案**：B

**解析**：runAsync执行Runnable，没有返回值；supplyAsync执行Supplier，有返回值。

---

### 3. thenApply和thenCompose的区别是什么？
A. 完全相同  
B. thenApply用于转换结果，thenCompose用于串行组合  
C. thenApply有返回值，thenCompose没有  
D. thenApply是同步的，thenCompose是异步的

**答案**：B

**解析**：thenApply将结果转换为新值，thenCompose用于串行组合多个CompletableFuture。

---

### 4. CompletableFuture.allOf的作用是什么？
A. 任意一个完成就返回  
B. 等待所有完成  
C. 合并结果  
D. 串行执行

**答案**：B

**解析**：allOf等待所有CompletableFuture完成，anyOf是任意一个完成就返回。

---

### 5. join()和get()的区别是什么？
A. join()抛RuntimeException，get()抛检查异常  
B. join()不阻塞，get()阻塞  
C. join()异步，get()同步  
D. 完全相同

**答案**：A

**解析**：join()抛RuntimeException，不需要try-catch；get()抛检查异常，需要try-catch。

---

### 6. 如何为CompletableFuture指定线程池？
A. 在创建时作为参数传入  
B. 调用setExecutor()方法  
C. 无法指定  
D. 使用withExecutor()方法

**答案**：A

**解析**：在创建时通过参数指定线程池，如supplyAsync(supplier, executor)。

---

### 7. exceptionally方法的作用是什么？
A. 抛出异常  
B. 捕获异常并返回默认值  
C. 忽略异常  
D. 记录异常

**答案**：B

**解析**：exceptionally捕获异常并返回默认值，类似try-catch。

---

### 8. 以下哪个方法不接收前一个阶段的结果？
A. thenApply  
B. thenAccept  
C. thenRun  
D. thenCompose

**答案**：C

**解析**：thenRun不接收前一个阶段的结果，只是在完成后执行。

---

### 9. CompletableFuture默认使用什么线程池？
A. FixedThreadPool  
B. CachedThreadPool  
C. ForkJoinPool.commonPool()  
D. SingleThreadExecutor

**答案**：C

**解析**：CompletableFuture默认使用ForkJoinPool.commonPool()。

---

### 10. 如何获取CompletableFuture的结果但不阻塞？
A. join()  
B. get()  
C. getNow()  
D. 无法做到

**答案**：C

**解析**：getNow(defaultValue)立即返回结果，如果未完成返回默认值。

---

## 二、填空题

### 1. CompletableFuture的两个创建方法是：________（无返回值）和________（有返回值）。

**答案**：runAsync、supplyAsync

---

### 2. thenApply用于________结果，thenAccept用于________结果，thenRun________前一个阶段的结果。

**答案**：转换、消费、不关心（或：不接收）

---

### 3. CompletableFuture.allOf等待________完成，anyOf等待________完成。

**答案**：所有（全部）、任意一个

---

### 4. join()方法抛出________异常，get()方法抛出________异常。

**答案**：RuntimeException（或：非检查异常）、检查异常（或：InterruptedException和ExecutionException）

---

### 5. 使用________方法可以捕获CompletableFuture的异常并返回默认值。

**答案**：exceptionally

---

## 三、判断题

### 1. CompletableFuture支持链式调用。（ ）

**答案**：✓

**解析**：CompletableFuture的方法都返回CompletableFuture，支持链式调用。

---

### 2. thenApply和thenApplyAsync完全相同。（ ）

**答案**：✗

**解析**：thenApply在当前线程执行，thenApplyAsync在线程池中异步执行。

---

### 3. CompletableFuture.allOf会返回所有结果的列表。（ ）

**答案**：✗

**解析**：allOf返回CompletableFuture<Void>，需要手动从各个Future中获取结果。

---

### 4. 应该总是为CompletableFuture指定线程池。（ ）

**答案**：✓

**解析**：默认的ForkJoinPool.commonPool()是全局共享的，指定线程池可以隔离任务。

---

### 5. join()方法需要try-catch捕获异常。（ ）

**答案**：✗

**解析**：join()抛RuntimeException，不需要强制try-catch。

---

## 四、简答题

### 1. 说明CompletableFuture相比Future的优势。

**答案**：

**Future的局限性**：

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
Future<String> future = executor.submit(() -> {
    return "Hello";
});

// Future的问题：
try {
    String result = future.get();  // 1. 只能阻塞获取结果
    // 2. 不能链式调用
    // 3. 不能组合多个Future
    // 4. 没有异常处理机制
    // 5. 不能设置回调
} catch (Exception e) {
    e.printStackTrace();
}
```

---

**CompletableFuture的优势**：

**1. 支持链式调用**
```java
CompletableFuture.supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")
    .thenApply(s -> s + "!")
    .thenAccept(System.out::println);
```

**2. 支持组合多个Future**
```java
// 串行组合（有依赖）
CompletableFuture.supplyAsync(() -> getUserId())
    .thenCompose(userId -> CompletableFuture.supplyAsync(() -> getOrders(userId)));

// 并行组合（合并结果）
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "World");
f1.thenCombine(f2, (s1, s2) -> s1 + " " + s2);

// 等待所有完成
CompletableFuture.allOf(f1, f2, f3).join();

// 任意一个完成
CompletableFuture.anyOf(f1, f2, f3).join();
```

**3. 支持异常处理**
```java
CompletableFuture.supplyAsync(() -> {
    if (error) throw new RuntimeException("Error");
    return "Success";
}).exceptionally(ex -> {
    log.error("异常", ex);
    return "Default";
}).handle((result, ex) -> {
    if (ex != null) return "Error";
    return result;
});
```

**4. 支持回调**
```java
CompletableFuture.supplyAsync(() -> "Hello")
    .thenAccept(result -> {
        System.out.println("结果：" + result);
    })
    .whenComplete((result, ex) -> {
        if (ex != null) {
            System.out.println("异常：" + ex);
        }
    });
```

**5. 非阻塞获取结果**
```java
String result = future.getNow("Default");  // 立即返回
```

**6. 支持超时**
```java
String result = future.get(3, TimeUnit.SECONDS);
```

---

**对比总结**：

| 特性 | Future | CompletableFuture |
|-----|--------|------------------|
| **链式调用** | ✗ | ✓ |
| **组合多个任务** | ✗ | ✓（thenCompose、thenCombine） |
| **异常处理** | try-catch | exceptionally、handle |
| **回调** | ✗ | ✓（thenAccept、whenComplete） |
| **非阻塞获取** | ✗ | ✓（getNow） |
| **手动完成** | ✗ | ✓（complete） |

---

### 2. 解释thenApply、thenCompose和thenCombine的区别和使用场景。

**答案**：

**1. thenApply - 转换结果**

**定义**：将前一个阶段的结果转换为新值。

**特点**：
- 接收前一个阶段的结果
- 返回新值
- 单个Future的转换

**示例**：
```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    return "123";
}).thenApply(s -> {
    return Integer.parseInt(s);  // String → Integer
}).thenApply(i -> {
    return i * 2;  // Integer → Integer
});

Integer result = future.join();  // 246
```

**使用场景**：
- 类型转换（String → Integer）
- 数据处理（加工、过滤）
- 结果映射

---

**2. thenCompose - 串行组合**

**定义**：将前一个阶段的结果传递给下一个阶段，下一个阶段返回新的CompletableFuture。

**特点**：
- 接收前一个阶段的结果
- 返回CompletableFuture（避免嵌套）
- 串行执行，有依赖关系

**示例**：
```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "User123";  // 1. 获取用户ID
}).thenCompose(userId -> {
    // 2. 根据用户ID查询订单（返回CompletableFuture）
    return CompletableFuture.supplyAsync(() -> {
        return "Orders for " + userId;
    });
});

String result = future.join();  // "Orders for User123"
```

**对比thenApply的错误用法**：
```java
// ❌ 错误：返回CompletableFuture<CompletableFuture<String>>
CompletableFuture<CompletableFuture<String>> nested = 
    CompletableFuture.supplyAsync(() -> "User123")
        .thenApply(userId -> {
            return CompletableFuture.supplyAsync(() -> "Orders for " + userId);
        });

// ✅ 正确：使用thenCompose，返回CompletableFuture<String>
CompletableFuture<String> flat = 
    CompletableFuture.supplyAsync(() -> "User123")
        .thenCompose(userId -> {
            return CompletableFuture.supplyAsync(() -> "Orders for " + userId);
        });
```

**使用场景**：
- 串行依赖（第二个任务依赖第一个任务的结果）
- 避免Future嵌套
- 链式异步调用

---

**3. thenCombine - 并行合并**

**定义**：两个CompletableFuture并行执行，完成后合并结果。

**特点**：
- 两个Future并行执行
- 等待两个都完成
- 合并两个结果

**示例**：
```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
    sleep(1000);
    return "Hello";
});

CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    sleep(1000);
    return "World";
});

// 并行执行，合并结果
CompletableFuture<String> result = future1.thenCombine(future2, (s1, s2) -> {
    return s1 + " " + s2;
});

System.out.println(result.join());  // "Hello World"
// 总耗时：1秒（并行）
```

**使用场景**：
- 并行查询多个接口
- 合并多个数据源
- 独立任务的结果聚合

---

**对比总结**：

| 方法 | 输入 | 输出 | 执行方式 | 使用场景 |
|-----|------|------|---------|---------|
| **thenApply** | T | U | 单个Future | 结果转换 |
| **thenCompose** | T | CompletableFuture\<U\> | 串行（有依赖） | 链式异步调用 |
| **thenCombine** | T, U | V | 并行（合并） | 并行查询合并 |

**示例对比**：

```java
// thenApply：单个Future的转换
CompletableFuture.supplyAsync(() -> "123")
    .thenApply(Integer::parseInt)  // String → Integer
    .thenApply(i -> i * 2);  // Integer → Integer

// thenCompose：串行组合（有依赖）
CompletableFuture.supplyAsync(() -> getUserId())
    .thenCompose(userId -> getOrders(userId));  // 依赖userId

// thenCombine：并行合并
CompletableFuture.supplyAsync(() -> getUser())
    .thenCombine(
        CompletableFuture.supplyAsync(() -> getOrders()),
        (user, orders) -> merge(user, orders)  // 合并结果
    );
```

---

### 3. 使用CompletableFuture实现：并行查询用户信息、订单信息、地址信息，然后合并返回。

**答案**：

```java
public class UserService {
    
    // 用户详情DTO
    public static class UserDetail {
        private User user;
        private List<Order> orders;
        private List<Address> addresses;
        
        // getters and setters
    }
    
    /**
     * 获取用户完整信息（并行查询）
     */
    public UserDetail getUserDetail(Long userId) {
        // 1. 并行查询3个接口
        CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询用户信息...");
            sleep(1000);  // 模拟耗时
            return queryUserInfo(userId);
        });
        
        CompletableFuture<List<Order>> orderFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询订单信息...");
            sleep(1000);  // 模拟耗时
            return queryUserOrders(userId);
        });
        
        CompletableFuture<List<Address>> addressFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询地址信息...");
            sleep(1000);  // 模拟耗时
            return queryUserAddresses(userId);
        });
        
        // 2. 等待所有查询完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            userFuture, 
            orderFuture, 
            addressFuture
        );
        
        // 3. 获取所有结果并组装
        return allFutures.thenApply(v -> {
            UserDetail detail = new UserDetail();
            detail.setUser(userFuture.join());
            detail.setOrders(orderFuture.join());
            detail.setAddresses(addressFuture.join());
            return detail;
        }).join();
        
        // 总耗时：1秒（3个查询并行）
        // 如果串行：3秒
    }
    
    /**
     * 方式2：使用thenCombine
     */
    public UserDetail getUserDetail2(Long userId) {
        CompletableFuture<User> userFuture = 
            CompletableFuture.supplyAsync(() -> queryUserInfo(userId));
        
        CompletableFuture<List<Order>> orderFuture = 
            CompletableFuture.supplyAsync(() -> queryUserOrders(userId));
        
        CompletableFuture<List<Address>> addressFuture = 
            CompletableFuture.supplyAsync(() -> queryUserAddresses(userId));
        
        // 合并结果
        return userFuture
            .thenCombine(orderFuture, (user, orders) -> {
                UserDetail detail = new UserDetail();
                detail.setUser(user);
                detail.setOrders(orders);
                return detail;
            })
            .thenCombine(addressFuture, (detail, addresses) -> {
                detail.setAddresses(addresses);
                return detail;
            })
            .join();
    }
    
    /**
     * 方式3：带异常处理和超时
     */
    public UserDetail getUserDetailWithTimeout(Long userId) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        try {
            CompletableFuture<User> userFuture = CompletableFuture
                .supplyAsync(() -> queryUserInfo(userId), executor)
                .exceptionally(ex -> {
                    log.error("查询用户失败", ex);
                    return new User();  // 返回默认值
                });
            
            CompletableFuture<List<Order>> orderFuture = CompletableFuture
                .supplyAsync(() -> queryUserOrders(userId), executor)
                .exceptionally(ex -> {
                    log.error("查询订单失败", ex);
                    return Collections.emptyList();
                });
            
            CompletableFuture<List<Address>> addressFuture = CompletableFuture
                .supplyAsync(() -> queryUserAddresses(userId), executor)
                .exceptionally(ex -> {
                    log.error("查询地址失败", ex);
                    return Collections.emptyList();
                });
            
            // 等待所有完成，最多3秒
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                userFuture, orderFuture, addressFuture
            );
            
            allFutures.get(3, TimeUnit.SECONDS);  // 3秒超时
            
            // 组装结果
            UserDetail detail = new UserDetail();
            detail.setUser(userFuture.join());
            detail.setOrders(orderFuture.join());
            detail.setAddresses(addressFuture.join());
            
            return detail;
            
        } catch (TimeoutException e) {
            log.error("查询超时", e);
            return new UserDetail();  // 返回默认值
        } catch (Exception e) {
            log.error("查询失败", e);
            return new UserDetail();
        } finally {
            executor.shutdown();
        }
    }
    
    // 模拟查询方法
    private User queryUserInfo(Long userId) {
        // 查询数据库...
        return new User(userId, "张三");
    }
    
    private List<Order> queryUserOrders(Long userId) {
        // 查询订单...
        return Arrays.asList(new Order(1L), new Order(2L));
    }
    
    private List<Address> queryUserAddresses(Long userId) {
        // 查询地址...
        return Arrays.asList(new Address("北京"), new Address("上海"));
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**性能对比**：
```
串行执行：
queryUserInfo (1秒)
  → queryUserOrders (1秒)
    → queryUserAddresses (1秒)
总耗时：3秒

并行执行：
queryUserInfo (1秒)     ↘
queryUserOrders (1秒)    → 合并
queryUserAddresses (1秒) ↗
总耗时：1秒（提升3倍）
```

---

## 五、编程题

### 实现一个批量查询工具，使用CompletableFuture并行查询多个用户，设置超时时间为5秒。

**答案**：

```java
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BatchQueryService {
    private final ExecutorService executor = Executors.newFixedThreadPool(20);
    
    /**
     * 批量查询用户
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    public List<User> batchQueryUsers(List<Long> userIds) {
        // 为每个userId创建一个CompletableFuture
        List<CompletableFuture<User>> futures = userIds.stream()
            .map(userId -> CompletableFuture.supplyAsync(() -> {
                return queryUser(userId);  // 查询单个用户
            }, executor))
            .collect(Collectors.toList());
        
        // 等待所有完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        try {
            // 设置5秒超时
            allFutures.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.err.println("查询超时");
            // 取消未完成的任务
            futures.forEach(f -> f.cancel(true));
        } catch (Exception e) {
            System.err.println("查询失败：" + e.getMessage());
        }
        
        // 获取所有结果（过滤失败的）
        return futures.stream()
            .filter(f -> f.isDone() && !f.isCompletedExceptionally())
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }
    
    /**
     * 批量查询（带异常处理）
     */
    public List<User> batchQueryUsersWithErrorHandling(List<Long> userIds) {
        List<CompletableFuture<User>> futures = userIds.stream()
            .map(userId -> CompletableFuture
                .supplyAsync(() -> queryUser(userId), executor)
                .exceptionally(ex -> {
                    System.err.println("查询用户" + userId + "失败：" + ex.getMessage());
                    return null;  // 失败返回null
                })
            )
            .collect(Collectors.toList());
        
        // 等待所有完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .join();
        
        // 获取所有结果（过滤null）
        return futures.stream()
            .map(CompletableFuture::join)
            .filter(user -> user != null)
            .collect(Collectors.toList());
    }
    
    /**
     * 查询单个用户
     */
    private User queryUser(Long userId) {
        try {
            Thread.sleep(500);  // 模拟耗时
            return new User(userId, "User" + userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("查询中断", e);
        }
    }
    
    /**
     * 关闭线程池
     */
    public void shutdown() {
        executor.shutdown();
    }
    
    // 测试
    public static void main(String[] args) {
        BatchQueryService service = new BatchQueryService();
        
        // 查询100个用户
        List<Long> userIds = LongStream.rangeClosed(1, 100)
            .boxed()
            .collect(Collectors.toList());
        
        long start = System.currentTimeMillis();
        List<User> users = service.batchQueryUsers(userIds);
        long time = System.currentTimeMillis() - start;
        
        System.out.println("查询结果：" + users.size() + "个用户");
        System.out.println("耗时：" + time + "ms");
        
        service.shutdown();
    }
}

class User {
    private Long id;
    private String name;
    
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "'}";
    }
}
```

**输出示例**：
```
查询结果：100个用户
耗时：2500ms

串行执行：100 * 500ms = 50秒
并行执行：500ms（20个线程）* 5批 = 2.5秒
性能提升：20倍
```

---

💡 **提示**：CompletableFuture是异步编程的利器，合理使用可以大幅提升系统性能！记住：指定线程池，处理异常，设置超时！
