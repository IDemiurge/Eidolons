package main.game.battlecraft.logic.meta.party;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;

/**
 * Created by JustMe on 2/14/2017.
 * <p>
 * if each hero has his own separate gold and items
 * <p>
 * is there any common treasury?
 * that would be nice
 * <p>
 * requests of withdrawal...
 * or donations
 * taking it for yourself - ?
 * your own share... if you exceed, you risk denounciation!
 * <p>
 * general expenses are paid from the Treasury
 */
public class ShareMaster extends MetaPartyHandler {

    public ShareMaster(MetaGameMaster master) {
        super(master);
    }

    public void withdraw(Unit hero, int amount) {
//        getRelationsMaster().impact();
    }

    public void donate(Unit hero, int amount) {
    }


    public void checkTreasuryRequests() {

    }

    public void recalculateTreasuryShares() {

    }

    public void recalculateLootShares() {

    }


}
