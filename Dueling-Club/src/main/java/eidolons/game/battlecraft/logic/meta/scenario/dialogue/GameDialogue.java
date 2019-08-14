package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.system.datatypes.DequeImpl;

public class GameDialogue {


    protected Speech root;
    protected String name;
    private int timeBetweenLines;
    private int timeBetweenScripts;
    private int timeBetweenScriptsLengthMultiplier;

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


    public int getTimeBetweenLines() {
        return timeBetweenLines;
    }

    public void setTimeBetweenLines(int timeBetweenLines) {
        this.timeBetweenLines = timeBetweenLines;
    }

    public void setTimeBetweenScripts(int timeBetweenScripts) {
        this.timeBetweenScripts = timeBetweenScripts;
    }

    public int getTimeBetweenScripts() {
        return timeBetweenScripts;
    }

    public void setTimeBetweenScriptsLengthMultiplier(int timeBetweenScriptsLengthMultiplier) {
        this.timeBetweenScriptsLengthMultiplier = timeBetweenScriptsLengthMultiplier;
    }

    public int getTimeBetweenScriptsLengthMultiplier() {
        return timeBetweenScriptsLengthMultiplier;
    }
}
