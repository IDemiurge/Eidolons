package main.game.module.dungeoncrawl.objects;

import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.DungeonObjEnums.CONTAINER_CONTENTS;
import main.entity.item.DC_HeroItemObj;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;

import java.util.Map;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerObj extends DungeonObj {
    private DequeImpl<DC_HeroItemObj> items;
    private boolean itemsInitialized;
    private boolean test_mode=true;

    public ContainerObj(ObjType type, int x, int y) {
        super(type, x, y);

    }

    private void initInvProp() {
        //random generation?
        //what props will act here?
        // power, and types...
        // potion
        // weightmap!
        RandomWizard<CONTAINER_CONTENTS> wizard = new RandomWizard< >();
        String prop = getProperty(PROPS.CONTAINER_CONTENTS);
        if (test_mode)
            prop = "Potions(10);";
        Map<CONTAINER_CONTENTS, Integer> map =
        wizard.
          constructWeightMap(prop, CONTAINER_CONTENTS.class);
        Integer maxPower = getIntParam(PARAMS.POWER);
        if (test_mode)
            maxPower = 150;
        Integer power=0;
        while(power<maxPower){
            CONTAINER_CONTENTS c = wizard.getObjectByWeight(map);
           ObjType item=    getDM().getItem(c);
            addProperty(PROPS.INVENTORY, item.getName(), false);
            power += item.getIntParam(PARAMS.GOLD_COST);
        }

    }

    @Override
    public ContainerMaster getDM() {
        return (ContainerMaster) super.getDM();
    }
    @Override
    public void resetObjects() {
        if (items == null) {
            initInvProp();
           items= getInitializer().initContainedItems(PROPS.INVENTORY, new DequeImpl<>(), false);
            itemsInitialized = true;
        }
        super.resetObjects();
    }

    @Override
    public boolean isItemsInitialized() {
        return itemsInitialized;
    }

    public DequeImpl<DC_HeroItemObj> getItems() {
        return items;
    }


    @Override
    public DUNGEON_OBJ_TYPE getDungeonObjType() {
        return DUNGEON_OBJ_TYPE.CONTAINER;
    }
}
