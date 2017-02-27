package main.entity.tools.unit;

import main.entity.obj.unit.Unit;
import main.entity.tools.EntityCalculator;
import main.entity.tools.EntityMaster;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitCalculator  extends EntityCalculator<Unit> {
    public UnitCalculator(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }
}
