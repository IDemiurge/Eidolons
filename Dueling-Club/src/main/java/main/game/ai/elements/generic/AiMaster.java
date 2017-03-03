package main.game.ai.elements.generic;

import main.entity.obj.unit.Unit;
import main.game.ai.AI_Logic;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiMaster extends AiHandler {
    protected AI_Logic logic;

    public AiMaster(DC_Game game) {
        super(game);
    }

    public void setUnit(Unit unit) {
        this.unit=unit;
getHandlers().forEach(handler -> handler.setUnit(unit));
    }
}
