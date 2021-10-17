package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleActions;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger.PUZZLE_TRIGGER;
import eidolons.game.core.Eidolons;
import eidolons.system.ConditionsUtils;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster.CONDITION_TEMPLATES;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData.PUZZLE_VALUE;
import static eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums.PUZZLE_ACTION;

public class ArtSetup extends PuzzleSetup<ArtPuzzle, Object> {

    Set<DC_Cell> mosaic = new LinkedHashSet<>();

    public ArtSetup(ArtPuzzle puzzle, PuzzleData data, String... mutatorArgs) {
        super(puzzle, data);
    }

    public String getArtPiecePath(String n) {
        return PathFinder.getArtFolder() + "puzzles/" + n + ".png";

    }

    @Override
    public void ended() {
        if (puzzle.isSolved()) {
            setup(false);
        }
    }

    @Override
    public Object reset() {
        return null;
    }

    public void setup(boolean on) {
        for (DC_Cell cell : mosaic) {
            if (!on) {
                cell.setOverlayData(null);
            } else {
                Coordinates coordinates = cell.getCoordinates().getOffset(puzzle.getCoordinates().negative());
                cell.setOverlayData(arg + StringMaster.wrapInParenthesis(coordinates.toString()));
                cell.setArtPuzzleCell(true);
                if (RandomWizard.chance(getPuzzle().getRotateChance())) {
                    float rotation = 90 * RandomWizard.getRandomIntBetween(0, 4);
                    cell.setOverlayRotation(rotation);
                } else {
                    cell.setOverlayRotation(0);
                }
            }
            GuiEventManager.trigger(GuiEventType.INIT_CELL_OVERLAY, cell);
        }
    }

    @Override
    public void started() {
        puzzle.createTrigger(PUZZLE_TRIGGER.ACTION, STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE,
                ConditionsUtils.fromTemplate(CONDITION_TEMPLATES.MAIN_HERO),
                PuzzleActions.action(PUZZLE_ACTION.ROTATE_MOSAIC_CELL_CLOCKWISE));
        puzzle.createTrigger(PUZZLE_TRIGGER.ACTION, STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE,
                ConditionsUtils.fromTemplate(CONDITION_TEMPLATES.MAIN_HERO),
                PuzzleActions.action(PUZZLE_ACTION.ROTATE_MOSAIC_CELL_ANTICLOCKWISE));

        List<String> mutators =data.getMutators();
        if (mutators.contains("on_move"))
            puzzle.createTrigger(PUZZLE_TRIGGER.ACTION, STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING,
                    ConditionsUtils.fromTemplate(CONDITION_TEMPLATES.MAIN_HERO),
                    PuzzleActions.action(PUZZLE_ACTION.ROTATE_MOSAIC_CELL_CLOCKWISE));

        arg = getArtPiecePath(data.getValue(PUZZLE_VALUE.ARG));
        Coordinates c = puzzle.getCoordinates();

        for (int i = 0; i < puzzle.getWidth(); i++) {
            for (int j = 0; j < puzzle.getHeight(); j++) {
                //            for (int j = puzzle.getHeight()-1; j >= 0; j--) {
                Coordinates root = c.getOffsetByY(j).getOffsetByX(i);
                DC_Cell cell = Eidolons.getGame().getCell(
                        root);
                mosaic.add(cell);
            }
        }
        setup(true);
    }


    @Override
    public ArtPuzzle getPuzzle() {
        return super.getPuzzle();
    }
}
