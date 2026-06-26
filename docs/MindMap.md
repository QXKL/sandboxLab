## MindMap - 思维导图

> 这里是整个项目的目录兼索引
> 
> 当然，也包含了规范、指南和建议

---

### 教学大纲

#### 01 设计原则
- 单一职责原则（SRP） ✅
- 开闭原则（OCP） ✅
- 里氏替换原则（LSP） ✅
- 接口隔离原则（ISP） ✅
- 依赖倒置原则（DIP） ✅
- 迪米特法则（最少知道原则）
- 合成复用原则
- DRY（Don't Repeat Yourself）
- KISS（Keep It Simple, Stupid）
- YAGNI（You Aren't Gonna Need It）

#### 02 设计模式
**创建型模式**
- 单例模式 ✅
- 工厂模式（简单工厂、工厂方法、抽象工厂） ✅
- 建造者模式 ✅
- 原型模式 ✅

**结构型模式**
- 适配器模式 ✅
- 装饰器模式 ✅
- 代理模式 ✅
- 外观模式 ✅
- 桥接模式 ✅
- 组合模式 ✅
- 享元模式 ✅

**行为型模式**
- 策略模式
- 观察者模式
- 模板方法模式
- 责任链模式
- 状态模式
- 命令模式
- 迭代器模式
- 访问者模式
- 备忘录模式
- 中介者模式
- 解释器模式

#### 03 代码质量
- 单元测试
- 集成测试
- TDD（测试驱动开发）
- 重构技巧
- 代码坏味道识别
- 圈复杂度
- 代码覆盖率
- 静态代码分析

#### 04 API 设计
**RESTful API**
- RESTful 规范
- 资源命名（复数 vs 单数）
- HTTP 方法语义（GET、POST、PUT、PATCH、DELETE）
- HTTP 状态码
- API 版本管理
- 分页、过滤、排序
- HATEOAS

**其他 API 风格**
- GraphQL
- gRPC
- OpenAPI / Swagger

**API 安全**
- 认证与鉴权（JWT、OAuth2、API Key）
- HTTPS/TLS
- 跨域（CORS）
- 限流与防刷

#### 05 数据库相关
- 事务隔离级别
- 索引设计与优化
- 范式 vs 反范式
- 连接池管理
- N+1 查询问题
- 主从复制
- 读写分离
- 分库分表
- 数据库迁移工具（Flyway、Liquibase）
- ORM 设计与选型（JPA/Hibernate/MyBatis）

#### 06 架构设计
**架构风格**
- 分层架构
- 六边形架构（端口适配器）
- 洋葱架构
- 微服务 vs 单体
- 事件驱动架构
- CQRS（命令查询职责分离）
- 事件溯源

**领域驱动设计（DDD）**
- 限界上下文
- 聚合根、实体、值对象
- 领域服务
- 防腐层

**分布式系统**
- CAP 理论
- BASE 理论
- 最终一致性
- 分布式事务
- 服务网格

**高可用与高并发**
- 高可用设计
- 高并发设计
- 负载均衡
- 限流、熔断、降级
- 缓存策略

**部署策略**
- 灰度发布
- 蓝绿部署
- 金丝雀发布
- 滚动发布

#### 07 安全
- 常见安全漏洞（OWASP Top 10）
  - SQL 注入
  - XSS（跨站脚本攻击）
  - CSRF（跨站请求伪造）
  - 文件上传漏洞
  - 命令注入
- 认证 vs 授权
- 密码存储（加盐哈希）
- 敏感信息处理
- API 安全最佳实践
- 安全编码规范

#### 08 性能优化
- 性能分析工具与方法
- 数据库查询优化
- JVM 调优
- 缓存策略（多级缓存、缓存穿透/击穿/雪崩）
- 异步处理
- 批量处理
- 性能测试

#### 09 编程范式
- 面向对象编程（OOP）
- 函数式编程（FP）
- 响应式编程（Reactive）

#### 10 并发与异步
- 并发编程基础
- 线程安全
- 锁机制
- 异步编程模型
- 协程

#### 11 错误处理与韧性
- 异常设计
- 全局异常处理
- 重试策略
- 超时控制
- 优雅降级
- 断路器模式

#### 12 团队协作与工程化
**版本控制**
- Git 工作流（Git Flow、GitHub Flow、Trunk-Based）
- 提交规范（Conventional Commits）
- 分支管理策略

