package eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink;

import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.VariablesState;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.InkEnums.INK_GLOBAL_VAR;

/**
 * Created by JustMe on 11/20/2018.
 */
public class InkVarMaster {

    public void initGlobalVars(Story story, InkContext context){
        VariablesState state = story.getVariablesState();
        for (INK_GLOBAL_VAR var : InkEnums.INK_GLOBAL_VAR.values()) {
            Object value = getGlobalVarValue(var);
            String name = var.name().toLowerCase();
            try {
                state.set(name, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //        story.getVariablesState().assign(name, value);
    }

    private Object getGlobalVarValue(INK_GLOBAL_VAR var) {
        //branch that git
        switch (var) {

            case TIME_OF_DAY:
                break;
            case TOWN_NAME:
                break;
            case REGION_NAME:
                break;
            case PC_:
                break;
        }
        return "no value";
    }
}
