package boss.logic.rules;

import boss.BossHandler;
import boss.logic.action.BossAction;
import boss.logic.entity.BossUnit;
import eidolons.entity.feat.active.ActiveObj;
import boss.BossManager;
import boss.demo.DemoBoss;
import main.elements.conditions.DistanceCondition;
import main.game.bf.Coordinates;

public class BossTargeter extends BossHandler<DemoBoss> {
    public BossTargeter(BossManager<DemoBoss> manager) {
        super(manager);
    }
    /*
    just check if action X can target him
    and check if coordinate x:y is part of him, for zone fx
     */

    public boolean bossActionCanTargetMainHero(BossAction action){
        //we could even just switch thru action types and add special conditions that way
        return false;
    }
    public boolean bossHasCoordinate(Coordinates c, BossUnit bossUnit){
        //dynamic?
        return false;
    }
    public boolean actionCanTargetBoss(ActiveObj action, BossUnit bossUnit){
        // normal targeting check?
        // the trick is that boss being at 10:10 with size 3x3 should be targetable at 7:7 etc

        DistanceCondition condition;
        action.getRange();

        action.getOwnerUnit();



        return false;
    }
}
