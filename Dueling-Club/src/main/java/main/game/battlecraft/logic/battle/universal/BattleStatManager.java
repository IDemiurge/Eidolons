package main.game.battlecraft.logic.battle.universal;

import main.entity.obj.unit.Unit;
import main.system.datatypes.DequeImpl;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleStatManager<E extends Battle> extends BattleHandler<E> {
    private DequeImpl<Unit> slainUnits = new DequeImpl<>();
    private DequeImpl<Unit> ratedEnemyUnitsSlain = new DequeImpl<>();
    private DequeImpl<Unit> fallenHeroes = new DequeImpl<>();
    private DequeImpl<Unit> slainPlayerUnits = new DequeImpl<>();

    public BattleStatManager(BattleMaster master) {
        super(master);
    }

    private void initializeBattle() {
//        battle.setValue(BATTLE_STATS.PLAYER_STARTING_PARTY, game.getPlayerParty());
//        battle.setValue(BATTLE_STATS.LEVEL, getBattleLevel() + "");
//        battle.setValue(BATTLE_STATS.ROUND, "1");

    }

    public void unitDies(Unit killed) {
    }
}
