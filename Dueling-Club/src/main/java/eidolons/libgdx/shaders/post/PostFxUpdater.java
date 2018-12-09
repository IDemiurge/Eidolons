package eidolons.libgdx.shaders.post;

import com.bitfire.postprocessing.PostProcessorEffect;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer.BUFF_RULE;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer.BUFF_RULE_STATUS;
import eidolons.game.core.Eidolons;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.PostProcessingOptions.POST_PROCESSING_OPTIONS;
import main.system.auxiliary.EnumMaster;

import java.util.LinkedHashMap;
import java.util.Map;

import static eidolons.game.battlecraft.ai.tools.ParamAnalyzer.BUFF_RULE_STATUS.*;

/**
 * Created by JustMe on 12/5/2018.
 */
public class PostFxUpdater {
    public static final POST_FX_FACTOR[] vals = POST_FX_FACTOR.values();
    PostProcessController controller;
    private Map<POST_FX_FACTOR, Float> fxMap = new LinkedHashMap<>();

    public PostFxUpdater(PostProcessController controller) {
        this.controller = controller;
    }

    public void apply(POST_FX_FACTOR factor, float coef) {
        Float prev = fxMap.get(factor);
        if (prev != null) {
            coef = coef * prev;
        }
        fxMap.put(factor, coef);
    }

    public void update() {
        Unit hero = Eidolons.MAIN_HERO;
        if (hero == null) {
            return;
        }
        fxMap.clear();

        if (isTestMode()) {
            applyTest(OptionsMaster.getPostProcessingOptions().getValue(
             POST_PROCESSING_OPTIONS.TEST_MODE));
            //            apply(POST_FX_FACTOR.FADE_COLOR, OptionsMaster.getPostProcessingOptions().getFloatValue(
            //             POST_PROCESSING_OPTIONS.TEST_SATURATE_COEF) / 100);
            //            apply(POST_FX_FACTOR.BLOOM, OptionsMaster.getPostProcessingOptions().getFloatValue(
            //             POST_PROCESSING_OPTIONS.TEST_BLOOM_COEF) / 100);
            //            apply(POST_FX_FACTOR.BLUR, OptionsMaster.getPostProcessingOptions().getFloatValue(
            //             POST_PROCESSING_OPTIONS.TEST_BLUR_COEF) / 100);
            applyFactors();
            return;
        }
        BUFF_RULE_STATUS
         status = ParamAnalyzer.getStatus(hero, BUFF_RULE.STAMINA);
        apply(status, BUFF_RULE.STAMINA);
        status = ParamAnalyzer.getStatus(hero, BUFF_RULE.FOCUS);
        apply(status, BUFF_RULE.FOCUS);
        status = ParamAnalyzer.getStatus(hero, BUFF_RULE.MORALE);
        apply(status, BUFF_RULE.MORALE);
        status = ParamAnalyzer.getStatus(hero, BUFF_RULE.WOUNDS);
        apply(status, BUFF_RULE.WOUNDS);

        //order?  
        // ++ bleeding

        applyFactors();
    }

    private void apply(BUFF_RULE_STATUS... status) {
        apply(status[0], BUFF_RULE.STAMINA);
        apply(status[1], BUFF_RULE.MORALE);
        apply(status[2], BUFF_RULE.FOCUS);
        apply(status[3], BUFF_RULE.WOUNDS);
    }
        private void apply(BUFF_RULE_STATUS status, BUFF_RULE rule) {
        switch (rule) {
            case STAMINA:
                if (status == CRITICAL) {
                    apply(POST_FX_FACTOR.FADE_COLOR, 1);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.FADE_COLOR, 0.6f);
                } else if (status == HIGH) {
                    apply(POST_FX_FACTOR.LIGHTEN, 0.4f);
                }
                break;
            case MORALE:
                if (status == CRITICAL) {
                    apply(POST_FX_FACTOR.BLUR, 1);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.BLUR, 0.6f);
                } else if (status == HIGH) {
                    apply(POST_FX_FACTOR.BLOOM, 0.4f);
                }

                break;
            case FOCUS:
                if (status == CRITICAL) {
                    apply(POST_FX_FACTOR.BLUR, 1);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.BLUR, 0.6f);
                } else if (status == HIGH) {
                    apply(POST_FX_FACTOR.BLOOM, 0.4f);
                }
                break;
                case WOUNDS:
                if (status == CRITICAL) {
                    apply(POST_FX_FACTOR.BLOODY, 1);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.BLOODY, 0.6f);
                }
                break;
        }
    }

    private void applyTest(String value) {
        FX_TEST_MODE mode = new EnumMaster<FX_TEST_MODE>().
         retrieveEnumConst(FX_TEST_MODE.class, value);
        if (mode != null)
            switch (mode) {
            case CRITICAL_ALL:
                apply(CRITICAL, CRITICAL, CRITICAL, CRITICAL);
                break;
            case HIGH_ALL:
                apply(HIGH, HIGH, HIGH, HIGH);
                break;
            case MIXED_1:
                apply(HIGH, CRITICAL, HIGH, CRITICAL);
                break;
            case MIXED_2:
                apply(CRITICAL, HIGH, CRITICAL, LOW);
                break;
            case LOW_ALL:
                apply(LOW, LOW, LOW, LOW);
                break;
            case STA:
                break;
            case STA2:
                break;
            case STA3:
                break;
            case FOC:
                break;
            case FOC2:
                break;
            case FOC3:
                break;
            case MOR:
                break;
            case MOR2:
                break;
            case MOR3:
                break;
            case WOU:
                break;
            case WOU2:
                break;
        }
    }

    private boolean isTestMode() {
        return true;
    }

    private void applyFactors() {
        for (POST_FX_FACTOR fxFactor : vals) {
            PostProcessorEffect effect = getEffect(fxFactor);
            if (effect == null) {
                continue;
            }
            Float coef = fxMap.get(fxFactor);
            if (coef == null) {
                effect.setEnabled(false);
            } else {
                effect.setBase(coef);
                effect.setEnabled(true);
            }

        }
    }

    private PostProcessorEffect getEffect(POST_FX_FACTOR fxFactor) {
        switch (fxFactor) {

            case DISCOLOR:
                break;
            case FADE_COLOR:
                return controller.getSaturate();
            case BLUR:
                return controller.getBlur();
            case CONTRAST:
                break;
            case BLOOM:
                return controller.getBloom();
            case DARKEN:
                return controller.getDarken();
            case LIGHTEN:
                break;
            case BLOODY:
                break;
        }
        return null;
    }

    public enum FX_TEST_MODE {
        CRITICAL_ALL,
        HIGH_ALL,
        MIXED_1,
        MIXED_2,
        LOW_ALL,

        STA,
        STA2,
        STA3,
        FOC,
        FOC2,
        FOC3,
        MOR,
        MOR2,
        MOR3,
        WOU,
        WOU2,
    }

    public enum POST_FX_FACTOR {
        DISCOLOR,
        FADE_COLOR,
        BLUR,
        CONTRAST,
        BLOOM,
        DARKEN,
        LIGHTEN,
        BLOODY, //red vignette

        //burn, disease,
        // night sight ,
        ;
    }

    public enum POST_FX_TEMPLATE {
        DIZZY,
        FATIGUE,
        ENERGIZED,
        FEAR,
        INSPIRED,;

        public final POST_FX_FACTOR[] factors;

        POST_FX_TEMPLATE(POST_FX_FACTOR... factors) {
            this.factors = factors;
        }
    }
}
