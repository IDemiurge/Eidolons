package main.entity.tools;

import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Property;
import main.system.text.TextParser;

import java.util.HashSet;

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
        if (getEntity(). getModifierMaps() != null) {
            getEntity(). getModifierMaps().clear(); // remember? For interesting spells or log
        }
        // info...
        if (!getType(). checkProperty(G_PROPS.DISPLAYED_NAME)){
            getEntity(). setProperty(G_PROPS.DISPLAYED_NAME, getName(), true);
        }

        if (this.getEntity().getOwner() != getEntity().getOriginalOwner()) {
            LogMaster.log(LogMaster.CORE_DEBUG, getName()
             + ": original owner restored!");
        }

       getEntity().setOwner(getEntity().getOriginalOwner());

        HashSet<PARAMETER> params = new HashSet<>(getEntity().getParamMap().keySet());
        params.addAll(getType().getParamMap().keySet());
        for (PARAMETER p : params) {
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
            if (!value.equals(baseValue)) {
                String amount = getType().getParam(p);
                if (getEntity().isTypeLinked()) {
                    getType().getParamMap().put(p, value);
                }
                getEntity().getParamMap().  put(p, amount);
                if (game.isStarted() && !game.isSimulation()) {
                    if (p.isDynamic()) {
                        getEntity(). fireParamEvent(p, amount, CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED);
                    }
                }
            }
        }
        HashSet<PROPERTY> props = new HashSet<>(getEntity(). getPropMap().keySet());
        props.addAll(getType().getPropMap().keySet());
        for (PROPERTY p : props) {

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


    protected void resetStatus() {
        entity.setProperty(G_PROPS.STATUS, "");
        if (entity.isDead()) {
            entity.addStatus(UnitEnums.STATUS.DEAD.toString());
        }
    }
}
