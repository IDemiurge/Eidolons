package main.ability.effects.common;

import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.ability.effects.Effects;
import main.content.PARAMS;
import main.game.bf.Coordinates;
import main.system.math.Formula;
import main.system.math.PositionMaster;

public class LightEmittingEffect extends SpectrumEffect {
    private static final int REFLECTION_BONUS_PER_ADJACENT_WALL = 15;
    private static final String REDUCTION_FOR_DISTANCE_MODIFIER = "/2";//"*2";
    boolean debug;
    private Coordinates lastCoordinates;

    public LightEmittingEffect(String formula, Boolean circular) {
        super(null);
        this.formula = new Formula(formula);
        this.circular = circular;
        rangeFormula = "5";
        range = 5;
        vision = false;
        this.effects = new Effects(createEffect());
        reductionForDistance += REDUCTION_FOR_DISTANCE_MODIFIER;
        setApplyThrough(true);
        this.effects.setFormula(this.formula);
        this.effects.setOriginalFormula(this.formula);
        setQuietMode(true);
        lastCoordinates = new Coordinates(0, 0);
    }

    public LightEmittingEffect(String formula) {
        this(formula, true);
    }

    private Effect createEffect() {
        return new EffectImpl() {
            @Override
            public boolean applyThis() {
                if (getAmount() == 0 ||
                 PositionMaster.getDistance(lastCoordinates, ref.getSourceObj().getCoordinates()
                 ) != (PositionMaster.getDistance(ref.getTargetObj().getCoordinates(),
                  ref.getSourceObj().getCoordinates()))) {
                    setAmount(getFormula().getInt(ref));
                }
                ref.getTargetObj().modifyParameter(PARAMS.ILLUMINATION, getAmount(), true);
//             if (game.isDebugMode())
//                 main.system.auxiliary.log.LogMaster.log(1, ref.getTargetObj()+"'s ILLUMINATION: +" + getAmount()
//                  + "="+ ref.getTargetObj().getIntParam(PARAMS.ILLUMINATION));
                if (ref.getTargetObj() != null)
                    lastCoordinates = ref.getTargetObj().getCoordinates();
                return true;//addLight(ref);
            }

            @Override
            public boolean isQuietMode() {
                return true;
            }
        };
    }

    @Override
    public boolean applyThis() {
        // packaged into PassiveAbil
        // on self?
//        int reflectionMod = 0;
//        for (Coordinates c : ref.getSourceObj().getCoordinates().getAdjacentCoordinates()) {
//            for (BattleFieldObject obj : getGame().getMaster().getObjectsOnCoordinate(c,
//             false)) {
//                if (obj.isWall()) {
//                    reflectionMod += REFLECTION_BONUS_PER_ADJACENT_WALL;
//                    break;
//                }
//            }
//        }
//        if (reflectionMod > 0) {
//            reduction_for_distance_modifier  += ("+" + reflectionMod);
//        } 11-10, 11-9, 11-8, 11-11, 11-12, 10-10, 10-11, 9-10, 12-9, 12-8, 12-7, 12-11, 12-12, 12-13, 12-10]
        return super.applyThis();
    }


}
