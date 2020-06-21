package main.content.enums.rules;

import main.data.filesys.PathFinder;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.awt.*;

/**
 * Created by JustMe on 2/14/2017.
 */
public class VisionEnums {

    public enum INFO_LEVEL {
        MINIMAL,
        BASIC,
        NORMAL,
        VERBOSE,
    }

    public enum OUTLINE_HINT {
        SMALL, HUGE, SHORT, TALL, BROAD, INSECTOID, ANIMAL_LIKE,
        // "Something huge" "broadly" "insectoid" "animal-like"
        MONSTROUS, HUMANOID, HUMAN_LIKE, MONSTROUS_HUMANOID, FOUR_LEGGED, SOLID, IMMATERIAL, BESTIAL, ABHORRENT, OUTLINE_HINT() {

        }
    }

    public enum OUTLINE_IMAGE {
        MONSTROUS, HUMANOID, HUMAN, MONSTROUS_HUMANOID, HUMANLIKE, QUADRUPED, STRUCTURE, BEAST, INSECT,

        MULTIPLE,
        // 'certainty factor' - certain vs uncertain?
        // "four legged" "seemingly humanlike" "humanoid in shape"
        // "monstrious in shape"
        HORROR, WALL, UNKNOWN;

        OUTLINE_IMAGE() {
        }

        @Override
        public String toString() {
            return StringMaster.format(name());
        }
    }

    public enum OUTLINE_TYPE {
        CLEAR, BLINDING_LIGHT, VAGUE_LIGHT, DEEPER_DARKNESS, VAGUE_OUTLINE, DARK_OUTLINE,
        BRILLIANT_OUTLINE, CLEAR_OUTLINE, MASS_OUTLINE, BLOCKED_OUTLINE, FLAT_OUTLINE,
        UNKNOWN;

        String outlinePath = PathFinder.getOutlinesPath() + toString()+".jpg";
        String path;
        private Image image;

        OUTLINE_TYPE() {

        }

        public String getName() {
            return StringMaster.format(toString());
        }

        public Image getImage() {
            if (image == null) {
                image = ImageManager.getImage(outlinePath + ".jpg");
            }
            if (image == null) {
                return DARK_OUTLINE.getImage();
            }
            return image;

        }

        public Image getImage128() {
            String outlinePath = "ui/outlines/raw/" + toString();
            return ImageManager.getImage(outlinePath + ".jpg");
        }

        public String getImagePath() {
            return outlinePath;
        }

    }

    public enum PLAYER_VISION {
        DETECTED, KNOWN, // CURRENTLY UNDETECTED
        UNKNOWN,
        CONCEALED,
        INVISIBLE,
        INVISIBLE_ALLY;

        public boolean isGreater(PLAYER_VISION oldVision) {
            if (oldVision==null) {
                return true;
            }
            return EnumMaster.getEnumConstIndex(PLAYER_VISION.class , this)<
                    EnumMaster.getEnumConstIndex(PLAYER_VISION.class , oldVision);
        }
    }

    public enum UNIT_VISION {
        IN_PLAIN_SIGHT, IN_SIGHT, BEYOND_SIGHT,
        BLOCKED,

        CONCEALED;

        public boolean isSufficient(UNIT_VISION u_vision) {
            if (u_vision == BEYOND_SIGHT) {
                if (this == CONCEALED) {
                    return true;
                }
            }
            if (u_vision == IN_SIGHT) {
                if (this == IN_PLAIN_SIGHT) {
                    return true;
                }
            }
            return this == u_vision;
        }

    }

    public enum VISIBILITY_LEVEL {
        // Distance based - Outline?
        CLEAR_SIGHT(), OUTLINE(), VAGUE_OUTLINE(), CONCEALED(), BLOCKED, UNSEEN;
        // for info-panel, objComp... as an addition to UNIT_VISIBILITY?
        // what is the default? is this linked to Perception?
        // DETECTED vs KNOWN
        // IN_SIGHT vs BEYOND_SIGHT

        VISIBILITY_LEVEL() {

        }
    }


    public enum VISION_MODE {
        NORMAL_VISION, X_RAY_VISION, TRUE_SIGHT, WARP_SIGHT, INFRARED_VISION
    }

    public enum PLAYER_STATUS {
        EXPLORATION_UNDETECTED,
        EXPLORATION_DETECTED,
        PUZZLE,
        ALERTED,
        COMBAT,
        SHADOW,
        DEAD //GHOST!

    }

    public enum ENGAGEMENT_LEVEL {

        UNSUSPECTING, // will use its behavior and rest actions
        SUSPECTING, // will not Rest or otherwise let down their guard
        ALERTED // will search, ambush or stalk
        ,
        ENGAGED, // will engage and make combat-actions
        STALKING, //has visual on hero while unseen and seeks to attack
        AMBUSH, PRE_COMBAT, // ... remains still until can attack or close enough
    }
}
