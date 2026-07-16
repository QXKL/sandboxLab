import java.util.*;
import java.util.concurrent.*;

/**
 * 事务隔离级别演示
 *
 * 模拟数据库的事务隔离行为，展示不同隔离级别下的并发问题
 */
public class TransactionIsolationDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(80));
        System.out.println("事务隔离级别演示");
        System.out.println("=".repeat(80));
        System.out.println();

        demo1_dirtyRead();
        demo2_nonRepeatableRead();
        demo3_repeatableRead();
        demo4_serializable();
    }

    /**
     * 演示1：脏读问题（Read Uncommitted）
     */
    private static void demo1_dirtyRead() throws InterruptedException {
        System.out.println("━".repeat(80));
        System.out.println("1. 脏读问题（Read Uncommitted）");
        System.out.println("━".repeat(80));
        System.out.println();

        Database db = new Database(IsolationLevel.READ_UNCOMMITTED);
        db.insert("account1", 1000);

        System.out.println("初始余额: " + db.read("account1"));
        System.out.println();

        CountDownLatch latch = new CountDownLatch(1);

        // 事务1：修改但不提交（然后回滚）
        Thread t1 = new Thread(() -> {
            System.out.println("[T1] 开始事务");
            db.begin("T1");
            db.update("account1", 500, "T1");
            System.out.println("[T1] 修改余额为 500（未提交）");

            latch.countDown();  // 通知T2可以读取

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            db.rollback("T1");
            System.out.println("[T1] 回滚事务");
        });

        // 事务2：读取数据
        Thread t2 = new Thread(() -> {
            try {
                latch.await();  // 等待T1修改
                Thread.sleep(500);

                System.out.println("[T2] 开始事务");
                db.begin("T2");
                Integer balance = db.read("account1", "T2");
                System.out.println("[T2] 读到余额: " + balance + " ⚠️ 脏读！");
                db.commit("T2");
                System.out.println("[T2] 提交事务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println();
        System.out.println("最终余额: " + db.read("account1") + " (T1回滚后恢复)");
        System.out.println("问题：T2读到了T1未提交的数据（500），但T1最终回滚了！");
        System.out.println();
    }

    /**
     * 演示2：不可重复读问题（Read Committed）
     */
    private static void demo2_nonRepeatableRead() throws InterruptedException {
        System.out.println("━".repeat(80));
        System.out.println("2. 不可重复读问题（Read Committed）");
        System.out.println("━".repeat(80));
        System.out.println();

        Database db = new Database(IsolationLevel.READ_COMMITTED);
        db.insert("account1", 1000);

        System.out.println("初始余额: " + db.read("account1"));
        System.out.println();

        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        // 事务1：两次读取
        Thread t1 = new Thread(() -> {
            System.out.println("[T1] 开始事务");
            db.begin("T1");

            Integer balance1 = db.read("account1", "T1");
            System.out.println("[T1] 第一次读取余额: " + balance1);

            latch1.countDown();  // 通知T2可以修改

            try {
                latch2.await();  // 等待T2提交
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Integer balance2 = db.read("account1", "T1");
            System.out.println("[T1] 第二次读取余额: " + balance2 + " ⚠️ 不可重复读！");

            db.commit("T1");
            System.out.println("[T1] 提交事务");
        });

        // 事务2：修改数据
        Thread t2 = new Thread(() -> {
            try {
                latch1.await();  // 等待T1第一次读取
                Thread.sleep(500);

                System.out.println("[T2] 开始事务");
                db.begin("T2");
                db.update("account1", 500, "T2");
                System.out.println("[T2] 修改余额为 500");
                db.commit("T2");
                System.out.println("[T2] 提交事务");

                latch2.countDown();  // 通知T1可以第二次读取
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println();
        System.out.println("问题：T1在同一事务中两次读取，结果不同！");
        System.out.println();
    }

    /**
     * 演示3：可重复读（Repeatable Read）
     */
    private static void demo3_repeatableRead() throws InterruptedException {
        System.out.println("━".repeat(80));
        System.out.println("3. 可重复读（Repeatable Read）");
        System.out.println("━".repeat(80));
        System.out.println();

        Database db = new Database(IsolationLevel.REPEATABLE_READ);
        db.insert("account1", 1000);

        System.out.println("初始余额: " + db.read("account1"));
        System.out.println();

        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        // 事务1：两次读取
        Thread t1 = new Thread(() -> {
            System.out.println("[T1] 开始事务");
            db.begin("T1");

            Integer balance1 = db.read("account1", "T1");
            System.out.println("[T1] 第一次读取余额: " + balance1);

            latch1.countDown();  // 通知T2可以修改

            try {
                latch2.await();  // 等待T2提交
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Integer balance2 = db.read("account1", "T1");
            System.out.println("[T1] 第二次读取余额: " + balance2 + " ✅ 可重复读！");

            db.commit("T1");
            System.out.println("[T1] 提交事务");
        });

        // 事务2：修改数据
        Thread t2 = new Thread(() -> {
            try {
                latch1.await();  // 等待T1第一次读取
                Thread.sleep(500);

                System.out.println("[T2] 开始事务");
                db.begin("T2");
                db.update("account1", 500, "T2");
                System.out.println("[T2] 修改余额为 500");
                db.commit("T2");
                System.out.println("[T2] 提交事务");

                latch2.countDown();  // 通知T1可以第二次读取
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println();
        System.out.println("优点：T1在同一事务中两次读取，结果相同（通过MVCC实现）");
        System.out.println("最终余额: " + db.read("account1") + " (T2的修改已提交)");
        System.out.println();
    }

    /**
     * 演示4：串行化（Serializable）
     */
    private static void demo4_serializable() throws InterruptedException {
        System.out.println("━".repeat(80));
        System.out.println("4. 串行化（Serializable）");
        System.out.println("━".repeat(80));
        System.out.println();

        Database db = new Database(IsolationLevel.SERIALIZABLE);
        db.insert("account1", 1000);

        System.out.println("初始余额: " + db.read("account1"));
        System.out.println();

        long startTime = System.currentTimeMillis();

        // 事务1
        Thread t1 = new Thread(() -> {
            System.out.println("[T1] 开始事务");
            db.begin("T1");
            db.update("account1", 900, "T1");
            System.out.println("[T1] 修改余额为 900");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            db.commit("T1");
            System.out.println("[T1] 提交事务");
        });

        // 事务2（会被阻塞）
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(200);
                System.out.println("[T2] 尝试开始事务（等待T1完成...）");
                db.begin("T2");
                db.update("account1", 800, "T2");
                System.out.println("[T2] 修改余额为 800");
                db.commit("T2");
                System.out.println("[T2] 提交事务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        long endTime = System.currentTimeMillis();

        System.out.println();
        System.out.println("最终余额: " + db.read("account1"));
        System.out.println("总耗时: " + (endTime - startTime) + "ms");
        System.out.println("特点：事务串行执行，完全隔离，但性能最差");
        System.out.println();
    }
}

/**
 * 隔离级别枚举
 */
enum IsolationLevel {
    READ_UNCOMMITTED,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE
}

/**
 * 简化的数据库模拟
 */
class Database {
    private Map<String, Integer> data = new ConcurrentHashMap<>();
    private Map<String, Map<String, Integer>> versions = new ConcurrentHashMap<>();
    private Map<String, Integer> uncommitted = new ConcurrentHashMap<>();
    private IsolationLevel isolationLevel;
    private Object lock = new Object();

    public Database(IsolationLevel isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public void insert(String key, Integer value) {
        data.put(key, value);
    }

    public void begin(String txId) {
        if (isolationLevel == IsolationLevel.REPEATABLE_READ) {
            // 创建快照
            versions.put(txId, new HashMap<>(data));
        }
    }

    public Integer read(String key) {
        return data.get(key);
    }

    public Integer read(String key, String txId) {
        switch (isolationLevel) {
            case READ_UNCOMMITTED:
                // 读未提交：返回uncommitted中的值（如果有）
                return uncommitted.getOrDefault(key, data.get(key));

            case READ_COMMITTED:
                // 读已提交：只返回已提交的值
                return data.get(key);

            case REPEATABLE_READ:
                // 可重复读：从快照中读取
                return versions.get(txId).get(key);

            case SERIALIZABLE:
                synchronized (lock) {
                    return data.get(key);
                }

            default:
                return data.get(key);
        }
    }

    public void update(String key, Integer value, String txId) {
        if (isolationLevel == IsolationLevel.SERIALIZABLE) {
            synchronized (lock) {
                uncommitted.put(key, value);
            }
        } else {
            uncommitted.put(key, value);
        }
    }

    public void commit(String txId) {
        // 将uncommitted的值提交到data
        uncommitted.forEach((key, value) -> data.put(key, value));
        uncommitted.clear();
        versions.remove(txId);
    }

    public void rollback(String txId) {
        // 清除uncommitted的值
        uncommitted.clear();
        versions.remove(txId);
    }
}
