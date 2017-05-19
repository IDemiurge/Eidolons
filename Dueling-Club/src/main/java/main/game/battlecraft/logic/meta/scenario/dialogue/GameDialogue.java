package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.system.datatypes.DequeImpl;

public class GameDialogue {
/*
actors
entity ?
"line"

tree structure

xml parsed

each replica has children
 */
    Speech speech;

    public GameDialogue(Speech root) {
        speech = root;
    }

    public Speech getSpeech() {
        return speech;
    }

    public void skipped(){

    }
    public DequeImpl<Speech> getOptions(){
        return speech.getChildren();
    }






}
