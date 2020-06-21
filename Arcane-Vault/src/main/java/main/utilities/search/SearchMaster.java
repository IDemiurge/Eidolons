package main.utilities.search;

import eidolons.swing.generic.services.dialog.DialogMaster;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
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
