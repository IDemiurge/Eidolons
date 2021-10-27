package libgdx.map.layers;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMaster;
import libgdx.bf.generic.ImageContainer;
import libgdx.map.MapScreen;
import libgdx.map.layers.LightLayer.LightContainer;
import libgdx.assets.texture.SmartTextureAtlas;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ALPHA_TEMPLATE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.MapMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/28/2018.
 */
public class LightLayer extends MapTimedLayer<LightContainer> {

    private static SmartTextureAtlas lightsAtlas;

    static {
        LIGHT_LAYER.VERTICAL_MOONLIGHT.setEmitterPaths(GenericEnums.VFX.STARS.getPath());
    }

    boolean uiStage;
    Map<LIGHT_LAYER, Float> timerMap = new HashMap<>();
    Map<LIGHT_LAYER, Float> triggerMap = new HashMap<>();
    private static Boolean additive;

    public LightLayer(boolean uiStage) {
        super();
        this.uiStage = uiStage;
        setTransform(false);
    }

    public static String getPath() {
        return
                StrPathBuilder.build("ui", "light") + PathUtils.getPathSeparator();
    }

    @Override
    protected void init() {
        for (LIGHT_LAYER sub : LIGHT_LAYER.values()) {
            MapMaster.addToFloatMap(timerMap, sub, 0f);
            MapMaster.addToFloatMap(triggerMap, sub, 0f);
        }
        // with UI or just always beyond?
        //flip west/east
        //rotate for noon
        //randomize?
        for (LIGHT_LAYER sub : LIGHT_LAYER.values()) {
            //random position?
            for (int i = 0; i < sub.maxCount; i++) {

                LightContainer container = new LightContainer(sub);
                container.setAlphaTemplate(sub.alphaTemplate);
                for (DAY_TIME time : sub.times)
                    MapMaster.addToListMap(map, time, container);
            }
        }

        initEmitters();


        initialized = true;
    }

    private void initEmitters() {
//      TODO   if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.UI_VFX))
//        if (container.lightLayer.emitterPaths != null) {
//            for (String sub : container.lightLayer.emitterPaths) {
//                EmitterActor emitter = EmitterPools.getEmitterActor(sub); // new EmitterActor(sub);
//                container.addActor(emitter);
//                emitter.setPosition(container.getWidth() / 2,
//                        GdxMaster.getHeight() - 25);
//                emitter.setSpeed(0.5f);
//                emitter.start();
//                if (emitter.getEffect().getEmitters().size > 0) {
//                    emitter.getEffect().getEmitters().get(0).scaleSize(container.getScaleX() + 0.25f);
//                }
//            }
//            container.getContent().setZIndex(0);
//        }
    }

    protected void addLight(LIGHT_LAYER sub) {
        LightContainer container = new LightContainer(sub);
//        container.setAlphaTemplate(sub.alphaTemplate);

        adjust(container);
        ActionMaster.addFadeInAndOutAction(container.getContent(), 2.5f, true);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (LIGHT_LAYER sub : LIGHT_LAYER.values()) {
            MapMaster.addToFloatMap(timerMap, sub, delta);
            for (DAY_TIME day_time : sub.times) {
                if (time != day_time) {
                    continue;
                }
                if (triggerMap.get(sub) == null || timerMap.get(sub) > triggerMap.get(sub)) {
                    addLight(sub);
                    float willSpawnOn = RandomWizard.getRandomFloatBetween(sub.delay, 2 * sub.delay);
                    triggerMap.put(sub, willSpawnOn);
                    timerMap.put(sub, 0f);

                }
            }
        }
    }

    @Override
    protected void spawnLayer() {
        setVisible(!OptionsMaster.getGraphicsOptions().
                getBooleanValue(GRAPHIC_OPTION.LIGHT_OVERLAYS_OFF));

        if (uiStage)
            setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        else
            setSize(MapScreen.defaultSize, MapScreen.defaultSize);

        displayed = map.get(time);
        for (LightContainer sub : displayed) {
//            triggerMap.put(sub, time);
            //if implement oneshot light, this will be easier
        }
//        Collections.shuffle(displayed);
        //add gradually?
        //remove if alpha zero?
//        displayed = displayed.subList(0, getMaxLayers());

        displayed.forEach(this::adjust);
    }

    private void adjust(LightContainer container) {
        float scaleRange = container.lightLayer.scaleRange;
        container.getContent().setScale(RandomWizard.getRandomFloatBetween(1 + scaleRange / 2, 1 + scaleRange));
        container.setVisible(true);
        addActor(container);
        Boolean flipX;
        float y, x;
        if (container.lightLayer.vertical) {
            x = getWidth() / 3 +
                    RandomWizard.getRandomInt((int) getWidth() / 3) - container.getWidth() / 2;
            y = getHeight() -
                    RandomWizard.getRandomFloatBetween(100,
                            container.getContent().getScaleY() * container.getHeight() - 100);
            flipX = RandomWizard.random();
        } else {
            flipX = time == DAY_TIME.DUSK;
            if (time == DAY_TIME.MIDDAY) {
                flipX = RandomWizard.random();
            }
            x = flipX ? -container.getWidth() / 3 -
                    RandomWizard.getRandomInt((int) container.getWidth() / 3) :
                    getWidth() - container.getWidth() / 2.25f
                            - RandomWizard.getRandomInt((int) (container.getWidth() / 2));
            y =
                    RandomWizard.getRandomInt((int) getHeight());
        }
        container.setFlipX(flipX);
        container.setPosition(x, y);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if (isAdditive()) {
        //            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //            ((CustomSpriteBatch) batch).resetBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//        }

        super.draw(batch, parentAlpha);

    }

    public Boolean isAdditive() {
        if (additive == null) {
            additive = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.ADDITIVE_LIGHT);
        }
        return additive;
    }

