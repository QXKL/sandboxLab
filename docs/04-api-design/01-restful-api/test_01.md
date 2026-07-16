# RESTful API 设计 - 自测题

完成文档学习和代码示例后，通过以下题目检验学习效果。

**总分：100分**

---

## 一、概念理解（选择题，每题8分，共40分）

### 1. REST架构的核心思想是什么？

A. 使用JSON格式传输数据  
B. 用URL定位资源，用HTTP方法描述操作  
C. 前后端分离  
D. 使用微服务架构  

<details>
<summary>查看答案</summary>

**答案**：B

**解析**：REST（Representational State Transfer）的核心是资源思维：
- URL用于定位资源（如 `/users/1`）
- HTTP方法用于描述对资源的操作（GET查询、POST创建、PUT更新、DELETE删除）
- JSON只是表述格式之一，不是核心
- 前后端分离和微服务是应用场景，不是REST本身的定义

</details>

---

### 2. 以下哪个HTTP方法是非幂等的？

A. GET  
B. PUT  
C. POST  
D. DELETE  

<details>
<summary>查看答案</summary>

**答案**：C

**解析**：
- **幂等**：多次执行相同操作，结果相同
  - GET：查询操作，不改变状态，幂等
  - PUT：完整更新，多次更新到相同内容，结果相同，幂等
  - DELETE：删除后资源不存在，再次删除结果不变（仍然不存在），幂等
- **非幂等**：
  - POST：创建操作，每次执行创建一个新资源，结果不同

**幂等性的重要性**：网络不稳定时，客户端可以安全地重试幂等请求，而不用担心产生副作用。

</details>

---

### 3. 创建用户成功后，应该返回什么HTTP状态码？

A. 200 OK  
B. 201 Created  
C. 204 No Content  
D. 202 Accepted  

<details>
<summary>查看答案</summary>

**答案**：B

**解析**：
- **200 OK**：通用成功响应，适用于GET、PUT、PATCH
- **201 Created**：资源创建成功，专用于POST创建操作
  - 应该在响应Header中包含 `Location: /users/123` 指向新资源
  - 响应体通常包含创建后的完整资源
- **204 No Content**：成功但无响应体，适用于DELETE
- **202 Accepted**：请求已接受但未完成处理（异步操作）

**最佳实践**：POST创建成功使用201，明确表达"已创建"的语义。

</details>

---

### 4. PUT和PATCH的主要区别是什么？

A. PUT用于创建，PATCH用于更新  
B. PUT是幂等的，PATCH不是  
C. PUT是完整更新，PATCH是部分更新  
D. PUT用于单个资源，PATCH用于批量资源  

<details>
<summary>查看答案</summary>

**答案**：C

**解析**：
- **PUT**：完整更新（替换）
  ```
  PUT /users/1
  Body: {"name": "张三", "email": "zhangsan@example.com"}
  → 必须提供所有字段，缺失的字段会被设为null或默认值
  ```

- **PATCH**：部分更新
  ```
  PATCH /users/1
  Body: {"email": "newemail@example.com"}
  → 只更新提供的字段，其他字段不变
  ```

**补充**：
- PUT和PATCH都是幂等的（选项B错误）
- PUT不用于创建（通常由POST负责，选项A错误）
- 两者都针对单个资源（选项D错误）

</details>

---

### 5. 以下哪个API设计符合RESTful规范？

A. `GET /deleteUser?id=1`  
B. `POST /users/create`  
C. `DELETE /users/1`  
D. `GET /getUserList`  

<details>
<summary>查看答案</summary>

**答案**：C

**解析**：
- **A**: `GET /deleteUser?id=1` ❌
  - 用GET做删除操作（危险！搜索引擎爬虫会触发）
  - 应该用 `DELETE /users/1`

- **B**: `POST /users/create` ❌
  - URL中有动词 `create`，冗余
  - 应该用 `POST /users`（POST本身就表示创建）

- **C**: `DELETE /users/1` ✅
  - 用名词表示资源（users）
  - 用HTTP方法表示操作（DELETE）
  - 符合REST规范

- **D**: `GET /getUserList` ❌
  - URL中有动词 `get`
  - 应该用 `GET /users`

**记忆口诀**：URL用名词，操作靠方法。

</details>

---

## 二、实践应用（设计题，每题15分，共30分）

### 6. 设计博客系统的RESTful API

**需求**：
- 文章（Article）：创建、查询、更新、删除
- 评论（Comment）：为文章添加评论、查询文章的评论、删除评论
- 标签（Tag）：为文章添加标签、查询某标签下的所有文章

请设计完整的API端点（HTTP方法 + URL）。

<details>
<summary>参考答案</summary>

```
【文章管理】
GET    /articles              # 获取文章列表
GET    /articles/{id}         # 获取单篇文章
POST   /articles              # 创建文章
PUT    /articles/{id}         # 更新文章
DELETE /articles/{id}         # 删除文章

【评论管理】
GET    /articles/{id}/comments       # 获取文章的所有评论
POST   /articles/{id}/comments       # 为文章添加评论
DELETE /comments/{id}                # 删除评论（独立访问）

【标签管理】
GET    /tags                         # 获取所有标签
GET    /tags/{id}/articles           # 获取某标签下的所有文章
POST   /articles/{id}/tags           # 为文章添加标签
DELETE /articles/{id}/tags/{tagId}  # 移除文章的某个标签
```

