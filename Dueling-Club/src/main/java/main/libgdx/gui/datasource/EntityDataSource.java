package main.libgdx.gui.datasource;

import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;

/**
 * Created by JustMe on 3/29/2018.
 */
public class EntityDataSource<T extends Entity> {
    protected T entity;

    public EntityDataSource(T entity) {
        this.entity = entity;
    }

    public String getDescription() {
        return entity.getDescription();
    }

    public String getParam(String p) {
        return entity.getParam(p);
    }

    public String getParam(PARAMETER param) {
        return entity.getParam(param);
    }

    public Double getParamDouble(PARAMETER param) {
        return entity.getParamDouble(param);
    }

    public String getProperty(String prop) {
        return entity.getProperty(prop);
    }

    public String getProperty(PROPERTY prop) {
        return entity.getProperty(prop);
    }

    public String getImagePath() {
        return entity.getImagePath();
    }

    public String getDisplayedName() {
        return entity.getDisplayedName();
    }

    public String getNameIfKnown() {
        return entity.getNameIfKnown();
    }

    public String getName() {
        return entity.getName();
    }

    public T getEntity() {
        return entity;
    }
}
