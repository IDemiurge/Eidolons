package eidolons.game.battlecraft.ai.tools.path.alphastar;

import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.ai.tools.path.Choice;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StarBuilder extends AiHandler implements IPathHandler {

    private final PathingManager pathingManager;

    public StarBuilder(AiMaster master) {
        super(master);
        pathingManager= new PathingManager(getGame().getGrid(), this);
    }

    public ActionPath getPath(Coordinates c, Coordinates c2){
        Path path = pathingManager.getPath(false, true, c, c2);
        Choice[] choices =  path.nodes.stream().
                map(node -> node.coordinates).collect(Collectors.toList()).toArray(new Choice[0]);
      return   new ActionPath(c2, choices);
    }

    public List<ActionPath> build(List<Coordinates> targetCells) {
        List<ActionPath> paths=    new ArrayList<>() ;
        for (Coordinates c : targetCells) {
            paths.add(getPath(getUnit().getCoordinates(), c));
        }
        return paths;
    }

    @Override
    public Obj getObj(Coordinates c) {
        return getGame().getObjectByCoordinate(c);
    }

    @Override
    public int getWidth() {
        return   getGame().getModule().getWidth();
    }

    @Override
    public boolean canMoveOnto(Entity obj, Coordinates c) {
        return getGame().getRules().getStackingRule().canBeMovedOnto(obj, c);
    }

    @Override
    public int getHeight() {
        return getGame().getModule().getHeight();
    }
}
