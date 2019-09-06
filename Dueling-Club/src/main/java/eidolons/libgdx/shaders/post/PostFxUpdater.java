package eidolons.libgdx.shaders.post;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.bitfire.postprocessing.PostProcessorEffect;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer.BUFF_RULE;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer.BUFF_RULE_STATUS;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.PostProcessingOptions.POST_PROCESSING_OPTIONS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
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
    private Map<POST_FX_FACTOR, FloatAction> fxMap = new LinkedHashMap<>();

    public static Boolean heroFxOff;
    public static Boolean shadowFxOff;
    Actor actor;
    private POST_FX_TEMPLATE template;

    public PostFxUpdater(PostProcessController controller, Actor actor) {
        this.controller = controller;
        this.actor = actor;
        bindEvents();
    }

    public void apply(POST_FX_FACTOR factor, float coef) {
        FloatAction prev = fxMap.get(factor);
        if (prev != null) {
            prev.setStart(prev.getValue());
            prev.restart();
            prev.setEnd(coef);
        } else {
            prev = new FloatAction(0, coef);
            prev.setDuration(getDuration(coef, factor));
            prev.setInterpolation(Interpolation.fade);
        }
        actor.addAction(prev);
        fxMap.put(factor, prev);
    }

    private float getDuration(float coef, POST_FX_FACTOR factor) {
        return 5f;
    }

    public void bindEvents() {
        GuiEventManager.bind(GuiEventType.POST_PROCESSING_RESET, p -> {
            fxMap.clear();
            template = null;
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>>>>>>>>  Post processing template reset! ");
        });
        GuiEventManager.bind(GuiEventType.POST_PROCESSING, p -> {
//            PostProcessingOptions options = (PostProcessingOptions) p.getVar();
//            update(options);
            fxMap.clear();
            template = (POST_FX_TEMPLATE) p.get();
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>>>>>>>>  Post processing template : " + template);
//            bloom.setEnabled(true);
//            vignette.setEnabled(true);
//            blur.setEnabled(true);
//            bloom.applyCoef(1.2f);
//            vignette.applyCoef(1.5f);
//            blur.applyCoef(1.63f);
//            motionBlur.applyCoef(1.2f);
//            saturate.applyCoef(0.82f);
        });
    }

    public void update() {
        Unit hero = Eidolons.MAIN_HERO;
        if (hero == null) {
            return;
        }
//        fxMap.clear();
        if (template != null   ) {
            applyTemplate(template);
            applyFactors();
            return;
        }
        if (EidolonsGame.BRIDGE) {
//            apply(POST_FX_FACTOR.FADE_COLOR, 0.3f);
//            apply(POST_FX_FACTOR.LIGHTEN, 0.8f);
//            apply(POST_FX_FACTOR.BLOOM, 0.2f);
//            apply(POST_FX_FACTOR.BLUR, 0.1f);
//            applyFactors();
        }

        if (isTestMode()) {
            applyTest(OptionsMaster.getPostProcessingOptions().getValue(
                    POST_PROCESSING_OPTIONS.TEST_MODE));
            applyFactors();
            return;
        }

        if (heroFxOff != true) {
            BUFF_RULE_STATUS
                    status = ParamAnalyzer.getStatus(hero, BUFF_RULE.STAMINA);
            apply(status, BUFF_RULE.STAMINA);
            status = ParamAnalyzer.getStatus(hero, BUFF_RULE.FOCUS);
            apply(status, BUFF_RULE.FOCUS);
            status = ParamAnalyzer.getStatus(hero, BUFF_RULE.MORALE);
            apply(status, BUFF_RULE.MORALE);
            status = ParamAnalyzer.getStatus(hero, BUFF_RULE.WOUNDS);
            apply(status, BUFF_RULE.WOUNDS);
        }
        //order?
        // ++ bleeding

        applyFactors();
    }

    private void applyTemplate(POST_FX_TEMPLATE template) {
        switch (template) {
            case DIZZY:
                break;
            case FATIGUE:
                break;
            case ENERGIZED:
                break;
            case FEAR:
                //dunno why but it looks dope
                apply(POST_FX_FACTOR.FADE_COLOR, 0.0f);
                apply(POST_FX_FACTOR.BLOOM, 0f);
                apply(POST_FX_FACTOR.LENS, 0.0f);
                apply(POST_FX_FACTOR.BLUR, 0.0f);
                break;
            case INSPIRED:
                break;
            case MAZE:
                apply(POST_FX_FACTOR.FADE_COLOR, 0.8f);
                apply(POST_FX_FACTOR.BLUR, 0.1f);
                break;
            case MOSAIC:
//                apply(POST_FX_FACTOR.BLOOM, 0.1f);
//                apply(POST_FX_FACTOR.FADE_COLOR, 0.8f);
//                apply(POST_FX_FACTOR.BLUR, 0.1f);
                break;
            case PALE_ASPECT:
                apply(POST_FX_FACTOR.FADE_COLOR, 0.8f);
                apply(POST_FX_FACTOR.BLUR, 0.1f);
                break;
            case UNCONSCIOUS:
                if (shadowFxOff == true) {
                    return;
                }
                apply(POST_FX_FACTOR.FADE_COLOR, 0.8f);
//                apply(POST_FX_FACTOR.DISCOLOR, 0.5f);
                apply(POST_FX_FACTOR.BLOOM, 1.05f);
                apply(POST_FX_FACTOR.LENS, 0.3f);
                apply(POST_FX_FACTOR.BLUR, 0.5f);
                break;
        }
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
                    apply(POST_FX_FACTOR.BLUR, 0.5f);
//                    apply(POST_FX_FACTOR.DARKEN, 0.7f);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.FADE_COLOR, 0.7f);
//                    apply(POST_FX_FACTOR.DARKEN, 0.9f);
                    apply(POST_FX_FACTOR.SMOOTH, 1.2f);
                    apply(POST_FX_FACTOR.BLUR, 0.2f);
                } else if (status == HIGH) {
//                    apply(POST_FX_FACTOR.LIGHTEN, 1.2f);
                    apply(POST_FX_FACTOR.SMOOTH, 1.2f);
                }
                break;
            case MORALE:
                if (status == CRITICAL) {
                    apply(POST_FX_FACTOR.BLUR, 1);
                    apply(POST_FX_FACTOR.DISCOLOR, 1);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.BLUR, 0.6f);
                    apply(POST_FX_FACTOR.FADE_COLOR, 0.6f);
                } else if (status == HIGH) {
//                    apply(POST_FX_FACTOR.BLOOM, 1.21f);
//                    apply(POST_FX_FACTOR.LIGHTEN, 1.2f);
//                    apply(POST_FX_FACTOR.SMOOTH, 1.2f);
//                    apply(POST_FX_FACTOR.LENS2, 1.2f);
                }

                break;
            case FOCUS:
                if (status == CRITICAL) {
//                    apply(POST_FX_FACTOR.DISTORT, 1);
                    apply(POST_FX_FACTOR.BLUR, 0.6f);
                    apply(POST_FX_FACTOR.MOTION_BLUR, 0.6f);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.BLUR, 0.4f);
                    apply(POST_FX_FACTOR.MOTION_BLUR, 0.4f);
                } else if (status == HIGH) {
                    apply(POST_FX_FACTOR.LENS2, 1.2f);
//                    apply(POST_FX_FACTOR.SMOOTH, 1.2f);
//                    apply(POST_FX_FACTOR.DISTORT, -0.31f);
                }
                break;

            case WOUNDS:
                if (status == CRITICAL) {
                    apply(POST_FX_FACTOR.BLOODY, 1);
//                    apply(POST_FX_FACTOR.DISCOLOR, 1);
                } else if (status == LOW) {
                    apply(POST_FX_FACTOR.BLOODY, 0.6f);
//                    apply(POST_FX_FACTOR.DISCOLOR, 0.6f);
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
        return false;
    }

    private void applyFactors() {

        for (POST_FX_FACTOR fxFactor : vals) {
            PostProcessorEffect effect = getEffect(fxFactor);
            if (effect == null) {
                continue;
            }
            FloatAction action = fxMap.get(fxFactor);
            if (action == null) {
                effect.setEnabled(false);
            } else {
                effect.setBase(action.getValue());
                effect.setEnabled(true);
            }

        }
    }

    //            if (fxFactor== POST_FX_FACTOR.BLOOM) { TODO igg demo
