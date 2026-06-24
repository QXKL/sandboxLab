/**
 * 简单工厂模式 - 图形绘制系统
 *
 * 场景：图形绘制工具，支持多种图形类型
 * 演示：简单工厂如何封装对象创建逻辑
 */

// ========== 产品接口 ==========

interface Shape {
    void draw();
    double getArea();
}

// ========== 具体产品 ==========

class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.println("绘制圆形，半径=" + radius);
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

class Rectangle implements Shape {
    private double width;
    private double height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw() {
        System.out.println("绘制矩形，宽=" + width + ", 高=" + height);
    }

    @Override
    public double getArea() {
        return width * height;
    }
}

class Triangle implements Shape {
    private double base;
    private double height;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public void draw() {
        System.out.println("绘制三角形，底=" + base + ", 高=" + height);
    }

    @Override
    public double getArea() {
        return 0.5 * base * height;
    }
}

// ========== 简单工厂 ==========

class ShapeFactory {
    /**
     * 根据类型创建图形对象
     *
     * @param type 图形类型（circle, rectangle, triangle）
     * @param params 创建参数（圆形:半径, 矩形:宽高, 三角形:底高）
     * @return 图形对象
     */
    public static Shape createShape(String type, double... params) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("图形类型不能为空");
        }

        switch (type.toLowerCase()) {
            case "circle":
                if (params.length < 1) {
                    throw new IllegalArgumentException("圆形需要1个参数：半径");
                }
                return new Circle(params[0]);

            case "rectangle":
                if (params.length < 2) {
                    throw new IllegalArgumentException("矩形需要2个参数：宽、高");
                }
                return new Rectangle(params[0], params[1]);

            case "triangle":
                if (params.length < 2) {
                    throw new IllegalArgumentException("三角形需要2个参数：底、高");
                }
                return new Triangle(params[0], params[1]);

            default:
                throw new IllegalArgumentException("不支持的图形类型: " + type);
        }
    }
}

// ========== 客户端 ==========

public class SimpleFactoryDemo {
    public static void main(String[] args) {
        System.out.println("=== 简单工厂模式示例 ===\n");

        // 对比1：不使用工厂 vs 使用工厂
        System.out.println("【1. 不使用工厂的问题】");
        badExample();

        System.out.println("\n【2. 使用简单工厂的改进】");
        goodExample();

        System.out.println("\n【3. 扩展性测试】");
        extensionExample();
    }

    /**
     * 不使用工厂：创建逻辑分散，难以维护
     */
    static void badExample() {
        // 场景1：创建图形
        String type1 = "circle";
        Shape shape1 = null;
        if ("circle".equals(type1)) {
            shape1 = new Circle(5.0);
        } else if ("rectangle".equals(type1)) {
            shape1 = new Rectangle(4.0, 6.0);
        }
        if (shape1 != null) {
            shape1.draw();
        }

        // 场景2：又要创建图形（重复逻辑）
        String type2 = "rectangle";
        Shape shape2 = null;
        if ("circle".equals(type2)) {
            shape2 = new Circle(5.0);
        } else if ("rectangle".equals(type2)) {
            shape2 = new Rectangle(4.0, 6.0);
        }
        if (shape2 != null) {
            shape2.draw();
        }

        System.out.println("  ❌ 创建逻辑重复，修改困难");
    }

    /**
     * 使用简单工厂：创建逻辑集中，易于维护
     */
    static void goodExample() {
        // 场景1：创建图形
        Shape shape1 = ShapeFactory.createShape("circle", 5.0);
        shape1.draw();
        System.out.println("  面积: " + String.format("%.2f", shape1.getArea()));

        // 场景2：创建图形（代码简洁）
        Shape shape2 = ShapeFactory.createShape("rectangle", 4.0, 6.0);
        shape2.draw();
        System.out.println("  面积: " + String.format("%.2f", shape2.getArea()));

        // 场景3：创建图形
        Shape shape3 = ShapeFactory.createShape("triangle", 3.0, 4.0);
        shape3.draw();
        System.out.println("  面积: " + String.format("%.2f", shape3.getArea()));

        System.out.println("  ✅ 客户端代码简洁，易于理解");
    }

    /**
     * 扩展性测试
     */
    static void extensionExample() {
        // 批量创建不同图形
        String[] types = {"circle", "rectangle", "triangle", "circle"};
        double[][] params = {
            {10.0},
            {5.0, 8.0},
            {6.0, 7.0},
            {3.0}
        };

        System.out.println("批量创建图形：");
        for (int i = 0; i < types.length; i++) {
            Shape shape = ShapeFactory.createShape(types[i], params[i]);
            System.out.print("  " + (i + 1) + ". ");
            shape.draw();
        }

        // 异常处理
        System.out.println("\n错误处理测试：");
        try {
            ShapeFactory.createShape("pentagon", 5.0);
        } catch (IllegalArgumentException e) {
            System.out.println("  ⚠️  " + e.getMessage());
        }

        try {
            ShapeFactory.createShape("circle");
        } catch (IllegalArgumentException e) {
            System.out.println("  ⚠️  " + e.getMessage());
        }
    }
}
