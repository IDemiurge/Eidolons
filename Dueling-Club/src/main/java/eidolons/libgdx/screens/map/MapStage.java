package eidolons.libgdx.screens.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.screens.map.layers.*;
import eidolons.libgdx.texture.TextureCache;
import eidolons.macro.MacroGame;
import main.content.enums.GenericEnums;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.MapEvent.UPDATE_MAP_BACKGROUND;

/**
 * Created by JustMe on 2/20/2018.
 */
public class MapStage extends Stage {
    private final MapAlphaLayers alphaLayers;
    protected MapMoveLayers moveLayerMaster;
    protected MapParticles particles;
    List<MapTimedLayer> layers = new ArrayList<>();
    private final Group topLayer;
    private final ImageContainer map;
    private final ImageContainer nextMap;
    private final MapRoutes routes;
    private float lastNextMapAlphaPercentage;

    public MapStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addActor(alphaLayers = new MapAlphaLayers());
        addActor(particles = new MapParticles());
        addActor(routes = new MapRoutes());
        addActor(map = new ImageContainer());
        addActor(nextMap = new ImageContainer());
        layers.add(alphaLayers);
        layers.add(particles);
        layers.add(MapScreen.getInstance().getGuiStage().getLights());
        if (isMovingLayersOn())
            layers.add(moveLayerMaster = new MapMoveLayers(MapScreen.defaultSize, MapScreen.defaultSize));
        {
            addActor(moveLayerMaster);
        }
        GuiEventManager.bind(MapEvent.TIME_CHANGED, param -> {
            DAY_TIME time = (DAY_TIME) param.get();
            String path = getBackgroundPath(time);
            updateBackground(path, false);
            path = getBackgroundPath(time.getNext());
            updateBackground(path, true);
            GuiEventManager.trigger(MapEvent.TIME_UPDATED, param.get());

        });

        GuiEventManager.bind(UPDATE_MAP_BACKGROUND, param -> {
            updateBackground((String) param.get(), false);
            updateBackground((String) param.get(), true);


        });
        topLayer = new Group();
        TextureRegion tex = TextureCache.getOrCreateR(
         PathFinder.getMapLayersPath() + "top layer.png");
        ImageContainer layer = new ImageContainer(new Image(tex));
        layer.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.TOP_LAYER);
        topLayer.addActor(layer);
        addActor(topLayer);
    }

    private void updateBackground(String s, boolean nextMapUpdate) {
        final String path = s;
        TextureRegion backTexture = getOrCreateR(path);
        ImageContainer map = this.map;
        if (nextMapUpdate) {
            map = this.nextMap;
        }
        map.setContents(new Image(backTexture));
        lastNextMapAlphaPercentage = 0;
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        resetZIndices();
        if (Flags.isMapEditor())
            return;
        if (nextMap.getContent()==null )
            return; //TODO make sure background is initialized always on time! 
        Color color = nextMap.getContent().getColor();
        float percentage =
         MacroGame.getGame().getLoop().getTimeMaster().getPercentageIntoNextDaytime();
        if (percentage <= lastNextMapAlphaPercentage) //no going back in time...
            return;
        layers.forEach(layer -> layer.applyDynamicTint());
        lastNextMapAlphaPercentage = percentage;
        color.a = percentage;
//        nextMap.getContent().setColor(color);
    }

    private void resetZIndices() {

        map.setZIndex(0);
        nextMap.setZIndex(1);
        particles.setZIndex(Integer.MAX_VALUE);
        moveLayerMaster.setZIndex(Integer.MAX_VALUE);
        routes.setZIndex(Integer.MAX_VALUE);
        topLayer.setZIndex(Integer.MAX_VALUE);
        //move layers ARE ADEDED
    }

    private String getBackgroundPath(DAY_TIME time) {
        if (time == null)
            return MapScreen.defaultPath;
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
        return map.getContent();
    }
}
