package server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final String name;

    private final AtomicInteger index = new AtomicInteger(1);

    public NamedThreadFactory(String name) {
        this.name = name + " - ";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name + index.getAndIncrement());
        if (thread.isDaemon())
            thread.setDaemon(false);
        if (thread.getPriority() != Thread.NORM_PRIORITY)
            thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }


}
