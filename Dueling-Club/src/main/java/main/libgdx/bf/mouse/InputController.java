package main.libgdx.bf.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.anims.particles.lighting.FireLightProt;
import main.libgdx.anims.particles.lighting.LightMap;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.libgdx.bf.GridConst;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;

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

    public InputController(OrthographicCamera camera) {
        this.camera = camera;

    }

    @Override
    public boolean keyDown(int i) {
        if (i == ALT_LEFT) {
            alt = true;
        }
        if (i == CONTROL_LEFT) {
            ctrl = true;
        }

        if (i == 54) {
            LightMap.resizeFBOa();
        }
        if (i == 52) {
            LightMap.resizeFBOb();
        }

        return false;
        // alt = 57, crtl = 129
    }


    @Override
    public boolean keyUp(int i) {
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

        if (button == LEFT) {
            xCamPos = screenX;
            yCamPos = screenY;
            isLeftClick = true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        isLeftClick = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

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

    private boolean checkCameraPosLimitX(float x) {
        float max = MARGIN +
         DungeonScreen.getInstance().getGridPanel().getCols() * GridConst.CELL_W * camera.zoom;
        float min = -MARGIN;
        return (x > max || x < min);
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

        if (LightingManager.isMouse_light()) {
            LightMap.mouseMouseMove((camera.position.x - (camera.viewportWidth / 2) * camera.zoom + (i) * camera.zoom), (camera.position.y + (camera.viewportHeight / 2) * camera.zoom - i1 * camera.zoom), camera.zoom);
            float x_light = (camera.position.x - (camera.viewportWidth / 2) * camera.zoom + (i) * camera.zoom);
            float y_light = (camera.position.y + (camera.viewportHeight / 2) * camera.zoom - i1 * camera.zoom);
            boolean by_X;
            boolean by_Y;
            by_X = !(x_light < camera.position.x - (camera.viewportWidth / 2) * camera.zoom + LightingManager.mouse_light_distance_to_turn_off || x_light > camera.position.x + (camera.viewportWidth / 2) * camera.zoom - LightingManager.mouse_light_distance_to_turn_off);
            by_Y = !(y_light < camera.position.y - (camera.viewportHeight / 2) * camera.zoom + LightingManager.mouse_light_distance_to_turn_off || y_light > camera.position.y + (camera.viewportHeight / 2) * camera.zoom - LightingManager.mouse_light_distance_to_turn_off);
            if (by_X & by_Y) {
                LightMap.mouseLightDistance(LightingManager.mouse_light_distance);
            } else {
                LightMap.mouseLightDistance(0);
            }
        }
        return false;

    }

    @Override
    public boolean scrolled(int i) {

//        if (DialogDisplay.isDisplaying() )
//            return false;
//
        if (alt && !ctrl) {
            if (i == 1) {
                FireLightProt.setSmallerAlpha(FireLightProt.getAlphaSmaller() + 0.05f);
            }
            if (i == -1) {
                FireLightProt.setSmallerAlpha(FireLightProt.getAlphaSmaller() - 0.05f);
            }

        }
        if (!alt && ctrl) {
            if (i == 1) {
                FireLightProt.setBiggerAlpha(FireLightProt.getAlphaBigger() + 0.05f);
            }
            if (i == -1) {
                FireLightProt.setBiggerAlpha(FireLightProt.getAlphaBigger() - 0.05f);
            }

        }
        if (alt && ctrl) {
            if (i == 1) {
                LightMap.setAmbient(LightMap.getAmbient() + 0.02f);
            }
            if (i == -1) {
                LightMap.setAmbient(LightMap.getAmbient() - 0.02f);
            }

        }
        zoom(i);

//        System.out.println(camera.zoom);
        return false;
    }

    private void zoom(int i) {
        if (!alt && !ctrl) {
            if (i == 1) {
                camera.zoom += 0.25f;
            }
            if (i == -1) {
                if (camera.zoom >= 0.25f) {
                    camera.zoom -= 0.25f;
                }
            }
        }
    }

    public boolean isCellWithinCamera(int x, int y) {
        return isWithinCamera(GridConst.CELL_W * x, GridConst.CELL_H * y, GridConst.CELL_W, GridConst.CELL_H);
    }

    public boolean isWithinCamera(Actor actor) {
        return isWithinCamera(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
    }
        public boolean isWithinCamera(float x, float y, float width, float height) {
//            width = width / getZoom();
//            height = height / getZoom();
//            float minY = camera.position.y - Gdx.graphics.getHeight()/2;
//            float maxY = camera.position.y + Gdx.graphics.getHeight()/2;
//
//        float x1 =   x/getZoom();
//        if (camera.position.x - x1 <width ||camera.position.x - x1 >2*Gdx.graphics.getWidth())
//            return false;
        if (Math.abs(camera.position.x - x)-width > Gdx.graphics.getWidth() * getZoom() / 2)
            return false;
        if (Math.abs(camera.position.y - y)-height >  Gdx.graphics.getHeight() * getZoom() / 2)
//        if (y1-camera.position.y   <height ||y1-camera.position.y   >2*Gdx.graphics.getHeight())
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
        GuiEventManager.trigger(GuiEventType.ADD_LIGHT, new Vector2(v, v1));
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
