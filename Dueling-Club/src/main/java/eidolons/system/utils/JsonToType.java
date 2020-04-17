package eidolons.system.utils;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnitFactory;

import java.util.Map;

public class JsonToType {

    public static void convert(String json, DC_TYPE TYPE) {
        //base type
        for (String substring : ContainerUtils.openContainer(json, "||")) {
            ObjType baseType = DataManager.getType(substring.split("=")[0], TYPE);
            ObjType type = new ObjType(baseType);
            String s = substring.split("=")[1];
            processValueMap(type, s);
//            DC_Game.game.initType(type);
            DataManager.addType(type);
        }
    }

    private static void processValueMap(ObjType type, String s) {
        Map map = new DataUnitFactory().deconstructDataString(s);
        for (Object o : map.keySet()) {
            VALUE value = ContentValsManager.getValue(o.toString().trim());
            type.setValue(value, map.get(o).toString().trim());
        }
    }

    public static void convertAlt(String base,String json, DC_TYPE TYPE) {
        String group = null;
        String subGroup = null;
        PROPERTY groupP = TYPE.getGroupingKey();
        PROPERTY subGroupP= TYPE.getSubGroupingKey();
        ObjType baseType=DataManager.getType(base, TYPE);
        String groupSeparator = ">>";
        String subGroupSeparator = ">";
        for (String line : StringMaster.splitLines(json.trim())) {
            if (line.startsWith(groupSeparator)) {
                group = line.substring(2).trim();
                continue;
            } else {
                if (line.startsWith(subGroupSeparator)) {
                    subGroup = line.substring(1).trim();
                    continue;
                }
            }
            ObjType type = new ObjType(baseType);
            type.setName(line.trim());
            type.setProperty(groupP, group);
            type.setProperty(subGroupP, subGroup);
            if (line.contains("=")) {
                String s = line.split("=")[1];
                processValueMap(type, s);
            }
            DataManager.addType(type);
        }
    }
}
