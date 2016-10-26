package main.file;

import main.content.OBJ_TYPES;
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
	OBJ_TYPES[] syncedTypes = { OBJ_TYPES.SKILLS, OBJ_TYPES.SPELLS, OBJ_TYPES.CLASSES,
			OBJ_TYPES.DEITIES, OBJ_TYPES.ITEMS };

	public enum CONTENT_SCOPE {

	}

	public static void generateTypeForDC_Item() {

//		new ObjType(AT_OBJ_TYPE.TASK);

	}

	private static boolean checkGroupInWorkspace(String sub) {
		// WorkspaceMaster
//		scope.getGroups().contains(sub);
//		new Workspace(name, typeList); // save
		return false;
	}

	public static void generateTypesForDC_Content() {
		XML_Reader.readTypes(false); // TODO selective?
 List<OBJ_TYPES> syncedTypes = new LinkedList<>();
		for (OBJ_TYPES TYPE : syncedTypes)
			for (String sub : XML_Reader.getSubGroups(TYPE)) {
				if (!checkGroupInWorkspace(sub))
					continue;
//				goal = generateGoal(sub);
			}

		for (OBJ_TYPES t : OBJ_TYPES.values())
			for (ObjType type : DataManager.getTypes(t)) {
				// XML_Reader.getTypeMaps()

				// by subgroups

				// generateTask()

			}

	}

}
