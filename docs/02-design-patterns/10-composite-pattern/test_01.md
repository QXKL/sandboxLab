# 组合模式 - 自测题

> 总分：100分 | 及格线：80分

---

## 一、概念理解（选择题，每题 15 分，共 30 分）

### 1. 组合模式的主要目的是什么？

A. 动态地给对象添加额外的职责  
B. 将对象组合成树形结构以表示"部分-整体"的层次结构  
C. 将一个类的接口转换成客户希望的另一个接口  
D. 为其他对象提供一种代理以控制对这个对象的访问

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 组合模式的核心目的是**将对象组合成树形结构以表示"部分-整体"的层次结构**，使客户端对单个对象和组合对象的使用具有一致性。

**关键词**：树形结构、部分-整体、统一接口

**形象类比**：文件夹（容器）可以包含文件（叶子）或子文件夹（容器），统一用display()方法显示

**其他选项**：
- A：装饰器模式
- C：适配器模式
- D：代理模式
</details>

---

### 2. 组合模式中的三个角色是什么？

A. Subject, Proxy, RealSubject  
B. Component, Leaf, Composite  
C. Adapter, Adaptee, Target  
D. Abstraction, Implementor, ConcreteImplementor

<details>
<summary>查看答案</summary>

**答案**: B

**解析**: 组合模式的三个角色：

1. **Component（抽象构件）**：
   - 定义统一接口
   - 声明公共操作

2. **Leaf（叶子节点）**：
   - 实现Component接口
   - 没有子节点

3. **Composite（容器节点）**：
   - 实现Component接口
   - 包含子节点集合
   - 实现递归操作

**示例**：
```java
// Component
interface FileSystemNode {
    void display();
}

// Leaf
class File implements FileSystemNode { }

// Composite
class Folder implements FileSystemNode {
    List<FileSystemNode> children;
}
```

**其他选项**：
- A：代理模式
- C：适配器模式
- D：桥接模式
</details>

---

## 二、模式对比（15 分）

### 3. 组合模式和装饰器模式的主要区别是什么？

A. 组合模式是树形结构（一对多），装饰器模式是链式结构（一对一）  
B. 组合模式用于增强功能，装饰器模式用于表示层次结构  
C. 组合模式在运行时组合，装饰器模式在编译时组合  
D. 两者没有区别

<details>
<summary>查看答案</summary>

**答案**: A

**解析**: 

| 对比 | 组合模式 | 装饰器模式 |
|-----|---------|-----------|
| **目的** | 表示部分-整体层次结构 | 动态增强对象功能 |
| **结构** | 树形结构（一对多） | 链式结构（一对一） |
| **关系** | 容器包含多个子节点 | 装饰器包装一个对象 |
| **操作** | 递归处理所有子节点 | 增强单个对象 |

**组合模式示例**：
```java
Folder folder = new Folder("文档");
folder.add(new File("a.txt"));      // 一对多
folder.add(new File("b.txt"));
folder.add(new Folder("子文件夹"));
```

**装饰器模式示例**：
```java
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);      // 一对一
coffee = new SugarDecorator(coffee);     // 链式
```

**记忆口诀**：
> **组合是树形多包含，**  
> **装饰是链式一对一。**
</details>

---

## 三、场景判断（20 分）

### 4. 以下哪些场景适合使用组合模式？（多选）

A. 文件系统（文件夹与文件）  
B. 咖啡加配料（咖啡加奶加糖）  
C. GUI组件树（容器与组件）  
D. 公司组织架构（部门与员工）  
E. 旧系统接口适配

<details>
<summary>查看答案</summary>

**答案**: A, C, D

**解析**:

### ✅ A. 文件系统 - 适合
**理由**：
- 文件夹（Composite）可以包含文件（Leaf）或子文件夹
- 统一的display()、getSize()操作
- 典型的树形结构

```java
Folder root = new Folder("根目录");
root.add(new File("文件.txt"));
root.add(new Folder("子文件夹"));
root.display();  // 统一接口
```

