package eidolons.game.netherflame.main.death;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.universal.DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.CombatLoop;
import eidolons.game.netherflame.main.NF_MetaMaster;
import eidolons.game.netherflame.main.NF_PartyManager;
import eidolons.game.netherflame.main.event.TipMessageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.overlay.choice.VC_DataSource;
import eidolons.libgdx.gui.overlay.choice.VisualChoiceHandler;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import static eidolons.libgdx.gui.overlay.choice.VC_DataSource.VC_OPTION;
import static eidolons.libgdx.gui.overlay.choice.VC_DataSource.VC_TYPE;

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
        if (getMaster().getPartyManager().deathEndsGame()) {
            return true;
        }
        //TODO deal with the corpse loot!!!
        getGame().getLoop().setPaused(true);

        if (VisualChoiceHandler.isOn()) {
            GuiEventManager.triggerWithParams(GuiEventType.VISUAL_CHOICE,
                    new VC_DataSource(VC_TYPE.death));
        VC_OPTION o = (VC_OPTION) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.VISUAL_CHOICE);
        switch (o) {
            case ashen_rebirth:
                return false;
            case fiery_rebirth:
                return false;
            case dissolution:
                return true;
        }
        }
        //Boolean result
        if (getGame().getLoop() instanceof CombatLoop) {
            ((CombatLoop) getGame().getLoop()).endCombat();
        }

        GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK, 2f);
        WaitMaster.WAIT(1000); //need more time or different sequence
        if (!ShadowMaster.isShadowAlive()) {
            TipMessageMaster.death();
        } else {
            getMaster().getShadowMaster();
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

        GdxMaster.setDefaultCursor();
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI);


        WaitMaster.WAIT(600);
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
