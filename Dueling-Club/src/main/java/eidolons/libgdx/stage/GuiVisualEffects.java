package eidolons.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.anims.particles.EmitterActor;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.gui.panels.GroupX;
import main.content.CONTENT_CONSTS2.SFX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.texture.TextureCache;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 3/16/2018.
 */
public class GuiVisualEffects extends GroupX {
    private final String vignettePath = "ui\\macro\\vignette.png";
    LightLayer lightLayer;
    private SuperContainer vignette;

    public GuiVisualEffects() {
        initEmitters();
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
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private void initEmitters() {
        EmitterActor wisps = new EmitterActor(SFX.WISPS);

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
