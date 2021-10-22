package eidolons.game.battlecraft.logic.dungeon.puzzle.maze;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.exploration.dungeons.generator.GeneratorEnums;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;

import java.util.List;

/**
 *
 */
public class MazePuzzle extends Puzzle { //implements PuzzleTemplate {

    protected MazeData mazeData;

    public MazePuzzle(MazeType mazeType, MazeType alt) {
        mazeData = new MazeData(getCoordinates(), mazeType, alt);
    }

    public MazeData getMazeData() {
        return mazeData;
    }

    public List<Coordinates> getMarkedCells() {
        return getHandler().markedCells;
    }

    @Override
    public MazeHandler getHandler() {
        return (MazeHandler) super.getHandler();
    }

    @Override
    public PuzzleEnums.puzzle_type getType() {
        return PuzzleEnums.puzzle_type.maze;
    }

    @Override
    protected PuzzleHandler createHandler() {
        return new MazeHandler(this );
    }

    protected String getDefaultTitle() {
        return "Memory Maze";
    }

    @Override
    protected int getDefaultHeight() {
        return 9;
    }

    @Override
    protected int getDefaultWidth() {
        return 9;
    }

    protected int getGlimpseTime() {
        return (int) (2550 / getDifficultyCoef());
    }


    protected LocationBuilder.ROOM_TYPE getRoomTypeForPuzzle() {
        return LocationBuilder.ROOM_TYPE.THRONE_ROOM;
    }

    protected GeneratorEnums.ROOM_TEMPLATE_GROUP getTemplateGroup() {
        return GeneratorEnums.ROOM_TEMPLATE_GROUP.PUZZLE_MAZE;
    }

    protected GeneratorEnums.ROOM_CELL getMarkSymbol() {
        return GeneratorEnums.ROOM_CELL.WALL;
    }

    protected GeneratorEnums.EXIT_TEMPLATE getTemplateForPuzzle() {
        if (getWidth() >= 11) {
            return GeneratorEnums.EXIT_TEMPLATE.CROSSROAD;
        }
        if (getWidth() >= 9) {
            return GeneratorEnums.EXIT_TEMPLATE.FORK;
        }
        if (getWidth() >= 7) {
            return GeneratorEnums.EXIT_TEMPLATE.ANGLE;
        }
        return GeneratorEnums.EXIT_TEMPLATE.THROUGH;
    }

    public String getQuestText() {
        return "Reach the Light on the other side";
        //        return "Cross to the other side";
    }

    public static class MazeData {
        public Coordinates c;
        public MazeType mazeType;
        public List<Coordinates> mazeMarks;
        public MazeType mazeTypeAlt;
        public List<Coordinates> mazeMarksAlt;

        public MazeData(Coordinates c, MazeType mazeType) {
            this.c = c;
            this.mazeType = mazeType;
        }

        public MazeData(Coordinates c, MazeType mazeType, MazeType mazeTypeAlt) {
            this.c = c;
            this.mazeType = mazeType;
            this.mazeTypeAlt = mazeTypeAlt;
        }
    }


    public enum MazeType {
        STONE, SKULL,
        FLAME, DARK,    LIGHT,
        NONE;

        protected String imagePath;

        public String getImagePath() {
            return   PathFinder.getOutlinesPath()+"overlays/"+name().toLowerCase()+".png";
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }
}
