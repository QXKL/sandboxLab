/**
 * 装饰器模式 - I/O流装饰器示例
 *
 * 场景：模拟Java I/O流的装饰器设计
 * 演示：理解BufferedInputStream、DataInputStream等的设计原理
 */

// ========== Component接口 ==========

/**
 * 组件接口：输入流
 */
interface InputStream {
    /**
     * 读取一个字节
     */
    int read();

    /**
     * 关闭流
     */
    void close();
}

// ========== ConcreteComponent（具体组件） ==========

/**
 * 具体组件：文件输入流
 * 模拟从文件读取数据
 */
class FileInputStream implements InputStream {
    private String filename;
    private int position = 0;
    private String data = "Hello World from file!";

    public FileInputStream(String filename) {
        this.filename = filename;
        System.out.println("  📂 打开文件: " + filename);
    }

    @Override
    public int read() {
        if (position < data.length()) {
            return data.charAt(position++);
        }
        return -1;  // 文件结束
    }

    @Override
    public void close() {
        System.out.println("  📂 关闭文件: " + filename);
    }
}

// ========== Decorator（抽象装饰器） ==========

/**
 * 抽象装饰器：过滤流
 * 持有一个InputStream，转发调用
 */
abstract class FilterInputStream implements InputStream {
    protected InputStream in;  // 被装饰的流

    public FilterInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() {
        return in.read();
    }

    @Override
    public void close() {
        in.close();
    }
}

// ========== ConcreteDecorator（具体装饰器） ==========

/**
 * 具体装饰器1：缓冲输入流
 * 添加缓冲功能，减少I/O次数
 */
class BufferedInputStream extends FilterInputStream {
    private static final int BUFFER_SIZE = 8;
    private char[] buffer = new char[BUFFER_SIZE];
    private int bufferPos = 0;
    private int bufferSize = 0;

    public BufferedInputStream(InputStream in) {
        super(in);
        System.out.println("  🔄 添加缓冲功能（缓冲区大小: " + BUFFER_SIZE + "）");
    }

    @Override
    public int read() {
        // 如果缓冲区为空，填充缓冲区
        if (bufferPos >= bufferSize) {
            fillBuffer();
            if (bufferSize == 0) {
                return -1;  // 流结束
            }
            bufferPos = 0;
        }
        return buffer[bufferPos++];
    }

    /**
     * 填充缓冲区
     */
    private void fillBuffer() {
        bufferSize = 0;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            int data = in.read();
            if (data == -1) break;
            buffer[i] = (char) data;
            bufferSize++;
        }
        System.out.println("    📥 缓冲区填充: " + bufferSize + " 字节");
    }
}

/**
 * 具体装饰器2：数据输入流
 * 添加读取基本类型的功能
 */
class DataInputStream extends FilterInputStream {
    public DataInputStream(InputStream in) {
        super(in);
        System.out.println("  📊 添加数据类型读取功能");
    }

    /**
     * 读取一行文本
     */
    public String readLine() {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = read()) != -1 && c != '\n') {
            sb.append((char) c);
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    /**
     * 读取整数（简化实现）
     */
    public int readInt() {
        // 简化实现：读取4个字节
        return read() << 24 | read() << 16 | read() << 8 | read();
    }
}

/**
 * 具体装饰器3：计数输入流
 * 统计读取的字节数
 */
class CountingInputStream extends FilterInputStream {
    private int count = 0;

    public CountingInputStream(InputStream in) {
        super(in);
        System.out.println("  🔢 添加计数功能");
    }

    @Override
    public int read() {
        int data = in.read();
        if (data != -1) {
            count++;
        }
        return data;
    }

    public int getCount() {
        return count;
    }
}

/**
 * 具体装饰器4：日志输入流
 * 记录每次读取操作
 */
class LoggingInputStream extends FilterInputStream {
    private int readCount = 0;

    public LoggingInputStream(InputStream in) {
        super(in);
        System.out.println("  📝 添加日志功能");
    }

    @Override
    public int read() {
        int data = in.read();
        readCount++;
        System.out.println("    📝 [日志] 第" + readCount + "次读取: " +
                          (data == -1 ? "EOF" : "'" + (char)data + "'"));
        return data;
    }
}

// ========== 测试 ==========

