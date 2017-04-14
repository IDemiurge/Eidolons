package main.game.ai.tools.path;

import main.content.enums.entity.ActionEnums;
import main.elements.costs.Costs;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.ActionManager;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.ai.tools.target.ReasonMaster;
import main.game.ai.tools.target.ReasonMaster.FILTER_REASON;
import main.game.ai.tools.time.TimeLimitMaster;
import main.game.ai.tools.time.TimeLimitMaster.METRIC;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.FacingMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;

import java.util.LinkedList;
import java.util.List;

public class PathBuilder extends AiHandler {
    private static final int FILTER_THRESHOLD = 10;
    private static final int MAX_PATH_SIZE = 15;
    private static PathBuilder instance;
    private Unit unit;
    private Action targetAction;
    private List<Coordinates> targetCells;
    private Coordinates targetCoordinate;
    private Coordinates originalCoordinate;
    private Coordinates c_coordinate;
    private Coordinates previousCoordinate;
    private FACING_DIRECTION c_facing;
    private FACING_DIRECTION originalFacing;
    private FACING_DIRECTION previousFacing;

    // should these be dynamic? stronger units should getOrCreate more!
    private Choice base_choice;
    private List<ActionPath> paths;
    private List<ActionPath> filteredPaths;
    private ActionPath path;
    private Costs bestCost;
    private Integer bestResult;
    private boolean failed;

    private PathChoiceMaster pathChoiceMaster;

    private PathBuilder(AiHandler master) {
        super(master);
    }

    public static PathBuilder getInstance(AiHandler master) {
        instance = new PathBuilder(master);
        return instance;
    }
    public static PathBuilder getInstance() {
        return instance;
    }

    public PathBuilder init(List<DC_ActiveObj> moveActions, Action targetAction) {
        this.targetAction = targetAction;
        init();
        pathChoiceMaster.init(unit,targetAction, targetCoordinate,moveActions);
        return this;
    }

    private void init() {
        pathChoiceMaster = new PathChoiceMaster(this);
        unit = targetAction.getSource();
        originalCoordinate = unit.getCoordinates();
        originalFacing = unit.getFacing();
    }

    private void reset() {
        c_facing = originalFacing;
        c_coordinate = originalCoordinate;
        previousCoordinate = null;
        previousFacing = null;
        failed = false;
    }

