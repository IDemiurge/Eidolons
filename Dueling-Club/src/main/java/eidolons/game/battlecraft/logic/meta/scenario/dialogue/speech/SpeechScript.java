package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.text.Texts;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.important;

public class SpeechScript extends DataUnit<SpeechScript.SPEECH_ACTION> {
    public static final boolean TEST_MODE = true;
    public static final String SCRIPT_KEY = "script_key=";
    List<Pair<SPEECH_ACTION, String>> actions = new ArrayList<>();
    MetaGameMaster master;
    private String scriptText;
    private boolean executed;

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
        if (executed) {
            return;
        }
        //chain - confirm?
//        GuiEventManager.trigger(GuiEventType.DIALOGUE_SCRIPT_DONE)
//        createRunnableChain(

        //support user interrupt ?
//        WaitMaster.executeAfter();


        master.getDialogueManager().getSpeechExecutor().execute(this);

        executed = true;
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
        setValue(SPEECH_ACTION.valueOf(StringMaster.toEnumFormat(name)), value);
//        setValue(new EnumMaster<SPEECH_ACTION>().retrieveEnumConst(SPEECH_ACTION.class, name), value);
    }

    public enum SPEECH_ACTION {
        SOUND,
        MOMENT,
        MUSIC,

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
        DIALOG,

        WAIT, TIME, TIME_THIS,
        WAIT_EACH, WAIT_OFF,

        ZOOM,
        NEXT, NEXT_OFF, NEXT_ALL,
        SHAKE, WHITEOUT,
        REVEAL_AREA, PORTAL,

        //make templates mapped by name?
    }


    @Override
    public String toString() {

        return scriptText+" parsed \n" + actions;
    }
}
