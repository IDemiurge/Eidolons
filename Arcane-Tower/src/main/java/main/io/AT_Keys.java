package main.io;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import main.ArcaneTower;
import main.session.Session;
import main.session.SessionMaster;
import main.system.auxiliary.GuiManager;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class AT_Keys implements HotkeyListener {
    private static boolean globalKeysOn;

    public static void initKeys() {
        try {
            globalKeysOn = true;
            JIntellitype.getInstance();
            JIntellitype.getInstance().addHotKeyListener(new AT_Keys());

            JIntellitype.getInstance().registerHotKey(0, JIntellitype.MOD_ALT
                    // | JIntellitype.MOD_CONTROL | JIntellitype.MOD_SHIFT,
                    , KeyEvent.VK_CAPS_LOCK);
            JIntellitype.getInstance().registerHotKey(1,
                    JIntellitype.MOD_ALT | JIntellitype.MOD_CONTROL | JIntellitype.MOD_SHIFT,
                    KeyEvent.VK_Q);
            JIntellitype.getInstance().registerHotKey(2,
                    JIntellitype.MOD_ALT | JIntellitype.MOD_WIN | JIntellitype.MOD_SHIFT,
                    KeyEvent.VK_G);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHotKey(int i) {
        switch (i) {

            case 0:
                JFrame window = ArcaneTower.getSessionWindow().getWindow();
                GuiManager.toggleVisible(window);
                GuiManager.toggleFocus(window);
                break;
            case 1:
                Session session = ArcaneTower.getSessionWindow().getSession();
                if (session.isPaused()) {
                    SessionMaster.sessionResumed(session);
                } else {
                    SessionMaster.sessionPaused(session);
                    onHotKey(0);
                }
                break;
            case 2:
                window = ArcaneTower.getGateWindow().getWindow();
                GuiManager.toggleVisible(window);
                GuiManager.toggleFocus(window);
                break;
        }
    }

}
