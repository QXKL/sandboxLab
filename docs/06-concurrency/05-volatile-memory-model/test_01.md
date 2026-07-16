# volatile与内存模型 - 测试题

## 一、选择题

### 1. volatile关键字不能保证什么？
A. 可见性  
B. 有序性  
C. 原子性  
D. 禁止指令重排

**答案**：C

**解析**：volatile保证可见性和有序性，但不保证原子性。count++这类复合操作不是原子的。

---

### 2. Java内存模型中，线程间通信通过什么完成？
A. 直接访问对方的工作内存  
B. 通过主内存  
C. 通过消息队列  
D. 通过网络

**答案**：B

**解析**：线程不能直接访问对方的工作内存，必须通过主内存进行通信。

---

### 3. 以下哪个场景适合使用volatile？
A. 计数器（count++）  
B. 状态标志（boolean flag）  
C. 银行转账  
D. 复杂对象的更新

**答案**：B

**解析**：volatile适合简单的状态标志，不适合需要原子性的复合操作。

---

### 4. 双重检查锁单例模式中，为什么instance必须用volatile修饰？
A. 提高性能  
B. 防止指令重排导致返回未初始化对象  
C. 保证线程安全  
D. 没有必要

**答案**：B

**解析**：new对象可能被重排序（分配内存→指向内存→初始化），没有volatile可能返回未初始化的对象。

---

### 5. happens-before的传递性是什么意思？
A. A happens-before B, B happens-before C, 则 A happens-before C  
B. 操作按时间顺序执行  
C. 线程按顺序启动  
D. 变量按顺序初始化

**答案**：A

**解析**：happens-before具有传递性，如果A happens-before B，B happens-before C，则A happens-before C。

---

### 6. 以下代码有什么问题？
```java
private volatile int count = 0;
public void increment() {
    count++;
}
```
A. 没有问题  
B. count++不是原子操作  
C. volatile修饰错误  
D. 应该用synchronized

**答案**：B

**解析**：count++是三步操作（读-改-写），volatile不保证原子性，多线程下会丢失更新。

---

### 7. volatile变量的写操作会触发什么？
A. 立即刷新到主内存  
B. 阻塞其他线程  
C. 加锁  
D. 通知其他线程

**答案**：A

**解析**：volatile写操作会立即刷新到主内存，保证其他线程能看到最新值。

---

### 8. 以下哪个不是happens-before规则？
A. 程序顺序规则  
B. volatile变量规则  
C. 性能优化规则  
D. 锁规则

**答案**：C

**解析**：happens-before的8大规则不包括性能优化规则。

---

### 9. 为什么volatile不能代替synchronized？
A. 性能差  
B. 不保证原子性  
C. 语法复杂  
D. 不支持方法

**答案**：B

**解析**：volatile只保证可见性和有序性，不保证原子性，无法替代synchronized保护复合操作。

---

### 10. 以下关于内存屏障的说法正确的是？
A. 内存屏障会阻塞线程  
B. 内存屏障禁止特定类型的指令重排  
C. 内存屏障会降低性能50%  
D. 内存屏障只在多核CPU有效

**答案**：B

**解析**：内存屏障（Memory Barrier）禁止特定类型的指令重排，保证有序性和可见性。

---

## 二、填空题

### 1. volatile关键字保证了变量的________和________，但不保证________。

**答案**：可见性、有序性（禁止指令重排）、原子性

---

### 2. Java内存模型中，每个线程都有自己的________，所有共享变量存储在________。

**答案**：工作内存（CPU缓存）、主内存

---

### 3. happens-before原则中，如果操作A happens-before 操作B，那么A的结果对B________。

**答案**：可见

---

### 4. 双重检查锁单例模式中，instance变量必须用________修饰，否则可能返回________的对象。

**答案**：volatile、未初始化

---

### 5. volatile写操作会在前后插入________，防止指令重排序。

**答案**：内存屏障（Memory Barrier）

---

## 三、判断题

### 1. volatile修饰的变量，一个线程修改后，其他线程立即可见。（ ）

**答案**：✓

**解析**：这正是volatile的可见性保证。

---

### 2. volatile可以保证count++操作的原子性。（ ）

**答案**：✗

**解析**：count++是复合操作（读-改-写），volatile不保证原子性。

---

### 3. synchronized既保证可见性又保证原子性。（ ）

