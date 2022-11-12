package eidolons.entity.handlers.bf.structure;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.UnitModel;
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

    private boolean firstResetDone;

    public StructureResetter(Structure entity, EntityMaster<Structure> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public void toBase() {
        if (firstResetDone){
            if (getEntity().isFull(PARAMS.ENDURANCE))
                if (getEntity().isFull(PARAMS.TOUGHNESS))
                    return;
        }
        super.toBase();
        firstResetDone=true;
    }

}
