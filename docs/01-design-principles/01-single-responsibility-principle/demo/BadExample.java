/**
 * 违反单一职责原则的示例
 *
 * 这个类承担了太多职责：
 * 1. 数据存储（name, email, password）
 * 2. 数据验证（validate方法）
 * 3. 持久化（save方法）
 * 4. 业务逻辑（sendWelcomeEmail方法）
 */
class UserBad {
    private String name;
    private String email;
    private String password;

    public UserBad(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // 职责1: 数据访问
    public String getName() { return name; }
    public String getEmail() { return email; }

    // 职责2: 数据验证
    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ 验证失败：姓名不能为空");
            return false;
        }

        if (email == null || !email.contains("@")) {
            System.out.println("❌ 验证失败：邮箱格式不正确");
            return false;
        }

        if (password == null || password.length() < 6) {
            System.out.println("❌ 验证失败：密码至少6位");
            return false;
        }

        System.out.println("✓ 验证通过");
        return true;
    }

    // 职责3: 持久化
    public void save() {
        // 模拟数据库保存操作
        System.out.println("✓ 保存用户到数据库: " + name);
        try {
            Thread.sleep(100); // 模拟数据库延迟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 职责4: 业务逻辑（邮件发送）
    public void sendWelcomeEmail() {
        // 模拟发送邮件
        System.out.println("✓ 发送欢迎邮件到: " + email);
        try {
            Thread.sleep(100); // 模拟邮件发送延迟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 用户注册流程（混在一起）
    public void register() {
        System.out.println("\n=== 违反SRP的用户注册流程 ===");
        if (validate()) {
            save();
            sendWelcomeEmail();
            System.out.println("✓ 用户注册成功！");
        } else {
            System.out.println("❌ 用户注册失败！");
        }
    }
}

/**
 * 运行示例
 */
public class BadExample {
    public static void main(String[] args) {
        System.out.println("【问题演示】一个类承担多个职责的坏处：");
        System.out.println("- 难以维护：修改验证规则、数据库、邮件服务都要改这个类");
        System.out.println("- 难以测试：测试验证逻辑时也要mock数据库和邮件");
        System.out.println("- 难以复用：无法单独复用验证或邮件功能");
        System.out.println("- 职责不清：这个类到底是做什么的？\n");

        // 创建用户并注册
        UserBad user = new UserBad("张三", "zhangsan@example.com", "password123");
        user.register();

        System.out.println("\n---");

        // 无效用户
        UserBad invalidUser = new UserBad("", "invalid-email", "123");
        invalidUser.register();

        System.out.println("\n💡 思考：");
        System.out.println("1. 如果要修改邮箱验证规则，需要改动这个类");
        System.out.println("2. 如果要换数据库（MySQL → PostgreSQL），需要改动这个类");
        System.out.println("3. 如果要换邮件服务商，需要改动这个类");
        System.out.println("4. 多个开发者同时修改这个类，容易冲突");
        System.out.println("\n➡️  运行 GoodExample.java 查看如何解决这些问题");
    }
}
