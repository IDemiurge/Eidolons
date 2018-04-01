package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.system.datatypes.DequeImpl;

/**
 * Created by JustMe on 5/19/2017.
 */
public class LinearDialogue extends GameDialogue {


    public LinearDialogue(Speech root, String name) {
        super(root, name);
    }

    @Override
    public DequeImpl<Speech> getOptions() {
        return super.getOptions();
        //just "next"?
    }
}
