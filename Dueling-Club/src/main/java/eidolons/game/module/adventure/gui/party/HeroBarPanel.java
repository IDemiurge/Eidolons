package eidolons.game.module.adventure.gui.party;

import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.BarPanel;
import main.system.graphics.ColorManager;

import java.awt.*;
import java.util.List;

public class HeroBarPanel extends BarPanel {
    public static final PARAMETER[] params = {MACRO_PARAMS.HEALTH,
     MACRO_PARAMS.VIGOR, MACRO_PARAMS.COMBAT_READINESS,
     MACRO_PARAMS.ONENESS, MACRO_PARAMS.MOTIVATION,};
    public static final Color[] colors = {ColorManager.ORANGE,
     ColorManager.YELLOW, ColorManager.CYAN, ColorManager.BLUE,
     ColorManager.PURPLE

    };
    G_Panel panel;
    List<PARAMETER> vals;

    public HeroBarPanel() {
        // GradientPaint
        super(params, colors);
    }

    @Override
    protected String getWidth() {
        return HeroSlidePanel.width() + "";
    }
}
