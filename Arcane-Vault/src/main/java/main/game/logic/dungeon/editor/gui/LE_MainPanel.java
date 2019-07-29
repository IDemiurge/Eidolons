package main.game.logic.dungeon.editor.gui;

import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tabs.TabChangeListener;
import main.content.PROPS;
import main.game.logic.dungeon.editor.LE_DataMaster;
import main.game.logic.dungeon.editor.Level;
import main.game.logic.dungeon.editor.LevelEditor;
import main.game.logic.dungeon.editor.Mission;
import main.game.module.dungeoncrawl.dungeon.minimap.MiniGrid;
import main.swing.generic.components.G_Panel;
import main.system.graphics.GuiManager;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class LE_MainPanel extends G_Panel implements TabChangeListener {
    // Multiple tabs with grids?

    HC_TabPanel missionTabs;
    LE_ControlPanel cp;
    LE_InfoEditPanel ip;
    LE_Palette palette;
    JLabel background;

    private Mission currentMission;
    private Level currentLevel;
    private List<Mission> missions = new LinkedList<>();
    private List<Level> levels = new LinkedList<>(); // w/o mission
    private boolean hasLevelsTab = true;
    private LE_PlanPanel planPanel;
    private LE_KeyMaster keyMaster;

    public LE_MainPanel() {
        cp = new LE_ControlPanel();
        ip = new LE_InfoEditPanel();
        palette = new LE_Palette();
        planPanel = new LE_PlanPanel();
        background = new JLabel();
        if (LevelEditor.isTestMode()) {
            Level level = new Level(LevelEditor.testLevel, null, null, false);
            level.init();
            levels = new ListMaster<Level>().getList(level);
        } else {
            levels = LE_DataMaster.getLevelsWorkspace();
        }
        LE_MapViewComp mapViewComp = new LE_MapViewComp(null, levels, this);

        HC_Tab defaultTab = new HC_Tab("Levels", mapViewComp);
        missionTabs = new HC_TabPanel(defaultTab);
        missionTabs.setPanelSize(LE_MapViewComp.SIZE);
        missionTabs.setChangeListener(this);

        palette.refresh();

        setIgnoreRepaint(true);
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    public void removeMission(Mission mission) {
        int index = missions.indexOf(mission);
        missions.remove(mission);
        missionTabs.removeTab(index);
        int newIndex = missionTabs.getIndex();
        // tabs.getSelectedTabComponent()
        if (newIndex == index) {
            newIndex = (newIndex <= 0) ? ++newIndex : --newIndex;
            missionTabs.select(newIndex);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public void init() {
        // default tab?
        initBackground();

        keyMaster = new LE_KeyMaster(this);
        setKeyManager(keyMaster);
        add(cp, "id cp, pos 0 0");
        if (isInfoPanelOn())
            add(ip.getPanel(), "id ip, pos tabs.x2 cp.y2");
        else {
            add(ip.getPanel(), "id ip, pos 0 cp.y2");
            ip.getPanel().setVisible(true);

        }
        add(missionTabs, "id tabs, pos 0 cp.y2");
        add(palette, "id palette, pos plan.x2 770");
        add(planPanel, "id plan, pos 0 cp.y2");
        add(background);
        int i = 0;
        setComponentZOrder(palette, i);
        i++;
        setComponentZOrder(ip.getPanel(), i);
        i++;
        setComponentZOrder(cp, i);
        i++;
        setComponentZOrder(planPanel, i);
        i++;
        setComponentZOrder(missionTabs, i);
        i++;
        setComponentZOrder(background, i);

        getMapViewComp().getTabs().select(0);
        missionTabs.select(0);

        missionTabs.setOpaque(false);
        setOpaque(false);
    }

    public static final boolean isInfoPanelOn() {
        return false;
    }

    public void newMission(Mission mission) {
        LE_MapViewComp comp = new LE_MapViewComp(mission, this);
        missionTabs.addTab(mission.getName(), mission.getObj().getImagePath(), comp);

        setCurrentMission(mission);
        missions.add(mission);
        missionTabs.selectLast();
    }

    public void newLevel(Level level) {
        // TODO currentLevel.getDungeon().getIcon()
        setCurrentLevel(level);
        levels.add(level);
        // activateLevel(level);

        getMapViewComp().addLevel(level);

    }

    public void tabSelected(int index) {

        Mission mission = null;
        if (!hasLevelsTab || index > 0) {
            mission = missions.get(index - 1); // "Levels" non-mission tab
        }
        activateMission(mission);

    }

    public void activateMission(Mission mission) {
        boolean newMission = currentMission != mission;
        setCurrentMission(mission);
        LevelEditor.getSimulation().setMission(mission);
        palette.setMission(currentMission);
        ip.setMission(currentMission);

        if (currentMission != null) {
            planPanel.setSelectedBlock(null);
            planPanel.setSelectedZone(null);
            LevelEditor.setMouseInfoMode(false);
            LevelEditor.setMouseAddMode(false);
            ip.selectType(currentMission.getObj().getType());
            if (getCurrentLevel() != null) {
                if (getCurrentLevel().getDungeon().getPlan() != null) {
                    getPlanPanel().refresh();
                }
            }
        }
        if (newMission) {
            try {
                getMapViewComp().getTabs().select(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // if (mission == null)
        // activateLevel(levels.getOrCreate(0));
        // else
        // activateLevel(mission.getFirstLevel());
    }

    public void activateLevel(Level level) {
        // if (level.getMission() != null)
        setCurrentMission(level.getMission());
        setCurrentLevel(level);
        planPanel.setSelectedBlock(null);
        planPanel.setSelectedZone(null);
        ip.selectType(level.getDungeon().getType());
        LevelEditor.setMouseInfoMode(false);
        LevelEditor.setMouseAddMode(false);

        palette.setMission(currentMission);
        palette.setLevel(getCurrentLevel());
        ip.setMission(currentMission);
        ip.setLevel(getCurrentLevel());
        getMapViewComp().setCurrentLevel(getCurrentLevel());
        initBackground();
        refresh();
    }

    public void refreshGui() {
        palette.refresh();
        if (ip.getPanel().isVisible())
            ip.refresh();
        cp.refresh();
        planPanel.refresh();
    }

    public MiniGrid getMiniGrid() {
        return getMapViewComp().getMinigrid();
    }

    public void refresh() {
        refreshGui();
        // Chronos.mark("Map View refresh");
        getMapViewComp().refresh();
        // Chronos.logTimeElapsedForMark("Map View refresh");
    }

    public LE_MapViewComp getMapViewComp() {
        return (LE_MapViewComp) missionTabs.getCurrentComp();
    }

    public void setBackgroundImage(String newValue) {
        if (!ImageManager.isImage(newValue)) {
            return;
        }

        ImageIcon icon = ImageManager.getSizedIcon(newValue, new Dimension(background.getWidth(), background.getHeight()));
        if (icon != null) {
            background.setIcon(icon);
        }
    }

    private void initBackground() {

        Icon defaultIcon = getDefaultIcon();

        if (getCurrentLevel() != null) {
            if (ImageManager.isImage(getCurrentLevel().getDungeon().getProperty(
                    PROPS.MAP_BACKGROUND))) {
                ImageIcon icon = ImageManager.getSizedIcon(getCurrentLevel().getDungeon().getProperty(
                        PROPS.MAP_BACKGROUND), GuiManager.getScreenSize());
                background.setIcon(icon);
            } else {
                background.setIcon(defaultIcon);
            }
        }

    }

    private Icon getDefaultIcon() {
        return ImageManager.getIcon(ImageManager.DEFAULT_BACKGROUND);
    }

    public void tabSelected(String name) {

    }

    public Mission getCurrentMission() {
        return currentMission;
    }

    public void setCurrentMission(Mission currentMission) {
        // if (currentMission == null) {
        // this.currentMission = currentMission;
        // }
        this.currentMission = currentMission;
    }

    public LE_InfoEditPanel getInfoPanel() {
        return ip;
    }

    public LE_Palette getPalette() {
        return palette;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public LE_PlanPanel getPlanPanel() {
        return planPanel;
    }

    public void setPlanPanel(LE_PlanPanel planPanel) {
        this.planPanel = planPanel;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
        LevelEditor.getSimulation().getDungeonMaster().setDungeon(currentLevel.getDungeon());
    }

    public void toggleInfoPanel() {
        ip.getPanel().setVisible(!ip.getPanel().isVisible());
    }

    public void toggleUI() {
        Component c = getPalette();
        c.setVisible(!c.isVisible());
        c = getPlanPanel();
        c.setVisible(!c.isVisible());
        if (ip.getPanel().isVisible()) {
            ip.getPanel().setVisible(false);
        }
//        c = cp;
//        c.setVisible(!c.isVisible());
    }
}
