package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.libgdx.screens.map.sfx.MapAlphaLayers;
import main.libgdx.screens.map.sfx.MapMoveLayers;
import main.libgdx.screens.map.sfx.MapParticles;
import main.libgdx.screens.map.sfx.MapRoutes;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.MapEvent;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.MapEvent.UPDATE_MAP_BACKGROUND;

/**
 * Created by JustMe on 2/20/2018.
 */
public class MapStage extends Stage {
    protected MapMoveLayers moveLayerMaster;
    protected MapParticles particles;
    private Group topLayer;
    private Image map;
    private MapRoutes routes;

    public MapStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        new MapAlphaLayers(this).init();
        addActor(particles = new MapParticles());
        addActor(routes = new MapRoutes());

        GuiEventManager.bind(MapEvent.TIME_CHANGED, param -> {
            DAY_TIME time = (DAY_TIME) param.get();
            String path = getBackgroundPath(time);
            GuiEventManager.trigger(UPDATE_MAP_BACKGROUND, path);
        });
        GuiEventManager.bind(UPDATE_MAP_BACKGROUND, param -> {
            final String path = (String) param.get();
            TextureRegion backTexture = getOrCreateR(path);
            if (map != null)
                map.remove();
            map = new Image(backTexture);
            addActor(map);
            map.setZIndex(0);
            if (moveLayerMaster==null )
            if (isMovingLayersOn()) {
                moveLayerMaster = new MapMoveLayers(backTexture.getRegionWidth(),
                 backTexture.getRegionHeight());
                addActor(moveLayerMaster);
            }

            topLayer.setWidth(map.getImageWidth());
            topLayer.setHeight(map.getImageHeight());

        });
        topLayer = new Group();
        TextureRegion tex = TextureCache.getOrCreateR(
         PathFinder.getMapLayersPath() + "top layer.png");
        topLayer.addActor(new Image(tex));

    }


    @Override
    public void act(float delta) {
        super.act(delta);
        resetZIndices();
    }

    private void resetZIndices() {

        map.setZIndex(0);
        particles.setZIndex(Integer.MAX_VALUE);
        moveLayerMaster.setZIndex(Integer.MAX_VALUE);
        routes.setZIndex(Integer.MAX_VALUE);
        topLayer.setZIndex(Integer.MAX_VALUE);
    }

    private String getBackgroundPath(DAY_TIME time) {
        return MapScreen.timeVersionRootPath + time.toString() + ".jpg";
    }

    public MapMoveLayers getMoveLayerMaster() {
        return moveLayerMaster;
    }

    public MapParticles getParticles() {
        return particles;
    }

    public MapRoutes getRoutes() {
        return routes;
    }

    private boolean isMovingLayersOn() {
//        return !CoreEngine.isMapEditor();
        return true;
    }

    private boolean isAlphaLayersOn() {
//        return !CoreEngine.isMapEditor();
        return true;
    }

    public Image getMap() {
        return map;
    }
}
