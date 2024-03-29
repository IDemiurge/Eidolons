package libgdx.stage.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import libgdx.stage.camera.generic.CameraMotion;
import libgdx.utils.ActTimer;
import main.game.bf.directions.DIRECTION;

import java.util.HashMap;
import java.util.Map;

public class BorderCamControl extends CamControl {
    private final float period = 2f;
    private boolean reset;
    DIRECTION mouseBorder;
    private final ActTimer cameraTimer;
    Map<DIRECTION, CameraMotion> moveMap = new HashMap<>();
    private CameraMotion motion;

    public BorderCamControl(CameraMan cameraMan) {
        super(cameraMan);
        cameraTimer = new ActTimer(period, () -> this.reset = false);
    }

    boolean isBorderMouseMotionsOn() {
        return false;
    }

    public CameraMotion getMotion() {
        return motion;
    }

    public void act(float delta) {
        if (!isBorderMouseMotionsOn())
            return;
        cameraTimer.act(delta);
        if (reset)
            return;
        mouseBorder = getMouseBorder();

        //support same logic via arrows?
        if (mouseBorder != null) {
            motion = move(mouseBorder);
            System.out.printf("Mouse Border = %s%n", mouseBorder);
            reset = true;
            //push mouse back from screen edge!
//                Gdx.input.setCursorPosition( Gdx.input.getX()+diffX, Gdx.input.getY()+diffY);
        }
    }

    public DIRECTION getMouseBorder() {
        float mouseBorderBuffer = 50 * cam.zoom;
        float min = getX() - cameraMan.halfWidth * cam.zoom + mouseBorderBuffer*3;
        if (Gdx.input.getX() < min)
            return DIRECTION.LEFT;
        float max = getX() + cameraMan.halfWidth * cam.zoom - mouseBorderBuffer*3;
        if (Gdx.input.getX() >= max)
            return DIRECTION.RIGHT;

        min = getY() - cameraMan.halfHeight * cam.zoom + mouseBorderBuffer;
        if (Gdx.input.getY() < min)
            return DIRECTION.UP;
        max = getY() + cameraMan.halfHeight * cam.zoom - mouseBorderBuffer;
        if (Gdx.input.getY() >= max)
        return DIRECTION.DOWN;

        return null;
    }


    public CameraMotion move(DIRECTION direction) {
        float xDiff = 0;
        float yDiff = 0;
        // ???????
        float step = 200 * getZoom() * getZoom();
        switch (direction) {
            case UP:
                yDiff = step;
                break;
            case DOWN:
                yDiff = -step;
                break;
            case LEFT:
                xDiff = -step;
                break;
            case RIGHT:
                xDiff = step;
                break;
        }
        //this may be wrong design!
        float x;
        float y;
        x = cam.position.x;
        y = cam.position.y;
        motion = new CameraMotion(cameraMan, 1f, new Vector2(x, y), Interpolation.pow2Out);
        motion.getSpeedActionX().setStart(x);
        motion.getSpeedActionY().setStart(y);
        motion.getSpeedActionX().setEnd(x + xDiff);
        motion.getSpeedActionY().setEnd(y + yDiff);
        return motion;
    }


}
