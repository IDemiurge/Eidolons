package main.game.logic.dungeon.minimap;

import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.core.game.DC_Game;
import main.game.logic.dungeon.Dungeon;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.Refreshable;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.images.ImageManager.HIGHLIGHT;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.*;
import java.util.List;

public class MiniGrid implements Refreshable {
    MiniObjComp[][] comps; // for cells too? or can we leave something just
    // black/transparent?
    private G_Panel comp;
    private int cellWidth;
    private int cellHeight;
    private Dimension size;
    private Dungeon dungeon;
    private Minimap map;
    private Map<Coordinates, MiniObjComp> compMap = new HashMap<>();
    private boolean editMode;
    private MouseListener customMouseListener;
    private List<MiniObjComp> overlayingObjComps = new LinkedList<>();
    private int overlayingObjWidth;
    private int overlayingObjHeight;
    private int originalCellHeight;
    private int originalCellWidth;
    private int offsetX;
    private int offsetY;
    private MouseWheelListener mouseWheelListener;
    private int defaultOffsetX;
    private int defaultOffsetY;
    private MouseMotionListener mouseMotionListener;

    public MiniGrid(Minimap map) {
        this(false, map);

    }

    public MiniGrid(boolean editMode, Minimap map) {
        dungeon = map.getDungeon();
        setSize(map.getSize());
        this.editMode = editMode;
        this.map = map;
    }

    public static boolean isMouseDragOffsetModeOn() {
        return true;
    }

    public void init() {

        comp = new G_Panel(); // another canvas?
        comp.addMouseWheelListener(mouseWheelListener);
        comp.addMouseMotionListener(mouseMotionListener);
        // comp.addMouseListener(customMouseListener);

        LogMaster.log(1, getComp().getMouseListeners() + " on "
                + getComp().hashCode());
        // comp.addMouseMotionListener(new DragOffsetMouseListener(this));

        setCellWidth((int) (getSize().getWidth() / dungeon.getCellsX()));
        cellHeight = (int) (getSize().getHeight() / dungeon.getCellsY());
        // maximum possible size for square dimensions
        setCellWidth(Math.min(getCellWidth(), cellHeight));
        originalCellWidth = cellWidth;
        cellHeight = Math.min(getCellWidth(), cellHeight);
        originalCellHeight = cellHeight;
        overlayingObjHeight = cellHeight / 3 * 2;
        overlayingObjWidth = getCellWidth() / 3 * 2;

        resetDefaultOffset();
        resetComponents();
    }

    private void resetDefaultOffset() {
        defaultOffsetX = (int) (getSize().getWidth() - cellWidth * dungeon.getCellsX()) / 2;
        defaultOffsetY = (int) (getSize().getHeight() - cellHeight * dungeon.getCellsY()) / 2;
    }

    public void addOverlayingObjToLoad(Unit obj) {
        MiniObjComp minicomp = new MiniObjComp(true, obj, map);
        if (editMode) {
            minicomp.getComp().addMouseListener(customMouseListener);
        }
        overlayingObjComps.add(minicomp);

    }

    private void resetComponents() {
        for (Coordinates c : getGame().getCoordinates()) {
            addObj(getGame().getObjectByCoordinate(c, true));
        } // overlaying are never in topObjMap ...
        for (MiniObjComp minicomp : overlayingObjComps) {
            resetOverlayingComp(minicomp);
        }
        comp.revalidate();
    }

    public void resetOverlayingComp(Obj obj) {
        for (MiniObjComp minicomp : overlayingObjComps) {
            if (minicomp.getObj() == obj) {
                resetOverlayingComp(minicomp);
                return;
            }
        }
    }

    public void resetOverlayingComp(MiniObjComp minicomp) {
        boolean multi = false;
        for (MiniObjComp comp : overlayingObjComps) {
            if (comp != minicomp) {
                if (comp.getObj().getCoordinates().equals(minicomp)) {
                    multi = true;
                }
            }
        }
        this.comp.add(minicomp.getComp(), getOverlayingMigString((Unit) minicomp.getObj(),
                multi));
        minicomp.initSize(getSize());
    }

