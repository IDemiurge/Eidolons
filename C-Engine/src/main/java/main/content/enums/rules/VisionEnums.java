package main.content.enums.rules;

/**
 * Created by JustMe on 2/14/2017.
 */
public class VisionEnums {
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

    public enum VISIBILITY_STATUS {
        CONCEALED_KNOWN, CONCEALED_UNKNOWN, INVISIBLE,

    }

    public enum VISION_MODE {
        NORMAL_VISION, X_RAY_VISION, TRUE_SIGHT, WARP_SIGHT, INFRARED_VISION
    }
}
