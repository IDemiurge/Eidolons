package main.ability;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.elements.targeting.Targeting;
import main.entity.Ref;

import java.util.*;
import java.util.function.Consumer;

public class Abilities implements Ability, Iterable<Ability> {
    private List<Ability> abils;

    /**
     * Construction system: ???
     */
    public Abilities() {
        abils = new LinkedList<>();

    }

    public boolean add(Ability e) {
        return abils.add(e);
    }

    public boolean addAll(Collection<? extends Ability> c) {
        return abils.addAll(c);
    }

    @Override
    public boolean activate(boolean transmit) {
        return false;
    }

    // public void construct() {
    // AbilityConstructor.construct(abilsDoc);
    //
    // }
    public boolean activate(Ref ref) {
        for (Ability abil : abils) {
            // if (checkBool)
            // abil.activate(ref);

            if (!abil.activate(ref)) {
                if (abil instanceof OneshotAbility) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    public void setForceTargeting(boolean forceTargeting) {
        for (Ability abil : abils) {
            abil.setForceTargeting(forceTargeting);
        }
    }

    @Override
    public boolean resolve() {
        boolean result = true;
        for (Ability abil : abils) {
            result &= abil.resolve();
        }
        return result;
    }

    @Override
    public void addEffect(Effect effect) {
        throw new RuntimeException();
    }

    @Override
    public Effects getEffects() {
        Effects effects = new Effects();
        for (Ability abil : this) {
            effects.add(abil.getEffects());
        }
        return effects;
    }

    @Override
    public void setEffects(Effects effects) {
        throw new RuntimeException();
    }

    @Override
    public Targeting getTargeting() {
        return abils.get(0).getTargeting();
    }

    @Override
    public void setTargeting(Targeting targeting) {
        throw new RuntimeException();
    }

    public List<Ability> getAbils() {
        return abils;
    }

    public void setAbils(List<Ability> abils) {
        this.abils = abils;
    }

    @Override
    public Ref getRef() {
        if (abils.size() == 0) {
            return null;
        }
        return abils.get(0).getRef();
    }

    @Override
    public void setRef(Ref ref) {
        for (Ability abil : abils) {
            abil.setRef(ref);
        }

    }

    @Override
    public boolean isInterrupted() {
        boolean result = false;
        for (Ability abil : abils) {
            result |= abil.isInterrupted();

        }

        return result;
    }

    @Override
    public void setInterrupted(boolean b) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean activate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<Ability> iterator() {
        return abils.iterator();
    }

    @Override
    public void forEach(Consumer<? super Ability> action) {

    }

    @Override
    public Spliterator<Ability> spliterator() {
        return null;
    }

    @Override
    public boolean isForcePresetTargeting() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setForcePresetTargeting(boolean forcePresetTargeting) {
        for (Ability abil : abils) {
            abil.setForcePresetTargeting(forcePresetTargeting);
        }
    }

}
