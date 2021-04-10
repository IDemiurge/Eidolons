package libgdx.bf.mouse;

import com.badlogic.gdx.graphics.OrthographicCamera;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.screens.dungeon.GenericDungeonScreen;

/**
 * Created by JustMe on 2/7/2018.
 */
public class DungeonInputController extends InputController {
//    float cameraMaxX;
//    float cameraMaxX;
//    float cameraMaxX;
//    float cameraMaxX;

    public DungeonInputController(OrthographicCamera camera) {
        super(camera);
    }

    protected void outsideClick() {
        getScreen().getGuiStage().outsideClick();
    }

    @Override
    protected float getOffsetX() {
        return getScreen().getGridPanel().getOffsetX();
    }

    @Override
    protected float getOffsetY() {
        return getScreen().getGridPanel().getOffsetY();
    }
    @Override
    protected GenericDungeonScreen getScreen() {
        return DungeonScreen.getInstance();
    }

    protected float getHeight() {
        return getScreen().getGridPanel().getHeight();
    }

    @Override
    public boolean isWithinCamera(float x, float y, float width, float height) {
        return super.isWithinCamera(x, y, width, height);
    }

    @Override
    protected float getWidth() {
        return getScreen().getGridPanel().getWidth() ;
    }
    @Override
    protected float getMargin() {
        return 1100;
    }

}