**协作流程**
- 代码评审（Code Review）
- 结对编程
- 技术文档写作
- 架构决策记录（ADR）

**敏捷开发**
- Scrum
- Kanban
- 用户故事
- 需求拆分
- 估时技巧
- 站会、计划会、回顾会

#### 13 运维与部署（DevOps）
- CI/CD 流程
- Docker 容器化
- Kubernetes 编排
- 日志收集与分析
- 监控告警
- 链路追踪
- 配置中心
- 灾备与恢复

#### 14 网络基础
- HTTP/HTTPS 协议
- TCP/IP 协议
- DNS 解析
- 反向代理 vs 正向代理
- CDN
- WebSocket

#### 15 可观测性
- 日志规范（分级、格式、脱敏）
- 指标监控（Metrics）
- 链路追踪（Tracing）
- 告警策略
- 排查问题的方法论

#### 16 其他工程实践
- 配置管理
- 环境管理（开发/测试/生产）
- 国际化（i18n）
- 特性开关（Feature Toggle）
- 消息队列选型与使用
- 定时任务设计

---

### 当前目录
```text
docs/
├── MindMap.md                                   # 思维导图索引
│
├── 01-design-principles/                        # 设计原则 ✅ 已完成
│   ├── 01-single-responsibility-principle/      # 单一职责原则 ✅
│   │   ├── doc_01.md                            # 教学文档
│   │   ├── demo/                                # 代码示例
│   │   │   ├── README.md                        # 运行说明
│   │   │   ├── BadExample.java                  # 违反SRP的示例
│   │   │   └── GoodExample.java                 # 符合SRP的示例
│   │   ├── test_01.md                           # 自测题（8道题，100分）
│   │   └── note_template.md                     # 学习笔记模板
│   │
│   ├── 02-open-closed-principle/                # 开闭原则 ✅
│   │   ├── doc_01.md                            # 教学文档
│   │   ├── demo/                                # 代码示例
│   │   │   ├── README.md                        # 运行说明
│   │   │   ├── BadExample.java                  # 违反OCP的示例
│   │   │   └── GoodExample.java                 # 符合OCP的示例
│   │   ├── test_01.md                           # 自测题（7道题，100分）
│   │   └── note_template.md                     # 学习笔记模板
│   │
│   ├── 03-liskov-substitution-principle/        # 里氏替换原则 ✅
│   │   ├── doc_01.md                            # 教学文档
│   │   ├── demo/                                # 代码示例
│   │   │   ├── README.md                        # 运行说明
│   │   │   ├── BadExample.java                  # 违反LSP的示例（正方形-矩形问题）
│   │   │   └── GoodExample.java                 # 符合LSP的示例
│   │   ├── test_01.md                           # 自测题（7道题，100分）
│   │   └── note_template.md                     # 学习笔记模板
│   │
│   ├── 04-interface-segregation-principle/      # 接口隔离原则 ✅
│   │   ├── doc_01.md                            # 教学文档
│   │   ├── demo/                                # 代码示例
│   │   │   ├── README.md                        # 运行说明
│   │   │   ├── BadExample.java                  # 违反ISP的示例（胖接口问题）
│   │   │   └── GoodExample.java                 # 符合ISP的示例
│   │   ├── test_01.md                           # 自测题（6道题，100分）
│   │   └── note_template.md                     # 学习笔记模板
│   │
│   └── 05-dependency-inversion-principle/       # 依赖倒置原则 ✅
│       ├── doc_01.md                            # 教学文档
│       ├── demo/                                # 代码示例
│       │   ├── README.md                        # 运行说明
│       │   ├── BadExample.java                  # 违反DIP的示例
│       │   └── GoodExample.java                 # 符合DIP的示例（依赖注入）
│       ├── test_01.md                           # 自测题（5道题，100分）
│       └── note_template.md                     # 学习笔记模板
│
└── 02-design-patterns/                          # 设计模式（23种经典模式）
    ├── 01-singleton-pattern/                    # 单例模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   └── SingletonDemo.java               # 6种实现方式对比
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 02-factory-pattern/                      # 工厂模式 ✅
    │   ├── doc_01.md                            # 简单工厂 + 工厂方法
    │   ├── doc_02.md                            # 抽象工厂
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── SimpleFactoryDemo.java           # 简单工厂示例（图形系统）
    │   │   ├── FactoryMethodDemo.java           # 工厂方法示例（支付系统）
    │   │   └── AbstractFactoryDemo.java         # 抽象工厂示例（数据库访问层）
    │   ├── test_01.md                           # 自测题（7道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 03-builder-pattern/                      # 建造者模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── BasicBuilderDemo.java            # 经典建造者（电脑组装）
    │   │   ├── ChainBuilderDemo.java            # 链式建造者（SQL+User）
    │   │   └── DirectorBuilderDemo.java         # Director模式（套餐构建）
    │   ├── test_01.md                           # 自测题（7道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 04-prototype-pattern/                    # 原型模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── ShallowCopyDemo.java             # 浅拷贝问题演示
    │   │   ├── DeepCopyDemo.java                # 深拷贝三种实现
    │   │   └── PrototypeRegistryDemo.java       # 原型注册表
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 05-adapter-pattern/                      # 适配器模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── PaymentAdapterDemo.java          # 支付系统适配器
    │   │   ├── MediaPlayerDemo.java             # 媒体播放器适配器
    │   │   └── ClassVsObjectAdapterDemo.java    # 类适配器vs对象适配器
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 06-decorator-pattern/                    # 装饰器模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── CoffeeDecoratorDemo.java         # 咖啡装饰器
    │   │   ├── TextDecoratorDemo.java           # 文本装饰器
    │   │   └── IoStreamDemo.java                # I/O流装饰器
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 07-proxy-pattern/                        # 代理模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── VirtualProxyDemo.java            # 虚拟代理（懒加载）
    │   │   ├── ProtectionProxyDemo.java         # 保护代理（权限控制）
    │   │   └── CacheProxyDemo.java              # 缓存代理（性能优化）
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 08-facade-pattern/                       # 外观模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── HomeTheaterFacadeDemo.java       # 家庭影院外观
    │   │   ├── OrderFacadeDemo.java             # 订单处理外观
    │   │   └── DatabaseFacadeDemo.java          # 数据库操作外观
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 09-bridge-pattern/                       # 桥接模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── RemoteControlBridgeDemo.java     # 遥控器与电视
    │   │   └── ShapeBridgeDemo.java             # 形状与颜色
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    ├── 10-composite-pattern/                    # 组合模式 ✅
    │   ├── doc_01.md                            # 教学文档
    │   ├── demo/                                # 代码示例
    │   │   ├── README.md                        # 运行说明
    │   │   ├── FileSystemDemo.java              # 文件系统示例
    │   │   └── CompanyStructureDemo.java        # 公司组织架构示例
    │   ├── test_01.md                           # 自测题（6道题，100分）
    │   └── note_template.md                     # 学习笔记模板
    │
    └── 11-flyweight-pattern/                    # 享元模式 ✅
        ├── doc_01.md                            # 教学文档
        ├── demo/                                # 代码示例
        │   ├── README.md                        # 运行说明
        │   ├── ChessPieceDemo.java              # 围棋棋子示例
        │   └── TextEditorDemo.java              # 文本编辑器示例
        ├── test_01.md                           # 自测题（7道题，100分）
        └── note_template.md                     # 学习笔记模板
```

