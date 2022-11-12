package eidolons.game.exploration.dungeon.generator.init;

import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.EncounterEnums.UNIT_GROUP_TYPE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import static main.content.enums.DungeonEnums.DUNGEON_STYLE.PureEvil;
import static main.content.enums.DungeonEnums.DUNGEON_STYLE.Somber;

/**
 * Created by JustMe on 8/1/2018.
 */
@Deprecated
public class RngUnitProvider {
    //TODO Status: Review - YAML needed

    public static final Boolean BOSS = false;
    public static final Boolean ELITE = true;
    public static final Boolean REGULAR = null;
    private static final WeightMap<String> DEFAULT_MAP_REGULAR = new WeightMap<String>()
            // .chain(BLACK_WOLF, 10)
            // .chain(DEMENTED_PRISONER, 10)
            // .chain(DEMENTED_WOMAN, 10)
            // .chain(ABOMINATION, 10)
            // .chain(MUZZLED_MAN, 10)
            // .chain(FRENZIED_WOMAN, 10)
            // .chain(FRENZIED_MAN, 10)
            // .chain(RABID_MAN, 10)
            // .chain(DEADLY_SPIDER, 10)
            // .chain(NIGHT_BAT, 10)
            // .chain(ESCAPED_PRISONER, 10)
            ;
    private static final WeightMap<String> DEFAULT_MAP_ELITE = new WeightMap<String>()
            // .chain(BLACK_WIDOW, 10)
            // .chain(PALE_WEAVER, 10)
            // .chain(DARK_SCALES, 10)
            // .chain(MIST_EYE, 10)
            // .chain(MURKBORN, 10)
            // .chain(MURKBORN_DEFILER, 10)
            // .chain(CRIMSON_MASKED_CULTIST, 10)
            // .chain(WOLF_MASKED_CULTIST, 10)
            // .chain(WORM_MASKED_CULTIST, 10)
            // .chain(ONYX_MASKED_CULTIST, 10)
            // .chain(STEEL_MASKED_CULTIST, 10)
            // .chain(MURK_SPIDER, 10)
            // .chain(MURK_WEAVER, 10)
            // .chain(SKULL_MASKED_CULTIST, 10)
            // .chain(FERAL_CULTIST, 10)
            // .chain(DEMON_CULTIST, 10)
            // .chain(SCORCHED_CULTIST, 10)
            // .chain(SHADOW_WOLF, 10)
            ;
    private static final WeightMap<String> DEFAULT_MAP_BOSS = new WeightMap<String>();

    public static WeightMap<String> getUnitWeightMap
            (UNIT_GROUP group, Boolean elite_boss_regular) {
        WeightMap<String> map = new WeightMap<>();
        switch (group) {
            case ELEMENTALS:
            case FOREST:
            case ANIMALS:
            case PALE_ORCS:
            case ORCS:
            case HUMANS_BARBARIANS:
            case CELESTIALS:
                break;
            case Ravenguard:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case PRISONERS:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;

            case MAGI:
            case CULT_DEATH:
            case CULT_CERBERUS:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case CULT_DARK:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case DARK_ONES:
                if (elite_boss_regular == null)
                    return map
                            ;
                return  map
                        //                 chain(NIGHTMARE, 25).
                        ;

            case MUTANTS:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case PIRATES:

                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case HUMANS_CRUSADERS:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case CONSTRUCTS:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;

            case HUMANS:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        ;

            case HUMANS_KNIGHTS:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        ;
            case BANDITS:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case BANDIT_SCUM:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case NORTH:
            case DWARVEN_LORDS:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case DWARVEN_SCUM:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;


            case DWARVES:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        ;
            case UNDEAD:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        ;
            case UNDEAD_PLAGUE:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        //                 chain(UNDEAD_MONSTROCITY, 10).
                        //                 chain(DEATH_KNIGHT, 5).
                        ;
            case UNDEAD_CRIMSON:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        ;
            case UNDEAD_WRAITH:
                if (elite_boss_regular == null)
                    return map
                            ;
                return elite_boss_regular ? map
                        : map
                        //                 chain(KING_OF_THE_DEAD, 10).
                        //                 chain(DEATH_KNIGHT, 10).
                        ;
            case CULT_CHAOS:
            case DEMONS_ABYSS:
            case DEMONS_WARPED:
            case DEMONS:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map

                        : map
                        ;
            case DEMONS_HELLFIRE:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        ;
            case CRITTERS_COLONY:
                if (elite_boss_regular == null)
                    return map
                            ;

                return map
                        ;

            case CRITTERS:
                if (elite_boss_regular == null)
                    return map
                            ;

                return map
                        ;

            case CRITTERS_SPIDERS:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        ;
            case SPIDERS:
                if (elite_boss_regular == null)
                    return map
                            ;

                return map
                        ;
            case DUNGEON:
                if (elite_boss_regular == null)
                    return map
                            ;

                return   map
                        ;
            case REPTILES:
                if (elite_boss_regular == null)
                    return map
                            ;

                return elite_boss_regular ? map
                        : map
                        ;
        }

        if (elite_boss_regular == null)
            return DEFAULT_MAP_REGULAR;
        return elite_boss_regular ? DEFAULT_MAP_ELITE : DEFAULT_MAP_BOSS
                ;
    }

