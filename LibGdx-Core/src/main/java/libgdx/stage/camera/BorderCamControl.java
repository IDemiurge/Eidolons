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
    private final float period= 2f;
    private boolean reset;
    DIRECTION mouseBorder;
    private final ActTimer cameraTimer;
    Map<DIRECTION, CameraMotion> moveMap = new HashMap<>();
    private CameraMotion motion;

    public BorderCamControl(CameraMan cameraMan) {
        super(cameraMan);
        cameraTimer = new ActTimer(period, ()-> this.reset = false);
    }

    boolean isBorderMouseMotionsOn() {
        return true;
    }
    public CameraMotion getMotion() {
        return motion;
    }
    public void act(float delta) {
        if (!isBorderMouseMotionsOn())
            return ;
        cameraTimer.act(delta);
        if (reset)
            return;
        mouseBorder = getMouseBorder();

        //support same logic via arrows?
        if (mouseBorder != null) {
            switch (mouseBorder) {
                case UP:
                    motion = move(mouseBorder);
                    break;
                case DOWN:
                    break;
                case LEFT:
                    break;
                case RIGHT:
                    break;
            }
            reset = true;
            //push mouse back from screen edge!
//                Gdx.input.setCursorPosition( Gdx.input.getX()+diffX, Gdx.input.getY()+diffY);
        }
    }

    public DIRECTION getMouseBorder() {
        float mouseBorderBuffer=100*cam.zoom;
        float min=getX() -cameraMan.halfWidth*cam.zoom + mouseBorderBuffer;
        if (Gdx.input.getX()<min)
            return DIRECTION.LEFT;
        float max=getX() + cameraMan.halfWidth*cam.zoom - mouseBorderBuffer;
        if (Gdx.input.getX()>=max)
            return DIRECTION.RIGHT;
        return null;
    }


    public CameraMotion move(DIRECTION direction) {
        float xDiff = 0;
        float yDiff = 0;
        // ???????
        float step = 200 * getZoom() * getZoom() ;
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
        CameraMotion motion = moveMap.get(direction);
        //this may be wrong design!
        float x;
        float y;
        x = cam.position.x;
        y = cam.position.y;
        if (motion == null) {
            moveMap.put(direction, motion = new CameraMotion(cameraMan, 0.1f, new Vector2(x, y), Interpolation.pow2Out));
        } else {
            motion.reset(0.1f);
        }
        motion.getSpeedActionX().setStart(x);
        motion.getSpeedActionY().setStart(y);
        motion.getSpeedActionX().setEnd(x + xDiff);
        motion.getSpeedActionY().setEnd(y + yDiff);
        return motion;
    }


}
