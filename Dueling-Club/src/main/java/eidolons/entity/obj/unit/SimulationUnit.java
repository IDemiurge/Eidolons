package eidolons.entity.obj.unit;

import eidolons.content.PROPS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.SimulationGame;
import main.entity.type.ObjType;

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
