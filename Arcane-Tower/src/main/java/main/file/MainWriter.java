package main.file;

import main.ArcaneTower;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.logic.AT_OBJ_TYPE;
import main.swing.generic.components.editors.lists.GenericListChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.FileManager;

import java.util.LinkedList;
import java.util.List;

public class MainWriter {

    private String mainContents;

    private static String getMainFolderPath() {
        return PathFinder.getXML_PATH() + "" + ArcaneTower.getAT_Path() + "\\text\\";
    }

    public static void writeToMain(String content, String folderInMain, String fileName) {
        String filepath = getMainFolderPath() + "\\" + folderInMain + "\\" + fileName;
        FileManager.write(content, filepath);

    }

    public static void cleanUpAll() {
        cleanUp(false, AT_OBJ_TYPE.values());
    }

    public static void cleanUp(boolean selective, OBJ_TYPE... types) {
        VersionMaster.saveVersionFolder();
        List<ObjType> toRemove = new LinkedList<>();
        cleanup:
        for (OBJ_TYPE T : types) {
            int option = DialogMaster.optionChoice("What to do with " + T.getName(),
                    "Select Retain", "Select Remove", "Skip", "End Cleanup", "Cancel"

            );
            if (option == -1) {
                return;
            }
            Boolean remove = null;
            switch (option) {
                case 0:
                    remove = false;
                    break;
                case 1:
                    remove = true;
                    break;
                case 2:
                    continue cleanup;
                case 3:
                    break cleanup;
                case 4:
                    return;
            }
            List<ObjType> selected = null;

            if (remove != null) {
                GenericListChooser.setStaticTYPE(AT_OBJ_TYPE.SCRIPT);
                selected = ListChooser.chooseTypes_(T);
            }
            // selected = ListChooser.chooseTypesAsStrings(T);
            for (ObjType t : DataManager.getTypes(T)) {
                if (remove != null) {
                    if (remove ^ selected.contains(t)) {
                        continue;
                    }
                }
                if (!ArcaneTower.isNonTest(t)) {
                    toRemove.add(t);
                }
            }
            if (!DialogMaster.confirm("Confirm removing following types:  "
                    + DataManager.toStringList(toRemove))) {
                continue;
            }
            for (ObjType t : toRemove) {
                DataManager.removeType(t);
            }
            toRemove = new LinkedList<>();
            ArcaneTower.saveAll();
        }
    }

    public void readMainAndInit() {
        // mainContents = FileManager.readFile(getMainFolderPath());
        // new StatMaster(statData);

    }
}
