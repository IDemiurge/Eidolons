package main.entity.handlers.bf.unit;

import main.entity.active.DC_SpellObj;
import main.entity.obj.unit.Unit;
import main.entity.handlers.EntityHandler;
import main.entity.handlers.EntityMaster;
import main.game.battlecraft.rules.magic.ChannelingRule;
import main.game.core.ActionInput;
import main.game.logic.action.context.Context;

/**
 * Created by JustMe on 3/8/2017.
 */
public class UnitHandler extends EntityHandler<Unit> {
    private ActionInput channelingSpellData;

    public UnitHandler(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    public void initChannelingSpellData(DC_SpellObj spell) {
        Context context = new Context(spell.getOwnerObj(). getRef());
        if (ChannelingRule.isPreTargetingNeeded(spell)){
        spell.getTargeter().initTarget();
//        context.setTarget(target); // group?
        if (spell.getTargetGroup() != null)
            context.setGroup(spell.getTargetGroup());
        else
            context.setTarget(spell.getTargetObj().getId());
        }
        channelingSpellData = new ActionInput(spell, context);

    }

    public ActionInput getChannelingSpellData() {
        //try recheck and reselect target?
        return channelingSpellData;
    }

    public void clearChannelingData() {
        channelingSpellData = null;
    }
}
