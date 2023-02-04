package logic.v2.ai;

import logic.v2.ai.generic.action.AiAction;
import logic.v2.ai.generic.AiConsts;
import logic.v2.ai.generic.AiHandler;
import logic.v2.ai.generic.AiMaster;
import logic.v2.ai.ipc.AiStrategy;
import logic.v2.ai.single.SingleAi;

/**
 * Created by Alexander on 1/21/2023
 * In the new model:
 * - Assign Plan Sequence concurrently
 * - Retain impulse until new one is created (maybe be Sequence or SequenceProvider)
 * -
 * Actions must be defined as Template and as Concrete (with target, history, status, ..)
 *
 * So the pipeline of decision:
 * 1) see if there is an overpowering Impulse vs Discipline
 * 2) see if there is a command* vs Pride
 * Having Plan may increase Pride?..
 * 3) see if there is a pre-defined plan
 * 4) else run these with -force in 1) 3) 2)
 * From Template to concrete Action
 * > define specific in vars
 * >
 *
 * How is it shaping up so far? Will it fit Heroes too?
 */
public class AiDecision extends AiHandler {

    public AiDecision(AiMaster master) {
        super(master);
    }

    // Just atomic action upon ATB turn
    public AiAction chooseAction(SingleAi ai){
        AiConsts.AiMode ipc =master.getIpcHandler().getIpcMode(ai);
       AiStrategy strategy = null;
        switch (ipc){
            case Impulse -> {
                strategy =  ai.getImpulseStrategy();
            }
            case Plan -> {
                strategy =  ai.getPlanStrategy();
            }
            case Command -> {
                strategy =  ai.getCommandStrategy();
            }
        }
        return         strategy.getAction()                ;
    }


}
