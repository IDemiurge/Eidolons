package eidolons.game.battlecraft.rules.combat;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;

public class CombatFunctions {
    public static int getBlockChanceReduction(Unit attacker) {
        return attacker.getIntParam(PARAMS.ARMOR_PENETRATION);
    }
}
