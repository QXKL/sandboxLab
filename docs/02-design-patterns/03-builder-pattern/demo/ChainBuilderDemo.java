/**
 * 建造者模式 - 链式建造者示例
 *
 * 场景：SQL查询构建器
 * 演示：链式调用、可选参数处理、流式API
 */

// ========== SQL查询构建器 ==========

class SqlQueryBuilder {
    private StringBuilder query;
    private String table;
    private String[] columns;
    private String whereClause;
    private String orderByClause;
    private Integer limitValue;
    private Integer offsetValue;

    public SqlQueryBuilder() {
        this.query = new StringBuilder();
    }

    /**
     * SELECT子句
     */
    public SqlQueryBuilder select(String... columns) {
        this.columns = columns;
        return this;
    }

    /**
     * FROM子句
     */
    public SqlQueryBuilder from(String table) {
        this.table = table;
        return this;
    }

    /**
     * WHERE子句（可选）
     */
    public SqlQueryBuilder where(String condition) {
        this.whereClause = condition;
        return this;
    }

    /**
     * ORDER BY子句（可选）
     */
    public SqlQueryBuilder orderBy(String orderBy) {
        this.orderByClause = orderBy;
        return this;
    }

    /**
     * LIMIT子句（可选）
     */
    public SqlQueryBuilder limit(int limit) {
        this.limitValue = limit;
        return this;
    }

    /**
     * OFFSET子句（可选）
     */
    public SqlQueryBuilder offset(int offset) {
        this.offsetValue = offset;
        return this;
    }

    /**
     * 构建最终SQL
     */
    public String build() {
        // 验证必填参数
        if (table == null || table.isEmpty()) {
            throw new IllegalStateException("必须指定FROM子句");
        }

        query = new StringBuilder();

        // SELECT
        query.append("SELECT ");
        if (columns == null || columns.length == 0) {
            query.append("*");
        } else {
            query.append(String.join(", ", columns));
        }

        // FROM
        query.append(" FROM ").append(table);

        // WHERE（可选）
        if (whereClause != null && !whereClause.isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }

        // ORDER BY（可选）
        if (orderByClause != null && !orderByClause.isEmpty()) {
            query.append(" ORDER BY ").append(orderByClause);
        }

        // LIMIT（可选）
        if (limitValue != null) {
            query.append(" LIMIT ").append(limitValue);
        }

        // OFFSET（可选）
        if (offsetValue != null) {
            query.append(" OFFSET ").append(offsetValue);
        }

        return query.toString();
    }
}

// ========== User类（链式建造者 - Effective Java推荐） ==========

class User {
    // 所有字段都是final（不可变对象）
    private final String username;
    private final String password;
    private final String email;
    private final String phone;
    private final int age;
    private final String address;
    private final boolean newsletter;

    // 私有构造函数
    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.phone = builder.phone;
        this.age = builder.age;
        this.address = builder.address;
        this.newsletter = builder.newsletter;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", newsletter=" + newsletter +
                '}';
    }

    // ========== 静态内部类Builder ==========

    public static class Builder {
        // 必填参数
        private final String username;
        private final String password;

        // 可选参数（有默认值）
        private String email = "";
        private String phone = "";
        private int age = 0;
        private String address = "";
        private boolean newsletter = false;

        /**
         * 构造函数只包含必填参数
         */
        public Builder(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * 链式调用（返回this）
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder newsletter(boolean newsletter) {
            this.newsletter = newsletter;
            return this;
        }

        /**
         * 构建最终对象（含参数验证）
         */
        public User build() {
            // 参数验证
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("username不能为空");
            }
            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("密码至少6位");
            }
            if (email != null && !email.isEmpty() && !email.contains("@")) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("年龄范围不正确");
            }

            return new User(this);
        }
    }
}

// ========== 客户端 ==========

public class ChainBuilderDemo {
    public static void main(String[] args) {
        System.out.println("=== 链式建造者模式示例 ===\n");

        // 示例1：SQL查询构建器
        System.out.println("【1. SQL查询构建器】\n");

        // 简单查询
        String sql1 = new SqlQueryBuilder()
                .select("id", "name", "age")
                .from("users")
                .build();
        System.out.println("查询1: " + sql1);

        // 带WHERE条件
        String sql2 = new SqlQueryBuilder()
                .select("*")
                .from("products")
                .where("price > 100")
                .build();
        System.out.println("查询2: " + sql2);

        // 完整查询
        String sql3 = new SqlQueryBuilder()
                .select("id", "username", "email")
                .from("users")
                .where("age >= 18 AND status = 'active'")
                .orderBy("username ASC")
                .limit(10)
                .offset(20)
                .build();
        System.out.println("查询3: " + sql3);

        // 示例2：User对象构建
        System.out.println("\n【2. User对象构建（不可变对象）】\n");

        // 只有必填参数
        User user1 = new User.Builder("alice", "pass123456")
                .build();
        System.out.println("用户1: " + user1);

        // 部分可选参数
        User user2 = new User.Builder("bob", "pass456789")
                .email("bob@example.com")
                .age(25)
                .build();
        System.out.println("用户2: " + user2);

        // 所有参数
        User user3 = new User.Builder("charlie", "pass789012")
                .email("charlie@example.com")
                .phone("13800138000")
                .age(30)
                .address("Beijing, China")
                .newsletter(true)
                .build();
        System.out.println("用户3: " + user3);

        // 示例3：参数验证
        System.out.println("\n【3. 参数验证】\n");

        try {
            User invalidUser = new User.Builder("dave", "123")  // 密码太短
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 验证失败: " + e.getMessage());
        }

        try {
            User invalidUser = new User.Builder("eve", "pass123456")
                    .email("invalid-email")  // 邮箱格式错误
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("❌ 验证失败: " + e.getMessage());
        }

        // 示例4：对比构造函数方式
        System.out.println("\n【4. 对比：构造函数 vs 建造者】\n");

        System.out.println("❌ 构造函数方式的问题：");
        System.out.println("  User user = new User(\"frank\", \"pass123\", \"frank@example.com\", \"138...\", 28, \"Shanghai\", true);");
        System.out.println("  问题1: 参数顺序容易混淆");
        System.out.println("  问题2: 可读性差（不知道每个参数的含义）");
        System.out.println("  问题3: 可选参数需要传null或默认值\n");

        System.out.println("✅ 建造者方式的优势：");
        System.out.println("  User user = new User.Builder(\"frank\", \"pass123\")");
        System.out.println("      .email(\"frank@example.com\")");
        System.out.println("      .phone(\"138...\")");
        System.out.println("      .age(28)");
        System.out.println("      .address(\"Shanghai\")");
        System.out.println("      .newsletter(true)");
        System.out.println("      .build();");
        System.out.println("  优势1: 参数名称清晰");
        System.out.println("  优势2: 支持可选参数");
        System.out.println("  优势3: 链式调用，流畅API");
        System.out.println("  优势4: 创建不可变对象");

        // 示例5：链式调用的本质
        System.out.println("\n【5. 链式调用的本质】\n");

        System.out.println("每个方法都返回this，支持连续调用：");
        System.out.println();
        System.out.println("public Builder email(String email) {");
        System.out.println("    this.email = email;");
        System.out.println("    return this;  // ← 关键：返回this");
        System.out.println("}");
    }
}
