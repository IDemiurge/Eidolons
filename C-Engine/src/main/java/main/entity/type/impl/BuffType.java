package main.entity.type.impl;

import main.content.DC_TYPE;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;

public class BuffType extends ObjType {

    private boolean isTransient;

    public BuffType(ObjType type) {
        super(type);
    }

    public BuffType() {
        super();
        setProperty(G_PROPS.TYPE, DC_TYPE.BUFFS.getName());
        setOBJ_TYPE_ENUM(DC_TYPE.BUFFS);
    }

    public BuffType(Ref ref) {
        super(ref);
        setProperty(G_PROPS.TYPE, DC_TYPE.BUFFS.getName());
        setOBJ_TYPE_ENUM(DC_TYPE.BUFFS);
    }

    public BuffType(boolean gen, ObjType objType) {
        super(gen, objType);
    }

    public Integer getDuration() {
        if (getIntParam(G_PARAMS.DURATION) == 0) {
            return null;
        }
        return getIntParam(G_PARAMS.DURATION);
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean b) {
        this.isTransient = b;
    }

}
