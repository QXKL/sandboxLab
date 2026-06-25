/**
 * 装饰器模式 - 文本装饰器示例
 *
 * 场景：文本编辑器，支持加粗、斜体、下划线等格式
 * 演示：多层装饰嵌套、装饰顺序的影响
 */

// ========== Component接口 ==========

/**
 * 组件接口：文本
 */
interface Text {
    /**
     * 渲染文本
     */
    String render();
}

// ========== ConcreteComponent（具体组件） ==========

/**
 * 具体组件：纯文本
 */
class PlainText implements Text {
    private String content;

    public PlainText(String content) {
        this.content = content;
    }

    @Override
    public String render() {
        return content;
    }
}

// ========== Decorator（抽象装饰器） ==========

/**
 * 抽象装饰器：文本装饰器
 */
abstract class TextDecorator implements Text {
    protected Text text;  // 被装饰的文本

    public TextDecorator(Text text) {
        this.text = text;
    }

    @Override
    public String render() {
        return text.render();
    }
}

// ========== ConcreteDecorator（具体装饰器） ==========

/**
 * 具体装饰器1：加粗
 */
class Bold extends TextDecorator {
    public Bold(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<b>" + text.render() + "</b>";
    }
}

/**
 * 具体装饰器2：斜体
 */
class Italic extends TextDecorator {
    public Italic(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<i>" + text.render() + "</i>";
    }
}

/**
 * 具体装饰器3：下划线
 */
class Underline extends TextDecorator {
    public Underline(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<u>" + text.render() + "</u>";
    }
}

/**
 * 具体装饰器4：删除线
 */
class StrikeThrough extends TextDecorator {
    public StrikeThrough(Text text) {
        super(text);
    }

    @Override
    public String render() {
        return "<s>" + text.render() + "</s>";
    }
}

/**
 * 具体装饰器5：颜色
 */
class Color extends TextDecorator {
    private String color;

    public Color(Text text, String color) {
        super(text);
        this.color = color;
    }

    @Override
    public String render() {
        return "<span style='color:" + color + "'>" + text.render() + "</span>";
    }
}

// ========== 测试 ==========

public class TextDecoratorDemo {
    public static void main(String[] args) {
        System.out.println("=== 装饰器模式 - 文本装饰器 ===\n");

        // 示例1：纯文本
        System.out.println("【1. 纯文本（无装饰）】");
        Text text1 = new PlainText("Hello World");
        printText(text1);

        // 示例2：单层装饰
        System.out.println("\n【2. 单层装饰】");
        Text text2 = new Bold(new PlainText("Hello World"));
        printText(text2);

        // 示例3：多层装饰
        System.out.println("\n【3. 多层装饰】");
        Text text3 = new PlainText("Hello World");
        text3 = new Bold(text3);
        text3 = new Italic(text3);
        text3 = new Underline(text3);
        printText(text3);

        // 示例4：装饰顺序的影响
        System.out.println("\n【4. 装饰顺序的影响】");
        demonstrateOrder();

        // 示例5：复杂组合
        System.out.println("\n【5. 复杂组合】");
        Text text4 = new PlainText("Important");
        text4 = new Color(text4, "red");
        text4 = new Bold(text4);
        text4 = new Underline(text4);
        printText(text4);

        // 示例6：多个独立装饰
        System.out.println("\n【6. 多个独立装饰】");
        demonstrateMultipleDecorations();

        // 示例7：装饰器的嵌套结构
        System.out.println("\n【7. 装饰器的嵌套结构】");
        visualizeNesting();
    }

    /**
     * 打印文本
     */
    static void printText(Text text) {
        System.out.println("  渲染结果: " + text.render());
    }

    /**
     * 演示装饰顺序的影响
     */
    static void demonstrateOrder() {
        System.out.println("顺序1: 先加粗，再斜体，最后下划线");
        Text text1 = new PlainText("Hello");
        text1 = new Bold(text1);          // <b>Hello</b>
        text1 = new Italic(text1);        // <i><b>Hello</b></i>
        text1 = new Underline(text1);     // <u><i><b>Hello</b></i></u>
        printText(text1);

        System.out.println("\n顺序2: 先下划线，再斜体，最后加粗");
        Text text2 = new PlainText("Hello");
        text2 = new Underline(text2);     // <u>Hello</u>
        text2 = new Italic(text2);        // <i><u>Hello</u></i>
        text2 = new Bold(text2);          // <b><i><u>Hello</u></i></b>
        printText(text2);

        System.out.println("\n⚠️  注意：装饰顺序影响HTML标签嵌套顺序");
        System.out.println("   但视觉效果相同（都是加粗+斜体+下划线）");
    }

    /**
     * 演示多个独立装饰
     */
    static void demonstrateMultipleDecorations() {
        System.out.println("为不同文本应用不同装饰：\n");

        // 标题：加粗 + 大号
        Text title = new Bold(new PlainText("文章标题"));
        System.out.println("标题: " + title.render());

        // 强调：斜体 + 颜色
        Text emphasis = new Color(new Italic(new PlainText("重要内容")), "blue");
        System.out.println("强调: " + emphasis.render());

        // 警告：加粗 + 颜色 + 下划线
        Text warning = new PlainText("警告信息");
        warning = new Color(warning, "red");
        warning = new Bold(warning);
        warning = new Underline(warning);
        System.out.println("警告: " + warning.render());

        // 删除：删除线
        Text deleted = new StrikeThrough(new PlainText("已删除内容"));
        System.out.println("删除: " + deleted.render());
    }

    /**
     * 可视化装饰器的嵌套结构
     */
    static void visualizeNesting() {
        System.out.println("\n装饰器嵌套结构（俄罗斯套娃）：\n");

        System.out.println("代码：");
        System.out.println("  Text text = new PlainText(\"Hello\");");
        System.out.println("  text = new Bold(text);");
        System.out.println("  text = new Italic(text);");
        System.out.println("  text = new Underline(text);");

        System.out.println("\n嵌套结构：");
        System.out.println("  Underline");
        System.out.println("    └─ Italic");
        System.out.println("         └─ Bold");
        System.out.println("              └─ PlainText(\"Hello\")");

        System.out.println("\n调用 text.render() 的执行过程：");
        System.out.println("  1. Underline.render()");
        System.out.println("     → \"<u>\" + text.render() + \"</u>\"");
        System.out.println("  2. Italic.render()");
        System.out.println("     → \"<i>\" + text.render() + \"</i>\"");
        System.out.println("  3. Bold.render()");
        System.out.println("     → \"<b>\" + text.render() + \"</b>\"");
        System.out.println("  4. PlainText.render()");
        System.out.println("     → \"Hello\"");
        System.out.println("  5. 回溯组装：");
        System.out.println("     → \"<b>Hello</b>\"");
        System.out.println("     → \"<i><b>Hello</b></i>\"");
        System.out.println("     → \"<u><i><b>Hello</b></i></u>\"");

        System.out.println("\n✅ 关键点：");
        System.out.println("  - 装饰器像洋葱，一层包一层");
        System.out.println("  - 调用从外向内穿透，返回从内向外组装");
        System.out.println("  - 每个装饰器只负责自己的装饰逻辑");
        System.out.println("  - 接口保持一致（都是Text）");
    }
}
