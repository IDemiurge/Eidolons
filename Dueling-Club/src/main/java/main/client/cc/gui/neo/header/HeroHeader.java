package main.client.cc.gui.neo.header;

import main.client.cc.gui.neo.HeroPanel;
import main.entity.obj.unit.Unit;
import main.swing.generic.components.G_Panel;
import main.system.graphics.GuiManager;

import java.awt.*;
import java.beans.Transient;

public class HeroHeader extends G_Panel {

    private static final int Y_OFFSET = 0;
    private static final int X_OFFSET = 8;
    ClassLine primeClass;
    ClassLine secondClass;

    DeityComp deityComp;
    HeroTextComp textComp;
    PortraitComp portrait;
    private HeroPanel heroPanel;

    // TextComp integrityComp;
    // fix h/v bounds for text comps on name, bg and level!!!

    public HeroHeader(Unit hero, HeroPanel heroPanel) {
        primeClass = new ClassLine(true, true, hero);
        secondClass = new ClassLine(true, false, hero);
        textComp = new HeroTextComp(hero);
        portrait = new PortraitComp(this, hero);
        deityComp = new DeityComp(this, hero);
        addComps();
        this.heroPanel = heroPanel;
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {
        return new Dimension(VISUALS.DEITY.getWidth() + 2 * GuiManager.getSmallObjSize(),
         ClassLine.MAX_CLASSES * GuiManager.getSmallObjSize() + Y_OFFSET);
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    public void refresh() {
        primeClass.refresh();
        secondClass.refresh();
        textComp.refresh();
        portrait.refresh();
        deityComp.refresh();
    }

    private void addComps() {

        //
        String pos = "id pc, pos " + X_OFFSET + " 6+" + Y_OFFSET;
        add(primeClass, pos);

        pos = "id portrait, pos pc.x2 0 ";
        add(portrait.getComp(), pos);

        pos = "id sc, pos portrait.x2 pc.y";
        add(secondClass, pos);

        pos = "id tc, pos pc.x2 portrait.y2";
        add(textComp, pos);

        pos = "id dc, pos pc.x2 tc.y2+5";
        add(deityComp.getComp(), pos);

    }

    public void deityClicked() {
        heroPanel.deityClicked();
    }

}
