package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.battlecraft.logic.meta.adventure.party.FormationMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.logic.TeaMaster;

/**
 * Created by JustMe on 5/8/2017.
 */
public abstract class AfterCombatManager<E extends MetaGame> extends MetaGameHandler<E> {
    LootMaster lootMaster;
    FormationMaster formationMaster;
    TeaMaster loyaltyMaster;
    public AfterCombatManager(MetaGameMaster master) {
        super(master);
    }

    public void combatOver() {
        getMaster().getBattleMaster().getBattle();
//        data= getMaster().getBattleMaster().getStatManager().getStats();
//        lootMaster.awardLoot(data);
//        loyaltyMaster.awardLoyalty();
//        formationMaster.checkFormationRequests();
//        constructDialogueSequence
        // loyalty, relations, glory, formation requests,
    }
}
