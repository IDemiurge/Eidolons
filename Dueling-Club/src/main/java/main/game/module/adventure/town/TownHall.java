package main.game.module.adventure.town;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.module.adventure.MacroGame;

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
