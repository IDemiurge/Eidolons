package main.system.hotkey;

import com.melloware.jintellitype.JIntellitype;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.game.core.game.DC_GameManager;
import main.game.logic.generic.DC_ActionManager.ADDITIONAL_MOVE_ACTIONS;
import main.game.logic.generic.DC_ActionManager.STD_ACTIONS;
import main.game.logic.generic.DC_ActionManager.STD_MODE_ACTIONS;
import main.libgdx.anims.controls.AnimController;
import main.libgdx.anims.controls.Controller;
import main.libgdx.anims.controls.Controller.CONTROLLER;
import main.libgdx.anims.controls.EmitterController;
import main.rules.RuleMaster;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.entity.ValueHelper;
import main.test.debug.DebugController;
import main.test.debug.DebugMaster;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DC_KeyManager
// extends KeyboardFocusManager
 implements KeyListener {

    public static final int DEFAULT_MODE = JIntellitype.MOD_CONTROL;
    public static final int ALT_MODE = JIntellitype.MOD_ALT;
    public static final String numberChars = "1234567890";
    private static final int SPELL_MASK = KeyEvent.CTRL_DOWN_MASK;
    private static final int ACTION_MASK = KeyEvent.ALT_DOWN_MASK;
    private static final int ITEM_MASK = KeyEvent.ALT_DOWN_MASK + KeyEvent.CTRL_DOWN_MASK;
    G_PagePanel<?> p;
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
        int i = 0;
        for (STD_MODE_ACTIONS action : STD_MODE_ACTIONS.values()) {
            String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
             G_PROPS.HOTKEY);
            stdModeKeyMap.put(key, i);
            LogMaster.log(LogMaster.CORE_DEBUG, ">> mode hotkey " + key);
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
        if (checkDebugMaster(e)) {
            return true;
        }

        return false;
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
        if (GlobalKeys.isGlobalKeysOn()) {
            return false;
        }
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
        if (e == 'T') {//CONTROLLER_TOGGLE
//            if (keyMod==KeyEvent.SHIFT_MASK) {
//                    chooseEnum
            CONTROLLER c =
             new EnumMaster<CONTROLLER>().selectEnum(CONTROLLER.class);
            switch (c) {
                case ACTION:
                    controller = null;
                    break;
                case ANIM:
                    controller = AnimController.getInstance();
                    break;
                case DEBUG:
                        controller = DebugController.getInstance();
                    break;
                case RULES:
                    controller = RuleMaster.getInstance();
                    break;
                case EMITTER:
                    controller = EmitterController.getInstance();
                    break;
            }
            return true;
//            }
        }
        return false;
    }
    public void handleKeyTyped(int keyMod, char CHAR) {
        if (checkControllerHotkey(keyMod, CHAR)) {
            return;
        }
        if (controller != null) {
            try {
                if (controller.charTyped(CHAR)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }

        int index = -1;
        if (numberChars.indexOf(CHAR) != -1) {
            index = Integer.valueOf(CHAR + "");
        }
        if (index == -1) {
            if (stdActionKeyMap.containsKey(CHAR + "")) {
                actionHotkey(stdActionKeyMap.get(CHAR + ""), ActionEnums.ACTION_TYPE.STANDARD);
                return;
            }
            if (addMoveActionKeyMap.containsKey(CHAR + "")) {
                actionHotkey(addMoveActionKeyMap.get(CHAR + ""), ActionEnums.ACTION_TYPE.ADDITIONAL_MOVE);
                return;
            }
            if (stdModeKeyMap.containsKey(CHAR + "")) {
                actionHotkey(stdModeKeyMap.get(CHAR + ""), ActionEnums.ACTION_TYPE.MODE);
                return;
            }


        } else {

            if (index == 0) {
                index = 10;
            }

            switch (keyMod) {
                case ITEM_MASK:
                    itemHotkey((index - 1));
                case SPELL_MASK:
                    spellHotkey((index - 1));
                    break;

                case ACTION_MASK:
                    // if > 5 => next group
                    actionHotkey((index - 1), action_group);
                    break;
            }
        }
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
        mngr.activateMyAction(index, group);

    }

    private void hotkeyPressed(HOTKEYS hotKey) {
        switch (hotKey) {
            case END_TURN:
                mngr.endTurn();
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

}
