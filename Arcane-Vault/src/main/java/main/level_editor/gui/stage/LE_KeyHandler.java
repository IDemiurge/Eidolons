package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.game.core.EUtils;
import eidolons.libgdx.GdxMaster;
import eidolons.system.controls.GlobalController;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.gui.dialog.ChooserDialog;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.*;

public class LE_KeyHandler extends LE_Handler {

    GlobalController globalController = new GlobalController();

    public LE_KeyHandler(LE_Manager manager) {
        super(manager);
    }

    public void keyDown(int keyCode) {

        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (!alt && !ctrl) {
            ChooserDialog dialog = LE_Screen.getInstance().getGuiStage().getDialog();
            switch (keyCode) {
                case Input.Keys.F1:
                    getDisplayHandler().gameView();
                    break;
                case Input.Keys.TAB:
                    globalController.keyDown(keyCode);
                    GuiEventManager.trigger(GuiEventType.LE_GUI_TOGGLE);
                    return;
                case Input.Keys.B:
                    getModel().setBrushMode(!getModel().isBrushMode());
                    return;
                case Input.Keys.V:
                    operation(CLEAR_START);
                    for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
                        operation(VOID_SET, c);
                    }
                    operation(CLEAR_END);
                    return;
                case Input.Keys.ALT_RIGHT:
                    getModel().getDisplayMode().toggleAll();
                    return;
                case Input.Keys.CONTROL_RIGHT:
                    getModel().getDisplayMode().onAll();
                    return;
                case Input.Keys.SHIFT_RIGHT:
                    getModel().getDisplayMode().offAll();
                    return;

                case Input.Keys.FORWARD_DEL:
                case Input.Keys.DEL:
                    if (LE_GuiStage.dialogActive) {
                        return;
                    }
                    if (getModel().getBlock() != null) {
                        if (EUtils.waitConfirm("Delete block " + getModel().getBlock() + "?")) {
                            getStructureHandler().removeBlock();
                            return;
                        }
                    }
                    getModelManager().clear();
                    return;
                case Input.Keys.ESCAPE:
                    //do we have a 'main menu'?
                    if (GdxMaster.isVisibleEffectively(LE_Screen.getInstance().getGuiStage().getTextInputPanel())) {
                        LE_Screen.getInstance().getGuiStage().getTextInputPanel().close();
                    } else if (GdxMaster.isVisibleEffectively(dialog)) {
                        dialog.cancel();
                    } else {
                        getSelectionHandler().deselect();
                    }
                    return;
                case Input.Keys.ENTER:
                    dialog.ok();
                    //approve dialogue?
                    return;
                //camera?
            }
        }
        if (alt) {
            switch (keyCode) {
                case Input.Keys.SPACE:
                    getCameraHandler().cycleCameraMode();
                    return;
                case Input.Keys.Q:
                    manager.cycleLayer();
                    return;
            }
        }
        switch (keyCode) {
            case Input.Keys.UP:
            case Input.Keys.DOWN:
            case Input.Keys.RIGHT:
            case Input.Keys.LEFT:
                DIRECTION d = getDirectionForKey(keyCode, alt);
                if (ctrl) {
                    LevelEditor.getManager().getOperationHandler().move(d);
                } else {
                    LevelEditor.getManager().getDecorHandler().offset(d);

                }
        }
    }

    public void keyTyped(char character) {
        main.system.auxiliary.log.LogMaster.log(1, "keyTyped " + character);
        GuiEventManager.trigger(GuiEventType.KEY_TYPED, (int) character);
    }

    public void keyUp(int keyCode) {
        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (alt && ctrl) {
            switch (keyCode) {
                case Input.Keys.A:
                    getModel().toggleAppend();
                    break;
            }
            }
            if (alt) {
                switch (keyCode) {
                    case Input.Keys.V:
                        LevelEditor.getCurrent().getManager().getDataHandler().saveVersion();
                        return;
                    case Input.Keys.F:
                        LevelEditor.getCurrent().getManager().getAdvFuncs().fill();
                        return;
                    case Input.Keys.S:
                        LevelEditor.getCurrent().getManager().getDataHandler().saveFloor();
                        return;
                    case Input.Keys.M:
                        LevelEditor.getCurrent().getManager().getDataHandler().saveModulesSeparately();
                        return;
                }
            }
            if (ctrl) {
                switch (keyCode) {
                    case Input.Keys.R:

                        LE_Screen.getInstance().getGuiStage().getPalettePanel().reload();
                        GuiEventManager.trigger(GuiEventType.LE_PALETTE_RESELECT);
                        break;
                    case Input.Keys.TAB:

                        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
                        if (shift) {
                            FloorManager.selectPreviousFloor();
                        } else {
                            FloorManager.selectNextFloor();
                        }
                        return;
                    case Input.Keys.Q:
                        LevelEditor.getCurrent().getManager().getAdvFuncs().toggleVoid();
                        return;
                    case Input.Keys.M:
                        LevelEditor.getCurrent().getManager().getDataHandler().saveModule(getModel().getModule());
                        return;
                    case Input.Keys.S:
                        LevelEditor.getCurrent().getManager().getDataHandler().saveAs();
                        return;
                    case Input.Keys.SPACE:
                        LevelEditor.getCurrent().getManager().getEditHandler().edit();
                        return;
                    case Input.Keys.X:
                        LevelEditor.getCurrent().getManager().getModelManager().cut();
                        return;
                    case Input.Keys.Z:
                        LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                        return;
                    case Input.Keys.Y:
                        LevelEditor.getCurrent().getManager().getOperationHandler().redo();
                        return;
                    case Input.Keys.C:
                        LevelEditor.getCurrent().getManager().getModelManager().copy();
                        return;
                    case Input.Keys.V:
                        LevelEditor.getCurrent().getManager().getModelManager().paste();
                        return;

                }
            } else {
                switch (keyCode) {
                    case Input.Keys.F8:
                    case Input.Keys.F7:
                    case Input.Keys.TAB:
                    case Input.Keys.HOME:
                        globalController.keyDown(keyCode);
                        return;
                }
            }

        }

        private DIRECTION getDirectionForKey ( int keyCode, boolean alt){
            switch (keyCode) {
                case Input.Keys.UP:
                    return alt ? DIRECTION.UP_LEFT : DIRECTION.UP;
                case Input.Keys.DOWN:
                    return alt ? DIRECTION.DOWN_LEFT : DIRECTION.DOWN;
                case Input.Keys.RIGHT:
                    return alt ? DIRECTION.UP_RIGHT : DIRECTION.RIGHT;
                case Input.Keys.LEFT:
                    return alt ? DIRECTION.DOWN_RIGHT : DIRECTION.LEFT;

            }
            return null;
        }
    }
