# 建造者模式 - 代码示例

## 示例说明

本目录包含建造者模式的三种典型实现：

1. **BasicBuilderDemo.java** - 经典建造者模式（含Director）
2. **ChainBuilderDemo.java** - 链式建造者模式（最常用）
3. **DirectorBuilderDemo.java** - Director模式应用

---

## 运行方式

### 前提条件
- JDK 8 或更高版本

### 编译和运行

```bash
# 进入demo目录
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\03-builder-pattern\demo

# 编译所有文件
javac BasicBuilderDemo.java
javac ChainBuilderDemo.java
javac DirectorBuilderDemo.java

# 运行示例
java BasicBuilderDemo
java ChainBuilderDemo
java DirectorBuilderDemo
```

---

## 示例1：BasicBuilderDemo.java

### 场景
组装电脑系统（办公电脑、游戏电脑、设计师电脑）

### 演示内容
1. **经典GoF结构**：Builder接口 + ConcreteBuilder + Director
2. **使用Director构建预定义配置**：封装构建步骤
3. **客户端自定义配置**：灵活性展示
4. **Director的作用对比**：有Director vs 无Director

### 核心代码
```java
// 抽象建造者
interface ComputerBuilder {
    ComputerBuilder buildCPU(String cpu);
    ComputerBuilder buildMemory(String memory);
    // ... 其他方法
    Computer build();
}

// 导演类
class ComputerDirector {
    public Computer constructGamingComputer(ComputerBuilder builder) {
        return builder
            .buildCPU("Intel i9-13900K")
            .buildMemory("32GB DDR5")
            .buildStorage("2TB NVMe SSD")
            .buildGPU("RTX 4090")
            .build();
    }
}

// 使用
ComputerBuilder builder = new GamingComputerBuilder();
Computer pc = director.constructGamingComputer(builder);
```

### 关键点
- **Builder接口**：定义构建步骤
- **ConcreteBuilder**：实现具体构建逻辑
- **Director**：封装构建流程，提供预定义配置
- **链式调用**：每个方法返回this

---

## 示例2：ChainBuilderDemo.java

### 场景
- SQL查询构建器
- User对象构建（不可变对象）

### 演示内容
1. **SQL构建器**：流式API设计
2. **静态内部类Builder**：Effective Java推荐模式
3. **不可变对象**：所有字段final，无setter
4. **参数验证**：在build()方法中集中验证
5. **对比构造函数方式**：展示建造者的优势

### 核心代码

#### SQL查询构建器
```java
String sql = new SqlQueryBuilder()
    .select("id", "username", "email")
    .from("users")
    .where("age >= 18")
    .orderBy("username ASC")
    .limit(10)
    .build();
```

#### User对象（静态内部类Builder）
```java
public class User {
    private final String username;  // final = 不可变
    private final String password;
    private final String email;
    
    // 私有构造函数
    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
    }
    
    // 静态内部类Builder
    public static class Builder {
        private final String username;  // 必填
        private final String password;  // 必填
        private String email = "";      // 可选
        
        public Builder(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;  // 返回this实现链式调用
        }
        
        public User build() {
            // 参数验证
            if (password.length() < 6) {
                throw new IllegalArgumentException("密码至少6位");
            }
            return new User(this);
        }
    }
}

// 使用
User user = new User.Builder("alice", "pass123456")
    .email("alice@example.com")
    .phone("13800138000")
    .age(25)
    .build();
```

### 关键点
- **静态内部类**：避免包级别类污染
- **final字段**：创建不可变对象，线程安全
- **私有构造函数**：强制使用Builder
- **链式调用**：方法返回this
- **参数验证**：集中在build()方法

---

## 示例3：DirectorBuilderDemo.java

### 场景
快餐套餐构建器（素食套餐、标准套餐、豪华套餐、儿童套餐）

### 演示内容
1. **Director类的作用**：封装构建流程
2. **预定义套餐**：模板化构建
3. **自定义套餐**：灵活性展示
4. **何时使用Director**：使用场景分析

### 核心代码
```java
// 套餐（Product）
class Meal {
    private List<Item> items = new ArrayList<>();
    
    public void addItem(Item item) {
        items.add(item);
    }
}

// 建造者
class MealBuilder {
    private Meal meal = new Meal();
    
    public MealBuilder addBurger(Burger burger) {
        meal.addItem(burger);
        return this;
    }
    
    public Meal build() {
        return meal;
    }
}

// 导演类
class MealDirector {
    private MealBuilder builder;
    
    public Meal prepareStandardMeal() {
        return builder
            .addBurger(new ChickenBurger())
            .addDrink(new Coke())
            .addFries()
            .build();
    }
}

// 使用
MealDirector director = new MealDirector(builder);
Meal meal = director.prepareStandardMeal();  // 一行代码获取套餐
```

### 关键点
- **Director封装构建流程**：客户端无需知道细节
- **预定义配置**：提供常用套餐模板
- **构建逻辑复用**：同一流程多次使用
- **简化客户端**：一行代码获取复杂对象

---

## 三种示例对比

