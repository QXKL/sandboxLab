/**
 * 装饰器模式 - 咖啡装饰器示例
 *
 * 场景：咖啡店点单系统，支持动态添加配料
 * 演示：装饰器的基本使用、价格累加、描述拼接
 */

// ========== Component接口 ==========

/**
 * 组件接口：咖啡
 * 所有咖啡和装饰器都实现这个接口
 */
interface Coffee {
    /**
     * 获取价格
     */
    double getPrice();

    /**
     * 获取描述
     */
    String getDescription();
}

// ========== ConcreteComponent（具体组件） ==========

/**
 * 具体组件1：美式咖啡
 */
class Americano implements Coffee {
    @Override
    public double getPrice() {
        return 15.0;
    }

    @Override
    public String getDescription() {
        return "美式咖啡";
    }
}

/**
 * 具体组件2：浓缩咖啡
 */
class Espresso implements Coffee {
    @Override
    public double getPrice() {
        return 12.0;
    }

    @Override
    public String getDescription() {
        return "浓缩咖啡";
    }
}

/**
 * 具体组件3：拿铁
 */
class Latte implements Coffee {
    @Override
    public double getPrice() {
        return 18.0;
    }

    @Override
    public String getDescription() {
        return "拿铁";
    }
}

// ========== Decorator（抽象装饰器） ==========

/**
 * 抽象装饰器：配料
 * 持有一个Coffee对象，实现Coffee接口
 */
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;  // 被装饰的咖啡

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    /**
     * 默认实现：转发到被装饰对象
     */
    @Override
    public double getPrice() {
        return coffee.getPrice();
    }

    @Override
    public String getDescription() {
        return coffee.getDescription();
    }
}

// ========== ConcreteDecorator（具体装饰器） ==========

/**
 * 具体装饰器1：牛奶
 */
class Milk extends CoffeeDecorator {
    public Milk(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return coffee.getPrice() + 2.0;  // 原价格 + 牛奶价格
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + " + 牛奶";
    }
}

/**
 * 具体装饰器2：糖
 */
class Sugar extends CoffeeDecorator {
    public Sugar(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return coffee.getPrice() + 1.0;  // 原价格 + 糖价格
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + " + 糖";
    }
}

/**
 * 具体装饰器3：摩卡
 */
class Mocha extends CoffeeDecorator {
    public Mocha(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return coffee.getPrice() + 3.0;  // 原价格 + 摩卡价格
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + " + 摩卡";
    }
}

/**
 * 具体装饰器4：奶油
 */
class Whip extends CoffeeDecorator {
    public Whip(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return coffee.getPrice() + 2.5;  // 原价格 + 奶油价格
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + " + 奶油";
    }
}

// ========== 测试 ==========

public class CoffeeDecoratorDemo {
    public static void main(String[] args) {
        System.out.println("=== 装饰器模式 - 咖啡装饰器 ===\n");

        // 示例1：基础咖啡
        System.out.println("【1. 基础咖啡（无装饰）】");
        Coffee americano = new Americano();
        printCoffee(americano);

        // 示例2：单层装饰
        System.out.println("\n【2. 单层装饰】");
        Coffee americanoWithMilk = new Milk(new Americano());
        printCoffee(americanoWithMilk);

        // 示例3：多层装饰
        System.out.println("\n【3. 多层装饰】");
        Coffee fancyCoffee = new Americano();
        fancyCoffee = new Milk(fancyCoffee);
        fancyCoffee = new Sugar(fancyCoffee);
        fancyCoffee = new Mocha(fancyCoffee);
        printCoffee(fancyCoffee);

        // 示例4：不同的组合
        System.out.println("\n【4. 不同的组合】");

        // 拿铁 + 奶油 + 糖
        Coffee latte = new Latte();
        latte = new Whip(latte);
        latte = new Sugar(latte);
        printCoffee(latte);

        // 浓缩 + 摩卡 + 牛奶 + 奶油
        Coffee espresso = new Espresso();
        espresso = new Mocha(espresso);
        espresso = new Milk(espresso);
        espresso = new Whip(espresso);
        printCoffee(espresso);

        // 示例5：装饰器的灵活性
        System.out.println("\n【5. 装饰器的灵活性】");
        demonstrateFlexibility();

        // 示例6：装饰器 vs 继承
        System.out.println("\n【6. 装饰器 vs 继承】");
        compareWithInheritance();
    }

    /**
     * 打印咖啡信息
     */
    static void printCoffee(Coffee coffee) {
        System.out.println("  ☕ " + coffee.getDescription());
        System.out.println("  💰 价格: " + coffee.getPrice() + "元");
    }

    /**
     * 演示装饰器的灵活性
     */
    static void demonstrateFlexibility() {
        System.out.println("\n✅ 优势1: 运行时动态组合");
        System.out.println("   可以根据用户选择动态添加配料");

        // 模拟用户选择
        boolean wantMilk = true;
        boolean wantSugar = false;
        boolean wantMocha = true;

        Coffee coffee = new Americano();
        if (wantMilk) {
            coffee = new Milk(coffee);
        }
        if (wantSugar) {
            coffee = new Sugar(coffee);
        }
        if (wantMocha) {
            coffee = new Mocha(coffee);
        }

        printCoffee(coffee);

        System.out.println("\n✅ 优势2: 避免类爆炸");
        System.out.println("   如果用继承实现：");
        System.out.println("   - 3种咖啡 × 2^4种配料组合 = 48个类");
        System.out.println("   用装饰器实现：");
        System.out.println("   - 3种咖啡 + 4种配料 = 7个类");

        System.out.println("\n✅ 优势3: 符合开闭原则");
        System.out.println("   新增配料：只需添加新的装饰器类");
        System.out.println("   不需要修改现有代码");
    }

    /**
     * 装饰器 vs 继承对比
     */
    static void compareWithInheritance() {
        System.out.println("\n如果用继承实现配料组合：");
        System.out.println("  class AmericanoWithMilk extends Americano { }");
        System.out.println("  class AmericanoWithMilkAndSugar extends Americano { }");
        System.out.println("  class AmericanoWithMilkSugarMocha extends Americano { }");
        System.out.println("  ... (还有很多类)");

        System.out.println("\n❌ 继承的问题：");
        System.out.println("  1. 类爆炸：组合太多，类数量指数增长");
        System.out.println("  2. 不灵活：编译时确定，无法运行时调整");
        System.out.println("  3. 违反开闭原则：新增配料要修改代码");
        System.out.println("  4. 重复代码：相似逻辑在多个类中重复");

        System.out.println("\n✅ 装饰器的优势：");
        System.out.println("  1. 类数量线性增长：基础类 + 装饰器类");
        System.out.println("  2. 灵活：运行时动态组合");
        System.out.println("  3. 符合开闭原则：扩展无需修改");
        System.out.println("  4. 单一职责：每个装饰器只负责一种配料");

        System.out.println("\n📊 递归调用过程：");
        System.out.println("  Coffee coffee = new Mocha(new Sugar(new Milk(new Americano())));");
        System.out.println("  coffee.getPrice() 的调用链：");
        System.out.println("    Mocha.getPrice()");
        System.out.println("      → Sugar.getPrice() + 3.0");
        System.out.println("        → Milk.getPrice() + 1.0");
        System.out.println("          → Americano.getPrice() + 2.0");
        System.out.println("            → 15.0");
        System.out.println("    最终结果: 15 + 2 + 1 + 3 = 21元");
    }
}
