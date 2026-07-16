# 索引设计与优化

## 一、什么是索引？

### 书籍目录的类比

想象你在图书馆查找一本书中关于"事务隔离级别"的内容：

**没有目录（没有索引）**：
```
从第1页开始翻，逐页查找"事务隔离级别"
→ 如果在第500页，需要翻500页
→ 如果书有1000页，平均需要翻500页
```

**有目录（有索引）**：
```
1. 翻到目录页
2. 查找"事务隔离级别" → 第267页
3. 直接翻到第267页
→ 只需要翻3页！
```

**索引就是数据库的"目录"**：帮助快速定位数据，而不用扫描整张表。

### 索引的定义

**索引（Index）**：一种数据结构，用于快速查找数据。

```sql
-- 没有索引（全表扫描）
SELECT * FROM users WHERE email = 'zhangsan@example.com';
→ 扫描100万行数据，找到1行 → 慢

-- 有索引（索引查找）
CREATE INDEX idx_email ON users(email);
SELECT * FROM users WHERE email = 'zhangsan@example.com';
→ 通过索引直接定位 → 快
```

### 索引的存储结构（B+树）

**简化的B+树示意**：

```
        [50, 100, 150]           ← 根节点
       /      |      |      \
   [10,30] [60,80] [110,130] [160,180]  ← 中间节点
     |        |        |        |
  [数据]   [数据]   [数据]   [数据]    ← 叶子节点（实际数据）
```

**特点**：
- 树形结构，查找路径短（log(N)）
- 所有数据在叶子节点（范围查询高效）
- 叶子节点之间有指针（支持顺序遍历）

**查找过程**：
```
查找 id = 75：
1. 根节点：75在50和100之间 → 走第二个分支
2. 中间节点：75在60和80之间 → 走第二个分支
3. 叶子节点：找到id=75的数据

时间复杂度：O(log N)
100万数据，查找只需要3-4次磁盘IO
```

## 二、为什么需要索引？

### 性能对比

**场景**：1000万条用户数据，查找email='test@example.com'的用户

| 方式 | 扫描行数 | 耗时 | 说明 |
|-----|---------|------|------|
| **全表扫描** | 1000万行 | 10秒 | 逐行检查email |
| **索引查找** | 约20行 | 0.01秒 | 通过B+树定位 |

**性能提升：1000倍！**

### 索引的代价

**索引不是免费的**：

```
优点：
✅ 查询速度快（SELECT）
✅ 排序速度快（ORDER BY）
✅ 唯一性约束（UNIQUE INDEX）

缺点：
❌ 占用额外存储空间
❌ 写入变慢（INSERT、UPDATE、DELETE需要更新索引）
❌ 维护成本（索引需要定期优化）
```

**权衡**：
- 读多写少的场景 → 适合加索引
- 写多读少的场景 → 谨慎加索引

## 三、索引类型

### 类型1：主键索引（Primary Key）

**定义**：表的主键自动创建的索引，唯一且非空。

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,    -- 自动创建主键索引
    name VARCHAR(50)
);

-- 等价于
CREATE TABLE users (
    id BIGINT,
    name VARCHAR(50),
    PRIMARY KEY (id)
);
```

**特点**：
- 每张表只能有一个主键
- 自动创建聚簇索引（数据按主键顺序存储）
- 查询最快

**InnoDB中的主键索引**：
```
主键索引的叶子节点 = 完整的行数据

id索引：
  10 → [10, 'zhangsan', 'zhangsan@example.com']
  20 → [20, 'lisi', 'lisi@example.com']
  30 → [30, 'wangwu', 'wangwu@example.com']
```

### 类型2：唯一索引（Unique Index）

**定义**：保证字段值唯一，但允许NULL。

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    email VARCHAR(100),
    phone VARCHAR(20),
    UNIQUE KEY uk_email (email),     -- 唯一索引
    UNIQUE KEY uk_phone (phone)
);

-- 或
CREATE UNIQUE INDEX uk_email ON users(email);
```

**特点**：
- 字段值必须唯一（但可以有多个NULL）
- 自动防止重复数据
- 查询速度快

**使用场景**：
- 用户邮箱、手机号
- 订单号、流水号
- 身份证号

### 类型3：普通索引（Normal Index）

