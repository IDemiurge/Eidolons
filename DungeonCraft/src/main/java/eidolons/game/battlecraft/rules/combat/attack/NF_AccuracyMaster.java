package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.DC_Calculator;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.DiceMaster;
import eidolons.game.core.game.DC_Game;

public class NF_AccuracyMaster {
    public static final HitType[] types = HitType.values();
    private DC_Game game;

    public NF_AccuracyMaster(DC_Game game) {
        this.game = game;
    }

    public enum HitType{
        critical_miss,
        miss,
        graze,
        hit,
        critical_hit,
        deadeye,
        ;
    }
    public HitType getHitType(int accuracy){
        int index = (accuracy-1)/20;
        return types[index];
    }
        public HitType getHitType(Attack attack){
        int defense =         DefenseVsAttackRule.getDefenseValue(attack);
        int attackValue =         DefenseVsAttackRule.getAttackValue(attack);
                    int         accuracy = rollAccuracy(defense, attackValue ,
                            attack.getAttacked(),
                            attack.getAttacker(),
                            attack.getAction()
                            );
        return getHitType(accuracy);
    }

    private int rollAccuracy(int defense, int attackValue, BattleFieldObject attacked, Unit attacker, DC_ActiveObj action) {
        int base = DC_Calculator.getAccuracyRating(defense, attackValue);
        int dice =1+ game.getState().getChaosLevel();
        int  plus =  DiceMaster.d20(attacked, dice) ;
        int  minus =  DiceMaster.d20(attacker, dice) ;
        return base+plus-minus;
    }

}
