package eidolons.game.netherflame.main.soul;

import eidolons.content.PARAMS;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.main.NF_PartyManager;
import eidolons.game.netherflame.main.death.ChainHero;
import eidolons.game.netherflame.main.death.ShadowMaster;
import eidolons.libgdx.gui.overlay.choice.VC_DataSource;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Shadow action costs?
 * <p>
 * Soul Ash
 */
public class SoulforceMaster extends MetaGameHandler {

    private static Coordinates lastRespPoint;
    private static SoulforceMaster instance;

    public SoulforceMaster(MetaGameMaster master) {
        super(master);
    }

    public static Coordinates getLastRespPoint() {
        return lastRespPoint;
    }

    //TODO get CLOSEST
    public static void setLastRespPoint(Coordinates lastRespPoint) {
        SoulforceMaster.lastRespPoint = lastRespPoint;
    }

    public static void slain(Unit killed) {
        EidolonLord.lord.soulforceGained(getSoulforceFromKill(killed));
    }

    public static String getTooltip() {
        return "Current Soulforce: " +
                EidolonLord.lord.getSoulforce() + "/" +
                EidolonLord.lord.getSoulforceMax();
    }

    public void shrineActivated(Structure shrine) {
        // activeShrines.add(shrine);
    }

    public static SoulforceMaster getInstance() {
        if (instance == null) {
            instance = new SoulforceMaster(Eidolons.getGame().getMetaMaster());
        }
        return instance;
    }

    public boolean isTrueForm() {
        return ShadowMaster.isShadowAlive();
    }

    private int getResurrectCost(boolean inPlace, Unit hero) {
        return 10 + hero.getIntParam(PARAMS.LEVEL) * 5;
    }

    private static int getSoulforce() {
        return EidolonLord.lord.getSoulforce();
    }

    public static int getSoulforceFromKill(Unit killed) {
        return getForce(killed.getType());
    }

    public static int getForce(ObjType type) {
        return type.getIntParam(PARAMS.POWER) / 50;
    }

    public Set<ChainHero> getHeroesCanRespawn(boolean inPlace, List<ChainHero> heroes) {
        return heroes.stream().filter(hero -> getSoulforce() >= getResurrectCost(inPlace,hero.getUnit())).collect(Collectors.toSet());
    }

    public boolean slipPenalty() {
        int slipPenalty = getSlipPenalty();
        if (getSoulforce() <= slipPenalty) {
            return false;
        }
        EUtils.showInfoText("Soulforce lost: " + slipPenalty);
        EidolonLord.lord.soulforceLost(slipPenalty);
        return true;
    }

    private int getSlipPenalty() {
        return 20 + Eidolons.getMainHero().getLevel() * 2;
    }

    public Set<ChainHero> getHeroesCanRespawn(VC_DataSource.VC_OPTION chosen) {
        if (getGame().getMetaMaster().getPartyManager() instanceof NF_PartyManager) {
            return ((NF_PartyManager) getGame().getMetaMaster().getPartyManager()).getParty().getHeroes();
        }
        return new HashSet<>();
    }
}
