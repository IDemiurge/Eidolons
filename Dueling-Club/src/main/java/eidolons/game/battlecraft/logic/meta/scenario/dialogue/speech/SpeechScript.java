package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class SpeechScript extends DataUnit<SpeechScript.SCRIPT> {
    public static final boolean TEST_MODE = false;
    public static final String SCRIPT_KEY = "script_key=";
    public boolean interrupted;
    List<Pair<SCRIPT, String>> actions = new ArrayList<>();
    MetaGameMaster master;
    private final String scriptText;
    private boolean executed;
    private boolean executing;

    public String getScriptText() {
        return scriptText;
    }

    public SpeechScript(String data, MetaGameMaster master) {
        super.setData(data);
        this.scriptText = data;
        this.master = master;
    }

    protected String getPairSeparator() {
        return DataUnitFactory.getPairSeparator(false);
    }

    protected String getSeparator() {
        return DataUnitFactory.getSeparator(getFormat());
    }

    @Override
    public Boolean getFormat() {
        return true;
    }

    @Override
    protected void handleMalformedData(String entry) {
        setValue(SCRIPT.SCRIPT, entry);

    }

    public boolean isExecuted() {
        return executed;
    }

    public void execute() {
        if (executing) {
            return;
        }
        if (executed) {
            return;
        }
        //chain - confirm?
//        GuiEventManager.trigger(GuiEventType.DIALOGUE_SCRIPT_DONE)
//        createRunnableChain(

        //support user interrupt ?
//        WaitMaster.executeAfter();

        executing = true;

        Chronos.mark("script " + scriptText);
        master.getDialogueManager().getSpeechExecutor().execute(this);
        Chronos.logTimeElapsedForMark("script " + scriptText);
        executed = true;
        executing = false;
    }

    @Override
    public Class<? extends SCRIPT> getEnumClazz() {
        return SCRIPT.class;
    }

    @Override
    public DataUnit<SCRIPT> setValue(SCRIPT name, String value) {
        actions.add(new ImmutablePair<>(name, value));
        values.put(name.toString(), value);
        return null;
    }

    @Override
    public void setValue(SCRIPT name, Object val) {
        setValue(name, val.toString());
    }

    @Override
    public DataUnit<SCRIPT> setValue(String name, String value) {
        SCRIPT c = getEnumConst(StringMaster.toEnumFormat(name.trim()));
        if (c == null) {
            main.system.auxiliary.log.LogMaster.dev("No script command for: " + name);
            return null;
        }
        setValue(c, value);
//        setValue(new EnumMaster<SPEECH_ACTION>().retrieveEnumConst(SPEECH_ACTION.class, name), value);
        return null;
    }

    public enum SCRIPT {
        //GAMEPLAY
        AWAKEN,
        RAISE,
        COLLAPSE, //(area keyword/
        AUTO_RAISE_ON,
        AUTO_RAISE_OFF,
        //AUDIO
        SOUND, MOMENT, MUSIC, PARALLEL_MUSIC, RANDOM_SOUND, SOUNDSCAPE, SOUND_VARIANT,
        LOOP_TRACK, STOP_LOOP, STOP_LOOP_NOW,

        //VISUALS
        ANIM, PORTRAIT_ANIM, BG_ANIM, CUSTOM_ANIM, UI_ANIM,
        SPRITE, FULLSCREEN, BLACKOUT, POSTFX, SHAKE, WHITEOUT, PARTICLES,

        REMOVE_GRID_OBJ, GRID_OBJ,
        COLOR, GRID_OBJ_ANIM, DISPLACE, SCREEN,
        PORTAL, PORTAL_CLOSE, PORTAL_OPEN,
        ATTACHED, LINKED_OBJ, AREA, VFX,

        CAMERA_OFFSET, ZOOM, CAMERA, CAMERA_SET, AUTOCAMERA,

        //TEXT
        TIP, COMMENT, DIALOGUE, COMMENT_CENTERED,
        QUEST, QUEST_DONE, QUEST_ADD,

        //FLOW AND AUXILIARY
        WAIT, TIME, TIME_THIS, WAIT_EACH, WAIT_OFF,
        NEXT, NEXT_OFF, NEXT_ALL,
        SCRIPT_PARALLEL, SCRIPT_IF, SCRIPT_CHANCE, TUT_SCRIPT, LAST_TUTORIAL,
        CONFIRM, SWITCH, ALL, WAIT_ANIMS, CONTINUE_IF, WAIT_PASS, BREAK_IF,
        GLOBAL_CONTINUE_IF, NO_SKIP, SKIP, CHECK_SKIP, PASS, WAIT_FOR_NO_COMMENTS,
        SPEED, OFFSET, ABS, END, DIALOGUE_AFTER,

        WAIT_INPUT, VAR_MAP, ACTION_MAP, CINEMATICS,
        DEBUG, CHEAT, DEV,VAR, GLOBAL,COORDINATE,
        VAR_FLOAT, VAR_INTEGER, WAIT_FOR,

        HIGHLIGHT_ACTION, BLOCK_ACTION,
        ADD_SPELL, REMOVE_SPELL,
        FREEZE, UNFREEZE,

        //LOGIC
        ACTION, UNIT, SCRIPT,
        CONCEAL, REVEAL, REVEAL_AREA,
        VISION, RESET_VISION,
         MOVE, TURN,
        TRIGGER, TRIGGER_REMOVE,

        //AI
         TURN_AUTO, ORDER,

        //OBJS
        BF_OBJ, ADD, PARAM, PROP, FILL,
        NAMED_COORDINATES_ADD, CLEAR, REPLACE,
        ADD_PARAM, BUFF_REMOVE, BUFF_ADD,
        END_ROUND, RESET,

        //SYSTEM
        PRELOAD_MUSIC, UNLOAD_SCOPE, LOAD_SCOPE, AMBI_VFX,
        VIDEO,
        OPTION_SOUND, OPTION_GRAPHICS, OPTION_GAMEPLAY, OPTION_ANIM,
        //make templates mapped by name?
    }


    @Override
    public String toString() {

        return scriptText + " parsed \n" + actions;
    }
}
