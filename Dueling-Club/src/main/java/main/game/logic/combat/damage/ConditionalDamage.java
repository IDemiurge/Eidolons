package main.game.logic.combat.damage;

import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.elements.conditions.Condition;
import main.entity.Ref;

/**
 * Created by JustMe on 3/18/2017.
 */
@Deprecated
public class ConditionalDamage extends  Damage{
    Condition condition;

    public ConditionalDamage(Damage damage, Condition condition) {
        this(damage.getDmg_type(), damage .getRef(), damage .getAmount(), condition);
    }
    public ConditionalDamage(DAMAGE_TYPE damage_type, Ref ref, int amount, Condition condition) {
        super(damage_type, ref, amount);
        this.condition = condition;
    }
}
