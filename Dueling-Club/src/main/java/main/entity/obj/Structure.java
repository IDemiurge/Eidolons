package main.entity.obj;

import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.tools.EntityMaster;
import main.entity.tools.bf.structure.StructureMaster;
import main.entity.tools.bf.structure.StructureResetter;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
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
        if (getDirection()!=null ){
            setFacing(  FacingMaster.getFacingFromDirection(getDirection()));
        } else {
            setFacing( FACING_DIRECTION.NONE);
        }
    }

    @Override
    public FACING_DIRECTION getFacing() {
        resetFacing();
        return super.getFacing();
    }

    public boolean isLightEmitter() {
      return    checkProperty(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.LIGHT_EMITTER.toString());
    }
}
