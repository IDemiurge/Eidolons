package main.level_editor.gui.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import libgdx.bf.mouse.DungeonInputController;
import main.level_editor.backend.struct.level.LE_Floor;

public class LE_InputController extends DungeonInputController {

    public LE_InputController(OrthographicCamera camera, LE_Floor parameter) {
        super(camera);
        setUnlimitedZoom(true);

    }

    @Override
    protected void initZoom() {
        super.initZoom();
        zoomStep=0.1f;
    }

    @Override
    public boolean scrolled(int i) {
        if (getScreen().getGuiStage().getScrollFocus() != null) {
            return false;
        }
        return super.scrolled(i);
    }


    @Override
    public boolean mouseMoved(int i, int i1) {
        try {
            if (getScreen().getGuiStage().hit(i, i1, true) == null) {
                if (getScreen().getGuiStage().getScrollFocus() != null) {
                    getScreen().getGuiStage().unfocus(getScreen().getGuiStage().getScrollFocus());
                }
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return super.mouseMoved(i, i1);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (getScreen().getGuiStage().hit(screenX, screenY, true) != null) {
            return false;
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    protected boolean isFreeDrag() {
        return true;
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
        return LE_Screen.getInstance();
    }
}
