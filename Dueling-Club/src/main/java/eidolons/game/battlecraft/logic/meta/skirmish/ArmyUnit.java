package eidolons.game.battlecraft.logic.meta.skirmish;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.party.Party;
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
