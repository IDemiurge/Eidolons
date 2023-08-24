package elements.exec.condition;

import elements.exec.EntityRef;
import framework.entity.Entity;

/**
 * Created by Alexander on 8/22/2023
 */
@FunctionalInterface
public interface Condition  {
    boolean check(EntityRef ref);
}
