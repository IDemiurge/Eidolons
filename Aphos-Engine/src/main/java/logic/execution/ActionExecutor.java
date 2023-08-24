package logic.execution;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.EntityRef;
import elements.exec.Executable;
import elements.exec.effect.Effect;
import elements.exec.targeting.TargetGroup;
import elements.exec.targeting.Targeting;
import framework.client.UserEventHandler;
import framework.entity.field.FieldEntity;
import framework.entity.sub.UnitAction;
import logic.execution.event.user.UserEventType;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by Alexander on 8/21/2023
 */
public class ActionExecutor extends BattleHandler {

    public ActionExecutor(BattleManager manager) {
        super(manager);
        UserEventHandler.bind(UserEventType.activate_action, p ->
                activate(manager.getEntities().getEntityById(p.getInt("action"), UnitAction.class)));
    }

    public void activate(UnitAction action) {
        EntityRef ref = new EntityRef(action.getUnit()).set("action", action);
        for (Pair<Targeting, Effect> pair : action.getExecutable().getTargetedEffects()) {
            Targeting targeting = pair.getLeft();
            Effect effect = pair.getRight();

            EntityRef REF = ref.copy();
            targeting.select(REF);
            applyEffects(effect, REF);
            manager.resetAll();
        }
        //triggers visual effect and waits for input
        //pack all the cost/boost/targeting/fx into 'executable'?

        // action.getCost().pay();
        //set each action to disabled after a toBase() if can't pay; are there any fringe cases?
    }

    private boolean applyEffects(Effect effect, EntityRef ref) {
        // EntityRef ref = new EntityRef(action.getUnit());
        TargetGroup targets = ref.getGroup();
        if (targets == null) {
            effect.apply(ref);
            //some effects don't need us to inflate the group?
        } else
        // apply()
        for (FieldEntity target : targets.getTargets()) {
            EntityRef REF = ref.copy();
            REF.setTarget(target);
            effect.apply(REF);
            // boolean result = action.getExecutable().execute(REF);
            // if (REF.isValueBool()) {
            //     break; //interrupted
            // }
        }

        //state.toBase();
        return true; //meaning? premature turn end?
    }
}
