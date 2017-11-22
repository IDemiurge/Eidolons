package main.libgdx.bf.mouse;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.GdxMaster;
import main.libgdx.bf.GridConst;
import main.libgdx.screens.DungeonScreen;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import java.util.LinkedList;
import java.util.List;

import static com.badlogic.gdx.Input.Buttons.LEFT;
import static com.badlogic.gdx.Input.Keys.ALT_LEFT;
import static com.badlogic.gdx.Input.Keys.CONTROL_LEFT;

/**
 * Created by PC on 25.10.2016.
 */
public class InputController implements InputProcessor, GestureDetector.GestureListener {
    private static final float MARGIN = 300;
    private float xCamPos;
    private float yCamPos;
    private OrthographicCamera camera;
    private boolean isLeftClick = false;
    private boolean alt = false;
    private boolean ctrl = false;
    private char lastTyped;
    private List<String> charsUp = new LinkedList<>();
    private float width;
    private float height;
    float halfWidth;
    float halfHeight;
    private float zoomStep= OptionsMaster.getGraphicsOptions().
     getIntValue(GRAPHIC_OPTION.ZOOM_STEP)/new Float(100);

    public InputController(OrthographicCamera camera) {
        this.camera = camera;

        width = GdxMaster.getWidth() * getZoom();
        height = GdxMaster.getHeight() * getZoom();
        halfWidth = width/2;
        halfHeight = height/2;
    }

    public void setZoomStep(float zoomStep) {
        this.zoomStep = zoomStep;
    }

    @Override
    public boolean keyDown(int i) {
        if (isBlocked())
            return true;
        if (i == ALT_LEFT) {
            alt = true;
        }
        if (i == CONTROL_LEFT) {
            ctrl = true;
        }

//        if (i == 54) {
//            LightMap.resizeFBOa();
//        }
//        if (i == 52) {
//            LightMap.resizeFBOb();
//        }

        return false;
        // alt = 57, crtl = 129
    }

    private boolean isBlocked() {
        if (OptionsMaster.isMenuOpen())
            return true;
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        if (isBlocked())
            return true;
        switch (i) {
            case ALT_LEFT:
                alt = false;
                break;
            case CONTROL_LEFT:
                ctrl = false;
                break;
            default:
//                lastUp = ((char) i);
                String c = Keys.toString(i);//Character.valueOf((char) i);

                if (!charsUp.contains(c)) {
                    charsUp.add(c);
                }
                break;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        if (isBlocked())
            return true;
//        if (keyMap.get(c))
        String str = String.valueOf(c).toUpperCase();
        if (c == lastTyped) {
            if (!charsUp.contains(str)) {
                return false;
            }
        }
        charsUp.remove(str);
        lastTyped = c;

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (isBlocked())
            return true;
        if (button == LEFT) {
            xCamPos = screenX;
            yCamPos = screenY;
            isLeftClick = true;
        }
        DungeonScreen.getInstance().getGuiStage().outsideClick();
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if (isBlocked())
            return true;
        isLeftClick = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if (isBlocked())
            return true;
        if (isLeftClick) {
            tryPullCameraX(screenX);
            tryPullCameraY(screenY);
            DungeonScreen.getInstance().cameraStop();
        }

        return false;
    }

    private void tryPullCameraY(int screenY) {
        float diffY = (yCamPos - screenY) * camera.zoom;
        if (checkCameraPosLimitY(camera.position.y - diffY)) {
            camera.position.y -= diffY;
            yCamPos = screenY;
        }
    }

    private void tryPullCameraX(int screenX) {
        float diffX = (xCamPos - screenX) * camera.zoom;
        float max = MARGIN +
         DungeonScreen.getInstance().getGridPanel().getCols()
          * GridConst.CELL_W * camera.zoom;
        float min = -MARGIN;
        if (!(diffX > 0 && camera.position.x + diffX > max)
         || !(diffX < 0 && camera.position.x + diffX < min)) {
            camera.position.x += diffX;
            xCamPos = screenX;
        }
    }

    private boolean checkCameraPosLimitY(float y) {
        float max = MARGIN +
         DungeonScreen.getInstance().getGridPanel().getRows() * GridConst.CELL_H * camera.zoom;
        float min = -MARGIN;
        return !(y > max || y < min);
    }

    public void setDefaultPos() {
        centerAt(DC_Game.game.getPlayer(true).getHeroObj().getCoordinates());
    }

    private void centerAt(Coordinates coordinates) {
        float x = coordinates.x * GridConst.CELL_W * camera.zoom;
        float y = coordinates.x * GridConst.CELL_W * camera.zoom;
        camera.position.set(x, y, 0);

    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;

    }

    @Override
    public boolean scrolled(int i) {
        if (isBlocked())
            return true;
        zoom(i);
        return false;
    }

    private void zoom(int i) {
        if (!alt && !ctrl) {
            if (i == 1) {

                camera.zoom += zoomStep;
            }
            if (i == -1) {
                if (camera.zoom >= zoomStep) {
                    camera.zoom -= zoomStep;
                }
            }
        }
        width = GdxMaster.getWidth() * getZoom();
        height = GdxMaster.getHeight() * getZoom();
        halfWidth = width/2;
        halfHeight = height/2;
    }

    public boolean isWithinCamera(Actor actor) {
        return isWithinCamera(actor.getX() + actor.getWidth(), actor.getY() + actor.getHeight(), actor.getWidth(), actor.getHeight());
    }

    public boolean isWithinCamera(float x, float y, float width, float height) {
        float xPos = Math.abs(camera.position.x - x) - width;
        if (xPos > halfWidth)
            return false;
        float yPos = Math.abs(camera.position.y - y) - height;
        if (yPos > halfHeight)
            return false;

        return true;
    }

    public float getZoom() {
        return camera.zoom;
    }

    public float getXCamPos() {
        return xCamPos;
    }

    public float getYCamPos() {
        return yCamPos;
    }

    @Override
    public boolean touchDown(float v, float v1, int i, int i1) {
        return false;
    }

    @Override
    public boolean tap(float v, float v1, int i, int i1) {
        return false;
    }

    @Override
    public boolean longPress(float v, float v1) {
        return false;
    }

    @Override
    public boolean fling(float v, float v1, int i) {
        return false;
    }

    @Override
    public boolean pan(float v, float v1, float v2, float v3) {
        return false;
    }

    @Override
    public boolean panStop(float v, float v1, int i, int i1) {
        return false;
    }

    @Override
    public boolean zoom(float v, float v1) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector21, Vector2 vector22, Vector2 vector23) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

}
