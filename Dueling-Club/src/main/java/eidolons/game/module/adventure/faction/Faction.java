package eidolons.game.module.adventure.faction;

import eidolons.game.module.adventure.entity.MacroObj;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.module.adventure.MacroRef;

public class Faction extends MacroObj {

    public Faction(ObjType type, DC_Player player) {
        super(type, new MacroRef());
        setOwner(player);
        setOriginalOwner(player);
        player.setFaction(this);
    }

}
