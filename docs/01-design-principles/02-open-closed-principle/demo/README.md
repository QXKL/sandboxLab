# 开闭原则 - 代码示例

## 示例说明

本示例展示了通知系统的两种实现：

1. **违反 OCP**：`BadExample.java` - 使用 if-else 判断，每次新增通知方式都要修改代码
2. **符合 OCP**：`GoodExample.java` - 使用接口和多态，新增通知方式只需添加新类

## 运行方式

### 编译
```bash
cd O:/JavaProjects/sandboxLab/docs/01-design-principles/02-open-closed-principle/demo
javac *.java
```

### 运行违反OCP的示例
```bash
java BadExample
```

### 运行符合OCP的示例
```bash
java GoodExample
```

## 预期输出

两个示例都会输出通知发送的过程，但代码结构完全不同：

- **BadExample**：通过 if-else 判断通知类型，每次新增类型都要修改 send 方法
- **GoodExample**：通过接口和多态，新增类型只需添加新的实现类

## 代码结构对比

### 违反 OCP（BadExample）
```
NotificationService
└── send(message, type)
    ├── if (type == "email") → 发送邮件
    ├── else if (type == "sms") → 发送短信
    └── else if (type == "push") → 发送推送
    
新增通知方式 → 必须修改 send 方法 ❌
```

### 符合 OCP（GoodExample）
```
Notifier (接口)
├── EmailNotifier (实现类)
├── SmsNotifier (实现类)
├── PushNotifier (实现类)
└── WeChatNotifier (实现类) ← 新增，不修改现有代码 ✅

NotificationService
└── 依赖 Notifier 接口
```

## 关键对比点

| 维度 | BadExample | GoodExample |
|-----|-----------|------------|
| **扩展方式** | 修改 if-else 分支 | 添加新的实现类 |
| **修改现有代码** | 是（违反OCP） | 否（符合OCP） |
| **测试成本** | 每次都要测试所有分支 | 只需测试新类 |
| **风险** | 可能影响现有功能 | 不影响现有功能 |
| **代码复杂度** | if-else 链越来越长 | 每个类都很简单 |
| **运行时扩展** | 不支持 | 支持（通过配置/插件） |

## 思考题

1. 如果要新增"钉钉通知"，哪种实现改动更小、风险更低？
2. 如果 EmailNotifier 有 bug，修复时会影响其他通知方式吗？
3. 如果要在运行时动态加载通知插件，哪种实现更容易支持？
4. BadExample 的 send 方法有 10 个 if-else，如何重构成符合 OCP 的设计？

## 扩展练习

尝试为 GoodExample 添加以下功能，体会"对扩展开放，对修改关闭"：

1. 添加"钉钉通知"（DingTalkNotifier）
2. 添加"Slack 通知"（SlackNotifier）
3. 添加通知发送前的验证逻辑
4. 添加通知发送失败的重试机制

看看你是否能做到：**只添加新代码，不修改现有代码**。
