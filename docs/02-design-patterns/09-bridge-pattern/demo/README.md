# 桥接模式 - 代码示例

## 示例说明

本目录包含桥接模式的两个核心示例：

1. **RemoteControlBridgeDemo.java** - 遥控器与电视
2. **ShapeBridgeDemo.java** - 形状与颜色

---

## 运行方式

```bash
cd O:\JavaProjects\sandboxLab\docs\02-design-patterns\09-bridge-pattern\demo

javac RemoteControlBridgeDemo.java
javac ShapeBridgeDemo.java

java RemoteControlBridgeDemo
java ShapeBridgeDemo
```

---

## 核心概念

### 桥接模式的本质

```
抽象（Abstraction）
    ↓ 桥接（持有引用）
实现（Implementor）

两个维度独立变化
避免类爆炸：N×M → N+M
```

---

## 桥接 vs 继承

**继承方式（类爆炸）**：
```
3种形状 × 3种颜色 = 9个类
5种形状 × 10种颜色 = 50个类
```

**桥接方式**：
```
3种形状 + 3种颜色 = 6个类
5种形状 + 10种颜色 = 15个类
```

---

## 真实应用

### JDBC（经典）
```java
// 抽象：JDBC API
Connection conn = DriverManager.getConnection(url);

// 实现：数据库驱动
// - MySQL驱动
// - PostgreSQL驱动
```

---

**记住**：
> **两个维度要变化，**  
> **桥接模式来分离，**  
> **抽象实现各自扩展，**  
> **遥控电视是典型。**