//                fxMap.remove(POST_FX_FACTOR.LENS2);
//                fxMap.remove(POST_FX_FACTOR.FADE_COLOR);
//                fxMap.remove(POST_FX_FACTOR.SMOOTH);
//            }
    private PostProcessorEffect getEffect(POST_FX_FACTOR fxFactor) {
        switch (fxFactor) {

            case DISTORT:
                return controller.getCurvature();
            case DISCOLOR:
                return controller.getDiscolor();
            case FADE_COLOR:
                return controller.getSaturate();
            case MOTION_BLUR:
                return controller.getMotionBlur();
            case BLUR:
                return controller.getBlur();
            case LENS:
                return controller.getLens();
            case SMOOTH:
                return controller.getNfaa();
            case LENS2:
                return controller.getLens2();
            case BLOOM:
                return controller.getBloom();
            case DARKEN:
                return controller.getDarken();
            case LIGHTEN:
                return controller.getBloomBright();
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
        LENS,
        BLOOM,
        DARKEN,
        LIGHTEN,
        BLOODY, //red vignette
        MOTION_BLUR,
        //burn, disease,
        // night sight ,
        DISTORT, SMOOTH, LENS2;
    }

    public enum POST_FX_TEMPLATE {
        DIZZY,
        FATIGUE,
        ENERGIZED,
        FEAR,
        INSPIRED, UNCONSCIOUS(),

        PALE_ASPECT, MAZE(), MOSAIC();

        public final POST_FX_FACTOR[] factors;

        POST_FX_TEMPLATE(POST_FX_FACTOR... factors) {
            this.factors = factors;
        }
    }
}
