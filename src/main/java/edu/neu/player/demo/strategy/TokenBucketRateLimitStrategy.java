package edu.neu.player.demo.strategy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class TokenBucketRateLimitStrategy implements RateLimitStrategy {
    // Placeholder implementation for Token Bucket strategy.
    private final ConcurrentHashMap<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();
    private final int MAX_TOKENS = 8; // Maximum tokens in the bucket
    private final long REFILL_INTERVAL = TimeUnit.SECONDS.toMillis(15); // Refill interval in milliseconds
    private final int TOKENS_PER_REFILL = 2; // Tokens added per refill

    @Override
    public boolean isAllowed(String clientIp) {
        // Retrieve or create the token bucket for the given client IP
        TokenBucket bucket = tokenBuckets.computeIfAbsent(clientIp, k -> new TokenBucket(MAX_TOKENS, TOKENS_PER_REFILL, REFILL_INTERVAL));
        synchronized (bucket) {
            return bucket.consumeToken();
        }
    }

    // Inner class for managing token buckets
    private static class TokenBucket {
        private int tokens;
        private final int maxTokens;
        private final int tokensPerRefill;
        private final long refillIntervalMillis;
        private long lastRefillTime;

        public TokenBucket(int maxTokens, int tokensPerRefill, long refillIntervalMillis) {
            this.maxTokens = maxTokens;
            this.tokensPerRefill = tokensPerRefill;
            this.refillIntervalMillis = refillIntervalMillis;
            this.tokens = maxTokens; // Initialize with max tokens
            this.lastRefillTime = System.currentTimeMillis();
        }

        public boolean consumeToken() {
            refillTokensIfNecessary();

            if (tokens > 0) {
                tokens--;
                return true; // Request is allowed
            }
            return false; // Request is rejected due to no tokens
        }

        private void refillTokensIfNecessary() {
            long currentTime = System.currentTimeMillis();
            long timeSinceLastRefill = currentTime - lastRefillTime;

            if (timeSinceLastRefill >= refillIntervalMillis) {
                int refills = (int) (timeSinceLastRefill / refillIntervalMillis);
                tokens = Math.min(maxTokens, tokens + refills * tokensPerRefill);
                lastRefillTime += refills * refillIntervalMillis;
            }
        }
    }


}




