/**
 * 外观模式 - 数据库操作示例
 *
 * 场景：简化JDBC复杂API，提供简单的数据库操作接口
 * 演示：外观隐藏复杂的资源管理和异常处理
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ========== 子系统（模拟JDBC组件） ==========

/**
 * 子系统1：数据库连接（模拟）
 */
class Connection {
    public void open(String url) {
        System.out.println("  🔌 [连接] 打开数据库连接: " + url);
    }

    public void close() {
        System.out.println("  🔌 [连接] 关闭数据库连接");
    }

    public void commit() {
        System.out.println("  ✅ [连接] 提交事务");
    }

    public void rollback() {
        System.out.println("  ↩️  [连接] 回滚事务");
    }
}

/**
 * 子系统2：SQL语句（模拟）
 */
class Statement {
    public ResultSet executeQuery(String sql) {
        System.out.println("  📝 [语句] 执行查询: " + sql);
        // 模拟查询结果
        ResultSet rs = new ResultSet();
        rs.addRow(Map.of("id", "1", "name", "Alice"));
        rs.addRow(Map.of("id", "2", "name", "Bob"));
        return rs;
    }

    public int executeUpdate(String sql) {
        System.out.println("  📝 [语句] 执行更新: " + sql);
        return 1;  // 模拟影响1行
    }

    public void close() {
        System.out.println("  📝 [语句] 关闭语句");
    }
}

/**
 * 子系统3：结果集（模拟）
 */
class ResultSet {
    private List<Map<String, String>> data = new ArrayList<>();
    private int currentRow = -1;

    public void addRow(Map<String, String> row) {
        data.add(row);
    }

    public boolean next() {
        currentRow++;
        return currentRow < data.size();
    }

    public String getString(String column) {
        return data.get(currentRow).get(column);
    }

    public void close() {
        System.out.println("  📋 [结果集] 关闭结果集");
    }
}

// ========== 外观 ==========

/**
 * 外观：数据库操作外观（类似JdbcTemplate）
 */
class DatabaseFacade {
    private String dbUrl;

    public DatabaseFacade(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * 高层接口：查询（返回List）
     */
    public List<Map<String, String>> query(String sql) {
        System.out.println("\n📊 执行查询\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String, String>> results = new ArrayList<>();

        try {
            // 外观隐藏复杂的资源管理
            conn = new Connection();
            conn.open(dbUrl);

            stmt = new Statement();
            rs = stmt.executeQuery(sql);

            // 处理结果
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("id", rs.getString("id"));
                row.put("name", rs.getString("name"));
                results.add(row);
            }

            System.out.println("  ✅ 查询成功，返回" + results.size() + "条记录\n");
            return results;

        } catch (Exception e) {
            System.out.println("  ❌ 查询失败: " + e.getMessage() + "\n");
            return results;
        } finally {
            // 外观负责资源清理
            closeResources(rs, stmt, conn);
        }
    }

    /**
     * 高层接口：更新（返回影响行数）
     */
    public int update(String sql) {
        System.out.println("\n✏️  执行更新\n");

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = new Connection();
            conn.open(dbUrl);

            stmt = new Statement();
            int rows = stmt.executeUpdate(sql);

            conn.commit();
            System.out.println("  ✅ 更新成功，影响" + rows + "行\n");
            return rows;

        } catch (Exception e) {
            System.out.println("  ❌ 更新失败: " + e.getMessage());
            if (conn != null) {
                conn.rollback();
            }
            System.out.println();
            return 0;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    /**
     * 关闭资源（隐藏复杂性）
     */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            System.out.println("  ⚠️  关闭资源异常");
        }
    }
}

// ========== 测试 ==========

public class DatabaseFacadeDemo {
    public static void main(String[] args) {
        System.out.println("=== 外观模式 - 数据库操作 ===\n");

        // 示例1：原始JDBC（复杂）
        System.out.println("【1. 原始JDBC（复杂）】");
        demonstrateRawJDBC();

        // 示例2：使用外观（简单）
        System.out.println("\n【2. 使用外观（简化）】");
        demonstrateWithFacade();

        // 示例3：外观的价值
        System.out.println("\n【3. 外观的价值】");
        demonstrateValue();
    }

    /**
     * 原始JDBC（复杂）
     */
    static void demonstrateRawJDBC() {
        System.out.println("原始JDBC代码（复杂）:\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = new Connection();
            conn.open("jdbc:mysql://localhost:3306/test");

            stmt = new Statement();
            rs = stmt.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                System.out.println("  User: " + id + ", " + name);
            }

        } catch (Exception e) {
            System.out.println("查询失败");
        } finally {
            // 必须手动关闭资源（容易忘记）
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                // ...
            }
        }

        System.out.println("\n❌ 问题:");
        System.out.println("  - 代码冗长（资源管理）");
        System.out.println("  - 异常处理复杂");
        System.out.println("  - 容易忘记关闭资源");
        System.out.println("  - 重复代码多");
    }

    /**
     * 使用外观（简单）
     */
    static void demonstrateWithFacade() {
        DatabaseFacade db = new DatabaseFacade("jdbc:mysql://localhost:3306/test");

        // 简单的查询
        List<Map<String, String>> users = db.query("SELECT * FROM users");
        System.out.println("查询结果:");
        for (Map<String, String> user : users) {
            System.out.println("  User: " + user.get("id") + ", " + user.get("name"));
        }

        // 简单的更新
        int rows = db.update("UPDATE users SET name = 'Charlie' WHERE id = 1");
        System.out.println("更新结果: 影响" + rows + "行");

        System.out.println("\n✅ 优势:");
        System.out.println("  - 代码简洁（自动资源管理）");
        System.out.println("  - 异常处理统一");
        System.out.println("  - 不会忘记关闭资源");
        System.out.println("  - 消除重复代码");
    }

    /**
     * 演示外观的价值
     */
    static void demonstrateValue() {
        System.out.println("\n外观隐藏的复杂性:\n");
        System.out.println("1. 连接管理:");
        System.out.println("   - 打开连接");
        System.out.println("   - 关闭连接");
        System.out.println("   - 连接池管理");

        System.out.println("\n2. 事务管理:");
        System.out.println("   - 提交事务");
        System.out.println("   - 回滚事务");
        System.out.println("   - 异常时自动回滚");

        System.out.println("\n3. 资源清理:");
        System.out.println("   - 关闭ResultSet");
        System.out.println("   - 关闭Statement");
        System.out.println("   - 关闭Connection");
        System.out.println("   - 保证顺序正确");

        System.out.println("\n4. 异常处理:");
        System.out.println("   - 统一异常转换");
        System.out.println("   - 友好的错误信息");

        System.out.println("\n✅ 这就是Spring JdbcTemplate的本质!");
        System.out.println("  - JdbcTemplate是外观模式");
        System.out.println("  - 简化JDBC复杂API");
        System.out.println("  - 消除样板代码");

        System.out.println("\n📊 代码量对比:");
        System.out.println("  - 原始JDBC: ~30行（每次查询）");
        System.out.println("  - 使用外观: 1行（调用外观方法）");
        System.out.println("  - 减少代码: 97%");
    }
}
