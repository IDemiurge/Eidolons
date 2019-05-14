package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.UNIT_GROUP_TYPE;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import static main.content.enums.DungeonEnums.DUNGEON_STYLE.*;
import static main.content.enums.entity.UnitEnums.UNITS_TYPES.*;

/**
 * Created by JustMe on 8/1/2018.
 */
public class RngUnitProvider {


    public static final Boolean BOSS = false;
    public static final Boolean ELITE = true;
    public static final Boolean REGULAR = null;
    private static final WeightMap<String> DEFAULT_MAP_REGULAR = new WeightMap<String>()
     .chain(BLACK_WOLF, 10)
     .chain(DEMENTED_PRISONER, 10)
     .chain(DEMENTED_WOMAN, 10)
     .chain(ABOMINATION, 10)
     .chain(MUZZLED_MAN, 10)
     .chain(FRENZIED_WOMAN, 10)
     .chain(FRENZIED_MAN, 10)
     .chain(RABID_MAN, 10)
     .chain(DEADLY_SPIDER, 10)
     .chain(NIGHT_BAT, 10)
     .chain(ESCAPED_PRISONER, 10);
    private static final WeightMap<String> DEFAULT_MAP_ELITE = new WeightMap<String>()
     .chain(BLACK_WIDOW, 10)
     .chain(PALE_WEAVER, 10)
     .chain(DARK_SCALES, 10)
     .chain(MIST_EYE, 10)
     .chain(MURKBORN, 10)
     .chain(MURKBORN_DEFILER, 10)
     .chain(CRIMSON_MASKED_CULTIST, 10)
     .chain(WOLF_MASKED_CULTIST, 10)
     .chain(WORM_MASKED_CULTIST, 10)
     .chain(ONYX_MASKED_CULTIST, 10)
     .chain(STEEL_MASKED_CULTIST, 10)
     .chain(MURK_SPIDER, 10)
     .chain(MURK_WEAVER, 10)
     .chain(SKULL_MASKED_CULTIST, 10)
     .chain(FERAL_CULTIST, 10)
     .chain(DEMON_CULTIST, 10)
     .chain(SCORCHED_CULTIST, 10)
     .chain(SHADOW_WOLF, 10);
    private static final WeightMap<String> DEFAULT_MAP_BOSS = new WeightMap<String>()
     .chain(MISTBORN_GARGANTUAN, 20)
     .chain(RAVENGUARD_COMMANDER, 10);

