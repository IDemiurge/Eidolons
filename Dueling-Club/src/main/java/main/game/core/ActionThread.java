package main.game.core;

import main.entity.tools.active.Executor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JustMe on 2/21/2017.
 */
public class ActionThread extends Thread {
    private Executor executor;
    private Lock activateLock = new ReentrantLock();
    private volatile boolean activating = false;

    public void activateAllSynchronized() {

    }
    @Override
    public void run() {
        if (!activating) {
            try {
                activateLock.lock();
                if (!activating) {
                    activating = true;
                    getExecutor().activate();
                }
            } finally {
                activateLock.unlock();
                activating = false;
            }
        }

    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
