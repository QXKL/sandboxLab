# 事务隔离级别 - 自测题

完成文档学习和代码示例后，通过以下题目检验学习效果。

**总分：100分**

---

## 一、概念理解（选择题，每题10分，共40分）

### 1. ACID中的"I"代表什么？它解决什么问题？

A. Integrity（完整性），保证数据不丢失  
B. Isolation（隔离性），保证多个事务互不干扰  
C. Identity（标识性），保证每个事务有唯一ID  
D. Immutability（不可变性），保证事务不可修改  

<details>
<summary>查看答案</summary>

**答案**：B

**解析**：
- **I = Isolation（隔离性）**：保证并发执行的多个事务之间互不干扰
- **ACID完整含义**：
  - A = Atomicity（原子性）：要么全做，要么全不做
  - C = Consistency（一致性）：事务前后数据保持一致
  - I = Isolation（隔离性）：多个事务互不干扰
  - D = Durability（持久性）：提交后永久保存

**为什么需要隔离性**：
- 防止脏读、不可重复读、幻读等并发问题
- 保证事务执行结果的正确性

</details>

---

### 2. 以下哪种并发问题最严重？

A. 脏读（Dirty Read）  
B. 不可重复读（Non-Repeatable Read）  
C. 幻读（Phantom Read）  
D. 三者同样严重  

<details>
<summary>查看答案</summary>

**答案**：A

**解析**：

**脏读最严重**，因为读到的是**未提交**的数据，可能被回滚。

**危害对比**：

| 问题 | 读取的数据 | 危害程度 | 示例 |
|-----|----------|---------|------|
| **脏读** | 未提交的数据 | ⭐⭐⭐⭐⭐ | 读到余额500，但事务回滚了 |
| **不可重复读** | 已提交的数据 | ⭐⭐⭐ | 两次读到不同余额 |
| **幻读** | 已提交的数据 | ⭐⭐⭐ | 统计订单数量不准确 |

**脏读为什么最严重**：
- 基于错误数据做决策（该数据可能被回滚）
- 违反了事务的基本保证（只应该看到已提交的数据）
- 可能导致数据不一致

**示例**：
```
T1: UPDATE balance = 500（未提交）
T2: 读到500，发送短信"您的余额为500"
T1: ROLLBACK（回滚）
→ 用户收到错误的短信，实际余额是1000
```

</details>

---

### 3. MySQL InnoDB的默认隔离级别是什么？为什么选择它？

A. Read Uncommitted - 性能最好  
B. Read Committed - 和Oracle一致  
C. Repeatable Read - 平衡性能与一致性  
D. Serializable - 最安全  

<details>
<summary>查看答案</summary>

**答案**：C

**解析**：

**MySQL InnoDB默认使用Repeatable Read（可重复读）**

**选择原因**：

1. **解决了大部分并发问题**：
   - ✅ 避免脏读
   - ✅ 避免不可重复读
   - ⚠️ 幻读（通过MVCC基本解决）

2. **性能较好**：
   - 通过MVCC（多版本并发控制）实现
   - 读不阻塞写，写不阻塞读
   - 比Serializable性能好很多

3. **符合大多数应用需求**：
   - 电商、社交等场景适用
   - 不需要完全串行化

**对比其他数据库**：
- **Oracle、PostgreSQL**：默认Read Committed
- **MySQL选择Repeatable Read**：历史原因（早期主从复制需要）

**查看当前隔离级别**：
```sql
SELECT @@transaction_isolation;
```

</details>

---

### 4. 以下哪种场景应该使用Serializable隔离级别？

A. 电商商品列表查询  
B. 社交媒体Feed流  
C. 金融账户转账  
D. 日志记录系统  

<details>
<summary>查看答案</summary>

**答案**：C

**解析**：

**Serializable适用场景**：
- 对数据一致性要求**极高**
- 可以牺牲性能换取安全
- 典型场景：金融、支付、账户操作

**为什么选C**：
```sql
-- 金融转账必须保证原子性和一致性
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN;
UPDATE accounts SET balance = balance - 1000 WHERE id = 1;
UPDATE accounts SET balance = balance + 1000 WHERE id = 2;
COMMIT;
```

**为什么不选其他选项**：

**A. 电商商品列表查询**：
- 只读操作，对实时性要求不高
- 使用Read Committed或Repeatable Read即可

