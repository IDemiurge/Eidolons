package libgdx.shaders.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.demo.PostProcessing;
import com.bitfire.postprocessing.effects.*;
import com.bitfire.postprocessing.effects.Bloom.Settings;
import eidolons.content.consts.VisualEnums;
import eidolons.netherflame.campaign.assets.NF_Images;
import libgdx.GdxMaster;
import libgdx.bf.Fluctuating;
import libgdx.shaders.ShaderMaster.SHADER;
import libgdx.shaders.post.fx.BloomFx;
import libgdx.shaders.post.fx.BlurFx;
import libgdx.shaders.post.fx.SaturateFx;
import libgdx.shaders.post.spec.LocalFxProcessor;
import libgdx.assets.texture.TextureCache;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.PostProcessingOptions;
import eidolons.system.options.PostProcessingOptions.POST_PROCESSING_OPTIONS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ALPHA_TEMPLATE;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 12/3/2018.
 * <p>
 * special cases:
 * <p>
 * Movement
 * Death - more discoloration? soul-drain effect upwards?
 * Spells
 * Weapons3d
 */
public class PostProcessController {
    static PostProcessController instance;
    private final PostFxUpdater updater;
    private final Actor actor;
    private final Map<PostProcessorEffect, Fluctuating> effectMap = new LinkedHashMap<>();
    private final PostProcessing main;

    private final LensFlare2 lens2;
    private final LensFlare  lens1;
    private final SaturateFx saturate;
    private final Bloom bloom;
    private final BloomFx bloomBright;
    private final Vignette vignette;
    private final Nfaa nfaa;
    private final BlurFx blur;
    private final MotionBlur motionBlur;
    private final Curvature curvature;
    private final CrtMonitor discolor;
    private final CustomPostEffect darken;

    LocalFxProcessor localFxProcessor;
    private boolean off;
    private Map<FloatAction, VisualEnums.POST_FX_FACTOR> actionMap;

