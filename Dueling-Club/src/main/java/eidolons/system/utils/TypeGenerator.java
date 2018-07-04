package eidolons.system.utils;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.DC_Engine;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 6/30/2018.
 */
public class TypeGenerator {

    private static final String TYPE_SEPARATOR = "::";
    private static final String VALUE_SEPARATOR = "--";
    private static final DC_TYPE BASE_TYPE = DC_TYPE.PERKS;
    private static final  String BASE_TYPE_NAME = "Waxing Wrath";

    public static void main(String[] args) {
        CoreEngine.systemInit();
        DC_Engine.dataInit();
        String inputData = getInput();

        ObjType baseType = DataManager.getType(BASE_TYPE_NAME,BASE_TYPE);

        VALUE[] specifiedValues = {
         G_PROPS.DESCRIPTION,
         G_PROPS.PERK_CLASS_REQUIREMENTS,
//         G_PROPS.PERK_GROUP,
//         G_PARAMS.PERK_LEVEL,
         PROPS.PARAMETER_BONUSES,
         G_PROPS.VARIABLES
        };
        String typesGenerated="";
        for (String part : StringMaster.openContainer(inputData,
         "\n")) {
            for (String substring : StringMaster.openContainer(inputData, TYPE_SEPARATOR)) {
            ObjType type =generateType(baseType, substring, specifiedValues);
            processType(type, part);
            typesGenerated +=type.getName()+" \n";
            DataManager.addType(type);

        }
        }
        main.system.auxiliary.log.LogMaster.log(1,"Types Generated: \n "+typesGenerated );
        XML_Writer.writeXML_ForTypeGroup(BASE_TYPE);
    }

    private static void processType(ObjType type, String part) {
        type.setValue(G_PROPS.PERK_GROUP, part.trim());
    }

    private static ObjType generateType(ObjType baseType,
                                        String data,
                                        VALUE... specifiedValues) {
        String[] parts = data.split(VALUE_SEPARATOR);
        String name = parts[0].trim();
        ObjType type = new ObjType(name, baseType);
        int i =0;
        for (String sub : parts) {
            if (i==0) {
                i++;
                continue;
            }
            type.setValue(specifiedValues[i++-1], sub.trim());
        }
        type.setGenerated(false);
        return type;
    }

    private static String getInput() {
        return ":: Desperate gamble -- when Critically Wounded, hero gains +25% Initiative " +
         "and +35% damage. -- Swashbuckler \n" +
         ":: Battle Trance -- adds Cadence to hero's one-handed or two-handed single " +
         "weapon attacks. -- Warrior";
    }
}
