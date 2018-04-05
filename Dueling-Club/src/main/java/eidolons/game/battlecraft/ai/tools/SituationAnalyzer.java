package eidolons.game.battlecraft.ai.tools;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.priority.ParamPriorityAnalyzer;
import main.content.enums.system.AiEnums;
import main.entity.Entity;
import main.entity.obj.ActiveObj;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;

import java.util.List;

/**
 * Created by JustMe on 4/9/2017.
 */
public class SituationAnalyzer extends AiHandler {

    public SituationAnalyzer(AiMaster master) {
        super(master);
    }

    public int getMeleeDangerFactor(Unit unit) {
        return getMeleeDangerFactor(unit, true, true);
    }

    public int getRangedDangerFactor(Unit unit) {
        int factor = 0;
        for (Entity e : Analyzer.getVisibleEnemies(unit.getAI())) {
            Unit enemy = (Unit) e;
            try {
                int rangedThreat = getThreatAnalyzer().getRangedThreat(unit,
                 enemy);
                factor += rangedThreat;
                LogMaster.log(LOG_CHANNEL.AI_DEBUG, "Ranged threat " + rangedThreat + " from " + enemy.getName());
            } catch (Exception ex) {
                main.system.ExceptionMaster.printStackTrace(ex);
            }
        }

        int mod = getConstInt(AiConst.DANGER_RANGED_BASE) - ParamPriorityAnalyzer.getUnitLifeFactor(unit);
        LogMaster.log(LOG_CHANNEL.AI_DEBUG, "Ranged threat mod " + mod + " for " + unit.getName());

        if (mod != 0) {
            factor = factor * mod / 100;
        }
        LogMaster.log(LOG_CHANNEL.AI_DEBUG, "Ranged threat factor " + factor + " for " + unit.getName());

        return factor;
    }

    public int getMeleeDangerFactor(Unit unit, boolean adjacentOnly, boolean now) {
        List<? extends Entity> units = (adjacentOnly) ? Analyzer.getMeleeEnemies(unit)
         : Analyzer.getVisibleEnemies(unit.getAI());
        int factor = 0;
        for (Entity e : units) {
            Unit enemy = (Unit) e;
            int meleeThreat = getThreatAnalyzer().getMeleeThreat(enemy, now);
            factor += meleeThreat;
            LogMaster.log(LOG_CHANNEL.AI_DEBUG, "Melee threat " + meleeThreat + " from " + enemy.getName());
        }

        int mod = getConstInt(AiConst.DANGER_MELEE_BASE)
         - ParamPriorityAnalyzer.getUnitLifeFactor(unit);
        LogMaster.log(LOG_CHANNEL.AI_DEBUG, "Melee threat mod " + mod + " for " + unit.getName());

        if (mod != 0) {
            factor = factor * mod / 100;
        }
        LogMaster.log(LOG_CHANNEL.AI_DEBUG, "Melee threat factor " + factor + " for " + unit.getName());

        return factor;
    }


    public int getCastingPriority(Unit unit) {
        UnitAI unit_ai = unit.getAI();
        if (!Analyzer.hasSpells(unit)) {
//            if (!Analyzer.hasAnySpecialActions(unit))
            return 0;
        }
        // per spell? TODO

        if (unit_ai.getType() == AiEnums.AI_TYPE.BRUTE) {
            return 25;
        }
        if (unit_ai.getType() == AiEnums.AI_TYPE.ARCHER) {
            return 35;
        }
        if (unit_ai.getType() == AiEnums.AI_TYPE.TANK) {
            return 50;
        }
        if (unit_ai.getType().isCaster()) {
            return 200;
        }
        return 100;
    }

    public boolean canAttackNow(UnitAI ai) {
        for (ActiveObj a : ai.getUnit().getActives()) {
            DC_ActiveObj action = (DC_ActiveObj) a;
            if (action.isAttackAny())
                if (!action.isAttackGeneric())
                    for (DC_Obj enemy : getAnalyzer().getEnemies(ai.getUnit(), false, false,
                     action.isMelee())) {
                        if (action.canBeActivated())
                            if (action.canBeTargeted(enemy.getId()))
                                return true;
                    }
        }
        return false;
    }

    public int getDangerFactor(Unit unit) {
        try {
            return getMeleeDangerFactor(unit) + getRangedDangerFactor(unit);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return 0;
    }

    public int getTimeModifier() {
        return getGame().getRules().getTimeRule().getTimePercentageRemaining();
    }
}
