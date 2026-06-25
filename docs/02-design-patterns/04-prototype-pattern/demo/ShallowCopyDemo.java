/**
 * 原型模式 - 浅拷贝问题演示
 *
 * 场景：学生选课系统
 * 演示：浅拷贝导致的引用共享问题
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ========== 浅拷贝示例 ==========

class Student implements Cloneable {
    private String name;
    private int age;
    private List<String> courses;  // 引用类型字段

    public Student(String name, int age, List<String> courses) {
        this.name = name;
        this.age = age;
        this.courses = courses;
    }

    // 浅拷贝：使用Object.clone()
    @Override
    public Student clone() {
        try {
            // super.clone()执行浅拷贝
            return (Student) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public List<String> getCourses() { return courses; }

    @Override
    public String toString() {
        return "Student{name='" + name + "', age=" + age + ", courses=" + courses + "}";
    }
}

// ========== 深拷贝示例 ==========

class StudentDeep implements Cloneable {
    private String name;
    private int age;
    private List<String> courses;

    public StudentDeep(String name, int age, List<String> courses) {
        this.name = name;
        this.age = age;
        this.courses = courses;
    }

    // 深拷贝：手动复制引用类型字段
    @Override
    public StudentDeep clone() {
        try {
            StudentDeep cloned = (StudentDeep) super.clone();
            // 深拷贝List
            cloned.courses = new ArrayList<>(this.courses);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public List<String> getCourses() { return courses; }

    @Override
    public String toString() {
        return "StudentDeep{name='" + name + "', age=" + age + ", courses=" + courses + "}";
    }
}

// ========== 客户端 ==========

public class ShallowCopyDemo {
    public static void main(String[] args) {
        System.out.println("=== 原型模式 - 浅拷贝 vs 深拷贝 ===\n");

        // 示例1：浅拷贝的问题
        System.out.println("【1. 浅拷贝的问题】\n");
        testShallowCopy();

        // 示例2：深拷贝的解决方案
        System.out.println("\n【2. 深拷贝的解决方案】\n");
        testDeepCopy();

        // 示例3：对比总结
        System.out.println("\n【3. 浅拷贝 vs 深拷贝】\n");
        compareShallowAndDeep();
    }

    /**
     * 测试浅拷贝的问题
     */
    static void testShallowCopy() {
        // 创建原始对象
        List<String> courses = new ArrayList<>(Arrays.asList("Math", "English"));
        Student original = new Student("Alice", 20, courses);

        System.out.println("原始对象: " + original);

        // 克隆对象（浅拷贝）
        Student copy = original.clone();
        System.out.println("克隆对象: " + copy);

        // 验证是否是不同对象
        System.out.println("是否是同一对象: " + (original == copy));
        System.out.println("courses是否是同一对象: " + (original.getCourses() == copy.getCourses()));

        System.out.println("\n修改克隆对象的基本类型字段...");
        copy.setName("Bob");
        copy.setAge(21);
        System.out.println("原始对象: " + original);
        System.out.println("克隆对象: " + copy);
        System.out.println("✅ 基本类型字段独立，互不影响");

        System.out.println("\n修改克隆对象的引用类型字段...");
        copy.getCourses().add("Science");
        System.out.println("原始对象: " + original);
        System.out.println("克隆对象: " + copy);
        System.out.println("❌ 问题：引用类型字段被共享，修改副本影响了原对象！");
    }

    /**
     * 测试深拷贝
     */
    static void testDeepCopy() {
        // 创建原始对象
        List<String> courses = new ArrayList<>(Arrays.asList("Math", "English"));
        StudentDeep original = new StudentDeep("Alice", 20, courses);

        System.out.println("原始对象: " + original);

        // 克隆对象（深拷贝）
        StudentDeep copy = original.clone();
        System.out.println("克隆对象: " + copy);

        // 验证是否是不同对象
        System.out.println("是否是同一对象: " + (original == copy));
        System.out.println("courses是否是同一对象: " + (original.getCourses() == copy.getCourses()));

        System.out.println("\n修改克隆对象的所有字段...");
        copy.setName("Bob");
        copy.setAge(21);
        copy.getCourses().add("Science");

        System.out.println("原始对象: " + original);
        System.out.println("克隆对象: " + copy);
        System.out.println("✅ 深拷贝：所有字段都独立，互不影响");
    }

    /**
     * 对比浅拷贝和深拷贝
     */
    static void compareShallowAndDeep() {
        System.out.println("浅拷贝（Shallow Copy）：");
        System.out.println("  - 只复制对象本身");
        System.out.println("  - 基本类型字段：复制值");
        System.out.println("  - 引用类型字段：复制引用（共享）");
        System.out.println("  - 实现：super.clone()");
        System.out.println("  - 风险：修改副本可能影响原对象");
        System.out.println();

        System.out.println("深拷贝（Deep Copy）：");
        System.out.println("  - 复制对象及其引用的所有对象");
        System.out.println("  - 基本类型字段：复制值");
        System.out.println("  - 引用类型字段：递归复制（独立）");
        System.out.println("  - 实现：手动复制引用字段");
        System.out.println("  - 优点：完全独立，互不影响");
        System.out.println();

        System.out.println("如何选择？");
        System.out.println("  - 引用字段不会修改 → 浅拷贝（性能好）");
        System.out.println("  - 引用字段会修改 → 深拷贝（安全）");
        System.out.println("  - 不确定 → 深拷贝（保险）");
    }
}
