package main.game.battlefield;

import main.content.PROPS;
import main.content.enums.entity.ItemEnums;
import main.entity.Entity;
import main.entity.item.DC_HeroItemObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.ObjUtilities;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.StringMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DroppedItemManager {

    private DC_Game game;

    public DroppedItemManager(DC_Game game) {
        this.game = game;
    }

    public boolean checkHasItemsBeneath(Unit unit) {
        Obj cell = game.getCellByCoordinate(unit.getCoordinates());
        return checkHasItems(cell);
    }

    public void dropDead(Unit unit) {
        unit.unequip(ItemEnums.ITEM_SLOT.ARMOR);
        unit.unequip(ItemEnums.ITEM_SLOT.MAIN_HAND);
        unit.unequip(ItemEnums.ITEM_SLOT.OFF_HAND);
        // drop natural weapons?

        for (DC_HeroItemObj item : unit.getInventory()) {
            drop(item, unit);
        }
        for (DC_HeroItemObj item : unit.getQuickItems()) {
            drop(item, unit);
        }
        for (DC_HeroItemObj item : unit.getJewelry()) {
            drop(item, unit);
        }

    }

    public boolean checkHasItems(Obj obj) {
        // game.getBattleField().getGrid().getOrCreate
        return obj.checkProperty(PROPS.DROPPED_ITEMS);
    }

    public void itemFalls(Coordinates coordinates, DC_HeroItemObj item) {
        game.getCellByCoordinate(coordinates).addProperty(PROPS.DROPPED_ITEMS, "" + item.getId());

    }

    public void remove(DC_HeroItemObj item, Unit heroObj) {
        game.getCellByCoordinate(heroObj.getCoordinates()).removeProperty(PROPS.DROPPED_ITEMS,
                "" + item.getId());
    }

    public void remove(Unit heroObj, Entity item) {
        game.getCellByCoordinate(heroObj.getCoordinates()).removeProperty(PROPS.DROPPED_ITEMS,
                "" + item.getId());
    }

    public void drop(DC_HeroItemObj item, Unit heroObj) {
        game.getCellByCoordinate(heroObj.getCoordinates()).addProperty(PROPS.DROPPED_ITEMS,
                "" + item.getId());
    }

    public boolean pickUp(Obj cell, DC_HeroItemObj item) {
        cell.removeProperty(PROPS.DROPPED_ITEMS, "" + item.getId());
        return true;
    }

    public boolean pickUp(Unit hero, Entity type) {
        Obj cell = game.getCellByCoordinate(hero.getCoordinates());
        DC_HeroItemObj item = (DC_HeroItemObj) ObjUtilities.findObjByType(type,
                getDroppedItems(cell));
        if (item == null) {
            return false;
        }
        pickUp(cell, item);
        return true;
    }

    public DC_HeroItemObj findDroppedItem(String typeName, Coordinates coordinates) {
        Obj cell = game.getCellByCoordinate(coordinates);
        Obj item = new ListMaster<Obj>()
                .findType(typeName, new LinkedList<>(getDroppedItems(cell)));
        return (DC_HeroItemObj) item;
    }

    public Collection<? extends Obj> getDroppedItems(Obj cell) {
        Collection<Obj> list = new LinkedList<>();
        for (String id : StringMaster.openContainer(cell.getProperty(PROPS.DROPPED_ITEMS))) {
            Obj item = game.getObjectById(StringMaster.getInteger(id));
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public void pickedUp(Obj item) {
        if (!(item instanceof DC_HeroItemObj)) {
            return;
        }
        Coordinates coordinates = item.getCoordinates();
        Obj cell = game.getCellByCoordinate(coordinates);
        pickUp(cell, (DC_HeroItemObj) item);

    }

    public Collection<? extends Obj> getAllDroppedItems() {
        List<Obj> list = new LinkedList<>();
        for (Obj cell : game.getCells()) {
            list.addAll(getDroppedItems(cell));
        }
        return list;
    }

}
