# API 安全 - 认证与鉴权

## 一、认证 vs 鉴权

这是两个经常被混淆的概念，但它们解决的是不同的问题。

### 用机场安检类比

**认证（Authentication）**：验证身份证
- 问题：**你是谁？**
- 机场安检：检查你的身份证、护照，确认你的身份
- API：检查用户名密码、Token，确认是哪个用户

**鉴权（Authorization）**：检查登机牌
- 问题：**你能做什么？**
- 机场安检：检查你的登机牌，确认你能登哪个航班、坐哪个座位
- API：检查权限，确认你能访问哪些资源、执行哪些操作

### 实际场景

```
用户张三请求删除文章：

第一步 - 认证（Authentication）：
客户端: DELETE /articles/100
        Authorization: Bearer eyJhbGc...
服务端: 解析Token → 确认是"张三"

第二步 - 鉴权（Authorization）：
服务端: 检查张三是否有权限删除文章100
        → 查询数据库：文章100的作者是"李四"
        → 张三不是作者，也不是管理员
        → 返回 403 Forbidden
```

### 完整流程图

```mermaid
graph TD
    A[客户端请求] --> B{是否提供认证信息?}
    B -->|否| C[401 Unauthorized<br/>未认证]
    B -->|是| D{认证信息是否有效?}
    D -->|否| C
    D -->|是| E[认证通过<br/>确认用户身份]
    E --> F{用户是否有权限?}
    F -->|否| G[403 Forbidden<br/>无权限]
    F -->|是| H[200 OK<br/>执行操作]
    
    style C fill:#ffcccc
    style G fill:#ffcccc
    style H fill:#90EE90
```

### 关键区别

| | 认证（Authentication） | 鉴权（Authorization） |
|---|---|---|
| **问题** | 你是谁？ | 你能做什么？ |
| **验证内容** | 身份凭证（密码、Token） | 权限规则 |
| **失败状态码** | 401 Unauthorized | 403 Forbidden |
| **示例** | 登录验证 | 检查是否是管理员 |

## 二、常见认证方式

### 方式1：Basic Auth（基础认证）

**工作原理**：
```
1. 客户端发送用户名和密码（Base64编码）
2. 服务端验证用户名密码

请求示例：
GET /api/users
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
              # username:password 的Base64编码
```

**优点**：
- 简单易实现
- HTTP标准支持

**缺点**：
- Base64不是加密，只是编码（容易被解码）
- 每次请求都要传输用户名密码（不安全）
- 必须配合HTTPS使用

**使用场景**：
- 内部工具、临时API
- 配合HTTPS的简单场景

### 方式2：API Key

**工作原理**：
```
1. 服务端为每个客户端生成一个API Key
2. 客户端在请求中携带API Key
3. 服务端验证API Key

请求示例：
GET /api/users
X-API-Key: abc123def456

或
GET /api/users?apiKey=abc123def456
```

**优点**：
- 简单
- 易于管理（可随时撤销Key）
- 适合服务端到服务端（Server-to-Server）

**缺点**：
- 无法识别具体用户（只知道是哪个应用）
- Key泄露风险
- 无过期机制（需要手动实现）

**使用场景**：
- 第三方API调用（如天气API、地图API）
- 微服务间认证

### 方式3：Session-Cookie

**工作原理**：
```
1. 用户登录成功，服务端创建Session，返回Session ID
2. 客户端保存Session ID到Cookie
3. 后续请求自动携带Cookie
4. 服务端根据Session ID查找Session数据

登录：
POST /login
Body: {username: "zhangsan", password: "123456"}
→ 服务端创建Session，返回：Set-Cookie: sessionId=abc123

后续请求：
GET /api/users
Cookie: sessionId=abc123
```

**优点**：
- 服务端完全控制（可随时使Session失效）
- 浏览器自动处理Cookie

**缺点**：
- 服务端需要存储Session（内存或Redis）
- 难以横向扩展（多台服务器需要共享Session）
- 不适合移动端（移动App不使用Cookie）
- CSRF攻击风险

**使用场景**：
- 传统Web应用（前后端不分离）
- 需要服务端控制会话的场景

