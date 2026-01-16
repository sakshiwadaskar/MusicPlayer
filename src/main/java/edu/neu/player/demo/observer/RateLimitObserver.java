package edu.neu.player.demo.observer;


public interface RateLimitObserver {
    void notifyLimitReached(String clientIp);
}
