# CompletableFuture与异步编程

## 一、为什么需要异步编程？

### 同步vs异步

**同步执行（阻塞）**：
```java
// ❌ 同步：总耗时 = 3秒
String result1 = queryUserInfo();      // 1秒
String result2 = queryOrderInfo();     // 1秒
String result3 = queryProductInfo();   // 1秒
// 总耗时：3秒
```

**异步执行（非阻塞）**：
```java
// ✅ 异步：总耗时 = 1秒（并行）
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> queryUserInfo());
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> queryOrderInfo());
CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> queryProductInfo());

// 等待所有完成
CompletableFuture.allOf(future1, future2, future3).join();
// 总耗时：1秒（并行执行）
```

## 二、Future接口

### Future的局限性

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

// 提交任务
Future<String> future = executor.submit(() -> {
    Thread.sleep(2000);
    return "Hello";
});

// ❌ Future的问题
try {
    String result = future.get();  // 阻塞等待
    // 1. 只能通过get()阻塞获取结果
    // 2. 不能链式调用
    // 3. 不能组合多个Future
    // 4. 没有异常处理机制
} catch (Exception e) {
    e.printStackTrace();
}
```

## 三、CompletableFuture

### 定义

**CompletableFuture**：JDK 8引入的异步编程工具，弥补Future的不足。

**特点**：
- 支持链式调用
- 支持组合多个异步任务
- 支持异常处理
- 支持回调

### 创建CompletableFuture

#### 方法1：runAsync（无返回值）

```java
// 异步执行，无返回值
CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
    System.out.println("异步任务执行");
    // 无返回值
});
```

#### 方法2：supplyAsync（有返回值）

```java
// 异步执行，有返回值
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "Hello";
});
```

#### 方法3：指定线程池

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "Hello";
}, executor);  // 指定线程池
```

#### 方法4：completedFuture（已完成）

```java
// 创建已完成的Future
CompletableFuture<String> future = CompletableFuture.completedFuture("Hello");
String result = future.get();  // 立即返回
```

## 四、CompletableFuture核心方法

### 1. 转换结果

#### thenApply（有返回值）

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "Hello";
}).thenApply(s -> {
    return s + " World";  // 转换结果
}).thenApply(s -> {
    return s + "!";
});

String result = future.join();  // "Hello World!"
```

#### thenAccept（无返回值）

```java
CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
    return "Hello";
}).thenAccept(s -> {
    System.out.println(s);  // 消费结果，无返回值
});
```

#### thenRun（不关心结果）

```java
CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
    return "Hello";
}).thenRun(() -> {
    System.out.println("任务完成");  // 不接收结果
});
```

### 2. 组合多个Future

#### thenCompose（串行，有依赖）

```java
// 第二个任务依赖第一个任务的结果
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "User123";
}).thenCompose(userId -> {
    // 根据userId查询订单
    return CompletableFuture.supplyAsync(() -> {
        return "Order for " + userId;
    });
});

String result = future.join();  // "Order for User123"
```

#### thenCombine（并行，合并结果）

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
    return "Hello";
});

CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    return "World";
});

// 并行执行，合并结果
CompletableFuture<String> result = future1.thenCombine(future2, (s1, s2) -> {
    return s1 + " " + s2;
});

System.out.println(result.join());  // "Hello World"
```

#### allOf（等待所有完成）

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Task1");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Task2");
CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "Task3");

// 等待所有完成
CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);
allFutures.join();

// 获取所有结果
String result1 = future1.join();
String result2 = future2.join();
String result3 = future3.join();
```

#### anyOf（任意一个完成）

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
    sleep(2000);
    return "Task1";
});

CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    sleep(1000);
    return "Task2";  // 最快
});

// 任意一个完成就返回
CompletableFuture<Object> fastest = CompletableFuture.anyOf(future1, future2);
String result = (String) fastest.join();  // "Task2"
```

### 3. 异常处理

