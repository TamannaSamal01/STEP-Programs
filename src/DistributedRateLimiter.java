import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {
    int tokens;
    int maxTokens;
    double refillRate;
    long lastRefillTime;

    TokenBucket(int maxTokens, double refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = ((now - lastRefillTime) / 1000.0) * refillRate;
        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
            lastRefillTime = now;
        }
    }

    int getRemainingTokens() {
        refill();
        return tokens;
    }
}

public class DistributedRateLimiter {

    private Map<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private static final int LIMIT = 1000;
    private static final double REFILL_RATE = LIMIT / 3600.0;

    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(LIMIT, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry later)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "{used: 0, limit: " + LIMIT + "}";
        }

        int remaining = bucket.getRemainingTokens();
        int used = LIMIT - remaining;

        long resetTime = System.currentTimeMillis() + (remaining / (long)REFILL_RATE) * 1000;

        return "{used: " + used + ", limit: " + LIMIT + ", reset: " + resetTime + "}";
    }

    public static void main(String[] args) {

        DistributedRateLimiter limiter = new DistributedRateLimiter();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));

        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}