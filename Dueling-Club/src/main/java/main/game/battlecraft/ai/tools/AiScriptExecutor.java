package main.game.battlecraft.ai.tools;

import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.advanced.companion.Order;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.AiActionFactory;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.elements.task.Task;
import main.game.battlecraft.ai.tools.path.ActionPath;
import main.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import main.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

/**
 * Created by JustMe on 5/21/2017.
 */
public class AiScriptExecutor extends AiHandler implements ScriptExecutor<COMBAT_SCRIPT_FUNCTION> {


    public AiScriptExecutor(AiMaster aiMaster) {
        super(aiMaster);
    }

    @Override
    public String getSeparator(COMBAT_SCRIPT_FUNCTION func) {
        return null;
    }

    @Override
    public boolean execute(COMBAT_SCRIPT_FUNCTION function, Ref ref, String... args) {
        if (args[0].equalsIgnoreCase("group")) {

        }
        int i = 0;
        Unit unit = (Unit) ref.getObj(args[0]);
        if (unit == null) {
            String name = args[i];
            if (DataManager.isTypeName(name))
                i++;
            else name = null ;

            AI_ARG arg =
             new EnumMaster<AI_ARG>().retrieveEnumConst(AI_ARG.class, args[i]);
            unit = getUnit(arg);
            if (unit == null) {
                Boolean power = null;// getPower(arg);
                Boolean distance = getDistance(arg);
                Boolean ownership = getOwnership(arg);
                unit = getGame().getMaster().getUnitByName(name, ref,
                 ownership, distance, power);
            }
        }
        i++;
        //group?
        //with frozen gameLoop?

        String arg = null;
        boolean free = false;
        boolean immediate = false;
        if (args.length > i) {
            String options = args[i + 1];
            i++;
//            if (StringMaster.contains(options, FREE)
            free = true;
            immediate = true;
            arg = options;
        }
        String[] additionalArgs = null;;
        if (args.length>i) {
            additionalArgs=   new String[args.length-i];
            for (int j = 0; j < args.length; j++) {
                additionalArgs[j] = args[i + j];
            }
        }
        executeCommand(unit, function, arg, free, immediate, additionalArgs);

        return true;
    }

    private void executeCommand(Unit unit, COMBAT_SCRIPT_FUNCTION function, String arg,
                                boolean free, boolean immediate, String... args) {
        ActionSequence sequence = null;
        GOAL_TYPE goal = getGoalType(function);
        Task task = new Task(true, unit.getAI(), goal, arg);
        UnitAI ai = unit.getAI();
        switch (function) {
            case MOVE_TO:
                //via a path!
                ActionPath path = getPathSequenceConstructor().getOptimalPathSequence(unit.getAI(),
                 new Coordinates(arg.toString()));
                sequence = new ActionSequence(path.getActions(),task,unit.getAI());
                break;
            case TURN_TO:
                //cell id
                sequence = new ActionSequence(
                 getTurnSequenceConstructor().
                  getTurnSequence(FACING_SINGLE.IN_FRONT, unit,
                  new Coordinates(arg.toString())), task, unit.getAI());
                break;
            case ACTION:
                Action action = AiActionFactory.newAction(arg.toString(), ai);
                sequence = //new ActionSequence();
                getActionSequenceConstructor().getSequence(action,
                 new Task(ai, goal, args[0]));
                break;
            case ATTACK:

                break;
            case FREEZE:
                break;
            case UNFREEZE:
                break;
            case ORDER:
                Order a = //OrderFactory.getOrder();
                 new Order(arg.toString());
                unit.getAI().setCurrentOrder(a);
                return ;
        }
        if (immediate) {
            unit.getAI().setStandingOrders(sequence);
            unit.getAI().setFree(free);
        } else
            sequence.getActions().forEach(
             //TODO wait?
             action -> getExecutor().execute(action, free));

    }

    private GOAL_TYPE getGoalType(COMBAT_SCRIPT_FUNCTION function) {
        switch (function) {
            case ACTION:
                break;
            case ATTACK:
                return GOAL_TYPE.ATTACK;
        }
        return GOAL_TYPE.MOVE;
    }

    private Boolean getOwnership(AI_ARG arg) {
        switch (arg) {
            case CLOSEST_ALLY:
            case RANDOM_ALLY:
                return true;
            case CLOSEST_ENEMY:
            case RANDOM_ENEMY:
                return false;
        }
        return null;
    }

    private Boolean getDistance(AI_ARG arg) {
        switch (arg) {
            case CLOSEST_ALLY:
            case CLOSEST_ENEMY:
                return true;
        }
        return null;
    }

    private Unit getUnit(AI_ARG arg) {
        switch (arg) {
            case BOSS:
            case AI_LEADER:
            case MAIN_HERO:

        }
        return null;
    }

    public enum AI_ARG {
        CLOSEST_ENEMY,
        CLOSEST_ALLY,
        BOSS,
        MAIN_HERO,
        AI_LEADER,
        RANDOM_ENEMY,
        RANDOM_ALLY,
    }


}
