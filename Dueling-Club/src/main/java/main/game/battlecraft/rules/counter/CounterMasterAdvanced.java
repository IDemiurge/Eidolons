package main.game.battlecraft.rules.counter;

import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.COUNTER_INTERACTION;
import main.content.enums.entity.UnitEnums.COUNTER_OPERATION;
import main.data.XLinkedMap;
import main.entity.obj.unit.Unit;
import main.system.entity.CounterMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 4/19/2017.
 */
public class CounterMasterAdvanced {
    public static void defineInteractions() {
        for (COUNTER s : COUNTER.values()) {
            defineInteractions(s);
        }
    }

    public static void afterRoundEnds(Unit unit) {
//some interactions should happen immediately - e.g. moist vs blaze
// so we need to Interact, Convert and Transfer... what's the order? probably exactly that!
        Map<COUNTER, Integer> map = createCounterMap(unit);
        map.forEach((counter, value) -> map.forEach((counter2, value2) -> {
            if (counter != counter2) {
                COUNTER_INTERACTION interaction = getInteraction(counter, counter2);
                int max = getMaxInteractionValue(unit, counter, counter2, value, value2);
                interact(counter, counter2, interaction, max, unit);
            }
        }));
        Map<COUNTER, Integer> convertMap = createCounterMap(unit);
        map.forEach((counter, value) -> {
            COUNTER counter2 = getConvertion(counter);
            int max = getMaxConvertionValue(unit, counter, counter2, value);
            convertCounters(counter, counter2, max, unit);
        });
        Map<COUNTER, Integer> transferMap = createCounterMap(unit);
        map.forEach((counter, value) -> map.forEach((counter2, value2) -> {
            if (counter != counter2) {
                COUNTER_INTERACTION interaction = getInteraction(counter, counter2);
                int max = getMaxInteractionValue(unit, counter, counter2, value, value2);
                interact(counter, counter2, interaction, max, unit);
            }
        }));
    }

    private static int getMaxConvertionValue(Unit unit, COUNTER counter, COUNTER counter2, Integer value) {
        return 0;
    }

    private static COUNTER getConvertion(COUNTER counter) {
        return counter;
    }

    private static Map<COUNTER, Integer> createCounterMap(Unit unit) {
        Map<COUNTER, Integer> map = new XLinkedMap<>();
        for (DC_CounterRule rule : unit.getGame().getRules().getCounterRules()) {
            COUNTER c = getCounter(rule.getCounterName());
            Integer n = rule.getNumberOfCounters(unit);
            if (n > 0)
                map.put(c, n);
        }
        return map;
    }

    private static int getMaxInteractionValue(Unit unit, COUNTER counter, COUNTER counter2, Integer value, Integer value2) {
        //TODO
        return Math.min(value, value2);
    }

    private static COUNTER getCounter(String counterName) {
        return CounterMaster.getCounter(counterName, true);
    }

    public static void afterActionDone(Unit unit) {

    }

    private static COUNTER_INTERACTION getInteraction(COUNTER s, COUNTER s1) {
        return s.getInteractionMap().get(s1);
    }

    private static void initInteraction(COUNTER c, COUNTER counter, COUNTER_INTERACTION interaction) {
        initInteraction(c, counter, interaction, true);
    }

    private static void initInteraction(COUNTER c, COUNTER counter, COUNTER_INTERACTION interaction, boolean mirror) {
        c.getInteractionMap().put(counter, interaction);
        if (mirror)
            switch (interaction) {

                case TRANSFORM_UP:
                    break;
                case TRANSFORM_DOWN:
                    break;
                case GROW_BOTH:
                case MUTUAL_DELETION:
                    initInteraction(counter, c, interaction, false);
                    break;
                case DELETE_OTHER:
                case GROW_OTHER:
                case CONVERT_TO:
                    break;

                case DELETE_SELF:
                case GROW_SELF:
                case CONVERT_FROM:
                    break;
            }
    }

