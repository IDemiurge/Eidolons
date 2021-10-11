package main.handlers.gen;

import eidolons.content.PARAMS;
import eidolons.content.etalon.EtalonGen;
import eidolons.content.values.ValuePages;
import eidolons.system.utils.content.BfObjPropGenerator;
import eidolons.system.utils.content.ContentGenerator;
import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.launch.AV_Utils;
import main.v2_0.AV2;

import java.util.List;

public class AvGenHandler extends AvHandler {


    public AvGenHandler(AvManager manager) {
        super(manager);
    }

    @Override
    public void init() {
        if (AV_Utils.isTypeRead(DC_TYPE.UNITS)){
        List<ObjType> gen = EtalonGen.generateEtalonTypes();
        gen.forEach(t -> DataManager.addType(t));
        }
    }

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
