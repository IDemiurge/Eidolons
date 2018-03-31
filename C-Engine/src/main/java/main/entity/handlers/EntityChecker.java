package main.entity.handlers;

import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.VALUE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/15/2017.
 */
public class EntityChecker<E extends Entity> extends EntityHandler<E> {

    public EntityChecker(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);


    }

    public boolean checkValue(VALUE v) {
        return getEntity().checkValue(v);
    }

    public boolean checkValue(VALUE v, String value) {
        return getEntity().checkValue(v, value);
    }

    public boolean checkParam(PARAMETER param) {
        return getEntity().checkParam(param);
    }

    public boolean checkParameter(PARAMETER param, int value) {
        return getEntity().checkParameter(param, value);
    }

    public boolean checkParam(PARAMETER param, String value) {
        return getEntity().checkParam(param, value);
    }

    public boolean checkProperty(PROPERTY p, String value) {
        return getEntity().checkProperty(p, value);
    }

    public boolean checkProperty(PROPERTY p, String value, boolean base) {
        return getEntity().checkProperty(p, value, base);
    }

    public boolean checkSingleProp(String PROP, String value) {
        return getEntity().checkSingleProp(PROP, value);
    }

    public boolean checkSingleProp(PROPERTY PROP, String value) {
        return getEntity().checkSingleProp(PROP, value);
    }

    public boolean checkContainerProp(PROPERTY PROP, String value) {
        return getEntity().checkContainerProp(PROP, value);
    }

    public boolean checkContainerProp(PROPERTY PROP, String value, boolean any) {
        return getEntity().checkContainerProp(PROP, value, any);
    }

    public boolean checkSubGroup(String string) {
        return getEntity().checkSubGroup(string);
    }

    public boolean checkProperty(PROPERTY p) {
        return getEntity().checkProperty(p);
    }

    public boolean checkGroup(String string) {
        return getEntity().checkGroup(string);
    }

    public boolean checkBool(DYNAMIC_BOOLS bool) {
        return getEntity().checkBool(bool);
    }

    public boolean checkCustomProp(String name) {
        return getEntity().checkCustomProp(name);
    }

    public boolean checkBool(STD_BOOLS bool) {
        String value = getProperty(G_PROPS.STD_BOOLS);
        if (StringMaster.isEmpty(value)) {
            return false;
        }
        return StringMaster.compareContainers(value, bool.toString(), false);
    }

    public boolean isNeutral() {
        return isOwnedBy(Player.NEUTRAL);
    }

    public boolean isOwnedBy(Player player) {
        if (getOwner() == null) {
            return player == null;
        }
        return getOwner().equals(player);
    }

    public Player getOwner() {
        return getEntity().getOwner();
    }
}
