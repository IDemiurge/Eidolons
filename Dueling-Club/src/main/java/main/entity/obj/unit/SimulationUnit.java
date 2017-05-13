package main.entity.obj.unit;

import main.content.PROPS;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.core.game.SimulationGame;

/**
 * Created by JustMe on 5/11/2017.
 */
public class SimulationUnit extends Unit {
    public SimulationUnit(ObjType type, DC_Game game) {
        super(type, game);
    }

    @Override
    public SimulationGame getGame() {
        return (SimulationGame) super.getGame();
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        return (DC_FeatObj) getGame().getSimulationObj(this, type,
                skill ? PROPS.SKILLS : PROPS.CLASSES);
    }
}
