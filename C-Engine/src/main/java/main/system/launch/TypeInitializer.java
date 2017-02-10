package main.system.launch;

import main.ability.AbilityType;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.entity.type.*;

public class TypeInitializer {
    private boolean xmlTreeValue = false;

    public ObjType getNewType(OBJ_TYPE obj_type) {
        OBJ_TYPES OBJ_TYPE;

        ObjType type = new ObjType();
        if (obj_type instanceof OBJ_TYPES) {
            OBJ_TYPE = (OBJ_TYPES) obj_type;
            switch (OBJ_TYPE) {
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
}
