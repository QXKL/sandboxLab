# volatile与内存模型

## 一、什么是volatile？

### 定义

**volatile**：Java关键字，保证变量的**可见性**和**有序性**（禁止指令重排）。

```java
public class SharedData {
    private volatile boolean flag = false;  // volatile修饰
    
    public void writer() {
        flag = true;  // 写线程
    }
    
    public void reader() {
        if (flag) {  // 读线程能立即看到flag=true
            // ...
        }
    }
}
```

### 为什么需要volatile？

#### 问题1：可见性问题

```java
public class VisibilityProblem {
    private boolean flag = false;  // 没有volatile
    
    // 线程1
    public void writer() {
        flag = true;
    }
    
    // 线程2
    public void reader() {
        while (!flag) {
            // 可能永远循环（看不到flag=true）
        }
        System.out.println("Flag is true");
    }
}
```

**原因**：每个线程都有自己的工作内存（CPU缓存），线程1修改flag后，线程2可能读取的还是旧值。

#### 问题2：指令重排

```java
public class ReorderProblem {
    private int a = 0;
    private boolean flag = false;
    
    // 线程1
    public void writer() {
        a = 1;           // 1
        flag = true;     // 2
        // 可能被重排为：
        // flag = true;  // 2
        // a = 1;        // 1
    }
    
    // 线程2
    public void reader() {
        if (flag) {       // 看到flag=true
            int i = a;    // 但a可能还是0（重排导致）
        }
    }
}
```

## 二、Java内存模型（JMM）

### 定义

**Java内存模型（Java Memory Model, JMM）**：定义了线程如何与内存交互，保证多线程程序的可见性、有序性、原子性。

### 内存结构

```
主内存（Main Memory）
   ↑     ↓
工作内存1  工作内存2  工作内存3
(CPU缓存) (CPU缓存) (CPU缓存)
   ↑         ↑         ↑
线程1      线程2      线程3
```

**关键点**：
- 所有变量存储在主内存
- 每个线程有自己的工作内存（CPU缓存）
- 线程操作变量必须先拷贝到工作内存
- 线程间不能直接访问对方的工作内存

### 8种内存操作

1. **lock（锁定）**：主内存变量，标识为线程独占
2. **unlock（解锁）**：释放锁定的变量
3. **read（读取）**：从主内存读到工作内存
4. **load（载入）**：read后的值放入工作内存副本
5. **use（使用）**：工作内存值传给执行引擎
6. **assign（赋值）**：执行引擎的值赋给工作内存
7. **store（存储）**：工作内存值传到主内存
8. **write（写入）**：store后的值放入主内存

## 三、volatile的特性

### 特性1：可见性

**保证**：一个线程修改volatile变量后，其他线程立即可见。

```java
public class VolatileVisibility {
    private volatile boolean flag = false;
    
    // 线程1
    public void writer() {
        flag = true;  // 写入后立即刷新到主内存
    }
    
    // 线程2
    public void reader() {
        while (!flag) {
            // 每次都从主内存读取flag
        }
        System.out.println("Flag is true");  // 能看到变化
    }
}
```

**原理**：
- **写volatile变量**：立即刷新到主内存
- **读volatile变量**：从主内存读取最新值

### 特性2：有序性（禁止指令重排）

**保证**：volatile变量的读写不会被重排序。

```java
public class VolatileOrdering {
    private int a = 0;
    private volatile boolean flag = false;
    
    // 线程1
    public void writer() {
        a = 1;           // 1. 普通写
        flag = true;     // 2. volatile写
        // volatile写之前的操作不会被重排到后面
    }
    
    // 线程2
    public void reader() {
        if (flag) {       // 3. volatile读
            int i = a;    // 4. 普通读
            // i一定是1（不会是0）
        }
        // volatile读之后的操作不会被重排到前面
    }
}
```

**内存屏障（Memory Barrier）**：
```
写volatile变量：
  StoreStore屏障
  写volatile
  StoreLoad屏障

读volatile变量：
  LoadLoad屏障
  读volatile
  LoadStore屏障
```

### 特性3：不保证原子性

```java
public class VolatileAtomic {
    private volatile int count = 0;
    
    public void increment() {
        count++;  // ❌ 不是原子操作！
        // 实际是3步：
        // 1. 读取count
        // 2. count+1
        // 3. 写回count
    }
}

// 100个线程同时increment
// 最终count可能 < 100（丢失更新）
```

**解决方案**：
```java
// 方案1：synchronized
public synchronized void increment() {
    count++;
}

// 方案2：AtomicInteger
private AtomicInteger count = new AtomicInteger(0);
public void increment() {
    count.incrementAndGet();
}
```

## 四、happens-before原则

### 定义

**happens-before**：如果操作A happens-before 操作B，那么A的结果对B可见。

### 8大规则

#### 规则1：程序顺序规则

```java
int a = 1;  // A
int b = 2;  // B
// A happens-before B（单线程内按代码顺序）
```

#### 规则2：volatile变量规则

```java
volatile boolean flag = false;

// 线程1
a = 1;           // A
flag = true;     // B (volatile写)

// 线程2
if (flag) {      // C (volatile读)
    int i = a;   // D
}

// A happens-before B（volatile写之前）
// B happens-before C（volatile变量规则）
// C happens-before D（volatile读之后）
// 所以 A happens-before D（传递性）
```

