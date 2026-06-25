# 代理模式 (Proxy Pattern)

> 为其他对象提供一个代理以控制对这个对象的访问

---

## 一、生活中的例子

### 🚪 小区门卫

想象你住在一个小区：

```
访客 → 门卫 → 住户
      (代理)
```

**门卫做了什么？**
- ✅ 检查访客身份（权限控制）
- ✅ 登记访客信息（日志记录）
- ✅ 通知住户（访问控制）
- ✅ 拒绝可疑人员（安全保护）

**门卫是代理**：
- 不直接见住户，先经过门卫
- 门卫控制对住户的访问
- 门卫和住户提供相同的接口（都能"联系住户"）

---

### 🎬 明星经纪人

```
粉丝/广告商 → 经纪人 → 明星
             (代理)
```

**经纪人做了什么？**
- ✅ 筛选合作请求（保护代理）
- ✅ 安排日程（访问控制）
- ✅ 谈判价格（业务处理）
- ✅ 保护隐私（安全保护）

**经纪人是代理**：
- 外界不能直接接触明星
- 经纪人控制对明星的访问
- 必要时才让明星出面

---

### 🛒 海外代购

```
买家 → 代购 → 海外商品
      (代理)
```

**代购做了什么？**
- ✅ 代替买家购买（远程代理）
- ✅ 处理物流（访问控制）
- ✅ 承担风险（保护作用）

---

## 二、为什么需要代理模式？

### 问题场景1：图片加载

**直接加载大图片的问题**：

```java
// ❌ 问题：启动时加载所有大图片
class ImageViewer {
    public void showImages() {
        Image img1 = new RealImage("photo1.jpg");  // 10MB，很慢
        Image img2 = new RealImage("photo2.jpg");  // 15MB，很慢
        Image img3 = new RealImage("photo3.jpg");  // 20MB，很慢
        // 启动慢，内存占用高
    }
}
```

**问题**：
- ❌ 启动慢（一次性加载所有图片）
- ❌ 内存占用高（用不到的图片也加载了）
- ❌ 用户体验差（等待时间长）

---

**代理模式解决（懒加载）**：

```java
// ✅ 解决：需要时才加载
class ImageViewer {
    public void showImages() {
        Image img1 = new ImageProxy("photo1.jpg");  // 快速创建代理
        Image img2 = new ImageProxy("photo2.jpg");  // 快速创建代理
        Image img3 = new ImageProxy("photo3.jpg");  // 快速创建代理
        
        // 只有真正显示时才加载
        img1.display();  // 此时才加载photo1.jpg
    }
}
```

**优势**：
- ✅ 启动快（只创建代理，不加载图片）
- ✅ 按需加载（用到时才加载）
- ✅ 节省内存（不用的图片不加载）

---

### 问题场景2：文档访问控制

**直接访问的问题**：

```java
// ❌ 问题：任何人都能访问敏感文档
class DocumentSystem {
    public void accessDocument(String filename) {
        Document doc = new RealDocument(filename);
        doc.view();  // 任何人都能查看
        doc.edit();  // 任何人都能编辑
    }
}
```

**问题**：
- ❌ 没有权限控制
- ❌ 敏感信息泄露
- ❌ 数据被误删除

---

**代理模式解决（权限控制）**：

```java
// ✅ 解决：代理控制访问权限
class DocumentSystem {
    public void accessDocument(String filename, User user) {
        Document doc = new DocumentProxy(filename, user);
        
        if (user.hasReadPermission()) {
            doc.view();  // 代理检查权限后才允许查看
        }
        
        if (user.hasWritePermission()) {
            doc.edit();  // 代理检查权限后才允许编辑
        }
    }
}
```

**优势**：
- ✅ 权限控制（检查用户权限）
- ✅ 安全保护（防止未授权访问）
- ✅ 日志记录（记录访问行为）

---

## 三、代理模式的核心思想

### 定义

**代理模式（Proxy Pattern）**：为其他对象提供一种代理以控制对这个对象的访问。

### 核心要点

