package music;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Logger;

public class MuseCore implements NativeKeyListener {
    private static final int SHIFT = 17;
    private static final int ALT = 136;

    public static void main(String[] args) {
        new MuseCore().init();
    }

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
        // logger.setLevel(Level.OFF);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if ((ALT & nativeKeyEvent.getModifiers()) != 0) {
            if (nativeKeyEvent.getKeyCode() > 58) {
                if (nativeKeyEvent.getKeyCode() <= 58 + PlaylistHandler.PLAYLIST_TYPE.values().length) {
                    try {
                        int index = nativeKeyEvent.getKeyCode() - 59;
                        if (index==3) {
                            return ; //ALT F4!
                        }
                        PlaylistHandler.playRandom(PlaylistHandler.PLAYLIST_TYPE.values()[index]);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
            }

            if ((SHIFT & nativeKeyEvent.getModifiers()) != 0) {
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
            }
            //            default:
            //                main.system.auxiliary.log.LogMaster.log(1, " nativeKeyEvent.getModifiers()=" +
            //                        nativeKeyEvent.getModifiers());
        }

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
