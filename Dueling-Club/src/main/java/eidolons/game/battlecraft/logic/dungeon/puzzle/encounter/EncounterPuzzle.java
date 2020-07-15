package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;
import main.system.auxiliary.RandomWizard;

public class EncounterPuzzle extends Puzzle {
    private Encounter encounter;

    @Override
    protected PuzzleHandler createHandler() {
        return new EncPuzzleHandler(this);
    }

    @Override
    protected String getCompletionText() {
        //where would we pull these texts from? By default..
        return "My past victims can rest in peace now. Me, I will have to wait and suffer some more.";
    }

    @Override
    protected String getFailText() {
        return RandomWizard.random()? "By Dust, were they really that many? Will these wretches be my undoing?"
                : "My powers vane. Retribution was inevitable, but defeat, perhaps not...";
    }

    @Override
    protected String getDefaultTitle() {
        return "Souls of the Slain";
    }

    @Override
    public String getQuestText() {
        return  "Slay the risen";
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public int getMinUnits() {
        return 2;
    }

    public boolean isEscapeAllowed() {
        return false;
    }

    @Override
    public boolean isMinimizeUI() {
        return false;
    }

    public boolean isContinuousSpawning() {
        return true;
    }

    public boolean isEndless() {
        return false;
    }

    public int getMaxUnits() {
        return 8;
    }
}
