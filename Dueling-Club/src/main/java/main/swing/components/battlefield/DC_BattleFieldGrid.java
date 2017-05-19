package main.swing.components.battlefield;

import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.battlecraft.logic.dungeon.location.building.DC_Map;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.swing.components.obj.BfGridComp;
import main.swing.components.obj.CellComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.services.layout.LayoutInfo;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * pass down BF_GRID to this COMPONENT from Map Generator
 *
 * @author Regulus
 */
public class DC_BattleFieldGrid implements BattleFieldGrid {

    private DC_Game game;
    private DC_Map map;
    private boolean inverse = false;
    private List<Coordinates> coordinates;
    private int h;
    private int w;
    private Coordinates nextOffsetCoordinate;
    private Coordinates offsetCoordinate;
    private int manualOffsetX = 0;
    private int manualOffsetY = 0;
    private Unit activeObj;
    private Unit lastActiveObj;
    private Dungeon dungeon;
    private Obj cameraCenterObj;
    private BfGridComp gridComp;
    private Coordinates cameraCenterCoordinates;
    private Set<Obj> cells;
    private G_Panel comp;

    public DC_BattleFieldGrid(Dungeon dungeon) {
        this.dungeon = dungeon; // TODO

        this.game = dungeon.getGame();
        this.w = GuiManager.getBF_CompDisplayedCellsX();
        this.h = GuiManager.getBF_CompDisplayedCellsY();
        this.w = game.getDungeonMaster().getDungeonWrapper().getWidth();
        this.h = game.getDungeonMaster().getDungeonWrapper().getHeight();
        comp = new G_Panel();
        comp.setBackground(ColorManager.getTranslucent(ColorManager.OBSIDIAN, 10));

        gridComp = new BfGridComp(this);

        comp.add(gridComp.getPanel());
        comp.setIgnoreRepaint(true);
    }

    @Override
    public boolean isOccupied(Coordinates c) {
        // TODO cellCondition also takes into account visiblity, flyers etc...
        // this here is just about obj being present...
        for (Unit obj : gridComp.getMap().get(c).getObjects()) {
            if (obj.isPassable()) {
                continue;
            }

            return true;
        }
        return false;

    }

    public boolean isCoordinateObstructed(Coordinates c) {
        return isOccupied(c);
    }

    // @Override
    // public Map<Coordinates, CellComp> getObjCompMap() {
    // return gridComp.getMap();
    // }
    // 'merge' and
    // rename!
    public Map<Coordinates, CellComp> getCellCompMap() {
        return gridComp.getMap();
    }

    public String toString() {
        return "Grid for " + dungeon.getName() + "; Z=" + dungeon.getZ() + ";X=" + w + ";Y=" + h;
    }


    public void refresh() {
        gridComp.refresh(getOffsetX(), getOffsetY());
        gridComp.getPanel().repaint();
    }

    public void reset() {
        resetOffset();
        BfGridComp.getUnderlayMap().clear();
        BfGridComp.getOverlayMap().clear();
        resetComponents();
        gridComp.refresh();
    }

    private int getDisplayedCellsX() {
        return gridComp.getDisplayedCellsX();
    }

    private int getDisplayedCellsY() {
        return gridComp.getDisplayedCellsY();
    }

    private void resetComponents() {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        LogMaster.log(0, "resetting grid comps with offsetX = " + offsetX + ";offsetY =" + offsetY);

        for (int i = 0; i < getDisplayedCellsX(); i++) {
            for (int j = 0; j < getDisplayedCellsY(); j++) {
                int x = i + getOffsetX();
                int y = j + getOffsetY();
                Coordinates c = new Coordinates(x, y);
                List<Unit> objects = game.getObjectsOnCoordinate(getZ(), c, false, true,
                        false);
                List<Unit> overlayingObjects = new LinkedList<>(new DequeImpl(game
                        .getObjectsOnCoordinate(getZ(), c, true, true, false))
                        .getRemoveAll(objects));

                // visibility preCheck!

                CellComp comp = gridComp.getCells()[x][y];
                List<Unit> list = new LinkedList<>();
                for (Unit obj : objects) {
                    if (VisionManager.checkVisible(obj)) {
                        list.add(obj);
                    }
                }
                comp.setSizeFactor(gridComp.getZoom());
                comp.setOverlayingObjects(overlayingObjects);
                if (list.size() != 0) {
                    comp.setObjects(list);
                }
                comp.refresh();
            }
        }
        comp.refresh();
    }

