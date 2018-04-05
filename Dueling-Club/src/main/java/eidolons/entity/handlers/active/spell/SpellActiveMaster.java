package eidolons.entity.handlers.active.spell;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.handlers.active.ActiveMaster;
import eidolons.entity.handlers.active.Executor;
import main.entity.handlers.EntityChecker;
import main.entity.handlers.EntityInitializer;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/23/2017.
 */
public class SpellActiveMaster extends ActiveMaster {
    public SpellActiveMaster(DC_SpellObj entity) {
        super(entity);
    }
    /*

     */

    @Override
    public DC_SpellObj getEntity() {
        return (DC_SpellObj) super.getEntity();
    }

    @Override
    protected EntityChecker<DC_ActiveObj> createEntityChecker() {
        return new SpellChecker(getEntity(), this);
    }

    @Override
    protected EntityInitializer<DC_ActiveObj> createInitializer() {
        return new SpellInitializer(getEntity(), this);
    }

    @Override
    protected Executor createHandler() {
        if (CoreEngine.isArcaneVault())
            return null;
        return new SpellExecutor(getEntity(), this);
    }
}
