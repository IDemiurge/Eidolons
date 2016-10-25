package main.system.ai.logic.path;

import main.ability.conditions.special.SneakCondition;
import main.ability.effects.ChangeFacingEffect;
import main.ability.effects.Effect;
import main.ability.effects.SelfMoveEffect;
import main.content.CONTENT_CONSTS.ACTION_TYPE_GROUPS;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.PARAMS;
import main.elements.costs.Costs;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_UnitAction;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.FacingMaster;
import main.rules.DC_ActionManager;
import main.system.ai.UnitAI;
import main.system.ai.logic.actions.Action;
import main.system.ai.logic.actions.ActionManager;
import main.system.ai.logic.actions.ActionSequenceConstructor;
import main.system.ai.logic.actions.TimeLimitMaster;
import main.system.ai.logic.actions.TimeLimitMaster.METRIC;
import main.system.ai.logic.priority.PriorityManager;
import main.system.ai.logic.target.ReasonMaster;
import main.system.ai.logic.target.ReasonMaster.FILTER_REASON;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

import java.util.*;

public class PathBuilder {
    private static final int FILTER_THRESHOLD = 10;
    private static final int MAX_PATH_SIZE = 15;
    private DC_UnitAction stdMove;
    private List<DC_ActiveObj> moveActions; // only special here?
    private Action targetAction;
    private DC_HeroObj unit;
    private Coordinates c_coordinate;
    private FACING_DIRECTION c_facing;
    private Coordinates targetCoordinate;
    private Coordinates originalCoordinate;
    private List<ActionPath> paths;
    private ActionPath path;
    private Coordinates previousCoordinate;
    private List<Coordinates> targetCells;
    private FACING_DIRECTION originalFacing;
    private boolean failed;
    private FACING_DIRECTION previousFacing;
    private Costs bestCost;
    private Integer bestResult;
    private LinkedList<Object> sneakCells;
    private List<ActionPath> filteredPaths;
    private LinkedList<Object> nonSneakCells;
    private boolean firstStep;
    // should these be dynamic? stronger units should get more!
    private Choice base_choice;

    public PathBuilder(List<DC_ActiveObj> moveActions, Action targetAction) {
        this.targetAction = targetAction;
        this.moveActions = moveActions;
        init();
    }

