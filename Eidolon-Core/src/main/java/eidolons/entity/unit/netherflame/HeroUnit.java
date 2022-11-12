package eidolons.entity.unit.netherflame;

import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;

/*

 */
public class HeroUnit extends Unit {
    public HeroUnit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
    }

    public HeroUnit(Unit hero) {
        super(hero);
    }

    public HeroUnit(ObjType type) {
        super(type);
    }

    public HeroUnit(ObjType type, DC_Game game) {
        super(type, game);
    }
}
