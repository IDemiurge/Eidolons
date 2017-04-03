package main.ability.conditions;

import main.elements.conditions.ConditionImpl;
import main.entity.Ref;

public class SpellDamageCondition extends ConditionImpl {
    public SpellDamageCondition() {

    }

    @Override
    public boolean check(Ref ref) {
        // ref.getSpell()!=null ???
        return false;
    }

}
