package src.main.session;

import src.main.framework.C3Handler;
import src.main.framework.C3Manager;
import main.system.datatypes.DequeImpl;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.util.TimerTask;

public class C3TimerHandler extends C3Handler {
    DequeImpl<C3Timer> keyTimers = new DequeImpl();

    public C3TimerHandler(C3Manager manager) {
        super(manager);
    }

    public void playSystemSound(){
        final Runnable runnable =
                (Runnable) Toolkit.getDefaultToolkit().
                        getDesktopProperty("win.sound.exclamation");
        if (runnable != null)
            runnable.run();
    }

    public void initTimer(C3Session session) {
        Integer minutesTotal = session.getDuration();
        Integer minsBreakInverval = session.getMinsBreakInverval();
                keyTimers.add(initKeyTimer(minutesTotal,minsBreakInverval, session));
    }

    public void timerDone(C3Timer timer) {
        keyTimers.remove(timer);
    }
    private C3Timer initKeyTimer(int minutesTotal, int minsBreakInverval, C3Session session) {
        int key= NativeKeyEvent.VC_PAGE_DOWN;
        int mod= NativeKeyEvent.CTRL_MASK;
        long delay= minsBreakInverval*60*1000;
        long limit=minutesTotal*60*1000;
        C3Timer timer = new C3Timer(this, key, mod, delay, limit, session);
        timer.setIntervalTask(() -> new TimerTask() {
            @Override
            public void run() {
                timer.paused();
                intervalElapsed(timer);
                //TODO wait N seconds returned by elapsed()?
                timer.resumed();
            }
        });

        return timer.init();
    }

    public void shiftBreak() {
        keyTimers.get(0).paused();
        manager.getDialogHandler().showBreakMenu( keyTimers.get(0).getInterval());
        keyTimers.get(0).resumed();
    }


    public enum IntervalOption {
        tray, window, sound,
}
    private void intervalElapsed(C3Timer timer) {
        // intervalOption.switch
        manager.getDialogHandler().showBreakMenu(timer.getInterval());
        // manager.getTrayHandler().notify("Take a break!", "src.main.C3");
        // playSystemSound();
    }


    public boolean checkTimers(int modifiers, int keyCode) {
        for (C3Timer keyTimer : keyTimers) {
            if (keyTimer.check(keyCode, modifiers)) {
                return true;
            }
        }

        return false;
    }
}
