package boss.logic.rules;

import eidolons.entity.obj.unit.Unit;
import main.game.bf.Coordinates;

public class SoulforceRules {
/*
could change between rounds and bosses
 */
    public int getSoulforceGainFromCell(Coordinates c){
        return 0;
    }
    public int getSoulforceCostRespawn(Unit newHero){
        return 0;
    }

    public void respawned(){

    }

    public void died(){

    }

    public void sacrifice(Unit hero, boolean forced){

    }
}
