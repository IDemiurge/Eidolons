package eidolons.system.libgdx;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.IPortalMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.GdxSpeechActions;

public interface GdxBeans {

    GdxSpeechActions createGdxActions();

    IPortalMaster createPortalMaster(DungeonMaster dungeonMaster);
}
