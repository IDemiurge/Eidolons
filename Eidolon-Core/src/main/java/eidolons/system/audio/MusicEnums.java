package eidolons.system.audio;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

import static eidolons.system.audio.MusicEnums.MUSIC_TRACK.*;

public class MusicEnums {
    public static final String AMBIENT_FOLDER = "atmo";

    // SOUNDS VS THEME-FILES?!
    public enum AMBIENCE {
        MINE,
        SHIP,
        INTERIOR,
        MIST,
        HAUNTED,
        CAVE,
        EVIL,

        //        FOREST_NIGHT(),
        //        FOREST_DAY,
        //        TAVERN,
        //        TEMPLE,
        //        CASTLE,
        //        DUNGEON,
        //        MOUNTAINS,
        //        NORTH,
        TOWN();

        AMBIENCE(AmbientMaster.ATMO_SOUND_TYPE... TYPES) {

        }

        public String getPath() {
            return StrPathBuilder.build(PathFinder.getMusicPath(), AMBIENT_FOLDER, name() + AmbientMaster.FORMAT);
        }
    }

    public enum MUSIC_MOMENT {
        SAD,
        SELENE,
        HARVEST,
        SECRET,
        FALL,
        VICTORY,
        RISE,
        DEATH,
        TOWN,
        GAMEOVER,


        DANGER,
        WELCOME; // VARIANTS?

        public String getCorePath() {
            return PathFinder.getSoundPath() + "moments/" + name() + ".mp3";
        }
    }

    public enum MUSIC_SCOPE {
        MENU, ATMO, MAP, CINEMATIC, BATTLE
    }
    // FAR_BEYOND_FALLEN,
    // FRACTURES,
    // THERE_WILL_BE_PAIN_LOOP,
    // THE_END_OR_THE_BEGINNING,
    // OBSCURE_PATHS_LOOP,
    // FALLEN_REALMS,
    // FROM_DUSK_TILL_DAWN,
    // DARK_SECRETS,
    // DUNGEONS_OF_DOOM,
    // LOOMING_SHADES,
    //
    // ENTHRALLING_WOODS,
    // NIGHT_OF_DEMON,
    // TOWARDS_THE_UNKNOWN_LOOP,
    // PREPARE_FOR_WAR,
    // SUFFOCATION_LOOP,
    // BATTLE_INTRO_LOOP,
    // BATTLE_ALT,
    // BATTLE_LOOP,
    public enum MUSIC_TRACK {
        ATMO(MUSIC_SCOPE.CINEMATIC),
        ATMO_FIRE(MUSIC_SCOPE.CINEMATIC),
        FRACTURES(MUSIC_SCOPE.MENU),
        THERE_WILL_BE_PAIN_LOOP(MUSIC_SCOPE.MENU),
        THE_END_OR_THE_BEGINNING(MUSIC_SCOPE.MENU),
        FAR_BEYOND_FALLEN(MUSIC_SCOPE.MENU),

        OBSCURE_PATHS_LOOP,
        FALLEN_REALMS,
        FROM_DUSK_TILL_DAWN,
        DARK_SECRETS,
        DUNGEONS_OF_DOOM,
        LOOMING_SHADES,

        ENTHRALLING_WOODS(MUSIC_SCOPE.MAP),

        TOWARDS_THE_UNKNOWN_LOOP(MUSIC_SCOPE.BATTLE),
        PREPARE_FOR_WAR(MUSIC_SCOPE.BATTLE),
        NIGHT_OF_DEMON(MUSIC_SCOPE.BATTLE),
        SUFFOCATION_LOOP(MUSIC_SCOPE.BATTLE),
        BATTLE_INTRO_LOOP(MUSIC_SCOPE.BATTLE),
        BATTLE_ALT(MUSIC_SCOPE.BATTLE),
        BATTLE_LOOP(MUSIC_SCOPE.BATTLE),
        ;
        MUSIC_SCOPE scope;

        MUSIC_TRACK() {
        }

        MUSIC_TRACK(MUSIC_SCOPE scope) {
            this.scope = scope;
        }

        public String getPath() {
            if (scope == MUSIC_SCOPE.ATMO) {
                return "main/" + getName() + ".mp3";
            }
            return "main/" + scope.toString().toLowerCase() + "/" + getName() + ".mp3";
        }

        public String getName() {
            return StringMaster.format(name());
        }

        public String getFullPath() {
            return  PathFinder.getMusicPath() +"/" +  getPath();
        }
    }
    public enum MUSIC_THEME {
        evil( DUNGEONS_OF_DOOM, LOOMING_SHADES, FALLEN_REALMS),
        dungeon(LOOMING_SHADES, DARK_SECRETS, DUNGEONS_OF_DOOM),
        mist(OBSCURE_PATHS_LOOP, FAR_BEYOND_FALLEN, LOOMING_SHADES),
        vampire(FROM_DUSK_TILL_DAWN, FALLEN_REALMS, OBSCURE_PATHS_LOOP),
        ;

        MUSIC_THEME( MUSIC_TRACK... tracks) {
        }
    }

}
