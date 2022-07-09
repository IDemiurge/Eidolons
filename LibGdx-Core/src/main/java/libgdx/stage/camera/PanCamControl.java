package libgdx.stage.camera;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.exploration.story.cinematic.Cinematics;
import libgdx.stage.camera.generic.CameraMotion;
import libgdx.stage.camera.generic.CameraOptions;
import libgdx.stage.camera.generic.MotionData;
import main.system.auxiliary.log.LOG_CHANNEL;
import static main.system.auxiliary.log.LogMaster.devLog;

public class PanCamControl extends CamControl {
    private boolean mustFinish;
    public PanCamControl(CameraMan cameraMan) {
        super(cameraMan);
    }

    @Override
    public void act(float delta) {

    }

    protected void cameraPan(Vector2 destination, float duration, Interpolation interpolation, Boolean overrideCheck) {
        float dst = cam.position.dst(destination.x, destination.y, 0f);// / getCameraDistanceFactor();

        if (!overrideCheck && !Cinematics.ON)  //&& !EidolonsGame.DUEL
            if (dst < getCameraMinCameraPanDist())
                return;

        if (duration == 0) {
            duration = 1 + dst / getPanSpeed();
        }
//        motions.add(new CameraMotion(this, duration, destination, interpolation));

    }


    private float getPanSpeed() {
        return 450;
    }

    protected float getCameraMinCameraPanDist() {
        return 200 * CameraOptions.options.CENTER_CAMERA_DISTANCE_MOD; //TODO if too close to the edge also
    }

    private void cameraPan(MotionData motionData) {
        //        if (motionData.exclusive)
//        if (motions.isEmpty()) {
//            mustFinish = false;
//        }
//        if (!motions.isEmpty()) {
//            if (!mustFinish || motionData.exclusive) {
//                devLog(LOG_CHANNEL.CAMERA, "cleared pan motions! ");
//                motions.clear();
//            } else {
//                devLog(LOG_CHANNEL.CAMERA, "mustFinish pan motions! ");
//                return;
//            }
//            //                return;
//        }
//        cameraPan(motionData.dest, motionData.duration, motionData.interpolation, null);

    }

    protected void cameraPan(Vector2 unitPosition, Boolean overrideCheck) {
        cameraPan(unitPosition, 0, Interpolation.fade, overrideCheck);
    }
}
