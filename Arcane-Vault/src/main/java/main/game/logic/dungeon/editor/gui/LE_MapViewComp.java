package main.game.logic.dungeon.editor.gui;

import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tabs.TabChangeListener;
import main.game.logic.dungeon.editor.Level;
import main.game.logic.dungeon.editor.LevelEditor;
import main.game.logic.dungeon.editor.Mission;
import main.game.module.dungeoncrawl.dungeon.minimap.MiniGrid;
import main.game.module.dungeoncrawl.dungeon.minimap.Minimap;
import main.swing.components.obj.BfGridComp;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.G_Panel;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.datatypes.DequeImpl;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static main.game.logic.dungeon.editor.gui.LE_MainPanel.isInfoPanelOn;

public class LE_MapViewComp extends G_Panel implements TabChangeListener {
    public static final Dimension SIZE = new Dimension((int) (isInfoPanelOn() ?
                GuiManager.DEF_DIMENSION_HD.getWidth() - 50 :
        GuiManager.DEF_DIMENSION_HD.getWidth() - 32), 980);
    private static boolean minimapMode = false;
    Level currentLevel;
    Minimap map;
    HC_TabPanel tabs;
    WrappedTextComp infoComp = new WrappedTextComp(null);
    private Mission mission;
    private Map<Level, Minimap> maps = new HashMap<>();
    private DequeImpl<Level> levels;
    private LE_MainPanel mainPanel;
    private BfGridComp grid;
    private Map<Level, BfGridComp> grids = new HashMap<>();

    public LE_MapViewComp(Mission mission, LE_MainPanel mainPanel) {
        this(mission, new DequeImpl<>(), mainPanel);
    }

    public LE_MapViewComp(Mission mission, Collection<Level> list, LE_MainPanel mainPanel) {
        this.mission = mission;
        this.mainPanel = mainPanel;
        if (mission != null) {
            levels = mission.getLevels();
        } else {
            levels = new DequeImpl<>(list);
        }
        tabs = new HC_TabPanel();
        tabs.setPageSize(10);

        infoComp.setWrapText(true);

        infoComp.setDefaultFont(FontMaster.getFont(FONT.AVQ, 16, Font.PLAIN));
        infoComp.setDefaultSize(new Dimension(HC_TabPanel.TAB_DEFAULT.getWidth() * 3,
                HC_TabPanel.TAB_DEFAULT.getHeight() * 2));

        initTabs();
        tabs.setChangeListener(this);
        add(tabs);
        panelSize = SIZE;
        tabs.setOpaque(false);
        setOpaque(false);
        // new LE_MouseMaster();
    }

    @Override
    public void paint(Graphics g) {
//        pos 960-" +
//        grid.getCellsX()*grid.getCellWidth() +
//                tabs.getCurrentComp().

//        grid.getPanel().setBounds(960-grid.getCellsX()*64, 0, grid.getPanel().getWidth(), grid.getPanel().getHeight());
        tabs.getTabPanel().setLocation(400,0);
        super.paint(g);
    }

    public static boolean isMinimapMode() {
        return minimapMode;
    }

