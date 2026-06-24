/**
 * 符合里氏替换原则的示例
 *
 * 解决方案：取消 Square 继承 Rectangle 的关系
 * - 虽然数学上"正方形是矩形"，但 OOP 中这个继承关系会破坏行为契约
 * - 重新设计：让 Rectangle 和 Square 都实现共同的 Shape 接口
 * - 各自有独立的行为契约，互不影响
 */

// ========== 抽象层 ==========
/**
 * Shape 接口 - 定义图形的共同行为
 *
 * 契约：
 * - 所有图形都能计算面积
 */
interface Shape {
    int getArea();
    String getName();
}

// ========== 具体实现 ==========
/**
 * Rectangle - 矩形
 *
 * 契约：
 * - 宽度和高度可以独立设置
 * - 面积 = 宽度 × 高度
 */
class Rectangle implements Shape {
    private int width;
    private int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

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

    @Override
    public int getArea() {
        return width * height;
    }

    @Override
    public String getName() {
        return "矩形";
    }

    @Override
    public String toString() {
        return "Rectangle{width=" + width + ", height=" + height + ", area=" + getArea() + "}";
    }
}

/**
 * Square - 正方形
 *
 * 契约：
 * - 边长必须相等（这是正方形自己的契约，不影响其他类）
 * - 面积 = 边长²
 *
 * 重要：Square 不再继承 Rectangle，各自独立
 */
class Square implements Shape {
    private int side;

    public Square(int side) {
        this.side = side;
    }

    /**
     * 设置边长
     * 注意：这是 Square 自己的方法，不会与 Rectangle 的 setWidth/setHeight 冲突
     */
    public void setSide(int side) {
        this.side = side;
    }

    public int getSide() {
        return side;
    }

    @Override
    public int getArea() {
        return side * side;
    }

    @Override
    public String getName() {
        return "正方形";
    }

    @Override
    public String toString() {
        return "Square{side=" + side + ", area=" + getArea() + "}";
    }
}

/**
 * Circle - 圆形（演示扩展性）
 *
 * 新增图形时，不影响 Rectangle 和 Square
 */
class Circle implements Shape {
    private int radius;

    public Circle(int radius) {
        this.radius = radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public int getArea() {
        return (int) (Math.PI * radius * radius);
    }

    @Override
    public String getName() {
        return "圆形";
    }

    @Override
    public String toString() {
        return "Circle{radius=" + radius + ", area=" + getArea() + "}";
    }
}

// ========== 客户端代码 ==========
/**
 * ShapeProcessor - 图形处理器
 *
 * 关键：依赖 Shape 接口，不依赖具体实现
 * - 所有 Shape 的实现都能正确替换
 * - 符合里氏替换原则
 */
class ShapeProcessor {
    /**
     * 打印图形信息
     *
     * 任何 Shape 的实现都能正确工作
     */
    public void printShapeInfo(Shape shape) {
        System.out.println(shape.getName() + ": " + shape);
        System.out.println("  面积: " + shape.getArea());
    }

    /**
     * 批量处理图形
     */
    public void processShapes(Shape[] shapes) {
        System.out.println("批量处理 " + shapes.length + " 个图形:");
        for (Shape shape : shapes) {
            printShapeInfo(shape);
        }
    }
}

// ========== 运行示例 ==========
public class GoodExample {
    public static void main(String[] args) {
        System.out.println("=== 符合里氏替换原则的示例 ===\n");

        // 创建各种图形
        Rectangle rect = new Rectangle(5, 4);
        Square square = new Square(4);
        Circle circle = new Circle(3);

        System.out.println("【创建图形】");
        System.out.println("矩形: 宽=5, 高=4");
        System.out.println("正方形: 边长=4");
        System.out.println("圆形: 半径=3");

        // 使用 Shape 接口统一处理
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【使用 Shape 接口统一处理】");
        System.out.println("=".repeat(60) + "\n");

        ShapeProcessor processor = new ShapeProcessor();

        // 所有图形都能正确工作（符合 LSP）
        processor.printShapeInfo(rect);
        System.out.println();
        processor.printShapeInfo(square);
        System.out.println();
        processor.printShapeInfo(circle);

        // 批量处理
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【批量处理】");
        System.out.println("=".repeat(60) + "\n");

        Shape[] shapes = {rect, square, circle};
        processor.processShapes(shapes);

        // 演示各自独立的行为
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【各自独立的行为】");
        System.out.println("=".repeat(60) + "\n");

        System.out.println("矩形：可以独立设置宽和高");
        rect.setWidth(10);
        System.out.println("  设置宽度为 10: " + rect);
        rect.setHeight(3);
        System.out.println("  设置高度为 3: " + rect);

        System.out.println("\n正方形：只能设置边长");
        square.setSide(6);
        System.out.println("  设置边长为 6: " + square);

        System.out.println("\n圆形：只能设置半径");
        circle.setRadius(5);
        System.out.println("  设置半径为 5: " + circle);

        // 优势总结
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 符合里氏替换原则的优势");
        System.out.println("=".repeat(60));

        System.out.println("\n【行为一致性】");
        System.out.println("✓ Rectangle 和 Square 各自有独立的契约");
        System.out.println("✓ 不会互相干扰");
        System.out.println("✓ 客户端通过 Shape 接口统一使用");

        System.out.println("\n【可替换性】");
        System.out.println("✓ 所有 Shape 的实现都能正确替换");
        System.out.println("✓ 客户端不需要知道具体类型");
        System.out.println("✓ 不需要 instanceof 判断");

        System.out.println("\n【扩展性】");
        System.out.println("✓ 新增图形（如 Circle）不影响现有代码");
        System.out.println("✓ 符合开闭原则（对扩展开放，对修改关闭）");

        System.out.println("\n【设计清晰】");
        System.out.println("✓ Rectangle 专注于矩形的行为");
        System.out.println("✓ Square 专注于正方形的行为");
        System.out.println("✓ 职责清晰，易于维护");

        // 对比 BadExample
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【对比 BadExample】");
        System.out.println("=".repeat(60));

        System.out.println("\nBadExample（违反LSP）:");
        System.out.println("  ✗ Square 继承 Rectangle");
        System.out.println("  ✗ Square 破坏了 Rectangle 的契约");
        System.out.println("  ✗ 客户端代码用 Square 替换 Rectangle 会出错");
        System.out.println("  ✗ 需要 instanceof 判断并特殊处理");

        System.out.println("\nGoodExample（符合LSP）:");
        System.out.println("  ✓ Rectangle 和 Square 独立实现 Shape");
        System.out.println("  ✓ 各自有明确的契约，互不干扰");
        System.out.println("  ✓ 客户端代码使用任何 Shape 都正常工作");
        System.out.println("  ✓ 不需要特殊判断，代码简洁清晰");

        System.out.println("\n🎯 核心教训：");
        System.out.println("概念上的 is-a 关系 ≠ 行为上的 behaves-like-a 关系");
        System.out.println("继承要看行为契约，而非概念关系！");
    }
}
