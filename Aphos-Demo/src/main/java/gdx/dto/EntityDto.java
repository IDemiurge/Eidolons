package gdx.dto;

import logic.entity.Entity;

public class EntityDto<T extends Entity> implements DtoManager.Dto {
    T entity;

    public EntityDto(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
}
