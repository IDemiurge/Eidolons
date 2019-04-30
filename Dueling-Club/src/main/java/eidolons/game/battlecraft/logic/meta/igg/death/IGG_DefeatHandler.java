package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.battlecraft.logic.meta.igg.IGG_PartyManager;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;
import eidolons.game.battlecraft.logic.meta.universal.DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class IGG_DefeatHandler extends DefeatHandler<IGG_Meta> {
    public IGG_DefeatHandler(MetaGameMaster master) {
        super(master);
    }

    @Override
    public boolean isEnded(boolean surrender, boolean end) {

        if (getMetaGame().getMaster().getPartyManager().getHeroChain().isFinished())
            return true;
//deal with the corpse loot
        getGame().getLoop().setPaused(true);
        getGame().getDungeonMaster().getExplorationMaster().switchExplorationMode(true);

        GuiEventManager.trigger(GuiEventType. FADE_OUT_AND_BACK, 2);
        TipMessageMaster.death();
        //play sound
        GuiEventManager.trigger(GuiEventType. FADE_OUT_AND_BACK, 2);

        GuiEventManager.trigger(GuiEventType. SHOW_SELECTION_PANEL,
                getMetaGame().getMaster().getPartyManager().getChain().getTypes());
        //use the normal selection events?
        String newHero = (String) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.SELECTION);
        if (newHero == null) {
            return true;
        }
        getPartyManager().respawn(newHero);

//        getGame().getDungeonMaster().getSpawner().spawn();

        getGame().getLoop().setPaused(false);
        return false;
    }

    @Override
    public IGG_PartyManager getPartyManager() {
        return (IGG_PartyManager) super.getPartyManager();
    }

    @Override
    public void fallsUnconscious(Event event) {
        super.fallsUnconscious(event);
        //play sprite anim on a spot?

        // apply ability?

//        new SummonEffect()

        //how to give control?
    }
}
