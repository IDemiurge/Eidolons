package main.ability.conditions;

import main.elements.conditions.ConditionImpl;

public class SpellDamageCondition extends ConditionImpl {
    public SpellDamageCondition() {

    }

    @Override
    public boolean check() {
        // ref.getSpell()!=null ???
        return false;
    }

}
