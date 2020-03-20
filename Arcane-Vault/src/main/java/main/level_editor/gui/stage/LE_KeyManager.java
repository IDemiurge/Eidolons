package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.system.hotkey.DC_KeyManager;
import main.level_editor.LevelEditor;

public class LE_KeyManager extends DC_KeyManager {
    public LE_KeyManager( ) {
    }

    @Override
    public boolean handleKeyDown(int keyCode) {
        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (ctrl){
            switch (keyCode) {
                case Input.Keys.SPACE:
                    LevelEditor.getCurrent().getManager().getEditHandler().edit();
                    break;
                case Input.Keys.Z:
                    LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                    break;
                case Input.Keys.Y:
                    LevelEditor.getCurrent().getManager().getOperationHandler().redo();
                    break;
                case Input.Keys.C:
                    LevelEditor.getCurrent().getManager().getModelManager().copy();
                    break;
                case Input.Keys.V:
                    LevelEditor.getCurrent().getManager().getModelManager().paste();
                    break;

            }

        }
        switch (keyCode) {
            case Input.Keys.ESCAPE:
                //do we have a 'main menu'?
            case Input.Keys.ENTER:
                //approve dialogue?
            case Input.Keys.SPACE:
                //camera?

                return true;
        }
        return false;
    }
}
