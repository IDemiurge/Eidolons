package eidolons.macro.entity.faction;

import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.macro.entity.MacroObj;
import eidolons.macro.entity.MacroRef;
import main.entity.type.ObjType;

public class Faction extends MacroObj {

    public Faction(ObjType type, DC_Player player) {
        super(type, new MacroRef());
        setOwner(player);
        setOriginalOwner(player);
        player.setFaction(this);
    }

}
