package edu.neu.player.demo;

public class RequestCounter {
    private final long windowDuration;
    private long windowStart;
    private int requestCount;

    public RequestCounter(long windowDuration) {
        this.windowDuration = windowDuration;
        this.windowStart = System.currentTimeMillis();
        this.requestCount = 0;
    }

    public boolean incrementAndCheckLimit(int maxRequests) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - windowStart >= windowDuration) {
            windowStart = currentTime;
            requestCount = 0;
        }
        requestCount++;
        return requestCount > maxRequests;
    }
}