package main.ability.effects.oneshot.item;

import main.ability.effects.OneshotEffect;
import main.ability.effects.MicroEffect;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.unit.Unit;

public class EquipEffect extends MicroEffect  implements OneshotEffect {

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
        if (item == null) {
            if (quickItem) {
                item = (DC_HeroItemObj) ref.getObj(KEYS.ITEM);
            } else {
                item = (DC_HeroItemObj) ref.getObj((weapon) ? KEYS.WEAPON
                        : KEYS.ARMOR);
            }
        }
        // check if item can be equipped at all!

        Unit hero = (Unit) ref.getTargetObj();
        ITEM_SLOT slot = ItemEnums.ITEM_SLOT.ARMOR;
        // check if main hand is occupied

        boolean mainHand = true;
        // item.getProp(prop)
        if (hero.getMainWeapon() != null && hero.getSecondWeapon() == null) {
            mainHand = false;
        }
        if (weapon || quickItem) {
            slot = (mainHand || quickItem) ? ItemEnums.ITEM_SLOT.MAIN_HAND
                    : ItemEnums.ITEM_SLOT.OFF_HAND;
        }

        ref.setID((weapon || quickItem) ? KEYS.WEAPON : KEYS.ARMOR, item
                .getId());

        return hero.equip(item, slot);
    }
}
