# 建造者模式 - 代码示例

## 示例说明

本目录包含建造者模式的代码示例：

- **BadExample.java**: 不使用 Builder 模式的问题展示
- **GoodExample.java**: 使用 Builder 模式的标准实现

## 编译和运行

### 方式1：分别编译和运行

```bash
# 编译
javac BadExample.java
javac GoodExample.java

# 运行反例
java BadExample

# 运行正例
java GoodExample
```

### 方式2：一次性编译所有文件

```bash
# 编译所有 Java 文件
javac *.java

# 运行
java BadExample
java GoodExample
```

## 预期输出

### BadExample.java 输出

展示不使用 Builder 模式的问题：
- 构造函数参数过多，顺序难记
- 参数位置搞混导致的 bug（编译器检查不出来）
- 重载构造函数的局限性
- 无法灵活设置部分可选参数

### GoodExample.java 输出

展示使用 Builder 模式的优点：
- 链式调用，可读性强
- 灵活设置可选参数
- 参数校验生效
- 代码可读性对比

## 关键知识点

### 1. Telescoping Constructor Problem（构造函数参数过多问题）

当一个类有多个参数时，传统的解决方案是创建多个重载构造函数，但这会导致：
- 构造函数数量爆炸
- 参数顺序容易搞混
- 可读性差

### 2. Builder 模式的核心结构

```java
public class Product {
    // 1. 字段设为 final（不可变）
    private final String field;
    
    // 2. 私有构造函数
    private Product(Builder builder) {
        this.field = builder.field;
    }
    
    // 3. 静态内部类 Builder
    public static class Builder {
        private String field;
        
        // 4. 链式调用方法
        public Builder field(String value) {
            this.field = value;
            return this;  // 返回 this 支持链式调用
        }
        
        // 5. build() 方法
        public Product build() {
            // 参数校验
            return new Product(this);
        }
    }
}
```

### 3. 不可变对象（Immutable Object）

Builder 模式天然支持创建不可变对象：
- 所有字段设为 `final`
- 只提供 getter，不提供 setter
- 构造完成后无法修改

不可变对象的优点：
- 线程安全（可以在多线程环境中安全共享）
- 简化并发编程
- 可以作为 Map 的 key

### 4. 参数校验的最佳实践

在 `build()` 方法中统一进行参数校验：
- 校验必需参数不为空
- 校验参数的取值范围
- 校验参数之间的逻辑关系
- 规范化参数（如转大写、去空格）

## 扩展练习

1. **练习1**：为 `HttpRequest` 添加更多可选参数
   - `userAgent`（用户代理）
   - `cookies`（Cookie 集合）
   - `proxy`（代理设置）

2. **练习2**：实现一个 `EmailBuilder`
   - 必需参数：收件人、主题
   - 可选参数：抄送、密送、附件、优先级

3. **练习3**：实现一个 `SqlQueryBuilder`
   - 支持链式调用构建 SQL 查询
   - 例如：`SELECT ... FROM ... WHERE ... ORDER BY ...`

## 常见问题

**Q: Builder 和 JavaBeans 模式（setter）有什么区别？**

A: 
- JavaBeans 模式：创建对象后通过 setter 设置属性
  - 缺点：对象在构造过程中处于不一致状态、无法创建不可变对象
- Builder 模式：所有属性设置完成后一次性构建对象
  - 优点：对象创建过程清晰、支持不可变对象

**Q: 什么时候应该使用 Builder 模式？**

A: 
- 构造参数多于 4-5 个
- 有多个可选参数
- 需要创建不可变对象
- 参数之间有复杂的校验逻辑

**Q: Builder 模式的缺点是什么？**

A:
- 代码量增加（需要额外的 Builder 类）
- 对于简单对象来说过于复杂
- 但这些缺点可以通过 Lombok 的 `@Builder` 注解缓解
