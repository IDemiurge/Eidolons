package eidolons.system.options;

import eidolons.content.consts.VisualEnums;
import libgdx.shaders.post.PostFxUpdater.FX_TEST_MODE;
import eidolons.system.options.PostProcessingOptions.POST_PROCESSING_OPTIONS;

/**
 * Created by JustMe on 12/3/2018.
 */
public class PostProcessingOptions extends Options<POST_PROCESSING_OPTIONS,POST_PROCESSING_OPTIONS> {
    @Override
    protected Class<? extends POST_PROCESSING_OPTIONS> getOptionClass() {
        return POST_PROCESSING_OPTIONS.class;
    }
static {
//    TEST_MODE.setDevOnly(true);
}
    public   enum POST_PROCESSING_OPTIONS implements Options.OPTION {
        ENABLED(false),
        HERO_EFFECTS_OFF(true),
        SHADOW_EFFECT_OFF(false),

        BLOOM_ON(false),
        VIGNETTE_ON(false),
        BLUR_ON(false),
        ANTIALIASING_ON(false),
        MOTION_BLUR_ON(false),
        TEST_ON(false),
        LENS_ON(false),
        STANDARD_ON(false),
        //        vignetteIntensity,
//        vignetteSaturationMul,
//        vignetteSaturation,
//        bloomBlurAmount(2),
//        bloomBaseIntesity(0.5f),
//        bloomIntesity(2),
//        TEST_BLOOM_COEF(100, 0, 200),
//        TEST_BLUR_COEF(100, 0, 200),
//        TEST_SATURATE_COEF(100, 0, 200),
        TEST_MODE(VisualEnums.FX_TEST_MODE.values()),
        ;
        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private final Object defaultValue;

        POST_PROCESSING_OPTIONS(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        POST_PROCESSING_OPTIONS(Object... options) {
            this.options = options;
            defaultValue = options[0];
        }

        POST_PROCESSING_OPTIONS(Integer defaultValue, Integer min, Integer max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;

        }
        @Override
        public Boolean isExclusive() {
            return exclusive;
        }

        @Override
        public Integer getMin() {
            return min;
        }

        @Override
        public Integer getMax() {
            return max;
        }

        @Override
        public Object[] getOptions() {
            return options;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }
}
