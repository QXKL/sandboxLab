# N+1查询问题代码示例

## 文件说明

- **NPlusOneProblemDemo.java** - N+1问题演示与解决方案对比

## 如何运行

```bash
javac NPlusOneProblemDemo.java
java NPlusOneProblemDemo
```

## 演示内容

1. **N+1问题演示**
   - 1次查询用户 + N次查询订单
   - 性能对比

2. **JOIN解决方案**
   - 一次性查询所有数据
   - 性能提升明显

3. **IN查询解决方案**
   - 两次查询
   - 代码清晰

## 性能预期

```
N+1查询（100个用户）：100-200ms
JOIN查询：5-10ms
IN查询：10-20ms
```

## 学习建议

1. 阅读 doc_01.md
2. 运行代码观察性能差异
3. 在实际项目中检测N+1问题
4. 使用Hibernate Statistics监控
