package main.system;

import io.vertx.core.Vertx;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VetrxHolder {
    private static Vertx instance;
    private static Lock lock = new ReentrantLock();

    public static Vertx getInstance() {
        if (instance == null) {
            lock.lock();
            if (instance == null) {
                instance = Vertx.vertx();
                lock.unlock();
            }
        }

        return instance;
    }
}
