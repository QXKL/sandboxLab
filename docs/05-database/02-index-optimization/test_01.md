# 索引设计与优化 - 自测题

**总分：100分**

## 一、概念理解（选择题，每题10分，共40分）

### 1. 索引的作用是什么？

A. 保证数据不重复  
B. 加快查询速度  
C. 自动备份数据  
D. 压缩存储空间  

<details>
<summary>查看答案</summary>
**答案**：B  
**解析**：索引像书的目录，帮助快速定位数据，避免全表扫描。
</details>

### 2. 联合索引 (a, b, c) 能用于哪个查询？

A. WHERE b = 1 AND c = 2  
B. WHERE a = 1 AND c = 2  
C. WHERE c = 2  
D. WHERE b = 1  

<details>
<summary>查看答案</summary>
**答案**：B  
**解析**：最左前缀原则，a可以用，c因为跳过b不能完全用，但部分生效。
</details>

### 3. 以下哪种情况索引会失效？

A. WHERE name = 'zhang'  
B. WHERE name LIKE 'zhang%'  
C. WHERE UPPER(name) = 'ZHANG'  
D. WHERE name IN ('zhang', 'li')  

<details>
<summary>查看答案</summary>
**答案**：C  
**解析**：对索引字段使用函数会导致索引失效。
</details>

### 4. EXPLAIN中type=ALL表示什么？

A. 使用了所有索引  
B. 全表扫描（性能最差）  
C. 查询了所有字段  
D. 所有条件都满足  

<details>
<summary>查看答案</summary>
**答案**：B  
**解析**：type=ALL是全表扫描，应该优化为ref、range等更好的类型。
</details>

## 二、场景分析（每题20分，共40分）

### 5. 分析慢查询

**场景**：订单表10万条数据，查询某用户的已支付订单
```sql
SELECT * FROM orders WHERE user_id = 123 AND status = 'paid' ORDER BY created_at DESC;
-- 耗时：2秒
-- EXPLAIN: type=ALL, rows=100000
```

**问题**：如何优化？

<details>
<summary>参考答案</summary>
建立联合索引：
```sql
CREATE INDEX idx_user_status_time ON orders(user_id, status, created_at);
```
理由：覆盖查询条件和排序字段，避免filesort。
</details>

### 6. 索引设计

**场景**：用户表，常见查询：
- 通过邮箱查询：WHERE email = ?
- 通过手机号查询：WHERE phone = ?
- 查询某状态用户：WHERE status = ?

**问题**：如何设计索引？

<details>
<summary>参考答案</summary>
```sql
CREATE UNIQUE INDEX uk_email ON users(email);
CREATE UNIQUE INDEX uk_phone ON users(phone);
-- status区分度低，视查询频率决定是否建索引
```
</details>

## 三、方案设计（20分）

### 7. 电商商品搜索优化

**需求**：商品表100万条，支持按名称、分类、价格范围搜索

**任务**：设计索引方案

<details>
<summary>参考答案</summary>
```sql
-- 方案1：单字段索引
CREATE INDEX idx_name ON products(name(20));  -- 前缀索引
CREATE INDEX idx_category ON products(category_id);
CREATE INDEX idx_price ON products(price);

-- 方案2：联合索引（根据查询频率）
CREATE INDEX idx_cat_price ON products(category_id, price);
CREATE FULLTEXT INDEX ft_name ON products(name);  -- 全文搜索
```
</details>

---

**及格线：60分 | 目标：80分以上**
