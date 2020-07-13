package eidolons.game.battlecraft.rules.round;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.obj.ActiveObj;
import main.system.math.Formula;

import java.util.Set;

public class WaterRule extends RoundRule implements ActionRule {

    private static final float SWIM_FACTOR = 0.25f;
    private static final float SWIM_FORCE_FACTOR = 0.9f;
    private static Unit waterObj; //TODO remove this!

    public WaterRule(DC_Game game) {
        super(game);
    }

    public static boolean checkPassable(BattleFieldObject waterObj, Entity unit) {
        return checkPassable(true, waterObj, unit);
    }

    public static boolean isBridged(BattleFieldObject waterObj) {
        boolean bridged = false;
        Integer girth = 0;
        for (BattleFieldObject object : waterObj.getGame().getObjectsOnCoordinateNoOverlaying(waterObj.getCoordinates())) {
            if (object==waterObj) {
                continue;
            }
            if (object.isDead()) {
                continue;
            }
            girth += object.getIntParam(PARAMS.GIRTH);
            if (waterObj.getIntParam(PARAMS.SPACE)<=girth){
                bridged = true;
            }
        }
        return bridged;
    }

    public static boolean checkPassable(boolean manualCheck, BattleFieldObject waterObj, Entity obj) {
        if (obj != null)
        if (isSwimming(obj)) {
            return true;
        }
        boolean bridged = isBridged(waterObj);
        if (obj instanceof Structure){
            if (bridged) {
//                if (girth >= 1000-obj.getIntParam(PARAMS.GIRTH))
                    return false;
            } else
            FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.REQUIREMENT,
                    obj.getName()+ " falls into " + waterObj.getName(), obj);
            return true;
        }
        return bridged;
//        if (canSwim(unit)) {
//            if (isOnSwimmingDepth(unit)) {
//                return true;
//            }
//        }
//        obj.getGame().getManager().isSelecting()
//        if (manualCheck) {
//            if (EidolonsGame.BRIDGE_CROSSED)
//        FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.REQUIREMENT,
//                "Won't touch that"
//                "Too deep to cross!"
//                , obj);
//        }
    }

    private static boolean isSwimming(Entity obj) {
        if (EntityCheckMaster.isImmaterial(obj)) {
            return true;
        }
        return obj.checkBool(GenericEnums.STD_BOOLS.SWIMMING);
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
        return unit.isFlying();
    }

    public static boolean canSwim(Unit unit) {
        if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return false;
        }
        return !unit.checkClassification(UnitEnums.CLASSIFICATIONS.MECHANICAL);
    }

    public static int getWaterMoveApMod(Unit unit) {
        return Math.round(waterObj.getIntParam(PARAMS.MOVE_AP_PENALTY) * getSubmergedFactor(unit));

    }

    public static int getWaterMoveStaMod(Unit unit) {
        return Math.round(waterObj.getIntParam(PARAMS.MOVE_TOU_PENALTY) * getSubmergedFactor(unit));

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
        }
        // reduce endurance by percentage
    }

    @Override
    public void apply(Unit unit, float delta) {
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
        Set<BattleFieldObject> units = game.getObjectsOnCoordinateNoOverlaying(unit.getCoordinates());

        for (BattleFieldObject u : units) {
//            if (isWater(u)) {
//                waterObj = u;
//                return true;
//            }
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
