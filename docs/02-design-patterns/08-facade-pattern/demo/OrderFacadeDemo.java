/**
 * 外观模式 - 订单处理示例
 *
 * 场景：电商订单系统，协调库存、支付、物流、积分等子系统
 * 演示：外观协调多个子系统、业务编排
 */

// ========== 子系统 ==========

/**
 * 子系统1：库存系统
 */
class InventorySystem {
    public boolean checkStock(String productId, int quantity) {
        System.out.println("  📦 [库存系统] 检查库存: " + productId + " x " + quantity);
        // 模拟库存检查
        boolean available = quantity <= 100;
        System.out.println("  📦 [库存系统] 库存" + (available ? "充足" : "不足"));
        return available;
    }

    public void lockStock(String productId, int quantity) {
        System.out.println("  📦 [库存系统] 锁定库存: " + productId + " x " + quantity);
    }

    public void reduceStock(String productId, int quantity) {
        System.out.println("  📦 [库存系统] 扣减库存: " + productId + " x " + quantity);
    }
}

/**
 * 子系统2：支付系统
 */
class PaymentSystem {
    public boolean processPayment(String orderId, double amount) {
        System.out.println("  💰 [支付系统] 处理支付: 订单" + orderId + ", 金额" + amount + "元");
        // 模拟支付处理
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("  💰 [支付系统] 支付成功");
        return true;
    }
}

/**
 * 子系统3：物流系统
 */
class ShippingSystem {
    public String createShipment(String orderId, String address) {
        System.out.println("  🚚 [物流系统] 创建物流单: 订单" + orderId);
        System.out.println("  🚚 [物流系统] 配送地址: " + address);
        String trackingNumber = "SF" + System.currentTimeMillis();
        System.out.println("  🚚 [物流系统] 物流单号: " + trackingNumber);
        return trackingNumber;
    }
}

/**
 * 子系统4：积分系统
 */
class PointsSystem {
    public void addPoints(String userId, int points) {
        System.out.println("  ⭐ [积分系统] 用户" + userId + "获得积分: " + points);
    }
}

/**
 * 子系统5：通知系统
 */
class NotificationSystem {
    public void sendEmail(String email, String subject, String content) {
        System.out.println("  📧 [通知系统] 发送邮件到: " + email);
        System.out.println("  📧 [通知系统] 主题: " + subject);
    }

    public void sendSMS(String phone, String message) {
        System.out.println("  📱 [通知系统] 发送短信到: " + phone);
    }
}

// ========== 外观 ==========

/**
 * 外观：订单处理外观
 */
class OrderFacade {
    // 持有所有子系统
    private InventorySystem inventory;
    private PaymentSystem payment;
    private ShippingSystem shipping;
    private PointsSystem points;
    private NotificationSystem notification;

    public OrderFacade() {
        this.inventory = new InventorySystem();
        this.payment = new PaymentSystem();
        this.shipping = new ShippingSystem();
        this.points = new PointsSystem();
        this.notification = new NotificationSystem();
    }

    /**
     * 高层接口：下单
     */
    public boolean placeOrder(String orderId, String userId, String productId,
                             int quantity, double amount, String address,
                             String email, String phone) {
        System.out.println("\n🛒 开始处理订单: " + orderId + "\n");

        try {
            // 1. 检查库存
            if (!inventory.checkStock(productId, quantity)) {
                System.out.println("\n❌ 订单失败: 库存不足\n");
                return false;
            }

            // 2. 锁定库存
            inventory.lockStock(productId, quantity);

            // 3. 处理支付
            if (!payment.processPayment(orderId, amount)) {
                System.out.println("\n❌ 订单失败: 支付失败\n");
                return false;
            }

            // 4. 扣减库存
            inventory.reduceStock(productId, quantity);

            // 5. 创建物流
            String trackingNumber = shipping.createShipment(orderId, address);

            // 6. 增加积分
            int earnedPoints = (int) (amount / 10);
            points.addPoints(userId, earnedPoints);

            // 7. 发送通知
            notification.sendEmail(email, "订单确认",
                                 "您的订单" + orderId + "已确认，物流单号:" + trackingNumber);
            notification.sendSMS(phone, "订单已发货，物流单号:" + trackingNumber);

            System.out.println("\n✅ 订单处理成功\n");
            return true;

        } catch (Exception e) {
            System.out.println("\n❌ 订单失败: " + e.getMessage() + "\n");
            return false;
        }
    }

