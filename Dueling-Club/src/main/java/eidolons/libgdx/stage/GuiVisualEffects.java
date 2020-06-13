package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.decor.ShardVisuals;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_SIZE;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.VFX_TEMPLATE;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.shaders.VignetteShader;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/16/2018.
 */
public class GuiVisualEffects extends GroupX {
    LightLayer lightLayer;
    private SuperContainer vignette;
    private List<EmitterActor> emitters;
    private int emitterTypesCount;
    private static boolean off ;

    public static void setOff(Boolean off) {
        GuiVisualEffects.off = off;
    }

    public GuiVisualEffects() {
        if (isVignetteOn()) {
            addActor(vignette =
             VignetteShader.createVignetteActor());
        }
        //        initEmitters();
        addActor(lightLayer = new LightLayer(true));

        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {
            //                getEmitterData((DAY_TIME) p.getVar());
            if (!isCustomEmitters())
                return;
            LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(
             Eidolons.getPlayerCoordinates());
            initEmitters(AmbienceDataSource.getTemplate(struct.getStyle()), (DAY_TIME) p.get());

        });

        fadeIn();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if ( off) {
            return;
        }
        if (!CoreEngine.isDebugLaunch())
        if (HqPanel.getActiveInstance()!= null) {
            return; //TODO igg demo fix
        }
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
    }

    private boolean isCustomEmitters() {
        return false;
    }

    private void initEmitters(VFX_TEMPLATE template, DAY_TIME time) {
        if (emitters != null) {
            for (EmitterActor emitter : emitters) {
                emitter.hide();
            }
        }
        emitterTypesCount=0;
        emitters = new ArrayList<>();
        boolean night = time.isNight();
        WeightMap<GenericEnums.VFX> map = getEmittersWeightMap(template, night);
        int n = getEmitterCount(template, time);
        for (int i = 0; i < n; i++) {
            GenericEnums.VFX preset = map.getRandomByWeight();
            boolean bottom=RandomWizard.random();
            createEmitters(bottom, preset, 220 - 50 + RandomWizard.getRandomInt(100));
        }
        switch (template) {
            case CAVE:
                createEmitters(false, GenericEnums.VFX.MIST_WIND, 250);
                break;
            case COLD:
                createEmitters(false, GenericEnums.VFX.SNOWFALL, 250);
                break;
            case POISON:
                createEmitters(false, GenericEnums.VFX.MIST_BLACK, 250);
                createEmitters(false, GenericEnums.VFX.ASH, 250);
                break;
            case DUNGEON:
            case CRYPT:
                createEmitters(false, GenericEnums.VFX.MIST_BLACK, 250);
            case HELL:
                createEmitters(false, GenericEnums.VFX.ASH, 250);
                break;
            case HALL:
                break;
            case FOREST:
                createEmitters(false, GenericEnums.VFX.FALLING_LEAVES_WINDY, 250);
                createEmitters(false, GenericEnums.VFX.FALLING_LEAVES, 250);
                break;
            case DEEP_MIST:
                createEmitters(false, GenericEnums.VFX.MIST_ARCANE, 250);
                createEmitters(false, GenericEnums.VFX.MIST_NEW, 250);
                break;
        }
        if (night) {
            switch (template) {
                case COLD:
                    createEmitters(false, GenericEnums.VFX.SNOW, 250);
                case CAVE:
                case FOREST:
                    createEmitters(false, GenericEnums.VFX.STARS, 250);
                    break;

                case POISON:
                case DEEP_MIST:
                    break;
                case DUNGEON:
                    createEmitters(false, GenericEnums.VFX.WISPS, 250);
                    break;
                case CRYPT:
                    createEmitters(false, GenericEnums.VFX.MOTHS_BLUE3, 250);
                    break;
                case HELL:
                    createEmitters(false, GenericEnums.VFX.CINDERS3, 250);
                    break;
                case HALL:
                    createEmitters(false, GenericEnums.VFX.MOTHS_TIGHT2, 250);
                    break;
            }
        } else
            switch (template) {
                case CAVE:
                    createEmitters(false, GenericEnums.VFX.MIST_WHITE, 200);
                    break;
                case COLD:
                    createEmitters(false, GenericEnums.VFX.SNOW, 250);
                    break;
                case POISON:
                case DEEP_MIST:
                case FOREST:
                case HALL:
                case HELL:
                case CRYPT:
                case DUNGEON:
                    break;
            }
    }

    private int getEmitterCount(VFX_TEMPLATE template, DAY_TIME time) {
        return 3;
    }

    private WeightMap<GenericEnums.VFX> getEmittersWeightMap(VFX_TEMPLATE template, boolean night) {
        WeightMap<GenericEnums.VFX> map = new WeightMap<>(GenericEnums.VFX.class);
        int fog = night ? 10 : 5;
        int down = night? 5 : 10;
        GenericEnums.VFX special = night ? GenericEnums.VFX.STARS : GenericEnums.VFX.MOTHS;
        GenericEnums.VFX special2 = night ? GenericEnums.VFX.WISPS : GenericEnums.VFX.CINDERS;

        switch (template) {
            case CAVE:
                map.chain(GenericEnums.VFX.MIST_WIND, fog);
                map.chain(GenericEnums.VFX.MIST_WIND, fog);

                map.chain(GenericEnums.VFX.ASH, down);
                map.chain(GenericEnums.VFX.SNOWFALL, down);
                map.chain(GenericEnums.VFX.SNOWFALL_THICK, down);
                map.chain(GenericEnums.VFX.SNOWFALL_THICK, down);
                map.chain(GenericEnums.VFX.SNOW, down);
                map.chain(GenericEnums.VFX.SNOW_TIGHT, down);

                map.chain(GenericEnums.VFX.MIST_WIND, fog);
                map.chain(GenericEnums.VFX.MIST_ARCANE, fog);
                map.chain(GenericEnums.VFX.MIST_BLACK, fog);
                map.chain(GenericEnums.VFX.MIST_CYAN, fog);
                map.chain(GenericEnums.VFX.MIST_WHITE3, fog);
                map.chain(GenericEnums.VFX.DARK_MIST_LITE, fog);
                map.chain(GenericEnums.VFX.DARK_MIST, fog);
                map.chain(GenericEnums.VFX.POISON_MIST, fog);
                map.chain(GenericEnums.VFX.POISON_MIST2, fog);

                break;
            case COLD:
            case DEEP_MIST:
            case FOREST:
            case HALL:
            case HELL:
            case CRYPT:
            case DUNGEON:
            case POISON:
                break;
        }
        return map;
    }

    public void resized() {
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        lightLayer.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        vignette.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
    }

    @Override
    public void act(float delta) {

        if (off)
            return;
        if (emitters == null)
            if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.UI_VFX))
                initEmitters();
        super.act(delta);
    }

    private void hideEmitters() {

    }

    private void initEmitters() {
        if (!OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.UI_VFX))
            return;

        emitters = new ArrayList<>();
        if (!isCustomEmitters()) {
            createEmitters(true, GenericEnums.VFX.MIST_BLACK, 250);
            createEmitters(false, GenericEnums.VFX.MIST_WHITE, 200);
        }

    }

    private void createEmitters(boolean bottom, GenericEnums.VFX preset, int gap) {
        emitterTypesCount++;
        int chance = (int) Math.max(15, 50 - emitterTypesCount * 10 - emitterTypesCount * 5 * GdxMaster.getFontSizeModSquareRoot());
        for (int i = 0; i < GdxMaster.getWidth(); i += gap) {
            GenericEnums.VFX preset_ = preset;
            if (!RandomWizard.chance(chance*2)) {
                continue;
            }
            if (!RandomWizard.chance(chance*2)) {
                try {
                    preset_ = ShardVisuals.getEmitters(null , SHARD_SIZE.NORMAL)[0];
                } catch (Exception e) {
                    continue;
                }
            }
            if (preset_ == null) {
                continue;
            }
            EmitterActor actor = new EmitterActor(preset_);
            addActor(actor);
            emitters.add(actor);
            int x = i;
            int y = bottom ? 0 : GdxMaster.getHeight() - 50;

            actor.start();
            actor.setPosition(x + (50 - RandomWizard.getRandomInt(100)) / 2, y
             + (50 - RandomWizard.getRandomInt(100)) / 2);
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
