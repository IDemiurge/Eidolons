package eidolons.entity.handlers.bf.structure;

import eidolons.content.PROPS;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.DC_UnitModel;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.entity.Ref.KEYS;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.EntityResetter;
import main.game.bf.directions.FACING_DIRECTION;
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
                if (getEntity().getDirection() != null) {
                    FacingMaster.getFacingFromDirection(getEntity().getDirection());
                } else if (getRef().getObj(KEYS.SUMMONER) != null) {
                    facing = ((DC_UnitModel) getRef().getObj(KEYS.SUMMONER)).getFacing();
                } else {
                    facing = FacingMaster.getRandomFacing();
                }
            }

        }
        getEntity().setFacing(facing);
//        getEntity().setFacing(facing);
    }
}
