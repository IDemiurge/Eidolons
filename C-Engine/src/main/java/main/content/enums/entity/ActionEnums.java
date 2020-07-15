package main.content.enums.entity;

import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class ActionEnums {
    public static final String OFFHAND_ATTACK = StringMaster
            .format(STD_SPEC_ACTIONS.OFFHAND_ATTACK.name());
    public static final String ATTACK = (STD_ACTIONS.Attack.name());
    public static final String OFFHAND = "Off Hand ";
    public static final String RELOAD = "Reload";
    public static final String THROW = "Throw";
    public static final String CLUMSY_LEAP = "Clumsy Leap";
    public static final String MOVE_RIGHT = "Move Right";
    public static final String MOVE_LEFT = "Move Left";
    public static final String MOVE_BACK = "Move Back";
    public static final String FLEE = "Flee";
    public static final String TOGGLE_WEAPON_SET = "Toggle Weapon Set";
    public static final String THROW_MAIN = "Throw Main Hand Weapon";
    public static final String THROW_OFFHAND = "Throw Off Hand Weapon";
    public static final String TOSS_ITEM = "Toss Item";
    public static final String ENTER = "Enter";
    public static final String DISARM = "Disarm";
    public static final String UNLOCK = "Unlock";
    public static final String DUMMY_ACTION = "Dummy Action";
    public static final String USE_INVENTORY = StringMaster
            .format(STD_SPEC_ACTIONS.Use_Inventory.toString());
    public static final String DIVINATION = "Divination";
    public static final String PICK_UP = "Pick Up Items";
    public static ObjType DUMMY_ACTION_TYPE;

    public enum ACTION_TAGS {
        STANDARD_ATTACK,
        POWER_ATTACK,
        QUICK_ATTACK,
        SPECIAL_ATTACK,
        FIXED_COST, COMBAT_ONLY,
        FLYING,
        DUAL,
        UNARMED,
        OFF_HAND,
        MAIN_HAND,
        TWO_HANDED,
        TWO_HANDS,
        RANGED_TOUCH,
        ATTACK_OF_OPPORTUNITY,
        ATTACK_OF_OPPORTUNITY_ACTION,
        INSTANT,
        RANGED,
        THROW,
        MISSILE,
        INSTANT_ATTACK,
        RESTORATION, TOP_DOWN;

        private String string;

        public String toString() {
            if (string == null)
                string = StringMaster.format(name());
            return string;
        }

    }

    public enum ACTION_TYPE {
        STANDARD,
        MODE,
        SPECIAL_MOVE,
        STANDARD_ATTACK,
        SPECIAL_ATTACK,
        SPECIAL_ACTION,
        HIDDEN,
        ADDITIONAL_MOVE,
    }

    public enum ACTION_TYPE_GROUPS {
        MOVE, TURN, ATTACK, SPELL, ADDITIONAL, SPECIAL, HIDDEN, MODE, ITEM, ORDER,
        DUNGEON, // security fix...
        STANDARD

    }

    public enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum STANDARD_ACTION_PASSIVES {
        AGILE, DEXTEROUS,
    }

    public enum ATTACK_TYPE {
        STANDARD_ATTACK,
        QUICK_ATTACK,
        POWER_ATTACK,
        SPECIAL_ATTACK,

    }

    public enum ADDITIONAL_MOVE_ACTIONS {
        MOVE_LEFT, MOVE_RIGHT, MOVE_BACK, CLUMSY_LEAP;

        public String toString() {
            return StringMaster.format(name());
        }
    }

    public enum HIDDEN_ACTIONS {
        Cower_In_Terror,
        Helpless_Rage,
        Idle,
        Stumble_About;

        public String toString() {
            return StringMaster.format(name());
        }
    }

    public enum STD_ACTIONS {
        Attack, Turn_Anticlockwise, Turn_Clockwise, Move;

        public String toString() {
            return StringMaster.format(name());
        }
    }

    public enum STD_MODE_ACTIONS {
        Defend, Camp, Concentrate, Rest, On_Alert;

        public String toString() {
            return StringMaster.format(name());
        }
    }

    public enum STD_ORDER_ACTIONS {
        Press_the_Attack,
        Hold_Fast,
        Protect_me,
        Heal_me,
        Kill_Him,
        Retreat,
        Cancel_Order {
            @Override
            public String toString() {
                return StringMaster.format(name());
            }
        },
        ;

        public String toString() {
            return "Order: " + StringMaster.format(name() +
                    "!");
        }

    }

    public enum STD_SPEC_ACTIONS {
        On_Alert, Use_Inventory, OFFHAND_ATTACK, DUAL_ATTACK, Search_Mode, Guard_Mode, Watch, Wait, Toggle_Weapon_Set, Push, Pull,
        //        @Override
        //        public String toString() {
        //            return StringMaster.getWellFormattedString(name());
        //        }
    }

    public enum WEAPON_ATTACKS {
        Twohanded_Blade_Thrust,
        Twohanded_Sword_Swing,

        Sword_Swing,
        Blade_Thrust,
        Slash,
        Stab,

        Axe_Swing,
        Chop,
        Hack,
        Hook,
        Spike_Stab,

        Twohanded_Axe_Sweep,

        Heavy_Swing,
        Head_Smash,
        Slam,
        Chain_Thrust,

        Hilt_Smash,
        Shield_Push,

        Spear_Poke,
        Impale,

        Pole_Push,
        Pole_Smash,
        Pole_Thrust,
        Shield_Bash,
        Slice,
        Kick,
        Rip,
        Punch,
        Elbow_Smash,
        Fist_Swing,
        Nail_Swipe,
        Leg_Push,
        Arm_Push,
        Bite,
        Dig_Into,
        Tear,

        Tail_Smash,
        Tail_Sting,
        Pierce,

        Implode,
        Hoof_Slam,

        Force_Push,
        force_touch,
        force_blast,
        force_ray;

        public String toString() {
            return StringMaster.format(name());
        }

    }

    public enum MISC_ACTIONS {
        Cower,
        stumble

    }
}
