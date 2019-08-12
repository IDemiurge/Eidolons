package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.text.Texts;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.important;

public class SpeechScript extends DataUnit<SpeechScript.SPEECH_ACTION> {

    public static final boolean TEST_MODE = true;
    public static final String TEST_DATA =
            "time=2000. [[camera_set=orig;BG_ANIM=out;camera=me;wait=3000;script=action(source, Deathstorm)]]";
    private   String scriptText;
    List<Pair<SPEECH_ACTION, String>> actions = new ArrayList<>();
    private boolean executed;

    public enum SCRIPT_WRAPPER {
        chain,
        time_chain,
    }

    public String getScriptText() {
        return scriptText;
    }

    //TODO IDEA - use script syntax for conditions?
    public enum SPEECH_ACTION {
        SOUND,
        MOMENT,
        MUSIC,

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

        GRID_OBJ, ANIM,

        CAMERA,
        CAMERA_SET,

        COMMENT,
        DIALOG,

        WAIT,TIME, TIME_THIS,
        WAIT_EACH, WAIT_OFF,
        ZOOM, REVEAL_AREA, PORTAL,

        //make templates mapped by name?
    }

    MetaGameMaster master;

    public SpeechScript(String data, MetaGameMaster master) {
        data = processData(data);
        if (StringMaster.isEmpty(data)) {
            return;
        }
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

    private String processData(String data) {
        if (data.contains("script_key=")) {
            String key = data.split("script_key=")[1];
            data = Texts.getTextMap("scripts").get(key);
        }
//        while (data.contains("(")) {
//            String wrapper = VariableManager.removeVarPart(data);
//            String inner = VariableManager.getVarPart(data);
//            SCRIPT_WRAPPER wrap = new EnumMaster<SCRIPT_WRAPPER>().retrieveEnumConst(SCRIPT_WRAPPER.class, wrapper);
////            wrappers.add(wrap);
//            data = inner;
//        }

        return data;
    }

    @Override
    public Boolean getFormat() {
        return true;
    }

    private void executeAction(SPEECH_ACTION speechAction, String value) {
        important("Executing action: " +speechAction + " = "+ value);
        master.getDialogueManager().getSpeechExecutor().execute(speechAction, value);
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

        important("Executing script: " +scriptText + " parsed \n"+ actions);
        for (Pair<SPEECH_ACTION, String> pair : actions) {
            executeAction(pair.getKey(), pair.getValue());
        }
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

//TODO chain
// if (tips.length <= 1)
//            return () -> {
//        if (tips[0].once){
//            tips[0].done = true;
//        }
//        tips[0].run();
//    };
//    TipMessageMaster.TIP_MESSAGE[] tipsChopped =
//            Arrays.stream(tips).skip(1).collect(Collectors.toList()).toArray(new TipMessageMaster.TIP_MESSAGE[tips.length - 1]);
//        return () -> tip(tipsChopped);


}
