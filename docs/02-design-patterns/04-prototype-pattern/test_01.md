# 原型模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 45 分）

### 1. 原型模式的核心思想是什么？

A. 通过new关键字创建对象  
B. 通过复制现有对象创建新对象  
C. 通过工厂方法创建对象  
D. 通过构造函数创建对象

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 原型模式的核心是**通过克隆（复制）现有对象来创建新对象**，而不是通过new关键字。

**适用场景**：
- 对象创建成本高（复杂初始化、数据库查询）
- 需要创建大量相似对象
- 避免子类爆炸
</details>

---

### 2. 以下关于浅拷贝和深拷贝的描述，哪个是正确的？

A. 浅拷贝会递归复制所有引用对象  
B. 深拷贝只复制对象本身，引用字段共享  
C. Object.clone()默认是深拷贝  
D. 浅拷贝的引用类型字段与原对象共享

<details>
<summary>查看答案</summary>

**答案**: D

**解析**:
- **浅拷贝**：复制对象本身，引用类型字段共享（指向同一对象）
- **深拷贝**：递归复制所有对象，完全独立
- `Object.clone()`默认是**浅拷贝**

**图示**：
```
浅拷贝：对象1 ──┐
              ├──> 共享的List
对象2 ──┘

深拷贝：对象1 ──> List1
       对象2 ──> List2（独立）
```
</details>

---

### 3. 以下哪个场景最适合使用原型模式？

A. 创建简单的Point对象（x, y）  
B. 需要从数据库加载大量配置的对象，创建多个相似实例  
C. 创建不同类型的支付方式  
D. 逐步构建复杂的User对象

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**B正确** - 从数据库加载配置：
- 首次创建成本高（数据库查询）
- 需要创建多个相似实例
- 通过克隆避免重复加载

```java
// 首次创建（耗时）
Config prototype = loadFromDatabase();  // 1秒

// 克隆创建（快速）
Config config1 = prototype.clone();  // 0.001秒
Config config2 = prototype.clone();  // 0.001秒
```

**其他选项**：
- A：对象太简单，直接new即可
- C：创建不同类型，应该用工厂模式
- D：参数多，应该用建造者模式
</details>

---

## 二、代码分析（每题 20 分，共 40 分）

### 4. 以下代码有什么问题？如何修复？

```java
class Person implements Cloneable {
    private String name;
    private List<String> hobbies;
    
    @Override
    public Person clone() {
        try {
            return (Person) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

// 使用
Person p1 = new Person("Alice", Arrays.asList("Reading"));
Person p2 = p1.clone();
p2.getHobbies().add("Swimming");
```

<details>
<summary>参考答案</summary>

**问题**: 浅拷贝导致hobbies字段共享

```java
p2.getHobbies().add("Swimming");
// p1的hobbies也被修改了！
```

**修复方案**：实现深拷贝

```java
class Person implements Cloneable {
    private String name;
    private List<String> hobbies;
    
    @Override
    public Person clone() {
        try {
            Person cloned = (Person) super.clone();
            // 深拷贝List
            cloned.hobbies = new ArrayList<>(this.hobbies);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```

**关键点**：
- `super.clone()`只做浅拷贝
- 引用类型字段需要手动深拷贝
- 使用`new ArrayList<>(this.hobbies)`创建独立副本
</details>

---

### 5. 实现深拷贝的Email类

要求：
- 字段：收件人（to）、主题（subject）、附件列表（attachments）
- 实现深拷贝
- 使用拷贝构造函数方式

<details>
<summary>参考答案</summary>

```java
import java.util.ArrayList;
import java.util.List;

class Attachment {
    private String fileName;
    private int size;
    
    public Attachment(String fileName, int size) {
        this.fileName = fileName;
        this.size = size;
    }
    
    // 拷贝构造函数
    public Attachment(Attachment other) {
        this.fileName = other.fileName;
        this.size = other.size;
    }
}

class Email {
    private String to;
    private String subject;
    private List<Attachment> attachments;
    
    public Email(String to, String subject, List<Attachment> attachments) {
        this.to = to;
        this.subject = subject;
        this.attachments = attachments;
    }
    
    // 拷贝构造函数（深拷贝）
    public Email(Email other) {
        this.to = other.to;
        this.subject = other.subject;
        
        // 深拷贝List
        this.attachments = new ArrayList<>();
        for (Attachment att : other.attachments) {
            this.attachments.add(new Attachment(att));
        }
    }
    
    public List<Attachment> getAttachments() {
        return attachments;
    }
}

// 使用
Email original = new Email("user@example.com", "Hello", attachments);
Email copy = new Email(original);  // 拷贝构造函数

// 修改副本不影响原对象
copy.getAttachments().add(new Attachment("file.pdf", 1024));
```

