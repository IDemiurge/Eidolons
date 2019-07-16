package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.construction.PuzzleActions;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.game.core.Eidolons;
import eidolons.system.ConditionsUtils;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;

public class ArtSetup extends PuzzleSetup {

    private final String[] mutatorArgs;

    public ArtSetup(Puzzle puzzle, PuzzleData data, String... mutatorArgs) {
        super(puzzle, data);
        this.mutatorArgs = mutatorArgs;
    }

    public String getArtPiecePath(String n) {
        return PathFinder.getArtFolder() + "puzzles/" + n + ".png";

    }

    @Override
    public void started() {
        for (String mutatorArg : mutatorArgs) {
            switch (mutatorArg) {
                case "facing":
                    puzzle.createTrigger(PuzzleTrigger.PUZZLE_TRIGGER.ACTION, Event.STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE,
                            ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAINHERO),
                            PuzzleActions.action(PuzzleMaster.PUZZLE_ACTION.ROTATE_MOSAIC_CELL_CLOCKWISE));
                    puzzle.createTrigger(PuzzleTrigger.PUZZLE_TRIGGER.ACTION, Event.STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE,
                            ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAINHERO),
                            PuzzleActions.action(PuzzleMaster.PUZZLE_ACTION.ROTATE_MOSAIC_CELL_ANTICLOCKWISE));
                    break;
            }

        }

        arg = getArtPiecePath(data.getValue(PuzzleData.PUZZLE_VALUE.ARG));
        Coordinates c = puzzle.getCoordinates().getOffsetByY(-puzzle.getHeight() + 1);

        for (int i = 0; i < puzzle.getWidth(); i++) {
            for (int j = 0; j < puzzle.getHeight(); j++) {
//            for (int j = puzzle.getHeight()-1; j >= 0; j--) {

                Coordinates root = c.getOffsetByY(j).getOffsetByX(i);
                DC_Cell cell = Eidolons.getGame().getCellByCoordinate(
                        root);
                cell.setOverlayData(arg + StringMaster.wrapInParenthesis(Coordinates.get(i, j).toString()));
                if ( RandomWizard.chance(getPuzzle().getRotateChance())) {
                float rotation = 90 * RandomWizard.getRandomIntBetween(0, 4);
                cell.setOverlayRotation(rotation);
                }
                GuiEventManager.trigger(GuiEventType.INIT_CELL_OVERLAY, cell);
            }

        }
    }

    @Override
    public ArtPuzzle getPuzzle() {
        return (ArtPuzzle) super.getPuzzle();
    }
}
