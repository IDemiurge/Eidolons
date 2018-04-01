package eidolons.entity.handlers.bf;

import eidolons.content.DC_ContentManager;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.core.game.DC_Game;
import main.content.enums.rules.VisionEnums;
import main.data.DataManager;
import main.entity.handlers.EntityInitializer;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 3/25/2017.
 */
public abstract class BfObjInitializer<T extends BattleFieldObject> extends
 EntityInitializer<T> {
    public boolean initialized;
    public boolean dynamicValuesReady;

    public BfObjInitializer(T entity, EntityMaster<T> entityMaster) {
        super(entity, entityMaster);
    }

    public void addDynamicValues() {
        getEntity().addDynamicValues();

    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    @Override
    public void init() {
        super.init();

        addDefaultValues();
        if (getEntity() instanceof Structure)
            addDynamicValues();

    }

    protected void addDefaultFacing() {
        if (getEntity().getOwner() != null)
            getEntity().setFacing(
             DC_MovementManager.getDefaultFacingDirection(getEntity().getOwner().isMe()));
    }

    public void addDefaultValues() {
        getEntity().setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN);
        addDefaultFacing();

    }

    public DequeImpl<? extends DC_HeroItemObj> initContainedItems(PROPS prop,
                                                                  DequeImpl<? extends DC_HeroItemObj> list, boolean quick) {
        if (StringMaster.isEmpty(getProperty(prop))) {
            if (list == null) {
                return new DequeImpl<>();
            }
            if (list.isEmpty() || game.isSimulation()) {
                return new DequeImpl<>();
            }
        }
        if (list == null || (!game.isSimulation() && getEntity().isItemsInitialized())) {
            setProperty(prop, StringMaster.constructContainer(StringMaster.convertToIdList(list)));

        } else {
            List<String> idList = new ArrayList<>();
            Collection<DC_HeroItemObj> items = new ArrayList<>();
            for (String subString : StringMaster.open(getProperty(prop))) {
                ObjType type = DataManager.getType(subString, DC_ContentManager.getTypeForProperty(prop));
//|| !StringMaster.isInteger(subString)
                DC_HeroItemObj item = null;
                if (game.isSimulation()) {
                    item = (DC_HeroItemObj) getGame().getSimulationObj(getEntity(), type, prop);
                }
                if (item == null) {
                    if (type == null) {
                        item = (DC_HeroItemObj) game.getObjectById(StringMaster
                         .getInteger(subString));
                    } else {
                        item = ItemFactory.createItemObj(type, getEntity().getOriginalOwner(), getGame(), getRef(),
                         quick);
                    }
                    if (item != null) {
                        if (!game.isSimulation()) {
                            idList.add(item.getId() + "");
                        } else {
                            getGame().addSimulationObj(getEntity(), type, item, prop);
                        }
                    }
                }
                if (item == null) {
                    LogMaster.log(1, getName()
                     + " has null items in item container " + prop);
                } else {
                    items.add(item);
                }

            }
            list = new DequeImpl<>(items);
            if (!game.isSimulation())

            {
                setProperty(prop, StringMaster.constructContainer(idList));
            }
        }
        if (list == null) {
            return new DequeImpl<>();
        }
        return list;

    }
}
