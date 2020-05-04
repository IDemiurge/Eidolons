package main.utilities.music;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MuseCore implements NativeKeyListener {
    public void init(){
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(this);
        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        main.system.auxiliary.log.LogMaster.log(1," nativeKeyEvent.getModifiers()=" +
                nativeKeyEvent.getModifiers() +
                "\n nativeKeyTyped " +nativeKeyEvent.getKeyCode());
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        main.system.auxiliary.log.LogMaster.log(1,"nativeKeyPressed " +nativeKeyEvent.getKeyCode());
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        main.system.auxiliary.log.LogMaster.log(1,"nativeKeyReleased " +nativeKeyEvent.getKeyCode());

    }
}
