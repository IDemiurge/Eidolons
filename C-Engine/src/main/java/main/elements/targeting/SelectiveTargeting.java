package main.elements.targeting;

import main.ability.PassiveAbilityObj;
import main.content.CONTENT_CONSTS.TARGETING_MODE;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

import java.util.Set;

public class SelectiveTargeting extends TargetingImpl {
    protected Set<Integer> set;
    protected SELECTIVE_TARGETING_TEMPLATES template;
    private OBJ_TYPE filteredType;

	/*
     * 1 constructor only? mapping system... or maybe not! AE_Item per
	 * constructor, that's all
	 */

    public SelectiveTargeting(Condition conditions) {
        this(conditions, new Formula("1"));
    }

    public SelectiveTargeting(Condition conditions, Formula formula) {
        this(conditions, formula, null);
    }

    public SelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES template, Condition conditions,
                              Formula formula) {
        this(conditions, formula, null);
        this.template = template;
    }

    public SelectiveTargeting(Condition conditions, Formula formula, OBJ_TYPE filteredType) {
        this.filter = new Filter<>();
        this.filter.setConditions(conditions);
        numberOfTargets = formula;
        this.filteredType = filteredType;
    }

    public SELECTIVE_TARGETING_TEMPLATES getTemplate() {
        return template;
    }

    @Override
    public boolean select(Ref ref) {
        if (template != null) {
            filteredType = (getTYPEforTemplate(template));
        }
        if (filteredType != null) {
            filter.setTYPE(filteredType);
        }
        filter.setRef(ref);
        if (ref.getObj(KEYS.ABILITY) instanceof PassiveAbilityObj) {
            ref.setTarget(ref.getSource());
            return true;// return false;
        }

        Integer target = ref.getGame().getManager().select(filter, ref);

        if (target == null) {
            return false;
        }
        ref.setTarget(target);
        LogMaster.log(1, "TARGET SELECTED : "
                + ref.getGame().getObjectById(target));
        return true;

    }

    public OBJ_TYPE getTYPEforTemplate(SELECTIVE_TARGETING_TEMPLATES template) {
        switch (template) {
            case ANY_ALLY:
                return C_OBJ_TYPE.UNITS_CHARS;
            case ANY_ARMOR:
                return C_OBJ_TYPE.UNITS_CHARS;
            case ANY_ENEMY:
                return C_OBJ_TYPE.UNITS_CHARS;
            case ANY_ITEM:
                return C_OBJ_TYPE.ITEMS;
            case ANY_UNIT:
                return C_OBJ_TYPE.UNITS_CHARS;
            case ANY_WEAPON:
                return OBJ_TYPES.WEAPONS;
            case ATTACK:
                return C_OBJ_TYPE.BF_OBJ;
            case BF_OBJ:
                return C_OBJ_TYPE.UNITS_CHARS;
            case BLAST:
                return C_OBJ_TYPE.BF;
            case CELL:
                return OBJ_TYPES.TERRAIN;
            case ENEMY_ARMOR:
                return OBJ_TYPES.ARMOR;
            case ENEMY_ITEM:
                return C_OBJ_TYPE.ITEMS;
            case ENEMY_SPELLBOOK:
                return OBJ_TYPES.SPELLS;
            case ENEMY_WEAPON:
                return OBJ_TYPES.WEAPONS;
            case GRAVE_CELL:
                return C_OBJ_TYPE.BF;
            case MOVE:
                return OBJ_TYPES.TERRAIN;
            case MY_ARMOR:
                return OBJ_TYPES.ARMOR;
            case MY_ITEM:
                return C_OBJ_TYPE.ITEMS;
            case MY_SPELLBOOK:
                return OBJ_TYPES.SPELLS;
            case MY_WEAPON:
                return OBJ_TYPES.WEAPONS;
            case PRECISE_SHOT:
                return C_OBJ_TYPE.BF_OBJ;
            case RAY:
                return C_OBJ_TYPE.BF;
            case SHOT:
                return C_OBJ_TYPE.BF_OBJ;
            case UNOBSTRUCTED_SHOT:
                return C_OBJ_TYPE.BF_OBJ;
            case BLIND_SHOT:
                return C_OBJ_TYPE.BF_OBJ;
            default:
                break;

        }
        return null;
    }

    public enum SELECTIVE_TARGETING_TEMPLATES {
        ATTACK,
        CLAIM,
        SHOT,
        UNOBSTRUCTED_SHOT,
        MOVE,
        ANY_ENEMY,
        ANY_ALLY,
        BLAST,
        MY_SPELLBOOK,
        ENEMY_SPELLBOOK,
        GRAVE_CELL,
        RAY,
        ANY_UNIT,
        CELL,
        ANY_ITEM,
        ANY_WEAPON,
        ANY_ARMOR,
        MY_ARMOR,
        MY_WEAPON,
        BF_OBJ,
        BLIND_SHOT,
        PRECISE_SHOT,
        ENEMY_ARMOR,
        ENEMY_ITEM,
        ENEMY_WEAPON,
        MY_ITEM;
        private boolean zDependent = true;

        SELECTIVE_TARGETING_TEMPLATES() {

        }

        SELECTIVE_TARGETING_TEMPLATES(boolean z) {
            this.zDependent = z;
        }

        public TARGETING_MODE getMode() {
            switch (this) {
                case ANY_ALLY:
                    return TARGETING_MODE.ANY_ALLY;
                case ANY_ENEMY:
                    return TARGETING_MODE.ANY_ENEMY;
                case ANY_UNIT:
                    return TARGETING_MODE.ANY_UNIT;
                case ATTACK:
                    return TARGETING_MODE.ANY_ENEMY;
                case BLAST:
                    return TARGETING_MODE.CELL;
                case CELL:
                    return TARGETING_MODE.CELL;
                case RAY:
                    return TARGETING_MODE.ANY_ENEMY;
                case SHOT:
                    return TARGETING_MODE.ANY_UNIT;
                case UNOBSTRUCTED_SHOT:
                    return TARGETING_MODE.ANY_UNIT;
                default:
                    break;
            }
            return null;
        }

        public boolean isDependentOnZ() {
            return zDependent;
        }
    }
}
