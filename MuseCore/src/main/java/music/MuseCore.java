package music;

import main.system.graphics.GuiManager;
import music.funcs.MC_Funcs;
import music.tray.MC_Tray;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.util.logging.Logger;

public class MuseCore implements NativeKeyListener {
    private static final int CTRL_MASK = NativeKeyEvent.CTRL_MASK;
    private static final int SHIFT_MASK = NativeKeyEvent.SHIFT_MASK;
    private static final int ALT = 136;
    private int draftStd = 8;

    public static void main(String[] args) {
        new MuseCore().init();
    }

    public void init() {
        GuiManager.init();
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

        try {
            new MC_Tray().displayTray();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        boolean alt = ((SHIFT_MASK & nativeKeyEvent.getModifiers()) != 0);
        boolean ctrl = ((CTRL_MASK & nativeKeyEvent.getModifiers()) != 0);
        boolean caps = ((NativeKeyEvent.CAPS_LOCK_MASK & nativeKeyEvent.getModifiers()) != 0);
        if (ctrl) {
            PlaylistHandler.draft = draftStd;
        } else {
            PlaylistHandler.draft = 0;
        }
        if ((ALT & nativeKeyEvent.getModifiers()) != 0
                || alt) {
            //TODO make into tray func
            //     PlaylistFinder.findAndPlay();
            if (nativeKeyEvent.getKeyCode() > 58) {
                boolean F11_12 = (nativeKeyEvent.getKeyCode() == 87 || nativeKeyEvent.getKeyCode() == 88);
                if (
                        F11_12 || //F11 F12
                                nativeKeyEvent.getKeyCode() <= 58 + PlaylistHandler.PLAYLIST_TYPE.values().length) {
                    try {
                        int index = nativeKeyEvent.getKeyCode() - 59;
                        if (F11_12) {
                            index = nativeKeyEvent.getKeyCode() - 77;
                        }
                        if (index == 3) {
                            return; //ALT F4!
                        }
                        PlaylistHandler.PLAYLIST_TYPE type = PlaylistHandler.PLAYLIST_TYPE.values()[index];

                        if (caps) {
                            PlaylistHandler.play("", MC_Funcs.showAll(alt, false, type));
                        } else
                            PlaylistHandler.playRandom(alt, type);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
            }

            // if ((SHIFT & nativeKeyEvent.getModifiers()) != 0) {
            //     if (nativeKeyEvent.getKeyCode() > 58) {
            //         if (nativeKeyEvent.getKeyCode() < 70) {
            //             try {
            //                 int index = nativeKeyEvent.getKeyCode() - 59;
            //                 PlaylistHandler.playRandom(PlaylistHandler.PLAYLIST_TYPE.values()[index]);
            //             } catch (Exception e) {
            //                 main.system.ExceptionMaster.printStackTrace(e);
            //             }
            //         }
            //     }
            // }
            //            default:
            //                main.system.auxiliary.src.main.system.log.LogMaster.src.main.system.log(1, " nativeKeyEvent.getModifiers()=" +
            //                        nativeKeyEvent.getModifiers());
        }

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
