package eidolons.ability.effects.oneshot.item;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.item.HeroItem;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.ItemEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ToggleWeaponSetEffect extends DC_Effect {


    @Override
    public boolean applyThis() {
        Unit hero = (Unit) ref.getSourceObj();

        HeroItem main = hero.unequip(ItemEnums.ITEM_SLOT.MAIN_HAND);
        HeroItem off = hero.unequip(ItemEnums.ITEM_SLOT.OFF_HAND);

        HeroItem main2 = hero.unequip(ItemEnums.ITEM_SLOT.RESERVE_MAIN_HAND);
        HeroItem off2 = hero.unequip(ItemEnums.ITEM_SLOT.RESERVE_OFF_HAND);

        hero.equip(main , ItemEnums.ITEM_SLOT.RESERVE_MAIN_HAND);
        hero.equip(off , ItemEnums.ITEM_SLOT.RESERVE_OFF_HAND);

        hero.equip(main2, ItemEnums.ITEM_SLOT.MAIN_HAND);
        hero.equip(off2, ItemEnums.ITEM_SLOT.OFF_HAND);

        hero.removeFromInventory(main);
        hero.removeFromInventory(main2);
        hero.removeFromInventory(off);
        hero.removeFromInventory(off2);

        if (hero.isPlayerCharacter()) {
            GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO, hero);
        }

        return true;
    }
}
