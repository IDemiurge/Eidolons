package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.game.core.EUtils;
import eidolons.libgdx.GdxMaster;
import eidolons.system.controls.GlobalController;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_KeyHandler extends LE_Handler {

    GlobalController globalController = new GlobalController();

    public LE_KeyHandler(LE_Manager manager) {
        super(manager);
    }

    public void keyDown(int keyCode) {

        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ;
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (!alt && !ctrl) switch (keyCode) {
            case Input.Keys.TAB:
                globalController.keyDown(keyCode);
                GuiEventManager.trigger(GuiEventType.LE_GUI_TOGGLE);
                return ;
            case Input.Keys.B:
              getModel().setBrushMode(!getModel().isBrushMode());
                return ;
            case Input.Keys.ALT_RIGHT:
                getModel().getDisplayMode().toggleAll();
                return ;
            case Input.Keys.CONTROL_RIGHT:
                getModel().getDisplayMode().onAll();
                return ;
            case Input.Keys.SHIFT_RIGHT:
                getModel().getDisplayMode().offAll();
                return ;

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
                getObjHandler().removeSelected();
                return ;
            case Input.Keys.ESCAPE:
                //do we have a 'main menu'?
                if (GdxMaster.isVisibleEffectively(LE_Screen.getInstance().getGuiStage().getDialog())
                ) {
                     LE_Screen.getInstance().getGuiStage().getDialog().cancel();
                } else
                    getSelectionHandler().deselect();
                return ;
            case Input.Keys.ENTER:
                LE_Screen.getInstance().getGuiStage().getDialog().ok();
                //approve dialogue?
                return ;
            //camera?
        }
        if (alt) {
            switch (keyCode) {
                case Input.Keys.SPACE:
                    getCameraHandler().cycleCameraMode();
                    return ;
            }
        }
    }

    public void keyTyped(char character) {

        main.system.auxiliary.log.LogMaster.log(1,"keyTyped "+character );
    }
    public void keyUp(int keyCode) {
        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        if (alt) {
            switch (keyCode) {
                case Input.Keys.V :
                    LevelEditor.getCurrent().getManager().getDataHandler().saveVersion();
                    return ;
                case Input.Keys.F:
                    LevelEditor.getCurrent().getManager().getAdvFuncs().fill();
                    return ;
                case Input.Keys.S:
                    LevelEditor.getCurrent().getManager().getDataHandler().saveFloor();
                    return ;
                case Input.Keys.M:
                    LevelEditor.getCurrent().getManager().getDataHandler().saveModulesSeparately();
                    return ;
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
                    return ;
                case Input.Keys.Q:
                    LevelEditor.getCurrent().getManager().getAdvFuncs().toggleVoid( );
                    return ;
                case Input.Keys.M:
                    LevelEditor.getCurrent().getManager().getDataHandler().saveModule(getModel().getModule());
                    return ;
                case Input.Keys.S:
                    LevelEditor.getCurrent().getManager().getDataHandler().saveAs();
                    return ;
                case Input.Keys.SPACE:
                    LevelEditor.getCurrent().getManager().getEditHandler().edit();
                    return ;
                case Input.Keys.X:
                    LevelEditor.getCurrent().getManager().getModelManager().cut();
                    return ;
                case Input.Keys.Z:
                    LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                    return ;
                case Input.Keys.Y:
                    LevelEditor.getCurrent().getManager().getOperationHandler().redo();
                    return ;
                case Input.Keys.C:
                    LevelEditor.getCurrent().getManager().getModelManager().copy();
                    return ;
                case Input.Keys.V:
                    LevelEditor.getCurrent().getManager().getModelManager().paste();
                    return ;

            }
        } else {
            switch (keyCode) {
                case Input.Keys.TAB:
                    globalController.keyDown(keyCode);
                    return ;
            }
        }
    }

}
