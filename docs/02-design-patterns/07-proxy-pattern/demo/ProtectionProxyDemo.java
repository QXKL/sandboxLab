/**
 * 代理模式 - 保护代理示例
 *
 * 场景：文档管理系统，根据用户角色控制访问
 * 演示：保护代理、权限控制、访问日志
 */

// ========== Subject接口 ==========

/**
 * 抽象主题：文档接口
 */
interface Document {
    void view();
    void edit(String content);
    void delete();
}

// ========== RealSubject（真实对象） ==========

/**
 * 真实对象：真实文档
 */
class RealDocument implements Document {
    private String filename;
    private String content;

    public RealDocument(String filename, String content) {
        this.filename = filename;
        this.content = content;
        System.out.println("  📄 [真实文档] 加载文档: " + filename);
    }

    @Override
    public void view() {
        System.out.println("  👁️  [真实文档] 查看文档: " + filename);
        System.out.println("     内容: " + content);
    }

    @Override
    public void edit(String newContent) {
        this.content = newContent;
        System.out.println("  ✏️  [真实文档] 编辑文档: " + filename);
        System.out.println("     新内容: " + newContent);
    }

    @Override
    public void delete() {
        System.out.println("  🗑️  [真实文档] 删除文档: " + filename);
    }
}

// ========== User（用户） ==========

/**
 * 用户类
 */
class User {
    private String username;
    private String role;  // ADMIN, USER, GUEST

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean hasReadPermission() {
        return role.equals("ADMIN") || role.equals("USER") || role.equals("GUEST");
    }

    public boolean hasWritePermission() {
        return role.equals("ADMIN") || role.equals("USER");
    }

    public boolean hasDeletePermission() {
        return role.equals("ADMIN");
    }
}

// ========== Proxy（代理） ==========

/**
 * 代理：文档代理（权限控制）
 */
class DocumentProxy implements Document {
    private RealDocument realDocument;
    private User user;
    private String filename;

    public DocumentProxy(String filename, String content, User user) {
        this.filename = filename;
        this.user = user;
        this.realDocument = new RealDocument(filename, content);
    }

    @Override
    public void view() {
        logAccess("VIEW");

        if (user.hasReadPermission()) {
            System.out.println("  ✅ [代理] 权限检查通过: " + user.getUsername() + " 可以查看");
            realDocument.view();
        } else {
            System.out.println("  ❌ [代理] 权限不足: " + user.getUsername() + " 无法查看");
        }
    }

    @Override
    public void edit(String content) {
        logAccess("EDIT");

        if (user.hasWritePermission()) {
            System.out.println("  ✅ [代理] 权限检查通过: " + user.getUsername() + " 可以编辑");
            realDocument.edit(content);
        } else {
            System.out.println("  ❌ [代理] 权限不足: " + user.getUsername() + " 无法编辑");
        }
    }

    @Override
    public void delete() {
        logAccess("DELETE");

        if (user.hasDeletePermission()) {
            System.out.println("  ✅ [代理] 权限检查通过: " + user.getUsername() + " 可以删除");
            realDocument.delete();
        } else {
            System.out.println("  ❌ [代理] 权限不足: " + user.getUsername() + " 无法删除");
        }
    }

    /**
     * 记录访问日志
     */
    private void logAccess(String operation) {
        System.out.println("  📝 [日志] " + user.getUsername() + " (" + user.getRole() + ") 尝试 " +
                          operation + " 文档: " + filename);
    }
}

// ========== 测试 ==========

public class ProtectionProxyDemo {
    public static void main(String[] args) {
        System.out.println("=== 代理模式 - 保护代理（权限控制） ===\n");

        // 示例1：不同角色的访问控制
        System.out.println("【1. 不同角色的访问控制】\n");
        demonstrateRoleBasedAccess();

        // 示例2：访问日志记录
        System.out.println("\n【2. 访问日志记录】\n");
        demonstrateAccessLog();

        // 示例3：保护代理的优势
        System.out.println("\n【3. 保护代理的优势】\n");
        demonstrateAdvantages();
    }

    /**
     * 演示不同角色的访问控制
     */
    static void demonstrateRoleBasedAccess() {
        String content = "这是一份机密文档";

        // 管理员
        System.out.println("--- 管理员访问 ---");
        User admin = new User("Alice", "ADMIN");
        Document doc1 = new DocumentProxy("secret.txt", content, admin);
        doc1.view();
        doc1.edit("修改后的内容");
        doc1.delete();

        System.out.println();

        // 普通用户
        System.out.println("--- 普通用户访问 ---");
        User user = new User("Bob", "USER");
        Document doc2 = new DocumentProxy("secret.txt", content, user);
        doc2.view();
        doc2.edit("尝试修改");
        doc2.delete();  // 无权限

        System.out.println();

        // 访客
        System.out.println("--- 访客访问 ---");
        User guest = new User("Charlie", "GUEST");
        Document doc3 = new DocumentProxy("secret.txt", content, guest);
        doc3.view();
        doc3.edit("尝试修改");  // 无权限
        doc3.delete();  // 无权限
    }

    /**
     * 演示访问日志记录
     */
    static void demonstrateAccessLog() {
        System.out.println("系统自动记录所有访问操作:\n");

        User user = new User("David", "USER");
        Document doc = new DocumentProxy("report.txt", "季度报告", user);

        doc.view();
        System.out.println();
        doc.edit("更新报告");
        System.out.println();
        doc.view();
        System.out.println();
        doc.delete();  // 无权限

        System.out.println("\n✅ 所有操作都被记录，便于审计和追溯");
    }

    /**
     * 演示保护代理的优势
     */
    static void demonstrateAdvantages() {
        System.out.println("✅ 优势1: 权限控制");
        System.out.println("   - 在代理中统一检查权限");
        System.out.println("   - 真实对象无需关心权限逻辑");
        System.out.println("   - 职责分离清晰");

        System.out.println("\n✅ 优势2: 访问日志");
        System.out.println("   - 自动记录所有访问操作");
        System.out.println("   - 便于审计和追溯");
        System.out.println("   - 安全合规");

        System.out.println("\n✅ 优势3: 灵活性");
        System.out.println("   - 可以动态改变权限规则");
        System.out.println("   - 不修改真实对象代码");
        System.out.println("   - 符合开闭原则");

        System.out.println("\n✅ 优势4: 安全性");
        System.out.println("   - 防止未授权访问");
        System.out.println("   - 保护敏感数据");
        System.out.println("   - 降低安全风险");

        System.out.println("\n📊 权限矩阵:");
        System.out.println("   ┌─────────┬──────┬──────┬──────┐");
        System.out.println("   │ 角色    │ 查看 │ 编辑 │ 删除 │");
        System.out.println("   ├─────────┼──────┼──────┼──────┤");
        System.out.println("   │ ADMIN   │  ✅  │  ✅  │  ✅  │");
        System.out.println("   │ USER    │  ✅  │  ✅  │  ❌  │");
        System.out.println("   │ GUEST   │  ✅  │  ❌  │  ❌  │");
        System.out.println("   └─────────┴──────┴──────┴──────┘");

        System.out.println("\n⚠️  适用场景:");
        System.out.println("   - 需要权限控制的系统");
        System.out.println("   - 敏感数据访问");
        System.out.println("   - 需要审计日志");
        System.out.println("   - 多角色系统");

        System.out.println("\n🔒 安全增强:");
        System.out.println("   - 统一的权限检查点");
        System.out.println("   - 防止绕过检查");
        System.out.println("   - 细粒度的访问控制");
    }
}
