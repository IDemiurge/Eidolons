package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.system.text.HelpMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 6/30/2018.
 */
public class UnitDescriptionPanel extends DescriptionPanel{

    @Override
    protected int getFontSize() {
        return 18;
    }

    @Override
    protected FONT getFontStyle() {
        return FONT.MAIN;
    }

    @Override
    protected float getDefaultHeight() {
        return 500;
    }

    @Override
    protected float getDefaultWidth() {
        return GdxMaster.adjustSize(430);
    }
    @Override
    protected void updateAct() {
        HqHeroDataSource dataSource = (HqHeroDataSource) getUserObject();
        String text = dataSource.getDescription();
        if (text.isEmpty()) {
            text = HelpMaster.getHelpText();
        }
        setText(text);
        super.updateAct();
    }
}
