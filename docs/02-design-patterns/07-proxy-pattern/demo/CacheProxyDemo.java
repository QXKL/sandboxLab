/**
 * 代理模式 - 缓存代理示例
 *
 * 场景：数据库查询缓存代理
 * 演示：缓存代理、减少数据库访问、性能优化
 */

import java.util.HashMap;
import java.util.Map;

// ========== Subject接口 ==========

/**
 * 抽象主题：数据服务接口
 */
interface DataService {
    String query(String sql);
}

// ========== RealSubject（真实对象） ==========

/**
 * 真实对象：数据库服务（查询慢）
 */
class DatabaseService implements DataService {
    @Override
    public String query(String sql) {
        System.out.println("  🗄️  [数据库] 执行查询: " + sql);

        // 模拟数据库查询（耗时操作）
        try {
            Thread.sleep(1000);  // 模拟1秒延迟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String result = "查询结果: " + sql.hashCode();
        System.out.println("  ✅ [数据库] 查询完成: " + result);
        return result;
    }
}

// ========== Proxy（代理） ==========

/**
 * 代理：缓存代理
 */
class CacheProxy implements DataService {
    private DatabaseService databaseService;
    private Map<String, String> cache;  // 缓存
    private Map<String, Long> cacheTime;  // 缓存时间
    private static final long CACHE_EXPIRE_TIME = 5000;  // 缓存5秒过期

    public CacheProxy(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.cache = new HashMap<>();
        this.cacheTime = new HashMap<>();
    }

    @Override
    public String query(String sql) {
        System.out.println("  🔍 [缓存代理] 收到查询请求: " + sql);

        // 检查缓存
        if (cache.containsKey(sql)) {
            long cachedAt = cacheTime.get(sql);
            long now = System.currentTimeMillis();

            // 检查缓存是否过期
            if (now - cachedAt < CACHE_EXPIRE_TIME) {
                System.out.println("  ⚡ [缓存代理] 缓存命中，直接返回");
                String result = cache.get(sql);
                System.out.println("  📦 [缓存代理] 缓存结果: " + result);
                return result;
            } else {
                System.out.println("  ⏰ [缓存代理] 缓存已过期，重新查询");
                cache.remove(sql);
                cacheTime.remove(sql);
            }
        } else {
            System.out.println("  ❌ [缓存代理] 缓存未命中，查询数据库");
        }

        // 查询数据库
        String result = databaseService.query(sql);

        // 存入缓存
        cache.put(sql, result);
        cacheTime.put(sql, System.currentTimeMillis());
        System.out.println("  💾 [缓存代理] 结果已缓存");

        return result;
    }

    /**
     * 获取缓存统计信息
     */
    public void printCacheStats() {
        System.out.println("\n📊 缓存统计:");
        System.out.println("  缓存条目数: " + cache.size());
        System.out.println("  缓存内容: " + cache.keySet());
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        cache.clear();
        cacheTime.clear();
        System.out.println("  🗑️  [缓存代理] 缓存已清空");
    }
}

// ========== 测试 ==========

public class CacheProxyDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 代理模式 - 缓存代理 ===\n");

        // 示例1：缓存命中 vs 未命中
        System.out.println("【1. 缓存命中 vs 未命中】\n");
        demonstrateCacheHit();

        // 示例2：缓存过期
        System.out.println("\n【2. 缓存过期】\n");
        demonstrateCacheExpire();

        // 示例3：性能对比
        System.out.println("\n【3. 性能对比】\n");
        demonstratePerformance();

