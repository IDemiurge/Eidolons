package main.launch;

import eidolons.content.DC_ContentValsManager;
import eidolons.game.Simulation;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.content.ContentGenerator;
import eidolons.system.content.PlaceholderGenerator;
import eidolons.system.file.ResourceMaster;
import eidolons.system.utils.JsonToType;
import eidolons.system.utils.XmlCleaner;
import main.AV_DataManager;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.gui.builders.MainBuilder;
import main.gui.builders.TabBuilder;
import main.gui.components.controls.AV_ButtonPanel;
import main.gui.components.controls.ModelManager;
import main.gui.components.tree.AV_Tree;
import main.simulation.SimulationManager;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.utilities.hotkeys.AV_KeyListener;
import main.utilities.music.MuseCore;
import main.utilities.workspace.WorkspaceManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public class ArcaneVault {

    public static final String ICON_PATH = "UI\\" + "Forge4" +
            // "spellbook" +
            ".png";
    public final static boolean SINGLE_TAB_MODE = true;
    public final static boolean PRESENTATION_MODE = false;
    public static final int AE_WIDTH = 500;
    public static final int AE_HEIGHT = 800;
    public static final int WIDTH = 1680;
    public static final int HEIGHT = 999;
    public static final int TREE_WIDTH = 415;
    public static final int TREE_HEIGHT = HEIGHT * 11 / 12;
    public static final int TABLE_WIDTH = (WIDTH - TREE_WIDTH) / 2;
    public static final int TABLE_HEIGHT = TREE_HEIGHT * 19 / 20;
    public final static boolean defaultTypesGenerateOn = false;
    public final static String presetTypes = "units;bf obj;chars;party;missions;scenarios;" +
            "abils;spells;skills;" +
            "weapons;armor;items;" +
            "buffs;" +
            "classes;" +
            "perks;" +
            "lord;" +
            "actions;" + "";
    private static final boolean ENABLE_ITEM_GENERATION = true;
    private static final String[] LAUNCH_OPTIONS = {"Last", "Selective", "Selective Custom",
            "Full", "Battlecraft", "Arcane Tower",};
    private static final String actions = "actions;spells;buffs;abils;";
    private static final String skills = "skills;classes;buffs;abils;perks;actions;spells;";
    private static final String units = "chars;units;deities;dungeons;factions;";
    private static final String microForMacro =
            "party;scenarios;dungeons;factions;";
    private static final String items = "weapons;armor;actions;";
    public static boolean selectiveInit = true;
    public static boolean arcaneTower;
    public static boolean selectiveLaunch = true;
    public static boolean CUSTOM_LAUNCH;
    static MainBuilder mainBuilder;
    private static boolean testMode = false;
    private static String title = "Arcane Vault";
    private static JFrame window;
    private static Dimension size = new Dimension(WIDTH, HEIGHT);
    private static boolean dirty = false;
    private static boolean macroMode;
    private static DC_Game microGame;
    private static boolean showTime = true;
    private static ObjType previousSelectedType;
    private static ObjType selectedType;
    private static boolean simulationOn = false;
    private static boolean colorsInverted = true;
    private static WorkspaceManager workspaceManager;
    private static boolean altPressed;
    private static List<ObjType> selectedTypes;
    private static List<TabBuilder> additionalTrees;
    private static boolean worldEditAutoInit;
    private static WORKSPACE_TEMPLATE template;
    private static String types;
    private static ContentValsManager contentValsManager;
    private static boolean artGen = false;
    private static boolean workspaceLaunch = false;
    private static boolean imgPathUpdate = false;
    private static boolean dialogueMode = true;

    static {
        WORKSPACE_TEMPLATE.presetTypes.setTypes(presetTypes);
        // WORKSPACE_TEMPLATE.presetTypes
        // .setTypes("chars;dungeons;factions;units;deities;weapons;armor;actions;");
        WORKSPACE_TEMPLATE.actions.setTypes(actions);
        WORKSPACE_TEMPLATE.skills.setTypes(skills);
        WORKSPACE_TEMPLATE.units.setTypes(units);
        WORKSPACE_TEMPLATE.items.setTypes(items);
    }

    public static void main(String[] args) {
        CoreEngine.setSwingOn(true);
        CoreEngine.setArcaneVault(true);
        new MuseCore().init();

        if (args.length > 0) {
            args = args[0].split(";");
        }
        if (args.length > 0) {

            if (args.length > 1) {
                setMacroMode(true);
                worldEditAutoInit = true;
                types = microForMacro;
                selectiveLaunch = false;
            } else {
                selectiveLaunch = false;
                types = presetTypes;
                CoreEngine.setSelectivelyReadTypes(types);
            }
        }

        GuiManager.init();
        if (selectiveLaunch) {

            int init = DialogMaster.optionChoice("Launch Options", LAUNCH_OPTIONS);
            if (init == -1) {
                return;
            }
            WorkspaceManager.ADD_WORKSPACE_TAB_ON_INIT = workspaceLaunch;

            selectiveInit = !"Full".equals(LAUNCH_OPTIONS[init]);
            if (selectiveInit) {

                types = presetTypes;
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
                    case "Arcane Tower":
                        arcaneTower = true;
                        // XML_Reader.setCustomTypesPath(customTypesPath);
                        break;
                    case "Workspace":

                        break;
                    case "Selective Custom":
                        types = ListChooser.chooseEnum(DC_TYPE.class, SELECTION_MODE.MULTIPLE);
                        try {
                            FileManager.write(types, getLastTypesFilePath());
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }

                        break;
                    case "Selective":
                        init = DialogMaster.optionChoice("Selective Templates", WORKSPACE_TEMPLATE
                                .values());
                        if (init == -1) {
                            return;
                        }
                        template = WORKSPACE_TEMPLATE.values()[init];
                        types = template.getTypes();
                        break;
                }

                CoreEngine.setSelectivelyReadTypes(types);
            } else {
                AV_Tree.setFullNodeStructureOn(true);
            }

        }

        CoreEngine.setReflectionMapDisabled(!types.contains("abils"));


        ItemGenerator.setGenerationOn(!ENABLE_ITEM_GENERATION);
        LogMaster.PERFORMANCE_DEBUG_ON = showTime;

//TODO BANNER!
        log(3, "Welcome to Arcane Vault! \nBrace yourself to face the darkest mysteries of Edalar...");

        initialize();

        if (types.contains("encounters")) {
//            String input = DialogMaster.inputText("Input 'json' (baseType = name:val;... || baseType2 = ...)");
            String base = DialogMaster.inputText("base Type name");
//            JsonToType.convert(input, DC_TYPE.ENCOUNTERS);
            if (!StringMaster.isEmpty(base)) {
                JsonToType.convertAlt(base, FileManager.readFile
                        (PathFinder.getTYPES_PATH() + "sources/encounters.txt"), DC_TYPE.ENCOUNTERS);
            }
        }

        if (macroMode) {
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


        mainBuilder = new MainBuilder();
        mainBuilder.setKeyListener(new AV_KeyListener(getGame()));
//        if (!isCustomLaunch()) {
//            if (XML_Reader.getTypeMaps().containsKey(MACRO_OBJ_TYPES.FACTIONS.getName())) {
//                UnitGroupMaster.modifyFactions();
//            }
//        }
        // ModelManager.generateFactions();
        showAndCreateGUI();

        if (worldEditAutoInit) {
            //			WorldEditor.editDefaultCampaign();
        }

        ModelManager.startSaving();
        if (selectiveInit) {
            afterInit(template);
        }
        // CoreEngine.setWritingLogFilesOn(true);
    }

    // "bf obj";

    /*
     * 2 threads - init and gui? then when will gui request data?
     */

    private static boolean isCustomLaunch() {
        return CUSTOM_LAUNCH;
    }

    private static String getLastTypesFilePath() {
        return PathFinder.getPrefsPath() + "AV Last Types Selection.txt";
    }

    private static void afterInit(WORKSPACE_TEMPLATE template) {
        if (template != null) {
            switch (template) {
                case skills:
                    initSkillLaunch();

                    break;
            }
        }
    }

    private static void initSkillLaunch() {
        ObjType type = DataManager.getType("Controlled Engagement", DC_TYPE.SKILLS);
        getMainBuilder().getEditViewPanel().selectType(true, type);
        getMainBuilder().getButtonPanel().handleButtonClick(false, AV_ButtonPanel.NEW_TREE);
        // HC_Master.getAvTreeView().getBottomPanel().getOrCreate;
    }

    private static void initialize() {
        CoreEngine.setArcaneVault(true);
        // if (macroMode) {
        // MacroContentManager.init();
        // MacroEngine.init();
        // } else

//        XmlCleaner.setCleanReadTypes(
//                DC_TYPE.ENCOUNTERS
//        );
        getContentValsManager().init();
        AV_DataManager.init();

        CoreEngine.systemInit();
        CoreEngine.dataInit(macroMode);

        workspaceManager = new WorkspaceManager(macroMode, getGame());

        if (XML_Reader.getTypeMaps().keySet().size() + 3 < DC_TYPE.values().length) {
            testMode = true;
        }

        // if (!testMode)
        Simulation.init(testMode);
        microGame = Simulation.getGame();
        if (!testMode) {
            SimulationManager.init();
        }

        if (DataManager.isTypesRead(DC_TYPE.BF_OBJ)) {
            PlaceholderGenerator.generateForRoomCells();

            if (DataManager.isTypesRead(DC_TYPE.ITEMS))
                ContentGenerator.generateKeyObjects();
        }

        XmlCleaner.cleanTypesXml(DC_TYPE.ENCOUNTERS);
//        ContentGenerator.afterRead();

        CharacterCreator.setAV(true);
    }

    public static DC_Game getGame() {
        return microGame;

    }

    private static void showAndCreateGUI() {
        title += ((macroMode) ? " (macro)" : " (micro)");
        window = new JFrame(title);
        window.setLayout(new MigLayout("insets 0 0 0 0"));
        window.setSize(size);
        // window.setUndecorated(true);
        window.add(mainBuilder.build(), "pos 0 0");
        mainBuilder.refresh();
        window.setVisible(true);
        window.addWindowListener(new AV_WindowListener(window));
        setArcaneVaultIcon();

    }

    public static MainBuilder getMainBuilder() {
        return mainBuilder;
    }

    private static void setArcaneVaultIcon() {
        ImageIcon img = ImageManager.getIcon(ICON_PATH);
        if (macroMode) {
            img = ImageManager.getIcon("UI\\" + "spellbook" + ".png");
        }

        window.setIconImage(img.getImage());
    }

    public static ObjType getPreviousSelectedType() {
        if (previousSelectedType == null) {
            return selectedType;
        }
        return previousSelectedType;
    }

    public static void setPreviousSelectedType(ObjType previousSelectedType) {
        ArcaneVault.previousSelectedType = previousSelectedType;
    }

    // public static void selectedType() {
    // DefaultMutableTreeNode node = getMainBuilder().getSelectedNode();
    // if (node == null)
    // return null;
    // selectedType = DataManager
    // .getType((String) node.getUserObject(), getMainBuilder()
    // .getSelectedTabName());
    // }
    public static ObjType getSelectedType() {
        return selectedType;
    }

    public static void setSelectedType(ObjType selectedType) {
        if (ArcaneVault.selectedType == selectedType) {
            return;
        }
        previousSelectedType = ArcaneVault.selectedType;
        ArcaneVault.selectedType = selectedType;
    }

    // TODO macro types?
    public static OBJ_TYPE getSelectedOBJ_TYPE() {
        if (macroMode) {
            MACRO_OBJ_TYPES.getType(getMainBuilder().getSelectedTabName());
        }
        return ContentValsManager.getOBJ_TYPE(getMainBuilder().getSelectedTabName());
    }

    public static boolean isDirty() {
        return dirty;
    }

    public static void setDirty(boolean dirty) {
        if (window == null) {
            return;
        }
        ArcaneVault.dirty = dirty;
        if (dirty) {
            window.setTitle(title + "*");
        } else {
            window.setTitle(title);
        }
    }

    public static void reloadTree(TreeNode node) {
        mainBuilder.getTreeBuilder().reload(node);
        mainBuilder.getEditViewPanel().refresh();
    }

    public static boolean isMacroMode() {
        return macroMode;
    }

    public static void setMacroMode(boolean macroMode) {
        ArcaneVault.macroMode = macroMode;
    }

    public static DC_Game getMicroGame() {
        return microGame;
    }

    public static void setMicroGame(DC_Game microGame) {
        ArcaneVault.microGame = microGame;
    }

    public static boolean isSimulationOn() {
        return simulationOn;
    }

    public static void setSimulationOn(boolean simulationOn) {
        ArcaneVault.simulationOn = simulationOn;
    }

    public static boolean isColorsInverted() {
        return colorsInverted;
    }

    public static void setColorsInverted(boolean b) {
        colorsInverted = b;
    }

    public static WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

    public static boolean isAltPressed() {
        return altPressed;
    }

    public static void setAltPressed(boolean altPressed) {
        ArcaneVault.altPressed = altPressed;
    }

    public static List<ObjType> getSelectedTypes() {
        return selectedTypes;
    }

    public static void setSelectedTypes(List<ObjType> types) {
        selectedTypes = types;
    }

    public static void addTree(TabBuilder tabBuilder) {
        getAdditionalTrees().add(tabBuilder);

    }

    public static List<TabBuilder> getAdditionalTrees() {
        if (additionalTrees == null) {
            additionalTrees = new ArrayList<>();
        }
        return additionalTrees;
    }

    public static boolean isSelectiveInit() {
        return selectiveInit;
    }

    public static WORKSPACE_TEMPLATE getTemplate() {
        return template;
    }

    public static String getTypes() {
        return types;
    }

    public static ContentValsManager getContentValsManager() {
        if (contentValsManager == null) {
            contentValsManager = new DC_ContentValsManager();
        }
        return contentValsManager;
    }

    public static void setContentValsManager(ContentValsManager contentValsManager) {
        ArcaneVault.contentValsManager = contentValsManager;
    }

    public static JFrame getWindow() {
        return window;
    }

    public static boolean isDialogueMode() {
        return dialogueMode;
    }

    public static void setDialogueMode(boolean dialogueMode) {
        ArcaneVault.dialogueMode = dialogueMode;
    }

    public enum WORKSPACE_TEMPLATE {
        presetTypes, actions, skills, units, items,
        ;
        String types;

        @Override
        public String toString() {
            return StringMaster.getWellFormattedString(super.toString());
        }

        public String getTypes() {
            return types;
        }

        public void setTypes(String types) {
            this.types = types;
        }

    }

}