### 方式4：JWT（JSON Web Token）⭐ 推荐

**工作原理**：
```
1. 用户登录成功，服务端生成JWT返回
2. 客户端保存JWT（localStorage或内存）
3. 后续请求在Header中携带JWT
4. 服务端验证JWT签名，解析用户信息

登录：
POST /login
Body: {username: "zhangsan", password: "123456"}
→ 返回：{token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}

后续请求：
GET /api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**优点**：
- 无状态（服务端不需要存储）
- 易于扩展（多台服务器共享密钥即可）
- 适合前后端分离、移动端
- 跨域友好

**缺点**：
- Token无法主动失效（除非引入黑名单）
- Token体积较大（包含用户信息）
- 需要妥善保管密钥

**使用场景**：
- 前后端分离应用
- 移动App
- 微服务架构

### 方式5：OAuth 2.0

**工作原理**：
```
用于第三方授权登录（如"使用微信登录"）

1. 用户点击"使用微信登录"
2. 跳转到微信授权页面
3. 用户同意授权
4. 微信返回授权码（code）
5. 应用用code换取Access Token
6. 用Access Token访问微信API获取用户信息
```

**优点**：
- 用户无需在第三方应用注册
- 应用无需保存用户密码
- 标准化协议

**缺点**：
- 实现复杂
- 依赖第三方服务

**使用场景**：
- 第三方登录（微信、GitHub、Google）
- 开放平台API

### 认证方式对比

| 方式 | 无状态 | 适合场景 | 安全性 | 复杂度 |
|-----|-------|---------|-------|-------|
| **Basic Auth** | ✅ | 内部工具 | ⭐⭐ | ⭐ |
| **API Key** | ✅ | 服务端调用 | ⭐⭐⭐ | ⭐ |
| **Session-Cookie** | ❌ | 传统Web | ⭐⭐⭐ | ⭐⭐ |
| **JWT** | ✅ | 前后端分离 | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **OAuth 2.0** | ✅ | 第三方登录 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

## 三、JWT 深入讲解

JWT是目前最流行的认证方案，值得深入了解。

### JWT 结构

JWT由三部分组成，用 `.` 分隔：

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

Header.Payload.Signature
```

#### Part 1: Header（头部）

```json
{
  "alg": "HS256",    // 签名算法
  "typ": "JWT"       // Token类型
}
```

Base64编码后：`eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9`

#### Part 2: Payload（载荷）

```json
{
  "sub": "1234567890",           // Subject（用户ID）
  "name": "John Doe",            // 用户名
  "admin": true,                 // 自定义字段
  "iat": 1516239022,             // Issued At（签发时间）
  "exp": 1516242622              // Expiration Time（过期时间）
}
```

Base64编码后：`eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikpva...`

**标准字段**：
- `iss`（Issuer）：签发者
- `sub`（Subject）：主题（通常是用户ID）
- `aud`（Audience）：受众
- `exp`（Expiration Time）：过期时间
- `iat`（Issued At）：签发时间
- `jti`（JWT ID）：JWT的唯一标识

#### Part 3: Signature（签名）

```
签名算法：
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**作用**：防止Token被篡改

### JWT 工作流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant S as 服务端
    
    Note over C,S: 1. 登录
    C->>S: POST /login<br/>{username, password}
    S->>S: 验证用户名密码
    S->>S: 生成JWT<br/>{userId, name, exp}
    S-->>C: 返回JWT
    
    Note over C,S: 2. 访问受保护资源
    C->>S: GET /api/users<br/>Authorization: Bearer JWT
    S->>S: 验证JWT签名
    S->>S: 检查是否过期
    S->>S: 解析用户信息
    S->>S: 检查权限
    S-->>C: 返回资源
```

### JWT 优缺点

**优点**：

1. **无状态**
   - 服务端不需要存储Session
   - 易于横向扩展

2. **自包含**
   - Token中包含用户信息
   - 减少数据库查询

3. **跨域友好**
   - 不依赖Cookie
   - 适合前后端分离

4. **适合分布式**
   - 多台服务器共享密钥即可
   - 无需Session共享

**缺点**：