**要点**：
1. **嵌套资源**：评论属于文章，用 `/articles/{id}/comments`
2. **独立访问**：如果评论可以独立管理，也提供 `/comments/{id}`
3. **多对多关系**：文章-标签，可以从两个方向查询
   - 某标签的文章：`/tags/{id}/articles`
   - 某文章的标签：`/articles/{id}/tags`

**扩展**：如果需要查询参数
```
GET /articles?status=published&tag=技术&sort=-createdAt&page=1&size=10
```

</details>

---

### 7. 分析并改进以下API设计

**现有设计**（有问题）：
```
POST /api/getUserInfo?userId=1
POST /api/updateUserPassword
GET  /api/deleteUserAccount?userId=1
POST /api/searchUsers?keyword=zhang&page=1
```

**任务**：
1. 指出每个接口的问题
2. 给出改进后的设计

<details>
<summary>参考答案</summary>

**问题分析**：

1. `POST /api/getUserInfo?userId=1`
   - ❌ 查询操作用了POST（应该用GET）
   - ❌ URL中有动词 `get`
   - ❌ 参数用查询字符串，应该用路径参数

2. `POST /api/updateUserPassword`
   - ❌ URL中有动词 `update`
   - ❌ 没有指定是哪个用户
   - ❌ 更新操作应该用PUT或PATCH

3. `GET /api/deleteUserAccount?userId=1`
   - ❌ 删除操作用了GET（非常危险！）
   - ❌ URL中有动词 `delete`
   - ❌ 应该用DELETE方法

4. `POST /api/searchUsers?keyword=zhang&page=1`
   - ❌ 搜索是查询操作，应该用GET
   - ❌ URL中有动词 `search`

**改进设计**：

```
# 1. 获取用户信息
GET /users/1

# 2. 更新用户密码（部分更新）
PATCH /users/1/password
或
PATCH /users/1
Body: {"password": "newPassword"}

# 3. 删除用户账户
DELETE /users/1

# 4. 搜索用户
GET /users?search=zhang&page=1
或
GET /users/search?q=zhang&page=1  # 如果搜索逻辑复杂，可以单独端点
```

**核心改进**：
- 用正确的HTTP方法表示操作
- URL用名词表示资源
- 用路径参数标识具体资源
- 查询参数用于过滤和分页

</details>

---

## 三、场景分析（判断题，每题15分，共30分）

### 8. API版本管理场景

**场景**：
你正在维护一个用户管理API，当前版本返回：
```json
GET /users/1
{
  "id": 1,
  "name": "张三",
  "email": "zhangsan@example.com"
}
```

现在需求变更，需要拆分 `name` 字段为 `firstName` 和 `lastName`：
```json
{
  "id": 1,
  "firstName": "三",
  "lastName": "张",
  "email": "zhangsan@example.com"
}
```

**问题**：
1. 这个变更是否需要升级API版本？为什么？
2. 如果需要升级，你会选择哪种版本管理方案？
3. 如何平滑过渡，让老客户端不受影响？

<details>
<summary>参考答案</summary>

**1. 是否需要升级版本？**

**需要升级到v2**。

**原因**：
- 删除了 `name` 字段（破坏性变更）
- 老客户端依赖 `name` 字段，删除后会导致客户端报错
- 这不是向后兼容的改动

**向后兼容 vs 破坏性变更对比**：
```
✅ 向后兼容（不需要升级版本）：
- 新增字段：{"name": "张三", "age": 25}  # 新增age
- 新增可选参数

❌ 破坏性变更（需要升级版本）：
- 删除字段
- 重命名字段
- 改变字段类型
- 改变行为语义
```

**2. 版本管理方案选择**

**推荐：URL路径版本**

```
v1: GET /v1/users/1
→ {"id": 1, "name": "张三", "email": "..."}

v2: GET /v2/users/1
→ {"id": 1, "firstName": "三", "lastName": "张", "email": "..."}
```

**理由**：
- 清晰直观，测试方便
- 易于路由和缓存
- 适合公开API

**3. 平滑过渡策略**

**时间线**：
```
2026-01-01: v1上线
2026-07-01: v2上线，v1继续可用
2026-07-01: v1标记为Deprecated（废弃）
2027-01-01: v1下线
```

**技术实现**：
```
# v1响应中提示即将废弃
GET /v1/users/1

HTTP/1.1 200 OK
Deprecation: true
Sunset: Sun, 01 Jan 2027 00:00:00 GMT
Link: <https://api.example.com/docs/v2-migration>; rel="deprecation"

{
  "id": 1,
  "name": "张三",
  "email": "zhangsan@example.com"
}
```

**沟通策略**：
- 提前6个月发邮件通知
- 在文档中标注废弃信息
- 提供迁移指南
- 监控v1的使用量，评估下线时机

