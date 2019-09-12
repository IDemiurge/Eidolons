package main.entity;

import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;

import javax.swing.*;

/**
 * Created by JustMe on 5/17/2017.
 * Supposed to be used as 'singleton' one per ObjType
 */
public class LightweightEntity extends DataModel {

    protected ImageIcon customIcon;
    protected ImageIcon icon;

    public LightweightEntity(ObjType type) {
        this.type = type;
        type.checkBuild();
        setProperty(G_PROPS.NAME, type.getName());
        setPropMap(type.getPropMap());
        setParamMap(type.getParamMap());
    }

    @Override
    public String getDoubleParam(PARAMETER param) {
        return super.getDoubleParam(param, true);
    }

    @Override
    public String getProperty(PROPERTY prop) {
        return super.getProperty(prop, true);
    }
}