| 要点 | 说明 |
|-----|------|
| **控制访问** | 代理控制对真实对象的访问 |
| **保持接口** | 代理和真实对象实现相同接口 |
| **透明性** | 客户端无需知道是代理还是真实对象 |
| **延迟创建** | 可以延迟创建真实对象（懒加载） |

---

### 形象理解

```
代理就像"中介"：

客户端 → 代理（中介） → 真实对象
         ↓
       控制访问
       - 权限检查
       - 懒加载
       - 缓存
       - 日志
```

**关键点**：
- 客户端不直接访问真实对象
- 代理控制何时、如何访问真实对象
- 代理和真实对象对客户端透明

---

## 四、UML结构与角色

### UML类图

```
┌──────────────┐
│    Client    │ 使用
└──────┬───────┘
       │
       │ 依赖
       ↓
┌──────────────┐
│   Subject    │ 抽象主题
│  (interface) │
├──────────────┤
│ + request()  │
└──────▲───────┘
       │
    ┌──┴──┐
    │     │
┌───┴──┐ ┌┴──────────┐
│Real  │ │   Proxy   │ 代理
│Subject│ ├───────────┤
└──────┘ │- realSubject│ 持有真实对象
         ├───────────┤
         │+ request()│ 控制访问
         └───────────┘
```

---

### 角色说明

| 角色 | 职责 | 类比 |
|-----|------|------|
| **Subject** | 定义接口 | "访问住户"接口 |
| **RealSubject** | 真实对象，实际工作 | 住户 |
| **Proxy** | 代理对象，控制访问 | 门卫 |
| **Client** | 使用者 | 访客 |

---

### 关键设计

**1. Proxy持有RealSubject**
```java
class Proxy implements Subject {
    private RealSubject realSubject;  // 持有真实对象
    
    @Override
    public void request() {
        // 访问控制逻辑
        if (checkAccess()) {
            realSubject.request();  // 转发给真实对象
        }
    }
}
```

**2. 透明性**
```java
// 客户端无需知道是代理还是真实对象
Subject subject = new Proxy();
subject.request();  // 调用方式相同
```

**3. 懒加载**
```java
class Proxy implements Subject {
    private RealSubject realSubject;
    
    @Override
    public void request() {
        if (realSubject == null) {
            realSubject = new RealSubject();  // 需要时才创建
        }
        realSubject.request();
    }
}
```

---

## 五、三种代理类型详解

### 1. 虚拟代理（Virtual Proxy）

**目的**：延迟创建开销大的对象（懒加载）

**场景**：
- 大图片加载
- 大文件加载
- 数据库连接

**示例**：
```java
// 虚拟代理：图片懒加载
class ImageProxy implements Image {
    private String filename;
    private RealImage realImage;  // 真实图片（延迟创建）
    
    public ImageProxy(String filename) {
        this.filename = filename;
        System.out.println("代理创建（轻量）");
    }
    
    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);  // 需要时才加载
        }
        realImage.display();
    }
}
```

**优势**：
- ✅ 启动快（延迟加载）
- ✅ 节省内存（按需加载）
- ✅ 优化性能（避免不必要的加载）

---

### 2. 保护代理（Protection Proxy）

**目的**：控制对对象的访问权限

**场景**：
- 权限控制
- 访问日志
- 安全检查

**示例**：
```java
// 保护代理：文档权限控制
class DocumentProxy implements Document {
    private RealDocument realDocument;
    private User user;
    
    public DocumentProxy(String filename, User user) {
        this.realDocument = new RealDocument(filename);
        this.user = user;
    }
    
    @Override
    public void view() {
        if (user.hasReadPermission()) {
            realDocument.view();  // 有权限才允许查看
        } else {
            System.out.println("无权限查看");
        }
    }
    
    @Override
    public void edit() {
        if (user.hasWritePermission()) {
            realDocument.edit();  // 有权限才允许编辑
        } else {
            System.out.println("无权限编辑");
        }
    }
}
```

**优势**：
- ✅ 权限控制（安全）
- ✅ 访问日志（审计）
- ✅ 保护敏感数据

---

