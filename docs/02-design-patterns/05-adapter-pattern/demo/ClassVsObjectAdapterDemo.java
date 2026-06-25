/**
 * 适配器模式 - 类适配器 vs 对象适配器
 *
 * 场景：将USB-C适配为USB-A接口
 * 演示：两种适配器实现方式的对比
 */

// ========== 目标接口 ==========

/**
 * 目标接口：USB-A接口
 */
interface USBInterface {
    void connectUSB();
}

// ========== 被适配者 ==========

/**
 * 被适配者：USB-C设备
 */
class USBCDevice {
    public void connectUSBC() {
        System.out.println("  🔌 USB-C设备已连接");
    }

    public void transferData() {
        System.out.println("  📤 USB-C正在传输数据...");
    }
}

// ========== 对象适配器（推荐✅） ==========

/**
 * 对象适配器：通过组合实现
 * 优点：灵活，符合组合复用原则
 */
class USBCObjectAdapter implements USBInterface {
    private USBCDevice usbcDevice;  // 组合：持有被适配者

    public USBCObjectAdapter(USBCDevice usbcDevice) {
        this.usbcDevice = usbcDevice;
    }

    @Override
    public void connectUSB() {
        System.out.println("  🔄 对象适配器转换中...");
        // 调用被适配者的方法
        usbcDevice.connectUSBC();
    }

    /**
     * 可以增加额外的方法
     */
    public void transferDataViaAdapter() {
        usbcDevice.transferData();
    }
}

// ========== 类适配器（少用⚠️） ==========

/**
 * 类适配器：通过继承实现
 * 缺点：只能适配一个类（Java单继承限制）
 */
class USBCClassAdapter extends USBCDevice implements USBInterface {
    @Override
    public void connectUSB() {
        System.out.println("  🔄 类适配器转换中...");
        // 直接调用父类方法
        connectUSBC();
    }
}

// ========== 演示多适配器场景 ==========

/**
 * 另一个被适配者：Micro-USB设备
 */
class MicroUSBDevice {
    public void connectMicroUSB() {
        System.out.println("  🔌 Micro-USB设备已连接");
    }
}

/**
 * 对象适配器：可以适配Micro-USB
 * ✅ 灵活：可以适配多个不同的类
 */
class MicroUSBObjectAdapter implements USBInterface {
    private MicroUSBDevice microUSBDevice;

    public MicroUSBObjectAdapter(MicroUSBDevice microUSBDevice) {
        this.microUSBDevice = microUSBDevice;
    }

    @Override
    public void connectUSB() {
        System.out.println("  🔄 对象适配器转换中...");
        microUSBDevice.connectMicroUSB();
    }
}

/**
 * 类适配器：无法同时适配USB-C和Micro-USB
 * ❌ 限制：Java单继承，只能适配一个类
 */
// class MultiClassAdapter extends USBCDevice, MicroUSBDevice { ... }
// 编译错误！Java不支持多继承

// ========== 测试 ==========

public class ClassVsObjectAdapterDemo {
    public static void main(String[] args) {
        System.out.println("=== 类适配器 vs 对象适配器 ===\n");

        // 示例1：对象适配器
        System.out.println("【1. 对象适配器（组合）】");
        USBCDevice device1 = new USBCDevice();
        USBInterface adapter1 = new USBCObjectAdapter(device1);
        adapter1.connectUSB();

        // 示例2：类适配器
        System.out.println("\n【2. 类适配器（继承）】");
        USBInterface adapter2 = new USBCClassAdapter();
        adapter2.connectUSB();

        // 示例3：对象适配器的灵活性
        System.out.println("\n【3. 对象适配器的灵活性】");
        System.out.println("对象适配器可以适配多个不同的类：");
        USBInterface usbcAdapter = new USBCObjectAdapter(new USBCDevice());
        USBInterface microAdapter = new MicroUSBObjectAdapter(new MicroUSBDevice());
        usbcAdapter.connectUSB();
        microAdapter.connectUSB();

        // 示例4：类适配器的限制
        System.out.println("\n【4. 类适配器的限制】");
        System.out.println("❌ 类适配器无法同时适配USB-C和Micro-USB");
        System.out.println("   原因：Java单继承限制");
        System.out.println("   只能继承一个类（USBCDevice 或 MicroUSBDevice）");

        // 示例5：对象适配器可以复用被适配者
        System.out.println("\n【5. 对象适配器可以复用被适配者】");
        USBCDevice sharedDevice = new USBCDevice();
        USBInterface adapter3 = new USBCObjectAdapter(sharedDevice);
        USBInterface adapter4 = new USBCObjectAdapter(sharedDevice);
        System.out.println("多个适配器共享同一个USB-C设备：");
        adapter3.connectUSB();
        adapter4.connectUSB();

        // 示例6：访问被适配者的其他方法
        System.out.println("\n【6. 访问被适配者的其他方法】");
        USBCObjectAdapter objectAdapter = new USBCObjectAdapter(new USBCDevice());
        objectAdapter.connectUSB();
        objectAdapter.transferDataViaAdapter();

        USBCClassAdapter classAdapter = new USBCClassAdapter();
        classAdapter.connectUSB();
        classAdapter.transferData();  // 类适配器会暴露父类所有方法
        System.out.println("  ⚠️  类适配器暴露了被适配者的所有方法（可能不期望）");

        // 对比总结
        System.out.println("\n【7. 对比总结】");
        showComparison();
    }

    /**
     * 显示对比表格
     */
    static void showComparison() {
        System.out.println("\n┌──────────────┬────────────────┬────────────────┐");
        System.out.println("│ 对比维度     │ 对象适配器✅   │ 类适配器⚠️     │");
        System.out.println("├──────────────┼────────────────┼────────────────┤");
        System.out.println("│ 实现方式     │ 组合           │ 继承           │");
        System.out.println("│ 灵活性       │ 高             │ 低             │");
        System.out.println("│ 适配多个类   │ ✅ 可以        │ ❌ 不行        │");
        System.out.println("│ 代码量       │ 稍多           │ 简洁           │");
        System.out.println("│ 推荐度       │ ⭐⭐⭐⭐⭐    │ ⭐⭐           │");
        System.out.println("└──────────────┴────────────────┴────────────────┘");

        System.out.println("\n✅ 推荐：使用对象适配器（组合）");
        System.out.println("   理由：");
        System.out.println("   1. 更灵活（可以适配多个类）");
        System.out.println("   2. 符合组合复用原则");
        System.out.println("   3. 不暴露被适配者的其他方法");
        System.out.println("   4. 可以在运行时切换被适配者");

        System.out.println("\n⚠️  类适配器的问题：");
        System.out.println("   1. Java单继承限制");
        System.out.println("   2. 暴露被适配者的所有public方法");
        System.out.println("   3. 灵活性差");
    }
}
