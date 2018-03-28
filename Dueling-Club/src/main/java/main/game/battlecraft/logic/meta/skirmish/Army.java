package main.game.battlecraft.logic.meta.skirmish;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.meta.party.warband.Warband;
import main.game.module.adventure.faction.FactionObj;

import java.util.List;

/**
 * Created by JustMe on 3/24/2017.
 */
public class Army extends Warband { // entity?
    Unit general;
    List<ArmyUnit> units;
    FactionObj faction;
    Skirmish skirmish;


    public Army(Unit general, FactionObj faction, Skirmish skirmish) {
    }
}
