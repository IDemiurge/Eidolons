package main.entity.handlers;

import main.content.values.parameters.G_PARAMS;
import main.data.ability.construct.AbilityConstructor;
import main.entity.Entity;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/15/2017.
 */
public class EntityInitializer<E extends Entity> extends EntityHandler<E> {

    public EntityInitializer(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);
    }


    protected void initDefaults() {

    }

    public void init() {
        getEntity().addToState();
        getEntity().cloneMaps(getType());
        setParam(G_PARAMS.TURN_CREATED, game.getState().getRound());
    }


    public void construct() {
        if (!getEntity().isConstructed() || game.isSimulation() || getEntity().isConstructAlways()) {
            getEntity().resetRef(); // potential threat
            if (!CoreEngine.isIDE())
            {
                AbilityConstructor.constructObj(getEntity());
                if (!game.isSimulation())
                    getEntity().setConstructed(true);
                } else
                    try {
                        AbilityConstructor.constructObj(getEntity());
                        if (!game.isSimulation()) {
                            getEntity().setConstructed(true); //otherwise we want to do it each time??
                        }
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                        LogMaster.log(1
                         , "Error on construction: " + getName());
                    }
            }
        }
    }

