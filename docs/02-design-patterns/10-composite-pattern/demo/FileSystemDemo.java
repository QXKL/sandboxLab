/**
 * 组合模式 - 文件系统示例
 *
 * 场景：文件系统（文件夹与文件）
 * 演示：统一接口处理文件和文件夹，递归显示树形结构
 */

// ========== 抽象构件 ==========

/**
 * Component：文件系统节点
 */
interface FileSystemNode {
    void display(String indent);
    long getSize();
}

// ========== 叶子节点 ==========

/**
 * Leaf：文件
 */
class File implements FileSystemNode {
    private String name;
    private long size;  // 文件大小（KB）

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📄 " + name + " (" + size + "KB)");
    }

    @Override
    public long getSize() {
        return size;
    }
}

// ========== 容器节点 ==========

/**
 * Composite：文件夹
 */
class Folder implements FileSystemNode {
    private String name;
    private java.util.List<FileSystemNode> children = new java.util.ArrayList<>();

    public Folder(String name) {
        this.name = name;
    }

    // 添加子节点
    public void add(FileSystemNode node) {
        children.add(node);
    }

    // 删除子节点
    public void remove(FileSystemNode node) {
        children.remove(node);
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📁 " + name + "/ (" + getSize() + "KB)");
        for (FileSystemNode child : children) {
            child.display(indent + "  ");  // 递归显示
        }
    }

    @Override
    public long getSize() {
        long total = 0;
        for (FileSystemNode child : children) {
            total += child.getSize();  // 递归计算
        }
        return total;
    }

    public int getFileCount() {
        int count = 0;
        for (FileSystemNode child : children) {
            if (child instanceof File) {
                count++;
            } else if (child instanceof Folder) {
                count += ((Folder) child).getFileCount();
            }
        }
        return count;
    }
}

// ========== 测试 ==========

public class FileSystemDemo {
    public static void main(String[] args) {
        System.out.println("=== 组合模式 - 文件系统 ===\n");

        // 示例1：基本文件系统
        System.out.println("【1. 构建文件系统】");
        FileSystemNode system = buildFileSystem();
        system.display("");

        // 示例2：统一接口的威力
        System.out.println("\n【2. 统一接口 - 获取大小】");
        demonstrateUniformInterface(system);

        // 示例3：动态修改
        System.out.println("\n【3. 动态修改文件系统】");
        demonstrateDynamicModification();

        // 示例4：与非组合模式对比
        System.out.println("\n【4. 组合模式的优势】");
        compareWithNonComposite();
    }

    /**
     * 构建文件系统
     */
    static FileSystemNode buildFileSystem() {
        // 根目录
        Folder root = new Folder("我的文档");

        // 直接文件
        root.add(new File("简历.docx", 50));
        root.add(new File("自我介绍.txt", 5));

        // 照片文件夹
        Folder photos = new Folder("照片");
        photos.add(new File("旅行.jpg", 2048));
        photos.add(new File("生活.jpg", 1024));
        photos.add(new File("证件照.png", 512));
        root.add(photos);

        // 工作文件夹（嵌套）
        Folder work = new Folder("工作");
        work.add(new File("报告.pdf", 300));

        Folder projectA = new Folder("项目A");
        projectA.add(new File("需求文档.docx", 120));
        projectA.add(new File("设计文档.docx", 200));
        projectA.add(new File("代码.zip", 5000));
        work.add(projectA);

        Folder projectB = new Folder("项目B");
        projectB.add(new File("会议记录.txt", 10));
        work.add(projectB);

        root.add(work);

        return root;
    }

    /**
     * 演示统一接口
     */
    static void demonstrateUniformInterface(FileSystemNode node) {
        // 关键：客户端无需知道是文件还是文件夹
        System.out.println("总大小: " + node.getSize() + "KB");

        if (node instanceof Folder) {
            Folder folder = (Folder) node;
            System.out.println("文件数量: " + folder.getFileCount());
        }

        System.out.println("\n✅ 优势: 客户端只需调用 getSize()，无需区分文件和文件夹");
        System.out.println("  - 文件：直接返回大小");
        System.out.println("  - 文件夹：递归计算所有子节点大小");
    }

    /**
     * 演示动态修改
     */
    static void demonstrateDynamicModification() {
        Folder temp = new Folder("临时文件");
        temp.add(new File("草稿.txt", 10));

        System.out.println("初始状态:");
        temp.display("");
        System.out.println("总大小: " + temp.getSize() + "KB");

        // 添加新文件
        temp.add(new File("备份.zip", 1000));

        System.out.println("\n添加文件后:");
        temp.display("");
        System.out.println("总大小: " + temp.getSize() + "KB");

        System.out.println("\n✅ 优势: 可以动态增加/删除节点，结构灵活");
    }

    /**
     * 对比非组合模式
     */
    static void compareWithNonComposite() {
        System.out.println("❌ 不使用组合模式:");
        System.out.println("```java");
        System.out.println("class Folder {");
        System.out.println("    List<File> files;");
        System.out.println("    List<Folder> subFolders;  // 两个集合");
        System.out.println("    ");
        System.out.println("    long getSize() {");
        System.out.println("        long total = 0;");
        System.out.println("        for (File f : files) {");
        System.out.println("            total += f.getSize();");
        System.out.println("        }");
        System.out.println("        for (Folder folder : subFolders) {");
        System.out.println("            total += folder.getSize();");
        System.out.println("        }");
        System.out.println("        return total;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println("问题: 需要分别处理文件和文件夹");

        System.out.println("\n✅ 使用组合模式:");
        System.out.println("```java");
        System.out.println("class Folder implements FileSystemNode {");
        System.out.println("    List<FileSystemNode> children;  // 统一集合");
        System.out.println("    ");
        System.out.println("    long getSize() {");
        System.out.println("        long total = 0;");
        System.out.println("        for (FileSystemNode child : children) {");
        System.out.println("            total += child.getSize();  // 统一调用");
        System.out.println("        }");
        System.out.println("        return total;");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");
        System.out.println("优势: 统一接口，客户端无需区分类型");

        System.out.println("\n📊 对比总结:");
        System.out.println("  ┌─────────────────┬─────────────┬─────────────┐");
        System.out.println("  │ 对比            │ 非组合模式  │ 组合模式    │");
        System.out.println("  ├─────────────────┼─────────────┼─────────────┤");
        System.out.println("  │ 集合数量        │ 2个         │ 1个         │");
        System.out.println("  │ 客户端是否区分  │ 需要        │ 不需要      │");
        System.out.println("  │ 代码复杂度      │ 高          │ 低          │");
        System.out.println("  │ 扩展性          │ 差          │ 好          │");
        System.out.println("  └─────────────────┴─────────────┴─────────────┘");
    }
}
