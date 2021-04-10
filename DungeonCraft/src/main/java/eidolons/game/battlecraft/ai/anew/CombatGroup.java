package eidolons.game.battlecraft.ai.anew;

import eidolons.game.battlecraft.ai.GroupAI;

public class CombatGroup {
    GroupAI groupAI;
    AiGroupState state;
    AiGroupState prevState;
    int stateLastedFor;
    public enum AiGroupState{
        offense, defense, preparation, maneuver, retreat,;
    }

    /*

     */

}
