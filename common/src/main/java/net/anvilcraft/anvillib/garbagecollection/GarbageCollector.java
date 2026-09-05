package net.anvilcraft.anvillib.garbagecollection;

import java.util.Set;
import java.util.function.Consumer;

public class GarbageCollector<T> implements Runnable {
    private Set<T> targets;
    private Consumer<T> gcJob;
    private boolean isRunning = false;
    private Thread ownThread = null;

    public GarbageCollector(Set<T> targets, Consumer<T> gcJob) {
        this.targets = targets;
        this.gcJob = gcJob;
    }

    @Override
    public void run() {
        Thread current = Thread.currentThread();
        boolean isInThread = current == ownThread;
        while(isInThread) {
            for (T target : targets) {
                gcJob.accept(target);
            }
            if (isInThread) {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Thread asThread() {
        if (ownThread == null) {
            ownThread = new Thread(this, "AnvilLib Garbage Collector");
        }
        return ownThread;
    }

    public void start() {
        if (!isRunning) {
            asThread().start();
            isRunning = true;
        }
    }
    
}
