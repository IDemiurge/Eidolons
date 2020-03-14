package eidolons.game.netherflame.boss_.logic.entity;

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


   }

    @Override
    public void init() {
        super.init();

//        initRoundTriggers();

    //special event handler?


    }
}
