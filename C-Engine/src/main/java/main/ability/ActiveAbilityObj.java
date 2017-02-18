package main.ability;

import main.entity.Ref;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;

public class ActiveAbilityObj extends AbilityObj {

    public ActiveAbilityObj(AbilityType type, Ref ref, Player player, Game game) {
        super(type, ref, player, game);
    }

}
