# 索引设计与优化代码示例

本目录包含演示程序，帮助你理解索引的性能影响。

## 文件说明

- **IndexPerformanceDemo.java** - 索引性能对比演示

## 如何运行

```bash
# 编译
javac IndexPerformanceDemo.java

# 运行
java IndexPerformanceDemo
```

## 演示内容

1. **全表扫描 vs 索引查询性能对比**
   - 10万条数据
   - 无索引：线性查找
   - 有索引：二分查找

2. **联合索引与最左前缀原则**
   - 索引 (user_id, status, created_at)
   - 演示哪些查询能用到索引

3. **索引失效场景**
   - 函数调用
   - 类型转换
   - LIKE通配符

## 性能对比预期

```
无索引查询：100-200ms
有索引查询：1-2ms
性能提升：100倍
```

## 学习建议

1. 先阅读 `doc_01.md`
2. 运行代码，观察性能差异
3. 修改代码，测试不同场景
4. 在真实MySQL数据库中实践EXPLAIN分析

## 扩展练习

1. 在MySQL中创建测试表，插入100万条数据
2. 使用EXPLAIN分析不同查询的执行计划
3. 实践索引优化，观察慢查询日志
4. 使用pt-query-digest分析慢查询
