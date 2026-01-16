package edu.neu.player.demo.strategy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import edu.neu.player.demo.RequestCounter;

//This strategy means that during a 1 minute window, only 5 requests can be sent to change the song or play a song

public class FixedWindowRateLimitStrategy implements RateLimitStrategy {
    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS_PER_WINDOW = 5;
    private final long WINDOW_DURATION = TimeUnit.MINUTES.toMillis(1);

    @Override
    public boolean isAllowed(String clientIp) {
        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter(WINDOW_DURATION));
        synchronized (counter) {
            return !counter.incrementAndCheckLimit(MAX_REQUESTS_PER_WINDOW);
        }
    }
}