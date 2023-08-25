package elements.exec.targeting;

import elements.exec.condition.Condition;
import elements.exec.targeting.area.MeleeTargeter;
import elements.exec.targeting.area.RangeTargeter;

import java.util.function.Supplier;

import static elements.exec.targeting.TargetingTemplates.TargetingType.*;


/**
 * Created by Alexander on 8/23/2023
 */
public class TargetingTemplates {

    public enum TargetingType {
        FIXED,  SELECTIVE, ALL
    }
    public enum TargetingTemplate {
        SELF(FIXED, () -> ref -> ref.getMatch() == ref.getSource()),
        // MELEE,
        // RANGE,
        //RAY(ALL, ...
        CLOSE_QUARTERS(SELECTIVE, () -> MeleeTargeter.getCloseQuartersCondition()),
        RANGED(SELECTIVE, () -> RangeTargeter.getRangeCondition());

        public final TargetingType type;
        public final Supplier<Condition> supplier;

        TargetingTemplate(TargetingType type, Supplier<Condition> supplier) {
            this.type = type;
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

    public enum ConditionTemplate {
        SELF_CHECK,
        UNTIL_ATTACK_OR_FALL

    }
}
