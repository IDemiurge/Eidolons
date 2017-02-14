package main.system.graphics;

import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.attach.DC_BuffObj;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.StringMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.images.ImageManager;
import main.system.math.roll.Roll;
import main.system.util.CounterMaster;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class EffectAnimation extends ActionAnimation {

    public EffectAnimation(DC_ActiveObj action) {
        super(action);
    }

    @Override
    protected boolean drawPhase(AnimPhase phase) {
        if (super.drawPhase(phase)) {
            return true;
        }
        this.phase = phase; // TODO mass-add as argument instead!
        switch (phase.getType()) {
            case ROLL:
                return drawRoll(phase);
            case BUFF:
                return drawBuff(phase);
            case PARAM_MODS:
                return drawParamMods(phase);
            case PROP_MODS:
                return drawPropMods(phase);
            case COUNTER:
                return drawCounterMod(phase);

        }
        return false;
    }

    private boolean drawCounterMod(AnimPhase phase) {
        String counterName = (String) phase.getArgs()[0];
        STD_COUNTERS counter = CounterMaster.findCounterConst(counterName);
        MOD modtype = (MOD) phase.getArgs()[1];
        Integer modValue = (Integer) phase.getArgs()[2];

        Boolean negative = false;
        if (counter != null)
            // counter.isNegative(); // img
        {
            drawTextOnTarget(StringMaster.getBonusString(modValue), font, CENTERED_X, CENTERED_Y,
                    ColorManager.getStandardColor(negative));
        }

        StringMaster.getBonusString(modValue);

        return true;
    }

    private boolean drawRoll(AnimPhase phase) {
        Roll roll = (Roll) phase.getArgs()[0];
        if (roll == null) {
            return false;
        }

        Boolean result = roll.getResult();
        Boolean negative = source.getOwner().isEnemy() && getTarget().getOwner().isMe();
        Image image = roll.getType().getImage();
        int x;
        int y = 0;
        if (image == null) {
            String name = roll.getType().getName() + " Roll ";
            name += result ? "Failed:" : "Won:";
            x = MigMaster.getCenteredTextPosition(name, font, w);
            y += drawTextColored(false, negative, name, font, x, y);
        } else {
            x = MigMaster.getCenteredPosition(w, image.getWidth(null));
            drawOnTarget(image, x, y);
            y += image.getHeight(null);
        }
        // TODO tooltips? or subphases? update final numbers only?
        String str = roll.getRollTarget();
        x = MigMaster.getCenteredTextPosition(str, font, w);
        drawOnTarget(ImageManager.getDiceIcon(negative, true), x - 45, y);
        y += drawTextColored(false, negative, str, font, x, y);

        font = font.deriveFont(font.getSize() * 4 / 3);
        str = " Versus ";
        x = MigMaster.getCenteredTextPosition(str, font, w);
        // Boolean luck =RollMaster.getLuck(value, n);

        y += drawTextColored(false, negative, str, font, x, y);
        // TODO for dice?
        // getTooltipMap().put(new Rectangle(x, y, width, height), textItem);
        str = roll.getRollSource();
        x = MigMaster.getCenteredTextPosition(str, font, w);
        drawOnTarget(ImageManager.getDiceIcon(!negative, true), w - x, y);
        y += drawTextColored(false, negative, str, font, x, y);

        // roll.getString(result);

        return true;
    }

    protected boolean drawParamMap(Map<PARAMETER, String> map) {
        joinsMaps(phase, map);

        Map<Image, String> valMap = getImageMapFromParams(map);

        drawIconMap(valMap, false);
        return true;
    }

    private void joinsMaps(AnimPhase phase, Map map) {
        int i = 1;
        while (true) {
            if (phase.getArgs().length <= i) {
                break;
            }
            if (phase.getArgs()[i] instanceof Map) {
                Map map1 = (Map) phase.getArgs()[i];
                map.putAll(map1);
                // MapMaster.addAllToListMap(map, map1); TODO numbers aren't
                // lists...
            }
            i++;
        }
    }

    private boolean drawBuff(AnimPhase phase) {
        // TODO boolean source,
        for (Object arg : phase.getArgs()) {
            if ((arg instanceof DC_BuffObj)) {
                DC_BuffObj buff = (DC_BuffObj) arg;
                Image image = buff.getIcon().getImage();
                int y = 0;
                drawOnTargetCenterX(image, y);
                y += image.getHeight(null);
                Color c = getDefaultTextColor();
                if (buff.isNegative() != null) {
                    c = getColorForModifier(buff.isNegative() ? -1 : 1);
                }
                y += drawTextOnTarget(buff.getName(), MigMaster.getCenteredTextPosition(buff
                        .getName(), font, w), y, c);
            }
            if (arg instanceof Map) {
                // drawParamMap(phase);
                // drawTextColumn(map, onSource);
            }

        }

        for (Object arg : phase.getArgs()) {

        }
        return true;
    }

    private boolean drawPropMods(AnimPhase phase) {
        // single column?
        Map<PROPERTY, List<Object>> map = (Map<PROPERTY, List<Object>>) phase.getArgs()[0];
        joinsMaps(phase, map);
        drawTextColumn(map, false);
        // String prefix = () ? "+":"=";

        return true;
    }

    private void drawTextColumn(Map<PROPERTY, List<Object>> map, boolean onSource) {
        int y = 4;

        for (PROPERTY p : map.keySet()) {
            MOD_PROP_TYPE type = (MOD_PROP_TYPE) map.get(p).get(0);
            String prefix = (type == MOD_PROP_TYPE.ADD) ? "+" : "=";
            Boolean negative = (type == MOD_PROP_TYPE.ADD) ? false : null;
            if (type == MOD_PROP_TYPE.REMOVE) {
                prefix = "-";
                negative = true;
            }
            String string = p.getName() + prefix
                    + StringMaster.getWellFormattedString(map.get(p).get(1).toString());
            int x = 5;// MigMaster.getCenteredTextPosition(string, font, w);
            y += drawTextColored(onSource, negative, string, font, x, y);
        }

    }

    @Override
    protected PHASE_TYPE getSubPhaseTypeForKey(String string) {
        // params?
        return super.getSubPhaseTypeForKey(string);
    }

    protected boolean drawParamMods(AnimPhase phase) {
        Map<PARAMETER, String> map = (Map<PARAMETER, String>) phase.getArgs()[0];
        return drawParamMap(map);
    }

    protected boolean drawParamBonuses(AnimPhase phase) {
        Map<PARAMETER, String> map = (Map<PARAMETER, String>) phase.getArgs()[0];
        return drawParamMap(map);
    }

}
