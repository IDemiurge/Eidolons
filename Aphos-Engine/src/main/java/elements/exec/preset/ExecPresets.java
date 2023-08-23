package elements.exec.preset;

import content.LinkedStringMap;
import elements.exec.ActionExecutable;
import elements.exec.Executable;
import elements.exec.effect.Effect;
import elements.exec.effect.framework.ContinuousEffect;
import elements.exec.targeting.Targeting;
import framework.data.TypeData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static elements.exec.preset.ConstructTemplates.*;
import static elements.exec.preset.ConstructTemplates.ConditionTemplate.*;
import static elements.exec.preset.ConstructTemplates.EffectTemplate.*;
import static elements.exec.preset.ConstructTemplates.TargetingTemplate.*;
/**
 * Created by Alexander on 8/23/2023
 */
public class ExecPresets {
    public static final Map<String, Executable> presets = new LinkedStringMap<>();

    //does not support multiple exec pairs
    private static Executable construct(boolean continuous,
                                        TargetingTemplate targetingTmlt,
                                        EffectTemplate effectTmlt,
                                        ConditionTemplate conditionTmlt, String... args) {
        List<Pair<Targeting, Effect>> toExecute=   new ArrayList<>() ;
        Targeting targeting = createTargeting(targetingTmlt, args[0], args[1]);
        Effect effect= createEffect(effectTmlt, conditionTmlt, args[2], args[3]);
        if (continuous){
            //check?
            effect = new ContinuousEffect(effect);
        }
        toExecute.add(new ImmutablePair<>(targeting, effect));
        Executable exec = new ActionExecutable(toExecute);
        return exec;
    }

    private static Effect createEffect(EffectTemplate effectTmlt, ConditionTemplate conditionTmlt, String fxArgs, String cndArgs) {
        //switches?
        Effect effect= effectTmlt.supplier.get();
        TypeData data = createData(effect.getArgNames(), fxArgs);
        effect.setData(data);
        if (conditionTmlt!=null){
            //wrap
        }
        return effect;
    }

    private static Targeting createTargeting(TargetingTemplate targetingTmlt, String targetingData, String cndArgs) {
      Targeting targeting=  new Targeting();
      targeting.setCondition(targetingTmlt.supplier.get());
        // TypeData data = createData(effect.getArgNames(), fxArgs);
        // effect.setData(data);
        return  targeting;
    }

    public static TypeData createData(String[] argNames, String args) {
        Iterator<String> split = Arrays.stream(args.split(";")).iterator();
        Map<String, Object> map = Arrays.stream(argNames).collect(Collectors.toMap(key-> key, value-> split.next()));
        return new TypeData(map);
    }

    public static void initAllPresets(){
        //it seems that with this syntax, we can easily create EXEC's from plain strings
        //but what will happen with nested stuff?
        Executable executable = construct(true, SELF, MODIFY, UNTIL_ATTACK_OR_FALL, null, null, "defense_min;2", "");
        presets.put("Brace", executable);

    }

}
