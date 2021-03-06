package eidolons.ability.effects.containers.customtarget;

import main.ability.effects.Effect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.elements.conditions.Conditions;
import main.system.math.Formula;

public class ChainEffect extends SpecialTargetingEffect {

    private boolean override = false;
    private int nOfJumps = 0;

    public ChainEffect(Formula nOfJumps, Effect effects,
                       Conditions targetFilter, Boolean closest, Boolean friendlyFire) {
        super(effects, friendlyFire, targetFilter);
        this.formula = nOfJumps;
        initTargeting();
    }

    public ChainEffect(Formula nOfJumps, Effect effects,
                       CHAIN_TEMPLATES template) {

        this.effects = effects;
        this.formula = nOfJumps;
        initTargeting();
    }

    //Unimplemented
    @Override
    public void initTargeting() {
        // TODO Templates?
        //targeting = new AutoTargeting(c, new Formula("1"), closest); // if c ==
        // adjacent,
    }

    @Override
    public boolean applyThis() {

        if (override) {

            nOfJumps--;
            return effects.apply(ref);
        }
        targeting.setLastTarget(ref.getTargetObj());
        //initial impact
        setOverride(true);
        applyThis();
        nOfJumps = formula.getInt(ref);
        //while has adjacent?
        while (nOfJumps > 0) {
            if (!targeting.select(ref)) {
                break;
            }
            //targeting.setLastTarget(lastTarget);

//			ref.setTarget(target);
            setOverride(true);
            applyThis();
        }
        setOverride(false);
        return true;
    }

    private void setOverride(boolean b) {
        this.override = b;

    }
}
