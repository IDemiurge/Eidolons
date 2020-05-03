package eidolons.game.battlecraft.ai.advanced.engagement;

public class PlayerStatus {
    String iconPath;
    String statusText;
    String subText;

    PLAYER_STATUS status;
    int arg;

    public PlayerStatus(PLAYER_STATUS status, int arg) {
        this.status = status;
        this.arg = arg;
        init();
    }

    private void init() {
        switch (status) {
            case EXPLORATION_UNDETECTED:
            case EXPLORATION_DETECTED:
            case ALERTED:
                if (arg == 0) {
                    subText ="No enemies spotted";
                } else {
                    subText = arg+" enemies spotted";
                }
            case COMBAT:
                break;
            case SHADOW:
                break;
            case DEAD:
                break;
        }
            switch (status) {
            case EXPLORATION_UNDETECTED:
                statusText = "Exploration\nStatus:\nUndetected";
                break;
            case EXPLORATION_DETECTED:
                statusText = "Exploration\nStatus:\nDetected";
                break;
            case ALERTED:
                statusText = "On Alert\nStatus:\nHunted";
                break;
            case COMBAT:
                statusText = "Combat\nStatus:\nAlive";
                break;
            case SHADOW:
                statusText = "Combat\nStatus:\nShadow";
                break;
            case DEAD:
                statusText = "Combat\nStatus:\nDead";
                break;
        }
    }

    public enum PLAYER_STATUS {
        EXPLORATION_UNDETECTED,
        EXPLORATION_DETECTED,
        ALERTED,
        COMBAT,
        SHADOW,
        DEAD //GHOST!
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
}
