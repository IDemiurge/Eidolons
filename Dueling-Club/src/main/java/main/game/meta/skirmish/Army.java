package main.game.meta.skirmish;

import main.entity.obj.unit.Unit;
import main.game.logic.faction.Faction;

import java.util.List;

/**
 * Created by JustMe on 3/24/2017.
 */
public class Army { // entity?
    Unit general;
    List<ArmyUnit> units;
    Faction faction;
    Skirmish skirmish;


    public Army(Unit general, Faction faction, Skirmish skirmish) {
    }
}
