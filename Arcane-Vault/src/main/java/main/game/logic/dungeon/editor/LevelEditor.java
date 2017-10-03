package main.game.logic.dungeon.editor;

import main.game.battlecraft.DC_Engine;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.bf.Coordinates;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.logic.dungeon.editor.gui.LE_MainPanel;
import main.swing.SwingMaster;
import main.swing.components.obj.BfGridComp;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.sound.Player;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.*;

public class LevelEditor {
    private static final String title = "Eidolons Rpg, Level Editor";
    private static final String ICON_PATH = "UI\\Dungeon Crawl.png";
    private static final boolean testMode = false;
    private static final boolean loadWorkspace = false;
    public static boolean DEBUG_ON = true;
    // load workspace control!
    public static String testMission = "Twilight Church";
    public static String testLevel = "Arcane Tower";
    static int cachedStepsLimit = 10;
    /*
     * Mission editing -> xml template to use ?
	 *  multiple DC_Game instances ?
	 *  either way, it's gonna be necessary to save/load dc_games!
	 */
    private static LE_Simulation baseSimulation;
    private static LE_MainPanel mainPanel;
    private static JFrame window;
    private static LE_ObjMaster objMaster;
    private static LE_MouseMaster mouseMaster;
    private static LE_MapMaster mapMaster;
    private static boolean mouseAddMode;
    private static Map<Level, LE_Simulation> simulationMap = new HashMap<>();
    private static boolean mouseInfoMode;
    private static Map<Level, Stack<Level>> undoCache = new HashMap<>();
    private static boolean cachingOff = true;
    private static String actionStatusTooltip;

    // private static LE_DataMaster dataMaster;

    public static void clearAllCaches() {

    }

    public static void clearCache() {

    }

    public static void cache() {
        cache(true);
    }

    public static void cache(boolean newThread) {
        if (cachingOff) {
            return;
        }
        if (newThread) {
            new Thread(new Runnable() {
                public void run() {
                    cacheLevel();
                }
            }).start();
        } else {
            cacheLevel();
        }
    }

    private static void cacheLevel() {
        Stack<Level> stack = undoCache.get(getCurrentLevel());
        if (!ListMaster.isNotEmpty(stack)) {
            stack = new Stack<>();
            undoCache.put(getCurrentLevel(), stack);
        }
        if (stack.size() > cachedStepsLimit) {
            stack.remove(stack.size() - 1);
        }
        stack.push(getCurrentLevel().getCopy());
    }

    public static void stepBack() {
        Stack<Level> stack = undoCache.get(getCurrentLevel());
        if (ListMaster.isNotEmpty(stack)) {
            Level prev = stack.pop();
            if (prev != null) {
                getCurrentLevel().copyFrom(prev);
                getMainPanel().getMapViewComp().getMinigrid().getMap().init();
                getMainPanel().refresh();
                getMainPanel().getPlanPanel().getTreePanel().initTree();
                getMainPanel().getPlanPanel().resetTree();
                LogMaster.log(1, getSimulation().toString());
                simulationMap.put(getCurrentLevel(), simulationMap.get(prev));
                LogMaster.log(1, getSimulation().toString());
            }
        }

    }

    public static void main(String[] args) {
        Player.setSwitcher(false);
        DC_Engine.jarInit();
        SwingMaster.DEBUG_ON = false;
        DC_Engine.systemInit();
        initFullData();
        DC_Engine.microInitialization( );
        // WaitMaster.unmarkAsComplete(WAIT_OPERATIONS.READING_DONE);
        // XML_Reader.readTypes(true);
        CoreEngine.setLevelEditor(true);
        // simulation = new LE_Simulation();
        objMaster = new LE_ObjMaster();
        mouseMaster = new LE_MouseMaster();
        mapMaster = new LE_MapMaster();
        // dataMaster = new LE_DataMaster();
        LE_DataMaster.initDefaultWorkspaces();
        // initKeyManager();
        VisionManager.setVisionHacked(true);
        initGui();
        if (loadWorkspace) {
            LE_DataMaster.initMissionsCustomWorkspace();
        }
        LE_DataMaster.resetMissionWorkspace();
    }

    private static void initFullData() {
        CoreEngine.setMenuScope(false);
        DC_Engine.dataInit(   );
    }

    public static Level newLevel(String baseDungeonType, String data, boolean empty) {
        Level level = new Level(baseDungeonType, getCurrentMission(), data, empty);
        level.init();
        mainPanel.newLevel(level);
        return level;
    }

    private static void initGui() {
        mainPanel = new LE_MainPanel();
        // newMission(testMission);
        mainPanel.init();

        window = new JFrame(title);
        window.setIconImage(ImageManager.getImage(ICON_PATH));
        window.setLayout(new MigLayout());
        window.setUndecorated(true);
        window.setSize(GuiManager.DEF_DIMENSION);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(mainPanel, "pos 0 0");

        if (isFullscreen()) {
            GuiManager.setWindowToFullscreen(window);
        } else {
            window.setVisible(true);
        }
    }

    private static boolean isFullscreen() {
        return false;
    }

