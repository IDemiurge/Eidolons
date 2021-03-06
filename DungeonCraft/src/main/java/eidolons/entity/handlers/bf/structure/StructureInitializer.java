package eidolons.entity.handlers.bf.structure;

import eidolons.content.PARAMS;
import eidolons.entity.handlers.bf.BfObjInitializer;
import eidolons.entity.obj.Structure;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums;
import main.content.values.properties.G_PROPS;
import main.entity.handlers.EntityMaster;

/**
 * Created by JustMe on 3/25/2017.
 */
public class StructureInitializer extends BfObjInitializer<Structure> {
    public StructureInitializer(Structure entity, EntityMaster<Structure> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public void addDefaultValues() {
        super.addDefaultValues();
        if (checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.INDESTRUCTIBLE)) {
            getType().addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.INDESTRUCTIBLE.toString());
        }
        if (checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.PASSABLE)) {
            getType().addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.PASSABLE.toString());
        }

        getType().addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.LEAVES_NO_CORPSE.toString());

        setParam(PARAMS.C_FOCUS, 0);
        setParam(PARAMS.C_ESSENCE, 0);
    }
}
