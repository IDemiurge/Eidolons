package boss.logic.entity;

import eidolons.entity.handlers.bf.unit.UnitInitializer;
import eidolons.entity.unit.Unit;
import main.entity.handlers.EntityMaster;

public class BossInitializer extends UnitInitializer {
    public BossInitializer(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }


    @Override
    public void initActives() {
        super.initActives();

    }

    @Override
    public void init() {
        super.init();

        //        initRoundTriggers();

        //special event handler?


    }
}
