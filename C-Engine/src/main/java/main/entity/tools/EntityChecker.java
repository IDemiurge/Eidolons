package main.entity.tools;

import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/15/2017.
 */
public class EntityChecker  <E extends Entity> extends EntityHandler<E> {

    public EntityChecker(E entity, EntityMaster<E> entityMaster) {
        super(entity, entityMaster);




    }
    public boolean checkBool(STD_BOOLS bool) {
        String value = getProperty(G_PROPS.STD_BOOLS);
        if (StringMaster.isEmpty(value)) {
            return false;
        }
        return StringMaster.compareContainers(value, bool.toString(), false);
    }


}
