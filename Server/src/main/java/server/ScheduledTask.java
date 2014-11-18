package server;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.ScheduledFuture;

@AllArgsConstructor
public class ScheduledTask {

    @Getter
    private String taskOwner;
    @Getter
    private String taskName;
    @Getter
    private Runnable runnable;
    @Getter
    private ScheduledFuture<?> scheduledFuture;
}
