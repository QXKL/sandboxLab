/**
 * 符合里氏替换原则的示例
 *
 * 解决方案：使用正确的抽象层次，避免错误的继承关系
 */

// ========== 方案1: 使用接口 + 组合（推荐） ==========

/**
 * Shape接口 - 定义所有形状的共同行为
 * 不包含可变状态，只定义查询操作
 */
interface Shape {
    int getArea();
    String getDescription();
}

/**
 * Rectangle - 矩形实现
 * 使用不可变设计，避免状态变化带来的契约问题
 */
class Rectangle implements Shape {
    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("宽度和高度必须大于0");
        }
        this.width = width;
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
    public String getDescription() {
        return "矩形[宽=" + width + ", 高=" + height + ", 面积=" + getArea() + "]";
    }

    /**
     * 创建新的矩形（不修改当前对象）
     */
    public Rectangle withWidth(int newWidth) {
        return new Rectangle(newWidth, this.height);
    }

    public Rectangle withHeight(int newHeight) {
        return new Rectangle(this.width, newHeight);
    }
}

/**
 * Square - 正方形实现
 * 不继承Rectangle，而是独立实现Shape接口
 */
class Square implements Shape {
    private final int side;

    public Square(int side) {
        if (side <= 0) {
            throw new IllegalArgumentException("边长必须大于0");
        }
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
    public String getDescription() {
        return "正方形[边长=" + side + ", 面积=" + getArea() + "]";
    }

    /**
     * 创建新的正方形（不修改当前对象）
     */
    public Square withSide(int newSide) {
        return new Square(newSide);
    }
}

/**
 * Circle - 圆形实现
 * 演示多种形状都可以实现Shape接口
 */
class Circle implements Shape {
    private final int radius;

    public Circle(int radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("半径必须大于0");
        }
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
    public String getDescription() {
        return "圆形[半径=" + radius + ", 面积=" + getArea() + "]";
    }

    public Circle withRadius(int newRadius) {
        return new Circle(newRadius);
    }
}

// ========== 方案2: 只读的继承层次（备选方案） ==========

/**
 * 如果确实需要继承，可以使用只读的基类
 * 关键：不提供setter方法，避免状态变化
 */
abstract class ReadOnlyShape {
    public abstract int getArea();
    public abstract String getType();
}

class ReadOnlyRectangle extends ReadOnlyShape {
    protected final int width;
    protected final int height;

    public ReadOnlyRectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public int getArea() {
        return width * height;
    }

    @Override
    public String getType() {
        return "Rectangle";
    }
}

class ReadOnlySquare extends ReadOnlyRectangle {
    public ReadOnlySquare(int side) {
        super(side, side);  // ✅ 不改变行为，只是特化构造
    }

    @Override
    public String getType() {
        return "Square";
    }
}

// ========== 客户端代码 ==========

public class GoodExample {

    /**
     * 使用Shape接口，所有实现都可以替换
     */
    public static void processShape(Shape shape) {
        System.out.println("  处理形状: " + shape.getDescription());
        System.out.println("  面积: " + shape.getArea());
    }

    /**
     * 演示多态：不同的形状实现可以互相替换
     */
    public static void demonstratePolymorphism() {
        System.out.println("=== 演示：所有Shape实现都可以替换 ===\n");

        Shape[] shapes = {
            new Rectangle(5, 4),
            new Square(5),
            new Circle(3)
        };

        for (Shape shape : shapes) {
            processShape(shape);
            System.out.println();
        }

        System.out.println("✓ 所有形状都遵循Shape接口的契约");
        System.out.println("✓ 可以放心地替换，不会出现意外行为\n");
    }

