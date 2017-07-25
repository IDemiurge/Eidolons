package main.entity.tools.bf;

import main.content.enums.rules.VisionEnums;
import main.entity.obj.BattleFieldObject;
import main.entity.tools.EntityInitializer;
import main.entity.tools.EntityMaster;
import main.game.battlecraft.logic.battlefield.DC_MovementManager;

/**
 * Created by JustMe on 3/25/2017.
 */
public abstract class BfObjInitializer<T extends BattleFieldObject> extends
 EntityInitializer<T> {
    public boolean initialized;
    public boolean dynamicValuesReady;

    public BfObjInitializer(T entity, EntityMaster<T> entityMaster) {
        super(entity, entityMaster);
    }

    public void addDynamicValues() {
        getEntity().addDynamicValues();

    }

    @Override
    public void init() {
        super.init();

        addDefaultValues();
        addDynamicValues();

    }

    protected void addDefaultFacing() {
        if (getEntity().getOwner()!=null )
        getEntity().setFacing(
         DC_MovementManager.getDefaultFacingDirection(getEntity().getOwner().isMe()));
    }

    public void addDefaultValues() {
        getEntity().setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN);
        addDefaultFacing();

    }
}
