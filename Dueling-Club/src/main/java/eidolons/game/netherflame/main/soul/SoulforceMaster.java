package eidolons.game.netherflame.main.soul;

import eidolons.content.PARAMS;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.main.death.ChainHero;
import eidolons.game.netherflame.main.death.ShadowMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.List;

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

    public void shrineActivated(Structure shrine){
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

    private float getResurrectCost(Unit hero) {
        return 10 + hero.getIntParam(PARAMS.LEVEL) * 5;
    }

    private static int getSoulforce() {
        return EidolonLord.lord.getSoulforce();
    }

    public static float getSoulforceFromKill(Unit killed) {
        return getForce(killed.getType());
    }

    public static float getForce(ObjType type) {
        return type.getIntParam(PARAMS.POWER)/50;
    }

    public boolean canRespawnAny(List<ChainHero> heroes) {
        for (ChainHero hero : heroes) {
            if (getSoulforce() >= getResurrectCost(hero.getUnit())) {
                return true;
            }
        }
        return false;
    }
}
