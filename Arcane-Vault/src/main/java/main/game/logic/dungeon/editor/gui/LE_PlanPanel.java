package main.game.logic.dungeon.editor.gui;

import main.content.PROPS;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjAtCoordinate;
import main.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.battlecraft.logic.dungeon.location.building.MapZone;
import main.game.bf.Coordinates;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.battlecraft.logic.dungeon.location.building.MapZone;
import main.game.logic.dungeon.editor.*;
import main.game.logic.dungeon.editor.LE_MouseMaster.CONTROL_MODE;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LE_PlanPanel extends G_Panel {

    private static final DYNAMIC_CONTROL_GROUPS defaultCp = DYNAMIC_CONTROL_GROUPS.MISSION;
    DYNAMIC_CONTROL_GROUPS controlGroup;
    MapBlock activeBlock;
    MapZone activeZone;
    Map<DungeonPlan, LE_TreePlanPanel> treePanels = new HashMap<>();
    G_Panel dynamicControlPanel;
    G_Panel treeHolder;
    private LE_TreePlanPanel treePanel;
    private LE_TreePlanPanel prevTree;
    private DYNAMIC_CONTROL_GROUPS previontrolGroup;
    private ControlPanel<PLAN_CONTROLS> controlPanel;
    private ControlPanel<BLOCK_CONTROLS> blockControlPanel;
    private ControlPanel<LEVEL_CONTROLS> levelControlPanel;
    private ControlPanel<MISSION_CONTROLS> missionControlPanel;
    private ControlPanel<OBJ_CONTROLS> objControlPanel;
    private Integer n = 0;
    private String lastUnitGroupName = "group ";

    public LE_PlanPanel() {
        super(VISUALS.PLAN_PANEL_FRAME
                // PANEL_LARGE
        );
        treeHolder = new G_Panel();
        controlPanel = new ControlPanel<>(PLAN_CONTROLS.class);
        blockControlPanel = new ControlPanel<>(BLOCK_CONTROLS.class);
        levelControlPanel = new ControlPanel<>(LEVEL_CONTROLS.class);
        missionControlPanel = new ControlPanel<>(MISSION_CONTROLS.class);
        objControlPanel = new ControlPanel<>(OBJ_CONTROLS.class);
        dynamicControlPanel = new G_Panel();
        dynamicControlPanel.add(getControlPanel(defaultCp), "pos 0 0");
        resetComponents();
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    private void resetComponents() {
        int treeHeight = LE_TreePlanPanel.ROW_HEIGHT * LE_TreePlanPanel.ROWS_DISPLAYED
                // Math.min(LE_TreePlanPanel.MAX_ROWS_DISPLAYED, (treePanel.getTree()
                // .getVisibleRowCount()))
                ;
        treeHolder
                .setPanelSize(new Dimension(VISUALS.PLAN_PANEL_FRAME.getWidth() - 50, treeHeight));
        removeAll();
        add(treeHolder, "id tree, pos 60 90");
        add(new G_Panel(VISUALS.DRAGON_DIVIDER_SMALL), "id d, pos 52 tree.y2");
        add(dynamicControlPanel, "id dcp, pos 67 d.y2+4");
        add(new G_Panel(VISUALS.DRAGON_DIVIDER_SMALL), "id d2, pos 52 dcp.y2");
        add(controlPanel, "id controlPanel, pos 67 d2.y2+4");
        revalidate();
    }

    public void refresh() {
        previontrolGroup = controlGroup;
        controlGroup = DYNAMIC_CONTROL_GROUPS.MISSION;
        if (LevelEditor.isMouseInfoMode()) {
            controlGroup = DYNAMIC_CONTROL_GROUPS.OBJ;
        } else {
            if (LevelEditor.isLevelSelected()) {
                controlGroup = DYNAMIC_CONTROL_GROUPS.LEVEL;
            } else if (getSelectedBlock() != null) {
                controlGroup = DYNAMIC_CONTROL_GROUPS.BLOCK;
            }
        }
        if (previontrolGroup != controlGroup) {
            G_Panel panel = getControlPanel(controlGroup);
            dynamicControlPanel.removeAll();
            dynamicControlPanel.add(panel, "pos 0 0");
            dynamicControlPanel.revalidate();
        }

        prevTree = treePanel;
        treePanel = treePanels.get(getPlan());
        if (treePanel == null) {
            treePanel = new LE_TreePlanPanel(this, getPlan());
            treePanels.put(getPlan(), treePanel);
        }
        if (prevTree != treePanel) {
            resetTree();
        }

        // resetComponents();

        super.refresh();
    }

    public void resetTree() {
        treeHolder.removeAll();
        treeHolder.add(treePanel.getTree(), "pos 0 0");
        treeHolder.revalidate();
    }

    private ControlPanel<?> getControlPanel(DYNAMIC_CONTROL_GROUPS controlGroup) {
        ControlPanel<?> panel = null;
        switch (controlGroup) {
            case BLOCK:
                panel = blockControlPanel;
                break;
            case LEVEL:
                panel = levelControlPanel;
                break;
            case MISSION:
                panel = missionControlPanel;
                break;
            case OBJ:
                panel = objControlPanel;
                break;

        }
        return panel;
    }

    public void handleBlockControl(BLOCK_CONTROLS c) {
        MapBlock b = getSelectedBlock();
        switch (c) {
            case COPY:
                LevelEditor.getMapMaster().copyBlock(b);
                break;
            case PASTE:
                LevelEditor.getMapMaster().pasteBlock();
                break;
            case LOAD:
                LevelEditor.getMapMaster().loadBlock();
                break;
            case SAVE:
                String path = PathFinder.getMapBlockFolderPath();
                String fileName = DialogMaster.inputText("Input name", b.getRoomType() + " - "
                        + (getMission() == null ? "" : getMission().getName() + " - ")
                        + getLevel().getName());
                if (fileName.isEmpty()) {
                    return;
                } else {
                    fileName += ".xml";
                }
                XML_Writer.write(b.getXml(), path, fileName);
                break;
        }

    }

    public void handleControl(CONTROLS control, boolean alt) {
        if (control instanceof PLAN_CONTROLS) {
            handlePlanControl((PLAN_CONTROLS) control, alt);
        } else if (control instanceof BLOCK_CONTROLS) {
            handleBlockControl((BLOCK_CONTROLS) control);
        } else if (control instanceof LEVEL_CONTROLS) {
            handleLevelControl((LEVEL_CONTROLS) control, alt);
        } else if (control instanceof OBJ_CONTROLS) {
            handleObjControl((OBJ_CONTROLS) control);
        } else if (control instanceof MISSION_CONTROLS) {
            handleMissionControl((MISSION_CONTROLS) control);
        }
    }

    public void handleObjControl(OBJ_CONTROLS control) {
        Obj obj = LevelEditor.getMouseMaster().getSelectedObj();
        switch (control) {
            case GROUP:
                newGroup();
                break;
            case AI:
                LE_AiMaster.editAI(obj);

                break;
            case POSITIONING:
                if (obj instanceof Unit) {
                    LE_ObjMaster.setDirection((Unit) obj, obj.getCoordinates());
                    LE_ObjMaster.setFlip((Unit) obj, obj.getCoordinates());
                    LevelEditor.getMainPanel().getMapViewComp().getMinigrid().resetOverlayingComp(
                            obj);
                }
                break;
            case PALETTE:
                LevelEditor.getMainPanel().getPalette().checkRemoveOrAddToPalette(obj.getType());
                break;
            default:
                break;

        }

    }

    private void newGroup() {
        Coordinates coordinate = LE_MapMaster.pickCoordinate();
        if (coordinate == null) {
            return;
        }
        Coordinates coordinate2 = LE_MapMaster.pickCoordinate();
        if (coordinate2 == null) {
            return;
        }
        List<ObjAtCoordinate> group = LE_ObjMaster.newUnitGroup(coordinate, Math.abs(coordinate.x
                - coordinate2.x), Math.abs(coordinate.y - coordinate2.y), false);

        String name = DialogMaster.inputText("group's name?", lastUnitGroupName + " " + n);
        if (name == null) {
            return;
        }
        // TODO next file version?
        if (!StringMaster.contains(name, lastUnitGroupName)) {
            n = 0;
        }
        lastUnitGroupName = name.replace(" " + n, ""); // n will be +1

        if (name.contains(lastUnitGroupName + " " + (n))) {
            n++;
        }
        LE_DataMaster.saveUnitGroup(name, group, "");
    }

    public void handleMissionControl(MISSION_CONTROLS control) {
        switch (control) {
            case SUBLEVELS:
                // TODO random sublevels string
                String property = getMission().getObj().getProperty(MACRO_PROPS.RANDOM_SUBLEVELS);
                if (property.isEmpty()) {
                    int i = 0;
                    for (Level level : getMission().getLevels()) {
                        property += i + ";";
                        i++;
                    }
                }
                String data = DialogMaster
                        .inputText(
                                "Edit random sublevels - insert 'subDungeonType(rand(lvlN))' between static levels; use ' OR ' separator within block if needed and separate blocks with ';'.)",
                                property);
                getMission().getObj().setProperty(MACRO_PROPS.RANDOM_SUBLEVELS, data);
                break;
            case TRIGGERS:
                // classical triggers! By template, e.g. on enter, on open, on
                // kill ...
                // action-effects? also by template :)
                ObjectiveHelper.editTriggers(getMission().getObj());
                break;
            case OBJECTIVES:

                ObjectiveHelper.editObjectives(getMission().getObj());

                break;
            case QUESTS:
                // perhaps the same way as with objectives, but
                ObjectiveHelper.editSubObjectives(getMission().getObj());
                break;
            default:
                break;
        }
    }

    public void handleLevelControl(LEVEL_CONTROLS control, boolean alt) {
        switch (control) {
            case ENTRANCES:
                Entrance entrance = getLevel().getDungeon().getMainEntrance();
                Entrance exit = getLevel().getDungeon().getMainExit();
                DialogMaster.ask(" Entrance: "
                                + (entrance == null ? "" : entrance.getNameAndCoordinate()) + ", exit: "
                                + (exit == null ? "" : exit.getNameAndCoordinate()) + " What to do?", true,
                        // String[] options = {
                        "Set Entrance", "Set Exit", "Swap");
                Boolean result = (Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.OPTION_DIALOG);
                if (result == null) {
                    getLevel().getDungeon().setMainEntrance(exit);
                    getLevel().getDungeon().setMainExit(entrance);

                } else {
                    if (result) {
                        // swap places?
                        //
                        int index = DialogMaster.optionChoice(getLevel().getDungeon()
                                .getEntrances().toArray(), "Choose an entrance");

                    } else {

                    }
                }

                break;
            case LAYERS:
                // "Units", "Encounters", "Treasures", "Custom Props"
                newGroup();

                // result = DialogMaster.askAndWait("What?", "New", "Set",
                // "Edit");
                // generate layer...
                // if (result == null) {
                // LayerMaster.edit(LevelEditor.getCurrentLevel().getLayer());
                // }
                // LayerMaster.save(LevelEditor.getCurrentLevel().getLayer());
                break;
            case REPLACE:
                LE_ObjMaster.replace();
                break;
            case META:
                int i = DialogMaster.optionChoice("Choose meta info to set...",
                        "Player Spawn Coordinates", "Enemy Spawn Coordinates", "Level readiness",
                        "Auto-set Points");
                if (i == -1) {
                    return;
                }
                Coordinates c;
                switch (i) {
                    case 4:

                        break;
                    case 0:

                        c = LE_MapMaster.pickCoordinate();

                        if (c != null) {
                            LevelEditor.getCurrentLevel().getDungeon().setProperty(
                                    PROPS.PARTY_SPAWN_COORDINATES, c.toString(), false);
                        }
                        break;
                    case 1:
                        c = LE_MapMaster.pickCoordinate();
                        if (c == null) {
                            if (!alt) {
                                if (DialogMaster.confirm("Remove last Encounter Point?")) {
                                    LevelEditor.getCurrentLevel().getDungeon()
                                            .removeLastPartFromProperty(
                                                    PROPS.ENCOUNTER_SPAWN_POINTS);
                                }
                            } else if (DialogMaster.confirm("Clear Encounter Points?")) {
                                LevelEditor.getCurrentLevel().getDungeon().removeProperty(
                                        PROPS.ENCOUNTER_SPAWN_POINTS);
                            }
                            return;
                        }

                        if (alt) {
                            LevelEditor.getCurrentLevel().getDungeon().setProperty(
                                    PROPS.ENEMY_SPAWN_COORDINATES, c.toString(), false);
                        } else {
                            LevelEditor.getCurrentLevel().getDungeon().addOrRemoveProperty(
                                    PROPS.ENCOUNTER_SPAWN_POINTS, c.toString());
                        }
                        break;
                    case 2:
                        String text = ListChooser.chooseEnum(WORKSPACE_GROUP.class);
                        if (text != null) {
                            LevelEditor.getCurrentLevel().getDungeon().setProperty(
                                    G_PROPS.WORKSPACE_GROUP, text, false);
                        }
                        break;
                }

                break;
            default:
                break;

        }
    }

    public void handlePlanControl(PLAN_CONTROLS control, boolean alt) {
        boolean mode = false;
        switch (control) {
            case COPY:
                if (alt) {
                    while (copy()) {

                    }
                } else {
                    copy();
                }
                break;
            case EDIT:
                if (edit()) {
                    return;
                } else {
                    LE_DataMaster.edit();
                }
                // change block type
                break;
            case MOVE:
                move(alt);

                break;
            case CLEAR:
                if (LevelEditor.getMouseMaster().getMode() == CONTROL_MODE.CLEAR) {
                    mode = true;
                } else {
                    LevelEditor.getMouseMaster().setMode(CONTROL_MODE.CLEAR);
                }

                if (mode) {
                    while (LevelEditor.getMapMaster().clearArea()) {
                        // if () break;
                    }
                } else {
                    LevelEditor.getMapMaster().clearArea();
                }

                break;
            case FILL:
                if (LevelEditor.getMouseMaster().getMode() == CONTROL_MODE.FILL) {
                    mode = true;
                } else {
                    LevelEditor.getMouseMaster().setMode(CONTROL_MODE.FILL);
                }
                if (mode) {
                    while (LE_ObjMaster.fillArea(alt)) {
                        // if () break;

                    }
                } else {
                    LE_ObjMaster.fillArea(alt);
                    LevelEditor.getMouseMaster().setMode(null);
                }
                break;
            case REPLACE:
                LE_ObjMaster.replace();
                break;
            case ROOM:
                LevelEditor.getMapMaster().newRoom();
                treePanel.refresh();
                break;
            case MIRROR:
                LevelEditor.getObjMaster().mirror();
                break;
            // case REMOVE:
            // if (alt)
            // LevelEditor.getMouseMaster().setMode(CONTROL_MODE.REMOVE);
            // else
            // LevelEditor.getMouseMaster().setMode(null);
            // if (activeBlock != null)
            // LevelEditor.getMapMaster().removeBlock(activeBlock);
            // else if (activeZone != null)
            // LevelEditor.getMapMaster().removeZone(activeZone);
            //
            // treePanel.refresh();
            // break;

        }
        LevelEditor.getMouseMaster().setMode(null);
    }

    private boolean copy() {
        return LE_ObjMaster.copy();

    }

    private boolean move(boolean alt) {
        if (alt) {
            LE_ObjMaster.moveSelectedObj();
            return true;
        }
        LE_ObjMaster.moveObjects();

        // MapZone zone = getSelectedZone();
        // if (zone == null)
        // if (getSelectedBlock() == null)
        // return false;
        // else {
        // LevelEditor.getMapMaster().moveBlock(getSelectedBlock());
        // }
        // LevelEditor.getMapMaster().moveZone(zone);
        return true;
    }

    private boolean edit() {
        MapZone zone = getSelectedZone();
        if (zone == null) {
            return false;
        }
        // Boolean filler_size_position =
        // DialogMaster.askAndWait("What to edit for ",
        // "Filler", "Size",
        // "Position");
        // move?
        String prevFiller = zone.getFillerType();
        String filler = DialogMaster.inputText("Set new Filler Type for " + zone.getName() + "...",
                prevFiller);
        if (filler != null) {
            if (!Objects.equals(filler, prevFiller)) {
                zone.setFillerType(filler);
            }
        }

        // zone.setX1(x1);
        // zone.setName(name)

        // size -> re-fill spaces!

        List<Coordinates> coordinates = zone.getCoordinates();
        for (MapBlock b : zone.getBlocks()) {
            coordinates.removeAll(b.getCoordinates());
        }
        LevelEditor.getMapMaster().replace(prevFiller, filler, coordinates);
        return true;
    }

    public Mission getMission() {
        return LevelEditor.getCurrentMission();
    }

    public Level getLevel() {
        return LevelEditor.getCurrentLevel();
    }

    public DungeonPlan getPlan() {
        return getLevel().getDungeon().getPlan();
    }

    public MapBlock getSelectedBlock() {
        return activeBlock;
    }

    public void setSelectedBlock(MapBlock block) {
        activeBlock = block;
        if (LevelEditor.getGrid() != null) {
            // LevelEditor.getGrid().highlightsOff();
        } else {

            // try {
            // LevelEditor.getCurrentLevel().getDungeon().getMinimap().getGrid().highlightsOff();
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            if (block != null) {
                highlight(block.getCoordinates());
            }
        }
    }

    public MapZone getSelectedZone() {
        return activeZone;
    }

    public void setSelectedZone(MapZone mapZone) {
        activeZone = mapZone;
        if (LevelEditor.getCurrentLevel() != null)
        // try {
        // LevelEditor.getCurrentLevel().getDungeon().getMinimap().getGrid().highlightsOff();
        // } catch (NullPointerException e) {
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        {
            if (mapZone != null) {
                highlight(mapZone.getCoordinates());
            }
        }

    }

    private void highlight(List<Coordinates> list) {
        try {
            LevelEditor.highlight(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LevelEditor.setMouseInfoMode(false);
        LevelEditor.setMouseAddMode(false);
    }

    public LE_TreePlanPanel getTreePanel() {
        return treePanel;
    }

    public void setTreePanel(LE_TreePlanPanel treePanel) {
        this.treePanel = treePanel;
    }

    public MapBlock getActiveBlock() {
        return activeBlock;
    }

    public void setActiveBlock(MapBlock activeBlock) {
        this.activeBlock = activeBlock;
    }

    public MapZone getActiveZone() {
        return activeZone;
    }

    public void setActiveZone(MapZone activeZone) {
        this.activeZone = activeZone;
    }

    public DYNAMIC_CONTROL_GROUPS getControlGroup() {
        return controlGroup;
    }

    public void setControlGroup(DYNAMIC_CONTROL_GROUPS controlGroup) {
        this.controlGroup = controlGroup;
    }

    public DYNAMIC_CONTROL_GROUPS getPreviontrolGroup() {
        return previontrolGroup;
    }

    public void setPreviontrolGroup(DYNAMIC_CONTROL_GROUPS previontrolGroup) {
        this.previontrolGroup = previontrolGroup;
    }

    public enum DYNAMIC_CONTROL_GROUPS {
        BLOCK, OBJ, MISSION, LEVEL,
    }

    public enum OBJ_CONTROLS implements CONTROLS {
        POSITIONING, GROUP, PALETTE, AI
        // hotkeys, tooltips
    }

    public enum LEVEL_CONTROLS implements CONTROLS {
        ENTRANCES, META, REPLACE, LAYERS
    }

    public enum MISSION_CONTROLS implements CONTROLS {
        TRIGGERS, OBJECTIVES, QUESTS, SUBLEVELS,
    }

    public enum BLOCK_CONTROLS implements CONTROLS {
        SAVE, LOAD, COPY, PASTE
    }

    public enum PLAN_CONTROLS implements CONTROLS {
        FILL, CLEAR, MOVE, COPY, MIRROR, REPLACE, ROOM, EDIT
    }

    public interface CONTROLS {

    }

    public class ControlPanel<E extends CONTROLS> extends G_Panel {

        public ControlPanel(Class<E> clazz) {
            Boolean wrap = false;
            for (E c : clazz.getEnumConstants()) {
                add(getControlButton(c), wrap ? "wrap" : "");
                wrap = !wrap;
            }
        }

        public CustomButton getControlButton(final E c) {
            return new CustomButton(VISUALS.VALUE_BOX_TINY, c.toString()) {

                public void handleAltClick() {
                    new Thread(new Runnable() {
                        public void run() {
                            handleControl(c, true);
                        }
                    }).start();
                }

                public void handleClick() {
                    new Thread(new Runnable() {
                        public void run() {
                            handleControl(c, false);
                        }
                    }).start();
                }
            };
        }

    }
}
