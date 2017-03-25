package main.entity.tools;

import main.content.values.parameters.G_PARAMS;
import main.data.ability.construct.AbilityConstructor;
import main.entity.Entity;
import main.system.auxiliary.log.LogMaster;

/**
 * Created by JustMe on 2/15/2017.
 */
public   class EntityInitializer<E extends Entity> extends EntityHandler<E> {

    public EntityInitializer(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);
    }


    protected void initDefaults() {

    }

    public void init() {
        getEntity().addToState();
        getEntity(). cloneMaps(getType());
        setParam(G_PARAMS.TURN_CREATED, game.getState().getRound());
    }


    public void construct() {
        if (!getEntity().isConstructed() || game.isSimulation() || getEntity().isConstructAlways()) {
            try {
                getEntity(). resetRef( ); // potential threat
                AbilityConstructor.constructObj(getEntity());
                if (!game.isSimulation()) {
                    getEntity(). setConstructed(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogMaster.log(1
                 , "Error on construction: " + getName());
            }
        }
    }
}

