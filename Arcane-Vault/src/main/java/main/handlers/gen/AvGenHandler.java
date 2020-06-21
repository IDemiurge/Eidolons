package main.handlers.gen;

import eidolons.content.PARAMS;
import eidolons.content.ValuePages;
import eidolons.system.content.BfObjPropGenerator;
import eidolons.system.content.ContentGenerator;
import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;

public class AvGenHandler {
    public static void generateNewArmorParams() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.ARMOR)) {
            ContentGenerator.generateArmorParams(type);
        }
    }

    public static void generateNewWeaponParams() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.WEAPONS)) {
            ContentGenerator.generateWeaponParams(type);
        }
    }

    public static void generateBfObjProps() {
        for (ObjType t : DataManager.getTypes(DC_TYPE.BF_OBJ)) {
            BfObjPropGenerator.generateBfObjProps(t);
            if (t.getProperty(G_PROPS.BF_OBJECT_GROUP).equalsIgnoreCase("water")) {
                for (PARAMETER resistance : ValuePages.RESISTANCES) {
                    if (resistance == PARAMS.FIRE_RESISTANCE)
                        t.setParam(resistance, 20);
                    else if (resistance == PARAMS.SONIC_RESISTANCE)
                        t.setParam(resistance, 30);
                    else if (resistance == PARAMS.ACID_RESISTANCE)
                        t.setParam(resistance, 40);
                    else
                        t.setParam(resistance, 100);
                    //TODO freeze to ice?
                }
                //                BfObjPropGenerator.generateBfObjStatProps(t);
            }
        }
    }

    private static void generateBfObjParams() {
        // for (ObjType t : DataManager.getTypes(OBJ_TYPES.BF_OBJ)) {
        // ContentGenerator.generateBfObjParams(t);
        // }

    }
}
