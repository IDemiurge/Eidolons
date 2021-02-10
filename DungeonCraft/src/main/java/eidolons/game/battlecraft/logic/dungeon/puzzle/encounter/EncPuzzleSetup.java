package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;

public class EncPuzzleSetup extends PuzzleSetup<EncounterPuzzle, Encounter> {
    public EncPuzzleSetup(EncounterPuzzle puzzle, PuzzleData data) {
        super(puzzle, data);
    }

    @Override
    public Encounter reset() {
        return null;
    }
}
