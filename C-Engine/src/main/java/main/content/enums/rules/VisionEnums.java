package main.content.enums.rules;

import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.awt.*;

/**
 * Created by JustMe on 2/14/2017.
 */
public class VisionEnums {

    public enum IDENTIFICATION_LEVEL {

        SHAPE, // size and 'shape' (monster, humanoid, strange shape, animal,
        // avian...)
        TYPE, // classifications
        GROUP, // lesser demon, ++ race
        UNIT, DETAILED, // special for rangers? All value pages available... Can
        // be a
        // spell too!
        ;
    }

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
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum OUTLINE_TYPE {
        BLINDING_LIGHT, VAGUE_LIGHT, THICK_DARKNESS, VAGUE_OUTLINE, DARK_OUTLINE,
        BRILLIANT_OUTLINE, CLEAR_OUTLINE, MASS_OUTLINE, BLOCKED_OUTLINE, FLAT_OUTLINE,
        OUT_OF_RANGE {
            @Override
            public String getImagePath() {
                return THICK_DARKNESS.toString();
            }
        };

        String outlinePath = "ui\\outlines\\" + toString();
        String path;
        private Image image;

        OUTLINE_TYPE() {

        }

        public String getName() {
            return StringMaster.getWellFormattedString(toString());
        }

        // hasSelectImage(){
        //
        // }
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
            String outlinePath = "ui\\outlines\\raw\\" + toString();
            return ImageManager.getImage(outlinePath + ".jpg");
        }

        public String getImagePath() {
            return toString();
        }

    }

    public enum UNIT_TO_PLAYER_VISION {
        DETECTED, KNOWN, // CURRENTLY UNDETECTED
        UNKNOWN,
        CONCEALED,
        INVISIBLE,
        INVISIBLE_ALLY
    }

    public enum UNIT_TO_UNIT_VISION {
        IN_PLAIN_SIGHT, IN_SIGHT, BEYOND_SIGHT, CONCEALED;

        public boolean isSufficient(UNIT_TO_UNIT_VISION u_vision) {
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
        private int illuminationBarrier;

        VISIBILITY_LEVEL() {

        }
        // VISIBILITY_LEVEL(int illuminationBarrier) {
        // this.setIlluminationBarrier(illuminationBarrier);
        // }
        //
        // public int getIlluminationBarrier() {
        // return illuminationBarrier;
        // }
        //
        // public void setIlluminationBarrier(int illuminationBarrier) {
        // this.illuminationBarrier = illuminationBarrier;
        // }
    }

    public enum VISIBILITY_STATUS {
        CONCEALED_KNOWN, CONCEALED_UNKNOWN, INVISIBLE,

    }

    public enum VISION_MODE {
        NORMAL_VISION, X_RAY_VISION, TRUE_SIGHT, WARP_SIGHT, INFRARED_VISION
    }
}
