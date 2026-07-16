# 事务隔离级别代码示例

本目录包含演示程序，帮助你理解事务隔离级别。

## 文件说明

- **TransactionIsolationDemo.java** - 演示不同隔离级别下的并发问题

## 如何运行

### 运行 TransactionIsolationDemo

```bash
# 编译
javac TransactionIsolationDemo.java

# 运行
java TransactionIsolationDemo
```

## 预期输出

演示内容：
1. **脏读问题演示**（Read Uncommitted）
   - 事务1修改但未提交
   - 事务2读到未提交的数据
   - 事务1回滚
   - 事务2基于错误数据做决策

2. **不可重复读问题演示**（Read Committed）
   - 事务1两次读取同一数据
   - 中间事务2修改并提交
   - 事务1两次读到不同结果

3. **可重复读演示**（Repeatable Read）
   - 事务1多次读取同一数据
   - 即使其他事务修改并提交
   - 事务1仍读到一致的结果

4. **串行化演示**（Serializable）
   - 事务完全隔离
   - 性能影响

## 注意事项

1. 这是**教学演示代码**，模拟了数据库的行为
2. 实际项目中应使用真实数据库连接
3. 重点理解不同隔离级别的差异

## 学习建议

1. 先阅读 `doc_01.md` 理解概念
2. 运行代码，观察输出
3. 修改代码，尝试不同场景
4. 思考实际项目中如何选择隔离级别

## 扩展练习

1. 使用真实数据库（MySQL）测试不同隔离级别
   ```sql
   SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
   BEGIN;
   -- 执行操作
   COMMIT;
   ```

2. 实现一个并发下单系统，避免超卖

3. 研究MVCC机制，理解MySQL如何实现可重复读
