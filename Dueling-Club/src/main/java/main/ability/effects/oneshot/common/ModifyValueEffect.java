package main.ability.effects.oneshot.common;

import main.ability.PassiveAbilityObj;
import main.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.ability.effects.ReducedEffect;
import main.ability.effects.ResistibleEffect;
import main.content.CONTENT_CONSTS.STD_BOOLS;
import main.content.ContentManager;
import main.content.parameters.PARAMETER;
import main.data.ability.OmittedConstructor;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_Obj;
import main.entity.obj.HeroItem;
import main.entity.obj.Obj;
import main.system.ai.logic.target.EffectMaster;
import main.system.ai.tools.ParamAnalyzer;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.auxiliary.StringMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.math.Formula;
import main.system.math.MathMaster;

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

    public ModifyValueEffect(PARAMETER param, MOD code, String formula) {
        if (param == null) {
            LogMaster.log(1, "null param on " + this);
        } else {
            this.param = param;
            this.sparam = param.getName();
        }
        this.formula = new Formula(formula);
        this.mod_type = code;
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
        if (sparam.contains(StringMaster.BASE_CHAR)) {
            sparam = sparam.replace(StringMaster.BASE_CHAR, "");
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

    @OmittedConstructor
    public ModifyValueEffect() {
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
            return ContentManager.getBaseParameterFromCurrent(param);
        }
        return null;
    }

    @Override
    public String getTooltip() {
        if (mod_type == MOD.SET) {
            return getParamString() + " set to " + formula.getInt(ref);
        }
        return (mod_type == MOD.MODIFY_BY_PERCENT ? StringMaster.getModifierString(formula
                .getInt(ref)) : StringMaster.getBonusString(formula.getInt(ref)))
                + " " + getParamString();
        // return ((param != null) ? param : sparam) +
        // " modified by " + mod_type + " " + formula.getInt(ref) + "%";
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

        if (ref.getTargetObj() instanceof HeroItem
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
            return !ref.getObj(KEYS.ACTIVE).checkBool(STD_BOOLS.C_VALUE_OVER_MAXIMUM);
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
            this.param = ContentManager.getPARAM(sparam);
            if (param == null) {
                if (param == null) {
                    this.param = ContentManager.getMastery(sparam);
                }
            }

            if (param == null) {
                if (StringMaster.openContainer(sparam, StringMaster.AND_SEPARATOR).size() > 1) {
                    params = game.getValueManager().getParamsFromContainer(sparam);
                } else {
                    params = game.getValueManager().getValueGroupParams(sparam);
                }
            }

        }
        Obj obj = ref.getTargetObj();
        if (obj instanceof DC_Obj) {
            ((DC_Obj) obj).modified(this);
        }
        if (ref.getTargetObj() == null) {
            main.system.auxiliary.LogMaster.log(1, "null target!" + this);
            return false;
        }

        Map<PARAMETER, String> map = new HashMap<>();

        if (param == null) {
            if (params == null) { // TODO
                return new ModifyCounterEffect(sparam, mod_type, formula).apply(ref);
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

        boolean result = modify(obj, map);
        if (result) {
            if (!isAnimationDisabled()) {
                if (map != null) {
                    if (!map.isEmpty()) {
                        if (getAnimation() != null) {
                            if (!isContinuousWrapped()) {
                                getAnimation().addPhaseArgs(PHASE_TYPE.PARAM_MODS, map);

                                // getAnimation().start(); // TODO sync?
                            } else {
                                wrapInBuffPhase(map);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private boolean modify(Obj obj, Map<PARAMETER, String> map) {
        formula.setRef(ref);
        Double amount;

        if (staticAmount == null) {
            if (checkStaticallyParsed()) {
                staticAmount = formula.getDouble();
            }
        }
        if (staticAmount != null) {
            amount = staticAmount;
        } else {
            amount = formula.getDouble(ref);
        }

        synchronized (ref) { // ain't that stupid...
            if (ref.getObj(KEYS.ACTIVE) != null) {
                if (ref.getObj(KEYS.ACTIVE).checkBool(STD_BOOLS.INVERT_ON_ENEMY)) {
                    if (!obj.getOwner().equals(ref.getSourceObj().getOwner())) {
                        amount = -amount;
                    }
                }
            }
        }
        int intAmount = (int) Math.round(amount);
        if (getResistanceMod() != null) {
            amount = MathMaster.applyModDouble(amount, getResistanceMod());
        }
        int min_max_amount = initMinMaxAmount(obj, intAmount);

        double final_amount = 0;
        ref.setValue(param);
        switch (mod_type) {

            case MODIFY_BY_PERCENT: {
                double mod =
                        // = obj.getParam(param, ref.isBase()) + "*(" + amount
                        // + "/100)";
                        MathMaster.getFractionValueCentimalDouble(obj.getParamDouble(param, base), amount);
                ref.setAmount(mod + "");

                if (param.isDynamic()) {
                    game.getLogManager().logValueMod(param, amount, obj);
                }
                if (mod < 0) {
                    amount_modified = Math.min(obj.getIntParam(param), Math.abs((int) mod));
                } else {
                    amount_modified = (int) mod;
                }
                final_amount = obj.getParamDouble(param) + mod;
                // new Formula(obj.getParam(param) + "+" + mod)
                // .getInt();

                LogMaster.log(LOG_CHANNELS.EFFECT_DEBUG, getLayer() + " layer - " + obj.getName()
                        + "'s " + param.getName() + " is modified by " + amount + "% ("
                        + obj.getParam(param) + " + " + mod + " = " + final_amount + ")");
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

                LogMaster.log(LogMaster.COMBAT_DEBUG, obj.getName() + "'s " + param.getName()
                        + " is modified by " + amount);
                break;
            }
            case SET: {
                final_amount = amount;
                LogMaster.log(1, obj.getName() + "'s " + param.getName() + " is set to " + amount);
                break;
            }
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
            getGame().getAnimationManager().valueModified(Ref.getCopy(ref));
        }

        lastModValue= String.valueOf(amount_modified);
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
            if (ref.getObj(KEYS.ABILITY) instanceof PassiveAbilityObj) {
                return true;
            }
        }

        return false;
    }

    private boolean checkStaticallyParsed() {
        if (isForceStaticParse() != null) {
            return isForceStaticParse();
        }

        if (ref.getActive() != null) {
            if (ref.getObj(KEYS.BUFF) != null || ref.getActive().getRef().getObj(KEYS.BUFF) != null) {
                if (ref.getActive().equals(ref.getThisObj())
                        || ref.getObj(KEYS.BUFF).equals(ref.getThisObj())) {
                    return true; // only if (?) it is a matter of spell-buff
                }
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
            this.param = ContentManager.getPARAM(sparam);
        }
        return param;
    }

    public void setParam(PARAMETER param) {
        if (param.isMastery()) {
            param = ContentManager.getMasteryScore(param);
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
            return param.getName();
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
