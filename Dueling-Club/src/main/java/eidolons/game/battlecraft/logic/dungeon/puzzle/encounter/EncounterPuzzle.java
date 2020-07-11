package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;

public class EncounterPuzzle extends Puzzle {
    private Encounter encounter;

    @Override
    protected PuzzleHandler createHandler() {
        return new EncPuzzleHandler(this);
    }

    @Override
    protected String getCompletionText() {
        //where would we pull these texts from? By default..
        return "Hurray!";
    }

    @Override
    protected String getFailText() {
        return "Ya rigid";
    }

    @Override
    protected String getDefaultTitle() {
        return "Slash and Burn";
    }

    @Override
    public String getQuestText() {
        return  "Kill the satona!";
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
        return true;
    }

    @Override
    public boolean isMinimizeUI() {
        return false;
    }

    public boolean isContinuousSpawning() {
        return true;
    }
}
