package main.libgdx.gui.menu.selection;

import main.libgdx.gui.panels.dc.logpanel.text.ScrollTextPanel;
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
         "2017",
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
        return super.getFontStyle();
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
