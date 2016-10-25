package main.system.math;


import main.ability.effects.Effects;
import main.entity.Ref;
import main.entity.Referred;
import main.entity.obj.Attachment;

@Deprecated
public class Counter
// extends SoEObj //???
        implements Attachment, MyMathObj, Referred {
    // incremented counter, methods: roll=back, show history
    int value;
    String name;
    boolean runOut;
    private Integer basis;
    private Ref ref;

    // has ref to the source spellOBJ's ID
    public Counter(String name, Ref ref, int value) {
        this.name = name;
        this.value = value;

        setRef(ref);
        basis = ref.getBasis();
    }

    @Override
    public int getInt() {
        return value;
    }

    @Override
    public Integer getInt(Ref ref) {
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    public int tick() {
        value++;
        return value;
    }

    @Override
    public boolean isRetainAfterDeath() {
        return false;
    }

    @Override
    public void setRetainAfterDeath(boolean retainAfterDeath) {

    }

    @Override
    public boolean checkRetainCondition() {
        return false;
    }

    public void tack() {
        value--;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void modifyValue(int value) {
        this.value += value;
    }


    @Override
    public void remove() {

    }

    @Override
    public Effects getEffects() {
        return null;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean b) {

    }

    @Override
    public Ref getRef() {
        // TODO Auto-generated method stub
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
    }
}
