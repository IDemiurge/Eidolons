package eidolons.game.module.adventure.entity.town;

import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.entity.MacroObj;
import eidolons.game.module.adventure.entity.town.Tavern.TOWN_PLACE_TYPE;
import main.content.values.properties.MACRO_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;

public class TownPlace extends MacroObj {

    Town town;
    private TOWN_PLACE_TYPE townPlaceType;

    public TownPlace(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public TOWN_PLACE_TYPE getTownPlaceType() {
        if (townPlaceType == null)
            townPlaceType = new EnumMaster<TOWN_PLACE_TYPE>().
             retrieveEnumConst(TOWN_PLACE_TYPE.class, getProperty(MACRO_PROPS.TOWN_PLACE_TYPE));
        return townPlaceType;
    }

    public void setTownPlaceType(TOWN_PLACE_TYPE townPlaceType) {
        this.townPlaceType = townPlaceType;
    }
}
