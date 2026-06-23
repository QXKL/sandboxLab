/**
 * 违反里氏替换原则的示例
 *
 * 问题：正方形继承矩形，看似合理（数学上正方形是特殊的矩形）
 * 但在代码中，Square改变了Rectangle的行为契约，导致不能替换
 */

// ========== 父类：矩形 ==========
class Rectangle {
    protected int width;
    protected int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // 契约：设置宽度不影响高度
    public void setWidth(int width) {
        this.width = width;
    }

    // 契约：设置高度不影响宽度
    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getArea() {
        return width * height;
    }

    @Override
    public String toString() {
        return "矩形[宽=" + width + ", 高=" + height + ", 面积=" + getArea() + "]";
    }
}

// ========== 子类：正方形 ==========
/**
 * 问题：为了保持"正方形宽高相等"的特性，
 * Square重写了setWidth和setHeight，破坏了父类的契约
 */
class Square extends Rectangle {

    public Square(int side) {
        super(side, side);
    }

    // ❌ 违反契约：设置宽度会同时改变高度
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // 为了保持正方形特性
    }

    // ❌ 违反契约：设置高度会同时改变宽度
    @Override
    public void setHeight(int height) {
        this.width = height;  // 为了保持正方形特性
        this.height = height;
    }

    @Override
    public String toString() {
        return "正方形[边长=" + width + ", 面积=" + getArea() + "]";
    }
}

// ========== 客户端代码 ==========
/**
 * 客户端期望Rectangle的行为：宽高可以独立设置
 */
public class BadExample {

    /**
     * 这个方法期望Rectangle的行为：
     * 1. setWidth(5) 只改变宽度
     * 2. setHeight(4) 只改变高度
     * 3. 最终面积是 5 * 4 = 20
     */
    public static void resizeRectangle(Rectangle rect) {
        System.out.println("  [操作前] " + rect);

        rect.setWidth(5);
        System.out.println("  [设置宽度=5] " + rect);

        rect.setHeight(4);
        System.out.println("  [设置高度=4] " + rect);

        // 期望面积是 5 * 4 = 20
        int expectedArea = 20;
        int actualArea = rect.getArea();

        if (actualArea == expectedArea) {
            System.out.println("  ✓ 面积正确: " + actualArea);
        } else {
            System.out.println("  ❌ 面积错误: 期望=" + expectedArea + ", 实际=" + actualArea);
        }
    }

    public static void main(String[] args) {
        System.out.println("【问题演示】违反里氏替换原则的后果：\n");

        System.out.println("=== 测试1: 使用Rectangle（父类） ===");
        Rectangle rectangle = new Rectangle(3, 3);
        resizeRectangle(rectangle);
        System.out.println("  结果: ✅ 符合预期，面积是20\n");

        System.out.println("=== 测试2: 使用Square（子类） ===");
        Square square = new Square(3);
        resizeRectangle(square);  // ❌ 子类不能替换父类
        System.out.println("  结果: ❌ 不符合预期，面积是16（4*4）而不是20\n");

        System.out.println("=".repeat(60));
        System.out.println("\n💡 问题分析：");
        System.out.println("1. Rectangle的契约：setWidth不影响height，setHeight不影响width");
        System.out.println("2. Square违反了这个契约：为了保持\"宽高相等\"，修改width会同时修改height");
        System.out.println("3. 当Square替换Rectangle时，客户端代码的行为发生了变化");
        System.out.println("4. 这违反了LSP：子类不能可靠地替换父类\n");

        System.out.println("❌ 违反LSP的后果：");
        System.out.println("- 代码不可预测：相同的操作，传入子类时行为不同");
        System.out.println("- 难以扩展：每增加一个子类，都要检查是否破坏原有逻辑");
        System.out.println("- 多态失效：不能放心地用父类引用指向子类对象");
        System.out.println("- 测试困难：需要针对每个子类编写特殊的测试用例\n");

        System.out.println("🤔 思考：");
        System.out.println("- 为什么数学上\"正方形是矩形\"，代码上就不能这样设计？");
        System.out.println("- 问题的根源是什么？（提示：可变状态 + 行为约定）");
        System.out.println("- 如何重构才能符合LSP？\n");

        System.out.println("➡️  运行 GoodExample.java 查看正确的设计方案");
    }
}