| 特性 | BasicBuilderDemo | ChainBuilderDemo | DirectorBuilderDemo |
|-----|-----------------|------------------|---------------------|
| **场景** | 组装电脑 | SQL查询、User对象 | 快餐套餐 |
| **结构** | 完整GoF结构 | 静态内部类 | Director封装 |
| **Director** | 有 | 无 | 有 |
| **链式调用** | 是 | 是 | 是 |
| **不可变对象** | 否 | 是（User） | 否 |
| **适用场景** | 复杂构建流程 | 多可选参数 | 预定义配置 |
| **推荐度** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## 学习建议

### 学习顺序
1. **先运行ChainBuilderDemo** - 理解最常用的形式
2. **再运行BasicBuilderDemo** - 理解经典GoF结构
3. **最后运行DirectorBuilderDemo** - 理解Director的作用

### 重点理解

#### 1. ChainBuilderDemo（最重要）
- **为什么用静态内部类？**
  - 避免顶层类污染
  - Builder和Product紧密相关
  
- **如何实现链式调用？**
  ```java
  public Builder email(String email) {
      this.email = email;
      return this;  // 关键：返回this
  }
  ```

- **为什么字段是final？**
  - 创建不可变对象
  - 线程安全
  - 无setter方法

- **何时验证参数？**
  - 在build()方法中统一验证
  - 而不是在每个setter中验证

#### 2. BasicBuilderDemo
- **Builder接口的作用？**
  - 定义构建步骤
  - 支持多种Builder实现
  
- **Director的作用？**
  - 封装构建流程
  - 提供预定义配置

#### 3. DirectorBuilderDemo
- **何时使用Director？**
  - 有明确的预定义配置
  - 构建步骤复杂
  - 需要复用构建流程

- **何时不使用Director？**
  - 构建步骤简单
  - 需要高度定制化
  - 没有预定义配置

---

## 动手练习

### 练习1：HTTP请求构建器
创建一个HTTP请求构建器，支持：
- URL（必填）
- Method（GET/POST/PUT/DELETE）
- Headers（可选）
- Body（可选）
- Timeout（可选）

```java
Request request = new Request.Builder("https://api.example.com")
    .method("POST")
    .header("Authorization", "Bearer token")
    .body("{\"name\": \"Alice\"}")
    .timeout(5000)
    .build();
```

### 练习2：邮件构建器
创建一个邮件构建器，支持：
- 收件人（必填）
- 主题（必填）
- 正文（必填）
- 抄送（可选）
- 附件（可选）

### 练习3：重构Telescoping Constructor
重构以下代码，使用建造者模式：

```java
// 原代码：Telescoping Constructor
public class Config {
    public Config(String host, int port) { /*...*/ }
    public Config(String host, int port, String username) { /*...*/ }
    public Config(String host, int port, String username, String password) { /*...*/ }
    public Config(String host, int port, String username, String password, 
                  int timeout) { /*...*/ }
    public Config(String host, int port, String username, String password,
                  int timeout, boolean ssl) { /*...*/ }
}
```

---

## 思考题

1. **建造者 vs 工厂**：
   - 什么时候用建造者？什么时候用工厂？
   - 能否举出实际项目中的例子？

2. **不可变对象**：
   - 为什么要创建不可变对象？
   - 不可变对象有哪些优点？

3. **参数验证**：
   - 为什么在build()中验证，而不是在setter中？
   - 如果在setter中验证会有什么问题？

4. **Director的必要性**：
   - 什么场景下必须使用Director？
   - 什么场景下不需要Director？

5. **链式调用**：
   - 除了建造者，还有哪些设计模式用链式调用？
   - 链式调用的优缺点是什么？

---

## 常见问题

### Q1: 建造者模式和工厂模式有什么区别？
A: 
- **工厂模式**：关注**创建哪个对象**（类型选择）
- **建造者模式**：关注**如何构建对象**（逐步配置）

### Q2: 什么时候必须用建造者模式？
A: 
- 参数 ≥ 4个
- 多个可选参数
- 需要创建不可变对象
- 参数之间有复杂约束

### Q3: 静态内部类Builder和普通类Builder有什么区别？
A:
- **静态内部类**：推荐，避免包级别类污染
- **普通类**：如果Builder会被其他类使用

### Q4: 为什么User的字段是final？
A: 创建不可变对象（Immutable Object）：
- 线程安全
- 可作为Map的key
- 可以缓存和共享

### Q5: 建造者模式的成本是什么？
A: 
- 需要额外的Builder类
- 代码量增加
- 只适合参数较多的场景

---

## 扩展阅读

完成这三个示例后，建议：
1. 阅读 `doc_01.md` 了解理论细节
2. 完成 `test_01.md` 的自测题
3. 填写 `note_template.md` 巩固知识
4. 阅读《Effective Java》第2条：遇到多个构造器参数时要考虑使用建造者

---

**记住**：
> **参数多且可选多，**  
> **建造者来帮助我，**  
> **逐步构建链式调，**  
> **不可变对象更安全。**

**核心思想**：将对象的构建过程与表示分离，支持逐步构建复杂对象。

**最常用形式**：静态内部类Builder + 链式调用 + 不可变对象
