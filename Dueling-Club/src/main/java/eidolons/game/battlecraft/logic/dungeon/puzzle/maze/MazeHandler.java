package eidolons.game.battlecraft.logic.dungeon.puzzle.maze;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.system.audio.MusicMaster;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.List;

public class MazeHandler<T extends MazePuzzle> extends PuzzleHandler<T> {

    public List<Coordinates> markedCells;

    public MazeHandler(T mazePuzzle) {
        super(mazePuzzle);
    }

    protected PuzzleSetup<T, ?> createSetup() {
        return new MazeSetup(puzzle);
    }

    @Override
    public void afterTipAction() {
        super.afterTipAction();
        resetAndGlimpseMaze();
    }

    @Override
    protected void beforeTip() {
        setup.reset();
        if (isFirstAttempt()) {
            GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, getExitCoordinates());
        }
    }

    public void showMaze() {
        MazePuzzle.MazeData mazeData = getMazeData();
        mazeData.c = getCoordinates();
        mazeData.mazeMarks = markedCells;
        List<Coordinates> area = CoordinatesMaster.getCoordinatesBetween(Coordinates.get(0, 0),
                Coordinates.get(getWidth(), getHeight()));
        area.removeAll(markedCells);
        mazeData.mazeMarksAlt = area;
        GuiEventManager.trigger(GuiEventType.SHOW_MAZE, mazeData);
    }

    protected MazePuzzle.MazeData getMazeData() {
        return getPuzzle().getMazeData();
    }

    public void hideMaze() {
        getMazeData().c = getCoordinates();
        GuiEventManager.trigger(GuiEventType.HIDE_MAZE, getMazeData());
    }

    public void resetAndGlimpseMaze() {
        setup.reset();
        showMaze();
        MusicMaster.playMoment(MusicMaster.MUSIC_MOMENT.SECRET);
        WaitMaster.WAIT(puzzle.getGlimpseTime());
        hideMaze();
    }


    @Override
    protected void playerActionDone(DC_ActiveObj action) {

    }


}
