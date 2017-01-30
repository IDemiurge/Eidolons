package main.elements.targeting;

import main.elements.Filter;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Referred;
import main.entity.obj.Obj;

/*
Uses Filter with Conditions to getFilteredObjects(Ref ref). Then:

Selective – launches User Selection (or AI Selection) from filtered objects;

AutoTargeting – finds the right target by a preset algorithm, e.g. TemplateAutoTarget with ALL will create a Group containing all units in the game to apply Effects to them.

FixedTargeting doesn’t use Filter, just takes direct reference, e.g. {SOURCE} from Ref.

 */
public interface Targeting extends Referred {
    String MULTI_TARGETING = "MULTI";

    Conditions getConditions();

    void setConditions(Conditions conditions);

    boolean select(Ref ref);

    Filter<Obj> getFilter();

    boolean isModsAdded();

    void setModsAdded(boolean b);
}
