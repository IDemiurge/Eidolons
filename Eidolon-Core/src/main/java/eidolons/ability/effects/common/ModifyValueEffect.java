package eidolons.ability.effects.common;

import eidolons.ability.effects.DC_Effect;
import eidolons.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.core.master.EffectMaster;
import main.ability.PassiveAbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.ReducedEffect;
import main.ability.effects.ResistibleEffect;
import main.content.ContentValsManager;
import main.content.enums.GenericEnums;
import main.content.enums.entity.EffectEnums;
import main.content.values.parameters.PARAMETER;
import main.data.ability.OmittedConstructor;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.IHeroItem;
import main.entity.obj.Obj;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.LogMaster;
import main.system.entity.CounterMaster;
import main.system.math.Formula;
import main.system.math.MathMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.Map;

public class ModifyValueEffect extends DC_Effect implements ResistibleEffect, ReducedEffect {

    protected PARAMETER param;
    protected String sparam;
    protected MOD mod_type;
    protected int amount_modified;
    private PARAMETER max_param;
    private Formula min_max_formula;
    private PARAMETER[] params;
    private Integer resistanceMod;
    private boolean valueOverMax;
    private Double staticAmount;
    private boolean base;
    private String modString;
    private String lastModValue;
    private boolean broken;

    public ModifyValueEffect(PARAMETER param, MOD code, String formula) {
        if (param == null) {
            LogMaster.log(1, "null param on " + this);
        } else {
            this.param = param;
            this.sparam = param.getName();
        }
        this.formula = new Formula(formula);
        this.mod_type = code;

        mapThisToConstrParams(param, code, formula);
    }

    public ModifyValueEffect(PARAMETER param, MOD code, String formula, PARAMETER max) {
        this(param, code, formula);
        this.setMaxParam(max);
    }

    public ModifyValueEffect(PARAMETER param, MOD code, String formula, Formula max_formula) {
        this(param, code, formula);
        this.setMin_max_formula(max_formula);
    }

    public ModifyValueEffect(String sparam, MOD code, Formula formula, Formula max_formula) {
        this(sparam, code, formula);
        this.setMin_max_formula(max_formula);
    }

    public ModifyValueEffect(Boolean base, PARAMETER param, MOD mod_type, String formula) {
        this(base, param.getName(), mod_type, formula);
    }

    public ModifyValueEffect(String percOrConst, String sparam, String formula) {
        this(sparam, (checkPercentOrConst(percOrConst)) ? MOD.MODIFY_BY_PERCENT
         : MOD.MODIFY_BY_CONST, formula);
    }

    public ModifyValueEffect(Boolean base, String sparam, MOD mod_type, String formula) {
        this(sparam, mod_type, formula);
        this.base = base;
    }

    public ModifyValueEffect(String sparam, MOD code, Formula formula) {
        if (sparam.contains(Strings.BASE_CHAR)) {
            sparam = sparam.replace(Strings.BASE_CHAR, "");
            base = true;
        }
        this.sparam = sparam;
        this.formula = formula;
        this.mod_type = code;
    }

    public ModifyValueEffect(String modString) {
        this.modString = modString;

    }

    @OmittedConstructor
    public ModifyValueEffect(String sparam, MOD code, String formula) {
        this(sparam, code, new Formula(formula));
    }

    public ModifyValueEffect(String sparam, MOD code, String formula, String max) {
        this(sparam, code, new Formula(formula), new Formula(max));
    }
    @OmittedConstructor
    public ModifyValueEffect() {
    }

    public ModifyValueEffect(PARAMS p, MOD mod,
                             String formula, boolean valueOverMax) {
        this(p, mod, formula);
        setValueOverMax(valueOverMax);
    }



    private static boolean checkPercentOrConst(String percOrConst) {
        return StringMaster.contains(percOrConst, "mod");
    }