**答案**：✓

**解析**：synchronized保证原子性、可见性、有序性，是重量级同步机制。

---

### 4. 线程可以直接访问其他线程的工作内存。（ ）

**答案**：✗

**解析**：线程间不能直接访问对方的工作内存，必须通过主内存通信。

---

### 5. volatile变量的读写操作不会被重排序。（ ）

**答案**：✓

**解析**：volatile通过内存屏障禁止指令重排序。

---

## 四、简答题

### 1. 解释什么是可见性问题，以及volatile如何解决这个问题。

**答案**：

**可见性问题**：

在多线程环境下，一个线程修改了共享变量的值，其他线程可能看不到这个修改。

**原因**：
```
1. 每个线程有自己的工作内存（CPU缓存）
2. 线程读写变量都在工作内存中进行
3. 工作内存和主内存之间不是实时同步的
4. 线程A修改变量后，可能只更新了自己的工作内存
5. 线程B读取时，可能读到的还是旧值
```

**示例**：
```java
public class VisibilityProblem {
    private boolean flag = false;  // 没有volatile
    
    // 线程1
    public void writer() {
        flag = true;  // 修改在工作内存中
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

**执行过程**：
```
1. 线程1：读取flag=false到工作内存
2. 线程1：修改工作内存中的flag=true
3. 线程1：可能没有立即刷新到主内存
4. 线程2：从工作内存读取flag=false（旧值）
5. 线程2：一直循环等待
```

---

**volatile解决方案**：

```java
public class VolatileSolution {
    private volatile boolean flag = false;  // 加volatile
    
    // 线程1
    public void writer() {
        flag = true;  // 立即刷新到主内存
    }
    
    // 线程2
    public void reader() {
        while (!flag) {
            // 每次都从主内存读取
        }
        System.out.println("Flag is true");  // 能正常执行
    }
}
```

**volatile的两个保证**：

1. **写可见性**：
   - volatile变量写操作后，立即刷新到主内存
   - 强制将工作内存的值写回主内存

2. **读可见性**：
   - volatile变量读操作前，从主内存读取最新值
   - 强制从主内存读取，而不是工作内存

**底层实现**：
```
1. 写volatile变量：加入StoreLoad内存屏障
2. 读volatile变量：加入LoadLoad内存屏障
3. 内存屏障确保缓存一致性
```

**总结**：volatile通过强制读写主内存，保证了变量在多线程间的可见性。

---

### 2. 说明volatile如何禁止指令重排序，以及为什么需要禁止重排序。

**答案**：

**什么是指令重排序**：

编译器和处理器为了优化性能，可能会改变指令的执行顺序。

```java
// 原始代码
int a = 1;        // 1
int b = 2;        // 2
int c = a + b;    // 3

// 可能被重排为
int b = 2;        // 2
int a = 1;        // 1
int c = a + b;    // 3
// 单线程下不影响结果
```

---

**为什么需要禁止重排序**：

多线程下，重排序可能导致程序错误。

**示例**：
```java
public class ReorderProblem {
    private int a = 0;
    private boolean flag = false;
    
    // 线程1
    public void writer() {
        a = 1;           // 1
        flag = true;     // 2
    }
    
    // 线程2
    public void reader() {
        if (flag) {      // 3
            int i = a;   // 4
            // 期望：i=1
            // 实际：可能i=0（因为重排序）
        }
    }
}
```

**重排序问题**：
```
线程1可能被重排为：
  flag = true;   // 2
  a = 1;         // 1

执行时序：
1. 线程1：flag = true
2. 线程2：看到flag=true，读取a
3. 线程2：a还是0（线程1还没执行a=1）
4. 线程1：a = 1
→ 线程2得到错误结果
```

---

**volatile如何禁止重排序**：

通过插入**内存屏障（Memory Barrier）**。

**4种内存屏障**：
1. **LoadLoad**：禁止Load之间重排
2. **StoreStore**：禁止Store之间重排
3. **LoadStore**：禁止Load和Store重排
4. **StoreLoad**：禁止Store和Load重排

**volatile写操作**：
```
普通写操作
StoreStore屏障  ← 禁止上面的写和下面的写重排
volatile写
StoreLoad屏障   ← 禁止上面的写和下面的读重排
```

**volatile读操作**：
```
volatile读
LoadLoad屏障    ← 禁止下面的读和上面的读重排
LoadStore屏障   ← 禁止下面的写和上面的读重排
普通读/写操作
```

---

**示例**：
```java
public class VolatileReorder {
    private int a = 0;
    private volatile boolean flag = false;  // volatile
    
