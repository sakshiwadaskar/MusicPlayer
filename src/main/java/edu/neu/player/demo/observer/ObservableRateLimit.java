package edu.neu.player.demo.observer;


import edu.neu.player.demo.strategy.RateLimitStrategy;

import java.util.ArrayList;
import java.util.List;

public class ObservableRateLimit implements RateLimitStrategy {

    private final RateLimitStrategy wrappedStrategy;     // The real algorithm (Fixed Window for M2)
    private final List<RateLimitObserver> observers = new ArrayList<>();

    public ObservableRateLimit(RateLimitStrategy wrappedStrategy) {
        this.wrappedStrategy = wrappedStrategy;
    }

    public void addObserver(RateLimitObserver observer) {
        observers.add(observer);
    }

    @Override
    public boolean isAllowed(String clientIp) {
        boolean allowed = wrappedStrategy.isAllowed(clientIp);

        if (!allowed) {
            notifyObservers(clientIp);
        }

        return allowed;
    }

    private void notifyObservers(String clientIp) {
        for (RateLimitObserver observer : observers) {
            observer.notifyLimitReached(clientIp);
        }
    }
}
