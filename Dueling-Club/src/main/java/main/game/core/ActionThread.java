package main.game.core;

import main.entity.tools.active.Executor;

/**
 * Created by JustMe on 2/21/2017.
 */
public class ActionThread extends Thread {
    private Executor executor;


    @Override
    public void run() {
        while (true) {
            if (getExecutor() != null) {
                try {
                    getExecutor().activate();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    executor = null;
                }
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
