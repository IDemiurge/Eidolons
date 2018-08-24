package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.SPAWN_GROUP_TYPE;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.system.datatypes.WeightMap;

import static main.content.enums.entity.UnitEnums.UNITS_TYPES.*;

/**
 * Created by JustMe on 8/1/2018.
 */
public class RngUnitProvider {
    public static WeightMap<String> getGroupWeightMap(DUNGEON_STYLE style, boolean underground) {
        switch (style) {
            case Knightly:
                break;
            case Holy:
                break;
            case Pagan:
                break;
            case DarkElegance:
                break;
            case PureEvil:
                break;
            case Brimstone:
                break;
            case Stony:
                break;
            case Grimy:
                return underground ? new WeightMap<String>()
                 .chain(UNIT_GROUP.DUNGEON, 10)
                 .chain(UNIT_GROUP.DWARVES, 10)
                 .chain(UNIT_GROUP.UNDEAD, 10)
                 : new WeightMap<String>()
                 .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                 .chain(UNIT_GROUP.DWARVES, 10)
                 .chain(UNIT_GROUP.UNDEAD_CRIMSON, 10)
                 .chain(UNIT_GROUP.UNDEAD_PLAGUE, 10);
            case Somber:
                return new WeightMap<String>()
                 .chain(UNIT_GROUP.HUMANS_BANDITS, 10)
                 .chain(UNIT_GROUP.UNDEAD, 10)
                 .chain(UNIT_GROUP.Ravenguard, 10);
            case Arcane:
                break;
            case Cold:
                break;
        }
        return new WeightMap<String>().chain(UNIT_GROUP.HUMANS_BANDITS, 10);

    }

    public static WeightMap<String> getBossWeightMap(UNIT_GROUP group) {
        return getUnitWeightMap(group, true);
    }

