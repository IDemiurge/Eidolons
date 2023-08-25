import framework.C3Manager;
import framework.query.C3_Query;
import framework.task.C3_Task;
import main.system.ExceptionMaster;
import main.system.graphics.GuiManager;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class C3 implements NativeKeyListener {
    private C3Manager manager;
    private final boolean sessionTestMode = false;


    public static void main(String[] args) {
        ExceptionMaster.setFileLoggingOn(false);
        GuiManager.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        GuiManager.init();
        // C3GdxAdapter gdxAdapter = new C3GdxAdapter();
        // new C3(gdxAdapter );
        new C3().init();
    }


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
        logger.setLevel(Level.WARNING);

        try {
            manager.getTrayHandler().displayTray();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        manager.getQueryManager().persist();
        if (sessionTestMode) {
            manager.getSessionHandler().initQuickSession();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        manager.getKeys().resolveKeyPressed(nativeKeyEvent);


    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
