package main.entity.tools;

import main.entity.obj.Obj;

/**
 * Created by JustMe on 2/27/2017.
 */
public class ObjMaster extends EntityMaster<Obj> {
    public ObjMaster(Obj entity) {
        super(entity);
    }

    @Override
    protected EntityAnimator<Obj> createEntityAnimator() {
        return null;
    }

    @Override
    protected EntityLogger<Obj> createEntityLogger() {
        return null;
    }

    @Override
    protected EntityInitializer<Obj> createInitializer() {
        return new EntityInitializer(getEntity(), this);
    }

    @Override
    protected EntityChecker<Obj> createEntityChecker() {
        return null;
    }

    @Override
    protected EntityResetter<Obj> createResetter() {
        return new EntityResetter<>(getEntity(), this);
    }

    @Override
    protected EntityCalculator<Obj> createCalculator() {
        return null;
    }

    @Override
    protected EntityHandler<Obj> createHandler() {
        return null;
    }
}
