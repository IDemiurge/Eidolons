package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
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
//        HqHeroDataSource dataSource = getUserObject();
        setText("Weave\n" +
         "X is not right \n" +
         "Examine the notion\n" +
         "Turn on debug\n" +
         "\n" +
         "Issues\n" +
         "Why clustering ? \n" +
         "And some skills are in hell \n" +
         "no link \n" +
         "why highlight failed\n" +
         "\n" +
         "Ideas \n" +
         "dummy icons for skills for now? \n" +
         "Display their numeric values (circle, n, …) \n" +
         "\n" +
         "Solutions \n" +
         "Build one weave at a time…\n" +
         "\n" +
         "Add some background to weave / tree \n" +
         "\n" +
         "Add some bigger recognizable symbol for each Group \n" +
         "\n" +
         "\n");
        super.updateAct();
    }
}