    public static void setMinimapMode(boolean minimapMode) {
        LE_MapViewComp.minimapMode = minimapMode;
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    private void initTabs() {
        DequeImpl<Level> levelsToAdd = new DequeImpl<>(levels);
        for (Level level : levelsToAdd) {
            addLevel(level);
        }
    }

    public void removeLevel(Level level) {
        int index = levels.indexOf(level);
        levels.remove(level);
        tabs.removeTab(index);
        int newIndex = tabs.getIndex();
        // tabs.getSelectedTabComponent()
        if (newIndex == index) {
            newIndex = (newIndex <= 0) ? ++newIndex : --newIndex;
            tabs.select(newIndex);
        }
    }

    public void addLevel(Level level) {
        if (mission == null) {
            levels.add(level);
        } else {
            mission.addLevel(level);
        }
        G_Panel panel = new G_Panel();
        panel.setKeyManager(mainPanel.getKeyManager());
        panel.addKeyListener(mainPanel.getKeyManager());
        panel.setPanelSize(SIZE);
        tabs.addTab(
                level.getName()==null ? tabs.getTabs().size()+"" :
                level.getName(), level.getDungeon().getImagePath(), panel);
        // TODO support tab sort/swap


        try {
            tabs.selectLast();
            // tabSelected(tabs.getTabs().size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        // removeAll();
        // map = maps.getOrCreate(currentLevel);
        // if (map == null) {
        // map = new Minimap(true, currentLevel.getDungeon());
        // map.init();
        // maps.put(currentLevel, map);
        // } else if (!map.isInitialized())
        // map.init();

        // tabs.getCurrentComp().add(map.getComp(), "pos 0 0");
        // revalidate();
        if (map != null) {
            getMinigrid().refresh();
        }
        super.refresh();

        if (LevelEditor.getActionStatusTooltip() == null) {
            infoComp.setText("");
        } else {
            infoComp.setText(LevelEditor.getActionStatusTooltip());
        }
    }

    public WrappedTextComp getInfoComp() {
        return infoComp;
    }

    public MiniGrid getMinigrid() {
        return map.getGrid();
    }

    @Override
    public void tabSelected(int index) {
        if (mission == null) {
            currentLevel = levels.get(index);
        } else {
            currentLevel = mission.getLevels().get(index);
        }

        LevelEditor.getMainPanel().setCurrentLevel(currentLevel);

        LevelEditor.getMainPanel().activateLevel(currentLevel);
        tabs.getCurrentComp().requestFocusInWindow();

        if (isMinimapMode()) {
            map = maps.get(currentLevel);
            grid = null;
        } else {
            grid = grids.get(currentLevel);
            map = null;
        }
        if (map == null && grid == null) {

            if (isMinimapMode()) {
                map = currentLevel.getDungeon().getMinimap();
                map.setCustomMouseListener(LevelEditor.getMouseMaster());
                map.setMouseWheelListener(LevelEditor.getMouseMaster());
                map.init();
                maps.put(currentLevel, map);
            } else {
                grid = new BfGridComp(LevelEditor.getSimulation(), SIZE.width, SIZE.height,
                        getZoom());
                grid.getPanel().addMouseListener(LevelEditor.getMouseMaster());
                grids.put(currentLevel, grid);
            }

            currentLevel.setInitialized(true);
        } else {
            // if (map != null) {
            // }
            // if (grid != null) {
            // }
            // if (!map.isInitialized()) {
            // map.init();
            // }
        }
        // grid.refresh();
        if (tabs.getCurrentComp().getComponentCount() > 0) {
            if (!isMinimapMode()) {
                grid.refresh();
            }
            return;
        }
        if (isMinimapMode()) {
            tabs.getCurrentComp().add(map.getComp(), "pos 0 0");
        } else {
            grid.refresh();
            tabs.getCurrentComp().add(grid.getPanel());
//            , "id grid, pos 660-" +
//                    grid.getCellsX()*grid.getCellWidth()/4 +
//                    " 0");
//            "pos 0 0 1920 1080, w 1920");
        }
    }
    private int getZoom() {
        return MathMaster.getMinMax(75 - 3 / 2 * Math.max(getCurrentLevel().getDungeon()
                .getCellsX(), getCurrentLevel().getDungeon().getCellsY()), 25, 100);
    }

    public HC_TabPanel getTabs() {
        return tabs;
    }

    @Override
    public void tabSelected(String name) {
        // TODO Auto-generated method stub

    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Mission getMission() {
        return mission;
    }

    public Minimap getMap() {
        return map;
    }

    public Map<Level, Minimap> getMaps() {
        return maps;
    }

    public DequeImpl<Level> getLevels() {
        return levels;
    }

    public LE_MainPanel getMainPanel() {
        return mainPanel;
    }

    public BfGridComp getGrid() {
        return grid;
    }

    public Map<Level, BfGridComp> getGrids() {
        return grids;
    }

}
