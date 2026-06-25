/**
 * 外观模式 - 家庭影院示例
 *
 * 场景：家庭影院系统，统一控制电视、音响、灯光等
 * 演示：外观模式的基本使用、简化复杂操作
 */

// ========== 子系统 ==========

/**
 * 子系统1：电视
 */
class TV {
    public void on() {
        System.out.println("  📺 电视已打开");
    }

    public void off() {
        System.out.println("  📺 电视已关闭");
    }

    public void setInputChannel(String channel) {
        System.out.println("  📺 电视切换输入源: " + channel);
    }
}

/**
 * 子系统2：音响系统
 */
class SoundSystem {
    public void on() {
        System.out.println("  🔊 音响已打开");
    }

    public void off() {
        System.out.println("  🔊 音响已关闭");
    }

    public void setVolume(int level) {
        System.out.println("  🔊 音响音量设置为: " + level);
    }

    public void setSurroundMode(String mode) {
        System.out.println("  🔊 音响环绕模式: " + mode);
    }
}

/**
 * 子系统3：灯光
 */
class Lights {
    public void on() {
        System.out.println("  💡 灯光已打开");
    }

    public void off() {
        System.out.println("  💡 灯光已关闭");
    }

    public void dim(int level) {
        System.out.println("  💡 灯光调暗至: " + level + "%");
    }
}

/**
 * 子系统4：投影仪
 */
class Projector {
    public void on() {
        System.out.println("  📽️  投影仪已打开");
    }

    public void off() {
        System.out.println("  📽️  投影仪已关闭");
    }

    public void setInput(String input) {
        System.out.println("  📽️  投影仪输入源: " + input);
    }

    public void setWideScreenMode() {
        System.out.println("  📽️  投影仪切换至宽屏模式");
    }
}

/**
 * 子系统5：DVD播放器
 */
class DVDPlayer {
    public void on() {
        System.out.println("  💿 DVD播放器已打开");
    }

    public void off() {
        System.out.println("  💿 DVD播放器已关闭");
    }

    public void play(String movie) {
        System.out.println("  💿 正在播放电影: " + movie);
    }

    public void stop() {
        System.out.println("  💿 停止播放");
    }
}

// ========== 外观 ==========

/**
 * 外观：家庭影院外观
 */
class HomeTheaterFacade {
    // 持有所有子系统
    private TV tv;
    private SoundSystem sound;
    private Lights lights;
    private Projector projector;
    private DVDPlayer dvd;

    public HomeTheaterFacade() {
        this.tv = new TV();
        this.sound = new SoundSystem();
        this.lights = new Lights();
        this.projector = new Projector();
        this.dvd = new DVDPlayer();
    }

    /**
     * 高层接口：看电影
     */
    public void watchMovie(String movie) {
        System.out.println("\n🎬 准备看电影...\n");

        // 协调所有子系统
        lights.dim(10);
        projector.on();
        projector.setWideScreenMode();
        sound.on();
        sound.setVolume(30);
        sound.setSurroundMode("5.1");
        dvd.on();
        dvd.play(movie);

        System.out.println("\n✅ 电影开始，请欣赏！\n");
    }

    /**
     * 高层接口：结束电影
     */
    public void endMovie() {
        System.out.println("\n🛑 关闭影院...\n");

        dvd.stop();
        dvd.off();
        sound.off();
        projector.off();
        lights.on();

        System.out.println("\n✅ 影院已关闭\n");
    }

    /**
     * 高层接口：听音乐
     */
    public void listenToMusic(String music) {
        System.out.println("\n🎵 准备听音乐...\n");

        lights.dim(30);
        sound.on();
        sound.setVolume(20);
        dvd.on();
        dvd.play(music);

        System.out.println("\n✅ 音乐播放中\n");
    }
}

// ========== 测试 ==========

public class HomeTheaterFacadeDemo {
    public static void main(String[] args) {
        System.out.println("=== 外观模式 - 家庭影院 ===\n");

        // 示例1：对比使用外观前后
        System.out.println("【1. 不使用外观（复杂）】");
        demonstrateWithoutFacade();

        System.out.println("\n【2. 使用外观（简单）】");
        demonstrateWithFacade();

        // 示例3：外观的优势
        System.out.println("\n【3. 外观的优势】");
        demonstrateAdvantages();
    }

    /**
     * 不使用外观（复杂）
     */
    static void demonstrateWithoutFacade() {
        System.out.println("客户端需要了解所有子系统:\n");

        TV tv = new TV();
        SoundSystem sound = new SoundSystem();
        Lights lights = new Lights();
        Projector projector = new Projector();
        DVDPlayer dvd = new DVDPlayer();

        // 20多行代码才能完成"看电影"
        System.out.println("准备看电影...\n");
        lights.dim(10);
        projector.on();
        projector.setWideScreenMode();
        sound.on();
        sound.setVolume(30);
        sound.setSurroundMode("5.1");
        dvd.on();
        dvd.play("阿凡达");
        System.out.println("\n电影开始！");

        System.out.println("\n❌ 问题:");
        System.out.println("  - 客户端需要了解所有子系统");
        System.out.println("  - 调用复杂（20多行代码）");
        System.out.println("  - 容易出错（操作顺序、遗漏步骤）");
        System.out.println("  - 高度耦合（子系统变化影响客户端）");
    }

    /**
     * 使用外观（简单）
     */
    static void demonstrateWithFacade() {
        HomeTheaterFacade homeTheater = new HomeTheaterFacade();

        // 一行代码搞定！
        homeTheater.watchMovie("阿凡达");

        try {
            Thread.sleep(2000);  // 模拟观影
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        homeTheater.endMovie();

        System.out.println("✅ 优势:");
        System.out.println("  - 简单（1行代替20行）");
        System.out.println("  - 解耦（只依赖外观）");
        System.out.println("  - 不易出错（外观保证顺序）");
        System.out.println("  - 易维护（子系统变化不影响客户端）");
    }

    /**
     * 演示外观的优势
     */
    static void demonstrateAdvantages() {
        HomeTheaterFacade homeTheater = new HomeTheaterFacade();

        System.out.println("\n✅ 优势1: 提供多种高层接口");
        System.out.println("  - watchMovie(): 看电影");
        System.out.println("  - listenToMusic(): 听音乐");
        System.out.println("  - endMovie(): 关闭影院");

        System.out.println("\n✅ 优势2: 隐藏复杂性");
        System.out.println("  - 客户端无需知道有哪些设备");
        System.out.println("  - 客户端无需知道操作顺序");
        System.out.println("  - 外观内部协调所有细节");

        System.out.println("\n✅ 优势3: 解耦");
        System.out.println("  - 客户端只依赖外观");
        System.out.println("  - 子系统变化不影响客户端");
        System.out.println("  - 新增设备只需修改外观");

        System.out.println("\n✅ 优势4: 灵活性");
        System.out.println("  - 仍可直接访问子系统（如需精细控制）");
        System.out.println("  - 外观不阻止直接调用");

        System.out.println("\n📊 代码行数对比:");
        System.out.println("  - 不使用外观: ~20行（每次操作）");
        System.out.println("  - 使用外观: 1行（调用外观方法）");
        System.out.println("  - 减少代码: 95%");

        System.out.println("\n🎯 适用场景:");
        System.out.println("  - 复杂子系统需要简化");
        System.out.println("  - 多个子系统需要协调");
        System.out.println("  - 客户端与子系统需要解耦");
    }
}