    public static void newMission() {
        String name = ListChooser.chooseType(MACRO_OBJ_TYPES.MISSIONS);
        if (name == null) {
            return;
        }
        newMission(name);
    }

    public static void updateDynamicControls() {
        getMainPanel().getPlanPanel().refresh();
    }

    public static void newMission(String placeName) {
        Mission mission = new Mission(placeName);
        getMainPanel().newMission(mission);
        if (!LE_DataMaster.getMissionsWorkspace().getTypeList()
                .contains(mission.getObj().getType())) {
            LE_DataMaster.getMissionsWorkspace().addType(mission.getObj().getType());
        }
        // newLevel();
        mission.initLevels();
    }

    public static void newLevel(boolean alt) {
        String name = ListChooser.chooseType(DC_TYPE.DUNGEONS);
        if (name == null) {
            return;
        }
        // ObjType dungeonType = DataManager.getType(name, OBJ_TYPES.DUNGEONS);
        newLevel(name, null, alt);
        // if (selectedDungeon != null) {
        // }
    }

    public static String getTitle() {
        return title;
    }

    public static LE_Simulation getSimulation() {
        return getSimulation(getCurrentLevel());
    }

    public static void levelRemoved(Level level) {
        simulationMap.remove(level);
    }

    public static LE_Simulation getSimulation(Level level) {// if
        // (getCurrentMission()==null)
        LE_Simulation simulation = simulationMap.get(level);
        if (simulation == null) {
            if (simulationMap.get(null) != null) {
                simulation = simulationMap.get(null);
                simulationMap.remove(null);
            } else {
                simulation = new LE_Simulation();
                simulation.init();
            }

            simulationMap.put(level, simulation);
        }
        DC_Game.game = simulation;
        return simulation;
    }

    public static LE_MainPanel getMainPanel() {
        return mainPanel;
    }

    public static JFrame getWindow() {
        return window;
    }

    public static Mission getCurrentMission() {
        return getMainPanel().getCurrentMission();
    }

    public static Level getCurrentLevel() {
        return getMainPanel().getCurrentLevel();
    }

    public static LE_ObjMaster getObjMaster() {
        return objMaster;
    }

    public static LE_MouseMaster getMouseMaster() {
        return mouseMaster;
    }

    public static LE_MapMaster getMapMaster() {
        return mapMaster;

    }

    public static boolean isMouseAddMode() {
        return mouseAddMode;
    }

    public static void setMouseAddMode(boolean mouseAddMode) {
        LevelEditor.mouseAddMode = mouseAddMode;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static boolean isMouseInfoMode() {
        return mouseInfoMode;
    }

    public static void setMouseInfoMode(boolean mouseInfoMode) {
        LevelEditor.mouseInfoMode = mouseInfoMode;
    }

    public static boolean isLevelSelected() {
        return getMainPanel().getInfoPanel().getSelectedType().getOBJ_TYPE_ENUM() == DC_TYPE.DUNGEONS;
    }

    public static boolean isMissionSelected() {
        return getMainPanel().getInfoPanel().getSelectedType().getOBJ_TYPE_ENUM() == MACRO_OBJ_TYPES.PLACE;
    }

    public static ObjType checkSubstitute(ObjType type) {
        if (type.getOBJ_TYPE_ENUM() != DC_TYPE.DUNGEONS) {
            return type;
        }
        // if (type.getType() == null)
        // return new ObjType(type, true);
        ObjType customType = getCustomType(type.getName());
        if (customType == null) {
            return type;
        }
        return customType;
    }

    public static void refreshGrid() {
        getMainPanel().getMapViewComp().getGrid().refresh();
    }

    private static ObjType getCustomType(String typeName) {
        for (ObjType t : getSimulation().getCustomTypes()) {
            if (typeName.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    public static String getActionStatusTooltip() {
        return actionStatusTooltip;
    }

    public static void setActionStatusTooltip(String actionStatusTooltip) {
        LevelEditor.actionStatusTooltip = actionStatusTooltip;
        if (actionStatusTooltip == null) {
            getMainPanel().getMapViewComp().getInfoComp().setText("");
        } else {
            getMainPanel().getMapViewComp().getInfoComp().setText(
                    LevelEditor.getActionStatusTooltip());
        }
    }

    public static void highlight(List<Coordinates> list) {
        if (getGrid() != null) {

        } else {
            getCurrentLevel().getDungeon().getMinimap().getGrid().highlight(list);
        }
    }

    public static void highlight(Coordinates... list) {
        if (getGrid() != null) {
getGrid(). setStackedInfoHighlightRelativeCoordinates(list[0]);
            getGrid(). setSelectedObjSize(true,128* getGrid().getZoom()/100);
        } else {
            getCurrentLevel().getDungeon().getMinimap().getGrid().highlight(Arrays.asList(list));
        }
    }

    public static void highlightsOff() {
        if (getGrid() != null) {

        } else {
            getCurrentLevel().getDungeon().getMinimap().getGrid().highlightsOff();
        }
    }

    public static boolean isMiniGridMode() {
        return getGrid() == null;
    }

    public static BfGridComp getGrid() {
        return getMainPanel().getMapViewComp().getGrid();
    }

}