public class IoStreamDemo {
    public static void main(String[] args) {
        System.out.println("=== 装饰器模式 - I/O流装饰器 ===\n");

        // 示例1：基础流（无装饰）
        System.out.println("【1. 基础流（无装饰）】");
        InputStream in1 = new FileInputStream("data.txt");
        readAndPrint(in1, 5);
        in1.close();

        // 示例2：添加缓冲功能
        System.out.println("\n【2. 添加缓冲功能】");
        InputStream in2 = new FileInputStream("data.txt");
        in2 = new BufferedInputStream(in2);
        readAndPrint(in2, 10);
        in2.close();

        // 示例3：多层装饰
        System.out.println("\n【3. 多层装饰】");
        InputStream in3 = new FileInputStream("data.txt");
        in3 = new BufferedInputStream(in3);
        in3 = new CountingInputStream(in3);
        readAndPrint(in3, 8);
        System.out.println("  📊 总共读取: " + ((CountingInputStream)in3).getCount() + " 字节");
        in3.close();

        // 示例4：添加日志装饰器
        System.out.println("\n【4. 添加日志装饰器】");
        InputStream in4 = new FileInputStream("data.txt");
        in4 = new LoggingInputStream(in4);
        readAndPrint(in4, 5);
        in4.close();

        // 示例5：Java I/O的真实结构
        System.out.println("\n【5. Java I/O的真实结构】");
        demonstrateRealJavaIO();

        // 示例6：装饰器的优势
        System.out.println("\n【6. 装饰器的优势】");
        demonstrateAdvantages();
    }

    /**
     * 读取并打印数据
     */
    static void readAndPrint(InputStream in, int count) {
        System.out.print("  读取数据: ");
        for (int i = 0; i < count; i++) {
            int data = in.read();
            if (data == -1) break;
            System.out.print((char) data);
        }
        System.out.println();
    }

    /**
     * 演示Java I/O的真实结构
     */
    static void demonstrateRealJavaIO() {
        System.out.println("\nJava I/O流的装饰器结构：\n");

        System.out.println("基础流（Component）：");
        System.out.println("  - InputStream (抽象类)");
        System.out.println("  - FileInputStream (具体组件)");
        System.out.println("  - ByteArrayInputStream (具体组件)");

        System.out.println("\n过滤流（Decorator）：");
        System.out.println("  - FilterInputStream (抽象装饰器)");
        System.out.println("    ├─ BufferedInputStream (缓冲)");
        System.out.println("    ├─ DataInputStream (数据类型)");
        System.out.println("    ├─ PushbackInputStream (回退)");
        System.out.println("    └─ LineNumberInputStream (行号)");

        System.out.println("\n典型用法：");
        System.out.println("  InputStream in = new FileInputStream(\"file.txt\");");
        System.out.println("  in = new BufferedInputStream(in);");
        System.out.println("  in = new DataInputStream(in);");

        System.out.println("\n嵌套结构：");
        System.out.println("  DataInputStream");
        System.out.println("    └─ BufferedInputStream");
        System.out.println("         └─ FileInputStream");

        System.out.println("\n✅ 为什么这么设计？");
        System.out.println("  1. 灵活组合：需要缓冲就加BufferedInputStream");
        System.out.println("  2. 单一职责：每个装饰器只负责一个功能");
        System.out.println("  3. 开闭原则：新增功能不修改现有类");
        System.out.println("  4. 避免类爆炸：10个基础流 × 5个功能 = 15个类（而非50个）");
    }

    /**
     * 演示装饰器的优势
     */
    static void demonstrateAdvantages() {
        System.out.println("\n✅ 优势1: 功能组合灵活");
        System.out.println("  只要缓冲: BufferedInputStream(FileInputStream)");
        System.out.println("  只要计数: CountingInputStream(FileInputStream)");
        System.out.println("  两者都要: CountingInputStream(BufferedInputStream(FileInputStream))");

        System.out.println("\n✅ 优势2: 符合开闭原则");
        System.out.println("  新增加密功能：");
        System.out.println("    class EncryptedInputStream extends FilterInputStream { }");
        System.out.println("  无需修改现有代码");

        System.out.println("\n✅ 优势3: 运行时动态组合");
        System.out.println("  可以根据配置动态选择装饰器");

        System.out.println("\n⚠️  注意装饰顺序：");
        System.out.println("  ✅ 正确: DataInputStream(BufferedInputStream(FileInputStream))");
        System.out.println("     先缓冲，再读取数据类型");
        System.out.println("  ❌ 不好: BufferedInputStream(DataInputStream(FileInputStream))");
        System.out.println("     DataInputStream的缓冲效果被浪费");
    }
}
