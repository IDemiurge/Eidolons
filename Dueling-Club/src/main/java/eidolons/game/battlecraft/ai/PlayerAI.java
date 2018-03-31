package eidolons.game.battlecraft.ai;

import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.PLAYER_AI_TYPE;

public class PlayerAI {
    private PLAYER_AI_TYPE type;
    private SITUATION situation;

    public PlayerAI(PLAYER_AI_TYPE type) {
        this.type = type;
    }

    public PLAYER_AI_TYPE getType() {
        return type;
    }

    public void setType(PLAYER_AI_TYPE type) {
        this.type = type;
    }

    public void resetSituation() {
        // set STALLING by default and at the start of each round, then
        // engage() with visible attacks
        if (type == AiEnums.PLAYER_AI_TYPE.DEFENSIVE) {
            situation = SITUATION.STALLING;
        }
        if (type == AiEnums.PLAYER_AI_TYPE.BRUTE) {
            situation = SITUATION.ENGAGED; // brute!
        } else {
            situation = SITUATION.PREPARING;
        }
    }

    private void initSituation() {
        // preCheck melee
        // preCheck reinforcements for Stalling
        // preCheck ranged threat
        if (type == AiEnums.PLAYER_AI_TYPE.BRUTE) {
            situation = SITUATION.ENGAGED; // brute!
        } else {
            situation = SITUATION.PREPARING;
        }
    }

    public SITUATION getSituation() {
        if (situation == null) {
            initSituation();
        }
        return situation;
    }

    public void setSituation(SITUATION situation) {
        this.situation = situation;
    }

    // ++ unit situation?
    public enum SITUATION {
        ENGAGED, STALLING, PREPARING,
    }

}
