package java.tests.entity;

import main.entity.Entity;

import java.init.JUnitDcInitializer;
import java.tests.JUnitTest;

/**
 * Created by JustMe on 3/6/2017.
 */
public abstract class EntityTest<T extends Entity> extends JUnitTest {

    protected T entity;

    public EntityTest(JUnitDcInitializer initializer) {
        super(initializer);
    }


}
