# RESTful API 代码示例

本目录包含两个演示程序，帮助你理解RESTful API设计。

## 文件说明

- **UserApiDemo.java** - 完整的用户管理API示例
- **ApiDesignComparison.java** - 好vs坏的API设计对比

## 如何运行

### 运行 UserApiDemo

```bash
# 编译
javac UserApiDemo.java

# 运行
java UserApiDemo
```

### 运行 ApiDesignComparison

```bash
# 编译
javac ApiDesignComparison.java

# 运行
java ApiDesignComparison
```

## 预期输出

### UserApiDemo 输出

展示完整的用户管理API操作流程：
- 列出所有用户
- 获取单个用户
- 创建新用户
- 更新用户信息
- 删除用户
- 分页查询
- 过滤和排序

每个操作都会打印：
- HTTP方法和路径
- 请求体（如果有）
- HTTP状态码
- 响应体

### ApiDesignComparison 输出

对比展示：
- **坏的设计**：动词命名、方法混乱、状态码错误
- **好的设计**：符合RESTful规范的设计

帮助你快速建立"什么是好API"的直觉。

## 注意事项

1. 这些是**模拟代码**，不依赖Web框架
2. 重点在于展示API设计原则，而非实现细节
3. 实际项目中会使用Spring Boot等框架，但核心思想相同

## 学习建议

1. 先阅读 `doc_01.md` 理解概念
2. 运行 `ApiDesignComparison.java` 建立直觉
3. 运行 `UserApiDemo.java` 看完整示例
4. 修改代码，尝试添加新功能（如商品管理、订单管理）
