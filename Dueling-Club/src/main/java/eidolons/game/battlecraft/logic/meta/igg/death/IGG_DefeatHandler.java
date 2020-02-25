package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.battlecraft.logic.meta.igg.IGG_PartyManager;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;
import eidolons.game.battlecraft.logic.meta.igg.soul.SoulforceMaster;
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
        if (SoulforceMaster.getInstance().isTrueForm()) {
            if (!SoulforceMaster.getInstance().died())
                return true;

            return false;
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

        GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK, 2f);
        WaitMaster.WAIT(1000);
        if (!ShadowMaster.isShadowAlive()) {
            if (!EidolonsGame.TUTORIAL_PATH) {
                TipMessageMaster.death();
            }
        }
        //play sound
        GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK, 1.5f);
        WaitMaster.WAIT(1100);
        //use the normal selection events?
        String newHero = getPartyManager().chooseNextHero();
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
        if (EidolonsGame.TUTORIAL_PATH) {
            return false;
        }
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
    public void fallsUnconscious(Event event) {
        if (!isOn())
            return;
        getMaster().getShadowMaster().heroFell(event);

    }
}
