package eidolons.game.netherflame.boss.logic.rules;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.demo.DemoHarvester;
import eidolons.game.netherflame.boss.logic.action.BossAction;
import main.elements.conditions.DistanceCondition;
import main.game.bf.Coordinates;

public class BossTargeter extends BossHandler<DemoHarvester> {
    public BossTargeter(BossManager<DemoHarvester> manager) {
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
    public boolean bossHasCoordinate(Coordinates c){
        //dynamic?
        return false;
    }
    public boolean actionCanTargetBoss(DC_ActiveObj action){
        // normal targeting check?
        // the trick is that boss being at 10:10 with size 3x3 should be targetable at 7:7 etc

        DistanceCondition condition;
        action.getRange();

        action.getOwnerUnit();



        return false;
    }
}
