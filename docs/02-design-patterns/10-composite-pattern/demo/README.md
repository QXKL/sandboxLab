# 组合模式 - 代码示例

## 示例说明

本目录包含组合模式的两个核心示例：

1. **FileSystemDemo.java** - 文件系统（文件夹与文件）
2. **CompanyStructureDemo.java** - 公司组织架构（部门与员工）

---

## 运行方式

```bash
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\10-composite-pattern\demo

javac FileSystemDemo.java
javac CompanyStructureDemo.java

java FileSystemDemo
java CompanyStructureDemo
```

---

## 核心概念

### 组合模式的本质

```
Component（抽象构件）
    ↓
├── Leaf（叶子节点）
└── Composite（容器节点）
        ↓
    包含多个Component（递归）

统一接口 + 树形结构 = 组合模式
```

---

## 三个角色

### 1. Component（抽象构件）
```java
interface FileSystemNode {
    void display(String indent);
    long getSize();
}
```

### 2. Leaf（叶子节点）
```java
class File implements FileSystemNode {
    // 没有子节点
    // 直接实现操作
}
```

### 3. Composite（容器节点）
```java
class Folder implements FileSystemNode {
    private List<FileSystemNode> children;  // 包含子节点
    
    public void display(String indent) {
        // 递归调用子节点
        for (FileSystemNode child : children) {
            child.display(indent + "  ");
        }
    }
}
```

---

## 组合模式 vs 非组合模式

### ❌ 不使用组合模式

```java
class Folder {
    List<File> files;           // 需要两个集合
    List<Folder> subFolders;
    
    void display() {
        for (File f : files) {          // 分别处理
            f.display();
        }
        for (Folder folder : subFolders) {
            folder.display();
        }
    }
}
```

**问题**：
- 需要区分文件和文件夹
- 代码重复
- 扩展困难

---

### ✅ 使用组合模式

```java
class Folder implements FileSystemNode {
    List<FileSystemNode> children;  // 统一集合
    
    void display() {
        for (FileSystemNode child : children) {  // 统一处理
            child.display();
        }
    }
}
```

**优势**：
- 统一接口
- 客户端无需区分类型
- 易于扩展

---

## 真实应用

### Java AWT/Swing

```java
// Component是抽象构件
Container container = new JPanel();
container.add(new JButton("按钮"));  // Leaf
container.add(new JLabel("标签"));   // Leaf

JPanel subPanel = new JPanel();      // Composite
subPanel.add(new JButton("子按钮"));
container.add(subPanel);

// 统一的paint方法
container.paint(g);  // 递归绘制所有子组件
```

---

## 关键点

1. **统一接口**：叶子和容器实现同一接口
2. **递归组合**：容器可以包含容器
3. **客户端透明**：无需区分叶子和容器
4. **树形结构**：表示部分-整体层次结构

---

**记住**：
> **树形结构要统一，**  
> **组合模式来帮你，**  
> **部分整体一个样，**  
> **文件夹中有乾坤。**
