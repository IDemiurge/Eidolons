package main.ability.effects.special;

import main.ability.effects.oneshot.MicroEffect;
import main.content.CONTENT_CONSTS.ITEM_SLOT;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroItemObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_WeaponObj;

public class EquipEffect extends MicroEffect {

    private DC_HeroItemObj item;
    private Boolean weapon = false;
    private Boolean quickItem = false;

    @OmittedConstructor
    public EquipEffect(DC_HeroItemObj item) {
        this.item = item;
        weapon = item instanceof DC_WeaponObj;
    }

    public EquipEffect() {
        quickItem = true;
        weapon = true;
    }

    public EquipEffect(Boolean weapon) {
        this.weapon = weapon;
    }

    @Override
    public boolean applyThis() {
        if (item == null)
            if (quickItem) {
                item = (DC_HeroItemObj) ref.getObj(KEYS.ITEM);
            } else
                item = (DC_HeroItemObj) ref.getObj((weapon) ? KEYS.WEAPON
                        : KEYS.ARMOR);
        // check if item can be equipped at all!

        DC_HeroObj hero = (DC_HeroObj) ref.getTargetObj();
        ITEM_SLOT slot = ITEM_SLOT.ARMOR;
        // check if main hand is occupied

        boolean mainHand = true;
        // item.getProp(prop)
        if (hero.getMainWeapon() != null && hero.getSecondWeapon() == null)
            mainHand = false;
        if (weapon || quickItem)
            slot = (mainHand || quickItem) ? ITEM_SLOT.MAIN_HAND
                    : ITEM_SLOT.OFF_HAND;

        ref.setID((weapon || quickItem) ? KEYS.WEAPON : KEYS.ARMOR, item
                .getId());

        return hero.equip(item, slot);
    }
}
