package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.text.Texts;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.important;

public class SpeechScript extends DataUnit<SpeechScript.SPEECH_ACTION> {
    public static final boolean TEST_MODE = false;
    public static final String SCRIPT_KEY = "script_key=";
    List<Pair<SPEECH_ACTION, String>> actions = new ArrayList<>();
    MetaGameMaster master;
    private String scriptText;
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
    public Class<? extends SPEECH_ACTION> getEnumClazz() {
        return SPEECH_ACTION.class;
    }

    @Override
    public void setValue(SPEECH_ACTION name, String value) {
        actions.add(new ImmutablePair<>(name, value));
        values.put(name.toString(), value);
    }

    @Override
    public void setValue(SPEECH_ACTION name, Object val) {
        setValue(name, val.toString());
    }

    @Override
    public void setValue(String name, String value) {
        setValue(SPEECH_ACTION.valueOf(StringMaster.toEnumFormat(name.trim())), value);
//        setValue(new EnumMaster<SPEECH_ACTION>().retrieveEnumConst(SPEECH_ACTION.class, name), value);
    }

    public enum SPEECH_ACTION {
        SOUND,
        MOMENT,
        MUSIC,
        PARALLEL_MUSIC,

        ANIM,
        PORTRAIT_ANIM, // do something with it!
        BG_ANIM,
        CUSTOM_ANIM,
        UI_ANIM,

        SPRITE,
        FULLSCREEN,
        BLACKOUT,
        POSTFX,

        ACTION,
        UNIT,
        SCRIPT,

        REMOVE_GRID_OBJ,
        GRID_OBJ,


        CAMERA,
        CAMERA_SET,

        COMMENT,
        DIALOGUE,

        WAIT, TIME, TIME_THIS,
        WAIT_EACH, WAIT_OFF,

        ZOOM,
        NEXT, NEXT_OFF, NEXT_ALL,
        SHAKE, WHITEOUT,
        REVEAL_AREA, PORTAL, AUTOCAMERA, PARTICLES, SOUNDSCAPE, OPTION_SOUND, OPTION_GRAPHICS, OPTION_GAMEPLAY, OPTION_ANIM, LOOP_TRACK, STOP_LOOP, SPEED, GLOBAL, ABS, CAMERA_OFFSET, DIALOGUE_AFTER, COORDINATE, MOVE, TURN, VAR, SOUND_VARIANT, DISPLACE, SCREEN, VAR_FLOAT, VAR_INTEGER, WAIT_FOR, COLOR, GRID_OBJ_ANIM, PORTAL_CLOSE, PORTAL_OPEN, ATTACHED, TURN_AUTO, ADD_SPELL, REMOVE_SPELL, WAIT_INPUT, VAR_MAP, ACTION_MAP, BF_OBJ, ADD, PARAM, PROP, BLOCK_ACTION, ORDER, LINKED_OBJ, DEBUG, CHEAT, DEV, OFFSET, TRIGGER, AREA, VFX, CINEMATICS, SCRIPT_PARALLEL, FILL, NAMED_COORDINATES_ADD, CLEAR, VIDEO, REPLACE, TIP, BUFF_REMOVE, BUFF_ADD, TRIGGER_REMOVE, SCRIPT_IF, SCRIPT_CHANCE, TUT_SCRIPT, ALL, WAIT_ANIMS,

        //make templates mapped by name?
    }


    @Override
    public String toString() {

        return scriptText + " parsed \n" + actions;
    }
}