---

### ❌ B. 咖啡加配料 - 不适合，应该用**装饰器模式**
**理由**：
- 不是"包含"关系，是"增强"关系
- 链式结构，不是树形结构
- 一对一包装，不是一对多

---

### ✅ C. GUI组件树 - 适合
**理由**：
- Java Swing就是组合模式
- Container（容器）可以包含Component（组件）
- 统一的paint()方法

```java
Container panel = new JPanel();
panel.add(new JButton("按钮"));
panel.add(new JLabel("标签"));
```

---

### ✅ D. 公司组织架构 - 适合
**理由**：
- 部门（Composite）可以包含员工（Leaf）或子部门
- 统一的printStructure()、getTotalSalary()操作
- 典型的树形层次结构

---

### ❌ E. 旧系统接口适配 - 不适合，应该用**适配器模式**
**理由**：
- 不是树形结构
- 是接口转换问题
- 适配器模式更合适
</details>

---

## 四、代码分析（25 分）

### 5. 以下代码有什么问题？如何用组合模式改进？

```java
// 菜单系统（不使用组合模式）
class Menu {
    private String name;
    private List<MenuItem> items;      // 只能包含菜单项
    private List<Menu> subMenus;       // 需要单独的子菜单列表
    
    public void display() {
        System.out.println(name);
        
        // 需要分别处理菜单项和子菜单
        for (MenuItem item : items) {
            System.out.println("  " + item.getName());
        }
        
        for (Menu subMenu : subMenus) {
            System.out.println("  " + subMenu.getName());
            subMenu.display();  // 递归
        }
    }
}

class MenuItem {
    private String name;
    
    public String getName() {
        return name;
    }
}
```

<details>
<summary>参考答案</summary>

**问题分析**：

1. ❌ **两个集合**：items和subMenus，需要分别管理
2. ❌ **客户端需要区分**：菜单项和子菜单
3. ❌ **代码重复**：display方法需要分别处理
4. ❌ **不统一**：MenuItem和Menu没有共同接口
5. ❌ **扩展困难**：新增类型需要修改Menu类

---

**改进方案：使用组合模式**

```java
// 1. Component：抽象菜单组件
interface MenuComponent {
    void display(String indent);
}

// 2. Leaf：菜单项
class MenuItem implements MenuComponent {
    private String name;
    private String description;
    
    public MenuItem(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "📄 " + name + " - " + description);
    }
}

// 3. Composite：菜单
class Menu implements MenuComponent {
    private String name;
    private List<MenuComponent> components = new ArrayList<>();  // 统一集合
    
    public Menu(String name) {
        this.name = name;
    }
    
    public void add(MenuComponent component) {
        components.add(component);
    }
    
    public void remove(MenuComponent component) {
        components.remove(component);
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "📁 " + name);
        for (MenuComponent component : components) {
            component.display(indent + "  ");  // 统一调用
        }
    }
}

// 4. 使用
public class MenuDemo {
    public static void main(String[] args) {
        // 主菜单
        Menu mainMenu = new Menu("主菜单");
        
        // 文件菜单
        Menu fileMenu = new Menu("文件");
        fileMenu.add(new MenuItem("新建", "创建新文件"));
        fileMenu.add(new MenuItem("打开", "打开现有文件"));
        fileMenu.add(new MenuItem("保存", "保存当前文件"));
        mainMenu.add(fileMenu);
        
        // 编辑菜单
        Menu editMenu = new Menu("编辑");
        editMenu.add(new MenuItem("复制", "复制选中内容"));
        editMenu.add(new MenuItem("粘贴", "粘贴剪贴板内容"));
        
        // 编辑菜单的子菜单
        Menu findMenu = new Menu("查找");
        findMenu.add(new MenuItem("查找", "查找文本"));
        findMenu.add(new MenuItem("替换", "替换文本"));
        editMenu.add(findMenu);
        
        mainMenu.add(editMenu);
        
        // 显示整个菜单树
        mainMenu.display("");
    }
}
```

