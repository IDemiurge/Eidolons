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
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;

import java.util.List;

/**
 * Created by JustMe on 4/9/2017.
 */
public class SituationAnalyzer extends AiHandler {

    public SituationAnalyzer(AiMaster master) {
        super(master);
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

@Deprecated
    public int getDangerFactor(Unit unit) {
        return 0;
    }
@Deprecated
    public int getTimeModifier() {
        return 1;
    }
}