</details>

---

### 9. 分页方案选择

**场景1：电商后台管理系统**
- 管理员需要查看所有订单
- 订单列表需要支持跳页功能
- 数据量：约5万条订单
- 更新频率：每天新增100-200条

**场景2：社交媒体Feed流**
- 用户刷首页动态
- 只需要"下一页"功能，不需要跳页
- 数据量：百万级
- 更新频率：每秒新增大量内容

**问题**：
1. 两个场景分别适合用哪种分页方案？
2. 请设计具体的API端点和响应格式

<details>
<summary>参考答案</summary>

**1. 方案选择**

**场景1：电商后台管理 → Page-based分页**

**理由**：
- 需要跳页功能（管理员可能直接跳到第50页）
- 数据量不大（5万条）
- 更新频率低（数据相对稳定）
- 偶尔的重复/遗漏可以接受（管理员可以刷新）

**场景2：社交媒体Feed → Cursor-based分页**

**理由**：
- 只需要"下一页"，不需要跳页
- 数据量大（百万级，深度分页性能差）
- 更新频繁（每秒新增内容）
- 需要保证一致性（用户不希望刷新时看到重复内容）

**2. API设计**

**场景1：Page-based分页**

```
请求：
GET /orders?page=1&size=20&status=completed&sort=-createdAt

响应：
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1001,
      "status": "completed",
      "amount": 299.00,
      "createdAt": "2026-07-15T10:30:00Z"
    },
    ...
  ],
  "pagination": {
    "page": 1,
    "size": 20,
    "totalPages": 2500,
    "totalCount": 50000,
    "hasNext": true,
    "hasPrev": false
  }
}
```

**关键字段**：
- `totalPages`：总页数（支持跳页）
- `totalCount`：总数量（显示"共50000条"）
- `hasNext/hasPrev`：是否有下一页/上一页

**场景2：Cursor-based分页**

```
第一次请求：
GET /feed?limit=20

响应：
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 9001,
      "content": "今天天气真好",
      "author": "张三",
      "createdAt": "2026-07-16T10:30:00Z"
    },
    ...
  ],
  "pagination": {
    "nextCursor": "eyJpZCI6OTAwMSwiY3JlYXRlZEF0IjoiMjAyNi0wNy0xNlQxMDozMDowMFoifQ==",
    "hasMore": true
  }
}

下一页请求：
GET /feed?cursor=eyJpZCI6OTAwMSwiY3JlYXRlZEF0IjoiMjAyNi0wNy0xNlQxMDozMDowMFoifQ==&limit=20
```

**Cursor设计**：
```
Cursor原始值：{"id": 9001, "createdAt": "2026-07-16T10:30:00Z"}
编码后：eyJpZCI6OTAwMSwiY3JlYXRlZEF0IjoiMjAyNi0wNy0xNlQxMDozMDowMFoifQ==

后端解码后，SQL查询：
SELECT * FROM posts
WHERE (createdAt, id) < ('2026-07-16T10:30:00Z', 9001)
ORDER BY createdAt DESC, id DESC
LIMIT 20;
```

**关键点**：
- 不返回 `totalCount`（无意义，数据量太大且实时变化）
- 只返回 `nextCursor` 和 `hasMore`
- Cursor包含排序字段（createdAt）和唯一标识（id）

**对比总结**：

| 特性 | Page-based | Cursor-based |
|-----|-----------|--------------|
| **跳页** | ✅ 支持 | ❌ 不支持 |
| **总数** | ✅ 提供 | ❌ 不提供 |
| **一致性** | ⚠️ 可能重复/遗漏 | ✅ 强一致性 |
| **性能** | ⚠️ 深度分页慢 | ✅ 性能稳定 |
| **适用场景** | 后台管理、报表 | Feed流、消息列表 |

</details>

---

## 评分标准

- **选择题（40分）**：每题8分，选对得分
- **设计题（30分）**：
  - 题6：API设计合理（10分）+ 考虑资源层级（5分）
  - 题7：问题识别准确（8分）+ 改进方案正确（7分）
- **场景题（30分）**：
  - 题8：版本判断正确（5分）+ 方案选择合理（5分）+ 过渡策略完整（5分）
  - 题9：方案选择正确（8分）+ API设计合理（7分）

**及格线：60分**  
**建议目标：80分以上**

---

## 自我检测清单

完成自测后，检查以下内容：

- [ ] 我能说出REST的核心思想（资源+HTTP方法）
- [ ] 我能区分幂等和非幂等操作
- [ ] 我知道何时用200、201、204、404、409状态码
- [ ] 我能区分PUT和PATCH
- [ ] 我能设计符合RESTful规范的API端点
- [ ] 我知道何时需要升级API版本
- [ ] 我能根据场景选择合适的分页方案
- [ ] 我理解HATEOAS的概念（即使不完全实现）

**如果以上8项都能做到，恭喜你已经掌握了RESTful API设计的核心知识！** 🎉

---

💡 **下一步**：填写 `note_template.md`，用自己的话总结学到的知识，并完成 easySay 环节。
