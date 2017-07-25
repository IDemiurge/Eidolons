package main.game.battlecraft.logic.meta.universal;

import main.game.battlecraft.logic.meta.party.FormationMaster;
import main.game.battlecraft.logic.meta.party.LoyaltyMaster;
import main.game.module.adventure.travel.LootMaster;

/**
 * Created by JustMe on 5/8/2017.
 */
public abstract class AfterCombatManager<E extends MetaGame> extends MetaGameHandler<E>{
    public AfterCombatManager(MetaGameMaster master) {
        super(master);
    }
    LootMaster lootMaster;
    FormationMaster formationMaster;
    LoyaltyMaster loyaltyMaster;
    public void combatOver(){
        getMaster().getBattleMaster().getBattle();
//        data= getMaster().getBattleMaster().getStatManager().getStats();
//        lootMaster.awardLoot(data);
//        loyaltyMaster.awardLoyalty();
//        formationMaster.checkFormationRequests();
//        constructDialogueSequence
        // loyalty, relations, glory, formation requests,
    }
}
