package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;

public class NF_PriorityManager {

    private AiPriorityCalc calc;

    public AiPriorityCalc calc(ActionSequence sequence) {
        UnitAI ai = sequence.getAi();
        AiCalcData data= createData(ai, sequence);
        calc = new AiPriorityCalc(sequence, ai, data);

        /*
        base value - via custom function or some such
        mods and terms should add up even as we calc with that base func!!

        Simplified form -

        >> Std funcs to chain up in the base func!! Functional programming...


        Then:
        >> Moves/turns required
        >> RPG factors (character, rationality,
        >>
        Global



         */
        sequence.getType();

        return calc;
    }

    private AiCalcData createData(UnitAI ai, ActionSequence sequence) {
        AiCalcData data=null ;
        return data ;
    }

}
