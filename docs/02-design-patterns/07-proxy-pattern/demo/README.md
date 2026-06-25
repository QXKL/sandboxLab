# 代理模式 - 代码示例

## 示例说明

本目录包含代理模式的三个核心示例：

1. **VirtualProxyDemo.java** - 虚拟代理（懒加载）
2. **ProtectionProxyDemo.java** - 保护代理（权限控制）
3. **CacheProxyDemo.java** - 缓存代理（性能优化）

---

## 运行方式

```bash
# 进入demo目录
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\07-proxy-pattern\demo

# 编译
javac VirtualProxyDemo.java
javac ProtectionProxyDemo.java
javac CacheProxyDemo.java

# 运行
java VirtualProxyDemo
java ProtectionProxyDemo
java CacheProxyDemo
```

---

## 示例1：VirtualProxyDemo.java

### 场景
图片查看器，大图片延迟加载

### 演示内容
- 虚拟代理的懒加载机制
- 启动速度优化
- 按需创建真实对象
- 性能对比

### 核心要点

**问题：直接加载所有大图片**
```java
// ❌ 启动慢（一次性加载所有图片）
Image img1 = new RealImage("photo1.jpg");  // 2秒
Image img2 = new RealImage("photo2.jpg");  // 2秒
Image img3 = new RealImage("photo3.jpg");  // 2秒
// 总耗时：6秒
```

**解决：虚拟代理**
```java
// ✅ 启动快（只创建代理）
Image img1 = new ImageProxy("photo1.jpg");  // 几乎瞬间
Image img2 = new ImageProxy("photo2.jpg");  // 几乎瞬间
Image img3 = new ImageProxy("photo3.jpg");  // 几乎瞬间

// 需要时才加载
img1.display();  // 此时才加载photo1.jpg（2秒）
```

**性能提升**：
```
100张大图片的场景：
- 直接加载：200秒（启动时）
- 代理加载：几乎瞬间（启动时） + 2秒 × 实际查看数量
- 如果只查看10张：20秒 vs 200秒，性能提升90%
```

---

## 示例2：ProtectionProxyDemo.java

### 场景
文档管理系统，根据用户角色控制访问

### 演示内容
- 保护代理的权限控制
- 基于角色的访问控制（RBAC）
- 访问日志记录
- 安全审计

### 核心要点

**权限矩阵**：
```
┌─────────┬──────┬──────┬──────┐
│ 角色    │ 查看 │ 编辑 │ 删除 │
├─────────┼──────┼──────┼──────┤
│ ADMIN   │  ✅  │  ✅  │  ✅  │
│ USER    │  ✅  │  ✅  │  ❌  │
│ GUEST   │  ✅  │  ❌  │  ❌  │
└─────────┴──────┴──────┴──────┘
```

**保护代理实现**：
```java
class DocumentProxy implements Document {
    private RealDocument realDocument;
    private User user;
    
    @Override
    public void edit(String content) {
        if (user.hasWritePermission()) {
            realDocument.edit(content);  // 有权限才允许
        } else {
            System.out.println("权限不足");
        }
    }
}
```

**优势**：
- 统一的权限检查点
- 自动记录访问日志
- 真实对象无需关心权限逻辑

---

## 示例3：CacheProxyDemo.java

### 场景
数据库查询缓存代理

### 演示内容
- 缓存代理的实现
- 缓存命中 vs 未命中
- 缓存过期策略
- 性能优化效果

### 核心要点

**缓存代理实现**：
```java
class CacheProxy implements DataService {
    private DatabaseService databaseService;
    private Map<String, String> cache;  // 缓存
    
    @Override
    public String query(String sql) {
        // 检查缓存
        if (cache.containsKey(sql)) {
            return cache.get(sql);  // 缓存命中，直接返回
        }
        
        // 缓存未命中，查询数据库
        String result = databaseService.query(sql);
        cache.put(sql, result);  // 存入缓存
        return result;
    }
}
```

**性能对比**：
```
场景：1000次查询相同数据
- 无缓存：1000秒（每次查询1秒）
- 有缓存：1秒（首次） + 0秒（后续999次）
- 性能提升：99.9%
```

---

## 三种代理类型对比

| 代理类型 | 目的 | 典型场景 | 示例 |
|---------|------|---------|------|
| **虚拟代理** | 延迟创建 | 大对象加载 | 图片懒加载 |
| **保护代理** | 权限控制 | 访问控制 | 文档权限 |
| **缓存代理** | 性能优化 | 结果缓存 | 数据库查询 |

---

## 核心概念

### 1. 代理模式的结构

```
Client → Proxy → RealSubject
         (控制)
```

