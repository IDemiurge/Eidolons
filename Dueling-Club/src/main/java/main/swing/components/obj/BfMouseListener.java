package main.swing.components.obj;

import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.swing.components.buttons.DynamicButton;
import main.swing.generic.services.dialog.DialogMaster;
import main.swing.generic.services.dialog.DialogPanel;
import main.system.auxiliary.GuiManager;
import main.system.datatypes.DequeImpl;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.AnimationManager.MouseItem;
import main.system.graphics.PhaseAnimation;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.ToolTipMaster;
import main.system.threading.WaitMaster;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;

public class BfMouseListener implements Runnable, MouseListener, MouseMotionListener,
        MouseWheelListener {
    protected int delay = 1100;
    private BfGridComp gridComp;
    private boolean dragging;
    private int curX;
    private int curY;
    private Point releasePoint;
    private Point pressPoint;
    private Thread tooltipUpdateThread;
    private JComponent component;
    private Integer xOffset;
    private Integer yOffset;
    private CellComp prevComp;
    private boolean toolTipsEnabled;
    private Point point;
    private CellComp cellComp;
    private Obj objClicked;

    public BfMouseListener(BfGridComp bfGridComp) {
        this.gridComp = bfGridComp;
    }

    public void stopTooltipUpdateThread() {
        tooltipUpdateThread.interrupt();
    }

    public void setComponent(Component c) {
        component = (JComponent) c;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (xOffset == null) {
                    xOffset = gridComp.getHolder().getDungeon().getGame().getBattleField()
                            .getBuilder().getBfGridPosX();
                }
                if (yOffset == null) {
                    yOffset = gridComp.getHolder().getDungeon().getGame().getBattleField()
                            .getBuilder().getBfGridPosY();
                }
                if (!DC_Game.game.getGUI().getWindow().isActive()) {
                    continue;
                }
                Point onScreen = MouseInfo.getPointerInfo().getLocation();
                Point point = new Point(onScreen.x - xOffset, onScreen.y - yOffset);
                if (point.x < 0 || point.x > GuiManager.getBfGridWidth() || point.y < 0
                        || point.y > GuiManager.getBfGridHeight()) {
                    try {
                        ToolTipManager.sharedInstance().mouseClicked(
                                new MouseEvent(component, MouseEvent.MOUSE_MOVED, System
                                        .currentTimeMillis(), 0, onScreen.x, onScreen.y, 0, false));
                    } catch (Exception e) {

                    }
                    continue;
                }
                CellComp cellComp = gridComp.getCompByPoint(point);

                String text = ToolTipMaster.getObjTooltip(cellComp);
                component.setToolTipText(text);
                if (cellComp != prevComp)
                    // main.system.auxiliary.LogMaster.log(1,
                    // "tooltip updated - " +
                    // text + ";point= "
                    // + point + ";cellComp= " + cellComp);
                {
                    prevComp = cellComp;
                }
                MouseEvent event = new MouseEvent(component, MouseEvent.MOUSE_MOVED, System
                        .currentTimeMillis(), 0, onScreen.x, onScreen.y, 0, false);

                ToolTipManager.sharedInstance().mouseMoved(event);
            } catch (Exception e) {
                e.printStackTrace();
            }

            WaitMaster.WAIT(delay);
        }
    }

    public void startTooltipUpdateThread() {
        if (!toolTipsEnabled) {
            return;
        }
        if (tooltipUpdateThread == null) {
            tooltipUpdateThread = new Thread(this, "tooltip update thread");
        }
        tooltipUpdateThread.start();

    }

    private boolean checkAnimationPageFlipped(MouseWheelEvent e) {
        for (PhaseAnimation anim : gridComp.getGame().getAnimationManager().getAnimations()) {
            if (anim.contains(e.getPoint())) {
                if (anim.isWheelSupported()) {
                    if (anim.isManualFlippingSupported()) {
                        boolean forward = e.getWheelRotation() < 0;
                        anim.pageFlipped(forward);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkDynamicButtonClick(MouseEvent e) {
        for (DynamicButton d : gridComp.getGame().getGuiMaster().getDynamicButtons()) {
            if (d.getRect().contains(e.getPoint())) {
                d.clicked(e);
                return true;
            }
        }
        return false;
    }

    public boolean checkAnimationClick(MouseEvent e) {
        point = e.getPoint();
        DequeImpl<PhaseAnimation> animations = new DequeImpl<>(gridComp.getGame().getAnimationManager()
                .getAnimations());
        animations.addAll(gridComp.getGame().getAnimationManager().getTempAnims());
        if (SwingUtilities.isRightMouseButton(e)) {

            for (PhaseAnimation anim : animations) {
                if (checkToggleTooltip(anim, e)) {
                    return true;
                }

                if (anim.contains(e.getPoint())) {
                    if (e.getClickCount() > 1) {

                        if (anim.isPaused()) {
                            anim.resume();
                        } else {
                            anim.pause();
                        }
                        return true;
                    }
                    AnimPhase phase = anim.getPhase();
                    if (phase != null) {
                        if (anim.subPhaseClosed()) {
                            SoundMaster.playStandardSound(STD_SOUNDS.BACK);
                            return true;
                        }
                    }
                }
            }
        }
        for (PhaseAnimation anim : animations) {
            if (anim.getMouseMap() != null)

            {
                for (Rectangle rect : anim.getMouseMap().keySet()) {
                    if (rect.contains(point)) {
                        MouseItem item = anim.getMouseMap().get(rect);
                        return itemClicked(item, anim);
                    }
                }
            }
        }

        return false;
    }

    private boolean checkToggleTooltip(PhaseAnimation anim, MouseEvent e) {
        for (Rectangle rect : anim.getTooltipMap().keySet()) {
            if (rect.contains(e.getPoint())) {
                gridComp.getGame().getToolTipMaster().toggleToolTip(anim.getTooltipMap().get(rect));

                return true;
            }
        }
        return false;

    }

    private void displayTooltip(PhaseAnimation anim, MouseItem item) {
        // TODO
        gridComp.getGame().getToolTipMaster().addTooltip(anim, item.getPoint(),
                item.getRectangle(), item);

    }

    private boolean itemClicked(MouseItem item, PhaseAnimation anim) {

        if (item.getType() != null) {
            switch (item.getType()) {

                case THUMBNAIL:
                    anim.toggleThumbnail();
                    break;
                case TOOLTIP:
                    displayTooltip(anim, item);
                    break;
                case SUB_PHASE:
                    if (anim.getPhase().getType().isSubPhase()) {
                        return false;
                    }
                    if (item.getArg() == null) {
                        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
                        return true;
                    }
                    anim.subPhaseOpened(anim.getPhase((PHASE_TYPE) item.getArg()));
                    SoundMaster.playStandardSound(STD_SOUNDS.DIS__OPEN_MENU);
                    return true;
                case CONTROL_BACK:
                    anim.pageFlipped(false);
                    return true;
                case CONTROL_FORWARD:
                    anim.pageFlipped(true);
                    return true;

            }
        }
        return false;
    }

    private boolean checkDialogClick(MouseEvent e) {
        if (gridComp == null) {
            return false;
        }
        if (gridComp.getGame().getBattleField() == null) {
            return false;
        }
        DialogPanel d = gridComp.getGame().getBattleField().getBuilder().getDialog();
        if (d == null) {
            return false;
        }
        return d.checkClick(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // new ToolTipManager().
        gridComp.getGame().getToolTipMaster().removeToolTips();
        if (checkDialogClick(e)) {
            return;
        }
        if (checkAnimationClick(e)) {
            return;
        }
        if (checkDynamicButtonClick(e)) {
            return;
        }
        point = e.getPoint();

        cellComp = gridComp.getCompByPoint(e.getPoint());
        Point relativePoint = new Point(point.x % gridComp.getCellWidth(), point.y
                % gridComp.getCellHeight());
        objClicked = null;

        for (Rectangle rect : cellComp.getMouseMap().keySet()) {
            if (rect.contains(relativePoint)) {
                Object object = cellComp.getMouseMap().get(rect);
                if (object instanceof INTERACTIVE_ELEMENT) {
                    INTERACTIVE_ELEMENT element = (INTERACTIVE_ELEMENT) object;
                    switch (element) {
                        case STACK:
                            // if (CoreEngine.isLevelEditor())
                            objClicked = DialogMaster
                                    .objChoice("Which object?", cellComp.getObjects().toArray(
                                            new DC_Obj[cellComp.getObjects().size()]));
                            break;
                        case AP:
                            break;
                        case COUNTERS:
                            break;
                        case ITEMS:
                            Collection<? extends Obj> droppedItems = cellComp.getGame()
                                    .getDroppedItemManager().getDroppedItems(
                                            cellComp.getTerrainObj());

                            break;
                        case LOCKS:
                            break;
                        case TRAPS:
                            break;
                    }

                    // break ;
                } else if (object instanceof Obj) {
                    if (object instanceof DC_HeroObj) {
                        if (objClicked != null) {
                            int index = cellComp.getObjects().indexOf(objClicked);
                            int index2 = cellComp.getObjects().indexOf(object);
                            main.system.auxiliary.LogMaster.log(1, objClicked + "'s " + index
                                    + " vs " + object + "'s " + index2);
                            if (index > index2)
                                // TODO so we need to keep this always sorted...
                            {
                                continue;
                            }
                        }
                        DC_HeroObj unit = (DC_HeroObj) object;
                        if (unit.isOverlaying()) {
                            // corpseClicked(unit);
                            if (unit.getVisibilityLevel() == VISIBILITY_LEVEL.CONCEALED) {
                                continue;
                            }
                            objClicked = unit;
                            break;
                        } else {
                            objClicked = unit;
                            continue;
                        }
                    } else if (object instanceof DC_Cell) {
                        if (e.isAltDown()) {
                            objClicked = (DC_Obj) object;
                            break;
                        } else if (objClicked == null) {
                            objClicked = (DC_Obj) object;
                        }
                        continue; // ?
                    }
                }
                // if (objClicked == null)
                // return; // ??
            }

        }
        if (objClicked == null) {
            objClicked = cellComp.getTopObjOrCell();
        }
        if (CoreEngine.isLevelEditor()) {
            if (objClicked instanceof DC_Cell) {
                return;
            }
        }
        boolean right = SwingUtilities.isRightMouseButton(e);
        if (right) {
            objClicked.invokeRightClicked();
            // gridComp.getGame().getManager().rightClicked(objClicked);
        } else {
            gridComp.getGame().getManager().objClicked(objClicked);
        }

        boolean debugMode = gridComp.getGame().isDebugMode();
        if (debugMode) {
            gridComp.getGame().getDebugMaster().setArg(objClicked);
        }
        if (e.isAltDown()) {
            if (debugMode) {
                invokeAltClick(right);
            } else {
                new Thread(new Runnable() {
                    public void run() {
                        gridComp.getGame().getMovementManager().moveTo(objClicked);
                    }
                }, "moveTo thread").start();
            }
        }
        if (e.isShiftDown()) {
            invokeShiftClick(right);
        }
        if (e.isControlDown()) {
            invokeControlClick(right);
        }

    }

    private void invokeControlClick(boolean right) {

        gridComp.getGame().getDebugMaster().executeDebugFunctionNewThread(getFunction(true, right));

    }

    private void invokeShiftClick(boolean right) {
        gridComp.getGame().getDebugMaster().executeDebugFunctionNewThread(getFunction(null, right));
    }

    private void invokeAltClick(boolean right) {

        gridComp.getGame().getDebugMaster()
                .executeDebugFunctionNewThread(getFunction(false, right));
    }

    // right click?
    private DEBUG_FUNCTIONS getFunction(Boolean alt_control_shift, boolean right) {
        if (alt_control_shift == null) {
            return DEBUG_FUNCTIONS.ACTIVATE_UNIT; // set main
        }
        if (alt_control_shift) {
            return right ? DEBUG_FUNCTIONS.ADD_UNIT : DEBUG_FUNCTIONS.ADD_ENEMY_UNIT;
        }

        return DEBUG_FUNCTIONS.KILL_UNIT; // block/hide/reset?
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Coordinates c = mapToCoordinate(e.getPoint()); TODO this won't work!
        // :))
        // CellComp cellComp = gridComp.getMap().getOrCreate(c);
        // gridComp.getGame().getManager().setHoverObj(cellComp.getTopObj());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressPoint = e.getPoint();
        main.system.auxiliary.LogMaster.log(0, "mouse pressed at " + pressPoint);
    }

    public void mouseReleased(MouseEvent e) {
        dragging = false;
        releasePoint = e.getPoint();
        main.system.auxiliary.LogMaster.log(0, "released from " + pressPoint.x + "," + pressPoint.y
                + " to " + releasePoint.x + "," + releasePoint.y);

        int diff_x = pressPoint.x - releasePoint.x;
        int diff_y = pressPoint.y - releasePoint.y;
        int factor = 75 * gridComp.getZoom() / 100; // relative to size? TODO
        int yOffset = diff_y / factor;
        int xOffset = diff_x / factor;
        int max_offsetY = 4;
        int max_offsetX = 6;
        yOffset = Math.min(max_offsetY, yOffset);
        xOffset = Math.min(max_offsetX, xOffset);
        if (Math.abs(xOffset) > 0 || Math.abs(yOffset) > 0) {
            SoundMaster.playStandardSound(STD_SOUNDS.MOVE);
            gridComp.offset(xOffset, true);
            gridComp.offset(yOffset, false);
        }

    }

    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        // main.system.auxiliary.LogMaster.log(1, "DRAGGED to " + p);
        curX = p.x;
        curY = p.y;
        if (dragging) {
            // repaint(); ?
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // SwingUtilities.
        // main.system.auxiliary.LogMaster.log(1, "mouseMoved; point= " +
        // e.getPoint());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (gridComp.getGame().isSimulation()) {
            gridComp.zoom(e.getWheelRotation());
        } else if (!checkAnimationPageFlipped(e)) {
            // TODO could be nice if it worked!
            // gridComp.zoom(e.getWheelRotation());
        }

    }

    public enum INTERACTIVE_ELEMENT {
        AP, COUNTERS, ITEMS, TRAPS, LOCKS, CORPSES, STACK,

    }

}
