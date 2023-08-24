package logic.rules.combat.wounds;

import elements.exec.EntityRef;
import framework.entity.Entity;

/**
 * Created by Alexander on 8/22/2023
 */
public class WoundsRule {

    public void apply(int excessDamage, EntityRef ref) {
        Entity target = ref.get("target");
        //TODO
        // Wound wound = getWound(target);
        // target.addWound(wound);
        // effect = wound.getEffect();
        // effect.apply(ref.copy().setValueInt(excessDamage));
    }
}
