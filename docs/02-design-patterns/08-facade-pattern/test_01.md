# 外观模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 45 分）

### 1. 外观模式的主要目的是什么？

A. 将一个类的接口转换成客户希望的另一个接口  
B. 动态地给一个对象添加额外的职责  
C. 为其他对象提供一种代理以控制对这个对象的访问  
D. 为子系统中的一组接口提供一个统一的高层接口

<details>
<summary>查看答案</summary>

**答案**: D

**解析**: 外观模式的核心目的是**为子系统提供统一的高层接口**，简化客户端对复杂子系统的使用。

**关键词**：统一接口、简化、多对一

**形象类比**：前台接待（客户只需对接前台，前台协调各部门）

**与其他模式的区别**：
- **A选项**：适配器模式（转换接口）
- **B选项**：装饰器模式（增强功能）
- **C选项**：代理模式（控制访问）
- **D选项**：✅ 外观模式（简化接口）
</details>

---

### 2. 外观模式和适配器模式的主要区别是什么？

A. 外观是多对一，适配器是一对一  
B. 外观改变接口，适配器保持接口  
C. 外观用于单个对象，适配器用于多个对象  
D. 两者没有区别

<details>
<summary>查看答案</summary>

**答案**: A

**解析**:

**外观模式**：
- **对象数量**：多对一（协调多个子系统）
- **目的**：简化接口
- **例子**：前台接待

**适配器模式**：
- **对象数量**：一对一（转换一个对象）
- **目的**：转换接口
- **例子**：插座转换器

**对比表格**：

| 对比 | 外观模式 | 适配器模式 |
|-----|---------|-----------|
| 目的 | 简化接口 | 转换接口 |
| 对象数量 | 多对一 | 一对一 |
| 接口 | 新高层接口 | 转换目标接口 |

**记忆口诀**：
> **外观简化多对一，适配转换一对一。**
</details>

---

### 3. 以下哪个场景最适合使用外观模式？

A. 需要转换第三方接口以适配系统  
B. 家庭影院系统需要一键操作电视、音响、投影仪等多个设备  
C. 需要在不修改对象的情况下动态添加功能  
D. 需要控制对某个对象的访问权限

<details>
<summary>查看答案</summary>

**答案**: B

**解析**:

**B正确** - 家庭影院系统：

```java
// 外观：统一控制多个设备
class HomeTheaterFacade {
    private TV tv;
    private SoundSystem sound;
    private Lights lights;
    private Projector projector;
    
    public void watchMovie() {
        lights.dim();
        projector.on();
        sound.on();
        // 协调多个子系统
    }
}
```

**优势**：
- ✅ 简化调用（一键操作）
- ✅ 协调多个子系统
- ✅ 隐藏复杂性

**其他选项**：
- **A**：转换接口 → **适配器模式**
- **C**：动态添加功能 → **装饰器模式**
- **D**：控制访问 → **代理模式**

**判断标准**：
```
需要简化复杂子系统？
  ├─ 多个子系统需协调？ → ✅ 外观模式
  ├─ 一个接口需转换？ → 适配器模式
  └─ 需要控制访问？ → 代理模式
```
</details>

---

## 二、代码分析（每题 20 分，共 40 分）

### 4. 以下代码有什么问题？如何改进？

```java
// 客户端直接调用多个子系统
public class Client {
    public void processOrder() {
        InventorySystem inventory = new InventorySystem();
        PaymentSystem payment = new PaymentSystem();
        ShippingSystem shipping = new ShippingSystem();
        
        if (inventory.checkStock("P001", 1)) {
            inventory.lockStock("P001", 1);
            if (payment.process(100.0)) {
                inventory.reduceStock("P001", 1);
                shipping.createShipment("ORD001");
            }
        }
        // 客户端需要了解所有子系统！
    }
}
```

<details>
<summary>参考答案</summary>

**问题**: 客户端与多个子系统高度耦合，调用复杂

**问题分析**：
1. ❌ **高度耦合**（客户端需要了解所有子系统）
2. ❌ **调用复杂**（业务逻辑分散在客户端）
3. ❌ **难以维护**（子系统变化影响客户端）
4. ❌ **容易出错**（操作顺序、遗漏步骤）

**改进方案**：使用外观模式

```java
// 1. 创建外观
class OrderFacade {
    // 持有所有子系统
    private InventorySystem inventory;
    private PaymentSystem payment;
    private ShippingSystem shipping;
    
    public OrderFacade() {
        this.inventory = new InventorySystem();
        this.payment = new PaymentSystem();
        this.shipping = new ShippingSystem();
    }
    
    // 提供简单的高层接口
    public boolean placeOrder(String orderId, String productId,
                             int quantity, double amount) {
        System.out.println("处理订单: " + orderId);
        
        // 外观协调所有子系统
        if (!inventory.checkStock(productId, quantity)) {
            return false;
        }
        
        inventory.lockStock(productId, quantity);
        
        if (!payment.process(amount)) {
            return false;
        }
        
        inventory.reduceStock(productId, quantity);
        shipping.createShipment(orderId);
        
        return true;
    }
}

// 2. 客户端使用（简单！）
public class Client {
    public void processOrder() {
        OrderFacade facade = new OrderFacade();
        
        // 一行代码搞定！
        boolean success = facade.placeOrder("ORD001", "P001", 1, 100.0);
    }
}
```

