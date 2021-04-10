package libgdx.adapters;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.IPortalMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.GdxSpeechActions;
import eidolons.puzzle.portal.PortalMaster;
import eidolons.system.libgdx.GdxBeans;

public class GdxBeansImpl implements GdxBeans {
    @Override
    public GdxSpeechActions createGdxActions() {
        return new SpeechActionsImpl();
    }

    @Override
    public IPortalMaster createPortalMaster(DungeonMaster dungeonMaster) {
        return new PortalMaster(dungeonMaster);
    }
}
