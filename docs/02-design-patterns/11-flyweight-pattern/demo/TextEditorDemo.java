/**
 * 享元模式 - 文本编辑器示例
 *
 * 场景：文本编辑器（字符样式）
 * 演示：1000个字符，样式对象大幅减少
 */

import java.util.*;

// ========== 抽象享元 ==========

/**
 * Flyweight：字符样式（享元对象）
 */
class CharacterStyle {
    private final String font;   // 字体
    private final int size;      // 大小
    private final String color;  // 颜色

    public CharacterStyle(String font, int size, String color) {
        this.font = font;
        this.size = size;
        this.color = color;
        System.out.println("  [创建样式] " + this);
    }

    public void applyStyle(char character, int position) {
        // 外部状态：字符内容、位置
        System.out.printf("  位置%d: '%c' [%s, %dpt, %s]\n",
                position, character, font, size, color);
    }

    @Override
    public String toString() {
        return font + "-" + size + "pt-" + color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterStyle)) return false;
        CharacterStyle that = (CharacterStyle) o;
        return size == that.size &&
               Objects.equals(font, that.font) &&
               Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, size, color);
    }
}

// ========== 享元工厂 ==========

/**
 * FlyweightFactory：样式工厂
 */
class StyleFactory {
    private final Map<String, CharacterStyle> styles = new HashMap<>();

    public CharacterStyle getStyle(String font, int size, String color) {
        String key = font + "-" + size + "-" + color;

        if (!styles.containsKey(key)) {
            styles.put(key, new CharacterStyle(font, size, color));
        }

        return styles.get(key);
    }

    public int getPoolSize() {
        return styles.size();
    }

    public void printPoolInfo() {
        System.out.println("\n【样式池信息】");
        System.out.println("  池中样式数量: " + styles.size());
        System.out.println("  样式列表:");
        styles.values().forEach(style -> System.out.println("    - " + style));
    }
}

// ========== 字符（包含外部状态） ==========

/**
 * Character：文本字符
 * 包含外部状态：字符内容、位置
 */
class TextCharacter {
    private final char content;           // 外部状态：字符内容
    private final int position;           // 外部状态：位置
    private final CharacterStyle style;   // 内部状态：样式（共享）

    public TextCharacter(char content, int position, CharacterStyle style) {
        this.content = content;
        this.position = position;
        this.style = style;
    }

    public void render() {
        style.applyStyle(content, position);
    }
}

// ========== 文本编辑器 ==========

/**
 * TextEditor：文本编辑器
 */
class TextEditor {
    private final List<TextCharacter> characters = new ArrayList<>();
    private final StyleFactory styleFactory = new StyleFactory();

    public void addText(String text, String font, int size, String color) {
        CharacterStyle style = styleFactory.getStyle(font, size, color);

        for (int i = 0; i < text.length(); i++) {
            characters.add(new TextCharacter(
                    text.charAt(i),
                    characters.size(),
                    style
            ));
        }
    }

    public void render() {
        System.out.println("\n【渲染文本】");
        for (TextCharacter character : characters) {
            character.render();
        }
    }

    public void printStatistics() {
        System.out.println("\n【统计信息】");
        System.out.println("  字符总数: " + characters.size());
        System.out.println("  样式对象数: " + styleFactory.getPoolSize());
        System.out.println("  对象复用率: " +
                String.format("%.2f%%",
                        (1 - (double) styleFactory.getPoolSize() / characters.size()) * 100));
    }

    public StyleFactory getStyleFactory() {
        return styleFactory;
    }
}

// ========== 测试 ==========

public class TextEditorDemo {
    public static void main(String[] args) {
        System.out.println("=== 享元模式 - 文本编辑器 ===\n");

        // 示例1：基本使用
        System.out.println("【1. 基本文本编辑】");
        demonstrateBasicUsage();

        // 示例2：大量文本
        System.out.println("\n【2. 大量文本场景】");
        demonstrateLargeText();

        // 示例3：样式复用效果
        System.out.println("\n【3. 样式复用效果】");
        demonstrateStyleReuse();

        // 示例4：内存对比
        System.out.println("\n【4. 内存优化效果】");
        compareMemoryUsage();
    }