    public static WeightMap<String> getWeightMap(UNIT_GROUP group, UNIT_GROUP_TYPE groupType
            , boolean alt) {
        Boolean elite_boss_regular = null;
        switch (groupType) {
            case CROWD:
            case IDLERS:
                break;
            case PATROL:
                if (RandomWizard.chance(65))
                    break;
            case GUARDS:
                if (RandomWizard.chance(35))
                    break;
            case STALKER:
            case AMBUSH:
                elite_boss_regular = true;
                break;
            case BOSS:
                elite_boss_regular = false;
                break;
        }
        if (alt) {
            if (elite_boss_regular == null)
                elite_boss_regular = true;
            else if (elite_boss_regular)
                elite_boss_regular = false;
        }
        return getUnitWeightMap(group, elite_boss_regular)
                ;
    }

    public static WeightMap<UNIT_GROUP> getUnitGroup(
            boolean surface, DUNGEON_STYLE style) {
        WeightMap<UNIT_GROUP> map = new WeightMap<>(UNIT_GROUP.class);
        switch (style) {
            case ROGUE:
                return map
                        .chain(UNIT_GROUP.BANDIT_SCUM, 12);
            case SPIDER:
                return map
                        .chain(UNIT_GROUP.SPIDERS, 12);
            case CRYPTS:
                return map
                        .chain(UNIT_GROUP.CULT_CERBERUS, 14)
                        .chain(UNIT_GROUP.UNDEAD, 35)
                        .chain(UNIT_GROUP.UNDEAD_WRAITH, 12)
                        ;
            case PRISON:
                return map
                        .chain(UNIT_GROUP.PRISONERS, 34);
            case BASTION:
                return map
                        .chain(UNIT_GROUP.Ravenguard, 40);
            case MONASTERY:

                return map
                        .chain(UNIT_GROUP.CONSTRUCTS, 12)
                        .chain(UNIT_GROUP.CULT_CERBERUS, 14)
                        .chain(UNIT_GROUP.MUTANTS, 8)
                        .chain(UNIT_GROUP.Ravenguard, 21)
                        .chain(UNIT_GROUP.UNDEAD, 15)
                        .chain(UNIT_GROUP.UNDEAD_WRAITH, 12)
                        ;

            case DWARF:
                return map
                        .chain(UNIT_GROUP.DWARVEN_SCUM, 12)
                        .chain(UNIT_GROUP.DWARVEN_LORDS, 8)
                        ;
            case Arcane:
                return map
                        .chain(UNIT_GROUP.CONSTRUCTS, 12)
                        .chain(UNIT_GROUP.MAGI, 7)
                        .chain(UNIT_GROUP.CULT_DARK, 4)
                        .chain(UNIT_GROUP.CULT_CERBERUS, 4)
                        .chain(UNIT_GROUP.MUTANTS, 5)
                        .chain(UNIT_GROUP.Ravenguard, 3)
                        ;
            case Holy:
                return surface ?
                        map
                                .chain(UNIT_GROUP.Ravenguard, 10)
                                .chain(UNIT_GROUP.HUMANS_KNIGHTS, 7)
                                .chain(UNIT_GROUP.HUMANS, 4)
                                .chain(UNIT_GROUP.HUMANS_CRUSADERS, 4)

                        :
                        map
                                .chain(UNIT_GROUP.Ravenguard, 5)
                                .chain(UNIT_GROUP.HUMANS_KNIGHTS, 3)
                                .chain(UNIT_GROUP.HUMANS, 2)
                                .chain(UNIT_GROUP.HUMANS_CRUSADERS, 2)
                        ;

            case Knightly:
                return surface ?
                        map
                                .chain(UNIT_GROUP.Ravenguard, 10)
                                .chain(UNIT_GROUP.HUMANS_KNIGHTS, 3)
                                .chain(UNIT_GROUP.HUMANS, 4)

                        :
                        map
                                .chain(UNIT_GROUP.Ravenguard, 15)
                                .chain(UNIT_GROUP.BANDITS, 10)
                                .chain(UNIT_GROUP.HUMANS, 6)
                        ;


            case Brimstone:
                return surface ?
                        map
                                .chain(UNIT_GROUP.DEMONS_ABYSS, 10)
                                .chain(UNIT_GROUP.DEMONS_HELLFIRE, 7)
                                .chain(UNIT_GROUP.DEMONS_WARPED, 4)
                                .chain(UNIT_GROUP.CULT_CHAOS, 4)

                        :
                        map
                                .chain(UNIT_GROUP.DEMONS_ABYSS, 10)
                                .chain(UNIT_GROUP.DEMONS_HELLFIRE, 7)
                                .chain(UNIT_GROUP.DEMONS_WARPED, 4)
                        ;
            case DarkElegance:
                return getUnitGroup(surface, Somber).merge(getUnitGroup(surface, PureEvil))
                        ;
            case PureEvil:
                return surface ?
                        map
                                .chain(UNIT_GROUP.UNDEAD, 10)
                                .chain(UNIT_GROUP.DEMONS_WARPED, 10)
                                .chain(UNIT_GROUP.UNDEAD_PLAGUE, 7)
                                .chain(UNIT_GROUP.UNDEAD_CRIMSON, 9)
                                .chain(UNIT_GROUP.UNDEAD_WRAITH, 7)
                                .chain(UNIT_GROUP.DARK_ONES, 8)
                                .chain(UNIT_GROUP.CULT_DEATH, 5)
                                .chain(UNIT_GROUP.CULT_DARK, 10)

                        :
                        map
                                .chain(UNIT_GROUP.DEMONS_ABYSS, 10)
                                .chain(UNIT_GROUP.DEMONS_HELLFIRE, 7)
                                .chain(UNIT_GROUP.DEMONS_WARPED, 4)
                                .chain(UNIT_GROUP.CULT_DEATH, 7)
                                .chain(UNIT_GROUP.UNDEAD, 14)
                                .chain(UNIT_GROUP.UNDEAD_PLAGUE, 8)
                                .chain(UNIT_GROUP.UNDEAD_CRIMSON, 6)
                                .chain(UNIT_GROUP.CULT_DARK, 5)
                        ;
            case Grimy:
                return surface ?
                        map
                                .chain(UNIT_GROUP.BANDITS, 10)
                                .chain(UNIT_GROUP.PIRATES, 6)
                                .chain(UNIT_GROUP.PRISONERS, 5)
                                .chain(UNIT_GROUP.MUTANTS, 5)

                        :
                        map
                                .chain(UNIT_GROUP.DWARVES, 15)
                                .chain(UNIT_GROUP.DUNGEON, 10)
                                .chain(UNIT_GROUP.MUTANTS, 6)
                                //                  .chain(UNIT_GROUP.PALE_ORCS, 6)
                                .chain(UNIT_GROUP.BANDITS, 6)
                                .chain(UNIT_GROUP.CRITTERS_SPIDERS, 6)
                        ;
            case Pagan:
                return surface ?
                        map
                                //                  .chain(UNIT_GROUP.NORTH, 12)
                                .chain(UNIT_GROUP.BANDITS, 10)
                                .chain(UNIT_GROUP.DWARVES, 12)
                        //                  .chain(UNIT_GROUP.ANIMALS, 5)
                        //                  .chain(UNIT_GROUP.HUMANS_BARBARIANS, 3)

                        :
                        map
                                .chain(UNIT_GROUP.DWARVES, 15)
                                .chain(UNIT_GROUP.DUNGEON, 5)
                                .chain(UNIT_GROUP.BANDITS, 10)
                        //                  .chain(UNIT_GROUP.ELEMENTALS, 6)
                        //                  .chain(UNIT_GROUP.UNDEAD_WRAITH, 6)
                        //                  .chain(UNIT_GROUP.PALE_ORCS, 5)
                        ;
            case Stony:
                return surface ?
                        map
                                .chain(UNIT_GROUP.BANDITS, 10)
                                .chain(UNIT_GROUP.PIRATES, 6)
                                .chain(UNIT_GROUP.PRISONERS, 5)
                                .chain(UNIT_GROUP.ANIMALS, 5)
                                .chain(UNIT_GROUP.MUTANTS, 5)
                        //                  .chain(UNIT_GROUP.BARBARIANS, 3)

                        :
                        map
                                .chain(UNIT_GROUP.DUNGEON, 20)
                                .chain(UNIT_GROUP.DWARVES, 12)
                                .chain(UNIT_GROUP.MUTANTS, 6)
                                //                  .chain(UNIT_GROUP.PALE_ORCS, 6)
                                .chain(UNIT_GROUP.CRITTERS, 6)
                        ;
            case Somber:
                return surface ?
                        map
                                .chain(UNIT_GROUP.UNDEAD, 10)
                                .chain(UNIT_GROUP.UNDEAD_PLAGUE, 7)
                                .chain(UNIT_GROUP.UNDEAD_CRIMSON, 9)
                                .chain(UNIT_GROUP.UNDEAD_WRAITH, 7)
                                .chain(UNIT_GROUP.DARK_ONES, 8)
                                .chain(UNIT_GROUP.CULT_DEATH, 5)
                                .chain(UNIT_GROUP.CULT_DARK, 10)
                                .chain(UNIT_GROUP.PRISONERS, 4)

                        :
                        map
                                .chain(UNIT_GROUP.UNDEAD, 10)
                                .chain(UNIT_GROUP.CULT_DEATH, 7)
                                .chain(UNIT_GROUP.UNDEAD_PLAGUE, 5)
                                .chain(UNIT_GROUP.UNDEAD_CRIMSON, 4)
                                .chain(UNIT_GROUP.UNDEAD_WRAITH, 5)
                                .chain(UNIT_GROUP.CRITTERS, 5)
                                .chain(UNIT_GROUP.CULT_DARK, 5)
                                .chain(UNIT_GROUP.PRISONERS, 5)
                        ;
            case Cold:
                return surface ?
                        map
                                .chain(UNIT_GROUP.UNDEAD, 10)
                                .chain(UNIT_GROUP.DWARVES, 17)
                                .chain(UNIT_GROUP.PRISONERS, 14)
                                .chain(UNIT_GROUP.MUTANTS, 14)
                                .chain(UNIT_GROUP.NORTH, 9)
                                .chain(UNIT_GROUP.UNDEAD_WRAITH, 27)
                                .chain(UNIT_GROUP.DARK_ONES, 8)
                                .chain(UNIT_GROUP.CULT_DEATH, 5)
                                .chain(UNIT_GROUP.CULT_DARK, 10)

                        :
                        map
                                .chain(UNIT_GROUP.DUNGEON, 10)
                                .chain(UNIT_GROUP.UNDEAD, 10)
                                .chain(UNIT_GROUP.DWARVES, 27)
                                .chain(UNIT_GROUP.PRISONERS, 14)
                                .chain(UNIT_GROUP.MUTANTS, 14)
                                .chain(UNIT_GROUP.NORTH, 9)
                                .chain(UNIT_GROUP.UNDEAD_WRAITH, 17)
                                .chain(UNIT_GROUP.DARK_ONES, 8)
                                .chain(UNIT_GROUP.CULT_DEATH, 5)
                                .chain(UNIT_GROUP.CULT_DARK, 10)
                        ;
        }
        return map
                .chain(UNIT_GROUP.Ravenguard, 10)
                .chain(UNIT_GROUP.BANDITS, 10)
                .chain(UNIT_GROUP.PRISONERS, 10)
                .chain(UNIT_GROUP.MUTANTS, 10)
                ;
    }
}
