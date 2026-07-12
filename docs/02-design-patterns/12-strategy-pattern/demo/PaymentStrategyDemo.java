/**
 * 策略模式 - 支付策略示例
 *
 * 场景：支付系统（多种支付方式）
 * 演示：运行时切换支付策略，消除if-else
 */

import java.util.*;

// ========== 抽象策略 ==========

/**
 * Strategy：支付策略接口
 */
interface PaymentStrategy {
    boolean pay(double amount);
    String getPaymentType();
}

// ========== 具体策略 ==========

/**
 * ConcreteStrategy：支付宝支付
 */
class AlipayPayment implements PaymentStrategy {
    private String account;
    private String password;

    public AlipayPayment(String account, String password) {
        this.account = account;
        this.password = password;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("\n【支付宝支付】");
        System.out.println("  账号: " + maskAccount(account));
        System.out.println("  金额: ¥" + amount);
        System.out.println("  验证密码...");
        System.out.println("  ✅ 支付成功！");
        return true;
    }

    @Override
    public String getPaymentType() {
        return "支付宝";
    }

    private String maskAccount(String account) {
        if (account.length() <= 4) return account;
        return account.substring(0, 3) + "****" + account.substring(account.length() - 2);
    }
}

/**
 * ConcreteStrategy：微信支付
 */
class WeChatPayment implements PaymentStrategy {
    private String wechatId;

    public WeChatPayment(String wechatId) {
        this.wechatId = wechatId;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("\n【微信支付】");
        System.out.println("  微信号: " + wechatId);
        System.out.println("  金额: ¥" + amount);
        System.out.println("  扫描二维码...");
        System.out.println("  ✅ 支付成功！");
        return true;
    }

    @Override
    public String getPaymentType() {
        return "微信支付";
    }
}

/**
 * ConcreteStrategy：信用卡支付
 */
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cvv;
    private String expiryDate;

    public CreditCardPayment(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("\n【信用卡支付】");
        System.out.println("  卡号: " + maskCardNumber(cardNumber));
        System.out.println("  有效期: " + expiryDate);
        System.out.println("  金额: ¥" + amount);
        System.out.println("  验证CVV...");
        System.out.println("  ✅ 支付成功！");
        return true;
    }

    @Override
    public String getPaymentType() {
        return "信用卡";
    }

    private String maskCardNumber(String number) {
        if (number.length() <= 8) return number;
        return number.substring(0, 4) + " **** **** " + number.substring(number.length() - 4);
    }
}

// ========== 上下文 ==========

/**
 * Context：支付上下文
 */
class PaymentContext {
    private PaymentStrategy strategy;
    private double totalAmount;

