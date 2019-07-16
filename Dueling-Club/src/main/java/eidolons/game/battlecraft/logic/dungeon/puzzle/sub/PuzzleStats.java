package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import main.system.auxiliary.log.Chronos;
import main.system.data.DataUnit;

public class PuzzleStats extends DataUnit<PuzzleStats.PUZZLE_STAT> {
    Puzzle puzzle;

    public PuzzleStats(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public void started() {
        Chronos.logTimeElapsedForMark(getMark());
    }

    private String getMark() {
        return "puzzle started " + puzzle.getClass().getSimpleName();
    }

    public void ended() {
        addToInt(PUZZLE_STAT.TIME_TOTAL, Math.toIntExact(Chronos.getTimeElapsedForMark(getMark())));
    }

    public void failed() {
        addToInt(PUZZLE_STAT.FAILED, 1);
    }

    public enum PUZZLE_STAT {
        COUNTER_VALUE_AVERAGE,

        FAILED,

        TIME,
        TIME_MAX,
        TIME_MIN,
        TIME_TOTAL,
        TIME_AVRG,

        SCORE,

        DIFFICULTY,


        MIN_MOVES,
        MOVES_MADE,


    }
}