    public void resetZoom() {
        gridComp.setZoom(100);
    }

    private void resetOffset() {
        if (getOffsetCoordinate() == null) {
            setOffsetCoordinate(new Coordinates(0, 0));
            if (game.getPlayer(true).getHeroObj() != null) {
                setOffsetCoordinate(game.getPlayer(true).getHeroObj().getCoordinates());
            }
        } else {
            lastActiveObj = getActiveObj();
            setActiveObj(game.getManager().getActiveObj());
            if (getActiveObj() == null) {
                return;
            }
            boolean newCoordinate = getActiveObj() != lastActiveObj;
            boolean change = false;
            if (game.isDebugMode()) {
                change = true;
            } else if (getActiveObj().getOwner().isMe()) {
                change = true;
            } else if (getGame().getVisionMaster().checkDetectedEnemy(getActiveObj())) {
                change = true;
            }
            if (change) {
                if (newCoordinate) {
                    setOffsetCoordinate(game.getManager().getActiveObj().getCoordinates());
                } else {
                    setOffsetCoordinate(getNextOffsetCoordinate());
                }
            }
        }
        // for the visual effect of actually moving somewhere with the active
        // unit on 1st refresh
        setNextOffsetCoordinate(game.getManager().getActiveObj().getCoordinates());

    }

    private int getOffset(boolean x) {
        // 6*132 = 7* x ;

        if (getOffsetCoordinate() == null) {
            // TODO initial camera
            Unit obj = getActiveObj();
            if (obj == null || !getGame().getVisionMaster().checkDetectedEnemy(obj)) {
                obj = (Unit) getGame().getPlayer(true).getHeroObj();
            }
            return obj.getCoordinates().getXorY(x);
            // return (getFullCells(x) - getDisplayedCells(x)) / 2;
        }
        int displayedCells = getDisplayedCells(x);
        int currentLevelCells = getFullCells(x);
        int offset = getCameraOffset(x, displayedCells);
        int minMax = MathMaster.getMinMax(offset, 0, currentLevelCells - displayedCells);
        if (x) {
            minMax = MathMaster.getMinMax(minMax + manualOffsetX, 0, currentLevelCells
                    - displayedCells);
        } else {
            minMax = MathMaster.getMinMax(minMax + manualOffsetY, 0, currentLevelCells
                    - displayedCells);
        }
        return minMax;

    }

    private int getCameraOffset(boolean x, int displayedCells) {
        if (cameraCenterObj != null) {
            cameraCenterCoordinates = cameraCenterObj.getCoordinates();
        }
        Coordinates coordinate = cameraCenterCoordinates;
        if (coordinate == null) {
            coordinate = getOffsetCoordinate();
        }

        int offset = (x ? coordinate.getX() : getOffsetCoordinate().getY()) - displayedCells / 2;

        if (cameraCenterObj == null) {
            if (getActiveObj().getFacing().isVertical() != x) {
                if (getActiveObj().getIntParam(PARAMS.SIGHT_RANGE) > displayedCells / 2) {
                    int additionalVisionOffset = Math.min(getActiveObj().getIntParam(
                            PARAMS.SIGHT_RANGE)
                            - displayedCells / 2, displayedCells / 2 - 1);
                    if (getActiveObj().getFacing().isCloserToZero()) {
                        additionalVisionOffset = -additionalVisionOffset;
                    }
                    offset += additionalVisionOffset;
                }
            }
        }

        return offset;
    }

    private int getFullCells(boolean x) {
        return x ? GuiManager.getCurrentLevelCellsX() : GuiManager.getCurrentLevelCellsY();
    }

    private int getDisplayedCells(boolean x) {
        return x ? GuiManager.getBF_CompDisplayedCellsX() : GuiManager.getBF_CompDisplayedCellsY();
    }

