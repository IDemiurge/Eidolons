package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.module.herocreator.logic.party.Party;

public class IGG_PartyManager extends PartyManager<IGG_Meta> {
    public IGG_PartyManager(MetaGameMaster master) {
        super(master);
    }

    @Override
    public Party initPlayerParty() {
        return null;
    }

    @Override
    protected Unit findMainHero() {
        return null;
    }
}