**定义**：普通的索引，允许重复值。

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    status VARCHAR(20),
    created_at DATETIME,
    INDEX idx_user_id (user_id),        -- 普通索引
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- 或
CREATE INDEX idx_user_id ON orders(user_id);
```

**特点**：
- 最常用的索引类型
- 允许字段值重复
- 提升查询速度

**使用场景**：
- 查询条件字段（WHERE、JOIN）
- 排序字段（ORDER BY）
- 分组字段（GROUP BY）

### 类型4：联合索引（Composite Index）

**定义**：多个字段组合的索引。

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    status VARCHAR(20),
    created_at DATETIME,
    INDEX idx_user_status (user_id, status),     -- 联合索引
    INDEX idx_status_time (status, created_at)
);
```

**最左前缀原则**：

```
索引：(user_id, status, created_at)

✅ 能用到索引：
SELECT * FROM orders WHERE user_id = 1;
SELECT * FROM orders WHERE user_id = 1 AND status = 'paid';
SELECT * FROM orders WHERE user_id = 1 AND status = 'paid' AND created_at > '2026-01-01';

❌ 不能用到索引：
SELECT * FROM orders WHERE status = 'paid';               -- 跳过user_id
SELECT * FROM orders WHERE created_at > '2026-01-01';     -- 跳过user_id和status

⚠️ 部分用到索引：
SELECT * FROM orders WHERE user_id = 1 AND created_at > '2026-01-01';  
-- 只用到user_id部分，created_at不走索引
```

**为什么要遵循最左前缀？**

```
索引结构：
(user_id=1, status='paid', created_at='2026-01-01')
(user_id=1, status='paid', created_at='2026-01-02')
(user_id=1, status='unpaid', created_at='2026-01-03')
(user_id=2, status='paid', created_at='2026-01-04')

数据按 user_id → status → created_at 排序
→ 如果跳过user_id，直接找status，数据是无序的！
```

### 类型5：全文索引（Full-Text Index）

**定义**：用于全文搜索的索引。

```sql
CREATE TABLE articles (
    id BIGINT PRIMARY KEY,
    title VARCHAR(200),
    content TEXT,
    FULLTEXT KEY ft_content (content)    -- 全文索引
);

-- 全文搜索
SELECT * FROM articles WHERE MATCH(content) AGAINST('数据库优化');
```

**特点**：
- 支持中文分词（需要配置）
- 适合大段文本搜索
- 性能优于 LIKE '%关键词%'

**使用场景**：
- 文章搜索
- 商品描述搜索
- 日志搜索

**注意**：实际项目中，复杂的全文搜索通常使用Elasticsearch等专业搜索引擎。

## 四、索引设计原则

### 原则1：为查询条件字段建索引

```sql
-- 经常这样查询
SELECT * FROM orders WHERE user_id = 123;

-- 应该建索引
CREATE INDEX idx_user_id ON orders(user_id);
```

### 原则2：为排序和分组字段建索引

```sql
-- 经常这样排序
SELECT * FROM orders WHERE user_id = 123 ORDER BY created_at DESC;

-- 建联合索引
CREATE INDEX idx_user_created ON orders(user_id, created_at);
```

### 原则3：区分度高的字段优先

**区分度 = COUNT(DISTINCT column) / COUNT(*))**

```sql
-- 性别字段（区分度低：2种值）
SELECT COUNT(DISTINCT gender) / COUNT(*) FROM users;  -- 0.5
❌ 不适合建索引

-- 邮箱字段（区分度高：几乎唯一）
SELECT COUNT(DISTINCT email) / COUNT(*) FROM users;  -- 0.99
✅ 适合建索引

-- 状态字段（区分度中：5-10种值）
SELECT COUNT(DISTINCT status) / COUNT(*) FROM orders;  -- 0.1
⚠️ 根据查询频率决定
```

**建议**：
- 区分度 > 0.1：考虑建索引
- 区分度 > 0.3：建议建索引
- 区分度 > 0.8：强烈建议建索引

### 原则4：联合索引字段顺序

**顺序选择**：
1. 区分度高的字段在前
2. 查询频率高的字段在前
3. 范围查询字段放最后

```sql
-- 查询场景1：WHERE user_id = 1 AND status = 'paid'
-- user_id区分度高（100万用户），status区分度低（5种状态）
✅ CREATE INDEX idx_user_status ON orders(user_id, status);

-- 查询场景2：WHERE status = 'paid' AND created_at > '2026-01-01'
-- created_at是范围查询，放最后
✅ CREATE INDEX idx_status_time ON orders(status, created_at);
```

