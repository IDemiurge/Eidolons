package eidolons.game.battlecraft.logic.battlefield;

import eidolons.content.PROPS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.entity.item.handlers.ItemMaster;
import eidolons.system.ObjUtilities;
import main.content.enums.entity.ItemEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class DroppedItemManager {

    private static final List<DC_HeroItemObj> VOID = new ArrayList<>();
    List<DC_HeroItemObj>[][] itemMap;
    private final DC_Game game;

    public DroppedItemManager(DC_Game game) {
        this.game = game;
    }

    public void init() {
        itemMap = new List
                [game.getDungeon().getCellsX()]
                [game.getDungeon().getCellsY()];

    }

    public boolean checkHasItemsBeneath(Unit unit) {
        Obj cell = game.getCell(unit.getCoordinates());
        return checkHasItems(cell);
    }

    public void dropDead(Unit unit) {
        if (unit.isSummoned())
            return;
        if (ItemMaster.isItemsDisabled()) {
            return;
        }
        if (!unit.isRevenant()) {
            unit.unequip(ItemEnums.ITEM_SLOT.ARMOR);
            unit.unequip(ItemEnums.ITEM_SLOT.MAIN_HAND);
            unit.unequip(ItemEnums.ITEM_SLOT.OFF_HAND);
            // drop natural weapons?
        }
        for (DC_HeroItemObj item : unit.getInventory()) {
            if (checkLootDrops(item, unit))
                drop(item, unit.getCoordinates());
            else {
                if (!unit.isMine())
                    destroyItem(item);
            }
        }
        if (!unit.isRevenant()) {
            for (DC_HeroItemObj item : unit.getQuickItems()) {
                drop(item, unit.getCoordinates());
            }
            for (DC_HeroItemObj item : unit.getJewelry()) {
                drop(item, unit.getCoordinates());
            }
        }
    }

    public static boolean canDropItem(DC_HeroItemObj item) {
        return !item.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase("Keys");
    }

    private boolean checkLootDrops(DC_HeroItemObj item, Unit unit) {
        if (unit.isBoss() || unit.isNamedUnit()) {
            return true;
        }
        if (item.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase("Keys")) {
            return true;
        }

        if (unit.isMine()) {
            //check if not original!
            if (item instanceof DC_HeroSlotItem)
                return item.getOriginalUnit() != unit;
            return true;
        }
        return
                RandomWizard.chance(66);
                        // unit.getGame().getMetaMaster().getLootMaster().
                        // getChanceForOwnedItemToDrop(unit, item));
    }

    private void destroyItem(DC_HeroItemObj item) {
        item.kill();
        game.getStateManager().removeObject(item.getId(), item.getOBJ_TYPE_ENUM());
    }

    public boolean checkHasItems(Obj obj) {
        // game.getBattleField().getGrid().getOrCreate
        return obj.checkProperty(PROPS.DROPPED_ITEMS);
    }

    public void itemFalls(Coordinates coordinates, DC_HeroItemObj item) {
        game.getCell(coordinates).addProperty(PROPS.DROPPED_ITEMS, "" + item.getId());
    }

    public void reset() {
        for (int i = 0; i < itemMap.length; i++) {
            for (int j = 0; j < itemMap[0].length; j++) {
                reset(i, j);
            }
        }
    }

    public void reset(int i, int j) {
        itemMap[i][j] =
                getItems(Coordinates.get(i, j));
    }

    public List<DC_HeroItemObj> getDroppedItems(Obj cell) {
        return getDroppedItems(cell.getCoordinates());
    }

    public List<DC_HeroItemObj> getDroppedItems(Coordinates coordinates) {
        return itemMap[coordinates.getX()][coordinates.getY()];
    }


    private List<DC_HeroItemObj> getItems(Coordinates coordinates) {
        return getItems(game.getCell(coordinates));
    }

    private List<DC_HeroItemObj> getItems(Obj cell) {
        if (cell == null) {
            return VOID;
        }
        List<DC_HeroItemObj> list = new ArrayList<>();
        for (String id : ContainerUtils.open(cell.getProperty(PROPS.DROPPED_ITEMS))) {
            Obj item = game.getObjectById(NumberUtils.getIntParse(id));
            if (item != null) {
                list.add((DC_HeroItemObj) item);
            }
        }
        return list;
    }


    public void remove(DC_HeroItemObj item, Unit heroObj) {
        game.getCell(heroObj.getCoordinates()).removeProperty(PROPS.DROPPED_ITEMS,
                "" + item.getId());
    }

    public void remove(Unit heroObj, Entity item) {
        game.getCell(heroObj.getCoordinates()).removeProperty(PROPS.DROPPED_ITEMS,
                "" + item.getId());
    }

    public void drop(DC_HeroItemObj item, Coordinates c) {
        game.getCell(c).addProperty(PROPS.DROPPED_ITEMS,
                "" + item.getId());
        item.setCoordinates(c);
        item.setContainer(VisualEnums.CONTAINER.UNASSIGNED);
    }

    public boolean pickUp(Obj cell, DC_HeroItemObj item) {
        cell.removeProperty(PROPS.DROPPED_ITEMS, "" + item.getId());
        return true;
    }

    public void pickedUp(Obj item) {
        if (!(item instanceof DC_HeroItemObj)) {
            return;
        }
        Coordinates coordinates = item.getCoordinates();
        Obj cell = game.getCell(coordinates);
        pickUp(cell, (DC_HeroItemObj) item);

    }

    public Collection<? extends Obj> getAllDroppedItems() {
        List<Obj> list = new ArrayList<>();
        for (Obj cell : game.getCells()) {
            list.addAll(getDroppedItems(cell));
        }
        return list;
    }

    public boolean pickUp(Unit hero, Entity type) {
        Obj cell = game.getCell(hero.getCoordinates());
        DC_HeroItemObj item = (DC_HeroItemObj) ObjUtilities.findObjByType(type,
                getDroppedItems(cell));
        if (item == null) {
            return false;
        }
        pickUp(cell, item);
        return true;
    }

    public DC_HeroItemObj findDroppedItem(String typeName, Coordinates coordinates) {
        Obj cell = game.getCell(coordinates);
        Obj item = new ListMaster<Obj>()
                .findType(typeName, new ArrayList<>(getDroppedItems(cell)));
        return (DC_HeroItemObj) item;
    }

}
