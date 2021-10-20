package main.content.enums.entity;

import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.awt.*;

/**
 * Created by JustMe on 2/14/2017.
 */
public class ActionEnums {
    public static final String ATTACK = (STD_ACTIONS.Attack.toString());
    public static final String OFFHAND_ATTACK = (STD_ACTIONS.Offhand_Attack.toString());
    public static final String OFFHAND = "Off Hand ";
    public static final String RELOAD = "Reload";
    public static final String MOVE_BACK = "Move Back";
    public static final String ENTER = "Enter";
    public static final String DUMMY_ACTION = "Dummy Action";
    public static final String THROW = "Throw";

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

    public enum DEFAULT_ACTION {
        Wait, On_Alert, Concentrate, Examine, Rest, Defend,
    }
        public enum STD_ACTIONS {
        Attack, Offhand_Attack, Turn_Anticlockwise, Turn_Clockwise, Move;

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

    public enum STD_SPEC_ACTIONS {
        Search_Mode,
        Toggle_Weapon,
        Toggle_Offhand_Weapon,
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


    public enum MOD_IDENTIFIER {
        ATK_DEF,
        HIT_TYPE("ui/value icons/identifiers/CRIT.jpg"),
        ACTION,
        WEAPON,
        UNIT,
        EXTRA_ATTACK,
        RANDOM,
        AMMO,
        FORCE,

        POS("ui/value icons/identifiers/POS.png"),
        SNEAK("ui/value icons/identifiers/SNEAK.jpg"),
        CLOSE_QUARTERS("ui/value icons/identifiers/CLOSE_QUARTERS.jpg"),
        LONG_REACH("ui/value icons/identifiers/LONG_REACH.jpg"),
        DIAGONAL_ATTACK("ui/value icons/identifiers/DIAGONAL_ATTACK.png"),
        SIDE_ATTACK("ui/value icons/identifiers/SIDE_ATTACK.png"),

        ARMOR("ui/value icons/identifiers/ARMOR.jpg"),
        RESISTANCE("ui/value icons/identifiers/RESISTANCE.jpg"),
        DIE_SIZE("ui/value icons/identifiers/DIE_SIZE.png"),
        DIE_NUMBER("ui/value icons/identifiers/DIE_NUMBER.png"),
        DIE_RESULT,
        THROW(ImageManager.STD_IMAGES.THROW.getPath()),
        INSTANT_ATTACK(ImageManager.STD_IMAGES.INSTANT_ATTACK.getPath()),
        AOO(ImageManager.STD_IMAGES.ATTACK_OF_OPPORTUNITY.getPath()),
        COUNTER_ATTACK(ImageManager.STD_IMAGES.COUNTER_ATTACK.getPath()),
        DISENGAGEMENT,
        WATCHED(ImageManager.STD_IMAGES.EYE.getPath()),
        SIGHT_RANGE(ImageManager.STD_IMAGES.EYE.getPath());
        private String imagePath;

        MOD_IDENTIFIER() {

        }

        MOD_IDENTIFIER(String path) {
            this.imagePath = path;
        }

        public String getImagePath() {
            return imagePath;
        }

        public Image getImage(Object... values) {
            if (imagePath == null) {
                try {
                    //???
                    // return DC_ImageMaster.getImageDynamic(this, values);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    return ImageManager.getImage(ImageManager.getEmptyListIconSmall());
                }
            }
            return ImageManager.getImage(imagePath);
        }

        public String getName() {
            return StringMaster.format(name());
        }

    }
}
