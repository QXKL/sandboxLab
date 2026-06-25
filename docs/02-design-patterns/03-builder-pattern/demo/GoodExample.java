import java.util.HashMap;
import java.util.Map;

/**
 * 正例：使用 Builder 模式
 *
 * 优点：
 * 1. 链式调用，可读性强
 * 2. 灵活设置可选参数
 * 3. 支持不可变对象
 * 4. 参数校验集中在 build() 方法
 */
public class GoodExample {

    /**
     * HTTP 请求类（使用 Builder 模式）
     */
    static class HttpRequest {
        // 所有字段设为 final，创建不可变对象
        private final String method;
        private final String url;
        private final Map<String, String> headers;
        private final String body;
        private final int timeout;
        private final boolean followRedirects;
        private final String charset;

        // 私有构造函数，只能通过 Builder 创建
        private HttpRequest(Builder builder) {
            this.method = builder.method;
            this.url = builder.url;
            this.headers = new HashMap<>(builder.headers); // 防御性拷贝
            this.body = builder.body;
            this.timeout = builder.timeout;
            this.followRedirects = builder.followRedirects;
            this.charset = builder.charset;
        }

        // 只提供 getter，不提供 setter（不可变对象）
        public String getMethod() { return method; }
        public String getUrl() { return url; }
        public Map<String, String> getHeaders() { return new HashMap<>(headers); }
        public String getBody() { return body; }
        public int getTimeout() { return timeout; }
        public boolean isFollowRedirects() { return followRedirects; }
        public String getCharset() { return charset; }

        @Override
        public String toString() {
            return String.format("%s %s\nHeaders: %s\nBody: %s\nTimeout: %dms\nCharset: %s\nFollowRedirects: %s",
                method, url, headers, body, timeout, charset, followRedirects);
        }

        /**
         * 静态内部类 Builder
         */
        public static class Builder {
            // 必需参数
            private String method;
            private String url;

            // 可选参数（设置默认值）
            private Map<String, String> headers = new HashMap<>();
            private String body;
            private int timeout = 5000;  // 默认 5 秒
            private boolean followRedirects = true;
            private String charset = "UTF-8";

            // 设置必需参数的构造函数（可选）
            public Builder() {
            }

            // 或者通过方法设置必需参数
            public Builder method(String method) {
                this.method = method;
                return this;
            }

            public Builder url(String url) {
                this.url = url;
                return this;
            }

            // 可选参数的设置方法
            public Builder addHeader(String key, String value) {
                this.headers.put(key, value);
                return this;
            }

            public Builder headers(Map<String, String> headers) {
                this.headers.putAll(headers);
                return this;
            }

            public Builder body(String body) {
                this.body = body;
                return this;
            }

            public Builder timeout(int timeout) {
                this.timeout = timeout;
                return this;
            }

            public Builder followRedirects(boolean followRedirects) {
                this.followRedirects = followRedirects;
                return this;
            }

            public Builder charset(String charset) {
                this.charset = charset;
                return this;
            }

            /**
             * 构建 HttpRequest 对象
             * 在这里进行参数校验
             */
            public HttpRequest build() {
                // 校验必需参数
                if (method == null || method.trim().isEmpty()) {
                    throw new IllegalStateException("method 不能为空");
                }
                if (url == null || url.trim().isEmpty()) {
                    throw new IllegalStateException("url 不能为空");
                }

                // 校验参数范围
                if (timeout < 0) {
                    throw new IllegalArgumentException("timeout 不能为负数");
                }

                // 校验参数逻辑关系
                if ("GET".equalsIgnoreCase(method) && body != null) {
                    throw new IllegalStateException("GET 请求不能有 body");
                }

                // 规范化参数
                this.method = method.toUpperCase();

                return new HttpRequest(this);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 正例：使用 Builder 模式 ===\n");

        // 示例1：构建完整的 HTTP 请求
        System.out.println("--- 示例1：构建完整的 POST 请求 ---");
        HttpRequest request1 = new HttpRequest.Builder()
            .method("POST")
            .url("https://api.example.com/users")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer token123")
            .body("{\"name\":\"Alice\",\"age\":25}")
            .timeout(3000)
            .followRedirects(true)
            .build();

        System.out.println(request1);
        System.out.println();

        // 示例2：只设置必需参数，其他使用默认值
        System.out.println("--- 示例2：构建简单的 GET 请求（只设置必需参数）---");
        HttpRequest request2 = new HttpRequest.Builder()
            .method("GET")
            .url("https://api.example.com/users/123")
            .build();

        System.out.println(request2);
        System.out.println("✓ 未设置的参数自动使用默认值");
        System.out.println();

        // 示例3：灵活设置部分可选参数
        System.out.println("--- 示例3：灵活设置部分参数 ---");
        HttpRequest request3 = new HttpRequest.Builder()
            .method("GET")
            .url("https://api.example.com/search")
            .timeout(10000)  // 只想设置 timeout
            .charset("GBK")   // 和 charset
            .build();

        System.out.println(request3);
        System.out.println("✓ 可以灵活设置任意可选参数组合");
        System.out.println();

        // 示例4：参数校验
        System.out.println("--- 示例4：参数校验（会抛出异常）---");
        try {
            HttpRequest request4 = new HttpRequest.Builder()
                .method("GET")
                .url("https://api.example.com/users")
                .body("{\"data\":\"test\"}")  // GET 请求不能有 body
                .build();
        } catch (IllegalStateException e) {
            System.out.println("✓ 参数校验生效：" + e.getMessage());
        }
        System.out.println();

        // 示例5：链式调用的可读性
        System.out.println("--- 示例5：链式调用的可读性 ---");
        System.out.println("对比代码：");
        System.out.println("// 不使用 Builder（可读性差）");
        System.out.println("new HttpRequest(\"POST\", \"url\", \"json\", \"token\", \"body\", 3000, true, \"UTF-8\")");
        System.out.println();
        System.out.println("// 使用 Builder（可读性强）");
        System.out.println("new HttpRequest.Builder()");
        System.out.println("    .method(\"POST\")");
        System.out.println("    .url(\"url\")");
        System.out.println("    .addHeader(\"Content-Type\", \"json\")");
        System.out.println("    .addHeader(\"Authorization\", \"token\")");
        System.out.println("    .body(\"body\")");
        System.out.println("    .timeout(3000)");
        System.out.println("    .build()");
        System.out.println();

        System.out.println("=== 总结：使用 Builder 的优点 ===");
        System.out.println("✓ 链式调用，可读性强");
        System.out.println("✓ 参数名称明确，不会传错");
        System.out.println("✓ 灵活设置可选参数");
        System.out.println("✓ 支持不可变对象（线程安全）");
        System.out.println("✓ 参数校验集中在 build() 方法");
        System.out.println("✓ 易于维护和扩展");
    }
}
