package main.content.enums.system;

import main.content.mode.STD_MODES;
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
        BRAWLER,
        ASSASSIN,
        GUARD,

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

        public boolean isCaster() {
            if (this == ARCHER)
                return false;
            return isRanged();
        }
    }

    public enum BEHAVIOR_MODE
     // implements MODE
    {
        PANIC(STD_MODES.PANIC),
        CONFUSED(STD_MODES.CONFUSED),
        BERSERK(STD_MODES.BERSERK) {
        public boolean isDisableCounters() {
            return false;
        }
    };

        BEHAVIOR_MODE(STD_MODES mode) {
            this.mode = mode;
        }

        public STD_MODES mode;

        public String getName() {
            return StringMaster.format(name());
        }

        public boolean isDisableCounters() {
            return true;
        }
    }

    public enum CHARACTER_TYPE {
        PROTECTOR,
        ASSASSIN,
        LEADER,
        ARCHER,
        SUPPORT,
    }

    public enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    // AI_TYPE can influence preferred goals
    // getting task arguments based on GOAL TYPE???
    public enum GOAL_TYPE {
        ATTACK, // ALL HOSTILE GOALS
        APPROACH,
        BUFF, // ALL ALLIES
        SELF, // ALL non-std SELFIES
        DEBUFF,
        RESTORE,
        DEBILITATE,

        SUMMONING,
        MOVE, // STD AND CUSTOM MOVE ACTIONS
        WAIT, // on allies or enemies!
        PREPARE, // BUFFS, MODES
        DEFEND, // ALERT OR DEFEND
        PROTECT,
        RETREAT, // USUALLY FORCED
        SEARCH, // IF NO ENEMIES DETECTED, LOOK AROUND

        ZONE_SPECIAL,
        AUTO_DAMAGE,
        AUTO_BUFF,
        AUTO_DEBUFF,
        OTHER,
        ZONE_DAMAGE,
        CUSTOM_HOSTILE,
        CUSTOM_SUPPORT,
        STEALTH,
        COWER,
        COATING,

        AMBUSH(true), // SPEC MODE - KIND OF ON ALERT...
        WANDER(true), // RANDOM DESTINATION MOVEMENT, BLOCK SPECIAL MOVES
        STALK(true),
        AGGRO(true),
        PATROL(true),
        STAND_GUARD(true),
        IDLE(true),;
        private boolean behavior;

        GOAL_TYPE() {

        }

        GOAL_TYPE(boolean b) {
            behavior = b;
        }

        public boolean isFilterByCanActivate() {
            return !isBehavior();
        } // FOLLOW AT SAFE DISTANCE

        public boolean isBehavior() {
            return behavior;
        }

    }

    public enum IMPULSE_TYPE {
        IMPULSES, VENGEANCE, HATRED, FEAR, GREED, CURIOSITY, PROTECTIVENESS
    }

    public enum INCLINATION_TYPE {
        DEFENSE,
        ASSASSINATION,
        BRAWL,
        SUPPORT,
        CAUTION,
    }

    public enum META_GOAL_TYPE {
        PROTECT,
        AVENGE,
        AVOID,
        AID,
        BRAWL,
        ASSASSINATE,
    }

    public enum ORDER_PRIORITY_MODS {
        ATTACK(GOAL_TYPE.ATTACK),
        RETREAT(GOAL_TYPE.RETREAT),
        RESTORE(GOAL_TYPE.RESTORE),
        WAIT(GOAL_TYPE.WAIT),
        PREPARE(GOAL_TYPE.PREPARE),
        DEFEND(GOAL_TYPE.DEFEND),
        SEARCH(GOAL_TYPE.SEARCH),


        APPROACH(),
        MOVE(),
        STEALTH(),
        GUARD(),;
        GOAL_TYPE[] goalTypes;

        ORDER_PRIORITY_MODS(GOAL_TYPE... goalTypes) {
            this.goalTypes = goalTypes;
        }

        public GOAL_TYPE[] getGoalTypes() {
            return goalTypes;
        }
    }

    public enum PLAYER_AI_TYPE {
        BRUTE, SNEAK, DEFENSIVE, MAD, SMART, NORMAL
    }
}
