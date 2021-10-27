package libgdx.gui.dungeon.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.GenericGridView;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.screens.handlers.ScreenMaster;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 11/11/2018.
 * <p>
 * I could in fact have a class for it
 * <p>
 * what could be improved?
 * <p>
 * just proper show/hide!
 * <p>
 * hide when: mouse leaves and stays out for X time user hits ESC any action is activated
 * <p>
 * <p>
 * if showing: don't let other things getVar in the way of tooltips/hover
 */
public class StackViewMaster {
    private static final float WAIT_AFTER_HOVER_OFF = 2;
    private static final float WAIT_AFTER_SHOW = 5;
    private float stackTimer;
    private final int minStackSize = OptionsMaster.getControlOptions().
            getIntValue(CONTROL_OPTION.MIN_OBJECTS_TO_OPEN_STACK_ON_HOVER);
    private float waitToHideStack;
    private final Map<GenericGridView, Vector2> posMap = new HashMap<>();
    private final Map<GenericGridView, Float> scaleMap = new HashMap<>();
    private GridCellContainer stackCell;

    public StackViewMaster() {
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_MOVED, p -> {
            if (p.get() instanceof UnitGridView) {
                if (((UnitGridView) p.get()).isStackView()) {
                    stackOff();
                }

            }
        });
    }

    public void act(float delta) {

        if (waitToHideStack > 0) {
            stackTimer += delta;
            if (stackTimer >= waitToHideStack) {
                stackOff();
                stackTimer = 0;
                waitToHideStack = 0;
            }
        } else stackTimer = 0;
    }

    private void stackOn(GridCellContainer cell, boolean horizontal) {
        stackOff();
        //        showingStack = true;
        List<GenericGridView> views = cell.getUnitViews(true);

        if (views.size() < minStackSize) {
            return;
        }
        int size = 128;
        //        boolean horizontal = bottom != null;
        int x = horizontal ? -size * views.size() / 2 + size / 2 : 0;
        int y = !horizontal ? -size * views.size() / 2 + size / 2 : 0;
        for (GenericGridView view : views) {
            //sorted?
            ActionMasterGdx.addMoveByAction(view, x, y, 0.9f);
            posMap.put(view, new Vector2(-x, -y));
            if (horizontal) {
                x += 140;
            } else {
                y += 140;
            }
            view.setStackView(true);
            view.setHovered(true);

            ActionMasterGdx.addScaleAction(view, 1, 1.2f);
            //                ScreenMaster.getDungeonGrid().getCells()[c.x][c.y];
            //stackView(true);
            scaleMap.put(view, view.getScaleX());

            //scaling on?

            //highlight by color ally

        }
        cell.setStackView(true);
        stackCell = cell;
        waitToHideStack = WAIT_AFTER_SHOW;
        //ESC to cancel
        main.system.auxiliary.log.LogMaster.log(1, "Stack on! \n" + cell + "\n" + posMap + "\n" + scaleMap);
    }

    public void stackOff() {
        if (!posMap.isEmpty() || !scaleMap.isEmpty())
            main.system.auxiliary.log.LogMaster.log(1, "Stack off!\n " + posMap + "\n" + scaleMap);
        else
            return;
        for (GenericGridView view : posMap.keySet()) {
            Vector2 v = posMap.get(view);
            view.setHovered(false);
            view.setStackView(false);
            ActionMasterGdx.addMoveByAction(view, v.x, v.y, 1.2f);
        }
        for (GenericGridView view : scaleMap.keySet()) {
            ActionMasterGdx.addScaleAction(view, scaleMap.get(view), 1.4f);
        }
        posMap.clear();
        scaleMap.clear();
        waitToHideStack = 0;
        stackTimer = 0;
        stackCell.setStackView(false);
    }

    public void checkStackOff(BaseView object) {
        boolean stack = false;
        if (object instanceof GenericGridView) {
            if (((GenericGridView) object).isStackView()) {
                stack = true;
            }
        }
        if (stack) {
            waitToHideStack = WAIT_AFTER_HOVER_OFF;
        }
    }

    public void checkShowStack(BaseView object) {
        if (true)
            return;
        //TODO review this
        Coordinates c = object.getUserObject().getCoordinates();
        GridCellContainer cell = ScreenMaster.getGrid().getCells()[c.x][
                PositionMaster.getLogicalY(c.y)];


        if (cell.isStackView()) {
            waitToHideStack = 0; //user is browsing the stack, don't hide
        } else {
            if (!isStackHoverOn(cell)) return;
            stackOn(cell, false);  //open another stack
        }
    }

    private boolean isStackHoverOn(GridCellContainer cell) {
        if (Flags.isFootageMode())
            return false;
        if (isOff())
            return false;

        int n = cell.getUnitViewCount();
        if (OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.OPEN_OBJECT_STACKS_ON_ALT_HOVER)) {
            if (Gdx.input.isKeyPressed(Keys.ALT_LEFT))
                return true;
        }
        return n <=
                OptionsMaster.getControlOptions().
                        getIntValue(CONTROL_OPTION.MIN_OBJECTS_TO_OPEN_STACK_ON_HOVER);
    }

    private boolean isOff() {
        return true;
    }


}
