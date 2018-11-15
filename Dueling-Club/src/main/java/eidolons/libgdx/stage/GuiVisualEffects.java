package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.decor.ShardVisuals;
import eidolons.libgdx.bf.decor.ShardVisuals.SHARD_SIZE;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.AMBIENCE_TEMPLATE;
import eidolons.libgdx.particles.EMITTER_PRESET;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.shaders.VignetteShader;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.system.GuiEventManager;
import main.system.GuiEventType;
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
    private DungeonLevel level;
    private SuperContainer vignette;
    private List<EmitterActor> emitters;
    private int emitterTypesCount;

    public GuiVisualEffects() {
        if (isVignetteOn()) {
            addActor(vignette =
             VignetteShader.createVignetteActor());
        }
        //        initEmitters();
        addActor(lightLayer = new LightLayer(true));

        GuiEventManager.bind(GuiEventType.GAME_STARTED, p ->
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
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
        emitterTypesCount=0;
        emitters = new ArrayList<>();
        boolean night = time.isNight();
        WeightMap<EMITTER_PRESET> map = getEmittersWeightMap(template, night);
        int n = getEmitterCount(template, time);
        for (int i = 0; i < n; i++) {
            EMITTER_PRESET preset = map.getRandomByWeight();
            boolean bottom=RandomWizard.random();
            createEmitters(bottom, preset, 220 - 50 + RandomWizard.getRandomInt(100));
        }
        switch (template) {
            case CAVE:
                createEmitters(false, EMITTER_PRESET.MIST_WIND, 250);
                break;
            case COLD:
                createEmitters(false, EMITTER_PRESET.SNOWFALL, 250);
                break;
            case POISON:
                createEmitters(false, EMITTER_PRESET.MIST_BLACK, 250);
                createEmitters(false, EMITTER_PRESET.ASH, 250);
                break;
            case DUNGEON:
            case CRYPT:
                createEmitters(false, EMITTER_PRESET.MIST_BLACK, 250);
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
        if (night) {
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
        } else
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

    private int getEmitterCount(AMBIENCE_TEMPLATE template, DAY_TIME time) {
        return 3;
    }

    private WeightMap<EMITTER_PRESET> getEmittersWeightMap(AMBIENCE_TEMPLATE template, boolean night) {
        WeightMap<EMITTER_PRESET> map = new WeightMap<>(EMITTER_PRESET.class);
        int fog = night ? 10 : 5;
        int down = night? 5 : 10;
        EMITTER_PRESET special = night ? EMITTER_PRESET.STARS : EMITTER_PRESET.MOTHS;
        EMITTER_PRESET special2 = night ? EMITTER_PRESET.WISPS : EMITTER_PRESET.CINDERS;

        switch (template) {
            case CAVE:
                map.chain(EMITTER_PRESET.MIST_WIND, fog);
                map.chain(EMITTER_PRESET.MIST_WIND, fog);

                map.chain(EMITTER_PRESET.ASH, down);
                map.chain(EMITTER_PRESET.SNOWFALL, down);
                map.chain(EMITTER_PRESET.SNOWFALL_THICK, down);
                map.chain(EMITTER_PRESET.SNOWFALL_THICK, down);
                map.chain(EMITTER_PRESET.SNOW, down);
                map.chain(EMITTER_PRESET.SNOW_TIGHT, down);

                map.chain(EMITTER_PRESET.MIST_WIND, fog);
                map.chain(EMITTER_PRESET.MIST_ARCANE, fog);
                map.chain(EMITTER_PRESET.MIST_BLACK, fog);
                map.chain(EMITTER_PRESET.MIST_CYAN, fog);
                map.chain(EMITTER_PRESET.MIST_WHITE3, fog);
                map.chain(EMITTER_PRESET.DARK_MIST_LITE, fog);
                map.chain(EMITTER_PRESET.DARK_MIST, fog);
                map.chain(EMITTER_PRESET.POISON_MIST, fog);
                map.chain(EMITTER_PRESET.POISON_MIST2, fog);

                break;
            case COLD:
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
        return map;
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
        int chance = (int) Math.max(15, 50 - emitterTypesCount * 10 - emitterTypesCount * 5 * GdxMaster.getFontSizeModSquareRoot());
        for (int i = 0; i < GdxMaster.getWidth(); i += gap) {
            EMITTER_PRESET preset_ = preset;
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
