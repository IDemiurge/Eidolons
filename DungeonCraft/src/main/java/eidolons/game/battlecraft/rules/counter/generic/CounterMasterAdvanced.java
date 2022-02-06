package eidolons.game.battlecraft.rules.counter.generic;

import eidolons.entity.obj.BattleFieldObject;
import main.content.enums.entity.EffectEnums;
import main.content.enums.entity.EffectEnums.COUNTER;
import main.content.enums.entity.EffectEnums.COUNTER_INTERACTION;
import main.content.enums.entity.EffectEnums.COUNTER_OPERATION;
import main.data.XLinkedMap;
import main.system.entity.CounterMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 4/19/2017.
 */
public class CounterMasterAdvanced {
    public static void defineInteractions() {
        for (COUNTER s : EffectEnums.COUNTER.values()) {
            defineInteractions(s);
        }
    }

    //TODO
    public static void countersAdded(BattleFieldObject unit, COUNTER counter, int newTotal) {
        COUNTER upgraded = counter.getUpgraded();
        if (upgraded == null) {
            return;
        }
        int upToAdd = newTotal / 10;
        // addCounters(unit, upgraded, upToAdd);
        // removeCounters(unit, counter, upToAdd*10);
    }

    public static void afterRoundEnds(BattleFieldObject unit) {
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

    private static int getMaxConvertionValue(BattleFieldObject unit, COUNTER counter, COUNTER counter2, Integer value) {
        return 0;
    }

    private static COUNTER getConvertion(COUNTER counter) {
        return counter;
    }

    private static Map<COUNTER, Integer> createCounterMap(BattleFieldObject unit) {
        Map<COUNTER, Integer> map = new XLinkedMap<>();
        for (DC_CounterRule rule : unit.getGame().getRules().getCounterRules()) {
            COUNTER c = getCounter(rule.getCounterName());
            Integer n = rule.getNumberOfCounters(unit);
            if (n > 0)
                map.put(c, n);
        }
        return map;
    }

    private static int getMaxInteractionValue(BattleFieldObject unit, COUNTER counter, COUNTER counter2, Integer value, Integer value2) {
        //TODO
        return Math.min(value, value2);
    }

    private static COUNTER getCounter(String counterName) {
        return CounterMaster.getCounter(counterName, true);
    }

    public static void afterActionDone(BattleFieldObject unit) {

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

                case DELETE_SELF:
                case GROW_SELF:
                case CONVERT_FROM:
                case DELETE_OTHER:
                case GROW_OTHER:
                case CONVERT_TO:
                case TRANSFORM_DOWN:
                    break;
                case GROW_BOTH:
                case MUTUAL_DELETION:
                    initInteraction(counter, c, interaction, false);
                    break;
            }
    }

    public static void defineInteractions(COUNTER s) {
        Map<COUNTER, COUNTER_INTERACTION> map = new HashMap<>();
        s.setInteractionMap(map);
        switch (s) {
            case Blaze:
                s.setDown(EffectEnums.COUNTER.Corrosion);
                s.setUp(EffectEnums.COUNTER.Charge);
                break;
            case Chill:
                s.setDown(EffectEnums.COUNTER.Encase);
                s.setUp(EffectEnums.COUNTER.Moist);
                initInteraction(s, EffectEnums.COUNTER.Blaze, EffectEnums.COUNTER_INTERACTION.MUTUAL_DELETION);
                map.put(EffectEnums.COUNTER.Moist, EffectEnums.COUNTER_INTERACTION.GROW_SELF);
                break;
            case Blight:
                s.setDown(EffectEnums.COUNTER.Poison);
                s.setUp(EffectEnums.COUNTER.Corrosion);
                break;
            case Corrosion:
                s.setDown(EffectEnums.COUNTER.Blight);
                s.setUp(EffectEnums.COUNTER.Blaze);
                break;
            case Charge:
                s.setDown(EffectEnums.COUNTER.Blaze);
                s.setUp(EffectEnums.COUNTER.Magnetized);
                break;
            case Moist:
                s.setDown(EffectEnums.COUNTER.Chill);
                s.setUp(EffectEnums.COUNTER.Corrosion);
                break;
            case Grease:
                s.setDown(EffectEnums.COUNTER.Clay);
                s.setUp(EffectEnums.COUNTER.Moist);
                break;
            case Clay:
                s.setDown(EffectEnums.COUNTER.Encase);
                s.setUp(EffectEnums.COUNTER.Grease);
                break;
            case Encase:
                s.setUp(EffectEnums.COUNTER.Ensnared);
                break;
            case Ensnared:
                s.setDown(EffectEnums.COUNTER.Encase);
                break;
            case Bleeding:
            case Mutagen:
            case Magnetized:
            case Haze:
            case Poison:
            case Disease:
                break;

        }
    }


    public static void interact(COUNTER from, COUNTER to,
                                COUNTER_INTERACTION interactionType,
                                Integer max, BattleFieldObject unit) {


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

    private static void removeCounters(COUNTER counter, Integer amount, BattleFieldObject unit) {
        int toValue = unit.getCounter(counter) - amount;
        unit.setCounter(counter.getName(), toValue);
    }

    private static void setCounters(COUNTER counter, Integer amount, BattleFieldObject unit) {
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

    public static void transform(COUNTER counter, Boolean upOrDownTransform, Integer max, BattleFieldObject target) {
        COUNTER to = upOrDownTransform ? counter.getUp() : counter.getDown();
        convertCounters(counter, to, max, target);
    }

    public static void convertCounters(COUNTER from,
                                       COUNTER to, int max, BattleFieldObject unit) {
        convertCounters(from, to, max, unit, unit);
    }

    public static void convertCounters(COUNTER from,
                                       COUNTER to, int max, BattleFieldObject unitFrom, BattleFieldObject unitTo) {
        int amount = Math.min(max, unitFrom.getCounter(from));
        int fromValue = unitFrom.getCounter(from) - amount;
        int toValue = unitFrom.getCounter(to) + amount;
        unitFrom.setCounter(from.getName(), fromValue);
        unitTo.setCounter(to.getName(), toValue);


    }


    public static void operation(COUNTER counter, COUNTER_OPERATION operation, Integer amount, BattleFieldObject source, BattleFieldObject target) {
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
