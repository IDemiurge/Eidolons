package session;

import main.system.auxiliary.TimeMaster;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class C3Timer {
    private C3TimerHandler manager;
    /*
       This is only for controlled timers - exit/resume
         */
    int keyCode;
    int mod;
    // Runnable onElapse;
    // Runnable onKey;

    long timeStarted;
    long timePaused;
    private long totalTime;
    boolean paused;

    private Timer regularTimer;
    private Supplier<TimerTask> ringTask;
    private long ringDelay;
    private Timer exitTimer;

    private long timeLimit;
    private C3Session session;

    public C3Timer(C3TimerHandler c3TimerHandler, int keyCode, int mod, Supplier<TimerTask> ringTask, long ringDelay, long timeLimit, C3Session session) {
        manager = c3TimerHandler;
        this.keyCode = keyCode;
        this.mod = mod;
        this.ringTask = ringTask;
        this.ringDelay = ringDelay;
        this.timeLimit = timeLimit;
        this.session = session;
    }

    public C3Timer init() {
        timeStarted = TimeMaster.getTime();
        regularTimer = new Timer();
        exitTimer = new Timer();
        exitTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!paused)
                    if (getTimeLeft() <= 0) {
                        finished();
                        exitTimer.cancel();
                    }
                //TODO update tray icon?
            }
        },1000, 1000);
        started();
        return this;
    }

    private long getTimeLeft() {
        return timeLimit - (totalTime + (TimeMaster.getTime() - timeStarted));
    }

    private void finished() {
        manager.getManager().getSessionLogger().finished(session);
        manager.timerDone(this);
    }

    private void started() {
        manager.getManager().getSessionLogger().started(session);
        regularTimer.schedule(ringTask.get(), ringDelay, ringDelay);
        manager.getManager().getTrayHandler().notify(session + " started!\n >>" +
                TimeMaster.getMinutes(getTimeLeft()) +
                " minutes left", "C3 Session");

    }

    private void resumed() {
        manager.getManager().getSessionLogger().resumed(session);
        regularTimer = new Timer();
        regularTimer.schedule(ringTask.get(), ringDelay, ringDelay);
        manager.getManager().getTrayHandler().notify("Resumed!\n >>" +
                TimeMaster.getMinutes(getTimeLeft()) +
                " minutes left", "C3 Session");

    }

    private void paused() {
        manager.getManager().getSessionLogger().paused(session);
        regularTimer.cancel();
        manager.getManager().getTrayHandler().notify("Paused!", "C3 Session");
    }

    public boolean check(int keyCode, int keyMode) {
        if ((keyMode & this.mod) != 0) {
            if (keyCode == this.keyCode) {
                toggle();
                return true;
            }
        }
        return false;
    }

    private void toggle() {
        paused = !paused;
        if (paused) {
            timePaused = TimeMaster.getTime();
            totalTime += timePaused - timeStarted;
            paused();
        } else {
            timeStarted = TimeMaster.getTime();
            resumed();
        }
    }


}
