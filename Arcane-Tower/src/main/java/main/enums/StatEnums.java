package main.enums;

public class StatEnums {
    public enum WORK_DIRECTION {
        HC,
        AV,
        DC,
        RPG,
        CONTENT,
        SYSTEMS,
        VISUALS,
        USABILITY,
        WRITING,
        META_GAME,
        PROJECT,
        MANAGEMENT,
    }

    public enum TASK_STATUS {
        PINNED, DONE, PROTOTYPED, ACTIVE, PENDING, IDEA, BLOCKED, FAILED,
    }

    public enum SESSION_STATUS {
        DONE, ACTIVE, PENDING, UNFINISHED, PAUSED,

    }

    public enum TASK_TYPE {
        NEW_FEATURE("[+]"),
        FIX("[!]"),
        INVESTIGATION("[?]"),
        TEST("[>]"),
        UPGRADE("[$]"),
        DESIGN("[@]"),
        ADDITIONAL("[�]"),
        RECURRING("[-]"),;
        public String prefix;

        TASK_TYPE(String prefix) {
            this.prefix = prefix;
        }

    }

    // APPLY_CASES,
    // REMIND_CASES,
    public enum PROJECT_AREA {
        GAMEPLAY, VISUALS, LOGIC, BALANCE, USABILITY,
    }

    // SEVERITY,
    // COMPLEXITY,
    public enum PROBLEM_TYPE {
        GLITCH, MYSTERY_BUG, CLEAR_BUG,
    }

    public enum WORK_TYPE {
        CAPTURE, OUTLINE, CODE, TEST, FIX,
    }

    public enum PRIORITY {
        TOP, HIGH, AVERAGE, LOW, ALT
    }

    public enum RATE_TYPE {
        MAIN_PROGRESS, ALT_PROGRESS, EXPERIENCE,

    }

    public enum PROMPT_TYPE {
        OUTCOME, NEXT, KEYNOTE,

    }

    public enum WORK_STYLE {
        CONTINUOUS, SNACKING, DINING, INTENSE, RACING, HARD,
    }

    public enum STATE {
        HIGH, LOW, BALANCED, DEEP, SHARP, HEAVY, DRIVEN,
    }

    public enum DEV_CYCLE {
        PREPARATION, BATTLE, AFTERMATH,
    }

    public enum PERIOD {
        MORNING, MIDDAY, AFTERNOON, DUSK, NIGHT
    }

    public enum MUSIC_GENRE {
        EPIC, RPG, FANTASY, MOVIE_OST, SONG, AMBIENT, CLASSIC, ELECTRO,
    }

    public enum MUSIC_TAGS {
        DARK, HEAVY, ACTION, NORTH, ARCANE, TRAURIG, RPG, CELTIC, FANTASY,

        COOL, DEEP, JOLLY, GOODLY, SACRED, EVIL, FIERY, SHARP, LIGHT, FUTURE,

        BEST, DAY, EVENING, NIGHT, AMBIENT,

        OLD, FINEST, NEW,
    }

    public enum MUSIC_TAG_GROUPS {
        RPG_DAY, GOODLY_SACRED_LIGHT,
        CELTIC_FANTASY, DARK_TRAURIG, NORTH,
        EVIL_FIERY, ARCANE_EVENING, NIGHT_AMBIENT,
        DEEP, SHARP_FUTURE_COOL_JOLLY_NEW, LIGHT_SACRED , FINEST_OLD_BEST
        //HEAVY_ACTION

    }
//		public enum MUSIC_TAG_GROUPS_OLD {
//		RPG_FANTASY, DARK_TRAURIG, GOODLY_SACRED_LIGHT,
//		HEAVY_ACTION, ARCANE_DEEP, EVIL_FIERY,
//		CELTIC_NORTH, COOL_JOLLY, SHARP_FUTURE,
//		BEST_EVENING,
//		DAY,
//		NIGHT_AMBIENT,
//	}

    public enum MUSIC_TYPE {
        //+1 for win+f1 help
        GYM_MORNING,
        GYM_NIGHT,
        JOG,

        BATTLE,
        PREPARATION,
        IMMERSION,

        RUMINATION,
        AFTERMATH,
        WALK,

        AWAKENING,
        GYM,
    }

}
