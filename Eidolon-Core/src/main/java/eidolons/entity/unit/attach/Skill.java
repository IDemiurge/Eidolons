package eidolons.entity.unit.attach;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;

public class Skill extends DC_PassiveObj{
    public Skill(ObjType type, Player owner, GenericGame game, Ref ref) {
        super(type, owner, game, ref);
    }
}
