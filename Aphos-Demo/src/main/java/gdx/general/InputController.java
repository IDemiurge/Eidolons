package gdx.general;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import libgdx.GdxMaster;
import libgdx.stage.camera.CameraMan;
import main.system.math.MathMaster;

public class InputController implements InputProcessor {
    protected static final float MARGIN = 300;

    protected static float zoomStep_ = 0.1f;
    private final CameraMan cameraMan;
    protected float zoomStep;
    protected float defaultZoom;

    protected OrthographicCamera camera;
    protected float width;
    protected float height;
    protected int mouseButtonPressed;
    protected float xTouchPos;
    protected float yTouchPos;
    protected static float halfWidth;
    protected static float halfHeight;
    protected static boolean unlimitedZoom;
    protected static boolean dragOff;

    public InputController(CameraMan cameraMan) {
        this.cameraMan = cameraMan;
        this.camera = (OrthographicCamera) cameraMan.getCam();

        width = GdxMaster.getWidth() * getZoom();
        height = GdxMaster.getHeight() * getZoom();
        halfWidth = width / 2;
        halfHeight = height / 2;
        try {
            initZoom();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    protected void initZoom() {
        //TODO
        defaultZoom= 1; //should depend on resolution
//        defaultZoom = Math.min(getWidth() * getPreferredMinimumOfScreenToFitOnDisplay()
//                / GdxMaster.getWidth(), 1f);
//        defaultZoom = Math.min(getHeight() * getPreferredMinimumOfScreenToFitOnDisplay()
//                / GdxMaster.getHeight(), defaultZoom);
        camera.zoom = defaultZoom;

        zoomStep = zoomStep_ * defaultZoom;

        width = GdxMaster.getWidth() * camera.zoom;
        height = GdxMaster.getHeight() * camera.zoom;
        halfWidth = width / 2;
        halfHeight = height / 2;
    }
    public float getZoom() {
        return camera.zoom;
    }
    protected float getHeight() {
        //TODO
//        return getScreen().getGridPanel().getHeight();
        return 0;
    }

    protected float getWidth() {
        //TODO
//        return getScreen().getGridPanel().getWidth() ;
        return 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //some default behavior? to make it feel more alive and responsive
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    ////////////////////// DRAG ///////////////////
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        if (isDragOff())
//            return false;
//        if (isBlocked())
//            return false;
//        if (isManualCameraDisabled())
//            return false;
//        if (mouseButtonPressed == Input.Buttons.LEFT) {
//            System.out.printf("dragged to %2.0f:%2.0f", camera.position.x, camera.position.y);
//            camera.position.x += screenX;
//            camera.position.y += screenY;
//            if (is)
//            tryPullCameraX(screenX);
//            tryPullCameraY(screenY);
//            cameraMan.drag(screenX, screenY);
//            cameraStop();
//        }
        return false;
    }

    private void cameraStop() {
        //TODO
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    protected boolean isFreeDrag() {
        return true;
    }

    protected void tryPullCameraY(int screenY) {
        float diffY = (yTouchPos - screenY) * camera.zoom * getDragCoef();
        if (isFreeDrag()) {
            camera.position.y = camera.position.y - diffY;
        } else
            camera.position.y = MathMaster.getMinMax(
                    camera.position.y - diffY,
                    halfHeight - getMargin(),
                    getHeight() - halfHeight + getMargin());
        yTouchPos = screenY;
//is it useful to push cursor as well?
//        Gdx.input.setCursorPosition(Gdx.input.getX(), (int) yTouchPos);
    }


    protected void tryPullCameraX(int screenX) {
        //TODO custom bounds esp DOWN!
        float diffX = (xTouchPos - screenX) * camera.zoom * getDragCoef();
        if (isFreeDrag()) {
            camera.position.x = camera.position.x + diffX;
        } else
            camera.position.x = MathMaster.getMinMax(
                    camera.position.x + diffX,//-getMargin(),
                    halfWidth - getMargin(),
                    getWidth() - halfWidth + getMargin());
        xTouchPos = screenX;
//        Gdx.input.setCursorPosition((int) xTouchPos, Gdx.input.getY());
    }

    protected float getDragCoef() {
        return 1f;
    }


    protected float getMargin() {
        return MARGIN;
    }

    ////////////////////// ZOOM ///////////////////

    @Override
    public boolean scrolled(int i) {
//        if (isBlocked())
//            return true;
        zoom(i);
        return false;
    }


    protected boolean zoom(int i) {
        // if (!isUnlimitedZoom())
        //     if (!checkZoom(i))
        //         return false;

        //TODO Smooth Zoom!
//        FloatActionLimited zoomAction = new FloatActionLimited();
//        zoomAction.setValue();

        if (isSmoothZoom()){
            cameraMan.zoom(i);
        } else {
            if (i == 1) {
                camera.zoom += zoomStep;
            }
            if (i == -1) {
                if (camera.zoom >= zoomStep) {
                    camera.zoom -= zoomStep;
                }
            }
            if (camera.zoom < 0)
                camera.zoom = defaultZoom;

            cameraZoomChanged();
        }

        return true;
    }

    private boolean isSmoothZoom() {
        return true;
    }

    public void cameraZoomChanged() {
        width = GdxMaster.getWidth() * camera.zoom;
        height = GdxMaster.getHeight() * camera.zoom;
        halfWidth = width / 2;
        halfHeight = height / 2;
    }


}
