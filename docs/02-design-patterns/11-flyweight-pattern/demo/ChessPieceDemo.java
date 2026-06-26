/**
 * 享元模式 - 围棋棋子示例
 *
 * 场景：围棋游戏（黑白棋子）
 * 演示：100颗棋子只需要2个对象（享元共享）
 */

import java.util.*;

// ========== 抽象享元 ==========

/**
 * Flyweight：抽象棋子
 */
interface ChessPiece {
    void place(int x, int y);  // 外部状态作为参数传入
}

// ========== 具体享元 ==========

/**
 * ConcreteFlyweight：具体棋子
 */
class ConcreteChessPiece implements ChessPiece {
    private final String color;  // 内部状态（共享，不可变）

    public ConcreteChessPiece(String color) {
        this.color = color;
        System.out.println("  [创建] " + color + "棋子对象");
    }

    @Override
    public void place(int x, int y) {  // 外部状态（位置）
        System.out.println("  " + color + "棋子放在 (" + x + ", " + y + ")");
    }
}

// ========== 享元工厂 ==========

/**
 * FlyweightFactory：享元工厂
 * 管理共享对象池
 */
class ChessPieceFactory {
    private final Map<String, ChessPiece> pieces = new HashMap<>();

    public ChessPiece getChessPiece(String color) {
        // 如果不存在，创建新对象
        if (!pieces.containsKey(color)) {
            pieces.put(color, new ConcreteChessPiece(color));
        }
        // 返回共享对象
        return pieces.get(color);
    }

    public int getPoolSize() {
        return pieces.size();
    }

    public void printPoolInfo() {
        System.out.println("\n【享元池信息】");
        System.out.println("  池中对象数量: " + pieces.size());
        System.out.println("  共享对象: " + pieces.keySet());
    }
}

// ========== 测试 ==========

public class ChessPieceDemo {
    public static void main(String[] args) {
        System.out.println("=== 享元模式 - 围棋棋子 ===\n");

        // 示例1：基本使用
        System.out.println("【1. 基本使用】");
        demonstrateBasicUsage();

        // 示例2：大量棋子
        System.out.println("\n【2. 大量棋子场景】");
        demonstrateManyPieces();

        // 示例3：对比非享元模式
        System.out.println("\n【3. 享元模式 vs 非享元模式】");
        compareWithNonFlyweight();

        // 示例4：内存优化效果
        System.out.println("\n【4. 内存优化效果】");
        demonstrateMemoryOptimization();
    }

    /**
     * 基本使用
     */
    static void demonstrateBasicUsage() {
        ChessPieceFactory factory = new ChessPieceFactory();

        // 获取黑色棋子
        ChessPiece black1 = factory.getChessPiece("黑色");
        black1.place(3, 3);

        // 再次获取黑色棋子（返回共享对象）
        ChessPiece black2 = factory.getChessPiece("黑色");
        black2.place(4, 4);

        // 验证是同一个对象
        System.out.println("\n  验证: black1 == black2? " + (black1 == black2));

        // 获取白色棋子
        ChessPiece white1 = factory.getChessPiece("白色");
        white1.place(3, 4);

        ChessPiece white2 = factory.getChessPiece("白色");
        white2.place(4, 3);

        factory.printPoolInfo();

        System.out.println("\n✅ 关键点:");
        System.out.println("  - 4次获取棋子，只创建了2个对象");
        System.out.println("  - 相同颜色的棋子共享同一个对象");
        System.out.println("  - 位置信息作为外部状态传入");
    }

    /**
     * 大量棋子场景
     */
    static void demonstrateManyPieces() {
        ChessPieceFactory factory = new ChessPieceFactory();

        // 模拟一盘围棋（100颗棋子）
        Random random = new Random(42);
        int totalMoves = 100;

        System.out.println("模拟 " + totalMoves + " 步棋...");

        for (int i = 0; i < totalMoves; i++) {
            String color = (i % 2 == 0) ? "黑色" : "白色";
            int x = random.nextInt(19) + 1;
            int y = random.nextInt(19) + 1;

            ChessPiece piece = factory.getChessPiece(color);
            if (i < 5) {  // 只显示前5步
                piece.place(x, y);
            }
        }

        if (totalMoves > 5) {
            System.out.println("  ...");
        }

        factory.printPoolInfo();

        System.out.println("\n✅ 享元模式的威力:");
        System.out.println("  - " + totalMoves + " 颗棋子");
        System.out.println("  - 只需要 " + factory.getPoolSize() + " 个对象");
        System.out.println("  - 内存占用减少 " + (totalMoves - factory.getPoolSize()) + " 个对象");
    }

    /**
     * 对比非享元模式
     */
    static void compareWithNonFlyweight() {
        System.out.println("❌ 不使用享元模式:");
        System.out.println("```java");
        System.out.println("class ChessPiece {");
        System.out.println("    private String color;  // 每个对象都存储颜色");
        System.out.println("    private int x, y;      // 每个对象都存储位置");
        System.out.println("}");
        System.out.println("");
        System.out.println("// 100颗棋子 = 100个对象");
        System.out.println("for (int i = 0; i < 100; i++) {");
        System.out.println("    new ChessPiece(color, x, y);  // 每次都创建新对象");
        System.out.println("}");
        System.out.println("```");

        System.out.println("\n✅ 使用享元模式:");
        System.out.println("```java");
        System.out.println("class ChessPiece {");
        System.out.println("    private String color;  // 内部状态（共享）");
        System.out.println("    void place(int x, int y) { }  // 外部状态（参数传入）");
        System.out.println("}");
        System.out.println("");
        System.out.println("// 100颗棋子 = 2个对象（黑+白）");
        System.out.println("ChessPiece black = factory.get(\"黑色\");  // 共享");
        System.out.println("ChessPiece white = factory.get(\"白色\");  // 共享");
        System.out.println("```");

        System.out.println("\n📊 对比:");
        System.out.println("  ┌───────────────┬─────────────┬─────────────┐");
        System.out.println("  │ 对比          │ 非享元模式  │ 享元模式    │");
        System.out.println("  ├───────────────┼─────────────┼─────────────┤");
        System.out.println("  │ 对象数量      │ 100个       │ 2个         │");
        System.out.println("  │ 内存占用      │ 高          │ 低          │");
        System.out.println("  │ 创建开销      │ 高          │ 低          │");
        System.out.println("  │ 复杂度        │ 低          │ 中          │");
        System.out.println("  └───────────────┴─────────────┴─────────────┘");
    }

    /**
     * 内存优化效果
     */
    static void demonstrateMemoryOptimization() {
        int[] testCases = {100, 1000, 10000, 100000};

        System.out.println("不同数量棋子的内存优化效果:\n");
        System.out.println("  棋子数量     非享元模式    享元模式    节省比例");
        System.out.println("  ─────────────────────────────────────────────");

        for (int count : testCases) {
            int nonFlyweightObjects = count;
            int flyweightObjects = 2;  // 黑+白
            double savedPercentage = (1 - (double) flyweightObjects / nonFlyweightObjects) * 100;

            System.out.printf("  %-12d %-13d %-11d %.2f%%\n",
                    count, nonFlyweightObjects, flyweightObjects, savedPercentage);
        }

        System.out.println("\n✅ 结论:");
        System.out.println("  - 棋子数量越多，享元模式的优势越明显");
        System.out.println("  - 10万颗棋子：从10万个对象减少到2个对象");
        System.out.println("  - 节省 99.998% 的内存占用");
    }
}
