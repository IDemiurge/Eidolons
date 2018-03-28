package main.game.battlecraft.logic.meta.skirmish;

import main.entity.obj.unit.Unit;
import main.game.module.adventure.faction.FactionObj;

/**
 * Created by JustMe on 3/26/2017.
 */
public class ArmyFactory {

    public static Army getArmy(Unit general, FactionObj faction, Skirmish skirmish) {
        Army army = new Army(general, faction, skirmish);
//        generateArmyUnits(army);
        return army;
    }
}
