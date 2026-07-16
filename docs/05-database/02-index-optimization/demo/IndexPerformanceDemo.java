import java.util.*;

/**
 * 索引性能演示
 *
 * 模拟全表扫描 vs 索引查询的性能差异
 */
public class IndexPerformanceDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("索引性能演示");
        System.out.println("=".repeat(80));
        System.out.println();

        demo1_performanceComparison();
        demo2_compositeIndex();
        demo3_indexFailure();
    }

    /**
     * 演示1：性能对比（全表扫描 vs 索引查询）
     */
    private static void demo1_performanceComparison() {
        System.out.println("━".repeat(80));
        System.out.println("1. 性能对比：全表扫描 vs 索引查询");
        System.out.println("━".repeat(80));
        System.out.println();

        int dataSize = 100000;
        System.out.println("准备测试数据：" + dataSize + " 条用户记录");

        // 无索引的表（使用List模拟）
        List<User> usersWithoutIndex = new ArrayList<>();
        for (int i = 1; i <= dataSize; i++) {
            usersWithoutIndex.add(new User(i, "user" + i + "@example.com"));
        }

        // 有索引的表（使用HashMap模拟）
        Map<String, User> usersWithIndex = new HashMap<>();
        for (User user : usersWithoutIndex) {
            usersWithIndex.put(user.email, user);
        }

        System.out.println();

        // 测试查询：查找 email = "user99999@example.com"
        String targetEmail = "user99999@example.com";

        // 全表扫描
        long start1 = System.nanoTime();
        User result1 = null;
        for (User user : usersWithoutIndex) {
            if (user.email.equals(targetEmail)) {
                result1 = user;
                break;
            }
        }
        long end1 = System.nanoTime();
        double time1 = (end1 - start1) / 1_000_000.0;

        System.out.println("❌ 无索引（全表扫描）：");
        System.out.println("  SELECT * FROM users WHERE email = '" + targetEmail + "'");
        System.out.println("  扫描行数: " + dataSize);
        System.out.println("  耗时: " + String.format("%.2f", time1) + " ms");
        System.out.println("  结果: " + (result1 != null ? "找到 id=" + result1.id : "未找到"));
        System.out.println();

        // 索引查询
        long start2 = System.nanoTime();
        User result2 = usersWithIndex.get(targetEmail);
        long end2 = System.nanoTime();
        double time2 = (end2 - start2) / 1_000_000.0;

        System.out.println("✅ 有索引（索引查询）：");
        System.out.println("  CREATE INDEX idx_email ON users(email);");
        System.out.println("  SELECT * FROM users WHERE email = '" + targetEmail + "'");
        System.out.println("  扫描行数: 1");
        System.out.println("  耗时: " + String.format("%.4f", time2) + " ms");
        System.out.println("  结果: " + (result2 != null ? "找到 id=" + result2.id : "未找到"));
        System.out.println();

        System.out.println("性能提升: " + String.format("%.0f", time1 / time2) + " 倍");
        System.out.println();
    }

    /**
     * 演示2：联合索引与最左前缀原则
     */
    private static void demo2_compositeIndex() {
        System.out.println("━".repeat(80));
        System.out.println("2. 联合索引与最左前缀原则");
        System.out.println("━".repeat(80));
        System.out.println();

        System.out.println("假设有索引：INDEX idx_abc (a, b, c)");
        System.out.println();

        String[][] queries = {
            {"WHERE a = 1", "✅ 能用索引（a）"},
            {"WHERE a = 1 AND b = 2", "✅ 能用索引（a, b）"},
            {"WHERE a = 1 AND b = 2 AND c = 3", "✅ 能用索引（a, b, c）"},
            {"WHERE b = 2", "❌ 不能用索引（跳过a）"},
            {"WHERE c = 3", "❌ 不能用索引（跳过a, b）"},
            {"WHERE a = 1 AND c = 3", "⚠️ 部分用索引（只用a，c不用）"},
            {"WHERE b = 2 AND c = 3", "❌ 不能用索引（跳过a）"}
        };

        for (String[] query : queries) {
            System.out.println("  " + query[0]);
            System.out.println("    → " + query[1]);
        }

        System.out.println();
        System.out.println("原理：索引数据按 a → b → c 排序");
        System.out.println("  如果跳过前面的字段，后面的字段是无序的，无法使用！");
        System.out.println();
    }

    /**
     * 演示3：索引失效场景
     */
    private static void demo3_indexFailure() {
        System.out.println("━".repeat(80));
        System.out.println("3. 索引失效场景");
        System.out.println("━".repeat(80));
        System.out.println();

        System.out.println("假设有索引：INDEX idx_name ON users(name)");
        System.out.println();

        System.out.println("场景1：对索引字段使用函数");
        System.out.println("  ❌ SELECT * FROM users WHERE UPPER(name) = 'ZHANG'");
        System.out.println("     → 索引失效（对name使用了UPPER函数）");
        System.out.println("  ✅ SELECT * FROM users WHERE name = 'zhang'");
        System.out.println("     → 能用索引");
        System.out.println();

        System.out.println("场景2：LIKE以通配符开头");
        System.out.println("  ❌ SELECT * FROM users WHERE name LIKE '%zhang%'");
        System.out.println("     → 索引失效（%在开头）");
        System.out.println("  ✅ SELECT * FROM users WHERE name LIKE 'zhang%'");
        System.out.println("     → 能用索引");
        System.out.println();

        System.out.println("场景3：隐式类型转换");
        System.out.println("  假设 phone 字段是 VARCHAR");
        System.out.println("  ❌ SELECT * FROM users WHERE phone = 13800138000");
        System.out.println("     → 索引失效（字符串 vs 数字）");
        System.out.println("  ✅ SELECT * FROM users WHERE phone = '13800138000'");
        System.out.println("     → 能用索引");
        System.out.println();

        System.out.println("场景4：OR条件中有未建索引的字段");
        System.out.println("  假设 email 有索引，phone 无索引");
        System.out.println("  ❌ SELECT * FROM users WHERE email = 'test@example.com' OR phone = '138...'");
        System.out.println("     → 索引失效");
        System.out.println("  ✅ SELECT * FROM users WHERE email = 'test@example.com'");
        System.out.println("     UNION");
        System.out.println("     SELECT * FROM users WHERE phone = '138...'");
        System.out.println("     → 分别走索引");
        System.out.println();

        System.out.println("💡 避免索引失效的原则：");
        System.out.println("  1. 不对索引字段使用函数");
        System.out.println("  2. LIKE不以%开头");
        System.out.println("  3. 注意字段类型匹配");
        System.out.println("  4. OR条件改为UNION");
        System.out.println("  5. 遵循最左前缀原则");
        System.out.println();
    }

    /**
     * 用户实体
     */
    static class User {
        long id;
        String email;

        User(long id, String email) {
            this.id = id;
            this.email = email;
        }
    }
}
