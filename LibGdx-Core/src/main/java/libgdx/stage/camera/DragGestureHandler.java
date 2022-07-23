package libgdx.stage.camera;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;

public class DragGestureHandler  extends GestureDetector.GestureAdapter  {
    private OrthographicCamera cam;

    public DragGestureHandler(OrthographicCamera cam) {
        this.cam = cam;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        cam.position.x += deltaX;
        cam.position.y += deltaY;
        cam.update();
        return false;
    }
}
