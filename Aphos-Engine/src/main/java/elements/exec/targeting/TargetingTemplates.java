package elements.exec.targeting;

import elements.exec.condition.Condition;
import elements.exec.condition.ConditionBuilder;
import elements.exec.targeting.area.MeleeTargeter;
import elements.exec.targeting.area.RangeTargeter;
import framework.data.DataManager;

import java.util.Map;
import java.util.function.Supplier;

import static elements.exec.targeting.TargetingTemplates.TargetingType.*;


/**
 * Created by Alexander on 8/23/2023
 */
public class TargetingTemplates {
    private static Map emptyMap;

    public static Map map(String s) {
        return DataManager.stringToMap(s);
    }

    public enum TargetingType {
        FIXED, SELECTIVE, ALL, RANDOM,
    }

    public enum TargetingTemplate {
        TARGET(FIXED, () -> ref -> ref.getMatch() == ref.getPrevTarget()),
        SELF(FIXED, () -> ref -> ref.getMatch() == ref.getSource()),
        // MELEE,
        // RANGE,
        //RAY(ALL, ...
        RANDOM_LEFT_RIGHT(RANDOM,()-> ConditionBuilder.prebuild(true,
                ConditionTemplate.POS_CHECK, map("positions=left,right")).or().build()),

        MELEE(SELECTIVE, () ->MeleeTargeter.getMeleeCondition()),

        CLOSE_QUARTERS(SELECTIVE, () ->MeleeTargeter.getCloseQuartersCondition()),

        RANGED(SELECTIVE, () ->RangeTargeter.getRangeCondition());

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
        TARGET,
        SELF,
        SELF_VALUE_CHECK,
        SELF_IDENTITY_CHECK,
        IDENTITY_CHECK,
        POS_CHECK,
        UNTIL_ATTACK_OR_FALL

    }
}
