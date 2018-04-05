package eidolons.entity.handlers.active.spell;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.handlers.active.Activator;
import eidolons.entity.handlers.active.ActiveMaster;

/**
 * Created by JustMe on 2/26/2017.
 */
public class SpellActivator extends Activator {

    public SpellActivator(DC_ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    public String getStatusString() {
        return (getCanActivate()) ? "Activate " : "" + getAction().getCosts().getReasonsString() + " to activate ";
    }
}
