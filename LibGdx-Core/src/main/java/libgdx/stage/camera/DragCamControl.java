package libgdx.stage.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.stage.camera.generic.MotionData;
import main.system.math.MathMaster;

import static main.system.auxiliary.log.LogMaster.devLog;

public class DragCamControl extends CamControl {
    private MoveToAction drag;

    public DragCamControl(CameraMan cameraMan) {
        super(cameraMan);
    }

    public void drag(int screenX, int screenY) {
        if (drag!=null)
            if (drag.getTime() < drag.getDuration())
                return;

        ////TODO how to INVERT X properly?
//        screenX = (int) (getCam().position.x+(getCam().position.x - screenX));

        //camera limits
//        screenX = MathMaster.minMax(screenX, getCamMinX(), getCamMaxX());
//        screenY = MathMaster.minMax(screenY, getCamMinY(), getCamMaxY());

        System.out.printf("dragged to %2.0f : %2.0f \n", new Float(screenX), new Float(screenY));
        drag =  ActionMasterGdx.getMoveToAction(screenX, screenY, 0.01F );
        Actor dummy = new Actor();
        dummy.setPosition(cam.position.x, cam.position.y);
        drag.setTarget(dummy);
        drag.setInterpolation(Interpolation.smooth);
    }




    public void act(float delta) {
        if (drag != null) {
            if (drag.getTime() < drag.getDuration()) {
                drag.act(delta);
//                int diffX= (int) (cam.position.x-(drag.getX()));
//                int diffY= (int) (cam.position.y-(drag.getY()));
                cam.position.x=(drag.getX());
                cam.position.y=(drag.getY());

//                Gdx.input.setCursorPosition( Gdx.input.getX()+diffX, Gdx.input.getY()+diffY);
               cameraMan.cameraChanged();
                cam.update();
            }
        }
//        if (camController.isArrowMotionsOn()) {
//            for (int key : DragCamControl.keys) {
//                if (Gdx.input.isKeyPressed(key)) {
//                    camController.keyDown(key, delta);
//                }
//            }
//        }
    }

    public InputProcessor createDragGestureHandler() {
        return new GestureDetector(new DragGestureHandler(cam));
    }
}