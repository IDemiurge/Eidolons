package eidolons.entity.handlers.bf.unit;

import eidolons.entity.feat.active.Spell;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.core.ActionInput;
import main.entity.handlers.EntityHandler;
import main.entity.handlers.EntityMaster;
import main.game.logic.action.context.Context;

/**
 * Created by JustMe on 3/8/2017.
 */
public class UnitHandler extends EntityHandler<Unit> {
    private ActionInput channelingSpellData;

    public UnitHandler(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    public void initChannelingSpellData(Spell spell) {
        Context context = new Context(spell.getOwnerUnit().getRef());
        if (ChannelingRule.isPreTargetingNeeded(spell)) {
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
