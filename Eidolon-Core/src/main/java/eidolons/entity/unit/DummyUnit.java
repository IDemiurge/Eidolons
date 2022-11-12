package eidolons.entity.unit;

import main.entity.Entity;
import main.entity.handlers.EntityMaster;

public abstract class DummyUnit extends Entity implements GridEntity {

    @Override
    public void init() {

    }

    @Override
    protected EntityMaster initMaster() {
        return null;
    }
}
