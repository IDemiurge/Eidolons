package main.entity.tools.active;

import main.ability.ActivesConstructor;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.values.properties.G_PROPS;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.MultiTargeting;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.game.ai.elements.actions.ActionManager;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/25/2017.
 */
public class Targeter extends ActiveHandler {
    protected Map<Coordinates, Map<FACING_DIRECTION, Boolean>> targetingAnyCache;
    protected Map<Coordinates, Map<FACING_DIRECTION, Map<Integer, Boolean>>> targetingCache;

    protected Obj presetTarget;
    protected boolean forcePresetTarget;
    private TARGETING_MODE targetingMode;

    public Targeter(DC_ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    public void initTarget() {


        if (presetTarget != null) {
            getRef().setTarget(presetTarget.getId());
        } else {
            if (getEntity().getTargeting() != null) {
                if (!isForcePresetTarget()) {
                    if (!selectTarget(getRef())) {
                        getHandler().result = false;
                        getHandler().interrupted = true;
                    }
                } else {
                    if (getRef().getTargetObj()==null ){
                        // what to do?
                    }
                }
            }

        }
    }

    public boolean selectTarget(Ref ref) {
        if (isForcePresetTarget()) {
            return true;
        }
        if (getTargeting() == null) {
            getInitializer().construct();
            if (getTargeting() == null) {
                LogMaster.log(1, "null targeting invoked on " + getName());
                return false;
            }
        }
        boolean result = false;
        if (getTargeting() instanceof SelectiveTargeting && getOwnerObj().isAiControlled()) {
            Integer id = ActionManager.selectTargetForAction(getEntity());
            if (id != null) {
                getRef().setTarget(id);
                result = true;
            }
        } else {
            result = getTargeting().select(ref);
        }
        if (result) {
            getHandler().setCancelled(null);
        } else {
            getHandler().setCancelled(true);
        }

        return result;

    }

    protected Targeting getTargeting() {
        return getAction().getTargeting();
    }

    protected void initTargetingMode() {
        if (targetingMode == null) {
            targetingMode = new EnumMaster<TARGETING_MODE>().retrieveEnumConst(
             TARGETING_MODE.class, getType().getProperty(G_PROPS.TARGETING_MODE));
        }

        if (targetingMode == null) {
            targetingMode = AbilityEnums.TARGETING_MODE.MULTI;
        }
        ActivesConstructor.constructActive(targetingMode, getEntity());
//        if (targeting == null) {
//            LogMaster.log(LOG_CHANNELS.CONSTRUCTION_DEBUG,
//             "null targeting for " + getName() + targetingMode + abilities);
        // ActivesConstructor.constructActive(mode, this);
//        }
    }

    public void resetTargetingCache() {
        getTargetingAnyCache().clear();
        getTargetingCache().clear();
        setForcePresetTarget(false);
    }

    public boolean canTargetAny() {
        Targeting targeting = getTargeting();
        if (!(targeting instanceof SelectiveTargeting)) {
            return true;
        }
        Map<FACING_DIRECTION, Boolean> map = getTargetingAnyCache().get(
         getOwnerObj().getCoordinates());
        if (map == null) {
            map = new HashMap<>();
            targetingAnyCache.put(getOwnerObj().getCoordinates(), map);
        }

        Boolean canTargetAny = map.get(getOwnerObj().getFacing());
        if (canTargetAny == null) {
            canTargetAny = !targeting.getFilter().getObjects(getRef()).isEmpty();
        }
        map.put(getOwnerObj().getFacing(), canTargetAny);
        return canTargetAny;
    }

    public boolean canBeTargeted(Integer id) {

        Targeting targeting = getTargeting();
        Map<FACING_DIRECTION, Map<Integer, Boolean>> map = getTargetingCache().get(
         getOwnerObj().getCoordinates());
        if (map == null) {
            map = new HashMap<>();
            getTargetingCache().put(getOwnerObj().getCoordinates(), map);
        }
        Map<Integer, Boolean> map2 = map.get(getOwnerObj().getFacing());
        if (map2 == null) {
            map2 = new HashMap<>();
            map.put(getOwnerObj().getFacing(), map2);
        }
        Boolean result = map2.get(id);
        // if (result != null)
        // return result;

        if (targeting == null) {
            // TODO ??
            if (getEntity().getActives().size() > 1) {
                return true;
            }
            if (!getEntity().getActives().isEmpty()) {
                if (getEntity().getActives().get(0).getAbilities().getAbils().size() > 1) {
                    return true;
                }
            }
            return false;
        }
        Ref REF = getRef().getCopy();
        REF.setMatch(id);
        if (targeting instanceof MultiTargeting) {
            // TODO ??
        }
        if (result != null) {
            if (result) {
                if (!targeting.getFilter().getConditions().check(REF)) {
                    return false;
                }
            }
            if (!result) {
                if (targeting.getFilter().getConditions().check(REF)) {
                    return true;
                }
            }
        }
        result = targeting.getFilter().getConditions().check(REF);
        map2.put(id, result);
        return result;

    }

    public Obj getPresetTarget() {
        return presetTarget;
    }

    public boolean isForcePresetTarget() {
        return forcePresetTarget;
    }

    public void setForcePresetTarget(boolean b) {
        forcePresetTarget = b;
        if (getEntity().getActives() != null) {
            for (ActiveObj a : getEntity().getActives()) {
                a.setForcePresetTarget(b);
            }
        }
    }

    public TARGETING_MODE getTargetingMode() {
        return targetingMode;
    }

    public Map<Coordinates, Map<FACING_DIRECTION, Boolean>> getTargetingAnyCache() {
        if (targetingAnyCache == null) {
            targetingAnyCache = new HashMap<>();
        }
        return targetingAnyCache;
    }

    public Map<Coordinates, Map<FACING_DIRECTION, Map<Integer, Boolean>>> getTargetingCache() {
        if (targetingCache == null) {
            targetingCache = new HashMap<>();
        }
        return targetingCache;
    }

    public void initAutoTargeting() {
        if (getTargeting() instanceof AutoTargeting) {
            selectTarget(getRef());
        }
    }
}
