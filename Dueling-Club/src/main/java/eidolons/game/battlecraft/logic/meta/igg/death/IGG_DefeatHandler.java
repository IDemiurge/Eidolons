package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.battlecraft.logic.meta.universal.DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class IGG_DefeatHandler extends DefeatHandler<IGG_Meta> {
    public IGG_DefeatHandler(MetaGameMaster master) {
        super(master);
    }

    @Override
    public boolean isEnded(boolean surrender, boolean end) {

        if (getMetaGame().getHeroChain().isFinished())
            return true;
//deal with the corpse loot
        getGame().getLoop().setPaused(true);

//        GuiEventManager.trigger(GuiEventType. SHOW_CHAIN, getMetaGame().getHeroChain());
//use the normal selection events?
        String newHero = (String) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.SELECTION);
        if (newHero == null) {
            return true;
        }
        getPartyManager().heroSelected(newHero);

//        getGame().getDungeonMaster().getSpawner().spawn();

        getGame().getLoop().setPaused(false);
        return false;
    }
}
