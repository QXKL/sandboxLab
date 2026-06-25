/**
 * 适配器模式 - 媒体播放器示例
 *
 * 场景：你的播放器只支持MP3，通过适配器支持VLC和MP4格式
 * 演示：对象适配器的实现
 */

// ========== 目标接口 ==========

/**
 * 目标接口：统一的媒体播放接口
 * 这是客户端期望的接口
 */
interface MediaPlayer {
    void play(String audioType, String filename);
}

// ========== 被适配者 ==========

/**
 * 被适配者1：VLC播放器
 * 有自己特定的接口
 */
class VLCPlayer {
    public void playVLC(String filename) {
        System.out.println("  🎬 VLC播放器: 正在播放 " + filename);
    }
}

/**
 * 被适配者2：MP4播放器
 * 有自己特定的接口
 */
class MP4Player {
    public void playMP4(String filename) {
        System.out.println("  🎞️  MP4播放器: 正在播放 " + filename);
    }
}

// ========== 适配器 ==========

/**
 * 适配器：高级媒体播放器适配器
 * 将VLC和MP4播放器适配为MediaPlayer接口
 */
class MediaAdapter implements MediaPlayer {
    private VLCPlayer vlcPlayer;
    private MP4Player mp4Player;

    /**
     * 根据音频类型创建对应的播放器
     */
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            vlcPlayer = new VLCPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            mp4Player = new MP4Player();
        }
    }

    /**
     * 适配接口：转换调用到具体播放器
     */
    @Override
    public void play(String audioType, String filename) {
        if (audioType.equalsIgnoreCase("vlc")) {
            vlcPlayer.playVLC(filename);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            mp4Player.playMP4(filename);
        }
    }
}

// ========== 客户端 ==========

/**
 * 客户端：音频播放器
 * 原生支持MP3，通过适配器支持其他格式
 */
class AudioPlayer implements MediaPlayer {
    @Override
    public void play(String audioType, String filename) {
        // 原生支持MP3格式
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("  🎵 MP3播放器: 正在播放 " + filename);
        }
        // 通过适配器支持VLC和MP4格式
        else if (audioType.equalsIgnoreCase("vlc") ||
                 audioType.equalsIgnoreCase("mp4")) {
            MediaAdapter adapter = new MediaAdapter(audioType);
            adapter.play(audioType, filename);
        }
        // 不支持的格式
        else {
            System.out.println("  ❌ 错误: 不支持的格式 " + audioType);
        }
    }
}

// ========== 测试 ==========

public class MediaPlayerDemo {
    public static void main(String[] args) {
        System.out.println("=== 适配器模式 - 媒体播放器 ===\n");

        AudioPlayer player = new AudioPlayer();

        // 示例1：播放MP3（原生支持）
        System.out.println("【1. 播放MP3 - 原生支持】");
        player.play("mp3", "song.mp3");

        // 示例2：播放VLC（通过适配器）
        System.out.println("\n【2. 播放VLC - 通过适配器】");
        player.play("vlc", "movie.vlc");

        // 示例3：播放MP4（通过适配器）
        System.out.println("\n【3. 播放MP4 - 通过适配器】");
        player.play("mp4", "video.mp4");

        // 示例4：不支持的格式
        System.out.println("\n【4. 播放AVI - 不支持】");
        player.play("avi", "video.avi");

        // 示例5：批量播放
        System.out.println("\n【5. 批量播放】");
        String[][] playlist = {
            {"mp3", "好久不见.mp3"},
            {"vlc", "教父.vlc"},
            {"mp4", "阿凡达.mp4"},
            {"mp3", "晴天.mp3"}
        };

        for (String[] item : playlist) {
            System.out.println();
            player.play(item[0], item[1]);
        }

        // 示例6：适配器模式的优势
        System.out.println("\n\n【6. 适配器模式的优势】");
        demonstrateAdvantages();
    }

    /**
     * 演示适配器模式的优势
     */
    static void demonstrateAdvantages() {
        System.out.println("\n✅ 优势1: 不修改AudioPlayer代码");
        System.out.println("   - AudioPlayer无需知道VLCPlayer和MP4Player的存在");
        System.out.println("   - 通过适配器透明地支持新格式");

        System.out.println("\n✅ 优势2: 符合开闭原则");
        System.out.println("   - 增加新格式：只需添加新适配器");
        System.out.println("   - 无需修改现有代码");

        System.out.println("\n✅ 优势3: 复用现有类");
        System.out.println("   - VLCPlayer和MP4Player可以独立演进");
        System.out.println("   - 适配器只做接口转换");

        System.out.println("\n📊 流程图:");
        System.out.println("   AudioPlayer");
        System.out.println("      ├─ mp3 → 直接播放");
        System.out.println("      ├─ vlc → MediaAdapter → VLCPlayer");
        System.out.println("      └─ mp4 → MediaAdapter → MP4Player");
    }
}
