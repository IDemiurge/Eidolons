package eidolons.game.module.cinematic;

import eidolons.libgdx.anims.construct.AnimConstructor;
import main.content.enums.GenericEnums;
import main.content.enums.entity.SpellEnums;

/**
 * alternative branch of handlers?
 * <p>
 * - character choice
 * I'd want to show best portraits, weapons,
 * <p>
 * - spawning
 * I will need a direct method anyway, if only for the campaign
 * -
 * - VFX control - set ambience data
 * More experiments?
 * <p>
 * select map template (ASCII
 * <p>
 * TO TURN OFF:
 * - All shaders
 * - Light rays?
 * <p>
 * All this should be done via single 'debug interface' functions
 */
public class FootageInitializer {

    public static String getVfx(SpellEnums.SPELL_GROUP group, AnimConstructor.ANIM_PART part) {
        if (part == AnimConstructor.ANIM_PART.CAST) {
            return getCastVfx(group);
        }

        if (part == AnimConstructor.ANIM_PART.MISSILE) {
            return getMISSILEVfx(group);
        }


        if (part == AnimConstructor.ANIM_PART.IMPACT) {
            return getImpactVfx(group);
        }

        return null;
}

    private static String getMISSILEVfx(SpellEnums.SPELL_GROUP group) {

        for (GenericEnums.VFX value : GenericEnums.VFX.values()) {
            switch (value) {
                case missile_nether:

                case missile_nether_nox:

                case missile_electric:

                case missile_electric_intense:

                case missile_arcane:

                case missile_arcane_pink:

                case missile_death:

                case missile_arcane_intense:

                case missile_warp:

                case missile_chaos:

                case missile_pale:

                case invert_missile:
            }
        }
        return null;
    }

    private static String getCastVfx(SpellEnums.SPELL_GROUP group) {
        for (GenericEnums.VFX value : GenericEnums.VFX.values()) {
            switch (value) {
                case CAST_dark:
                case CAST_dark2:
                case CAST_dark3:
                case CAST_dark4:
                case CAST_dark5:
                case cold_cast:
                case weave_nether:

                case weave_arcane_pink:

                case weave_arcane:

                case weave_death:

                case weave_chaos:

                case weave_pale:

                case weave_warp:

                case weave_arcane_pink2:

                case invert_fountain:

                case invert_storm:
                case invert_storm_green:
                case invert_storm_brewing:
                case invert_storm_ambi:
                case invert_vortex:
                case invert_abyss:
                case invert_pillar:
                case invert_bleed:
                case invert_ring:
                case invert_bloody_bleed2:
                case invert_breath:
                case invert_darkness:
                    switch (group) {
                        case WITCHERY:
                            return value.getPath();
                    }
            }
        }
        return null;
    }

    private static String getImpactVfx(SpellEnums.SPELL_GROUP group) {

        for (GenericEnums.VFX value : GenericEnums.VFX.values()) {
            switch (value) {
                case invert_impact:

                case nether_impact3:

                case nether_impact2:

                case nether_impact:

                case chaos_impact:

                case cold_impact:

                case pale_impact:

                case warp_impact:

                case necro_impact:

                case death_impact:

                case frost_impact:

                case acid_impact:

                case arcane_impact:
                    return null;
            }
        }
        return null;
    }
}











// case spell_chaos_flames:
//
//         case spell_demonfire:
//
//         case spell_firewave:
//
//         case spell_volcano:
//
//         case spell_hollow_flames:
//
//         case dark_blood:
//
//         case dark_impact:
//
//         case spell_wraiths:
//
//         case spell_poison_veil:
//
//         case spell_teleport_fade:
//
//         case spell_fireball:
//
//         case spell_cold:
//
//         case spell_pale_ward:
//
//         case spell_ghostly_teleport:
//
//         case spell_ghostly_teleport_small:
//
//         case dissipation:
//
//         case dissipation_pale:
//
//         case soulflux_continuous:
//
//         case soul_bleed:
//
//         case soul_bleed_red:
//
//         case darkness: