/**
 * API设计对比：坏的设计 vs 好的设计
 *
 * 通过对比，快速建立"什么是RESTful API"的直觉
 */
public class ApiDesignComparison {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("API设计对比：坏的设计 vs 好的设计");
        System.out.println("=".repeat(80));
        System.out.println();

        section1_naming();
        section2_httpMethods();
        section3_statusCodes();
        section4_resourceHierarchy();
        section5_errorHandling();
    }

    /**
     * 1. 资源命名对比
     */
    private static void section1_naming() {
        printSection("1. 资源命名");

        printBad("使用动词命名");
        printApi("POST /getUserInfo?id=1");
        printApi("POST /createUser");
        printApi("POST /updateUser?id=1");
        printApi("POST /deleteUser?id=1");
        printProblem("- 全用POST，无法区分操作类型");
        printProblem("- 动词冗余（HTTP方法本身就是动词）");
        printProblem("- 不符合资源思维");

        System.out.println();

        printGood("使用名词，HTTP方法表示操作");
        printApi("GET    /users/1        # 获取用户");
        printApi("POST   /users          # 创建用户");
        printApi("PUT    /users/1        # 更新用户");
        printApi("DELETE /users/1        # 删除用户");
        printBenefit("✓ 清晰的资源标识");
        printBenefit("✓ HTTP方法语义化");
        printBenefit("✓ 符合REST规范");

        System.out.println();
    }

    /**
     * 2. HTTP方法使用对比
     */
    private static void section2_httpMethods() {
        printSection("2. HTTP方法使用");

        printBad("方法混乱");
        printApi("GET /deleteUser?id=1        # 用GET做删除（危险！）");
        printApi("POST /getUser               # 用POST做查询");
        printApi("GET /createUser?name=张三   # 用GET做创建");
        printProblem("- 搜索引擎爬虫会触发删除操作");
        printProblem("- 无法利用HTTP缓存");
        printProblem("- 违反幂等性原则");

        System.out.println();

        printGood("方法语义正确");
        printApi("DELETE /users/1             # DELETE做删除（幂等）");
        printApi("GET    /users/1             # GET做查询（安全、幂等）");
        printApi("POST   /users               # POST做创建（非幂等）");
        printBenefit("✓ 符合HTTP语义");
        printBenefit("✓ 可以安全缓存GET请求");
        printBenefit("✓ 幂等操作可以安全重试");

        System.out.println();
    }

    /**
     * 3. 状态码使用对比
     */
    private static void section3_statusCodes() {
        printSection("3. HTTP状态码使用");

        printBad("所有情况都返回200");
        printApi("GET /users/999");
        printResponse("HTTP/1.1 200 OK");
        printResponse("{\"success\": false, \"error\": \"用户不存在\"}");
        System.out.println();
        printApi("POST /users (邮箱已存在)");
        printResponse("HTTP/1.1 200 OK");
        printResponse("{\"success\": false, \"error\": \"邮箱已被注册\"}");
        printProblem("- 监控系统无法识别错误");
        printProblem("- 客户端需要解析响应体才知道成功与否");
        printProblem("- 违反HTTP标准");

        System.out.println();

        printGood("使用正确的状态码");
        printApi("GET /users/999");
        printResponse("HTTP/1.1 404 Not Found");
        printResponse("{\"code\": 40400, \"message\": \"用户不存在\"}");
        System.out.println();
        printApi("POST /users (邮箱已存在)");
        printResponse("HTTP/1.1 409 Conflict");
        printResponse("{\"code\": 40900, \"message\": \"邮箱已被注册\"}");
        printBenefit("✓ 状态码直接表达结果");
        printBenefit("✓ 易于监控和日志分析");
        printBenefit("✓ 客户端可以根据状态码做决策");

        System.out.println();
    }

    /**
     * 4. 资源层级对比
     */
    private static void section4_resourceHierarchy() {
        printSection("4. 资源层级关系");

        printBad("扁平结构，关系不清晰");
        printApi("GET /getUserOrders?userId=1");
        printApi("GET /getOrderItems?orderId=100");
        printApi("POST /addCommentToPost?postId=5");
        printProblem("- 资源关系不直观");
        printProblem("- 命名不一致");

        System.out.println();

        printGood("嵌套资源，关系清晰");
        printApi("GET  /users/1/orders           # 用户1的所有订单");
        printApi("GET  /orders/100/items         # 订单100的所有商品");
        printApi("POST /posts/5/comments         # 为文章5添加评论");
        printBenefit("✓ 资源关系一目了然");
        printBenefit("✓ URL即文档");
        printBenefit("✓ 符合REST规范");

        System.out.println();

        printNote("注意：避免过深嵌套");
        printApi("❌ /users/1/orders/100/items/5/reviews/3  # 太深了");
        printApi("✅ /order-items/5/reviews/3               # 简化");
        printApi("✅ /reviews/3                             # 独立访问");

        System.out.println();
    }

    /**
     * 5. 错误处理对比
     */
    private static void section5_errorHandling() {
        printSection("5. 错误处理");

        printBad("错误信息不规范");
        printApi("POST /users");
        printResponse("HTTP/1.1 500 Internal Server Error");
        printResponse("\"error\"");
        printProblem("- 错误信息太简略，无法排查问题");
        printProblem("- 没有错误码，无法做国际化");
        printProblem("- 没有请求追踪ID");

        System.out.println();

        printGood("结构化错误响应");
        printApi("POST /users");
        printResponse("HTTP/1.1 422 Unprocessable Entity");
        printResponse("{");
        printResponse("  \"code\": 42200,");
        printResponse("  \"message\": \"邮箱格式错误\",");
        printResponse("  \"details\": {");
        printResponse("    \"field\": \"email\",");
        printResponse("    \"value\": \"invalid-email\",");
        printResponse("    \"constraint\": \"必须是有效的邮箱地址\"");
        printResponse("  },");
        printResponse("  \"requestId\": \"abc-123-def\",");
        printResponse("  \"timestamp\": \"2026-07-16T10:30:00Z\"");
        printResponse("}");
        printBenefit("✓ 结构化错误信息");
        printBenefit("✓ 包含错误码（业务错误码）");
        printBenefit("✓ 详细的调试信息");
        printBenefit("✓ 请求ID便于追踪");

        System.out.println();
    }

    // ==================== 辅助方法 ====================

    private static void printSection(String title) {
        System.out.println("━".repeat(80));
        System.out.println(title);
        System.out.println("━".repeat(80));
        System.out.println();
    }

    private static void printBad(String title) {
        System.out.println("❌ 坏的设计：" + title);
        System.out.println();
    }

    private static void printGood(String title) {
        System.out.println("✅ 好的设计：" + title);
        System.out.println();
    }

    private static void printApi(String text) {
        System.out.println("  " + text);
    }

    private static void printResponse(String text) {
        System.out.println("  " + text);
    }

    private static void printProblem(String text) {
        System.out.println("  " + text);
    }

    private static void printBenefit(String text) {
        System.out.println("  " + text);
    }

    private static void printNote(String text) {
        System.out.println("💡 " + text);
    }
}
