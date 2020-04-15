package eidolons.game.battlecraft.logic.meta.custom;

public class QD_Enums {
/*
approx. list of modules I'd want
 */

    public enum DungeonProperty {
        location, tags, elevation, length, preset_floors
    }
        public enum FloorProperty {
        type, location, tags, elevation, length, preset_modules,
        //for preset modules and as requirements! / generated
    }

    public enum ModuleProperty {
        size, dimension, type, location, elevation,

    }

    public enum ModuleTags {
        puzzle, treasure,
    }

    public enum ModuleType {
        boss, explore, normal, hard

    }

    public enum ModuleSize {
        tiny, small, medium, large, xl
    }

    public enum ElevationLevel {
        nether, deep_dungeon, dungeon, surface, tower, aether
    }

    public enum EntranceType {
        portal, tunnel, gate, path, stairs
    }

    ////////////////////////
    public interface QuestDungeonEnum {
        default String getTooltip() {
            return toString();
        }

        default QuestDungeonEnum[] getProhibitedByThis() {
            return null;
        }

    }

    public enum QD_LOCATION implements QuestDungeonEnum {
        evil_twins,
        bastion,
        sealed_kingdom,
        monastery,
        crusader_dungeon,
        blightstone,

    }

    /*
        SUB_QUEST,
        EVENTS,
     */
    public enum QD_QUEST implements QuestDungeonEnum {
        dark_relic, runes, ritual, rebels, magi, fae_drug,
        // additional


        ;
        String floorRequired;
        String moduleRequired;

    }

    public enum QD_LENGTH implements QuestDungeonEnum {
        blitz, normal, hardy, grueling,
        ;
    }

    public enum QD_DIFFICULTY implements QuestDungeonEnum {
        initiate,
        ;
    }

    public enum QD_MODE implements QuestDungeonEnum {
        no_return,
        ;
    }

    public enum QD_BOSS implements QuestDungeonEnum {

    }

    public enum QD_EIDOLON_LORD implements QuestDungeonEnum {
        keserim, morkei, akalath, kynariel, azathoth
    }
}
