package eidolons.game.battlecraft.logic.dungeon.puzzle.maze;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.sub.CustomRoomTemplateMaster;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.LevelData;
import eidolons.game.module.generator.model.RoomModel;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class MazeSetup<T extends MazePuzzle> extends PuzzleSetup<T, RoomModel> {
    protected CustomRoomTemplateMaster master;

    public  MazeSetup(T puzzle) {
        super(puzzle, puzzle.getData());
    }

    public RoomModel reset() {
        LevelData data = new LevelData("");
        data.setTemplateGroups(new GeneratorEnums.ROOM_TEMPLATE_GROUP[]{
                puzzle.getTemplateGroup()
        });
        master = new CustomRoomTemplateMaster(data.getTemplateGroups());
        GeneratorEnums.EXIT_TEMPLATE template = puzzle.getTemplateForPuzzle();
        RoomModel maze = master.getNextRandomModel(puzzle.getRoomTypeForPuzzle(),
                template,
                FacingMaster.getRandomFacing(), puzzle.getTemplateGroup());

        List<Coordinates> markedCells = new ArrayList<>();
        //        maze.setRotations();
        for (int i = 0; i < maze.getCells().length; i++) {
            for (int j = 0; j < maze.getCells()[0].length; j++) {
                if (maze.getCells()[i][j].equalsIgnoreCase(puzzle.getMarkSymbol().symbol)) {
                    markedCells.add(Coordinates.get(i, j));
                }
            }
        }
        puzzle.getHandler().markedCells=markedCells;
        return maze;
    }
}