    public static PARAMETER getMaxParameter(PARAMETER param) {
        // ContentManager TODO
        if (param.isDynamic()) {
            if (param.isWriteToType()) {
                return null; // xp, gold, points...
            }
            return ContentValsManager.getBaseParameterFromCurrent(param);
        }
        return null;
    }

    @Override
    public String getTooltip() {
        Ref ref = getRef();
        if (ref == null) {
            ref = new Ref();
        }
        if (mod_type == MOD.SET) {
            return getParamString() + " set to " + formula.getInt(ref);
        }
        return (mod_type == MOD.MODIFY_BY_PERCENT ? StringMaster.getModifierString(formula
         .getInt(ref)) : StringMaster.getBonusString(formula.getInt(ref)))
         + " " + getParamString();
        // return ((param != null) ? param : sparam) +
        // " modified by " + mod_type + " " + formula.getLevel(ref) + "%";
    }

    @Override
    public String toString() {
        return " Param mod effect (" + getParamString() +

         " " + mod_type + " " + formula + ")" + getTargetString();
    }

    private String getTargetString() {
        if (ref == null) {
            return "";
        }
        return (ref.getTargetObj() == null) ? "" : " on " + ref.getTargetObj() + ", Layer ["
         + getLayer() + "]";
    }

    @Override
    public void initLayer() {
        if (ref == null) {
            return;
        }
        if (ref.getTargetObj() == null) {
            return;
        }

        if (ref.getTargetObj() instanceof IHeroItem
            // && !(ref.getTargetObj().getOBJ_TYPE().equals(OBJ_TYPES.ARMOR
            // .getName()))
         ) {
            setLayer(Effect.ZERO_LAYER);
        } else if (mod_type == MOD.MODIFY_BY_PERCENT) {
            if (getParam() != null) {
                if (!getParam().isMastery() && !getParam().isAttribute()) {
                    setLayer(Effect.SECOND_LAYER);
                }
            } else {
                setLayer(Effect.SECOND_LAYER);
            }
        } else {
            super.initLayer();
        }
    }

    public boolean checkLimit() {
        if (isValueOverMax()) {
            return false;
        }
        try {
            return !ref.getObj(KEYS.ACTIVE).checkBool(GenericEnums.STD_BOOLS.C_VALUE_OVER_MAXIMUM);
        } catch (Exception ignored) {
        }
        return true;
    }

    private boolean isValueOverMax() {
        return valueOverMax;
    }

    public void setValueOverMax(boolean valueOverMax) {
        this.valueOverMax = valueOverMax;
    }

    @Override
    public Integer getResistanceMod() {
        return resistanceMod;
    }

    @Override
    public void setResistanceMod(int mod) {
        this.resistanceMod = mod;

    }

    @Override
    public boolean applyThis() {
        if (modString != null) {
            return EffectMaster.initParamModEffects(modString, ref).apply(ref);
        }
        if (param == null) {
            if (sparam.contains(Strings.VERTICAL_BAR)) {
                params = game.getValueManager().getParamsFromContainer(sparam);
            } else {
                this.param = ContentValsManager.getPARAM(sparam);
                if (param == null) {
                    if (param == null) {
                        this.param = ContentValsManager.getMastery(sparam);
                    }
                }
                if (param == null) {
                    params = game.getValueManager().getValueGroupParams(sparam);
                }
            }


        }
        Obj obj = ref.getTargetObj();
        if (obj instanceof DC_Obj) {
            ((DC_Obj) obj).modified(this);
        }
        if (ref.getTargetObj() == null) {
            LogMaster.log(1, "null target!" + this);
            return false;
        }

        Map<PARAMETER, String> map = new HashMap<>();

        if (param == null) {
            if (params == null) {
                EffectEnums.COUNTER counter = CounterMaster.getCounter(sparam, false);
                if (counter == null) {
                    return false;
                }
                return new ModifyCounterEffect(counter, mod_type, formula.toString()).apply(ref);
            }
            for (PARAMETER p : params) {
                if (p == null) {
                    continue;
                }
                param = p;
                modify(obj, map);
            }
            param = null;
            return true;
        }
        // TODO how to determined when formula should be statically parsed?
        // what if some part of the formula depends on the target instead?

        return modify(obj, map);
    }

