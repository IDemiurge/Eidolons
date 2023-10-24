package logic.rules.combat.wounds;

import elements.exec.EntityRef;
import elements.stats.UnitParam;
import system.log.result.WoundResult;

/**
 * Created by Alexander on 8/21/2023
 */
public class Wounds {
    private static FaithWounds faithWounds = new FaithWounds();
    private static SanityWounds sanityWounds = new SanityWounds();
    private static HpWounds hpWounds = new HpWounds();
    public static WoundResult apply(int excessDamage, UnitParam value, EntityRef ref) {
        return switch (value){
            case Faith -> faithWounds.apply(excessDamage, ref);
            case Sanity -> sanityWounds.apply(excessDamage, ref);
            case Health -> hpWounds.apply(excessDamage, ref);
            default -> null;
        };
    }
}
