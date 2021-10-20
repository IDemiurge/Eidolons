package eidolons.game.battlecraft.ai.tools.priority;

public class PriorityCalculator {

    private StringBuilder log;

    public int calculate(AiPriorityCalc calc) {
        log = new StringBuilder();
        Float amount=0f;
        for (String key : calc.getBonuses().keySet()) {
            Float val = calc.getBonuses().get(key);
            amount += val;
        }
        Float multiply=0f;
        for (String key : calc.getMultipliers().keySet()) {
            Float val = calc.getBonuses().get(key);
            multiply += val;
        }
        amount *= multiply;
        return  Math.round(amount);
    }
}