#### exceptionally（捕获异常）

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    if (Math.random() > 0.5) {
        throw new RuntimeException("Error!");
    }
    return "Success";
}).exceptionally(ex -> {
    System.out.println("异常：" + ex.getMessage());
    return "Default";  // 返回默认值
});
```

#### handle（处理结果或异常）

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    if (Math.random() > 0.5) {
        throw new RuntimeException("Error!");
    }
    return "Success";
}).handle((result, ex) -> {
    if (ex != null) {
        return "Error: " + ex.getMessage();
    }
    return result;
});
```

#### whenComplete（完成时回调）

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "Hello";
}).whenComplete((result, ex) -> {
    if (ex != null) {
        System.out.println("异常：" + ex.getMessage());
    } else {
        System.out.println("结果：" + result);
    }
});
```

### 4. 获取结果

#### join（等待完成，抛RuntimeException）

```java
String result = future.join();  // 阻塞等待
```

#### get（等待完成，抛检查异常）

```java
try {
    String result = future.get();  // 阻塞等待
    // 或设置超时
    String result2 = future.get(3, TimeUnit.SECONDS);
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    e.printStackTrace();
}
```

#### getNow（立即获取，不等待）

```java
String result = future.getNow("Default");  // 未完成返回默认值
```

## 五、实战应用场景

### 场景1：并行查询，合并结果

```java
public class UserService {
    public UserDetail getUserDetail(Long userId) {
        // 并行查询3个接口
        CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> {
            return queryUserInfo(userId);  // 1秒
        });
        
        CompletableFuture<List<Order>> orderFuture = CompletableFuture.supplyAsync(() -> {
            return queryUserOrders(userId);  // 1秒
        });
        
        CompletableFuture<List<Address>> addressFuture = CompletableFuture.supplyAsync(() -> {
            return queryUserAddresses(userId);  // 1秒
        });
        
        // 等待所有完成
        CompletableFuture.allOf(userFuture, orderFuture, addressFuture).join();
        
        // 组装结果
        UserDetail detail = new UserDetail();
        detail.setUser(userFuture.join());
        detail.setOrders(orderFuture.join());
        detail.setAddresses(addressFuture.join());
        
        return detail;  // 总耗时：1秒（并行）
    }
}
```

### 场景2：流式处理

```java
public List<String> processUsers(List<Long> userIds) {
    return userIds.stream()
        .map(userId -> CompletableFuture.supplyAsync(() -> {
            // 异步查询用户
            return queryUser(userId);
        }))
        .collect(Collectors.toList())
        .stream()
        .map(CompletableFuture::join)  // 等待所有完成
        .map(User::getName)
        .collect(Collectors.toList());
}
```

### 场景3：超时控制

```java
public String queryWithTimeout(String url) {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
        return httpGet(url);
    });
    
    try {
        return future.get(3, TimeUnit.SECONDS);  // 3秒超时
    } catch (TimeoutException e) {
        future.cancel(true);  // 取消任务
        return "Timeout";
    } catch (Exception e) {
        return "Error";
    }
}
```

### 场景4：异步回调

```java
public void processAsync(Long orderId) {
    CompletableFuture.supplyAsync(() -> {
        // 1. 查询订单
        return queryOrder(orderId);
    }).thenApplyAsync(order -> {
        // 2. 处理订单
        return processOrder(order);
    }).thenAcceptAsync(result -> {
        // 3. 发送通知
        sendNotification(result);
    }).exceptionally(ex -> {
        // 4. 异常处理
        log.error("处理失败", ex);
        return null;
    });
}
```

### 场景5：缓存击穿防护

```java
public class CacheService {
    private Map<String, CompletableFuture<String>> cache = new ConcurrentHashMap<>();
    
    public String getData(String key) {
        // 如果缓存中有未完成的Future，直接返回
        CompletableFuture<String> future = cache.computeIfAbsent(key, k -> {
            // 只有一个线程会执行这里
            return CompletableFuture.supplyAsync(() -> {
                // 查询数据库
                return queryFromDB(k);
            }).whenComplete((result, ex) -> {
                // 完成后移除Future
                cache.remove(k);
            });
        });
        
        return future.join();
    }
}
```

## 六、最佳实践

### 实践1：指定线程池

```java
// ❌ 不推荐：使用默认线程池（ForkJoinPool）
CompletableFuture.supplyAsync(() -> {
    return "Hello";
});

