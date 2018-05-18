package eidolons.system.hotkey;

import eidolons.entity.active.DC_ActionManager.ADDITIONAL_MOVE_ACTIONS;
import eidolons.entity.active.DC_ActionManager.STD_ACTIONS;
import eidolons.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.game.DC_GameManager;
import eidolons.libgdx.anims.controls.AnimController;
import eidolons.libgdx.anims.controls.EmitterController;
import eidolons.system.controls.Controller;
import eidolons.system.controls.Controller.CONTROLLER;
import eidolons.system.controls.GlobalController;
import eidolons.test.debug.DebugController;
import eidolons.test.debug.DebugMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;
import eidolons.content.ValueHelper;
import main.system.launch.CoreEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DC_KeyManager
// extends KeyboardFocusManager
 implements KeyListener {

    public static final int DEFAULT_MODE = 2;
    public static final int ALT_MODE = 1;
    public static final String numberChars = "1234567890";
    private static final int SPELL_MASK = KeyEvent.CTRL_DOWN_MASK;
    private static final int ACTION_MASK = KeyEvent.ALT_DOWN_MASK;
    private static final int ITEM_MASK = KeyEvent.ALT_DOWN_MASK + KeyEvent.CTRL_DOWN_MASK;
    public static CONTROLLER DEFAULT_CONTROLLER = CONTROLLER.DEBUG;
    G_PagePanel<?> p;
    GlobalController globalController = new GlobalController();
    private Map<String, Integer> stdActionKeyMap;
    private Map<String, Integer> stdModeKeyMap;
    private Map<String, Integer> addMoveActionKeyMap;
    private Map<String, HOTKEYS> specKeyMap;
    // private Map<Integer, HotKey> keyMap;
    private DC_GameManager mngr;
    private ACTION_TYPE action_group = ActionEnums.ACTION_TYPE.STANDARD;
    private Controller controller;

    public DC_KeyManager(DC_GameManager mngr) {
        this.mngr = mngr;
        stdActionKeyMap = new ConcurrentHashMap<>();
        stdModeKeyMap = new ConcurrentHashMap<>();
        addMoveActionKeyMap = new ConcurrentHashMap<>();
        controller = getControllerInstance(DEFAULT_CONTROLLER);
        if (EmitterController.overrideKeys) {
            controller = EmitterController.getInstance();
        }
    }

    public void initHotkeysForUnit() {
        // quick panel?
    }

    public void init() {
        initStdHotkeys();
        initStdModeHotkeys();

    }

    private void initStdModeHotkeys() {
        int i = 0; //TODO [quick fix] - due to "either camp or defend", one removed always
        for (STD_MODE_ACTIONS action : STD_MODE_ACTIONS.values()) {
            String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
             G_PROPS.HOTKEY);
            stdModeKeyMap.put(key, i);
            LogMaster.log(LogMaster.CORE_DEBUG, ">> mode hotkey " + key);
            if (action != STD_MODE_ACTIONS.Defend)
                i++;

        }

    }

    private void initStdHotkeys() {
        int i = 0;
        for (STD_ACTIONS action : STD_ACTIONS.values()) {
            String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
             G_PROPS.HOTKEY);
            stdActionKeyMap.put(key, i);
            LogMaster.log(LogMaster.CORE_DEBUG, ">> std hotkey " + key);
            i++;
        }
        i = 0;
        for (ADDITIONAL_MOVE_ACTIONS action : ADDITIONAL_MOVE_ACTIONS.values()) {
            String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
             G_PROPS.HOTKEY);
            addMoveActionKeyMap.put(key, i);
            LogMaster.log(LogMaster.CORE_DEBUG, ">> std hotkey " + key);
            i++;
        }

    }

    private boolean checkCustomHotkey(KeyEvent e) {


        if (!e.isAltDown()) {
            return false;
        }
        // if (!e.isControlDown())
        // return false;
        if (checkFunctionHelper(e)) {
            return true;
        }
        if (checkValueHelper(e)) {
            return true;
        }
        return checkDebugMaster(e);

    }

    private boolean checkValueHelper(KeyEvent e) {
        // if (GlobalKeys.isGlobalKeysOn())
        // return false;
        if (e.getKeyChar() != ValueHelper.HOTKEY_CHAR) {
            return false;
        }
        mngr.getGame().getValueHelper().promptSetValue();
        return true;
    }

    private boolean checkFunctionHelper(KeyEvent e) {

        if (e.getKeyChar() != DebugMaster.FUNCTION_HOTKEY_CHAR) {
            return false;
        }

        mngr.getGame().getDebugMaster().promptFunctionToExecute();
        return true;
    }

    private boolean checkDebugMaster(KeyEvent e) {
        // if (GlobalKeys.isGlobalKeysOn())
        // return false;
        if (e.getKeyChar() != DebugMaster.HOTKEY_CHAR) {
            return false;
        }
        mngr.getGame().getDebugMaster().showDebugWindow();
        return true;
    }

    // return boolean to know if success/failure and play a sound!
    @Override
    public void keyTyped(KeyEvent e) {
        LogMaster.log(LogMaster.GUI_DEBUG, "key typed: " + e.getKeyChar());
        if (mngr.getActiveObj().isAiControlled()) {
            return; // play random sound!...
        }


        char CHAR = (e.getKeyChar());
        int keyMod = e.getModifiers();
//        arrowPressed(e); TODO
        handleKeyTyped(keyMod, CHAR);
    }

    private boolean checkControllerHotkey(int keyMod, char e) {
        if (e == 'T') {
            selectController();
            return true;
        }
//        if (e == 'G') {
//            toggleGlobalController();
//            return true;
//        }
        return false;
    }

    private void selectController() {
        CONTROLLER c =
         new EnumMaster<CONTROLLER>().selectEnum(CONTROLLER.class);
        if (c == null) {
            controller = new GlobalController();
        } else {
            controller = getControllerInstance(c);
        }
    }

    private Controller getControllerInstance(CONTROLLER c) {
        if (c == null) {
            return null;
        }
        switch (c) {
            case ACTION:
                return null;

            case ANIM:
                return AnimController.getInstance();

            case DEBUG:
                return DebugController.getInstance();

            case RULES:
                return RuleKeeper.getInstance();

            case EMITTER:
                return EmitterController.getInstance();

        }
        return null;
    }

    public boolean handleKeyTyped(int keyMod, char CHAR) {
        if (globalController != null) {
            if (globalController.charTyped(CHAR)) {
                return true;
            }
        }
        if (!CoreEngine.isJar() && !CoreEngine.isJarlike()) {
            if (checkControllerHotkey(keyMod, CHAR)) {
                return true;
            }

            if (controller != null) {
                try {
                    if (controller.charTyped(CHAR)) {
                        return true;
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    return false;
                }
            }
        }
        int index = -1;
        String charString = CHAR + "";
        boolean preview = false;
        if (Character.isUpperCase(CHAR)) {
            preview = true;
            charString = charString.toLowerCase();
        }
        if (numberChars.indexOf(CHAR) != -1) {
            index = Integer.valueOf(charString);
        }
        if (index == -1) {
            if (stdActionKeyMap.containsKey(charString)) {
                actionHotkey(stdActionKeyMap.get(charString), ActionEnums.ACTION_TYPE.STANDARD, preview);
                return true;
            }
            if (addMoveActionKeyMap.containsKey(charString)) {
                actionHotkey(addMoveActionKeyMap.get(charString), ActionEnums.ACTION_TYPE.ADDITIONAL_MOVE, preview);
                return true;
            }
            if (stdModeKeyMap.containsKey(charString)) {
                actionHotkey(stdModeKeyMap.get(charString), ActionEnums.ACTION_TYPE.MODE, preview);
                return true;
            }


        } else {

            if (index == 0) {
                index = 10;
            }

            switch (keyMod) {
                case ITEM_MASK:
                    itemHotkey((index - 1));
                    return true;
                case SPELL_MASK:
                    spellHotkey((index - 1));
                    return true;

                case ACTION_MASK:
                    // if > 5 => next group
                    actionHotkey((index - 1), action_group);
                    return true;
            }
        }
        return false;
    }


    private void arrowPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                // handle up
                break;
            case KeyEvent.VK_DOWN:
                // handle down
                break;
            case KeyEvent.VK_LEFT:
                // handle left
                break;
            case KeyEvent.VK_RIGHT:
                // handle right
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // SHIFT: swap action panel
        // ALT: highlight
        // CTRL: attack!
        // main.system.auxiliary.LogMaster.log(1, "keyPressed: " +
        // e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // main.system.auxiliary.LogMaster
        // .log(1, "keyReleased: " + e.getKeyChar());
    }

    private void spellHotkey(int index) {
        mngr.activateMySpell(index);

    }

    private void itemHotkey(int index) {
        // manager.activateMyItem(index);

    }

    private void actionHotkey(int index, ACTION_TYPE group) {
        actionHotkey(index, group, false);
    }

    private void actionHotkey(int index, ACTION_TYPE group, boolean preview) {
        if (preview) {
            mngr.previewMyAction(index, group);
        } else mngr.activateMyAction(index, group);
    }

    private void hotkeyPressed(HOTKEYS hotKey) {
        switch (hotKey) {
            case END_TURN:
                mngr.endRound();
                break;
            case SELECT_HERO:
                mngr.selectMyHero();
                break;
            default:
                break;

        }

    }

    private void specialActionKeyPressed(String key) {
        int n = stdActionKeyMap.get(key);
        actionHotkey(n, action_group);
    }

    public void handleKeyDown(int keyCode) {
        controller.keyDown(keyCode);
        globalController.keyDown(keyCode);
    }
}
