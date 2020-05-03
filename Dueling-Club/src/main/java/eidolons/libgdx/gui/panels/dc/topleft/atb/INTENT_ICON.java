package eidolons.libgdx.gui.panels.dc.topleft.atb;

import main.content.enums.GenericEnums;
import main.content.mode.MODE;
import main.content.mode.STD_MODES;

public enum INTENT_ICON {
    ATTACK,
    MOVE("attack"),
    OTHER("wheel"),
    SPELL("eye"),
    DEBUFF("eye"),
    BUFF("eye"),
    HOSTILE_SPELL("eye"),
    CHANNELING(GenericEnums.BLENDING.SCREEN),
    UNKNOWN,

    PREPARE(GenericEnums.BLENDING.SCREEN),
    WAIT,
    DEFEND,
    SEARCH,

    WHEEL,

    ;
    GenericEnums.BLENDING blending;
    protected String path;

    INTENT_ICON() {
        path = toString().toLowerCase();
    }

    INTENT_ICON(String path) {
        this.path = path;
    }

    INTENT_ICON(GenericEnums.BLENDING blending) {
        this();
        this.blending = blending;
    }

    public static INTENT_ICON getModeIcon(MODE mode) {
        if (mode instanceof STD_MODES) {
            switch (((STD_MODES) mode)) {
                case CHANNELING:
                    return  CHANNELING;
                case STEALTH:
                    break;
                case ALERT:
                case SEARCH:
                    return SEARCH;
                case CONCENTRATION:
                case RESTING:
                case MEDITATION:
                    return PREPARE;
                case DEFENDING:
                    return  DEFEND;
                case WAITING:
                    return  WAIT;
                    default:
                        return WHEEL;
            }
        }
        return null;
    }

    public String getPath() {
        return "ui/content/intent icons/" + path + ".txt";

    }
}
