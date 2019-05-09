package eidolons.entity.obj;

import eidolons.entity.handlers.bf.BfObjInitializer;
import eidolons.entity.handlers.bf.structure.StructureMaster;
import eidolons.entity.handlers.bf.structure.StructureResetter;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.core.game.DC_Game;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Structure extends BattleFieldObject {


    private Boolean wall;
    private Boolean landscape;
    private BF_OBJECT_GROUP bfObjGroup;

    public Structure(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public Structure(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        this(type, owner, game, ref);
        setCoordinates(Coordinates.get(x, y));
        if (isWall()) {
            try {
                if (getCoordinates().equals(((LocationMaster) game.getDungeonMaster()).getDungeonWrapper().
                        getMainEntrance().getCoordinates())) {
                    setCoordinates(Coordinates.get(0, 0));
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        boolean results = super.kill(killer, leaveCorpse, quietly);
    if (!overlaying)
        for (BattleFieldObject overlayingObject : getGame().getOverlayingObjects(getCoordinates())) {
            overlayingObject.kill(killer, leaveCorpse, quietly);
        }
if (isWall()){
    getVisionController().getWallObstructionMapper().wallDestroyed(this);
}
        return results;
    }

    @Override
    public String getImagePath() {
        //     this is insanity...
           if (getGame().getDungeonMaster().getDungeonWrapper() != null) {
                    return ImageManager.getThemedImagePath(super.getImagePath(), getGame()
                     .getDungeonMaster().getDungeonWrapper().getColorTheme());
                }
        return super.getImagePath();
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
            setFacing(main.game.bf.directions.FACING_DIRECTION.NONE);
        }
    }

    @Override
    public FACING_DIRECTION getFacing() {
        resetFacing();
        return super.getFacing();
    }

    @Override
    public boolean isNeutral() {
        return true;
    }

    public boolean isLightEmitter() {
        return checkProperty(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.LIGHT_EMITTER.toString());
    }

    public BF_OBJECT_GROUP getBfObjGroup() {
        if (bfObjGroup == null) {
            bfObjGroup = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(BF_OBJECT_GROUP.class, getProperty(G_PROPS.BF_OBJECT_GROUP));
        }
        return bfObjGroup;
    }

}
