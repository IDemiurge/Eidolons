package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.data.ability.construct.VariableManager;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

public class SpeechScript extends DataUnit<SpeechScript.SPEECH_ACTION> {

    public static final boolean TEST_MODE = true;
    private static final String TEST_DATA = "script=action(hollowing sleeper, move)";

    /**
     * everything that must happen on a single line
     * syntax:
     * [[sound=blast;portrait anim =evil]]
     */

    public enum SCRIPT_WRAPPER {
        chain,
        time_chain,
    }

    public enum SPEECH_ACTION {
        SOUND,
        PORTRAIT_ANIM, // do something with it!
        BG_ANIM,
        CUSTOM_ANIM,

        UNIT_ACTION,
        SCRIPT,
        timed, MUSIC,

        //make templates mapped by name?
    }

    MetaGameMaster master;

    public SpeechScript(String data, MetaGameMaster master) {
        data = processData(data);
        super.setData(data);
        this.master = master;
    }


    private String processData(String data) {
        if (TEST_MODE) {
            data = TEST_DATA;
        }
        while (data.contains("(")) {
            String wrapper = VariableManager.removeVarPart(data);
            String inner = VariableManager.getVarPart(data);
            SCRIPT_WRAPPER wrap = new EnumMaster<SCRIPT_WRAPPER>().retrieveEnumConst(SCRIPT_WRAPPER.class, wrapper);
//            wrappers.add(wrap);
            data = inner;
        }

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
        //chain - confirm?
//        GuiEventManager.trigger(GuiEventType.DIALOGUE_SCRIPT_DONE)
//        createRunnableChain(

        //support user interrupt ?
//        WaitMaster.executeAfter();

        for (String s : getValues().keySet()) {
            executeAction(getEnumConst(s), getValue(s));


        }


    }

    //if (tips.length <= 1)
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
