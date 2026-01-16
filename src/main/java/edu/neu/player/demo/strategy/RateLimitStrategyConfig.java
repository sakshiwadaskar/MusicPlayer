package edu.neu.player.demo.strategy;
import edu.neu.player.demo.observer.ObservableRateLimit;
import edu.neu.player.demo.observer.RateLimitNotifier;

public class RateLimitStrategyConfig {

    /**
     * Creates a rate limit strategy for premium users.
     */
    public static RateLimitStrategy createPremiumStrategy() {
        return new TokenBucketRateLimitStrategy();
    }

    /**
     * Creates a rate limit strategy for standard users.
     */
    public static RateLimitStrategy createStandardStrategy() {
        return new FixedWindowRateLimitStrategy();
    }

    /**
     * Wraps a strategy with Logging and Observer support.
     */
    public static ObservableRateLimit createObservableRateLimiter(RateLimitStrategy strategy) {
        // Optional decorator – enable if needed
        // strategy = new LoggingRateLimitDecorator(strategy);

        ObservableRateLimit observable = new ObservableRateLimit(strategy);
        observable.addObserver(new RateLimitNotifier());
        return observable;
    }

    /**
     * Utility method: Build everything in one call.
     */
    public static ObservableRateLimit buildRateLimiter(boolean isPremiumUser) {
        RateLimitStrategy strategy = isPremiumUser
                ? createPremiumStrategy()
                : createStandardStrategy();

        return createObservableRateLimiter(strategy);
    }

    /**
     * Method expected by Test.java:
     * Returns a concrete RateLimitStrategy based on a string key.
     */
    public static RateLimitStrategy getStrategy(String strategyType) {
        if (strategyType == null) {
            // default to standard
            return createStandardStrategy();
        }

        String t = strategyType.trim().toLowerCase();

        switch (t) {
            case "premium":
            case "tokenbucket":
            case "token_bucket":
            case "token-bucket":
                return createPremiumStrategy();

            case "standard":
            case "fixedwindow":
            case "fixed_window":
            case "fixed-window":
            default:
                return createStandardStrategy();
        }
    }
}
