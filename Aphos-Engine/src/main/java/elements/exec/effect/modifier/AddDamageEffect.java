package elements.exec.effect.modifier;

import elements.content.enums.types.CombatTypes;
import elements.exec.condition.Condition;
import logic.execution.event.combat.CombatEventType;

import java.util.Map;

/**
 * Created by Alexander on 8/25/2023
 */
public class AddDamageEffect extends ModifierEffect {

    private CombatTypes.DamageType damageType;

    public AddDamageEffect(CombatEventType eventType, Condition condition) {
        super(eventType, condition);
    }

    public void modify(Map map) {
        int amount= 1;
        //if null - increase ALL?
        Object o = map.get(damageType);
        if (o!=null){

        } else {
            map.put(damageType, amount);
        }
    }
}
