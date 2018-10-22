package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 10/18/2018.
 */
public class WanderAi extends AiBehavior {
    public WanderAi(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected boolean isFollowOrAvoid() {
            return false;
    }


    public int getMaxWanderDistance() {
        // default - percent of size? 'don't leave the Block'
        // getType()
        // checkMod(trueBrute)
        return 5;
    }
}
