/**
 * 原型模式 - 深拷贝的三种实现方式
 *
 * 场景：文档编辑器
 * 演示：手动复制、序列化、拷贝构造函数
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// ========== 文档类 ==========

class Paragraph implements Serializable, Cloneable {
    private String content;

    public Paragraph(String content) {
        this.content = content;
    }

    public Paragraph(Paragraph other) {
        this.content = other.content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public Paragraph clone() {
        try {
            return (Paragraph) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Paragraph{" + content + "}";
    }
}

// ========== 方式1：手动复制 ==========

class DocumentManual implements Cloneable {
    private String title;
    private List<Paragraph> paragraphs;

    public DocumentManual(String title, List<Paragraph> paragraphs) {
        this.title = title;
        this.paragraphs = paragraphs;
    }

    @Override
    public DocumentManual clone() {
        try {
            DocumentManual cloned = (DocumentManual) super.clone();

            // 手动深拷贝List
            cloned.paragraphs = new ArrayList<>();
            for (Paragraph p : this.paragraphs) {
                cloned.paragraphs.add(p.clone());
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getTitle() { return title; }
    public List<Paragraph> getParagraphs() { return paragraphs; }

    @Override
    public String toString() {
        return "DocumentManual{title='" + title + "', paragraphs=" + paragraphs + "}";
    }
}

// ========== 方式2：序列化 ==========

class DocumentSerialize implements Serializable {
    private String title;
    private List<Paragraph> paragraphs;

    public DocumentSerialize(String title, List<Paragraph> paragraphs) {
        this.title = title;
        this.paragraphs = paragraphs;
    }

    public DocumentSerialize deepClone() {
        try {
            // 序列化
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.close();

            // 反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            DocumentSerialize cloned = (DocumentSerialize) ois.readObject();
            ois.close();

            return cloned;
        } catch (Exception e) {
            throw new RuntimeException("序列化克隆失败", e);
        }
    }

    public String getTitle() { return title; }
    public List<Paragraph> getParagraphs() { return paragraphs; }

    @Override
    public String toString() {
        return "DocumentSerialize{title='" + title + "', paragraphs=" + paragraphs + "}";
    }
}

// ========== 方式3：拷贝构造函数 ==========

class DocumentCopyConstructor {
    private String title;
    private List<Paragraph> paragraphs;

    public DocumentCopyConstructor(String title, List<Paragraph> paragraphs) {
        this.title = title;
        this.paragraphs = paragraphs;
    }

    // 拷贝构造函数
    public DocumentCopyConstructor(DocumentCopyConstructor other) {
        this.title = other.title;

        // 深拷贝List
        this.paragraphs = new ArrayList<>();
        for (Paragraph p : other.paragraphs) {
            this.paragraphs.add(new Paragraph(p));
        }
    }

    public String getTitle() { return title; }
    public List<Paragraph> getParagraphs() { return paragraphs; }

    @Override
    public String toString() {
        return "DocumentCopyConstructor{title='" + title + "', paragraphs=" + paragraphs + "}";
    }
}

// ========== 客户端 ==========

public class DeepCopyDemo {
    public static void main(String[] args) {
        System.out.println("=== 原型模式 - 深拷贝的三种实现方式 ===\n");

        // 准备测试数据
        List<Paragraph> paragraphs = new ArrayList<>();
        paragraphs.add(new Paragraph("第一段内容"));
        paragraphs.add(new Paragraph("第二段内容"));

        // 示例1：手动复制
        System.out.println("【1. 手动复制】\n");
        testManualCopy(paragraphs);

        // 示例2：序列化反序列化
        System.out.println("\n【2. 序列化反序列化】\n");
        testSerializeCopy(paragraphs);

        // 示例3：拷贝构造函数
        System.out.println("\n【3. 拷贝构造函数】\n");
        testCopyConstructor(paragraphs);

        // 示例4：性能对比
        System.out.println("\n【4. 性能对比】\n");
        performanceComparison();

        // 示例5：总结对比
        System.out.println("\n【5. 三种方式对比】\n");
        summaryComparison();
    }

    /**
     * 测试手动复制
     */
    static void testManualCopy(List<Paragraph> paragraphs) {
        DocumentManual original = new DocumentManual("原始文档", new ArrayList<>(paragraphs));
        System.out.println("原始对象: " + original);

        DocumentManual copy = original.clone();
        System.out.println("克隆对象: " + copy);

        // 修改克隆对象
        copy.getParagraphs().get(0).setContent("修改后的第一段");
        copy.getParagraphs().add(new Paragraph("新增的第三段"));

        System.out.println("\n修改克隆对象后：");
        System.out.println("原始对象: " + original);
        System.out.println("克隆对象: " + copy);
        System.out.println("✅ 完全独立，互不影响");
    }

    /**
     * 测试序列化反序列化
     */
    static void testSerializeCopy(List<Paragraph> paragraphs) {
        DocumentSerialize original = new DocumentSerialize("原始文档", new ArrayList<>(paragraphs));
        System.out.println("原始对象: " + original);

        DocumentSerialize copy = original.deepClone();
        System.out.println("克隆对象: " + copy);

        // 修改克隆对象
        copy.getParagraphs().get(0).setContent("修改后的第一段");
        copy.getParagraphs().add(new Paragraph("新增的第三段"));

        System.out.println("\n修改克隆对象后：");
        System.out.println("原始对象: " + original);
        System.out.println("克隆对象: " + copy);
        System.out.println("✅ 完全独立，互不影响");
    }

    /**
     * 测试拷贝构造函数
     */
    static void testCopyConstructor(List<Paragraph> paragraphs) {
        DocumentCopyConstructor original = new DocumentCopyConstructor("原始文档", new ArrayList<>(paragraphs));
        System.out.println("原始对象: " + original);

        DocumentCopyConstructor copy = new DocumentCopyConstructor(original);
        System.out.println("克隆对象: " + copy);

        // 修改克隆对象
        copy.getParagraphs().get(0).setContent("修改后的第一段");
        copy.getParagraphs().add(new Paragraph("新增的第三段"));

        System.out.println("\n修改克隆对象后：");
        System.out.println("原始对象: " + original);
        System.out.println("克隆对象: " + copy);
        System.out.println("✅ 完全独立，互不影响");
    }

    /**
     * 性能对比
     */
    static void performanceComparison() {
        // 创建较大的测试对象
        List<Paragraph> largeParagraphs = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeParagraphs.add(new Paragraph("段落内容" + i));
        }

        // 测试手动复制
        DocumentManual docManual = new DocumentManual("测试", new ArrayList<>(largeParagraphs));
        long start1 = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            docManual.clone();
        }
        long time1 = System.nanoTime() - start1;
        System.out.println("手动复制 1000次: " + time1 / 1_000_000 + "ms");

        // 测试序列化
        DocumentSerialize docSerialize = new DocumentSerialize("测试", new ArrayList<>(largeParagraphs));
        long start2 = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            docSerialize.deepClone();
        }
        long time2 = System.nanoTime() - start2;
        System.out.println("序列化方式 1000次: " + time2 / 1_000_000 + "ms");

        // 测试拷贝构造函数
        DocumentCopyConstructor docCopy = new DocumentCopyConstructor("测试", new ArrayList<>(largeParagraphs));
        long start3 = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            new DocumentCopyConstructor(docCopy);
        }
        long time3 = System.nanoTime() - start3;
        System.out.println("拷贝构造函数 1000次: " + time3 / 1_000_000 + "ms");

        System.out.println("\n性能对比（越小越快）：");
        System.out.println("  手动复制: " + time1 / 1_000_000 + "ms (性能最好)");
        System.out.println("  拷贝构造: " + time3 / 1_000_000 + "ms");
        System.out.println("  序列化: " + time2 / 1_000_000 + "ms (性能最差)");
    }

    /**
     * 总结对比
     */
    static void summaryComparison() {
        System.out.println("方式1：手动复制（clone()）");
        System.out.println("  优点：性能好、可控");
        System.out.println("  缺点：引用层次深时代码复杂");
        System.out.println("  适用：一般场景");
        System.out.println();

        System.out.println("方式2：序列化反序列化");
        System.out.println("  优点：自动深拷贝所有字段");
        System.out.println("  缺点：性能差、所有类必须实现Serializable");
        System.out.println("  适用：对象结构复杂、性能要求不高");
        System.out.println();

        System.out.println("方式3：拷贝构造函数");
        System.out.println("  优点：清晰、可控、类型安全");
        System.out.println("  缺点：需要手动编写");
        System.out.println("  适用：推荐方式，清晰明确");
        System.out.println();

        System.out.println("推荐选择：");
        System.out.println("  1. 简单对象 → 拷贝构造函数（最清晰）");
        System.out.println("  2. 一般对象 → 手动复制（性能好）");
        System.out.println("  3. 复杂对象 → 序列化（自动但慢）");
    }
}
