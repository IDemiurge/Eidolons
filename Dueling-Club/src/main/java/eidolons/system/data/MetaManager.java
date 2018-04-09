package eidolons.system.data;

import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.system.auxiliary.data.FileManager;
import main.system.launch.TypeBuilder;

import java.io.File;

public class MetaManager {

    public static final String ENTITY_NAME = "Data Entity";
    public static final String FILE_NAME = "meta data.xml";
    private static ObjType data;

    public static void init() {
        File file = new File(getInitFilePath() + FILE_NAME);
        if (!file.isFile()) {
            data = new ObjType(ENTITY_NAME);
        } else {
            String text = FileManager.readFile(file);
            setData(TypeBuilder.buildType(XML_Converter.getDoc(text)
             .getFirstChild(), new ObjType(ENTITY_NAME)));
            text = FileManager.readFile(file);
        }
    }

    public static void saveMetaData() {
        String xml = XML_Writer.getTypeXML(getData(), new StringBuilder(5000));
        XML_Writer.write(xml, getInitFilePath(), FILE_NAME);
    }

    private static String getInitFilePath() {

        return PathFinder.getXML_PATH();
    }

    public static ObjType getData() {
        return data;
    }

    public static void setData(ObjType data) {
        MetaManager.data = data;
    }

    public static String getParam(String p) {
        return data.getParam(p);
    }

    public static String getParam(PARAMETER param) {
        return data.getParam(param);
    }

    public static String getProperty(String prop) {
        return data.getProperty(prop);
    }

    public static String getProperty(PROPERTY prop) {
        return data.getProperty(prop);
    }

    public static boolean checkParam(PARAMETER param, String value) {
        return data.checkParam(param, value);
    }

    public static boolean checkProperty(PROPERTY p, String value) {
        return data.checkProperty(p, value);
    }

    public static boolean checkContainerProp(PROPERTY PROP, String value) {
        return data.checkContainerProp(PROP, value);
    }

    public static boolean addProperty(PROPERTY prop, String value) {
        return data.addProperty(prop, value);
    }

    public static void addProperty(String prop, String value) {
        data.addProperty(prop, value);
    }

    public static boolean setParam(PARAMETER param, String value) {
        return data.setParam(param, value);
    }

    public static void setProperty(PROPERTY name, String value) {
        data.setProperty(name, value);
    }
}
