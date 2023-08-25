package elements.exec.preset;

import content.LinkedStringMap;
import elements.exec.Executable;
import elements.exec.effect.framework.EffectTemplate;
import elements.exec.targeting.TargetingTemplates;

import java.util.Map;

import static elements.exec.effect.framework.EffectTemplate.*;
import static elements.exec.preset.ExecPresetConstructor.construct;
import static elements.exec.targeting.TargetingTemplates.ConditionTemplate;
import static elements.exec.targeting.TargetingTemplates.ConditionTemplate.*;
import static elements.exec.targeting.TargetingTemplates.TargetingTemplate;
import static elements.exec.targeting.TargetingTemplates.TargetingTemplate.*;

/**
 * Created by Alexander on 8/23/2023
 */
public class ExecPresets {
    public static final Map<String, Executable> presets = new LinkedStringMap<>();

    public static void initPreset(String name,
                                  TargetingTemplate targetingTmlt,
                                  TargetingTemplates.ConditionTemplate activationConditionTmlt,
                                  EffectTemplate effectTmlt,
                                  ConditionTemplate conditionTmlt, boolean continuous,
                                  String targetingData,
                                  String fxArgs,String cndArgs,
                                  String activationCndArgs) {
        presets.put(name, construct(continuous, targetingTmlt, activationConditionTmlt, effectTmlt, conditionTmlt, targetingData, fxArgs, cndArgs, activationCndArgs));
    }

    public static void initAllPresets() {

    }

}
