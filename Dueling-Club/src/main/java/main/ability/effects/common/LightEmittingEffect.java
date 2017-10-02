package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.ability.effects.EffectImpl;
import main.content.PARAMS;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.math.Formula;
import main.system.math.PositionMaster;

public class LightEmittingEffect extends DC_Effect {
    boolean debug;
    private static final int REFLECTION_BONUS_PER_ADJACENT_WALL = 15;
    private static final String REDUCTION_FOR_DISTANCE_MODIFIER = "/3";//"*2";
    String reduction_for_distance_modifier = REDUCTION_FOR_DISTANCE_MODIFIER;
    boolean circular;
    private String rangeFormula = "3";
    private SpectrumEffect effect;
    private Obj lastTarget;

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
        for (Coordinates c : ref.getSourceObj().getCoordinates().getAdjacentCoordinates()) {
            for (BattleFieldObject obj : getGame().getMaster().getObjectsOnCoordinate(c,
             false)) {
                if (obj.isWall()) {
                    reflectionMod += REFLECTION_BONUS_PER_ADJACENT_WALL;
                    break;
                }
            }
        }
        if (reflectionMod > 0) {
//            reduction_for_distance_modifier  += ("+" + reflectionMod);
        }
        return getEffect().apply(ref);
    }

    private boolean addLight(Ref ref) {
        if (
         getAmount()==0 ||
         PositionMaster.getDistance(lastTarget.getCoordinates(), ref.getSourceObj().getCoordinates()
        )!=(PositionMaster.getDistance(ref.getTargetObj().getCoordinates(),
         ref.getSourceObj().getCoordinates()))) {
            setAmount(getFormula().getInt(ref));
        }

        ref.getTargetObj().modifyParameter(PARAMS.ILLUMINATION, getAmount(), true);
        lastTarget=ref.getTargetObj();
//       if (debug)
//           main.system.auxiliary.log.LogMaster.log(1,ref.getTargetObj()+
//         " gets illumination: " +getAmount()+
//         " ; total illumination: " +lastTarget.getIntParam(PARAMS.ILLUMINATION)+
//         " ; Source: " +ref.getSourceObj());

        return true;
    }

    public SpectrumEffect getEffect() {
        if (effect == null) {
            effect =
             // SpectrumEffect
             new SpectrumEffect(
              new EffectImpl() {
                  @Override
                  public boolean applyThis() {
                      return addLight(ref);
                  }

                  @Override
                  public boolean isQuietMode() {
                      return true;
                  }
              }
//            new Effects(
//               new ModifyValueEffect(PARAMS.ILLUMINATION,
//                MOD.MODIFY_BY_PERCENT, "-66"),
//               new ModifyValueEffect(PARAMS.ILLUMINATION,
//                MOD.MODIFY_BY_CONST, formula.toString()))
            , rangeFormula, circular);

            if (reduction_for_distance_modifier != null)
                effect.setReductionForDistanceModifier(reduction_for_distance_modifier);
            effect.setApplyThrough(true);

            effect.setQuietMode(true);
        }

        return effect;
    }

    public void setEffect(SpectrumEffect effect) {
        this.effect = effect;
    }
}
