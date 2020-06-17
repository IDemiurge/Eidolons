package eidolons.game.battlecraft.ai.tools.path.alphastar;

import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

public interface IPathHandler {

    Obj getObj(Coordinates c);

    int getWidth();

    boolean canMoveOnto(Entity obj, Coordinates c);

    int getHeight();
}
