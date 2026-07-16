import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

/**
 * API安全综合演示
 *
 * 演示内容：
 * 1. SQL注入攻击与防护
 * 2. XSS攻击与防护
 * 3. IDOR（不安全的直接对象引用）与防护
 * 4. 密码存储（加盐哈希）
 * 5. 输入验证
 */
public class SecurityDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("API安全综合演示");
        System.out.println("=".repeat(80));
        System.out.println();

        demo1_sqlInjection();
        demo2_xss();
        demo3_idor();
        demo4_passwordStorage();
        demo5_inputValidation();
    }

    private static void demo1_sqlInjection() {
        System.out.println("━".repeat(80));
        System.out.println("1. SQL注入攻击与防护");
        System.out.println("━".repeat(80));
        System.out.println();

        // 模拟数据库
        Map<String, String> database = new HashMap<>();
        database.put("admin", "admin123");
        database.put("user1", "password1");

        System.out.println("❌ 不安全的实现（字符串拼接）：");
        System.out.println();

        String username1 = "admin";
        String password1 = "wrong_password";
        System.out.println("正常登录尝试：");
        System.out.println("  username: " + username1);
        System.out.println("  password: " + password1);

        String sql1 = "SELECT * FROM users WHERE username = '" + username1 +
                     "' AND password = '" + password1 + "'";
        System.out.println("  生成的SQL: " + sql1);
        System.out.println("  结果: 登录失败（密码错误）");
        System.out.println();

        // SQL注入攻击
        String username2 = "admin' OR '1'='1";
        String password2 = "anything";
        System.out.println("SQL注入攻击：");
        System.out.println("  username: " + username2);
        System.out.println("  password: " + password2);

        String sql2 = "SELECT * FROM users WHERE username = '" + username2 +
                     "' AND password = '" + password2 + "'";
        System.out.println("  生成的SQL: " + sql2);
        System.out.println("  等价于: SELECT * FROM users WHERE username = 'admin' OR '1'='1' AND password = 'anything'");
        System.out.println("  结果: ⚠️  登录成功（OR '1'='1' 永远为真，绕过密码验证）");
        System.out.println();

        System.out.println("✅ 安全的实现（参数化查询）：");
        System.out.println();
        System.out.println("  String sql = \"SELECT * FROM users WHERE username = ? AND password = ?\";");
        System.out.println("  PreparedStatement stmt = connection.prepareStatement(sql);");
        System.out.println("  stmt.setString(1, username);  // 参数会被自动转义");
        System.out.println("  stmt.setString(2, password);");
        System.out.println();
        System.out.println("  攻击输入: admin' OR '1'='1");
        System.out.println("  实际查询: WHERE username = 'admin\\' OR \\'1\\'=\\'1'");
        System.out.println("  结果: 登录失败（被当作普通字符串，不会执行）");
        System.out.println();
    }

    private static void demo2_xss() {
        System.out.println("━".repeat(80));
        System.out.println("2. XSS攻击与防护");
        System.out.println("━".repeat(80));
        System.out.println();

        String normalComment = "这是一条正常评论";
        String xssComment = "<script>alert('XSS攻击！窃取Cookie: ' + document.cookie)</script>";

        System.out.println("❌ 不安全的实现（直接输出HTML）：");
        System.out.println();
        System.out.println("用户提交评论: " + xssComment);
        System.out.println();
        System.out.println("页面渲染：");
        System.out.println("  <div>" + xssComment + "</div>");
        System.out.println();
        System.out.println("  结果: ⚠️  脚本会执行，攻击者可以窃取Cookie、劫持会话");
        System.out.println();

        System.out.println("✅ 安全的实现（HTML转义）：");
        System.out.println();
        String escapedComment = escapeHtml(xssComment);
        System.out.println("转义后的内容: " + escapedComment);
        System.out.println();
        System.out.println("页面渲染：");
        System.out.println("  <div>" + escapedComment + "</div>");
        System.out.println();
        System.out.println("  结果: ✅ 脚本被转义，作为普通文本显示，不会执行");
        System.out.println();
    }

    private static void demo3_idor() {
        System.out.println("━".repeat(80));
        System.out.println("3. IDOR（不安全的直接对象引用）与防护");
        System.out.println("━".repeat(80));
        System.out.println();

        // 模拟当前登录用户
        long currentUserId = 123L;

        System.out.println("当前登录用户ID: " + currentUserId);
        System.out.println();

        System.out.println("❌ 不安全的实现（不检查资源归属）：");
        System.out.println();
        System.out.println("请求: GET /api/users/124/orders");
        System.out.println("代码:");
        System.out.println("  public List<Order> getUserOrders(@PathVariable Long userId) {");
        System.out.println("      return orderService.getOrdersByUserId(userId);");
        System.out.println("  }");
        System.out.println();
        System.out.println("  结果: ⚠️  用户123可以查看用户124的订单（越权）");
        System.out.println();

        System.out.println("✅ 安全的实现（检查资源归属）：");
        System.out.println();
        System.out.println("请求: GET /api/users/124/orders");
        System.out.println("代码:");
        System.out.println("  public List<Order> getUserOrders(");
        System.out.println("      @PathVariable Long userId,");
        System.out.println("      @AuthenticationPrincipal User currentUser");
        System.out.println("  ) {");
        System.out.println("      if (!currentUser.getId().equals(userId) && !currentUser.isAdmin()) {");
        System.out.println("          throw new ForbiddenException(\"无权访问其他用户的订单\");");
        System.out.println("      }");
        System.out.println("      return orderService.getOrdersByUserId(userId);");
        System.out.println("  }");
        System.out.println();
        System.out.println("  结果: ✅ 返回403 Forbidden（检查通过才允许访问）");
        System.out.println();
    }

    private static void demo4_passwordStorage() {
        System.out.println("━".repeat(80));
        System.out.println("4. 密码存储（加盐哈希）");
        System.out.println("━".repeat(80));
        System.out.println();

        String password = "myPassword123";

        System.out.println("❌ 绝对不要：明文存储");
        System.out.println("  数据库: password = \"myPassword123\"");
        System.out.println("  危害: 数据库泄露，所有密码暴露");
        System.out.println();

        System.out.println("❌ 不要：简单MD5");
        String md5 = simpleMd5(password);
        System.out.println("  数据库: password = \"" + md5 + "\"");
        System.out.println("  危害: 彩虹表攻击可以反推（常见密码的MD5已被收录）");
        System.out.println();

        System.out.println("✅ 正确做法：加盐哈希（bcrypt）");
        System.out.println();
        PasswordService passwordService = new PasswordService();
        String hashedPassword = passwordService.hashPassword(password);
        System.out.println("  原始密码: " + password);
        System.out.println("  存储值: " + hashedPassword);
        System.out.println();

        System.out.println("验证密码：");
        boolean valid1 = passwordService.verifyPassword("myPassword123", hashedPassword);
        System.out.println("  输入: myPassword123 → " + (valid1 ? "✅ 正确" : "❌ 错误"));

        boolean valid2 = passwordService.verifyPassword("wrongPassword", hashedPassword);
        System.out.println("  输入: wrongPassword → " + (valid2 ? "✅ 正确" : "❌ 错误"));
        System.out.println();

        System.out.println("每次生成的哈希都不同（随机盐）：");
        String hash1 = passwordService.hashPassword(password);
        String hash2 = passwordService.hashPassword(password);
        System.out.println("  Hash 1: " + hash1);
        System.out.println("  Hash 2: " + hash2);
        System.out.println("  但都能验证通过！");
        System.out.println();
    }

    private static void demo5_inputValidation() {
        System.out.println("━".repeat(80));
        System.out.println("5. 输入验证");
        System.out.println("━".repeat(80));
        System.out.println();

        InputValidator validator = new InputValidator();

        System.out.println("用户名验证（3-20位，字母、数字、下划线）：");
        testValidation("zhangsan", () -> validator.validateUsername("zhangsan"));
        testValidation("ab", () -> validator.validateUsername("ab"));
        testValidation("admin<script>", () -> validator.validateUsername("admin<script>"));
        System.out.println();

        System.out.println("邮箱验证：");
        testValidation("test@example.com", () -> validator.validateEmail("test@example.com"));
        testValidation("invalid-email", () -> validator.validateEmail("invalid-email"));
        System.out.println();

        System.out.println("手机号验证（中国）：");
        testValidation("13800138000", () -> validator.validatePhone("13800138000"));
        testValidation("12345678901", () -> validator.validatePhone("12345678901"));
        System.out.println();

        System.out.println("💡 验证原则：");
        System.out.println("  ✓ 使用白名单（只允许合法字符）");
        System.out.println("  ✓ 前端验证 + 后端验证（前端可被绕过）");
        System.out.println("  ✓ 限制字段长度");
        System.out.println("  ✓ 对特殊场景使用正则表达式");
        System.out.println();
    }

    // ==================== 辅助方法 ====================

    private static String escapeHtml(String input) {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }

    private static String simpleMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return "error";
        }
    }

    private static void testValidation(String input, Runnable validation) {
        try {
            validation.run();
            System.out.println("  \"" + input + "\" → ✅ 通过");
        } catch (Exception e) {
            System.out.println("  \"" + input + "\" → ❌ " + e.getMessage());
        }
    }
}

/**
 * 密码服务（简化实现）
 */
class PasswordService {
    public String hashPassword(String plainPassword) {
        String salt = generateSalt();
        String hash = hash(plainPassword + salt);
        return salt + ":" + hash;
    }

    public boolean verifyPassword(String plainPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        String salt = parts[0];
        String storedHash = parts[1];
        String computedHash = hash(plainPassword + salt);
        return computedHash.equals(storedHash);
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return "error";
        }
    }
}

/**
 * 输入验证器
 */
class InputValidator {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    public void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("用户名格式不正确（3-20位，字母数字下划线）");
        }
    }

    public void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
    }
}
