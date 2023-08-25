package elements.exec.preset;

import content.LinkedStringMap;
import elements.exec.ActionExecutable;
import elements.exec.Executable;
import elements.exec.effect.Effect;
import elements.exec.effect.framework.EffectTemplate;
import elements.exec.effect.generic.ContinuousEffect;
import elements.exec.targeting.Targeting;
import elements.exec.targeting.TargetingTemplates;
import framework.data.TypeData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 8/24/2023
 */
public class ExecPresetConstructor {

    //does not support multiple exec pairs
    //maybe we can basically just merge executables...
    public static Executable construct(boolean continuous,
                                       TargetingTemplates.TargetingTemplate targetingTmlt,
                                       TargetingTemplates.ConditionTemplate activationConditionTmlt,
                                       EffectTemplate effectTmlt,
                                       TargetingTemplates.ConditionTemplate conditionTmlt,
                                       String targetingData,
                                       String fxArgs,String cndArgs,
                                       String activationCndArgs) {
        List<Pair<Targeting, Effect>> toExecute=   new ArrayList<>() ;
        Targeting targeting = createTargeting(targetingTmlt, targetingData);
        Effect effect= createEffect(effectTmlt, conditionTmlt, fxArgs, cndArgs);
        if (continuous){
            //check?
            //TODO
            Map map=null;
            effect = new ContinuousEffect(effect, elements.exec.condition.ConditionBuilder.build(conditionTmlt, map));
        }
        toExecute.add(new ImmutablePair<>(targeting, effect));
        Executable exec = new ActionExecutable(toExecute);
        if (activationConditionTmlt != null) {
            //activationCndArgs
            // exec.setActivationCondition(aCondition);
        }
        return exec;
    }

    private static Effect createEffect(EffectTemplate effectTmlt, TargetingTemplates.ConditionTemplate conditionTmlt, String fxArgs, String cndArgs) {
        //switches?
        Effect effect= effectTmlt.supplier.get();
        TypeData data = createData(effect.getArgNames(), fxArgs);
        effect.setData(data);
        if (conditionTmlt!=null){
            //wrap -  use for both continuous wrap and oneshot conditional ; just make sure it isn't used by both...
        }
        return effect;
    }

    private static Targeting createTargeting(TargetingTemplates.TargetingTemplate targetingTmlt, String targetingData) {
        Targeting targeting=  new Targeting();
        targeting.setCondition(targetingTmlt.supplier.get());
        targeting.setType(targetingTmlt.type);
        TypeData data = createData(null, targetingData); //activation?
        targeting.setData(data);
        return  targeting;
    }

    public static TypeData createData(String[] argNames, String args) {
        if (args==null)
            return new TypeData(new LinkedStringMap<>());
        Iterator<String> split = Arrays.stream(args.split(";")).iterator();
        Map<String, Object> map = Arrays.stream(argNames).collect(Collectors.toMap(key-> key, value-> split.next()));
        return new TypeData(map);
    }
}
