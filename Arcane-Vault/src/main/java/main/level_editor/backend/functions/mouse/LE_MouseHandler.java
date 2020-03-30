package main.level_editor.backend.functions.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.EUtils;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.screen.LE_Screen;
import main.system.threading.WaitMaster;

import java.util.concurrent.atomic.AtomicReference;

public class LE_MouseHandler extends LE_Handler {
    public static final WaitMaster.WAIT_OPERATIONS SELECTION_OPERATION = WaitMaster.WAIT_OPERATIONS.SELECT_BF_OBJ;
    //sync with CombatMouseListener
    private static CLICK_MODE[] clickModes;

    public LE_MouseHandler(LE_Manager manager) {
        super(manager);
    }


    public void handleCellClick(InputEvent event, int tapCount, int gridX, int gridY) {
//        LE_Selection selection = getSelectionHandler().getSelection();
        Coordinates c = Coordinates.get(gridX, gridY);
        switch (getSelectionHandler().getMode()) {
            case COORDINATE:
            case AREA:
                WaitMaster.receiveInput(SELECTION_OPERATION, Coordinates.get(gridX, gridY));
                return;
        }
        //check simplest click
//        selection.setCoordinates(new ListMaster<Coordinates>().toSet(Coordinates.get(gridX,gridY)));


        CLICK_MODE mode = getModeForClick(event, tapCount);
        clickedCell(mode, c);

    }

    private void clickedCell(CLICK_MODE mode, Coordinates c) {
        switch (mode) {
            case ALT:
                manager.getScriptHandler().editScriptData(c);
                break;
            case SHIFT:
                getSelectionHandler().addAreaToSelectedCoordinates(c);
                getSelectionHandler().areaSelected();
                //TODO add alternative w/o objs
                break;
            case CTRL:
                getSelectionHandler().addSelectedCoordinate(c);
                break;
            case NORMAL:
                getSelectionHandler().selectedCoordinate(c);
                break;
            case RIGHT:
                getObjHandler().addFromPalette(c);
                return;
            //copy metadata
            //delete top
            //delete all
            //select area
            case ALT_R:
                getStructureManager().insertBlock(getModel().getPaletteSelection().getTemplate(), c);
                // remap modules - put them far enough apart...
                //switch to adjacent?
                /*
                to be fair, it'd be much better to edit modules REALLY separately
                 */
                break;
            case ALT_CTRL:
                operation(Operation.LE_OPERATION.VOID_TOGGLE, c);
                break;
        }
    }

    public void handleObjectClick(InputEvent event, int tapCount, BattleFieldObject bfObj) {
        CLICK_MODE mode = getModeForClick(event, tapCount);

        switch (mode) {
            //replace
            //drag
            //edit
            //add to selection
            //remove
            case ALT_2:
                getSelectionHandler().select(bfObj);
                getModel().getPaletteSelection().setType(bfObj.getType());
                break;

            case SHIFT:
                clickedCell(mode, bfObj.getCoordinates());
                break;
            case SHIFT_R:
                //TODO check space!
                getObjHandler().addFromPalette(bfObj.getX(), bfObj.getY());
                break;
            case CTRL:
                getSelectionHandler().addToSelected(bfObj);
                break;
            case NORMAL:
                getSelectionHandler().select(bfObj);
                break;
            case DOUBLE:
            case RIGHT:
                if (getModel().getPaletteSelection().getObjTypeOverlaying() != null) {
                    if (bfObj instanceof Structure) {
//                    event.getStageY()
                        AtomicReference<DIRECTION> d = new AtomicReference<>(LE_Screen.getInstance().getGuiStage().getEnumChooser()
                                .chooseEnum(DIRECTION.class));
                        if (d.get() == null) {
                            EUtils.onConfirm("Random or center?", ()-> d.set(FacingMaster.getRandomFacing().getDirection()),
                                    ()-> {});
                        }
                        operation(Operation.LE_OPERATION.ADD_OVERLAY, getModel().getPaletteSelection().getObjTypeOverlaying(),
                                bfObj.getCoordinates(), d.get());
                        break;
                    }
                }
                operation(Operation.LE_OPERATION.REMOVE_OBJ, bfObj);
                break;
            case DOUBLE_RIGHT:
                break;

        }
    }

