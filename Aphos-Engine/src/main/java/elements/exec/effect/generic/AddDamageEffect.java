package elements.exec.effect.generic;

import elements.content.enums.types.CombatTypes;
import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.effect.Effect;
import logic.execution.event.combat.CombatEvent;
import logic.execution.event.combat.CombatEventType;

import java.util.Map;

import static combat.sub.BattleManager.combat;

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
