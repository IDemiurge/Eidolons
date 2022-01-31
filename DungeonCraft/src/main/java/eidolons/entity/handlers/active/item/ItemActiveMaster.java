package eidolons.entity.handlers.active.item;

import eidolons.entity.active.ActiveObj;
import eidolons.entity.active.QuickItemAction;
import eidolons.entity.handlers.active.ActiveMaster;
import eidolons.entity.handlers.active.ActiveResetter;
import main.entity.handlers.EntityResetter;

/**
 * Created by JustMe on 2/26/2017.
 */
public class ItemActiveMaster extends ActiveMaster {
    public ItemActiveMaster(QuickItemAction entity) {
        super(entity);
    }

    @Override
    public QuickItemAction getEntity() {
        return (QuickItemAction) super.getEntity();
    }

    @Override
    protected EntityResetter<ActiveObj> createResetter() {
        return new ActiveResetter(getEntity(), this) {
            @Override
            protected void applyPenalties() {
            }
        };
    }


}
