package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.AmbienceDataSource;
import eidolons.libgdx.particles.AmbienceDataSource.AMBIENCE_TEMPLATE;
import eidolons.libgdx.particles.EMITTER_PRESET;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.shaders.VignetteShader;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/16/2018.
 */
public class GuiVisualEffects extends GroupX {
    private   DungeonLevel level;
    LightLayer lightLayer;
    private SuperContainer vignette;
    private List<EmitterActor> emitters;
    private int emitterTypesCount;

    public GuiVisualEffects() {
        if (isVignetteOn()) {
            addActor(  vignette =
                    VignetteShader.createVignetteActor());
        }
        //        initEmitters();
        addActor(lightLayer = new LightLayer(true));

        GuiEventManager.bind(GuiEventType.GAME_STARTED, p->
        {
            DC_Game game = (DC_Game) p.get();
              level = game.getDungeonMaster().getDungeonLevel();
            //set current block?
        });
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {
//                getEmitterData((DAY_TIME) p.get());
            if (!isCustomEmitters())
                return;
            LevelBlock block = level.getBlockForCoordinate(
             Eidolons.getMainHero().getCoordinates());
            initEmitters(AmbienceDataSource.getTemplate(block.getStyle()), (DAY_TIME) p.get());

        });
    }


    private boolean isCustomEmitters() {
        return true;
    }

    private void initEmitters(AMBIENCE_TEMPLATE template, DAY_TIME time) {
        if (emitters != null) {
            for (EmitterActor emitter : emitters) {
                emitter.hide();
            }
        }
        emitters = new ArrayList<>();
        boolean night = time.isNight();
        switch (template) {
            case CAVE:
                createEmitters(false, EMITTER_PRESET.MIST_WIND, 250);
                break;
            case COLD:
                createEmitters(false, EMITTER_PRESET.SNOWFALL, 250);
                break;
            case POISON:
                createEmitters(false, EMITTER_PRESET.MIST_BLACK, 250);
                createEmitters(false, EMITTER_PRESET.FLIES, 250);
                break;
            case DUNGEON:
                break;
            case CRYPT:
                break;
            case HELL:
                createEmitters(false, EMITTER_PRESET.ASH, 250);
                break;
            case HALL:
                break;
            case FOREST:
                createEmitters(false, EMITTER_PRESET.FALLING_LEAVES_WINDY, 250);
                createEmitters(false, EMITTER_PRESET.FALLING_LEAVES, 250);
                break;
            case DEEP_MIST:
                createEmitters(false, EMITTER_PRESET.MIST_ARCANE, 250);
                createEmitters(false, EMITTER_PRESET.MIST_NEW, 250);
                break;
        }
        if (night){
            switch (template) {
                case COLD:
                    createEmitters(false, EMITTER_PRESET.SNOW, 250);
                case CAVE:
                case FOREST:
                    createEmitters(false, EMITTER_PRESET.STARS, 250);
                    break;

                case POISON:
                    break;
                case DUNGEON:
                    createEmitters(false, EMITTER_PRESET.WISPS, 250);
                    break;
                case CRYPT:
                    createEmitters(false, EMITTER_PRESET.MOTHS_BLUE3, 250);
                    break;
                case HELL:
                    createEmitters(false, EMITTER_PRESET.CINDERS3, 250);
                    break;
                case HALL:
                    createEmitters(false, EMITTER_PRESET.MOTHS_TIGHT2, 250);
                    break;
                case DEEP_MIST:
                    break;
            }
        }
        else
        switch (template) {
            case CAVE:
                    createEmitters(false, EMITTER_PRESET.MIST_WHITE, 200);
                    break;
            case COLD:
                createEmitters(false, EMITTER_PRESET.SNOW, 250);
                break;
            case POISON:
                break;
            case DUNGEON:
                break;
            case CRYPT:
                break;
            case HELL:
                break;
            case HALL:
                break;
            case FOREST:
                break;
            case DEEP_MIST:
                break;
        }
    }

    public void resized() {
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        lightLayer.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        vignette.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
    }

    @Override
    public void act(float delta) {
        if (emitters == null)
            if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.UI_EMITTERS))
                initEmitters();
        super.act(delta);
    }

    private void hideEmitters() {

    }

    private void initEmitters() {
        if (!OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.UI_EMITTERS))
            return;

        emitters = new ArrayList<>();
        if (!isCustomEmitters()) {
            createEmitters(true, EMITTER_PRESET.MIST_BLACK, 250);
            createEmitters(false, EMITTER_PRESET.MIST_WHITE, 200);
        }

    }

    private void createEmitters(boolean bottom, EMITTER_PRESET preset, int gap) {
        emitterTypesCount++;
        int chance = (int) (80-emitterTypesCount*10-emitterTypesCount*5*GdxMaster.getFontSizeModSquareRoot());
        for (int i = 0; i < GdxMaster.getWidth(); i += gap) {
            if (!RandomWizard.chance(chance))
                continue;
            EmitterActor actor = new EmitterActor(preset);
            addActor(actor);
            emitters.add(actor);
            int x = i;
            int y = bottom ? 0 : GdxMaster.getHeight() - 100;
            actor.start();
            actor.setPosition(x, y);
            actor.act(RandomWizard.getRandomFloatBetween(0, 2));
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