### 原则5：避免过多索引

```
❌ 不要为每个字段都建索引

问题：
1. 占用大量存储空间
2. 写入性能下降（每次INSERT/UPDATE都要更新所有索引）
3. 优化器选择困难（索引太多，可能选错）

建议：
- 单表索引数量控制在5个以内
- 只为高频查询字段建索引
```

### 原则6：字符串字段考虑前缀索引

```sql
-- 邮箱字段很长，完整索引占用空间大
❌ CREATE INDEX idx_email ON users(email);

-- 只索引前10个字符
✅ CREATE INDEX idx_email ON users(email(10));

-- 查询
SELECT * FROM users WHERE email = 'zhangsan@example.com';
→ 通过前缀索引快速定位到候选行，再精确匹配
```

**适用场景**：
- URL字段
- 长文本字段
- 固定格式的字符串（邮箱、手机号）

**注意**：前缀长度要保证区分度

```sql
-- 检查前缀区分度
SELECT 
    COUNT(DISTINCT LEFT(email, 5)) / COUNT(*) AS prefix_5,
    COUNT(DISTINCT LEFT(email, 10)) / COUNT(*) AS prefix_10,
    COUNT(DISTINCT LEFT(email, 15)) / COUNT(*) AS prefix_15,
    COUNT(DISTINCT email) / COUNT(*) AS full
FROM users;

-- 选择区分度接近full的最短前缀长度
```

## 五、索引失效场景

### 场景1：对索引字段使用函数

```sql
-- ❌ 索引失效
CREATE INDEX idx_created_at ON orders(created_at);
SELECT * FROM orders WHERE YEAR(created_at) = 2026;

-- ✅ 改写为范围查询
SELECT * FROM orders 
WHERE created_at >= '2026-01-01' AND created_at < '2027-01-01';
```

### 场景2：隐式类型转换

```sql
-- phone字段是VARCHAR
CREATE INDEX idx_phone ON users(phone);

-- ❌ 索引失效（phone是字符串，13800138000是数字）
SELECT * FROM users WHERE phone = 13800138000;

-- ✅ 加引号
SELECT * FROM users WHERE phone = '13800138000';
```

### 场景3：LIKE以通配符开头

```sql
CREATE INDEX idx_name ON users(name);

-- ❌ 索引失效
SELECT * FROM users WHERE name LIKE '%zhang%';

-- ✅ 可以用索引
SELECT * FROM users WHERE name LIKE 'zhang%';
```

### 场景4：OR条件中有未建索引的字段

```sql
CREATE INDEX idx_email ON users(email);
-- phone没有索引

-- ❌ 索引失效
SELECT * FROM users WHERE email = 'test@example.com' OR phone = '13800138000';

-- ✅ 改为UNION
SELECT * FROM users WHERE email = 'test@example.com'
UNION
SELECT * FROM users WHERE phone = '13800138000';
```

### 场景5：!=、<>、NOT IN

```sql
CREATE INDEX idx_status ON orders(status);

-- ❌ 可能不走索引
SELECT * FROM orders WHERE status != 'cancelled';

-- ✅ 改为IN（如果状态值较少）
SELECT * FROM orders WHERE status IN ('pending', 'paid', 'shipped');
```

### 场景6：违反最左前缀原则

```sql
CREATE INDEX idx_abc ON table(a, b, c);

-- ❌ 不走索引
SELECT * FROM table WHERE b = 1 AND c = 2;  -- 跳过a

-- ✅ 走索引
SELECT * FROM table WHERE a = 1 AND b = 2 AND c = 3;
```

## 六、EXPLAIN分析

### EXPLAIN基本用法

```sql
EXPLAIN SELECT * FROM orders WHERE user_id = 123;
```

### 关键字段解读

| 字段 | 含义 | 关注点 |
|-----|------|-------|
| **type** | 连接类型 | system > const > eq_ref > ref > range > index > ALL |
| **possible_keys** | 可能用到的索引 | |
| **key** | 实际使用的索引 | NULL表示没用索引 |
| **key_len** | 索引长度 | 越短越好 |
| **rows** | 扫描行数 | 越少越好 |
| **Extra** | 额外信息 | Using filesort（需要排序）、Using temporary（需要临时表）要优化 |

### type类型详解

