package main.game.module.adventure.town;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroObj;

public class TownPlace extends MacroObj {

    Town town;

    public TownPlace(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

}
