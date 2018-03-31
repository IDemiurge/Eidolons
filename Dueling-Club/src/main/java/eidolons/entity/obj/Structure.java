package eidolons.entity.obj;

import eidolons.entity.handlers.bf.structure.StructureResetter;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import eidolons.entity.handlers.bf.BfObjInitializer;
import eidolons.entity.handlers.bf.structure.StructureMaster;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import eidolons.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Structure extends BattleFieldObject {


    private Boolean wall;
    private Boolean landscape;

    public Structure(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public Structure(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        this(type, owner, game, ref);
        setCoordinates(new Coordinates(x, y));
    }

    public boolean isWall() {
        if (wall == null)
            wall = getType().checkProperty(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.WALL.toString());
        return wall;
    }

    public boolean isOwnedBy(Player player) {
        if (getOwner() == null) {
            return player == null;
        }
        return getOwner().equals(player);
    }

    @Override
    public BfObjInitializer getInitializer() {
        return (BfObjInitializer) super.getInitializer();
    }

    @Override
    public Boolean isLandscape() {
        if (landscape == null)
            landscape = getType().checkProperty(G_PROPS.BF_OBJECT_TAGS, BfObjEnums.BF_OBJECT_TAGS.LANDSCAPE.toString());
        return landscape;
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
        if (getDirection() != null) {
            setFacing(FacingMaster.getFacingFromDirection(getDirection()));
        } else {
            setFacing(FACING_DIRECTION.NONE);
        }
    }

    @Override
    public FACING_DIRECTION getFacing() {
        resetFacing();
        return super.getFacing();
    }

    public boolean isLightEmitter() {
        return checkProperty(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.LIGHT_EMITTER.toString());
    }
}