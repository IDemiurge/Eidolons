package main.entity.type;

import main.content.OBJ_TYPES;
import main.content.parameters.G_PARAMS;
import main.content.properties.G_PROPS;
import main.entity.Ref;

public class BuffType extends ObjType {

    private boolean isTransient;

    public BuffType(ObjType type) {
        super(type);
    }

    public BuffType() {
        super();
        setProperty(G_PROPS.TYPE, OBJ_TYPES.BUFFS.getName());
        setOBJ_TYPE_ENUM(OBJ_TYPES.BUFFS);
    }

    public BuffType(Ref ref) {
        super(ref);
        setProperty(G_PROPS.TYPE, OBJ_TYPES.BUFFS.getName());
        setOBJ_TYPE_ENUM(OBJ_TYPES.BUFFS);
    }

    public Integer getDuration() {
        if (getIntParam(G_PARAMS.DURATION) == 0)
            return null;
        return getIntParam(G_PARAMS.DURATION);
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean b) {
        this.isTransient = b;
    }

}
