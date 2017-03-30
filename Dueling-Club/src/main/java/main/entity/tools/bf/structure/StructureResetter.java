package main.entity.tools.bf.structure;

import main.content.PROPS;
import main.entity.Ref.KEYS;
import main.entity.obj.Structure;
import main.entity.obj.unit.DC_UnitModel;
import main.entity.tools.EntityMaster;
import main.entity.tools.EntityResetter;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.FacingMaster;
import main.system.auxiliary.EnumMaster;

/**
 * Created by JustMe on 3/5/2017.
 */
public class StructureResetter extends EntityResetter<Structure> {

    public StructureResetter(Structure entity, EntityMaster<Structure> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public void toBase() {
        super.toBase();
    }

    public void resetFacing() {
        FACING_DIRECTION facing = null;
        if (facing != null) {
            setProperty(PROPS.FACING_DIRECTION, facing.getName());
        } else {
            String name = getProperty(PROPS.FACING_DIRECTION);
            facing = (new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class,
             name));
            if (facing == null) {
                if (getEntity(). getDirection() != null) {
                    FacingMaster.getFacingFromDirection(getEntity().getDirection());
                } else if (getRef().getObj(KEYS.SUMMONER) != null) {
                    facing = ((DC_UnitModel) getRef().getObj(KEYS.SUMMONER)).getFacing();
                } else {
                    facing = FacingMaster.getRandomFacing();
                }
            }

        }
        getEntity().setFacing(facing);
//        getEntity().resetFacing(facing);
    }
}