package main.entity.tools.active;

import main.ability.Ability;
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
import main.game.battlecraft.ai.tools.target.TargetingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

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
    private boolean targetingInitialized;

    public Targeter(DC_ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    public void initTarget() {

        if (presetTarget != null) { //TODO figure out when to get source's ref and when not!!!
             getRef().setTarget(presetTarget.getId());
            presetTarget=null ;
        } else {
            if (getEntity().getTargeting() != null) {
                if (!isForcePresetTarget()) {
                    if (!selectTarget(getRef())) {
                        if (getEntity().getOwnerObj().isAiControlled()) {
                            throw new RuntimeException();
                        }
                    }
                } else {
                    if (getRef().getTargetObj() == null) {
                        // what to do?
                    }
                }
            }

        }

    }
    public Ref getRef() {
        return getEntity().getRef();
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
//        TODO into SelectiveTargeting!
        if (getTargeting() instanceof SelectiveTargeting && getOwnerObj().isAiControlled()) {
            Integer id = TargetingMaster.selectTargetForAction(getEntity());
            if (id != null) {
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
        if (result) {
    getAction().setTargetObj(getRef().getTargetObj());
    getAction().setTargetGroup(getRef().getGroup());
}

        return result;

    }

    public Targeting getTargeting() {
        return getAction().getTargeting();
    }

    protected void initTargetingMode() {
       if (!getGame().isDebugMode())
           if (targetingInitialized) {
            return;
        }
        if (targetingMode == null) {
            targetingMode = new EnumMaster<TARGETING_MODE>().retrieveEnumConst(
                    TARGETING_MODE.class, getType().getProperty(G_PROPS.TARGETING_MODE));
        }

        if (targetingMode == null) {
            targetingMode = AbilityEnums.TARGETING_MODE.MULTI;
        }
        ActivesConstructor.constructActive(targetingMode, getEntity());
        targetingInitialized=true;
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
        return canBeTargeted(id, CoreEngine.isTargetingResultCachingOn());
    }
        public boolean canBeTargeted(Integer id, boolean caching) {

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
        Boolean result = map2.get(id); //TODO for ai?
        if (caching) {
         if (result != null)
            return result;
        }
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
        Ref REF =getEntity(). getRef().getCopy();
        REF.setMatch(id);
        if (targeting instanceof MultiTargeting) {
            // TODO ??
        }
        if (result != null) {
            if (result) {
                if (!targeting.getFilter().getConditions().preCheck(REF)) {
                    return false;
                }
            }
            if (!result) {
                if (targeting.getFilter().getConditions().preCheck(REF)) {
                    return true;
                }
            }
        }
        getEntity(). getRef().getSourceObj().getRef() .setInfoEntity(getEntity());
        result = targeting.getFilter().getConditions().preCheck(REF);
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
        if (getEntity().getAbilities() != null) {
            for (Ability a : getEntity().getAbilities()) {
                a.setForcePresetTargeting(b);
            }
        }
    }

    public void setTargetingInitialized(boolean targetingInitialized) {
        this.targetingInitialized = targetingInitialized;
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
