package main.client.cc;

import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.ObjUtilities;

public class DC_HeroManager extends HeroManager {

    public DC_HeroManager(DC_Game game) {
        super(game);

    }

    @Override
    protected int addJewelryItem(Unit hero, Entity type) {
        DC_HeroItemObj item = (DC_HeroItemObj) ObjUtilities
                .findObjByType(type, hero.getInventory());
        int result = super.addJewelryItem(hero, type);
        if (result == 0) {
            return 0;
        }
        hero.addJewelryItem(item);
        hero.removeFromInventory(item);
        update(hero);
        return result;
    }

    @Override
    public void removeJewelryItem(Unit hero, Entity type) {
        DC_HeroItemObj item = (DC_HeroItemObj) ObjUtilities.findObjByType(type, hero.getJewelry());
        hero.removeJewelryItem(item);
        hero.addItemToInventory(item);
        update(hero);
    }

    @Override
    public boolean addItem(Unit hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP) {
        Obj cell = game.getCellByCoordinate(hero.getCoordinates());
        DC_HeroItemObj item = (DC_HeroItemObj) ObjUtilities.findObjByType(type, hero.getGame()
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
        for (DC_HeroItemObj item : hero.getInventory()) {
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
        for (DC_QuickItemObj itemObj : hero.getQuickItems()) {
            if (itemObj.getType() == type) {
                hero.removeQuickItem(itemObj);
                hero.addItemToInventory(itemObj);
                break;
            }
        }
        // if (hero.getQuickItems().isEmpty())
        // hero.setQuickItems(null); //in DC_HeroObj for now
        update(hero);
    }

    @Override
    public int addQuickItem(Unit hero, Entity type) {
        if (hero.isQuickSlotsFull()) {
            return 0;
        }
        for (DC_HeroItemObj itemObj : hero.getInventory()) {
            if (itemObj.getType() == type) {
                hero.removeFromInventory(itemObj);
                // if (itemObj instanceof Trap) {
                // } else
                if (itemObj instanceof DC_WeaponObj) {
                    hero.getQuickItems().add(
                            new DC_QuickItemObj(itemObj.getType(), hero.getOwner(), game, hero
                                    .getRef(), true));
                } else {
                    hero.getQuickItems().add((DC_QuickItemObj) itemObj);
                }
                update(hero);
                return 1;
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

        for (DC_HeroItemObj item : hero.getInventory()) {
            if (item.getType() == type) {
                hero.setItem(item, slot);
                hero.removeFromInventory(item);
                result++;
                break;
            }
        }// item
        if (result > 0) {
            update(hero);
        }
        return result;
    }
}
