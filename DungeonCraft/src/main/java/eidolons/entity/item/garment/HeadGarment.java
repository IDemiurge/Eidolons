package eidolons.entity.item.garment;

import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;

public class HeadGarment extends Garment {
    public HeadGarment(ObjType type, Player owner, GenericGame game, Ref ref) {
        super(type, owner, game, ref);
    }
}
