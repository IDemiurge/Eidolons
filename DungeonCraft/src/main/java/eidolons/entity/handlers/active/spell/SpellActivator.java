package eidolons.entity.handlers.active.spell;

import eidolons.entity.active.ActiveObj;
import eidolons.entity.handlers.active.Activator;
import eidolons.entity.handlers.active.ActiveMaster;
import main.entity.Ref;

/**
 * Created by JustMe on 2/26/2017.
 */
public class SpellActivator extends Activator {

    public SpellActivator(ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public boolean canBeActivated(Ref ref, boolean first) {
        return super.canBeActivated(ref, first);
    }

    public String getStatusString() {
        return (getCanActivate()) ? "Activate " : "" + getAction().getCosts().getReasonsString() + " to activate ";
    }
}
