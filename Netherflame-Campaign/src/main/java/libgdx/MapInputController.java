package libgdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import libgdx.GdxMaster;
import libgdx.map.MapScreen;
import main.system.launch.Flags;

/**
 * Created by JustMe on 2/7/2018.
 */
public class MapInputController extends InputController {
    public MapInputController(OrthographicCamera camera) {
        super(camera);
    }

    @Override
    protected float getWidth() {
        return MapScreen.defaultSize;
//         MapScreen.getInstance().getMapStage().getMap().getWidth();
    }

    @Override
    protected void initZoom() {
        super.initZoom();
        if (Flags.isMapPreview()) {
            defaultZoom = 1;
            camera.zoom = defaultZoom;
            zoomStep = InputController.zoomStep_ * defaultZoom;

            width = GdxMaster.getWidth() * camera.zoom;
            height = GdxMaster.getHeight() * camera.zoom;
            InputController.halfWidth = width / 2;
            InputController.halfHeight = height / 2;
        }
    }

    @Override
    protected MapScreen getScreen() {
        return MapScreen.getInstance();
    }

    @Override
    protected float getMargin() {
        return 0;
    }

    @Override
    protected float getHeight() {
        return MapScreen.defaultSize;
//        return MapScreen.getInstance().getMapStage().getMap().getImageHeight();
    }
}
