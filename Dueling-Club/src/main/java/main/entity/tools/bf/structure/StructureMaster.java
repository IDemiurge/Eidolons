package main.entity.tools.bf.structure;

import main.entity.obj.Structure;
import main.entity.obj.Structure;
import main.entity.tools.*;

/**
 * Created by JustMe on 3/5/2017.
 */
public class StructureMaster extends EntityMaster<Structure>{


    public StructureMaster(Structure entity) {
        super(entity);
    }

    @Override
    protected EntityAnimator<Structure> createEntityAnimator() {
        return null;
    }

    @Override
    protected EntityLogger<Structure> createEntityLogger() {
        return null;
    }

    @Override
    protected EntityInitializer<Structure> createInitializer() {
        return new StructureInitializer(getEntity(), this);
    }

    @Override
    protected EntityChecker<Structure> createEntityChecker() {
        return null;
    }

    @Override
    protected EntityResetter<Structure> createResetter() {
        return new StructureResetter(getEntity(), this);
    }

    @Override
    protected EntityCalculator<Structure> createCalculator() {
        return null;
    }

    @Override
    protected EntityHandler<Structure> createHandler() {
        return null;
    }
}
