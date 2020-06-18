package eidolons.game.battlecraft.ai.tools.path;

import eidolons.ability.conditions.special.SneakCondition;
import eidolons.ability.effects.oneshot.move.SelfMoveEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.ability.effects.Effect;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.math.PositionMaster;

import java.util.*;

/**
 * Created by JustMe on 4/13/2017.
 */
public class PathChoiceMaster {
    protected PathBuilder pathBuilder;
    protected List<DC_ActiveObj> moveActions; // only special here?
    private DC_UnitAction stdMove;
    private final ArrayList<Object> sneakCells;
    private final ArrayList<Object> nonSneakCells;
    private Unit unit;
    private Action targetAction;
    private Coordinates targetCoordinate;

    private boolean firstStep;

    public PathChoiceMaster(PathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
        sneakCells = new ArrayList<>();
        nonSneakCells = new ArrayList<>();
    }

    public PathChoiceMaster init(Unit unit, Action targetAction, Coordinates targetCoordinate,
                                 List<DC_ActiveObj> moveActions) {
        this.unit = unit;
        this.targetAction = targetAction;
        this.targetCoordinate = targetCoordinate;
        this.moveActions = moveActions;
        stdMove = unit.getAction(ActionEnums.STD_ACTIONS.Move.name());
        return this;
    }


    List<Choice> getChoices(boolean simplified, ActionPath path, Coordinates c_coordinate, Coordinates targetCoordinate, FACING_DIRECTION c_facing) {
        Chronos.mark("Finding choices for " + path);
        pathBuilder.adjustUnit();

        List<Choice> choices = new ArrayList<>();
        for (Coordinates c : getDefaultCoordinateTargets(path, c_coordinate)) {
            Choice stdMoveChoice = constructStdMoveChoice(c, c_coordinate, c_facing);
            if (stdMoveChoice != null) {
                choices.add(stdMoveChoice);
            }
        }
        Chronos.mark("Finding custom choices for " + path);

        List<Choice> specialChoices = new ArrayList<>();
        if (ListMaster.isNotEmpty(moveActions)) {
            // add special
            // will need to remove actions from list when used? preCheck CD

            for (DC_ActiveObj a : moveActions) {

                if (path.hasAction(a)) {
                    if (a.getIntParam(PARAMS.COOLDOWN) >= 0) {
                        continue;
                    }
                }
                Targeting targeting = a.getTargeting();
                Collection<Obj> objects = null;
                if (targeting != null)
                if (targeting instanceof FixedTargeting) {
                    Targeting t = a.getAbilities().getTargeting();
                    if (t != null) {
                        objects = t.getFilter().getObjects(a.getRef());
                    }
                    Effect e = a.getAbilities().getEffects().getEffects().get(0);
                    e.setRef(unit.getRef());
                    if (e instanceof SelfMoveEffect) {
                        try {
                            Coordinates coordinates = ((SelfMoveEffect) e).getCoordinates();
                            if (coordinates != null) {
                                objects = new ArrayList<>(Collections.singletonList(unit
                                        .getGame().getCellByCoordinate(coordinates)));
                            }
                        } catch (Exception ex) {
                            main.system.ExceptionMaster.printStackTrace(ex);
                        }
                    }
                } else {
                    pathBuilder.adjustUnit();
                    objects = targeting.getFilter().getObjects(a.getRef());
                }
                if (objects != null) {

                    List<Choice> choicesForAction = new ArrayList<>();
                    for (Object obj : objects) {
                        if (obj instanceof DC_Cell) {
                            Coordinates coordinates = ((DC_Cell) obj).getCoordinates();
                            // if (a.getName().equals("Clumsy Leap"))
                            if (PositionMaster.getDistance(coordinates, c_coordinate) > Math.max(1,
                             a.getIntParam(PARAMS.RANGE))) {
                                continue;
                            }

                            if (PositionMaster.getDistance(coordinates, targetCoordinate) > PositionMaster
                             .getDistance(c_coordinate, targetCoordinate)) {
                                continue; // TODO will this not eliminate good
                            }
                            // choices?

                            Ref ref = unit.getRef().getCopy();
                            ref.setTarget(((DC_Cell) obj).getId());
                            Choice choice = new Choice(coordinates, c_coordinate,
                             new Action(a, ref));
                            choicesForAction.add(choice);
                        }
                    }

                    Chronos.mark("Filter custom choices for " + a);
                    specialChoices.addAll(filterSpecialMoveChoices(choicesForAction, a, c_coordinate, path));
                    Chronos.logTimeElapsedForMark("Filter custom choices for " + a);
                }
                // if (choices.size() > 1)

            }
        }
        Chronos.logTimeElapsedForMark("Finding custom choices for " + path);
        choices.addAll(specialChoices);
        Chronos.mark("Sort choices");
        sortChoices(choices);
        Chronos.logTimeElapsedForMark("Sort choices");

        // resetUnit();// TODO is that right?
        Chronos.logTimeElapsedForMark("Finding choices for " + path);
        // Chronos.mark("Sorting choices for " + path);

        // choices.addAll(stdChoices);
        // Chronos.logTimeElapsedForMark("Sorting choices for " + path);
        return choices;
    }


