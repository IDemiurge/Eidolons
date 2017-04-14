package main.content.enums.system;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class AiEnums {
    public enum AI_LOGIC { // targeting prioritizing? should be of various
        // types...
        DAMAGE,
        DAMAGE_ZONE,
        RESTORE,
        DEBILITATE,
        BUFF_POSITIVE,
        BUFF_NEGATIVE,
        SUMMON,
        CUSTOM_HOSTILE,
        CUSTOM_SUPPORT,
        MOVE,
        AUTO_DAMAGE,
        SELF,
        BUFF_POSITIVE_ZONE,
        BUFF_NEGATIVE_ZONE,
        DEBILITATE_ZONE,
        RESTORE_ZONE,
        COATING,
        OTHER

    }

    public enum AI_TYPE {
        NORMAL,
        BRUTE,
        SNEAK,
        TANK,
        CASTER_MELEE,
        CASTER(true),
        CASTER_SUPPORT(true),
        CASTER_SUMMONER(true),
        CASTER_OFFENSE(true),
        ARCHER(true);

        private boolean ranged;

        AI_TYPE(boolean ranged) {
            this.ranged = ranged;
        }

        AI_TYPE() {
        }

        public boolean isRanged() {
            return ranged;
        }

        public void setRanged(boolean ranged) {
            this.ranged = ranged;
        }
    }

    public enum BEHAVIOR_MODE
     // implements MODE
    {
        PANIC, CONFUSED, BERSERK {
        public boolean isDisableCounters() {
            return false;
        }
    };

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        public boolean isDisableCounters() {
            return true;
        }
    }

    public static enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum PLAYER_AI_TYPE {
        BRUTE, SNEAK, DEFENSIVE, MAD, SMART, NORMAL
    }
}
