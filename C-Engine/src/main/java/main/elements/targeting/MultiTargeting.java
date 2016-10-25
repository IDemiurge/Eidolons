package main.elements.targeting;

import main.elements.Filter;
import main.entity.Ref;
import main.entity.group.GroupImpl;
import main.entity.obj.Obj;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class MultiTargeting extends TargetingImpl {
    private Targeting[] targetings;
    private Ref[] refs;
    private Targeting targeting;
    private Formula nOfTargets;
    private boolean noDuplicates = true;
    private boolean ignoreGroupTargeting = true;

    public MultiTargeting(Targeting targeting1, Targeting targeting2) {
        this(true, targeting1, targeting2);
    }

    public MultiTargeting(Targeting targeting1, Targeting targeting2, Targeting targeting3) {
        this(true, targeting1, targeting2, targeting3);
    }

    public MultiTargeting(Targeting targeting1, Targeting targeting2, Targeting targeting3,
                          Targeting targeting4) {
        this(true, targeting1, targeting2, targeting3, targeting4);
    }

    public MultiTargeting(Boolean noDuplicates, Targeting... targetings) {
        this.noDuplicates = noDuplicates;
        this.targetings = targetings;
        refs = new Ref[targetings.length];
    }

    public MultiTargeting(Targeting targeting, Formula nOfTargets) {
        this.targeting = targeting;
        this.nOfTargets = nOfTargets;
    }

    @Override
    public Filter<Obj> getFilter() {
        try {
            if (targeting != null) {
                return targeting.getFilter();
            } else {
                return targetings[0].getFilter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.getFilter();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean select(Ref ref) {

        boolean result = true;
        int i = 0;

        if (nOfTargets != null && targeting != null) {
            int number = nOfTargets.getInt(ref);

            targetings = new Targeting[number];
            refs = new Ref[targetings.length];
            Arrays.fill(targetings, targeting);
            ignoreGroupTargeting = false;
        }
        ArrayList<Integer> IDs = new ArrayList<Integer>(targetings.length);
        for (Targeting targeting : targetings) {
            Ref REF = Ref.getCopy(ref);
            if (noDuplicates) {
                for (Integer id : IDs) {
                    if (id != null) // ?
                        targeting.getFilter().getDynamicExceptions().add(id);
                }

            }

            result &= targeting.select(REF);
            ref.setTarget(REF.getTarget());
            refs[i] = REF;
            IDs.add(REF.getTarget());
            i++;
        }
        if (!result)
            return false;

        Collection<Integer> ids = new LinkedList<Integer>();
        for (Ref REF : refs) {
            ids.add(REF.getTarget());
        }
        GroupImpl group = new GroupImpl(ref.getGame(), ids, ignoreGroupTargeting);
        ref.setGroup(group);
        return result;
    }

    public synchronized boolean isIgnoreGroupTargeting() {
        return ignoreGroupTargeting;
    }

    public synchronized void setIgnoreGroupTargeting(boolean ignoreGroupTargeting) {
        this.ignoreGroupTargeting = ignoreGroupTargeting;
    }

    public Targeting[] getTargetings() {
        return targetings;
    }

    public Ref[] getRefs() {
        return refs;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public Formula getnOfTargets() {
        return nOfTargets;
    }

    public boolean isNoDuplicates() {
        return noDuplicates;
    }
}