    public static WeightMap<String> getUnitWeightMap
     (UNIT_GROUP group, Boolean elite_boss_regular) {
        WeightMap<String> map = new WeightMap<>();
        switch (group) {
            case ELEMENTALS:
                break;
            case CELESTIALS:
                break;
            case HUMANS_BARBARIANS:
                break;
            case ORCS:
                break;
            case PALE_ORCS:
                break;
            case ANIMALS:
                break;
            case FOREST:
                break;
            case Ravenguard:
                if (elite_boss_regular == null)
                    return map.
                     chain(RAVENGUARD_ENFORCER, 10).
                     chain(RAVENGUARD_CROSSBOWMAN, 8).
                     chain(SHIELDMAN, 8).
                     chain(SWORDSMAN, 6).
                     chain(RAVENGUARD_SPECIALIST, 6).
                     chain(PIKEMAN, 4).
                     chain(RAVENGUARD_TORTURER, 3).
                     chain(RAVENGUARD_WARDEN, 4);
                return elite_boss_regular ? map.
                 chain(RAVENGUARD_KNIGHT, 10).
                 chain(RAVENGUARD_SPECIALIST, 9).
                 chain(RAVENGUARD_EXECUTIONER, 9).
                 chain(RAVENGUARD_CROSSBOWMAN, 5).
                 chain(RAVENGUARD_TORTURER, 5).
                 chain(RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT, 5).
                 chain(RAVENGUARD_LIEUTENANT, 4).
                 chain(RAVENGUARD_WARDEN, 5)
                 : map.
                 chain(RAVENGUARD_COMMANDER, 10).
                 chain(RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT, 4).
                 chain(RAVENGUARD_LIEUTENANT, 2).
                 chain(RAVENGUARD_KNIGHT, 1).
                 chain(RAVENGUARD_SPECIALIST, 1);
            case PRISONERS:
                if (elite_boss_regular == null)
                    return map.
                     chain(ABOMINATION, 10).
                     chain(DEMENTED_PRISONER, 8).
                     chain(RABID_MAN, 8).
                     chain(FRENZIED_MAN, 6).
                     chain(FRENZIED_WOMAN, 6).
                     chain(MUZZLED_MAN, 6).
                     chain(DEMENTED_WOMAN, 5).
                     chain(ESCAPED_PRISONER, 5);
                return elite_boss_regular ? map.
                 chain(MURKBORN, 10).
                 chain(MURK_WEAVER, 10).
                 chain(RAVENGUARD_TORTURER, 12).
                 chain(RAVENGUARD_WARDEN, 12).
                 chain(MURKBORN_DEFILER, 11)
                 : map.
                 chain(MISTBORN_GARGANTUAN, 5);

            case MAGI:
            case CULT_DEATH:
            case CULT_CERBERUS:
                if (elite_boss_regular == null)
                    return map.
                     chain(APOSTATE, 15).
                     chain(SKELETON, 10).
                     chain(IMP, 10).
                     chain(HELLGUARD, 10).
                     chain(ABOMINATION, 10).
                     chain(MURK_SPIDER, 10).
                     chain(STEEL_MASKED_CULTIST, 6).
                     chain(DEMENTED_PRISONER, 8).
                     chain(DEMENTED_WOMAN, 5);
                return elite_boss_regular ? map.
                 chain(MURKBORN, 10).
                 chain(MURK_WEAVER, 10).
                 chain(MIST_EYE, 15).
                 chain(DARK_SCALES, 10).

                 chain(CRIMSON_MASKED_CULTIST, 12).
                 chain(WOLF_MASKED_CULTIST, 12).
                 chain(WORM_MASKED_CULTIST, 12).
                 chain(ONYX_MASKED_CULTIST, 12).
                 chain(STEEL_MASKED_CULTIST, 12).
                 chain(MURKBORN_DEFILER, 11)
                 : map.
                 chain(WOLF_MASKED_CULTIST, 1).
                 chain(WORM_MASKED_CULTIST, 1).
                 chain(ONYX_MASKED_CULTIST, 1).
                 chain(STEEL_MASKED_CULTIST, 1).
                 chain(MISTBORN_GARGANTUAN, 5);
            case CULT_DARK:
                if (elite_boss_regular == null)
                    return map.
                     chain(APOSTATE, 12).
                     chain(ABOMINATION, 10).
                     chain(WITCH, 16).
                     chain(DARK_APOSTATE, 26).
                     chain(SHADOW, 9).
                     chain(SHADE, 12).
                     chain(MURK_SPIDER, 10).
                     chain(ONYX_MASKED_CULTIST, 9).
                     chain(DEMENTED_PRISONER, 8).
                     chain(DEMENTED_WOMAN, 5);
                return elite_boss_regular ? map.
                 chain(WOLF_MASKED_CULTIST, 22).
                 chain(ONYX_MASKED_CULTIST, 22).
//                 chain(NIGHTMARE, 15).
                 chain(WEREWOLF, 18).
                 chain(POSSESSED_GARGOYLE, 18).
                 chain(DARK_ADEPT, 31).
                 chain(SHADOW_DISCIPLE, 22).
                 chain(MIST_EYE, 10).
                 chain(DARK_SCALES, 10).
                 chain(MURK_WEAVER, 10).
                 chain(MURKBORN_DEFILER, 11)
                 : map.
                 chain(WOLF_MASKED_CULTIST, 1).
                 chain(WORM_MASKED_CULTIST, 1).
                 chain(ONYX_MASKED_CULTIST, 1).
                 chain(STEEL_MASKED_CULTIST, 1).
                 chain(MISTBORN_GARGANTUAN, 5).
                 chain(DARK_ANGEL, 10);
            case DARK_ONES:
                if (elite_boss_regular == null)
                    return map.
                     chain(BLACK_WOLF, 10).
                     chain(ABOMINATION, 10).
                     chain(WITCH, 6).
                     chain(DARK_APOSTATE, 6).
                     chain(SHADOW, 6).
                     chain(SHADE, 5).
                     chain(MURKBORN, 10).
                     chain(MURK_SPIDER, 10);
                return elite_boss_regular ? map.
//                 chain(NIGHTMARE, 25).
                 chain(WEREWOLF, 18).
//                 chain(POSSESSED_GARGOYLE, 18).
//                 chain(VAMPIRE_MISTRESS, 12).
                 chain(DARK_ADEPT, 11).
                 chain(SHADOW_DISCIPLE, 12).
                 chain(VAMPIRE_BEAST, 15).
                 chain(MIST_EYE, 10).
                 chain(DARK_SCALES, 10).
                 chain(MURK_WEAVER, 10).
                 chain(MURKBORN_DEFILER, 11)
                 : map.
                 chain(DARK_ANGEL, 20).
                 chain(VAMPIRE, 10).
                 chain(MISTBORN_GARGANTUAN, 5);

            case MUTANTS:
                if (elite_boss_regular == null)
                    return map.
                     chain(ABOMINATION, 10).
                     chain(DEMENTED_PRISONER, 8).
                     chain(RABID_MAN, 8).
                     chain(FRENZIED_MAN, 6).
                     chain(FRENZIED_WOMAN, 6).
                     chain(DEMENTED_WOMAN, 5).
                     chain(MURKBORN, 10).
                     chain(MURK_SPIDER, 10);
                return elite_boss_regular ? map.
                 chain(MIST_EYE, 15).
                 chain(DARK_SCALES, 10).
                 chain(MURK_WEAVER, 10).
                 chain(MURKBORN_DEFILER, 11)
                 : map.
                 chain(MISTBORN_GARGANTUAN, 5);
            case HUMANS_PIRATES:

                if (elite_boss_regular == null)
                    return map.
                     chain(PIRATE_SKIRMISHER, 11).
                     chain(PIRATE, 8).
                     chain(THUG, 6).
                     chain(THIEF, 7).
                     chain(BANDIT_ARCHER, 10);
                return elite_boss_regular ? map.
                 chain(PIRATE_BRUTE, 10).
                 chain(PIRATE_SABOTEUR, 10).
                 chain(PIRATE_TASKMASTER, 10).
                 chain(PIRATE_FIRST_MATE, 10)
                 : map.
                 chain(PIRATE_CAPTAIN, 5).
                 chain(PIRATE_FIRST_MATE, 3)
                 ;
            case HUMANS_CRUSADERS:
                if (elite_boss_regular == null)
                    return map.
                     chain(MARTYR, 15).
                     chain(PEASANT, 10).
                     chain(SWORDSMAN, 10).
                     chain(SHIELDMAN, 10).
                     chain(CRUSADER, 12).
                     chain(FANATIC, 14);
                return elite_boss_regular ? map.
                 chain(CRUSADER, 25).
                 chain(RED_DAWN_PRIEST, 10).
                 chain(BATTLE_MONK, 35).
                 chain(VINDICATOR, 18).
                 chain(INQUISITOR, 18)
                 : map.
                 chain(DEFENDER_OF_FAITH, 20).
                 chain(LORD_CARDINAL, 20);
            case CONSTRUCTS:
                if (elite_boss_regular == null)
                    return map.
                     chain(IRON_GOLEM, 10).
                     chain(GARGOYLE, 10).
                     chain(STONE_GOLEM, 6);
                return elite_boss_regular ? map.
                 chain(ANCIENT_AUTOMATA, 25).
                 chain(STEEL_GOLEM, 35).
                 chain(POSSESSED_GARGOYLE, 18).
                 chain(GARGOYLE_SENTINEL, 18)
                 : map.
                 chain(MECHANICUM_TITAN, 20);

            case HUMANS:
                if (elite_boss_regular == null)
                    return map.
                     chain(PEASANT, 10).
                     chain(SQUIRE, 8).
                     chain(GUARDSMAN, 7).
                     chain(SWORDSMAN, 7).
                     chain(SHIELDMAN, 7).
                     chain(PIKEMAN, 6).
                     chain(MERCENARY_RANGER, 5).
                     chain(CROSSBOWMAN, 5);

                return elite_boss_regular ? map.
                 chain(KNIGHT_ERRANT, 10).
                 chain(RENEGADE_WIZARD, 10).
                 chain(BOUNTY_HUNTER, 10).
                 chain(FALLEN_KNIGHT, 5).
                 chain(MERCENARY_RANGER, 15).
                 chain(CROSSBOWMAN, 15)
                 : map.
                 chain(KNIGHT_ERRANT, 15).
                 chain(RENEGADE_WIZARD, 12).
                 chain(BOUNTY_HUNTER, 10).
                 chain(RONIN_BALLESTERO, 10).
                 chain(FALLEN_KNIGHT, 5);

            case HUMANS_KNIGHTS:
                if (elite_boss_regular == null)
                    return map.
                     chain(SQUIRE, 12).
                     chain(SWORDSMAN, 7).
                     chain(SHIELDMAN, 7).
                     chain(PIKEMAN, 6).
                     chain(MARTYR, 5).
                     chain(SERVANT_OF_THE_THREE, 5).
                     chain(CROSSBOWMAN, 10);

                return elite_boss_regular ? map.
                 chain(KNIGHT_ERRANT, 10).
                 chain(PRIEST_OF_THE_THREE, 5).
                 chain(CRUSADER, 5).
                 chain(SILVERLANCE_KNIGHT, 5).
                 chain(RONIN_BALLESTERO, 15)
                 : map.
                 chain(DEFENDER_OF_LIGHT, 2).
                 chain(SILVERLANCE_COMMANDER, 5);
            case HUMANS_BANDITS:
                if (elite_boss_regular == null)
                    return map.
                            chain(THIEF, 10).
                            chain(THUG, 10).
                            chain(DESERTER, 10).
                            chain(BANDIT_ARCHER, 10).
                            chain(MARAUDER, 7).
                            chain(CANNIBAL, 3).
                            chain(GRAVE_LOOTER, 5);
                return elite_boss_regular ? map.
                        chain(ASSASSIN, 10).
                        chain(CUTTHROAT, 10).
                        chain(BOUNTY_HUNTER, 5).
                        chain(MARAUDER, 5).
                        chain(MERCENARY_RANGER, 5).
                        chain(SNIPER, 5)
                        : map.
                        chain(BANDIT_LORD, 25).
                        chain(ASSASSIN, 10).
                        chain(SNIPER, 12).
                        chain(SORCERESS, 3).
                        chain(ENCHANTRESS, 2).
                        chain(WARLOCK, 2);
            case BANDIT_SCUM:
                if (elite_boss_regular == null)
                    return map.
                     chain(THIEF, 10).
                     chain(THUG, 10).
                     chain(DESERTER, 10).
                     chain(BANDIT_ARCHER, 10).
                     chain(MARAUDER, 7).
                     chain(CANNIBAL, 3).
                     chain(GRAVE_LOOTER, 5);
                return elite_boss_regular ? map.
                 chain(ASSASSIN, 6).
                 chain(CUTTHROAT, 10).
                 chain(BOUNTY_HUNTER, 5).
                 chain(MARAUDER, 5).
                 chain(MERCENARY_RANGER, 5).
                 chain(RENEGADE_WIZARD, 5)
                 : map.
                 chain(BANDIT_LORD, 25).
                        chain(BOUNTY_HUNTER, 15).
                 chain(ASSASSIN, 20).
                 chain(RENEGADE_WIZARD, 12);
            case NORTH:
            case DWARVEN_LORDS:
                if (elite_boss_regular == null)
                    return map.
                            chain(DWARF_GUARDSMAN, 10).
                            chain(DWARVEN_MILITIA, 10).
                            chain(DWARF_WARRIOR, 10).
                            chain(DWARF_SHOOTER, 10);
                return elite_boss_regular ? map.
                        chain(DWARF_VETERAN, 15).
                        chain(DWARF_SHIELDMAN, 14).
                        chain(DWARF_SHARPEYE, 12).
                        chain(DWARF_GUARDSMAN, 6).
                        chain(DWARF_RUNEPRIEST, 7)
                        : map.
                        chain(DWARF_RUNEPRIEST, 15).
                        chain(DWARF_VETERAN, 10).
                        chain(DWARF_GUARDSMAN, 5).
                        chain(DWARF_WARRIOR, 5).
                        chain(DWARF_SHARPEYE, 5);
            case DWARVEN_SCUM:
                if (elite_boss_regular == null)
                    return map.
                            chain(DWARF_BRAWLER, 10).
                            chain(DWARF_SHOOTER, 10).
                            chain(FORSWORN_DWARF, 15).
                            chain(DOGGY, 10).
                            chain(FORSWORN_MINER, 15);
                return elite_boss_regular ? map.
                        chain(DWARF_BRAWLER, 14).
                        chain(DWARF_WARRIOR, 10).
                        chain(DOGGY, 10).
                        chain(FORSWORN_DWARF, 10).
                        chain(DWARF_SHARPEYE, 12).
                        chain(DWARF_WARRIOR, 7)
                        : map.
                        chain(DWARF_VETERAN, 6).
                        chain(DIRE_GRIZZLY, 6).
                        chain(DWARF_WARRIOR, 8).
                        chain(DWARF_SHARPEYE, 7);



            case DWARVES:
                if (elite_boss_regular == null)
                    return map.
                     chain(DWARF_GUARDSMAN, 10).
                     chain(DWARVEN_MILITIA, 10).
                     chain(DWARF_WARRIOR, 10).
                     chain(DWARF_SHOOTER, 10).
                     chain(FORSWORN_DWARF, 10).
                     chain(FORSWORN_MINER, 10);
                return elite_boss_regular ? map.
                 chain(DWARF_VETERAN, 15).
                 chain(DWARF_BRAWLER, 14).
                 chain(DWARF_SHARPEYE, 12).
                 chain(DWARF_GUARDSMAN, 6).
                 chain(DWARF_WARRIOR, 7)
                 : map.
                 chain(EXILED_THANE, 15).
                 chain(DWARF_VETERAN, 10).
                 chain(DWARF_GUARDSMAN, 5).
                 chain(DWARF_WARRIOR, 5).
                 chain(DWARF_SHARPEYE, 10);
            case UNDEAD:
                if (elite_boss_regular == null)
                    return map.
                     chain(SKELETON, 15).
                     chain(SKELETAL_BEAST, 10).
                     chain(SKELETON_ARCHER, 10).
                     chain(DEATH_WORSHIPPER, 11).
                     chain(ZOMBIE, 10).
//                     chain(ZOMBIE_BEAST, 6).
                     chain(GHAST_ZOMBIE, 6).
                     chain(GHOUL, 5);

                return elite_boss_regular ? map.
                 chain(BONE_KNIGHT, 12).
                 chain(UNDEAD_BEAST, 6).
                 chain(GHOST, 6).
                 chain(DEATH_ADEPT, 11).
                 chain(WRAITH, 5).
                 chain(VAMPIRE, 5).
                 chain(CRIMSON_CHAMPION, 4).
                 chain(GHAST_WRAITH, 4).
                 chain(VAMPIRE_BEAST, 4)
                 : map.
                 chain(VAMPIRE_LORD, 10).
                 //                 chain(DEATH_KNIGHT, 10).
                  chain(DEATH_LORD, 10).
                 //                 chain(LICH, 10).
                 // chain(BONE_DRAGON, 8)
                  chain(VAMPIRE, 6)
                 ;
            case UNDEAD_PLAGUE:
                if (elite_boss_regular == null)
                    return map.
                     chain(PLAGUE_BEARER, 15).
                     chain(PLAGUE_ZOMBIE, 10).
                     chain(PLAGUE_SERVANT, 10).
                     chain(PLAGUE_RAT, 10).
                     chain(ZOMBIE, 7).
                     chain(CORPSE_SLUG, 6);

                return elite_boss_regular ? map.
                 chain(PLAGUE_BRINGER, 12).
                 chain(CORPSEBORN, 6).
                 chain(GHOUL, 6).
                 chain(GHAST_ZOMBIE, 6).
                 chain(CORPSE_SLUG, 6).
                 chain(UNDEAD_BEAST, 6)
                 : map.
                 chain(VAMPIRE_LORD, 10).
                 //                 chain(UNDEAD_MONSTROCITY, 10).
                 //                 chain(DEATH_KNIGHT, 5).
                  chain(LICH, 5).
                  chain(BONE_DRAGON, 1)
                 ;
            case UNDEAD_CRIMSON:
                if (elite_boss_regular == null)
                    return map.
                     chain(VAMPIRE_BAT, 20).
                     chain(BLOOD_GHAST, 15).
                     chain(GHAST_ZOMBIE, 6).
                     chain(VAMPIRE_BEAST, 5).
                     chain(BLOODMAGE_APPRENTICE, 5).
                     chain(SKELETON_ARCHER, 10);

                return elite_boss_regular ? map.
                 chain(BLOOD_REVENANT, 12).
                 chain(CRIMSON_CHAMPION, 12).
                 chain(GHAST_ZOMBIE, 12).
                 chain(CRIMSON_MASKED_CULTIST, 12).
                 chain(VAMPIRE_MISTRESS, 6).
                 chain(VAMPIRE_BEAST, 6).
                 chain(VAMPIRE, 6)
                 : map.
                 chain(DEATH_KNIGHT, 5).
                 chain(VAMPIRE_LORD, 10).
                 chain(VAMPIRE, 6)
                 ;
            case UNDEAD_WRAITH:
                if (elite_boss_regular == null)
                    return map.
                     chain(SKELETON, 15).
                     chain(SKELETAL_BEAST, 10).
                     chain(WRAITH_WHISPERER, 10).
                     chain(SKELETON_ARCHER, 10).
                     chain(GHOST, 5);
                return elite_boss_regular ? map.
                 chain(BONE_KNIGHT, 12).
                 chain(REVENANT, 11).
                 chain(DEATH_ADEPT, 11).
                 chain(GHOST, 11).
                 chain(WRAITH, 12).
                 chain(UNDEAD_BEAST, 6).
                 chain(GHAST_WRAITH, 7)
                 : map.
                 chain(DEATH_LORD, 15).
                 //                 chain(KING_OF_THE_DEAD, 10).
                 //                 chain(DEATH_KNIGHT, 10).
                  chain(LICH, 10)
                 ;
            case CULT_CHAOS:
            case DEMONS_ABYSS:
            case DEMONS_WARPED:
            case DEMONS:
                if (elite_boss_regular == null)
                    return map.
                     chain(FAMILIAR, 10).
                     chain(IMP, 10).
                     chain(HELLGUARD, 10).
                     chain(DEMON_BRUTE, 10).
                     //                     chain(DEMON_WORSHIPPER, 5).
                      chain(FIEND, 4);

                return elite_boss_regular ? map.

                 chain(DEMON_CARNIFEX, 10).
                 chain(DEMON_GORGER, 10).
                 chain(FIEND, 10).
                 //                 chain(WARP_HUNTER, 10).
                 //                 chain(INCUBUS, 10).
                 //                 chain(SUCCUBUS, 10).
                 //                 chain(DEMON_CALLER, 5).
                 //                 chain(DEMON_TORMENTOR, 4).
                 //                 chain(SCREAMER, 4).
                 //                 chain(SATYR_DEMONCALLER, 4).
                  chain(HELLUNICODE39CODEENDS_TYRANT, 4)
                 : map.
                 chain(DEMON_LORD, 10).
                 //                 chain(DEMON_PRINCE, 10).
                  chain(DEVIL_MAGE, 3).
                  chain(ABYSSAL_FIEND, 7)
                 //                 chain(LORD_OF_HATRED, 4).
                 //                 chain(LORD_OF_PAIN, 4).
                 //                 chain(LORD_OF_DESPAIR, 4).
                 //                 chain(LORD_OF_TERROR, 4)
                 ;
            case DEMONS_HELLFIRE:
                if (elite_boss_regular == null)
                    return map.
                     chain(FAMILIAR, 7).
                     chain(IMP, 10).
                     chain(HELLGUARD, 10).
                     chain(DEMON_BRUTE, 10).
                     chain(DEMON_WORSHIPPER, 5).
                     chain(POSSESSED, 5).
                     chain(FIEND, 4);

                return elite_boss_regular ? map.
                 chain(DEMON_CARNIFEX, 10).
                 chain(DEMON_GORGER, 5).
                 chain(FIEND, 15).
                 chain(DEVIL_MAGE, 4).
                 chain(INFERI_SORCEROR, 11)
                 : map.
                 chain(DEMON_LORD, 10).
                 chain(DEMON_PRINCE, 10).
                 chain(LORD_OF_HATRED, 4).
                 chain(LORD_OF_TERROR, 4)
                 ;
            case CRITTERS_COLONY:
                if (elite_boss_regular == null)
                    return map.
                     chain(COLONY_DRONE, 15).
                     chain(COLONY_HARVESTER, 10).
                     chain(COLONY_WARRIOR, 7).
                     chain(COLONY_OVERSEER, 4).
                     chain(HUSK, 6);

                return elite_boss_regular ? map.
                 chain(COLONY_WARRIOR, 7).
                 chain(COLONY_OVERSEER, 4).
                 chain(COLONY_QUEEN, 2)
                 : map.
                 chain(COLONY_WARRIOR, 6).
                 chain(COLONY_OVERSEER, 6).
                 chain(COLONY_QUEEN, 10);

            case CRITTERS:
                if (elite_boss_regular == null)
                    return map.
                     chain(VAMPIRE_BAT, 12).
                     chain(NIGHT_BAT, 8).
                     chain(DEADLY_SPIDER, 7).
                     chain(HARPY, 5).
                     chain(DRAKELING, 6);

                return elite_boss_regular ? map.
                 chain(DRAKE, 14).
                 chain(BASILISK, 10).
                 chain(PALE_WEAVER, 7).
                 chain(BLACK_WIDOW, 5)
                 : map.
//                 chain(MYCOSA, 6).
                 chain(HYDRA, 4)
//                 chain(NIGHTMARE, 4)
                        ;

            case CRITTERS_SPIDERS:
                if (elite_boss_regular == null)
                    return map.
                            chain(SPIDERLING, 12).
                            chain(DEADLY_SPIDER, 7).
                            chain(SPIDERITE, 5).
                            chain(HUSK, 4);

                return elite_boss_regular ? map.
                        chain(SPIDERITE, 14).
                        chain(DEADLY_SPIDER, 10).
                        chain(PALE_WEAVER, 7).
                        chain(BLACK_WIDOW, 5).
                        chain(SHADOW_WEAVER, 7)
                        : map.
                        chain(MYCOSA, 10).
                        chain(HYDRA, 4).
                        chain(CORRUPTED_MIND_FLAYER, 4);
            case SPIDERS:
                if (elite_boss_regular == null)
                    return map.
                     chain(SPIDERLING, 12).
                     chain(DEADLY_SPIDER, 7).
                     chain(SPIDERITE, 5).
                     chain(ABOMINATION, 5).
                     chain(HUSK, 4);

                return elite_boss_regular ? map.
                 chain(SPIDERITE, 14).
                 chain(DEADLY_SPIDER, 10).
                 chain(PALE_WEAVER, 7).
                 chain(BLACK_WIDOW, 5).
                 chain(MURK_SPIDER, 5).
                 chain(MURK_WEAVER, 7)
                 : map.
                        chain(PALE_WEAVER, 7).
                        chain(BLACK_WIDOW, 15).
                        chain(MURK_SPIDER, 5).
                        chain(MURK_WEAVER, 7);
            case DUNGEON:
                if (elite_boss_regular == null)
                    return map.
                     chain(TROGLODYTE, 10).
                     chain(TROGLODYTE_MUTANT, 5).
                     chain(HARPY, 5).
                     chain(BASILISK, 3).
                     chain(SLAAG, 3).
                     chain(DRAKE, 3).
                     chain(MINOTAUR, 4).
                     chain(EVIL_EYE, 6);

                return elite_boss_regular ? map.
                 chain(MINOTAUR_PRAETOR, 10).
                 chain(MINOTAUR, 6).
                 chain(MIND_FLAYER, 5).
                 chain(CORRUPTED_MIND_FLAYER, 4).
                 chain(SLAAG, 3).
                 chain(DRAKE, 3).
                 chain(MANTICORE, 4).
                 chain(EVIL_EYE, 6)
                 : map.
                 chain(HYDRA, 10).
                 chain(MANTICORE, 10).
                 chain(MINOTAUR_PRAETOR, 4).
                 chain(BLACK_DRAGON, 5).
                 chain(CORRUPTED_MIND_FLAYER, 4);
            case REPTILES:
                if (elite_boss_regular == null)
                    return map.
                     chain(BASILISK, 10).
                     chain(SLAAG, 10).
                     chain(DRAKELING, 14).
                     chain(DRAKE, 5);

                return elite_boss_regular ? map.
                 chain(BASILISK, 10).
                 chain(SLAAG, 10).
                 chain(WYVERN, 10).
                 chain(YOUNG_WYVERN, 15)
                 : map.
                 chain(HYDRA, 10).
                 chain(WYVERN, 10).
                 chain(ARMORED_WYVERN, 10).
                 chain(YOUNG_WYVERN, 5).
                 chain(BLACK_DRAGON, 5);
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
        return getUnitWeightMap(group, elite_boss_regular);
    }

    public static WeightMap<UNIT_GROUP> getUnitGroup(
     boolean surface, DUNGEON_STYLE style) {
        WeightMap<UNIT_GROUP> map = new WeightMap<>(UNIT_GROUP.class);
        switch (style) {
            case ROGUE:
                return map
                        .chain(UNIT_GROUP.BANDIT_SCUM, 12) ;
            case SPIDER:
                return map
                        .chain(UNIT_GROUP. SPIDERS, 12) ;
            case CRYPTS:
                return map
                        .chain(UNIT_GROUP.CULT_CERBERUS, 14)
                        .chain(UNIT_GROUP.UNDEAD, 35)
                        .chain(UNIT_GROUP.UNDEAD_WRAITH, 12)
                        ;
            case PRISON:
                return map
                        .chain(UNIT_GROUP.PRISONERS, 34) ;
            case BASTION:
                return map
                        .chain(UNIT_GROUP.Ravenguard, 40) ;
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
                        .chain(UNIT_GROUP.DWARVEN_LORDS, 8);
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
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
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
                return getUnitGroup(surface, Somber).merge(getUnitGroup(surface, PureEvil));
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
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                  .chain(UNIT_GROUP.HUMANS_PIRATES, 6)
                  .chain(UNIT_GROUP.PRISONERS, 5)
                  .chain(UNIT_GROUP.MUTANTS, 5)

                 :
                 map
                  .chain(UNIT_GROUP.DWARVES, 15)
                  .chain(UNIT_GROUP.DUNGEON, 10)
                  .chain(UNIT_GROUP.MUTANTS, 6)
//                  .chain(UNIT_GROUP.PALE_ORCS, 6)
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 6)
                  .chain(UNIT_GROUP.CRITTERS_SPIDERS, 6)
                 ;
            case Pagan:
                return surface ?
                 map
                  //                  .chain(UNIT_GROUP.NORTH, 12)
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                  .chain(UNIT_GROUP.DWARVES, 12)
                 //                  .chain(UNIT_GROUP.ANIMALS, 5)
                 //                  .chain(UNIT_GROUP.HUMANS_BARBARIANS, 3)

                 :
                 map
                  .chain(UNIT_GROUP.DWARVES, 15)
                  .chain(UNIT_GROUP.DUNGEON, 5)
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                 //                  .chain(UNIT_GROUP.ELEMENTALS, 6)
                 //                  .chain(UNIT_GROUP.UNDEAD_WRAITH, 6)
                 //                  .chain(UNIT_GROUP.PALE_ORCS, 5)
                 ;
            case Stony:
                return surface ?
                 map
                  .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                  .chain(UNIT_GROUP.HUMANS_PIRATES, 6)
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
        return  map
         .chain(UNIT_GROUP.Ravenguard, 10)
         .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
         .chain(UNIT_GROUP.PRISONERS, 10)
         .chain(UNIT_GROUP.MUTANTS, 10)
         ;
    }
}
