package eidolons.libgdx.gui.overlay.choice;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.netherflame.main.NF_MetaMaster;
import eidolons.game.netherflame.main.death.ChainHero;

import java.util.Set;

public class VisualChoiceHandler extends MetaGameHandler {

    private static VisualChoiceHandler instance;

    public VisualChoiceHandler(MetaGameMaster master) {
        super(master);
        instance = this;
    }

    public static boolean checkOptionDisabled(VC_DataSource.VC_OPTION option) {
       return  instance.isDisabled(option);
    }

    public static boolean isOn() {
        return false;
    }

    public   boolean isDisabled(VC_DataSource.VC_OPTION option) {

            Set<ChainHero> heroes = null;
            switch (option) {
            case ashen_rebirth:
                heroes= getMaster().getSoulforceMaster().
                        getHeroesCanRespawn(false, getMaster().getPartyManager().
                                getHeroChain().getHeroes());
                return heroes.isEmpty();
            case fiery_rebirth:
                heroes= getMaster().getSoulforceMaster().
                        getHeroesCanRespawn(true, getMaster().getPartyManager().
                                getHeroChain().getHeroes());
                return heroes.isEmpty();
        }

        return false;
    }

    @Override
    public NF_MetaMaster getMaster() {
        return (NF_MetaMaster) super.getMaster();
    }
}