### 3. 远程代理（Remote Proxy）

**目的**：为远程对象提供本地代表

**场景**：
- RMI（远程方法调用）
- Web服务调用
- 分布式系统

**示例**：
```java
// 远程代理：访问远程服务
class RemoteServiceProxy implements Service {
    private String serverUrl;
    
    public RemoteServiceProxy(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    @Override
    public String getData() {
        // 通过网络访问远程服务
        return httpGet(serverUrl + "/data");
    }
    
    private String httpGet(String url) {
        // 网络请求逻辑
        return "remote data";
    }
}
```

**优势**：
- ✅ 隐藏网络细节
- ✅ 本地对象的使用方式
- ✅ 透明的远程访问

---

### 三种代理对比

| 代理类型 | 目的 | 典型场景 | 例子 |
|---------|------|---------|------|
| **虚拟代理** | 延迟创建 | 大对象加载 | 图片懒加载 |
| **保护代理** | 权限控制 | 访问控制 | 文档权限 |
| **远程代理** | 远程访问 | 分布式系统 | RMI |

---

## 六、代码示例讲解

### 示例1：虚拟代理 - 图片懒加载

**场景**：图片查看器，大图片延迟加载

**核心设计**：
```java
// 抽象主题
interface Image {
    void display();
}

// 真实对象：大图片（创建慢）
class RealImage implements Image {
    private String filename;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();  // 耗时操作
    }
    
    private void loadFromDisk() {
        System.out.println("加载图片: " + filename);
        // 模拟耗时操作
        sleep(2000);
    }
    
    @Override
    public void display() {
        System.out.println("显示图片: " + filename);
    }
}

// 代理：延迟加载
class ImageProxy implements Image {
    private String filename;
    private RealImage realImage;  // 延迟创建
    
    public ImageProxy(String filename) {
        this.filename = filename;
        // 不加载图片，只记录文件名
    }
    
    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);  // 需要时才加载
        }
        realImage.display();
    }
}

// 使用
Image img1 = new ImageProxy("photo1.jpg");  // 快速创建
Image img2 = new ImageProxy("photo2.jpg");  // 快速创建
// 此时图片还没加载

img1.display();  // 此时才加载photo1.jpg
```

---

### 示例2：保护代理 - 文档权限控制

**场景**：文档管理系统，根据用户角色控制访问

**详细内容请查看 `demo/` 目录**

---

### 示例3：缓存代理 - 数据库查询缓存

**场景**：数据库查询代理，缓存查询结果

**详细内容请查看 `demo/` 目录**

---

## 七、代理 vs 装饰器 vs 适配器

### 三者对比

| 模式 | 目的 | 接口 | 关注点 | 例子 |
|-----|------|------|--------|------|
| **代理** | 控制访问 | 保持不变 | 访问控制 | 门卫 |
| **装饰器** | 增强功能 | 保持不变 | 功能增强 | 给咖啡加料 |
| **适配器** | 转换接口 | 改变 | 接口转换 | 插座转换器 |

---

### 详细对比

**1. 代理 vs 装饰器**

```java
// 代理：控制访问
class ImageProxy implements Image {
    private RealImage realImage;
    
    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage();  // 懒加载（控制创建时机）
        }
        realImage.display();
    }
}

// 装饰器：增强功能
class BorderDecorator implements Image {
    private Image image;
    
    @Override
    public void display() {
        image.display();
        addBorder();  // 增强功能（添加边框）
    }
}
```

**区别**：
- **代理**：控制何时、如何访问（懒加载、权限检查）
- **装饰器**：添加额外功能（边框、滚动条）

---

**2. 代理 vs 适配器**

```java
// 代理：接口相同，控制访问
class Proxy implements Subject {
    private RealSubject subject;
    // 控制访问
}

// 适配器：接口不同，做转换
class Adapter implements NewInterface {
    private OldClass old;
    // 转换接口
}
```

**区别**：
- **代理**：接口相同，控制访问
- **适配器**：接口不同，做转换

---

### 记忆口诀

> **代理控访问，**  
> **装饰加功能，**  
> **适配转接口，**  
> **三者要分清。**

