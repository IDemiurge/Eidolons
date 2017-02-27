package main.entity.tools.unit;

import main.entity.obj.unit.Unit;
import main.entity.tools.EntityChecker;
import main.entity.tools.EntityMaster;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitChecker extends EntityChecker<Unit> {
    public UnitChecker(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }
}
