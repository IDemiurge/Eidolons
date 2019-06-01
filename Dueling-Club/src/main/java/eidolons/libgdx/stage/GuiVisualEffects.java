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
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.libgdx.particles.ambi.AmbienceDataSource.AMBIENCE_TEMPLATE;
import eidolons.libgdx.particles.VFX;
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
        WeightMap<VFX> map = getEmittersWeightMap(template, night);
        int n = getEmitterCount(template, time);
        for (int i = 0; i < n; i++) {
            VFX preset = map.getRandomByWeight();
            boolean bottom=RandomWizard.random();
            createEmitters(bottom, preset, 220 - 50 + RandomWizard.getRandomInt(100));
        }
        switch (template) {
            case CAVE:
                createEmitters(false, VFX.MIST_WIND, 250);
                break;
            case COLD:
                createEmitters(false, VFX.SNOWFALL, 250);
                break;
            case POISON:
                createEmitters(false, VFX.MIST_BLACK, 250);
                createEmitters(false, VFX.ASH, 250);
                break;
            case DUNGEON:
            case CRYPT:
                createEmitters(false, VFX.MIST_BLACK, 250);
            case HELL:
                createEmitters(false, VFX.ASH, 250);
                break;
            case HALL:
                break;
            case FOREST:
                createEmitters(false, VFX.FALLING_LEAVES_WINDY, 250);
                createEmitters(false, VFX.FALLING_LEAVES, 250);
                break;
            case DEEP_MIST:
                createEmitters(false, VFX.MIST_ARCANE, 250);
                createEmitters(false, VFX.MIST_NEW, 250);
                break;
        }
        if (night) {
            switch (template) {
                case COLD:
                    createEmitters(false, VFX.SNOW, 250);
                case CAVE:
                case FOREST:
                    createEmitters(false, VFX.STARS, 250);
                    break;

                case POISON:
                    break;
                case DUNGEON:
                    createEmitters(false, VFX.WISPS, 250);
                    break;
                case CRYPT:
                    createEmitters(false, VFX.MOTHS_BLUE3, 250);
                    break;
                case HELL:
                    createEmitters(false, VFX.CINDERS3, 250);
                    break;
                case HALL:
                    createEmitters(false, VFX.MOTHS_TIGHT2, 250);
                    break;
                case DEEP_MIST:
                    break;
            }
        } else
            switch (template) {
                case CAVE:
                    createEmitters(false, VFX.MIST_WHITE, 200);
                    break;
                case COLD:
                    createEmitters(false, VFX.SNOW, 250);
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

    private WeightMap<VFX> getEmittersWeightMap(AMBIENCE_TEMPLATE template, boolean night) {
        WeightMap<VFX> map = new WeightMap<>(VFX.class);
        int fog = night ? 10 : 5;
        int down = night? 5 : 10;
        VFX special = night ? VFX.STARS : VFX.MOTHS;
        VFX special2 = night ? VFX.WISPS : VFX.CINDERS;

        switch (template) {
            case CAVE:
                map.chain(VFX.MIST_WIND, fog);
                map.chain(VFX.MIST_WIND, fog);

                map.chain(VFX.ASH, down);
                map.chain(VFX.SNOWFALL, down);
                map.chain(VFX.SNOWFALL_THICK, down);
                map.chain(VFX.SNOWFALL_THICK, down);
                map.chain(VFX.SNOW, down);
                map.chain(VFX.SNOW_TIGHT, down);

                map.chain(VFX.MIST_WIND, fog);
                map.chain(VFX.MIST_ARCANE, fog);
                map.chain(VFX.MIST_BLACK, fog);
                map.chain(VFX.MIST_CYAN, fog);
                map.chain(VFX.MIST_WHITE3, fog);
                map.chain(VFX.DARK_MIST_LITE, fog);
                map.chain(VFX.DARK_MIST, fog);
                map.chain(VFX.POISON_MIST, fog);
                map.chain(VFX.POISON_MIST2, fog);

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
            createEmitters(true, VFX.MIST_BLACK, 250);
            createEmitters(false, VFX.MIST_WHITE, 200);
        }

    }

    private void createEmitters(boolean bottom, VFX preset, int gap) {
        emitterTypesCount++;
        int chance = (int) Math.max(15, 50 - emitterTypesCount * 10 - emitterTypesCount * 5 * GdxMaster.getFontSizeModSquareRoot());
        for (int i = 0; i < GdxMaster.getWidth(); i += gap) {
            VFX preset_ = preset;
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
