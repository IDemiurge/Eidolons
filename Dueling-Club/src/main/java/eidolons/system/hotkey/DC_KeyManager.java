package eidolons.system.hotkey;

import com.badlogic.gdx.Input;
import eidolons.content.ValueHelper;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_GameManager;
import eidolons.system.controls.Controller;
import eidolons.system.controls.Controller.CONTROLLER;
import eidolons.system.controls.GlobalController;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ADDITIONAL_MOVE_ACTIONS;
import main.content.enums.entity.ActionEnums.STD_ACTIONS;
import main.content.enums.entity.ActionEnums.STD_MODE_ACTIONS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.Flags;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
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
    GlobalController globalController = new GlobalController();
    private Map<String, Integer> stdActionKeyMap;
    private Map<String, String> customActionKeyMap;
    private Map<String, Integer> stdModeKeyMap;
    private Map<String, Integer> addMoveActionKeyMap;
    //    private Map<String, HOTKEYS> specKeyMap;
    // private Map<Integer, HotKey> keyMap;
    private DC_GameManager mngr;
    private final ACTION_TYPE action_group = ActionEnums.ACTION_TYPE.STANDARD;
    private Controller controller;

    public DC_KeyManager() {

    }
    public DC_KeyManager(DC_GameManager mngr) {
        this.mngr = mngr;
        stdActionKeyMap = new ConcurrentHashMap<>();
        stdModeKeyMap = new ConcurrentHashMap<>();
        addMoveActionKeyMap = new ConcurrentHashMap<>();
        controller = getControllerInstance(DEFAULT_CONTROLLER);
        if (controller == null) {
            controller = new GlobalController();
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
        for (STD_MODE_ACTIONS action : ActionEnums.STD_MODE_ACTIONS.values()) {
            String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
                    G_PROPS.HOTKEY);
            stdModeKeyMap.put(key, i);
            LogMaster.log(LogMaster.CORE_DEBUG, ">> mode hotkey " + key);
            if (action != ActionEnums.STD_MODE_ACTIONS.Defend)
                i++;

        }

    }

    private void initStdHotkeys() {
        int i = 0;
        for (STD_ACTIONS action : ActionEnums.STD_ACTIONS.values()) {
            String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
                    G_PROPS.HOTKEY);
            stdActionKeyMap.put(key, i);
            LogMaster.log(LogMaster.CORE_DEBUG, ">> std hotkey " + key);
            i++;
        }
        i = 0;
        for (ADDITIONAL_MOVE_ACTIONS action : ActionEnums.ADDITIONAL_MOVE_ACTIONS.values()) {
            String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
                    G_PROPS.HOTKEY);
            addMoveActionKeyMap.put(key, i);
            LogMaster.log(LogMaster.CORE_DEBUG, ">> std hotkey " + key);
            i++;
        }
        customActionKeyMap = new LinkedHashMap<>();
        customActionKeyMap.put("l", ActionEnums.STD_SPEC_ACTIONS.On_Alert.toString());
        customActionKeyMap.put("v", ActionEnums.STD_SPEC_ACTIONS.Wait.toString());
        customActionKeyMap.put("p", ActionEnums.STD_SPEC_ACTIONS.Push.toString());
        customActionKeyMap.put("u", ActionEnums.STD_SPEC_ACTIONS.Pull.toString());
        customActionKeyMap.put("g",
                StringMaster.format(
                        ActionEnums.STD_SPEC_ACTIONS.Toggle_Weapon_Set.toString()));
        customActionKeyMap.put("h", ActionEnums.STD_SPEC_ACTIONS.Search_Mode.toString());
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
        return true;
    }

    private boolean checkDebugMaster(KeyEvent e) {
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

        if (Flags.isIDE()){
            GuiEventManager.trigger(GuiEventType.KEY_TYPED, (int) CHAR);
        }
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

            case RULES:
                return RuleKeeper.getInstance();

        }
        return null;
    }

    public boolean handleKeyTyped(int keyMod, char CHAR) {
        if (globalController != null) {
            if (globalController.charTyped(CHAR)) {
                return true;
            }
        }

        if (!Flags.isJar() && !Flags.isJarlike()) {
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
        if (OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.WASD_INDEPENDENT_FROM_FACING)) {
            CHAR = checkReplaceWasd(CHAR);
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

                case ACTION_MASK:
                    // if > 5 => next group
                    actionHotkey((index - 1), action_group);
                    return true;
                case SPELL_MASK:
                default:
                    spellHotkey((index - 1));
                    return true;
            }
        }

        for (String s : customActionKeyMap.keySet()) {
            if (charString.equals(s)) {
                mngr.activateMyAction(customActionKeyMap.get(s));
                return true;
            }
        }
        return false;
    }


    public static char checkReplaceWasd(char aChar) {
        FACING_DIRECTION moveDirection = getAbsoluteDirectionForWasd(aChar);

        if (moveDirection == null)
            return aChar;

        Unit unit = null;
        try {
            unit = Eidolons.getGame().getLoop().getActiveUnit();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (unit == null)
            unit = Eidolons.getMainHero();
        FACING_DIRECTION facing = unit.getFacing();
        return getCorrectedWsad(facing, moveDirection);
    }

    public static FACING_DIRECTION getAbsoluteDirectionForWasd(char aChar) {
        switch (aChar) {
            case 'w':
                return FACING_DIRECTION.NORTH;
            case 'a':
                return FACING_DIRECTION.WEST;
            case 's':
                return FACING_DIRECTION.SOUTH;
            case 'd':
                return FACING_DIRECTION.EAST;
        }
        return null;
    }


    public static char getCorrectedWsad(FACING_DIRECTION facing,
                                        FACING_DIRECTION moveDirection) {
        int degrees = (360 + moveDirection.getDirection().getDegrees()
                - facing.getDirection().getDegrees() + 90) % 360;
        switch (degrees) {
            case 0:
                return 'd';
            case 90:
                return 'w';
            case 180:
                return 'a';
            case 270:
                return 's';
        }
        return 0;
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

    public boolean handleKeyDown(int keyCode) {
        if (controller != globalController) {
            if (controller.keyDown(keyCode) || globalController.keyDown(keyCode))
                return true;
        } else {
            if (controller.keyDown(keyCode)) {
                return true;
            }
        }
            Character CHAR = getKeyTyped(keyCode);
        if (CHAR != null) {
            return handleKeyTyped(0, CHAR);
        }

        return false;
    }

    private Character getKeyTyped(int keyCode) {
        switch (keyCode) {
            case Input.Keys.NUMPAD_8:

            case Input.Keys.UP:
                return 'w';
            case Input.Keys.NUMPAD_4:
            case Input.Keys.LEFT:
                return 'a';
            case Input.Keys.NUMPAD_6:
            case Input.Keys.RIGHT:
                return 'd';
            case Input.Keys.NUMPAD_2:
            case Input.Keys.DOWN:
                return 's';

            case Input.Keys.NUMPAD_7:
                return 'q';
            case Input.Keys.NUMPAD_9:
                return 'e';
            case Input.Keys.NUMPAD_0:
                return ' ';

        }
        return null;
    }
}