    // 线程1
    public void writer() {
        a = 1;           // 1. 普通写
        flag = true;     // 2. volatile写
        // StoreStore屏障：禁止1和2重排
        // 保证a=1一定在flag=true之前执行
    }
    
    // 线程2
    public void reader() {
        if (flag) {      // 3. volatile读
            // LoadLoad屏障：禁止3和4重排
            int i = a;   // 4. 普通读
            // i一定是1
        }
    }
}
```

**happens-before关系**：
```
a = 1 (1)
  ↓ (程序顺序)
flag = true (2)
  ↓ (volatile变量规则)
if (flag) (3)
  ↓ (程序顺序)
int i = a (4)

所以：1 happens-before 4
保证：线程2看到flag=true时，一定能看到a=1
```

---

**总结**：

volatile通过内存屏障禁止指令重排序，保证：
1. **volatile写之前的操作**不会被重排到后面
2. **volatile读之后的操作**不会被重排到前面
3. 建立happens-before关系
4. 保证多线程程序的正确性

---

### 3. 什么是happens-before原则？列举至少5个happens-before规则。

**答案**：

**happens-before定义**：

如果操作A happens-before 操作B，那么A的执行结果对B可见，并且A的执行顺序在B之前。

**作用**：
- 定义内存可见性
- 建立偏序关系
- 保证多线程程序正确性

---

**8大happens-before规则**：

**1. 程序顺序规则（Program Order Rule）**
```java
int a = 1;  // A
int b = 2;  // B

// A happens-before B
// 单线程内，按代码顺序执行
```

---

**2. volatile变量规则（Volatile Variable Rule）**
```java
volatile boolean flag = false;

// 线程1
a = 1;           // A
flag = true;     // B (volatile写)

// 线程2
if (flag) {      // C (volatile读)
    int i = a;   // D
}

// B happens-before C（volatile变量规则）
// A happens-before B（程序顺序）
// C happens-before D（程序顺序）
// 传递性：A happens-before D
```

---

**3. 锁规则（Monitor Lock Rule）**
```java
synchronized (lock) {
    // A：操作1
}  // 解锁

// happens-before

synchronized (lock) {
    // B：操作2
}  // 加锁

// A happens-before B
// 解锁操作 happens-before 后续的加锁操作
```

---

**4. 线程启动规则（Thread Start Rule）**
```java
int x = 0;

Thread t = new Thread(() -> {
    int y = x;  // B：能看到x=10
});

x = 10;  // A
t.start();  // 启动

// A happens-before B
// t.start()之前的操作对线程t可见
```

---

**5. 线程终止规则（Thread Termination Rule）**
```java
Thread t = new Thread(() -> {
    x = 10;  // A
});

t.start();
t.join();  // B：等待线程结束

int y = x;  // C：能看到x=10

// A happens-before C
// 线程结束 happens-before join()返回
```

---

**6. 中断规则（Thread Interruption Rule）**
```java
// 线程1
thread.interrupt();  // A

// 线程2（被中断线程）
if (Thread.interrupted()) {  // B
    // 能检测到中断
}

// A happens-before B
```

---

**7. 对象终结规则（Finalizer Rule）**
```java
public class MyObject {
    public MyObject() {
        // 构造函数
    }
    
    @Override
    protected void finalize() {
        // finalize方法
    }
}

// 构造函数结束 happens-before finalize()方法开始
```

---

**8. 传递性（Transitivity）**
```java
// A happens-before B
// B happens-before C
// 则 A happens-before C

// 示例：
a = 1;           // A
flag = true;     // B (volatile写)
if (flag) {      // C (volatile读)
    int i = a;   // D
}

// A happens-before B（程序顺序）
// B happens-before C（volatile规则）
// C happens-before D（程序顺序）
// 传递：A happens-before D
```

---

**应用示例**：

```java
public class HappensBeforeExample {
    private int a = 0;
    private volatile boolean flag = false;
    
    // 线程1
    public void writer() {
        a = 1;           // 1
        flag = true;     // 2 (volatile写)
    }
    
