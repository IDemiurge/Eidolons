package main.level_editor.gui.screen;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import eidolons.libgdx.bf.mouse.DungeonInputController;
import eidolons.libgdx.screens.DungeonScreen;
import main.level_editor.functions.LE_Manager;
import main.level_editor.struct.level.Floor;

public class LE_InputProcessor extends DungeonInputController {

    public LE_InputProcessor(OrthographicCamera camera, Floor parameter) {
        super(camera);
    }

    @Override
    protected float getDefaultZoom() {
        return 0.33f;
    }

    @Override
    protected float getPreferredMinimumOfScreenToFitOnDisplay() {
        return 0.1f;
    }

    @Override
    protected LE_Screen getScreen() {
        return LE_Screen.getInstance ();
    }
}
