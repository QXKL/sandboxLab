/**
 * 桥接模式 - 遥控器与电视示例
 *
 * 场景：遥控器控制不同品牌的电视
 * 演示：抽象（遥控器）与实现（电视）分离
 */

// ========== 实现接口 ==========

/**
 * 实现接口：电视
 */
interface TV {
    void on();
    void off();
    void setChannel(int channel);
    void setVolume(int volume);
}

// ========== 具体实现 ==========

/**
 * 具体实现：索尼电视
 */
class SonyTV implements TV {
    @Override
    public void on() {
        System.out.println("  📺 [索尼电视] 开机");
    }

    @Override
    public void off() {
        System.out.println("  📺 [索尼电视] 关机");
    }

    @Override
    public void setChannel(int channel) {
        System.out.println("  📺 [索尼电视] 切换到频道: " + channel);
    }

    @Override
    public void setVolume(int volume) {
        System.out.println("  📺 [索尼电视] 音量设置为: " + volume);
    }
}

/**
 * 具体实现：三星电视
 */
class SamsungTV implements TV {
    @Override
    public void on() {
        System.out.println("  📺 [三星电视] 开机");
    }

    @Override
    public void off() {
        System.out.println("  📺 [三星电视] 关机");
    }

    @Override
    public void setChannel(int channel) {
        System.out.println("  📺 [三星电视] 切换到频道: " + channel);
    }

    @Override
    public void setVolume(int volume) {
        System.out.println("  📺 [三星电视] 音量设置为: " + volume);
    }
}

/**
 * 具体实现：LG电视
 */
class LGTV implements TV {
    @Override
    public void on() {
        System.out.println("  📺 [LG电视] 开机");
    }

    @Override
    public void off() {
        System.out.println("  📺 [LG电视] 关机");
    }

    @Override
    public void setChannel(int channel) {
        System.out.println("  📺 [LG电视] 切换到频道: " + channel);
    }

    @Override
    public void setVolume(int volume) {
        System.out.println("  📺 [LG电视] 音量设置为: " + volume);
    }
}

// ========== 抽象类 ==========

/**
 * 抽象类：遥控器
 */
abstract class RemoteControl {
    protected TV tv;  // 桥接：持有电视引用

    public RemoteControl(TV tv) {
        this.tv = tv;
    }

    abstract void on();
    abstract void off();
    abstract void setChannel(int channel);
}

// ========== 扩充抽象类 ==========

/**
 * 扩充抽象：基础遥控器
 */
class BasicRemote extends RemoteControl {
    public BasicRemote(TV tv) {
        super(tv);
    }

    @Override
    public void on() {
        System.out.println("🎮 [基础遥控器] 开机");
        tv.on();
    }

    @Override
    public void off() {
        System.out.println("🎮 [基础遥控器] 关机");
        tv.off();
    }

    @Override
    public void setChannel(int channel) {
        System.out.println("🎮 [基础遥控器] 切换频道");
        tv.setChannel(channel);
    }
}

/**
 * 扩充抽象：高级遥控器（增强功能）
 */
class AdvancedRemote extends RemoteControl {
    public AdvancedRemote(TV tv) {
        super(tv);
    }

    @Override
    public void on() {
        System.out.println("🎮 [高级遥控器] 开机");
        tv.on();
    }

    @Override
    public void off() {
        System.out.println("🎮 [高级遥控器] 关机");
        tv.off();
    }

    @Override
    public void setChannel(int channel) {
        System.out.println("🎮 [高级遥控器] 切换频道");
        tv.setChannel(channel);
    }

    // 高级功能：静音
    public void mute() {
        System.out.println("🎮 [高级遥控器] 静音");
        tv.setVolume(0);
    }
}

// ========== 测试 ==========

public class RemoteControlBridgeDemo {
    public static void main(String[] args) {
        System.out.println("=== 桥接模式 - 遥控器与电视 ===\n");

        // 示例1：基础遥控器控制索尼电视
        System.out.println("【1. 基础遥控器 + 索尼电视】");
        TV sonyTV = new SonyTV();
        RemoteControl basicRemote = new BasicRemote(sonyTV);
        basicRemote.on();
        basicRemote.setChannel(5);
        basicRemote.off();

        // 示例2：高级遥控器控制三星电视
        System.out.println("\n【2. 高级遥控器 + 三星电视】");
        TV samsungTV = new SamsungTV();
        AdvancedRemote advancedRemote = new AdvancedRemote(samsungTV);
        advancedRemote.on();
        advancedRemote.setChannel(10);
        advancedRemote.mute();
        advancedRemote.off();

        // 示例3：灵活组合
        System.out.println("\n【3. 灵活组合】");
        demonstrateFlexibility();

        // 示例4：对比继承方式
        System.out.println("\n【4. 桥接 vs 继承】");
        compareWithInheritance();
    }

    /**
     * 演示灵活组合
     */
    static void demonstrateFlexibility() {
        System.out.println("同一遥控器可以控制不同电视:\n");

        RemoteControl remote = new BasicRemote(new SonyTV());
        remote.on();

        // 运行时切换电视
        remote = new BasicRemote(new LGTV());
        remote.on();

        System.out.println("\n同一电视可以用不同遥控器:\n");

        TV tv = new SamsungTV();
        RemoteControl basic = new BasicRemote(tv);
        basic.on();

        AdvancedRemote advanced = new AdvancedRemote(tv);
        advanced.mute();
    }

    /**
     * 对比继承方式
     */
    static void compareWithInheritance() {
        System.out.println("❌ 如果用继承实现:");
        System.out.println("  - BasicRemoteForSony");
        System.out.println("  - BasicRemoteForSamsung");
        System.out.println("  - BasicRemoteForLG");
        System.out.println("  - AdvancedRemoteForSony");
        System.out.println("  - AdvancedRemoteForSamsung");
        System.out.println("  - AdvancedRemoteForLG");
        System.out.println("  → 2种遥控器 × 3种电视 = 6个类");

        System.out.println("\n✅ 桥接模式:");
        System.out.println("  遥控器: BasicRemote, AdvancedRemote (2个)");
        System.out.println("  电视: SonyTV, SamsungTV, LGTV (3个)");
        System.out.println("  → 2 + 3 = 5个类");

        System.out.println("\n✅ 优势:");
        System.out.println("  - 避免类爆炸");
        System.out.println("  - 遥控器和电视独立扩展");
        System.out.println("  - 灵活组合");
        System.out.println("  - 符合开闭原则");
    }
}
