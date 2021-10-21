package main.data.xml;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;

import java.io.File;
import java.util.*;

public class XML_Writer {

    public static final String XML_VERSION_PREFIX = "version=";
    public static final String XML_VERSION_SEPARATOR = "%%";
    public static final int STR_CAPACITY = 15000;
    public static final String separator = PathUtils.getPathSeparator();
    private static final String XML = "XML";
    private static final String BACK_UP = separator+"backup";
    private static final String RESERVE = separator+"reserve";
    private static final String EMPTY_XML = "<XML></XML>";
    static Map<String, ObjType> map;
    static String subgroup = "";
    static Map<String, StringBuilder> subStrings;
    static private StringBuilder stringPool;
    static private String path;
    private static OBJ_TYPE currentObjTypeGroup;
    private static String filePath;
    private static String fileName;
    private static boolean backUp = false;
    private static boolean reserve;
    private static String customPath;
    private static boolean dirtyOnly;
    private static Boolean writingBlocked;
    private static List<OBJ_TYPE> blocked = new ArrayList<>();


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
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            setBackUpMode(false);
        }

    }

    public static void saveAll() {
        for (String typeName : XML_Reader.getTypeMaps().keySet()) {
            OBJ_TYPE type = ContentValsManager.getOBJ_TYPE(typeName);
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
        stringPool = new StringBuilder( "<XML ");
stringPool
        .append(XML_VERSION_PREFIX)
        .append("'")
        .append(CoreEngine.XML_BUILD)
        .append(XML_VERSION_SEPARATOR)
        .append("'")
        .append(">");
        if (group == null) {
            map = DataManager.getTypeMap(TYPE);
            if (map == null) {
                return false;
            }
        }
        constructSubStrings();

        putSubGroups();

        stringPool.append(closeXML(XML));

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

            stringPool = new StringBuilder(xml.replace(typeSubString, newTypeString));

        } else

        {
            String groupNode = openXML(type.getProperty(TYPE.getGroupingKey()));

            if (xml.contains(groupNode)) {
                stringPool = new StringBuilder(xml.replace(groupNode, groupNode + newTypeString));
            } else {
                stringPool = new StringBuilder(xml.replace(closeXML(XML), new StringBuilder().append(groupNode).
                        append(newTypeString).append(closeXML(type.getProperty(TYPE.getGroupingKey())))
                        .append(closeXML(XML)).toString()));
            }
        }
        return write();
    }

    private static String getXML() {
        return FileManager.readFile((path + fileName));
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
                main.system.ExceptionMaster.printStackTrace(e);
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
            stringPool.append(openXML(strname));
            stringPool.append(subGroup.toString());
            stringPool.append(closeXML(strname));
        }

    }

    public static String getIncompleteTypeXML(Entity type, Entity parent) {
        return getTypeXML_Builder(type, new StringBuilder(200), parent).toString();
    }

    private static StringBuilder getTypeXML_Builder(Entity type, StringBuilder builder) {
        return getTypeXML_Builder(type, builder, null);
    }

    public static StringBuilder getTypeXML_Builder(Entity type,
                                                   Entity parent,
                                                   boolean writeAllValues) {
        return getTypeXML_Builder(type, null, parent, writeAllValues);
    }

    public static StringBuilder getTypeXML_Builder(Entity type,
                                                   StringBuilder builder, Entity parent) {
        return getTypeXML_Builder(type, builder, parent, false);
    }


    public static StringBuilder getTypeXML_Builder(Entity type,
                                                   StringBuilder builder,
                                                   Entity parent,
                                                   boolean writeAllValues

    ) {
        return getTypeXML_Builder(type, builder, parent, null, writeAllValues);
    }

    public static StringBuilder getTypeXML_Builder(Entity type,
                                                   StringBuilder builder,
                                                   Entity parent,
                                                   Entity originalType,
                                                   boolean writeAllValues, VALUE... exceptions

    ) {
        if (type.getName().isEmpty()) {
            return builder;
        }
        if (builder == null)
            builder = new StringBuilder();
        builder.append(openXML(type.getName()));
        builder.append("<params>");

        List<VALUE> exceptionList=new ArrayList<>();
        if (exceptions != null) {
            if (exceptions.length > 0) {
                exceptionList.addAll(Arrays.asList(exceptions));
            }
        }
        for (PARAMETER param : type.getParamMap().keySet()) {
            if (exceptionList.contains(param))
                continue;

                ParamMap map = type.getParamMap();
            if (originalType != null)
                if (!param.isDynamic()) {
                    map = parent.getParamMap();
                }
            String value = XML_Formatter.formatXmlTextContent(map.get(param), param);
            if (!writeAllValues)
                if (!param.isDynamic()) {
                    if (parent != null) {
                        String parentValue = (originalType != null)
                         ? originalType.getParamMap().get(param)
                         : parent.getParamMap().get(param);
                        if (parentValue != null)
                            if (parentValue.equals(value)) {
                                continue;
                            }
                    }
                }
            if (!checkWriteValue(param, value, type.getOBJ_TYPE_ENUM())) {
                continue;
            }

            appendLeafNode(builder,
             StringMaster.capitalizeFirstLetter(param.getName()), value);
        }

        builder.append("</params>");
        builder.append("<props>");

        for (PROPERTY prop : type.getPropMap().keySet()) {

            if (exceptionList.contains(prop))
                continue;
            String value = XML_Formatter.formatXmlTextContent(type.getPropMap().get(prop), prop);
            if (!writeAllValues)
                if (parent != null) { // don't duplicate
                    String parentValue = parent.getPropMap().get(prop);
                    if (parentValue != null)
                        if (parentValue.equalsIgnoreCase(value)) {
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
        if (!(ContentValsManager.isValueForOBJ_TYPE(TYPE, val))) {
            return false;
        }
        if (TYPE == DC_TYPE.SKILLS || TYPE == DC_TYPE.CHARS || TYPE == DC_TYPE.UNITS
         || TYPE == DC_TYPE.SPELLS) {
            if (StringMaster.isEmpty(value) || value.equals("0")) {
                return val.getName().equalsIgnoreCase("CIRCLE");
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
        if (value.contains("/s")) {
            value= value.replace("/s", "\\s");
        }
        // + "\n" //costs 10x performance...
        sub.append(openXML(valName)).append(value).append(closeXML(valName));
    }

    private static boolean write() {
        return write(stringPool.toString(), path, fileName);
    }

    public static void write(XML_File file) {
        setPathForOBJ_TYPE(file.getType(), file.getGroup());
        stringPool = new StringBuilder(file.getContents());
        write();
    }

    public static void setDirtyOnly(boolean dirty) {
        dirtyOnly = dirty;

    }

    public static boolean write(String content, String pathAndName) {
        return write(content, PathUtils.cropLastPathSegment(pathAndName),
         PathUtils.getLastPathSegment(pathAndName));
    }

    public static boolean write(String content, String path, String fileName) {

        if (content.equalsIgnoreCase(EMPTY_XML)) {
            main.system.auxiliary.log.LogMaster.log(1,"not writing EMPTY_XML! - " +path);
            return false;
        }

        File dir = FileManager.getFile(path);
        if (!dir.isDirectory()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return false;
            }
        }
        return FileManager.write(content, path + separator+"" + fileName);
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
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            backUp = false;
            reserve = false;
        }

    }

}
