package main.client.cc.gui.neo;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.neo.bars.SpecialValueBar;
import main.client.cc.gui.neo.header.HeroHeader;
import main.content.PARAMS;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.rules.rpg.IntegrityRule;
import main.swing.components.panels.DC_ItemPanel;
import main.swing.components.panels.ValueIconPanel;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.components.panels.page.small.DC_PagedBuffPanel;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;

public class HeroPanel extends G_Panel {
    public final static VISUALS HERO_PANEL_FRAME = VISUALS.FRAME;

    private static final int X_OFFSET = 52;
    private static final int Y_OFFSET = 54;
    DC_ItemPanel itemPanel;
    SpecialValueBar xpBar;
    SpecialValueBar integrityBar;
    private HeroHeader header;
    // always for hero?
    private DC_PagedInfoPanel infoPanel;
    private ValueIconPanel values;
    // for selected deity/class? By default - ???
    private DC_PagedInfoPanel lowerPanel;
    private Unit hero;
    private Obj selectedObj;
    private G_Component[] comps;
    private boolean deityMode;
    private Component deityComp;
    private DC_PagedBuffPanel buffPanel;

    public HeroPanel(Unit hero) {
        super(HERO_PANEL_FRAME);
        this.hero = hero;
        this.panelSize = HERO_PANEL_FRAME.getSize();
        init();
    }

    private void init() {
        xpBar = new SpecialValueBar(false, PARAMS.XP);
        xpBar.setObj(hero);
        integrityBar = new SpecialValueBar(true, PARAMS.INTEGRITY);
        integrityBar.setObj(hero);
        values = new ValueIconPanel(hero);
        infoPanel = new DC_PagedInfoPanel(hero);

        buffPanel = new DC_PagedBuffPanel();
        buffPanel.setObj(hero);
        itemPanel = new DC_ItemPanel(hero.getGame(), true);
        itemPanel.setObj(hero);
        lowerPanel = new DC_PagedInfoPanel(hero
                // , true
        );
        header = new HeroHeader(hero, this);
        comps = new G_Component[]{header, values, buffPanel, infoPanel, lowerPanel};

    }

    @Override
    public void refresh() {
        if (!deityMode) {
            for (G_Component c : comps) {
                c.refresh();
            }
        }
        removeAll();
        addComps();
        itemPanel.setObj(hero);
        itemPanel.refresh();
        revalidate();
    }

    public void deityClicked() {
        if (hero.getDeity() == null) {
            // init emblem choosing or deity choosing?
        }
        deityMode = !deityMode;
        CharacterCreator.getPanel().togglePrincipleView();

    }

    private void addComps() {
        add(header, "id header, pos " + X_OFFSET + " " + Y_OFFSET);
        if (deityMode) {
            if (deityComp == null) {
                deityComp = new JLabel(hero.getDeity().getType().getIcon());
            }
            add(deityComp, "id dc, pos header.x+12 header.y2");
            add(infoPanel, "id ip, pos header.x dc.y2");
            infoPanel.setEntity(hero.getDeity());
            infoPanel.refresh();
            return;
        }
        TextCompDC xpText = new TextCompDC(null, "Level " + hero.getLevel()) {
            protected Font getDefaultFont() {
                return FontMaster.getFont(FONT.AVQ, 18, Font.BOLD);
            }
        };
        add(xpText, "@id xpText, pos center_x header.y2-6");

        add(new GraphicComponent(ImageManager.getValueIcon(PARAMS.XP), "Experience"),
                "@id xpIcon, pos 52 xpText.y2-30");
        xpBar.refresh();
        add(xpBar, "id xpBar, pos xpIcon.x2-2 xpText.y2-17");

        add(new GraphicComponent(ImageManager.getValueIcon(PARAMS.INTEGRITY), "integrity"),
                "@id integrityIcon, pos 52 xpBar.y2-3");
        integrityBar.refresh();
        add(integrityBar, "id integrityBar, pos integrityIcon.x2-2 xpBar.y2");

        TextCompDC integrityText = new TextCompDC(null, "", 16) {
            protected String getText() {
                return IntegrityRule.getDescription(hero);
            }

            ;
        };
        add(integrityText, "@id integrityText, pos center_x integrityBar.y2");
        // xp label

        add(buffPanel, "id buffs, pos header.x integrityText.y2");
        add(infoPanel, "id ip, pos header.x buffs.y2");
        infoPanel.setEntity(selectedObj != null ? selectedObj : hero);
        infoPanel.refresh();

        add(values, "id center, pos header.x ip.y2");
    }

}