**B. 社交媒体Feed流**：
- 对一致性要求低（晚几秒看到新内容可接受）
- 使用Read Committed即可

**D. 日志记录系统**：
- 只写入，不读取
- 对一致性要求最低
- 使用Read Uncommitted或Read Committed即可

**Serializable的代价**：
- 性能最差（事务串行执行）
- 并发度为0
- 只在必要时使用

</details>

---

## 二、场景分析（判断分析题，每题15分，共30分）

### 5. 分析并发问题

**场景**：电影院售票系统

```sql
-- 初始状态：座位A1可售
BEGIN;
SELECT available FROM seats WHERE seat_id = 'A1';  -- 查询：可售
-- 其他用户也在此时查询，结果也是可售
INSERT INTO tickets (seat_id, user_id) VALUES ('A1', 123);  -- 购买
COMMIT;
```

**问题**：
1. 这段代码可能出现什么并发问题？
2. 使用哪种隔离级别可以解决？
3. 除了调整隔离级别，还有什么解决方案？

<details>
<summary>参考答案</summary>

**1. 并发问题：超卖（一个座位卖给多人）**

**发生过程**：
```
时间线：
T1: SELECT available FROM seats WHERE seat_id = 'A1';  → 可售
T2: SELECT available FROM seats WHERE seat_id = 'A1';  → 可售
T1: INSERT INTO tickets (seat_id, user_id) VALUES ('A1', 123);
T2: INSERT INTO tickets (seat_id, user_id) VALUES ('A1', 456);
→ 座位A1被卖给了用户123和456！
```

**本质原因**：
- 查询和插入之间有时间间隙
- 没有加锁，多个事务同时操作

**2. 隔离级别解决方案**

**方案1：Serializable**
```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN;
SELECT available FROM seats WHERE seat_id = 'A1';
INSERT INTO tickets (seat_id, user_id) VALUES ('A1', 123);
COMMIT;
```

**效果**：事务串行执行，不会超卖  
**缺点**：性能差，高并发时排队严重

**3. 更好的解决方案（不调整隔离级别）**

**方案2：悲观锁（FOR UPDATE）**
```sql
BEGIN;
SELECT available FROM seats WHERE seat_id = 'A1' FOR UPDATE;  -- 加锁
-- 其他事务会等待，直到本事务提交
IF available THEN
    INSERT INTO tickets (seat_id, user_id) VALUES ('A1', 123);
    UPDATE seats SET available = 0 WHERE seat_id = 'A1';
END IF;
COMMIT;
```

**方案3：乐观锁（版本号）**
```sql
-- seats表增加version字段
BEGIN;
SELECT available, version FROM seats WHERE seat_id = 'A1';  -- version=1
INSERT INTO tickets (seat_id, user_id) VALUES ('A1', 123);
UPDATE seats SET available = 0, version = version + 1 
WHERE seat_id = 'A1' AND version = 1;  -- 如果version已被其他事务修改，更新失败
IF affected_rows == 0 THEN
    ROLLBACK;  -- 版本冲突，回滚
ELSE
    COMMIT;
END IF;
```

**方案4：唯一约束**
```sql
-- 为seat_id添加唯一约束
CREATE UNIQUE INDEX idx_seat ON tickets(seat_id);

-- 插入时会自动检查唯一性
INSERT INTO tickets (seat_id, user_id) VALUES ('A1', 123);
-- 如果seat_id='A1'已存在，会抛出异常
```

**推荐方案**：
- **高并发场景**：悲观锁（FOR UPDATE）
- **冲突较少**：乐观锁（version）
- **结合数据库约束**：唯一索引兜底

</details>

---

### 6. 隔离级别选择

**场景**：你正在开发一个订单系统，包含以下功能

```
功能1：用户下单（检查库存 → 扣减库存 → 创建订单）
功能2：管理员查看销售报表（统计订单总额）
功能3：用户查看自己的订单列表
```

**问题**：
1. 为每个功能选择合适的隔离级别
2. 说明选择理由
3. 如果全部使用Serializable会有什么问题？

<details>
<summary>参考答案</summary>

**1. 隔离级别选择**

