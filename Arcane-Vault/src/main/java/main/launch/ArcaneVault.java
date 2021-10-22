package main.launch;

import eidolons.content.DC_ContentValsManager;
import eidolons.game.Simulation;
import eidolons.game.core.game.DC_Game;
import eidolons.entity.item.handlers.ItemGenerator;
import eidolons.system.utils.content.ContentGenerator;
import eidolons.system.utils.XmlCleaner;
import eidolons.system.utils.content.PlaceholderGenerator;
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
import main.handlers.types.SimulationHandler;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.utilities.workspace.WorkspaceManager;
import main.v2_0.AV2;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public class ArcaneVault {

    private static String title = "Arcane Vault";
    static MainBuilder mainBuilder;
    private static JFrame window;
    private static final Dimension size = new Dimension(AvConsts.WIDTH, AvConsts.HEIGHT);

    private static final boolean ENABLE_ITEM_GENERATION = true;
    public static boolean selectiveInit = true;
    public static boolean selectiveLaunch = true;
    public final static String presetTypes =
            "units;bf obj;chars;party;missions;scenarios;" +
                    "abils;spells;skills;weapons;armor;items;buffs;classes;perks;lord;actions;";
    private static String types;
    private static ObjType previousSelectedType;
    private static ObjType selectedType;
    private static List<ObjType> selectedTypes;
    private static List<TabBuilder> additionalTrees;

    private static DC_Game game;
    private static ContentValsManager contentValsManager;

    public static void launchFromLE() {
        //TODO
    }
        public static void main(String[] args) {
        CoreEngine.setSwingOn(true);
        CoreEngine.setArcaneVault(true);
        CoreEngine.setGraphicsOff(true);
        GuiManager.init();

        if (args.length > 0) {
            args = args[0].split(";");
        }
        if (args.length > 0) {
            selectiveLaunch = false;
            if (args.length > 0) {
                setMacroMode(true);
            } else {
                types = presetTypes;
                CoreEngine.setSelectivelyReadTypes(types);
            }
        }

        if (selectiveLaunch) {
            types = AV_Utils.selectiveLaunch();
            CoreEngine.setReflectionMapDisabled(!types.contains("abils"));
        } else
            types = "";

        ItemGenerator.setGenerationOn(ENABLE_ITEM_GENERATION);
        LogMaster.PERFORMANCE_DEBUG_ON = AvFlags.showTime;

        //TODO BANNER!
        log(3, "Welcome to Arcane Vault! \nBrace yourself to face the darkest mysteries of Edalar...");

        initialize();
        AV_Utils.backendLoaded();
        mainBuilder = new MainBuilder();
        mainBuilder.setKeyListener(AV2.getManager().getKeyHandler());
        createWindow();

    }

    private static String getLastTypesFilePath() {
        return PathFinder.getPrefsPath() + "AV Last Types Selection.txt";
    }

    private static void initialize() {
        CoreEngine.setArcaneVault(true);
        getContentValsManager().init();
        AV_DataManager.init();

        CoreEngine.systemInit();
        CoreEngine.dataInit(AvFlags.macroMode);

        if (XML_Reader.getTypeMaps().keySet().size() + 3 < DC_TYPE.values().length) {
            AvFlags.testMode = true;
        }
        // if (!testMode)
        Simulation.init(AvFlags.testMode);
        game = Simulation.getGame();
        AV2.init();

        if (!AvFlags.testMode) {
            SimulationHandler.initSim();
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
        return game;

    }

    private static void createWindow() {
        title += ((AvFlags.macroMode) ? " (macro)" : " (micro)");
        window = new JFrame(title);
        window.setLayout(new MigLayout("insets 0 0 0 0"));
        window.setSize(size);
        // window.setUndecorated(true);
        window.add(mainBuilder.build(), "pos 0 0");
        mainBuilder.refresh();
        window.setVisible(true);
        window.addWindowListener(new AV_WindowListener(window));
        setArcaneVaultIcon();

        AV2.getManager().setMainBuilder(mainBuilder);
        AV2.getManager().loaded();
    }

    public static MainBuilder getMainBuilder() {
        return mainBuilder;
    }

    private static void setArcaneVaultIcon() {
        ImageIcon img = ImageManager.getIcon(AvConsts.ICON_PATH);
        if (AvFlags.macroMode) {
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
        if (AvFlags.macroMode) {
            MACRO_OBJ_TYPES.getType(getMainBuilder().getSelectedTabName());
        }
        return ContentValsManager.getOBJ_TYPE(getMainBuilder().getSelectedTabName());
    }

    public static boolean isDirty() {
        return AvFlags.dirty;
    }

    public static void setDirty(boolean dirty) {
        if (window == null) {
            return;
        }
        AvFlags.dirty = dirty;
        if (dirty) {
            window.setTitle(title+ " v" + CoreEngine.xmlBuildId + "*");
        } else {
            window.setTitle(title+ " v" + CoreEngine.xmlBuildId);
        }
    }
    public static void resetTitle() {
        getWindow().setTitle(title + " v" + CoreEngine.VERSION);
    }

    public static boolean isMacroMode() {
        return AvFlags.macroMode;
    }

    public static void setMacroMode(boolean macroMode) {
        AvFlags.macroMode = macroMode;
    }

    public static boolean isSimulationOn() {
        return AvFlags.simulationOn;
    }

    public static void setSimulationOn(boolean simulationOn) {
        AvFlags.simulationOn = simulationOn;
    }

    public static boolean isColorsInverted() {
        return AvFlags.colorsInverted;
    }

    public static void setColorsInverted(boolean b) {
        AvFlags.colorsInverted = b;
    }

    public static WorkspaceManager getWorkspaceManager() {
        return AV2.getWorkspaceManager();
    }

    public static boolean isAltPressed() {
        return AvFlags.altPressed;
    }

    public static void setAltPressed(boolean altPressed) {
        AvFlags.altPressed = altPressed;
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
        return AvFlags.dialogueMode;
    }

    public static void setDialogueMode(boolean dialogueMode) {
        AvFlags.dialogueMode = dialogueMode;
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
            return StringMaster.format(super.toString());
        }

        public String getTypes() {
            return types;
        }

        public void setTypes(String types) {
            this.types = types;
        }

    }

}
