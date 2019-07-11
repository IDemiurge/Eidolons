package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class PuzzleRules extends PuzzleElement {

    public PuzzleRules(Puzzle puzzle) {
        super(puzzle);
    }

    /**
     * create triggers more or less?
     */

    //special rule for the puzzle
    private Runnable createAction() {
        return ()->{
            //branch to full solve

//            Door door= getDoor();
//            door.getDM().open(door, new Ref());

            //msg
            //xp
        };
    }

    public enum PUZZLE_MOVE_RULE{

    }
    public enum MANIPULATOR_RULE{

    }

    public void manipulatorActs(Manipulator manipulator) {

        DC_Cell cell = DC_Game.game.getCellByCoordinate(Eidolons.getMainHero().getCoordinates());

        cell.setOverlayRotation(cell.getOverlayRotation()+90);

        GuiEventManager.trigger(GuiEventType.CELL_RESET, cell);

    }
}
