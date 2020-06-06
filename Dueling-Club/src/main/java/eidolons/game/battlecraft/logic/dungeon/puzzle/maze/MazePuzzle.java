package eidolons.game.battlecraft.logic.dungeon.puzzle.maze;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.LevelData;
import eidolons.game.module.generator.model.RoomModel;
import eidolons.libgdx.shaders.post.PostFxUpdater;
import eidolons.system.audio.MusicMaster;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * show the maze on Action
 * <p>
 * check
 * <p>
 * solved when arrived at the end - then the maze is revealed
 * <p>
 * use room templates for the maze structure
 * <p>
 * variant - block the cell behind hero variant - just find a way out, culdesacs have traps/fights dynamically changing
 * <p>
 * heart of the maze
 * <p>
 * custom target coordinate to arrive at
 * <p>
 * golem in a maze! one chance or it is destroyed
 */
public class MazePuzzle extends Puzzle { //implements PuzzleTemplate {

    protected CustomRoomTemplateMaster master;
    protected List<Coordinates> markedCells;
    protected MazeData data;

    public MazePuzzle(MazeType mazeType, MazeType alt) {
        data = new MazeData(getCoordinates(), mazeType, alt);

    }

    public void showMaze() {
        data.c=getCoordinates();
        data.mazeMarks = markedCells;
        List<Coordinates> area = CoordinatesMaster.getCoordinatesBetween(Coordinates.get(0,0),
               Coordinates.get(getWidth(), getHeight()));
        area.removeAll(markedCells);
        data.mazeMarksAlt = area;
        GuiEventManager.trigger(GuiEventType.SHOW_MAZE, data);

    }

    public void hideMaze() {
        data.c=getCoordinates();
        GuiEventManager.trigger(GuiEventType.HIDE_MAZE, data);
    }

    public List<Coordinates> getMarkedCells() {
        return markedCells;
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

    protected String getDefaultTitle() {
        return "Twilit Maze";
    }

    public void resetAndGlimpseMaze() {
        resetMaze();
        showMaze();
        MusicMaster.playMoment(MusicMaster.MUSIC_MOMENT.SECRET);
        WaitMaster.WAIT(getGlimpseTime());
        hideMaze();
    }

    protected int getGlimpseTime() {
        return (int) (2550 / getDifficultyCoef());
    }


    @Override
    public void activate() {
        super.activate();
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING, PostFxUpdater.POST_FX_TEMPLATE.MAZE);
    }

    public RoomModel resetMaze() {

        LevelData data = new LevelData("");
        data.setTemplateGroups(new GeneratorEnums.ROOM_TEMPLATE_GROUP[]{
                getTemplateGroup()
        });
        master = new CustomRoomTemplateMaster(data.getTemplateGroups());
        GeneratorEnums.EXIT_TEMPLATE template = getTemplateForPuzzle();
        RoomModel maze = master.getNextRandomModel(getRoomTypeForPuzzle(),
                template,
                FacingMaster.getRandomFacing(), getTemplateGroup());
        markedCells = new ArrayList<>();
        //        maze.setRotations();
        for (int i = 0; i < maze.getCells().length; i++) {
            for (int j = 0; j < maze.getCells()[0].length; j++) {
                if (maze.getCells()[i][j].equalsIgnoreCase(getMarkSymbol().symbol)) {
                    markedCells.add(Coordinates.get(i, j));
                }
            }
        }
        return maze;
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

}
