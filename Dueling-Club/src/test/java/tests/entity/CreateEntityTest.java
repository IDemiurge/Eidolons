package tests.entity;

import main.entity.Entity;

import init.JUnitDcInitializer;

/**
 * Created by JustMe on 3/6/2017.
 */
public abstract class CreateEntityTest<T extends Entity> extends tests.entity.EntityTest<T> {
    public CreateEntityTest(JUnitDcInitializer initializer) {
        super(initializer);
    }

    @Override
    public void setUp() {
       entity= createEntity();
    }

    protected abstract T createEntity();
}
