package eidolons.client.cc.gui.neo.principles;

import eidolons.client.cc.gui.neo.bars.SpecialValueBar;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.rpg.IntegrityRule;
import eidolons.swing.components.panels.page.info.element.TextCompDC;
import eidolons.swing.components.panels.page.log.WrappedTextComp;
import eidolons.system.graphics.ImageTransformer;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;

public class PrincipleMiddlePanel extends G_Panel {

    private Unit hero;
    private WrappedTextComp bonusTextComp;
    private TextCompDC rpgDescription;
    private SpecialValueBar integrityBar;

    public PrincipleMiddlePanel(Unit hero) {
        this.hero = hero;
        panelSize = new Dimension(VISUALS.INFO_PANEL_LARGE.getWidth(), 230);
    }

    public void init() {
        add(new GraphicComponent(STD_IMAGES.GUARDIAN), "@pos 0 max_bottom");
        add(new GraphicComponent(ImageTransformer.flipHorizontally(ImageManager
         .getBufferedImage(STD_IMAGES.GUARDIAN.getImage()))), "@pos max_right max_bottom");
        integrityBar = new SpecialValueBar(false, PARAMS.INTEGRITY);
        integrityBar.setObj(hero);
        rpgDescription = new TextCompDC(null, "", 18, FONT.AVQ);
        bonusTextComp = new WrappedTextComp(null, false);

        bonusTextComp.setPanelSize(new Dimension(250, 170));
        add(integrityBar, "@id bar, pos center_x 0");
        add(bonusTextComp, "@id text, pos center_x rpg.y2");

    }

    @Override
    protected boolean isAutoZOrder() {
        return !super.isAutoZOrder();
    }

    @Override
    public void refresh() {
        rpgDescription.setText(IntegrityRule.getDescription(hero));
        rpgDescription.setPanelSize(new Dimension(250, 32));
        int offset = FontMaster.getStringWidth(rpgDescription.getFont(), rpgDescription
         .getTextString()) / 2;
        remove(rpgDescription);
        add(rpgDescription, "@id rpg, pos center_x-" + offset + " bar.y2");
        // TODO the real issue is that Panel Size is not dynamically adjusted to
        // text!
        bonusTextComp.setPanelSize(new Dimension(250, 170));
        bonusTextComp.setDefaultSize(new Dimension(250, 170));
        bonusTextComp.setTextLines(IntegrityRule.getIntegrityBonusInfo(hero));

        offset = FontMaster.getStringWidth(bonusTextComp.getFont(), StringMaster
         .getLongestString(bonusTextComp.getTextLines())) / 2;
        remove(bonusTextComp);
        add(bonusTextComp, "@id text, pos center_x rpg.y2"); // -" + offset + "
        bonusTextComp.repaint();
        integrityBar.refresh();
        integrityBar.refresh();

    }
}
