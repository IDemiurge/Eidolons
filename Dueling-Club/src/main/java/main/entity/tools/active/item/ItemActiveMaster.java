package main.entity.tools.active.item;

import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_QuickItemAction;
import main.entity.tools.EntityResetter;
import main.entity.tools.active.ActiveMaster;
import main.entity.tools.active.ActiveResetter;

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
