package main.swing.renderers;

import main.content.ContentManager;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.Game;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.StringMaster;

import java.awt.*;

/**
 * color tags icons x out of y = x/y
 *
 * @author JustMe
 */
public class SmartTextManager {

    public static final Integer VALUE_CRITICAL = 20;
    public static final Integer VALUE_LOW = 40;
    public static final Integer VALUE_AVERAGE = 70;
    public static final Integer VALUE_FULL = 100;
    public static final Integer VALUE_HIGH = 125;

    public static VALUE_CASES getPropCase(PROPERTY prop, Entity obj) {
        String value = obj.getProperty(prop, true);
        String c_value = obj.getProperty(prop, false);
        if (c_value.equals(value)) {
            return VALUE_CASES.NULL;
        } else {
            return VALUE_CASES.MODIFIED;
        }

    }

    public static VALUE_CASES getParamCase(PARAMETER param, Entity obj) {
        // if (checkVitalValue()) {
        // }
        // if (checkNegativeValue()) {
        //
        // }

        String value = obj.getParamRounded(param, false);
        if (StringMaster.isEmpty(value)) {
            return VALUE_CASES.DEFAULT;
        }

        int amount;
        // dynamic - percentage! x/y !
        try {
            amount = Integer.valueOf(value);
        } catch (Exception e) {
            return VALUE_CASES.DEFAULT;
        }
        if (param.isDynamic()) {
            return getDynamicValueCase(param, amount, obj);
        }
        if (obj.getParamRounded(param, true).isEmpty()) {
            return VALUE_CASES.DEFAULT;
        }
        int base_amount;
        try {
            base_amount = Integer.valueOf(obj.getRawValue(param));
        } catch (Exception e) {
            return VALUE_CASES.DEFAULT;
        }
        if (base_amount == amount) {
            return VALUE_CASES.DEFAULT;
        }
        if (base_amount < amount) {
            return VALUE_CASES.MODIFIED_POSITIVELY;
        } else {
            return VALUE_CASES.MODIFIED_NEGATIVELY;
        }
    }

    private static VALUE_CASES getDynamicValueCase(PARAMETER param, int amount, Entity obj) {
        // check special cases - morale, etc
        PARAMETER base_value = ContentManager.getBaseParameterFromCurrent(param);
        if (base_value == null) {
            return VALUE_CASES.VALUE_NORMAL;
        }
        //
        Integer percentage;
        int base_amount =

                obj.getIntParam(base_value);
        if (base_amount == 0) {
            percentage = 100;
        } else {
            percentage = amount * 100 / base_amount;
        }
        main.system.auxiliary.LogMaster.log(0, param + ": " + amount + " out of " + base_amount
                + " = " + percentage + "%");
        // try {
        //
        // } catch (Exception e) {
        //
        // percentage = Integer.valueOf(obj.getParam(ContentManager
        // .getPercentageParam(base_value)));
        // }

        // TODO get default value color => darken by % that's missing
        return getValueCase(percentage);

    }

    public static VALUE_CASES getValueCase(Integer percentage) {
        if (percentage < VALUE_CRITICAL) {
            return VALUE_CASES.VALUE_CRITICAL;
        }
        if (percentage < VALUE_LOW) {
            return VALUE_CASES.VALUE_LOW;
        }
        if (percentage < VALUE_AVERAGE) {
            return VALUE_CASES.VALUE_AVERAGE;
        }
        if (percentage < VALUE_FULL) {
            return VALUE_CASES.VALUE_BELOW;
        }
        if (percentage > VALUE_HIGH) {
            return VALUE_CASES.VALUE_HIGH;
        }
        return VALUE_CASES.VALUE_NORMAL;
    }

    public static VALUE_CASES getValueCase(VALUE value, Obj obj) {
        if (value instanceof PROPERTY) {
            return getValueCase(value, obj);
        }
        return getParamCase((PARAMETER) value, obj);
    }

    public enum VALUE_CASES {
        NULL(ColorManager.OBSIDIAN), DEFAULT(ColorManager.BLUE), MODIFIED(ColorManager.PURPLE
                // , ColorManager.LIGHT_PURPLE
        ), // STATUS,
        MODIFIED_POSITIVELY(ColorManager.GREEN),
        MODIFIED_NEGATIVELY(ColorManager.RED),
        VALUE_HIGH(ColorManager.BLUE),
        VALUE_LOW(ColorManager.DARK_ORANGE),
        VALUE_AVERAGE(ColorManager.ORANGE), // MORALE, TGH/END, ...
        VALUE_CRITICAL(ColorManager.RED),
        VALUE_NORMAL(ColorManager.GREEN),
        VALUE_BELOW(ColorManager.YELLOW.brighter()),;
        private Color color;
        private Color altColor;

        VALUE_CASES(Color color) {
            this.setColor(color);
        }

        VALUE_CASES(Color color, Color altColor) {
            this.setColor(color);
            this.altColor = altColor;
        }

        public Color getColor() {
            if (Game.game.isSimulation()) {
                return ColorManager.GOLDEN_WHITE;
            }
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public Color getAltColor() {
            if (Game.game.isSimulation()) {
                return ColorManager.GOLDEN_WHITE;
            }
            if (altColor == null) {
                altColor = color.brighter().brighter();
            }
            return altColor;
        }
    }

}
