package elements.exec;

import elements.exec.effect.Effect;
import elements.exec.targeting.TargetGroup;
import elements.exec.targeting.Targeting;
import framework.entity.field.FieldEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ActionExecutable implements Executable{

    List<Pair<Targeting, Effect>> targetedEffects;

    public ActionExecutable(List<Pair<Targeting, Effect>> targetedEffects) {
        this.targetedEffects = targetedEffects;
    }

    @Override
    public List<Pair<Targeting, Effect>> getTargetedEffects() {
        return targetedEffects;
    }

    public TargetGroup selectTargets(EntityRef ref) {
        //wait for UI?!
        // TargetGroup availableTargets= ;

        //TODO MOCK!
        // new UiEvent(UiEventType.Selection, );
        List<FieldEntity> fullList=     new ArrayList<>() ;
        for (Pair<Targeting, Effect> pair : targetedEffects) {
           // list =  pair.getLeft().select(ref).var
           //  fullList.addAll
        }
        return new TargetGroup(fullList);
    }

}
