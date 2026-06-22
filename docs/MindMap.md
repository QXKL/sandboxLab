## MindMap - 思维导图

> 这里是整个项目的目录兼索引
> 
> 当然，也包含了规范、指南和建议

---

### 目录
```text


```


<details>
<summary>目录示例(用于指导目录的编写)</summary>
```text
sandboxLab/
├── docs/                                    # 文档根目录
│   ├── MindMap.md                           # 思维导图索引
│   │
│   ├── 01-design-patterns/                  # 设计模式
│   │   ├── 01-strategy-pattern/             # 策略模式
│   │   │   ├── doc_1.md                       # 文档(命名规范看下面)
│   │   │   ├── doc_2.md                       # 文档(一篇讲不完就再来一篇, 笔记和自测题同理)
│   │   │   ├── note.md                      # 笔记
│   │   │   └── test.md                      # 自测题
│   │   ├── 02-observer-pattern/             # 观察者模式
│   │   ├── 03-singleton-pattern/            # 单例模式
│   │   └── ...                              # 更多模式
│   │
│   ├── 02-restful-api/                      # RESTful 规范
│   │   ├── 01-resource-naming/              # 资源命名
│   │   ├── 02-http-methods/                 # HTTP 方法语义
│   │   ├── 03-status-codes/                 # 状态码
│   │   └── ...
│   │
│   ├── 03-architecture/                     # 架构设计
│   │   ├── 01-monolith-vs-micro/            # 单体 vs 微服务
│   │   ├── 02-layered-architecture/         # 分层架构
│   │   └── ...
│   │
│   ├── 04-solid-principles/                 # SOLID 原则
│   │   ├── 01-srp/                          # 单一职责
│   │   ├── 02-ocp/                          # 开闭原则
│   │   └── ...
│   │
│   ├── 05-team-collaboration/               # 团队协作
│   │   ├── 01-git-workflow/                 # Git 工作流
│   │   ├── 02-agile-scrum/                  # 敏捷/Scrum
│   │   └── ...
│   │
│   └── 06-.../                              # 后续新增主题
│
├── src/                                     # 代码（暂未组织）
│   ├── main/
│   │   └── java/
│   └── test/
│       └── java/
│
├── pom.xml                                   # Maven 配置
└── README.md                                 # 项目说明
```
</details>

### 规范 && 指南 && 建议
> 当前项目仅有docs、readme.md和mindmap.md

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

