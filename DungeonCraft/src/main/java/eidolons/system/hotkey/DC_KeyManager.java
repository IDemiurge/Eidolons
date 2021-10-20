package eidolons.system.hotkey;

import com.badlogic.gdx.Input;
import eidolons.content.consts.HOTKEYS;
import eidolons.content.values.ValueHelper;
import eidolons.game.core.game.DC_GameManager;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ADDITIONAL_MOVE_ACTIONS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.Flags;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DC_KeyManager implements KeyListener {

    private final ACTION_TYPE action_group = ActionEnums.ACTION_TYPE.STANDARD;
    public static final int DEFAULT_MODE = 2;
    public static final String numberChars = "1234567890";

    private Map<String, Integer> stdActionKeyMap;
    private Map<String, String> customActionKeyMap;
    private Map<String, Integer> addMoveActionKeyMap;

    private DC_GameManager mngr;

    public DC_KeyManager(DC_GameManager mngr) {
        this.mngr = mngr;
        stdActionKeyMap = new ConcurrentHashMap<>();
        addMoveActionKeyMap = new ConcurrentHashMap<>();
    }

    public void init() {
        initStdHotkeys();
        initFeatSpaceHotkeys();

    }

    private void initFeatSpaceHotkeys() {
        for (int i = 0; i < 6; i++) {
            //TODO
        }
    }

    /*
    HotKeys
    1-6 / 7-= for SPACES
    std actions - [z][x][c][v][b][n] ?
     */
    private void initStdHotkeys() {
        // int i = 0;
        // for (ADDITIONAL_MOVE_ACTIONS action : ActionEnums.ADDITIONAL_MOVE_ACTIONS.values()) {
        //     String key = DataManager.getType(action.toString(), DC_TYPE.ACTIONS).getProperty(
        //             G_PROPS.HOTKEY);
        //     addMoveActionKeyMap.put(key, i);
        //     LogMaster.log(LogMaster.CORE_DEBUG, ">> std hotkey " + key);
        //     i++;
        // }

        customActionKeyMap.put("w", ActionEnums.STD_ACTIONS.Move.toString());
        customActionKeyMap.put("q", ActionEnums.STD_ACTIONS.Turn_Anticlockwise.toString());
        customActionKeyMap.put("e", ActionEnums.STD_ACTIONS.Turn_Clockwise.toString());
        customActionKeyMap.put("a", ADDITIONAL_MOVE_ACTIONS.MOVE_LEFT.toString());
        customActionKeyMap.put("d", ADDITIONAL_MOVE_ACTIONS.MOVE_RIGHT.toString());
        customActionKeyMap.put("s", ADDITIONAL_MOVE_ACTIONS.MOVE_BACK.toString());
        customActionKeyMap.put("r", ADDITIONAL_MOVE_ACTIONS.CLUMSY_LEAP.toString());

        customActionKeyMap = new LinkedHashMap<>();
        //TODO support remapping ?
        customActionKeyMap.put("z", ActionEnums.DEFAULT_ACTION.Wait.toString());
        customActionKeyMap.put("x", ActionEnums.DEFAULT_ACTION.On_Alert.toString());
        customActionKeyMap.put("c", ActionEnums.DEFAULT_ACTION.Concentrate.toString());
        customActionKeyMap.put("v", ActionEnums.DEFAULT_ACTION.Examine.toString());
        customActionKeyMap.put("b", ActionEnums.DEFAULT_ACTION.Rest.toString());
        customActionKeyMap.put("n", ActionEnums.DEFAULT_ACTION.Defend.toString());

        //tab? for swap
    }

    // return boolean to know if success/failure and play a sound!
    @Override
    public void keyTyped(KeyEvent e) {
        LogMaster.log(LogMaster.GUI_DEBUG, "key typed: " + e.getKeyChar());
        if (mngr.getActiveObj().isAiControlled()) {
            return;
        }

        char CHAR = (e.getKeyChar());
        int keyMod = e.getModifiers();

        if (Flags.isIDE()) { // ???
            GuiEventManager.trigger(GuiEventType.KEY_TYPED, (int) CHAR);
        }
        handleKeyTyped(keyMod, CHAR);
    }


    public boolean handleKeyTyped(int keyMod, char CHAR) {
        if (OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.WASD_INDEPENDENT_FROM_FACING)) {
            CHAR = Accessibility.checkReplaceWasd(CHAR);
        }
        int index = -1;
        String charString = CHAR + "";
        boolean preview = false;
        if (Character.isUpperCase(CHAR)) {
            preview = true; //TODO interesting...
            charString = charString.toLowerCase();
        }
        if (numberChars.indexOf(CHAR) != -1) {
            index = Integer.parseInt(charString);
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


        }

        for (String s : customActionKeyMap.keySet()) {
            if (charString.equals(s)) {
                mngr.activateMyAction(customActionKeyMap.get(s));
                return true;
            }
        }
        return false;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        // SHIFT: toggle weapon?
        // ALT: highlight
        // CTRL: attack!
    }

    @Override
    public void keyReleased(KeyEvent e) {
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

}
