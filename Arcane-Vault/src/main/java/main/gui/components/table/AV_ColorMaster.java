package main.gui.components.table;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.values.A_ValuePages;
import eidolons.content.values.ValuePageManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.entity.type.ObjType;
import main.handlers.types.SimulationHandler;
import main.launch.ArcaneVault;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.ArrayMaster;
import main.system.graphics.ColorManager;

import java.awt.*;

public class AV_ColorMaster {
public enum highlight_scheme{
    vcs,
    balance, // relative to avrg for this level
    current,


}
    public static final Color defaultFore = ColorManager.GOLDEN_WHITE;
    public static final Color defaultBack = ColorManager.OBSIDIAN;

    public static final Color incremented = ColorManager.getDarkerColor(ColorManager.GREEN, 10);
    public static final Color decremented = ColorManager.getDarkerColor(ColorManager.RED, 10);
    public static final Color derived = ColorManager.getDarkerColor(ColorManager.PURPLE, 10);

    /*
    how to derive colors?
    compare with previous version and last in cache
    compare with non-simulated version (base)

    foreground - versioning
    background - other effects

     */

    // public static Color getBackgroundColor(boolean colorsInverted, ObjType selectedType, String name) {
    //     return getBackgroundColor(colorsInverted, selectedType, name, false);
    // }
    public static Color getBackgroundColor(boolean colorsInverted, ObjType selectedType, String s, String name, boolean isSelected) {


        return defaultBack;
    }

    public static Color getForegroundColor(boolean colorsInverted, ObjType selectedType, String s, String name, boolean isSelected) {
        VALUE value = DC_ContentValsManager.getValue(name);
        if (value != null) {
            if (ArcaneVault.isSimulationOn())
                if (value instanceof PARAMETER) {
                    if (ArrayMaster.contains_(A_ValuePages.A_DERIVED_UNIT_PARAMETERS, value)) {
                        return decremented;
                    }
                    // SimulationHandler.getUnit()
                    Integer val = NumberUtils.getInt(s);
                    Integer base = selectedType.getIntParam(name);
                    if (val > base) {
                        return incremented;
                    }
                    if (val < base) {
                        return decremented;
                    }
                    // ((PARAMETER) value).is
                }
        }
        return defaultFore;
    }

    public static Color getDefaultForegroundColor(boolean colorsInverted) {
        return defaultFore;
    }

    public static Color getDefaultBackgroundColor(boolean colorsInverted) {
        return defaultBack;
    }

    public static Color getBorderColor(boolean isSelected) {
        if (isSelected)
            return ColorManager.PURPLE;
        return null;
    }
}
