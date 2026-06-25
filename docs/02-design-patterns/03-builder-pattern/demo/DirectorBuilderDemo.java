/**
 * 建造者模式 - Director示例
 *
 * 场景：快餐套餐构建器
 * 演示：Director如何封装构建流程，提供预定义套餐
 */

import java.util.ArrayList;
import java.util.List;

// ========== 产品类 ==========

/**
 * 食品项接口
 */
interface Item {
    String getName();
    double getPrice();
    String getPacking();
}

/**
 * 汉堡抽象类
 */
abstract class Burger implements Item {
    @Override
    public String getPacking() {
        return "盒装";
    }
}

/**
 * 饮料抽象类
 */
abstract class Drink implements Item {
    @Override
    public String getPacking() {
        return "杯装";
    }
}

// ========== 具体产品 ==========

class VegBurger extends Burger {
    @Override
    public String getName() {
        return "素食汉堡";
    }

    @Override
    public double getPrice() {
        return 25.0;
    }
}

class ChickenBurger extends Burger {
    @Override
    public String getName() {
        return "鸡肉汉堡";
    }

    @Override
    public double getPrice() {
        return 35.0;
    }
}

class BeefBurger extends Burger {
    @Override
    public String getName() {
        return "牛肉汉堡";
    }

    @Override
    public double getPrice() {
        return 45.0;
    }
}

class Coke extends Drink {
    @Override
    public String getName() {
        return "可乐";
    }

    @Override
    public double getPrice() {
        return 8.0;
    }
}

class Juice extends Drink {
    @Override
    public String getName() {
        return "果汁";
    }

    @Override
    public double getPrice() {
        return 12.0;
    }
}

class Fries {
    public String getName() {
        return "薯条";
    }

    public double getPrice() {
        return 10.0;
    }

    public String getPacking() {
        return "纸袋";
    }
}

// ========== 套餐类（Product） ==========

class Meal {
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
    }

    public double getCost() {
        return items.stream()
                .mapToDouble(Item::getPrice)
                .sum();
    }

    public void showItems() {
        System.out.println("=== 套餐内容 ===");
        for (Item item : items) {
            System.out.printf("  %-12s  %s  ¥%.2f\n",
                item.getName(), item.getPacking(), item.getPrice());
        }
        System.out.printf("总价: ¥%.2f\n", getCost());
        System.out.println();
    }
}

// ========== 建造者 ==========

class MealBuilder {
    private Meal meal = new Meal();

    public MealBuilder addBurger(Burger burger) {
        meal.addItem(burger);
        return this;
    }

    public MealBuilder addDrink(Drink drink) {
        meal.addItem(drink);
        return this;
    }

    public MealBuilder addFries() {
        Fries fries = new Fries();
        meal.addItem(new Item() {
            @Override
            public String getName() {
                return fries.getName();
            }

            @Override
            public double getPrice() {
                return fries.getPrice();
            }

            @Override
            public String getPacking() {
                return fries.getPacking();
            }
        });
        return this;
    }

    public Meal build() {
        return meal;
    }

    public void reset() {
        this.meal = new Meal();
    }
}

// ========== 导演类 ==========

class MealDirector {
    private MealBuilder builder;

    public MealDirector(MealBuilder builder) {
        this.builder = builder;
    }

    /**
     * 素食套餐
     */
    public Meal prepareVegMeal() {
        builder.reset();
        return builder
                .addBurger(new VegBurger())
                .addDrink(new Juice())
                .addFries()
                .build();
    }

    /**
     * 标准套餐
     */
    public Meal prepareStandardMeal() {
        builder.reset();
        return builder
                .addBurger(new ChickenBurger())
                .addDrink(new Coke())
                .addFries()
                .build();
    }

    /**
     * 豪华套餐
     */
    public Meal prepareLuxuryMeal() {
        builder.reset();
        return builder
                .addBurger(new BeefBurger())
                .addDrink(new Juice())
                .addFries()
                .build();
    }

    /**
     * 儿童套餐
     */
    public Meal prepareKidsMeal() {
        builder.reset();
        return builder
                .addBurger(new ChickenBurger())
                .addDrink(new Juice())
                .build();
    }
}

