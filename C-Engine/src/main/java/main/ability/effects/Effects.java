package main.ability.effects;

import main.ability.effects.continuous.ContinuousEffect;
import main.data.ability.OmittedConstructor;
import main.data.ability.construct.ConstructionManager;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.system.datatypes.DequeImpl;
import main.system.math.Formula;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Effects extends EffectImpl implements Iterable<Effect> {
    private DequeImpl<Effect> effects;

    public Effects(Effect effects2) {
        this.add(effects2);

    }

    public Effects(Effect effect, Effect effect2) {
        this(effect);
        add(effect2);
    }

    public Effects(Effect effect, Effect effect2, Effect effect3) {
        this(effect, effect2);
        add(effect3);
    }

    public Effects(Effect effect, Effect effect2, Effect effect3, Effect effect4) {
        this(effect, effect2, effect3);
        add(effect4);
    }

    public Effects(Effect effect, Effect effect2, Effect effect3, Effect effect4, Effect effect5) {
        this(effect, effect2, effect3, effect4);
        add(effect5);
    }

    public Effects(Effect[] effs) {
        this();
        this.addAll(Arrays.asList(effs));
    }

    @OmittedConstructor
    public Effects() {
        setEffects(new DequeImpl<>());
    }

    // @Override
    // public boolean apply(Ref ref) {
    // setRef(ref);
    //
    // return apply();
    // }
    @OmittedConstructor
    public Effects(List<Effect> effects) {
        this(effects.toArray(new Effect[effects.size()]));
    }

    @Override
    public void initLayer() {
        for (Effect effect : getEffects()) {
            super.initLayer();
            setLayer(Math.max(getLayer(), effect.getLayer()));
        }
    }

    @Override
    public void remove() {
        getEffects().forEach(e-> e.remove());
    }

    @Override
    public Formula getFormula() {
        return effects.get(0).getFormula();
    }

    public void setIrresistible(boolean b) {
        for (Effect effect : getEffects()) {
            effect.setIrresistible(b);
        }

    }

    @Override
    public void setForcedLayer(Integer forcedLayer) {
        for (Effect effect : getEffects()) {
            effect.setForcedLayer(forcedLayer);
        }
        this.forcedLayer = forcedLayer;
    }

    @Override
    public void setForceStaticParse(Boolean forceStaticParse) {
        for (Effect effect : getEffects()) {
            effect.setForceStaticParse(forceStaticParse);
        }
    }

    @Override
    public boolean applyThis() {
        boolean result = true;
        for (Effect effect : getEffects()) {
            // result &= effect.apply(ref);
            if (isReconstruct()) {
                if (effect.getConstruct() != null) {
                    try {
                        result &= ((Effect) ConstructionManager.construct(effect.getConstruct()))
                                .apply(ref);
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (effect instanceof ContinuousEffect) // ?
            {
                result &= effect.apply(ref);
            } else {
                effect.setRef(ref);
                result &= effect.apply();
            }
        }
        return result;

    }

    @Override
    public String toString() {
        String result = "Effects: ";
        for (Effect effect : getEffects()) {
            result += effect.toString() + ";\n";
        }

        return result.substring(0, result.length() - 1);
    }

    @Override
    public String getTooltip() {
        String result = "";
        for (Effect effect : getEffects()) {
            result += effect.getTooltip() + "; ";
        }
        return result.substring(0, result.length() - 2);
    }

    public boolean add(Effect e) {
        if (e instanceof Effects) {
            return addAll(((Effects) e).getEffects());
        }
        return getEffects().add(e);
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        for (Effect effect : getEffects()) {
            effect.setRef(ref);
        }
    }

    public boolean addAll(Collection<? extends Effect> c) {
        return getEffects().addAll(c);
    }

    public boolean contains(Object o) {
        return getEffects().contains(o);
    }

    public Iterator<Effect> iterator() {
        return getEffects().iterator();
    }

    public boolean remove(Object o) {
        return getEffects().remove(o);
    }

    public int size() {
        return getEffects().size();
    }

    public Object[] toArray() {
        return getEffects().toArray();
    }

    public DequeImpl<Effect> getEffects() {
        if (effects == null) {
            setEffects(new DequeImpl<>());
        }
        return effects;
    }

    public void setEffects(DequeImpl<Effect> effects) {
        this.effects = effects;
    }

    @Override
    public void appendFormulaByMod(Object mod) {
        for (Effect e : this.effects) {
            e.appendFormulaByMod(mod);
        }
    }

    @Override
    public void addToFormula(Object mod) {
        for (Effect e : this.effects) {
            e.addToFormula(mod);
        }
    }

    @Override
    public boolean isIgnoreGroupTargeting() {
        for (Effect e : this.effects) {
            if (e.isIgnoreGroupTargeting()) {
                return true;
            }
        }
        return super.isIgnoreGroupTargeting();
    }

    @Override
    public void setTrigger(Trigger trigger) {
        for (Effect e : this.effects) {
            e.setTrigger(trigger);
        }
    }

    @Override
    public void  setOriginalFormula(Formula formula) {
        for (Effect e : this.effects) {
            e. setOriginalFormula(formula);
        }
    }

    @Override
    public Effect getCopy() {
        if (!isCopied()) {
            setCopied(true);
            return this;
        }
        Effects effects = new Effects();
        for (Effect e : this.effects) {
            effects.add(e.getCopy());
        }
        // for (Construct c : construct.getConstructs()) {
        // effects.add((Effect) c.construct());
        // }

        return effects;
        // (Effect) construct.construct();
    }

    public Effect get(int index) {
        return effects.get(index);
    }

    public void addFirst(Effect e) {
        effects.addFirst(e);
    }

    public void addLast(Effect e) {
        effects.addLast(e);
    }

}
