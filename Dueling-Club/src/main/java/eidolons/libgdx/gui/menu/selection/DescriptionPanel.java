package eidolons.libgdx.gui.menu.selection;

import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextPanel;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/30/2017.
 */
public class DescriptionPanel extends ScrollTextPanel {
    public DescriptionPanel() {
    }

    @Override
    protected String getBgPath() {
        return StrPathBuilder.build(
         "UI",
         "components",
         "dc",
         "dialog",
         "log",
         "background.png");
    }


    @Override
    protected int getFontSize() {
        return 18;
    }

    @Override
    protected FONT getFontStyle() {
        return FONT.RU;
    }

    @Override
    protected float getDefaultHeight() {
        return 430;
    }

    @Override
    protected float getDefaultWidth() {
        return 450;
    }
}
