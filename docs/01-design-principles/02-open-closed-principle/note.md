~~虽然我不是很想写md格式啦~~

这里解答一些疑惑：

## 为什么能够动态添加？
比如说Loggers接口，存储着List<LoggerAPI>，再结合接口，就可以做到动态添加和删除

如果代码在写完之后，发送日志的目标就固定不变了，这就是静态的。比如：
```java
class MySystem {
    // 写死了只能用 ConsoleLogger 和 FileLogger
    private ConsoleLogger console = new ConsoleLogger();
    private FileLogger file = new FileLogger();
    
    public void log(String msg) {
        console.log(msg);
        file.log(msg);
    }
}
```
在这种情况下，程序一旦跑起来，日志就只能发往控制台和文件。
如果某天线上出了问题，想临时加一个 DatabaseLogger，必须修改这段代码，重新编译，重新打包，重新部署。这在生产环境中是非常麻烦且有风险的。

回看List<LoggerAPI> loggers，它是一个动态的集合，可以随时添加新的 LoggerAPI，而不需要修改已有的代码。这就是**对扩展开放**。
```java
class Loggers {
    private List<LoggerAPI> loggers = new ArrayList<>();

    public void addLogger(LoggerAPI loggerAPI) {
        loggers.add(loggerAPI);
    }
    
    // ...
}
```


假设你正在开发一个 Web 服务器：

1. 平时运行：你只添加了 ConsoleLogger，为了节省性能，不写硬盘不连数据库。
```java
Loggers loggers = new Loggers();
loggers.addLogger(new ConsoleLogger());
```

2. 突发情况：服务器突然出现偶发 Bug，你需要详细追踪日志。你不需要停机重新写代码，而是可以通过一个 **管理员接口（API）或配置中心（如 Nacos/Apollo）** 的热更新功能，在运行时发送一个指令：
```java
// 程序接收到指令，在运行时动态添加了文件日志
loggers.addLogger(new FileLogger());
```

3. 危机解除：Bug 修好了，为了不让硬盘被日志撑满，你再次通过接口发送指令：
```java
// 程序在运行时动态移除
loggers.removeLogger(new FileLogger());
```

上面就是实现了 **对修改关闭，对扩展开放** 的原则，在对象的层次上。

## 我还想进一步，将这个原则扩展到类的层次上怎么办？

答案是：**取决于你的代码架构，如果只是普通的代码，依然需要重新打包；但如果你引入了“插件化”或“SPI”机制，就可以做到完全不重新打包，在运行时动态添加！**

我们来详细剖析一下：

### 1. 为什么当前代码依然需要重新打包？
在你目前的代码中，`ConsoleLogger`、`FileLogger` 等类都是写死在当前项目里的。虽然你可以通过 `addLogger()` 动态往 `Loggers` 列表里塞对象，但**前提是你能 `new` 出这个对象**。

如果第五个日志类（比如 `KafkaLogger`）是你下周才写出来的，当前打包运行的程序里根本没有 `KafkaLogger.class` 这个文件，JVM 在运行时自然找不到这个类，你也无法 `new` 出它的实例。

所以，如果新增的类写在当前项目里，你依然需要修改代码、重新编译打包、重启服务。

### 2. 如何做到真正的“不重新打包、热插拔”？
要想在程序运行中，不重启、不重新打包当前项目就能加入 `KafkaLogger`，你需要利用 Java 的**动态加载机制**，将你的日志系统做成**插件化架构**。

核心思想是：**主程序只依赖 `LoggerAPI` 接口，第五、第六个日志类作为独立的 Jar 包（插件）放在外面，主程序在运行时去读取并加载这些外部的类。**

以下是实现这种高级动态添加的步骤思路：

#### 第一步：将接口抽离为独立模块
把 `LoggerAPI` 接口打包成一个独立的 Jar 包（比如 `logger-api.jar`）。主程序和未来的日志插件都只依赖这个接口。

#### 第二步：主程序提供动态加载外部类的能力
在主程序中，使用 Java 的 `URLClassLoader` 或者 Java SPI（Service Provider Interface）机制，去扫描指定的插件目录（比如 `./plugins` 文件夹），把里面的 Jar 包加载进 JVM。

修改你的 `Loggers` 类，增加加载外部插件的逻辑：

