package eidolons.game.battlecraft.logic.meta.skirmish;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.party.warband.Warband;
import eidolons.game.module.adventure.entity.faction.FactionObj;

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
