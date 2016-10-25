package main.elements.targeting;

import main.elements.Filter;
import main.entity.Ref;

public class SelectionManager {

    public static Integer select(Filter filter, Ref ref) {

        return ref.getGame().getManager().select(filter, ref);
    }

}
