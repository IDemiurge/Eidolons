package eidolons.game.battlecraft.ai.anew;

import eidolons.game.battlecraft.ai.GroupAI;
import main.game.logic.event.Event;

public class AiStateMachine {
    /*
    support INTENTIONS
    easy to debug (including VISUAL)
    customizable with simple parametration
    simple logic for most abilities and spells
    custom logic for some that really are complex
    solid basics - positioning, waiting, modes,
     */

    //after an action is completed OR round ends
    public void checkStateChange(Event event){
        /*
when does it change?
1) time
2) finished action
3) player action
4) custom triggers

There is a natural cycle for this?
         */
        GroupAI group;

    }

}
