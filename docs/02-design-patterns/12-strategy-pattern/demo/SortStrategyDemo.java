/**
 * 策略模式 - 排序策略示例
 *
 * 场景：排序算法（多种排序方式）
 * 演示：根据数据量选择合适的排序算法
 */

import java.util.*;

// ========== 抽象策略 ==========

/**
 * Strategy：排序策略接口
 */
interface SortStrategy {
    void sort(int[] array);
    String getAlgorithmName();
}

// ========== 具体策略 ==========

/**
 * ConcreteStrategy：冒泡排序
 */
class BubbleSort implements SortStrategy {
    @Override
    public void sort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // 交换
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "冒泡排序 (Bubble Sort)";
    }
}

/**
 * ConcreteStrategy：快速排序
 */
class QuickSort implements SortStrategy {
    @Override
    public void sort(int[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        return i + 1;
    }

    @Override
    public String getAlgorithmName() {
        return "快速排序 (Quick Sort)";
    }
}

/**
 * ConcreteStrategy：归并排序
 */
class MergeSort implements SortStrategy {
    @Override
    public void sort(int[] array) {
        if (array.length > 1) {
            mergeSort(array, 0, array.length - 1);
        }
    }

    private void mergeSort(int[] array, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    private void merge(int[] array, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = array[i++];
        }

        while (j <= right) {
            temp[k++] = array[j++];
        }

        for (i = 0; i < temp.length; i++) {
            array[left + i] = temp[i];
        }
    }

    @Override
    public String getAlgorithmName() {
        return "归并排序 (Merge Sort)";
    }
}

// ========== 上下文 ==========

/**
 * Context：排序上下文
 */
class SortContext {
    private SortStrategy strategy;

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeSort(int[] array) {
        if (strategy == null) {
            System.out.println("❌ 请先设置排序策略！");
            return;
        }

        System.out.println("\n使用: " + strategy.getAlgorithmName());
        System.out.println("原始数组: " + Arrays.toString(array));

        long startTime = System.nanoTime();
        strategy.sort(array);
        long endTime = System.nanoTime();

        System.out.println("排序结果: " + Arrays.toString(array));
        System.out.println("耗时: " + (endTime - startTime) / 1000 + " 微秒");
    }
}

// ========== 智能策略选择器 ==========

/**
 * 智能策略选择器：根据数据量自动选择合适的算法
 */
class SmartSortSelector {
    public static SortStrategy selectStrategy(int dataSize) {
        if (dataSize < 10) {
            System.out.println("  [策略选择] 数据量较小(" + dataSize + ")，选择冒泡排序");
            return new BubbleSort();
        } else if (dataSize < 100) {
            System.out.println("  [策略选择] 数据量中等(" + dataSize + ")，选择快速排序");
            return new QuickSort();
        } else {
            System.out.println("  [策略选择] 数据量较大(" + dataSize + ")，选择归并排序");
            return new MergeSort();
        }
    }
}

// ========== 测试 ==========

public class SortStrategyDemo {
    public static void main(String[] args) {
        System.out.println("=== 策略模式 - 排序算法 ===\n");

        // 示例1：手动选择策略
        System.out.println("【1. 手动选择排序策略】");
        demonstrateManualSelection();

        // 示例2：智能选择策略
        System.out.println("\n\n【2. 智能选择排序策略】");
        demonstrateSmartSelection();

        // 示例3：性能对比
        System.out.println("\n\n【3. 不同算法性能对比】");
        demonstratePerformanceComparison();

        // 示例4：策略模式的优势
        System.out.println("\n\n【4. 策略模式的优势】");
        demonstrateAdvantages();
    }

    /**
     * 手动选择策略
     */
    static void demonstrateManualSelection() {
        SortContext context = new SortContext();

        // 测试数据
        int[] data1 = {5, 2, 8, 1, 9};
        int[] data2 = Arrays.copyOf(data1, data1.length);
        int[] data3 = Arrays.copyOf(data1, data1.length);

        // 使用冒泡排序
        context.setStrategy(new BubbleSort());
        context.executeSort(data1);

        // 切换到快速排序
        context.setStrategy(new QuickSort());
        context.executeSort(data2);

        // 切换到归并排序
        context.setStrategy(new MergeSort());
        context.executeSort(data3);

        System.out.println("\n✅ 关键点:");
        System.out.println("  - 同一个Context");
        System.out.println("  - 灵活切换算法");
        System.out.println("  - 算法独立实现");
    }

