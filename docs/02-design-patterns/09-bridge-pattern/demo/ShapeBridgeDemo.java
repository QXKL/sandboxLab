/**
 * 桥接模式 - 形状与颜色示例
 *
 * 场景：形状（圆形、矩形）与颜色（红色、蓝色）分离
 * 演示：避免继承爆炸（4个类 vs 12个类）
 */

// ========== 实现接口 ==========

/**
 * 实现接口：颜色
 */
interface Color {
    void applyColor();
}

// ========== 具体实现 ==========

/**
 * 具体实现：红色
 */
class Red implements Color {
    @Override
    public void applyColor() {
        System.out.print("红色");
    }
}

/**
 * 具体实现：蓝色
 */
class Blue implements Color {
    @Override
    public void applyColor() {
        System.out.print("蓝色");
    }
}

/**
 * 具体实现：绿色
 */
class Green implements Color {
    @Override
    public void applyColor() {
        System.out.print("绿色");
    }
}

// ========== 抽象类 ==========

/**
 * 抽象类：形状
 */
abstract class Shape {
    protected Color color;  // 桥接：持有颜色引用

    public Shape(Color color) {
        this.color = color;
    }

    abstract void draw();
}

// ========== 扩充抽象类 ==========

/**
 * 扩充抽象：圆形
 */
class Circle extends Shape {
    public Circle(Color color) {
        super(color);
    }

    @Override
    public void draw() {
        System.out.print("  🔵 绘制圆形，颜色: ");
        color.applyColor();
        System.out.println();
    }
}

/**
 * 扩充抽象：矩形
 */
class Rectangle extends Shape {
    public Rectangle(Color color) {
        super(color);
    }

    @Override
    public void draw() {
        System.out.print("  ▭  绘制矩形，颜色: ");
        color.applyColor();
        System.out.println();
    }
}

/**
 * 扩充抽象：三角形
 */
class Triangle extends Shape {
    public Triangle(Color color) {
        super(color);
    }

    @Override
    public void draw() {
        System.out.print("  △  绘制三角形，颜色: ");
        color.applyColor();
        System.out.println();
    }
}

// ========== 测试 ==========

public class ShapeBridgeDemo {
    public static void main(String[] args) {
        System.out.println("=== 桥接模式 - 形状与颜色 ===\n");

        // 示例1：不同形状和颜色的组合
        System.out.println("【1. 形状与颜色的组合】");
        Shape redCircle = new Circle(new Red());
        redCircle.draw();

        Shape blueRectangle = new Rectangle(new Blue());
        blueRectangle.draw();

        Shape greenTriangle = new Triangle(new Green());
        greenTriangle.draw();

        // 示例2：展示所有组合
        System.out.println("\n【2. 展示所有组合】");
        demonstrateAllCombinations();

        // 示例3：对比继承方式
        System.out.println("\n【3. 桥接 vs 继承】");
        compareWithInheritance();
    }

    /**
     * 展示所有组合
     */
    static void demonstrateAllCombinations() {
        Color[] colors = {new Red(), new Blue(), new Green()};
        String[] colorNames = {"红色", "蓝色", "绿色"};

        for (int i = 0; i < colors.length; i++) {
            System.out.println("\n" + colorNames[i] + "系列:");
            new Circle(colors[i]).draw();
            new Rectangle(colors[i]).draw();
            new Triangle(colors[i]).draw();
        }
    }

    /**
     * 对比继承方式
     */
    static void compareWithInheritance() {
        System.out.println("❌ 如果用继承实现:");
        System.out.println("  - RedCircle, BlueCircle, GreenCircle");
        System.out.println("  - RedRectangle, BlueRectangle, GreenRectangle");
        System.out.println("  - RedTriangle, BlueTriangle, GreenTriangle");
        System.out.println("  → 3种形状 × 3种颜色 = 9个类");
        System.out.println("  → 如果5种形状 × 10种颜色 = 50个类！");

        System.out.println("\n✅ 桥接模式:");
        System.out.println("  形状: Circle, Rectangle, Triangle (3个)");
        System.out.println("  颜色: Red, Blue, Green (3个)");
        System.out.println("  → 3 + 3 = 6个类");
        System.out.println("  → 5种形状 + 10种颜色 = 15个类");

        System.out.println("\n📊 类数量对比:");
        System.out.println("  ┌─────────┬──────────┬──────────┐");
        System.out.println("  │ 组合    │ 继承方式 │ 桥接模式 │");
        System.out.println("  ├─────────┼──────────┼──────────┤");
        System.out.println("  │ 3 × 3   │   9类    │   6类    │");
        System.out.println("  │ 5 × 10  │  50类    │  15类    │");
        System.out.println("  │ 10 × 20 │ 200类    │  30类    │");
        System.out.println("  └─────────┴──────────┴──────────┘");

        System.out.println("\n✅ 桥接模式的优势:");
        System.out.println("  - 类数量从 N×M 减少到 N+M");
        System.out.println("  - 形状和颜色独立扩展");
        System.out.println("  - 新增形状不影响颜色");
        System.out.println("  - 新增颜色不影响形状");
    }
}
