package main.ability.effects.container;

import main.ability.effects.ContainerEffect;
import main.ability.effects.Effect;
import main.ability.effects.MicroEffect;
import main.data.ability.OmittedConstructor;
import main.elements.conditions.Conditions;
import main.elements.conditions.standard.ZLevelCondition;
import main.elements.targeting.AutoTargeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.bf.Coordinates;
import main.system.math.Formula;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SpecialTargetingEffect extends MicroEffect implements ContainerEffect {
    protected Effect effects;
    protected Boolean allyOrEnemyOnly;
    protected Conditions filteringConditions;
    protected AutoTargeting targeting;
    protected Boolean notSelf;
    protected Formula reductionFormula;
    protected Set<Coordinates> coordinates;

    @OmittedConstructor
    public SpecialTargetingEffect() {

    }

    @OmittedConstructor
    public SpecialTargetingEffect(Effect effect, boolean friendlyFire, Conditions conditions,
                                  Formula reductionFormula) {
        this.filteringConditions = conditions;
        if (!friendlyFire) {
            this.allyOrEnemyOnly = false;
        }

        this.effects = effect;
        this.reductionFormula = reductionFormula;
        initConstruct();
        effect.setReconstruct(true);
    }

    @OmittedConstructor
    public SpecialTargetingEffect(Effect effect, boolean friendlyFire, Conditions conditions) {
        this(effect, friendlyFire, conditions, null);
    }

    public abstract void initTargeting();

    @Override
    public Effect getEffect() {
        return effects;
    }

    private void initConstruct() {
        // this.construct = ConstructionManager.getConstruct(effects
        // .getConstruct(), this);

    }

    @Override
    public boolean applyThis() {
        if (reductionFormula != null) {
            ref.setValue(KEYS.FORMULA, reductionFormula.toString());
        }
        if (coordinates == null)
            initAnimRef(ref);
        boolean result = effects.apply(ref);
        if (isLoggingWrapped()) {
            getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.ZONE_EFFECT, ref.getActive());
        }
        return result;
    }

    public Set<Coordinates> getAndNullCoordinates() {
        Set<Coordinates> c = coordinates;
        coordinates = null;
        return c;
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    @Override
    public void initAnimRef(Ref ref) {
        setRef(ref);
        initTargeting();
        if (isZLevelDependent()) {
            getFilteringConditions().add(new ZLevelCondition(true));
        }
        this.targeting.select(ref);
        getActiveObj().getRef().setGroup(ref.getGroup());
        //        if (         (coordinates == null || getActiveObj().checkBool(STD_BOOLS.APPLY_THRU))) {
        if (ref.getGroup() != null) {
            if (!ref.getGroup().getObjects().isEmpty()) {
                if (coordinates == null)
                    coordinates = new LinkedHashSet();
                ref.getGroup().getObjects().forEach(o -> coordinates.add(o.getCoordinates()));
            }
        }
    }

    protected boolean isLoggingWrapped() {
        return true;
    }

    protected boolean isZLevelDependent() {
        //        return getGame().isMultiLevel();
        return false;
    }

    public Effect getEffects() {
        return effects;
    }

    public void setEffects(Effect effects) {
        this.effects = effects;
    }

    public Boolean getAllyOrEnemyOnly() {
        return allyOrEnemyOnly;
    }

    public void setAllyOrEnemyOnly(Boolean allyOrEnemyOnly) {
        this.allyOrEnemyOnly = allyOrEnemyOnly;
    }

    public Conditions getFilteringConditions() {
        return filteringConditions;
    }

    public void setFilteringConditions(Conditions filteringConditions) {
        this.filteringConditions = filteringConditions;
    }

    public AutoTargeting getTargeting() {
        if (targeting == null) {
            initTargeting();
        }
        return targeting;
    }

    public void setTargeting(AutoTargeting targeting) {
        this.targeting = targeting;
    }

    public Boolean getNotSelf() {
        return notSelf;
    }

    public void setNotSelf(Boolean notSelf) {
        this.notSelf = notSelf;
    }

    public Formula getReductionFormula() {
        return reductionFormula;
    }

    public void setReductionFormula(Formula reductionFormula) {
        this.reductionFormula = reductionFormula;
    }

    public enum CHAIN_TEMPLATES {
        CLOSEST_ANY, CLOSEST_CONDITIONAL, CLOSEST_ALLY, CLOSEST_ENEMY,

        RANDOM_ANY, RANDOM_CONDITIONAL, RANDOM_ALLY, RANDOM_ENEMY,

    }

}