**关键点**：
- Proxy和RealSubject实现相同接口
- Proxy持有RealSubject
- Proxy控制对RealSubject的访问

---

### 2. 代理 vs 装饰器 vs 适配器

| 模式 | 目的 | 接口 | 例子 |
|-----|------|------|------|
| **代理** | 控制访问 | 保持不变 | 门卫 |
| **装饰器** | 增强功能 | 保持不变 | 给咖啡加料 |
| **适配器** | 转换接口 | 改变 | 插座转换器 |

**区别要点**：
- **代理**：控制何时、如何访问（懒加载、权限检查）
- **装饰器**：添加额外功能（边框、滚动条）
- **适配器**：转换接口（让不兼容的接口能合作）

---

### 3. 代理的关键设计

**持有真实对象**：
```java
class Proxy implements Subject {
    private RealSubject realSubject;
}
```

**控制访问**：
```java
@Override
public void request() {
    // 控制逻辑
    if (checkAccess()) {
        realSubject.request();
    }
}
```

**懒加载**：
```java
@Override
public void request() {
    if (realSubject == null) {
        realSubject = new RealSubject();  // 需要时才创建
    }
    realSubject.request();
}
```

---

## 学习建议

### 学习顺序
1. **VirtualProxyDemo** - 理解懒加载机制
2. **ProtectionProxyDemo** - 理解权限控制
3. **CacheProxyDemo** - 理解性能优化

### 重点理解

#### 1. 虚拟代理的懒加载
运行VirtualProxyDemo，观察：
- 代理创建几乎瞬间
- 真实对象延迟创建
- 多个图片的场景下效果明显

#### 2. 保护代理的权限控制
运行ProtectionProxyDemo，思考：
- 如何在代理中统一检查权限？
- 如何记录访问日志？
- 为什么真实对象无需关心权限？

#### 3. 缓存代理的性能优化
运行CacheProxyDemo，分析：
- 缓存命中 vs 未命中的性能差异
- 缓存过期策略
- 缓存代理的透明性

---

## 思考题

1. **代理 vs 装饰器**：
   - 两者都持有对象，如何区分？
   - 何时用代理，何时用装饰器？

2. **虚拟代理的应用**：
   - Hibernate的懒加载是虚拟代理吗？
   - 还有哪些场景适合虚拟代理？

3. **保护代理的扩展**：
   - 如何实现更复杂的权限规则？
   - 如何支持动态权限？

4. **缓存代理的优化**：
   - 如何实现LRU缓存淘汰？
   - 如何处理缓存更新？

---

## 常见问题

### Q1: 代理和装饰器有什么区别？
A: 
- **代理**：控制访问（懒加载、权限检查）
- **装饰器**：增强功能（添加边框、日志）
- **关键区别**：意图不同

### Q2: 虚拟代理一定比直接创建快吗？
A: 不一定。只有当：
- 对象创建成本高
- 对象不一定被使用
- 需要优化启动速度
时，虚拟代理才有优势。

### Q3: 如何选择代理类型？
A: 
- 延迟创建 → 虚拟代理
- 权限控制 → 保护代理
- 性能优化 → 缓存代理
- 远程访问 → 远程代理

### Q4: 代理会增加性能开销吗？
A: 会有轻微开销（多一层调用），但：
- 虚拟代理：减少启动时间
- 缓存代理：大幅提升性能
- 整体收益远大于开销

---

## 扩展阅读

完成这三个示例后，建议：
1. 阅读 `doc_01.md` 了解理论细节
2. 完成 `test_01.md` 的自测题
3. 填写 `note_template.md` 巩固知识
4. 思考：Spring AOP是如何实现代理的？

---

## 真实应用案例

### Java标准库中的代理

**1. JDK动态代理**
```java
InvocationHandler handler = new MyHandler(realObject);
Object proxy = Proxy.newProxyInstance(...);
```

**2. Collections代理**
```java
List<String> list = new ArrayList<>();
list = Collections.synchronizedList(list);  // 代理，添加同步
```

---

## 代理模式的优势

### 优势总结
✅ **职责清晰**：代理负责控制，真实对象负责业务  
✅ **高扩展性**：可以在不修改真实对象的情况下扩展  
✅ **符合开闭原则**：新增代理不修改现有代码  
✅ **智能化**：可以在访问时做额外处理

### 适用场景
- 对象创建成本高 → 虚拟代理
- 需要权限控制 → 保护代理
- 需要性能优化 → 缓存代理
- 需要远程访问 → 远程代理

---

**记住**：
> **门卫控制访问权，**  
> **代理模式解难题，**  
> **懒加载权限缓存，**  
> **透明控制是关键。**