    public int getOffsetY() {
        return getOffset(false);
    }

    public int getOffsetX() {
        return getOffset(true);
    }

    public LayoutInfo getLayoutInfo(Obj obj) {
        return new LayoutInfo(obj.getX() - getOffsetX(), obj.getY() - getOffsetY());
    }

    private boolean isWithinBounds(Obj u) {
        return isWithinBounds(u.getCoordinates());
    }

    private boolean isWithinBounds(Coordinates c) {
        if (Math.abs(c.getX() - getOffsetX()) > GuiManager.getBF_CompDisplayedCellsX()) {
            return false;
        }
        return Math.abs(c.getY() - getOffsetY()) <= GuiManager.getBF_CompDisplayedCellsY();

    }

    public Boolean isOnEdgeX(Coordinates coordinates) {
        return gridComp.isOnEdgeX(coordinates);
    }

    public Boolean isOnEdgeY(Coordinates coordinates) {
        return gridComp.isOnEdgeY(coordinates);
    }


    public boolean checkPassable(Obj obj) {
        if (obj.checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.PASSABLE)) {
            return true;
        }
        return obj.checkBool(GenericEnums.STD_BOOLS.PASSABLE);
    }

    private boolean checkSetNoBorderImg(int i, int j) {
        return (i - j) % 2 != 0;
    }

    public DC_Map getMap() {
        if (map == null) {
            map = new DC_Map();
            map.setBackground(dungeon.getProperty(PROPS.MAP_BACKGROUND));
        }
        return map;
    }

    public void setMap(DC_Map map1) {
        map = map1;
    }

    @Override
    public boolean noObstaclesOnDiagonal(Coordinates c1, Coordinates c2) {
        return noObstaclesOnDiagonal(c1, c2, null);
    }

    public BfGridComp getGridComp() {
        return gridComp;
    }

    public CellComp getCompForObject(Obj obj) {
        return gridComp.getCompForObject(obj);
    }

    public Point getPointForCoordinateWithOffset(Coordinates c) {
        return gridComp.getPointForCoordinateWithOffset(c);
    }

    public Point mapToPoint(Coordinates c) {
        return gridComp.mapToPoint(c);
    }

    public Coordinates mapToCoordinate(Point point) {
        return gridComp.mapToCoordinate(point);
    }

    public CellComp getCompByPoint(Point point) {
        return gridComp.getCompByPoint(point);
    }

    @Override
    public boolean noObstaclesOnDiagonal(Coordinates c1, Coordinates c2, Obj source) {
        boolean above = PositionMaster.isAbove(c1, c2);
        boolean left = PositionMaster.isToTheLeft(c1, c2);
        int X = c1.x;
        int Y = c1.y;
        while (X != c2.x || Y != c2.y) {

            if (above) {
                Y++;
            } else {
                Y--;
            }
            if (left) {
                X++;
            } else {
                X--;
            }
            if (X == c2.x || Y == c2.y) {
                break;
            }
            Coordinates c = new Coordinates(X, Y);
            List<Unit> objects = game.getObjectsOnCoordinate(c);
            for (Unit obj : objects) {
                if (obj.isObstructing(source, game.getCellByCoordinate(c))) {
                    return false;
                }
            }

        }
        return true;

    }

    private boolean noObstacles(int xy, int xy1, int xy2, Obj source, boolean x_y) {

        int max = xy2;
        int min = xy1;
        if (xy1 > xy2) {
            max = xy1;
            min = xy2;
        }
        for (int i = min + 1; i < max; i++) {
            Coordinates c = (x_y) ? new Coordinates(xy, i) : new Coordinates(i, xy);
            List<Unit> objects = game.getObjectsOnCoordinate(c);
            for (Unit obj : objects) {
                if (obj.isObstructing(source, game.getCellByCoordinate(c))) {
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public boolean noObstaclesX(int x, int x1, int x2, Obj objComponent) {
        return noObstacles(x, x1, x2, objComponent, true);
    }

    @Override
    public boolean noObstaclesY(int y, int y1, int y2, Obj objComponent) {
        return noObstacles(y, y1, y2, objComponent, false);
    }

    @Override
    public boolean noObstaclesY(int y, int y1, int y2) {
        return noObstaclesY(y, y1, y2, null);
    }

    @Override
    public boolean noObstaclesX(int x, int x1, int x2) {
        return noObstaclesX(x, x1, x2, null);
    }

    @Override
    public void highlight(Set<Obj> set) {
        for (Obj obj : set) {
            obj.setTargetHighlighted(true);
        }
        // for (Obj obj : set) {
        // ObjComponent objC = getObjCompMap().getOrCreate(obj.getCoordinates());
        // if (objC != null)
        // if (objC.getObj() == obj) {
        // objC.setHighlighted(true);
        // continue;
        // }
        //
        // objC = getPassableObjMap().getOrCreate(obj.getCoordinates());
        //
        // if (objC != null)
        // if (objC.getObj() == obj) {
        // objC.setHighlighted(true);
        // continue;
        // }
        // objC = getOverlayingObjMap().getOrCreate(obj.getCoordinates());
        //
        // if (objC != null)
        // if (objC.getObj() == obj) {
        // objC.setHighlighted(true);
        // continue;
        // }
        //
        // objC = getCellCompMap().getOrCreate(obj.getCoordinates());
        // if (objC != null)
        // if (objC.getObj() == obj)
        // objC.setHighlighted(true);
        // }
        // for (ObjComponent objC : getObjCompMap().values()) {
        // if (set.contains(objC.getObj())) {
        // objC.setHighlighted(true);
        // }
        // }
        // for (ObjComponent objC : getCellCompMap().values()) {
        // if (set.contains(objC.getObj())) {
        // objC.setHighlighted(true);
        // }
        // }
        // for (ObjComponent objC : getPassableObjMap().values()) {
        // if (set.contains(objC.getObj())) {
        // objC.setHighlighted(true);
        // } else if (set.contains(game.getCellByCoordinate(objC.getObj()
        // .getCoordinates())))
        // objC.setHighlighted(true);
        // }
        refresh();
    }

    @Override
    public void highlightsOff() {
        for (Obj obj : game.getUnits()) {
            obj.setTargetHighlighted(false);
        }
        for (Obj obj : game.getCells()) {
            obj.setTargetHighlighted(false);
        }
        // for (ObjComponent comp : overlayingObjMap.values()) {
        // comp.setHighlighted(false);
        // }
        // for (ObjComponent comp : passableObjMap.values()) {
        // comp.setHighlighted(false);
        // }
        // for (ObjComponent comp : objCompMap.values()) {
        // comp.setHighlighted(false);
        // }
        // for (ObjComponent comp : cellCompMap.values()) {
        // comp.setHighlighted(false);
        // }

        refresh();
    }

    public boolean isInverse() {
        return inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public List<Coordinates> getCoordinatesList() {
        if (coordinates == null) {
            coordinates = new LinkedList<>();
            for (Coordinates c : gridComp.getMap().keySet()) {
                coordinates.add(c);
            }
        }
        return coordinates;
    }

    @Override
    public int getHeight() {
        return h;
    }

    @Override
    public int getWidth() {
        return w;
    }

    @Override
    public Set<Obj> getCells() {
        if (cells == null) {
            cells = new HashSet<>();
            for (CellComp comp : gridComp.getMap().values()) {
                cells.add(comp.getTerrainObj());
            }
        }
        return cells;
    }

    public Set<Obj> getCellsWithinVisionBounds() {
        Set<Obj> cells = new HashSet<>();
        for (CellComp comp : gridComp.getMap().values()) {
            if (isWithinBounds(comp.getTerrainObj())) {
                cells.add(comp.getTerrainObj());
            }
        }
        return cells;
    }

    @Override
    public Obj getCell(Coordinates coordinates) {
        if (gridComp.getMap().get(coordinates) == null) {
            return null;
        }
        return gridComp.getMap().get(coordinates).getTerrainObj();
    }

    public Obj getObj(Coordinates c) {
        return getTopObj(c);
    }

    @Override
    public Obj getTopObj(Coordinates c) {
        if (gridComp.getMap().get(c) == null) {
            return null;
        }
        return gridComp.getMap().get(c).getTopObj();
    }

    @Override
    public Obj getObjOrCell(Coordinates c) {
        if (gridComp.getMap().get(c) == null) {
            return null;
        }
        return gridComp.getMap().get(c).getTopObjOrCell();
        // ObjComponent objComponent = getObjCompMap().getOrCreate(c);
        // if (objComponent == null)
        // objComponent = getPassableObjMap().getOrCreate(c);
        // if (objComponent == null)
        // objComponent = getCellCompMap().getOrCreate(c);
        // if (objComponent == null)
        // return null;
        // return objComponent.getObj();
    }

    // public Map<Coordinates, ObjComponent> getPassableObjMap() {
    // return passableObjMap;
    // }
    //
    // public boolean isCoordinateObstructed(Coordinates c) {
    // return (objCompMap.getOrCreate(c) != null);
    // }
    //
    // public Map<Coordinates, ObjComponent> getHiddenObjCompMap() {
    // return hiddenObjCompMap;
    // }

    public Coordinates getOffsetCoordinate() {

        return offsetCoordinate;
    }

    public void setOffsetCoordinate(Coordinates offsetCoordinate) {
        LogMaster.log(0, "Current Offset Coordinate :" + offsetCoordinate);
        this.offsetCoordinate = offsetCoordinate;
    }

    private Coordinates getNextOffsetCoordinate() {
        return nextOffsetCoordinate;
    }

    public void setNextOffsetCoordinate(Coordinates nextOffsetCoordinate) {
        LogMaster.log(0, "Next Offset Coordinate :" + offsetCoordinate);
        this.nextOffsetCoordinate = nextOffsetCoordinate;
    }

    @Override
    public void manualOffsetReset() {
        manualOffsetY = 0;
        manualOffsetX = 0;
    }

    @Override
    public void wheelRotates(int n, boolean alt) {
        if (n == 0) {
            return;
        }
        if (alt) {
            manualOffsetX = MathMaster.getMinMax(manualOffsetX + n, -getFullCells(alt)
                    + getDisplayedCells(alt), getFullCells(alt) - getDisplayedCells(alt));
        } else {
            manualOffsetY = MathMaster.getMinMax(manualOffsetY + n, -getFullCells(alt)
                    + getDisplayedCells(alt), getFullCells(alt) - getDisplayedCells(alt));
        }
        game.getManager().refresh(false);
    }

    @Override
    public int getZ() {
        return dungeon.getZ();
    }

    @Override
    public void setCameraCenterCoordinates(Coordinates c) {
        manualOffsetReset();
        if (cameraCenterCoordinates == null) {
            this.cameraCenterCoordinates = c;
        } else if (cameraCenterCoordinates.equals(c)) {// ||
            // cameraCenterObj.equals(activeObj)
            cameraCenterObj = null;
            this.cameraCenterCoordinates = null;
        } else {
            this.cameraCenterCoordinates = c;
        }
        game.getManager().refresh(false);

    }

    private Unit getActiveObj() {
        if (activeObj == null) {
            if (PartyHelper.getParty() != null) {
                setActiveObj(PartyHelper.getParty().getLeader());
            } else {
                setActiveObj((Unit) game.getPlayer(true).getHeroObj());
            }
        }
        return activeObj;
    }

    private void setActiveObj(Unit activeObj) {
        this.activeObj = activeObj;
    }

    // @Override
    // public Map<Coordinates, ObjComponent> getOverlayingObjMap() {
    // return overlayingObjMap;
    // }
    //
    // public void setOverlayingObjMap(Map<Coordinates, ObjComponent>
    // overlayingObjMap) {
    // this.overlayingObjMap = overlayingObjMap;
    // }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public DC_Game getGame() {
        return game;
    }


    @Override
    public boolean canMoveOnto(Entity obj, Coordinates c) {
        return game.getRules().getStackingRule().canBeMovedOnto(obj,
         c, dungeon.getZ(), null);
    }

    @Override
    public void addUnitObj(Obj targetObj) {
        //TODO
    }

    public G_Panel getComp() {
        return comp;
    }

}
