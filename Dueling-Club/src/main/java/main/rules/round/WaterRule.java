package main.rules.round;

import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.BF_OBJECT_TAGS;
import main.content.CONTENT_CONSTS.CLASSIFICATIONS;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.rules.action.ActionRule;
import main.system.math.Formula;

import java.util.List;

public class WaterRule extends RoundRule implements ActionRule {

    private static final float SWIM_FACTOR = 0.25f;
    private static final float SWIM_FORCE_FACTOR = 0.9f;
    private static DC_HeroObj waterObj;

    public WaterRule(DC_Game game) {
        super(game);
    }

    public static boolean checkPassable(DC_HeroObj unit) {
        if (!canSwim(unit)) {
            if (!isOnSwimmingDepth(unit)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isForceSwimmingDepth(DC_HeroObj unit) {
        float heightFactor = getSubmergedFactor(unit);
        return heightFactor > SWIM_FORCE_FACTOR;
    }

    private static boolean isOnSwimmingDepth(DC_HeroObj unit) {
        float heightFactor = getSubmergedFactor(unit);
        return heightFactor > SWIM_FACTOR;
    }

    private static float getSubmergedFactor(DC_HeroObj unit) {
        float heightFactor = Math.abs(waterObj.getIntParam(PARAMS.HEIGHT))
                / unit.getIntParam(PARAMS.HEIGHT);
        return heightFactor;
    }

    public static boolean ignoresWater(DC_HeroObj unit) {
        if (unit.checkPassive(STANDARD_PASSIVES.IMMATERIAL)) {
            return true;
        }
        if (unit.isFlying()) {
            return true;
        }
        return false;
    }

    public static boolean canSwim(DC_HeroObj unit) {
        if (unit.checkPassive(STANDARD_PASSIVES.IMMATERIAL)) {
            return false;
        }
        if (unit.checkClassification(CLASSIFICATIONS.MECHANICAL)) {
            return false;
        }
        return true;
    }

    public static int getWaterMoveApMod(DC_HeroObj unit) {
        return Math.round(waterObj.getIntParam(PARAMS.MOVE_AP_PENALTY) * getSubmergedFactor(unit));

    }

    public static int getWaterMoveStaMod(DC_HeroObj unit) {
        return Math.round(waterObj.getIntParam(PARAMS.MOVE_STA_PENALTY) * getSubmergedFactor(unit));

    }

    private static boolean isWater(DC_HeroObj u) {
        return (u.checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BF_OBJECT_TAGS.WATER));
    }

    public static DC_HeroObj getWaterObj() {
        return waterObj;
    }

    public static void setWaterObj(DC_HeroObj waterObj) {
        WaterRule.waterObj = waterObj;
    }

    public boolean checkSwimming(DC_HeroObj unit) {
        return false;
        // apply status?
    }

    public void checkDrowning(DC_HeroObj unit) {
        if (!isOnSwimmingDepth(unit)) {
            return;
        }
        // reduce endurance by percentage
    }

    @Override
    public void apply(DC_HeroObj unit) {
        // addMoistCounters(unit);
        //
        // float factor = Math.max(1, getSubmergedFactor(unit));
        // Effects submergedEffects = initSubmergedEffects(factor);
        // submergedEffects.apply(Ref.getSelfTargetingRefNew(unit));
        // add moist counters , check drowning, check swimming

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

    private void addMoistCounters(DC_HeroObj unit) {
        float factor = Math.min(1, getSubmergedFactor(unit));
        // new effect?
        int counters = unit.getCounter(STD_COUNTERS.Moist_Counter);
        int max = Math.round(factor * 100);
        int mod = Math.round(factor * 50);
        int newValue = Math.min(max, counters + mod);
        unit.setCounter(STD_COUNTERS.Moist_Counter.getName(), newValue);

    }

    @Override
    public boolean check(DC_HeroObj unit) {
        List<DC_HeroObj> units = game.getObjectsOnCoordinate(unit.getCoordinates());

        for (DC_HeroObj u : units) {
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
    public boolean unitBecomesActive(DC_HeroObj unit) {
        // TODO Auto-generated method stub
        return true;
    }

}
