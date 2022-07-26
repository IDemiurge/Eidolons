package logic.functions.combat;

import logic.entity.Unit;
import logic.functions.GameController;
import logic.functions.LogicController;
import logic.lane.LanePos;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.SortMaster;

public class UnitMoveLogic extends LogicController {
    public UnitMoveLogic(GameController controller) {
        super(controller);
    }

    public void unitMoveForward(Unit unit, int i) {
        LanePos pos = new LanePos(unit.getPos().lane, unit.getPos().cell - i);
        unit.setPos(pos);
        GuiEventManager.triggerWithNamedParams(AphosEvent.UNIT_MOVE, "unit",unit, "pos", pos);
    }

    public int maxDstMoveForward(Unit unit) {
        int farthest = game.getUnits().stream()
                .filter(u -> u.getPos().lane == unit.getPos().lane && unit.getPos().cell > u.getPos().cell)
                .sorted(SortMaster.sort(s -> s.getPos().lane)).findFirst().orElse(unit).getPos().lane;
        return unit.getPos().lane - farthest;
    }
}
