package main.entity.tools.bf;

import main.entity.obj.BattleFieldObject;
import main.entity.tools.EntityMaster;
import main.entity.tools.EntityResetter;

/**
 * Created by JustMe on 3/5/2017.
 */
public class BfObjResetter extends EntityResetter<BattleFieldObject> {
    public BfObjResetter(BattleFieldObject entity, EntityMaster<BattleFieldObject> entityMaster) {
        super(entity, entityMaster);
    }

}
