package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.rules.combat.attack.AttackCalculator;
import eidolons.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.screens.map.ui.tooltips.PartyTooltip;
import eidolons.macro.entity.party.MacroParty;
import main.content.enums.entity.ActionEnums;
import main.game.logic.action.context.Context;
import main.system.math.FuncMaster;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.Map;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 4/11/2017.
 */
public class ThreatAnalyzer extends AiHandler {

    Map<UnitAI, Map<Unit, Integer>> threatMemoryMap = new HashMap<>();

    public ThreatAnalyzer(AiMaster master) {
        super(master);
    }

    public static PartyTooltip.THREAT_LEVEL getThreatLevel(BattleFieldObject object) {
        int percentage = object.getIntParam(PARAMS.POWER)
                * 100 / Eidolons.getMainHero().getIntParam(PARAMS.POWER);
        return getThreatLevel(percentage);
    }

    public static PartyTooltip.THREAT_LEVEL getThreatLevel(MacroParty party, MacroParty playerParty) {
        int percentage = party.getParamSum(PARAMS.POWER)
                * 100 / playerParty.getParamSum(PARAMS.POWER);
        return getThreatLevel(percentage);
    }

    public float getRelativeThreat(UnitAI ai, Unit enemy) {
        int threat = getThreat(ai, enemy);
        int selfPriority = getPriorityManager().getUnitPriority(null, ai.getUnit(), false);
        return new Float(threat) / selfPriority;
    }

    public static PartyTooltip.THREAT_LEVEL getThreatLevel(int percentage) {
        PartyTooltip.THREAT_LEVEL level = null;
        for (PartyTooltip.THREAT_LEVEL sub : PartyTooltip.THREAT_LEVEL.values()) {
            level = sub;
            if (sub.powerPercentage <= percentage)
                break;
        }
        return level;
    }

    public int getThreat(UnitAI ai, Unit enemy) {
        Integer threat = getMemoryMap(ai).get(enemy);
        if (threat != null)
            return threat;
        double distance = PositionMaster.getExactDistance(ai.getUnit().getCoordinates(), enemy.getCoordinates());
        threat =
                (int) Math.round(enemy.getIntParam(PARAMS.POWER) / distance);

        if (!AI_Manager.isSimplifiedLogic())
        if (enemy.getAI().getType().isRanged()) {
            threat += getRangedThreat(ai.getUnit(), enemy);
        }
        getMemoryMap(ai).put(enemy, threat);
        return threat;
    }

    private Map<Unit, Integer> getMemoryMap(UnitAI ai) {
        Map<Unit, Integer> map = threatMemoryMap.get(ai);
        if (map == null)
            threatMemoryMap.put(ai, new HashMap<>());
        return threatMemoryMap.get(ai);
    }

    public int getRangedThreat(Unit target, Unit unit) {
        if (unit.getAI().getType().isCaster())
            return new FuncMaster().total(unit.getSpells(), s -> {
                Spell spell = (Spell) s;
                return getPriorityManager().getSpellPriority(spell, new Context(unit, target))
                        / getRangedThreatFactorSpell(unit, target);
            });
        if (unit.getRangedWeapon() == null)
            return 0;
        return new FuncMaster().getGreatestValueEntity(unit.getRangedWeapon().getOrCreateAttackActions(),
                t -> {
                    DC_ActiveObj action = (DC_ActiveObj) t;
                    if (action.isRanged())
                        return new AttackCalculator(
                                DC_AttackMaster.getAttackFromAction(action), true)
                                .initTarget(unit).calculateFinalDamage()
                                / getRangedThreatFactorAttack(unit, target);
                    return 0;
                });
    }

    private int getRangedThreatFactorAttack(Unit unit, Unit target) {
        return 5;
    }

    private int getRangedThreatFactorSpell(Unit unit, Unit target) {
        return 10;
    }

    public int getMeleeThreat(Unit enemy) {
        return getMeleeThreat(enemy, true);
    }


    public int getMeleeThreat(Unit enemy, boolean now) {
        if (now) {
            if (!enemy.canActNow() || !enemy.canAttack()) {
                return 0;
            }
        }
        int distance = 1 + PositionMaster.getDistance(getUnit(), enemy);
        if (distance > 5)
            return 0;
        int threat = 0;
        int factor = 1;
        DC_UnitAction attack = enemy.getAction(ActionEnums.ATTACK);
        if (attack == null) {
            return 0;
        }
        DC_ActiveObj subAttack = (DC_ActiveObj) FuncMaster.getGreatestEntity(
                attack.getSubActions(), atk ->
                {
                    if (enemy.getIntParam(PARAMS.C_ATB) >
                            atk.getIntParam(PARAMS.AP_COST))
                        return atk.getIntParam(PARAMS.AP_COST);
                    return 0;
                }
        );

        try {
            threat = DC_PriorityManager.getAttackPriority(subAttack, getUnit()) * factor;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        threat /= distance;
        log(1, getUnit() + " feels " + threat + " threat from " + enemy);
        return threat;
    }


}
