package eidolons.ability.effects.oneshot.item;

import eidolons.entity.unit.Unit;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;

public class UnequipEffect extends MicroEffect implements OneshotEffect {

    ITEM_SLOT slot;
    boolean drop;

    public UnequipEffect(ITEM_SLOT s, Boolean drop) {
        this.drop = drop;
        this.slot = s;
    }

    @Override
    public boolean applyThis() {
        Unit target = (Unit) ref.getTargetObj();
        target.unequip(slot, drop);
        return true;
    }

}
