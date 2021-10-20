package eidolons.game.battlecraft.ai.anew;

import eidolons.game.battlecraft.ai.GroupAI;

/*
Whole enemy party or not? Maybe reinforcements would come with a new group
Dreamers - as one? Or more chaotic?
Boss fights - each aspect enforces its own kind of behavior largely
Then there are minions... those come in custom groups ofc.

 */
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
