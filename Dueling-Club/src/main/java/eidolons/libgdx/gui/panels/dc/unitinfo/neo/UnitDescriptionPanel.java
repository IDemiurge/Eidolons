package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
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
        String text = null ;
        if (getUserObject() instanceof String){
            text = (String) getUserObject();
        } else {
            HqHeroDataSource dataSource = (HqHeroDataSource) getUserObject();
            text =dataSource==null  ?"" : dataSource.getDescription();
        }

        if (text.isEmpty()) {
            text = "There isn't anything else to be said here.";
            text = "I am too deeply depressed by the state of this panel.\n"+text;
        }
        setText(text);
        super.updateAct();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }
}
