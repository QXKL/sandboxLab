/**
 * 原型模式 - 原型注册表
 *
 * 场景：游戏角色系统
 * 演示：管理多个原型对象，通过key快速克隆
 */

import java.util.HashMap;
import java.util.Map;

// ========== 抽象原型 ==========

interface Prototype extends Cloneable {
    Prototype clone();
}

// ========== 具体原型：游戏角色 ==========

class GameCharacter implements Prototype {
    private String name;
    private String type;
    private int health;
    private int attack;
    private int defense;
    private String weapon;

    public GameCharacter(String name, String type, int health, int attack, int defense, String weapon) {
        this.name = name;
        this.type = type;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.weapon = weapon;
    }

    @Override
    public GameCharacter clone() {
        try {
            return (GameCharacter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public int getHealth() { return health; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public String getWeapon() { return weapon; }

    public void showInfo() {
        System.out.printf("  %s (%s) - HP:%d, ATK:%d, DEF:%d, 武器:%s\n",
                name, type, health, attack, defense, weapon);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)[HP:%d,ATK:%d,DEF:%d,%s]",
                name, type, health, attack, defense, weapon);
    }
}

// ========== 原型注册表 ==========

class PrototypeRegistry {
    private Map<String, Prototype> prototypes = new HashMap<>();

    /**
     * 注册原型
     */
    public void register(String key, Prototype prototype) {
        prototypes.put(key, prototype);
        System.out.println("✅ 注册原型: " + key);
    }

    /**
     * 获取克隆对象
     */
    public Prototype getPrototype(String key) {
        Prototype prototype = prototypes.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("原型不存在: " + key);
        }
        return prototype.clone();
    }

    /**
     * 移除原型
     */
    public void unregister(String key) {
        prototypes.remove(key);
        System.out.println("🗑️  移除原型: " + key);
    }

    /**
     * 列出所有原型
     */
    public void listPrototypes() {
        System.out.println("\n当前注册的原型：");
        if (prototypes.isEmpty()) {
            System.out.println("  (无)");
        } else {
            prototypes.forEach((key, prototype) -> {
                System.out.println("  - " + key + ": " + prototype);
            });
        }
    }
}

// ========== 客户端 ==========

public class PrototypeRegistryDemo {
    public static void main(String[] args) {
        System.out.println("=== 原型模式 - 原型注册表 ===\n");

        // 创建原型注册表
        PrototypeRegistry registry = new PrototypeRegistry();

        // 示例1：注册原型
        System.out.println("【1. 注册原型】\n");
        registerPrototypes(registry);

        // 示例2：使用原型创建角色
        System.out.println("\n【2. 通过原型快速创建角色】\n");
        createCharacters(registry);

        // 示例3：原型的优势
        System.out.println("\n【3. 原型模式的优势】\n");
        demonstrateAdvantages(registry);

        // 示例4：动态管理原型
        System.out.println("\n【4. 动态管理原型】\n");
        dynamicManagement(registry);
    }

    /**
     * 注册原型
     */
    static void registerPrototypes(PrototypeRegistry registry) {
        // 战士原型
        GameCharacter warrior = new GameCharacter(
                "战士原型", "Warrior", 150, 30, 20, "大剑"
        );
        registry.register("warrior", warrior);

        // 法师原型
        GameCharacter mage = new GameCharacter(
                "法师原型", "Mage", 80, 50, 10, "魔杖"
        );
        registry.register("mage", mage);

        // 刺客原型
        GameCharacter assassin = new GameCharacter(
                "刺客原型", "Assassin", 100, 40, 15, "双刃"
        );
        registry.register("assassin", assassin);

        registry.listPrototypes();
    }

    /**
     * 使用原型创建角色
     */
    static void createCharacters(PrototypeRegistry registry) {
        System.out.println("创建3个战士：");
        for (int i = 1; i <= 3; i++) {
            GameCharacter warrior = (GameCharacter) registry.getPrototype("warrior");
            warrior.setName("战士" + i);
            warrior.showInfo();
        }

        System.out.println("\n创建2个法师：");
        for (int i = 1; i <= 2; i++) {
            GameCharacter mage = (GameCharacter) registry.getPrototype("mage");
            mage.setName("法师" + i);
            mage.showInfo();
        }

        System.out.println("\n创建1个刺客：");
        GameCharacter assassin = (GameCharacter) registry.getPrototype("assassin");
        assassin.setName("刺客1");
        assassin.showInfo();
    }

    /**
     * 演示原型模式的优势
     */
    static void demonstrateAdvantages(PrototypeRegistry registry) {
        System.out.println("对比：直接new vs 原型克隆\n");

        // 方式1：直接new（需要设置所有属性）
        System.out.println("方式1：直接new");
        long start1 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            GameCharacter warrior = new GameCharacter(
                    "战士" + i, "Warrior", 150, 30, 20, "大剑"
            );
        }
        long time1 = System.nanoTime() - start1;
        System.out.println("  创建10000个战士耗时: " + time1 / 1_000_000 + "ms");
        System.out.println("  问题：每次都要设置所有属性\n");

        // 方式2：原型克隆
        System.out.println("方式2：原型克隆");
        long start2 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            GameCharacter warrior = (GameCharacter) registry.getPrototype("warrior");
            warrior.setName("战士" + i);
        }
        long time2 = System.nanoTime() - start2;
        System.out.println("  创建10000个战士耗时: " + time2 / 1_000_000 + "ms");
        System.out.println("  优点：快速克隆，只需修改个别属性\n");

        System.out.println("✅ 原型模式性能提升: " + ((time1 - time2) * 100 / time1) + "%");
    }

    /**
     * 动态管理原型
     */
    static void dynamicManagement(PrototypeRegistry registry) {
        // 新增原型
        System.out.println("新增原型：弓箭手");
        GameCharacter archer = new GameCharacter(
                "弓箭手原型", "Archer", 110, 35, 12, "长弓"
        );
        registry.register("archer", archer);
        registry.listPrototypes();

        // 使用新原型
        System.out.println("\n使用弓箭手原型：");
        GameCharacter archer1 = (GameCharacter) registry.getPrototype("archer");
        archer1.setName("弓箭手1");
        archer1.showInfo();

        // 移除原型
        System.out.println("\n移除刺客原型：");
        registry.unregister("assassin");
        registry.listPrototypes();

        // 尝试使用已移除的原型
        System.out.println("\n尝试使用已移除的原型：");
        try {
            registry.getPrototype("assassin");
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ 错误: " + e.getMessage());
        }
    }
}