    /**
     * 演示不可变设计的好处
     */
    public static void demonstrateImmutability() {
        System.out.println("=== 演示：不可变设计避免契约冲突 ===\n");

        Rectangle rect = new Rectangle(5, 4);
        System.out.println("  原始矩形: " + rect.getDescription());

        // 创建新对象而不是修改原对象
        Rectangle newRect = rect.withWidth(10).withHeight(8);
        System.out.println("  修改后的矩形: " + newRect.getDescription());
        System.out.println("  原始矩形不变: " + rect.getDescription());

        System.out.println("\n✓ 不可变对象没有状态变化，不存在契约冲突");
        System.out.println("✓ 线程安全，不需要同步");
        System.out.println("✓ 更容易推理代码行为\n");
    }

    /**
     * 演示只读继承层次
     */
    public static void demonstrateReadOnlyHierarchy() {
        System.out.println("=== 演示：只读继承层次的可替换性 ===\n");

        ReadOnlyShape rect = new ReadOnlyRectangle(5, 4);
        ReadOnlyShape square = new ReadOnlySquare(5);

        System.out.println("  " + rect.getType() + " 面积: " + rect.getArea());
        System.out.println("  " + square.getType() + " 面积: " + square.getArea());

        System.out.println("\n✓ 只读对象可以安全地继承");
        System.out.println("✓ 没有setter，就没有行为契约冲突\n");
    }

    /**
     * 对比违反LSP和遵循LSP的设计
     */
    public static void compareDesigns() {
        System.out.println("=== 设计对比 ===\n");

        System.out.println("❌ 违反LSP的设计（BadExample）：");
        System.out.println("  - Square继承Rectangle");
        System.out.println("  - 重写setWidth/setHeight，改变了父类行为");
        System.out.println("  - 子类不能替换父类");
        System.out.println("  - 多态失效，代码不可预测\n");

        System.out.println("✅ 遵循LSP的设计（GoodExample）：");
        System.out.println("  - 方案1: Rectangle和Square都实现Shape接口");
        System.out.println("  - 方案2: 使用不可变对象，避免状态变化");
        System.out.println("  - 所有实现都遵循接口契约");
        System.out.println("  - 可以安全地替换，多态可靠\n");
    }

    public static void main(String[] args) {
        System.out.println("【改进方案】符合里氏替换原则的设计：\n");
        System.out.println("核心思想：");
        System.out.println("✓ 接口定义契约，所有实现必须遵守");
        System.out.println("✓ 使用不可变对象，避免状态变化导致的契约冲突");
        System.out.println("✓ 组合优于继承，避免错误的继承关系");
        System.out.println("✓ 子类可以扩展功能，但不能改变已有行为\n");
        System.out.println("=".repeat(60) + "\n");

        // 演示1：多态的可靠性
        demonstratePolymorphism();
        System.out.println("=".repeat(60) + "\n");

        // 演示2：不可变设计
        demonstrateImmutability();
        System.out.println("=".repeat(60) + "\n");

        // 演示3：只读继承
        demonstrateReadOnlyHierarchy();
        System.out.println("=".repeat(60) + "\n");

        // 对比总结
        compareDesigns();
        System.out.println("=".repeat(60) + "\n");

        System.out.println("💡 关键启示：");
        System.out.println("1. 数学/概念上的\"is-a\"不等于代码上的\"is-a\"");
        System.out.println("2. 继承是强耦合关系，要谨慎使用");
        System.out.println("3. 优先使用接口和组合，而非继承");
        System.out.println("4. 不可变对象天然符合LSP");
        System.out.println("5. 子类必须能够替换父类，这是多态的前提\n");

        System.out.println("📊 设计原则：");
        System.out.println("✓ 契约式设计：明确定义前置条件、后置条件、不变式");
        System.out.println("✓ 前置条件不强化：子类的输入要求不能更严格");
        System.out.println("✓ 后置条件不弱化：子类的输出保证不能更弱");
        System.out.println("✓ 不变式必须保持：父类的约束在子类中依然成立\n");

        System.out.println("🎯 记住：子类必须能够替换父类，而不改变程序的正确性！");
    }
}
