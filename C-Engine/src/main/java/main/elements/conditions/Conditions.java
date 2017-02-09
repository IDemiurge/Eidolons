package main.elements.conditions;

import main.data.ability.OmittedConstructor;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Referred;
import main.system.ConditionMaster;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

public class Conditions extends Vector<Condition> implements Referred, Condition {

    protected boolean negative = false;
    boolean isTrue;
    boolean or = false;
    Ref ref;
    private Condition lastCheckedCondition;
    private boolean fastFailOnCheck;

    @OmittedConstructor
    public Conditions(Condition... c) {
        this.addAll(Arrays.asList(c));
    }

    @OmittedConstructor
    public Conditions(Conditions c) {
        this.addAll(c);
    }

    @OmittedConstructor
    public Conditions(Condition c1) {
        this.add(c1);
    }

    public Conditions(Condition c1, Condition c2) {
        this.add(c1);
        this.add(c2);

    }

    @OmittedConstructor
    public Conditions(Condition c1, Condition c2, Condition c3) {
        this.add(c1);
        this.add(c2);
        this.add(c3);
    }

    @OmittedConstructor
    public Conditions(Condition c1, Condition c2, Condition c3, Condition c4) {
        this.add(c1);
        this.add(c2);
        this.add(c3);
        this.add(c4);
    }

    @OmittedConstructor
    public Conditions(Collection<? extends Condition> cs) {
        addAll(cs);
    }

    @OmittedConstructor
    public Conditions() {
    }

    public static Conditions join(Condition c1, Condition c2) {
        Conditions c = new Conditions();
        if (c1 != null) {
            c.add(c1);
        }
        if (c2 != null) {
            c.add(c2);
        }
        return c;
    }

    // public Conditions(Condition[] c) {
    // for (Condition condition : c)
    // this.add(condition);
    // }
    @Override
    public boolean contains(Object o) {
        if (o instanceof Condition) {
            for (Condition c : this) {
                if (c.getClass().equals(o.getClass())) {
                    return true;
                }
            }
        }

        return super.contains(o);
    }

    @Override
    public Condition join(Condition condition) {
        add(condition);
        return this;
    }

    @Override
    public boolean check(Ref ref) {
        setRef(ref);
        if (or) {
            return checkAny();
        }
        return check();
    }

    @Override
    public String getTooltip() {
        return "Conditions: ...";
        // return StringMaster.concatenate(new StringGetter(){
        //
        // }, this);
    }

    public boolean checkAny(Ref ref) {
        setRef(ref);
        return checkAny();
    }

    @Override
    public boolean check() {
        isTrue = true;
        for (int i = 0; i < this.size(); i++) {

            isTrue &= this.get(i).check(ref);
            if (!isTrue) {
                if (isFastFailOnCheck()) {
                    break;
                }
                this.setLastCheckedCondition(get(i));
                // break;
            }
        }
        if (negative) {
            return !isTrue;
        }
        return isTrue;
    }

    protected boolean isFastFailOnCheck() {
        return fastFailOnCheck;
    }

    public void setFastFailOnCheck(boolean fastFailOnCheck) {
        this.fastFailOnCheck = fastFailOnCheck;
    }

    @Override
    public boolean check(Entity match) {
        ref = match.getRef().getCopy();
        ref.setMatch(match.getId());
        return check();
    }

    public boolean checkAny() {
        isTrue = false;
        for (int i = 0; i < this.size(); i++) {

            isTrue |= this.get(i).check(ref);
            if (isTrue) {
                if (isFastFailOnCheck()) {
                    break;
                }
                this.setLastCheckedCondition(get(i));

            }
        }

        return isTrue;
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
    public boolean add(Condition c) {
        if (c == null) {
            return false;
        }
        if (c.getClass().getName().contains("ClearShot")) {
            if (ConditionMaster.contains(this, c.getClass())) {
                return false;
            }
        }
        if (checkConditionUnwrap(c)) {
            for (Condition cond : ((Conditions) c)) {
                add(cond);
            }
            return true;
        }

        return super.add(c);

    }

    protected boolean checkConditionUnwrap(Condition c) {
        if (!(this instanceof OrConditions)) {
            if (!(c instanceof OrConditions)) {
                if (c instanceof Conditions) {
                    return true;
                }
            }
        }
        return false;
    }

    public Condition getLastCheckedCondition() {
        return lastCheckedCondition;
    }

    public void setLastCheckedCondition(Condition lastCheckedCondition) {
        this.lastCheckedCondition = lastCheckedCondition;
    }

    public void setNegative(boolean reverse) {
        this.negative = reverse;

    }

    @Override
    public boolean isTrue() {
        return false;
    }

}
