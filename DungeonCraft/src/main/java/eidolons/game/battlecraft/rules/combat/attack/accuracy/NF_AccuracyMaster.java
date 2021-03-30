package eidolons.game.battlecraft.rules.combat.attack.accuracy;

import eidolons.content.DC_Calculator;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.event.ChoiceEventMaster;
import eidolons.game.battlecraft.rules.DiceMaster;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.attack.DefenseAttackCalculator;
import eidolons.game.core.game.DC_Game;
import main.content.enums.entity.NewRpgEnums;

import java.util.LinkedHashMap;
import java.util.Map;

public class NF_AccuracyMaster {
    public static final NewRpgEnums.HitType[] types = NewRpgEnums.HitType.values();
    private DC_Game game;

    public NF_AccuracyMaster(DC_Game game) {
        this.game = game;
    }

    public NewRpgEnums.HitType getHitType(int accuracy) {
        int index = (accuracy - 1) / 20;
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
        return getHitType(accuracy);
    }

    private int rollAccuracy(int defense, int attackValue, BattleFieldObject attacked, Unit attacker, DC_ActiveObj action) {
        int base = DC_Calculator.getAccuracyRating(defense, attackValue);
        int dice = 1 + game.getState().getChaosLevel();
        int plus = DiceMaster.d20(attacked, dice);
        int minus = DiceMaster.d20(attacker, dice);
        return base + plus - minus;
    }

    public void initHitType(Attack attack) {
        NewRpgEnums.HitType hitType = getHitType(attack);
        logHit(hitType, attack);


    }

    private void logHit(NewRpgEnums.HitType hitType, Attack attack) {
    }

    public AccuracyBreakdown createBreakdown(Attack attack) {
        Map<NewRpgEnums.HitType, Integer> chances = new LinkedHashMap<>();
        Map<NewRpgEnums.HitType, Integer> min = new LinkedHashMap<>();
        Map<NewRpgEnums.HitType, Integer> max = new LinkedHashMap<>();
        for (NewRpgEnums.HitType type : types) {
            int chance = getChance(attack, type);
            if (chance > 0) {
                chances.put(type, chance);
            } else continue;
            min.put(type, getMinDmg(attack, type));
            max.put(type, getMaxDmg(attack, type));
        }
        AccuracyBreakdown breakdown = new AccuracyBreakdown(chances, min, max);
        return breakdown;
    }

    public void critMiss(Attack attack) {
        //change the target of the attack - and then? Graze? Self-damage?

    }

    public void deadEye(Attack attack) {
        Deadeye[] options = getDeadEyeOptions(attack);
        Deadeye deadeye = new ChoiceEventMaster<Deadeye>().promptAndWait(options);
        apply(deadeye, attack);
    }

    private void apply(Deadeye deadeye, Attack attack) {
        //abstract? damageModifier, effect, target(s)
    }

    private Deadeye[] getDeadEyeOptions(Attack attack) {

        return new Deadeye[]{

        };
    }


}
