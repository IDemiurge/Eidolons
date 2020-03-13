package eidolons.game.netherflame.boss_.ai;

import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.boss_.BossModel;

public abstract class BossAi extends AiMaster {
    public BossAi(DC_Game game, BossModel model) {
        super(game);
        atomicAi = new BossAtomicAi(this, model);
        //ignore facing - how?
        priorityManager = new BossPriorityManager(this, model);
    }

    /*
    use priority functions?

     */

    public abstract Action getAction();



}
