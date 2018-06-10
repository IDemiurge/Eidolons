package eidolons.game.module.adventure.generation;

import eidolons.macro.MacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.town.Town;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 6/7/2018.
 *
 * Game will usually start in a secondary RNG town...
 */
public class TownGenerator extends EntityGenerator<Town> {

    public Town generate(){
        ObjType type=null ;
        Town town = new Town(getGame(), type, getRef());

        return town;
    }

    private MacroRef getRef() {
        return null;
    }

    private MacroGame getGame() {
        return null;
    }
}
