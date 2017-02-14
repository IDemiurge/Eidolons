package main.ability.effects.special;

import main.ability.effects.oneshot.MicroEffect;
import main.content.CONTENT_CONSTS.ITEM_SLOT;
import main.entity.obj.unit.DC_HeroObj;

public class UnequipEffect extends MicroEffect {

    ITEM_SLOT slot;
    boolean drop;

    public UnequipEffect(ITEM_SLOT s, Boolean drop) {
        this.drop = drop;
        this.slot = s;
    }

    @Override
    public boolean applyThis() {
        DC_HeroObj target = (DC_HeroObj) ref.getTargetObj();
        target.unequip(slot, drop);
        return true;
    }

}
