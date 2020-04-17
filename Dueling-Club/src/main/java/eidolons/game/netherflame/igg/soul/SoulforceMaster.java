package eidolons.game.netherflame.igg.soul;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.igg.IGG_PartyManager;
import eidolons.game.netherflame.igg.death.HeroChain;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

/**
 * Shadow action costs?
 *
 * Soul Ash
 *
 *
 *
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

    public static void setLastRespPoint(Coordinates lastRespPoint) {
        SoulforceMaster.lastRespPoint = lastRespPoint;
    }

    public static SoulforceMaster getInstance() {
        if (instance == null) {
            instance = new SoulforceMaster(Eidolons.getGame().getMetaMaster());
        }
        return instance;
    }

    /**
     *
    exchange for life
     advance the Lord

     auto-accumulate?

     souls - manually consume or use for <...> </...>
     */

    public  boolean isTrueForm(){
        return EidolonsGame.BRIDGE;
    }
    //true if gameover
    public  boolean died(){
        if (EidolonsGame.BRIDGE){
            return false;
        }
        /**
         *
         */
        int cost = getResurrectCost();
        if (getSoulforce()<cost) {
            return false;
        }
        EidolonLord.lord.soulforceLost(cost);
        if (getPartyManager() instanceof IGG_PartyManager) {
            ((IGG_PartyManager) getPartyManager()).respawn("Sleepless One");
        }
        return true;
    }

    private int getInitialValue() {
        return 100;
    }
    private int getResurrectCost() {
        return 100;
    }

    private static int getSoulforce() {
        return EidolonLord.lord.getSoulforce();
    }


    public int applySoulforce(Unit hero, HeroChain chain) {
        int amount=0;
        chain.getParty().setParam(PARAMS.SOULFORCE, amount);

        return 0;
    }
        public int getSoulforceCostToRaise(Unit hero) {

        return 0;
    }
        public int getSoulforceCost(DC_ActiveObj action) {
        //displayed how?

        //perhaps it is Shadow's Essence?

        return 0;
    }
    public static int getSoulforceFromKill(Unit killed) {
        return getForce(killed.getType());
    }

    public static int getForce(ObjType type) {
        return type.getIntParam(PARAMS.POWER);
    }

}