    public PostProcessController() {
        main = new PostProcessing();
         actor = new Actor();
        updater = new PostFxUpdater(this, actor);

        main.setEnabled(true);
        main.enableBlending();


        addEffect(darken = new CustomPostEffect(SHADER.DARKEN));
        addEffect(motionBlur = new MotionBlur());
        addEffect(curvature = new Curvature());
        addEffect(nfaa = new Nfaa(GdxMaster.getWidth(), GdxMaster.getHeight()));
        addEffect(blur = new BlurFx());
        Zoomer zoomer;
        addEffect(zoomer = main.zoomer, false);
        //TODO
        zoomer.setEnabled(false);

        addEffect(bloomBright = new BloomFx( (int)(Gdx.graphics.getWidth() * 0.5f),
                (int)(Gdx.graphics.getHeight() * 0.5f) , true, true));
        addEffect(saturate = new SaturateFx(GdxMaster.getWidth(), GdxMaster.getHeight()));

        addEffect(lens1 = new LensFlare(GdxMaster.getWidth(), GdxMaster.getHeight()), false);

        addEffect(lens2 = new LensFlare2(GdxMaster.getWidth(), GdxMaster.getHeight()), false);
        lens2.setLensColorTexture(TextureCache.getOrCreate(NF_Images.PROMO_ART.THE_HALL.getPath()));
//TODO this lens could be better....


//        addEffect(vignette = main.vignette);
        addFluctuationForEffect(this.vignette = main.vignette);
        addFluctuationForEffect(this.discolor = main.crt);
        addFluctuationForEffect(this.bloom = main.bloom);

        initFxForScreen(VisualEnums.SCREEN_TYPE.MAIN_MENU);
        if (PostProcessController.isTestMode()) {
            bloom.setEnabled(true);
            bloom.setBlurAmount(0.24f);
            bloom.setBaseIntesity(0.5f);
            bloom.setBloomIntesity(0.2f);
            vignette.setIntensity(1);
        }
        for (PostProcessorEffect effect : effectMap.keySet()) {
            effect.setEnabled(false);
        }
        bindEvents();
        //what else determines defaults? Type of level? Cinematic mode
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.POST_PROCESS_FX_ANIM, p -> {
            List args = (List) p.get();
            VisualEnums.POST_FX_FACTOR fx = (VisualEnums.POST_FX_FACTOR) args.get(0);
            FloatAction action= (FloatAction) args.get(1);
            actionMap.put(action, fx);

        });
            GuiEventManager.bind(GuiEventType.POST_PROCESSING_RESET, p -> {
//            bloom.rebind( );
//            vignette.rebind( );
//            blur.rebind( );
            bloom.applyCoef(1.0f);
            vignette.applyCoef(1.0f);
            blur.applyCoef(1.0f);
            update(OptionsMaster.getPostProcessingOptions());

        });

    }

    public static boolean isTestMode() {
        return false;
    }

    public static PostProcessController getInstance() {
        if (instance == null) {
            instance = new PostProcessController();
        }
        return instance;
    }

    private void initFxForScreen(VisualEnums.SCREEN_TYPE screen_type) {
        switch (screen_type) {
            case MAP:
            case WEAVE:
            case MAIN_MENU:
            case DUNGEON:
                break;
            case PRE_BATTLE: //hero creation? shops?
                break;
        }
    }

    private void addEffect(PostProcessorEffect effect) {
        addEffect(effect, true);
    }

    private void addEffect(PostProcessorEffect effect, boolean fluctuate) {
        main.postProcessor.addEffect(effect);
        effect.setEnabled(true);
        //        fx.add(effect);
        addFluctuationForEffect(effect);
    }

    public void update(PostProcessingOptions options) {
        PostFxUpdater.heroFxOff =
                options.getBooleanValue(
                        POST_PROCESSING_OPTIONS.HERO_EFFECTS_OFF);
        PostFxUpdater.shadowFxOff =
                options.getBooleanValue(
                        POST_PROCESSING_OPTIONS.SHADOW_EFFECT_OFF);


        if (!OptionsMaster.getPostProcessingOptions().getBooleanValue(
                PostProcessingOptions.POST_PROCESSING_OPTIONS.ENABLED)){
            for (PostProcessorEffect effect : effectMap.keySet()) {
                effect.setEnabled(false);
            }
            off=true;
            return;
        }

        off=false;
        setEnabled(bloom, options.getBooleanValue(POST_PROCESSING_OPTIONS.BLOOM_ON));
        setEnabled(blur, options.getBooleanValue(POST_PROCESSING_OPTIONS.BLUR_ON));
        setEnabled(nfaa, options.getBooleanValue(POST_PROCESSING_OPTIONS.ANTIALIASING_ON));
        setEnabled(vignette, options.getBooleanValue(POST_PROCESSING_OPTIONS.VIGNETTE_ON));
        setEnabled(motionBlur, options.getBooleanValue(POST_PROCESSING_OPTIONS.MOTION_BLUR_ON));

        setEnabled(darken, options.getBooleanValue(POST_PROCESSING_OPTIONS.STANDARD_ON));
        setEnabled(lens2, options.getBooleanValue(POST_PROCESSING_OPTIONS.LENS_ON));


    }

    private void setEnabled(PostProcessorEffect effect, boolean booleanValue) {
        if (effect != null) {
            effect.setEnabled(booleanValue);
        }
    }

    public void setup() {
        update(OptionsMaster.getPostProcessingOptions());
        instance = this;
        //        main.capture();
    }

    public PostProcessing getMain() {
        return main;
    }

    private void update(PostEffectDataSource dataSource) {


        switch (dataSource.effect) {
            case BLOOM:
                main.bloom.setSettings((Settings) dataSource.getParams()[0]);
                break;
        }
    }

    public void act(float delta) {
        if (off)
            return;
//        if (OptionsMaster.getPostProcessingOptions().getBooleanValue(POST_PROCESSING_OPTIONS.TEST_ON))
            updater.update();
        actor.act(delta);
        fluctuate(delta);

        //        main.postProcessor.addEffect(vignette);
    }

    public void fluctuate(float delta) {
        for (PostProcessorEffect effect : effectMap.keySet()) {
            if (!effect.isEnabled()) {
                continue;
            }
            Fluctuating fluctuation = effectMap.get(effect);
            fluctuation.fluctuate(delta);
            //                applyCoef(effect, fluctuation.getColor().a);
            effect.applyCoef(fluctuation.getColor().a);
        }
    }

    public void end() {
        //        temp = new PostProcessing();
        //        if (main.isReady())
        try {
            main.end();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
    }

    public void begin(PostEffectDataSource... data) {
        for (PostEffectDataSource datum : data) {
            update(datum);
        }
        main.begin();
    }

    public void enable() {
        //        main.setEnabled(true);
        //        main.bloom.setEnabled(true);
        //        main.bloom.applyCoef(1);
    }

    public void disable() {
        //        main.setEnabled(false);
        //        main.bloom.setEnabled(false);
        //        main.bloom.applyCoef(0);
        //        GuiEventManager.bind(GuiEventType.POST_EFFECT_UPDATE, p-> {
        //            PostEffectDataSource dataSource = (PostEffectDataSource) p.getVar();
        //            update(dataSource);
        //        });
    }

    public void reset() {
        //        main.captureEnd();
    }


    private void addFluctuationForEffect(PostProcessorEffect effect) {
        effectMap.put(effect, new Fluctuating(getAlphaTemplate(effect)));
    }

    private ALPHA_TEMPLATE getAlphaTemplate(PostProcessorEffect effect) {
        if (effect instanceof Bloom) {
            return GenericEnums.ALPHA_TEMPLATE.BLOOM;
        }
        if (effect instanceof Vignette) {
            return GenericEnums.ALPHA_TEMPLATE.VIGNETTE;
        }
        return GenericEnums.ALPHA_TEMPLATE.POST_PROCESS;
    }

    public SaturateFx getSaturate() {
        return saturate;
    }

    public LensFlare2 getLens2() {
        return lens2;
    }

    public LensFlare getLens () {
        return lens1;
    }

    public Map<PostProcessorEffect, Fluctuating> getEffectMap() {
        return effectMap;
    }

    public Bloom getBloom() {
        return bloom;
    }

    public Vignette getVignette() {
        return vignette;
    }

    public Nfaa getNfaa() {
        return nfaa;
    }

    public BlurFx getBlur() {
        return blur;
    }

    public BloomFx getBloomBright() {
        return bloomBright;
    }

    public Curvature getCurvature() {
        return curvature;
    }

    public CrtMonitor getDiscolor() {
        return discolor;
    }

    public MotionBlur getMotionBlur() {
        return motionBlur;
    }

    public CustomPostEffect getDarken() {
        return darken;
    }
}
