package main.game.battlefield;

import main.ability.effects.MoveEffect;
import main.ability.effects.SelfMoveEffect;
import main.content.CONTENT_CONSTS.ACTION_TYPE;
import main.content.CONTENT_CONSTS.AI_LOGIC;
import main.content.PARAMS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.elements.Filter;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.DC_Cell;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.ActionManager;
import main.game.ai.tools.path.ActionPath;
import main.game.ai.tools.path.PathBuilder;
import main.game.ai.tools.target.EffectMaster;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;
import main.game.battlefield.pathing.Path;
import main.game.battlefield.pathing.PathingManager;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.rules.DC_ActionManager;
import main.rules.mechanics.CollisionRule;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.entity.ConditionMaster;
import main.system.CustomValueManager;
import main.system.entity.FilterMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.PositionMaster;

import java.util.*;

public class DC_MovementManager implements MovementManager {

    private static DC_MovementManager instance;
    Map<DC_HeroObj, List<ActionPath>> pathCache = new HashMap<>();
    private DC_Game game;
    private PathingManager pathingManager;

    public DC_MovementManager(DC_Game game) {
        this.game = game;
        setPathingManager(new PathingManager());
        instance = this;
    }

    public static Coordinates getMovementDestinationCoordinate(DC_ActiveObj active) {
        try {
            MoveEffect effect = (MoveEffect) EffectMaster.getEffectsOfClass(active.getAbilities(),
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
        return null;
    }

    public static Action getFirstAction(DC_HeroObj unit, Coordinates coordinates) {
        List<ActionPath> paths = instance.buildPath(unit, coordinates);
        ActionPath path = paths.get(0);
        return path.getActions().get(0);
    }

    public static List<DC_ActiveObj> getMoves(DC_HeroObj unit) {
        List<DC_ActiveObj> moveActions = new ArrayList<>();
        List<DC_UnitAction> actions = unit.getActionMap().get(ACTION_TYPE.SPECIAL_MOVE);
        if (actions != null) {
            moveActions = new ArrayList<>(Arrays.asList(actions.toArray(new DC_ActiveObj[actions
                    .size()])));
        }
        if (moveActions.isEmpty()) {
            moveActions.addAll(unit.getActionMap().get(ACTION_TYPE.ADDITIONAL_MOVE));
        } else {
            for (DC_UnitAction a : unit.getActionMap().get(ACTION_TYPE.ADDITIONAL_MOVE)) {
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

        moveActions.addAll(ActionManager.getSpells(AI_LOGIC.MOVE, unit));

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

    public List<ActionPath> buildPath(DC_HeroObj unit, Coordinates coordinates) {
        List<DC_ActiveObj> moves = getMoves(unit);
        PathBuilder builder = new PathBuilder(moves, new Action(unit.getAction("Move")));
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
        DC_HeroObj unit = game.getManager().getActiveObj();
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
        action.getActive().activate(ref);
        action.getActive().actionComplete();
    }

    @Deprecated
    public boolean canMove(Obj obj, Obj cell) {
        DC_HeroObj unit = (DC_HeroObj) obj;
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
        return getPath((DC_HeroObj) unit, cell);
    }

    public Path getPath(DC_HeroObj unit, Obj cell) {
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
        return move((DC_HeroObj) obj, (DC_Cell) getGrid().getCell(c), free, null, mod, ref);
    }

    public boolean move(DC_HeroObj obj, DC_Cell cell, boolean free, Path path, MOVE_MODIFIER mod,
                        Ref ref) {
        // if (path == null) {
        // if (!free)
        // path = getPath(obj, cell); // TODO just check if it's blocked
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

        int x = cell.getX();
        int y = cell.getY();
        if (mod != MOVE_MODIFIER.TELEPORT) { // TODO UPDATE!
            DC_HeroObj moveObj = (DC_HeroObj) getGrid().getObj(cell.getCoordinates());
            if (moveObj != null) {
                if (ref.getActive() instanceof DC_ActiveObj) {
                    DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                    if (moveObj instanceof DC_HeroObj) {
                        DC_HeroObj heroObj = moveObj;
                        Coordinates c = CollisionRule.collision(ref, activeObj, moveObj, heroObj,
                                false, activeObj.getIntParam(PARAMS.FORCE));
                        if (c != null) {// TODO UPDATE!
                            x = c.x;
                            y = c.y;
                        } else {
                            return true; // displaced by Collision rule?
                        }
                    }
                }
            }
        }
        if (obj.isDead()) {
            return false;
        }
        if (!game.getRules().getEngagedRule().unitMoved(obj, x, y)) {
            return false;
        }
        getBf().moveBattleFieldObj(obj, x, y);

        event = new Event(STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING, REF);
        return game.fireEvent(event);
    }

    private boolean checkCanMove(DC_HeroObj obj, DC_Cell cell, MOVE_MODIFIER mod) {
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
    public DC_BattleFieldGrid getGrid() {
        return ((DC_BattleField) getBf()).getGrid();
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
            // check int >= formla

        }

        return null;

    }

}
