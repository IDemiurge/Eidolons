package main.libgdx.bf.mouse;

import com.badlogic.gdx.graphics.OrthographicCamera;
import main.libgdx.screens.map.MapScreen;

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
    protected MapScreen getScreen() {
        return  MapScreen.getInstance();
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
