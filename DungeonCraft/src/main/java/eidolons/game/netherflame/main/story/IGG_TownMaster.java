package eidolons.game.netherflame.main.story;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.TownMaster;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;

public class IGG_TownMaster extends TownMaster {
    public IGG_TownMaster(MetaGameMaster master) {
        super(master);
    }

    @Override
    protected QuestMaster createQuestMaster() {
        return new IGG_QuestMaster(master);
    }
}
