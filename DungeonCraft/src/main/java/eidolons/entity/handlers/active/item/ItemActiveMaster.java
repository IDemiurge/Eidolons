package eidolons.entity.handlers.active.item;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.handlers.active.ActiveMaster;
import eidolons.entity.handlers.active.ActiveResetter;
import main.entity.handlers.EntityResetter;

/**
 * Created by JustMe on 2/26/2017.
 */
public class ItemActiveMaster extends ActiveMaster {
    public ItemActiveMaster(DC_QuickItemAction entity) {
        super(entity);
    }

    @Override
    public DC_QuickItemAction getEntity() {
        return (DC_QuickItemAction) super.getEntity();
    }

    @Override
    protected EntityResetter<DC_ActiveObj> createResetter() {
        return new ActiveResetter(getEntity(), this) {
            @Override
            protected void applyPenalties() {
            }
        };
    }


}
