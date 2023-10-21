package logic.rules.combat.wounds;

import elements.exec.EntityRef;
import elements.stats.UnitParam;

/**
 * Created by Alexander on 8/21/2023
 */
public class Wounds {
    static FaithWounds faithWounds;
    static SanityWounds sanityWounds;
    static HpWounds hpWounds;
    public static void apply(int excessDamage, UnitParam value, EntityRef ref) {
        if (value == UnitParam.Faith) {
            faithWounds.apply(excessDamage, ref);
        } else
        if (value == UnitParam.Sanity) {
            sanityWounds.apply(excessDamage, ref);
        } else
        if (value == UnitParam.Health) {
            DeathDoorRule.apply(ref);
            hpWounds.apply(excessDamage, ref);

        }
    }
}
