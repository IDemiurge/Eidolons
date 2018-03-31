package eidolons.game.battlecraft.logic.battle.arena;

import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.PlayerManager;

/**
 * Created by JustMe on 5/7/2017.
 */
public class ArenaPlayerManager extends PlayerManager<ArenaBattle> {


    public ArenaPlayerManager(BattleMaster<ArenaBattle> master) {
        super(master);
    }

    public void initializePlayers() {
//        player = new DC_Player(PLAYER_NAME, Color.WHITE, true); // emblem?
//        try {
//            player.setHero_type(getPlayerHeroName());
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }
//        enemyPlayer = new DC_Player(ENEMY_NAME, Color.BLACK, false); // emblem?
//        Player.ENEMY = enemyPlayer;
    }


}
