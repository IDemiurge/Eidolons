package eidolons.netherflame.eidolon.chain;

import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.netherflame.lord.EidolonLord;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

/**
 * Shadow action costs?
 * <p>
 * Soul Ash
 */
public class SoulforceMaster extends MetaGameHandler {

    private static Coordinates lastRespPoint;
    private static SoulforceMaster instance;
    private boolean trueForm;

    public SoulforceMaster(MetaGameMaster master) {
        super(master);
    }

    public static void slain(Unit killed) {
        EidolonLord.lord.soulforceGained(getSoulforceFromKill(killed));
    }

    public static String getTooltip() {
        return "Current Soulforce: " +
                EidolonLord.lord.getSoulforce() + "/" +
                EidolonLord.lord.getSoulforceMax();
    }

    public static SoulforceMaster getInstance() {
        if (instance == null) {
            instance = new SoulforceMaster(Core.getGame().getMetaMaster());
        }
        return instance;
    }

    public boolean isTrueForm() {
        return trueForm;
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

    //TODO
    public boolean soulburn() {
        int slipPenalty = getSlipPenalty();
        if (getSoulforce() <= slipPenalty) {
            return false;
        }
        EUtils.showInfoText("Soulforce lost: " + slipPenalty);
        EidolonLord.lord.soulforceLost(slipPenalty);
        return true;
    }

    private int getSlipPenalty() {
        return 20 + Core.getMainHero().getLevel() * 2;
    }


    public static Coordinates getLastRespPoint() {
        return lastRespPoint;
    }

    //TODO get CLOSEST
    public static void setLastRespPoint(Coordinates lastRespPoint) {
        SoulforceMaster.lastRespPoint = lastRespPoint;
    }

}
