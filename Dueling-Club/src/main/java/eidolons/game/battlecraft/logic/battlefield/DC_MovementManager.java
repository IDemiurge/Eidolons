package eidolons.game.battlecraft.logic.battlefield;

import eidolons.ability.conditions.req.CellCondition;
import eidolons.ability.conditions.shortcut.PushableCondition;
import eidolons.ability.effects.oneshot.move.MoveEffect;
import eidolons.ability.effects.oneshot.move.SelfMoveEffect;
import eidolons.content.DC_ValueManager;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.ai.tools.path.PathBuilder;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.core.ActionInput;
import eidolons.game.core.game.DC_BattleFieldGrid;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.MovementManager;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.bf.directions.UNIT_DIRECTION;
import main.game.logic.action.context.Context;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;

import java.util.*;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

public class DC_MovementManager implements MovementManager {

    private static DC_MovementManager instance;
    Map<Unit, List<ActionPath>> pathCache = new HashMap<>();
    private final DC_Game game;
    public static Coordinates playerDestination;
    public static boolean outsideInterrupt;
    public static List<Coordinates> playerPath;

    public DC_MovementManager(DC_Game game) {
        this.game = game;
        instance = this;
    }

    public static Coordinates getMovementDestinationCoordinate(DC_ActiveObj active) {
        try {
            MoveEffect effect = (MoveEffect) EffectFinder.getEffectsOfClass(active.getAbilities(),
                    MoveEffect.class).get(0);
            effect.setRef(active.getRef());
            if (effect instanceof SelfMoveEffect) {
                SelfMoveEffect selfMoveEffect = (SelfMoveEffect) effect;
                return selfMoveEffect.getCoordinates(); // TODO
            }
            return effect.getRef().getTargetObj().getCoordinates();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return active.getOwnerUnit().getCoordinates();
    }

    public static Action getFirstAction(Unit unit, Coordinates coordinates) {
        FACING_SINGLE relative = FacingMaster.getSingleFacing(unit.getFacing(),
                unit.getCoordinates(), coordinates);
        if (relative == FACING_SINGLE.IN_FRONT) {
            if (!new CellCondition(UNIT_DIRECTION.AHEAD).check(unit))
                return null;
            return AiActionFactory.newAction("Move", unit.getAI());
        }
        boolean wantToMoveLeft = (unit.getFacing().isVertical()) ?
                PositionMaster.isToTheLeft(unit.getCoordinates(), coordinates)
                : PositionMaster.isAbove(unit.getCoordinates(), coordinates);
        if (!unit.getFacing().isCloserToZero()) {
            wantToMoveLeft = !wantToMoveLeft;
        }

        if (!new CellCondition(wantToMoveLeft ? UNIT_DIRECTION.LEFT : UNIT_DIRECTION.RIGHT).check(unit))
            return null;
        return AiActionFactory.newAction("Move " + (wantToMoveLeft ? "Left" : "Right"), unit.getAI());
    }

    public static List<DC_ActiveObj> getMoves(Unit unit) {
        List<DC_ActiveObj> moveActions = new ArrayList<>();
        DequeImpl<DC_UnitAction> actions = unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_MOVE);
        if (actions != null) {
            moveActions = new ArrayList<>(Arrays.asList(actions.toArray(new DC_ActiveObj[0])));
        }
        if (moveActions.isEmpty()) {
            moveActions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.ADDITIONAL_MOVE));
        } else {
            for (DC_UnitAction a : unit.getActionMap().get(ActionEnums.ACTION_TYPE.ADDITIONAL_MOVE)) {
                String name = a.getName();
                switch (name) { // have a switch to turn off all default moves!
                    case "Clumsy Leap":
                        if (!DataManager.toStringList(moveActions).contains("Leap")) {
                            moveActions.add(a);
                        }
                        continue;
                    case "Move Right":
                    case "Move Left":
                        if (!DataManager.toStringList(moveActions).contains("Sidestep Right")) {
                            moveActions.add(a);
                        }
                        continue;
                }
                moveActions.add(a);

            }
        }

        moveActions.addAll(AiUnitActionMaster.getSpells(AiEnums.AI_LOGIC.MOVE, unit));

        moveActions = DC_ActionManager.filterActionsByCanBePaid(moveActions);
        return moveActions;
    }

    public static FACING_DIRECTION getDefaultFacingDirection(boolean me) {

        return (me) ? main.game.bf.directions.FACING_DIRECTION.NORTH : main.game.bf.directions.FACING_DIRECTION.SOUTH;
    }

    @Override
    public void promptContinuePath(Obj activeUnit) {
        List<ActionPath> list = pathCache.get(activeUnit);
        if (list == null) {
            return;
        }
        ActionPath path = list.get(0);

        moveTo(path.getTargetCoordinates());
    }

    public List<ActionPath> getAutoPath(Obj activeUnit) {
        return pathCache.get(activeUnit);
    }

    public void cancelAutomove(Obj activeUnit) {
        pathCache.remove(activeUnit);
        playerDestination = null;
        playerPath = null;
    }

    public List<ActionPath> buildPath(Unit unit, Coordinates coordinates) {
        List<DC_ActiveObj> moves = getMoves(unit);

        // ActionPath path = game.getAiManager().getStarBuilder().getPath(unit.getCoordinates(), coordinates);
        // return ImmutableList.of(path);
        PathBuilder builder = PathBuilder.getInstance().init
                (moves, new Action(unit.getAction("Move")));
        builder.simplified = true;
        List<ActionPath> paths = builder.build(new ListMaster<Coordinates>().getList(coordinates));
        builder.simplified = false;
        if (paths.isEmpty()) {
            return null;
        }
        return paths;
    }

    @Override
    public void moveTo(Obj objClicked) {
        moveTo(objClicked.getCoordinates());

    }

    public void moveTo(Coordinates coordinates) {
        playerDestination = coordinates;
        //highlight the destination with overlays
    }

    private boolean checkInterruption(Unit unit) {
        if (outsideInterrupt) {
            outsideInterrupt = false;
            return true;
        }
        /*
                check status

         */
        return false;
    }

    public boolean isValidDestination(Coordinates coordinates, Unit unit) {
        if (coordinates == null)
            return false;

        return !unit.getCoordinates().equals(coordinates);
    }

    public boolean checkContinueMove() {
        if (playerDestination == null)
            return false;
        Unit unit = game.getManager().getActiveObj();
        if (checkInterruption(unit)) {
            return false;
        }
        if (!isValidDestination(playerDestination, unit)) {
            return false;
        }
        try {
            List<ActionPath> paths = pathCache.get(unit);
            //TODO IDEA: plot the path on the grid for PC to see!
            if (paths == null) {
                paths = buildPath(unit, playerDestination);
                pathCache.put(unit, paths);
            }
            if (paths == null) {
                log(1, "Cannot find path to " + playerDestination);
                game.getLogManager().log("Cannot find path to " + playerDestination);
                return false;
            }
            List<Action> actions = null;
            for (ActionPath path : paths) {
                if (!checkPathStillValid(path, playerDestination)) {
                    continue;
                }
                actions = path.choices.remove(0).getActions();
                playerPath = path.choices.stream().map(c -> c.getCoordinates()).collect(Collectors.toList());
                break;
            }
            if (actions == null) {
                pathCache.remove(unit);
                return false;
            }
            for (Action action : actions) {
                Context context = new Context(unit.getRef());
                if (action.getTarget() != null) {
                    context.setTarget(action.getTarget().getId());
                }
                unit.getGame().getGameLoop().
                        actionInput(new ActionInput(action.getActive(), context), true);
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        return true;

    }

    private boolean checkPathStillValid(ActionPath path, Coordinates playerDestination) {
        return true;
    }

    @Override
    public boolean canMove(Entity obj, Coordinates c) {
        return game.getRules().getStackingRule().canBeMovedOnto(obj, c);
    }

    @Override
    public DC_BattleFieldGrid getGrid() {
        return game.getGrid();
    }


    @Override
    public boolean move(Obj obj, Coordinates c, boolean free, MOVE_MODIFIER mod, Ref ref) {
        return move((BattleFieldObject) obj, getGrid().getCell(c), free, mod, ref);
    }

    @Override
    public boolean move(Obj obj, Coordinates c) {
        return move((BattleFieldObject) obj, getGrid().getCell(c), false, MOVE_MODIFIER.NONE, obj.getRef());
    }

    public boolean checkPushByMovement(BattleFieldObject obj, Coordinates c) {
        for (BattleFieldObject object : game.getObjectsOnCoordinateNoOverlaying(c)) {
            if (object instanceof Structure) {
                if (PushableCondition.isPushable((Structure) object, (Unit) obj)) {
                    //                    if (obj.getIntParam(PARAMS.WEIGHT)>=object.getIntParam(PARAMS.WEIGHT))
                    {
                        push(obj, object);
                    }
                    //only one?
                    return true;
                }
            }
        }


        return false;

    }

    private void push(BattleFieldObject obj, BattleFieldObject object) {
        DIRECTION d = DirectionMaster.getRelativeDirection(obj, object);
        Coordinates c = object.getCoordinates().getAdjacentCoordinate(d);
        if (c != null) {
            move(object, c);
        }

    }

    public boolean move(BattleFieldObject obj, DC_Cell cell, boolean free, MOVE_MODIFIER mod,
                        Ref ref) {
        Ref REF = ref.getCopy();// new Ref(obj.getGame());
        REF.setTarget(cell.getId());
        REF.setSource(obj.getId());
        log(LogMaster.MOVEMENT_DEBUG, "Moving " + obj + " to " + cell);
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_BEING_MOVED, REF);
        if (!game.fireEvent(event)) {
            return false;
        }

        Coordinates c = cell.getCoordinates();
        if (mod != MOVE_MODIFIER.TELEPORT) { // TODO UPDATE!
            //            if (RuleKeeper.isRuleOn(RuleKeeper.RULE.COLLISION)) {
            //            BattleFieldObject moveObj = (BattleFieldObject)game.getObjMaster().getObjectByCoordinate( cell.getCoordinates(), false);
            //            if (moveObj != null) {
            //                if (ref.getActive() instanceof DC_ActiveObj) {
            //                    DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
            //                    if (moveObj instanceof Unit) {
            //                        BattleFieldObject heroObj = moveObj;
            //                        c = CollisionRule.collision(ref, activeObj, moveObj, heroObj,
            //                         false, activeObj.getIntParam(PARAMS.FORCE));
            //                        if (c == null) {// TODO UPDATE!
            //                            return true; // displaced by Collision rule?
            //                        }
            //                    }
            //                }
            //            }
            //            }
        }
        if (obj.isDead()) {
            return false;
        }

        if (!game.getRules().getStackingRule().canBeMovedOnto(obj, c)) {
            return false;
        }
        //        if (checkPushByMovement(obj, c)) {
        //TODO
        //        }


        //
        //   if (game.getObjectByCoordinate(c) instanceof BattleFieldObject) {
        //            BattleFieldObject bfObj = (BattleFieldObject) game.getObjectByCoordinate(c);
        //            if (!bfObj.isDead())
        //                if (bfObj.isWall()) {
        //                    return false;
        //                }
        //        }
        if (obj instanceof Unit) {
            if (!game.getRules().getEngagedRule().unitMoved((Unit) obj, c.x, c.y)) {
                return false;
            }
        }
        obj.setCoordinates(c);

        return moved(obj, cell, false);
    }

    public boolean moved(BattleFieldObject obj, DC_Cell cell, boolean quiet) {
        Ref ref = Ref.getSelfTargetingRefCopy(obj);
        ref.setQuiet(quiet);
        return moved(obj, cell, ref);
    }

    public void moved(Unit unit, boolean quiet) {
        moved(unit, game.getCellByCoordinate(unit.getCoordinates()), quiet);
    }

    public boolean moved(BattleFieldObject obj, DC_Cell cell, Ref REF) {
        if (!REF.isQuiet())
        if (obj instanceof Unit) {
            game.getDungeonMaster().getTrapMaster().unitMoved((Unit) obj);
            game.getDungeonMaster().getPortalMaster().unitMoved((Unit) obj);
        }
        cell.setObjectsModified(true);
        if (obj.isPlayerCharacter()) {
            LevelStruct struct = game.getDungeonMaster().getStructMaster().getLowestStruct(obj.getCoordinates());
            String background = struct.getPropagatedValue("background");
            if (!ScreenMaster.getScreen().getBackgroundPath().equalsIgnoreCase(background)) {
                GuiEventManager.trigger(GuiEventType.UPDATE_DUNGEON_BACKGROUND, background);
            }
        }
        if ( REF.isQuiet())
            return true;
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING, REF);
        return game.fireEvent(event);
    }

    @Override
    public int getDistance(Obj obj1, Obj obj2) {

        return PositionMaster.getDistance(obj1, obj2);
    }

    @Override
    public Coordinates getTemplateMoveCoordinate(MOVE_TEMPLATES template, FACING_DIRECTION facing,
                                                 Obj obj, Ref ref) {
        // getOrCreate some caching to optimize this!
        UNIT_DIRECTION direction = template.getDirection();
        String range = template.getRange();
        Boolean selective = template.isSelectiveTargeting();

        if (template.getVarClasses() != null) {
            String vars = ref.getObj(KEYS.ACTIVE).getCustomProperty(
                    DC_ValueManager.getVarEnumCustomValueName(MOVE_TEMPLATES.class));
            List<String> varList = VariableManager.getVarList(vars);

            range = varList.get(0);
            if (varList.size() > 1) {
                direction = new EnumMaster<UNIT_DIRECTION>().retrieveEnumConst(
                        UNIT_DIRECTION.class, varList.get(1));
            }

            if (varList.size() > 2) {
                selective = new Boolean(varList.get(2));
            }

        }
        switch (template) {
            // some custom templates
        }
        if (selective == null) {
            selective = false;
        }
        if (selective) {
            // create filter by directions and range!
        } else {
            if (range.equals("1")) {
                return obj.getCoordinates().getAdjacentCoordinate(
                        DirectionMaster.getDirectionByFacing(facing, direction));
            }
            // preCheck int >= formla

        }

        return null;

    }


}
