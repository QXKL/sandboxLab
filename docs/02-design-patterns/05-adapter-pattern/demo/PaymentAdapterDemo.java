/**
 * 适配器模式 - 支付系统示例
 *
 * 场景：统一支付接口，适配不同的第三方支付SDK
 * 演示：接口转换、参数转换、单位转换
 */

// ========== 目标接口 ==========

/**
 * 目标接口：统一的支付处理接口
 * 这是你的系统期望的接口
 */
interface PaymentProcessor {
    /**
     * 处理支付
     * @param orderId 订单号
     * @param amount 金额（元）
     * @return 是否成功
     */
    boolean processPayment(String orderId, double amount);

    /**
     * 获取支付方式名称
     */
    String getPaymentMethod();
}

// ========== 被适配者 ==========

/**
 * 被适配者1：支付宝SDK
 * 接口：pay(String orderId, int cents)
 * 单位：分
 * 返回：String
 */
class AlipaySDK {
    public String pay(String orderId, int cents) {
        System.out.println("  💰 支付宝支付");
        System.out.println("     订单号: " + orderId);
        System.out.println("     金额: " + cents + "分 (" + (cents / 100.0) + "元)");

        // 模拟支付
        if (cents > 0) {
            return "SUCCESS";
        }
        return "FAILED";
    }
}

/**
 * 被适配者2：微信支付SDK
 * 接口：doPay(long orderNumber, double yuan)
 * 单位：元
 * 返回：boolean
 */
class WeChatPaySDK {
    public boolean doPay(long orderNumber, double yuan) {
        System.out.println("  💚 微信支付");
        System.out.println("     订单号: " + orderNumber);
        System.out.println("     金额: " + yuan + "元");

        // 模拟支付
        return yuan > 0;
    }
}

/**
 * 被适配者3：银联支付SDK
 * 接口：payByUnionPay(String order, String amount)
 * 单位：字符串金额（元）
 * 返回：int (0=成功, -1=失败)
 */
