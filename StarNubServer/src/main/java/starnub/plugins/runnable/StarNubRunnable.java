package starnub.plugins.runnable;

public abstract class StarNubRunnable implements Runnable {


    public boolean isShuttingDown;

    public boolean isShuttingDown() {
        return isShuttingDown;
    }

    public void setShuttingDown(boolean isShuttingDown) {
        this.isShuttingDown = isShuttingDown;
    }

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