1. **无法主动失效**
   - Token签发后，在过期前一直有效
   - 解决方案：引入黑名单（但失去无状态优势）

2. **Token体积大**
   - 包含用户信息，比Session ID大
   - 每次请求都要传输

3. **续签问题**
   - Token过期后需要重新登录
   - 解决方案：Refresh Token机制

### JWT 安全注意事项

#### ⚠️ 不要存储敏感信息

```
❌ 错误示范：
{
  "userId": 1,
  "password": "123456",        // 密码！
  "creditCard": "1234-5678"    // 信用卡！
}

✅ 正确做法：
{
  "userId": 1,
  "username": "zhangsan",
  "role": "admin"
}
```

**原因**：Payload只是Base64编码，不是加密，任何人都能解码查看。

#### ⚠️ 设置合理的过期时间

```
短期Token（Access Token）：
- 过期时间：15分钟 - 1小时
- 用于日常API请求

长期Token（Refresh Token）：
- 过期时间：7天 - 30天
- 仅用于刷新Access Token
```

#### ⚠️ 妥善保管密钥

```
❌ 不要硬编码：
String secret = "my-secret-key";

✅ 从环境变量读取：
String secret = System.getenv("JWT_SECRET");

✅ 使用足够长的密钥：
至少256位（32字节）
```

#### ⚠️ 使用HTTPS

```
HTTP传输：JWT可能被截获（中间人攻击）
HTTPS传输：加密传输，安全
```

### Refresh Token 机制

解决"Token过期需要重新登录"的问题。

```mermaid
sequenceDiagram
    participant C as 客户端
    participant S as 服务端
    
    Note over C,S: 1. 登录
    C->>S: POST /login
    S-->>C: Access Token (15分钟)<br/>Refresh Token (7天)
    
    Note over C,S: 2. 正常请求
    C->>S: GET /api/users<br/>Authorization: Bearer AccessToken
    S-->>C: 返回数据
    
    Note over C,S: 3. Access Token过期
    C->>S: GET /api/users<br/>Authorization: Bearer AccessToken(已过期)
    S-->>C: 401 Token Expired
    
    Note over C,S: 4. 刷新Token
    C->>S: POST /refresh<br/>Body: {refreshToken}
    S->>S: 验证Refresh Token
    S-->>C: 新的Access Token (15分钟)
    
    Note over C,S: 5. 使用新Token
    C->>S: GET /api/users<br/>Authorization: Bearer 新AccessToken
    S-->>C: 返回数据
```

**实现要点**：
- Access Token存内存（不持久化）
- Refresh Token存安全位置（如HttpOnly Cookie）
- Refresh Token使用后立即失效（一次性）

## 四、OAuth 2.0 简介

OAuth 2.0是一个授权框架，主要用于第三方登录。

### 使用场景

```
场景：你开发了一个博客网站，想让用户用GitHub账号登录

传统方式：
- 用户在你的网站注册
- 你需要存储用户密码

OAuth 2.0方式：
- 用户点击"使用GitHub登录"
- GitHub验证用户身份
- GitHub授权你的网站访问用户基本信息
- 你的网站无需存储用户密码
```

### OAuth 2.0 四种授权模式

#### 1. 授权码模式（Authorization Code）⭐ 最常用

```
适用场景：Web应用、移动App

流程：
1. 用户点击"使用GitHub登录"
2. 跳转到GitHub授权页
3. 用户同意授权
4. GitHub返回授权码（code）
5. 应用用code换取Access Token
6. 用Access Token访问GitHub API
```

#### 2. 简化模式（Implicit）

```
适用场景：纯前端应用（已不推荐）

流程：
1-3. 同上
4. GitHub直接返回Access Token（跳过授权码）

缺点：Token暴露在URL中，不安全
```

#### 3. 密码模式（Password）

```
适用场景：高度信任的应用（已不推荐）

流程：
用户直接在你的应用输入GitHub用户名密码
你的应用直接用用户名密码换取Token

缺点：违背OAuth初衷（避免第三方应用接触密码）
```

#### 4. 客户端模式（Client Credentials）

```
适用场景：服务端到服务端（无用户参与）

流程：
应用用自己的凭证（Client ID + Secret）换取Token
访问不涉及用户的资源
```