class UnionPaySDK {
    public int payByUnionPay(String order, String amount) {
        System.out.println("  🏦 银联支付");
        System.out.println("     订单号: " + order);
        System.out.println("     金额: " + amount + "元");

        // 模拟支付
        try {
            double amt = Double.parseDouble(amount);
            return amt > 0 ? 0 : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

// ========== 适配器 ==========

/**
 * 适配器1：支付宝适配器
 * 转换：String orderId, double amount → String orderId, int cents
 * 单位转换：元 → 分
 * 返回值转换：String → boolean
 */
class AlipayAdapter implements PaymentProcessor {
    private AlipaySDK alipaySDK;

    public AlipayAdapter(AlipaySDK alipaySDK) {
        this.alipaySDK = alipaySDK;
    }

    @Override
    public boolean processPayment(String orderId, double amount) {
        // 单位转换：元 → 分
        int cents = (int) (amount * 100);

        // 调用支付宝SDK
        String result = alipaySDK.pay(orderId, cents);

        // 返回值转换：String → boolean
        return "SUCCESS".equals(result);
    }

    @Override
    public String getPaymentMethod() {
        return "支付宝";
    }
}

/**
 * 适配器2：微信支付适配器
 * 转换：String orderId, double amount → long orderNumber, double yuan
 * 参数类型转换：String → long
 */
class WeChatPayAdapter implements PaymentProcessor {
    private WeChatPaySDK weChatPaySDK;

    public WeChatPayAdapter(WeChatPaySDK weChatPaySDK) {
        this.weChatPaySDK = weChatPaySDK;
    }

    @Override
    public boolean processPayment(String orderId, double amount) {
        // 参数类型转换：String → long
        long orderNumber = Long.parseLong(orderId);

        // 调用微信支付SDK（无需单位转换）
        return weChatPaySDK.doPay(orderNumber, amount);
    }

    @Override
    public String getPaymentMethod() {
        return "微信支付";
    }
}

/**
 * 适配器3：银联支付适配器
 * 转换：String orderId, double amount → String order, String amount
 * 参数类型转换：double → String
 * 返回值转换：int → boolean
 */
class UnionPayAdapter implements PaymentProcessor {
    private UnionPaySDK unionPaySDK;

    public UnionPayAdapter(UnionPaySDK unionPaySDK) {
        this.unionPaySDK = unionPaySDK;
    }

    @Override
    public boolean processPayment(String orderId, double amount) {
        // 参数类型转换：double → String
        String amountStr = String.valueOf(amount);

        // 调用银联SDK
        int result = unionPaySDK.payByUnionPay(orderId, amountStr);

        // 返回值转换：int → boolean (0表示成功)
        return result == 0;
    }

    @Override
    public String getPaymentMethod() {
        return "银联支付";
    }
}

// ========== 客户端 ==========

/**
 * 客户端：订单处理器
 */
class OrderProcessor {
    /**
     * 处理订单支付
     */
    public void processOrder(String orderId, double amount, PaymentProcessor payment) {
        System.out.println("\n📦 处理订单");
        System.out.println("   订单号: " + orderId);
        System.out.println("   金额: " + amount + "元");
        System.out.println("   支付方式: " + payment.getPaymentMethod());
        System.out.println();

        boolean success = payment.processPayment(orderId, amount);

        if (success) {
            System.out.println("   ✅ 支付成功!");
        } else {
            System.out.println("   ❌ 支付失败!");
        }
    }
}

// ========== 测试 ==========

public class PaymentAdapterDemo {
    public static void main(String[] args) {
        System.out.println("=== 适配器模式 - 支付系统 ===\n");

        OrderProcessor processor = new OrderProcessor();

        // 示例1：支付宝支付
        System.out.println("【1. 支付宝支付】");
        PaymentProcessor alipay = new AlipayAdapter(new AlipaySDK());
        processor.processOrder("ALI20240001", 99.99, alipay);

        // 示例2：微信支付
        System.out.println("\n【2. 微信支付】");
        PaymentProcessor wechat = new WeChatPayAdapter(new WeChatPaySDK());
        processor.processOrder("12345678901234", 199.00, wechat);

        // 示例3：银联支付
        System.out.println("\n【3. 银联支付】");
        PaymentProcessor unionpay = new UnionPayAdapter(new UnionPaySDK());
        processor.processOrder("UNI20240001", 299.50, unionpay);

        // 示例4：统一接口的优势
        System.out.println("\n【4. 统一接口的优势】");
        demonstrateUniformInterface();

        // 示例5：适配器转换内容对比
        System.out.println("\n【5. 适配器转换内容对比】");
        showConversionComparison();
    }

    /**
     * 演示统一接口的优势
     */
    static void demonstrateUniformInterface() {
        System.out.println("\n✅ 客户端代码统一:");
        System.out.println("   payment.processPayment(orderId, amount);");
        System.out.println();
        System.out.println("   不需要关心底层SDK的差异:");
        System.out.println("   - 支付宝: alipaySDK.pay(orderId, cents)");
        System.out.println("   - 微信: weChatPaySDK.doPay(orderNumber, yuan)");
        System.out.println("   - 银联: unionPaySDK.payByUnionPay(order, amount)");
        System.out.println();
        System.out.println("✅ 易于切换支付方式:");
        System.out.println("   只需替换适配器对象即可");
        System.out.println();
        System.out.println("✅ 易于扩展:");
        System.out.println("   增加新支付方式，只需添加新适配器");
    }

    /**
     * 显示适配器转换内容对比
     */
    static void showConversionComparison() {
        System.out.println("\n┌─────────┬──────────────────┬─────────────────┬────────┐");
        System.out.println("│ SDK     │ 方法名           │ 参数类型        │ 单位   │");
        System.out.println("├─────────┼──────────────────┼─────────────────┼────────┤");
        System.out.println("│ 支付宝  │ pay()            │ String, int     │ 分     │");
        System.out.println("│ 微信    │ doPay()          │ long, double    │ 元     │");
        System.out.println("│ 银联    │ payByUnionPay()  │ String, String  │ 元     │");
        System.out.println("└─────────┴──────────────────┴─────────────────┴────────┘");
        System.out.println();
        System.out.println("适配器统一为:");
        System.out.println("  processPayment(String orderId, double amount)");
        System.out.println();
        System.out.println("转换内容:");
        System.out.println("  1️⃣  方法名转换");
        System.out.println("  2️⃣  参数类型转换 (String ↔ long)");
        System.out.println("  3️⃣  单位转换 (元 ↔ 分)");
        System.out.println("  4️⃣  返回值转换 (String/int → boolean)");
    }
}
