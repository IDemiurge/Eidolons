package eidolons.game.netherflame.boss.ai;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.logic.atomic.AtomicAi;
import eidolons.game.netherflame.boss.BossModel;

public class BossAtomicAi extends AtomicAi {
    public BossAtomicAi(BossAi bossAI, BossModel model) {
        super(bossAI);
    }

    @Override
    public Action getAtomicAction(UnitAI ai) {
        return super.getAtomicAction(ai);
    }

    @Override
    public Action getAtomicActionForced(UnitAI ai) {
        return super.getAtomicActionForced(ai);
    }

    @Override
    public Action getAtomicWait(Unit unit) {
        return super.getAtomicWait(unit);
    }

    @Override
    public boolean isOn() {
        return super.isOn();
    }
}
