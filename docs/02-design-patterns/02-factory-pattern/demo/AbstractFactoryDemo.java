/**
 * 抽象工厂模式 - 跨数据库访问层
 *
 * 场景：数据访问层需要支持多种数据库（MySQL、PostgreSQL）
 * 演示：抽象工厂如何保证产品族的一致性
 */

// ========== 抽象产品A：数据库连接 ==========

interface Connection {
    void connect();
    void executeSQL(String sql);
    void close();
}

// ========== 抽象产品B：事务管理 ==========

interface Transaction {
    void begin();
    void commit();
    void rollback();
}

// ========== 具体产品族1：MySQL ==========

class MySQLConnection implements Connection {
    @Override
    public void connect() {
        System.out.println("  [MySQL Connection] 连接到MySQL数据库");
        System.out.println("    → jdbc:mysql://localhost:3306/mydb");
    }

    @Override
    public void executeSQL(String sql) {
        System.out.println("  [MySQL Connection] 执行SQL: " + sql);
        System.out.println("    → 使用MySQL语法解析");
    }

    @Override
    public void close() {
        System.out.println("  [MySQL Connection] 关闭MySQL连接");
    }
}

class MySQLTransaction implements Transaction {
    @Override
    public void begin() {
        System.out.println("  [MySQL Transaction] 开始事务: START TRANSACTION");
    }

    @Override
    public void commit() {
        System.out.println("  [MySQL Transaction] 提交事务: COMMIT");
    }

    @Override
    public void rollback() {
        System.out.println("  [MySQL Transaction] 回滚事务: ROLLBACK");
    }
}

// ========== 具体产品族2：PostgreSQL ==========

class PostgreSQLConnection implements Connection {
    @Override
    public void connect() {
        System.out.println("  [PostgreSQL Connection] 连接到PostgreSQL数据库");
        System.out.println("    → jdbc:postgresql://localhost:5432/mydb");
    }

    @Override
    public void executeSQL(String sql) {
        System.out.println("  [PostgreSQL Connection] 执行SQL: " + sql);
        System.out.println("    → 使用PostgreSQL语法解析");
    }

    @Override
    public void close() {
        System.out.println("  [PostgreSQL Connection] 关闭PostgreSQL连接");
    }
}

class PostgreSQLTransaction implements Transaction {
    @Override
    public void begin() {
        System.out.println("  [PostgreSQL Transaction] 开始事务: BEGIN");
    }

    @Override
    public void commit() {
        System.out.println("  [PostgreSQL Transaction] 提交事务: COMMIT");
    }

    @Override
    public void rollback() {
        System.out.println("  [PostgreSQL Transaction] 回滚事务: ROLLBACK");
    }
}

// ========== 具体产品族3：Oracle ==========

class OracleConnection implements Connection {
    @Override
    public void connect() {
        System.out.println("  [Oracle Connection] 连接到Oracle数据库");
        System.out.println("    → jdbc:oracle:thin:@localhost:1521:orcl");
    }

    @Override
    public void executeSQL(String sql) {
        System.out.println("  [Oracle Connection] 执行SQL: " + sql);
        System.out.println("    → 使用Oracle语法解析");
    }

    @Override
    public void close() {
        System.out.println("  [Oracle Connection] 关闭Oracle连接");
    }
}

class OracleTransaction implements Transaction {
    @Override
    public void begin() {
        System.out.println("  [Oracle Transaction] 开始事务: SET TRANSACTION");
    }

    @Override
    public void commit() {
        System.out.println("  [Oracle Transaction] 提交事务: COMMIT");
    }

    @Override
    public void rollback() {
        System.out.println("  [Oracle Transaction] 回滚事务: ROLLBACK");
    }
}

// ========== 抽象工厂 ==========

interface DatabaseFactory {
    Connection createConnection();
    Transaction createTransaction();
}

// ========== 具体工厂 ==========

class MySQLFactory implements DatabaseFactory {
    @Override
    public Connection createConnection() {
        return new MySQLConnection();
    }

    @Override
    public Transaction createTransaction() {
        return new MySQLTransaction();
    }
}

