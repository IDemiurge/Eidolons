package eidolons.ability.effects.common;

import eidolons.ability.effects.DC_Effect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.GridCell;
import eidolons.game.core.master.EffectMaster;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.Formula;
import main.system.math.PositionMaster;

import java.util.Collection;

public class SpectrumEffect extends DC_Effect {
    protected static final String X = "x";
    protected String paramString;
    protected Effects effects;
    protected boolean applyThrough = true;
    protected boolean circular;
    protected boolean vision;
    protected Condition filterConditions;
    protected String reductionForDistanceModifier;
    protected BattleFieldObject bfObj;
    protected Integer range;
    String rangeFormula;
    KEYS source = KEYS.SOURCE;
    int defaultSidePenalty = 1;
    // String reductionForDistance = "(x)/distance+sqrt(x)";
    String reductionForDistance = "-(x)/10*(2+distance*1.5)"; // *sqrt(distance)
    private Collection<Coordinates> cached;

    public SpectrumEffect(Effect effects, String rangeFormula, Boolean circular) {
        if (effects != null)
            this.effects = new Effects(effects);
        this.rangeFormula = rangeFormula;
        this.circular = circular;
    }

    public SpectrumEffect(Effect effects) {
        this(effects, "", false);
        this.vision = true;
    }

    public SpectrumEffect(Effect effects, String rangeFormula) {
        this(effects, rangeFormula, false);
    }

    public SpectrumEffect(String paramString, String rangeFormula) {
        this.paramString = paramString;
        this.rangeFormula = rangeFormula;
    }

    public SpectrumEffect(Condition filterConditions, Effect effect) {
        this(effect);
        this.filterConditions = filterConditions;

    }

    @Override
    public String toString() {
        return ref.getSourceObj() + "'s Spectrum effect with " + effects;
    }

    public void resetCache() {
        cached = null;
    }

    public boolean applyThis() {
        if (range == null)
            range = new Formula(rangeFormula).getInt(ref);
        Integer backwardRange = 0;
        Integer sidePenalty = 0;
        if (vision) {
            range = new Formula(StringMaster.getValueRef(KEYS.SOURCE, PARAMS.SIGHT_RANGE))
             .getInt(ref);
            backwardRange = null; // TODO
            sidePenalty = null; //will be taken from unit
        }

        if (ref.getObj(source) instanceof BattleFieldObject)
            bfObj = ((BattleFieldObject) ref.getObj(source));
        else {
            //TODO
        }

        if (circular) {
            backwardRange = range;
        } else {
            sidePenalty = 1;
        }
        Collection<Coordinates> coordinates = null;
        if (isCoordinatesCached()) {
            coordinates = cached;
        }
        // if (coordinates == null) //TODO LC 2.0
        //     coordinates =getCoordinates(sidePenalty, backwardRange, facing);
        cached = coordinates;


        //        main.system.auxiliary.log.LogMaster.log(1, this + " applied on " + coordinates);
        // boolean x-ray ++ tall/short/etc
        if (effects == null) {
            initEffects();
        }

        for (Coordinates c : coordinates) {
            // TODO WHAT IF IT'S ON A DIFFERENT Z-LEVEL?
            // applyThrough = true; // ?
            // if (!applyThrough)
            // if (!(getGame().getObjectByCoordinate(c, true) instanceof
            // DC_Cell))
            // continue;
            DequeImpl<? extends Obj> objects = new DequeImpl<>(getGame().getObjectsOnCoordinate(
               c, false ));
            if (applyThrough) {
                GridCell cell = getGame().getCell(c);
                if (cell != null) {
                    objects.addCast(cell);
                }
            }

            for (Obj o : objects) {
                ref.setMatch(o.getId());
                if (filterConditions != null) {
                    if (!filterConditions.preCheck(ref)) {
                        continue;
                    }
                }
                Integer target = o.getId();
                // for (Effect effect : effects.getEffectsStage()) {
                // Ref REF = Ref.getCopy(ref);
                // REF.setTarget(target);
                // effect.apply(REF);
                // }
                // target = getGame().getCellByCoordinate(c).getId();
                if (getGame().getObjectById(target) == null) {
                    continue;
                }
                for (Effect effect : effects.getEffects()) {
                    Ref REF = Ref.getCopy(ref);
                    REF.setTarget(target);
                    if (reductionForDistance != null) {
                        effect.resetOriginalFormula(); // for the first time
                        // to set original
                        effect.resetOriginalFormula();
                        String reduction = reductionForDistance;
                        if (reductionForDistanceModifier != null)
                            reduction += (reductionForDistanceModifier);

                        Formula effectFormula = effect.getFormula();
                        reduction = reduction.replace(X, effectFormula.toString());
                        int distance = PositionMaster.getDistance(REF.getSourceObj(), REF
                         .getTargetObj());
                        reduction = reduction.replace("distance", distance + "");
                        effectFormula.append(reduction);
                        //TODO
                        Integer amount = effectFormula.getInt(ref);
                        if (amount < 0) {
                            effect.setAmount(amount);
                        }
                        effect.setAmount(amount);
                    }
                    effect.apply(REF);
                }
            }
        }
        return true;
    }

    protected boolean isCoordinatesCached() {
        return false;
    }

    protected void initEffects() {
        ref.setID(KEYS.INFO, ref.getId(KEYS.ACTIVE));
        effects = EffectMaster.initParamModEffects(paramString, ref);
    }

    public String getRangeFormula() {
        return rangeFormula;
    }

    public void setRangeFormula(String rangeFormula) {
        this.rangeFormula = rangeFormula;
    }

    public Effects getEffects() {
        return effects;
    }

    public void setEffects(Effects effects) {
        this.effects = effects;
    }

    public boolean isApplyThrough() {
        return applyThrough;
    }

    public void setApplyThrough(boolean applyThrough) {
        this.applyThrough = applyThrough;
    }

    public boolean isCircular() {
        return circular;
    }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public String getReductionForDistanceModifier() {
        return reductionForDistance;
    }

    public void setReductionForDistanceModifier(String reductionForDistance) {
        this.reductionForDistanceModifier = reductionForDistance;
    }

    public Collection<Coordinates> getCache() {
        return cached;
    }
}
