package main.game.logic.dungeon.editor;

import main.content.values.parameters.G_PARAMS;
import main.entity.EntityCheckMaster;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import eidolons.game.module.dungeoncrawl.dungeon.minimap.MiniObjComp;
import main.launch.ArcaneVault;
import eidolons.swing.components.obj.BfGridComp;
import eidolons.swing.components.obj.CellComp;
import main.swing.generic.components.G_Panel;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class LE_MouseMaster implements MouseMotionListener, MouseListener, MouseWheelListener {
    // select multiple

    private Obj selectedObj;
    private Point pressPoint;
    private Point releasePoint;
    private Obj lastClicked;
    private Obj hoverObj;
    private Map<G_Panel, MiniObjComp> map;
    private boolean coordinateListeningMode;
    private Obj coordinateListeningObj;
    private boolean interrupted;
    private boolean exited;
    private boolean pressOffset;
    private Coordinates coordinates;
    private CONTROL_MODE mode;
    private Coordinates previousCoordinate;

    public void dragObj() {
        // MOVE! :)
    }

    public void objClicked(Obj obj) {
    }

    public void removeObj(Obj obj) {
        LevelEditor.getObjMaster().removeObj((DC_Obj) obj);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        int modifier = 1;
        if (e.isAltDown()) {
            if (e.isShiftDown()) {
                modifier = 5;
            }
            zoom(modifier * e.getWheelRotation());
            SoundMaster.playStandardSound(STD_SOUNDS.PAGE_TURNED);
            return;
        }
        // int x1 = 1; // TODO VISUAL OFFSET!
        // int x2 = LevelEditor.getCurrentLevel().getDungeon().getCellsX() - 2;
        // int y1 = 1;
        // int y2 = LevelEditor.getCurrentLevel().getDungeon().getCellsY() - 2;
        // // preCheck on edge
        // if (!CoordinatesMaster.isWithinBounds(hoverObj.getCoordinates(), x1,
        // x2, y1, y2)) {
        // }
        // DIRECTION d =
        // CoordinatesMaster.getClosestEdge(hoverObj.getCoordinates(),
        // LevelEditor
        // .getCurrentLevel().getDungeon().getCellsX(),
        // LevelEditor.getCurrentLevel()
        // .getDungeon().getCellsY());
        // boolean x = !d.isVertical();
        boolean x = isXEdgeCloserToHoverObj();

        if (e.isShiftDown()) {
            x = false;
        } else if (e.isControlDown()) {
            x = true;
        }
        // boolean invert= !d.isVertical();

        SoundMaster.playStandardSound(STD_SOUNDS.MOVE);
        offset(-modifier * e.getWheelRotation(), x);

    }

    private boolean isXEdgeCloserToHoverObj() {
        int xDiff = LevelEditor.getCurrentLevel().getDungeon().getCellsX()
                * 100
                / Math.max(
                LevelEditor.getCurrentLevel().getDungeon().getCellsX() - hoverObj.getX(),
                hoverObj.getX());
        int yDiff = LevelEditor.getCurrentLevel().getDungeon().getCellsY()
                * 100
                / Math.max(
                LevelEditor.getCurrentLevel().getDungeon().getCellsY() - hoverObj.getY(),
                hoverObj.getY());

        boolean x = xDiff < yDiff;
        return x;
    }

    private void offset(int i, boolean x) {
        LevelEditor.getCurrentLevel().getGrid().offset(x, i);
    }

    public Obj pickObject() {
        // Coordinates c =
        pickCoordinate();
        return getSelectedObj();
    }

    public Coordinates pickCoordinate() {
        coordinateListeningMode = true;
        // comp.refresh();
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ACTIVATE);
        boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.CUSTOM_SELECT);
        coordinateListeningMode = false;
        if (!result) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return null;
        }
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_TARGET_SELECTED);
        Coordinates coordinates = coordinateListeningObj.getCoordinates();
        return coordinates;
    }

    public void zoom(int wheelRotation) {
        LevelEditor.getCurrentLevel().getGrid().zoom(wheelRotation);

    }

    @Override
    public String toString() {
        return "LE MouseListener";
    }

    // boolean rightClick
    public boolean checkEventConsumed(Obj obj, boolean rightClick) {
        if (coordinateListeningMode) {
            if (rightClick) {
                WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, false);
                return true;
            }
            coordinateListeningObj = obj;
            WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, true);
            return true;
        } else if (rightClick) {
            // if (LevelEditor.isMouseAddMode()) {
            // LevelEditor.setMouseAddMode(false);
            // return true;
            // }
            return false;
        }
        lastClicked = obj;
        if (obj instanceof DC_Cell) {
            if (mode != null) {
                return false;
            }
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
            return true;
        }
        selectedObj = obj;
        SoundMaster.playStandardSound(STD_SOUNDS.CHECK);
        // TODO place // ...
        return false;
    }

    public void mouseClicked(MouseEvent e) {
        // info click!
        DC_Obj obj;
        coordinates = null;

        boolean right = SwingUtilities.isRightMouseButton(e);
        CellComp cellComp = getGrid().getCompByPoint(e.getPoint());

        previousCoordinate = coordinates;
        coordinates = cellComp.getCoordinates();
        obj = cellComp.getTopObjOrCell();
        obj.setCoordinates(coordinates);
        lastClicked = obj;
        if (!right) {
            selectedObj = obj;
        }
        if (right) {
            setMode(null);
        }
        if (checkEventConsumed(obj, right)) {
            getGrid().getPanel().repaint();
            return;
        }
        LevelEditor.getGrid().setDirty(true);
        handleClick(e, right);
        if (LevelEditor.getGrid().isDirty()) {
            LevelEditor.getGrid().refresh();
        }

    }

    private void handleClick(MouseEvent e, boolean right) {
        if (mode != null) {

        }
        if (lastClicked == null) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return;
        }
        Coordinates coordinates = lastClicked.getCoordinates();
        boolean alt = e.isAltDown();
        boolean add = e.isShiftDown() || LevelEditor.isMouseAddMode();
        boolean empty = LevelEditor.getSimulation().getObjectByCoordinate(null, coordinates, false,
                true, true) == null;
        // LevelEditor.getMapMaster().getActiveZone()
        // how to ignore this if necessary?
        // don't wanna select obj being removed, e.g. ...
        // DC_Obj obj = map.getOrCreate(e.getSource()).getObj();

        if (e.getClickCount() > 1 || (alt && !empty)) {
            if (right) {
                // choose if stacked?

                return;
            } else if (e.isShiftDown()) {
                int i = lastClicked.getIntParam(G_PARAMS.CHANCE);
                i = DialogMaster.inputInt("Set chance for object to be there..."
                        + " (set negative to add '1 object only' rule for this coordinate)", i);
                lastClicked.setParam(G_PARAMS.CHANCE, i);
                SoundMaster.playStandardSound(STD_SOUNDS.NOTE);
                return;
            } else if (alt && !empty) {

                LE_ObjMaster.fill(CoordinatesMaster.getCoordinatesBetween(previousCoordinate,
                        coordinates), ArcaneVault.getSelectedType());
                return;

                // DC_HeroObj unit = (DC_HeroObj)
                // LevelEditor.getSimulation().getObjectByCoordinate(
                // lastClicked.getCoordinates(), false);
                // if (unit != null)
                // if (lastClicked instanceof DC_HeroObj)
                // LE_ObjMaster.setFlip((DC_HeroObj) lastClicked,
                // lastClicked.getCoordinates());
            }
        } // could use same right click, just try() - if empty=>add, else remove
        // ++ DIRECTION CHANGE
        if (!empty) {
            if (e.isControlDown() && right) {
                ObjType selectedType = LevelEditor.getMainPanel().getPalette().getSelectedType();
                LevelEditor.cache();
                LevelEditor.setMouseAddMode(true);
                LevelEditor.getObjMaster().stackObj(selectedType, coordinates);
                SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ACTIVATE);
                return;
            }
        }
        if (right) {
            ObjType selectedType = LevelEditor.getMainPanel().getPalette().getSelectedType();

            if (selectedType != null) {
                if ((EntityCheckMaster.isOverlaying(selectedType) && !EntityCheckMaster
                        .isOverlaying(lastClicked))
                        || ((empty || add)) && !alt) {
                    if (lastClicked != null) {

                        if (e.isShiftDown()) {
                            selectedType = LevelEditor.getMainPanel().getInfoPanel()
                                    .getSelectedType();
                        }
                        LevelEditor.cache();
                        LevelEditor.getObjMaster().addObj(selectedType, coordinates);
                        LevelEditor.setMouseAddMode(true);
                        // if (alt) //left click instead!
                        // obj.setDirection(DirectionMaster.FLIP_DIRECTION);
                        SoundMaster.playStandardSound(STD_SOUNDS.OK);

                        // LevelEditor.getMainPanel().getPalette().checkAddToPalette(selectedType);
                    }
                    return;
                }
            }
        }
        if (selectedObj != null) {
            try {
                selectedObj.setInfoSelected(true);
                // LevelEditor.highlightsOff();
                // LevelEditor.highlight(selectedObj.getCoordinates());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        // LevelEditor.getMainPanel().getPalette().
        // LevelEditor.getMainPanel().getInfoPanel()

    }

    private BfGridComp getGrid() {
        return LevelEditor.getGrid();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressPoint = e.getPoint();
        // new thread
        if (!pressOffset) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                WaitMaster.WAIT(500); // speed up for right click

                while (true) {
                    if (interrupted) {
                        LogMaster.log(1, "interrupted");
                        break;
                    }
                    if (exited) {
                        LogMaster.log(1, "exited");
                        break;
                    }
                    boolean x = isXEdgeCloserToHoverObj();
                    boolean positive;
                    if (x) {
                        positive = (CoordinatesMaster.getClosestEdge(hoverObj.getCoordinates(),
                                LevelEditor.getCurrentLevel().getDungeon().getCellsX(), LevelEditor
                                        .getCurrentLevel().getDungeon().getCellsY(), true) == DIRECTION.RIGHT);
                    } else {
                        positive = (CoordinatesMaster.getClosestEdge(hoverObj.getCoordinates(),
                                LevelEditor.getCurrentLevel().getDungeon().getCellsX(), LevelEditor
                                        .getCurrentLevel().getDungeon().getCellsY(), false) == DIRECTION.DOWN);
                    }
                    int i = (positive ? 1 : -1);

                    offset(i, x); // left right button? mods?
                    // preCheck exited, released, off limits

                    WaitMaster.WAIT(500);
                }
                interrupted = false;
                exited = false;
            }
        }).start();

    }

    public CONTROL_MODE getMode() {
        return mode;
    }

    public void setMode(CONTROL_MODE mode) {
        this.mode = mode;
        if (mode != null) {
            LevelEditor.setActionStatusTooltip(StringMaster.getWellFormattedString(mode.name()));
        } else {
            LevelEditor.setActionStatusTooltip(null);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releasePoint = e.getPoint();
        interrupted = true;
        // SoundMaster.playStandardSound(STD_SOUNDS.MOVE);
        // int diff_x = pressPoint.x - releasePoint.x;
        // int diff_y = pressPoint.y - releasePoint.y;
        // offset(diff_y / 10, false);
        // offset(diff_x / 10, true);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        exited = true;
        // TODO Auto-generated method stub

    }

    public Obj getSelectedObj() {
        return selectedObj;
    }

    public Obj getLastClicked() {
        return lastClicked;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public enum CONTROL_MODE {
        CLEAR, REMOVE, FILL
    }

}
