/**
 * 符合接口隔离原则的示例
 *
 * 解决方案：拆分胖接口为多个小接口
 * - 每个接口只包含相关的方法
 * - 设备只实现它支持的接口
 * - 客户端只依赖它需要的接口
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

// ========== 接口层：按功能拆分 ==========

/**
 * Printer - 打印机接口
 * 只包含打印相关的方法
 */
interface Printer {
    void print(Document doc);
}

/**
 * Scanner - 扫描仪接口
 * 只包含扫描相关的方法
 */
interface Scanner {
    void scan(Document doc);
}

/**
 * Fax - 传真机接口
 * 只包含传真相关的方法
 */
interface Fax {
    void fax(Document doc);
}

// ========== 实现层：按需实现接口 ==========

/**
 * SimplePrinter - 简单打印机
 * 只实现 Printer 接口，不需要实现不支持的功能
 */
class SimplePrinter implements Printer {
    private String name;

    public SimplePrinter(String name) {
        this.name = name;
    }

    @Override
    public void print(Document doc) {
        System.out.println("[" + name + "] 打印文档: " + doc.getContent());
    }
}

/**
 * BasicScanner - 基础扫描仪
 * 只实现 Scanner 接口
 */
class BasicScanner implements Scanner {
    private String name;

    public BasicScanner(String name) {
        this.name = name;
    }

    @Override
    public void scan(Document doc) {
        System.out.println("[" + name + "] 扫描文档: " + doc.getContent());
    }
}

/**
 * AdvancedPrinter - 高级多功能一体机
 * 通过实现多个接口来组合功能
 */
class AdvancedPrinter implements Printer, Scanner, Fax {
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

// ========== 客户端代码：只依赖需要的接口 ==========

/**
 * PrintClient - 打印客户端
 * 只依赖 Printer 接口，不关心其他功能
 */
class PrintClient {
    private Printer printer;

    public PrintClient(Printer printer) {
        this.printer = printer;
    }

    public void printDocument(Document doc) {
        System.out.println("  [PrintClient] 执行打印任务");
        printer.print(doc);
    }
}

/**
 * ScanClient - 扫描客户端
 * 只依赖 Scanner 接口
 */
class ScanClient {
    private Scanner scanner;

    public ScanClient(Scanner scanner) {
        this.scanner = scanner;
    }

    public void scanDocument(Document doc) {
        System.out.println("  [ScanClient] 执行扫描任务");
        scanner.scan(doc);
    }
}

/**
 * FaxClient - 传真客户端
 * 只依赖 Fax 接口
 */
class FaxClient {
    private Fax fax;

    public FaxClient(Fax fax) {
        this.fax = fax;
    }

    public void faxDocument(Document doc) {
        System.out.println("  [FaxClient] 执行传真任务");
        fax.fax(doc);
    }
}

/**
 * 运行示例
 */
public class GoodExample {
    public static void main(String[] args) {
        System.out.println("=== 符合接口隔离原则的示例 ===\n");

        Document doc = new Document("重要文件.pdf");

        // 创建不同的设备
        Printer simplePrinter = new SimplePrinter("HP打印机");
        Scanner basicScanner = new BasicScanner("Canon扫描仪");
        AdvancedPrinter advancedPrinter = new AdvancedPrinter("Xerox多功能一体机");

        // 测试1：打印客户端
        System.out.println("【测试1】打印客户端使用不同设备:");
        PrintClient printClient1 = new PrintClient(simplePrinter);
        printClient1.printDocument(doc);

        PrintClient printClient2 = new PrintClient(advancedPrinter);
        printClient2.printDocument(doc);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // 测试2：扫描客户端
        System.out.println("【测试2】扫描客户端使用不同设备:");
        ScanClient scanClient1 = new ScanClient(basicScanner);
        scanClient1.scanDocument(doc);

        ScanClient scanClient2 = new ScanClient(advancedPrinter);
        scanClient2.scanDocument(doc);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // 测试3：传真客户端
        System.out.println("【测试3】传真客户端使用多功能设备:");
        FaxClient faxClient = new FaxClient(advancedPrinter);
        faxClient.faxDocument(doc);

        // 测试4：类型安全
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【测试4】类型安全（编译期检查）");
        System.out.println("=".repeat(60) + "\n");

        System.out.println("✓ SimplePrinter 只实现了 Printer 接口");
        System.out.println("✓ 无法传递给需要 Scanner 接口的客户端");
        System.out.println("✓ 编译器会报错，在编译期就发现问题");
        System.out.println();
        System.out.println("// ❌ 编译错误：类型不匹配");
        System.out.println("// ScanClient client = new ScanClient(simplePrinter);");

        // 优势总结
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 符合接口隔离原则的优势");
        System.out.println("=".repeat(60));

        System.out.println("\n【接口隔离】");
        System.out.println("✓ 每个接口只包含相关的方法");
        System.out.println("✓ Printer 接口只有 print()");
        System.out.println("✓ Scanner 接口只有 scan()");
        System.out.println("✓ Fax 接口只有 fax()");

        System.out.println("\n【按需实现】");
        System.out.println("✓ SimplePrinter 只实现 Printer 接口");
        System.out.println("✓ BasicScanner 只实现 Scanner 接口");
        System.out.println("✓ AdvancedPrinter 实现多个接口来组合功能");
        System.out.println("✓ 不会有空实现或抛异常的方法");

        System.out.println("\n【客户端隔离】");
        System.out.println("✓ PrintClient 只依赖 Printer 接口");
        System.out.println("✓ ScanClient 只依赖 Scanner 接口");
        System.out.println("✓ 客户端不依赖不需要的接口");

        System.out.println("\n【类型安全】");
        System.out.println("✓ 编译期就能发现类型错误");
        System.out.println("✓ 不需要运行时 try-catch");
        System.out.println("✓ 不需要 instanceof 判断");

        System.out.println("\n【灵活组合】");
        System.out.println("✓ 通过实现多个接口来组合功能");
        System.out.println("✓ 想要什么功能就实现什么接口");
        System.out.println("✓ 高度灵活，易于扩展");

        System.out.println("\n【维护性】");
        System.out.println("✓ 接口变化影响范围小");
        System.out.println("✓ Printer 接口变化只影响实现了它的类");
        System.out.println("✓ 不会影响只实现 Scanner 的类");

        System.out.println("\n【符合其他原则】");
        System.out.println("✓ 符合里氏替换原则（所有方法都能正常工作）");
        System.out.println("✓ 符合单一职责原则（每个接口职责单一）");
        System.out.println("✓ 符合开闭原则（扩展新设备不修改现有代码）");

        // 对比 BadExample
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【对比 BadExample】");
        System.out.println("=".repeat(60));

        System.out.println("\nBadExample（违反ISP）:");
        System.out.println("  ✗ 胖接口包含所有方法");
        System.out.println("  ✗ 简单设备被迫实现不需要的方法");
        System.out.println("  ✗ 抛出 UnsupportedOperationException");
        System.out.println("  ✗ 客户端需要 try-catch 捕获异常");
        System.out.println("  ✗ 运行时才发现方法不支持");

        System.out.println("\nGoodExample（符合ISP）:");
        System.out.println("  ✓ 小接口只包含相关方法");
        System.out.println("  ✓ 设备只实现支持的接口");
        System.out.println("  ✓ 所有方法都能正常工作");
        System.out.println("  ✓ 客户端不需要异常处理");
        System.out.println("  ✓ 编译期就能发现类型错误");

        System.out.println("\n🎯 核心原则：客户端不应该依赖它不使用的接口");
        System.out.println("接口应该小而专注，按客户端需求设计！");
    }
}
