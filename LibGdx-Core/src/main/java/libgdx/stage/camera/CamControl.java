package libgdx.stage.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class CamControl {

    protected final OrthographicCamera cam;
    protected CameraMan cameraMan;


    public CamControl(CameraMan cameraMan) {
        this.cameraMan = cameraMan;
        this.cam = (OrthographicCamera) cameraMan.getCam();
    }

    protected float getX() {
        return cam.position.x;
    }

    protected float getY() {
        return cam.position.y;
    }

    protected void setZoom(float value) {
        cam.zoom=value;
    }

    protected float getZoom() {
        return cam.zoom;
    }

    public abstract void act(float delta);
}
