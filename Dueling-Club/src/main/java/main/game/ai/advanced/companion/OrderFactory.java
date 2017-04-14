package main.game.ai.advanced.companion;

import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.ActiveObj;
import main.game.ai.advanced.companion.OrderMaster.ORDER_PRIORITY_MODS;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.system.auxiliary.data.MapMaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 4/14/2017.
 */
public class OrderFactory {
    private static final String PREFIX = "ORDER_PRIORITY_MOD_";

    public static Order getOrder(boolean partyTargeting, ActiveObj active) {
        String arg = null;
        Order order = new Order(arg);

        Map<GOAL_TYPE, Integer> modMap = new HashMap<>();

        Arrays.stream(ORDER_PRIORITY_MODS.values()).forEach(mod ->
                Arrays.stream(mod.getGoalTypes()).forEach(type ->
                        MapMaster.addToIntegerMap(modMap, type,
                                active.getIntParam(getParam(mod), false))));

        order.setPriorityModsMap(modMap);
        return order;
    }

    private static PARAMETER getParam(ORDER_PRIORITY_MODS mod) {
        return ContentManager.getPARAM(PREFIX + mod);
    }
}
