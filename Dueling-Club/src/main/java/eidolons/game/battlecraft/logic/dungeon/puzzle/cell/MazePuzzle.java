package eidolons.game.battlecraft.logic.dungeon.puzzle.cell;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTemplate;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.model.RoomTemplateMaster;
import eidolons.libgdx.shaders.post.PostFxUpdater;
import eidolons.libgdx.texture.Images;
import eidolons.system.audio.MusicMaster;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
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
 * variant - block the cell behind hero
 * variant - just find a way out, culdesacs have traps/fights
 * dynamically changing
 * <p>
 * heart of the maze
 * <p>
 * custom target coordinate to arrive at
 * <p>
 * golem in a maze! one chance or it is destroyed
 */
public class MazePuzzle extends Puzzle { //implements PuzzleTemplate {

    private RoomTemplateMaster master;

    GeneratorEnums.ROOM_TEMPLATE_GROUP group;
    LocationBuilder.ROOM_TYPE type;
    GeneratorEnums.EXIT_TEMPLATE exit;
    FACING_DIRECTION enter;


    List<Coordinates> mazeWalls;
    MazeData data;
    public MazePuzzle(MazeType mazeType) {
        data = new MazeData(getCoordinates(), mazeType);

    }

    public void showMaze() {
        //idea - in Pale?
        data = new MazeData(getCoordinates(), data.mazeType);
        data.mazeWalls = mazeWalls;
        GuiEventManager.trigger(GuiEventType.SHOW_MAZE, data);

    }

    public void hideMaze() {
        //idea - in Pale?
        data = new MazeData(getCoordinates(), data.mazeType);
        data.mazeWalls = mazeWalls;
        GuiEventManager.trigger(GuiEventType.HIDE_MAZE, data);
    }

    public List<Coordinates> getMazeWalls() {
        return mazeWalls;
    }


    public class MazeData {
        public Coordinates c;
        public MazeType mazeType;
        public List<Coordinates> mazeWalls;

        public MazeData(Coordinates c, MazeType mazeType) {
            this.c = c;
            this.mazeType = mazeType;
        }
    }


    public enum MazeType {
        STONE, VOID,
        FLAME, DARK,
        ;

        private String imagePath;

        public String getImagePath() {
            //random variant?
//            PathFinder.get
            return Images.LIGHT_SKULL;
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

    private int getGlimpseTime() {
        return (int) (2550/getDifficultyCoef());
    }

    @Override
    public void activate() {
        super.activate();

        GuiEventManager.trigger(GuiEventType.POST_PROCESSING, PostFxUpdater.POST_FX_TEMPLATE.MAZE);
    }

    public void resetMaze() {

        LevelData data = new LevelData("");
//        data.setValue(GeneratorEnums.LEVEL_VALUES.ROOM_COUNT_MOD);
//        data.setValue(GeneratorEnums.LEVEL_VALUES.WRAP_CELL_TYPE, 1);
        data.setTemplateGroups(new GeneratorEnums.ROOM_TEMPLATE_GROUP[]{
                GeneratorEnums.ROOM_TEMPLATE_GROUP.PUZZLE_MAZE
        });
        master = new RoomTemplateMaster(data);
        GeneratorEnums.EXIT_TEMPLATE template = getTemplateForPuzzle();
        RoomModel maze = master.getNextRandomModel(LocationBuilder.ROOM_TYPE.THRONE_ROOM,
                template,
                FacingMaster.getRandomFacing(), GeneratorEnums.ROOM_TEMPLATE_GROUP.PUZZLE_MAZE);
        mazeWalls = new ArrayList<>();
//        maze.setRotations();
        for (int i = 0; i < maze.getCells().length; i++) {
            for (int j = 0; j < maze.getCells()[0].length; j++) {
                if (maze.getCells()[i][j].equalsIgnoreCase(GeneratorEnums.ROOM_CELL.WALL.symbol)) {
                    mazeWalls.add(Coordinates.get(i, j));
                }

            }
        }

        //cell overlays for blocked?


//        RoomTemplateMaster.createRoomModel(1, "#", data, GeneratorEnums.EXIT_TEMPLATE.THROUGH,
//                LocationBuilder.ROOM_TYPE.CORRIDOR);

    }

    private GeneratorEnums.EXIT_TEMPLATE getTemplateForPuzzle() {
        if (getWidth()>=11) {
            return GeneratorEnums.EXIT_TEMPLATE.CROSSROAD;
        }
        if (getWidth()>=9) {
            return GeneratorEnums.EXIT_TEMPLATE.FORK;
        }
        if (getWidth()>=7) {
            return GeneratorEnums.EXIT_TEMPLATE.ANGLE;
        }
        return GeneratorEnums.EXIT_TEMPLATE.THROUGH;
    }

    public String getQuestText() {
        return "Reach the Light on the other side";
//        return "Cross to the other side";
    }

}
