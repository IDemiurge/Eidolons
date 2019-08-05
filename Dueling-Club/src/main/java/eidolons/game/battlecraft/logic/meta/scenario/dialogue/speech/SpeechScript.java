package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class SpeechScript extends DataUnit<SpeechScript.SPEECH_ACTION> {

    public static final boolean TEST_MODE = false;
    public static final String TEST_DATA =
            "time=2000. [[camera_set=orig;BG_ANIM=out;camera=me;wait=3000;script=action(source, Deathstorm)]]";
    List<Pair<SPEECH_ACTION, String>> actions = new ArrayList<>();
    private boolean executed;

    public enum SCRIPT_WRAPPER {
        chain,
        time_chain,
    }

    public enum SPEECH_ACTION {
        SOUND,
        PORTRAIT_ANIM, // do something with it!
        BG_ANIM,
        CUSTOM_ANIM,

        ACTION,
        SCRIPT,
        timed, MUSIC, CAMERA, CAMERA_SET, WAIT, UI_ANIM, COMMENT,

        //make templates mapped by name?
    }

    MetaGameMaster master;

    public SpeechScript(String data, MetaGameMaster master) {
        data = processData(data);
        super.setData(data);
        this.master = master;
    }

    protected String getPairSeparator() {
        return DataUnitFactory.getPairSeparator(false);
    }
    protected String getSeparator() {
        return DataUnitFactory.getSeparator(getFormat());
    }
    private String processData(String data) {
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
        master.getDialogueManager().getSpeechExecutor().execute(speechAction, value);
    }

    public void execute() {
        if (executed){
            return;
        }
        //chain - confirm?
//        GuiEventManager.trigger(GuiEventType.DIALOGUE_SCRIPT_DONE)
//        createRunnableChain(

        //support user interrupt ?
//        WaitMaster.executeAfter();
        for (Pair<SPEECH_ACTION, String> pair : actions) {
            executeAction(pair.getKey(), pair.getValue());
        }
        executed=true;
    }

    @Override
    public Class<? extends SPEECH_ACTION> getEnumClazz() {
        return SPEECH_ACTION.class;
    }

    @Override
    public void setValue(SPEECH_ACTION name, String value) {
        actions.add(new ImmutablePair<>(name, value));
    }

    @Override
    public void setValue(SPEECH_ACTION name, Object val) {
        setValue(name, val.toString());
    }
    @Override
    public void setValue(String name, String value) {
        setValue(new EnumMaster<SPEECH_ACTION>().retrieveEnumConst(SPEECH_ACTION.class, name), value);
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