    public static WeightMap<String> getUnitWeightMap
     (UNIT_GROUP group, Boolean elite_boss_regular) {
        switch (group) {
            case ELEMENTALS:
                break;
            case Ravenguard:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(RAVENGUARD_ENFORCER, 10).
                     chain(RAVENGUARD_CROSSBOWMAN, 8).
                     chain(SHIELDMAN, 8).
                     chain(SWORDSMAN, 6).
                     chain(RAVENGUARD_SPECIALIST, 6).
                     chain(PIKEMAN, 4).
                     chain(RAVENGUARD_TORTURER, 5).
                     chain(RAVENGUARD_WARDEN, 5);
                return elite_boss_regular ? new WeightMap<String>().
                 chain(RAVENGUARD_COMMANDER, 10).
                 chain(RAVENGUARD_KNIGHT, 1).
                 chain(RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT, 1).
                 chain(RAVENGUARD_SPECIALIST, 1)
                 : new WeightMap<String>().
                 chain(RAVENGUARD_KNIGHT, 10).
                 chain(RAVENGUARD_SPECIALIST, 9).
                 chain(RAVENGUARD_CROSSBOWMAN, 8).
                 chain(RAVENGUARD_TORTURER, 5).
                 chain(RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT, 5).
                 chain(RAVENGUARD_WARDEN, 4);
            case PRISONERS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(ABOMINATION, 10).
                     chain(DEMENTED_PRISONER, 8).
                     chain(RABID_MAN, 8).
                     chain(FRENZIED_MAN, 6).
                     chain(FRENZIED_WOMAN, 6).
                     chain(MUZZLED_MAN, 6).
                     chain(DEMENTED_WOMAN, 5).
                     chain(ESCAPED_PRISONER, 5);
                return elite_boss_regular ? new WeightMap<String>().
                 chain(MURKBORN, 10).
                 chain(MURK_WEAVER, 10).
                 chain(RAVENGUARD_TORTURER, 12).
                 chain(RAVENGUARD_WARDEN, 12).
                 chain(MURKBORN_DEFILER, 11)
                 : new WeightMap<String>().
                 chain(MISTBORN_GARGANTUAN, 5) ;

            case CULT_CERBERUS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(APOSTATE, 15).
                     chain(SKELETON, 10).
                     chain(IMP, 10).
                     chain(HELLGUARD, 10).
                     chain(ABOMINATION, 10).
                     chain(MURK_SPIDER, 10).
                     chain(STEEL_MASKED_CULTIST, 6).
                     chain(DEMENTED_PRISONER, 8).
                     chain(DEMENTED_WOMAN, 5);
                return elite_boss_regular ? new WeightMap<String>().
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
                 : new WeightMap<String>().
                 chain(WOLF_MASKED_CULTIST, 1).
                 chain(WORM_MASKED_CULTIST, 1).
                 chain(ONYX_MASKED_CULTIST, 1).
                 chain(STEEL_MASKED_CULTIST, 1).
                 chain(MISTBORN_GARGANTUAN, 5) ;
            case CULT_DEATH:
                break;
            case CULT_DARK:
                if (elite_boss_regular == null)
                return new WeightMap<String>().
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
                return elite_boss_regular ? new WeightMap<String>().
                 chain(WOLF_MASKED_CULTIST, 22).
                 chain(ONYX_MASKED_CULTIST, 22).
                 chain(NIGHTMARE, 15).
                 chain(WEREWOLF, 18).
                 chain(POSSESSED_GARGOYLE, 18).
                 chain(DARK_ADEPT, 31).
                 chain(SHADOW_DISCIPLE , 22).
                 chain(MIST_EYE, 10).
                 chain(DARK_SCALES, 10).
                 chain(MURK_WEAVER, 10).
                 chain(MURKBORN_DEFILER, 11)
                 : new WeightMap<String>().
                 chain(WOLF_MASKED_CULTIST, 1).
                 chain(WORM_MASKED_CULTIST, 1).
                 chain(ONYX_MASKED_CULTIST, 1).
                 chain(STEEL_MASKED_CULTIST, 1).
                 chain(MISTBORN_GARGANTUAN, 5).
                 chain(DARK_ANGEL, 10) ;
            case CULT_CHAOS:
                break;

            case DARK_ONES:
                if (elite_boss_regular == null)
                return new WeightMap<String>().
                 chain(BLACK_WOLF, 10).
                 chain(ABOMINATION, 10).
                 chain(WITCH, 6).
                 chain(DARK_APOSTATE, 6).
                 chain(SHADOW, 6).
                 chain(SHADE, 5).
                 chain(MURKBORN, 10).
                 chain(MURK_SPIDER, 10);
                return elite_boss_regular ? new WeightMap<String>().
                 chain(NIGHTMARE, 25).
                 chain(WEREWOLF, 18).
                 chain(POSSESSED_GARGOYLE, 18).
                 chain(VAMPIRE_MISTRESS , 12).
                 chain(DARK_ADEPT, 11).
                 chain(SHADOW_DISCIPLE , 12).
                 chain(VAMPIRE_BEAST , 15).
                 chain(MIST_EYE, 10).
                 chain(DARK_SCALES, 10).
                 chain(MURK_WEAVER, 10).
                 chain(MURKBORN_DEFILER, 11)
                 : new WeightMap<String>().
                 chain(DARK_ANGEL , 20).
                 chain(VAMPIRE , 10).
                 chain(MISTBORN_GARGANTUAN, 5) ;

            case MUTANTS:
                if (elite_boss_regular == null)
                return new WeightMap<String>().
                 chain(ABOMINATION, 10).
                 chain(DEMENTED_PRISONER, 8).
                 chain(RABID_MAN, 8).
                 chain(FRENZIED_MAN, 6).
                 chain(FRENZIED_WOMAN, 6).
                 chain(DEMENTED_WOMAN, 5).
                 chain(MURKBORN, 10).
                 chain(MURK_SPIDER, 10);
                return elite_boss_regular ? new WeightMap<String>().
                 chain(MIST_EYE, 15).
                 chain(DARK_SCALES, 10).
                 chain(MURK_WEAVER, 10).
                 chain(MURKBORN_DEFILER, 11)
                 : new WeightMap<String>().
                 chain(MISTBORN_GARGANTUAN, 5) ;
            case HUMANS_PIRATES:

                if (elite_boss_regular == null)
                return new WeightMap<String>().
                 chain(PIRATE_SKIRMISHER, 11).
                 chain(PIRATE, 8).
                 chain(THUG, 6).
                 chain(THIEF, 7).
                 chain(BANDIT_ARCHER, 10) ;
                return elite_boss_regular ? new WeightMap<String>().
                 chain(PIRATE_BRUTE, 10).
                 chain(PIRATE_SABOTEUR, 10).
                 chain(PIRATE_TASKMASTER, 10).
                 chain(PIRATE_FIRST_MATE, 10)
                 : new WeightMap<String>().
                 chain(PIRATE_CAPTAIN, 5) .
                 chain(PIRATE_FIRST_MATE, 3)
                 ;
            case HUMANS_CRUSADERS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(MARTYR, 15).
                     chain(PEASANT, 10).
                     chain(SWORDSMAN, 10).
                     chain(SHIELDMAN, 10).
                     chain(CRUSADER, 12).
                     chain(FANATIC, 14) ;
                return elite_boss_regular ? new WeightMap<String>().
                 chain(CRUSADER, 25).
                 chain(RED_DAWN_PRIEST, 10).
                 chain(BATTLE_MONK, 35).
                 chain(VINDICATOR, 18).
                 chain(INQUISITOR, 18)
                 : new WeightMap<String>().
                 chain(DEFENDER_OF_FAITH , 20).
                 chain(LORD_CARDINAL , 20)  ;
            case CONSTRUCTS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(IRON_GOLEM, 10).
                     chain(GARGOYLE, 10).
                     chain(STONE_GOLEM, 6) ;
                return elite_boss_regular ? new WeightMap<String>().
                 chain(ANCIENT_AUTOMATA, 25).
                 chain(STEEL_GOLEM, 35).
                 chain(POSSESSED_GARGOYLE, 18).
                 chain(GARGOYLE_SENTINEL, 18)
                 : new WeightMap<String>().
                 chain(MECHANICUM_TITAN , 20)  ;

            case CELESTIALS:
                break;
            case HUMANS_BARBARIANS:
                break;
            case HUMANS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(PEASANT, 10).
                     chain(SQUIRE, 8).
                     chain(GUARDSMAN, 7).
                     chain(SWORDSMAN, 7).
                     chain(SHIELDMAN, 7).
                     chain(PIKEMAN, 6).
                     chain(MERCENARY_RANGER, 5).
                     chain(CROSSBOWMAN, 5);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(KNIGHT_ERRANT, 10).
                 chain(RENEGADE_WIZARD, 10).
                 chain(BOUNTY_HUNTER, 10).
                 chain(FALLEN_KNIGHT, 5).
                 chain(MERCENARY_RANGER, 15).
                 chain(CROSSBOWMAN, 15)
                 : new WeightMap<String>().
                 chain(KNIGHT_ERRANT, 15).
                 chain(RENEGADE_WIZARD, 12).
                 chain(BOUNTY_HUNTER, 10).
                 chain(RONIN_BALLESTERO, 10).
                 chain(FALLEN_KNIGHT, 5);

            case HUMANS_KNIGHTS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(SQUIRE, 12).
                     chain(SWORDSMAN, 7).
                     chain(SHIELDMAN, 7).
                     chain(PIKEMAN, 6).
                     chain(MARTYR, 5).
                     chain(SERVANT_OF_THE_THREE, 5).
                     chain(CROSSBOWMAN, 10);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(KNIGHT_ERRANT, 10).
                 chain(PRIEST_OF_THE_THREE, 5).
                 chain(CRUSADER, 5).
                 chain(SILVERLANCE_KNIGHT, 5).
                 chain(RONIN_BALLESTERO, 15)
                 : new WeightMap<String>().
                 chain(DEFENDER_OF_LIGHT, 2).
                 chain(SILVERLANCE_COMMANDER, 5);
            case ORCS:
                break;
            case HUMANS_BANDITS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(THIEF, 10).
                     chain(THUG, 10).
                     chain(DESERTER, 10).
                     chain(BANDIT_ARCHER, 10).
                     chain(MARAUDER, 7).
                     chain(GRAVE_LOOTER, 5);
                return elite_boss_regular ? new WeightMap<String>().
                 chain(ASSASSIN, 10).
                 chain(CUTTHROAT, 10).
                 chain(BOUNTY_HUNTER, 5).
                 chain(MARAUDER, 5).
                 chain(MERCENARY_RANGER, 5).
                 chain(SNIPER, 5)
                 : new WeightMap<String>().
                 chain(BANDIT_LORD, 15).
                 chain(ASSASSIN, 10).
                 chain(SNIPER, 12).
                 chain(SORCERESS, 3).
                 chain(ENCHANTRESS, 2).
                 chain(WARLOCK, 2);
            case DWARVES:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(DWARF_GUARDSMAN, 10).
                     chain(DWARVEN_MILITIA, 10).
                     chain(DWARF_WARRIOR, 10).
                     chain(DWARF_SHOOTER, 10).
                     chain(FORSWORN_DWARF, 10).
                     chain(FORSWORN_MINER, 10);
                return elite_boss_regular ? new WeightMap<String>().
                 chain(DWARF_VETERAN, 15).
                 chain(DWARF_BRAWLER, 14).
                 chain(DWARF_SHARPEYE, 12).
                 chain(DWARF_GUARDSMAN, 6).
                 chain(DWARF_WARRIOR, 7)
                 : new WeightMap<String>().
                 chain(EXILED_THANE, 15).
                 chain(DWARF_VETERAN, 10).
                 chain(DWARF_GUARDSMAN, 5).
                 chain(DWARF_WARRIOR, 5).
                 chain(DWARF_SHARPEYE, 10);
            case NORTH:
                break;
            case PALE_ORCS:
                break;
            case UNDEAD:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(SKELETON, 15).
                     chain(SKELETAL_BEAST, 10).
                     chain(SKELETON_ARCHER, 10).
                     chain(DEATH_WORSHIPPER, 11).
                     chain(ZOMBIE, 10).
                     chain(ZOMBIE_BEAST, 6).
                     chain(GHAST_ZOMBIE, 6).
                     chain(GHOUL, 5);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(BONE_KNIGHT, 12).
                 chain(UNDEAD_BEAST, 6).
                 chain(GHOST, 6).
                 chain(DEATH_ADEPT, 11).
                 chain(WRAITH, 5).
                 chain(VAMPIRE, 5).
                 chain(CRIMSON_CHAMPION, 4).
                 chain(GHAST_WRAITH, 4).
                 chain(VAMPIRE_BEAST, 4)
                 : new WeightMap<String>().
                 chain(VAMPIRE_LORD, 10).
                 chain(DEATH_KNIGHT, 10).
                 chain(DEATH_LORD, 10).
                 chain(LICH, 10).
                 chain(VAMPIRE, 6).
                 chain(BONE_DRAGON, 8)
                 ;
            case UNDEAD_PLAGUE:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(PLAGUE_BEARER, 15).
                     chain(PLAGUE_ZOMBIE, 10).
                     chain(PLAGUE_SERVANT, 10).
                     chain(PLAGUE_RAT, 10).
                     chain(ZOMBIE, 7).
                     chain(CORPSE_SLUG, 6) .
                     chain(ZOMBIE_BEAST, 6);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(PLAGUE_BRINGER, 12).
                 chain(CORPSEBORN, 6) .
                 chain(GHOUL, 6) .
                 chain(GHAST_ZOMBIE, 6) .
                 chain(CORPSE_SLUG, 6) .
                 chain(UNDEAD_BEAST, 6)
                 : new WeightMap<String>().
                 chain(UNDEAD_MONSTROCITY, 10).
                 chain(DEATH_KNIGHT, 5).
                 chain(LICH, 5).
                 chain(BONE_DRAGON, 3)
                 ;
            case UNDEAD_CRIMSON:
                if (elite_boss_regular == null)
                return new WeightMap<String>().
                 chain(VAMPIRE_BAT, 20).
                 chain(BLOOD_GHAST, 15).
                 chain(GHAST_ZOMBIE, 6) .
                 chain(VAMPIRE_BEAST, 5).
                 chain(BLOODMAGE_APPRENTICE, 5).
                 chain(SKELETON_ARCHER, 10);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(BLOOD_REVENANT, 12).
                 chain(CRIMSON_CHAMPION, 12) .
                 chain(GHAST_ZOMBIE, 12) .
                 chain(CRIMSON_MASKED_CULTIST, 12).
                 chain(VAMPIRE_MISTRESS, 6).
                 chain(VAMPIRE_BEAST, 6).
                 chain(VAMPIRE, 6)
                 : new WeightMap<String>().
                 chain(DEATH_KNIGHT, 5).
                 chain(VAMPIRE_LORD, 10).
                 chain(VAMPIRE, 6)
                 ;
            case UNDEAD_WRAITH:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(SKELETON, 15).
                     chain(SKELETAL_BEAST, 10).
                     chain(WRAITH_WHISPERER, 10).
                     chain(SKELETON_ARCHER, 10).
                     chain(GHOST, 5);
                return elite_boss_regular ? new WeightMap<String>().
                 chain(BONE_KNIGHT, 12).
                 chain(REVENANT, 11).
                 chain(DEATH_ADEPT, 11).
                 chain(GHOST, 11).
                 chain(WRAITH, 12).
                 chain(UNDEAD_BEAST, 6).
                 chain(GHAST_WRAITH, 7)
                 : new WeightMap<String>().
                 chain(DEATH_LORD, 15).
                 chain(KING_OF_THE_DEAD, 10).
                 chain(DEATH_KNIGHT, 10).
                 chain(LICH, 10)
                 ;
            case DEMONS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(FAMILIAR, 10).
                     chain(IMP, 10).
                     chain(HELLGUARD, 10).
                     chain(DEMON_BRUTE, 10).
                     chain(DEMON_WORSHIPPER, 5).
                     chain(FIEND, 4);

                return elite_boss_regular ? new WeightMap<String>().

                 chain(DEMON_CARNIFEX, 10).
                 chain(DEMON_GORGER, 10).
                 chain(FIEND, 10).
                 chain(WARP_HUNTER, 10).
                 chain(INCUBUS, 10).
                 chain(SUCCUBUS, 10).
                 chain(DEMON_CALLER, 5).
                 chain(DEMON_TORMENTOR, 4).
                 chain(SCREAMER, 4).
                 chain(SATYR_DEMONCALLER, 4).
                 chain(HELLUNICODE39CODEENDS_TYRANT, 4)
                 : new WeightMap<String>().
                 chain(DEMON_LORD, 10).
                 chain(DEMON_PRINCE, 10).
                 chain(DEVIL_MAGE, 5).
                 chain(ABYSSAL_FIEND, 4).
                 chain(LORD_OF_DESPAIR, 4).
                 chain(LORD_OF_HATRED, 4).
                 chain(LORD_OF_PAIN, 4).
                 chain(LORD_OF_TERROR, 4)
                 ;
            case ANIMALS:
                break;
            case DEMONS_HELLFIRE: if (elite_boss_regular == null)
                return new WeightMap<String>().
                 chain(FAMILIAR, 7).
                 chain(IMP, 10).
                 chain(HELLGUARD, 10).
                 chain(DEMON_BRUTE, 10).
                 chain(DEMON_WORSHIPPER, 5).
                 chain(POSSESSED, 5).
                 chain(FIEND, 4);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(DEMON_CARNIFEX, 10).
                 chain(DEMON_GORGER, 5).
                 chain(FIEND, 10).
                 chain(DEVIL_MAGE, 4).
                 chain(INFERI_SORCEROR, 11).
                 chain(INFERNAL_GOLEM, 12)
                 : new WeightMap<String>().
                 chain(DEMON_LORD, 10).
                 chain(DEMON_PRINCE, 10).
                 chain(LORD_OF_HATRED, 4).
                 chain(LORD_OF_TERROR, 4)
                 ;
            case DEMONS_ABYSS:
                break;
            case DEMONS_WARPED:
                break;
            case CRITTERS_COLONY:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(COLONY_DRONE, 15).
                     chain(COLONY_HARVESTER, 10).
                     chain(COLONY_WARRIOR, 7).
                     chain(COLONY_OVERSEER, 4).
                     chain(HUSK, 6);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(COLONY_WARRIOR, 7).
                 chain(COLONY_OVERSEER, 4).
                 chain(COLONY_QUEEN, 2)
                 : new WeightMap<String>().
                 chain(COLONY_WARRIOR, 6).
                 chain(COLONY_OVERSEER, 6).
                 chain(COLONY_QUEEN, 10);

            case CRITTERS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(VAMPIRE_BAT, 12).
                     chain(NIGHT_BAT, 8).
                     chain(DEADLY_SPIDER, 7).
                     chain(RHINO_BEETLE, 7).
                     chain(STAG_BEETLE, 7).
                     chain(HARPY, 5).
                     chain(DRAKELING, 6);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(DRAKE, 14).
                 chain(BASILISK, 10).
                 chain(PALE_WEAVER, 7).
                 chain(BLACK_WIDOW, 5)
                 : new WeightMap<String>().
                 chain(MYCOSA, 6).
                 chain(HYDRA, 4).
                 chain(NIGHTMARE, 4);
            case CRITTERS_SPIDERS:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(SPIDERLING, 12).
                     chain(DEADLY_SPIDER, 7).
                     chain(SPIDERITE, 5).
                     chain(HUSK, 4);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(SPIDERITE, 14).
                 chain(DEADLY_SPIDER, 10).
                 chain(PALE_WEAVER, 7).
                 chain(BLACK_WIDOW, 5).
                 chain(SHADOW_WEAVER, 7)
                 : new WeightMap<String>().
                 chain(MYCOSA, 10).
                 chain(HYDRA, 4).
                 chain(CORRUPTED_MIND_FLAYER, 4);
            case DUNGEON:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(TROGLODYTE, 10).
                     chain(TROGLODYTE_MUTANT, 4).
                     chain(HARPY, 5).
                     chain(MINOTAUR, 4).
                     chain(EVIL_EYE, 6);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(MINOTAUR_PRAETOR, 10).
                 chain(MINOTAUR, 6).
                 chain(MIND_FLAYER, 5).
                 chain(CORRUPTED_MIND_FLAYER, 4).
                 chain(MANTICORE, 4).
                 chain(EVIL_EYE, 6)
                 : new WeightMap<String>().
                 chain(HYDRA, 10).
                 chain(MANTICORE, 10).
                 chain(MINOTAUR_PRAETOR, 4).
                 chain(BLACK_DRAGON, 5).
                 chain(CORRUPTED_MIND_FLAYER, 4);
            case FOREST:
                break;
            case REPTILES:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(BASILISK, 10).
                     chain(SLAAG, 10).
                     chain(DRAKELING, 14).
                     chain(DRAKE, 5) ;

                return elite_boss_regular ? new WeightMap<String>().
                 chain(BASILISK, 10).
                 chain(SLAAG, 10).
                 chain(WYVERN, 10).
                 chain(YOUNG_WYVERN, 15)
                 : new WeightMap<String>().
                 chain(HYDRA, 10).
                 chain(WYVERN, 10).
                 chain(ARMORED_WYVERN, 10).
                 chain(YOUNG_WYVERN, 5).
                 chain(BLACK_DRAGON, 5) ;
        }
        return new WeightMap<String>().
         chain(VENOMOUS_HAIRY_SPIDER, 10);
    }

    public static WeightMap<String> getWeightMap(UNIT_GROUP group, SPAWN_GROUP_TYPE groupType
     , boolean alt) {
        Boolean elite_boss_regular = null;
        switch (groupType) {
            case PATROL:
            case CROWD:
            case IDLERS:
                break;
            case STALKER:
            case GUARDS:
            case AMBUSH:
                elite_boss_regular = false;
                break;
            case BOSS:
                elite_boss_regular = true;
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
}
