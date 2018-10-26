package eidolons.game.battlecraft.ai.explore;

import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehaviorManager;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 10/25/2018.
 */
public class ExploreAiManager extends AiMaster {
    private AiBehaviorManager behaviorManager;

    public ExploreAiManager(DC_Game game) {
        super(game);
        behaviorManager = new AiBehaviorManager(this);
        priorityManager = DC_PriorityManager.alt(this);
    }

    @Override
    public void initialize() {
        super.initialize();
        behaviorManager.initialize();
    }

    public AiBehaviorManager getBehaviorManager() {
        return behaviorManager;
    }
}
