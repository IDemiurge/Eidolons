package main.elements.targeting;

import main.ability.PassiveAbilityObj;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.log.LogMaster;

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
        this(conditions, null);
    }

    public SelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES template, Condition conditions) {
        this(conditions, null);
        this.template = template;
    }

    public SelectiveTargeting(Condition conditions, OBJ_TYPE filteredType) {
        this.filter = new Filter<>();
        this.filter.setConditions(conditions);
        this.filteredType = filteredType;
    }

    public SELECTIVE_TARGETING_TEMPLATES getTemplate() {
        return template;
    }

    @Override
    public boolean select(Ref ref) {
//        if (ref.getSourceObj().isAiControlled()) {
//            Integer id = TargetingMaster.selectTargetForAction(getEntity());
//            if (id != null) {
//               return  true;
//            }
//            return false;
//        }
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
        if (target > 0) {
            ref.setTarget(target);
        }
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
                return DC_TYPE.WEAPONS;
            case ATTACK:
                return C_OBJ_TYPE.BF_OBJ;
            case BF_OBJ:
                return C_OBJ_TYPE.UNITS_CHARS;
            case BLAST:
                return C_OBJ_TYPE.BF;
            case CELL:
                return DC_TYPE.TERRAIN;
            case ENEMY_ARMOR:
                return DC_TYPE.ARMOR;
            case ENEMY_ITEM:
                return C_OBJ_TYPE.ITEMS;
            case ENEMY_SPELLBOOK:
                return DC_TYPE.SPELLS;
            case ENEMY_WEAPON:
                return DC_TYPE.WEAPONS;
            case GRAVE_CELL:
                return C_OBJ_TYPE.BF;
            case MOVE:
                return DC_TYPE.TERRAIN;
            case MY_ARMOR:
                return DC_TYPE.ARMOR;
            case MY_ITEM:
                return C_OBJ_TYPE.ITEMS;
            case MY_SPELLBOOK:
                return DC_TYPE.SPELLS;
            case MY_WEAPON:
                return DC_TYPE.WEAPONS;
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
        KEY,
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
        private boolean zDependent = false;

        SELECTIVE_TARGETING_TEMPLATES() {

        }

        SELECTIVE_TARGETING_TEMPLATES(boolean z) {
            this.zDependent = z;
        }

        public TARGETING_MODE getMode() {
            switch (this) {
                case ANY_ALLY:
                    return AbilityEnums.TARGETING_MODE.ANY_ALLY;
                case ANY_ENEMY:
                    return AbilityEnums.TARGETING_MODE.ANY_ENEMY;
                case ANY_UNIT:
                    return AbilityEnums.TARGETING_MODE.ANY_UNIT;
                case ATTACK:
                    return AbilityEnums.TARGETING_MODE.ANY_ENEMY;
                case BLAST:
                    return AbilityEnums.TARGETING_MODE.CELL;
                case CELL:
                    return AbilityEnums.TARGETING_MODE.CELL;
                case RAY:
                    return AbilityEnums.TARGETING_MODE.ANY_ENEMY;
                case SHOT:
                    return AbilityEnums.TARGETING_MODE.ANY_UNIT;
                case UNOBSTRUCTED_SHOT:
                    return AbilityEnums.TARGETING_MODE.ANY_UNIT;
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
