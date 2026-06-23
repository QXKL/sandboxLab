/**
 * 违反开闭原则的示例
 *
 * 问题：每次新增通知方式，都要修改 NotificationService 的 send 方法
 * 这违反了"对修改关闭"的原则
 */

class NotificationServiceBad {
    /**
     * 发送通知
     *
     * 问题：使用 if-else 判断通知类型
     * - 每次新增通知方式，都要在这里加新的 else-if 分支
     * - 代码会越来越长，越来越难维护
     * - 修改这个方法可能影响所有通知方式
     */
    public void send(String message, String type) {
        if (type.equals("email")) {
            // 邮件通知逻辑
            System.out.println("  [Email] 发送邮件: " + message);
            System.out.println("  → 连接邮件服务器...");
            System.out.println("  → 邮件已发送");

        } else if (type.equals("sms")) {
            // 短信通知逻辑
            System.out.println("  [SMS] 发送短信: " + message);
            System.out.println("  → 连接短信网关...");
            System.out.println("  → 短信已发送");

        } else if (type.equals("push")) {
            // 推送通知逻辑
            System.out.println("  [Push] 发送推送: " + message);
            System.out.println("  → 连接推送服务...");
            System.out.println("  → 推送已发送");

        } else {
            System.out.println("  [Error] 不支持的通知类型: " + type);
        }
    }

    /**
     * 批量发送通知
     *
     * 问题：如果要新增通知类型，这个方法也要修改
     */
    public void sendToAll(String message, String[] types) {
        for (String type : types) {
            send(message, type);
        }
    }
}

/**
 * 运行示例
 */
public class BadExample {
    public static void main(String[] args) {
        System.out.println("=== 违反开闭原则的通知系统 ===\n");

        NotificationServiceBad service = new NotificationServiceBad();

        // 发送不同类型的通知
        System.out.println("1. 发送邮件通知:");
        service.send("您的订单已发货", "email");

        System.out.println("\n2. 发送短信通知:");
        service.send("验证码: 123456", "sms");

        System.out.println("\n3. 发送推送通知:");
        service.send("您有新消息", "push");

        System.out.println("\n4. 批量发送:");
        service.sendToAll("系统维护通知", new String[]{"email", "sms", "push"});

        // 演示问题
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💥 问题演示：");
        System.out.println("=".repeat(60));

        System.out.println("\n【场景1】产品要求新增微信通知");
        System.out.println("❌ 必须修改 NotificationServiceBad.send() 方法");
        System.out.println("❌ 增加新的 else-if 分支");
        System.out.println("❌ 可能引入 bug，影响现有通知方式");
        System.out.println("❌ 需要重新测试所有通知类型");

        System.out.println("\n【场景2】产品继续要求新增钉钉通知、Slack通知...");
        System.out.println("❌ send() 方法会有 10+ 个 if-else 分支");
        System.out.println("❌ 代码越来越臃肿，难以维护");
        System.out.println("❌ 违反了单一职责原则");

        System.out.println("\n【场景3】运行时动态加载通知插件");
        System.out.println("❌ 无法实现：所有逻辑都硬编码在 if-else 中");
        System.out.println("❌ 必须重新编译、部署");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("核心问题：违反了开闭原则");
        System.out.println("=".repeat(60));
        System.out.println("✗ 对修改开放：每次新增功能都要修改现有代码");
        System.out.println("✗ 对扩展关闭：无法通过扩展来添加新功能");

        System.out.println("\n➡️  运行 GoodExample.java 查看如何解决这些问题");
    }
}
