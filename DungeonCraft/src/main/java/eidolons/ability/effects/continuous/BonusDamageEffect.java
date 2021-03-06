package eidolons.ability.effects.continuous;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.DamageFactory;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.elements.conditions.Condition;
import main.system.auxiliary.EnumMaster;
import main.system.math.Formula;

/**
 * Created by JustMe on 3/18/2017.
 * <p>
 * Can be applied to a unit, weapon or action/spell
 * <p>
 * will cause creation of @MultiDamage
 */
public class BonusDamageEffect extends DC_Effect {
    Condition conditions;
    private final DAMAGE_TYPE type;
    private final Formula formula;
    private final Formula radiusFormula;
    private final boolean percentage;
    private final DAMAGE_CASE CASE;
    private final boolean fromRaw;

    public BonusDamageEffect(String type,
                             String formula) {
        this(new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class, type), new Formula(formula));
    }

    public BonusDamageEffect(DAMAGE_TYPE type,
                             Formula formula) {
        this(type, formula, null, false, false, DAMAGE_CASE.ATTACK);
    }

    public BonusDamageEffect(DAMAGE_TYPE type,
                             Formula formula,
                             Boolean percentage,
                             Boolean fromRaw,
                             DAMAGE_CASE CASE) {

        this(type, formula, null, percentage, fromRaw, CASE);
    }

    public BonusDamageEffect(DAMAGE_TYPE type,
                             Formula formula,
                             Formula radiusFormula,
                             Boolean percentage,
                             Boolean fromRaw,
                             DAMAGE_CASE CASE) {
        this.type = type;
        this.formula = formula;
        this.radiusFormula = radiusFormula;
        this.percentage = percentage;
        this.fromRaw = fromRaw;
        this.CASE = CASE;
    }

    @Override
    public boolean applyThis() {
        DC_Obj target = (DC_Obj) ref.getTargetObj();
        target.addBonusDamage(CASE, getDamage());
        return false;
    }


    public Damage getDamage() {
        Damage damage = DamageFactory.getDamageForBonusEffect(this);
        return damage;
    }

    public DAMAGE_TYPE getType() {
        return type;
    }

    @Override
    public Formula getFormula() {
        return formula;
    }

    public Formula getRadiusFormula() {
        return radiusFormula;
    }

    public boolean isPercentage() {
        return percentage;
    }

    public DAMAGE_CASE getCASE() {
        return CASE;
    }

    public Condition getConditions() {
        return conditions;
    }

    public boolean isFromRaw() {
        return fromRaw;
    }
}
