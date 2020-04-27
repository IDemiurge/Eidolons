package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.game.core.EUtils;
import eidolons.system.controls.GlobalController;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_KeyHandler extends LE_Handler {

    GlobalController globalController = new GlobalController();

    public LE_KeyHandler(LE_Manager manager) {
        super(manager);
    }

    public void keyDown(int keyCode) {

        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        switch (keyCode) {
            case Input.Keys.TAB:
                globalController.keyDown(keyCode);
                GuiEventManager.trigger(GuiEventType.LE_GUI_TOGGLE);
                break;
            case Input.Keys.ALT_RIGHT:
                getModel().getDisplayMode().toggleAll();
                break;
            case Input.Keys.CONTROL_RIGHT:
                getModel().getDisplayMode().onAll();
                break;
            case Input.Keys.SHIFT_RIGHT:
                getModel().getDisplayMode().offAll();
                break;

            case Input.Keys.FORWARD_DEL:
            case Input.Keys.DEL:
                if (getModel().getBlock() != null) {
                    if (EUtils.waitConfirm("Delete block " + getModel().getBlock() + "?")) {
                        getStructureHandler().removeBlock();
                        return;
                    }
                }
                getObjHandler().removeSelected();
                break;
            case Input.Keys.ESCAPE:
                //do we have a 'main menu'?
                getSelectionHandler().deselect();
                break;
            case Input.Keys.ENTER:
                LE_Screen.getInstance().getGuiStage().getDialog().ok();
                //approve dialogue?
                break;
            case Input.Keys.SPACE:
                getCameraHandler().cycleCameraMode();
                break;
            //camera?
        }
    }

    public void keyTyped(char character) {
        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        if (ctrl) {
            switch (character) {
                case ' ':
                    LevelEditor.getCurrent().getManager().getEditHandler().edit();
                    break;
                case 'X':
                case 'x':
                    LevelEditor.getCurrent().getManager().getModelManager().cut();
                    break;
                case 'Z':
                case 'z':
                    LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                    break;
                case 'Y':
                case 'y':
                    LevelEditor.getCurrent().getManager().getOperationHandler().redo();
                    break;
                case 'C':
                case 'c':
                    LevelEditor.getCurrent().getManager().getModelManager().copy();
                    break;
                case 'V':
                case 'v':
                    LevelEditor.getCurrent().getManager().getModelManager().paste();
                    break;

            }
        } else {
            switch (character) {
                case Input.Keys.TAB:
                    globalController.keyDown(character);
                    break;
            }
        }
    }
}
