package main.file;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;

import java.util.LinkedList;
import java.util.List;

public class ContentSyncMaster {
    /*
     * AV workspace sync with AT types
     * Generate/Update AT types for Content
     *
     */
    DC_TYPE[] syncedTypes = {DC_TYPE.SKILLS, DC_TYPE.SPELLS, DC_TYPE.CLASSES,
            DC_TYPE.DEITIES, DC_TYPE.ITEMS};

    public static void generateTypeForDC_Item() {

//		new ObjType(AT_OBJ_TYPE.TASK);

    }

    private static boolean checkGroupInWorkspace(String sub) {
        // WorkspaceMaster
//		scope.getGroups().contains(generic);
//		new Workspace(name, typeList); // save
        return false;
    }

    public static void generateTypesForDC_Content() {
        XML_Reader.readTypes(false); // TODO selective?
        List<DC_TYPE> syncedTypes = new LinkedList<>();
        for (DC_TYPE TYPE : syncedTypes) {
            for (String sub : XML_Reader.getSubGroups(TYPE)) {
                if (!checkGroupInWorkspace(sub)) {
                    continue;
                }
//				goal = generateGoal(generic);
            }
        }

        for (DC_TYPE t : DC_TYPE.values()) {
            for (ObjType type : DataManager.getTypes(t)) {
                // XML_Reader.getTypeMaps()

                // by subgroups

                // generateTask()

            }
        }

    }

    public enum CONTENT_SCOPE {

    }

}