```
从好到坏：

✅ system：表只有一行（系统表）
✅ const：主键或唯一索引查询，最多返回1行
  SELECT * FROM users WHERE id = 1;

✅ eq_ref：唯一索引扫描，JOIN时使用
  SELECT * FROM orders o JOIN users u ON o.user_id = u.id;

✅ ref：非唯一索引扫描
  SELECT * FROM orders WHERE user_id = 123;

⚠️ range：范围扫描
  SELECT * FROM orders WHERE created_at > '2026-01-01';

⚠️ index：索引全扫描
  SELECT id FROM orders;  -- 只查索引列

❌ ALL：全表扫描（最差）
  SELECT * FROM orders WHERE YEAR(created_at) = 2026;
```

### 示例分析

```sql
-- 查询
EXPLAIN SELECT * FROM orders WHERE user_id = 123 ORDER BY created_at DESC;

-- 结果分析
+------+-------+---------------+---------+------+-------------+
| type | key   | key_len       | rows    | Extra           |
+------+-------+---------------+---------+------+-------------+
| ref  | idx_user| 8           | 100     | Using filesort  |
+------+-------+---------------+---------+------+-------------+

问题：Using filesort（需要排序，慢）

优化：建立联合索引
CREATE INDEX idx_user_created ON orders(user_id, created_at);

优化后：
+------+------------------+----------+------+-------+
| type | key              | key_len  | rows | Extra |
+------+------------------+----------+------+-------+
| ref  | idx_user_created | 16       | 100  | NULL  |
+------+------------------+----------+------+-------+

Extra为NULL，不需要额外排序，性能提升！
```

## 七、索引优化实战

### 案例1：慢查询优化

**问题**：
```sql
SELECT * FROM orders 
WHERE status = 'paid' AND created_at > '2026-01-01'
ORDER BY created_at DESC
LIMIT 20;

-- 执行时间：5秒
-- EXPLAIN：type=ALL, rows=1000000（全表扫描）
```

**优化步骤**：

1. **建立索引**
```sql
CREATE INDEX idx_status_time ON orders(status, created_at);
```

2. **验证**
```sql
EXPLAIN SELECT * FROM orders 
WHERE status = 'paid' AND created_at > '2026-01-01'
ORDER BY created_at DESC
LIMIT 20;

-- type=range, key=idx_status_time, rows=50000
-- 执行时间：0.1秒（提升50倍）
```

### 案例2：覆盖索引优化

**问题**：
```sql
SELECT user_id, status, created_at FROM orders WHERE user_id = 123;

-- 有索引idx_user_id，但还需要回表查status和created_at
```

**优化**：
```sql
-- 建立覆盖索引（包含所有查询字段）
CREATE INDEX idx_user_status_time ON orders(user_id, status, created_at);

-- EXPLAIN：Extra=Using index（覆盖索引，不需要回表）
-- 性能提升：避免回表的IO开销
```

### 案例3：索引合并优化

**问题**：
```sql
SELECT * FROM users WHERE email = 'test@example.com' OR phone = '13800138000';

-- 有idx_email和idx_phone，但OR导致索引失效
```

**优化**：
```sql
-- 改为UNION
SELECT * FROM users WHERE email = 'test@example.com'
UNION
SELECT * FROM users WHERE phone = '13800138000';

-- 分别走索引，再合并结果
```

## 八、小结

**核心要点**：

1. **索引的本质**：数据库的"目录"，加快查找速度

2. **索引类型**：
   - 主键索引：唯一且非空
   - 唯一索引：保证唯一
   - 普通索引：最常用
   - 联合索引：多字段组合
   - 全文索引：文本搜索

3. **设计原则**：
   - 为查询条件、排序、分组字段建索引
   - 区分度高的字段优先
   - 联合索引遵循最左前缀原则
   - 避免过多索引（5个以内）

4. **索引失效场景**：
   - 对索引字段使用函数
   - 隐式类型转换
   - LIKE以%开头
   - OR中有未索引字段
   - 违反最左前缀原则

5. **优化工具**：
   - EXPLAIN分析执行计划
   - 关注type（ref以上）、rows（越少越好）、Extra（避免Using filesort）

**记忆口诀**：
- 查询条件加索引
- 区分度高优先建
- 联合索引左前缀
- 函数转换索引废
- EXPLAIN分析要常看

---

**下一步**：运行 `demo/` 目录中的代码示例，实际感受索引的性能差异！

💡 **提示**：索引优化是数据库性能优化的核心，掌握好索引，数据库性能提升10倍不是梦！
