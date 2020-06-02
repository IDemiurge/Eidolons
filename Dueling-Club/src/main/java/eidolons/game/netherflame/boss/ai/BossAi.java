package eidolons.game.netherflame.boss.ai;

import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;

public abstract class BossAi extends BossHandler {

    public BossAi(BossManager manager) {
        super(manager);
    }

    public abstract Action getAction();

}