    /**
     * 基本使用
     */
    static void demonstrateBasicUsage() {
        TextEditor editor = new TextEditor();

        System.out.println("添加文本...");
        editor.addText("Hello", "Arial", 12, "黑色");
        editor.addText(" ", "Arial", 12, "黑色");
        editor.addText("World", "Arial", 14, "红色");

        editor.render();
        editor.getStyleFactory().printPoolInfo();
        editor.printStatistics();

        System.out.println("\n✅ 关键点:");
        System.out.println("  - 11个字符，只需要2个样式对象");
        System.out.println("  - \"Hello \" 共享同一个样式");
        System.out.println("  - \"World\" 使用另一个样式");
    }

    /**
     * 大量文本
     */
    static void demonstrateLargeText() {
        TextEditor editor = new TextEditor();

        System.out.println("模拟编辑大量文本...");

        // 标题
        editor.addText("文档标题", "宋体", 18, "黑色");
        editor.addText("\n", "宋体", 12, "黑色");

        // 正文段落1
        editor.addText("这是第一段正文内容。", "宋体", 12, "黑色");
        editor.addText("这里有重要提示。", "宋体", 12, "红色");
        editor.addText("继续正文内容。\n", "宋体", 12, "黑色");

        // 正文段落2
        editor.addText("这是第二段正文。", "宋体", 12, "黑色");
        editor.addText("加粗强调部分。", "宋体", 14, "黑色");
        editor.addText("结尾。", "宋体", 12, "黑色");

        // 不显示所有字符渲染（太长）
        // editor.render();

        editor.getStyleFactory().printPoolInfo();
        editor.printStatistics();

        System.out.println("\n✅ 享元效果:");
        System.out.println("  - 大量字符共享少量样式对象");
        System.out.println("  - 内存占用大幅减少");
    }

    /**
     * 样式复用效果
     */
    static void demonstrateStyleReuse() {
        TextEditor editor = new TextEditor();

        // 添加重复样式的文本
        String[] words = {"Java", "Python", "Go", "Rust", "JavaScript"};
        String[] styles = {
                "Arial-12-黑色",
                "Arial-14-红色",
                "宋体-12-蓝色"
        };

        System.out.println("添加使用不同样式的文本...");

        for (String word : words) {
            for (String styleStr : styles) {
                String[] parts = styleStr.split("-");
                editor.addText(word + " ", parts[0], Integer.parseInt(parts[1]), parts[2]);
            }
        }

        editor.getStyleFactory().printPoolInfo();
        editor.printStatistics();

        int totalChars = editor.characters.size();
        int totalStyles = editor.getStyleFactory().getPoolSize();

        System.out.println("\n✅ 复用效果:");
        System.out.println("  - 字符数: " + totalChars);
        System.out.println("  - 样式数: " + totalStyles);
        System.out.println("  - 平均每个样式被复用: " + (totalChars / totalStyles) + " 次");
    }

    /**
     * 内存对比
     */
    static void compareMemoryUsage() {
        System.out.println("假设每个对象占 100 字节:\n");

        int[] charCounts = {100, 1000, 10000, 100000};
        int avgStyles = 10;  // 平均样式数量

        System.out.println("  字符数      非享元模式(KB)  享元模式(KB)    节省(KB)     节省比例");
        System.out.println("  ─────────────────────────────────────────────────────────────────");

        for (int count : charCounts) {
            int nonFlyweightMemory = count * 100 / 1024;  // 每个字符一个完整对象
            int flyweightMemory = (count * 20 + avgStyles * 100) / 1024;  // 字符小对象+共享样式
            int saved = nonFlyweightMemory - flyweightMemory;
            double savedPercentage = (double) saved / nonFlyweightMemory * 100;

            System.out.printf("  %-11d %-17d %-15d %-12d %.2f%%\n",
                    count, nonFlyweightMemory, flyweightMemory, saved, savedPercentage);
        }

        System.out.println("\n说明:");
        System.out.println("  - 非享元: 每个字符包含完整样式信息(100字节)");
        System.out.println("  - 享元: 字符只包含引用(20字节) + 共享样式对象");
        System.out.println("  - 假设平均有10种不同样式");

        System.out.println("\n✅ 结论:");
        System.out.println("  - 10万字符：节省约 8.7MB 内存");
        System.out.println("  - 字符越多，优化效果越明显");
    }
}