    // 线程2
    public void reader() {
        if (flag) {      // 3 (volatile读)
            assert a == 1;  // 4：断言成功
        }
    }
}
```

**happens-before链**：
```
1. a = 1
   ↓ (程序顺序规则)
2. flag = true
   ↓ (volatile变量规则)
3. if (flag)
   ↓ (程序顺序规则)
4. assert a == 1

所以：1 happens-before 4
保证：线程2能看到a=1
```

---

### 4. 为什么双重检查锁单例模式中instance必须用volatile修饰？

**答案**：

**双重检查锁代码**：

```java
public class Singleton {
    private static volatile Singleton instance;  // 必须volatile
    
    public static Singleton getInstance() {
        if (instance == null) {  // 第一次检查（不加锁）
            synchronized (Singleton.class) {
                if (instance == null) {  // 第二次检查
                    instance = new Singleton();  // 问题出在这里
                }
            }
        }
        return instance;
    }
}
```

---

**问题分析**：

**new Singleton()不是原子操作**，分为3步：

```
1. 分配内存空间
2. 初始化对象
3. instance指向内存地址
```

**可能的指令重排**：
```
1. 分配内存空间
3. instance指向内存地址  ← 重排到这里
2. 初始化对象            ← 延后到这里
```

---

**出现问题的场景**：

```
时刻1：线程A进入synchronized，执行new Singleton()
      1. 分配内存
      3. instance指向内存（重排）
      
时刻2：线程B执行第一次检查
      if (instance == null)  ← instance不为null
      return instance;  ← 返回未初始化的对象！
      
时刻3：线程A继续执行
      2. 初始化对象（但线程B已经拿到了未初始化的对象）
```

**结果**：线程B得到一个未初始化的对象，使用时可能出现NPE或其他错误。

---

**volatile解决方案**：

```java
private static volatile Singleton instance;  // 加volatile
```

**volatile的作用**：

1. **禁止指令重排**：
   ```
   保证执行顺序：
   1. 分配内存
   2. 初始化对象
   3. instance指向内存
   不会重排
   ```

2. **保证可见性**：
   ```
   线程A：instance = new Singleton() 后立即刷新到主内存
   线程B：读取instance时从主内存读取最新值
   ```

3. **建立happens-before关系**：
   ```
   初始化对象 happens-before instance赋值
   instance赋值 happens-before 其他线程读取instance
   保证：其他线程看到instance!=null时，对象一定已初始化
   ```

---

**内存屏障分析**：

```java
instance = new Singleton();

// 展开为：
memory = allocate();        // 1. 分配内存
initInstance(memory);       // 2. 初始化
instance = memory;          // 3. volatile写

// volatile写会插入StoreStore屏障：
initInstance(memory);       // 2
StoreStore屏障  ← 禁止2和3重排
instance = memory;          // 3 (volatile写)
```

---

**完整的安全保证**：

```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        if (instance == null) {  // 第一次检查（快速路径）
            synchronized (Singleton.class) {  // 加锁
                if (instance == null) {  // 第二次检查
                    instance = new Singleton();  // volatile写
                    // 保证：
                    // 1. 对象完全初始化
                    // 2. 其他线程立即可见
                    // 3. 不会重排序
                }
            }
        }
        return instance;
    }
}
```

**总结**：
- 没有volatile：可能返回未初始化对象
- 有了volatile：保证对象完全初始化后才对其他线程可见
- volatile同时保证可见性和有序性，缺一不可

---

### 5. 比较volatile和synchronized的区别，以及各自的适用场景。

**答案**：

**详细对比**：

| 特性 | volatile | synchronized |
|-----|----------|-------------|
| **可见性** | ✓ 保证 | ✓ 保证 |
| **有序性** | ✓ 禁止指令重排 | ✓ 保证 |
| **原子性** | ✗ 不保证 | ✓ 保证 |
| **锁机制** | 无锁 | 加锁（互斥） |
| **性能** | 高（无锁） | 低（加锁/解锁开销） |
| **阻塞** | 不阻塞 | 可能阻塞 |
| **适用范围** | 变量 | 代码块/方法 |
| **修饰对象** | 变量 | 方法/代码块 |
| **重入** | - | 可重入 |

---

**原理对比**：

**volatile原理**：
```
1. 写操作：立即刷新到主内存
2. 读操作：从主内存读取最新值
3. 内存屏障：禁止指令重排
4. 无锁：不会阻塞线程
```

**synchronized原理**：
```
1. 互斥锁：同一时刻只有一个线程进入
2. 内存语义：
   - 解锁前：刷新到主内存
   - 加锁后：从主内存读取
