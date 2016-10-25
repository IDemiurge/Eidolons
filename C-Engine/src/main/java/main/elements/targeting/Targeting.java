package main.elements.targeting;

import main.elements.Filter;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Referred;
import main.entity.obj.Obj;

//default targeting mode
public interface Targeting extends Referred {
    String MULTI_TARGETING = "MULTI";

    Conditions getConditions();

    void setConditions(Conditions conditions);

    boolean select(Ref ref);

    Filter<Obj> getFilter();

    boolean isModsAdded();

    void setModsAdded(boolean b);
}
