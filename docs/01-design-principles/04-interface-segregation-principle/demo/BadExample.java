/**
 * 违反接口隔离原则的示例
 *
 * 问题：创建了一个"胖接口"，包含所有设备可能有的功能
 * 导致：简单设备被迫实现不需要的方法
 */

/**
 * Document - 文档类
 */
class Document {
    private String content;

    public Document(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Document{" + content + "}";
    }
}

/**
 * MultiFunctionDevice - 多功能设备接口（胖接口）
 *
 * 问题：
 * - 包含了所有可能的功能（打印、扫描、传真）
 * - 不是所有设备都支持所有功能
 * - 简单设备被迫实现不需要的方法
 */
interface MultiFunctionDevice {
    void print(Document doc);
    void scan(Document doc);
    void fax(Document doc);
}

/**
 * SimplePrinter - 简单打印机（只能打印）
 *
 * 问题：
 * - 只支持打印功能
 * - 但被迫实现 scan 和 fax 方法
 * - 只能抛出异常，违反了里氏替换原则
 */
class SimplePrinter implements MultiFunctionDevice {
    private String name;

    public SimplePrinter(String name) {
        this.name = name;
    }

    @Override
    public void print(Document doc) {
        System.out.println("[" + name + "] 打印文档: " + doc.getContent());
    }

    /**
     * ❌ 被迫实现不支持的方法
     */
    @Override
    public void scan(Document doc) {
        throw new UnsupportedOperationException(name + " 不支持扫描功能");
    }

    /**
     * ❌ 被迫实现不支持的方法
     */
    @Override
    public void fax(Document doc) {
        throw new UnsupportedOperationException(name + " 不支持传真功能");
    }
}

/**
 * BasicScanner - 基础扫描仪（只能扫描）
 *
 * 问题：同 SimplePrinter
 */
class BasicScanner implements MultiFunctionDevice {
    private String name;

    public BasicScanner(String name) {
        this.name = name;
    }

    /**
     * ❌ 被迫实现不支持的方法
     */
    @Override
    public void print(Document doc) {
        throw new UnsupportedOperationException(name + " 不支持打印功能");
    }

    @Override
    public void scan(Document doc) {
        System.out.println("[" + name + "] 扫描文档: " + doc.getContent());
    }

    /**
     * ❌ 被迫实现不支持的方法
     */
    @Override
    public void fax(Document doc) {
        throw new UnsupportedOperationException(name + " 不支持传真功能");
    }
}

/**
 * AdvancedPrinter - 高级多功能一体机（支持所有功能）
 *
 * 这个类没问题，因为它确实支持所有功能
 */
class AdvancedPrinter implements MultiFunctionDevice {
    private String name;

    public AdvancedPrinter(String name) {
        this.name = name;
    }

    @Override
    public void print(Document doc) {
        System.out.println("[" + name + "] 打印文档: " + doc.getContent());
    }

    @Override
    public void scan(Document doc) {
        System.out.println("[" + name + "] 扫描文档: " + doc.getContent());
    }

    @Override
    public void fax(Document doc) {
        System.out.println("[" + name + "] 传真文档: " + doc.getContent());
    }
}

/**
 * 运行示例
 */
public class BadExample {
    public static void main(String[] args) {
        System.out.println("=== 违反接口隔离原则的示例 ===\n");

        Document doc = new Document("重要文件.pdf");

        // 创建不同的设备
        MultiFunctionDevice printer = new SimplePrinter("HP打印机");
        MultiFunctionDevice scanner = new BasicScanner("Canon扫描仪");
        MultiFunctionDevice advanced = new AdvancedPrinter("Xerox多功能一体机");

        // 测试打印功能
        System.out.println("【测试1】所有设备尝试打印:");
        tryPrint(printer, doc);   // ✓ 成功
        tryPrint(scanner, doc);   // ✗ 抛异常
        tryPrint(advanced, doc);  // ✓ 成功

        System.out.println("\n" + "=".repeat(60) + "\n");

        // 测试扫描功能
        System.out.println("【测试2】所有设备尝试扫描:");
        tryScan(printer, doc);    // ✗ 抛异常
        tryScan(scanner, doc);    // ✓ 成功
        tryScan(advanced, doc);   // ✓ 成功

        System.out.println("\n" + "=".repeat(60) + "\n");

        // 测试传真功能
        System.out.println("【测试3】所有设备尝试传真:");
        tryFax(printer, doc);     // ✗ 抛异常
        tryFax(scanner, doc);     // ✗ 抛异常
        tryFax(advanced, doc);    // ✓ 成功

        // 问题总结
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💥 问题总结");
        System.out.println("=".repeat(60));

        System.out.println("\n【问题1】接口污染");
        System.out.println("SimplePrinter 只需要 print() 方法");
        System.out.println("但被迫实现 scan() 和 fax() 方法");
        System.out.println("只能抛出 UnsupportedOperationException");

        System.out.println("\n【问题2】违反里氏替换原则");
        System.out.println("都实现了 MultiFunctionDevice 接口");
        System.out.println("但有些方法会抛异常，无法互相替换");
        System.out.println("客户端无法从接口判断哪些方法可用");

        System.out.println("\n【问题3】客户端使用困难");
        System.out.println("客户端必须知道具体设备类型");
        System.out.println("需要用 try-catch 捕获异常");
        System.out.println("或者用 instanceof 判断类型");

        System.out.println("\n【问题4】维护成本高");
        System.out.println("接口增加新方法，所有实现类都要修改");
        System.out.println("即使有些类根本不需要这个功能");

        System.out.println("\n【问题5】理解成本高");
        System.out.println("看到接口有 3 个方法，以为都能用");
        System.out.println("运行时才发现只有部分方法可用");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("核心问题：胖接口（Fat Interface）");
        System.out.println("=".repeat(60));
        System.out.println("✗ 接口包含了太多方法");
        System.out.println("✗ 客户端只使用部分方法");
        System.out.println("✗ 实现类被迫实现不需要的方法");

        System.out.println("\n➡️  运行 GoodExample.java 查看如何解决这些问题");
    }

    /**
     * 尝试打印（需要 try-catch）
     */
    static void tryPrint(MultiFunctionDevice device, Document doc) {
        try {
            device.print(doc);
        } catch (UnsupportedOperationException e) {
            System.out.println("  ✗ " + e.getMessage());
        }
    }

    /**
     * 尝试扫描（需要 try-catch）
     */
    static void tryScan(MultiFunctionDevice device, Document doc) {
        try {
            device.scan(doc);
        } catch (UnsupportedOperationException e) {
            System.out.println("  ✗ " + e.getMessage());
        }
    }

    /**
     * 尝试传真（需要 try-catch）
     */
    static void tryFax(MultiFunctionDevice device, Document doc) {
        try {
            device.fax(doc);
        } catch (UnsupportedOperationException e) {
            System.out.println("  ✗ " + e.getMessage());
        }
    }
}
