package main.entity.handlers;

import main.entity.Entity;

/**
 * Created by JustMe on 2/15/2017.
 */
public class EntityCalculator<E extends Entity> extends EntityHandler<E> {

    public EntityCalculator(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);
    }
}
