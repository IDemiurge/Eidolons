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
            case Castle:
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
            case Survivor:
                break;
            case Grimy:
                return underground ? new WeightMap<String>()
                 .chain(UNIT_GROUP.DUNGEON, 10)
                 .chain(UNIT_GROUP.DWARVES, 10)
                 .chain(UNIT_GROUP.UNDEAD, 10)
                 : new WeightMap<String>()
                 .chain(UNIT_GROUP.BANDITS, 10)
                 .chain(UNIT_GROUP.DWARVES, 10)
                 .chain(UNIT_GROUP.UNDEAD_CRIMSON, 10)
                 .chain(UNIT_GROUP.UNDEAD_PLAGUE, 10);
            case Somber:
                return new WeightMap<String>()
                 .chain(UNIT_GROUP.BANDITS, 10)
                 .chain(UNIT_GROUP.UNDEAD, 10)
                 .chain(UNIT_GROUP.Ravenguard, 10);
            case Arcane:
                break;
            case Cold:
                break;
        }
        return new WeightMap<String>().chain(UNIT_GROUP.BANDITS, 10);

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
                break;
            case DEATH_CULT:
                break;
            case DARK_CULT:
                break;
            case CHAOS_CULT:
                break;
            case CRUSADERS:
                break;
            case CONSTRUCTS:
                break;
            case DARK_ONES:
                break;
            case MUTANTS:
                break;
            case PIRATES:
                break;
            case CELESTIALS:
                break;
            case BARBARIANS:
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

            case KNIGHTS:
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
            case GREENSKINS:
                break;
            case BANDITS:
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
            case UNDEAD:
                if (elite_boss_regular == null)
                    return new WeightMap<String>().
                     chain(SKELETON, 15).
                     chain(SKELETAL_BEAST, 10).
                     chain(SKELETON_ARCHER, 10).
                     chain(ZOMBIE, 10).
                     chain(ZOMBIE_BEAST, 6).
                     chain(GHAST_ZOMBIE, 6).
                     chain(GHOUL, 5);

                return elite_boss_regular ? new WeightMap<String>().
                 chain(BONE_KNIGHT, 12).
                 chain(UNDEAD_BEAST, 6).
                 chain(GHOST, 6).
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
                //                     chain(PLAGUE_ZOMBIE, 10).
                //                     chain(PLAGUE_BEARER, 10).
                //                     chain(PLAGUE_RAT, 10).
                //                     chain(PLAGUE_ZOMBIE, 10).
                break;
            case UNDEAD_CRIMSON:
                break;
            case UNDEAD_WRAITH:
                break;
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
            case MAGI:
                break;
            case CRITTERS:
                break;
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