// ========== 客户端 ==========

public class DirectorBuilderDemo {
    public static void main(String[] args) {
        System.out.println("=== 建造者模式 - Director示例 ===\n");

        // 创建建造者和导演
        MealBuilder builder = new MealBuilder();
        MealDirector director = new MealDirector(builder);

        // 示例1：使用Director构建预定义套餐
        System.out.println("【1. 使用Director构建预定义套餐】\n");

        System.out.println("素食套餐：");
        Meal vegMeal = director.prepareVegMeal();
        vegMeal.showItems();

        System.out.println("标准套餐：");
        Meal standardMeal = director.prepareStandardMeal();
        standardMeal.showItems();

        System.out.println("豪华套餐：");
        Meal luxuryMeal = director.prepareLuxuryMeal();
        luxuryMeal.showItems();

        System.out.println("儿童套餐：");
        Meal kidsMeal = director.prepareKidsMeal();
        kidsMeal.showItems();

        // 示例2：客户端自定义套餐（不使用Director）
        System.out.println("【2. 客户端自定义套餐（不使用Director）】\n");

        MealBuilder customBuilder = new MealBuilder();
        Meal customMeal = customBuilder
                .addBurger(new BeefBurger())
                .addBurger(new ChickenBurger())  // 两个汉堡
                .addDrink(new Coke())
                .addDrink(new Juice())           // 两杯饮料
                .addFries()
                .build();

        System.out.println("自定义双汉堡套餐：");
        customMeal.showItems();

        // 示例3：Director的优势
        System.out.println("【3. Director的作用】\n");

        System.out.println("✅ 使用Director的优势：");
        System.out.println("  1. 封装构建步骤");
        System.out.println("     客户端：director.prepareVegMeal()");
        System.out.println("     无需知道：素食套餐包含哪些食品\n");

        System.out.println("  2. 提供预定义套餐");
        System.out.println("     - 素食套餐");
        System.out.println("     - 标准套餐");
        System.out.println("     - 豪华套餐");
        System.out.println("     - 儿童套餐\n");

        System.out.println("  3. 构建逻辑复用");
        System.out.println("     套餐配置变更时，只需修改Director\n");

        System.out.println("  4. 简化客户端代码");
        System.out.println("     一行代码获取完整套餐\n");

        System.out.println("⚠️  不使用Director：");
        System.out.println("  - 灵活性更高，可以完全自定义");
        System.out.println("  - 适合需要高度定制化的场景");
        System.out.println("  - 客户端需要知道所有构建步骤");

        // 示例4：实际业务场景
        System.out.println("\n【4. 实际业务场景】\n");

        System.out.println("场景1：快餐店点餐系统");
        System.out.println("  - 顾客点\"标准套餐\" → Director.prepareStandardMeal()");
        System.out.println("  - 顾客自己配 → 直接使用Builder\n");

        System.out.println("场景2：报表生成系统");
        System.out.println("  - 月度报表 → Director.prepareMonthlyReport()");
        System.out.println("  - 年度报表 → Director.prepareAnnualReport()");
        System.out.println("  - 自定义报表 → 直接使用Builder\n");

        System.out.println("场景3：电脑配置系统");
        System.out.println("  - 办公电脑 → Director.constructOfficePC()");
        System.out.println("  - 游戏电脑 → Director.constructGamingPC()");
        System.out.println("  - DIY配置 → 直接使用Builder");

        // 示例5：何时使用Director？
        System.out.println("\n【5. 何时使用Director？】\n");

        System.out.println("✅ 适合使用Director：");
        System.out.println("  1. 有明确的预定义配置（套餐、模板）");
        System.out.println("  2. 构建步骤复杂，客户端不应关心细节");
        System.out.println("  3. 同一种构建流程需要复用");
        System.out.println("  4. 想要简化客户端代码\n");

        System.out.println("❌ 不需要Director：");
        System.out.println("  1. 构建步骤简单");
        System.out.println("  2. 需要高度定制化");
        System.out.println("  3. 没有预定义配置");
        System.out.println("  4. 客户端愿意直接控制构建过程");
    }
}