class PostgreSQLFactory implements DatabaseFactory {
    @Override
    public Connection createConnection() {
        return new PostgreSQLConnection();
    }

    @Override
    public Transaction createTransaction() {
        return new PostgreSQLTransaction();
    }
}

class OracleFactory implements DatabaseFactory {
    @Override
    public Connection createConnection() {
        return new OracleConnection();
    }

    @Override
    public Transaction createTransaction() {
        return new OracleTransaction();
    }
}

// ========== 数据访问层 ==========

class DataAccessLayer {
    private DatabaseFactory factory;

    public DataAccessLayer(DatabaseFactory factory) {
        this.factory = factory;
    }

    /**
     * 执行带事务的数据库操作
     */
    public void executeWithTransaction(String[] sqls) {
        // 创建一整套配套的数据库组件
        Connection conn = factory.createConnection();
        Transaction tx = factory.createTransaction();

        try {
            // 1. 连接数据库
            conn.connect();

            // 2. 开始事务
            tx.begin();

            // 3. 执行SQL
            for (String sql : sqls) {
                conn.executeSQL(sql);
            }

            // 4. 提交事务
            tx.commit();
            System.out.println("  ✅ 事务执行成功\n");

        } catch (Exception e) {
            // 5. 回滚事务
            tx.rollback();
            System.out.println("  ❌ 事务执行失败，已回滚\n");

        } finally {
            // 6. 关闭连接
            conn.close();
        }
    }
}

// ========== 客户端 ==========

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("=== 抽象工厂模式示例 ===\n");

        // 示例1：使用MySQL
        System.out.println("【1. 使用MySQL数据库】");
        testMySQL();

        // 示例2：切换到PostgreSQL
        System.out.println("\n【2. 切换到PostgreSQL数据库】");
        testPostgreSQL();

        // 示例3：扩展Oracle
        System.out.println("\n【3. 扩展Oracle数据库（无需修改现有代码）】");
        testOracle();

        // 示例4：产品族一致性保证
        System.out.println("\n【4. 产品族一致性保证】");
        testConsistency();
    }

    /**
     * 测试MySQL
     */
    static void testMySQL() {
        DatabaseFactory factory = new MySQLFactory();
        DataAccessLayer dal = new DataAccessLayer(factory);

        String[] sqls = {
            "INSERT INTO users VALUES (1, 'Alice')",
            "UPDATE users SET age = 25 WHERE id = 1"
        };

        dal.executeWithTransaction(sqls);
    }

    /**
     * 测试PostgreSQL（只需更换工厂）
     */
    static void testPostgreSQL() {
        DatabaseFactory factory = new PostgreSQLFactory();
        DataAccessLayer dal = new DataAccessLayer(factory);

        String[] sqls = {
            "INSERT INTO orders VALUES (101, 'Order-A')",
            "UPDATE orders SET status = 'PAID' WHERE id = 101"
        };

        dal.executeWithTransaction(sqls);
    }

    /**
     * 测试Oracle（扩展新产品族）
     */
    static void testOracle() {
        DatabaseFactory factory = new OracleFactory();
        DataAccessLayer dal = new DataAccessLayer(factory);

        String[] sqls = {
            "INSERT INTO products VALUES (1001, 'Product-X')",
            "UPDATE products SET price = 99.99 WHERE id = 1001"
        };

        dal.executeWithTransaction(sqls);
        System.out.println("✅ 新增Oracle支持，无需修改DataAccessLayer代码");
    }

    /**
     * 产品族一致性保证
     */
    static void testConsistency() {
        System.out.println("问题场景：如果不用抽象工厂");
        System.out.println("  可能出现：MySQL的Connection + PostgreSQL的Transaction");
        System.out.println("  后果：不兼容，导致运行时错误\n");

        System.out.println("抽象工厂的保证：");
        System.out.println("  ✅ 同一个工厂创建的对象一定是配套的");
        System.out.println("  ✅ MySQLFactory 只创建 MySQL 的组件");
        System.out.println("  ✅ PostgreSQLFactory 只创建 PostgreSQL 的组件");
        System.out.println("  ✅ 切换数据库只需更换工厂，不会混用组件");
    }
}
