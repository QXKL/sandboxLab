# 依赖倒置原则 - 代码示例

## 示例说明

本示例展示了订单处理系统的两种设计：

1. **违反 DIP**：`BadExample.java` - 高层直接依赖低层具体实现
2. **符合 DIP**：`GoodExample.java` - 通过抽象接口解耦，使用依赖注入

## 核心问题

**依赖方向错误**的问题：
- 高层模块（业务逻辑）直接依赖低层模块（具体实现）
- 紧耦合，无法更换实现
- 难以测试

## 运行方式

```bash
cd O:/JavaProjects/sandboxLab/docs/01-design-principles/05-dependency-inversion-principle/demo
javac *.java
java BadExample
java GoodExample
```

## 关键对比

| 维度 | BadExample | GoodExample |
|-----|-----------|------------|
| **依赖方向** | 高层 → 低层 | 高层 → 抽象 ← 低层 |
| **耦合度** | 紧耦合 | 松耦合 |
| **可测试性** | 难以测试 | 易于测试（Mock） |
| **可扩展性** | 难以更换实现 | 易于更换实现 |
| **灵活性** | 低 | 高 |

## 思考题

1. 为什么说依赖方向"倒置"了？
2. 如何通过依赖注入提高可测试性？
3. DIP 如何支持 OCP（开闭原则）？
4. 什么时候需要定义接口？

## 记住

> **高层低层不直连，都靠抽象来牵线！**
