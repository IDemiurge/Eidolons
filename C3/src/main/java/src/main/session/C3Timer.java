package src.main.session;

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
    long timeFinished;
    long timePaused;
    private long totalTime;
    boolean paused;

    private Timer regularTimer;
    private Supplier<TimerTask> intervalTask;
    private long interval;
    private Timer exitTimer;

    private long timeLimit;
    private C3Session session;
    private boolean finished;

    public C3Timer(C3TimerHandler c3TimerHandler, int keyCode, int mod, long interval, long timeLimit, C3Session session) {
        manager = c3TimerHandler;
        this.keyCode = keyCode;
        this.mod = mod;
        this.interval = interval;
        this.timeLimit = timeLimit;
        this.session = session;
        session.setTimer(this);
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
                    } else {

                        int minutes = TimeMaster.getMinutes(getTimeLeft());
                        session.setMinutesLeft(minutes);
                        minutes =  (session.getMinsBreakInverval() - TimeMaster.getMinutes((TimeMaster.getTime() - timeStarted)));
                        manager.getManager().getTrayHandler().setTooltip(
                                session+", break in: "+
                                        minutes);
                    }
            }
        },1000, 1000);
        started();
        return this;
    }

    private long getTimeLeft() {
        return timeLimit - (totalTime + (TimeMaster.getTime() - timeStarted));
    }

    private void finished() {
        finished = true;
        timeFinished = TimeMaster.getTime();
        manager.getManager().getSessionHandler().finished(session);
        manager.getManager().getSessionLogger().finished(session);
        manager.timerDone(this);
    }

    private void started() {
        manager.getManager().getSessionLogger().started(session);
        regularTimer.schedule(intervalTask.get(), interval, interval);
        manager.getManager().getTrayHandler().notify(session + " started!\n >>" +
                TimeMaster.getMinutes(getTimeLeft()) +
                " minutes left", "src.main.C3 Session");

    }

    public void resumed() {
        if (finished)
            return;
        paused = false;
        timeStarted = TimeMaster.getTime();
        manager.getManager().getSessionLogger().resumed(session);
        regularTimer = new Timer();
        regularTimer.schedule(intervalTask.get(), interval, interval);
        manager.getManager().getSessionHandler().displayActiveTasks();

        manager.getManager().getTrayHandler().notify("Resumed!\n >>" +
                TimeMaster.getMinutes(getTimeLeft()) +
                " minutes left", "src.main.C3 Session");

    }

    public void paused() {
        if (finished)
            return;
        paused = true;
        timePaused = TimeMaster.getTime();
        totalTime += timePaused - timeStarted;
        manager.getManager().getSessionLogger().paused(session);
        regularTimer.cancel();
        manager.getManager().getTrayHandler().notify("Paused!", "src.main.C3 Session");
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
            paused();
        } else {
            resumed();
        }
    }

    public long getInterval() {
        return interval;
    }

    public void setIntervalTask(Supplier<TimerTask> intervalTask) {
        this.intervalTask = intervalTask;
    }
}
