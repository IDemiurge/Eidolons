package eidolons.ability.effects.common;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.battlefield.vision.Illumination;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.ability.effects.Effects;
import main.content.enums.GenericEnums;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.math.Formula;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.Map;

public class LightEmittingEffect extends SpectrumEffect {
    private static final int REFLECTION_BONUS_PER_ADJACENT_WALL = 15;
    private static final String REDUCTION_FOR_DISTANCE_MODIFIER = "/2.5";//"*2";
    boolean debug;
    private Coordinates lastCoordinates;
    private Map<Coordinates, Float> map;
    private final GenericEnums.ALPHA_TEMPLATE flicker;
    private final Color color;
    private final Illumination illumination;
    private ColorMapDataSource.LightDS light;

    public LightEmittingEffect(String formula, Boolean circular, Color color,
                               GenericEnums.ALPHA_TEMPLATE flicker,
                               Illumination illumination) {
        super(null);
        this.flicker = flicker;
        this.color = color;
        this.illumination = illumination;
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
        lastCoordinates = Coordinates.get(0, 0);
    }


    @Override
    protected boolean isCoordinatesCached() {
        return true;
    }

    public ColorMapDataSource.LightDS createAndApplyLight() {
        if (light == null) {
            light = new ColorMapDataSource.LightDS(color, map = new HashMap<>(), flicker);
        }
        apply();
        return light;
    }
    //Light revamp
    private Effect createEffect() {
        return new EffectImpl() {
            @Override
            public boolean applyThis() {
                Obj targetObj = ref.getTargetObj();
                if (getAmount() == 0 ||
                        PositionMaster.getDistance(lastCoordinates, ref.getSourceObj().getCoordinates()
                        ) != (PositionMaster.getDistance(ref.getTargetObj().getCoordinates(),
                                ref.getSourceObj().getCoordinates()))) {
                    setAmount(getFormula().getInt(ref)); //recalc
                }

                int plus = getAmount();
                Integer light = ref.getTargetObj().getIntParam(PARAMS.ILLUMINATION);
                if (light > 0) {
                    plus = plus / 3 * 2; //if there is already illumination...
                }
                targetObj.modifyParameter(PARAMS.ILLUMINATION, plus, true);
                lastCoordinates = ref.getTargetObj().getCoordinates();

                float lerp=plus*plus / 2500f;
                map.put(ref.getTargetObj().getCoordinates(), lerp);

                illumination.lightAdded(targetObj);
                return true;
            }

            @Override
            public boolean isQuietMode() {
                return true;
            }
        };
    }

    @Override
    public boolean applyThis() {
        return super.applyThis();
    }


}
