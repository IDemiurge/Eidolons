package main.entity.handlers;

import main.entity.Entity;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

/**
 * Created by JustMe on 2/23/2017.
 */
public class EntityLogger<E extends Entity> extends EntityHandler<E> {

    public EntityLogger(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);

    }

    public ENTRY_TYPE log() {
        return null;
    }
}
