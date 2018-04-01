package eidolons.game.battlecraft.rules.combat.damage;

import main.elements.conditions.Condition;

/**
 * Created by JustMe on 3/18/2017.
 */
@Deprecated
public class ConditionalDamage extends Damage {
    Condition condition;

    public ConditionalDamage(Damage damage, Condition condition) {
        setDmgType(damage.getDmgType());
        setRef(damage.getRef());
        setAmount(damage.getAmount());
        this.condition = condition;
    }


}
