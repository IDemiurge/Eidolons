package main.rules.round;

import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.values.properties.G_PROPS;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.rules.action.ActionRule;
import main.system.math.Formula;

import java.util.List;

public class WaterRule extends RoundRule implements ActionRule {

    private static final float SWIM_FACTOR = 0.25f;
    private static final float SWIM_FORCE_FACTOR = 0.9f;
    private static Unit waterObj;

    public WaterRule(DC_Game game) {
        super(game);
    }

    public static boolean checkPassable(Unit unit) {
        if (!canSwim(unit)) {
            if (!isOnSwimmingDepth(unit)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isForceSwimmingDepth(Unit unit) {
        float heightFactor = getSubmergedFactor(unit);
        return heightFactor > SWIM_FORCE_FACTOR;
    }

    private static boolean isOnSwimmingDepth(Unit unit) {
        float heightFactor = getSubmergedFactor(unit);
        return heightFactor > SWIM_FACTOR;
    }

    private static float getSubmergedFactor(Unit unit) {
        float heightFactor = Math.abs(waterObj.getIntParam(PARAMS.HEIGHT))
                / unit.getIntParam(PARAMS.HEIGHT);
        return heightFactor;
    }

    public static boolean ignoresWater(Unit unit) {
        if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return true;
        }
        if (unit.isFlying()) {
            return true;
        }
        return false;
    }

    public static boolean canSwim(Unit unit) {
        if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return false;
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.MECHANICAL)) {
            return false;
        }
        return true;
    }

    public static int getWaterMoveApMod(Unit unit) {
        return Math.round(waterObj.getIntParam(PARAMS.MOVE_AP_PENALTY) * getSubmergedFactor(unit));

    }

    public static int getWaterMoveStaMod(Unit unit) {
        return Math.round(waterObj.getIntParam(PARAMS.MOVE_STA_PENALTY) * getSubmergedFactor(unit));

    }

    private static boolean isWater(Unit u) {
        return (u.checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.WATER));
    }

    public static Unit getWaterObj() {
        return waterObj;
    }

    public static void setWaterObj(Unit waterObj) {
        WaterRule.waterObj = waterObj;
    }

    public boolean checkSwimming(Unit unit) {
        return false;
        // apply status?
    }

    public void checkDrowning(Unit unit) {
        if (!isOnSwimmingDepth(unit)) {
            return;
        }
        // reduce endurance by percentage
    }

    @Override
    public void apply(Unit unit) {
        // addMoistCounters(unit);
        //
        // float factor = Math.max(1, getSubmergedFactor(unit));
        // Effects submergedEffects = initSubmergedEffects(factor);
        // submergedEffects.apply(Ref.getSelfTargetingRefNew(unit));
        // add moist counters , preCheck drowning, preCheck swimming

    }

    private Effects initSubmergedEffects(float factor) {
        Effects effects = new Effects();
        Formula formula = new Formula("x*50");
        effects.add(new ModifyValueEffect(PARAMS.STEALTH, MOD.MODIFY_BY_PERCENT, formula
                .substituteVarValue("x", factor + "").toString()));
        effects.add(new ModifyValueEffect(PARAMS.NOISE, MOD.MODIFY_BY_PERCENT, formula
                .substituteVarValue("x", factor + "").toString()));
        formula = new Formula("-x*50");
        effects.add(new ModifyValueEffect(PARAMS.DEFENSE, MOD.MODIFY_BY_PERCENT, formula
                .substituteVarValue("x", factor + "").toString()));
        effects.add(new ModifyValueEffect(PARAMS.FIRE_RESISTANCE, MOD.MODIFY_BY_CONST,
                formula.substituteVarValue("x", factor + "").toString()));
        return effects;
    }

    private void addMoistCounters(Unit unit) {
        float factor = Math.min(1, getSubmergedFactor(unit));
        // new effect?
        int counters = unit.getCounter(COUNTER.Moist);
        int max = Math.round(factor * 100);
        int mod = Math.round(factor * 50);
        int newValue = Math.min(max, counters + mod);
        unit.setCounter(COUNTER.Moist.getName(), newValue);

    }

    @Override
    public boolean check(Unit unit) {
        List<Unit> units = game.getObjectsOnCoordinate(unit.getCoordinates());

        for (Unit u : units) {
            if (isWater(u)) {
                waterObj = u;
                return true;
            }
        }

        return false;
    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        // TODO Auto-generated method stub
        return true;
    }

}
