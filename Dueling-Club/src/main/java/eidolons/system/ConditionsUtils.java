package eidolons.system;

import eidolons.ability.conditions.AreaCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.art.ArtPuzzleCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.art.PortalSlotsCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.cell.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.cell.MazePuzzleCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.portal.PortalPuzzle;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.OrConditions;
import main.elements.conditions.standard.PositionCondition;
import main.entity.Ref;
import main.system.entity.ConditionMaster;

import java.util.Set;

public class ConditionsUtils {

    public static Conditions join(Condition... conditions){
        return new Conditions(conditions);
    }

    public static Condition or(Condition... conditions) {
        return new OrConditions(conditions);
    }
    public static Condition fromTemplate(ConditionMaster.CONDITION_TEMPLATES template){
        return DC_ConditionMaster.getInstance().getConditionFromTemplate(template);
    }

    public static Condition forPuzzleSolution(PuzzleMaster.PUZZLE_SOLUTION solution, Puzzle puzzle) {
        Condition condition = puzzle.createSolutionCondition();
        if (condition == null)
            switch (solution) {
            case MOSAIC:
                return            new ArtPuzzleCondition(puzzle, false);
            case GET_TO_EXIT:
                return new PositionCondition(
                        Ref.KEYS.MATCH.toString(), puzzle.getExitCoordinates());
            case SHAPE:
                break;
            case PATH:
                break;
            case FIND_SECRET:
                break;
            case DISCOVER_PATTERN:
                break;
        }
        return condition;
    }

    public static Condition forPuzzlePunishment(Puzzle puzzle, Set<PuzzleResolution.PUZZLE_PUNISHMENT> punishments) {
        for (PuzzleResolution.PUZZLE_PUNISHMENT punishment : punishments) {
            switch (punishment) {

                case battle:
                    break;
                case spell:
                    break;
                case teleport:
                    break;
                case death:
                    break;
            }

        }
        if (puzzle instanceof MazePuzzle){
            return new MazePuzzleCondition((MazePuzzle) puzzle);
        }
        return null ;
    }

}
