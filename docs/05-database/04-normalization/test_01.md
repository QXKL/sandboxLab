# 范式 vs 反范式 - 自测题

**总分：100分**

## 一、概念理解（40分）

### 1. 第三范式（3NF）的定义是什么？（10分）

<details>
<summary>查看答案</summary>
**答案**：消除传递依赖，非主键字段不能依赖于其他非主键字段  
**示例**：订单表中不应存储用户姓名（因为姓名依赖用户ID，形成传递依赖）
</details>

### 2. 范式化的主要优点是什么？（10分）

<details>
<summary>查看答案</summary>
- 减少数据冗余
- 提高数据一致性
- 易于维护
- 节省存储空间
</details>

### 3. 什么场景适合使用反范式化？（10分）

<details>
<summary>查看答案</summary>
- 读多写少的场景
- 需要历史快照的数据（如订单）
- 统计字段（如点赞数）
- 查询性能要求极高
</details>

### 4. 反范式化的代价是什么？（10分）

<details>
<summary>查看答案</summary>
- 数据冗余增加
- 写入性能下降（需要更新多处）
- 需要额外维护数据一致性
- 存储空间增加
</details>

## 二、场景分析（30分）

### 5. 分析表设计

**表结构**：
```sql
订单表：
| 订单ID | 用户ID | 用户姓名 | 用户电话 | 商品ID | 商品名称 | 商品价格 | 数量 |
```

**问题**：
1. 该表违反了哪些范式？（15分）
2. 如何改进？（15分）

<details>
<summary>参考答案</summary>

**1. 违反的范式**：
- 违反2NF：用户姓名、电话只依赖用户ID（部分依赖）
- 违反2NF：商品名称、价格只依赖商品ID（部分依赖）
- 违反3NF：用户姓名、电话依赖用户ID（传递依赖）

**2. 改进方案**：
```sql
用户表：
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    phone VARCHAR(20)
);

商品表：
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200),
    price DECIMAL(10,2)
);

订单表：
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    created_at DATETIME
);

订单明细表：
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    -- 可选：冗余商品名称和价格（快照）
    product_name VARCHAR(200),
    product_price DECIMAL(10,2)
);
```
</details>

## 三、设计题（30分）

### 6. 设计电商评论系统

**需求**：
- 用户可以评论商品
- 显示评论数量
- 显示评论列表

**任务**：
1. 设计范式化方案（10分）
2. 设计反范式化方案（10分）
3. 说明各自的优缺点（10分）

<details>
<summary>参考答案</summary>

**范式化方案**：
```sql
商品表：
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200)
);

评论表：
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY,
    product_id BIGINT,
    user_id BIGINT,
    content TEXT,
    created_at DATETIME
);

查询评论数：
SELECT COUNT(*) FROM reviews WHERE product_id = 1;
```

**优点**：数据一致性好，无冗余  
**缺点**：每次查询都要COUNT，性能差

**反范式化方案**：
```sql
商品表：
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200),
    review_count INT DEFAULT 0  -- 冗余字段
);

评论表：
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY,
    product_id BIGINT,
    user_id BIGINT,
    content TEXT,
    created_at DATETIME
);

维护：
-- 新增评论时
UPDATE products SET review_count = review_count + 1 WHERE id = 1;
```

**优点**：查询快，无需COUNT  
**缺点**：需要维护一致性，写入稍慢

**推荐方案**：缓存
```
- 数据库使用范式化设计
- 评论数缓存在Redis
- 定期刷新缓存
```
</details>

---

**及格线：60分 | 目标：80分以上**

## 自我检测

- [ ] 我能说出三大范式的定义
- [ ] 我能识别违反范式的表设计
- [ ] 我知道何时使用反范式化
- [ ] 我能权衡范式化和反范式化的利弊
- [ ] 我完成了自测题

**完成日期**：________