3. 会阻塞等待的线程
```

---

**适用场景**：

**volatile适用场景**：

**1. 状态标志**
```java
public class Server {
    private volatile boolean running = true;
    
    public void start() {
        while (running) {
            handleRequest();
        }
    }
    
    public void stop() {
        running = false;  // 其他线程立即看到
    }
}
```

**2. 双重检查锁**
```java
private static volatile Singleton instance;
```

**3. 一次性安全发布**
```java
public class Holder {
    private volatile Resource resource;
    
    public void initialize() {
        resource = new Resource();  // 安全发布
    }
}
```

**4. 读多写少的状态变量**
```java
public class Config {
    private volatile int maxConnections = 100;
    
    public int getMaxConnections() {
        return maxConnections;  // 读操作多
    }
    
    public void setMaxConnections(int max) {
        maxConnections = max;  // 写操作少
    }
}
```

---

**synchronized适用场景**：

**1. 需要原子性的复合操作**
```java
private int count = 0;

public synchronized void increment() {
    count++;  // 需要原子性
}
```

**2. 保护多个变量的一致性**
```java
public class Account {
    private int balance;
    private int transactions;
    
    public synchronized void transfer(int amount) {
        balance -= amount;      // 两个变量
        transactions++;         // 需要保持一致性
    }
}
```

**3. 复杂的临界区**
```java
public synchronized void complexOperation() {
    // 多个操作
    step1();
    step2();
    step3();
    // 需要作为整体执行
}
```

**4. 等待-通知机制**
```java
public synchronized void waitForCondition() throws InterruptedException {
    while (!condition) {
        wait();
    }
}

public synchronized void notifyCondition() {
    condition = true;
    notifyAll();
}
```

---

**选择建议**：

```
只需要可见性和有序性 → volatile
  - 状态标志
  - 读多写少
  - 简单赋值操作

需要原子性 → synchronized 或 原子类
  - count++
  - check-then-act
  - 复合操作

性能优先 + 不需要原子性 → volatile
性能要求不高 + 需要原子性 → synchronized
高性能 + 需要原子性 → AtomicXXX
```

---

**错误使用示例**：

```java
// ❌ 错误：用volatile保证原子性
private volatile int count = 0;
public void increment() {
    count++;  // 不是原子操作
}

// ✅ 正确：用synchronized或AtomicInteger
private AtomicInteger count = new AtomicInteger(0);
public void increment() {
    count.incrementAndGet();
}
```

```java
// ❌ 错误：用synchronized仅为可见性
public synchronized void setFlag(boolean flag) {
    this.flag = flag;  // 简单赋值，synchronized太重
}

// ✅ 正确：用volatile
private volatile boolean flag;
public void setFlag(boolean flag) {
    this.flag = flag;
}
```

---

**总结**：
- **volatile**：轻量级，只保证可见性和有序性
- **synchronized**：重量级，保证原子性、可见性、有序性
- 选择原则：根据需求选择最轻量级的机制

---

## 五、编程题

### 实现一个使用volatile的线程安全的状态管理类，支持start()和stop()方法。

**答案**：

```java
public class StateManager {
    private volatile boolean running = false;
    
    /**
     * 启动
     */
    public void start() {
        if (!running) {
            running = true;
            System.out.println("状态已启动");
        }
    }
    
    /**
     * 停止
     */
    public void stop() {
        if (running) {
            running = false;
            System.out.println("状态已停止");
        }
    }
    
    /**
     * 判断是否运行中
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * 工作方法
     */
    public void work() {
        new Thread(() -> {
            while (running) {
                System.out.println("正在工作...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("工作结束");
        }).start();
    }
    
    // 测试
    public static void main(String[] args) throws InterruptedException {
        StateManager manager = new StateManager();
        
        manager.start();
        manager.work();
        
        Thread.sleep(5000);
        manager.stop();
    }
}
```

---

💡 **提示**：volatile是轻量级同步机制，适合状态标志等简单场景。记住：不保证原子性！