    private void init() {
        unit = targetAction.getSource();
        stdMove = unit.getAction(DC_ActionManager.STD_ACTIONS.Move.name());
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

    private void adjustUnit() {
        unit.setCoordinates(c_coordinate);
        unit.setFacing(c_facing);
        unit.getGame().getRules().getStackingRule().clearCache();
    }

    private Action getMoveAction() {
        return new Action(stdMove);
    }

    private Choice constructStdMoveChoice(Coordinates targetCoordinate) {
        FACING_SINGLE facing = FacingMaster.getSingleFacing(c_facing, c_coordinate,
                targetCoordinate);
        Action moveAction = getMoveAction();
        if (facing == FACING_SINGLE.IN_FRONT) {
            if (firstStep)
                if (!moveAction.canBeActivated())
                    return null;
            return new Choice(targetCoordinate, c_coordinate, moveAction);
        }
        adjustUnit();
        Collection<Action> actions = ActionSequenceConstructor.getTurnSequence(
                FACING_SINGLE.IN_FRONT, unit, targetCoordinate);
        actions.add(moveAction);
        // resetUnit();// TODO is that right?
        Choice choice = new Choice(targetCoordinate, c_coordinate, actions
                .toArray(new Action[actions.size()]));

        return choice;
    }

    private List<Choice> getChoices() {
        Chronos.mark("Finding choices for " + path);
        adjustUnit();

        List<Choice> choices = new LinkedList<>();
        for (Coordinates targetCoordinate : getDefaultCoordinateTargets()) {
            Choice stdMoveChoice = constructStdMoveChoice(targetCoordinate);
            if (stdMoveChoice != null)
                choices.add(stdMoveChoice);
        }
        Chronos.mark("Finding custom choices for " + path);
        if (ListMaster.isNotEmpty(moveActions)) {
            // add special
            // will need to remove actions from list when used? check CD

            for (DC_ActiveObj a : moveActions) {

                if (!a.canBeActivated()) {
                    if (firstStep)
                        if (!ReasonMaster.checkReasonCannotActivate(a, PARAMS.C_N_OF_ACTIONS
                                .getName()))
                            continue; // exception for AP TODO
                }
                if (path.hasAction(a))
                    if (a.getIntParam(PARAMS.COOLDOWN) >= 0)
                        continue;
                Targeting targeting = a.getTargeting();
                Collection<Obj> objects = null;
                if (targeting instanceof FixedTargeting) {
                    Targeting t = a.getAbilities().getTargeting();
                    if (t != null) {
                        objects = t.getFilter().getObjects(a.getRef());
                    }
                    Effect e = a.getAbilities().getEffects().getEffects().get(0);
                    e.setRef(unit.getRef());
                    if (e instanceof SelfMoveEffect)
                        try {
                            Coordinates coordinates = ((SelfMoveEffect) e).getCoordinates();
                            if (coordinates != null)
                                objects = new LinkedList<Obj>(Arrays.asList(new Obj[]{unit
                                        .getGame().getCellByCoordinate(coordinates)}));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                } else {
                    adjustUnit();
                    objects = targeting.getFilter().getObjects(a.getRef());
                }
                if (objects != null)
                    for (Object obj : objects) {
                        if (obj instanceof DC_Cell) {
                            Coordinates coordinates = ((DC_Cell) obj).getCoordinates();
                            // if (a.getName().equals("Clumsy Leap"))
                            if (PositionMaster.getDistance(coordinates, c_coordinate) > Math.max(1,
                                    a.getIntParam(PARAMS.RANGE)))
                                continue;

                            if (PositionMaster.getDistance(coordinates, targetCoordinate) > PositionMaster
                                    .getDistance(c_coordinate, targetCoordinate))
                                continue; // TODO will this not eliminate good
                            // choices?

                            Ref ref = unit.getRef().getCopy();
                            ref.setTarget(((DC_Cell) obj).getId());
                            Choice choice = new Choice(coordinates, c_coordinate,
                                    new Action(a, ref));
                            choices.add(choice);
                        }
                    }
                // if (choices.size() > 1)
                choices = filterSpecialMoveChoices(choices, a);
            }
        }

        sortChoices(choices);
        Chronos.logTimeElapsedForMark("Finding custom choices for " + path);

        // resetUnit();// TODO is that right?
        Chronos.logTimeElapsedForMark("Finding choices for " + path);
        // Chronos.mark("Sorting choices for " + path);

        // choices.addAll(stdChoices);
        // Chronos.logTimeElapsedForMark("Sorting choices for " + path);
        return choices;
    }

    private void sortChoices(List<Choice> choices) {
        Collections.sort(choices, getSorter());

    }

    private Comparator<? super Choice> getSorter() {
        return new Comparator<Choice>() {
            @Override
            public int compare(Choice c1, Choice c2) {
                int distance = PositionMaster.getDistance(c1.getCoordinates(), targetCoordinate);
                int distance2 = PositionMaster.getDistance(c2.getCoordinates(), targetCoordinate);
                if (distance > distance2)
                    return 1;
                if (distance < distance2)
                    return -1;
                distance = c1.actions.size();
                distance2 = c2.actions.size();
                if (distance > distance2)
                    return 1;
                if (distance < distance2)
                    return -1;
                return 0;
            }
        };
    }

    private List<Choice> filterSpecialMoveChoices(List<Choice> choices, DC_ActiveObj a) {
        int bestDistance_1 = 0;
        int bestDistance_2 = Integer.MAX_VALUE;
        Coordinates coordinates = targetAction.getTarget().getCoordinates();
        for (Choice choice : choices) {
            Coordinates c = choice.getCoordinates();

            if (c.isAdjacent(coordinates) || c.equals(coordinates)) {
                int distance_1 = PositionMaster.getDistance(c_coordinate, c); // max
                int distance_2 = PositionMaster.getDistance(coordinates, c); // min
                if (distance_2 <= bestDistance_2) {
                    bestDistance_2 = distance_2;
                    if (distance_1 > bestDistance_1) {
                        bestDistance_1 = distance_1;
                    }
                }

            }
        }

        List<Choice> filteredList = new LinkedList<>();
        for (Choice choice : choices) {
            Coordinates c = choice.getCoordinates();
            if (c.equals(c_coordinate))
                continue;
            if (path.hasCoordinate(c))
                continue;
            if (checkSneak(c)) // cache sneak cells?
                filteredList.add(choice);
            else if (PositionMaster.getDistance(coordinates, c) <= bestDistance_2
                    || c.isAdjacent(targetAction.getTarget().getCoordinates())) {
                if (PositionMaster.getDistance(c_coordinate, c) <= bestDistance_1)
                    filteredList.add(choice);
            }
        }

        return filteredList;
    }

    private boolean checkSneak(Coordinates c) {
        if (nonSneakCells.contains(c))
            return false;
        if (sneakCells.contains(c))
            return true;
        unit.setCoordinates(c); // change facing
        // check range
        if (PositionMaster.getDistance(targetAction.getTarget().getCoordinates(), c) > targetAction
                .getActive().getIntParam(PARAMS.RANGE)) {
            nonSneakCells.add(c);
            return false;
        }
        Ref ref = targetAction.getRef();
        ref.setTarget(targetAction.getTarget().getId());
        boolean result = new SneakCondition().check(ref);

        adjustUnit();
        if (result)
            sneakCells.add(c);
        else
            nonSneakCells.add(c);
        return result;
    }

    private List<Coordinates> getDefaultCoordinateTargets() {

        List<Coordinates> list = new LinkedList<>();
        for (DIRECTION d : DIRECTION.values()) {
            if (d.isDiagonal())
                continue;
            Coordinates c = c_coordinate.getAdjacentCoordinate(d);
            if (c == previousCoordinate || c == null)
                continue;
            if (path.hasCoordinate(c))
                continue;

            if (!checkEmpty(c))
                continue;
            // if (FacingManager.getSingleFacing(c_facing, c_coordinate, c) !=
            // FACING_SINGLE.BEHIND)
            list.add(c);

        }
        return list;
    }

    private boolean checkEmpty(Coordinates c) {
        return unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit, c);
        // return !unit.getGame().getBattleField().getGrid().isOccupied(c);
        // unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit, c);
        // unit.getGame().getBattleField().getGrid().getObj(c) == null;
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
            if (targetAction.getActive().getActionGroup() != ACTION_TYPE_GROUPS.MOVE)
                checkAddFaceTurn(); // TODO better check?
            if (checkFailed())
                return true;
            finished();
            return true;
        }
        if (Chronos.getTimeElapsedForMark(getChronosPrefix() + targetAction) > TimeLimitMaster
                .getTimeLimitForPathBuilding()
                * TimeLimitMaster.CRITICAL_FAIL_FACTOR) {
            Chronos.logTimeElapsedForMark(getChronosPrefix() + targetAction);
            main.system.auxiliary.LogMaster.log(1, "*** CRITICAL_FAIL TimeLimitForPathBuilding "
                    + targetAction);
            return false;
        }
        if (paths.size() > 0) {
            // if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH_STEP,
            // getChronosPrefix() + targetAction))
            // return false; TODO ???
            if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH, getChronosPrefix() + base_choice))
                return false;
            if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH_CELL, getChronosPrefix()
                    + targetCoordinate))
                return false;
            if (!TimeLimitMaster.checkTimeLimit(METRIC.ACTION, getChronosPrefix() + targetAction))
                return false;
        }
        if (checkFailed())
            return true;

        List<Choice> choices = getChoices();
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
            List<Action> sequence = ActionSequenceConstructor.getTurnSequence(targetAction);
            for (Action a : sequence)
                path.add(new Choice(c_coordinate, a));
        }
        unit.setFacing(originalFacing);
        unit.setCoordinates(originalCoordinate);
    }

    private void clonePath() {
        path = new ActionPath(path);
        // reset();
    }

    private void back() {
        if (!path.getChoices().isEmpty())
            path.getChoices().remove(path.getChoices().size() - 1);
        if (previousCoordinate != null)
            c_coordinate = previousCoordinate;
        previousCoordinate = null;
        if (previousFacing != null)
            c_facing = previousFacing;
        previousFacing = null;
    }

    private void finished() {
        if (checkDuplicate())
            return;
        paths.add(path);
        int result = getPathPriority();
        log(result);

        if (bestResult == null)
            bestResult = result;
        else {
            if (result > bestResult)
                bestResult = result;
        }
        // Integer pathPriority = PriorityManager.getPathPriority(path);
        // Costs c;
        // c.compare(bestCost);
        // if (pathPriority >= bestPathPriority)
        // this.bestPathPriority = pathPriority;

    }

    private void log(int result) {
        main.system.auxiliary.LogMaster.log(LOG_CHANNELS.PATHING_DEBUG, result
                + " priority for path: " + path);
    }

    private boolean checkFailed() {
        failed = false;
        if (path.getChoices().size() > MAX_PATH_SIZE)
            failed = true;
        else {
            if (bestResult == null)
                return failed;
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
        int result = PriorityManager.getCostFactor(cost, unit);
        try {
            // result += getAoOPenalty(); TODO instant atks check !
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
        return ActionSequenceConstructor.getTotalCost(path.getActions());
    }

    public ActionPath getPathByPriority(List<Coordinates> targetCoordinates) {
        paths = build(targetCoordinates);
        if (paths.isEmpty())
            return null;
        return paths.get(0);
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
        return filteredPaths;
    }

    private List<ActionPath> buildPaths(List<Coordinates> targetCoordinates) { // List<ActionSequence>

        Chronos.mark(getChronosPrefix() + targetAction); // getSequences
        paths = new LinkedList<>();
        sneakCells = new LinkedList<>();
        nonSneakCells = new LinkedList<>();
        bestResult = null;
        targetCells = targetCoordinates;
        // depth-first search

        loop:
        for (Coordinates dest : targetCells) {
            Chronos.mark(getChronosPrefix() + dest);
            targetCoordinate = dest;
            reset();
            firstStep = true;
            path = new ActionPath(originalCoordinate); // TODO first step must
            // be
            // activateable!
            List<Choice> choices = getChoices();
            firstStep = false;
            for (Choice choice : choices) {
                base_choice = choice;
                Chronos.mark(getChronosPrefix() + base_choice);
                path = new ActionPath(targetCoordinate);
                if (!step(choice)) {
                    break;
                }
                if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_RESULTS)
                    Chronos.logTimeElapsedForMark(getChronosPrefix() + choice); // TODO
                // mark
                // removed???
            }
            if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_BASIC)
                Chronos.logTimeElapsedForMark(getChronosPrefix() + dest);
        }
        if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_BASIC)
            Chronos.logTimeElapsedForMark(getChronosPrefix() + targetAction);
        return paths;
    }

    private String getChronosPrefix() {
        return "Building path for ";
    }

    private void filterPaths() {
        filteredPaths = new LinkedList<>();
        for (ActionPath p : paths) {
            int priority = p.getPriority();
            if (bestResult - priority >= FILTER_THRESHOLD)
                continue;
            filteredPaths.add(p);
        }

    }

    public class Choice {
        private Coordinates coordinates;
        private Coordinates prevCoordinates;
        private List<Action> actions;
        private Boolean[] turns;

        public Choice(Coordinates targetCoordinate, Action... actions) {
            this(targetCoordinate, null, actions);
        }

        public Choice(Coordinates targetCoordinate, Coordinates prevCoordinates, Action... actions) {
            this.coordinates = targetCoordinate;
            this.prevCoordinates = prevCoordinates;
            this.actions = new LinkedList<>(Arrays.asList(actions));
        }

        public boolean equals(Object obj) {
            if (obj instanceof Choice) {
                Choice choice = (Choice) obj;
                if (choice.getCoordinates().equals(getCoordinates()))
                    if (choice.getActions().equals(getActions()))
                        return true;
            }
            return false;
        }

        public Boolean[] getTurns() {
            if (actions.size() == 1 || turns != null)
                return turns;
            try {
                List<Boolean> list = new LinkedList<>();
                for (Action a : actions) {
                    DC_ActiveObj active = a.getActive();
                    if (active.getName().contains("lockwise")) {
                        if (!active.isConstructed())
                            active.construct();
                        for (Effect e : active.getAbilities().getEffects()) {
                            if (e instanceof ChangeFacingEffect) {
                                list.add(((ChangeFacingEffect) e).isClockwise());
                            }
                        }

                    }
                }
                turns = list.toArray(new Boolean[list.size()]);
                return turns;
            } catch (Exception e) {

            }
            return null;
        }

        public String toString() {
            if (actions.size() == 1)
                return actions.get(0).getActive().getName() + " to " + coordinates;
            return StringMaster.joinStringList(StringMaster.convertToNameIntList(ActionManager
                    .getActionObjectList(actions)), ", ")
                    + " to " + coordinates;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public List<Action> getActions() {
            return actions;
        }

        public Coordinates getPrevCoordinates() {
            return prevCoordinates;
        }

        public void setPrevCoordinates(Coordinates prevCoordinates) {
            this.prevCoordinates = prevCoordinates;
        }

    }
}
