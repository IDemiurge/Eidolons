package main.entity.obj;

import main.entity.Ref;
import main.entity.tools.EntityMaster;
import main.entity.tools.bf.structure.StructureMaster;
import main.entity.tools.bf.structure.StructureResetter;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Structure extends BattleFieldObject {


    public Structure(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public Structure(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        this(type, owner, game, ref);
        setCoordinates(new Coordinates(x, y));
    }

    @Override
    protected EntityMaster initMaster() {
        return new StructureMaster(this);
    }

    @Override
    public StructureResetter getResetter() {
        return (StructureResetter) super.getResetter();
    }

    public boolean isBfObj() {
        return true;
    }

    public boolean canAct() {
        return false;
    }

    public void resetFacing() {
//        getResetter().setFacing();
    }
}
