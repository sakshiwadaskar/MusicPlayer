package edu.neu.player.demo.decorator;



import edu.neu.player.demo.strategy.RateLimitStrategy;

//@Component
public  class LoggingRateLimitDecorator implements RateLimitStrategy {
    private final RateLimitStrategy wrapped;

    public LoggingRateLimitDecorator(RateLimitStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isAllowed(String clientIp) {
        System.out.println("Request received from IP: " + clientIp);
        return wrapped.isAllowed(clientIp);
    }
}
