package eidolons.game.battlecraft.logic.meta.adventure;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.game.DC_Game;
import eidolons.system.text.TextMaster;
import main.system.PathUtils;

/**
 * Created by JustMe on 2/7/2018.
 */
public class AdventureMetaMaster extends ScenarioMetaMaster {
    public boolean loaded;

    public AdventureMetaMaster(String data, boolean load) {
        super(data);
        this.loaded = load;
    }

    @Override
    public boolean isRngDungeon() {
        return true;
    }

    @Override
    protected PartyManager createPartyManager() {
        return new AdventurePartyManager(this, loaded);
    }

    @Override
    protected DialogueFactory createDialogueFactory() {
        return new DialogueFactory() {
            protected String getFileRootPath() {
                return
                        PathUtils.buildPath(
                                "adventure",
                                "dialogue"
                                , TextMaster.getLocale(),
                                PathUtils.getPathSeparator());
            }
        };
    }
}