**要点**：
1. ✅ Attachment也需要拷贝构造函数
2. ✅ Email的拷贝构造函数中深拷贝List
3. ✅ 递归调用Attachment的拷贝构造函数
</details>

---

## 三、场景判断（15 分）

### 6. 判断以下场景是否适合原型模式

**场景A**: 游戏中需要创建1000个相同的NPC  
**场景B**: 电商系统需要创建不同的订单对象  
**场景C**: 从配置文件加载数据库配置，需要为不同模块创建副本  
**场景D**: 创建User对象，有10个可选参数

<details>
<summary>参考答案</summary>

### 场景A: 游戏NPC
**推荐**: ✅ 非常适合原型模式

**理由**：
- 需要大量相似对象（1000个）
- NPC属性相同，只需微调位置
- 避免重复初始化

```java
Monster prototype = new Monster("Goblin", 100, 20);
for (int i = 0; i < 1000; i++) {
    Monster npc = prototype.clone();
    npc.setPosition(randomPosition());
}
```

---

### 场景B: 订单对象
**推荐**: ❌ 不适合原型模式

**理由**：
- 每个订单都不同（订单号、用户、商品）
- 不存在"相似"对象
- 应该直接new创建

---

### 场景C: 数据库配置副本
**推荐**: ✅ 适合原型模式

**理由**：
- 配置加载成本高（读取文件）
- 多个模块需要相同配置
- 克隆避免重复加载

```java
// 首次加载（耗时）
DBConfig prototype = loadFromFile();

// 克隆给各模块（快速）
DBConfig moduleA = prototype.clone();
DBConfig moduleB = prototype.clone();
```

---

### 场景D: User对象（10个可选参数）
**推荐**: ❌ 不适合原型模式，应该用建造者模式

**理由**：
- 关注点是"如何配置参数"
- 不是"对象创建成本高"
- 建造者模式更合适

```java
// 应该用建造者模式
User user = new User.Builder("alice", "pass")
    .email("alice@example.com")
    .age(25)
    .build();
```

---

### 总结

**适合原型模式**：
- ✅ 场景A：大量相似对象
- ✅ 场景C：创建成本高

**不适合原型模式**：
- ❌ 场景B：对象差异大（直接new）
- ❌ 场景D：参数配置问题（用建造者）
</details>

---

## 四、评分标准

### 满分答案特征
- ✅ 理解原型模式的本质（克隆 vs new）
- ✅ 掌握浅拷贝和深拷贝的区别
- ✅ 能手写深拷贝代码
- ✅ 能准确判断使用场景
- ✅ 理解原型模式 vs 其他模式

### 常见扣分点
- ❌ 混淆浅拷贝和深拷贝
- ❌ 不知道super.clone()是浅拷贝
- ❌ 无法实现深拷贝
- ❌ 场景判断错误
- ❌ 混淆原型模式和工厂模式

---

## 核心要点回顾

### 原型模式三要素
1. **实现Cloneable接口**
2. **重写clone()方法**
3. **处理深拷贝（如果需要）**

### 浅拷贝 vs 深拷贝
| 对比 | 浅拷贝 | 深拷贝 |
|-----|--------|--------|
| 基本类型 | 复制值 | 复制值 |
| 引用类型 | 共享 | 独立 |
| 实现 | super.clone() | 手动复制 |

### 记忆口诀
> **创建成本高又高，**  
> **原型克隆效率好，**  
> **浅拷贝要小心，**  
> **深拷贝更可靠。**

---

**完成自测后**，填写 `note_template.md` 巩固知识！
