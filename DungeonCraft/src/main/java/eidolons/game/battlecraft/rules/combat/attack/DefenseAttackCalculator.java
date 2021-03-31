package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.WatchRule;
import eidolons.game.battlecraft.rules.perk.FlyingRule;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class DefenseAttackCalculator {
    public static int getAttackValue(Attack attack) {
        return getAttackValue(attack.isOffhand(), attack.getAttacker(), attack.getAttacked(),
                attack.getAction());
    }

    public static int getDefenseValue(Attack attack) {
        return getDefenseValue(attack.getAttacker(), attack.getAttacked(), attack.getAction());
    }

    //
    public static int getDefenseValue(BattleFieldObject attacker, BattleFieldObject attacked, DC_ActiveObj action) {
        int defense = attacked.getIntParam(PARAMS.DEFENSE)
                - attacker.getIntParam(PARAMS.DEFENSE_PENETRATION);
        defense = defense * (action.getIntParam(PARAMS.DEFENSE_MOD)) / 100;
        defense += action.getIntParam(PARAMS.DEFENSE_BONUS);
        if (attacked instanceof Unit)
            if (WatchRule.checkWatched((Unit) attacked, attacker)) {
                //increase defense if attacked watches attacker
                //TODO add reverse mods - 'defense when watched on attack' for trickster
                int bonus = MathMaster.applyPercent(WatchRule.DEFENSE_MOD, attacked
                        .getIntParam(PARAMS.WATCH_DEFENSE_MOD));
                defense += bonus;
            }
        return defense;
    }

    public static int getAttackValue(boolean offhand, BattleFieldObject attacker, BattleFieldObject attacked,
                                     DC_ActiveObj action) {
        int attack = attacker.getIntParam((offhand) ? PARAMS.OFF_HAND_ATTACK : PARAMS.ATTACK);
        Boolean flying_mod = null;
        if (!action.isRanged()) {
            if (attacker.isFlying()) {
                if (!attacked.isFlying()) {
                    flying_mod = true;
                }
            }
            if (!attacker.isFlying()) {
                if (attacked.isFlying()) {
                    flying_mod = false;
                }
            }
        }
        attack = MathMaster.applyPercent(attack,
                action.getIntParam(PARAMS.ATTACK_MOD));
        attack += action.getIntParam(PARAMS.ATTACK_BONUS);

        if (flying_mod != null) {
            int bonus = FlyingRule.getAttackBonus(attack, flying_mod);
            attack += bonus;
        }
        if (attacker instanceof Unit)
            if (WatchRule.checkWatched((Unit) attacker, attacked)) {
                //increase attack if attacker watches attacked
                int bonus = MathMaster.applyPercent(WatchRule.ATTACK_MOD, attacker
                        .getIntParam(PARAMS.WATCH_ATTACK_MOD));
                attack += bonus;
            }

        return attack;
    }

}
