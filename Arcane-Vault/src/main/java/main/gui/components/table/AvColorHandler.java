package main.gui.components.table;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.values.A_ValuePages;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.v2_0.AV2;
import main.launch.ArcaneVault;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.ArrayMaster;
import main.system.graphics.ColorManager;

import java.awt.*;

/*
    how to derive colors?
    compare with previous version and last in cache
    compare with non-simulated version (base)
    foreground - versioning
    background - other effects
     */
public class AvColorHandler extends AvHandler {
    public AvColorHandler(AvManager manager) {
        super(manager);
    }

    public enum HIGHLIGHT_SCHEME {
        vcs,
        balance, // relative to avrg for this level
        current,
        compare,

    }

    public static final Color defaultFore = ColorManager.GOLDEN_WHITE;
    public static final Color defaultBack = ColorManager.OBSIDIAN;

    public static final Color incremented = ColorManager.getDarkerColor(ColorManager.GREEN, 10);
    public static final Color decremented = ColorManager.getDarkerColor(ColorManager.RED, 10);
    public static final Color derived = ColorManager.getDarkerColor(ColorManager.PURPLE, 10);

    HIGHLIGHT_SCHEME scheme = HIGHLIGHT_SCHEME.current;

    public Color getBackgroundColor(boolean colorsInverted, ObjType selectedType, String s, String name, boolean isSelected) {
        return defaultBack;
    }

    public Color getForegroundColor(boolean colorsInverted, ObjType selectedType, String s, String name, boolean isSelected) {
        Integer val = NumberUtils.getInt(s);
        VALUE value = DC_ContentValsManager.getValue(name);
        if (value != null) {
            if (value instanceof PARAMETER) {
                switch (scheme) {
                    case vcs -> {
                        ObjType previousVersion = AV2.getVersionHandler().getPreviousVersion(selectedType);
                        if (previousVersion == null) {
                            //TODO
                        }
                        return getComparedColor(((PARAMETER) value), val, previousVersion);
                    }
                    case balance -> {
                    }
                    case current -> {
                        if (ArcaneVault.isSimulationOn()) {
                            return getComparedColor(((PARAMETER) value), val, selectedType);
                        }
                    }
                    case compare -> {
                        return getComparedColor(((PARAMETER) value), val, ArcaneVault.getPreviousSelectedType());
                    }
                }
            }
        }
        return defaultFore;
    }

    private Color getComparedColor(PARAMETER param, Integer val, ObjType compareTo) {
        if (ArrayMaster.contains_(A_ValuePages.A_DERIVED_UNIT_PARAMETERS, param)) {
            return decremented;
        }
        // SimulationHandler.getUnit()
        Integer base = compareTo.getIntParam(param);
        if (val > base) {
            return incremented;
        }
        if (val < base) {
            return decremented;
        }
        return defaultFore;
    }

    public Color getDefaultForegroundColor(boolean colorsInverted) {
        return defaultFore;
    }

    public Color getDefaultBackgroundColor(boolean colorsInverted) {
        return defaultBack;
    }

    public Color getBorderColor(boolean isSelected) {
        if (isSelected)
            return ColorManager.PURPLE;
        return null;
    }

    public void setScheme(HIGHLIGHT_SCHEME scheme) {
        this.scheme = scheme;
    }
}
