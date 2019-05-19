package main.content.enums.system;

import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.content.enums.entity.SpellEnums.SPELL_TYPE;
import main.content.mode.STD_MODES;
import main.data.ability.construct.VarHolder;
import main.data.ability.construct.VariableManager;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class MetaEnums {


    public enum CUSTOM_VALUES {
        COST_REDUCTION_VERBATIM_SPELLS,
        COST_REDUCTION_MEMORIZED_SPELLS,
        COST_REDUCTION_DIVINED_SPELLS,

        DEFENSE_BONUS_VS_AOO_UPON_SPELL,
    }

    public enum CUSTOM_VALUE_TEMPLATE implements VarHolder {
        COST_REDUCTION_ACTIVE_TAG("cost value;tag", VariableManager.getVarIndex(0)
         + "_REDUCTION_FOR_ACTIVES_WITH_TAG_" + VariableManager.getVarIndex(1) + "_EQUALS_"
         + VariableManager.getVarIndex(2), VariableManager.PARAM_VAR_CLASS, String.class),

        COST_MOD_SPELL_TYPE("cost value;spell type", VariableManager.getVarIndex(0) + "_MOD_FOR_"
         + "" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_TYPE.class),

        COST_MOD_SPELL_GROUP("cost value;spell group", VariableManager.getVarIndex(0) + "_MOD_FOR_"
         + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_GROUP.class),
        COST_MOD_SPELL_POOL("cost value;spell group", VariableManager.getVarIndex(0) + "_MOD_FOR_"
         + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_POOL.class),

        COST_MOD_ACTIVE_NAME("cost value;name", VariableManager.getVarIndex(0) + "_MOD_FOR_"
         + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, String.class),

        COST_REDUCTION_SPELL_TYPE("cost value;spell type", VariableManager.getVarIndex(0)
         + "_REDUCTION_FOR_" + "" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_TYPE.class),

        COST_REDUCTION_SPELL_GROUP("cost value;spell group", VariableManager.getVarIndex(0)
         + "_REDUCTION_FOR_" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_GROUP.class),
        COST_REDUCTION_SPELL_POOL("cost value;spell group", VariableManager.getVarIndex(0)
         + "_REDUCTION_FOR_" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_POOL.class),

        COST_REDUCTION_ACTIVE_NAME("cost value;name", VariableManager.getVarIndex(0)
         + "_REDUCTION_FOR_" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, String.class),

        ENABLE_COUNTER(false, "mode", "ENABLE_COUNTER_FOR_" + VariableManager.getVarIndex(0), STD_MODES.class),;

        private String text;
        private Object[] vars;
        private String variableNames;
        private Boolean param = true;

        CUSTOM_VALUE_TEMPLATE(Boolean param, String variableNames, String text,
                              Object... vars) {
            this(variableNames, text, vars);
            this.param = param;
        }

        CUSTOM_VALUE_TEMPLATE(String variableNames, String text, Object... vars) {

            this.text = text;
            this.vars = (vars);
            this.setVariableNames(variableNames);
        }

        public String getText(Object[] variables) {
            return VariableManager.getVarText(text, variables);

        }

        @Override
        public Object[] getVarClasses() {
            return vars;
        }

        @Override
        public String getVariableNames() {
            return variableNames;
        }

        public void setVariableNames(String variableNames) {
            this.variableNames = variableNames;
        }

        public Boolean getParam() {
            return param;
        }

        public void setParam(Boolean param) {
            this.param = param;
        }
    }

    public enum DC_OBJ_CLASSES {
        Unit,
        Structure,
        DC_WeaponObj,
        DC_ArmorObj,
        DC_QuickItemObj,
        DC_ItemActiveObj,
        DC_UnitAction,
        DC_SpellObj,
        DC_FeatObj,
        DC_BuffObj,
        DC_Cell,
        Wave,
    }

    public enum PALETTE {
        INTERIOR, DUNGEON, NATURE, DARK, DEATH, CHAOS, ARCANE,
    }

    public enum STD_BUFF_NAMES {
        Encumbered,
        Overburdened,
        Immobilized,
        Dizzy,
        Discombobulated,
        Razorsharp,
        Exhausted,
        Fatigued,
        Energized,
        Inspired,
        Fearful,
        Terrified,
        Treason,
        Main_Hand_Cadence,
        Off_Hand_Cadence,
        Critically_Wounded,
        Wounded,
        Poison,
        Bleeding,
        Faithless,
        Panic,
        Enraged,
        On_Alert,
        Frost,
        Ablaze,
        Contaminated,
        Spotted,
        Soaked,
        Enchantment,
        Ensnared,
        Asleep,
        Hallucinogetic_Poison,
        Weakening_Poison,
        Paralyzing_Poison,
        Entangled,
        Channeling;

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum SUBPALETTE {
        LIGHT_EMITTERS, CONTAINERS, DOORS, TRAPS, GRAVES, TREES, ROCKS,

    }

    public enum WORKSPACE_GROUP {
        FOCUS, FIX, TEST, IMPLEMENT, DESIGN, POLISH, COMPLETE, EXLCUDED, DEMO, IGG_TODO, DEMO_READY
    }
}
