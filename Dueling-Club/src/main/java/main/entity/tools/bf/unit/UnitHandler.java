package main.entity.tools.bf.unit;

import main.entity.active.DC_SpellObj;
import main.entity.obj.unit.Unit;
import main.entity.tools.EntityHandler;
import main.entity.tools.EntityMaster;
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

    public void initChannelingSpellData(DC_SpellObj spell){
        Context context = new Context(getRef());
//        context.setTarget(target); // group?
        channelingSpellData = new ActionInput(spell, context);

    }
        public ActionInput getChannelingSpellData(){
        return channelingSpellData;
    }
}