| 功能 | 隔离级别 | 理由 |
|-----|---------|------|
| **用户下单** | Repeatable Read | 需要避免库存超卖，MySQL默认级别已够用 |
| **销售报表** | Read Committed | 统计数据，对实时性要求不高 |
| **订单列表** | Read Committed | 只读操作，读已提交即可 |

**2. 详细理由**

**功能1：用户下单 - Repeatable Read**
```sql
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;

-- 检查库存（加锁）
SELECT stock FROM products WHERE id = 1 FOR UPDATE;

-- 扣减库存
UPDATE products SET stock = stock - 1 WHERE id = 1;

-- 创建订单
INSERT INTO orders (product_id, user_id, quantity) VALUES (1, 123, 1);

COMMIT;
```

**理由**：
- 需要避免不可重复读（检查库存和扣减库存之间，数据不能变）
- MySQL的Repeatable Read + FOR UPDATE 可以防止超卖
- 性能与安全平衡

**功能2：销售报表 - Read Committed**
```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN;

SELECT SUM(amount) FROM orders WHERE date = CURRENT_DATE;

COMMIT;
```

**理由**：
- 只读操作，不会修改数据
- 统计数据允许有微小偏差（晚几秒统计到新订单可接受）
- Read Committed性能更好

**功能3：订单列表 - Read Committed**
```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN;

SELECT * FROM orders WHERE user_id = 123 ORDER BY created_at DESC;

COMMIT;
```

**理由**：
- 只读操作
- 用户查看自己的订单，对一致性要求不高
- Read Committed足够

**3. 如果全部使用Serializable的问题**

**问题1：性能崩溃**
```
场景：双11大促，每秒10000个下单请求

Serializable：事务串行执行
→ 吞吐量：100 TPS（事务/秒）
→ 99%的请求超时

Repeatable Read + FOR UPDATE：
→ 吞吐量：5000 TPS
→ 响应正常
```

**问题2：死锁增加**
```
T1: 锁定商品A → 等待商品B
T2: 锁定商品B → 等待商品A
→ 死锁！

Serializable下死锁概率大大增加
```

**问题3：资源浪费**
- 报表查询（可能需要几秒）占用锁
- 其他事务全部等待
- 数据库连接池耗尽

**正确做法**：
- 根据场景选择合适的隔离级别
- 关键操作（下单、支付）：Repeatable Read + 悲观锁
- 普通查询：Read Committed
- 极少数场景：Serializable（如金融转账）

</details>

---

## 三、方案设计（综合题，30分）

### 7. 设计秒杀系统的并发控制方案

**场景**：
- 1000个商品库存
- 100000个用户抢购
- 要求不能超卖
- 要求性能尽可能高

**任务**：
1. 选择数据库隔离级别
2. 设计SQL方案（防止超卖）
3. 优化方案（提高性能）
4. 分析可能的问题和解决方案

<details>
<summary>参考答案</summary>

**1. 隔离级别选择：Repeatable Read**

**理由**：
- MySQL默认级别，性能较好
- 配合FOR UPDATE可以防止超卖
- 不需要Serializable的完全隔离

**2. SQL方案（防止超卖）**

**方案1：悲观锁（FOR UPDATE）**
```sql
BEGIN;

-- 1. 锁定库存记录
SELECT stock FROM products WHERE id = 1 FOR UPDATE;

-- 2. 检查库存
IF stock > 0 THEN
    -- 3. 扣减库存
    UPDATE products SET stock = stock - 1 WHERE id = 1;
    
    -- 4. 创建订单
    INSERT INTO orders (product_id, user_id) VALUES (1, 123);
    
    COMMIT;
ELSE
    ROLLBACK;
    RETURN "库存不足";
END IF;
```

**优点**：
- 简单可靠，不会超卖
- 数据强一致性

**缺点**：
- 性能较差（高并发时锁竞争激烈）
- 吞吐量低

**方案2：乐观锁（版本号）**
```sql
-- 1. 查询库存和版本号
SELECT stock, version FROM products WHERE id = 1;

-- 2. 检查库存
IF stock > 0 THEN
    BEGIN;
    
    -- 3. 扣减库存（带版本检查）
    UPDATE products 
    SET stock = stock - 1, version = version + 1 
    WHERE id = 1 AND version = :old_version;
    
    -- 4. 检查更新是否成功
    IF affected_rows == 0 THEN
        ROLLBACK;
        RETURN "抢购失败，请重试";  -- 版本冲突
    ELSE
        -- 5. 创建订单
        INSERT INTO orders (product_id, user_id) VALUES (1, 123);
        COMMIT;
        RETURN "抢购成功";
    END IF;
END IF;
```

