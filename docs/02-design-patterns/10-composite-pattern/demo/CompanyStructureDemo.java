/**
 * 组合模式 - 公司组织架构示例
 *
 * 场景：公司组织架构（部门与员工）
 * 演示：统一接口处理部门和员工，计算总薪资
 */

// ========== 抽象构件 ==========

/**
 * Component：组织结构节点
 */
interface OrganizationComponent {
    void printStructure(String indent);
    double getTotalSalary();
    int getEmployeeCount();
}

// ========== 叶子节点 ==========

/**
 * Leaf：员工
 */
class Employee implements OrganizationComponent {
    private String name;
    private String position;
    private double salary;

    public Employee(String name, String position, double salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    @Override
    public void printStructure(String indent) {
        System.out.println(indent + "👨‍💼 " + name + " - " + position + " (¥" + salary + ")");
    }

    @Override
    public double getTotalSalary() {
        return salary;
    }

    @Override
    public int getEmployeeCount() {
        return 1;
    }
}

// ========== 容器节点 ==========

/**
 * Composite：部门
 */
class Department implements OrganizationComponent {
    private String name;
    private java.util.List<OrganizationComponent> members = new java.util.ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    // 添加成员（员工或子部门）
    public void add(OrganizationComponent component) {
        members.add(component);
    }

    // 删除成员
    public void remove(OrganizationComponent component) {
        members.remove(component);
    }

    @Override
    public void printStructure(String indent) {
        System.out.println(indent + "🏢 " + name + " (人数: " + getEmployeeCount() +
                           ", 总薪资: ¥" + getTotalSalary() + ")");
        for (OrganizationComponent member : members) {
            member.printStructure(indent + "  ");  // 递归显示
        }
    }

    @Override
    public double getTotalSalary() {
        double total = 0;
        for (OrganizationComponent member : members) {
            total += member.getTotalSalary();  // 递归计算
        }
        return total;
    }

    @Override
    public int getEmployeeCount() {
        int count = 0;
        for (OrganizationComponent member : members) {
            count += member.getEmployeeCount();  // 递归统计
        }
        return count;
    }
}

// ========== 测试 ==========

public class CompanyStructureDemo {
    public static void main(String[] args) {
        System.out.println("=== 组合模式 - 公司组织架构 ===\n");

        // 示例1：构建组织架构
        System.out.println("【1. 公司组织架构】");
        OrganizationComponent company = buildCompany();
        company.printStructure("");

        // 示例2：统计信息
        System.out.println("\n【2. 统计信息】");
        printStatistics(company);

        // 示例3：部门级统计
        System.out.println("\n【3. 部门级统计】");
        demonstrateDepartmentStats();

        // 示例4：动态调整
        System.out.println("\n【4. 动态调整组织架构】");
        demonstrateDynamicAdjustment();
    }

    /**
     * 构建公司组织架构
     */
    static OrganizationComponent buildCompany() {
        // 公司
        Department company = new Department("科技有限公司");

        // CEO
        company.add(new Employee("张三", "CEO", 50000));

        // 技术部
        Department techDept = new Department("技术部");
        techDept.add(new Employee("李四", "技术总监", 35000));
        techDept.add(new Employee("王五", "架构师", 30000));

        // 技术部 - 前端组
        Department frontendTeam = new Department("前端组");
        frontendTeam.add(new Employee("赵六", "前端Leader", 25000));
        frontendTeam.add(new Employee("钱七", "前端工程师", 18000));
        frontendTeam.add(new Employee("孙八", "前端工程师", 16000));
        techDept.add(frontendTeam);

        // 技术部 - 后端组
        Department backendTeam = new Department("后端组");
        backendTeam.add(new Employee("周九", "后端Leader", 25000));
        backendTeam.add(new Employee("吴十", "后端工程师", 20000));
        backendTeam.add(new Employee("郑十一", "后端工程师", 19000));
        techDept.add(backendTeam);

        company.add(techDept);

        // 市场部
        Department marketDept = new Department("市场部");
        marketDept.add(new Employee("冯十二", "市场总监", 32000));
        marketDept.add(new Employee("陈十三", "市场专员", 15000));
        marketDept.add(new Employee("褚十四", "市场专员", 14000));
        company.add(marketDept);

        // 人事部
        Department hrDept = new Department("人事部");
        hrDept.add(new Employee("卫十五", "人事经理", 22000));
        hrDept.add(new Employee("蒋十六", "招聘专员", 12000));
        company.add(hrDept);

        return company;
    }

    /**
     * 打印统计信息
     */
    static void printStatistics(OrganizationComponent org) {
        System.out.println("公司总人数: " + org.getEmployeeCount() + " 人");
        System.out.println("公司总薪资: ¥" + org.getTotalSalary());
        System.out.println("平均薪资: ¥" + (org.getTotalSalary() / org.getEmployeeCount()));

        System.out.println("\n✅ 优势: 统一接口轻松获取统计信息");
        System.out.println("  - 员工：直接返回1人，自己的薪资");
        System.out.println("  - 部门：递归统计所有下属的人数和薪资");
    }

    /**
     * 演示部门级统计
     */
    static void demonstrateDepartmentStats() {
        Department techDept = new Department("技术部");
        techDept.add(new Employee("张三", "工程师", 20000));
        techDept.add(new Employee("李四", "工程师", 22000));

        Department frontendTeam = new Department("前端组");
        frontendTeam.add(new Employee("王五", "前端工程师", 18000));
        techDept.add(frontendTeam);

        System.out.println("技术部组织架构:");
        techDept.printStructure("");

        System.out.println("\n技术部统计:");
        System.out.println("  人数: " + techDept.getEmployeeCount());
        System.out.println("  总薪资: ¥" + techDept.getTotalSalary());

        System.out.println("\n✅ 优势: 可以对任意层级的组织单元进行统计");
    }

    /**
     * 演示动态调整
     */
    static void demonstrateDynamicAdjustment() {
        Department dept = new Department("研发部");
        dept.add(new Employee("张三", "工程师", 20000));

        System.out.println("初始状态:");
        dept.printStructure("");
        System.out.println("人数: " + dept.getEmployeeCount() + ", 总薪资: ¥" + dept.getTotalSalary());

        // 新增员工
        dept.add(new Employee("李四", "工程师", 22000));

        System.out.println("\n新增员工后:");
        dept.printStructure("");
        System.out.println("人数: " + dept.getEmployeeCount() + ", 总薪资: ¥" + dept.getTotalSalary());

        // 新增子部门
        Department subTeam = new Department("测试组");
        subTeam.add(new Employee("王五", "测试工程师", 18000));
        dept.add(subTeam);

        System.out.println("\n新增测试组后:");
        dept.printStructure("");
        System.out.println("人数: " + dept.getEmployeeCount() + ", 总薪资: ¥" + dept.getTotalSalary());

        System.out.println("\n✅ 优势: 灵活调整组织结构，统计信息自动更新");
    }
}
