package eidolons.game.module.adventure.global.persist;

import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.datatypes.DequeImpl;
import main.system.entity.FilterMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/8/2018.
 */
public class PersistenceMaster {

    private static DequeImpl<OBJ_TYPE> custom_OBJ_TYPES;
    private static DequeImpl<ObjType> customTypes;

    private static DequeImpl<ObjType> copyTypes;
    private static DequeImpl<ObjType> removedTypes;

    public static List<ObjType> getCustomTypes(MACRO_OBJ_TYPES TYPE) {
        ArrayList<ObjType> list = new ArrayList<>(customTypes);
        FilterMaster.filterByProp(list, "Type", TYPE.getName());
        return list;
    }

    public static ObjType addCustomType(ObjType parent, String name) {
        ObjType type = new ObjType(parent, true);
        type.initType();
        type.setName(name);
        type.setProperty(G_PROPS.PARENT_TYPE, parent.getName());
        getCustomTypes().add(type);
        if (!getCustom_OBJ_TYPES().contains(type.getOBJ_TYPE_ENUM())) {
            getCustom_OBJ_TYPES().add(type.getOBJ_TYPE_ENUM());
        }
        type.setGenerated(true);
        DataManager.addType(type);
        return type;
    }


//    private static void initCustomTypes() {
//        customTypes = new DequeImpl<>();
//        custom_OBJ_TYPES = new DequeImpl<>();
//        for (OBJ_TYPE t : MACRO_OBJ_TYPES.values()) {
//            File file = FileManager.getFile(getTypeDataPath() + t.getName() + ".xml");
//            if (!file.isFile()) {
//                continue;
//            }
//            custom_OBJ_TYPES.add(t);
//            String xml = FileManager.readFile(file);
//            customTypes.addAll(XML_Reader.createCustomTypeList(xml, t, MacroGame.getGame(),
//             !isEditMode(), true, false));
//
//            // full type data or on top of base type?
//        }
//        for (ObjType type : customTypes) {
//            type.setGenerated(true);
//            DataManager.addType(type);
//        }
//    }

    public static ObjType getCustomType(String typeName) {
        if (typeName == null) {
            return null;
        }
        for (ObjType t :  getCustomTypes()) {
            if (typeName.equals(t.getName())) {
                return t;
            }
        }

        return null;
    }

    public static void saveCustomTypes() {
        // saveCopyTypes();
//        for (OBJ_TYPE t : custom_OBJ_TYPES) {
//            String content = XML_Converter.openXmlFormatted(t.getName());
//            for (ObjType type : customTypes) {
//
//                if (type.getOBJ_TYPE_ENUM() == t) {
//                    content += XML_Writer.getIncompleteTypeXML(type, type.getType());
//                }
//            }
//            content += XML_Converter.closeXmlFormatted(t.getName());
//
//            String path = getTypeDataPath();
//            String fileName = t.getName() + ".xml";
//            XML_Writer.write(content, path, fileName);
//        }
    }

    public static DequeImpl<OBJ_TYPE> getCustom_OBJ_TYPES() {
        return custom_OBJ_TYPES;
    }

    public static DequeImpl<ObjType> getCustomTypes() {
        return customTypes;
    }

    public static DequeImpl<ObjType> getRemovedTypes() {
        if (removedTypes == null) {
            removedTypes = new DequeImpl<>();
        }
        return removedTypes;

    }
//    private static String getTypeDataPath() {
//        return PathFinder.getTYPES_PATH() + (!isSave() ? "\\campaign\\" : "\\save\\")
//         + getCampaignName() + "\\";
//    }
}
