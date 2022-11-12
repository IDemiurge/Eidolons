package eidolons.ability.effects.oneshot.item;

import eidolons.entity.item.HeroItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;

public class EquipEffect extends MicroEffect implements OneshotEffect {

    private HeroItem item;
    private final Boolean weapon;
    private Boolean quickItem = false;

    @OmittedConstructor
    public EquipEffect(HeroItem item) {
        this.item = item;
        weapon = item instanceof WeaponItem;
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
                item = (HeroItem) ref.getObj(KEYS.ITEM);
            } else {
                item = (HeroItem) ref.getObj((weapon) ? KEYS.WEAPON
                 : KEYS.ARMOR);
            }
        }
        // preCheck if item can be equipped at all!

        Unit hero = (Unit) ref.getTargetObj();
        ITEM_SLOT slot = ItemEnums.ITEM_SLOT.ARMOR;
        // preCheck if main hand is occupied

        boolean mainHand = true;
        // item.getProp(prop)
        if (hero.getMainWeapon() != null && hero.getOffhandWeapon() == null) {
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
