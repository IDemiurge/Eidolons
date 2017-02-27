package main.entity.tools.active.spell;

import main.entity.active.DC_SpellObj;
import main.entity.tools.active.ActiveMaster;
import main.entity.tools.active.Executor;

/**
 * Created by JustMe on 2/23/2017.
 */
public class SpellActiveMaster extends ActiveMaster{
    public SpellActiveMaster(DC_SpellObj entity) {
        super(entity);
    }
    /*

     */

    @Override
    protected Executor createHandler() {
        return new Executor(getEntity(), this){

        } ;
    }
}