代码位置：[en.java](src/main/java/com/qx/test/en.java#L80-L84)
```java
class Loggers {
    private List<LoggerAPI> loggers = new ArrayList<>();

    public void addLogger(LoggerAPI loggerAPI) {
        loggers.add(loggerAPI);
    }

    // 新增：通过反射动态加载外部的 Jar 包并实例化
    public void loadPlugin(String jarPath, String className) {
        try {
            File jarFile = new File(jarPath);
            URL url = jarFile.toURI().toURL();
            // 使用自定义 ClassLoader 加载外部 Jar
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
            
            // 动态加载类并实例化（前提是该类实现了 LoggerAPI）
            Class<?> clazz = classLoader.loadClass(className);
            LoggerAPI logger = (LoggerAPI) clazz.getDeclaredConstructor().newInstance();
            
            // 动态添加到现有日志系统中！
            addLogger(logger);
            System.out.println("插件 " + className + " 加载成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logAll(String message) {
        for (LoggerAPI loggerAPI : loggers) {
            loggerAPI.log(message);
        }
    }
}
```

#### 第三步：开发第五个日志（作为独立插件）
几周后，你需要加一个 `KafkaLogger`。你不需要修改原来的项目，而是新建一个项目，引入 `logger-api.jar`，然后实现接口：

```java
// 这是一个独立的项目，打包成 kafka-logger-plugin.jar
package com.qx.plugin;

public class KafkaLogger implements LoggerAPI {
    @Override
    public void log(String message) {
        System.out.println("Kafka: " + message);
        // 真实的 Kafka 发送逻辑
    }
}
```

#### 第四步：运行时热插拔
你的主程序已经在服务器上运行了，**完全不需要停机重新打包**。你只需要：
1. 把 `kafka-logger-plugin.jar` 扔到服务器的 `./plugins` 目录下。
2. 通过主程序提供的 API 接口、或者管理控制台，触发刚才写的 `loadPlugin` 方法：
   ```java
   loggers.loadPlugin("./plugins/kafka-logger-plugin.jar", "com.qx.plugin.KafkaLogger");
   ```
3. 瞬间，`KafkaLogger` 就被加载进 JVM，并添加到了 `loggers` 列表中，后续的日志就会自动发送到 Kafka！

### 总结
- 目前的 `addLogger` 实现了**对象级别**的动态（程序运行状态可变）。
- 要实现**类级别**的动态（不重启加新功能），需要结合 Java 的**类加载机制（ClassLoader / SPI）**实现插件化。
- 业界大量知名的开源项目（如 Tomcat、ElasticSearch、IntelliJ IDEA）都是通过这种机制来实现极其灵活的动态扩展的。


















## 我还是不理解。能给我讲一下流程和代码吗？
well

好吧。我们先讲一个示例：

现在用户屏幕上有多个支付方式：微信支付、支付宝支付、银联支付......
用户选择了微信支付，如果是接口直接调用微信支付的实现，你应该还能理解。

但是现在是调用PaymentService，就不能理解了，是怎么选到微信的？这不可能避开if-else吧？

对的对的，你的直觉是对的。if-else是从逻辑上就不能避开的。
先说上面的示例，你可以通过在controller层通过前端传回来的type参数路由到对应的分支，创建对应的对象实例(抽象就在这里)，然后通过接口使用支付功能。这里面还是if-else，只不过移到了更高层。

PaymentService 并不是凭空猜到用户要用微信的。
而是系统在调用 PaymentService 的业务方法之前，根据用户的点击动作，提前把微信支付的对象赋值给了 PaymentService 内部的接口变量。这就是面向对象中“多态”的精髓——运行时动态绑定。

在现代 Java 开发中，我们很少手动写上面的 if-else 来设置状态，而是用 Spring 框架。Spring 把这个“选择并注入”的过程自动化了。
```java
@RestController
public class PayController {
    
    // Spring 会自动把所有支付实现放进一个 Map 里，Key 是 Bean 的名字
    @Autowired
    private Map<String, PaymentAPI> payMap; 

    public Result pay(String payType, int amount) {
        // 从 Map 里根据用户的选择，拿出对应的实现(获取Bean对象)
        PaymentAPI paymentAPI = payMap.get(payType + "Pay");
        
        PaymentService service = new PaymentService();
        service.setPaymentAPI(paymentAPI); // 动态设置状态
        service.processPayment(amount);
    }
}
```

## 啊？？？那这不还是if-else，只不过拿到更高层了而已？

**我们真的消灭了 if-else 吗？**

答案是：**没有。业务逻辑的分支是客观存在的，if-else 是不可能被凭空消灭的。**

我们做的所有设计模式、所有重构，**本质上就是你说的：把 if-else 转移到了更高层，或者更边缘的地方。**

但千万不要觉得“只是挪了个位置”就没意义，这种转移带来了巨大的架构收益。我们来对比一下“低层 if-else”和“高层 if-else”的区别：

### 1. 以前：if-else 在底层（违反开闭原则）

假设没有 `PaymentService`，你的订单核心逻辑直接写死了支付方式：

```java
class OrderService {
    public void processOrder(Order order, String payType) {
        // 核心业务逻辑：计算金额、扣库存...
        int amount = calculateAmount(order);
        
        // 臃肿的 if-else 埋藏在核心逻辑中
        if (payType.equals("weixin")) {
            WeiXinPay wx = new WeiXinPay();
            wx.pay(amount);
        } else if (payType.equals("alipay")) {
            AliPay ali = new AliPay();
            ali.pay(amount);
        }
        // 后续业务逻辑：修改订单状态、发短信...
    }
}
```
**痛点：** 如果要加一个“银联支付”，你必须修改 `OrderService` 的源代码，在核心业务逻辑里动刀子。这违反了开闭原则，极易引发 Bug，而且这段代码无法被复用。

### 2. 现在：if-else 在高层/边缘（符合开闭原则）

按照现在的设计，代码变成了两部分：

**底层（纯净的业务核心）：**
```java
class PaymentService {
    private PaymentAPI paymentAPI;
    // 这里没有任何 if-else，只负责纯粹的支付动作
    public void processPayment(int amount) {
        paymentAPI.pay(amount);
    }
}
```

**高层/边缘（负责组装和路由的控制器）：**
```java
class PayController {
    public Result pay(String payType, int amount) {
        PaymentService service = new PaymentService();
        
        // if-else 被赶到了最外层的入口处
        if (payType.equals("weixin")) {
            service.setPaymentAPI(new WeiXinPay());
        } else if (payType.equals("alipay")) {
            service.setPaymentAPI(new AliPay());
        }
        
        service.processPayment(amount);
    }
}
```

### 为什么把 if-else 赶到高层，就是胜利？

**1. 保护核心逻辑（单一职责）**
`OrderService` 或 `PaymentService` 变得极其干净，它们只关心“怎么完成支付动作”，不再关心“当前是哪种支付”。核心逻辑变得稳定、可复用、易于测试。

**2. 变更的影响范围变小（高内聚）**
当你要增加“银联支付”时，你不需要去改底层的 `PaymentService`，你只需要在 `PayController` 这个入口处加一个 `else if`。这就好比**把脏活累活都集中在门口，屋里保持干净**。

**3. 为彻底消除 if-else 提供了可能（策略模式+工厂模式）**
当 if-else 被提到最外层后，我们就有办法用更优雅的机制替换掉它。比如利用 Java 的反射或 Spring 的依赖注入，我们可以把 `PayController` 里的 if-else 也干掉：

```java
class PayController {
    // Spring 会自动把所有实现了 PaymentAPI 的类放进这个 Map，Key 是 Bean 的名字
    @Autowired
    private Map<String, PaymentAPI> payMap; 

    public Result pay(String payType, int amount) {
        PaymentService service = new PaymentService();
        
        // 没有if-else了！直接根据用户传来的类型，从Map里拿对应的实现
        PaymentAPI paymentAPI = payMap.get(payType + "Pay"); 
        
        service.setPaymentAPI(paymentAPI);
        service.processPayment(amount);
    }
}
```
在这个阶段，**连 Controller 层的 if-else 都不见了**。新增“银联支付”时，只需要写一个 `UnionPay` 类，加上 Spring 注解，完全不需要修改任何已有的代码（真正的开闭原则）。

### 总结

设计模式不是魔法，它不能改变业务有多个分支的客观事实。**架构设计的本质，就是要把“容易变化的逻辑（if-else）”和“核心稳定的逻辑”隔离开来**。

把 if-else 从底层赶到高层，再从高层用查表法（Map/反射）消灭掉，这就是代码从“能跑”走向“优雅”的必经之路。


## OCP 和 SRP 的区别和联系？
|    | SRP                      | OCP                              |
|----|--------------------------|----------------------------------|
| 定义 | 单一职责原则：一个类应该只有一个引起它变化的原因 | 开闭原则：软件实体（类、模块、函数等）应该对扩展开放，对修改关闭 |
