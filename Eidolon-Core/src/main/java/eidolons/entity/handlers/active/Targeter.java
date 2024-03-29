package eidolons.entity.handlers.active;

import eidolons.ability.ActivesConstructor;
import eidolons.ability.targeting.TemplateSelectiveTargeting;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import main.ability.Ability;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.Conditions;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.MultiTargeting;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.obj.IActiveObj;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/25/2017.
 */
public class Targeter extends ActiveHandler {
    protected Map<Coordinates,  Boolean> targetingAnyCache;
    protected Map<Coordinates, Map<Integer, Boolean>> targetingCache;

    private Obj presetTarget;
    protected boolean forcePresetTarget;
    private TARGETING_MODE targetingMode;
    private boolean targetingInitialized;

    public Targeter(ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    public void initTarget() {

        if (getPresetTarget() != null) { //TODO figure out when to getVar source's ref and when not!!!
            getRef().setTarget(getPresetTarget().getId());
            setPresetTarget(null);
        } else {
            if (getEntity().getTargeting() != null) {
                if (!isForcePresetTarget()) {
                    if (!selectTarget(getRef())) {
                        if (getEntity().getOwnerUnit().isAiControlled()) {
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
//            GuiEventManager.trigger(GuiEventType.ACTION_BEING_ACTIVATED, getAction());
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
        targetingInitialized = true;
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
        Coordinates c = getOwnerObj().getCoordinates();
        Boolean canTargetAny = targetingAnyCache.get(c);
        if (canTargetAny == null) {
            canTargetAny = !targeting.getFilter().getObjects(getRef()).isEmpty();
        }
        targetingAnyCache.put(c, canTargetAny);
        return canTargetAny;
    }

    public boolean canBeTargeted(Integer id) {
        return canBeTargeted(id, getEntity().isTargetingCached());
    }

    public boolean canBeTargeted(Integer id, boolean caching) {
        return canBeTargeted(id, caching, false);
    }

    public boolean canBeTargeted(Integer id, boolean caching, boolean recursion) {

        Targeting targeting = getTargeting();
        if (!(targeting instanceof SelectiveTargeting)) {
            return true;
        }
        Map<Integer, Boolean> map = getTargetingCache().get(
                getOwnerObj().getCoordinates());

        if (map == null) {
            map = new HashMap<>();
            getTargetingCache().put(getOwnerObj().getCoordinates(), map);
        }
        Boolean result = map.get(id); //TODO for ai?
        if (caching) {
            if (result != null)
                return result;
        }
        if (targeting == null) {
            // TODO ??


            if (getEntity().getActives().size() > 1) {
                return true;
            }

            targeting = TargetingMaster.findTargeting(getAction(), SelectiveTargeting.class);
            if (targeting == null) {
                if (!getEntity().getActives().isEmpty()) {
                    if (getEntity().getActives().get(0).getAbilities().getAbils().size() > 1) {
                        return true;
                    }
                }
                if (recursion) {
                    return false;
                }

                ActivesConstructor.constructActive(TARGETING_MODE.SINGLE, getEntity());
                return canBeTargeted(id, caching, true);
            }
        }
        Ref REF = getEntity().getRef().getCopy();
        REF.setMatch(id);
        if (targeting instanceof MultiTargeting) {
            // TODO ??
        }
        Conditions conditions = targeting.getFilter().getConditions();
        if (result != null) {
            if (result) {
                if (!conditions.preCheck(REF)) {
                    return false;
                }
            }
            if (!result) {
                if (conditions.preCheck(REF)) {
                    return true;
                }
            }
        }
        if (conditions.isEmpty())
            if (targeting instanceof TemplateSelectiveTargeting)
                ((TemplateSelectiveTargeting) targeting).initTargeting();

        getEntity().getRef().getSourceObj().getRef().setInfoEntity(getEntity());
        result = conditions.preCheck(REF);
        map.put(id, result);
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
            for (IActiveObj a : getEntity().getActives()) {
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

    public Map<Coordinates, Boolean> getTargetingAnyCache() {
        if (targetingAnyCache == null) {
            targetingAnyCache = new HashMap<>();
        }
        return targetingAnyCache;
    }

    public Map<Coordinates, Map<Integer, Boolean>> getTargetingCache() {
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

    public void setPresetTarget(Obj presetTarget) {
        this.presetTarget = presetTarget;
    }
}
