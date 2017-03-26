package main.game.meta.skirmish;

import main.entity.obj.unit.Unit;
import main.game.logic.faction.Faction;

/**
 * Created by JustMe on 3/26/2017.
 */
public class ArmyFactory {

    public static Army getArmy(Unit general, Faction faction, Skirmish skirmish){
        Army army=new Army(general, faction, skirmish);
//        generateArmyUnits(army);
        return army;
    }
}
