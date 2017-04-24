package main.rules.counter;

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
        Map<COUNTER, Integer> map =createCounterMap(unit);
        map.forEach((counter, value) -> map.forEach((counter2, value2) -> {
            if (counter != counter2) {
                COUNTER_INTERACTION interaction = getInteraction(counter, counter2);
                int max = getMaxInteractionValue(unit, counter, counter2, value, value2);
                interact(counter, counter2, interaction, max, unit);
            }
        }));
        Map<COUNTER, Integer> convertMap =createCounterMap(unit);
        map.forEach((counter, value) ->  {
                COUNTER counter2 = getConvertion(counter);
                int max = getMaxConvertionValue(unit, counter, counter2, value );
                convertCounters(counter, counter2, max, unit);
        });
        Map<COUNTER, Integer> transferMap =createCounterMap(unit);
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

    private static Map<COUNTER,Integer> createCounterMap(Unit unit) {
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
            case Blaze_Counter:
                s.setDown(COUNTER.Corrosion_Counter);
                s.setUp(COUNTER.Electrified_Counter);
                break;
            case Freeze_Counter:
                s.setDown(COUNTER.Encase_Counter);
                s.setUp(COUNTER.Moist_Counter);
                initInteraction(s, COUNTER.Blaze_Counter, COUNTER_INTERACTION.MUTUAL_DELETION);
                map.put(COUNTER.Moist_Counter, COUNTER_INTERACTION.GROW_SELF);
                break;
            case Blight_Counter:
                s.setDown(COUNTER.Poison_Counter);
                s.setUp(COUNTER.Corrosion_Counter);
                break;
            case Corrosion_Counter:
                s.setDown(COUNTER.Blight_Counter);
                s.setUp(COUNTER.Blaze_Counter);
                break;
            case Electrified_Counter:
                s.setDown(COUNTER.Blaze_Counter);
                s.setUp(COUNTER.Magnetized);
                break;
            case Moist_Counter:
                s.setDown(COUNTER.Freeze_Counter);
                s.setUp(COUNTER.Corrosion_Counter);
                break;
            case Grease_Counter:
                s.setDown(COUNTER.Clay_Counter);
                s.setUp(COUNTER.Moist_Counter);
                break;
            case Clay_Counter:
                s.setDown(COUNTER.Encase_Counter);
                s.setUp(COUNTER.Grease_Counter);
                break;
            case Encase_Counter:
                s.setUp(COUNTER.Ensnared_Counter);
                break;
            case Ensnared_Counter:
                s.setDown(COUNTER.Encase_Counter);
                break;
            case Bleeding_Counter:
                break;
            case Disease_Counter:
                break;
            case Poison_Counter:
                break;
            case Illumination_Counter:
                break;
            case Concealment_Counter:
                break;
            case Haze_Counter:
                break;
            case Magnetized:
                break;
            case Mutagenic:
                break;

            case Rage_Counter:
                s.setDown(COUNTER.Hatred_Counter);
                s.setUp(COUNTER.Oblivion_Counter);
                break;
            case Oblivion_Counter:
                s.setDown(COUNTER.Taint_Counter);
                s.setUp(COUNTER.Void_Counter);
                break;
            case Madness_Counter:
                s.setDown(COUNTER.Madness_Counter);
                s.setUp(COUNTER.Hatred_Counter);
                break;
            case Despair_Counter:
                s.setDown(COUNTER.Oblivion_Counter);
                s.setUp(COUNTER.Madness_Counter);
                break;
            case Lust_Counter:
                s.setDown(COUNTER.Madness_Counter);
                s.setUp(COUNTER.Hatred_Counter);
                break;
            case Hatred_Counter:
                s.setDown(COUNTER.Lust_Counter);
                s.setUp(COUNTER.Rage_Counter);
                break;

            case Virtue_Counter:
                s.setDown(COUNTER.Zeal_Counter);
                s.setUp(COUNTER.Void_Counter);
                break;
            case Zeal_Counter:
                s.setDown(COUNTER.Rage_Counter);
                s.setUp(COUNTER.Virtue_Counter);
                break;
            case Void_Counter:
                s.setDown(COUNTER.Time_Warped);
                s.setUp(COUNTER.Zen);
                break;
            case Warp_Counter:
                s.setDown(COUNTER.Lust_Counter);
                s.setUp(COUNTER.Zeal_Counter);
                break;
            case Aether_Counter:
                s.setDown(COUNTER.Electrified_Counter);
                s.setUp(COUNTER.Magnetized);
                break;
            case Encryption_Counter:
                s.setDown(COUNTER.Aether_Counter);
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
