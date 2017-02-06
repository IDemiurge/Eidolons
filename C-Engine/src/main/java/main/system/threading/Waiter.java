package main.system.threading;

import main.system.auxiliary.Chronos;
import main.system.auxiliary.LogMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Waiter implements Runnable {
    private static final long default_time_limit = 100000; // what if user is
    Lock lock = new ReentrantLock();
    Condition waiting = lock.newCondition();
    Long timeLimit;
    long timeElapsed;
    // afk?! no, no...
    private Object input;
    private WAIT_OPERATIONS operation;
    private boolean interrupted;
    private int n = 0;

    public Waiter(WAIT_OPERATIONS operation) {
        this.operation = operation;
    }

    public Long getDefaultTimeLimitForOperation(WAIT_OPERATIONS operation) {
        switch (operation) {
            case SELECTION:
                return default_time_limit;
            case SELECT_BF_OBJ:
                return default_time_limit;
            case CUSTOM_SELECT:
                return default_time_limit;

        }
        return null;
    }

    public synchronized Object startWaiting() {
        return startWaiting(null);
    }

    public synchronized Object startWaiting(Long timeLimit) {
        LogMaster.log(LogMaster.WAITING_DEBUG, operation.name() + " WAITING STARTED : " + input
                + interrupted);

        new Thread(this, getId()).start();
        n++;
        try {
            lock.lock();
            waiting.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        if (interrupted) {
            input = null;
        }
        return input;
    }

    @Override
    public void run() {
        input = null;
        interrupted = false;
        if (timeLimit != null) {
            Chronos.mark(getId());
        }
        while (input == null && interrupted == false) {
            if (timeLimit != null) {
                if (timeElapsed >= timeLimit) {
                    break;
                } else {
                    timeElapsed = Chronos.getTimeElapsedForMark(getId());
                }
            }
            lock.lock();
            try {
                waiting.await();
            } catch (InterruptedException e1) {

                e1.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        main.system.auxiliary.LogMaster.log(LogMaster.WAITING_DEBUG, operation.name()
                + " WAIT LOOP EXITED WITH : " + input);
        lock.lock();
        waiting.signal();
        lock.unlock();
    }

    private String getId() {
        return "Waiter " + operation + "#" + n;
    }

    // ??? do i need that ???
    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {

        main.system.auxiliary.LogMaster.log(LogMaster.WAITING_DEBUG, "INPUT RECEIVED: " + input);
        if (input == null) {
            interrupt();
            return;
        }

        lock.lock();
        this.input = input;

        waiting.signal();
        lock.unlock();
    }

    public void interrupt() {
        lock.lock();
        interrupted = true;
        input = null;
        waiting.signal();
        lock.unlock();

        main.system.auxiliary.LogMaster.log(LogMaster.WAITING_DEBUG, "WAITER INTERRUPTED: "
                + operation.name());
    }

}
