package main.launch;

import main.AV_DataManager;
import main.client.cc.CharacterCreator;
import main.client.cc.gui.neo.tree.view.HT_View;
import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Simulation;
import main.content.*;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.logic.arena.UnitGroupMaster;
import main.gui.builders.MainBuilder;
import main.gui.builders.TabBuilder;
import main.gui.components.controls.AV_ButtonPanel;
import main.gui.components.controls.ModelManager;
import main.gui.components.tree.AV_Tree;
import main.simulation.SimulationManager;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.*;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.utilities.res.ResourceMaster;
import main.utilities.hotkeys.AV_KeyListener;
import main.utilities.workspace.WorkspaceManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

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
    private static final boolean ENABLE_ITEM_GENERATION = true;
    private static final String[] LAUNCH_OPTIONS = {"Last", "Selective", "Selective Custom",
            "Full", "Battlecraft", "Arcane Tower",};
    public final static String presetTypes = "abils;spells;skills;"
     + "weapons;armor;actions;" + "";
    private static final String actions = "actions;spells;buffs;abils;";
    private static final String skills = "skills;classes;buffs;abils;";
    private static final String units = "chars;units;deities;dungeons;factions;";
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
    private static boolean macroMode = false;
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
    private static ContentManager contentManager;
    private static boolean artGen = false;
    private static boolean workspaceLaunch = false;
    private static boolean imgPathUpdate= false;

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
        CoreEngine.setArcaneVault(true);

        GuiManager.init();

        if (selectiveLaunch) {

            int init = DialogMaster.optionChoice("Launch Options", LAUNCH_OPTIONS);
            if (init == -1) {
                return;
            }
            HT_View.setWorkspaceLaunch(workspaceLaunch);
            WorkspaceManager.ADD_WORKSPACE_TAB_ON_INIT = workspaceLaunch;

            selectiveInit = !"Full".equals(LAUNCH_OPTIONS[init]);
            if (selectiveInit) {

                types = presetTypes;
                switch (LAUNCH_OPTIONS[init]) {
                    case "Battlecraft":
                        List<DC_TYPE> enumList = new EnumMaster<DC_TYPE>()
                                .getEnumList(DC_TYPE.class);
                        for (DC_TYPE sub : DC_TYPE.values()) {
                            if (sub.isNonBattlecraft()) {
                                enumList.remove(sub);
                            }
                        }
                        types = StringMaster.constructStringContainer(enumList);
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
                        FileManager.write(types, getLastTypesFilePath());
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
        ItemGenerator.setGenerationOn(!ENABLE_ITEM_GENERATION);
        LogMaster.PERFORMANCE_DEBUG_ON = showTime;
        if (args != null) {
            if (args.length > 0) {
                setMacroMode(true);
                if (args.length > 1) {
                    worldEditAutoInit = true;
                }
            }
        }

        LogMaster
                .log(3,
                        "Welcome to Arcane Vault! \nBrace yourself to face the darkest mysteries of Edalar...");
        initialize();

        if (artGen) {
            // ResourceMaster.createArtFolders(types);
            ResourceMaster.   createUpdatedArtDirectory();
            // ModelManager.saveAll();
             // return;
        }
        if (imgPathUpdate) {
            ResourceMaster. updateImagePaths();

        }
        mainBuilder = new MainBuilder();
        mainBuilder.setKeyListener(new AV_KeyListener(getGame()));
        if (!isCustomLaunch()) {
            if (XML_Reader.getTypeMaps().keySet().contains(DC_TYPE.FACTIONS.getName())) {
                UnitGroupMaster.modifyFactions();
            }
        }
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
        getContentManager().init();
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
        CharacterCreator.setAV(true);
    }

    public static Game getGame() {
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
            img = ImageManager.getIcon("UI\\" + "PentagramX3" + ".png");
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
        return ContentManager.getOBJ_TYPE(getMainBuilder().getSelectedTabName());
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
            additionalTrees = new LinkedList<>();
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

    public static ContentManager getContentManager() {
        if (contentManager == null) {
            contentManager = new DC_ContentManager();
        }
        return contentManager;
    }

    public static void setContentManager(ContentManager contentManager) {
        ArcaneVault.contentManager = contentManager;
    }

    public static JFrame getWindow() {
        return window;
    }

    public enum WORKSPACE_TEMPLATE {
        presetTypes, actions, skills, units, items,;
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
