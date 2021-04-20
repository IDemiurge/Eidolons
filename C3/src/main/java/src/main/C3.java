package src.main;

import src.main.framework.C3Manager;
import main.system.graphics.GuiManager;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import src.main.query.C3_Query;
import src.main.task.C3_Task;

import java.awt.*;
import java.util.logging.Logger;

public class C3     implements NativeKeyListener {
    private static final int SHIFT = NativeKeyEvent.SHIFT_MASK;
    private static final int ALT = NativeKeyEvent.ALT_MASK;
    private static final int CTRL = NativeKeyEvent.CTRL_MASK;

    private static final int KEY_TASK = NativeKeyEvent.VC_APP_CALCULATOR ;
    private static final int KEY_QUERY = NativeKeyEvent.VC_PAUSE ;

    private C3Manager manager;
    private final boolean sessionTestMode=true;


    // public src.main.C3(ApplicationListener listener ) {
    //     super(listener );
    // }

    public static void main(String[] args) {
        GuiManager.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        GuiManager.init();
        // src.main.C3GdxAdapter gdxAdapter = new src.main.C3GdxAdapter();
        // new src.main.C3(gdxAdapter );
        new C3().init();    }


    public void init() {
        manager = new C3Manager();
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(this);
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setUseParentHandlers(false);

        try {
            manager.getTrayHandler().displayTray();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        manager.getQueryManager().persist();
        if (sessionTestMode) {
            manager.getSessionHandler().initSession();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (manager.getTimerHandler().checkTimers(nativeKeyEvent.getModifiers(), nativeKeyEvent.getKeyCode()))
            return;
        if ((SHIFT & nativeKeyEvent.getModifiers()) != 0) {
            if (nativeKeyEvent.getKeyCode() == KEY_QUERY) {
                if (checkPendingQuery())
                    return;
                C3_Query query= manager.getQueryManager().createRandomQuery();
                if (manager.getQueryResolver().resolve(query))
                    manager.setCurrentQuery(query);
            } else
            if (nativeKeyEvent.getKeyCode() == KEY_TASK) {
                    manager.getSessionHandler().initSession();
                    // if (checkPendingTask())
                    //     return;
                    // C3_Task src.main.task= manager.getTaskManager().createRandomTask();
                    // if (manager.getTaskResolver().resolveTask(src.main.task))
                    //     manager.setCurrentTask(src.main.task);
            }
        }
        if ((ALT & nativeKeyEvent.getModifiers()) != 0) {
            if (nativeKeyEvent.getKeyCode() == KEY_QUERY) {
                if (checkPendingQuery())
                    return;
                C3_Query query= manager.getQueryManager().createQuery();
                if (manager.getQueryResolver().resolve(query))
                manager.setCurrentQuery(query);
            } else
            if (nativeKeyEvent.getKeyCode() == KEY_TASK) {
                if (checkPendingTask())
                    return;
                C3_Task task= manager.getTaskManager().createTask(false); //TODO full manual!
                if (manager.getTaskResolver().resolveTask(task))
                    manager.setCurrentTask(task);
            }
        }

        if ((CTRL & nativeKeyEvent.getModifiers()) != 0) {
            if (nativeKeyEvent.getKeyCode() == KEY_QUERY) {
                manager.getQueryManager().addQuery();
            }
            if (nativeKeyEvent.getKeyCode() == KEY_TASK) {
                manager.getTaskManager().addTask();
            }
        }

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

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
