/**
 * 反例：不使用 Builder 模式的问题
 *
 * 问题展示：
 * 1. 构造函数参数过多（Telescoping Constructor Problem）
 * 2. 参数顺序难记，容易传错
 * 3. 可选参数需要多个重载构造函数
 * 4. 代码可读性差
 */
public class BadExample {

    /**
     * HTTP 请求类（不使用 Builder）
     */
    static class HttpRequest {
        private String method;
        private String url;
        private String contentType;
        private String authorization;
        private String body;
        private int timeout;
        private boolean followRedirects;
        private String charset;

        // 问题1：构造函数参数过多，顺序难记
        public HttpRequest(String method, String url, String contentType,
                          String authorization, String body, int timeout,
                          boolean followRedirects, String charset) {
            this.method = method;
            this.url = url;
            this.contentType = contentType;
            this.authorization = authorization;
            this.body = body;
            this.timeout = timeout;
            this.followRedirects = followRedirects;
            this.charset = charset;
        }

        // 问题2：为了支持可选参数，需要多个重载构造函数
        public HttpRequest(String method, String url) {
            this(method, url, null, null, null, 5000, true, "UTF-8");
        }

        public HttpRequest(String method, String url, String body) {
            this(method, url, null, null, body, 5000, true, "UTF-8");
        }

        public HttpRequest(String method, String url, String contentType, String body) {
            this(method, url, contentType, null, body, 5000, true, "UTF-8");
        }

        // 问题3：还需要更多重载？组合爆炸！
        // 如果有 N 个可选参数，可能需要 2^N 个构造函数

        @Override
        public String toString() {
            return String.format("%s %s\nContent-Type: %s\nAuthorization: %s\nBody: %s\nTimeout: %dms\nCharset: %s",
                method, url, contentType, authorization, body, timeout, charset);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 反例：不使用 Builder 模式 ===\n");

        // 问题1：参数过多，顺序难记，可读性差
        System.out.println("--- 示例1：使用完整构造函数 ---");
        HttpRequest request1 = new HttpRequest(
            "POST",                           // method
            "https://api.example.com/users",  // url
            "application/json",               // contentType
            "Bearer token123",                // authorization
            "{\"name\":\"Alice\"}",           // body
            3000,                             // timeout
            true,                             // followRedirects - 这是什么？
            "UTF-8"                           // charset - 这又是什么？
        );
        System.out.println(request1);
        System.out.println();

        // 问题2：参数顺序容易搞混
        System.out.println("--- 示例2：参数顺序错误（bug！）---");
        // 假设开发者不小心把 contentType 和 authorization 的位置搞反了
        HttpRequest request2 = new HttpRequest(
            "POST",
            "https://api.example.com/users",
            "Bearer token123",                // 错误！这应该是 authorization
            "application/json",               // 错误！这应该是 contentType
            "{\"name\":\"Bob\"}",
            3000,
            true,
            "UTF-8"
        );
        System.out.println("⚠️ 注意：contentType 和 authorization 搞反了，但编译器检查不出来！");
        System.out.println(request2);
        System.out.println();

        // 问题3：使用重载构造函数，但仍然不够灵活
        System.out.println("--- 示例3：使用重载构造函数 ---");
        HttpRequest request3 = new HttpRequest("GET", "https://api.example.com/users/123");
        System.out.println("只能使用默认值，无法灵活设置部分参数");
        System.out.println(request3);
        System.out.println();

        // 问题4：如果想设置 timeout 但不设置 body，怎么办？
        System.out.println("--- 示例4：想设置 timeout 但不设置其他可选参数 ---");
        System.out.println("需要传很多 null 和默认值，非常丑陋：");
        HttpRequest request4 = new HttpRequest(
            "GET",
            "https://api.example.com/users",
            null,    // contentType - 不需要
            null,    // authorization - 不需要
            null,    // body - 不需要
            10000,   // timeout - 想设置这个
            true,    // followRedirects - 被迫传默认值
            "UTF-8"  // charset - 被迫传默认值
        );
        System.out.println(request4);
        System.out.println();

        System.out.println("=== 总结：不使用 Builder 的问题 ===");
        System.out.println("✗ 参数过多，顺序难记");
        System.out.println("✗ 容易传错参数，编译器检查不出来");
        System.out.println("✗ 重载构造函数组合爆炸");
        System.out.println("✗ 可读性差，维护困难");
        System.out.println("✗ 无法灵活设置部分可选参数");
    }
}