**优势**：
1. ✅ **解耦**（客户端只依赖外观）
2. ✅ **简化调用**（1行代替10多行）
3. ✅ **易维护**（业务逻辑集中在外观）
4. ✅ **不易出错**（外观保证操作顺序）

**代码量对比**：
```
客户端代码：
- 不使用外观: ~15行
- 使用外观: 1行
- 减少代码: 93%
```
</details>

---

### 5. 实现文件操作外观

要求：
- 基础操作：文件读取、写入、复制、删除
- 外观：`FileFacade`，提供简单的文件操作接口
- 隐藏：异常处理、资源管理

<details>
<summary>参考答案</summary>

```java
import java.io.*;

// 外观：文件操作外观
class FileFacade {
    
    /**
     * 简单的文件读取
     */
    public String readFile(String filepath) {
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        
        try {
            reader = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
            
        } catch (IOException e) {
            System.out.println("读取文件失败: " + e.getMessage());
            return null;
        } finally {
            // 外观负责资源清理
            closeQuietly(reader);
        }
    }
    
    /**
     * 简单的文件写入
     */
    public boolean writeFile(String filepath, String content) {
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter(new FileWriter(filepath));
            writer.write(content);
            return true;
            
        } catch (IOException e) {
            System.out.println("写入文件失败: " + e.getMessage());
            return false;
        } finally {
            closeQuietly(writer);
        }
    }
    
    /**
     * 简单的文件复制
     */
    public boolean copyFile(String source, String target) {
        String content = readFile(source);
        if (content != null) {
            return writeFile(target, content);
        }
        return false;
    }
    
    /**
     * 简单的文件删除
     */
    public boolean deleteFile(String filepath) {
        File file = new File(filepath);
        return file.delete();
    }
    
    /**
     * 安静地关闭资源（隐藏异常处理）
     */
    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // 忽略关闭异常
            }
        }
    }
}

// 使用
FileFacade fileFacade = new FileFacade();

// 简单的读取
String content = fileFacade.readFile("input.txt");

// 简单的写入
fileFacade.writeFile("output.txt", "Hello World");

// 简单的复制
fileFacade.copyFile("input.txt", "backup.txt");
```

**关键点**：
- ✅ 外观隐藏异常处理
- ✅ 外观负责资源管理
- ✅ 提供简单的高层接口
- ✅ 客户端无需关心细节
</details>

---

## 三、场景判断（15 分）

### 6. 判断以下场景是否适合外观模式

**场景A**: Spring的Service层协调多个DAO  
**场景B**: 将USB-C接口适配为USB-A接口  
**场景C**: Spring JdbcTemplate简化JDBC操作  
**场景D**: 图片懒加载

<details>
<summary>参考答案</summary>

### 场景A: Service层
**推荐**: ✅ 适合外观模式

**理由**：
- Service层协调多个DAO
- 封装业务逻辑
- 提供统一接口

```java
@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private InventoryDao inventoryDao;
    
    // 外观方法：协调多个DAO
    public void placeOrder(Order order) {
        inventoryDao.check();
        orderDao.save();
    }
}
```

---

### 场景B: USB接口适配
**推荐**: ❌ 不适合外观模式，应该用**适配器模式**

**理由**：
- 一对一转换接口
- 不是多个子系统
- 适配器更合适

---

### 场景C: JdbcTemplate
**推荐**: ✅ 适合外观模式

**理由**：
- 简化JDBC复杂API
- 隐藏资源管理和异常处理
- 提供简单的高层接口

---

### 场景D: 图片懒加载
**推荐**: ❌ 不适合外观模式，应该用**代理模式（虚拟代理）**

**理由**：
- 控制对象创建时机
- 不是多个子系统
- 代理模式更合适

---

### 总结

**适合外观模式**：
- ✅ 场景A：Service层（协调多个DAO）
- ✅ 场景C：JdbcTemplate（简化复杂API）

**不适合外观模式**：
- ❌ 场景B：USB适配（用适配器模式）
- ❌ 场景D：懒加载（用代理模式）

**判断标准**：
```
是否协调多个子系统？
  ├─ 是 → ✅ 外观模式
  └─ 否
      ├─ 一对一转换接口？ → 适配器模式
      └─ 控制访问？ → 代理模式
```
</details>

---

## 四、评分标准

### 满分答案特征
- ✅ 理解外观模式的本质（简化接口）
- ✅ 掌握外观 vs 适配器 vs 代理的区别
- ✅ 理解多对一的关系
- ✅ 能手写外观代码
- ✅ 能准确判断使用场景

### 常见扣分点
- ❌ 混淆外观和适配器
- ❌ 不理解多对一的关系
- ❌ 无法识别协调子系统的场景
- ❌ 外观实现不完整
- ❌ 场景判断错误

---

## 核心要点回顾

### 外观模式三要素
1. **Client**：客户端
2. **Facade**：外观（提供统一接口）
3. **Subsystems**：子系统（多个）

### 外观 vs 其他模式

| 模式 | 目的 | 对象数量 | 接口 |
|-----|------|---------|------|
| **外观** | 简化接口 | 多对一 | 新高层接口 |
| **适配器** | 转换接口 | 一对一 | 转换目标接口 |
| **代理** | 控制访问 | 一对一 | 相同接口 |

### 记忆口诀
> **子系统复杂难调用，**  
> **外观模式来帮忙，**  
> **统一接口简单化，**  
> **前台接待是榜样。**

---

**完成自测后**，填写 `note_template.md` 巩固知识！
