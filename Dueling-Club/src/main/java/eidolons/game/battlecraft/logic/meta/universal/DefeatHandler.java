package eidolons.game.battlecraft.logic.meta.universal;

import main.game.logic.event.Event;

public class DefeatHandler<E extends MetaGame> extends MetaGameHandler<E>{
    public DefeatHandler(MetaGameMaster master) {
        super(master);
    }

    public boolean isEnded(boolean surrender, boolean end) {

        return true;
    }

    public void fallsUnconscious(Event event) {
    }
}