### OAuth 2.0 vs JWT

这是两个不同层面的东西：

| | OAuth 2.0 | JWT |
|---|-----------|-----|
| **类型** | 授权框架 | Token格式 |
| **解决问题** | 第三方授权 | 认证信息传输 |
| **关系** | OAuth可以使用JWT作为Token格式 | JWT可以用于OAuth的Access Token |

**结合使用**：
```
1. 用户通过OAuth 2.0授权
2. 服务端生成JWT格式的Access Token
3. 客户端用JWT访问API
```

## 五、HTTPS/TLS

### 为什么需要HTTPS？

**HTTP的问题**：
```
客户端 --[明文传输]--> 服务端

中间人可以：
1. 窃听：看到用户名密码、Token
2. 篡改：修改请求内容
3. 冒充：伪造服务端
```

**HTTPS的保护**：
```
客户端 --[加密传输]--> 服务端

中间人无法：
1. 窃听：数据已加密
2. 篡改：篡改后无法解密
3. 冒充：需要合法证书
```

### HTTPS 工作原理（简化）

```mermaid
sequenceDiagram
    participant C as 客户端
    participant S as 服务端
    
    Note over C,S: 1. 握手
    C->>S: 你好，我支持这些加密算法
    S->>C: 我选择这个算法<br/>这是我的证书
    C->>C: 验证证书是否合法
    C->>S: 生成密钥，用你的公钥加密发给你
    S->>S: 用私钥解密，得到密钥
    
    Note over C,S: 2. 加密通信
    C->>S: 用密钥加密请求
    S->>C: 用密钥加密响应
```

### API安全的必要性

```
✅ 必须使用HTTPS的场景：
- 传输敏感信息（密码、Token、支付信息）
- 公开互联网API
- 移动App后端
- 生产环境

⚠️ 可以用HTTP的场景：
- 本地开发
- 内网环境（但仍然推荐HTTPS）
```

## 六、使用场景总结

根据项目类型选择认证方案：

### 前后端分离Web应用 → JWT

```
技术栈：React/Vue + Spring Boot

认证流程：
1. 用户登录 → 返回JWT
2. 前端保存JWT到localStorage
3. 每次请求在Header中携带JWT
4. 后端验证JWT

优点：无状态、易扩展
```

### 传统Web应用 → Session-Cookie

```
技术栈：JSP + Spring MVC

认证流程：
1. 用户登录 → 创建Session
2. 浏览器自动保存Cookie
3. 后续请求自动携带Cookie
4. 后端根据Session ID查找Session

优点：服务端完全控制、安全
```

### 移动App → JWT

```
技术栈：Android/iOS + RESTful API

认证流程：
1. 用户登录 → 返回JWT
2. App保存JWT到安全存储
3. 每次请求在Header中携带JWT

优点：无状态、跨平台
```

### 第三方API调用 → API Key

```
场景：调用天气API、地图API

认证流程：
1. 在服务商平台申请API Key
2. 每次请求携带API Key

优点：简单、易管理
```

### 开放平台 → OAuth 2.0

```
场景：提供"使用XX登录"功能

认证流程：
1. 用户授权
2. 获取Access Token
3. 访问用户信息

优点：无需存储用户密码、标准化
```

## 七、小结

**核心要点**：

1. **认证 vs 鉴权**
   - 认证：验证身份（你是谁？）→ 401
   - 鉴权：验证权限（你能做什么？）→ 403

2. **认证方式选择**
   - 前后端分离/移动App → JWT
   - 传统Web → Session-Cookie
   - 第三方API → API Key
   - 第三方登录 → OAuth 2.0

3. **JWT 核心**
   - 结构：Header.Payload.Signature
   - 优点：无状态、易扩展
   - 注意：不存敏感信息、设置过期时间、用HTTPS

4. **HTTPS必不可少**
   - 加密传输
   - 防止中间人攻击

---

**下一步**：继续学习 `doc_02.md`，了解常见安全威胁与防护措施。

💡 **提示**：认证是API安全的第一道防线，但还不够。后续还需要学习如何防御各种攻击。
