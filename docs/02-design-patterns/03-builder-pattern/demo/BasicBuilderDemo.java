/**
 * 建造者模式 - 基础示例
 *
 * 场景：组装电脑系统
 * 演示：经典建造者模式的完整结构（Product + Builder + Director）
 */

// ========== 产品类 ==========

class Computer {
    private String cpu;
    private String memory;
    private String storage;
    private String gpu;
    private String motherboard;
    private String powerSupply;

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard;
    }

    public void setPowerSupply(String powerSupply) {
        this.powerSupply = powerSupply;
    }

    public void showSpecs() {
        System.out.println("=== 电脑配置 ===");
        System.out.println("CPU: " + cpu);
        System.out.println("内存: " + memory);
        System.out.println("硬盘: " + storage);
        System.out.println("显卡: " + gpu);
        System.out.println("主板: " + motherboard);
        System.out.println("电源: " + powerSupply);
        System.out.println();
    }
}

// ========== 抽象建造者 ==========

interface ComputerBuilder {
    ComputerBuilder buildCPU(String cpu);
    ComputerBuilder buildMemory(String memory);
    ComputerBuilder buildStorage(String storage);
    ComputerBuilder buildGPU(String gpu);
    ComputerBuilder buildMotherboard(String motherboard);
    ComputerBuilder buildPowerSupply(String powerSupply);
    Computer build();
}

// ========== 具体建造者1：办公电脑 ==========

class OfficeComputerBuilder implements ComputerBuilder {
    private Computer computer = new Computer();

    @Override
    public ComputerBuilder buildCPU(String cpu) {
        computer.setCpu(cpu);
        return this;
    }

    @Override
    public ComputerBuilder buildMemory(String memory) {
        computer.setMemory(memory);
        return this;
    }

    @Override
    public ComputerBuilder buildStorage(String storage) {
        computer.setStorage(storage);
        return this;
    }

    @Override
    public ComputerBuilder buildGPU(String gpu) {
        computer.setGpu(gpu);
        return this;
    }

    @Override
    public ComputerBuilder buildMotherboard(String motherboard) {
        computer.setMotherboard(motherboard);
        return this;
    }

    @Override
    public ComputerBuilder buildPowerSupply(String powerSupply) {
        computer.setPowerSupply(powerSupply);
        return this;
    }

    @Override
    public Computer build() {
        return computer;
    }
}

// ========== 具体建造者2：游戏电脑 ==========

class GamingComputerBuilder implements ComputerBuilder {
    private Computer computer = new Computer();

    @Override
    public ComputerBuilder buildCPU(String cpu) {
        computer.setCpu(cpu);
        return this;
    }

    @Override
    public ComputerBuilder buildMemory(String memory) {
        computer.setMemory(memory);
        return this;
    }

    @Override
    public ComputerBuilder buildStorage(String storage) {
        computer.setStorage(storage);
        return this;
    }

    @Override
    public ComputerBuilder buildGPU(String gpu) {
        computer.setGpu(gpu);
        return this;
    }

    @Override
    public ComputerBuilder buildMotherboard(String motherboard) {
        computer.setMotherboard(motherboard);
        return this;
    }

    @Override
    public ComputerBuilder buildPowerSupply(String powerSupply) {
        computer.setPowerSupply(powerSupply);
        return this;
    }

    @Override
    public Computer build() {
        return computer;
    }
}

// ========== 导演类 ==========

class ComputerDirector {
    /**
     * 构建办公电脑（预定义配置）
     */
    public Computer constructOfficeComputer(ComputerBuilder builder) {
        return builder
                .buildCPU("Intel i5-13400")
                .buildMemory("16GB DDR4")
                .buildStorage("512GB SSD")
                .buildGPU("集成显卡")
                .buildMotherboard("B660主板")
                .buildPowerSupply("450W电源")
                .build();
    }

    /**
     * 构建游戏电脑（预定义配置）
     */
    public Computer constructGamingComputer(ComputerBuilder builder) {
        return builder
                .buildCPU("Intel i9-13900K")
                .buildMemory("32GB DDR5")
                .buildStorage("2TB NVMe SSD")
                .buildGPU("RTX 4090")
                .buildMotherboard("Z790主板")
                .buildPowerSupply("1000W电源")
                .build();
    }

    /**
     * 构建设计师电脑（预定义配置）
     */
    public Computer constructDesignerComputer(ComputerBuilder builder) {
        return builder
                .buildCPU("AMD Ryzen 9 7950X")
                .buildMemory("64GB DDR5")
                .buildStorage("4TB NVMe SSD")
                .buildGPU("RTX 4080")
                .buildMotherboard("X670主板")
                .buildPowerSupply("850W电源")
                .build();
    }
}

// ========== 客户端 ==========

public class BasicBuilderDemo {
    public static void main(String[] args) {
        System.out.println("=== 建造者模式 - 基础示例 ===\n");

        // 创建导演和建造者
        ComputerDirector director = new ComputerDirector();

        // 示例1：使用Director构建预定义配置
        System.out.println("【1. 使用Director构建预定义配置】\n");

        System.out.println("办公电脑配置：");
        ComputerBuilder officeBuilder = new OfficeComputerBuilder();
        Computer officePC = director.constructOfficeComputer(officeBuilder);
        officePC.showSpecs();

        System.out.println("游戏电脑配置：");
        ComputerBuilder gamingBuilder = new GamingComputerBuilder();
        Computer gamingPC = director.constructGamingComputer(gamingBuilder);
        gamingPC.showSpecs();

        System.out.println("设计师电脑配置：");
        ComputerBuilder designerBuilder = new GamingComputerBuilder();
        Computer designerPC = director.constructDesignerComputer(designerBuilder);
        designerPC.showSpecs();

        // 示例2：客户端直接使用Builder（自定义配置）
        System.out.println("【2. 客户端自定义配置（不使用Director）】\n");

        System.out.println("自定义游戏电脑：");
        Computer customPC = new GamingComputerBuilder()
                .buildCPU("Intel i7-13700K")
                .buildMemory("32GB DDR5")
                .buildStorage("1TB SSD + 2TB HDD")
                .buildGPU("RTX 4070 Ti")
                .buildMotherboard("Z690主板")
                .buildPowerSupply("750W电源")
                .build();
        customPC.showSpecs();

        // 示例3：对比有Director和无Director
        System.out.println("【3. Director的作用】");
        System.out.println("✅ 有Director：");
        System.out.println("  - 封装构建步骤，客户端无需知道细节");
        System.out.println("  - 提供预定义配置（办公、游戏、设计师）");
        System.out.println("  - 构建逻辑可复用");
        System.out.println();
        System.out.println("⚠️  无Director：");
        System.out.println("  - 客户端需要知道所有构建步骤");
        System.out.println("  - 灵活性更高，可以完全自定义");
        System.out.println("  - 适合配置多样化的场景");
    }
}
