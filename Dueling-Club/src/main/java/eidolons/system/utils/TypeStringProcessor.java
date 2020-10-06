package eidolons.system.utils;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnitFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TypeStringProcessor {

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

    public static void convertAltFile(String base, String path, DC_TYPE TYPE) {
        String contents = FileManager.readFile(path);
        convertAlt(base, contents, TYPE);
    }

    public static void convertAlt(String base, String json, DC_TYPE TYPE) {
        String group = null;
        String subGroup = null;
        PROPERTY groupP =TYPE==DC_TYPE.ENCOUNTERS
                ? G_PROPS.ENCOUNTER_GROUP
        : TYPE.getGroupingKey();
        PROPERTY subGroupP = TYPE==DC_TYPE.ENCOUNTERS
                ? G_PROPS.ENCOUNTER_SUBGROUP
                :TYPE.getSubGroupingKey();
        Set<String> groups = XML_Reader.getTabGroupMap().get(TYPE.getName());
        Set<String> subGroups = XML_Reader.getSubGroups(TYPE);
        ObjType baseType = DataManager.getType(base, TYPE);
        String groupSeparator = ">>";
        String subGroupSeparator = ">";
        String customPropSeparator = "|";
        Map customMap = new LinkedHashMap();
        for (String line : StringMaster.splitLines(json.trim())) {
            line = line.trim();
            if (line.trim().isEmpty()) {
                continue;
            }
            if (line.startsWith(groupSeparator)) {
                group = line.substring(2).trim();
                if (groups != null) {
                    groups.add(group);
                }
                continue;
            } else {
                if (line.startsWith(subGroupSeparator)) {
                    subGroup = line.substring(1).trim();
                    if (subGroups != null) {
                        subGroups.add(subGroup);
                    }
                    continue;
                } else {
                    if (line.startsWith(customPropSeparator)) {
                        String s = line.substring(1).trim();
                        customMap = new DataUnitFactory().deconstructDataString(s);
                        continue;
                    }
                }
                String name = line;
                if (!isOverwrite()){
                    if (DataManager.getType(name, TYPE) != null) {
                        continue;
                    }
                }
                ObjType type = new ObjType(baseType);

                for (Object o : customMap.keySet()) {
                    VALUE value = ContentValsManager.getValue(o.toString().trim());
                    type.setValue(value, customMap.get(o).toString().trim());
                }
                type.setProperty(groupP, group);
                type.setProperty(G_PROPS.GROUP, group);
                type.setProperty(subGroupP, subGroup);
                if (line.contains("=")) {
                    name = line.split("=")[0];
                    String s = line.split("=")[1];
                    processValueMap(type, s);
                }
                type.setName(name);
                type.setGenerated(false);
                DataManager.addType(type);
            }
        }
        }

    private static boolean isOverwrite() {
        return true;
    }
}
