package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 10/20/2018.
 *
 * For leader
 *
 */
public abstract class CyclicBehavior extends AiBehavior{

    DC_Obj[] cycledArgs;
    int step;
    public CyclicBehavior(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected DC_Obj updateTarget() {
if (cycledArgs==null || isArgUpdateRequired()){
    cycledArgs = new DC_Obj[getCycledStepsNumber()];
    for (int i = 0; i < getCycledStepsNumber(); i++) {
        cycledArgs[i]= createCycledArg(i, cycledArgs);
    }
    step =0;
}
        return cycledArgs[step++];
    }

    protected boolean isArgUpdateRequired() {
        return false;
    }

    protected abstract DC_Obj createCycledArg(int i, DC_Obj[] cycledArgs);

    protected abstract int getCycledStepsNumber();

    //    public DC_Obj getAltArg(DC_Obj obj){
//
//    }
//    public boolean isArgValid(DC_Obj obj){
//
//    }
}
