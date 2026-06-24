/**
 * 单例模式 - 完整示例
 *
 * 演示6种实现方式及其对比
 */

// ========== 方式1：饿汉式（线程安全）==========

/**
 * EagerSingleton - 饿汉式单例
 *
 * 优点：线程安全、实现简单
 * 缺点：类加载时就创建，可能浪费资源
 */
class EagerSingleton {
    // 类加载时就创建实例
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {
        System.out.println("  [EagerSingleton] 创建实例");
    }

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }

    public void showMessage() {
        System.out.println("  [EagerSingleton] 实例ID: " + System.identityHashCode(this));
    }
}

// ========== 方式2：懒汉式（线程不安全）==========

/**
 * LazySingleton - 懒汉式单例（不安全）
 *
 * 优点：延迟加载
 * 缺点：线程不安全，多线程下可能创建多个实例
 */
class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {
        System.out.println("  [LazySingleton] 创建实例");
    }

    // ❌ 线程不安全
    public static LazySingleton getInstance() {
        if (instance == null) {
            // 模拟耗时操作
            try { Thread.sleep(10); } catch (InterruptedException e) {}
            instance = new LazySingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("  [LazySingleton] 实例ID: " + System.identityHashCode(this));
    }
}

// ========== 方式3：懒汉式（同步方法，线程安全但性能差）==========

/**
 * SynchronizedLazySingleton - 同步懒汉式
 *
 * 优点：线程安全、延迟加载
 * 缺点：性能差，每次调用都同步
 */
class SynchronizedLazySingleton {
    private static SynchronizedLazySingleton instance;

    private SynchronizedLazySingleton() {
        System.out.println("  [SynchronizedLazySingleton] 创建实例");
    }

    // synchronized 保证线程安全，但性能差
    public static synchronized SynchronizedLazySingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedLazySingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("  [SynchronizedLazySingleton] 实例ID: " + System.identityHashCode(this));
    }
}

// ========== 方式4：双重检查锁定（DCL）==========

/**
 * DoubleCheckedLockingSingleton - DCL单例
 *
 * 优点：线程安全、延迟加载、性能好
 * 缺点：实现复杂，需要volatile
 */
class DoubleCheckedLockingSingleton {
    // volatile 防止指令重排序
    private static volatile DoubleCheckedLockingSingleton instance;

    private DoubleCheckedLockingSingleton() {
        System.out.println("  [DoubleCheckedLockingSingleton] 创建实例");
    }

    public static DoubleCheckedLockingSingleton getInstance() {
        if (instance == null) {  // 第一次检查（无锁）
            synchronized (DoubleCheckedLockingSingleton.class) {
                if (instance == null) {  // 第二次检查（有锁）
                    instance = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("  [DoubleCheckedLockingSingleton] 实例ID: " + System.identityHashCode(this));
    }
}

// ========== 方式5：静态内部类（推荐）==========

/**
 * StaticInnerClassSingleton - 静态内部类单例
 *
 * 优点：线程安全、延迟加载、实现简单、性能好
 * 缺点：无
 *
 * ✅✅✅ 强烈推荐
 */
class StaticInnerClassSingleton {
    private StaticInnerClassSingleton() {
        System.out.println("  [StaticInnerClassSingleton] 创建实例");
    }

    // 静态内部类，只有调用getInstance时才加载
    private static class SingletonHolder {
        private static final StaticInnerClassSingleton INSTANCE = new StaticInnerClassSingleton();
    }

    public static StaticInnerClassSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void showMessage() {
        System.out.println("  [StaticInnerClassSingleton] 实例ID: " + System.identityHashCode(this));
    }
}

// ========== 方式6：枚举单例（最安全）==========

/**
 * EnumSingleton - 枚举单例
 *
 * 优点：线程安全、防反射攻击、防序列化攻击、代码简洁
 * 缺点：不够灵活
 *
 * ✅✅ Effective Java 作者推荐
 */
enum EnumSingleton {
    INSTANCE;

    EnumSingleton() {
        System.out.println("  [EnumSingleton] 创建实例");
    }

    public void showMessage() {
        System.out.println("  [EnumSingleton] 实例ID: " + System.identityHashCode(this));
    }
}

// ========== 主程序 ==========

public class SingletonDemo {
    public static void main(String[] args) {
        System.out.println("=== 单例模式示例 ===\n");

        // 测试1：饿汉式
        System.out.println("【1. 饿汉式】类加载时创建");
        EagerSingleton eager1 = EagerSingleton.getInstance();
        EagerSingleton eager2 = EagerSingleton.getInstance();
        eager1.showMessage();
        eager2.showMessage();
        System.out.println("  是否同一实例: " + (eager1 == eager2));

        System.out.println("\n【2. 懒汉式（不安全）】延迟加载，但线程不安全");
        testThreadSafety();

        System.out.println("\n【3. 懒汉式（同步方法）】线程安全但性能差");
        SynchronizedLazySingleton sync1 = SynchronizedLazySingleton.getInstance();
        SynchronizedLazySingleton sync2 = SynchronizedLazySingleton.getInstance();
        sync1.showMessage();
        sync2.showMessage();
        System.out.println("  是否同一实例: " + (sync1 == sync2));

        System.out.println("\n【4. 双重检查锁定（DCL）】线程安全+性能好");
        DoubleCheckedLockingSingleton dcl1 = DoubleCheckedLockingSingleton.getInstance();
        DoubleCheckedLockingSingleton dcl2 = DoubleCheckedLockingSingleton.getInstance();
        dcl1.showMessage();
        dcl2.showMessage();
        System.out.println("  是否同一实例: " + (dcl1 == dcl2));

        System.out.println("\n【5. 静态内部类】✅✅✅ 推荐");
        StaticInnerClassSingleton static1 = StaticInnerClassSingleton.getInstance();
        StaticInnerClassSingleton static2 = StaticInnerClassSingleton.getInstance();
        static1.showMessage();
        static2.showMessage();
        System.out.println("  是否同一实例: " + (static1 == static2));

        System.out.println("\n【6. 枚举单例】✅✅ 最安全");
        EnumSingleton enum1 = EnumSingleton.INSTANCE;
        EnumSingleton enum2 = EnumSingleton.INSTANCE;
        enum1.showMessage();
        enum2.showMessage();
        System.out.println("  是否同一实例: " + (enum1 == enum2));

        // 实现方式对比
        System.out.println("\n" + "=".repeat(60));
        System.out.println("实现方式对比");
        System.out.println("=".repeat(60));

        System.out.println("\n✅ 推荐实现：");
        System.out.println("  1. 静态内部类（最优雅）");
        System.out.println("     - 线程安全 + 延迟加载 + 实现简单");
        System.out.println("  2. 枚举（最安全）");
        System.out.println("     - 防反射 + 防序列化攻击");
        System.out.println("  3. 饿汉式（简单场景）");
        System.out.println("     - 不需要延迟加载时使用");

        System.out.println("\n❌ 不推荐实现：");
        System.out.println("  1. 懒汉式（不同步）- 线程不安全");
        System.out.println("  2. 懒汉式（同步方法）- 性能差");

        System.out.println("\n⚠️  可选实现：");
        System.out.println("  1. DCL - 需要理解volatile和指令重排序");
    }

    /**
     * 测试懒汉式的线程安全问题
     */
    static void testThreadSafety() {
        System.out.println("  启动10个线程同时获取实例...");

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                LazySingleton instance = LazySingleton.getInstance();
                instance.showMessage();
            }).start();
        }

        // 等待所有线程执行完
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        System.out.println("  ⚠️  可能创建了多个实例（线程不安全）");
    }
}
