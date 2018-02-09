package main.game.battlecraft.logic.meta.skirmish;

import main.client.cc.logic.party.Party;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 3/26/2017.
 */
public class ArmyUnit extends Party {

    //allegiance

    public ArmyUnit(ObjType type, Unit leader) {
        super(type, leader);
    }
}
