package server.plugins.runnable;

import lombok.Getter;
import lombok.Setter;

public abstract class StarNubRunnable implements Runnable {

    @Getter @Setter
    public boolean isShuttingDown;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public abstract void run();
}
