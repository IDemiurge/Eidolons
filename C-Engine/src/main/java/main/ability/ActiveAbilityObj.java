package main.ability;

import main.entity.Ref;
import main.game.Game;
import main.game.player.Player;

public class ActiveAbilityObj extends AbilityObj {

    public ActiveAbilityObj(AbilityType type, Ref ref, Player player, Game game) {
        super(type, ref, player, game);
    }

}
