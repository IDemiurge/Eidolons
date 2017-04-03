package main.game.logic.combat.attack;

import main.ability.conditions.special.SneakCondition;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;

/**
 * Created by JustMe on 3/14/2017.
 */
public class SneakRule {
    private static ConditionImpl sneakCondition;

    public static boolean checkSneak(Ref ref) {
        if (sneakCondition == null) {
            sneakCondition = new SneakCondition();
        }
        return sneakCondition.preCheck(ref);
    }
}
