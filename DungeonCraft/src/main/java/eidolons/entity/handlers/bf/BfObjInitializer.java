package eidolons.entity.handlers.bf;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.SimCache;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.SimulationGame;
import main.entity.handlers.EntityInitializer;
import main.entity.handlers.EntityMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
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
        addDefaultFacing();

    }

    public DequeImpl<? extends HeroItem> initContainedItems(PROPS prop,
                                                            DequeImpl<? extends HeroItem> list, boolean quick) {
        // if (getEntity().isLoaded()) {
        //     return Loader.getLoadedItemContainer(getEntity(), prop);;
        // }
        if (StringMaster.isEmpty(getProperty(prop))) {
            if (list == null) {
                return new DequeImpl<>();
            }
            if (list.isEmpty()) {
                return new DequeImpl<>();
            }
        }
        if (list == null || (getEntity().isItemsInitialized())) {
            setProperty(prop, ContainerUtils.toIdContainer(list));
        } else {

            List<String> idList = new ArrayList<>();
            Collection<HeroItem> items = new ArrayList<>();
            for (String subString : ContainerUtils.open(getProperty(prop))) {
                HeroItem item;
                if (NumberUtils.isInteger(subString)) {
                    Integer id = NumberUtils
                            .getIntParse(subString);
                    if (!game.isSimulation()) {
                        item = (HeroItem) game.getObjectById(id);
                    } else {
                        item = (HeroItem) ((SimulationGame) game).getRealGame().getObjectById(id);
                        if (item == null) {
                            try {
                                item = (HeroItem)  SimCache.getInstance().getById(subString);
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                                continue;
                            }
                        } else if ( SimCache.getInstance().getSim(item) == null) {
                             SimCache.getInstance().addSim(item,
                             ItemFactory.createItemObj(item.getType(), getEntity().getOriginalOwner(), getGame(), getRef(),
                                    quick));
                        }
                        Integer durability = null;
                        if (item instanceof QuickItem) {
                            if (((QuickItem) item).getWrappedWeapon() != null) {
                                durability = ((QuickItem) item).getWrappedWeapon()
                                        .getIntParam(PARAMS.C_DURABILITY);
                            }
                        } else {
                            durability = item.getIntParam(PARAMS.C_DURABILITY);
                        }
                        item = (HeroItem)  SimCache.getInstance().getSim(item);
                        if (durability != null)
                            item.setParam(PARAMS.C_DURABILITY, durability);
                    }
                } else {

                    item = ItemFactory.createItemObj(subString,
                     DC_ContentValsManager.getTypeForProp(prop),
                     getEntity().getOriginalOwner(), getGame(), getRef(),
                            quick);
                }

                if (item != null) {
                    idList.add(item.getId() + "");
                    items.add(item);
                } else {
                    LogMaster.log(0, getName()
                            + " has null item in item container " + prop);
                }

            }
            list = new DequeImpl<>(items);
            setProperty(prop, ContainerUtils.constructContainer(idList));
        }
        if (list == null) {
            return new DequeImpl<>();
        }
        return list;

    }

}
