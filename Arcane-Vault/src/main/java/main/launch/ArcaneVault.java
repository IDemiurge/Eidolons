package main.launch;

import eidolons.content.DC_ContentValsManager;
import eidolons.game.Simulation;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.system.content.ContentGenerator;
import eidolons.system.content.PlaceholderGenerator;
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
import main.simulation.SimulationManager;
import main.system.auxiliary.StringMaster;
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

    public static final String ICON_PATH = "UI/Forge4.png";
    public static final int AE_WIDTH = 500;
    public static final int AE_HEIGHT = 800;
    public static final int WIDTH = 1680;
    public static final int HEIGHT = 999;
    public static final int TREE_WIDTH = 415;
    public static final int TREE_HEIGHT = HEIGHT * 11 / 12;
    public static final int TABLE_WIDTH = (WIDTH - TREE_WIDTH) / 2;
    public static final int TABLE_HEIGHT = TREE_HEIGHT * 19 / 20;
    public final static String presetTypes =
            "units;bf obj;chars;party;missions;scenarios;" +
            "abils;spells;skills;weapons;armor;items;buffs;classes;perks;lord;actions;" ;

    private static final boolean ENABLE_ITEM_GENERATION = true;
    // public final static boolean defaultTypesGenerateOn = false;
    public static boolean selectiveInit = true;
    public static boolean selectiveLaunch = true;
    static MainBuilder mainBuilder;
    private static boolean testMode = false;
    private static String title = "Arcane Vault";
    private static JFrame window;
    private static final Dimension size = new Dimension(WIDTH, HEIGHT);
    private static boolean dirty = false;
    private static boolean macroMode;
    private static DC_Game microGame;
    private static final boolean showTime = true;
    private static ObjType previousSelectedType;
    private static ObjType selectedType;
    private static boolean simulationOn = false;
    private static boolean colorsInverted = true;
    private static WorkspaceManager workspaceManager;
    private static boolean altPressed;
    private static List<ObjType> selectedTypes;
    private static List<TabBuilder> additionalTrees;
    private static String types;
    private static ContentValsManager contentValsManager;
    private static boolean dialogueMode = true;

    public static void main(String[] args) {
        CoreEngine.setSwingOn(true);
        CoreEngine.setArcaneVault(true);
        CoreEngine.setGraphicsOff(true);
        GuiManager.init();
        new MuseCore().init();

        if (args.length > 0) {
            args = args[0].split(";");
        }
        if (args.length > 0) {
            selectiveLaunch = false;
            if (args.length > 1) {
                setMacroMode(true);
            } else {
                types = presetTypes;
                CoreEngine.setSelectivelyReadTypes(types);
            }
        }

        if (selectiveLaunch) {
         types=AV_Utils.selectiveLaunch();
        }

        CoreEngine.setReflectionMapDisabled(!types.contains("abils"));

        ItemGenerator.setGenerationOn(!ENABLE_ITEM_GENERATION);
        LogMaster.PERFORMANCE_DEBUG_ON = showTime;

        //TODO BANNER!
        log(3, "Welcome to Arcane Vault! \nBrace yourself to face the darkest mysteries of Edalar...");

        initialize();
        AV_Utils.launched();

        mainBuilder = new MainBuilder();
        mainBuilder.setKeyListener(new AV_KeyListener(getGame()));
        showAndCreateGUI();

    }

    private static String getLastTypesFilePath() {
        return PathFinder.getPrefsPath() + "AV Last Types Selection.txt";
    }

    private static void initialize() {
        CoreEngine.setArcaneVault(true);
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
        presetTypes(ArcaneVault.presetTypes),
        actions("actions;spells;buffs;abils;"), skills("skills;classes;buffs;abils;perks;actions;spells;"),
        units("chars;units;deities;dungeons;factions;"), items("weapons;armor;actions;"),
        ;
        String types;

        WORKSPACE_TEMPLATE(String types) {
            this.types = types;
        }

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
