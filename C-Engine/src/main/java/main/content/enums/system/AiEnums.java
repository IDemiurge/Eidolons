package main.content.enums.system;

import main.content.mode.STD_MODES;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class AiEnums {

    public enum AI_LOGIC_CASE {
        RELOAD, RESTORE, APPROACH, FAR_UNSEEN
    }

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


    // AI_TYPE can influence preferred goals
    // getting src.main.framework.task arguments based on GOAL TYPE???
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

    public enum GROUP_AI_TYPE {
        BRUTE, SNEAK, DEFENSIVE, MAD, SMART, NORMAL
    }

    public enum IMPULSE_TYPE {
        HATRED, //unlimited aggro, plus secondary berserk mode (attacks other things if PC is being respawned or hidden)
        VENGEANCE, // aggro until kills/hits hard
        FINISH, // aggro until kills
        PROTECTION, // won't leave an ally's side
        BULLY_CHASE, // aggro until gets seriously hit or sees an ally fall
        ROOTED , // won’t move
        HESITANCE , // won’t move until an ally lands a hit
        PANIC , // will try to put space between itself and you
        MADNESS
    }

    public enum ai_parameter {
        aggression, //
        cunning, // ?
        restlessness, // high means AI wants to change strategy even if it's good for the moment as is
        intelligence, // core parameter for rational optimization, the higher - the closer to ideal AI
        courage, // there will be some internal checks when need to atk or gets a blow / ally-kill
        teamwork, // switch strategy together with leader/team or not
        empathy //will they help allies? will they hit downed enemies?
    }

    public enum ai_template {
        brute(100, 80, 25, 80, 35, 10),
        warrior(70, 70, 45, 70, 45, 30),
        brawler(60, 80, 35, 80, 15, 50),
        natural(50, 50, 35, 50, 35, 35),
        predator(70, 40, 45, 70, 15, 10),
        sneak(60, 40, 65, 30, 45, 20),
        professional(50, 15, 75, 65, 45, 15),
        soldier(70, 40, 25, 50, 75, 30),
        support(20, 20, 65, 30, 100, 60),
        mastermind(40, 50, 100, 40, 65, 20),
        sissy(10, 30, 50, 20, 75, 100),
        ;

        ai_template(float aggression, float restlessness, float intelligence, float courage, float teamwork, float empathy) {
            this.aggression = aggression;
            this.restlessness = restlessness;
            this.intelligence = intelligence;
            this.courage = courage;
            this.teamwork = teamwork;
            this.empathy = empathy;
        }

        float aggression, restlessness, intelligence, courage, teamwork, empathy;
    }

    public enum ai_strategy {
        Berserk,
        Offense,
        Engage,
        Defense,
        Retreat,
        Flight,
        Panic
    }

    public enum TOTAL_PRIORITY {
        no_allies,
        never,
        always,
    }

    public     enum AI_EFFECT_PRIORITIZING {
        ATTACK, DAMAGE, BUFF, PARAM_MOD, COUNTER_MOD, SUMMON, MODE, BEHAVIOR_MODE,
    }

    public     enum PRIORITY_FUNCS {
        NO_ALLIES,
        NEVER,
        ALWAYS,

        DURATION, DANGER, CAPACITY, DANGER_TO_ALLY,
    }
}