---

## 八、使用场景

### ✅ 适合代理模式的场景

**1. 虚拟代理：延迟加载**
```
大对象、昂贵资源的懒加载：
- 大图片、大文件
- 数据库连接
- 网络资源
```

**2. 保护代理：权限控制**
```
需要访问控制的场景：
- 文档权限管理
- API访问控制
- 敏感数据保护
```

**3. 缓存代理：性能优化**
```
需要缓存结果的场景：
- 数据库查询缓存
- HTTP请求缓存
- 计算结果缓存
```

**4. 日志代理：监控审计**
```
需要记录访问的场景：
- 方法调用日志
- 性能监控
- 审计追踪
```

**5. 远程代理：分布式系统**
```
访问远程对象的场景：
- RMI远程调用
- Web服务
- 微服务通信
```

---

### ❌ 不适合代理模式的场景

**1. 需要增强功能**
```
添加新功能 → 装饰器模式
```

**2. 需要转换接口**
```
接口不兼容 → 适配器模式
```

**3. 简单的转发**
```
没有控制逻辑 → 不需要代理
```

---

## 九、优缺点分析

### 优点

| 优点 | 说明 | 例子 |
|-----|------|------|
| ✅ **职责清晰** | 代理负责访问控制，真实对象负责业务 | 门卫 + 住户 |
| ✅ **高扩展性** | 可以在不修改真实对象的情况下扩展功能 | 添加日志代理 |
| ✅ **符合开闭原则** | 新增代理不修改现有代码 | 新增缓存代理 |
| ✅ **智能化** | 可以在访问时做额外处理 | 懒加载、缓存 |

---

### 缺点

| 缺点 | 说明 | 解决方案 |
|-----|------|---------|
| ❌ **增加复杂度** | 多一层代理增加理解难度 | 必要时才用 |
| ❌ **性能开销** | 多一层调用可能影响性能 | 缓存代理优化 |
| ❌ **响应时间延长** | 代理的处理逻辑增加响应时间 | 优化代理逻辑 |

---

## 十、注意事项与常见误区

### 陷阱1：混淆代理和装饰器

```java
// ❌ 误区：把装饰器当成代理
class LoggingProxy implements Service {
    private Service service;
    
    @Override
    public void execute() {
        log("开始执行");      // 这是增强功能，像装饰器
        service.execute();
        log("执行完成");      // 这是增强功能，像装饰器
    }
}

// ✅ 正确：代理应该控制访问
class AccessProxy implements Service {
    private Service service;
    
    @Override
    public void execute() {
        if (checkPermission()) {  // 控制访问
            service.execute();
        } else {
            throw new SecurityException("无权限");
        }
    }
}
```

**区别**：
- **代理**：控制是否访问、何时访问
- **装饰器**：添加额外功能

---

### 陷阱2：过度代理

```java
// ❌ 不好：多层代理
Image img = new RealImage("photo.jpg");
img = new CacheProxy(img);
img = new LoggingProxy(img);
img = new PermissionProxy(img);
img = new MetricsProxy(img);
// 太多层了！
```

**解决**：
- 合并相似职责的代理
- 考虑使用AOP

---

### 陷阱3：代理没有真正控制访问

```java
// ❌ 不好：只是简单转发
class SimpleProxy implements Service {
    private Service service;
    
    @Override
    public void execute() {
        service.execute();  // 没有任何控制逻辑
    }
}

// 这不是代理，只是无用的中间层
```

**正确**：代理必须有控制逻辑（权限检查、懒加载、缓存等）

---

## 十一、真实应用案例

### 1. JDK动态代理

**场景**：Java反射API中的代理

```java
// 创建代理对象
InvocationHandler handler = new MyInvocationHandler(realObject);
Object proxy = Proxy.newProxyInstance(
    classLoader,
    interfaces,
    handler
);

// 调用代理方法
proxy.someMethod();  // 实际调用handler.invoke()
```

**应用**：
- Spring AOP
- MyBatis Mapper接口
- RPC框架

---

### 2. Spring AOP

