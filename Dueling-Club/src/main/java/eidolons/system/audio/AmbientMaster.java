package eidolons.system.audio;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;

import static eidolons.system.audio.MusicMaster.AMBIENCE.*;

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
//        if (!ExplorationMaster.isExplorationOn()){
//            return null;
//        }
        Unit hero = Eidolons.getMainHero();
        if (hero == null)
            return DEFAULT_AMBIENCE;
        if (hero.getGame().getDungeonMaster().getDungeonLevel() == null)
            return DEFAULT_AMBIENCE;

        if (TownPanel.getActiveInstance() != null && Eidolons.getGame().getMetaMaster().getTownMaster().isInTown()) {
            return Eidolons.getGame().getMetaMaster().getTownMaster().getTown().getAmbience();
        }
        DUNGEON_STYLE style;
        LOCATION_TYPE locationType;
            try {
                locationType = hero.getGame().getDungeonMaster().getDungeonLevel()
                        .getLocationType();
                style = hero.getGame().getDungeonMaster().getDungeonLevel().
                        getBlockForCoordinate(hero.getCoordinates()).getStyle();
            } catch (Exception e) {
                style = hero.getGame().getDungeonMaster().getDungeonLevel().getMainStyle();
//            style = hero.getGame().getDungeon().getStyle();
                locationType = hero.getGame().getDungeon().getDungeonSubtype();
            }
        if (locationType == null) {
            return null;
        }

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
                    return alt ? MIST : EVIL;
                case TOWER:
                    return alt ? EVIL : MIST;
                case RUIN:
                    return alt ? MIST : EVIL;
                case CASTLE:
                    return alt ? MIST : MINE;

                case SEWER:
                    break;
                case HELL:
                    break;
                case ASTRAL:
                    break;
                case DEN:
                    break;
                case CAMP:
                    break;
                case HOUSE:
                    break;
                case GROVE:
                    break;
            }
        } else
            switch (style) {
                //TODO
            }
        return MIST;
    }
}
