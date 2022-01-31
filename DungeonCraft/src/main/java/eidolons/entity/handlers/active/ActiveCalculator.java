package eidolons.entity.handlers.active;

import eidolons.entity.active.ActiveObj;
import main.entity.handlers.EntityCalculator;
import main.entity.handlers.EntityMaster;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveCalculator extends EntityCalculator<ActiveObj> {

    public ActiveCalculator(ActiveObj entity, EntityMaster<ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

}
