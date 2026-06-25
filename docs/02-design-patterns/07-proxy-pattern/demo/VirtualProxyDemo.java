/**
 * 代理模式 - 虚拟代理示例
 *
 * 场景：图片查看器，大图片延迟加载
 * 演示：虚拟代理、懒加载、节省内存
 */

// ========== Subject接口 ==========

/**
 * 抽象主题：图片接口
 */
interface Image {
    void display();
    String getInfo();
}

// ========== RealSubject（真实对象） ==========

/**
 * 真实对象：真实图片（加载慢）
 */
class RealImage implements Image {
    private String filename;
    private byte[] imageData;

    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();  // 构造时加载（耗时）
    }

    /**
     * 从磁盘加载图片（耗时操作）
     */
    private void loadFromDisk() {
        System.out.println("  📥 [真实图片] 从磁盘加载: " + filename);
        try {
            // 模拟耗时操作（2秒）
            Thread.sleep(2000);
            // 模拟加载大量数据
            imageData = new byte[10 * 1024 * 1024];  // 10MB
            System.out.println("  ✅ [真实图片] 加载完成: " + filename + " (10MB)");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display() {
        System.out.println("  🖼️  [真实图片] 显示图片: " + filename);
    }

    @Override
    public String getInfo() {
        return "真实图片: " + filename + " (10MB)";
    }
}

// ========== Proxy（代理） ==========

/**
 * 代理：图片代理（懒加载）
 */
class ImageProxy implements Image {
    private String filename;
    private RealImage realImage;  // 真实图片（延迟创建）

    public ImageProxy(String filename) {
        this.filename = filename;
        System.out.println("  ⚡ [代理] 创建代理（轻量，快速）: " + filename);
    }

    @Override
    public void display() {
        // 需要时才创建真实图片（懒加载）
        if (realImage == null) {
            System.out.println("  🔄 [代理] 首次访问，开始加载真实图片...");
            realImage = new RealImage(filename);
        } else {
            System.out.println("  ✅ [代理] 真实图片已加载，直接显示");
        }
        realImage.display();
    }

    @Override
    public String getInfo() {
        if (realImage == null) {
            return "代理: " + filename + " (未加载)";
        } else {
            return realImage.getInfo();
        }
    }
}

// ========== 测试 ==========

public class VirtualProxyDemo {
    public static void main(String[] args) {
        System.out.println("=== 代理模式 - 虚拟代理（懒加载） ===\n");

        // 示例1：对比直接加载 vs 代理加载
        System.out.println("【1. 对比：直接加载 vs 代理加载】\n");
        compareDirectVsProxy();

        // 示例2：代理的懒加载
        System.out.println("\n【2. 代理的懒加载】\n");
        demonstrateLazyLoading();

        // 示例3：多个图片的场景
        System.out.println("\n【3. 多个图片的场景】\n");
        demonstrateMultipleImages();

        // 示例4：虚拟代理的优势
        System.out.println("\n【4. 虚拟代理的优势】\n");
        demonstrateAdvantages();
    }

    /**
     * 对比直接加载 vs 代理加载
     */
    static void compareDirectVsProxy() {
        System.out.println("方式1: 直接创建真实图片");
        long start1 = System.currentTimeMillis();
        Image realImage = new RealImage("photo1.jpg");
        long time1 = System.currentTimeMillis() - start1;
        System.out.println("  耗时: " + time1 + "ms\n");

        System.out.println("方式2: 创建代理");
        long start2 = System.currentTimeMillis();
        Image proxyImage = new ImageProxy("photo2.jpg");
        long time2 = System.currentTimeMillis() - start2;
        System.out.println("  耗时: " + time2 + "ms");

        System.out.println("\n✅ 对比结果:");
        System.out.println("  - 直接创建: " + time1 + "ms (慢，启动时加载)");
        System.out.println("  - 代理创建: " + time2 + "ms (快，延迟加载)");
        System.out.println("  - 性能提升: " + (time1 - time2) + "ms");
    }

    /**
     * 演示懒加载
     */
    static void demonstrateLazyLoading() {
        System.out.println("创建图片代理（快速）:");
        Image image = new ImageProxy("photo3.jpg");
        System.out.println("代理信息: " + image.getInfo());

        System.out.println("\n首次显示图片（触发加载）:");
        image.display();
        System.out.println("代理信息: " + image.getInfo());

        System.out.println("\n第二次显示图片（已加载）:");
        image.display();

        System.out.println("\n第三次显示图片（已加载）:");
        image.display();
    }

    /**
     * 演示多个图片的场景
     */
    static void demonstrateMultipleImages() {
        System.out.println("场景：图片库，启动时创建100个图片代理\n");

        System.out.println("⚡ 启动阶段：快速创建所有代理");
        long startTime = System.currentTimeMillis();
        Image[] images = new Image[100];
        for (int i = 0; i < 100; i++) {
            images[i] = new ImageProxy("photo" + i + ".jpg");
        }
        long createTime = System.currentTimeMillis() - startTime;
        System.out.println("  创建100个代理耗时: " + createTime + "ms (几乎瞬间)\n");

        System.out.println("📖 用户浏览阶段：按需加载");
        System.out.println("  用户只查看了第1、5、10张图片:");
        images[0].display();
        System.out.println();
        images[4].display();
        System.out.println();
        images[9].display();

        System.out.println("\n✅ 优势:");
        System.out.println("  - 启动快：只创建代理，不加载图片");
        System.out.println("  - 节省内存：只加载用户查看的图片");
        System.out.println("  - 用户体验好：无需等待所有图片加载");
    }

    /**
     * 演示虚拟代理的优势
     */
    static void demonstrateAdvantages() {
        System.out.println("✅ 优势1: 启动速度快");
        System.out.println("   - 代理创建几乎不耗时");
        System.out.println("   - 真实对象延迟创建");

        System.out.println("\n✅ 优势2: 节省内存");
        System.out.println("   - 不使用的图片不加载");
        System.out.println("   - 按需分配内存");

        System.out.println("\n✅ 优势3: 透明性");
        System.out.println("   - 客户端无需知道是代理还是真实对象");
        System.out.println("   - 调用方式完全相同");

        System.out.println("\n✅ 优势4: 改善用户体验");
        System.out.println("   - 应用启动快");
        System.out.println("   - 响应及时");

        System.out.println("\n⚠️  适用场景:");
        System.out.println("   - 对象创建成本高");
        System.out.println("   - 对象不一定会被使用");
        System.out.println("   - 需要优化启动速度");

        System.out.println("\n📊 性能对比:");
        System.out.println("   场景：图片库有100张大图片");
        System.out.println("   - 直接加载: 100 × 2秒 = 200秒 (启动时)");
        System.out.println("   - 代理加载: 几乎瞬间 (启动时) + 2秒 × 实际查看数量");
        System.out.println("   - 如果用户只查看10张: 20秒 vs 200秒");
        System.out.println("   - 性能提升: 90%");
    }
}
