package elements.exec.targeting.area;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;

/**
 * Created by Alexander on 8/23/2023
 */
public class MeleeTargeter {
    public static Condition getCloseQuartersCondition() {
        return new Condition() {
            @Override
            public boolean check(EntityRef ref) {
                return false;
            }
        };
    }
}
