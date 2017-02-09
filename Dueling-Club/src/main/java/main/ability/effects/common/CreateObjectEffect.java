package main.ability.effects.common;

import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.game.battlefield.BuffMaster;
import main.system.auxiliary.StringMaster;

public class CreateObjectEffect extends SummonEffect {

    public static final String STANDARD_PARAM_MODS = "Toughness(1);Endurance(2);"
            + "Armor(0.1);Magic Resistances(0.15);Physical Resistances(0.15);";
    public static final String DEFAULT_BUFF_NAME = "Creation Mastery";
    private String param_mods;
    private String buff_name;
    private boolean neutral = false;

    public CreateObjectEffect(String typeName, String param_mods) {

        super(typeName);
        this.param_mods = param_mods;
    }

    public CreateObjectEffect(String typeName) {
        super(typeName);
        param_mods = STANDARD_PARAM_MODS;
    }

    public CreateObjectEffect(String typeName, Boolean neutral) {
        super(typeName);
        this.neutral = neutral;
    }

    private void applyParamBuff() {
        Effects param_effects = new Effects();
        for (String s : StringMaster.openContainer(param_mods)) {
            String varPart = VariableManager.getVarPart(s);
            String valueName = s.replace(varPart, "");
            String formula = StringMaster.cropParenthesises(varPart)
                    + " *({Summoner_Spellpower}+{Summoner_Mastery}) ";// MAX?

            param_effects.add(new ModifyValueEffect(valueName,
                    MOD.MODIFY_BY_CONST, formula));
        }
        BuffMaster.applyBuff(getBuffName(), param_effects, unit,
                ContentManager.INFINITE_VALUE); // retain only
        // while
        // summoner
        // alive?
    }

    @Override
    protected ObjType addXp(ObjType type) {
        return (type);
    }

    private String getBuffName() {
        if (buff_name != null) {
            return buff_name;
        }
        return DEFAULT_BUFF_NAME;
    }

    // upkeep?
    // formula for hp/armor/resistance, similar to
    @Override
    public boolean applyThis() {
        if (!super.applyThis()) {
            return false;
        }
        if (!neutral) {
            applyParamBuff();
        }

        return true;

    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return OBJ_TYPES.BF_OBJ;
    }

}
