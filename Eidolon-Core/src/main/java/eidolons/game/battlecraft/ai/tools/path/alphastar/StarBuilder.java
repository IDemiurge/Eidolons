package eidolons.game.battlecraft.ai.tools.path.alphastar;

import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.ai.tools.path.Choice;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

public class StarBuilder extends AiHandler implements IPathHandler {

    public static final int PREF_MIN_RANGE = 3;
    private final PathingManager pathingManager;

    public StarBuilder(AiMaster master) {
        super(master);
        pathingManager = new PathingManager(this);
    }

    public ActionPath getPath(Unit unit, Coordinates c, Coordinates... target) {
        Path path = pathingManager.getPath(false, true, c, target);
        if (path == null) {
            return null;
        }
        Coordinates prev;
        List<Choice> choices = new ArrayList<>(path.nodes.size());
        try {
            for (int i = path.nodes.size() - 1; i >= 0; i--) {
                prev = c;
                c = path.nodes.get(i).getCoordinates();
                choices.add(createChoice(unit, prev, c));
                game.getCell(c).setObjectsModified(true); //TODO core Review
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            unit.removeTempCoordinates();
        }
        return new ActionPath(path.endPoint, choices);
    }

    private Choice createChoice(Unit unit, Coordinates prev, Coordinates c) {
        AiAction[] aiActions = getActions(unit, prev, c);
        return new Choice(c, aiActions);
    }

    private AiAction[] getActions(Unit unit, Coordinates prev, Coordinates c) {
        List<AiAction> sequence = new ArrayList<>();
        AiAction move = DC_MovementManager.getMoveAction(unit, prev, c);
        if (move == null) {
            return null ;
        }
        sequence.add(move);
        unit.setTempCoordinates(c);
        return sequence.toArray(new AiAction[0]);
    }


    public List<ActionPath> build(Unit unit, List<Coordinates> targetCells) {
        List<ActionPath> paths = new ArrayList<>();
            ActionPath path = getPath(unit, unit.getCoordinates(), targetCells.toArray(new Coordinates[0]));
            if (path != null) {
                paths.add(path);
            }
        return paths;
    }

    @Override
    public Obj getObj(Coordinates c) {
        return getGame().getObjectByCoordinate(c);
    }

    @Override
    public int getWidth() {
        return getGame().getModule().getEffectiveWidth();
    }

    @Override
    public boolean canMoveOnto(Entity obj, Coordinates c) {
        return getGame().getRules().getStackingRule().canBeMovedOnto(obj, c);
    }

    @Override
    public int getHeight() {
        return getGame().getModule().getEffectiveHeight();
    }
}
