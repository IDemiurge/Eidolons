package main.entity.handlers;

import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Property;
import main.system.text.TextParser;

import java.util.*;

/**
 * Created by JustMe on 2/15/2017.
 */
public class EntityResetter<E extends Entity> extends EntityHandler<E> {

    public EntityResetter(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);
    }

    public void toBase() {
        getPropCache().clear();
        getEntity().getIntegerMap(false).clear(); // TODO [OPTIMIZED] no need to clear
        // type's map?
        if (getEntity().getModifierMaps() != null) {
            getEntity().getModifierMaps().clear(); // remember? For interesting spells or system.log
        }
        // info...
        if (!getType().checkProperty(G_PROPS.DISPLAYED_NAME)) {
            getEntity().setProperty(G_PROPS.DISPLAYED_NAME, getName(), true);
        }

        if (this.getEntity().getOwner() != getEntity().getOriginalOwner()) {
            LogMaster.log(LogMaster.CORE_DEBUG, getName()
             + ": original owner restored!");
        }

        getEntity().setOwner(getEntity().getOriginalOwner());
        //TODO optimize this...
//         Iterator<PARAMETER> i1 = getEntity().getParamMap().keySet().iterator();
//         Iterator<PARAMETER> i2 = getEntity().getType(). getParamMap().keySet().iterator();
//         Iterator<PARAMETER> iterator = new Iterator<PARAMETER>() {
//     @Override
//     public boolean hasNext() {
//         return false;
//     }
//     @Override
//     public PARAMETER next() {
//         PARAMETER next = i1.next();
//         checked.add(next);
//         if (checked.contains())
//         return null;
//     }
// };
        Set<PARAMETER> set = new LinkedHashSet<>(getEntity().getParamMap().keySet().size() +
                getEntity().getType().getParamMap().keySet().size());
        set.addAll(getEntity().getParamMap().keySet());
        set.addAll(getEntity().getType(). getParamMap().keySet());

        for (PARAMETER p : set) {
            if (p == null) {
                continue;
            }
            if (p.isDynamic()) {
                if (p.isWriteToType()) {
                    getType().setParam(p, getParam(p), true);
                }
                continue;
            }

            String baseValue = getType().getParam(p);
            String value = getParam(p);
            getEntity().getValueCache().put(p, value);
            if (!value.equals(baseValue)) {
                if (getEntity(). isValidMapStored(p)){
                    getEntity().getValidParams().put(p, NumberUtils.getIntParse(value));
                }
                String amount = getType().getParam(p);
                if (getEntity().isTypeLinked()) {
                    getType().getParamMap().put(p, value);
                }
                getEntity().getParamMap().put(p, amount);
                if (game.isStarted() && !game.isSimulation()) {
                    if (p.isDynamic()) {
                        getEntity().fireParamEvent(p, amount, CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED);
                    }
                }
            }
        }
        Set<PROPERTY> set2 = new LinkedHashSet<>(getEntity().getPropMap().keySet().size() +
                getEntity().getType().getPropMap().keySet().size());
        set2.addAll(getEntity().getPropMap().keySet());
        set2.addAll(getEntity().getType(). getPropMap().keySet());

        for (PROPERTY p : set2  ) {

            if (p.isDynamic()) {
                if (p.isWriteToType()) {
                    getType().setProperty(p, getProperty(p));
                }
                continue;
            }
            String baseValue = getType().getProperty(p);
            if (TextParser.isRef(baseValue)) {
                baseValue = new Property(baseValue).getStr(getRef());
                if ((baseValue) == null) {
                    baseValue = getType().getProperty(p);
                }
            }
            String value = getProperty(p);
            getEntity().getValueCache().put(p, value);
            if (!value.equals(baseValue)) {
                if (getEntity().isTypeLinked()) {
                    getType().getPropMap().put(p, value);
                }
                getEntity().getPropMap().put(p, baseValue);
            } else { //TODO ???
                if (getEntity().isTypeLinked()) {
                    getType().getPropMap().put(p, value);
                }
                getEntity().getPropMap().put(p, baseValue);
            }

        }
        resetStatus();
        getEntity().setDirty(false);
    }

    public void resetParam(PARAMETER param) {
        getEntity().resetDynamicParam(param);
    }

    public void resetPropertyFromList(PROPERTY prop, List<? extends Entity> list) {
        getEntity().resetPropertyFromList(prop, list);
    }

    protected void resetStatus() {
        entity.setProperty(G_PROPS.STATUS, "");
        if (entity.isDead()) {
            entity.addStatus(UnitEnums.STATUS.DEAD.toString());
        }
    }

    public void resetObjects() {
    }

    public void resetPercentages() {
    }

    public void reset() {
    }
}
