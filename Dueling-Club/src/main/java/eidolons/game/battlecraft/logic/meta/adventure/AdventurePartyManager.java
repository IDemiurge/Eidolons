package eidolons.game.battlecraft.logic.meta.adventure;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.macro.global.persist.Loader;

/**
 * Created by JustMe on 2/7/2018.
 *
 * Manage save/load?
 */
public class AdventurePartyManager extends ScenarioPartyManager {
    private final boolean load;

    public AdventurePartyManager(MetaGameMaster master, boolean load) {
        super(master);
        this.load=load;
    }

    @Override
    public Party initPlayerParty() {
        selectedHero= Loader.getLoadedMainHeroName();
        return super.initPlayerParty();
    }

    @Override
    public boolean isRandomOneHero() {
        return !load;
    }

    @Override
    public boolean isChooseOneHero() {
        return true; //TODO refactor - now just used as a flag for if
    }

    protected boolean isWaitForGdx() {
        return false;
    }

    @Override
    protected Unit findMainHero() {
        return party.getLeader();
    }
}
