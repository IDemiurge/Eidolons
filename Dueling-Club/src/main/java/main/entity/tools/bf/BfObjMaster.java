package main.entity.tools.bf;

import main.entity.obj.BattleFieldObject;
import main.entity.tools.*;

/**
 * Created by JustMe on 3/5/2017.
 */
public class BfObjMaster extends EntityMaster<BattleFieldObject>{


    public BfObjMaster(BattleFieldObject entity) {
        super(entity);
    }

    @Override
    protected EntityAnimator<BattleFieldObject> createEntityAnimator() {
        return null;
    }

    @Override
    protected EntityLogger<BattleFieldObject> createEntityLogger() {
        return null;
    }

    @Override
    protected EntityInitializer<BattleFieldObject> createInitializer() {
        return null;
    }

    @Override
    protected EntityChecker<BattleFieldObject> createEntityChecker() {
        return null;
    }

    @Override
    protected EntityResetter<BattleFieldObject> createResetter() {
        return new BfObjResetter(getEntity(), this);
    }

    @Override
    protected EntityCalculator<BattleFieldObject> createCalculator() {
        return null;
    }

    @Override
    protected EntityHandler<BattleFieldObject> createHandler() {
        return null;
    }
}
