package eidolons.game.battlecraft.logic.battlefield;

import eidolons.ability.conditions.req.CellCondition;
import eidolons.ability.effects.oneshot.move.MoveEffect;
import eidolons.ability.effects.oneshot.move.SelfMoveEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.ai.tools.path.PathBuilder;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.rules.mechanics.CollisionRule;
import eidolons.game.core.ActionInput;
import eidolons.game.core.game.DC_BattleFieldGrid;
import eidolons.game.core.game.DC_Game;
import eidolons.system.CustomValueManager;
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
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.bf.directions.UNIT_DIRECTION;
import main.game.logic.action.context.Context;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;

import java.util.*;

public class DC_MovementManager implements MovementManager {

    private static DC_MovementManager instance;
    Map<Unit, List<ActionPath>> pathCache = new HashMap<>();
    private DC_Game game;

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
    }

    public List<ActionPath> buildPath(Unit unit, Coordinates coordinates) {
        List<DC_ActiveObj> moves = getMoves(unit);
        PathBuilder builder = PathBuilder.getInstance().init
         (moves, new Action(unit.getAction("Move")));
        List<ActionPath> paths = builder.build(new ListMaster<Coordinates>().getList(coordinates));
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
        Unit unit = game.getManager().getActiveObj();
        List<ActionPath> paths = pathCache.get(unit);
        if (paths == null) {
            paths = buildPath(unit, coordinates);
            pathCache.put(unit, paths);
        }
        if (paths == null) {
            return;
        }
        Action action = null;
        for (ActionPath path : paths) {
            action = path.getActions().get(0);
            break;
        }
        if (action == null) {
            pathCache.remove(unit);
            return;
        }
        // ActionAnimation anim = new ActionAnimation(action);
        // anim.start();

        Context context = new Context(unit.getRef());
        if (action.getActive().isMove()) {
            context.setTarget(game.getCellByCoordinate(coordinates).getId());
        }
        unit.getGame().getGameLoop().
         actionInput(new ActionInput(action.getActive(), context));
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

    public boolean move(BattleFieldObject obj, DC_Cell cell, boolean free, MOVE_MODIFIER mod,
                        Ref ref) {
        Ref REF = new Ref(obj.getGame());
        REF.setTarget(cell.getId());
        REF.setSource(obj.getId());
        LogMaster.log(LogMaster.MOVEMENT_DEBUG, "Moving " + obj + " to " + cell);
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_BEING_MOVED, REF);
        if (!game.fireEvent(event)) {
            return false;
        }

        Coordinates c = cell.getCoordinates();
        if (mod != MOVE_MODIFIER.TELEPORT) { // TODO UPDATE!
            Unit moveObj = (Unit) getGrid().getObj(cell.getCoordinates());
            if (moveObj != null) {
                if (ref.getActive() instanceof DC_ActiveObj) {
                    DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                    if (moveObj instanceof Unit) {
                        Unit heroObj = moveObj;
                        c = CollisionRule.collision(ref, activeObj, moveObj, heroObj,
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

        if (!game.getRules().getStackingRule().canBeMovedOnto(obj, c)) {
            return false;
        }
        if (game.getObjectByCoordinate(c) instanceof BattleFieldObject) {
            BattleFieldObject bfObj = (BattleFieldObject) game.getObjectByCoordinate(c);
            if (!bfObj.isDead())
                if (bfObj.isWall()) {
                    return false;
                }
        }
        if (obj instanceof Unit) {
            if (!game.getRules().getEngagedRule().unitMoved((Unit) obj, c.x, c.y)) {
                return false;
            }
        }

        obj.setCoordinates(c);
//        if (IGG_HACK_MOVE)
//            DungeonScreen.getInstance().getGridPanel().unitMoved(obj); //igg demo hack
        event = new Event(STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING, REF);

        if (obj instanceof Unit)
        {
            game.getDungeonMaster().getTrapMaster().unitMoved((Unit) obj);
            game.getDungeonMaster().getPortalMaster().unitMoved((Unit) obj);
        }
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
