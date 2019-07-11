package eidolons.game.battlecraft.logic.dungeon.puzzle.cell;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTemplate;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.model.RoomTemplateMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class MazePuzzle extends PuzzleTemplate {
    private RoomTemplateMaster master;

    /**
     * show the maze on Action
     *
     * check
     *
     * solved when arrived at the end - then the maze is revealed
     *
     * use room templates for the maze structure
     *
     *
     *
     * variant - block the cell behind hero
     * variant - just find a way out, culdesacs have traps/fights
     * dynamically changing
     *
     * heart of the maze
     *
     * custom target coordinate to arrive at
     *
     * golem in a maze! one chance or it is destroyed
     *
     *
     *
     */


//    initResolutions

    public void showMaze(){
        //idea - in Pale?
    }
    GeneratorEnums.ROOM_TEMPLATE_GROUP group;
    LocationBuilder.ROOM_TYPE type;
    GeneratorEnums.EXIT_TEMPLATE exit;
    FACING_DIRECTION enter;

    public void initMaze(){

        LevelData data= new LevelData("");
        master= new RoomTemplateMaster(data);

        RoomModel maze = master.getNextRandomModel(type, exit, enter, group);

        maze.getCells();

        //cell overlays for blocked?

        GuiEventManager.trigger(GuiEventType.INIT_CELL_OVERLAY);



//        RoomTemplateMaster.createRoomModel(1, "#", data, GeneratorEnums.EXIT_TEMPLATE.THROUGH,
//                LocationBuilder.ROOM_TYPE.CORRIDOR);

    }
        public void initPunishment(){

}

    @Override
    protected PuzzleMaster.PUZZLE_ACTION_BASE getActionType() {
        return null;
    }

    @Override
    protected PuzzleMaster.PUZZLE_SOLUTION getSolution() {
        return null;
    }
}
