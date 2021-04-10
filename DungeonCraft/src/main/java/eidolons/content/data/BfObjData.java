package eidolons.content.data;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import main.entity.Entity;

public class BfObjData extends EntityData<BfObjData.BF_OBJ_VALUE> {

    public BfObjData(Entity entity) {
        super(entity);
    }

    public enum  BF_OBJ_VALUE implements LevelStructure.EditableValue {
        //common
        name,
        id,
        displayed_name,


        space,
        std_bools,
        ;

        private LevelStructure.EDIT_VALUE_TYPE type;

        BF_OBJ_VALUE() {
        }

        BF_OBJ_VALUE(LevelStructure.EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        @Override
        public LevelStructure.EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
    }

    @Override
    public Class<? extends BF_OBJ_VALUE> getEnumClazz() {
        return BF_OBJ_VALUE.class;
    }
}