    public static void setAdditive(Boolean b) {
        additive = b;
    }


    public enum LIGHT_LAYER {
//        HEAVENLY_LIGHT_LARGE(5, GenericEnums.ALPHA_TEMPLATE.LIGHT, DAY_TIME.MORNING),
//        HEAVENLY_LIGHT_LARGE_GOLDEN(5, GenericEnums.ALPHA_TEMPLATE.LIGHT, DAY_TIME.DUSK),

        HEAVENLY_LIGHT_LARGE_GOLDEN_SPECKLED(5, GenericEnums.ALPHA_TEMPLATE.LIGHT, DAY_TIME.DUSK, DAY_TIME.MIDDAY),
        HEAVENLY_LIGHT_LARGE_SPECKLED(5, GenericEnums.ALPHA_TEMPLATE.LIGHT, DAY_TIME.MORNING),

        //        LIGHT_SPREAD_SILVER(5, GenericEnums.ALPHA_TEMPLATE.LIGHT, DAY_TIME.MIDDAY),
//        LIGHT_SPREAD_GOLDEN(5, GenericEnums.ALPHA_TEMPLATE.LIGHT, DAY_TIME.DUSK),
//        LIGHT_SPREAD(5, GenericEnums.ALPHA_TEMPLATE.LIGHT, DAY_TIME.MIDDAY),
//
        VERTICAL_MOONLIGHT(0.4f, -0.15f, 6, GenericEnums.ALPHA_TEMPLATE.MOONLIGHT, true,
                DAY_TIME.NIGHTFALL, DAY_TIME.MIDNIGHT,
                DAY_TIME.DAWN, DAY_TIME.DUSK
        ),
//
//        VERTICAL_LIGHT_SILVER(1f, -0.25f, 5, GenericEnums.ALPHA_TEMPLATE.LIGHT, true, DAY_TIME.MIDDAY, DAY_TIME.MORNING),
//        VERTICAL_LIGHT_ASHEN(1f, -0.25f, 5, GenericEnums.ALPHA_TEMPLATE.LIGHT, true, DAY_TIME.DUSK, DAY_TIME.MORNING),


//        VERTICAL_LIGHT_LARGE_GOLDEN(0.5f, 5, ALPHA_TEMPLATE.LIGHT,true, DAY_TIME.DUSK),
//        VERTICAL_LIGHT_LARGE_GOLDEN_SPECKLED(0.5f, 5, ALPHA_TEMPLATE.LIGHT,true, DAY_TIME.DUSK, DAY_TIME.MIDDAY),
//        VERTICAL_LIGHT_LARGE_SILVER_SPECKLED(0.5f, 5, ALPHA_TEMPLATE.LIGHT,true, DAY_TIME.MORNING),

//        MOON_LIGHT(5, ALPHA_TEMPLATE.SUN, DAY_TIME.MIDDAY), //colorize per active moon?
        ;

        public float delay = 0.5f;
        public boolean vertical;
        public String[] emitterPaths;
        int maxCount;
        ALPHA_TEMPLATE alphaTemplate;
        DAY_TIME[] times;
        private float scaleRange = 0.15f;

        LIGHT_LAYER(float delay, float scaleRange, int maxCount, ALPHA_TEMPLATE alphaTemplate, boolean vertical, DAY_TIME... times) {
            this.delay = delay * 1.5f;
            this.scaleRange = scaleRange;
            this.maxCount = maxCount;
            this.alphaTemplate = alphaTemplate;
            this.times = times;
            this.vertical = vertical;
        }

        LIGHT_LAYER(int maxCount, ALPHA_TEMPLATE alphaTemplate, DAY_TIME... times) {
            this.maxCount = maxCount;
            this.alphaTemplate = alphaTemplate;
            this.times = times;
        }

        public void setEmitterPaths(String... emitterPaths) {
            this.emitterPaths = emitterPaths;
        }

    }

    public static SmartTextureAtlas getLightAtlas() {
        if (lightsAtlas == null) {
            lightsAtlas = new SmartTextureAtlas(
                    PathFinder.getImagePath() +
                            "ui/light/light.txt");
        }
        return lightsAtlas;
    }

    private static TextureRegion getTexture(LIGHT_LAYER type) {
        //        if (texture == null) {
//            return null;
//        }
        return getLightAtlas().findRegion(
                type.name().toLowerCase(). replace("_", " "));
    }

    public static class LightContainer extends ImageContainer {
        LIGHT_LAYER lightLayer;

        public LightContainer(LIGHT_LAYER lightLayer) {
            super(new Image(getTexture(lightLayer)));

            this.lightLayer = lightLayer;
            setTransform(false);
        }

        @Override
        public boolean removeActor(Actor actor, boolean unfocus) {
            remove();
            return super.removeActor(actor, unfocus);
        }

        @Override
        public boolean removeActor(Actor actor) {
            remove();
            return super.removeActor(actor);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
        }
    }
}