    public void objRemoved(DC_Obj obj) {
        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            if (unit.isOverlaying()) {
                for (MiniObjComp c : overlayingObjComps) {
                    if (c.getObj().equals(obj)) {
                        overlayingObjComps.remove(c);
                        comp.remove(c.getComp());
                        break;
                    }

                }
                return;
            }
        }
        DC_Cell cell = (DC_Cell) dungeon.getGame().getCellByCoordinate(obj.getCoordinates());
        if (cell == null) {
            return;
        }
        // PERHAPS REFRESH?
        // compMap.getOrCreate(obj.getCoordinates()).removeObj(obj);
        if (compMap.get(obj.getCoordinates()).getObjects().size() <= 1) {
            objAdded(cell);
        }

    }

    public void objAdded(DC_Obj obj) {
        objAdded(obj, false);
    }

    public void objAdded(DC_Obj obj, boolean stack) {
        // comp.remove(compMap.getOrCreate(obj.getCoordinates()).getComp());
        MiniObjComp minicomp;

        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            if (unit.isOverlaying()) {
                minicomp = new MiniObjComp(true, obj, map);
                if (editMode) {
                    minicomp.getComp().addMouseListener(customMouseListener);
                }
                boolean multi = false;
                for (MiniObjComp comp : overlayingObjComps) {
                    if (comp.getObj().getCoordinates().equals(minicomp)) {
                        multi = true;
                    }
                }
                this.comp.add(minicomp.getComp(), getOverlayingMigString(unit, multi));
                overlayingObjComps.add(minicomp);
                refresh();
                return;
            }

        }
        minicomp = compMap.get(obj.getCoordinates());
        if (minicomp == null) {
            return;
        }
        if (stack) {
            minicomp.addObj(obj);
        } else {
            minicomp.setObj(obj);
        }
        minicomp.refresh();
        // addObj(obj);
        // refresh();
    }

    public MiniObjComp getCompByPoint(Point p) {
        int x = p.x / getCellWidth();
        int y = p.y / getCellWidth();
        Coordinates coordinates = new Coordinates(true, x, y);
        // check overlaying!
        for (MiniObjComp o : overlayingObjComps) {
            // if (!o.getCoordinates().eq)
            // continue; TODO

            ((Unit) o.getObj()).getDirection();

            o.getCellSize();
            getOverlayingObjWidth();

        }
        //

        return compMap.get(coordinates);

    }

    private void addObj(Obj obj) {
        Coordinates c = obj.getCoordinates();
        MiniObjComp objComp = compMap.get(c);
        if (objComp == null) {
            objComp = new MiniObjComp((DC_Obj) obj, map);
            compMap.put(c, objComp);
        }

        if (MiniGrid.isMouseDragOffsetModeOn()) {
            // mouseMap.put(rect, objComp); //or maybe I can map things
            // dynamically on click!
        } else // if (editMode)
        {
            if (objComp.getComp().getMouseListeners().length != 0) {
                objComp.getComp().removeMouseListener(customMouseListener);
            }
            // if (objComp.getComp().getMouseListeners().length == 0) fails?
            objComp.getComp().addMouseListener(customMouseListener);
        }
        comp.add(objComp.getComp(), getMigString(c));
    }

    public void setCustomMouseListener(MouseListener customMouseListener) {
        this.customMouseListener = customMouseListener;

    }

    private String getOverlayingMigString(Unit obj, boolean multi) {
        Coordinates c = obj.getCoordinates();
        int width = getOverlayingObjWidth();
        int height = overlayingObjHeight;
        if (multi) {
            width = getOverlayingObjWidth() * 3 / 4;
            height = getOverlayingObjWidth() * 3 / 4;
        }
        int xOffset = (getCellWidth() - width) / 2;
        int yOffset = (cellHeight - height) / 2;
        DIRECTION d = obj.getDirection();
        if (d != null) {
            if (d.isGrowX() != null) {
                xOffset = (d.isGrowX()) ? getCellWidth() - width : 0;
            }
            if (d.isGrowY() != null) {
                yOffset = (d.isGrowY()) ? cellHeight - overlayingObjHeight : 0;
            }

        }
        int w = (offsetX + c.x) * getCellWidth();
        w += xOffset;
        int h = (offsetY + c.y) * cellHeight;
        h += yOffset;
        return "pos " + (defaultOffsetX + w) + " " + (defaultOffsetY + h);

    }

    private String getMigString(Coordinates c) {
        return "pos " + (defaultOffsetX + (offsetX + c.x) * getCellWidth()) + " "
                + (defaultOffsetY + (offsetY + c.y) * cellHeight);

    }

    public void resetZoom() {
        cellHeight = originalCellHeight;
        cellWidth = originalCellWidth;
        overlayingObjHeight = cellHeight / 3 * 2;
        overlayingObjWidth = getCellWidth() / 3 * 2;

        resetComponents();
        refresh();
    }

    public void zoom(int wheelRotation) {
        setCellWidth(getCellWidth() + wheelRotation);
        cellHeight += wheelRotation;

        overlayingObjHeight = cellHeight / 3 * 2;
        overlayingObjWidth = getCellWidth() / 3 * 2;

        // TODO WAIT IF THERE IS MORE!!!

        resetDefaultOffset();

        resetComponents();
        refresh();
        // min/max!

        // setSize(new Dimension(
        // getSize().width + (cells*wheelRotation),
        // );

    }

    public void resetOffset() {
        offsetX = 0;
        offsetY = 0;
    }

    public void offset(boolean xOrY, int wheelRotation) {
        if (xOrY) {
            offsetX += wheelRotation;
        } else {
            offsetY += wheelRotation;
        }
        resetComponents();
    }

    public void highlightsOff() {
        for (MiniObjComp c : compMap.values()) {
            c.setHighlight(null);
            refreshComp(null, null, c);
            // ++ overlaying
        }
        // refresh();
    }

    public void highlight(List<Coordinates> list) {
        for (Coordinates c : list) {
            compMap.get(c).setHighlight(HIGHLIGHT.DEFAULT);
            refreshComp(null, c);
        }
        // refresh();
    }

    @Override
    public void refresh() {
        Chronos.mark("minigrid refresh");
        Set<Coordinates> set = compMap.keySet();
        int index = 0;
        for (MiniObjComp comp : overlayingObjComps) {
            comp.refresh();
            this.comp.setComponentZOrder(comp.getComp(), index);
            index++;
        }
        for (Coordinates c : set) {
            refreshComp(index, c);
            index++;
        }
        comp.revalidate();
        // Chronos.logTimeElapsedForMark("minigrid refresh");
    }

    public void refreshComp(Integer index, Coordinates c) {
        MiniObjComp objComp = compMap.get(c);
        refreshComp(index, c, objComp);
    }

    public void refreshComp(Integer index, Coordinates c, MiniObjComp objComp) {
        if (index != null) {
            this.comp.setComponentZOrder(objComp.getComp(), index);
        }

        DC_Obj obj = objComp.getObj();
        if (c == null) {
            c = obj.getCoordinates();
        }
        List<? extends DC_Obj> objects = getGame().getObjectsOnCoordinate(c);
        LinkedList<Unit> overlaying = new LinkedList<>();
        for (Unit o : map.getDungeon().getGame().getObjectsOnCoordinate(c)) {
            if (o.isOverlaying()) {
                overlaying.add(o);
            }
        }
        // TODO WHAT IF THERE ARE STACKED OBJECT + OVERLAYING??? FUTURE...
        // for non-Level Editor
        objComp.setObjects(new DequeImpl(objects).getRemoveAll(overlaying));
        // sort top?

        objComp.setObj(obj);
        // TODO WHAT ABOUT OVERLAYING OBJECTS?
        objComp.initSize(getSize());
        // Chronos.mark(obj + " objComp refresh");
        objComp.refresh();
        // Chronos.logTimeElapsedForMark(obj + " objComp refresh");
    }

    private DC_Game getGame() {
        return map.getDungeon().getGame();
    }

    public G_Panel getComp() {
        return comp;
    }

    public void setComp(G_Panel comp) {
        this.comp = comp;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public MiniObjComp[][] getComps() {
        return comps;
    }

    public void setComps(MiniObjComp[][] comps) {
        this.comps = comps;
    }

    public Minimap getMap() {
        return map;
    }

    public void setMap(Minimap map) {
        this.map = map;
    }

    public Map<Coordinates, MiniObjComp> getCompMap() {
        return compMap;
    }

    public void setCompMap(Map<Coordinates, MiniObjComp> compMap) {
        this.compMap = compMap;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public DC_Obj getTopObj(Coordinates coordinates) {

        for (MiniObjComp comp : overlayingObjComps)
        // if (obj.getObj().checkBool(TOP))
        {
            if (comp.getObj().getCoordinates().equals(coordinates)) {
                return comp.getObj();
            }
        }
        return getCompMap().get(coordinates).getTopObj();
    }

    public MouseWheelListener getMouseWheelListener() {
        return mouseWheelListener;
    }

    public void setMouseWheelListener(MouseWheelListener mouseWheelListener) {
        this.mouseWheelListener = mouseWheelListener;
    }

    public int getOverlayingObjWidth() {
        return overlayingObjWidth;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public List<MiniObjComp> getOverlayingObjComps() {
        return overlayingObjComps;
    }

    public void setOverlayingObjComps(List<MiniObjComp> overlayingObjComps) {
        this.overlayingObjComps = overlayingObjComps;
    }

    public MouseMotionListener getMouseMotionListener() {
        return mouseMotionListener;
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

}
