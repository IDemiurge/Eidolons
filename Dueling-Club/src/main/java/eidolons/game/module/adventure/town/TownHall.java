package eidolons.game.module.adventure.town;

import eidolons.game.module.adventure.MacroGame;
import main.entity.Ref;
import main.entity.type.ObjType;

public class TownHall extends TownPlace {

    public TownHall(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
    }

    public enum QUEST_TYPE {

    }

    public class Quest {
        // giver, type, objective args/descr,
    }

}
