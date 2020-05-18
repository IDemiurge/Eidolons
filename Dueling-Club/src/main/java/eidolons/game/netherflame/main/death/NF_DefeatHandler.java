package eidolons.game.netherflame.main.death;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.universal.DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.CombatLoop;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.main.NF_MetaMaster;
import eidolons.game.netherflame.main.NF_PartyManager;
import eidolons.game.netherflame.main.event.TipMessageMaster;
import eidolons.game.netherflame.main.soul.SoulforceMaster;
import eidolons.libgdx.GdxMaster;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class NF_DefeatHandler extends DefeatHandler {

    public NF_DefeatHandler(MetaGameMaster master) {
        super(master);
    }


    @Override
    public boolean isEnded(boolean surrender, boolean end) {
        //destroyed as a Shadow
        // if (getMaster().getSoulforceMaster().isTrueForm()) {
        //     return !SoulforceMaster.getInstance().died();
        // }
        if (!getMaster().getShadowMaster().death()) {
            Eidolons.getMainHero().preventDeath();
            //what is that?
            return false;
        }
        getMaster().getPartyManager().getHeroChain().death();

        if (SoulforceMaster.getInstance().canRespawnAny( getMaster().getPartyManager().
                getHeroChain().getHeroes()))
            return true;
        //TODO deal with the corpse loot
        getGame().getLoop().setPaused(true);

        if (getGame().getLoop() instanceof CombatLoop) { // really?
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
    @Override
    public void fallsUnconscious(Event event) {
        if (!isOn())
            return;
        getMaster().getShadowMaster().heroFell(event);
    }

    @Deprecated
    public boolean isNoLivesLeft() {
        if (EidolonsGame.TUTORIAL_PATH) {
            return false;
        }
        return getMaster().getPartyManager().getHeroChain().isFinished();
    }

    public static final boolean isOn() {
        return true;
    }

    public static final boolean isTestOn() {
        return true;
    }

    @Override
    public NF_PartyManager getPartyManager() {
        return (NF_PartyManager) super.getPartyManager();
    }


    @Override
    public NF_MetaMaster getMaster() {
        return (NF_MetaMaster) super.getMaster();
    }
}
