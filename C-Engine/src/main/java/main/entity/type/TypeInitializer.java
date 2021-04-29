package main.entity.type;

import main.ability.AbilityType;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.dialogue.DialogueType;
import main.entity.type.impl.ActionType;
import main.entity.type.impl.BuffType;
import main.entity.type.impl.SpellType;
import main.entity.type.impl.UnitType;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

public class TypeInitializer {
    private boolean xmlTreeValue = false;
    private final Map<OBJ_TYPE, ObjType> defaultTypes = new HashMap<>();

    public ObjType getNewType(OBJ_TYPE obj_type) {
        DC_TYPE OBJ_TYPE;

        ObjType type = new ObjType();
        if (obj_type instanceof DC_TYPE) {
            OBJ_TYPE = (DC_TYPE) obj_type;
            switch (OBJ_TYPE) {
                case DIALOGUE:
                    type = new DialogueType();
                    setXmlTreeValue(true);
                    break;
                case ABILS:
                    type = new AbilityType();
                    setXmlTreeValue(true);
                    break;

                case BUFFS:
                    type = new BuffType();
                    break;
                case ACTIONS:

                    type = new ActionType();
                    break;
                case SPELLS:

                    type = new SpellType();
                    break;
                // case BF_OBJ:
                // type = new BfObjType();
                // type.addProp(CLASSIFICATION, BF_OBJ);
                // break;

                case UNITS:
                    type = new UnitType();
                    // type.addProp(CLASSIFICATION, UNIT);
                    break;
                case CHARS:

                    type = new UnitType();

                    // // type.addProp(CLASSIFICATION, UNIT);
                    // type.addProp(CLASSIFICATION, CHAR);
                    break;

                default:

                    break;

            }
        }
        type.setProperty(G_PROPS.TYPE, obj_type.getName());
        type.setOBJ_TYPE_ENUM(obj_type);
        return type;
    }

    public boolean isXmlTreeValue() {
        return xmlTreeValue;
    }

    public void setXmlTreeValue(boolean xmlTreeValue) {
        this.xmlTreeValue = xmlTreeValue;
    }

    public ObjType getOrCreateDefault(OBJ_TYPE type) {
        return getOrCreateDefault(type, true, true);
    }

    public ObjType getOrCreateDefault(OBJ_TYPE type, boolean setDefaultProps, boolean setDefaultParams) {
        if (!(  setDefaultParams && setDefaultParams)) {
            return getNewType(type);
        }
            ObjType objType = defaultTypes.get(type);
        if (objType != null && setDefaultParams && setDefaultParams) { ////TODO just separate caches?
            if (type == DC_TYPE.BUFFS) {
                return new BuffType(false, objType);
            }
            if (type == DC_TYPE.ABILS) {
                return new AbilityType(false, objType);
            }
            return new ObjType(false, objType);
        }
        objType = getNewType(type);
        for (VALUE value : ContentValsManager.getValuesForType(type.getName(), false)) {
            if (!setDefaultProps) {
                if (value instanceof PROPERTY)
                    continue;
            }
            if (!setDefaultParams) {
                if (value instanceof PARAMETER)
                    continue;
            }
            if (!StringMaster.isEmpty(value.getDefaultValue())) {
                objType.setValue(value, value.getDefaultValue());
            }
        }
        defaultTypes.put(type, objType);
        if (type == DC_TYPE.ABILS) {
            return new AbilityType(false, objType);
        }
        if (type == DC_TYPE.BUFFS) {
            return new BuffType(false, objType);
        }
        return new ObjType(false, objType);
    }

}