**优点**：
- 性能好（不加锁，冲突时才重试）
- 吞吐量高

**缺点**：
- 冲突时需要重试
- 高并发下重试率高

**3. 优化方案**

**优化1：减库存在前**
```sql
-- 先扣库存，再检查
BEGIN;
UPDATE products SET stock = stock - 1 WHERE id = 1 AND stock > 0;

IF affected_rows == 0 THEN
    ROLLBACK;
    RETURN "库存不足";
END IF;

-- 扣减成功，创建订单
INSERT INTO orders (product_id, user_id) VALUES (1, 123);
COMMIT;
```

**优点**：
- 减少锁持有时间
- 性能提升

**优化2：Redis预扣库存**
```java
// 1. Redis预扣库存（原子操作）
Long stock = redis.decr("product:1:stock");

if (stock < 0) {
    redis.incr("product:1:stock");  // 回滚
    return "库存不足";
}

// 2. 异步写入数据库
messageQueue.send({productId: 1, userId: 123});

// 3. 消费者批量写入数据库
```

**优点**：
- Redis性能极高（10万+ QPS）
- 减轻数据库压力

**优化3：限流**
```java
// 令牌桶限流
RateLimiter limiter = RateLimiter.create(5000);  // 每秒5000次

if (!limiter.tryAcquire()) {
    return "系统繁忙，请稍后再试";
}

// 执行秒杀逻辑
```

**优化4：分库分表**
```sql
-- 按商品ID分片
产品1-1000 → DB1
产品1001-2000 → DB2

-- 分散锁竞争
```

**4. 可能的问题和解决方案**

**问题1：超时**
```
100000个请求同时到达
→ 数据库连接池耗尽
→ 大量超时

解决方案：
1. 限流（令牌桶）
2. 排队（消息队列）
3. 前端限制（按钮点击后禁用）
```

**问题2：缓存击穿**
```
库存为0后，大量请求仍然打到数据库
→ 数据库压力大

解决方案：
1. Redis缓存库存状态
2. 库存为0后，直接返回"已售罄"
3. 布隆过滤器
```

**问题3：黄牛刷单**
```
同一用户多次抢购

解决方案：
1. 限制每个用户只能抢1次
2. 验证码
3. 风控系统
```

**完整架构**：
```
用户请求
  ↓
Nginx限流
  ↓
应用层限流（令牌桶）
  ↓
Redis预扣库存
  ↓
消息队列（异步）
  ↓
数据库写入（批量）
```

**性能对比**：

| 方案 | QPS | 超卖风险 | 复杂度 |
|-----|-----|---------|-------|
| 悲观锁 | 500 | 无 | 低 |
| 乐观锁 | 2000 | 无 | 中 |
| Redis+MQ | 50000 | 极低 | 高 |

**推荐方案**：
- **小规模秒杀**（<10000人）：乐观锁
- **大规模秒杀**（>100000人）：Redis+MQ

</details>

---

## 评分标准

- **选择题（40分）**：每题10分
- **场景分析题（30分）**：
  - 题5：问题识别（5分）+ 隔离级别方案（5分）+ 其他方案（5分）
  - 题6：级别选择（5分）+ 理由说明（5分）+ Serializable问题分析（5分）
- **方案设计题（30分）**：
  - 隔离级别选择（5分）+ SQL方案（10分）+ 优化方案（10分）+ 问题分析（5分）

**及格线：60分**  
**建议目标：80分以上**

---

## 自我检测清单

- [ ] 我能说出ACID的含义
- [ ] 我能区分脏读、不可重复读、幻读
- [ ] 我能说出四种隔离级别及其解决的问题
- [ ] 我知道MySQL的默认隔离级别及原因
- [ ] 我理解MVCC的基本原理
- [ ] 我能为不同场景选择合适的隔离级别
- [ ] 我知道如何用FOR UPDATE防止超卖
- [ ] 我能设计秒杀系统的并发控制方案
- [ ] 我完成了自测题，正确率 ≥ 80%

---

💡 **下一步**：填写 `note_template.md`，用自己的话总结学到的知识。
