package main.utilities.search;

import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.StringMaster;
import main.utilities.workspace.Workspace;
import main.utilities.workspace.WorkspaceManager;

import java.io.File;
import java.util.List;

public class SearchMaster {
    /*
     * search vs filter: 1) search top tab, filter generic-tabs per type-tab or
	 * custom ones in Workspace 2) both use [...] value-condition filtering
	 * (should add a dropbox with condition templates - greater, less, equal,
	 * contains, strict, not, ) => OR could be an AE_EditPanel!
	 */

    public final static char HOTKEY_CHAR = 's';

    public static void deleteSearch(String name) {
        File file = FileManager.getFile(WorkspaceManager.getFolderPath(true)
                + name);
        if (file.isFile()) {
            file.delete();
        }

    }

    public static void newSearch() {
        // getOrCreate TYPES
        // getOrCreate search text, perhaps with some basic regex syntax like "not",
        // "or"...

        // List<OBJ_TYPE> types = new ArrayList<>();
        // int i = DialogMaster.optionChoice(OBJ_TYPES.values(),
        // "include object type... (at least one)");
        // // C_TYPES?
        // if (i == -1)
        // return;
        // types.add(OBJ_TYPES.values()[i]);
        // while (true) {
        // i = DialogMaster.optionChoice(OBJ_TYPES.values(),
        // "include another object type...");
        // if (i == -1)
        // break;
        // if (!types.contains(OBJ_TYPES.values()[i]))
        // types.add(OBJ_TYPES.values()[i]);
        // }
        // OBJ_TYPE[] TYPES = new OBJ_TYPE[types.size()];
        List<ObjType> list;
        String typeName = DialogMaster.inputText("Enter type search...",
                TypeFinder.getLastSearch());
        // TODO update search?
        if (ArcaneVault.getWorkspaceManager().getWorkspaceByName(typeName) != null) {
            deleteSearch(typeName);
        }
        if (!StringMaster.isEmpty(typeName)) {
            list = TypeFinder.findTypes(typeName,
                    // types.toArray(TYPES)
                    DC_TYPE.values());
        } else {
            // TypeFilter.filter(filter, groupingValue); //should be separate I
            // guess
            return;
        }
        // grouping - always by TYPE?

        Workspace ws = new Workspace(typeName, list, true);
        ArcaneVault.getWorkspaceManager().addWorkspace(ws);
        ArcaneVault.getMainBuilder().getTabBuilder().addWorkspaceTab(ws);

    }
}
