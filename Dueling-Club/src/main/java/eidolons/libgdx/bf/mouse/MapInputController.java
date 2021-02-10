package eidolons.libgdx.bf.mouse;

import com.badlogic.gdx.graphics.OrthographicCamera;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.screens.map.MapScreen;
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
            zoomStep = zoomStep_ * defaultZoom;

            width = GdxMaster.getWidth() * camera.zoom;
            height = GdxMaster.getHeight() * camera.zoom;
            halfWidth = width / 2;
            halfHeight = height / 2;
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
