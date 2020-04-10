package eidolons.content.data;

import eidolons.entity.obj.BattleFieldObject;

public class UnitData extends EntityData {
    public UnitData(String text) {
        super(text);
    }

    public UnitData(BattleFieldObject entity) {
        super(entity);
    }
}