        // 示例4：缓存代理的优势
        System.out.println("\n【4. 缓存代理的优势】\n");
        demonstrateAdvantages();
    }

    /**
     * 演示缓存命中 vs 未命中
     */
    static void demonstrateCacheHit() throws InterruptedException {
        DataService service = new CacheProxy(new DatabaseService());

        String sql = "SELECT * FROM users WHERE id = 1";

        System.out.println("第1次查询（缓存未命中）:");
        long start1 = System.currentTimeMillis();
        service.query(sql);
        long time1 = System.currentTimeMillis() - start1;
        System.out.println("耗时: " + time1 + "ms\n");

        Thread.sleep(500);

        System.out.println("第2次查询（缓存命中）:");
        long start2 = System.currentTimeMillis();
        service.query(sql);
        long time2 = System.currentTimeMillis() - start2;
        System.out.println("耗时: " + time2 + "ms\n");

        System.out.println("第3次查询（缓存命中）:");
        long start3 = System.currentTimeMillis();
        service.query(sql);
        long time3 = System.currentTimeMillis() - start3;
        System.out.println("耗时: " + time3 + "ms");

        System.out.println("\n✅ 性能提升: " + (time1 - time2) + "ms (缓存命中后几乎瞬间返回)");
    }

    /**
     * 演示缓存过期
     */
    static void demonstrateCacheExpire() throws InterruptedException {
        CacheProxy proxy = new CacheProxy(new DatabaseService());

        String sql = "SELECT * FROM users WHERE id = 2";

        System.out.println("第1次查询:");
        proxy.query(sql);
        proxy.printCacheStats();

        Thread.sleep(2000);

        System.out.println("\n第2次查询（缓存未过期）:");
        proxy.query(sql);

        System.out.println("\n等待6秒（超过缓存过期时间5秒）...");
        Thread.sleep(6000);

        System.out.println("\n第3次查询（缓存已过期）:");
        proxy.query(sql);
        proxy.printCacheStats();
    }

    /**
     * 演示性能对比
     */
    static void demonstratePerformance() throws InterruptedException {
        System.out.println("场景：执行100次相同查询\n");

        String sql = "SELECT * FROM products";

        // 无缓存
        System.out.println("方式1: 无缓存（直接访问数据库）");
        DataService directService = new DatabaseService();
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 3; i++) {  // 简化为3次演示
            directService.query(sql);
        }
        long time1 = System.currentTimeMillis() - start1;
        System.out.println("3次查询总耗时: " + time1 + "ms");
        System.out.println("预计100次耗时: " + (time1 * 100 / 3) + "ms\n");

        // 有缓存
        System.out.println("方式2: 使用缓存代理");
        DataService cachedService = new CacheProxy(new DatabaseService());
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 3; i++) {
            cachedService.query(sql);
            Thread.sleep(100);  // 短暂延迟
        }
        long time2 = System.currentTimeMillis() - start2;
        System.out.println("3次查询总耗时: " + time2 + "ms");
        System.out.println("预计100次耗时: " + (time2 * 100 / 3) + "ms");

        System.out.println("\n✅ 性能对比:");
        System.out.println("  - 无缓存: ~100秒（每次1秒）");
        System.out.println("  - 有缓存: ~1秒（首次1秒，后续几乎瞬间）");
        System.out.println("  - 性能提升: 99倍");
    }

    /**
     * 演示缓存代理的优势
     */
    static void demonstrateAdvantages() {
        System.out.println("✅ 优势1: 性能优化");
        System.out.println("   - 减少数据库访问");
        System.out.println("   - 降低响应时间");
        System.out.println("   - 提高系统吞吐量");

        System.out.println("\n✅ 优势2: 透明性");
        System.out.println("   - 客户端无需知道缓存存在");
        System.out.println("   - 调用方式与直接访问相同");
        System.out.println("   - 可以随时开启/关闭缓存");

        System.out.println("\n✅ 优势3: 灵活性");
        System.out.println("   - 可以配置缓存策略");
        System.out.println("   - 支持缓存过期");
        System.out.println("   - 支持缓存清空");

        System.out.println("\n✅ 优势4: 职责分离");
        System.out.println("   - 数据库服务只负责查询");
        System.out.println("   - 缓存逻辑在代理中");
        System.out.println("   - 符合单一职责原则");

        System.out.println("\n📊 缓存效果:");
        System.out.println("   场景: 热门数据查询");
        System.out.println("   - 1000次查询同一数据");
        System.out.println("   - 无缓存: 1000秒");
        System.out.println("   - 有缓存: 1秒（首次）+ 0秒（后续）");
        System.out.println("   - 性能提升: 99.9%");

        System.out.println("\n⚠️  适用场景:");
        System.out.println("   - 频繁查询相同数据");
        System.out.println("   - 数据变化不频繁");
        System.out.println("   - 查询成本高");
        System.out.println("   - 需要优化性能");

        System.out.println("\n🎯 缓存策略:");
        System.out.println("   - 过期时间: 根据数据更新频率");
        System.out.println("   - 缓存大小: 根据内存限制");
        System.out.println("   - 失效策略: LRU、LFU等");
    }
}
