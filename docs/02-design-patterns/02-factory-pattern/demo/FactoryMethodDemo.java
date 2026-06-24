/**
 * 工厂方法模式 - 支付系统
 *
 * 场景：电商支付系统，支持多种支付方式
 * 演示：工厂方法如何符合开闭原则
 */

// ========== 抽象产品 ==========

interface Payment {
    /**
     * 执行支付
     * @param amount 支付金额
     * @return 是否成功
     */
    boolean pay(double amount);

    /**
     * 退款
     * @param amount 退款金额
     * @return 是否成功
     */
    boolean refund(double amount);
}

// ========== 具体产品 ==========

class AlipayPayment implements Payment {
    @Override
    public boolean pay(double amount) {
        System.out.println("[支付宝] 支付 ¥" + amount);
        // 模拟支付逻辑
        System.out.println("  → 调用支付宝SDK");
        System.out.println("  → 验证用户身份");
        System.out.println("  → 扣款成功");
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("[支付宝] 退款 ¥" + amount);
        return true;
    }
}

class WechatPayment implements Payment {
    @Override
    public boolean pay(double amount) {
        System.out.println("[微信支付] 支付 ¥" + amount);
        System.out.println("  → 调用微信支付API");
        System.out.println("  → 扫码验证");
        System.out.println("  → 扣款成功");
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("[微信支付] 退款 ¥" + amount);
        return true;
    }
}

class BankCardPayment implements Payment {
    @Override
    public boolean pay(double amount) {
        System.out.println("[银行卡] 支付 ¥" + amount);
        System.out.println("  → 连接银行网关");
        System.out.println("  → 输入密码验证");
        System.out.println("  → 扣款成功");
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("[银行卡] 退款 ¥" + amount);
        return true;
    }
}

// ========== 抽象工厂 ==========

abstract class PaymentFactory {
    /**
     * 工厂方法：由子类决定创建哪种支付方式
     */
    public abstract Payment createPayment();

    /**
     * 模板方法：统一的支付流程
     */
    public boolean processPayment(double amount) {
        System.out.println("\n开始支付流程...");

        // 1. 创建支付对象（由子类决定）
        Payment payment = createPayment();

        // 2. 验证金额
        if (amount <= 0) {
            System.out.println("❌ 金额无效");
            return false;
        }

        // 3. 执行支付
        boolean success = payment.pay(amount);

        // 4. 记录日志
        if (success) {
            System.out.println("✅ 支付成功！交易完成。\n");
        } else {
            System.out.println("❌ 支付失败\n");
        }

        return success;
    }
}

// ========== 具体工厂 ==========

class AlipayFactory extends PaymentFactory {
    @Override
    public Payment createPayment() {
        System.out.println("  → 选择支付方式: 支付宝");
        return new AlipayPayment();
    }
}

class WechatFactory extends PaymentFactory {
    @Override
    public Payment createPayment() {
        System.out.println("  → 选择支付方式: 微信支付");
        return new WechatPayment();
    }
}

class BankCardFactory extends PaymentFactory {
    @Override
    public Payment createPayment() {
        System.out.println("  → 选择支付方式: 银行卡");
        return new BankCardPayment();
    }
}

// ========== 客户端 ==========

public class FactoryMethodDemo {
    public static void main(String[] args) {
        System.out.println("=== 工厂方法模式示例 ===\n");

        // 示例1：使用不同的支付方式
        System.out.println("【1. 不同支付方式】");
        testDifferentPayments();

        // 示例2：扩展新支付方式
        System.out.println("\n【2. 扩展性演示】");
        testExtension();

        // 示例3：工厂方法 vs 简单工厂
        System.out.println("\n【3. 对比：工厂方法 vs 简单工厂】");
        comparison();
    }

    /**
     * 测试不同支付方式
     */
    static void testDifferentPayments() {
        // 使用支付宝
        PaymentFactory factory1 = new AlipayFactory();
        factory1.processPayment(99.99);

        // 使用微信支付
        PaymentFactory factory2 = new WechatFactory();
        factory2.processPayment(199.00);

        // 使用银行卡
        PaymentFactory factory3 = new BankCardFactory();
        factory3.processPayment(299.50);
    }

    /**
     * 扩展性演示：新增支付方式无需修改现有代码
     */
    static void testExtension() {
        System.out.println("新增PayPal支付（无需修改现有代码）：\n");

        // 新增的PayPal支付
        class PayPalPayment implements Payment {
            public boolean pay(double amount) {
                System.out.println("[PayPal] 支付 $" + amount);
                System.out.println("  → 调用PayPal API");
                System.out.println("  → 扣款成功");
                return true;
            }
            public boolean refund(double amount) {
                return true;
            }
        }

        class PayPalFactory extends PaymentFactory {
            public Payment createPayment() {
                System.out.println("  → 选择支付方式: PayPal");
                return new PayPalPayment();
            }
        }

        // 使用新支付方式（其他代码无需改动）
        PaymentFactory paypalFactory = new PayPalFactory();
        paypalFactory.processPayment(50.00);

        System.out.println("✅ 符合开闭原则：对扩展开放，对修改封闭");
    }

    /**
     * 对比工厂方法和简单工厂
     */
    static void comparison() {
        System.out.println("简单工厂的问题：");
        System.out.println("  ❌ 新增支付方式需要修改工厂类");
        System.out.println("  ❌ 工厂类职责过重");
        System.out.println("  ❌ 违反开闭原则\n");

        System.out.println("工厂方法的优势：");
        System.out.println("  ✅ 新增支付方式只需新增工厂类");
        System.out.println("  ✅ 每个工厂职责单一");
        System.out.println("  ✅ 符合开闭原则");
        System.out.println("  ✅ 支持模板方法（统一支付流程）");
    }
}
