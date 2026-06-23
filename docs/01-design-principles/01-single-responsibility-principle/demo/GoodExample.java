/**
 * 符合单一职责原则的示例
 *
 * 将不同的职责分离到不同的类中，每个类只做一件事
 */

// ========== 职责1: 数据存储 ==========
/**
 * User - 纯数据类（POJO/Entity）
 * 职责：只负责存储用户数据，不包含任何业务逻辑
 */
class User {
    private final String name;
    private final String email;
    private final String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "User{name='" + name + "', email='" + email + "'}";
    }
}

// ========== 职责2: 数据验证 ==========
/**
 * UserValidator - 用户数据验证器
 * 职责：只负责验证用户数据是否合法
 * 变化原因：验证规则变化时
 */
class UserValidator {

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }

    public ValidationResult validate(User user) {
        // 验证姓名
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return new ValidationResult(false, "姓名不能为空");
        }

        // 验证邮箱
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            return new ValidationResult(false, "邮箱格式不正确");
        }

        // 验证密码
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return new ValidationResult(false, "密码至少6位");
        }

        return new ValidationResult(true, "验证通过");
    }
}

// ========== 职责3: 数据持久化 ==========
/**
 * UserRepository - 用户数据仓库
 * 职责：只负责用户数据的持久化（保存、查询、删除）
 * 变化原因：数据库技术变化时（MySQL → PostgreSQL → MongoDB）
 */
class UserRepository {

    public void save(User user) {
        // 模拟数据库保存操作
        System.out.println("  [Repository] 保存用户到数据库: " + user);
        try {
            Thread.sleep(50); // 模拟数据库延迟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 未来可能添加的方法（同样是持久化职责）
    // public User findByEmail(String email) { ... }
    // public void delete(User user) { ... }
    // public List<User> findAll() { ... }
}

// ========== 职责4: 邮件发送 ==========
/**
 * EmailService - 邮件服务
 * 职责：只负责发送各种邮件
 * 变化原因：邮件服务提供商变化时（SendGrid → AWS SES → 阿里云）
 */
class EmailService {

    public void sendWelcomeEmail(User user) {
        // 模拟发送欢迎邮件
        System.out.println("  [EmailService] 发送欢迎邮件到: " + user.getEmail());
        try {
            Thread.sleep(50); // 模拟邮件发送延迟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 未来可能添加的方法（同样是邮件发送职责）
    // public void sendPasswordResetEmail(User user) { ... }
    // public void sendNotificationEmail(User user, String content) { ... }
}

// ========== 职责5: 业务协调 ==========
/**
 * UserService - 用户服务（业务逻辑协调者）
 * 职责：协调各个单一职责的类，完成业务流程
 * 变化原因：业务流程变化时（比如注册前增加短信验证）
 */
class UserService {
    private final UserValidator validator;
    private final UserRepository repository;
    private final EmailService emailService;

    public UserService(UserValidator validator,
                      UserRepository repository,
                      EmailService emailService) {
        this.validator = validator;
        this.repository = repository;
        this.emailService = emailService;
    }

    /**
     * 用户注册业务流程
     */
    public boolean registerUser(User user) {
        System.out.println("\n=== 符合SRP的用户注册流程 ===");

        // 步骤1: 验证用户数据
        UserValidator.ValidationResult result = validator.validate(user);
        System.out.println("✓ [Validator] " + result.getMessage());

        if (!result.isValid()) {
            System.out.println("❌ 用户注册失败！");
            return false;
        }

        // 步骤2: 保存到数据库
        repository.save(user);
        System.out.println("✓ [Repository] 用户保存成功");

        // 步骤3: 发送欢迎邮件
        emailService.sendWelcomeEmail(user);
        System.out.println("✓ [EmailService] 欢迎邮件发送成功");

        System.out.println("✓ 用户注册成功！");
        return true;
    }
}

// ========== 运行示例 ==========
public class GoodExample {
    public static void main(String[] args) {
        System.out.println("【改进方案】职责分离的好处：");
        System.out.println("✓ 易于维护：修改验证规则只需改UserValidator");
        System.out.println("✓ 易于测试：每个类可以独立测试，无需mock其他依赖");
        System.out.println("✓ 易于复用：EmailService可以在其他地方使用");
        System.out.println("✓ 职责清晰：每个类的名字就说明了它的职责\n");

        // 创建各个职责的实例
        UserValidator validator = new UserValidator();
        UserRepository repository = new UserRepository();
        EmailService emailService = new EmailService();
        UserService userService = new UserService(validator, repository, emailService);

        // 测试：有效用户
        User validUser = new User("张三", "zhangsan@example.com", "password123");
        userService.registerUser(validUser);

        System.out.println("\n" + "=".repeat(50) + "\n");

        // 测试：无效用户
        User invalidUser = new User("", "invalid-email", "123");
        userService.registerUser(invalidUser);

        // 优势展示
        System.out.println("\n💡 职责分离的优势：");
        System.out.println("1. 修改验证规则？只改 UserValidator，不影响其他类");
        System.out.println("2. 换数据库？只改 UserRepository，不影响业务逻辑");
        System.out.println("3. 换邮件服务？只改 EmailService，不影响其他部分");
        System.out.println("4. 多人协作？每个人改不同的类，不会冲突");
        System.out.println("5. 单元测试？每个类都很容易独立测试");

        System.out.println("\n📊 对比统计：");
        System.out.println("BadExample: 1个类，4个职责，难以维护");
        System.out.println("GoodExample: 5个类，每个1个职责，清晰明了");

        System.out.println("\n🎯 记住：一个类只做一件事，只有一个修改的理由！");
    }
}
