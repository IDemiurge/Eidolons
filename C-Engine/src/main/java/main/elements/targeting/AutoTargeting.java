package main.elements.targeting;

import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.enums.entity.AbilityEnums;
import main.data.ability.OmittedConstructor;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

import java.util.LinkedList;
import java.util.Set;

public class AutoTargeting extends TargetingImpl {
    protected Formula numberOfTargets;
    // GroupIDs multipleTargets;
    protected boolean multiple;
    protected Integer this_target;
    protected String maxNumberOfTargets;
    protected String minNumberOfTargets;
    protected boolean unlimitedTargets = false;
    private Boolean closest;
    private Obj lastTarget;
    private String TYPES;
    private KEYS keyword;
    private GroupImpl group;
    private OBJ_TYPE TYPE;

    public AutoTargeting(KEYS keyword) {
        this.keyword = keyword;
    }

    public AutoTargeting(Condition condition) {
        this(condition, null, true);
    }

    public AutoTargeting(Condition condition, Formula numberOfTargets,
                         Boolean closestOrRandom) {
        this.closest = closestOrRandom;
        this.setConditions(new Conditions(condition));
        if (numberOfTargets == null) {
            unlimitedTargets = true;
        } else {
            this.numberOfTargets = numberOfTargets;
        }
    }

    public AutoTargeting(Conditions conditions, OBJ_TYPE type) {
        this(conditions);
        this.TYPE = type;
    }

    public AutoTargeting(Conditions conditions, String TYPES) {
        this(conditions);
        this.TYPES = TYPES;

    }

    @OmittedConstructor
    public AutoTargeting(GroupImpl group) {
        this.group = group;
    }

    @Override
    public Filter<Obj> getFilter() {
        return super.getFilter();
    }

    @Override
    public boolean select(Ref ref) {

        if (group != null) {
            if (getConditions() != null) {
                Set<Obj> filteredGroup = new Filter<Obj>(group.getObjects(),
                        ref, getConditions()).getObjects();
                group = new GroupImpl(filteredGroup);
            }
            ref.setGroup(this.group);
            return true;
        }

        if (keyword != null) {
            Obj obj = ref.getObj(keyword);
            if (obj == null) {
                return false;
            }
            ref.setTarget(obj.getId());
            return true;
        }
        filter = new Filter<>(ref, getConditions());
        filter.setTYPE(TYPE);
        if (TYPES != null) {
            LinkedList<OBJ_TYPE> types = new LinkedList<>();
            for (String s : StringMaster.openContainer(TYPES)) {
                types.add(ContentManager.getOBJ_TYPE(s));
            }
            filter.setTYPES(types);
        }
        setRef(ref);

        if (unlimitedTargets) {
            ref.setGroup(filter.getGroup());
        } else {
            setLastTarget(ref.getTargetObj());
            int nOfTargets = numberOfTargets.getInt(ref);
            if (nOfTargets <= 0) {
                return false;
            }
            Set<Obj> objects = filter.getObjects();
            if (nOfTargets == 1) {
                Obj obj = selectObj(objects);
                ref.setTarget(obj.getId());
            } else {
                GroupImpl group = filterGroup(filter.getGroup(), nOfTargets);
                ref.setGroup(group);
            }
        }

        return true;

    }

    private GroupImpl filterGroup(GroupImpl group, int nOfTargets) {
        // TODO Auto-generated method stub
        return group;
    }

    private Obj selectObj(Set<Obj> objects) {

        return lastTarget.getGame().getAnalyzer()
                .getClosestUnit(lastTarget, friendlyFire);
    }

    public Obj getLastTarget() {
        return lastTarget;
    }

    public void setLastTarget(Obj lastTarget) {
        this.lastTarget = lastTarget;
    }

    public enum AUTO_TARGETING_TEMPLATES {
        SELF,
        ALL,
        ALL_ENEMIES,
        ADJACENT,
        ALL_ALLIES,
        ENEMY_HERO,
        ALL_UNITS,
        WAVE,
        ACTIONS,
        SPELLS,
        PARTY,;

        public TARGETING_MODE getMode() {
            switch (this) {
                case ADJACENT:
                    return AbilityEnums.TARGETING_MODE.NOVA;
                case ALL:
                    break;
                case ALL_ALLIES:
                    break;
                case ALL_ENEMIES:
                    break;
                case ALL_UNITS:
                    break;
                case SELF:
                    return AbilityEnums.TARGETING_MODE.SELF;
                case WAVE:
                    return AbilityEnums.TARGETING_MODE.SPRAY;
                default:
                    break;

            }
            return null;
        }
    }
}
