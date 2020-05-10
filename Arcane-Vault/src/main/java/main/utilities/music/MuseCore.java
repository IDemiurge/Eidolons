package main.utilities.music;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MuseCore implements NativeKeyListener {
    private static final int SHIFT = 1;
    private static final int ALT =8;

    public void init() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
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
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        switch (nativeKeyEvent.getModifiers()) {
            case ALT:
                if (nativeKeyEvent.getKeyCode() > 58) {
                    if (nativeKeyEvent.getKeyCode() <= 61) {
                        try {
                            int index = nativeKeyEvent.getKeyCode() - 59;
                            PlaylistHandler.playRandom(PlaylistHandler.PLAYLIST_TYPE.values()[index]);
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }
                }
                break;
            case SHIFT:
                if (nativeKeyEvent.getKeyCode() > 58) {
                    if (nativeKeyEvent.getKeyCode() < 70) {
                        try {
                            int index = nativeKeyEvent.getKeyCode() - 59;
                            PlaylistHandler.playRandom(PlaylistHandler.PLAYLIST_TYPE.values()[index]);
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }
                }
                break;
//            default:
//                main.system.auxiliary.log.LogMaster.log(1, " nativeKeyEvent.getModifiers()=" +
//                        nativeKeyEvent.getModifiers());
        }

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