#### 规则3：锁规则

```java
synchronized (lock) {
    // A
}

// happens-before

synchronized (lock) {
    // B
}
// 解锁 happens-before 下一次加锁
```

#### 规则4：线程启动规则

```java
Thread t = new Thread(() -> {
    // B
});
t.start();  // A

// A happens-before B
```

#### 规则5：线程终止规则

```java
Thread t = new Thread(() -> {
    // A
});
t.start();
t.join();  // B

// A happens-before B
```

#### 规则6：中断规则

```java
// 线程1
thread.interrupt();  // A

// 线程2
if (Thread.interrupted()) {  // B
    // ...
}

// A happens-before B
```

#### 规则7：对象终结规则

```java
// 构造函数结束 happens-before finalize()方法
```

#### 规则8：传递性

```java
// A happens-before B
// B happens-before C
// 那么 A happens-before C
```

## 五、volatile应用场景

### 场景1：状态标志

```java
public class Server {
    private volatile boolean running = true;
    
    public void start() {
        new Thread(() -> {
            while (running) {
                // 处理请求
                handleRequest();
            }
        }).start();
    }
    
    public void stop() {
        running = false;  // 其他线程立即看到
    }
}
```

### 场景2：双重检查锁单例

```java
public class Singleton {
    private static volatile Singleton instance;  // 必须volatile
    
    public static Singleton getInstance() {
        if (instance == null) {  // 第一次检查
            synchronized (Singleton.class) {
                if (instance == null) {  // 第二次检查
                    instance = new Singleton();
                    // 没有volatile可能出现问题：
                    // 1. 分配内存
                    // 2. instance指向内存（重排到这里）
                    // 3. 初始化对象
                    // → 其他线程看到instance!=null但对象未初始化
                }
            }
        }
        return instance;
    }
}
```

### 场景3：读写锁状态

```java
public class VolatileLock {
    private volatile int state = 0;
    
    public void lock() {
        while (!compareAndSwap(0, 1)) {
            // 自旋等待
        }
    }
    
    public void unlock() {
        state = 0;
    }
}
```

### 场景4：一次性安全发布

```java
public class BackgroundTask {
    private volatile TaskResult result;
    
    public void compute() {
        TaskResult temp = new TaskResult();
        // 初始化temp...
        result = temp;  // 安全发布（其他线程看到的是完整对象）
    }
    
    public TaskResult getResult() {
        return result;
    }
}
```

## 六、volatile vs synchronized

| 特性 | volatile | synchronized |
|-----|----------|-------------|
| **可见性** | ✓ | ✓ |
| **有序性** | ✓（禁止重排） | ✓ |
| **原子性** | ✗ | ✓ |
| **锁机制** | 无锁 | 加锁 |
| **性能** | 高 | 低 |
| **适用范围** | 变量 | 代码块/方法 |
| **阻塞** | 不阻塞 | 可能阻塞 |

**选择建议**：
```
只需可见性 → volatile
需要原子性 → synchronized 或 AtomicXXX
```

## 七、常见误区

### 误区1：volatile保证原子性

```java
// ❌ 错误
private volatile int count = 0;
public void increment() {
    count++;  // 不是原子操作
}

// ✅ 正确
private AtomicInteger count = new AtomicInteger(0);
public void increment() {
    count.incrementAndGet();
}
```

### 误区2：volatile代替锁

```java
// ❌ 错误
private volatile List<String> list = new ArrayList<>();
public void add(String item) {
    list.add(item);  // ArrayList不是线程安全的
}

// ✅ 正确
private final List<String> list = new CopyOnWriteArrayList<>();
public void add(String item) {
    list.add(item);
}
```

### 误区3：volatile变量之间没有关系

```java
// ❌ 错误理解
private volatile int a = 0;
private volatile int b = 0;

public void writer() {
    a = 1;
    b = 2;
    // 不保证其他线程看到a=1时b=2
}

// ✅ 正确
private int a = 0;
private volatile boolean flag = false;

public void writer() {
    a = 1;
    flag = true;  // 保证a=1对读线程可见
}

public void reader() {
    if (flag) {
        int i = a;  // 一定是1
    }
}
```

## 八、小结

**核心要点**：

1. **volatile定义**：
   - 保证可见性
   - 保证有序性（禁止指令重排）
   - 不保证原子性

2. **Java内存模型**：
   - 主内存 + 工作内存
   - 8种内存操作
   - 保证可见性、有序性、原子性

3. **happens-before**：
   - 8大规则
   - 保证内存可见性
   - 建立偏序关系

4. **volatile应用**：
   - 状态标志
   - 双重检查锁
   - 一次性安全发布

5. **volatile vs synchronized**：
   - volatile：轻量级，只保证可见性和有序性
   - synchronized：重量级，保证原子性、可见性、有序性

**记忆口诀**：
- volatile保可见，禁止指令重排
- 原子性不保证，复合操作要小心
- happens-before，内存可见性
- 状态标志最常用，双检锁要volatile

---

💡 **提示**：volatile是轻量级的同步机制，但不能代替锁。只在确实需要可见性和有序性时使用。
