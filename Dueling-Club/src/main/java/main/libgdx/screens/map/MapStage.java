package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.data.filesys.PathFinder;
import main.libgdx.screens.map.sfx.MapAlphaLayers;
import main.libgdx.screens.map.sfx.MapMoveLayers;
import main.libgdx.screens.map.sfx.MapParticles;
import main.libgdx.screens.map.sfx.MapRoutes;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.MapEvent.UPDATE_MAP_BACKGROUND;

/**
 * Created by JustMe on 2/20/2018.
 */
public class MapStage extends Stage {
    private   Group topLayer;
    private   Image map;
    protected MapMoveLayers moveLayerMaster;
    protected MapParticles particles;
    private MapRoutes routes;
    public MapStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        new MapAlphaLayers(this).init();
         addActor(particles= new MapParticles());
         addActor(routes= new MapRoutes());

        GuiEventManager.bind(UPDATE_MAP_BACKGROUND, param -> {
            final String path = (String) param.get();
            TextureRegion backTexture = getOrCreateR(path);
            if (map != null)
                map.remove();
            map = new Image(backTexture);
              addActor(map);
            map.setZIndex(0);
            if (isMovingLayersOn()) {
                moveLayerMaster = new MapMoveLayers(backTexture.getRegionWidth(),
                 backTexture.getRegionHeight());
                 addActor(moveLayerMaster);
                 topLayer.setZIndex(Integer.MAX_VALUE);
            }

        });
        topLayer= new Group();
        topLayer.addActor(new Image(TextureCache.getOrCreateR(
         PathFinder.getMapLayersPath()+"top layer.png")));

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
