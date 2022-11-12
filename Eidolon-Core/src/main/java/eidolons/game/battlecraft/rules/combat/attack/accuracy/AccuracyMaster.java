package eidolons.game.battlecraft.rules.combat.attack.accuracy;

import eidolons.content.DC_Calculator;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.attack.AttackCalculator;
import eidolons.game.battlecraft.rules.combat.attack.DefenseAttackCalculator;
import eidolons.game.core.game.DC_Game;
import eidolons.system.math.roll.DiceMaster;
import main.content.enums.entity.NewRpgEnums;
import main.system.auxiliary.EnumMaster;
import main.system.math.MathMaster;

import java.util.LinkedHashMap;
import java.util.Map;

import static main.content.enums.entity.NewRpgEnums.HitType.*;

public class AccuracyMaster {
    public static final NewRpgEnums.HitType[] types = values();
    private DC_Game game;

    public AccuracyMaster(DC_Game game) {
        this.game = game;
    }

    public static NewRpgEnums.HitType getHitType(int accuracy) {
        int index = (accuracy - 1) / 20;
        index =1+ MathMaster.getMinMax(index, 0, 4);
        return types[index];
    }

    public NewRpgEnums.HitType getHitType(Attack attack) {
        int defense = DefenseAttackCalculator.getDefenseValue(attack);
        int attackValue = DefenseAttackCalculator.getAttackValue(attack);
        int accuracy = rollAccuracy(defense, attackValue,
                attack.getAttacked(),
                attack.getAttacker(),
                attack.getAction()
        );
        attack.setAccuracyRate(accuracy);
        return getHitType(accuracy);
    }

    private int rollAccuracy(int defense, int attackValue, BattleFieldObject attacked, Unit attacker, ActiveObj action) {
        if (action.isSpell()) {
            //TODO chaos level applies?
        }
        int base = DC_Calculator.getAccuracyRating(defense, attackValue);
        int dice = getDiceNumberForAttack(action, attacker, true);
        int plus = DiceMaster.d20(attacked, dice);
        dice = getDiceNumberForAttack(action, attacked, false);
        int minus = DiceMaster.d20(attacker, dice);
        int result=base + plus - minus;
        return result;
    }

    private int getDiceNumberForAttack(ActiveObj action, BattleFieldObject unit, boolean attacker) {
        return 1 + game.getState().getChaosLevel();
    }

    public void initHitType(Attack attack) {
        NewRpgEnums.HitType hitType = getHitType(attack);
        logHit(hitType, attack);
    }

    private void logHit(NewRpgEnums.HitType hitType, Attack attack) {
    }

    //spell? should spells also create Attack object?
    public AccuracyBreakdown createBreakdown(Attack attack) {
        Map<NewRpgEnums.HitType, Integer> chances = new LinkedHashMap<>();
        Map<NewRpgEnums.HitType, Integer> minMap = new LinkedHashMap<>();
        Map<NewRpgEnums.HitType, Integer> maxMap = new LinkedHashMap<>();
        int accuracyBase, minAccuracy, maxAccuracy;

        int defense = DefenseAttackCalculator.getDefenseValue(attack);
        int attackValue = DefenseAttackCalculator.getAttackValue(attack);
        accuracyBase = DC_Calculator.getAccuracyRating(defense, attackValue);
        int dice = getDiceNumberForAttack(attack.getAction(), attack.getAttacked(), false);
        int dice2 = getDiceNumberForAttack(attack.getAction(), attack.getAttacker(), true);
        int max = Math.max(dice, dice2);
        int min = Math.min(dice, dice2);
        maxAccuracy = accuracyBase + max * 20 - min;
        minAccuracy = accuracyBase - max * 20 + min;

        for (NewRpgEnums.HitType type : types) {
            int chance = getChance(attack, type, minAccuracy, maxAccuracy);
            if (chance > 0) {
                chances.put(type, chance);
            } else continue;
            minMap.put(type, getMinDmg(attack, type));
            minMap.put(type, getMaxDmg(attack, type));
        }
        AccuracyBreakdown breakdown = new AccuracyBreakdown(chances, minMap, maxMap);
        return breakdown;
    }

    private Integer getMinDmg(Attack attack, NewRpgEnums.HitType type) {
        return getDmgRange(attack, type, true);
    }

    private Integer getMaxDmg(Attack attack, NewRpgEnums.HitType type) {
        return getDmgRange(attack, type, false);
    }

    private Integer getDmgRange(Attack attack, NewRpgEnums.HitType type, boolean minMax) {
        AttackCalculator calculator = new AttackCalculator(attack, true);
        calculator.setHitType(type);
        if (minMax) {
            calculator.setMin(true);
        } else {
            calculator.setMax(true);
        }
        return calculator.calculateFinalDamage();
    }

    private int getChance(Attack attack, NewRpgEnums.HitType type, int minAccuracy, int maxAccuracy) {
        if (type == critical_miss || type == deadeye) {
            if (attack.isExtra()) {
                return 0;
            }
        }
        int index = EnumMaster.getEnumConstIndex(NewRpgEnums.HitType.class, type);
        int min = index * 20;
        int max = min + 20;
        if (max < minAccuracy)
            return 0;
        if (min > maxAccuracy)
            return 0;
        int range = maxAccuracy - minAccuracy;
        int chance = Math.min(maxAccuracy - max, 20) / range * 100;
        return chance;
    }


}
