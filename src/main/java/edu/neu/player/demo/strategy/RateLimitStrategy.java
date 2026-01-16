package edu.neu.player.demo.strategy;

public interface RateLimitStrategy {
    boolean isAllowed(String clientIp);
}