### 规范 && 指南 && 建议

#### 目录规范
1. 文档类放在docs目录下
2. 代码类尚未组织

#### 代码文件规范
1. 放在src/main下面
2. 具体目录自行组织，建议按照模块分(所学知识点)

#### 建议的文件类型
1. 文档(doc)
2. 笔记(note) - 用来输出的。——光是吃可不行啊~
3. 题目(test) - 检测知识是否过关。——五年高考三年模拟
4. easySay - 将知识点用简单的大白话讲一遍(写一遍)。如果自己都不能流畅说出来，那就算不过关；如果别人不能理解，那也算不过关

备注： 
- 不要指望一两篇文档就能学会
- 自己输出的笔记远比文档重要 
- 题目要有难度才对，没难度没用
- 可以试试easySay


#### 建议的目录命令
1. 知识点: 0x-知识点/

#### 建议的文件命名(对于一个写不完的情况)
1. 文档: 知识点_0x.md
2. 笔记: note_0x.md
3. 题目: test_0x.md

> 总之遵循高内聚、低耦合的原则就行(wink~

> ~~当然，如果你觉得顺手一放很舒服，那也完全没问题~~


#### Other
> 项目已经初始化好了，包含一些轻型依赖，不包含数据库依赖。
> 你可以屏蔽或者删除pom.xml来获取一个纯净的项目👍