    /**
     * 高层接口：取消订单
     */
    public void cancelOrder(String orderId, String productId, int quantity) {
        System.out.println("\n🚫 取消订单: " + orderId + "\n");
        // 协调多个子系统处理取消逻辑
        inventory.reduceStock(productId, -quantity);  // 恢复库存
        System.out.println("\n✅ 订单已取消\n");
    }
}

// ========== 测试 ==========

public class OrderFacadeDemo {
    public static void main(String[] args) {
        System.out.println("=== 外观模式 - 订单处理系统 ===\n");

        // 示例1：不使用外观（复杂）
        System.out.println("【1. 不使用外观（客户端直接调用子系统）】");
        demonstrateWithoutFacade();

        // 示例2：使用外观（简单）
        System.out.println("\n【2. 使用外观（简化调用）】");
        demonstrateWithFacade();

        // 示例3：外观的业务编排
        System.out.println("\n【3. 外观的业务编排能力】");
        demonstrateOrchestration();
    }

    /**
     * 不使用外观（复杂）
     */
    static void demonstrateWithoutFacade() {
        System.out.println("客户端需要手动协调所有子系统:\n");

        InventorySystem inventory = new InventorySystem();
        PaymentSystem payment = new PaymentSystem();
        ShippingSystem shipping = new ShippingSystem();
        PointsSystem points = new PointsSystem();
        NotificationSystem notification = new NotificationSystem();

        String orderId = "ORD001";
        String productId = "P001";
        int quantity = 2;
        double amount = 299.0;

        // 客户端需要写大量代码协调
        if (inventory.checkStock(productId, quantity)) {
            inventory.lockStock(productId, quantity);
            if (payment.processPayment(orderId, amount)) {
                inventory.reduceStock(productId, quantity);
                shipping.createShipment(orderId, "北京市朝阳区");
                points.addPoints("U001", 29);
                notification.sendEmail("user@example.com", "订单确认", "...");
            }
        }

        System.out.println("\n❌ 问题:");
        System.out.println("  - 客户端需要了解所有子系统");
        System.out.println("  - 业务逻辑分散在客户端");
        System.out.println("  - 容易遗漏步骤或顺序错误");
        System.out.println("  - 难以维护和测试");
    }

    /**
     * 使用外观（简单）
     */
    static void demonstrateWithFacade() {
        OrderFacade orderFacade = new OrderFacade();

        // 一行代码搞定复杂的订单处理
        boolean success = orderFacade.placeOrder(
            "ORD002",              // 订单号
            "U002",                // 用户ID
            "P002",                // 商品ID
            1,                     // 数量
            599.0,                 // 金额
            "上海市浦东新区",      // 地址
            "user@example.com",    // 邮箱
            "13800138000"          // 手机
        );

        System.out.println("✅ 优势:");
        System.out.println("  - 简单（1行调用）");
        System.out.println("  - 业务逻辑集中在外观");
        System.out.println("  - 不易出错");
        System.out.println("  - 易于测试和维护");
    }

    /**
     * 演示外观的业务编排能力
     */
    static void demonstrateOrchestration() {
        OrderFacade orderFacade = new OrderFacade();

        System.out.println("\n外观协调多个子系统，完成复杂业务流程:\n");
        System.out.println("订单处理流程:");
        System.out.println("  1. 库存系统: 检查库存");
        System.out.println("  2. 库存系统: 锁定库存");
        System.out.println("  3. 支付系统: 处理支付");
        System.out.println("  4. 库存系统: 扣减库存");
        System.out.println("  5. 物流系统: 创建物流单");
        System.out.println("  6. 积分系统: 增加积分");
        System.out.println("  7. 通知系统: 发送邮件和短信");

        System.out.println("\n✅ 外观的价值:");
        System.out.println("  - 封装业务流程");
        System.out.println("  - 协调多个子系统");
        System.out.println("  - 保证操作顺序");
        System.out.println("  - 统一异常处理");
        System.out.println("  - 易于扩展和修改");

        System.out.println("\n🎯 这就是Service层的本质:");
        System.out.println("  - Service层是外观模式");
        System.out.println("  - 协调多个DAO层");
        System.out.println("  - 封装业务逻辑");
    }
}
