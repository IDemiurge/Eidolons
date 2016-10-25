package main.game.logic.macro.town;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.macro.MacroGame;

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
