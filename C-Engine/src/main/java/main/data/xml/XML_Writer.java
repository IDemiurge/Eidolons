package main.data.xml;

import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XML_Writer {

    public static final int STR_CAPACITY = 15000;
    private static final String XML = "XML";
    private static final String BACK_UP = "\\backup";
    private static final String RESERVE = "\\reserve";
    private static final String EMPTY_XML = "<XML></XML>";
    static Map<String, ObjType> map;
    static String subgroup = "";
    static Map<String, StringBuilder> subStrings;
    static private String stringPool;
    static private String path;
    private static OBJ_TYPE currentObjTypeGroup;
    private static String filePath;
    private static String fileName;
    private static boolean backUp = false;
    private static boolean reserve;
    private static String customPath;
    private static boolean dirtyOnly;
    private static Boolean writingBlocked;
    private static List<OBJ_TYPE> blocked = new LinkedList<>();



    public static void createXmlFileForTypeGroup(OBJ_TYPE TYPE) {
        // content = openXML(XML) + "" + closeXML(XML);
        // if (customPath != null) {
        // write(content, customPath, TYPE.getName());
        // }

    }

    public static boolean writeXML_ForTypeGroup(OBJ_TYPE TYPE) {
        return writeXML_ForTypeGroup(TYPE, null);
    }

    public static void backUpAll() {
        setBackUpMode(true);
        try {
            saveAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setBackUpMode(false);
        }

    }

    public static void saveAll() {
        for (String typeName : XML_Reader.getTypeMaps().keySet()) {
            OBJ_TYPE type = ContentManager.getOBJ_TYPE(typeName);
            if (isWritingBlocked(type)) {
                continue;
            }
            writeXML_ForTypeGroup(type);
        }
    }

    public static List<OBJ_TYPE> getBlocked() {
        return blocked;
    }

    private static boolean isWritingBlocked(OBJ_TYPE type) {
        return blocked.contains(type);
    }

    public static boolean writeXML_ForTypeGroup(OBJ_TYPE TYPE, String group) {
        if (XML_Reader.isBrokenXml()) {
            if (writingBlocked == null) {
                // writingBlocked = DialogMaster
                // .confirm("Xml wasn't read properly, block writing Types' xml?");
            }
        }
        if (BooleanMaster.isTrue(writingBlocked)) {
            return false;
        }
        if (group == null) {
            if (DC_TYPE.getXmlGroups(TYPE) != null) {
                for (Object obj : DC_TYPE.getXmlGroups(TYPE)) {
                    String name = obj.toString();
                    List<String> types = DataManager.getTypeNamesGroup(TYPE, name);
                    map = new MapMaster<String, ObjType>().constructMap(types, DataManager
                            .toTypeList(types, TYPE));
                    writeXML_ForTypeGroup(TYPE, name);

                }
                return true;
            }
        }

        setPathForOBJ_TYPE(TYPE, group);
        LogMaster.log(0, path + " - WRITING XML FOR GROUP " + TYPE);
        stringPool = "<XML>";

        if (group == null) {
            map = DataManager.getTypeMap(TYPE);
            if (map == null) {
                return false;
            }
        }
        constructSubStrings();

        putSubGroups();

        stringPool += closeXML(XML);

        return write();

    }

    public static void setCustomPath(String string) {
        customPath = string;
    }

    private static void setPathForOBJ_TYPE(OBJ_TYPE TYPE, String group) {
        currentObjTypeGroup = TYPE;
        if (customPath != null) {
            path = customPath;
            customPath = null;
        } else if (XML_Reader.getCustomTypesPath() != null) {
            path = XML_Reader.getCustomTypesPath();
        } else {
            path = (!DC_TYPE.isOBJ_TYPE(TYPE.toString()) ? PathFinder.getMACRO_TYPES_PATH()
                    : PathFinder.getTYPES_PATH());
        }
        if (backUp) {
            path += BACK_UP;
        }
        if (reserve) {
            path += RESERVE;
            path += " " + TimeMaster.getFormattedDate(true);
            File file = FileManager.getFile(path);
            if (!file.exists()) {
                file.mkdir();
            }
        }
        fileName = (!StringMaster.isEmpty(group)) ? TYPE + "-" + group + ".xml" : TYPE + ".xml";
    }

    public static boolean writeXML_ForType(ObjType type) {
        return writeXML_ForType(type, type.getOBJ_TYPE_ENUM());
    }

    public static boolean writeXML_ForType(ObjType type, OBJ_TYPE TYPE) {
        return writeXML_ForType(type, TYPE, null);
    }

    public static boolean writeXML_ForType(ObjType type, OBJ_TYPE TYPE, String group) {

        StringBuilder builder;
        builder = new StringBuilder(STR_CAPACITY);
        setPathForOBJ_TYPE(TYPE, group);
        String newTypeString = getTypeXML(type, builder);
        if (!newTypeString.isEmpty()) {
            if (XML_Converter.getDoc(newTypeString) == null) {
                LogMaster.log(1, "faulty xml for " + type.getName());
                return false;
            }
        }
        String xml = getXML();

        if (xml.contains(openXML(type.getName()))) {

            int endIndex = xml.indexOf(closeXML(type.getName()));

            int beginIndex = xml.indexOf(openXML(type.getName()));

            String typeSubString = xml.substring(beginIndex, endIndex) + closeXML(type.getName());

            stringPool = xml.replace(typeSubString, newTypeString);

        } else

        {
            String groupNode = openXML(type.getProperty(TYPE.getGroupingKey()));

            if (xml.contains(groupNode)) {
                stringPool = xml.replace(groupNode, groupNode + newTypeString);
            } else {
                stringPool = xml.replace(closeXML(XML), groupNode + newTypeString
                        + closeXML(type.getProperty(TYPE.getGroupingKey())) + closeXML(XML));
            }
        }
        return write();
    }

    private static String getXML() {
        return FileManager.readFile(new File(path + fileName));
    }

    private static void constructSubStrings() {
        subStrings = new HashMap<>();

        for (String typename : map.keySet()) {
            ObjType objType = map.get(typename);
            if (objType == null) {
                map.remove(typename);
                continue;
            }
            if (objType.isGenerated()) {
                continue;
            }

            if (dirtyOnly) {
                if (!objType.isDirty()) {
                    continue;
                }
            }
            objType.setDirty(false);
            String subgroup1 = objType.getProperty(DataManager.getGroupingKey(currentObjTypeGroup));

            StringBuilder subString;
            subString = subStrings.get(subgroup1);
            if (subString == null) {

                subString = new StringBuilder(STR_CAPACITY);
                subStrings.put(subgroup1, subString);
            }
            try {
                subString.append(getTypeXML(objType, new StringBuilder(STR_CAPACITY)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private static void putSubGroups() {
        for (String strname : subStrings.keySet()) {
            StringBuilder subGroup = subStrings.get(strname);
            LogMaster.log(0, "SUBSTRING: " + strname);
            if (strname.isEmpty()) {
                strname = "Empty";
            }
            stringPool += openXML(strname);
            stringPool += subGroup.toString();
            stringPool += closeXML(strname);
        }

    }

    public static String getIncompleteTypeXML(Entity type, Entity parent) {
        return getTypeXML_Builder(type, new StringBuilder(200), parent).toString();
    }

    private static StringBuilder getTypeXML_Builder(Entity type, StringBuilder builder) {
        return getTypeXML_Builder(type, builder, null);
    }

    public static StringBuilder getTypeXML_Builder(Entity type, StringBuilder builder, Entity parent) {
        if (type.getName().isEmpty()) {
            return builder;
        }
        builder.append(openXML(type.getName()));
        builder.append("<params>");
        for (PARAMETER param : type.getParamMap().keySet()) {
            if (param == null) {
                continue;
            }

            String value = XML_Formatter.formatXmlTextContent(type.getParamMap().get(param), param);
            if (parent != null) {
                if (parent.getParam(param).equals(value)) {
                    continue;
                }
            }

            if (!checkWriteValue(param, value, type.getOBJ_TYPE_ENUM())) {
                continue;
            }

            appendLeafNode(builder, StringMaster.capitalizeFirstLetter(param.getName()), value);
        }

        builder.append("</params>");
        builder.append("<props>");

        for (PROPERTY prop : type.getPropMap().keySet()) {

            String value = XML_Formatter.formatXmlTextContent(type.getPropMap().get(prop), prop);
            if (parent != null) { // don't duplicate
                if (parent.getProperty(prop).equals(value)) {
                    continue;
                }
            }
            if (prop == null) {
                LogMaster.log(1, "null key! ; value = "
                        + type.getPropMap().get(prop));
            }
            appendLeafNode(builder, StringMaster.capitalizeFirstLetter(prop

                    .getName()), value);
        }

        builder.append("</props>");

        return builder.append(closeXML(type.getName()));
    }

    private static boolean checkWriteValue(VALUE val, String value, OBJ_TYPE TYPE) {
        if (!(ContentManager.isValueForOBJ_TYPE(TYPE, val))) {
            return false;
        }
        if (TYPE == DC_TYPE.SKILLS || TYPE == DC_TYPE.CHARS || TYPE == DC_TYPE.UNITS
                || TYPE == DC_TYPE.SPELLS) {
            if (StringMaster.isEmpty(value) || value.equals("0")) {
                if (!val.getName().equalsIgnoreCase("CIRCLE")) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getTypeXML(ObjType type) {
        return getTypeXML(type, new StringBuilder(1000));
    }

    public static String getTypeXML(ObjType type, StringBuilder builder) {

        return getTypeXML_Builder(type, builder).toString();
    }

    public static String openXML(String s) {
        return "<" + XML_Formatter.formatStringForXmlNodeName(s) + ">";
    }

    public static String closeXML(String s) {
        return "</" + XML_Formatter.formatStringForXmlNodeName(s) + ">";
    }

    private static void appendLeafNode(StringBuilder sub, String valName, String value) {
        sub.append(openXML(valName) + value + closeXML(valName)
                // + "\n" //costs 10x performance...
        );
    }

    private static boolean write() {
        return write(stringPool, path, fileName);
    }

    public static void write(XML_File file) {
        setPathForOBJ_TYPE(file.getType(), file.getGroup());
        stringPool = file.getContents();
        write();
    }

    public static void setDirtyOnly(boolean dirty) {
        dirtyOnly = dirty;

    }

    public static boolean write(String content, String pathAndName) {
        return write(content, StringMaster.cropLastPathSegment(pathAndName),
         StringMaster.getLastPathSegment(pathAndName));
    }
        public static boolean write(String content, String path, String fileName) {

            if (fileName.contains("rack")) {
                return false;
            }

        File dir = new File(path);
            if (!dir.isDirectory()) {
                try {
                    dir.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        return FileManager.write(content, path + "\\" + fileName);
    }

    public static synchronized void setBackUpMode(boolean b) {
        backUp = b;

    }

    public static void createBackUpReserve() {
        backUp = true;
        reserve = true;
        try {
            saveAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            backUp = false;
            reserve = false;
        }

    }

}
