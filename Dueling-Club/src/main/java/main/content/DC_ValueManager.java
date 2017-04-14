package main.content;

import main.content.values.parameters.PARAMETER;
import main.game.core.game.DC_Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

import static main.content.PARAMS.*;

public class DC_ValueManager implements ValueManager {

    public DC_ValueManager(DC_Game game) {

    }

    private static VALUE_GROUP getValueGroup(String string) {
        VALUE_GROUP template = new EnumMaster<VALUE_GROUP>().retrieveEnumConst(VALUE_GROUP.class,
                StringMaster.cropValueGroup(string));
        return (template);
    }

    public static PARAMETER[] getGroupParams(String string) {
        VALUE_GROUP valueGroup = getValueGroup(string);
        if (valueGroup == null) {
            return null;
        }
        return valueGroup.getParams();
    }

    public static boolean isCentimalModParam(PARAMS p) {
        if (StringMaster.getInteger(p.getDefaultValue()) == 100)
            return true;
        return false;
    }

    public static int getMod(Integer mod, PARAMS p) {
        if (StringMaster.getInteger(p.getDefaultValue()) == 0)
            return 100 + mod;
        return mod;
    }

    public boolean checkValueGroup(String string) {
        if (!string.contains(StringMaster.VALUE_GROUP_OPEN_CHAR)) {
            return false;
        }

        return getValueGroup(string) != null;
    }

    @Override
    public PARAMETER[] getValueGroupParams(String string) {
        VALUE_GROUP valueGroup = getValueGroup(string);
        if (valueGroup == null) {
            return null;
        }
        return valueGroup.getParams();
    }

    @Override
    public PARAMETER[] getParamsFromContainer(String sparam) {
        List<String> container = StringMaster.openContainer(sparam, StringMaster.AND_SEPARATOR);
        LinkedList<PARAMETER> params = new LinkedList<>();
        for (String s : container) {
            PARAMETER param = ContentManager.getPARAM(s);
            if (param != null) {
                params.add(param);
            } else {
                param = ContentManager.getMastery(s);
                if (param != null) {
                    params.add(param);
                }
            }
        }
        return params.toArray(new PARAMETER[params.size()]);

    }

    @Override
    public boolean isRolledRoundind(PARAMETER valueToPay) {
        if (valueToPay == C_N_OF_ACTIONS) {
            return true;
        }
        return false;
    }

    public enum VALUE_GROUP {
        MAGIC(ValuePages.MASTERIES_MAGIC_SCHOOLS),

        OFFENSE_MAGIC(CELESTIAL_MASTERY, AFFLICTION_MASTERY,
                FIRE_MASTERY,
                SORCERY_MASTERY, DESTRUCTION_MASTERY),
        SUPPORT_MAGIC(WATER_MASTERY, BENEDICTION_MASTERY, WITCHERY_MASTERY, PSYCHIC_MASTERY, WARP_MASTERY, ENCHANTMENT_MASTERY, SYLVAN_MASTERY),
        SUMMON_MAGIC(EARTH_MASTERY, CONJURATION_MASTERY, DEMONOLOGY_MASTERY, SAVAGE_MASTERY, NECROMANCY_MASTERY),
        COMBAT_MAGIC(AIR_MASTERY, SHADOW_MASTERY, BLOOD_MAGIC_MASTERY, REDEMPTION_MASTERY),

        ARCANE(CONJURATION_MASTERY, SORCERY_MASTERY, ENCHANTMENT_MASTERY),
        DARK(PSYCHIC_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY),
        CHAOS(WARP_MASTERY, DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY),
        DEATH(NECROMANCY_MASTERY, BLOOD_MAGIC_MASTERY, AFFLICTION_MASTERY),
        LIGHT(REDEMPTION_MASTERY, CELESTIAL_MASTERY, BENEDICTION_MASTERY),
        LIFE(SAVAGE_MASTERY,
                FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY, ELEMENTAL_MASTERY, SYLVAN_MASTERY),
        ELEMENTAL(FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY),
        PRIMAL_MAGIC(SORCERY_MASTERY, WITCHERY_MASTERY, SAVAGE_MASTERY, DESTRUCTION_MASTERY, AFFLICTION_MASTERY),

