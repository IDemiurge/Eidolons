package elements.exec.condition;

import framework.entity.Entity;

/**
 * Created by Alexander on 8/22/2023
 */
public interface EntityCondition<T extends Entity> extends Condition{
    default boolean check(){
        return false;
    }
    boolean checkEntity(T entity);
}
