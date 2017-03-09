package main.entity.tools.bf.unit;

import main.entity.obj.unit.Unit;
import main.entity.tools.EntityHandler;
import main.entity.tools.EntityMaster;

/**
 * Created by JustMe on 3/8/2017.
 */
public class UnitHandler extends EntityHandler<Unit> {
    public UnitHandler(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }
}
