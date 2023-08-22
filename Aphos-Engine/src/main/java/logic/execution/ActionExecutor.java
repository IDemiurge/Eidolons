package logic.execution;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.EntityRef;
import elements.exec.targeting.TargetGroup;
import framework.client.UserEventHandler;
import framework.entity.field.FieldEntity;
import framework.entity.sub.UnitAction;
import logic.execution.event.user.UserEventType;

/**
 * Created by Alexander on 8/21/2023
 */
public class ActionExecutor extends BattleHandler {


    public ActionExecutor(BattleManager manager) {
        super(manager);
        UserEventHandler.bind(UserEventType.activate_action, p ->
                activate(manager.getData().getEntityById(p.getInt("action"), UnitAction.class)));
    }

    public void activate(UnitAction action){
        //TODO
        TargetGroup group = action.getExecutable().selectTargets(); //triggers visual effect and waits for input
        //pack all the cost/boost/targeting/fx into 'executable'?
        actionApplies(action, group);
        manager.resetAll();
        // action.getCost().pay();
        //set each action to disabled after a toBase() if can't pay; are there any fringe cases?
    }

    public boolean actionApplies(UnitAction action, TargetGroup targets) {
        EntityRef ref = new EntityRef(action.getUnit());
        ref.set("action", action);

        for (FieldEntity target : targets.getTargets()) {
            EntityRef REF = ref.copy();
            REF.set("target", target);
            boolean result = action.getExecutable().execute(REF);
            if (REF.isValueBool()){
                break; //interrupted
            }
        }

        //state.toBase();
        return true; //meaning? premature turn end?
    }
}