**输出**：
```
📁 主菜单
  📁 文件
    📄 新建 - 创建新文件
    📄 打开 - 打开现有文件
    📄 保存 - 保存当前文件
  📁 编辑
    📄 复制 - 复制选中内容
    📄 粘贴 - 粘贴剪贴板内容
    📁 查找
      📄 查找 - 查找文本
      📄 替换 - 替换文本
```

---

**优势对比**：

| 对比 | 改进前 | 改进后 |
|-----|--------|--------|
| 集合数量 | 2个（items, subMenus） | 1个（components） |
| 统一接口 | 无 | MenuComponent |
| 客户端区分 | 需要 | 不需要 |
| 代码重复 | 高 | 低 |
| 扩展性 | 差 | 好 |

</details>

---

## 五、透明模式 vs 安全模式（10 分）

### 6. 组合模式的透明模式和安全模式有什么区别？各有什么优缺点？

<details>
<summary>参考答案</summary>

### 透明模式（Transparent）

```java
// Component包含管理子节点的方法
interface Component {
    void operation();
    void add(Component c);      // 所有节点都有
    void remove(Component c);
    Component getChild(int i);
}

// Leaf必须实现，但抛出异常
class Leaf implements Component {
    public void operation() { /* ... */ }
    
    public void add(Component c) {
        throw new UnsupportedOperationException("叶子节点不能添加子节点");
    }
    public void remove(Component c) {
        throw new UnsupportedOperationException();
    }
    public Component getChild(int i) {
        throw new UnsupportedOperationException();
    }
}
```

**优点**：
- ✅ **透明性**：客户端可以统一对待所有节点
- ✅ **简单**：客户端无需类型判断

**缺点**：
- ❌ **不安全**：叶子节点调用add会运行时异常
- ❌ **违反接口隔离原则**：叶子节点有不需要的方法

---

### 安全模式（Safe）

```java
// Component只定义公共操作
interface Component {
    void operation();  // 只有公共操作
}

class Leaf implements Component {
    public void operation() { /* ... */ }
    // 没有add/remove方法
}

class Composite implements Component {
    public void operation() { /* ... */ }
    public void add(Component c) { /* ... */ }      // 只有容器有
    public void remove(Component c) { /* ... */ }
    public Component getChild(int i) { /* ... */ }
}
```

**优点**：
- ✅ **类型安全**：编译时就知道叶子节点没有add
- ✅ **符合接口隔离原则**

**缺点**：
- ❌ **失去透明性**：客户端需要区分叶子和容器
- ❌ **需要类型判断**：`if (component instanceof Composite)`

---

### 如何选择？

| 需求 | 推荐方式 |
|-----|---------|
| 客户端需要统一处理所有节点 | 透明模式 |
| 强调类型安全，避免运行时错误 | 安全模式 |
| 树形结构简单，不常调用add/remove | 安全模式 |
| 树形结构复杂，需要动态构建 | 透明模式 |

**Java的选择**：
- **AWT/Swing**：透明模式（Component接口包含add）
- **本教程示例**：安全模式（更符合Java最佳实践）

**推荐**：
- 对于教学和大多数场景，**安全模式**更好
- 只有在确实需要完全透明时才用透明模式

</details>

---

## 核心要点回顾

### 组合模式三要素
1. **Component**：统一接口
2. **Leaf**：叶子节点（没有子节点）
3. **Composite**：容器节点（包含子节点集合）

### 记忆口诀
> **树形结构要统一，**  
> **组合模式来帮你，**  
> **部分整体一个样，**  
> **文件夹中有乾坤。**

### 使用场景
- ✅ 树形结构（文件系统、GUI组件树）
- ✅ 部分-整体关系（公司架构、菜单系统）
- ✅ 需要统一处理单个对象和组合对象

---

**完成自测后**，填写 `note_template.md` 巩固知识！
