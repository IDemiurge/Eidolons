package eidolons.system.audio;

import eidolons.entity.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Core;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.audio.MusicEnums.AMBIENCE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;

import static eidolons.system.audio.MusicEnums.AMBIENCE.*;

/**
 * Created by JustMe on 9/6/2018.
 * <p>
 * ambient tracks are looped per zone?
 */
public class AmbientMaster {

    public static final String FORMAT = ".mp3";
    private static final AMBIENCE DEFAULT_AMBIENCE = EVIL;

    public enum ATMO_SCOPE {
        MENU, HC, BATTLE
    }

    public enum ATMO_SOUND_TYPE {
        CREEK,
        WHISPER,
        WAVES,
        WIND_HOWL,
        WIND_BLOW,
        ROCK_FALL,
        BIRD_CHIRP,
        RIVER,
        WOLF_HOWL,
        DOG_BARK,
        HUMAN_CHATTER,
        HUMAN_WHISPER,
        PICK_AXE,

    }

    public static AMBIENCE getCurrentAmbience(boolean alt, boolean global) {
        if (!ExplorationMaster.isExplorationOn()) {
            return null;
        }
        Unit hero = Core.getMainHero();
        if (hero == null)
            return DEFAULT_AMBIENCE;

        LevelStruct lowestStruct = hero.getGame().getDungeonMaster().getStructMaster().
                getLowestStruct(hero.getCoordinates());
        if (lowestStruct.getAmbiData().getAmbience()!=null ) {
                return lowestStruct.getAmbiData().getAmbience();
        }
        LOCATION_TYPE locationType = hero.getGame().getDungeonMaster().getFloorWrapper()
                .getLocationType();
        if (locationType == null)
            return null;
        if (global) {
            switch (locationType) {
                case CEMETERY:
                    return alt ? MIST : HAUNTED;
                case CAVE:
                    return alt ? MINE : CAVE;
                case BARROW:
                case CRYPT:
                    return alt ? EVIL : HAUNTED;
                case DUNGEON:
                    return alt ? CAVE : MINE;

                case TEMPLE:
                case RUIN:
                    return alt ? MIST : EVIL;
                case TOWER:
                    return alt ? EVIL : MIST;
                case CASTLE:
                    return alt ? MIST : MINE;

                case SEWER:
                case GROVE:
                case HOUSE:
                case CAMP:
                case DEN:
                case ASTRAL:
                case HELL:
                    break;
            }
        }
        return DEFAULT_AMBIENCE;
    }
}
