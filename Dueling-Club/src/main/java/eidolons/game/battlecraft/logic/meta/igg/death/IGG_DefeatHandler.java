package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.battlecraft.logic.meta.igg.IGG_MetaMaster;
import eidolons.game.battlecraft.logic.meta.igg.IGG_PartyManager;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;
import eidolons.game.battlecraft.logic.meta.universal.DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.CombatLoop;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
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
        if (!isOn()) {
            return true;
        }
        if (!getMaster().getShadowMaster().death()) { //igg demo TODO
            Eidolons.getMainHero().preventDeath();

            return false;
        }
        getMetaGame().getMaster().getPartyManager().getHeroChain().death();

        if (isNoLivesLeft())
            return true;
//deal with the corpse loot
        getGame().getLoop().setPaused(true);

        //isBossFight()
        if (getGame().getLoop() instanceof CombatLoop) {
            ((CombatLoop) getGame().getLoop()).endCombat();
        }

        GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK, 2f);
        WaitMaster.WAIT(1500);
        TipMessageMaster.death();
        //play sound
        GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK, 2f);
        WaitMaster.WAIT(1100);
        GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,
                getMetaGame().getMaster().getPartyManager().getChain().getTypes());
        //use the normal selection events?
        String newHero = (String) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.HERO_SELECTION);
        if (newHero == null) {
            return true;
        }
        getPartyManager().getParty().death();
        getPartyManager().respawn(newHero);
//        getGame().getVisionMaster().refresh();
        GdxMaster.setDefaultCursor();
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI);
//        getGame().getDungeonMaster().getSpawner().spawn();

        getGame().getLoop().setPaused(false);
        return false;
    }

    public boolean isNoLivesLeft() {
        return getMetaGame().getMaster().getPartyManager().getHeroChain().isFinished();
    }

    public static final boolean isOn() {
        return true;
    }

    public static final boolean isTestOn() {
        return true;
    }

    @Override
    public IGG_PartyManager getPartyManager() {
        return (IGG_PartyManager) super.getPartyManager();
    }

    @Override
    public IGG_MetaMaster getMaster() {
        return (IGG_MetaMaster) super.getMaster();
    }

    @Override
    public void fallsUnconscious(Event event) {
        if (!isOn())
            return;
        getMaster().getShadowMaster().heroFell(event);

    }
}
