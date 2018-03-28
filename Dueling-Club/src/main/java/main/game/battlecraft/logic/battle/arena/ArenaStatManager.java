package main.game.battlecraft.logic.battle.arena;

import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager;

/**
 * Created by JustMe on 5/8/2017.
 */
public class ArenaStatManager extends BattleStatManager<ArenaBattle> {
    private static float GLORY_FACTOR = 1;
    private int glory;

    public ArenaStatManager(BattleMaster master) {
        super(master);
    }


    public void unitDies(Unit killed) {
        if (killed.getGame().isDummyMode()

         // TODO until fixed
         || true
         ) {
            return;
        }
//        if (killed.getOriginalOwner().isMe()) {
//            if (killed.isHero() && isRated(killed)) {
//                if (!fallenHeroes.contains(killed)) {
//                    fallenHeroes.add(killed);
//                }
//            } else {
//                if (!slainPlayerUnits.contains(killed)) {
//                    slainPlayerUnits.add(killed);
//                }
//            }
//            getOutcomeManager(). checkOutcomeClear();
//        }
//        // TODO temp
//        if (!killed.getOriginalOwner().isMe()) {
//            if (isRated(killed)) {
//                if (!ratedEnemyUnitsSlain.contains(killed)) {
//                    ratedEnemyUnitsSlain.add(killed);
//                }
//            }
//            if (!slainUnits.contains(killed)) {
//                slainUnits.add(killed);
//            }
//
//            getOutcomeManager(). checkOutcomeClear();
//        }

    }

    private boolean isRated(Unit killed) {
        // TODO not summoned
        return killed.getRef().getObj(KEYS.SUMMONER) == null;
    }
}
