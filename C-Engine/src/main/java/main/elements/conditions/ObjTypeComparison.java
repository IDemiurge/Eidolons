package main.elements.conditions;

import main.content.OBJ_TYPE;
import main.data.ability.OmittedConstructor;
import main.entity.Entity;
import main.entity.Ref.KEYS;

public class ObjTypeComparison extends MicroCondition {

    private OBJ_TYPE TYPE;
    private String key;

    @OmittedConstructor
    public ObjTypeComparison(OBJ_TYPE TYPE, String key, boolean b) {
        this.TYPE = TYPE;
        this.key = key;
    }

    public ObjTypeComparison(OBJ_TYPE TYPE, String key) {
        this(TYPE, key, true);
    }

    public ObjTypeComparison(OBJ_TYPE TYPE) {
        this(TYPE, KEYS.MATCH.toString());
    }

    @Override
    public boolean check() {
        if (TYPE == null) {
            return true;
        }
        Entity entity = ref.getObj(key);
        if (entity == null) {
            entity = ref.getType(key);
        }
        if (entity == null) {
            return false;
        }
        return TYPE.equals(entity.getOBJ_TYPE_ENUM());
    }

    public boolean isLoggingBlocked() {
        return true;
    }
}
