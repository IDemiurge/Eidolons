package main.libgdx.screens.map.editor;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroManager;
import main.libgdx.GdxMaster;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.screens.map.MapGuiStage;
import main.libgdx.screens.map.MapScreen;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.images.ImageManager.STD_IMAGES;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 2/9/2018.
 *
 * Apart from main map, what will I edit?
 *
 * 'full map' can be created, then parts of it randomly used for play
 *
 *
 */
public class EditorMapView extends  MapScreen{
    private static EditorMapView instance;
    private EditorParticleMaster editorParticles;

//    Palette;
//    InfoPanel;

    private EditorMapView() {
        super();
    }

    public static EditorMapView getInstance() {
        if (instance==null )
            instance = new EditorMapView();
        return instance;
    }

    public EditorParticleMaster getEditorParticles() {
        return editorParticles;
    }

    @Override
    protected InputMultiplexer getInputController() {
        return
         new InputMultiplexer(guiStage,
          super.getInputController());
    }

    @Override
    protected void preLoad() {
        super.preLoad();
        mapStage.addActor(editorParticles = new EditorParticleMaster(mapStage.getParticles()));
        GuiEventManager.bind(MapEvent.LOCATION_ADDED, p -> {
            Pair<String, Coordinates> pair = (Pair<String, Coordinates>) p.get();
            TextureRegion region= TextureCache.getOrCreateR(STD_IMAGES.MAP_PLACE.getPath());
            Actor actor= new Image(region);
            actor.addListener(new ValueTooltip(pair.getKey()).getController());
            Coordinates c = pair.getValue();
            actor.setPosition(c.x-region.getRegionWidth()/2,c.y-region.getRegionHeight()/2);
            mapStage. addActor(actor);
        });
    }

    @Override
    protected void afterLoad() {
        super.afterLoad();
        MacroManager.getPointMaster().added();
    }

    protected InputController initController() {
        return new EditorInputController(cam);
    }
    @Override
    protected MapGuiStage createGuiStage() {
        return new EditorMapGuiStage( new ScalingViewport
         (Scaling.stretch, GdxMaster.getWidth(),
         GdxMaster.getHeight(), new OrthographicCamera()), getBatch());
    }

    public static void setInstance(EditorMapView instance) {
        EditorMapView.instance = instance;
    }

    public EditorMapGuiStage getGuiStage() {
        return (EditorMapGuiStage) guiStage;
    }
}