    public static void defineInteractions(COUNTER s) {
        Map<COUNTER, COUNTER_INTERACTION> map = new HashMap<>();
        s.setInteractionMap(map);
        switch (s) {
            case Blaze:
                s.setDown(COUNTER.Corrosion);
                s.setUp(COUNTER.Charge);
                break;
            case Freeze:
                s.setDown(COUNTER.Encase);
                s.setUp(COUNTER.Moist);
                initInteraction(s, COUNTER.Blaze, COUNTER_INTERACTION.MUTUAL_DELETION);
                map.put(COUNTER.Moist, COUNTER_INTERACTION.GROW_SELF);
                break;
            case Blight:
                s.setDown(COUNTER.Poison);
                s.setUp(COUNTER.Corrosion);
                break;
            case Corrosion:
                s.setDown(COUNTER.Blight);
                s.setUp(COUNTER.Blaze);
                break;
            case Charge:
                s.setDown(COUNTER.Blaze);
                s.setUp(COUNTER.Magnetized);
                break;
            case Moist:
                s.setDown(COUNTER.Freeze);
                s.setUp(COUNTER.Corrosion);
                break;
            case Grease:
                s.setDown(COUNTER.Clay);
                s.setUp(COUNTER.Moist);
                break;
            case Clay:
                s.setDown(COUNTER.Encase);
                s.setUp(COUNTER.Grease);
                break;
            case Encase:
                s.setUp(COUNTER.Ensnared);
                break;
            case Ensnared:
                s.setDown(COUNTER.Encase);
                break;
            case Bleeding:
                break;
            case Disease:
                break;
            case Poison:
                break;
            case Haze:
                break;
            case Magnetized:
                break;
            case Mutagenic:
                break;

            case Rage:
                s.setDown(COUNTER.Hatred);
                s.setUp(COUNTER.Oblivion);
                break;
            case Oblivion:
                s.setDown(COUNTER.Taint);
                s.setUp(COUNTER.Void);
                break;
            case Madness:
                s.setDown(COUNTER.Madness);
                s.setUp(COUNTER.Hatred);
                break;
            case Despair:
                s.setDown(COUNTER.Oblivion);
                s.setUp(COUNTER.Madness);
                break;
            case Lust:
                s.setDown(COUNTER.Madness);
                s.setUp(COUNTER.Hatred);
                break;
            case Hatred:
                s.setDown(COUNTER.Lust);
                s.setUp(COUNTER.Rage);
                break;

            case Virtue:
                s.setDown(COUNTER.Zeal);
                s.setUp(COUNTER.Void);
                break;
            case Zeal:
                s.setDown(COUNTER.Rage);
                s.setUp(COUNTER.Virtue);
                break;
            case Void:
                s.setDown(COUNTER.Time_Warped);
                s.setUp(COUNTER.Zen);
                break;
            case Warp:
                s.setDown(COUNTER.Lust);
                s.setUp(COUNTER.Zeal);
                break;
            case Aether:
                s.setDown(COUNTER.Charge);
                s.setUp(COUNTER.Magnetized);
                break;
            case Encryption:
                s.setDown(COUNTER.Aether);
                s.setUp(COUNTER.Time_Warped);
                break;
            case Time_Warped:
                break;
        }
    }


    public static void interact(COUNTER from, COUNTER to,
                                COUNTER_INTERACTION interactionType,
                                Integer max, Unit unit) {


        switch (interactionType) {
            case CONVERT_TO:
                COUNTER counter = to;
                to = from;
                from = counter;
                convertCounters(from, to, max, unit);
                break;
            case CONVERT_FROM:
                convertCounters(from, to, max, unit);
                break;
            case DELETE_OTHER:
                //TODO max defined by from!
                removeCounters(to, max, unit);
                break;
            case DELETE_SELF:
                removeCounters(from, max, unit);
                break;
            case MUTUAL_DELETION:
                break;

            case TRANSFORM_UP:
                to = from.getUp();
                convertCounters(from, to, max, unit);
                break;
            case TRANSFORM_DOWN:
                to = from.getDown();
                convertCounters(from, to, max, unit);
                break;
        }
    }

    private static void removeCounters(COUNTER counter, Integer amount, Unit unit) {
        int toValue = unit.getCounter(counter) - amount;
        unit.setCounter(counter.getName(), toValue);
    }

    private static void setCounters(COUNTER counter, Integer amount, Unit unit) {
        if (amount == 0) {
            unit.removeCounter(counter.getName());
            return;
        } else if (amount < 0) {
            if (!counter.isNegativeAllowed()) {
                unit.removeCounter(counter.getName());
                return;
            }
        }
        unit.setCounter(counter.getName(), amount);
    }

    public static void transform(COUNTER counter, Boolean upOrDownTransform, Integer max, Unit target) {
        COUNTER to = upOrDownTransform ? counter.getUp() : counter.getDown();
        convertCounters(counter, to, max, target);
    }

    public static void convertCounters(COUNTER from,
                                       COUNTER to, int max, Unit unit) {
        convertCounters(from, to, max, unit, unit);
    }

    public static void convertCounters(COUNTER from,
                                       COUNTER to, int max, Unit unitFrom, Unit unitTo) {
        int amount = Math.min(max, unitFrom.getCounter(from));
        int fromValue = unitFrom.getCounter(from) - amount;
        int toValue = unitFrom.getCounter(to) + amount;
        unitFrom.setCounter(from.getName(), fromValue);
        unitTo.setCounter(to.getName(), toValue);


    }


    public static void operation(COUNTER counter, COUNTER_OPERATION operation, Integer amount, Unit source, Unit target) {
        switch (operation) {
            case TRANSFER_TO:
                convertCounters(counter, counter, amount, source, target);
                break;
            case TRANSFER_FROM:
                convertCounters(counter, counter, amount, target, source);
                break;
        }
    }
}
