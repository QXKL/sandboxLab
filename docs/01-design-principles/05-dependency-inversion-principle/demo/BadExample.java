/**
 * 违反依赖倒置原则的示例
 *
 * 问题：高层模块（OrderService）直接依赖低层模块的具体实现
 */

class Order {
    private String id;
    private String product;
    private double amount;

    public Order(String id, String product, double amount) {
        this.id = id;
        this.product = product;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Order{id='" + id + "', product='" + product + "', amount=" + amount + "}";
    }
}

// ========== 低层模块：具体实现 ==========

/**
 * MySQLDatabase - MySQL数据库（具体实现）
 */
class MySQLDatabase {
    public void save(Order order) {
        System.out.println("  [MySQL] 保存订单: " + order);
    }
}

/**
 * EmailService - 邮件服务（具体实现）
 */
class EmailService {
    public void send(String message) {
        System.out.println("  [Email] 发送邮件: " + message);
    }
}

// ========== 高层模块：业务逻辑 ==========

/**
 * OrderService - 订单服务
 *
 * ❌ 问题：直接依赖具体实现类
 * - 依赖 MySQLDatabase（具体类）
 * - 依赖 EmailService（具体类）
 * - 硬编码创建依赖（new）
 */
class OrderService {
    private MySQLDatabase database;      // ❌ 依赖具体实现
    private EmailService emailService;   // ❌ 依赖具体实现

    public OrderService() {
        this.database = new MySQLDatabase();      // ❌ 硬编码依赖
        this.emailService = new EmailService();   // ❌ 硬编码依赖
    }

    public void processOrder(Order order) {
        System.out.println("处理订单: " + order.toString());
        database.save(order);
        emailService.send("订单 " + order + " 已创建");
        System.out.println("订单处理完成\n");
    }
}

/**
 * 运行示例
 */
public class BadExample {
    public static void main(String[] args) {
        System.out.println("=== 违反依赖倒置原则的示例 ===\n");

        OrderService service = new OrderService();

        Order order1 = new Order("001", "笔记本电脑", 5999.00);
        service.processOrder(order1);

        Order order2 = new Order("002", "机械键盘", 599.00);
        service.processOrder(order2);

        // 问题演示
        System.out.println("=".repeat(60));
        System.out.println("💥 问题分析");
        System.out.println("=".repeat(60));

        System.out.println("\n【问题1】紧耦合");
        System.out.println("OrderService 直接依赖 MySQLDatabase 和 EmailService");
        System.out.println("无法更换为 PostgreSQL 或 SMS 通知");

        System.out.println("\n【问题2】难以测试");
        System.out.println("单元测试时无法用 Mock 对象替换真实的数据库和邮件服务");
        System.out.println("必须连接真实的 MySQL 和邮件服务器");

        System.out.println("\n【问题3】违反开闭原则");
        System.out.println("要支持新的存储方式（如MongoDB），必须修改 OrderService");
        System.out.println("要支持新的通知方式（如SMS），也必须修改 OrderService");

        System.out.println("\n【问题4】依赖方向错误");
        System.out.println("高层模块（OrderService）依赖低层模块（MySQL、Email）");
        System.out.println("应该是：高层和低层都依赖抽象");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("核心问题：依赖了具体实现，而非抽象");
        System.out.println("=".repeat(60));
        System.out.println("✗ 高层直接依赖低层");
        System.out.println("✗ 硬编码创建依赖（new）");
        System.out.println("✗ 无法替换实现");

        System.out.println("\n➡️  运行 GoodExample.java 查看如何解决");
    }
}
