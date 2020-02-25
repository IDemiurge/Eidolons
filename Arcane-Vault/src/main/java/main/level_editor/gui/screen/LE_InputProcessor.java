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
        setZoomStep(0.2f);
        setUnlimitedZoom(true);

    }

    @Override
    protected float getMargin() {
        return 1200;
    }

    protected float getDragCoef() {
        return 1.5f;
    }

    @Override
    protected float getDefaultZoom() {
        return 1.73f;
    }

    @Override
    protected float getPreferredMinimumOfScreenToFitOnDisplay() {
        return 2.0f;
    }

    @Override
    protected LE_Screen getScreen() {
        return LE_Screen.getInstance ();
    }
}
