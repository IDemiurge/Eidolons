package elements.exec.effect.modifier;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.effect.Effect;
import logic.execution.event.combat.CombatEventType;

import java.util.Map;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/25/2023
 */
public  abstract class ModifierEffect extends Effect {
    CombatEventType eventType;
    Condition condition;

    public ModifierEffect(CombatEventType eventType, Condition condition) {
        this.eventType = eventType;
        this.condition = condition;
    }

    public abstract void modify(Map map);

    @Override
    protected void applyThis(EntityRef ref) {
        combat().getEventHandler().addModifier(eventType, condition, map -> modify(map));
    }
}
