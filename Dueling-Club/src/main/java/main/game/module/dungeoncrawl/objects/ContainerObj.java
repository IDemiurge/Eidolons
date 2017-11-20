package main.game.module.dungeoncrawl.objects;

import main.content.PROPS;
import main.entity.item.DC_HeroItemObj;
import main.entity.type.ObjType;
import main.system.datatypes.DequeImpl;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerObj extends DungeonObj {
    private DequeImpl<DC_HeroItemObj> items;
    private boolean itemsInitialized;

    public ContainerObj(ObjType type, int x, int y) {
        super(type, x, y);

    }

    private void initInventory() {
        if ( ContainerMaster.isGenerateItemsForContainers())
        getDM().initContents(this);
        items= getInitializer().initContainedItems(PROPS.INVENTORY, new DequeImpl<>(), false);
        itemsInitialized = true;


    }

    @Override
    public ContainerMaster getDM() {
        return (ContainerMaster) super.getDM();
    }
    @Override
    public void resetObjects() {
        if (items == null) {
            if (getDM().isPregenerateItems()) {
            initInventory();
            }
        }
        super.resetObjects();
    }

    @Override
    public boolean isItemsInitialized() {
        return itemsInitialized;
    }

    public DequeImpl<DC_HeroItemObj> getItems() {
        if (!itemsInitialized){
            initInventory();
            }
        return items;
    }


    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.CONTAINER;
    }
}
