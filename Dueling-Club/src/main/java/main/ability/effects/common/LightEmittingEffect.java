package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effects;
import main.content.PARAMS;
import main.entity.obj.BattleFieldObject;
import main.game.bf.Coordinates;
import main.system.math.Formula;

public class LightEmittingEffect extends DC_Effect {
    private static final int REFLECTION_BONUS_PER_ADJACENT_WALL = 15 ;
    private static final String REDUCTION_FOR_DISTANCE_MODIFIER = "/3" ;//"*2";
    String reduction_for_distance_modifier= REDUCTION_FOR_DISTANCE_MODIFIER;
    boolean circular;
    private String rangeFormula = "3";
    private SpectrumEffect effect;

    public LightEmittingEffect(String formula, Boolean circular) {
        this.formula = new Formula(formula);
        this.circular = circular;
    }

    public LightEmittingEffect(String formula) {
        this(formula, true);
    }

    @Override
    public boolean applyThis() {
        // packaged into PassiveAbil
        // on self?
        int reflectionMod = 0;
        for (Coordinates c: ref.getSourceObj().getCoordinates().getAdjacentCoordinates()){
            for (BattleFieldObject obj : getGame().getMaster().getObjectsOnCoordinate(c,
             false)) {
                  if (obj.isWall())
            {
                reflectionMod += REFLECTION_BONUS_PER_ADJACENT_WALL;
                break;
            }
        }}
        if (reflectionMod>0){
            reduction_for_distance_modifier+=("+" + reflectionMod);
        }
        return getEffect().apply(ref);
    }

    public SpectrumEffect getEffect() {
        if (effect == null) {
            effect =
             // SpectrumEffect
             new SpectrumEffect(
              new Effects(
               new ModifyValueEffect(PARAMS.ILLUMINATION,
                MOD.MODIFY_BY_PERCENT, "-66"),
               new ModifyValueEffect(PARAMS.ILLUMINATION,
                MOD.MODIFY_BY_CONST, formula.toString())), rangeFormula, circular);
          if (reduction_for_distance_modifier!=null )
           effect.setReductionForDistanceModifier(reduction_for_distance_modifier);
            effect.setApplyThrough(true);

        }

        return effect;
    }

    public void setEffect(SpectrumEffect effect) {
        this.effect = effect;
    }
}
