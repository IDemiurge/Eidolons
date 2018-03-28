package main.game.module.adventure.faction;

import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.module.adventure.MacroRef;
import main.game.module.adventure.entity.MacroObj;

public class Faction extends MacroObj {

    public Faction(ObjType type, DC_Player player) {
        super(type, new MacroRef());
        setOwner(player);
        setOriginalOwner(player);
        player.setFaction(this);
    }

}
