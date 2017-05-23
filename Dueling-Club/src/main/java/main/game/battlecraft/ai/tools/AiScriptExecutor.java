package main.game.battlecraft.ai.tools;

import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.task.Task;
import main.game.battlecraft.ai.tools.AiScriptExecutor.AI_SCRIPT_FUNCTION;
import main.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

/**
 * Created by JustMe on 5/21/2017.
 */
public class AiScriptExecutor extends AiHandler implements ScriptExecutor<AI_SCRIPT_FUNCTION> {


    @Override
    public String getSeparator(AI_SCRIPT_FUNCTION func) {
        return null;
    }

    @Override
    public boolean execute(AI_SCRIPT_FUNCTION function, Ref ref, String... args) {
        if (args[0].equalsIgnoreCase("group")) {

        }
        int i = 0;
        Unit unit = (Unit) ref.getObj(args[0]);
        if (unit == null) {
            String name = args[i];
            if (DataManager.isTypeName(name))
                i++;
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

        Object arg = null;
        boolean free = false;
        boolean immediate = false;
        if (args.length > i) {
            String options = args[i + 1];
//            if (StringMaster.contains(options, FREE)
            free = true;
            immediate = true;
            arg = options;
        }
        executeCommand(unit, function, arg, free, immediate);

        return true;
    }

    private void executeCommand(Unit unit, AI_SCRIPT_FUNCTION function, Object arg, boolean free, boolean immediate) {
        ActionSequence sequence = null;
        switch (function) {
            case MOVE_TO:
                //via a path!
                break;
            case TURN_TO:
                Task task = new Task(true, unit.getAI(), GOAL_TYPE.MOVE, arg);
                //cell id
                sequence = new ActionSequence(
                 getTurnSequenceConstructor().
                  getTurnSequence(FACING_SINGLE.IN_FRONT, unit,
                   (Coordinates) arg), task, unit.getAI());
                break;
            case ORDER:
                break;
        }
        if (immediate) {
            unit.getAI().setStandingOrders(sequence);
            unit.getAI().setFree(free);
        } else
            sequence.getActions().forEach(
             //TODO wait?
             action -> getExecutor().execute(action, free));

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

    public enum AI_SCRIPT_FUNCTION {
        MOVE_TO,
        TURN_TO,
        ORDER,
    }
}
