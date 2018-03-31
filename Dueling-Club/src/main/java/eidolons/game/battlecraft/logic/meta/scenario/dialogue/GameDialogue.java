package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.system.datatypes.DequeImpl;

public class GameDialogue {


    protected Speech root;
    protected String name;

    public GameDialogue(Speech root, String name) {
        this.root = root;
        this.name = name;
    }


    public Speech getRoot() {
        return root;
    }

    public String getName() {
        return name;
    }

    public void skipped() {

    }

    @Deprecated
    public DequeImpl<Speech> getOptions() {
        return root.getChildren();
    }


}
