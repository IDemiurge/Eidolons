package system.hotkey;

import framework.C3Handler;
import framework.C3Manager;
import framework.query.C3_Query;
import framework.task.C3_Task;
import org.jnativehook.keyboard.NativeKeyEvent;

public class C3KeyResolver extends C3Handler {
    private static final int SHIFT = NativeKeyEvent.SHIFT_MASK;
    private static final int ALT = NativeKeyEvent.ALT_MASK;
    private static final int CTRL = NativeKeyEvent.CTRL_MASK;

    private static final int KEY_QUERY = NativeKeyEvent.VC_SCROLL_LOCK;
    private static final int KEY_PAUSE = NativeKeyEvent.VC_PAUSE;
    private static final int KEY_SESSION = NativeKeyEvent.VC_MEDIA_PLAY;

    public C3KeyResolver(C3Manager manager) {
        super(manager);
    }

    public void resolveKeyPressed(NativeKeyEvent event) {
        resolveKeyPressed(event.getModifiers(), event.getKeyCode());
    }

    public void resolveKeyPressed(int mods, int charCode) {
        //silent pause?!
        if (manager.getTimerHandler().checkTimers(mods, charCode))
            return;
        boolean alt = (ALT & mods) != 0;
        boolean shift = (SHIFT & mods) != 0;
        boolean ctrl = (CTRL & mods) != 0;

        //////////PAUSE/////////////
        if (charCode == KEY_PAUSE) {
            if (alt) {
                //abandon
                manager.getSessionHandler().abortSession(manager.getSessionHandler().getCurrentSession());
            } else if (shift) {
                //pause (ezDraft)
                manager.getDialogHandler().ezChoiceDraft();
            } else if (ctrl) {
                //Review
            } else {
                // manager.getSessionHandler().displayActiveTasks();
                //pause (just pause?)
                manager.getTimerHandler().takeBreak();
            }
        }
        //////////QUERY/////////////
        if (charCode == KEY_QUERY) {
            if (alt) {
                if (checkPendingQuery())
                    return;
                C3_Query query = manager.getQueryManager().createQuery();
                if (manager.getQueryResolver().resolve(query))
                    manager.setCurrentQuery(query);
            } else if (shift) {
            } else if (ctrl) {
            } else {

            }
            //exercise
            //query
            // <?>
        }
        //////////SESSION/////////////
        if (charCode == KEY_SESSION) {
            if (alt) {
            } else if (shift) {
                manager.getSessionHandler().initQuickSession();
            } else if (ctrl) {
                manager.getSessionHandler().initSession();
                //default ? last?
            }
        }
        //TODO
        // if ((CTRL & mods) != 0) {
        //     if (charCode == KEY_PAUSE) {
        //         manager.getQueryManager().addQuery();
        //     }
        //     if (charCode == KEY_QUERY) {
        //         manager.getTaskManager().addTask();
        //     }
        // }


    }

    private boolean checkPendingQuery() {
        if (manager.getCurrentQuery() != null) {
            manager.getQueryResolver().promptQueryInput(manager.getCurrentQuery());
            manager.setCurrentQuery(null);
            return true;
        }
        return false;
    }

    private boolean checkPendingTask() {
        if (manager.getCurrentTask() != null) {
            manager.getTaskResolver().promptTaskInput(manager.getCurrentTask());
            manager.setCurrentTask(null);
            return true;
        }
        return false;
    }
}
