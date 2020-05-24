package eidolons.game.battlecraft.ai.advanced.engagement;

import eidolons.libgdx.texture.Images;
import main.content.enums.rules.VisionEnums;

public class PlayerStatus {
    String iconPath;
    String statusText;
    String subText;

    VisionEnums.PLAYER_STATUS type;
    int arg;

    public PlayerStatus(VisionEnums.PLAYER_STATUS status, int arg) {
        this.type = status;
        this.arg = arg;
        init();
    }

    private void init() {
        switch (type) {
            case EXPLORATION_UNDETECTED:
            case EXPLORATION_DETECTED:
            case ALERTED:
                if (arg == 0) {
                    subText = "No enemies spotted";
                } else {
                    subText = arg + " enemies spotted";
                }
            case COMBAT:
                break;
            case SHADOW:
                break;
            case DEAD:
                break;
        }
        switch (type) {
            case EXPLORATION_UNDETECTED:
                iconPath= Images.STATUS_EXPLORE;
                statusText = "Exploration\nStatus:\nUndetected";
                break;
            case EXPLORATION_DETECTED:
                iconPath= Images.STATUS_EXPLORE_DETECTED;
                statusText = "Exploration\nStatus:\nDetected";
                break;
            case ALERTED:
                iconPath= Images.STATUS_ALARM;
                statusText = "On Alert\nStatus:\nHunted";
                break;
            case COMBAT:
                iconPath= Images.STATUS_COMBAT;
                statusText = "Combat\nReinforcements:" ;
                // + "Status:\nAlive";
                // if (arg == 0) {
                    subText = "  Unknown";
                // } else
                //     subText = "Reinforcements: " + arg + " turns";
                break;
            case SHADOW:
                statusText = "Combat\nStatus:\nShadow";
                if (arg == 0) {
//                    subText = " turns left";
                } else
                    subText = arg +" turns left";
                break;
            case DEAD:
                statusText = "Combat\nStatus:\nDead";
                break;
        }
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getSubText() {
        return subText;
    }

    public VisionEnums.PLAYER_STATUS getType() {
        return type;
    }

    public int getArg() {
        return arg;
    }
}
