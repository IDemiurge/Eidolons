package eidolons.game.netherflame.main.story.brief;

import main.system.auxiliary.StringMaster;

public class BriefMusic {

    /**
     * override default track choice?
     * <p>
     * set pref for music master?
     * <p>
     * have multiple tracks in memory - fade between them?
     */

//    public enum MUSIC_MOMENT {
//        SAD,
//        DANGER,
//        VICTORY,
//        DISCOVERY,
//        ;
//
//    }

    public enum MUSIC_TRACK {
        FAR_BEYOND_FALLEN,
        FRACTURES,
        THERE_WILL_BE_PAIN_LOOP,
        THE_END_OR_THE_BEGINNING,
        OBSCURE_PATHS_LOOP,
        FALLEN_REALMS,


        FROM_DUSK_TILL_DAWN,
        DARK_SECRETS,
        DUNGEONS_OF_DOOM,
        LOOMING_SHADES,

        ENTHRALLING_WOODS,

        NIGHT_OF_DEMON,
        TOWARDS_THE_UNKNOWN_LOOP,
        PREPARE_FOR_WAR,
        SUFFOCATION_LOOP,
        BATTLE_INTRO_LOOP,
        BATTLE_ALT,
        BATTLE_LOOP,
        ;

        public String getPath() {
            return StringMaster.format(name());
        }
    }
}