    private void resetUnit() {
        unit.setCoordinates(originalCoordinate);
        unit.setFacing(originalFacing);

    }
    protected boolean checkEmpty(Coordinates c) {
        return unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit, c);
        // return !unit.getGame().getBattleField().getGrid().isOccupied(c);
        // unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit, c);
        // unit.getGame().getBattleField().getGrid().getObj(c) == null;
    }
    protected void adjustUnit() {
        unit.setCoordinates(c_coordinate);
        unit.setFacing(c_facing);
        unit.getGame().getRules().getStackingRule().clearCache();
    }

    public List<ActionPath> build(List<Coordinates> targetCoordinates
            , List<DC_ActiveObj> moveActions, Action targetAction) {
        this.targetAction = targetAction;
        init();
        pathChoiceMaster.init(unit,targetAction, targetCoordinate,moveActions);
        return build(targetCoordinates);
    }

    public List<ActionPath> build(List<Coordinates> targetCoordinates) {
        try {
            paths = buildPaths(targetCoordinates);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resetUnit();
        }
        try {
            filterPaths();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filteredPaths.isEmpty()){
            filteredPaths = paths;
        }
        return filteredPaths;
    }

    private List<ActionPath> buildPaths(List<Coordinates> targetCoordinates) {
        Chronos.mark(getChronosPrefix() + targetAction);
        paths = new LinkedList<>();
        bestResult = null;
        targetCells = targetCoordinates;

        // depth-first search
        loop:
        for (Coordinates dest : targetCells) {
            Chronos.mark(getChronosPrefix() + dest);
            targetCoordinate = dest;
            reset();
            path = new ActionPath(originalCoordinate);
            // TODO first step must be activateable!
            List<Choice> choices = pathChoiceMaster.getChoices(path, c_coordinate, c_facing);
            for (Choice choice : choices) {
                base_choice = choice;
                Chronos.mark(getChronosPrefix() + base_choice);
                path = new ActionPath(targetCoordinate);
                if (!step(choice)) {
                    break;
                }
                if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_RESULTS) {
                    Chronos.logTimeElapsedForMark(getChronosPrefix() + choice); // TODO
                }
                // mark removed???
            }
            if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_BASIC) {
                Chronos.logTimeElapsedForMark(getChronosPrefix() + dest);
            }
        }
        if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_BASIC) {
            Chronos.logTimeElapsedForMark(getChronosPrefix() + targetAction);
        }
        return paths;
    }



    private void applyChoice(Choice choice) {
        previousCoordinate = c_coordinate;
        c_coordinate = choice.getCoordinates();
        if (choice.getTurns() != null) {
            previousFacing = c_facing;
            for (Boolean turn : choice.getTurns()) {

                c_facing = FacingMaster.rotate(c_facing, turn);
            }
        }
    }

    private boolean checkFinished() {
        // targetCells.contains(c_coordinate))
        // TODO how to ensure that all targets are arrived at by a path?
        return targetCells.contains(c_coordinate) || targetCoordinate.equals(c_coordinate);
    }

    private boolean step(Choice choice) {

        applyChoice(choice);
        path.add(choice);
        if (checkFinished()) {
            if (targetAction.getActive().getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
                checkAddFaceTurn(); // TODO better preCheck?
            }
            if (checkFailed()) {
                return true;
            }
            finished();
            return true;
        }
        if (Chronos.getTimeElapsedForMark(getChronosPrefix() + targetAction) > TimeLimitMaster
                .getTimeLimitForPathBuilding()
                * TimeLimitMaster.CRITICAL_FAIL_FACTOR) {
            Chronos.logTimeElapsedForMark(getChronosPrefix() + targetAction);
            LogMaster.log(1, "*** CRITICAL_FAIL TimeLimitForPathBuilding "
                    + targetAction);
            return false;
        }
        if (paths.size() > 0) {
            // if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH_STEP,
            // getChronosPrefix() + targetAction))
            // return false; TODO ???
            if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH, getChronosPrefix() + base_choice)) {
                return false;
            }
            if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH_CELL, getChronosPrefix()
                    + targetCoordinate)) {
                return false;
            }
            if (!TimeLimitMaster.checkTimeLimit(METRIC.ACTION, getChronosPrefix() + targetAction)) {
                return false;
            }
        }
        if (checkFailed()) {
            return true;
        }

        List<Choice> choices = pathChoiceMaster.getChoices(path, c_coordinate, c_facing);
        // depth first search
        for (Choice nextChoice : choices) {
            if (!step(nextChoice)) {
                return false;
            }
            // if (!failed)
            clonePath();
            back();
        }
        return true;
    }

    private boolean checkDuplicate() {
        return paths.contains(path);
    }

    private void checkAddFaceTurn() {
        unit.setFacing(c_facing);
        unit.setCoordinates(c_coordinate);
        if (ReasonMaster.checkReasonCannotTarget(FILTER_REASON.FACING, targetAction)) {
            List<Action> sequence = getTurnSequenceConstructor().getTurnSequence(targetAction);
            for (Action a : sequence) {
                path.add(new Choice(c_coordinate, a));
            }
        }
        unit.setFacing(originalFacing);
        unit.setCoordinates(originalCoordinate);
    }

    private void clonePath() {
        path = new ActionPath(path);
        // reset();
    }

    private void back() {
        if (!path.getChoices().isEmpty()) {
            path.getChoices().remove(path.getChoices().size() - 1);
        }
        if (previousCoordinate != null) {
            c_coordinate = previousCoordinate;
        }
        previousCoordinate = null;
        if (previousFacing != null) {
            c_facing = previousFacing;
        }
        previousFacing = null;
    }

    private void finished() {
        if (checkDuplicate()) {
            return;
        }
        paths.add(path);
        int result = getPathPriority();
        log(result);

        if (bestResult == null) {
            bestResult = result;
        } else {
            if (result > bestResult) {
                bestResult = result;
            }
        }
        // Integer pathPriority = PriorityManager.getPathPriority(path);
        // Costs c;
        // c.compare(bestCost);
        // if (pathPriority >= bestPathPriority)
        // this.bestPathPriority = pathPriority;

    }

    private void log(int result) {
        LogMaster.log(LOG_CHANNELS.PATHING_DEBUG, result
                + " priority for path: " + path);
    }

    private boolean checkFailed() {
        failed = false;
        if (path.getChoices().size() > MAX_PATH_SIZE) {
            failed = true;
        } else {
            if (bestResult == null) {
                return failed;
            }
            Integer pathPriority = getPathPriority();

            if (pathPriority < bestResult || pathPriority == 0) {
                // if (isBestPathOnly())
                failed = true;
            }
        }
        return failed;
    }

    private Integer getPathPriority() {
        Costs cost = getPathCosts(path);
        int result = DC_PriorityManager.getCostFactor(cost, unit);
        try {
            // result += getAoOPenalty(); TODO instant atks preCheck !
        } catch (Exception e) {
            e.printStackTrace();
        }
        path.setPriority(result);
        // int size = path.getActions()
        // .size();
        // result = result
        // * PriorityManager.getActionNumberFactor(size) / 100;
        return result;
    }

    public Costs getPathCosts(ActionPath path) {
        return ActionManager.getTotalCost(path.getActions());
    }

    public ActionPath getPathByPriority(List<Coordinates> targetCoordinates) {
        paths = build(targetCoordinates);
        if (paths.isEmpty()) {
            return null;
        }
        return paths.get(0);
    }

    private String getChronosPrefix() {
        return "Building path for ";
    }

    private void filterPaths() {
        filteredPaths = new LinkedList<>();
        for (ActionPath p : paths) {
            int priority = p.getPriority();
            if (bestResult - priority >= FILTER_THRESHOLD) {
                continue;
            }
            filteredPaths.add(p);
        }

    }

    public Coordinates getPreviousCoordinate() {
        return previousCoordinate;
    }
}