// ✅ 推荐：指定线程池
ExecutorService executor = Executors.newFixedThreadPool(10);
CompletableFuture.supplyAsync(() -> {
    return "Hello";
}, executor);
```

**原因**：默认线程池是ForkJoinPool.commonPool()，所有异步任务共享，可能相互影响。

### 实践2：异常处理

```java
// ✅ 总是处理异常
CompletableFuture.supplyAsync(() -> {
    return riskyOperation();
}).exceptionally(ex -> {
    log.error("操作失败", ex);
    return defaultValue();
}).thenAccept(result -> {
    // 处理结果
});
```

### 实践3：避免阻塞

```java
// ❌ 不推荐：在回调中阻塞
future.thenApply(result -> {
    return anotherFuture.join();  // 阻塞！
});

// ✅ 推荐：使用thenCompose
future.thenCompose(result -> {
    return anotherFuture;  // 不阻塞
});
```

### 实践4：合理使用join vs get

```java
// join：不抛检查异常，适合链式调用
String result = future.join();

// get：抛检查异常，需要try-catch
try {
    String result = future.get();
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

### 实践5：超时保护

```java
// 为长时间任务设置超时
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return longRunningTask();
});

try {
    String result = future.get(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    future.cancel(true);
    // 处理超时
}
```

## 七、常见方法总结

| 方法 | 说明 | 返回值 |
|-----|------|-------|
| **创建** | | |
| runAsync | 异步执行，无返回值 | CompletableFuture\<Void\> |
| supplyAsync | 异步执行，有返回值 | CompletableFuture\<T\> |
| **转换** | | |
| thenApply | 转换结果（有返回值） | CompletableFuture\<U\> |
| thenAccept | 消费结果（无返回值） | CompletableFuture\<Void\> |
| thenRun | 不关心结果 | CompletableFuture\<Void\> |
| **组合** | | |
| thenCompose | 串行组合（有依赖） | CompletableFuture\<U\> |
| thenCombine | 并行组合（合并结果） | CompletableFuture\<V\> |
| allOf | 等待所有完成 | CompletableFuture\<Void\> |
| anyOf | 任意一个完成 | CompletableFuture\<Object\> |
| **异常** | | |
| exceptionally | 捕获异常 | CompletableFuture\<T\> |
| handle | 处理结果或异常 | CompletableFuture\<U\> |
| whenComplete | 完成时回调 | CompletableFuture\<T\> |
| **获取** | | |
| join | 阻塞等待（RuntimeException） | T |
| get | 阻塞等待（检查异常） | T |
| getNow | 立即获取 | T |

## 八、小结

**核心要点**：

1. **CompletableFuture定义**：
   - JDK 8引入的异步编程工具
   - 弥补Future的不足
   - 支持链式调用、组合、异常处理

2. **创建方式**：
   - runAsync：无返回值
   - supplyAsync：有返回值
   - 指定线程池

3. **核心方法**：
   - 转换：thenApply、thenAccept、thenRun
   - 组合：thenCompose、thenCombine、allOf、anyOf
   - 异常：exceptionally、handle、whenComplete
   - 获取：join、get、getNow

4. **应用场景**：
   - 并行查询，合并结果
   - 流式处理
   - 超时控制
   - 异步回调
   - 缓存击穿防护

5. **最佳实践**：
   - 指定线程池
   - 异常处理
   - 避免阻塞
   - 超时保护

**记忆口诀**：
- CompletableFuture异步编程，链式调用很方便
- thenApply转换结果，thenCompose串行执行
- allOf等待全部，anyOf任意一个
- 异常处理要记得，指定线程池更好

---

💡 **提示**：CompletableFuture是异步编程的利器，合理使用可大幅提升系统性能！记住：指定线程池，处理异常！