    public PaymentContext() {
        this.totalAmount = 0;
    }

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
        System.out.println("\n>>> 切换支付方式为: " + strategy.getPaymentType());
    }

    public void addItem(String item, double price) {
        totalAmount += price;
        System.out.println("  添加商品: " + item + " (¥" + price + ")");
    }

    public void executePayment() {
        if (strategy == null) {
            System.out.println("❌ 请先选择支付方式！");
            return;
        }

        System.out.println("\n==========================================");
        System.out.println("  总金额: ¥" + totalAmount);
        System.out.println("  支付方式: " + strategy.getPaymentType());
        System.out.println("==========================================");

        if (strategy.pay(totalAmount)) {
            System.out.println("\n🎉 订单支付完成！");
            totalAmount = 0;  // 重置金额
        } else {
            System.out.println("\n❌ 支付失败，请重试");
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}

// ========== 策略工厂 ==========

/**
 * 策略工厂：简化策略创建
 */
class PaymentStrategyFactory {
    public static PaymentStrategy createStrategy(String type, Map<String, String> params) {
        switch (type.toLowerCase()) {
            case "alipay":
                return new AlipayPayment(
                    params.get("account"),
                    params.get("password")
                );
            case "wechat":
                return new WeChatPayment(params.get("wechatId"));
            case "creditcard":
                return new CreditCardPayment(
                    params.get("cardNumber"),
                    params.get("cvv"),
                    params.get("expiryDate")
                );
            default:
                throw new IllegalArgumentException("未知的支付方式: " + type);
        }
    }
}

// ========== 测试 ==========

public class PaymentStrategyDemo {
    public static void main(String[] args) {
        System.out.println("=== 策略模式 - 支付系统 ===\n");

        // 示例1：基本使用
        System.out.println("【1. 基本支付流程】");
        demonstrateBasicPayment();

        // 示例2：运行时切换策略
        System.out.println("\n\n【2. 运行时切换支付方式】");
        demonstrateSwitchStrategy();

        // 示例3：使用策略工厂
        System.out.println("\n\n【3. 使用策略工厂】");
        demonstrateWithFactory();

        // 示例4：对比传统if-else实现
        System.out.println("\n\n【4. 策略模式 vs if-else】");
        compareWithIfElse();
    }

    /**
     * 基本支付流程
     */
    static void demonstrateBasicPayment() {
        PaymentContext context = new PaymentContext();

        // 添加商品
        System.out.println("【购物车】");
        context.addItem("Java编程思想", 89.00);
        context.addItem("设计模式", 69.00);

        // 选择支付方式
        context.setStrategy(new AlipayPayment("user@example.com", "******"));

        // 执行支付
        context.executePayment();

        System.out.println("\n✅ 关键点:");
        System.out.println("  - 客户端选择具体策略");
        System.out.println("  - 上下文委托策略执行");
        System.out.println("  - 支付逻辑独立封装");
    }

    /**
     * 运行时切换策略
     */
    static void demonstrateSwitchStrategy() {
        PaymentContext context = new PaymentContext();

        // 第一次购物：使用支付宝
        System.out.println("【第一笔订单】");
        context.addItem("机械键盘", 299.00);
        context.setStrategy(new AlipayPayment("138****5678", "******"));
        context.executePayment();

        // 第二次购物：切换到微信支付
        System.out.println("\n【第二笔订单】");
        context.addItem("鼠标垫", 29.00);
        context.setStrategy(new WeChatPayment("wxid_abc123"));
        context.executePayment();

        // 第三次购物：使用信用卡
        System.out.println("\n【第三笔订单】");
        context.addItem("显示器", 1299.00);
        context.setStrategy(new CreditCardPayment("6222 0012 3456 7890", "123", "12/25"));
        context.executePayment();

        System.out.println("\n✅ 关键点:");
        System.out.println("  - 同一个Context对象");
        System.out.println("  - 可以随时切换策略");
        System.out.println("  - 策略之间相互独立");
    }

    /**
     * 使用策略工厂
     */
    static void demonstrateWithFactory() {
        PaymentContext context = new PaymentContext();

        context.addItem("咖啡", 28.00);
        context.addItem("三明治", 18.00);

        // 使用工厂创建策略
        Map<String, String> params = new HashMap<>();
        params.put("wechatId", "user_wechat");

        PaymentStrategy strategy = PaymentStrategyFactory.createStrategy("wechat", params);
        context.setStrategy(strategy);
        context.executePayment();

        System.out.println("\n✅ 优势:");
        System.out.println("  - 工厂封装策略创建逻辑");
        System.out.println("  - 客户端无需知道具体类");
        System.out.println("  - 策略模式 + 工厂模式");
    }

    /**
     * 对比传统if-else实现
     */
    static void compareWithIfElse() {
        System.out.println("❌ 传统if-else实现:");
        System.out.println("```java");
        System.out.println("void processPayment(String type, double amount) {");
        System.out.println("    if (type.equals(\"alipay\")) {");
        System.out.println("        // 支付宝支付逻辑");
        System.out.println("    } else if (type.equals(\"wechat\")) {");
        System.out.println("        // 微信支付逻辑");
        System.out.println("    } else if (type.equals(\"creditcard\")) {");
        System.out.println("        // 信用卡支付逻辑");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println("问题:");
        System.out.println("  - 违反开闭原则");
        System.out.println("  - 难以测试");
        System.out.println("  - 代码重复");

        System.out.println("\n✅ 策略模式实现:");
        System.out.println("```java");
        System.out.println("// 1. 定义策略接口");
        System.out.println("interface PaymentStrategy {");
        System.out.println("    boolean pay(double amount);");
        System.out.println("}");
        System.out.println("");
        System.out.println("// 2. 具体策略（每个独立）");
        System.out.println("class AlipayPayment implements PaymentStrategy { }");
        System.out.println("class WeChatPayment implements PaymentStrategy { }");
        System.out.println("");
        System.out.println("// 3. 上下文");
        System.out.println("context.setStrategy(strategy);");
        System.out.println("context.executePayment();");
        System.out.println("```");

        System.out.println("\n优势:");
        System.out.println("  - 无if-else");
        System.out.println("  - 符合开闭原则");
        System.out.println("  - 易于测试");
        System.out.println("  - 策略独立");

        System.out.println("\n📊 对比:");
        System.out.println("  ┌──────────────┬─────────────┬─────────────┐");
        System.out.println("  │ 对比         │ if-else     │ 策略模式    │");
        System.out.println("  ├──────────────┼─────────────┼─────────────┤");
        System.out.println("  │ 代码可读性   │ 差          │ 好          │");
        System.out.println("  │ 扩展性       │ 差          │ 好          │");
        System.out.println("  │ 可测试性     │ 难          │ 易          │");
        System.out.println("  │ 开闭原则     │ 违反        │ 符合        │");
        System.out.println("  └──────────────┴─────────────┴─────────────┘");
    }
}
