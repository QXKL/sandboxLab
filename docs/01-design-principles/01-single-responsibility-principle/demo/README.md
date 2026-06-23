# 单一职责原则 - 代码示例

## 示例说明

本示例展示了用户注册系统的两种实现：

1. **违反 SRP**：`BadExample.java` - 所有职责混在一个类中
2. **符合 SRP**：`GoodExample.java` - 职责清晰分离

## 运行方式

### 编译
```bash
cd O:/JavaProjects/sandboxLab/docs/01-design-principles/01-single-responsibility-principle/demo
javac *.java
```

### 运行违反SRP的示例
```bash
java BadExample
```

### 运行符合SRP的示例
```bash
java GoodExample
```

## 预期输出

两个示例都会输出用户注册的流程，但代码结构完全不同：

- **BadExample**：一个类承担所有职责，难以维护和测试
- **GoodExample**：职责分离，每个类都很小、很专注

## 代码结构对比

### 违反 SRP（BadExample）
```
UserBad (一个类包含所有职责)
├── 数据存储
├── 数据验证
├── 持久化逻辑
└── 邮件发送
```

### 符合 SRP（GoodExample）
```
User (纯数据)
UserValidator (验证逻辑)
UserRepository (持久化)
EmailService (邮件发送)
UserService (协调者)
```

## 关键对比点

| 维度 | BadExample | GoodExample |
|-----|-----------|------------|
| **类的数量** | 1个类做所有事 | 5个类各司其职 |
| **测试难度** | 需要mock数据库+邮件 | 每个类独立测试 |
| **维护性** | 改验证规则要动整个类 | 只改UserValidator |
| **复用性** | 无法单独复用验证逻辑 | 验证器可以在别处使用 |
| **职责清晰度** | 不清楚这个类的主要职责 | 每个类名就说明了职责 |

## 思考题

1. 如果需求变化，要换一个数据库，哪种实现改动更小？
2. 如果要在其他地方复用邮件发送功能，哪种实现更容易？
3. 如果要为验证逻辑写单元测试，哪种实现更容易？
