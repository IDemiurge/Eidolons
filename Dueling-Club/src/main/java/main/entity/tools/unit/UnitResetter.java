package main.entity.tools.unit;

import main.entity.obj.unit.Unit;
import main.entity.tools.EntityMaster;
import main.entity.tools.EntityResetter;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitResetter extends EntityResetter<Unit> {
    public UnitResetter(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }
}
