package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
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
        return defense;
    }

    public static int getAttackValue(boolean offhand, BattleFieldObject attacker, BattleFieldObject attacked,
                                     DC_ActiveObj action) {
        int attack = attacker.getIntParam((offhand) ? PARAMS.OFF_HAND_ATTACK : PARAMS.ATTACK);
        attack = MathMaster.applyPercent(attack,
                action.getIntParam(PARAMS.ATTACK_MOD));
        attack += action.getIntParam(PARAMS.ATTACK_BONUS);

        return attack;
    }

}
