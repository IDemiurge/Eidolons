package eidolons.entity.handlers.bf;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.SimulationGame;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.macro.global.persist.Loader;
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

    public DequeImpl<? extends DC_HeroItemObj> initContainedItems(PROPS prop,
                                                                  DequeImpl<? extends DC_HeroItemObj> list, boolean quick) {
        if (getEntity().isLoaded()) {
            return getLoadedItemContainer(prop);
        }
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
            Collection<DC_HeroItemObj> items = new ArrayList<>();
            for (String subString : ContainerUtils.open(getProperty(prop))) {
                DC_HeroItemObj item;
                if (NumberUtils.isInteger(subString)) {
                    Integer id = NumberUtils
                            .getIntParse(subString);
                    if (!game.isSimulation()) {
                        item = (DC_HeroItemObj) game.getObjectById(id);
                    } else {
                        item = (DC_HeroItemObj) ((SimulationGame) game).getRealGame().getObjectById(id);
                        if (item == null) {
                            try {
                                item = (DC_HeroItemObj) HqMaster.getSimCache().getById(subString);
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                                continue;
                            }
                        } else if (HqMaster.getSimCache().getSim(item) == null) {
                            HqMaster.getSimCache().addSim(item,
                             ItemFactory.createItemObj(item.getType(), getEntity().getOriginalOwner(), getGame(), getRef(),
                                    quick));
                        }
                        Integer durability = null;
                        if (item instanceof DC_QuickItemObj) {
                            if (((DC_QuickItemObj) item).getWrappedWeapon() != null) {
                                durability = ((DC_QuickItemObj) item).getWrappedWeapon()
                                        .getIntParam(PARAMS.C_DURABILITY);
                            }
                        } else {
                            durability = item.getIntParam(PARAMS.C_DURABILITY);
                        }
                        item = (DC_HeroItemObj) HqMaster.getSimCache().getSim(item);
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

    private DequeImpl<? extends DC_HeroItemObj> getLoadedItemContainer(PROPS prop) {
        return Loader.getLoadedItemContainer(getEntity(), prop);
    }
}