    public CLICK_MODE getModeForClick(InputEvent event, int tapCount) {
        boolean doubleClick = false;
        if (tapCount >= 2) {
            doubleClick = true;
        }
        boolean right = false;
        if (event.getButton() != 0) {
            right = true;
        }
        boolean shift = false;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            shift = true;
        }
        boolean ctrl = false;
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            ctrl = true;
        }
        boolean alt = false;
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
            alt = true;
        }
        for (CLICK_MODE value : CLICK_MODE.values()) {
            switch (value) {
                //check
                case ALT:
                    if (alt)
                        return value;
                    break;
                case CTRL:
                    if (ctrl)
                        return value;
                    break;
                case SHIFT:
                    if (shift)
                        return value;
                    break;
                case DOUBLE:
                    if (doubleClick)
                        return value;
                    break;
                case RIGHT:
                    if (right)
                        return value;
                    break;
                case DOUBLE_RIGHT:
                    if (doubleClick && right)
                        return value;
                    break;
                case ALT_CTRL:
                    if (alt && ctrl)
                        return value;
                    break;
                case ALT_SHIFT:
                    if (alt && shift)
                        return value;
                    break;
                case CTRL_SHIFT:
                    if (ctrl && shift)
                        return value;
                    break;
                case ALT_2:
                    if (alt && doubleClick)
                        return value;
                    break;
                case CTRL_2:
                    if (ctrl && doubleClick)
                        return value;
                    break;
                case SHIFT_2:
                    if (shift && doubleClick)
                        return value;
                    break;
                case ALT_CTRL_2:
                    if (alt && ctrl && doubleClick)
                        return value;
                    break;
                case ALT_SHIFT_2:
                    if (alt && shift && doubleClick)
                        return value;
                    break;
                case CTRL_SHIFT_2:
                    if (ctrl && shift && doubleClick)
                        return value;
                    break;
                case ALT_R:
                    if (alt && right)
                        return value;
                    break;
                case CTRL_R:
                    if (ctrl && right)
                        return value;
                    break;
                case SHIFT_R:
                    if (shift && right)
                        return value;
                    break;
                case ALT_CTRL_R:
                    if (alt && ctrl && right)
                        return value;
                    break;
                case ALT_SHIFT_R:
                    if (alt && shift && right)
                        return value;
                    break;
                case CTRL_SHIFT_R:
                    if (ctrl && shift && right)
                        return value;
                    break;
                case ALT_R_2:
                    if (alt && doubleClick && right)
                        return value;
                    break;
                case CTRL_R_2:
                    if (ctrl && doubleClick && right)
                        return value;
                    break;
                case SHIFT_R_2:
                    if (shift && doubleClick && right)
                        return value;
                    break;
                case ALT_CTRL_R_2:
                    if (alt && ctrl && doubleClick && right)
                        return value;
                    break;
                case ALT_SHIFT_R_2:
                    if (alt && shift && doubleClick && right)
                        return value;
                    break;
                case CTRL_SHIFT_R_2:
                    if (ctrl && shift && doubleClick && right)
                        return value;
                    break;
            }
        }
        return CLICK_MODE.NORMAL;
    }


    public enum CLICK_MODE {
        ALT_R_2,
        CTRL_R_2,
        SHIFT_R_2,
        ALT_CTRL_R_2,
        ALT_SHIFT_R_2,
        CTRL_SHIFT_R_2,
        ALT_CTRL_R,
        ALT_SHIFT_R,
        CTRL_SHIFT_R,
        ALT_CTRL_2,
        ALT_SHIFT_2,
        CTRL_SHIFT_2,

        ALT_CTRL,
        ALT_SHIFT,
        CTRL_SHIFT,
        ALT_2,
        CTRL_2,
        SHIFT_2,
        ALT_R,
        CTRL_R,
        SHIFT_R,
        DOUBLE_RIGHT,

        NORMAL,
        ALT,
        CTRL,
        SHIFT, RIGHT, DOUBLE

    }


    static {
        clickModes = CLICK_MODE.values();
    }
}
