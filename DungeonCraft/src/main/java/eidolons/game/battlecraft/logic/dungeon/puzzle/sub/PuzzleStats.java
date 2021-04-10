package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogManager;
import main.system.data.DataUnit;

public class PuzzleStats extends DataUnit<PuzzleStats.PUZZLE_STAT> {
    Puzzle puzzle;
    public PuzzleStats(Puzzle puzzle) {
        this.puzzle = puzzle;
    }
    public void started() {
        Chronos.mark(getMark());
    }

    private String getMark() {
        return "puzzle started " + puzzle.getClass().getSimpleName();
    }

    public void ended(boolean success) {
        int time = Math.toIntExact(Chronos.getTimeElapsedForMark(getMark()));
        String timeString = NumberUtils.formatFloat(1, (float) time / 1000) + " seconds";
        addToInt(PUZZLE_STAT.TIME_TOTAL, time);

        FileLogManager.stream(FileLogManager.LOG_OUTPUT.MAIN, puzzle+" ended: " +
                getRelevantData());
        if (!success) {
            return;
        }
        setValue(PUZZLE_STAT.TIME_LAST, time);
        if ( getIntValue(PUZZLE_STAT.TIME_MAX)*1000 < time)
            setValue(PUZZLE_STAT.TIME_MAX, timeString);

        if (getIntValue(PUZZLE_STAT.TIME_MIN)==0 || getIntValue(PUZZLE_STAT.TIME_MIN)*1000 > time)
            setValue(PUZZLE_STAT.TIME_MIN, timeString);

//        addAverage(PUZZLE_STAT.TIME_AVRG, time, );
    }

    public void failed() {
        addToInt(PUZZLE_STAT.TIMES_FAILED, 1);
        ended(false);
    }

    public void complete() {
        ended(true);
    }
    public String getVictoryText() {
        String stats = getRelevantData();

        return "Stats: "+stats;
    }

    @Override
    public String getRelevantData() {
        StringBuilder s= new StringBuilder();
        for (PUZZLE_STAT value : PUZZLE_STAT.values()) {
            if (value.displayed ) {
                s.append(StringMaster.format(value.toString())).append(": ").append(getValue(value)).append("\n");
            }

        }
        return s.toString();
    }

    public enum PUZZLE_STAT {
        COUNTER_VALUE_AVERAGE,

        TIMES_FAILED(true),

        TIME_LAST(true),
        TIME_MAX(true),
        TIME_MIN(true),
        TIME_TOTAL,
        TIME_AVRG,

        SCORE,

        DIFFICULTY,

        MIN_MOVES,
//        MOVES_MADE(true),
;
        boolean displayed;

        PUZZLE_STAT() {
        }

        PUZZLE_STAT(boolean displayed) {
            this.displayed = displayed;
        }
    }
}