**场景**：面向切面编程，方法拦截

```java
@Aspect
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("调用方法: " + joinPoint.getSignature());
    }
}
```

**本质**：动态代理
- JDK动态代理（接口）
- CGLIB代理（类）

---

### 3. Hibernate懒加载

**场景**：延迟加载关联对象

```java
@Entity
public class User {
    @OneToMany(fetch = FetchType.LAZY)  // 懒加载
    private List<Order> orders;
}

// 使用
User user = session.get(User.class, 1);  // 只加载User
user.getOrders();  // 此时才加载orders（通过代理）
```

**实现**：虚拟代理

---

### 4. MyBatis Mapper代理

**场景**：Mapper接口的实现

```java
// 定义接口
interface UserMapper {
    User findById(int id);
}

// MyBatis自动生成代理实现
UserMapper mapper = sqlSession.getMapper(UserMapper.class);
User user = mapper.findById(1);  // 代理拦截，执行SQL
```

**实现**：JDK动态代理

---

### 5. 缓存代理

**场景**：Spring Cache

```java
@Cacheable("users")
public User getUserById(int id) {
    // 查询数据库
}
```

**实现**：
- Spring通过代理拦截方法
- 先查缓存，命中则返回
- 未命中则执行方法，缓存结果

---

## 十二、动态代理 vs 静态代理

### 静态代理

**定义**：手动编写代理类

```java
class StaticProxy implements Subject {
    private RealSubject subject;
    
    @Override
    public void request() {
        // 手动编写控制逻辑
        subject.request();
    }
}
```

**优点**：
- ✅ 简单直接
- ✅ 类型安全

**缺点**：
- ❌ 每个接口都要写一个代理类
- ❌ 代码冗余

---

### 动态代理

**定义**：运行时动态生成代理类

```java
Object proxy = Proxy.newProxyInstance(
    classLoader,
    interfaces,
    handler
);
```

**优点**：
- ✅ 灵活（一个Handler处理多个接口）
- ✅ 减少代码

**缺点**：
- ❌ 复杂（需要理解反射）
- ❌ 性能略低（反射调用）

---

## 十三、总结

### 核心要点

| 要点 | 内容 |
|-----|------|
| **定义** | 为其他对象提供代理以控制访问 |
| **目的** | 控制对对象的访问 |
| **关键设计** | 代理持有真实对象，实现相同接口 |
| **三种类型** | 虚拟代理、保护代理、远程代理 |
| **典型应用** | Spring AOP、Hibernate懒加载、MyBatis |

---

### 何时使用？

**判断标准**：
```
需要控制对象访问？
  ├─ 延迟创建？ → 虚拟代理
  ├─ 权限控制？ → 保护代理
  ├─ 远程访问？ → 远程代理
  ├─ 缓存结果？ → 缓存代理
  └─ 增强功能？ → 装饰器模式（不是代理）
```

---

### 与其他模式的关系

**代理 + 装饰器**：
```java
// 可以组合使用
Image img = new RealImage("photo.jpg");
img = new CacheProxy(img);          // 代理：缓存
img = new BorderDecorator(img);     // 装饰器：边框
```

**代理 + 工厂**：
```java
// 工厂创建代理
Subject subject = ProxyFactory.createProxy(realSubject);
```

---

### 记忆口诀

> **门卫控制访问权，**  
> **代理模式解难题，**  
> **懒加载权限缓存，**  
> **透明控制是关键。**

---

### 代理 vs 装饰器 vs 适配器

> **代理控访问，**  
> **装饰加功能，**  
> **适配转接口，**  
> **接口要相同。**

---

## 下一步

完成代理模式学习后：
1. ✅ 运行 `demo/` 目录下的代码示例
2. ✅ 完成 `test_01.md` 的自测题
3. ✅ 填写 `note_template.md` 巩固知识
4. ✅ 思考：Spring AOP是如何实现的？

**继续学习**：下一个结构型模式 → **外观模式**

---

**💡 记住**：代理模式控制访问，不是增强功能。就像门卫控制访客，不是给访客加东西。
