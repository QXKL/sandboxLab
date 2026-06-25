# 代理模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 45 分）

### 1. 代理模式的主要目的是什么？

A. 将一个类的接口转换成客户希望的另一个接口  
B. 动态地给一个对象添加额外的职责  
C. 为其他对象提供一种代理以控制对这个对象的访问  
D. 为子系统中的一组接口提供统一的接口

<details>
<summary>查看答案</summary>

**答案**: C

**解析**: 代理模式的核心目的是**控制对对象的访问**，而不是转换接口、增强功能或简化接口。

**关键词**：控制访问

**形象类比**：门卫控制对住户的访问

**与其他模式的区别**：
- **A选项**：适配器模式（转换接口）
- **B选项**：装饰器模式（增强功能）
- **C选项**：✅ 代理模式（控制访问）
- **D选项**：外观模式（简化接口）
</details>

---

### 2. 代理模式和装饰器模式的主要区别是什么？

A. 代理改变接口，装饰器保持接口  
B. 代理控制访问，装饰器增强功能  
C. 代理用于单个对象，装饰器用于多个对象  
D. 两者没有区别，只是名称不同

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**代理模式**：
- **目的**：控制访问
- **关注点**：何时、如何访问对象
- **例子**：门卫（控制访客进入）

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
```

**装饰器模式**：
- **目的**：增强功能
- **关注点**：添加新功能
- **例子**：给咖啡加料（增强味道）

```java
// 装饰器：增强功能
class MilkDecorator implements Coffee {
    private Coffee coffee;
    
    @Override
    public double getPrice() {
        return coffee.getPrice() + 2.0;  // 增强功能（价格增加）
    }
}
```

**对比表格**：

| 对比 | 代理模式 | 装饰器模式 |
|-----|---------|-----------|
| 目的 | 控制访问 | 增强功能 |
| 关注点 | 何时访问 | 添加功能 |
| 例子 | 门卫 | 给咖啡加料 |

**记忆口诀**：
> **代理控访问，装饰加功能。**
</details>

---

### 3. 以下哪个场景最适合使用虚拟代理？

A. 需要权限控制的文档系统  
B. 大图片需要延迟加载  
C. 需要给对象添加日志功能  
D. 需要适配不同的第三方接口

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**B正确** - 大图片延迟加载：

```java
// 虚拟代理：懒加载
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
```

**优势**：
- ✅ 启动快（延迟加载）
- ✅ 节省内存（按需加载）
- ✅ 优化性能

**其他选项**：
- **A**：权限控制 → **保护代理**
- **C**：添加日志 → **装饰器模式**
- **D**：适配接口 → **适配器模式**

**三种代理类型**：

| 代理类型 | 目的 | 典型场景 |
|---------|------|---------|
| **虚拟代理** | 延迟创建 | 大图片加载 ✅ |
| **保护代理** | 权限控制 | 文档权限 |
| **缓存代理** | 性能优化 | 数据库查询 |
</details>

---

## 二、代码分析（每题 20 分，共 40 分）

### 4. 以下代码有什么问题？如何改进？

```java
// 图片接口
interface Image {
    void display();
}

// 真实图片
class RealImage implements Image {
    private String filename;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();  // 构造时加载
    }
    
    private void loadFromDisk() {
        System.out.println("加载图片: " + filename);
        // 耗时2秒
    }
    
    @Override
    public void display() {
        System.out.println("显示图片: " + filename);
    }
}

// 使用
Image img1 = new RealImage("photo1.jpg");  // 2秒
Image img2 = new RealImage("photo2.jpg");  // 2秒
Image img3 = new RealImage("photo3.jpg");  // 2秒
// 启动慢！
```

<details>
<summary>参考答案</summary>

**问题**: 启动时加载所有图片，导致启动慢、内存占用高

**问题分析**：
1. ❌ 启动慢（一次性加载所有图片）
2. ❌ 内存占用高（用不到的图片也加载了）
3. ❌ 用户体验差（等待时间长）

**改进方案**：使用虚拟代理（懒加载）

```java
// 图片接口
interface Image {
    void display();
}