    /**
     * 智能选择策略
     */
    static void demonstrateSmartSelection() {
        SortContext context = new SortContext();

        // 场景1：小数据量
        System.out.println("【场景1：小数据量】");
        int[] smallData = generateRandomArray(5);
        SortStrategy strategy1 = SmartSortSelector.selectStrategy(smallData.length);
        context.setStrategy(strategy1);
        context.executeSort(smallData);

        // 场景2：中等数据量
        System.out.println("\n【场景2：中等数据量】");
        int[] mediumData = generateRandomArray(50);
        SortStrategy strategy2 = SmartSortSelector.selectStrategy(mediumData.length);
        context.setStrategy(strategy2);
        // 只显示部分数据
        System.out.println("\n使用: " + strategy2.getAlgorithmName());
        System.out.println("数据量: " + mediumData.length);
        long start = System.nanoTime();
        strategy2.sort(mediumData);
        long end = System.nanoTime();
        System.out.println("耗时: " + (end - start) / 1000 + " 微秒");

        // 场景3：大数据量
        System.out.println("\n【场景3：大数据量】");
        int[] largeData = generateRandomArray(1000);
        SortStrategy strategy3 = SmartSortSelector.selectStrategy(largeData.length);
        context.setStrategy(strategy3);
        System.out.println("\n使用: " + strategy3.getAlgorithmName());
        System.out.println("数据量: " + largeData.length);
        start = System.nanoTime();
        strategy3.sort(largeData);
        end = System.nanoTime();
        System.out.println("耗时: " + (end - start) / 1000 + " 微秒");

        System.out.println("\n✅ 优势:");
        System.out.println("  - 根据数据量自动选择");
        System.out.println("  - 优化性能");
        System.out.println("  - 客户端无需关心细节");
    }

    /**
     * 性能对比
     */
    static void demonstratePerformanceComparison() {
        int[] testSizes = {10, 50, 100, 500};

        System.out.println("不同数据量下各算法的性能对比:\n");
        System.out.println("  数据量      冒泡排序      快速排序      归并排序");
        System.out.println("  ───────────────────────────────────────────────");

        for (int size : testSizes) {
            int[] data1 = generateRandomArray(size);
            int[] data2 = Arrays.copyOf(data1, data1.length);
            int[] data3 = Arrays.copyOf(data1, data1.length);

            long bubbleTime = measureSort(new BubbleSort(), data1);
            long quickTime = measureSort(new QuickSort(), data2);
            long mergeTime = measureSort(new MergeSort(), data3);

            System.out.printf("  %-11d %-13d %-13d %-13d\n",
                    size, bubbleTime, quickTime, mergeTime);
        }

        System.out.println("\n✅ 结论:");
        System.out.println("  - 数据量小：冒泡排序足够");
        System.out.println("  - 数据量中：快速排序最优");
        System.out.println("  - 数据量大：归并排序稳定");
    }

    /**
     * 策略模式的优势
     */
    static void demonstrateAdvantages() {
        System.out.println("❌ 不使用策略模式:");
        System.out.println("```java");
        System.out.println("void sort(int[] array, String algorithm) {");
        System.out.println("    if (algorithm.equals(\"bubble\")) {");
        System.out.println("        // 冒泡排序逻辑");
        System.out.println("    } else if (algorithm.equals(\"quick\")) {");
        System.out.println("        // 快速排序逻辑");
        System.out.println("    } else if (algorithm.equals(\"merge\")) {");
        System.out.println("        // 归并排序逻辑");
        System.out.println("    }");
        System.out.println("}");
        System.out.println("```");

        System.out.println("\n✅ 使用策略模式:");
        System.out.println("```java");
        System.out.println("// 1. 定义策略接口");
        System.out.println("interface SortStrategy {");
        System.out.println("    void sort(int[] array);");
        System.out.println("}");
        System.out.println("");
        System.out.println("// 2. 具体策略");
        System.out.println("class BubbleSort implements SortStrategy { }");
        System.out.println("class QuickSort implements SortStrategy { }");
        System.out.println("");
        System.out.println("// 3. 使用");
        System.out.println("context.setStrategy(new QuickSort());");
        System.out.println("context.executeSort(array);");
        System.out.println("```");

        System.out.println("\n优势:");
        System.out.println("  ✅ 消除if-else");
        System.out.println("  ✅ 算法独立封装");
        System.out.println("  ✅ 易于扩展新算法");
        System.out.println("  ✅ 符合开闭原则");
        System.out.println("  ✅ 运行时切换算法");
    }

    // ========== 辅助方法 ==========

    static int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100);
        }
        return array;
    }

    static long measureSort(SortStrategy strategy, int[] array) {
        long start = System.nanoTime();
        strategy.sort(array);
        long end = System.nanoTime();
        return (end - start) / 1000;  // 微秒
    }
}
