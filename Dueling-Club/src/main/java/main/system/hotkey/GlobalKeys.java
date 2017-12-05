package main.system.hotkey;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import main.client.dc.Launcher;
import main.game.core.game.DC_Game;

import java.awt.event.KeyEvent;

public class GlobalKeys implements HotkeyListener {

    private static boolean globalKeysOn;

    public GlobalKeys() {

    }

    public static boolean isGlobalKeysOn() {
        return globalKeysOn;
    }

    public static void setGlobalKeysOn(boolean globalKeyzOn) {
        globalKeysOn = globalKeyzOn;
    }

    public void initDC_GlobalKeys() {
        initKeys(false, true, false);
    }

    // ++ HC
    public void initMenuGlobalKeys() {

        initKeys(true, true, false);
    }

    public void disable() {
        JIntellitype.getInstance().removeHotKeyListener(this);
    }

    public void initKeys(boolean launcher, boolean dc, boolean av) {
        // if (!Launcher.DEV_MODE)
        // return;
        try {
            globalKeysOn = true;

            JIntellitype.getInstance();
            JIntellitype.getInstance().addHotKeyListener(this);
            if (launcher) {
                JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_ALT,
                        KeyEvent.VK_ESCAPE);
                JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_CONTROL,
                        KeyEvent.VK_ENTER);
                JIntellitype.getInstance().registerHotKey(3, JIntellitype.MOD_ALT,
                        KeyEvent.VK_SPACE);
            }

            if (dc) {
                JIntellitype.getInstance().registerHotKey(4, JIntellitype.MOD_ALT, KeyEvent.VK_F);
                JIntellitype.getInstance().registerHotKey(5, JIntellitype.MOD_ALT, KeyEvent.VK_V);
                JIntellitype.getInstance().registerHotKey(6, JIntellitype.MOD_ALT, KeyEvent.VK_D);
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void onHotKey(final int aIdentifier) {
        new Thread(new Runnable() {
            public void run() {
                switch (aIdentifier) {
                    case 4: {
                        DC_Game.game.getDebugMaster().promptFunctionToExecute();
                        break;
                    }
                    case 5: {
                        DC_Game.game.getValueHelper().promptSetValue();
                        break;
                    }
                    case 6: {
                        DC_Game.game.getDebugMaster().showDebugWindow();
                        break;
                    }

                    case 1: {
                        Launcher.getMainManager().escape();
                    }
                    case 2: {
                        Launcher.getMainManager().enter();
                    }
                    case 3: {
                        Launcher.getMainManager().space();
                    }
                }
            }
        }).start();
    }
    // private void registerHotkey(HotKey key) {
    // main.system.auxiliary.LogMaster.log(1, "hotkey registered: " + key);
    // JIntellitype.getInstance()
    // .registerHotKey(key.id, key.MODE, (int) key.CHAR);
    // keyMap.put(key.id, key);
    //
    // }
    // public void onHotKey(int aIdentifier) {
    //
    // main.system.auxiliary.LogMaster
    // .log(1, "hotkey pressed: " + aIdentifier);
    // HotKey hotkey = keyMap.getOrCreate(aIdentifier);
    // if (hotkey.getHOTKEY() == null)
    // customHotkeyPressed(hotkey);
    // else
    // hotkeyPressed(hotkey.getHOTKEY());
    //
    // }
    // private void registerActionHotkeys() {
    // for (char c : numberChars)
    // registerHotkey(new HotKey(c, true));
    // }
    //
    // private void registerSpellHotkeys() {
    // for (char c : numberChars)
    // registerHotkey(new HotKey(c));
    // }
    //
    // private void registerMovementHotkeys() {
    //
    // }
    //
    // private void registerDefaultHotkeys() {
    // for (HOTKEYS hotKey : HOTKEYS.values()) {
    //
    // HotKey key = new HotKey(hotKey);
    //
    // registerHotkey(key);
    //
    // }
    // }
    //
    //
    //
    // private void customHotkeyPressed(HotKey hotkey) {
    // switch (hotkey.getMODE()) {
    // case DEFAULT_MODE:
    // int index = Integer.valueOf(hotkey.getCHAR() + "");
    // if (index == 0)
    // spellHotkey(9);
    // spellHotkey((index - 1));
    // break;
    //
    // case ALT_MODE:
    // index = Integer.valueOf(hotkey.getCHAR() + "");
    // if (index == 0)
    // actionHotkey(9);
    // actionHotkey((index - 1));
    // break;
    // }
    //
    // }
}