// 真实图片（加载慢）
class RealImage implements Image {
    private String filename;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();  // 耗时操作
    }
    
    private void loadFromDisk() {
        System.out.println("加载图片: " + filename);
        try {
            Thread.sleep(2000);  // 模拟2秒加载
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void display() {
        System.out.println("显示图片: " + filename);
    }
}

// 虚拟代理（懒加载）
class ImageProxy implements Image {
    private String filename;
    private RealImage realImage;  // 延迟创建
    
    public ImageProxy(String filename) {
        this.filename = filename;
        // 不加载图片，只记录文件名（几乎瞬间）
    }
    
    @Override
    public void display() {
        // 需要时才创建真实图片
        if (realImage == null) {
            realImage = new RealImage(filename);  // 懒加载
        }
        realImage.display();
    }
}

// 使用
Image img1 = new ImageProxy("photo1.jpg");  // 几乎瞬间
Image img2 = new ImageProxy("photo2.jpg");  // 几乎瞬间
Image img3 = new ImageProxy("photo3.jpg");  // 几乎瞬间
// 启动快！

// 只有真正显示时才加载
img1.display();  // 此时才加载photo1.jpg（2秒）
```

**优势**：
1. ✅ **启动快**：只创建代理，不加载图片
2. ✅ **节省内存**：按需加载
3. ✅ **用户体验好**：无需等待所有图片加载

**性能对比**：
```
100张大图片的场景：
- 直接加载：200秒（启动时）
- 代理加载：几乎瞬间（启动时） + 2秒 × 实际查看数量
- 性能提升：90%+
```
</details>

---

### 5. 实现权限控制代理

要求：
- 基础接口：`FileService`，有`read()`和`write()`方法
- 真实对象：`RealFileService`
- 代理：`FileServiceProxy`（根据用户角色控制访问）
- 支持两种角色：ADMIN（读写）、USER（只读）

<details>
<summary>参考答案</summary>

```java
// 1. 文件服务接口
interface FileService {
    void read(String filename);
    void write(String filename, String content);
}

// 2. 用户类
class User {
    private String username;
    private String role;  // ADMIN, USER
    
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}

// 3. 真实文件服务
class RealFileService implements FileService {
    @Override
    public void read(String filename) {
        System.out.println("读取文件: " + filename);
    }
    
    @Override
    public void write(String filename, String content) {
        System.out.println("写入文件: " + filename + ", 内容: " + content);
    }
}

// 4. 保护代理（权限控制）
class FileServiceProxy implements FileService {
    private RealFileService realService;
    private User user;
    
    public FileServiceProxy(User user) {
        this.realService = new RealFileService();
        this.user = user;
    }
    
    @Override
    public void read(String filename) {
        // 所有用户都可以读
        System.out.println("[代理] " + user.getUsername() + " 请求读取");
        realService.read(filename);
    }
    
    @Override
    public void write(String filename, String content) {
        System.out.println("[代理] " + user.getUsername() + " 请求写入");
        
        // 只有ADMIN可以写
        if (user.isAdmin()) {
            System.out.println("[代理] 权限检查通过");
            realService.write(filename, content);
        } else {
            System.out.println("[代理] 权限不足，拒绝写入");
        }
    }
}

// 5. 使用
User admin = new User("Alice", "ADMIN");
FileService adminService = new FileServiceProxy(admin);
adminService.read("file.txt");   // 允许
adminService.write("file.txt", "data");  // 允许

User user = new User("Bob", "USER");
FileService userService = new FileServiceProxy(user);
userService.read("file.txt");   // 允许
userService.write("file.txt", "data");  // 拒绝
```

**关键点**：
- ✅ 代理持有真实对象
- ✅ 代理检查权限后才转发调用
- ✅ 真实对象无需关心权限逻辑
- ✅ 透明性：客户端无需知道是代理
</details>

---

## 三、场景判断（15 分）

### 6. 判断以下场景是否适合代理模式

**场景A**: Hibernate的关联对象懒加载  
**场景B**: 给图片添加边框和滤镜  
**场景C**: 数据库连接池管理  
**场景D**: Spring AOP方法拦截

<details>
<summary>参考答案</summary>

### 场景A: Hibernate懒加载
**推荐**: ✅ 适合代理模式（虚拟代理）

**理由**：
- 延迟加载关联对象
- 减少数据库查询
- 优化性能

```java
@Entity
public class User {
    @OneToMany(fetch = FetchType.LAZY)  // 懒加载
    private List<Order> orders;  // 通过代理延迟加载
}
```

---

### 场景B: 添加边框和滤镜
**推荐**: ❌ 不适合代理模式，应该用**装饰器模式**

**理由**：
- 不是控制访问
- 是增强功能
- 装饰器更合适

```java
// 装饰器模式
Image image = new RealImage("photo.jpg");
image = new BorderDecorator(image);
image = new FilterDecorator(image);
```

---

### 场景C: 数据库连接池
**推荐**: ✅ 适合代理模式（保护代理/缓存代理）

**理由**：
- 控制对数据库连接的访问
- 复用连接（类似缓存）
- 管理连接生命周期

---

### 场景D: Spring AOP
**推荐**: ✅ 适合代理模式（动态代理）

**理由**：
- 方法拦截（控制访问）
- 添加横切关注点
- JDK动态代理/CGLIB代理

```java
@Aspect
public class LoggingAspect {
    @Before("execution(* com.example.*.*(..))")
    public void logBefore() {
        // 代理拦截方法调用
    }
}
```

---

### 总结

**适合代理模式**：
- ✅ 场景A：Hibernate懒加载（虚拟代理）
- ✅ 场景C：连接池管理（保护代理）
- ✅ 场景D：Spring AOP（动态代理）

**不适合代理模式**：
- ❌ 场景B：添加边框滤镜（用装饰器模式）

**判断标准**：
```
是否需要控制访问？
  ├─ 是 → ✅ 代理模式
  └─ 否
      ├─ 增强功能？ → 装饰器模式
      └─ 转换接口？ → 适配器模式
```
</details>

---

## 四、评分标准

### 满分答案特征
- ✅ 理解代理模式的本质（控制访问）
- ✅ 掌握代理 vs 装饰器 vs 适配器的区别
- ✅ 掌握三种代理类型（虚拟、保护、缓存）
- ✅ 能手写代理代码
- ✅ 能准确判断使用场景

### 常见扣分点
- ❌ 混淆代理和装饰器
- ❌ 不理解懒加载机制
- ❌ 无法识别控制访问场景
- ❌ 代理实现不完整
- ❌ 场景判断错误

---

## 核心要点回顾

### 代理模式三要素
1. **Subject**：抽象主题接口
2. **RealSubject**：真实对象
3. **Proxy**：代理（持有RealSubject，控制访问）

### 三种代理类型

| 代理类型 | 目的 | 典型场景 |
|---------|------|---------|
| **虚拟代理** | 延迟创建 | 图片懒加载 |
| **保护代理** | 权限控制 | 文档权限 |
| **缓存代理** | 性能优化 | 数据库查询 |

### 代理 vs 其他模式

| 模式 | 目的 | 接口 | 例子 |
|-----|------|------|------|
| **代理** | 控制访问 | 保持不变 | 门卫 |
| **装饰器** | 增强功能 | 保持不变 | 给咖啡加料 |
| **适配器** | 转换接口 | 改变 | 插座转换器 |

### 记忆口诀
> **门卫控制访问权，**  
> **代理模式解难题，**  
> **懒加载权限缓存，**  
> **透明控制是关键。**

---

**完成自测后**，填写 `note_template.md` 巩固知识！