        FORBIDDEN_ARTS(BLOOD_MAGIC_MASTERY, WARP_MASTERY, DEMONOLOGY_MASTERY, PSYCHIC_MASTERY),

        REVERED_ARTS(SHADOW_MASTERY, REDEMPTION_MASTERY, BENEDICTION_MASTERY, SYLVAN_MASTERY),

        HIGH_ARTS(CONJURATION_MASTERY, CELESTIAL_MASTERY, ENCHANTMENT_MASTERY, ELEMENTAL_MASTERY,

                NECROMANCY_MASTERY),

        MAGIC_SKILLS(WIZARDRY_MASTERY, SPELLCRAFT_MASTERY),

        SPIRIT_SKILLS(MEDITATION_MASTERY, DIVINATION_MASTERY),

        SURVIVAL_SKILLS(ATHLETICS_MASTERY, MOBILITY_MASTERY, UNARMED_MASTERY, MARKSMANSHIP_MASTERY, DETECTION),
        NOBLE_SKILLS(LEADERSHIP_MASTERY, TACTICS_MASTERY, DEFENSE_MASTERY, SHIELD_MASTERY),
        SHADY_SKILLS(MOBILITY_MASTERY, STEALTH_MASTERY, DETECTION, BLADE_MASTERY, ITEM_MASTERY, DUAL_WIELDING_MASTERY),

        SOLDIER_SKILLS(ARMORER_MASTERY, ATHLETICS_MASTERY, POLEARM_MASTERY, DISCIPLINE_MASTERY, SHIELD_MASTERY),

        DARK_ARTS(PSYCHIC_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY),
        ARCANE_ARTS(CONJURATION_MASTERY, SORCERY_MASTERY, ENCHANTMENT_MASTERY),
        DEATH_ARTS(NECROMANCY_MASTERY, BLOOD_MAGIC_MASTERY, AFFLICTION_MASTERY),
        HOLY_ARTS(REDEMPTION_MASTERY, CELESTIAL_MASTERY, BENEDICTION_MASTERY),
        CHAOS_ARTS(WARP_MASTERY, DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY),
        ARTS_OF_NATURE(SAVAGE_MASTERY, FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY, ELEMENTAL_MASTERY, SYLVAN_MASTERY),

        OFFENSE(UNARMED_MASTERY, DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY),
        ANY_OFFENSE(MARKSMANSHIP_MASTERY, UNARMED_MASTERY, DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY),
        BODY(ATHLETICS_MASTERY, MOBILITY_MASTERY),
        DEFENSE(ARMORER_MASTERY, DEFENSE_MASTERY, SHIELD_MASTERY),
        ANY_DEFENSE(ARMORER_MASTERY, DEFENSE_MASTERY, SHIELD_MASTERY),
        WEAPON(BLUNT_MASTERY, BLADE_MASTERY, POLEARM_MASTERY, AXE_MASTERY),
        KNIGHTLY_WEAPONS(BLADE_MASTERY, BLUNT_MASTERY, POLEARM_MASTERY),
        MARTIAL_WEAPONS(BLADE_MASTERY, BLUNT_MASTERY, AXE_MASTERY),
        PRIMITIVE_WEAPONS(UNARMED_MASTERY, BLUNT_MASTERY, AXE_MASTERY, POLEARM_MASTERY),

