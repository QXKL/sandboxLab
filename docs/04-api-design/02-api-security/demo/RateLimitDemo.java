import java.util.*;
import java.util.concurrent.*;

/**
 * 限流算法演示
 *
 * 演示内容：
 * 1. 固定窗口限流（展示临界点问题）
 * 2. 滑动窗口限流（推荐方案）
 * 3. 令牌桶限流（支持突发流量）
 */
public class RateLimitDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(80));
        System.out.println("限流算法演示");
        System.out.println("=".repeat(80));
        System.out.println();

        demo1_fixedWindow();
        demo2_slidingWindow();
        demo3_tokenBucket();
    }

    private static void demo1_fixedWindow() {
        System.out.println("━".repeat(80));
        System.out.println("1. 固定窗口限流");
        System.out.println("━".repeat(80));
        System.out.println();

        System.out.println("规则：每10秒最多5次请求");
        System.out.println();

        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(5, 10_000);

        // 正常请求
        System.out.println("发送5次请求：");
        for (int i = 1; i <= 5; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.printf("  请求%d: %s%n", i, allowed ? "✅ 允许" : "❌ 拒绝");
        }
        System.out.println();

        // 超出限制
        System.out.println("发送第6次请求：");
        boolean allowed = limiter.allowRequest();
        System.out.printf("  请求6: %s (已超出限制)%n", allowed ? "✅ 允许" : "❌ 拒绝");
        System.out.println();

        System.out.println("⚠️  固定窗口的问题：临界点流量突刺");
        System.out.println("  如果在09秒发送5次，10秒（窗口重置）又发送5次");
        System.out.println("  那么1秒内实际发送了10次请求！");
        System.out.println();
    }

    private static void demo2_slidingWindow() {
        System.out.println("━".repeat(80));
        System.out.println("2. 滑动窗口限流（推荐）");
        System.out.println("━".repeat(80));
        System.out.println();

        System.out.println("规则：任意10秒内最多5次请求");
        System.out.println();

        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(5, 10_000);

        System.out.println("发送请求：");
        for (int i = 1; i <= 7; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.printf("  请求%d: %s (窗口内请求数: %d)%n",
                i, allowed ? "✅ 允许" : "❌ 拒绝", limiter.getRequestCount());

            if (i == 5) {
                try {
                    Thread.sleep(2000);  // 等待2秒
                    System.out.println("  [等待2秒...]");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println();

        System.out.println("✅ 滑动窗口的优点：");
        System.out.println("  - 平滑限流，无临界点问题");
        System.out.println("  - 精确统计任意时间窗口内的请求数");
        System.out.println();
    }

    private static void demo3_tokenBucket() {
        System.out.println("━".repeat(80));
        System.out.println("3. 令牌桶限流");
        System.out.println("━".repeat(80));
        System.out.println();

        System.out.println("规则：桶容量10个令牌，每秒生成2个令牌");
        System.out.println();

        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 2);

        System.out.println("初始状态：桶中有10个令牌");
        System.out.println();

        // 突发请求
        System.out.println("突发请求（连续发送15次）：");
        for (int i = 1; i <= 15; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.printf("  请求%d: %s (剩余令牌: %d)%n",
                i, allowed ? "✅ 允许" : "❌ 拒绝", limiter.getTokenCount());
        }
        System.out.println();

        System.out.println("✅ 令牌桶的优点：");
        System.out.println("  - 允许突发流量（桶满时可以一次性消费所有令牌）");
        System.out.println("  - 平均速率稳定（令牌生成速率固定）");
        System.out.println();

        System.out.println("💡 使用场景：");
        System.out.println("  - 秒杀活动（允许短时突发）");
        System.out.println("  - API网关（平均限流 + 允许突发）");
        System.out.println();
    }
}

/**
 * 固定窗口限流器
 */
class FixedWindowRateLimiter {
    private int maxRequests;
    private long windowSize;
    private int counter = 0;
    private long windowStart = System.currentTimeMillis();

    public FixedWindowRateLimiter(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        // 检查是否需要重置窗口
        if (now - windowStart >= windowSize) {
            counter = 0;
            windowStart = now;
        }

        // 检查是否超过限制
        if (counter >= maxRequests) {
            return false;
        }

        counter++;
        return true;
    }
}

/**
 * 滑动窗口限流器
 */
class SlidingWindowRateLimiter {
    private int maxRequests;
    private long windowSize;
    private Queue<Long> timestamps = new LinkedList<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize;

        // 移除窗口外的请求记录
        while (!timestamps.isEmpty() && timestamps.peek() < windowStart) {
            timestamps.poll();
        }

        // 检查是否超过限制
        if (timestamps.size() >= maxRequests) {
            return false;
        }

        timestamps.offer(now);
        return true;
    }

    public synchronized int getRequestCount() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize;

        // 移除窗口外的请求记录
        while (!timestamps.isEmpty() && timestamps.peek() < windowStart) {
            timestamps.poll();
        }

        return timestamps.size();
    }
}

/**
 * 令牌桶限流器
 */
class TokenBucketRateLimiter {
    private int capacity;
    private int tokensPerSecond;
    private int tokens;
    private long lastRefillTime = System.currentTimeMillis();

    public TokenBucketRateLimiter(int capacity, int tokensPerSecond) {
        this.capacity = capacity;
        this.tokensPerSecond = tokensPerSecond;
        this.tokens = capacity;
    }

    public synchronized boolean allowRequest() {
        refillTokens();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public synchronized int getTokenCount() {
        refillTokens();
        return tokens;
    }

    private void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastRefillTime;

        // 计算应该生成的令牌数
        int newTokens = (int) (elapsedTime / 1000.0 * tokensPerSecond);

        if (newTokens > 0) {
            tokens = Math.min(capacity, tokens + newTokens);
            lastRefillTime = now;
        }
    }
}
