/**
 * 违反里氏替换原则的示例：正方形-矩形问题
 *
 * 核心问题：
 * - 数学上"正方形是矩形"，所以 Square 继承 Rectangle 看起来合理
 * - 但 Square 破坏了 Rectangle 的行为契约
 * - 导致 Square 不能替换 Rectangle
 */

/**
 * Rectangle - 矩形类
 *
 * 契约（承诺的行为）：
 * - 宽度和高度可以独立设置
 * - setWidth() 只改变宽度，不影响高度
 * - setHeight() 只改变高度，不影响宽度
 */
class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) {
        this.width = width;
    }

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
        return "Rectangle{width=" + width + ", height=" + height + ", area=" + getArea() + "}";
    }
}

/**
 * Square - 正方形类
 *
 * 问题：继承了 Rectangle，但破坏了父类的契约
 * - 宽度和高度必须相等
 * - setWidth() 会同时改变 height
 * - setHeight() 会同时改变 width
 * - 违反了"宽高可独立设置"的契约
 */
class Square extends Rectangle {
    /**
     * 设置宽度：强制宽高相等
     *
     * ❌ 违反契约：父类承诺只改变宽度，但这里同时改变了高度
     */
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // 强制高度等于宽度
    }

    /**
     * 设置高度：强制宽高相等
     *
     * ❌ 违反契约：父类承诺只改变高度，但这里同时改变了宽度
     */
    @Override
    public void setHeight(int height) {
        this.width = height;  // 强制宽度等于高度
        this.height = height;
    }

    @Override
    public String toString() {
        return "Square{side=" + width + ", area=" + getArea() + "}";
    }
}

/**
 * 运行示例
 */
public class BadExample {
    public static void main(String[] args) {
        System.out.println("=== 违反里氏替换原则的示例 ===\n");

        // 测试 Rectangle
        System.out.println("【测试1】使用 Rectangle:");
        Rectangle rect = new Rectangle();
        testResize(rect);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // 测试 Square：期望能替换 Rectangle，但失败了
        System.out.println("【测试2】用 Square 替换 Rectangle:");
        Rectangle square = new Square();
        testResize(square);

        // 问题分析
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💥 问题分析");
        System.out.println("=".repeat(60));

        System.out.println("\n【Rectangle 的契约】");
        System.out.println("✓ 宽度和高度可以独立设置");
        System.out.println("✓ setWidth(5) 只改变宽度，不影响高度");
        System.out.println("✓ setHeight(4) 只改变高度，不影响宽度");
        System.out.println("✓ 期望面积 = 5 × 4 = 20");

        System.out.println("\n【Square 破坏了这个契约】");
        System.out.println("✗ 宽度和高度必须相等");
        System.out.println("✗ setWidth(5) 会同时将高度改为 5");
        System.out.println("✗ setHeight(4) 会同时将宽度改为 4");
        System.out.println("✗ 最终面积 = 4 × 4 = 16（不符合预期）");

        System.out.println("\n【违反了里氏替换原则】");
        System.out.println("✗ Square 不能替换 Rectangle");
        System.out.println("✗ 客户端代码期望矩形的行为，正方形无法满足");
        System.out.println("✗ 虽然概念上"正方形是矩形"，但行为不一致");

        // 更多问题演示
        System.out.println("\n" + "=".repeat(60));
        System.out.println("更多问题演示");
        System.out.println("=".repeat(60));

        System.out.println("\n【场景1】批量调整尺寸");
        Rectangle[] shapes = {new Rectangle(), new Square()};
        for (int i = 0; i < shapes.length; i++) {
            shapes[i].setWidth(10);
            shapes[i].setHeight(5);
            System.out.println("形状" + (i+1) + ": " + shapes[i]);
            System.out.println("  期望面积: 50, 实际面积: " + shapes[i].getArea());
            if (shapes[i].getArea() != 50) {
                System.out.println("  ❌ 不符合预期！");
            }
        }

        System.out.println("\n【场景2】需要特殊判断（代码坏味道）");
        System.out.println("// 客户端代码不得不这样写：");
        System.out.println("if (shape instanceof Square) {");
        System.out.println("    // 特殊处理正方形");
        System.out.println("} else {");
        System.out.println("    // 处理矩形");
        System.out.println("}");
        System.out.println("这说明子类不能透明替换父类，违反了 LSP！");

        System.out.println("\n➡️  运行 GoodExample.java 查看如何解决这些问题");
    }

    /**
     * 调整矩形尺寸的方法
     *
     * 契约期望：
     * - 可以独立设置宽度和高度
     * - setWidth(5) 后宽度为 5
     * - setHeight(4) 后高度为 4
     * - 最终面积 = 5 × 4 = 20
     */
    static void testResize(Rectangle rect) {
        System.out.println("初始状态: " + rect);

        // 设置宽度
        rect.setWidth(5);
        System.out.println("设置宽度为 5: " + rect);
        System.out.println("  期望: width=5, height=0");
        System.out.println("  实际: width=" + rect.getWidth() + ", height=" + rect.getHeight());

        // 设置高度
        rect.setHeight(4);
        System.out.println("设置高度为 4: " + rect);
        System.out.println("  期望: width=5, height=4, area=20");
        System.out.println("  实际: width=" + rect.getWidth() +
                         ", height=" + rect.getHeight() +
                         ", area=" + rect.getArea());

        // 验证
        int expectedArea = 20;
        int actualArea = rect.getArea();
        if (actualArea == expectedArea) {
            System.out.println("✓ 测试通过：面积符合预期");
        } else {
            System.out.println("✗ 测试失败：期望面积 " + expectedArea +
                             ", 实际面积 " + actualArea);
            System.out.println("  原因：子类破坏了父类的行为契约");
        }
    }
}
