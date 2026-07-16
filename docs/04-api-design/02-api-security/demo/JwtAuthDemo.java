import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * JWT认证演示
 *
 * 演示内容：
 * 1. JWT的生成与验证
 * 2. Token过期处理
 * 3. Refresh Token机制
 * 4. 安全注意事项
 */
public class JwtAuthDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(80));
        System.out.println("JWT认证演示");
        System.out.println("=".repeat(80));
        System.out.println();

        // 1. 用户登录，生成JWT
        demo1_login();

        // 2. 使用JWT访问API
        demo2_accessApi();

        // 3. Token过期处理
        demo3_tokenExpiration();

        // 4. Refresh Token机制
        demo4_refreshToken();

        // 5. 安全注意事项
        demo5_securityConcerns();
    }

    private static void demo1_login() {
        printSection("1. 用户登录，生成JWT");

        JwtService jwtService = new JwtService();

        System.out.println("用户登录：");
        System.out.println("  username: zhangsan");
        System.out.println("  password: ******");
        System.out.println();

        // 验证用户名密码（省略）
        User user = new User(123L, "zhangsan", "admin");

        // 生成JWT
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        System.out.println("登录成功，返回Token：");
        System.out.println("Access Token (15分钟):");
        System.out.println("  " + accessToken);
        System.out.println();
        System.out.println("Refresh Token (7天):");
        System.out.println("  " + refreshToken);
        System.out.println();

        // 解析JWT
        System.out.println("解析Access Token：");
        JwtPayload payload = jwtService.parseToken(accessToken);
        System.out.println("  userId: " + payload.userId);
        System.out.println("  username: " + payload.username);
        System.out.println("  role: " + payload.role);
        System.out.println("  过期时间: " + new Date(payload.exp * 1000));
        System.out.println();
    }

    private static void demo2_accessApi() {
        printSection("2. 使用JWT访问API");

        JwtService jwtService = new JwtService();
        User user = new User(123L, "zhangsan", "admin");
        String token = jwtService.generateAccessToken(user);

        System.out.println("请求API：");
        System.out.println("  GET /api/users");
        System.out.println("  Authorization: Bearer " + token);
        System.out.println();

        // 服务端验证Token
        try {
            JwtPayload payload = jwtService.verifyToken(token);
            System.out.println("✅ Token验证成功");
            System.out.println("  当前用户: " + payload.username);
            System.out.println("  用户ID: " + payload.userId);
            System.out.println("  角色: " + payload.role);
            System.out.println();
            System.out.println("返回API数据...");
        } catch (Exception e) {
            System.out.println("❌ Token验证失败: " + e.getMessage());
        }
        System.out.println();
    }

    private static void demo3_tokenExpiration() throws InterruptedException {
        printSection("3. Token过期处理");

        JwtService jwtService = new JwtService();
        User user = new User(123L, "zhangsan", "admin");

        // 生成一个3秒后过期的Token（演示用）
        String token = jwtService.generateTokenWithExpiry(user, 3);

        System.out.println("生成Token（3秒后过期）");
        System.out.println();

        // 立即使用Token
        System.out.println("[0秒] 使用Token访问API：");
        try {
            jwtService.verifyToken(token);
            System.out.println("  ✅ Token有效");
        } catch (Exception e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        System.out.println();

        // 等待3秒
        System.out.println("等待3秒...");
        Thread.sleep(3000);
        System.out.println();

        // 再次使用Token
        System.out.println("[3秒后] 使用Token访问API：");
        try {
            jwtService.verifyToken(token);
            System.out.println("  ✅ Token有效");
        } catch (Exception e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        System.out.println();
    }

    private static void demo4_refreshToken() {
        printSection("4. Refresh Token机制");

        JwtService jwtService = new JwtService();
        User user = new User(123L, "zhangsan", "admin");

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        System.out.println("初始Token：");
        System.out.println("  Access Token: " + accessToken.substring(0, 50) + "...");
        System.out.println("  Refresh Token: " + refreshToken.substring(0, 50) + "...");
        System.out.println();

        // 模拟Access Token过期
        System.out.println("Access Token过期，使用Refresh Token刷新：");
        System.out.println("  POST /refresh");
        System.out.println("  Body: {\"refreshToken\": \"" + refreshToken.substring(0, 30) + "...\"}");
        System.out.println();

        // 验证Refresh Token并生成新的Access Token
        try {
            JwtPayload payload = jwtService.verifyToken(refreshToken);
            User refreshedUser = new User(payload.userId, payload.username, payload.role);
            String newAccessToken = jwtService.generateAccessToken(refreshedUser);

            System.out.println("✅ 刷新成功");
            System.out.println("  新的Access Token: " + newAccessToken.substring(0, 50) + "...");
            System.out.println();
            System.out.println("💡 注意：Refresh Token应该在使用后立即失效（一次性）");
        } catch (Exception e) {
            System.out.println("❌ 刷新失败: " + e.getMessage());
        }
        System.out.println();
    }

    private static void demo5_securityConcerns() {
        printSection("5. 安全注意事项");

        System.out.println("❌ 不要在JWT中存储敏感信息：");
        System.out.println();
        System.out.println("错误示例：");
        System.out.println("{");
        System.out.println("  \"userId\": 123,");
        System.out.println("  \"password\": \"123456\",        // ❌ 密码");
        System.out.println("  \"creditCard\": \"1234-5678\"   // ❌ 信用卡");
        System.out.println("}");
        System.out.println();
        System.out.println("原因：JWT的Payload只是Base64编码，不是加密！");
        System.out.println("任何人都可以解码查看内容。");
        System.out.println();

        System.out.println("✅ 正确做法：");
        System.out.println("{");
        System.out.println("  \"userId\": 123,");
        System.out.println("  \"username\": \"zhangsan\",");
        System.out.println("  \"role\": \"admin\",");
        System.out.println("  \"exp\": 1626239082");
        System.out.println("}");
        System.out.println();

        System.out.println("其他安全建议：");
        System.out.println("  ✓ 设置合理的过期时间（Access Token: 15分钟 - 1小时）");
        System.out.println("  ✓ 使用足够长的密钥（至少256位）");
        System.out.println("  ✓ 密钥从环境变量读取，不要硬编码");
        System.out.println("  ✓ 始终使用HTTPS传输JWT");
        System.out.println("  ✓ 前端存储JWT在内存或HttpOnly Cookie，不要存localStorage");
        System.out.println();
    }

    private static void printSection(String title) {
        System.out.println("━".repeat(80));
        System.out.println(title);
        System.out.println("━".repeat(80));
        System.out.println();
    }
}

/**
 * JWT服务（简化实现）
 *
 * 注意：这是教学演示代码，实际项目应使用成熟的JWT库（如jjwt、auth0-java-jwt）
 */
class JwtService {
    private static final String SECRET_KEY = "my-super-secret-key-at-least-256-bits-long-for-security";
    private static final int ACCESS_TOKEN_EXPIRY = 15 * 60;  // 15分钟
    private static final int REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60;  // 7天

    /**
     * 生成Access Token
     */
    public String generateAccessToken(User user) {
        return generateTokenWithExpiry(user, ACCESS_TOKEN_EXPIRY);
    }

    /**
     * 生成Refresh Token
     */
    public String generateRefreshToken(User user) {
        return generateTokenWithExpiry(user, REFRESH_TOKEN_EXPIRY);
    }

    /**
     * 生成指定过期时间的Token
     */
    public String generateTokenWithExpiry(User user, int expirySeconds) {
        long now = Instant.now().getEpochSecond();
        long exp = now + expirySeconds;

        // Header
        String header = base64Encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");

        // Payload
        String payload = base64Encode(String.format(
            "{\"userId\":%d,\"username\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
            user.id, user.username, user.role, now, exp
        ));

        // Signature（简化实现，实际应使用HMAC-SHA256）
        String signature = base64Encode(hmacSha256(header + "." + payload, SECRET_KEY));

        return header + "." + payload + "." + signature;
    }

    /**
     * 解析Token（不验证）
     */
    public JwtPayload parseToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        String payloadJson = base64Decode(parts[1]);
        return JwtPayload.fromJson(payloadJson);
    }

    /**
     * 验证Token
     */
    public JwtPayload verifyToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new SecurityException("Invalid JWT format");
        }

        // 验证签名
        String expectedSignature = base64Encode(hmacSha256(parts[0] + "." + parts[1], SECRET_KEY));
        if (!parts[2].equals(expectedSignature)) {
            throw new SecurityException("Invalid signature");
        }

        // 解析Payload
        JwtPayload payload = parseToken(token);

        // 检查是否过期
        long now = Instant.now().getEpochSecond();
        if (payload.exp < now) {
            throw new SecurityException("Token expired");
        }

        return payload;
    }

    // 简化的Base64编码/解码
    private String base64Encode(String str) {
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    private String base64Decode(String str) {
        return new String(Base64.getUrlDecoder().decode(str), StandardCharsets.UTF_8);
    }

    // 简化的HMAC-SHA256（实际应使用标准库）
    private String hmacSha256(String data, String key) {
        return "signature-" + data.hashCode() + "-" + key.hashCode();
    }
}

/**
 * JWT Payload
 */
class JwtPayload {
    long userId;
    String username;
    String role;
    long iat;  // issued at
    long exp;  // expiration

    static JwtPayload fromJson(String json) {
        // 简化的JSON解析（实际应使用JSON库）
        JwtPayload payload = new JwtPayload();

        json = json.replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] kv = pair.split(":");
            String key = kv[0].trim();
            String value = kv[1].trim();

            switch (key) {
                case "userId":
                    payload.userId = Long.parseLong(value);
                    break;
                case "username":
                    payload.username = value;
                    break;
                case "role":
                    payload.role = value;
                    break;
                case "iat":
                    payload.iat = Long.parseLong(value);
                    break;
                case "exp":
                    payload.exp = Long.parseLong(value);
                    break;
            }
        }

        return payload;
    }
}

/**
 * 用户实体
 */
class User {
    Long id;
    String username;
    String role;

    User(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
