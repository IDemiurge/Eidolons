package eidolons.game.exploration.dungeon.objects;

import eidolons.content.PROPS;
import eidolons.entity.item.HeroItem;
import main.entity.type.ObjType;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;

import static eidolons.content.consts.VisualEnums.CONTAINER.*;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerObj extends DungeonObj {
    private List<HeroItem> items;
    private boolean itemsInitialized;

    public ContainerObj(ObjType type, int x, int y) {
        super(type, x, y);

    }

    private void initInventory() {
        if (ContainerMaster.isGenerateItemsForContainers())
            getDM().initContents(this);
        items = new ArrayList<>(
         getInitializer().initContainedItems(PROPS.INVENTORY,
          new DequeImpl<>(), false));
        items.forEach(itemObj -> itemObj.setContainer(CONTAINER));
        itemsInitialized = true;


    }

    @Override
    public ContainerMaster getDM() {
        return (ContainerMaster) super.getDM();
    }

    @Override
    public void resetObjects() {
        if (items == null) {
            if (getDM().isPregenerateItems(this)) {
                initInventory();
            }
        }
        super.resetObjects();
    }

    @Override
    public boolean isItemsInitialized() {
        return itemsInitialized;
    }

    public List<HeroItem> getItems() {
        if (!itemsInitialized) {
            initInventory();
        }
        items.forEach(itemObj -> {
            if (itemObj != null) {
                itemObj.setContainer(CONTAINER.CONTAINER);
                itemObj.getRef().setSource(getId());
            }
        });
        return items;
    }


    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.CONTAINER;
    }
}
