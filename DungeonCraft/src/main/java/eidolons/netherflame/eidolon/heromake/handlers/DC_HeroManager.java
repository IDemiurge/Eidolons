package eidolons.netherflame.eidolon.heromake.handlers;

import eidolons.entity.item.HeroItem;
import eidolons.entity.item.trinket.JewelryItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.system.ObjUtilities;
import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.obj.Obj;

public class DC_HeroManager extends HeroManager {

    public DC_HeroManager(DC_Game game) {
        super(game);

    }

    @Override
    protected int addJewelryItem(Unit hero, Entity type) {
        HeroItem item;
        if (type instanceof JewelryItem) {
            item = (HeroItem) type;
        } else {
            item = (HeroItem) ObjUtilities
             .findObjByType(type, hero.getInventory());
        }
        int result = super.addJewelryItem(hero, type);
        if (result == 0) {
            return 0;
        }
        hero.addJewelryItem((JewelryItem) item);
        hero.removeFromInventory(item);
        update(hero);
        return result;
    }

    @Override
    public void removeJewelryItem(Unit hero, Entity type) {
        HeroItem item = (HeroItem) ObjUtilities.findObjByType(type, hero.getJewelry());
        hero.removeJewelryItem(item);
        hero.addItemToInventory(item);
        update(hero);
    }

    @Override
    public boolean addItem(Unit hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP) {
        Obj cell = game.getCell(hero.getCoordinates());
        HeroItem item = (HeroItem) ObjUtilities.findObjByType(type, hero.getGame()
         .getDroppedItemManager().getDroppedItems(cell));
        boolean result = hero.getGame().getDroppedItemManager().pickUp(cell, item);
        if (!result) {
            return false;
        }
        saveHero(hero);
        // hero.addProperty(PROP, "" + item.getId());
        // hero.getType().addProperty(PROP, "" + item.getId());
        hero.addItemToInventory(item);

        refreshInvWindow();
        return true;
    }

    @Override
    public void removeItem(Unit hero, Entity type, PROPERTY prop, OBJ_TYPE TYPE, boolean free) {
        // TODO drop from inventory
        for (HeroItem item : hero.getInventory()) {
            if (item.getType() == type) {
                hero.dropItemFromInventory(item);
                break;
            }
        }
        // if (hero.getInventory().isEmpty())
        // hero.setInventory(null); //in DC_HeroObj for now
        update(hero);
    }

    @Override
    public void removeSlotItem(Unit hero, ITEM_SLOT slot) {
        if (slot == ItemEnums.ITEM_SLOT.ARMOR) {
            if (!hero.checkBool(GenericEnums.STD_BOOLS.ARMOR_CHANGE)) {
                return;
            }
        }
        hero.unequip(hero.getItem(slot), false);
        update(hero);
    }

    @Override
    public void removeQuickSlotItem(Unit hero, Entity type) {
        QuickItem item = null;
        if (type instanceof QuickItem) {
            item = (QuickItem) type;
        }
        for (QuickItem itemObj : hero.getQuickItems()) {
            if (itemObj.getType() == type) {
                item = itemObj;
                break;
            }
        }
        hero.removeQuickItem(item);
        hero.addItemToInventory(item);
        // if (hero.getQuickItems().isEmpty())
        // hero.setQuickItems(null); //in DC_HeroObj for now
        update(hero);
    }

    private int addQuickItem_(Unit hero, HeroItem itemObj) {
        hero.removeFromInventory(itemObj);
        // if (itemObj instanceof Trap) {
        // } else
        if (itemObj instanceof WeaponItem) {
            hero.getQuickItems().add(
             new QuickItem(itemObj.getType(), hero.getOwner(), game, hero
              .getRef(), true));
        } else {
            hero.getQuickItems().add((QuickItem) itemObj);
        }
        update(hero);
        return 1;
    }

    @Override
    public int addQuickItem(Unit hero, Entity type) {
        if (hero.isQuickSlotsFull()) {
            return 0;
        }
        if (type instanceof HeroItem) {
            return addQuickItem_(hero, (HeroItem) type);
        }
        for (HeroItem itemObj : hero.getInventory()) {
            if (itemObj.getType() == type) {
                return addQuickItem_(hero, itemObj);
            }
        }
        return 0;
    }


    @Override
    public int setHeroItem(Unit hero, ITEM_SLOT slot, Entity type) {

        int result = 0;
        if (slot == ItemEnums.ITEM_SLOT.ARMOR) {
            if (!hero.checkBool(GenericEnums.STD_BOOLS.ARMOR_CHANGE)) {
                return 0;
            }
            result++;
        }
        HeroItem slotItem = null;
        QuickItem quick = null;
        if (type instanceof QuickItem) {
            quick = ((QuickItem) type);
            slotItem = quick.getWrappedWeapon();
        }
        if (type instanceof HeroItem) {
            slotItem = (HeroItem) type;
        } else {
            for (HeroItem item : hero.getInventory()) {
                if (item.getType() == type) {
                    slotItem = item;
                    break;
                }
            }// item
        }
        hero.setItem(slotItem, slot);
        if (quick != null) {
            hero.removeQuickItem(quick);
        } else {
            hero.removeFromInventory(slotItem);
        }

        result++;

        if (result > 0) {
            update(hero);
        }
        return result;
    }
}
