package eidolons.libgdx.bf.boss.entity;

import eidolons.entity.handlers.bf.unit.UnitInitializer;
import eidolons.entity.obj.unit.Unit;
import main.entity.handlers.EntityMaster;

public class BossInitializer extends UnitInitializer {
    public BossInitializer(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }



    @Override
    public void initActives() {
       /**
        * no turn, move, or such... or?
        *
        * actions from the enum?
        */

       for (BossActionMaster.BOSS_ACTION_REAPER action : BossActionMaster.BOSS_ACTION_REAPER.values()) {

       }

   }

    @Override
    public void init() {
        super.init();

//        initRoundTriggers();

    //special event handler?


    }
}
