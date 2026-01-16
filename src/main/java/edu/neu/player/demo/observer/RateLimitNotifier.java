package edu.neu.player.demo.observer;

public class RateLimitNotifier implements RateLimitObserver {

    @Override
    public void notifyLimitReached(String clientIp) {
        // Minimal implementation for Milestone 2
        System.out.println("Rate limit reached for IP: " + clientIp);
    }
}