        // KNIGHTLY_ARTS( BLADE_MASTERY,  POLEARM_MASTERY),
        // ROGUE_SKILLS( BLADE_MASTERY,  POLEARM_MASTERY),
        // WARRIOR_ARTS( BLADE_MASTERY,  POLEARM_MASTERY),
        // SOLDIER_SKILLS( BLADE_MASTERY,  POLEARM_MASTERY),
        // WIZARDING_SKILLS( BLADE_MASTERY,  POLEARM_MASTERY),
        // SORCEROUS_ARTS( BLADE_MASTERY,  POLEARM_MASTERY),

        PENALTIES(ValuePages.PENALTIES),
        RESISTANCES(ValuePages.RESISTANCES),
        PHYSICAL_RESISTANCES(ValuePages.PHYSICAL_RESISTANCES),
        NATURAL_RESISTANCES(ValuePages.NATURAL_RESISTANCES),
        ASTRAL_RESISTANCES(ValuePages.ASTRAL_RESISTANCES),
        ELEMENTAL_RESISTANCES(ValuePages.ELEMENTAL_RESISTANCES),
        MAGIC_RESISTANCES(ValuePages.MAGIC_RESISTANCES),
        SPELL_PENALTIES(ValuePages.PENALTIES),
        ATK_PENALTIES(ValuePages.PENALTIES),
        MOVE_PENALTIES(ValuePages.PENALTIES),
        ATTRIBUTES(ValuePages.ATTRIBUTES),
        XP_MOD(XP_LEVEL_MOD, XP_GAIN_MOD),

        MAGIC_SCORE(true, PSYCHIC_MASTERY, ENCHANTMENT_MASTERY, NECROMANCY_MASTERY, REDEMPTION_MASTERY, SAVAGE_MASTERY, WARP_MASTERY, DESTRUCTION_MASTERY, CELESTIAL_MASTERY,
                FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY,
                ELEMENTAL_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY, CONJURATION_MASTERY, SORCERY_MASTERY),
        ARCANE_SCORE(true, CONJURATION_MASTERY, SORCERY_MASTERY, ENCHANTMENT_MASTERY),
        DARK_SCORE(true, PSYCHIC_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY),
        CHAOS_SCORE(true, WARP_MASTERY, DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY),
        DEATH_SCORE(true, NECROMANCY_MASTERY, BLOOD_MAGIC_MASTERY, AFFLICTION_MASTERY),
        LIGHT_SCORE(true, REDEMPTION_MASTERY, CELESTIAL_MASTERY, BENEDICTION_MASTERY),
        LIFE_SCORE(true, FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY, SAVAGE_MASTERY, ELEMENTAL_MASTERY, SYLVAN_MASTERY),
        OFFENSE_SCORE(true, UNARMED_MASTERY, DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY),
        ANY_OFFENSE_SCORE(true, MARKSMANSHIP_MASTERY, UNARMED_MASTERY, DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY),
        BODY_SCORE(true, ATHLETICS_MASTERY, MOBILITY_MASTERY),
        DEFENSE_SCORE(true, ARMORER_MASTERY, DEFENSE_MASTERY, SHIELD_MASTERY),
        ANY_DEFENSE_SCORE(true, ARMORER_MASTERY, DEFENSE_MASTERY, SHIELD_MASTERY),
        WEAPON_SCORE(true, BLUNT_MASTERY, BLADE_MASTERY, POLEARM_MASTERY, AXE_MASTERY),;
        private PARAMETER[] params;
        private boolean score;

        VALUE_GROUP(boolean score, PARAMETER... params) {
            this(params);
            this.score = score;

        }

        VALUE_GROUP(PARAMETER... params) {
            this.setParams(params);
        }

        public PARAMETER[] getParams() {
            if (score) {
                List<PARAMETER> list = new LinkedList<>();
                for (PARAMETER p : params) {
                    list.add(ContentManager.getMasteryScore(p));
                }
                params = list.toArray(new PARAMETER[params.length]);
                score = false;
            }
            return params;
        }

        public void setParams(PARAMETER[] params) {
            this.params = params;
        }
    }
}
