package main.game.battlecraft.logic.battlefield;

import main.ability.effects.oneshot.move.MoveEffect;
import main.ability.effects.oneshot.move.SelfMoveEffect;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.elements.Filter;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.DC_Cell;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.AiActionFactory;
import main.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import main.game.battlecraft.ai.tools.path.ActionPath;
import main.game.battlecraft.ai.tools.path.PathBuilder;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.bf.*;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.Coordinates.UNIT_DIRECTION;
import main.game.bf.pathing.Path;
import main.game.bf.pathing.PathingManager;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.entity.active.DC_ActionManager;
import main.game.battlecraft.rules.mechanics.CollisionRule;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.CustomValueManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.entity.ConditionMaster;
import main.system.entity.FilterMaster;
import main.system.math.PositionMaster;

import java.util.*;

public class DC_MovementManager implements MovementManager {

    private static DC_MovementManager instance;
    Map<Unit, List<ActionPath>> pathCache = new HashMap<>();
    private DC_Game game;
    private PathingManager pathingManager;

    public DC_MovementManager(DC_Game game) {
        this.game = game;
        setPathingManager(new PathingManager());
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
            e.printStackTrace();
        }
        return active.getOwnerObj().getCoordinates();
    }

    public static Action getFirstAction(Unit unit, Coordinates coordinates) {
        FACING_SINGLE relative = FacingMaster.getSingleFacing(unit.getFacing(),
                unit.getCoordinates(), coordinates);
        if (relative == FACING_SINGLE.IN_FRONT) {
            return AiActionFactory.newAction("Move", unit.getAI());
        }
        boolean left = (unit.getFacing().isVertical()) ?
                PositionMaster.isToTheLeft(unit.getCoordinates(), coordinates)
                : PositionMaster.isAbove(unit.getCoordinates(), coordinates);
        if (unit.getFacing().isMirrored()) {
            left = !left;
        }

        return AiActionFactory.newAction("Move " + (left ? "Left" : "Right"), unit.getAI());
//        List<ActionPath> paths = instance.buildPath(unit, coordinates);
//            if (!ListMaster.isNotEmpty(paths)) {
//            return  null ;
//            }
//        ActionPath path =paths.get(0);
//        for (ActionPath portrait : paths){
//            if (portrait.getActions().get(0).getActive().isTurn())
//            {
//                path = portrait;
//                break;
//            }
//        }
//        return path.getActions().get(0);
    }

    public static List<DC_ActiveObj> getMoves(Unit unit) {
        List<DC_ActiveObj> moveActions = new ArrayList<>();
        DequeImpl<DC_UnitAction> actions = unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_MOVE);
        if (actions != null) {
            moveActions = new ArrayList<>(Arrays.asList(actions.toArray(new DC_ActiveObj[actions
                    .size()])));
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

        return (me) ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
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

    public List<ActionPath> buildPath(Unit unit, Coordinates coordinates) {
        List<DC_ActiveObj> moves = getMoves(unit);
        PathBuilder builder = PathBuilder.getInstance().init
                (moves, new Action(unit.getAction("Move")));
        List<ActionPath> paths = builder.build(new ListMaster<Coordinates>().getList(coordinates));
        if (paths.isEmpty()) {
            return null;
        }
        pathCache.put(unit, paths);
        return paths;
    }

    @Override
    public void moveTo(Obj objClicked) {
        moveTo(objClicked.getCoordinates());

    }

    public void moveTo(Coordinates coordinates) {
        Unit unit = game.getManager().getActiveObj();
        List<ActionPath> paths = buildPath(unit, coordinates);
        if (paths == null) {
            Coordinates adjacentCoordinate = coordinates.getAdjacentCoordinate(DirectionMaster
                    .getRelativeDirection(unit.getCoordinates(), coordinates));
            if (DialogMaster.confirm("No path could be built to " + coordinates
                    + "; proceed to the closest cell? - " + adjacentCoordinate)) {
                moveTo(adjacentCoordinate);
            } else {
                return;
            }
        }
        pathCache.get(unit);
        Action action = null;
        for (ActionPath path : paths) {
            if (DialogMaster.confirm("Path built to : " + path + "\nExecute "
                    + path.getActions().get(0).toString() + "?")) {
                action = path.getActions().get(0);
                break;
            }
        }
        if (action == null) {
            pathCache.remove(unit);
            return;
        }
        // ActionAnimation anim = new ActionAnimation(action);
        // anim.start();

        Ref ref = unit.getRef().getCopy();
        if (action.getActive().isMove()) {
            ref.setTarget(game.getCellByCoordinate(coordinates).getId());
        }
        action.getActive().activatedOn(ref);
        action.getActive().actionComplete();
    }

    @Deprecated
    public boolean canMove(Obj obj, Obj cell) {
        Unit unit = (Unit) obj;
        int moves = 0;
        int actions = unit.getIntParam(PARAMS.C_N_OF_ACTIONS);
        if (moves == 0 || actions == 0) {
            return false;
        }

        Path path = getPath(unit, cell);
        if (path == null) {
            return false;
        }
        double cost = path.getCost();
        if (cost == PathingManager.NO_PATH) {
            return false;
        }

        return Math.min(moves, actions) >= getIntegerCost(cost);
        // return cost

        // Coordinates objP = new Coordinates(unit.getX(), unit.getY());
        // Coordinates cellP = new Coordinates(cell.getX(), cell.getY());
        //
        // // straight line only
        // if (!PositionMaster.inLine(unit, cell))
        // if (!unit.isAgile())
        // return false;
        //
        // if (PositionMaster.inLine(unit, cell))
        // if (!noObstacles(objP, cellP))
        // if (!unit.isFlying())
        // return false;
        //
        // if (Math.min(moves, actions) < PositionMaster.getDistance(unit,
        // cell))
        // return false;
        // return true;
    }

    @Override
    public Path getPath(Obj unit, Obj cell) {
        return getPath((Unit) unit, cell);
    }

    public Path getPath(Unit unit, Obj cell) {
        if (getPathingManager().isOccupied(cell.getCoordinates())) {
            return null;
        }
        Coordinates c1 = new Coordinates(unit.getX(), unit.getY());
        Coordinates c2 = new Coordinates(cell.getX(), cell.getY());

        return getPathingManager().getPath(unit.isFlying(), unit.isAgile(), c1, c2);
    }

    @Override
    public boolean noObstacles(Coordinates objCoordinates, Coordinates cellCoordinates) {
        if (objCoordinates.x == cellCoordinates.x) {
            return getGrid().noObstaclesX(objCoordinates.x, objCoordinates.y, cellCoordinates.y);
        }
        if (objCoordinates.y == cellCoordinates.y) {
            return getGrid().noObstaclesY(objCoordinates.y, objCoordinates.x, cellCoordinates.x);
        }
        return true;
    }

    @Override
    public BattleFieldGrid getGrid() {
        return game.getBattleField().getGrid();
    }

    @Override
    public boolean move(Obj obj, Coordinates c, boolean free, Path path) {
        return free;
        // return move((DC_HeroObj) obj,
        // (DC_Cell) getGrid().getCellCompMap().getOrCreate(c).getObj(), free,
        // path, null, null);
    }

    @Override
    public boolean move(Obj obj, Coordinates c) {
        return false;
        // return move((DC_HeroObj) obj,
        // (DC_Cell) getGrid().getCellCompMap().getOrCreate(c).getObj(), false,
        // null, null, null);
    }

    @Override
    public boolean move(Obj obj, Coordinates c, boolean free, MOVE_MODIFIER mod, Ref ref) {
        return move((Unit) obj, (DC_Cell) getGrid().getCell(c), free, null, mod, ref);
    }

    public boolean move(Unit obj, DC_Cell cell, boolean free, Path path, MOVE_MODIFIER mod,
                        Ref ref) {
        // if (path == null) {
        // if (!free)
        // path = getPath(obj, cell); // TODO just preCheck if it's blocked
        // }
        // if (!free)
        // if (!canMove(obj, cell))
        // return false;
        Ref REF = new Ref(obj.getGame());
        REF.setTarget(cell.getId());
        REF.setSource(obj.getId());
        LogMaster.log(LogMaster.MOVEMENT_DEBUG, "Moving " + obj + " to " + cell);
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_BEING_MOVED, REF);
        if (!game.fireEvent(event)) {
            return false;
        }

        // double cost = (!free) ? path.traverse(obj) : 0;
        // int _cost = getIntegerCost(cost);

        // for AI simulation only!
        // obj.modifyParameter(PARAMS.C_N_OF_MOVES, -_cost, 0);
        // obj.modifyParameter(PARAMS.C_N_OF_ACTIONS, -_cost, 0);

        Coordinates c =cell.getCoordinates() ;
        if (mod != MOVE_MODIFIER.TELEPORT) { // TODO UPDATE!
            Unit moveObj =   (Unit) getGrid().getObj(cell.getCoordinates());
            if (moveObj != null) {
                if (ref.getActive() instanceof DC_ActiveObj) {
                    DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                    if (moveObj instanceof Unit) {
                        Unit heroObj = moveObj;
                      c= CollisionRule.collision(ref, activeObj, moveObj, heroObj,
                                false, activeObj.getIntParam(PARAMS.FORCE));
                        if (c == null) {// TODO UPDATE!
                            return true; // displaced by Collision rule?
                        }
                    }
                }
            }
        }
        if (obj.isDead()) {
            return false;
        }
        int x = cell.getX();
        int y = cell.getY();
        if (!game.getRules().getEngagedRule().unitMoved(obj, x, y)) {
            return false;
        }
        obj.setCoordinates(c);

        event = new Event(STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING, REF);
        return game.fireEvent(event);
    }

    private boolean checkCanMove(Unit obj, DC_Cell cell, MOVE_MODIFIER mod) {
        if (pathingManager.isOccupied(cell.getCoordinates())) {
            return false;
        }

        if (mod == MOVE_MODIFIER.TELEPORT) {
            return true;
        }
        if (pathingManager.isAdjacent(obj, cell)) {
            return true;
        }

        if (PositionMaster.inLine(obj, cell)) {
            if (mod == MOVE_MODIFIER.FLYING) {
                return true;
            }
            if (PositionMaster.checkNoObstaclesInLine(obj, cell)) {

            }
        } else {
            // build path?
        }

        return false;
    }

    @Override
    public int getIntegerCost(double cost) {
        if (cost > 1 && cost < 2) {
            return 2;
        }

        return (int) Math.round(cost);

    }

    @Override
    public void setGrid(BattleFieldGrid grid) {
        getPathingManager().setGrid(grid);
    }

    @Override
    public SwingBattleField getBf() {
        return game.getBattleField();
    }

    @Override
    public int getDistance(Obj obj1, Obj obj2) {

        return PositionMaster.getDistance(obj1, obj2);
    }

    @Override
    public List<Obj> getAdjacentObjs(Obj unit, boolean cell) {
        return getPathingManager().getAdjacentObjs(unit.getCoordinates(), cell);
    }

    @Override
    public List<Obj> getAdjacentObjs(Coordinates coordinates, boolean cell) {

        return getPathingManager().getAdjacentObjs(coordinates, cell);
    }

    @Override
    public Set<Obj> getCellsInRadius(Obj targetUnit, int i) {
        return FilterMaster.getCellsInRadius(targetUnit, i);
    }

    @Override
    public List<Obj> getAdjacentEnemies(Obj unit) {

        return new Filter<Obj>(unit.getRef(), ConditionMaster.getEnemyCondition())
                .filter(getAdjacentObjs(unit, false));

    }

    public boolean isAdjacent(Coordinates c1, Coordinates c2) {
        return getPathingManager().isAdjacent(c1, c2);
    }

    public boolean isAdjacent(Obj obj1, Obj obj2) {
        return getPathingManager().isAdjacent(obj1, obj2);
    }

    public Obj getCell(Coordinates c1) {
        return getPathingManager().getCell(c1);
    }

    @Override
    public PathingManager getPathingManager() {
        return pathingManager;
    }

    public void setPathingManager(PathingManager pathingManager) {
        this.pathingManager = pathingManager;
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
                    CustomValueManager.getVarEnumCustomValueName(MOVE_TEMPLATES.class));
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
