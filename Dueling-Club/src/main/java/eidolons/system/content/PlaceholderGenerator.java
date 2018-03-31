package eidolons.system.content;

import eidolons.game.core.master.ObjCreator;
import main.content.DC_TYPE;
import eidolons.content.PROPS;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 11/24/2017.
 */
public class PlaceholderGenerator {

    public static void generate() {
        /*
        unit subgroups
         */
        for (UNIT_GROUP group : UnitEnums.UNIT_GROUP.values()) {
            for (String sub : StringMaster.openContainer(group.getSubgroups(), ",")) {
                for (PLACEHOLDER_AI_TYPE aiType : PLACEHOLDER_AI_TYPE.values()) {
                    ObjType type = generate(group, sub, aiType.name());
                    type.setProperty(PROPS.AI_TYPE, aiType.name());
                }
                for (PLACEHOLDER_POWER power : PLACEHOLDER_POWER.values()) {
                    generate(group, sub, power.name());
                }
            }
        }
    }

    private static ObjType generate(UNIT_GROUP group, String sub, String suffix) {
        //aspect ?
        // base type?
        String name = ObjCreator.PLACEHOLDER + " " + group.toString()
         + " " + sub + "_" + suffix;
        ObjType type = new ObjType(name, DC_TYPE.UNITS);
        type.setProperty(G_PROPS.GROUP, sub);
        type.setProperty(G_PROPS.ASPECT, ASPECT.NEUTRAL.toString());
        type.setProperty(G_PROPS.UNIT_GROUP, group.toString());
        DataManager.addType(type);
        return type;
    }

    public enum PLACEHOLDER_AI_TYPE {
        RANGED,
        MAGE,
        SNEAK,
        TANK,
        BRUTE,

    }

    public enum PLACEHOLDER_POWER {
        REGULAR,
        REGULAR_MASS,
        ELITE,
        ELITE_MASS,
        BOSS,
    }
}
