package main.system.ai;

import main.content.CONTENT_CONSTS.PLAYER_AI_TYPE;

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
        if (type == PLAYER_AI_TYPE.DEFENSIVE) {
            situation = SITUATION.STALLING;
        }
        if (type == PLAYER_AI_TYPE.BRUTE) {
            situation = SITUATION.ENGAGED; // brute!
        } else {
            situation = SITUATION.PREPARING;
        }
    }

    private void initSituation() {
        // check melee
        // check reinforcements for Stalling
        // check ranged threat
        if (type == PLAYER_AI_TYPE.BRUTE) {
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
