package main.elements.targeting;

import main.elements.Filter;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.math.MyMathObj;

public abstract class TargetingImpl implements Targeting
// , Component
{
    protected Filter<Obj> filter;
    protected MyMathObj numberOfTargets;
    protected boolean friendlyFire;
    protected boolean modsAdded;
    protected Ref ref;

    // protected Conditions conditions;

    @Override
    public String toString() {

        return getClass().getSimpleName()
                // + " with " + filter.toString()
                ;
    }

    @Override
    public Ref getRef() {
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
    }

    @Override
    public Filter<Obj> getFilter() {
        if (filter == null)
            filter = new Filter<Obj>(getRef(), new Conditions());
        return filter;
    }

    public Conditions getConditions() {
        return getFilter().getConditions();
    }

    public void setConditions(Conditions conditions) {
        if (filter == null)
            filter = new Filter<Obj>(getRef(), conditions);
        else
            filter.setConditions(conditions);
    }

    public boolean isModsAdded() {
        return modsAdded;
    }

    public void setModsAdded(boolean modsAdded) {
        this.modsAdded = modsAdded;
    }

}
