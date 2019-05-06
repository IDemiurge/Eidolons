package eidolons.libgdx.shaders.post;

import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.demo.PostProcessing;
import com.bitfire.postprocessing.effects.*;
import com.bitfire.postprocessing.effects.Bloom.Settings;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.Fluctuating.ALPHA_TEMPLATE;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;
import eidolons.libgdx.shaders.post.fx.BlurFx;
import eidolons.libgdx.shaders.post.fx.SaturateFx;
import eidolons.libgdx.shaders.post.spec.LocalFxProcessor;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.PostProcessingOptions;
import eidolons.system.options.PostProcessingOptions.POST_PROCESSING_OPTIONS;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashMap;
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
    private Map<PostProcessorEffect, Fluctuating> effectMap = new LinkedHashMap<>();
    private PostProcessing main;

    private LensFlare2 lens;
    private Zoomer zoomer;
    private SaturateFx saturate;
    private Bloom bloom;
    private Vignette vignette;
    private Nfaa nfaa;
    private BlurFx blur;
    private MotionBlur motionBlur;
    private CustomPostEffect darken;

    LocalFxProcessor localFxProcessor;

    public PostProcessController() {
        main = new PostProcessing();
        updater = new PostFxUpdater(this);

        main.setEnabled(true);
        main.enableBlending();

        addEffect(darken = new CustomPostEffect(SHADER.DARKEN));
        addEffect(motionBlur = new MotionBlur());
        addEffect(nfaa = new Nfaa(GdxMaster.getWidth(), GdxMaster.getHeight()));
        addEffect(blur = new BlurFx());
        addEffect(vignette = main.vignette);
        addEffect(zoomer = main.zoomer, false);
        //TODO
        zoomer.setEnabled(false);

        addEffect(saturate = new SaturateFx(GdxMaster.getWidth(), GdxMaster.getHeight()));

        addEffect(lens = new LensFlare2(GdxMaster.getWidth(), GdxMaster.getHeight()), false);
        lens.setLensColorTexture(TextureCache.getOrCreate(Images.EMPTY_WEAPON_OFFHAND));

        addFluctuationForEffect(this.bloom = main.bloom);

        initFxForScreen(SCREEN_TYPE.MAIN_MENU);
        if (PostProcessController.isTestMode()) {
            bloom.setEnabled(true);
            bloom.setBlurAmount(0.24f);
            bloom.setBaseIntesity(0.5f);
            bloom.setBloomIntesity(0.2f);
            vignette.setIntensity(1);
        }

        bindEvents();
        //what else determines defaults? Type of level? Cinematic mode
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.POST_PROCESSING_RESET, p -> {
//            bloom.rebind( );
//            vignette.rebind( );
//            blur.rebind( );
            bloom.applyCoef(1.0f);
            vignette.applyCoef(1.0f);
            blur.applyCoef(1.0f);
        });
        GuiEventManager.bind(GuiEventType.POST_PROCESSING , p-> {
//            PostProcessingOptions options = (PostProcessingOptions) p.get();
//            update(options);
            bloom.setEnabled(true);
            vignette.setEnabled(true);
            blur.setEnabled(true);
            bloom.applyCoef(1.5f);
            vignette.applyCoef(1.5f);
            blur.applyCoef(1.5f);
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

    private void initFxForScreen(SCREEN_TYPE screen_type) {
        switch (screen_type) {
            case HEADQUARTERS:
                break;
            case BATTLE:
                break;
            case PRE_BATTLE: //hero creation? shops?
                break;
            case MAIN_MENU:
                break;
            case WEAVE:
                break;
            case MAP:
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
        setEnabled(bloom, options.getBooleanValue(POST_PROCESSING_OPTIONS.BLOOM_ON));
        setEnabled(blur, options.getBooleanValue(POST_PROCESSING_OPTIONS.BLUR_ON));
        setEnabled(nfaa, options.getBooleanValue(POST_PROCESSING_OPTIONS.ANTIALIASING_ON));
        setEnabled(vignette, options.getBooleanValue(POST_PROCESSING_OPTIONS.VIGNETTE_ON));
        setEnabled(motionBlur, options.getBooleanValue(POST_PROCESSING_OPTIONS.MOTION_BLUR_ON));

        setEnabled(darken, options.getBooleanValue(POST_PROCESSING_OPTIONS.STANDARD_ON));
        setEnabled(lens, options.getBooleanValue(POST_PROCESSING_OPTIONS.LENS_ON));
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
        if (OptionsMaster.getPostProcessingOptions().getBooleanValue(POST_PROCESSING_OPTIONS.TEST_ON))
            updater.update();
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
        //            PostEffectDataSource dataSource = (PostEffectDataSource) p.get();
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
            return ALPHA_TEMPLATE.BLOOM;
        }
        if (effect instanceof Vignette) {
            return ALPHA_TEMPLATE.VIGNETTE;
        }
        return ALPHA_TEMPLATE.POST_PROCESS;
    }

    public SaturateFx getSaturate() {
        return saturate;
    }

    public LensFlare2 getLens() {
        return lens;
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

    public MotionBlur getMotionBlur() {
        return motionBlur;
    }

    public CustomPostEffect getDarken() {
        return darken;
    }
}
