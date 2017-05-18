package main.entity;

import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/17/2017.
 */
public class LightweightEntity extends DataModel {

    public LightweightEntity(ObjType type) {
        this.type = type;
        setProperty(G_PROPS.NAME, type.getName());
        propMap = type.getPropMap();
        paramMap =type.getParamMap();
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
