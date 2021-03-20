import framework.C3Manager;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import query.C3_Query;
import task.C3_Task;

import java.util.logging.Logger;

public class C3 implements NativeKeyListener {
    private static final int SHIFT = NativeKeyEvent.SHIFT_MASK;
    private static final int ALT = NativeKeyEvent.ALT_MASK;
    private static final int CTRL = NativeKeyEvent.CTRL_MASK;

    private static final int KEY_QUERY = NativeKeyEvent.VC_END ;
    private static final int KEY_TASK = NativeKeyEvent.VC_HOME ;
    private final C3Manager manager;

    public static void main(String[] args) {
        new C3();
    }


    public C3() {
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
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if ((ALT & nativeKeyEvent.getModifiers()) != 0) {
            if (nativeKeyEvent.getKeyCode() == KEY_QUERY) {
                C3_Query query= manager.getQueryManager().createRandomQuery();
                manager.getQueryResolver().resolve(query);
            }
            if (nativeKeyEvent.getKeyCode() == KEY_TASK) {
                C3_Task task= manager.getTaskManager().createTask();
                manager.getTaskResolver().resolveTask(task);
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

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
