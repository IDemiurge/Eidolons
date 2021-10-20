package main.launch;

import eidolons.system.utils.content.ContentGenerator;
import eidolons.system.utils.TypeStringProcessor;
import eidolons.system.utils.file.ResourceMaster;
import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.gui.components.tree.AV_Tree;
import main.handlers.mod.AvSaveHandler;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.util.DialogMaster;
import main.utilities.workspace.WorkspaceManager;
import main.v2_0.AV2;

import java.util.List;

public class AV_Utils {
    private static final boolean artGen = false;
    private static final boolean imgPathUpdate = false;
    private static final String[] LAUNCH_OPTIONS = {"Last", "Selective", "Selective Custom",
            "Full", "Battlecraft", "Arcane Tower",};
    private static final boolean workspaceLaunch = false;

    public static boolean isTypeRead(DC_TYPE units) {
        return CoreEngine.getSelectivelyReadTypes().contains(units.getName());
    }
    public static void backendLoaded() {
        //TODO generify
        if (ArcaneVault.getTypes().contains("encounters")) {
            //            String input = DialogMaster.inputText("Input 'json' (baseType = name:val;... || baseType2 = ...)");
            String base = DialogMaster.inputText("base Type name");
            //            JsonToType.convert(input, DC_TYPE.ENCOUNTERS);
            if (!StringMaster.isEmpty(base)) {
                TypeStringProcessor.convertAlt(base, FileManager.readFile
                        (PathFinder.getTYPES_PATH() + "sources/encounters.txt"), DC_TYPE.ENCOUNTERS);
            }
        }

        if (ArcaneVault.isMacroMode()) {
            ContentGenerator.generatePlaces();
        }

        if (artGen) {
            // ResourceMaster.createArtFolders(types);
            ResourceMaster.createUpdatedArtDirectory();
            // ModelManager.saveAll();
            // return;
        }
        if (imgPathUpdate) {
            ResourceMaster.updateImagePaths();
        }
        ContentGenerator.updateImagePathsForJpg_Png();
    }

    public static String selectiveLaunch() {
        int init = DialogMaster.optionChoice("Launch Options", LAUNCH_OPTIONS);
        if (init == -1) {
            return null;
        }
        WorkspaceManager.ADD_WORKSPACE_TAB_ON_INIT = workspaceLaunch;
        String types = null;
        boolean selectiveInit = !"Full".equals(LAUNCH_OPTIONS[init]);
        if (selectiveInit) {
            types = ArcaneVault.presetTypes;
            switch (LAUNCH_OPTIONS[init]) {
                case "Battlecraft":
                    List<DC_TYPE> enumList = new EnumMaster<DC_TYPE>()
                            .getEnumList(DC_TYPE.class);
                    for (DC_TYPE sub : DC_TYPE.values()) {
                        if (sub.isNonBattlecraft() || sub.isOmitted()) {
                            enumList.remove(sub);
                        }
                    }
                    types = ContainerUtils.constructStringContainer(enumList);
                    break;
                case "Last":
                    types = FileManager.readFile(getLastTypesFilePath());
                    break;
                case "Workspace":
                    break;
                case "Selective Custom":
                    types = ListChooser.chooseEnum(DC_TYPE.class, ListChooser.SELECTION_MODE.MULTIPLE);
                    try {
                        FileManager.write(types, getLastTypesFilePath());
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

                    break;
                case "Selective":
                    init = DialogMaster.optionChoice("Selective Templates", ArcaneVault.WORKSPACE_TEMPLATE
                            .values());
                    if (init == -1) {
                        return null;
                    }
                    ArcaneVault.WORKSPACE_TEMPLATE template = ArcaneVault.WORKSPACE_TEMPLATE.values()[init];
                    types = template.getTypes();
                    break;
            }

            CoreEngine.setSelectivelyReadTypes(types);
        } else {
            AV_Tree.setFullNodeStructureOn(true);
        }
        return types;
    }
    private static String getLastTypesFilePath() {
        return PathFinder.getPrefsPath() + "AV Last Types Selection.txt";
    }
}