    private boolean modify(Obj obj, Map<PARAMETER, String> map) {
        Double amount;

        if (staticAmount == null && !broken) {
            try {
                if (checkStaticallyParsed()) {
                    staticAmount = formula.getDouble(ref);
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                broken = true;
            }

        }
        if (staticAmount != null) {
            amount = staticAmount;
        } else {
            amount = formula.getDouble(ref);
        }


        if (ref.getObj(KEYS.ACTIVE) != null) {
            if (ref.getObj(KEYS.ACTIVE).checkBool(GenericEnums.STD_BOOLS.INVERT_ON_ENEMY)) {
                if (!obj.getOwner().equals(ref.getSourceObj().getOwner())) {
                    amount = -amount;
                }
            }
        }

        int intAmount = (int) Math.round(amount);
        if (getResistanceMod() != null) {
            amount = MathMaster.applyModDouble(amount, getResistanceMod());
        }
        int min_max_amount = initMinMaxAmount(obj, intAmount);

        double final_amount = 0;
        switch (mod_type) {
            case SET_TO_PERCENTAGE:
            case MODIFY_BY_PERCENT: {
                double mod =
                 // = obj.getParams(param, ref.isBase()) + "*(" + amount
                 // + "/100)";
                 MathMaster.getFractionValueCentimalDouble(obj.getParamDouble(
                  mod_type == MOD.SET_TO_PERCENTAGE ? ContentValsManager.getBaseParameterFromCurrent(param)
                   : param, base), amount);
                ref.setAmount(mod + "");

                if (param.isDynamic()) {
                    game.getLogManager().logValueMod(param, amount, obj);
                }
                if (mod < 0) {
                    amount_modified = Math.min(obj.getIntParam(param), Math.abs((int) mod));
                } else {
                    amount_modified = (int) mod;
                }
                if (mod_type == MOD.SET_TO_PERCENTAGE) {
                    final_amount = mod;
                    amount_modified = (int) (final_amount - obj.getIntParam(param));
                } else {
                    final_amount = obj.getParamDouble(param) + mod;
                }
                break;
            }
            case MODIFY_BY_CONST: {
                ref.setAmount(amount + "");
//                if (amount < 0) TODO why was it here?
//                    amount_modified = Math.min(obj.getIntParam(param), intAmount);
//                else
                amount_modified = intAmount;
                final_amount = obj.getParamDouble(param) + amount;

                if (param.isDynamic()) {
                    game.getLogManager().logValueMod(param, amount, obj);
                }

//                LogMaster.log(LogMaster.COMBAT_DEBUG, obj.getName() + "'s " + param.getName()
//                 + " is modified by " + amount);
                break;
            }
            case SET: {
                final_amount = amount;
//                LogMaster.log(1, obj.getName() + "'s " + param.getName() + " is set to " + amount);
                break;
            }
        }
        if (!ref.isQuiet() && ref.getActive() != null)
            if (mod_type == MOD.MODIFY_BY_CONST || mod_type == MOD.MODIFY_BY_PERCENT) {
                if (!isContinuousWrapped())
                    GuiEventManager.trigger(GuiEventType.VALUE_MOD,
                     new ImmutablePair<>(param, ref.getCopy()));
            }

        if (amount > 0) {
            if (final_amount > min_max_amount) {
                final_amount = min_max_amount;
            }
            if (final_amount < obj.getIntParam(param)) {
                final_amount = obj.getIntParam(param);
            }
        } else if (amount < 0) {
            if (final_amount < min_max_amount) {
                final_amount = min_max_amount;
            }
        }
        switch (mod_type) {
            case MODIFY_BY_PERCENT: {
                map.put(param, StringMaster.getModifierString((int) final_amount
                 - obj.getIntParam(param)));
                break;
            }
            case MODIFY_BY_CONST:
                map.put(param, StringMaster.getBonusString((int) final_amount
                 - obj.getIntParam(param)));
                break;
            case SET:
                map.put(param, "=" + (int) (final_amount));
                break;

        }
        if (param.isMod()) {
            if (obj.getIntParam(param) == 0) {
                final_amount += 100;
            }
        }
        obj.setParamDouble(param, final_amount, false);

        if (mod_type != MOD.SET && !checkPassive()) {
//          TODO   getGame().getAnimationManager().valueModified(Ref.getCopy(ref));
        }

        lastModValue = String.valueOf(amount_modified);
        return true;
    }

    public int initMinMaxAmount(Obj obj, Integer amount) {
        int min_max_amount = (amount > 0) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (amount > 0) {
            if (max_param != null && max_param != getParam()) {
                min_max_amount = obj.getIntParam(getMax_param());
            }
        }
        if (getMin_max_formula() != null) {
            min_max_amount = getMin_max_formula().getInt(ref);
        }
        if (amount > 0) // maximum
        // if (min_max_amount == Integer.MAX_VALUE)
        {
            if (getParam() != null) {
                if (getParam().isDynamic()) {
                    if (checkLimit()) {
                        setMaxParam(getMaxParameter(getParam()));
                    }
                }
                if (getMax_param() != null) {
                    min_max_amount = obj.getIntParam(getMax_param());
                }
            } else {
                if (ParamAnalyzer.getParamMinValue(getParam()) != Integer.MIN_VALUE) {
                    return ParamAnalyzer.getParamMinValue(getParam());
                }
            }
        }
        return min_max_amount;
    }

    private boolean checkPassive() {
        if (ref.getActive() == null) {
            return true;
        }
        if (ref.getObj(KEYS.BUFF) != null) {
            if (ref.getObj(KEYS.BUFF).equals(ref.getThisObj())) {
                return true;
            }
        }
        if (ref.getObj(KEYS.ABILITY) != null) {
            return ref.getObj(KEYS.ABILITY) instanceof PassiveAbilityObj;
        }

        return false;
    }

    private boolean checkStaticallyParsed() {
        if (isForceStaticParse() != null) {
            return isForceStaticParse();
        }

        if (ref.getActive() != null) {
            if (ref.getObj(KEYS.BUFF) != null || ref.getActive().getRef().getObj(KEYS.BUFF) != null) {
                return ref.getActive().equals(ref.getThisObj())
                        || ref.getObj(KEYS.BUFF).equals(ref.getThisObj()); // only if (?) it is a matter of spell-buff
            }
        }
        // and
        // {this} is {spell} (i.e. the ref given
        // come
        // from
        // spell)
        return false;
    }

    public PARAMETER getMax_param() {
        return max_param;
    }

    public void setMaxParam(PARAMETER max_param) {
        this.max_param = max_param;
    }

    public PARAMETER getParam() {
        if (param == null) {
            this.param = ContentValsManager.getPARAM(sparam);
        }
        return param;
    }

    public void setParam(PARAMETER param) {
        if (param.isMastery()) {
            param = ContentValsManager.getMasteryScore(param);
        }
        this.param = param;
    }

    public MOD getMod_type() {
        return mod_type;
    }

    public void setMod_type(MOD mod_type) {
        this.mod_type = mod_type;
    }

    public Formula getMin_max_formula() {
        return min_max_formula;
    }

    public void setMin_max_formula(Formula min_max_formula) {
        this.min_max_formula = min_max_formula;
    }

    public String getParamString() {
        if (sparam == null) {
            return param.getDisplayedName();
        }
        return sparam;
    }

    public String getLastModValue() {
        return lastModValue;
    }

    public void setLastModValue(String lastModValue) {
        this.lastModValue = lastModValue;
    }
}
