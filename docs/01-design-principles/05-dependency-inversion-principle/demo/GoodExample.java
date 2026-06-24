/**
 * 符合依赖倒置原则的示例
 *
 * 解决方案：高层和低层都依赖抽象接口，通过依赖注入解耦
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

// ========== 抽象层：接口定义（由高层需求驱动）==========

/**
 * Repository - 数据存储接口（抽象）
 */
interface Repository {
    void save(Order order);
}

/**
 * NotificationService - 通知服务接口（抽象）
 */
interface NotificationService {
    void notify(String message);
}

// ========== 低层模块：具体实现（依赖抽象）==========

/**
 * MySQLRepository - MySQL实现
 */
class MySQLRepository implements Repository {
    @Override
    public void save(Order order) {
        System.out.println("  [MySQL] 保存订单: " + order);
    }
}

/**
 * PostgreSQLRepository - PostgreSQL实现（演示可替换性）
 */
class PostgreSQLRepository implements Repository {
    @Override
    public void save(Order order) {
        System.out.println("  [PostgreSQL] 保存订单: " + order);
    }
}

/**
 * EmailNotifier - 邮件通知实现
 */
class EmailNotifier implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("  [Email] 发送邮件: " + message);
    }
}

/**
 * SmsNotifier - 短信通知实现（演示可替换性）
 */
class SmsNotifier implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("  [SMS] 发送短信: " + message);
    }
}

// ========== 高层模块：业务逻辑（依赖抽象）==========

/**
 * OrderService - 订单服务
 *
 * ✅ 改进：依赖抽象接口，通过构造函数注入
 */
class OrderService {
    private Repository repository;                    // ✓ 依赖抽象
    private NotificationService notificationService;  // ✓ 依赖抽象

    /**
     * 构造函数注入（依赖注入）
     */
    public OrderService(Repository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public void processOrder(Order order) {
        System.out.println("处理订单: " + order.toString());
        repository.save(order);
        notificationService.notify("订单 " + order + " 已创建");
        System.out.println("订单处理完成\n");
    }
}

/**
 * 运行示例
 */
public class GoodExample {
    public static void main(String[] args) {
        System.out.println("=== 符合依赖倒置原则的示例 ===\n");

        // 测试1：MySQL + Email（原始配置）
        System.out.println("【配置1】MySQL数据库 + 邮件通知:");
        Repository mysqlRepo = new MySQLRepository();
        NotificationService emailNotifier = new EmailNotifier();
        OrderService service1 = new OrderService(mysqlRepo, emailNotifier);

        Order order1 = new Order("001", "笔记本电脑", 5999.00);
        service1.processOrder(order1);

        // 测试2：PostgreSQL + SMS（轻松切换实现）
        System.out.println("【配置2】PostgreSQL数据库 + 短信通知:");
        Repository postgresRepo = new PostgreSQLRepository();
        NotificationService smsNotifier = new SmsNotifier();
        OrderService service2 = new OrderService(postgresRepo, smsNotifier);

        Order order2 = new Order("002", "机械键盘", 599.00);
        service2.processOrder(order2);

        // 测试3：混合配置
        System.out.println("【配置3】MySQL数据库 + 短信通知:");
        OrderService service3 = new OrderService(mysqlRepo, smsNotifier);

        Order order3 = new Order("003", "显示器", 1999.00);
        service3.processOrder(order3);

        // 优势总结
        System.out.println("=".repeat(60));
        System.out.println("✅ 符合依赖倒置原则的优势");
        System.out.println("=".repeat(60));

        System.out.println("\n【依赖方向倒置】");
        System.out.println("✓ OrderService 依赖抽象接口（Repository、NotificationService）");
        System.out.println("✓ 具体实现类也依赖抽象接口");
        System.out.println("✓ 高层和低层都依赖抽象，而非相互依赖");

        System.out.println("\n【低耦合】");
        System.out.println("✓ OrderService 不知道具体使用的是 MySQL 还是 PostgreSQL");
        System.out.println("✓ OrderService 不知道具体使用的是 Email 还是 SMS");
        System.out.println("✓ 高层和低层完全解耦");

        System.out.println("\n【灵活性】");
        System.out.println("✓ 轻松切换实现（MySQL → PostgreSQL）");
        System.out.println("✓ 不需要修改 OrderService 代码");
        System.out.println("✓ 可以在运行时动态配置");

        System.out.println("\n【可测试性】");
        System.out.println("✓ 可以注入 Mock 对象进行单元测试");
        System.out.println("✓ 不需要真实的数据库和邮件服务");
        System.out.println("✓ 测试更快、更可靠");

        System.out.println("\n【符合其他原则】");
        System.out.println("✓ 符合开闭原则：新增实现无需修改高层");
        System.out.println("✓ 符合单一职责：OrderService 只关注业务逻辑");
        System.out.println("✓ 符合里氏替换：所有实现都能正确替换");

        // 对比 BadExample
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【对比 BadExample】");
        System.out.println("=".repeat(60));

        System.out.println("\nBadExample（违反DIP）:");
        System.out.println("  ✗ OrderService 直接依赖 MySQLDatabase");
        System.out.println("  ✗ 硬编码创建依赖（new MySQLDatabase()）");
        System.out.println("  ✗ 无法更换实现");
        System.out.println("  ✗ 难以测试");

        System.out.println("\nGoodExample（符合DIP）:");
        System.out.println("  ✓ OrderService 依赖抽象接口");
        System.out.println("  ✓ 通过依赖注入传入实现");
        System.out.println("  ✓ 轻松更换实现");
        System.out.println("  ✓ 易于测试");

        System.out.println("\n🎯 核心原则：高层低层不直连，都靠抽象来牵线！");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 恭喜！你已经学完了 SOLID 五大原则！");
        System.out.println("=".repeat(60));
        System.out.println("S - Single Responsibility（单一职责）");
        System.out.println("O - Open-Closed（开闭原则）");
        System.out.println("L - Liskov Substitution（里氏替换）");
        System.out.println("I - Interface Segregation（接口隔离）");
        System.out.println("D - Dependency Inversion（依赖倒置）");
    }
}
