package elements.exec.preset;

import elements.exec.EntityRef;
import elements.exec.EntityRef.ReferenceKey;
import elements.exec.condition.Condition;
import elements.exec.effect.Effect;
import elements.exec.effect.ModifyStatEffect;

import java.util.function.Supplier;


/**
 * Created by Alexander on 8/23/2023
 */
public class ConstructTemplates {
    public enum TargetingTemplate{
        SELF(()-> ref -> ref.getMatch() == ref.getSource()),
        // MELEE,
        // RANGE,
;

        Supplier<Condition> supplier;

        TargetingTemplate(Supplier<Condition> supplier) {
            this.supplier = supplier;
        }
    }
    //more specific, used in targetingData?
    public enum TargetingKeyword {
        Close_Quarters,
        Melee,
        Long_Range,
        Range,
        Ray,
        Ray_2x,
        Any,
    }
    public enum EffectTemplate{
        MODIFY(()-> new ModifyStatEffect()),
        ;
        Supplier<Effect> supplier;

        EffectTemplate(Supplier<Effect> supplier) {
            this.supplier = supplier;
        }
    }
    public enum ConditionTemplate{
         UNTIL_ATTACK_OR_FALL

    }
}
