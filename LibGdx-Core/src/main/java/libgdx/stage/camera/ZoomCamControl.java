package libgdx.stage.camera;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.stage.camera.generic.MotionData;

import static main.system.auxiliary.log.LogMaster.devLog;

public class ZoomCamControl extends CamControl {
    private FloatAction zoomAction;

    public ZoomCamControl(CameraMan cameraMan) {
        super(cameraMan);
        cam.zoom = 0.5f;
        initialMotion();
    }

    private void initialMotion() {
        zoom(1f, 4f, true);
    }

    public void zoom(float zoom) {
        zoom(zoom, 0.5f, false);
    }

    public void zoom(float zoom, float duration, boolean setTo) {
        if (zoomAction != null)
            if (zoomAction.getTime() < zoomAction.getDuration())
                return;
        zoomAction = (FloatAction) ActionMasterGdx.getAction(FloatAction.class);
        zoomAction.setStart(getZoom());
        if (setTo)
            zoomAction.setEnd( zoom);
        else
            zoomAction.setEnd(getZoom() + zoom);
        zoomAction.setDuration(duration);
        zoomAction.setInterpolation(Interpolation.smooth);
    }

    public void zoom(MotionData data) {
        zoomAction = (FloatAction) ActionMasterGdx.getAction(FloatAction.class);
        zoomAction.setStart(getZoom());
        zoomAction.setEnd(data.zoom);
        if (data.duration <= 0) {
            data.duration = Math.abs(getZoom() - data.zoom) * 15;
        }
        zoomAction.setDuration(data.duration);
        zoomAction.setInterpolation(data.interpolation);
        devLog("Zooming to " + data.zoom);
    }

    @Override
    public void act(float delta) {

        if (zoomAction != null) {
            if (zoomAction.getTime() < zoomAction.getDuration()) {
                zoomAction.act(delta);
                setZoom(zoomAction.getValue());
                cameraMan.cameraChanged();
                cam.update();
            }
        }
    }
}
