import java.util.ArrayList;
import java.util.List;

/**
 * 符合开闭原则的示例
 *
 * 核心思想：通过抽象接口和多态实现"对扩展开放，对修改关闭"
 */

// ========== 抽象层 ==========
/**
 * Notifier 接口 - 定义通知行为的抽象
 *
 * 作用：
 * 1. 定义"通知"这个行为的契约
 * 2. 让客户端依赖抽象而非具体实现
 * 3. 为扩展提供统一的接口
 */
interface Notifier {
    /**
     * 发送通知
     * @param message 通知内容
     */
    void send(String message);

    /**
     * 获取通知类型名称
     * @return 类型名称（用于日志）
     */
    String getType();
}

// ========== 具体实现层 ==========
/**
 * EmailNotifier - 邮件通知
 */
class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("  [Email] 发送邮件: " + message);
        System.out.println("  → 连接邮件服务器...");
        System.out.println("  → 邮件已发送");
    }

    @Override
    public String getType() {
        return "Email";
    }
}

/**
 * SmsNotifier - 短信通知
 */
class SmsNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("  [SMS] 发送短信: " + message);
        System.out.println("  → 连接短信网关...");
        System.out.println("  → 短信已发送");
    }

    @Override
    public String getType() {
        return "SMS";
    }
}

/**
 * PushNotifier - 推送通知
 */
class PushNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("  [Push] 发送推送: " + message);
        System.out.println("  → 连接推送服务...");
        System.out.println("  → 推送已发送");
    }

    @Override
    public String getType() {
        return "Push";
    }
}

/**
 * WeChatNotifier - 微信通知（新增功能）
 *
 * 重点：新增这个类时，不需要修改任何现有代码！
 * - 不修改 Notifier 接口
 * - 不修改 NotificationService
 * - 不修改其他 Notifier 实现类
 *
 * 这就是"对扩展开放，对修改关闭"
 */
class WeChatNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("  [WeChat] 发送微信通知: " + message);
        System.out.println("  → 连接微信接口...");
        System.out.println("  → 微信通知已发送");
    }

    @Override
    public String getType() {
        return "WeChat";
    }
}

// ========== 服务层 ==========
/**
 * NotificationService - 通知服务
 *
 * 关键设计：
 * 1. 依赖 Notifier 接口，不依赖具体实现
 * 2. 通过多态调用，无需 if-else 判断
 * 3. 新增通知方式时，这个类不需要修改
 */
class NotificationService {
    private List<Notifier> notifiers;

    public NotificationService() {
        this.notifiers = new ArrayList<>();
    }

    /**
     * 注册通知器
     *
     * 设计亮点：通过"注册"机制实现扩展
     * - 新增通知方式只需注册新的 Notifier
     * - 可以在运行时动态添加
     * - 支持插件化架构
     */
    public void registerNotifier(Notifier notifier) {
        notifiers.add(notifier);
        System.out.println("✓ 已注册通知器: " + notifier.getType());
    }

    /**
     * 发送给所有已注册的通知器
     *
     * 关键：使用多态，不需要 if-else 判断类型
     */
    public void sendToAll(String message) {
        System.out.println("\n=== 发送通知给所有渠道 ===");
        for (Notifier notifier : notifiers) {
            notifier.send(message);  // 多态调用
            System.out.println();
        }
    }

    /**
     * 发送给指定通知器
     */
    public void send(Notifier notifier, String message) {
        notifier.send(message);
    }
}

// ========== 运行示例 ==========
public class GoodExample {
    public static void main(String[] args) {
        System.out.println("=== 符合开闭原则的通知系统 ===\n");

        // 创建通知服务
        NotificationService service = new NotificationService();

        // 注册各种通知器（可以在运行时动态配置）
        System.out.println("【初始化】注册通知器:");
        service.registerNotifier(new EmailNotifier());
        service.registerNotifier(new SmsNotifier());
        service.registerNotifier(new PushNotifier());

        // 发送通知
        service.sendToAll("您的订单已发货");

        System.out.println("=".repeat(60));
        System.out.println("✅ 扩展演示：新增微信通知");
        System.out.println("=".repeat(60));

        // 新增微信通知：只需创建新类并注册，不修改现有代码
        service.registerNotifier(new WeChatNotifier());
        service.sendToAll("系统维护通知");

        // 优势展示
        System.out.println("=".repeat(60));
        System.out.println("✅ 符合开闭原则的优势");
        System.out.println("=".repeat(60));

        System.out.println("\n【扩展性】");
        System.out.println("✓ 新增通知方式：只需添加新的 Notifier 实现类");
        System.out.println("✓ 不修改现有代码：NotificationService 无需改动");
        System.out.println("✓ 不影响现有功能：其他 Notifier 完全不受影响");

        System.out.println("\n【可维护性】");
        System.out.println("✓ 每个 Notifier 都很小，职责单一");
        System.out.println("✓ 修复 bug 只需改对应的 Notifier");
        System.out.println("✓ 测试简单：每个 Notifier 独立测试");

        System.out.println("\n【灵活性】");
        System.out.println("✓ 运行时注册：可以通过配置文件动态加载");
        System.out.println("✓ 插件化：支持第三方插件");
        System.out.println("✓ 组合灵活：可以只注册需要的通知器");

        System.out.println("\n【对比 BadExample】");
        System.out.println("BadExample: 每次新增通知方式，都要修改 send() 方法");
        System.out.println("GoodExample: 只需添加新的 Notifier 类，注册即可使用");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 核心原则：对扩展开放，对修改关闭");
        System.out.println("=".repeat(60));
        System.out.println("✓ 对扩展开放：可以添加新的 Notifier 实现");
        System.out.println("✓ 对修改关闭：不需要修改现有的稳定代码");
        System.out.println("✓ 依赖抽象：Service 依赖 Notifier 接口，不依赖具体实现");
        System.out.println("✓ 多态实现：通过接口调用，无需 if-else 判断");

        System.out.println("\n💡 思考：");
        System.out.println("1. 如果要新增钉钉通知，需要改几行代码？");
        System.out.println("   答：只需创建 DingTalkNotifier 类（约10行），注册即可");
        System.out.println("2. 如果 EmailNotifier 有 bug，会影响其他通知器吗？");
        System.out.println("   答：不会，每个 Notifier 都是独立的");
        System.out.println("3. 能否在运行时根据配置动态加载通知器？");
        System.out.println("   答：可以，通过反射或配置文件实现插件化");
    }
}
