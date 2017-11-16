package main.game.module.dungeoncrawl.objects;

import main.client.cc.CharacterCreator;
import main.content.DC_TYPE;
import main.content.enums.entity.DungeonObjEnums.CONTAINER_CONTENTS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.module.dungeoncrawl.objects.ContainerMaster.CONTAINER_ACTION;
import main.libgdx.gui.panels.dc.inventory.container.ContainerDataSource;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerMaster extends DungeonObjMaster<CONTAINER_ACTION> {

    public ObjType getItem(CONTAINER_CONTENTS c) {
        switch (c) {
            case AMMO:
                break;
            case POTIONS:
                return DataManager.getType("Minor Healing Potion", DC_TYPE.ITEMS);
            case WEAPONS:
                break;
            case FOOD:
                break;
            case MISC:
                break;
        }

        return null;
    }

    public enum CONTAINER_ACTION implements DUNGEON_OBJ_ACTION {
        OPEN,
    }

    public ContainerMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    @Override
    protected boolean actionActivated(CONTAINER_ACTION sub, Unit unit, DungeonObj obj) {
        /*
        pickUpAction
         */
        unit.   getGame().getInventoryManager().setHero(unit);
        unit. getGame().getInventoryManager().setOperationsPool(5);
        CharacterCreator.getHeroManager().addHero(unit);

        Pair<InventoryDataSource, ContainerDataSource> param =
         new ImmutablePair<>(new InventoryDataSource(unit), new ContainerDataSource(obj));
        GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL, param);
        return true;
//        return (boolean) WaitMaster.waitForInput(InventoryTransactionManager.OPERATION);
    }

    @Override
    public List<DC_ActiveObj> getActions(DungeonObj obj, Unit unit) {
         List<DC_ActiveObj> list = new LinkedList<>();
         list.add(
         createAction(CONTAINER_ACTION.OPEN, unit, obj));
        return list;
    }

    @Override
    public void open(DungeonObj obj, Ref ref) {

    }

    @Override
    public DC_ActiveObj getDefaultAction(Unit source, DungeonObj target) {
        return createAction(CONTAINER_ACTION.OPEN, source, target);
    }
}
