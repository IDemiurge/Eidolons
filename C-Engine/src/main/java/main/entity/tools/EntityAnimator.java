package main.entity.tools;

import main.entity.Entity;

/**
 * Created by JustMe on 2/23/2017.
 */
public class EntityAnimator<E extends Entity> extends EntityHandler<E>{
    public EntityAnimator(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);
    }



}