    private Choice constructStdMoveChoice(Coordinates targetCoordinate, Coordinates c_coordinate, FACING_DIRECTION c_facing) {
        FACING_SINGLE facing = FacingMaster.getSingleFacing(c_facing, c_coordinate,
         targetCoordinate);
        Action moveAction = getMoveAction();
        if (facing == UnitEnums.FACING_SINGLE.IN_FRONT) {
            if (firstStep) {
                if (!moveAction.canBeActivated()) {
                    return null;
                }
            }
            return new Choice(targetCoordinate, c_coordinate, moveAction);
        }
        pathBuilder.adjustUnit();
        Collection<Action> actions = pathBuilder.getTurnSequenceConstructor().getTurnSequence(
         UnitEnums.FACING_SINGLE.IN_FRONT, unit, targetCoordinate);
        actions.add(moveAction);
        // resetUnit();// TODO is that right?
        Choice choice = new Choice(targetCoordinate, c_coordinate, actions
         .toArray(new Action[0]));

        return choice;
    }

    private void sortChoices(List<Choice> choices) {
        Collections.sort(choices, getSorter());

    }

    private Action getMoveAction() {
        return new Action(stdMove);
    }

    private Comparator<? super Choice> getSorter() {
        return new Comparator<Choice>() {
            @Override
            public int compare(Choice c1, Choice c2) {
                double distance = PositionMaster.getExactDistance(c1.getCoordinates(), pathBuilder.targetCoordinate);
                double distance2 = PositionMaster.getExactDistance(c2.getCoordinates(), pathBuilder.targetCoordinate);
                if (distance > distance2) {
                    return 1;
                }
                if (distance < distance2) {
                    return -1;
                }
                distance = c1.getActions().size();
                distance2 = c2.getActions().size();
                if (distance > distance2) {
                    return 1;
                }
                if (distance < distance2) {
                    return -1;
                }
                return 0;
            }
        };
    }

    private List<Choice> filterSpecialMoveChoices(List<Choice> choices, DC_ActiveObj a, Coordinates c_coordinate, ActionPath path) {
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

        List<Choice> filteredList = new ArrayList<>();
        for (Choice choice : choices) {
            Coordinates c = choice.getCoordinates();
            if (c.equals(c_coordinate)) {
                continue;
            }
            if (path.hasCoordinate(c)) {
                continue;
            }
            if (checkSneak(c)) // cache sneak cells?
            {
                filteredList.add(choice);
            } else if (PositionMaster.getDistance(coordinates, c) <= bestDistance_2
             || c.isAdjacent(targetAction.getTarget().getCoordinates())) {
                if (PositionMaster.getDistance(c_coordinate, c) <= bestDistance_1) {
                    filteredList.add(choice);
                }
            }
        }

        return filteredList;
    }

    private boolean checkSneak(Coordinates c) {
        if (nonSneakCells.contains(c)) {
            return false;
        }
        if (sneakCells.contains(c)) {
            return true;
        }
        unit.setCoordinates(c); // change facing
        // preCheck range
        if (PositionMaster.getDistance(targetAction.getTarget().getCoordinates(), c) > targetAction
         .getActive().getIntParam(PARAMS.RANGE)) {
            nonSneakCells.add(c);
            return false;
        }
        Ref ref = targetAction.getRef();
        ref.setTarget(targetAction.getTarget().getId());
        boolean result = new SneakCondition().preCheck(ref);

        pathBuilder.adjustUnit();
        if (result) {
            sneakCells.add(c);
        } else {
            nonSneakCells.add(c);
        }
        return result;
    }

    private List<Coordinates> getDefaultCoordinateTargets(ActionPath path, Coordinates c_coordinate) {

        List<Coordinates> list = new ArrayList<>();
        for (DIRECTION d : DIRECTION.values) {
            if (d.isDiagonal()) {
                continue;
            }
            Coordinates c = c_coordinate.getAdjacentCoordinate(d);
            if (c == pathBuilder.getPreviousCoordinate() || c == null) {
                continue;
            }
            if (path.hasCoordinate(c)) {
                continue;
            }

            if (!pathBuilder.checkEmpty(c)) {
                continue;
            }
            // if (FacingManager.getSingleFacing(c_facing, c_coordinate, c) !=
            // FACING_SINGLE.BEHIND)
            list.add(c);

        }
        return list;
    }

}
