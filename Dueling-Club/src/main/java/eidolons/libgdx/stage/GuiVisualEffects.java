package eidolons.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.libgdx.particles.EMITTER_PRESET;
import main.system.launch.CoreEngine;

import java.util.ArrayList;

/**
 * Created by JustMe on 3/16/2018.
 */
public class GuiVisualEffects extends GroupX {
    private final String vignettePath = "ui\\macro\\vignette.png";
    LightLayer lightLayer;
    private SuperContainer vignette;
    private ArrayList emitters;

    public GuiVisualEffects() {
        if (isVignetteOn()) {
            vignette = new SuperContainer(
             new Image(TextureCache.getOrCreateR(vignettePath)),
             true) {
                @Override
                protected float getAlphaFluctuationMin() {
                    return 0.3f;
                }

                @Override
                protected float getAlphaFluctuationMax() {
                    return 1;
                }
            };
            vignette.getContent().setWidth(GdxMaster.getWidth());
            vignette.getContent().setHeight(GdxMaster.getHeight());
            vignette.setAlphaTemplate(ALPHA_TEMPLATE.VIGNETTE);

            addActor(vignette);
            vignette.setTouchable(Touchable.disabled);
        }
//        initEmitters();
        addActor(lightLayer = new LightLayer(true));
    }

    public void resized() {
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        lightLayer.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        vignette.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
    }

    @Override
    public void act(float delta) {
        if (emitters==null )
            if ( OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.UI_EMITTERS))
                initEmitters();
        super.act(delta);
    }

    private void hideEmitters() {

    }
        private void initEmitters() {
        if (!OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.UI_EMITTERS))
            return;

            emitters = new ArrayList<>();
            createEmitters(true, EMITTER_PRESET.MIST_BLACK, 250);
            createEmitters(false, EMITTER_PRESET.MIST_WHITE, 200);

        }

    private void createEmitters(boolean bottom, EMITTER_PRESET preset, int gap) {
        for (int i = 0; i < GdxMaster.getWidth(); i+=gap) {
            EmitterActor actor = new EmitterActor(preset);
            addActor(actor);
            emitters.add(actor);
            int x = i;
            int y = bottom? 0: GdxMaster.getHeight()-100 ;
            actor.start();
            actor.setPosition(x, y);
        }
    }

    public void resetZIndices() {
        if (CoreEngine.isMapEditor())
            return;
        lightLayer.setZIndex(0);
        vignette.setZIndex(0);

    }

    private boolean isVignetteOn() {
        return true;
    }

    //    UiEmitters emitters;
//TODO SHADERS?
    public enum GUI_FX_TEMPLATE {
        SUNNY,
        SUN_AND_CLOUDS,
        NIGHTLY,
        MISTY,
        DARKEST,

    }
}
